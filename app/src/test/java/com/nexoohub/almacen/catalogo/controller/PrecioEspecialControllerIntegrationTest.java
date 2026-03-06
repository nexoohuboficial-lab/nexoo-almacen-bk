package com.nexoohub.almacen.catalogo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexoohub.almacen.catalogo.entity.PrecioEspecial;
import com.nexoohub.almacen.catalogo.entity.TipoCliente;
import com.nexoohub.almacen.catalogo.repository.PrecioEspecialRepository;
import com.nexoohub.almacen.catalogo.repository.TipoClienteRepository;
import com.nexoohub.almacen.common.config.JwtUtil;
import com.nexoohub.almacen.common.entity.Usuario;
import com.nexoohub.almacen.common.repository.UsuarioRepository;
import com.nexoohub.almacen.inventario.entity.ProductoMaestro;
import com.nexoohub.almacen.inventario.repository.ProductoMaestroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class PrecioEspecialControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PrecioEspecialRepository precioEspecialRepository;

    @Autowired
    private TipoClienteRepository tipoClienteRepository;

    @Autowired
    private ProductoMaestroRepository productoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;
    private Integer tipoClienteId;
    private String skuProducto;

    @BeforeEach
    void setUp() {
        precioEspecialRepository.deleteAll();
        tipoClienteRepository.deleteAll();
        productoRepository.deleteAll();
        usuarioRepository.deleteAll();

        Usuario usuario = new Usuario();
        usuario.setUsername("admin@nexoo.com");
        usuario.setPassword("$2a$10$test");
        usuario.setRole("ROLE_ADMIN");
        usuarioRepository.save(usuario);

        TipoCliente tipoCliente = new TipoCliente();
        tipoCliente.setNombre("Taller Mecánico");
        tipoCliente.setDescripcion("Cliente mayorista");
        TipoCliente guardado = tipoClienteRepository.save(tipoCliente);
        tipoClienteId = guardado.getId();

        ProductoMaestro producto = new ProductoMaestro();
        producto.setSkuInterno("ACEITE001");
        producto.setNombreComercial("Aceite Motor 20W50");
        producto.setActivo(true);
        productoRepository.save(producto);
        skuProducto = "ACEITE001";

        token = jwtUtil.generateToken("admin@nexoo.com");
    }

    @Test
    void crearPrecioEspecial_Exitoso() throws Exception {
        PrecioEspecial precioEspecial = new PrecioEspecial();
        precioEspecial.setSkuInterno(skuProducto);
        precioEspecial.setTipoClienteId(tipoClienteId);
        precioEspecial.setPrecioFijo(new BigDecimal("85.00"));

        mockMvc.perform(post("/api/v1/precios-especiales")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(precioEspecial)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exitoso").value(true))
                .andExpect(jsonPath("$.mensaje").exists())
                .andExpect(jsonPath("$.id").isNumber());

        assertThat(precioEspecialRepository.count()).isEqualTo(1);
    }

    @Test
    void crearPrecioEspecial_SkuNulo() throws Exception {
        PrecioEspecial precioEspecial = new PrecioEspecial();
        precioEspecial.setSkuInterno(null);
        precioEspecial.setTipoClienteId(tipoClienteId);
        precioEspecial.setPrecioFijo(new BigDecimal("85.00"));

        mockMvc.perform(post("/api/v1/precios-especiales")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(precioEspecial)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void crearPrecioEspecial_TipoClienteIdNulo() throws Exception {
        PrecioEspecial precioEspecial = new PrecioEspecial();
        precioEspecial.setSkuInterno(skuProducto);
        precioEspecial.setTipoClienteId(null);
        precioEspecial.setPrecioFijo(new BigDecimal("85.00"));

        mockMvc.perform(post("/api/v1/precios-especiales")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(precioEspecial)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void crearPrecioEspecial_PrecioFijoNulo() throws Exception {
        PrecioEspecial precioEspecial = new PrecioEspecial();
        precioEspecial.setSkuInterno(skuProducto);
        precioEspecial.setTipoClienteId(tipoClienteId);
        precioEspecial.setPrecioFijo(null);

        mockMvc.perform(post("/api/v1/precios-especiales")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(precioEspecial)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void eliminarPrecioEspecial_Exitoso() throws Exception {
        PrecioEspecial precioEspecial = new PrecioEspecial();
        precioEspecial.setSkuInterno(skuProducto);
        precioEspecial.setTipoClienteId(tipoClienteId);
        precioEspecial.setPrecioFijo(new BigDecimal("85.00"));
        PrecioEspecial guardado = precioEspecialRepository.save(precioEspecial);

        assertThat(precioEspecialRepository.count()).isEqualTo(1);

        mockMvc.perform(delete("/api/v1/precios-especiales/" + guardado.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        assertThat(precioEspecialRepository.count()).isEqualTo(0);
    }

    @Test
    void eliminarPrecioEspecial_NoExiste() throws Exception {
        mockMvc.perform(delete("/api/v1/precios-especiales/99999")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }

    @Test
    void sinAutenticacion_RetornaForbidden() throws Exception {
        PrecioEspecial precioEspecial = new PrecioEspecial();
        precioEspecial.setSkuInterno(skuProducto);
        precioEspecial.setTipoClienteId(tipoClienteId);
        precioEspecial.setPrecioFijo(new BigDecimal("85.00"));

        mockMvc.perform(post("/api/v1/precios-especiales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(precioEspecial)))
                .andExpect(status().isForbidden());
    }
}
