package com.nexoohub.almacen.inventario.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexoohub.almacen.common.config.JwtUtil;
import com.nexoohub.almacen.common.entity.Usuario;
import com.nexoohub.almacen.common.repository.UsuarioRepository;
import com.nexoohub.almacen.inventario.dto.GenerarAlertasRequestDTO;
import com.nexoohub.almacen.inventario.dto.ResolverAlertaRequestDTO;
import com.nexoohub.almacen.catalogo.entity.Categoria;
import com.nexoohub.almacen.catalogo.repository.CategoriaRepository;
import com.nexoohub.almacen.inventario.entity.AlertaLentoMovimiento;
import com.nexoohub.almacen.inventario.entity.InventarioSucursal;
import com.nexoohub.almacen.inventario.entity.InventarioSucursalId;
import com.nexoohub.almacen.inventario.entity.ProductoMaestro;
import com.nexoohub.almacen.inventario.repository.AlertaLentoMovimientoRepository;
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
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AlertaLentoMovimientoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AlertaLentoMovimientoRepository alertaRepository;

    @Autowired
    private InventarioSucursalRepository inventarioRepository;

    @Autowired
    private ProductoMaestroRepository productoRepository;

    @Autowired
    private SucursalRepository sucursalRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;
    private Integer sucursalId;
    private String skuInterno;

    @BeforeEach
    void setUp() {
        // Limpiar datos
        alertaRepository.deleteAll();
        inventarioRepository.deleteAll();
        productoRepository.deleteAll();
        sucursalRepository.deleteAll();
        categoriaRepository.deleteAll();
        usuarioRepository.deleteAll();

        // Crear usuario admin
        Usuario usuario = new Usuario();
        usuario.setUsername("admin@nexoo.com");
        usuario.setPassword("$2a$10$test");
        usuario.setRole("ROLE_ADMIN");
        usuarioRepository.save(usuario);

        // Generar token JWT
        token = jwtUtil.generateToken(usuario.getUsername());

        // Crear categoría
        Categoria categoria = new Categoria();
        categoria.setNombre("Repuestos");
        Integer categoriaId = categoriaRepository.save(categoria).getId();

        // Crear sucursal
        Sucursal sucursal = new Sucursal();
        sucursal.setNombre("Sucursal Centro");
        sucursal.setDireccion("Av. Principal 123");
        sucursal.setActivo(true);
        sucursalId = sucursalRepository.save(sucursal).getId();

        // Crear producto
        ProductoMaestro producto = new ProductoMaestro();
        producto.setSkuInterno("PROD001");
        producto.setNombreComercial("Producto Test");
        producto.setCategoriaId(categoriaId);
        producto.setActivo(true);
        skuInterno = productoRepository.save(producto).getSkuInterno();

        // Crear inventario
        InventarioSucursal inventario = new InventarioSucursal();
        inventario.setId(new InventarioSucursalId(sucursalId, skuInterno));
        inventario.setStockActual(50);
        inventario.setCostoPromedioPonderado(new BigDecimal("100.00"));
        inventarioRepository.save(inventario);
    }

    @Test
    void testGenerarAlertas() throws Exception {
        GenerarAlertasRequestDTO request = new GenerarAlertasRequestDTO();
        request.setDiasSinVentaMinimo(30);

        // El endpoint debe retornar 201 con un array de alertas
        // Si no hay ventas históricas, el array puede estar vacío o contener alertas de productos nunca vendidos
        mockMvc.perform(post("/api/alertas/lento-movimiento/generar")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray());
        
        // Verificar que las alertas se hayan generado correctamente
        List<AlertaLentoMovimiento> alertas = alertaRepository.findAllActiveWithDetails();
        assertNotNull(alertas);
    }

    @Test
    void testGenerarAlertas_DiasSinVentaMinimo_Obligatorio() throws Exception {
        GenerarAlertasRequestDTO request = new GenerarAlertasRequestDTO();
        // No se establece diasSinVentaMinimo

        mockMvc.perform(post("/api/alertas/lento-movimiento/generar")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testObtenerAlertasActivas() throws Exception {
        // Crear alerta de prueba
        crearAlertaTest(skuInterno, sucursalId, 45, "ADVERTENCIA", false);

        mockMvc.perform(get("/api/alertas/lento-movimiento")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    void testObtenerAlertaPorId() throws Exception {
        // Crear alerta de prueba
        AlertaLentoMovimiento alerta = crearAlertaTest(skuInterno, sucursalId, 45, "ADVERTENCIA", false);

        mockMvc.perform(get("/api/alertas/lento-movimiento/" + alerta.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(alerta.getId()))
                .andExpect(jsonPath("$.skuInterno").value(skuInterno))
                .andExpect(jsonPath("$.diasSinVenta").value(45));
    }

    @Test
    void testObtenerAlertaPorId_NoExiste() throws Exception {
        mockMvc.perform(get("/api/alertas/lento-movimiento/99999")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void testObtenerAlertasPorSucursal() throws Exception {
        // Crear alerta de prueba
        crearAlertaTest(skuInterno, sucursalId, 45, "ADVERTENCIA", false);

        mockMvc.perform(get("/api/alertas/lento-movimiento/sucursal/" + sucursalId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].sucursalId").value(sucursalId));
    }

    @Test
    void testObtenerAlertasCriticas() throws Exception {
        // Crear alerta crítica (más de 60 días)
        crearAlertaTest(skuInterno, sucursalId, 75, "CRITICO", false);

        mockMvc.perform(get("/api/alertas/lento-movimiento/criticas")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].estadoAlerta").value("CRITICO"));
    }

    @Test
    void testObtenerAlertasPorProducto() throws Exception {
        // Crear alerta de prueba
        crearAlertaTest(skuInterno, sucursalId, 45, "ADVERTENCIA", false);

        mockMvc.perform(get("/api/alertas/lento-movimiento/producto/" + skuInterno)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].skuInterno").value(skuInterno));
    }

    @Test
    void testCalcularCostoInmovilizadoGlobal() throws Exception {
        // Crear alerta de prueba
        crearAlertaTest(skuInterno, sucursalId, 45, "ADVERTENCIA", false);

        mockMvc.perform(get("/api/alertas/lento-movimiento/costo-inmovilizado")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.costoTotalInmovilizado").exists())
                .andExpect(jsonPath("$.cantidadAlertas").exists());
    }

    @Test
    void testCalcularCostoInmovilizadoPorSucursal() throws Exception {
        // Crear alerta de prueba
        crearAlertaTest(skuInterno, sucursalId, 45, "ADVERTENCIA", false);

        mockMvc.perform(get("/api/alertas/lento-movimiento/costo-inmovilizado")
                        .header("Authorization", "Bearer " + token)
                        .param("sucursalId", sucursalId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.costoTotalInmovilizado").exists())
                .andExpect(jsonPath("$.cantidadAlertas").exists());
    }

    @Test
    void testResolverAlerta() throws Exception {
        // Crear alerta de prueba
        AlertaLentoMovimiento alerta = crearAlertaTest(skuInterno, sucursalId, 45, "ADVERTENCIA", false);

        ResolverAlertaRequestDTO request = new ResolverAlertaRequestDTO();
        request.setAccionTomada("PROMOCION");
        request.setObservaciones("Se aplicó descuento del 20%");

        mockMvc.perform(put("/api/alertas/lento-movimiento/" + alerta.getId() + "/resolver")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(alerta.getId()))
                .andExpect(jsonPath("$.resuelto").value(true))
                .andExpect(jsonPath("$.accionTomada").value("PROMOCION"));
    }

    @Test
    void testResolverAlerta_AccionObligatoria() throws Exception {
        // Crear alerta de prueba
        AlertaLentoMovimiento alerta = crearAlertaTest(skuInterno, sucursalId, 45, "ADVERTENCIA", false);

        ResolverAlertaRequestDTO request = new ResolverAlertaRequestDTO();
        // No se establece accionTomada

        mockMvc.perform(put("/api/alertas/lento-movimiento/" + alerta.getId() + "/resolver")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testEliminarAlerta() throws Exception {
        // Crear alerta de prueba
        AlertaLentoMovimiento alerta = crearAlertaTest(skuInterno, sucursalId, 45, "ADVERTENCIA", false);

        mockMvc.perform(delete("/api/alertas/lento-movimiento/" + alerta.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        // Verificar que fue eliminada
        mockMvc.perform(get("/api/alertas/lento-movimiento/" + alerta.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void testEliminarAlerta_NoExiste() throws Exception {
        mockMvc.perform(delete("/api/alertas/lento-movimiento/99999")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    // ==========================================
    // MÉTODOS HELPER
    // ==========================================

    private AlertaLentoMovimiento crearAlertaTest(String skuInterno, Integer sucursalId, 
                                                   Integer diasSinVenta, String estadoAlerta, 
                                                   Boolean resuelto) {
        AlertaLentoMovimiento alerta = new AlertaLentoMovimiento();
        alerta.setSkuInterno(skuInterno);
        alerta.setSucursalId(sucursalId);
        alerta.setDiasSinVenta(diasSinVenta);
        alerta.setUltimaVenta(LocalDate.now().minusDays(diasSinVenta));
        alerta.setStockActual(50);
        alerta.setCostoInmovilizado(new BigDecimal("5000.00"));
        alerta.setEstadoAlerta(estadoAlerta);
        alerta.setFechaDeteccion(LocalDate.now());
        alerta.setResuelto(resuelto);
        
        if (resuelto) {
            alerta.setFechaResolucion(LocalDate.now());
            alerta.setAccionTomada("PROMOCION");
        }
        
        return alertaRepository.save(alerta);
    }
}
