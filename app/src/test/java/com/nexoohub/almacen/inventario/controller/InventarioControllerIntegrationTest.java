package com.nexoohub.almacen.inventario.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexoohub.almacen.common.config.JwtUtil;
import com.nexoohub.almacen.common.entity.Usuario;
import com.nexoohub.almacen.common.repository.UsuarioRepository;
import com.nexoohub.almacen.inventario.entity.InventarioSucursal;
import com.nexoohub.almacen.inventario.entity.InventarioSucursalId;
import com.nexoohub.almacen.inventario.entity.ProductoMaestro;
import com.nexoohub.almacen.inventario.repository.InventarioSucursalRepository;
import com.nexoohub.almacen.inventario.repository.ProductoMaestroRepository;
import com.nexoohub.almacen.sucursal.entity.Sucursal;
import com.nexoohub.almacen.sucursal.repository.SucursalRepository;
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
class InventarioControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private InventarioSucursalRepository inventarioRepository;

    @Autowired
    private ProductoMaestroRepository productoRepository;

    @Autowired
    private SucursalRepository sucursalRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;
    private Integer sucursalId;

    @BeforeEach
    void setUp() {
        inventarioRepository.deleteAll();
        productoRepository.deleteAll();
        sucursalRepository.deleteAll();
        usuarioRepository.deleteAll();

        Usuario usuario = new Usuario();
        usuario.setUsername("admin@nexoo.com");
        usuario.setPassword("$2a$10$test");
        usuario.setRole("ROLE_ADMIN");
        usuarioRepository.save(usuario);

        Sucursal sucursal = new Sucursal();
        sucursal.setNombre("Sucursal Centro");
        sucursal.setDireccion("Av. Principal 123");
        sucursal.setActivo(true);
        sucursalId = sucursalRepository.save(sucursal).getId();

        token = jwtUtil.generateToken("admin@nexoo.com");
    }

    @Test
    void obtenerInventarioPorSucursal_SinDatos() throws Exception {
        mockMvc.perform(get("/api/v1/inventario/sucursales/" + sucursalId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(0))
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void obtenerInventarioPorSucursal_ConDatos() throws Exception {
        // Crear productos
        ProductoMaestro p1 = new ProductoMaestro();
        p1.setSkuInterno("PROD001");
        p1.setNombreComercial("Producto Uno");
        p1.setActivo(true);
        productoRepository.save(p1);

        ProductoMaestro p2 = new ProductoMaestro();
        p2.setSkuInterno("PROD002");
        p2.setNombreComercial("Producto Dos");
        p2.setActivo(true);
        productoRepository.save(p2);

        // Crear inventario
        InventarioSucursal inv1 = new InventarioSucursal();
        inv1.setId(new InventarioSucursalId(sucursalId, "PROD001"));
        inv1.setStockActual(50);
        inv1.setCostoPromedioPonderado(new BigDecimal("100.00"));
        inventarioRepository.save(inv1);

        InventarioSucursal inv2 = new InventarioSucursal();
        inv2.setId(new InventarioSucursalId(sucursalId, "PROD002"));
        inv2.setStockActual(30);
        inv2.setCostoPromedioPonderado(new BigDecimal("200.00"));
        inventarioRepository.save(inv2);

        mockMvc.perform(get("/api/v1/inventario/sucursales/" + sucursalId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void registrarStock_Exitoso() throws Exception {
        ProductoMaestro producto = new ProductoMaestro();
        producto.setSkuInterno("NUEVO001");
        producto.setNombreComercial("Producto Nuevo");
        producto.setActivo(true);
        productoRepository.save(producto);

        InventarioSucursal inventario = new InventarioSucursal();
        inventario.setId(new InventarioSucursalId(sucursalId, "NUEVO001"));
        inventario.setStockActual(100);
        inventario.setCostoPromedioPonderado(new BigDecimal("150.00"));

        mockMvc.perform(post("/api/v1/inventario")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inventario)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exitoso").value(true))
                .andExpect(jsonPath("$.mensaje").value("Inventario registrado/actualizado correctamente"))
                .andExpect(jsonPath("$.fechaRegistro").exists());

        InventarioSucursal verificar = inventarioRepository.findById(
                new InventarioSucursalId(sucursalId, "NUEVO001")
        ).orElseThrow();
        assertThat(verificar.getStockActual()).isEqualTo(100);
    }

    @Test
    void registrarStock_Actualizar() throws Exception {
        ProductoMaestro producto = new ProductoMaestro();
        producto.setSkuInterno("EXISTENTE001");
        producto.setNombreComercial("Producto Existente");
        producto.setActivo(true);
        productoRepository.save(producto);

        // Stock inicial
        InventarioSucursal inicial = new InventarioSucursal();
        inicial.setId(new InventarioSucursalId(sucursalId, "EXISTENTE001"));
        inicial.setStockActual(50);
        inicial.setCostoPromedioPonderado(new BigDecimal("100.00"));
        inventarioRepository.save(inicial);

        // Actualizar stock
        InventarioSucursal actualizado = new InventarioSucursal();
        actualizado.setId(new InventarioSucursalId(sucursalId, "EXISTENTE001"));
        actualizado.setStockActual(75);
        actualizado.setCostoPromedioPonderado(new BigDecimal("120.00"));

        mockMvc.perform(post("/api/v1/inventario")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(actualizado)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exitoso").value(true));

        InventarioSucursal verificar = inventarioRepository.findById(
                new InventarioSucursalId(sucursalId, "EXISTENTE001")
        ).orElseThrow();
        assertThat(verificar.getStockActual()).isEqualTo(75);
        assertThat(verificar.getCostoPromedioPonderado()).isEqualByComparingTo(new BigDecimal("120.00"));
    }

    @Test
    void sinAutenticacion_RetornaForbidden() throws Exception {
        mockMvc.perform(get("/api/v1/inventario/sucursales/" + sucursalId))
                .andExpect(status().isForbidden());
    }
}
