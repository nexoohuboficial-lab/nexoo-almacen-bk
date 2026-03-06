package com.nexoohub.almacen.catalogo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexoohub.almacen.catalogo.entity.CompatibilidadProducto;
import com.nexoohub.almacen.catalogo.entity.Moto;
import com.nexoohub.almacen.catalogo.repository.CompatibilidadRepository;
import com.nexoohub.almacen.catalogo.repository.MotoRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class CompatibilidadControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CompatibilidadRepository compatibilidadRepository;

    @Autowired
    private MotoRepository motoRepository;

    @Autowired
    private ProductoMaestroRepository productoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;
    private Integer motoId;
    private String skuProducto;

    @BeforeEach
    void setUp() {
        compatibilidadRepository.deleteAll();
        motoRepository.deleteAll();
        productoRepository.deleteAll();
        usuarioRepository.deleteAll();

        Usuario usuario = new Usuario();
        usuario.setUsername("admin@nexoo.com");
        usuario.setPassword("$2a$10$test");
        usuario.setRole("ROLE_ADMIN");
        usuarioRepository.save(usuario);

        Moto moto = new Moto();
        moto.setMarca("Honda");
        moto.setModelo("CBR 600RR");
        moto.setCilindrada(600);
        Moto motoGuardada = motoRepository.save(moto);
        motoId = motoGuardada.getId();

        ProductoMaestro producto = new ProductoMaestro();
        producto.setSkuInterno("FILTRO001");
        producto.setNombreComercial("Filtro de Aceite");
        producto.setActivo(true);
        productoRepository.save(producto);
        skuProducto = "FILTRO001";

        token = jwtUtil.generateToken("admin@nexoo.com");
    }

    @Test
    void enlazarProductoConMoto_Exitoso() throws Exception {
        CompatibilidadProducto compatibilidad = new CompatibilidadProducto();
        compatibilidad.setSkuInterno(skuProducto);
        compatibilidad.setMotoId(motoId);
        compatibilidad.setAnioInicio(2013);
        compatibilidad.setAnioFin(2020);

        mockMvc.perform(post("/api/v1/compatibilidad")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(compatibilidad)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exitoso").value(true))
                .andExpect(jsonPath("$.mensaje").exists())
                .andExpect(jsonPath("$.id").isNumber());

        assertThat(compatibilidadRepository.count()).isEqualTo(1);
    }

    @Test
    void enlazarProductoConMoto_SkuVacio() throws Exception {
        CompatibilidadProducto compatibilidad = new CompatibilidadProducto();
        compatibilidad.setSkuInterno("");
        compatibilidad.setMotoId(motoId);

        mockMvc.perform(post("/api/v1/compatibilidad")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(compatibilidad)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void enlazarProductoConMoto_MotoIdNulo() throws Exception {
        CompatibilidadProducto compatibilidad = new CompatibilidadProducto();
        compatibilidad.setSkuInterno(skuProducto);
        compatibilidad.setMotoId(null);

        mockMvc.perform(post("/api/v1/compatibilidad")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(compatibilidad)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void buscarPorSku_ConCompatibilidades() throws Exception {
        CompatibilidadProducto comp1 = new CompatibilidadProducto();
        comp1.setSkuInterno(skuProducto);
        comp1.setMotoId(motoId);
        comp1.setAnioInicio(2013);
        comp1.setAnioFin(2020);
        compatibilidadRepository.save(comp1);

        Moto moto2 = new Moto();
        moto2.setMarca("Yamaha");
        moto2.setModelo("R6");
        moto2.setCilindrada(600);
        Moto moto2Guardada = motoRepository.save(moto2);

        CompatibilidadProducto comp2 = new CompatibilidadProducto();
        comp2.setSkuInterno(skuProducto);
        comp2.setMotoId(moto2Guardada.getId());
        comp2.setAnioInicio(2017);
        comp2.setAnioFin(2023);
        compatibilidadRepository.save(comp2);

        mockMvc.perform(get("/api/v1/compatibilidad/producto/" + skuProducto)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void buscarPorSku_SinCompatibilidades() throws Exception {
        mockMvc.perform(get("/api/v1/compatibilidad/producto/NOEXISTE")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(0))
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void buscarPorMoto_ConCompatibilidades() throws Exception {
        CompatibilidadProducto comp1 = new CompatibilidadProducto();
        comp1.setSkuInterno(skuProducto);
        comp1.setMotoId(motoId);
        compatibilidadRepository.save(comp1);

        ProductoMaestro producto2 = new ProductoMaestro();
        producto2.setSkuInterno("PASTILLA001");
        producto2.setNombreComercial("Pastillas de Freno");
        productoRepository.save(producto2);

        CompatibilidadProducto comp2 = new CompatibilidadProducto();
        comp2.setSkuInterno("PASTILLA001");
        comp2.setMotoId(motoId);
        compatibilidadRepository.save(comp2);

        mockMvc.perform(get("/api/v1/compatibilidad/moto/" + motoId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void buscarPorMoto_SinCompatibilidades() throws Exception {
        mockMvc.perform(get("/api/v1/compatibilidad/moto/99999")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(0))
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void sinAutenticacion_RetornaForbidden() throws Exception {
        mockMvc.perform(get("/api/v1/compatibilidad/producto/TEST"))
                .andExpect(status().isForbidden());
    }
}
