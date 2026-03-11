package com.nexoohub.almacen.prediccion.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexoohub.almacen.common.exception.GlobalExceptionHandler;
import com.nexoohub.almacen.prediccion.dto.GenerarPrediccionRequestDTO;
import com.nexoohub.almacen.prediccion.dto.PrediccionDemandaResponseDTO;
import com.nexoohub.almacen.prediccion.dto.RecomendacionCompraDTO;
import com.nexoohub.almacen.prediccion.service.PrediccionDemandaService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests de integración para PrediccionDemandaController
 * 
 * @author NexooHub Development Team
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:prediccion-testdb;MODE=MySQL;DB_CLOSE_DELAY=-1",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.flyway.enabled=false"
})
@DisplayName("PrediccionDemandaController - Tests de Integración")
class PrediccionDemandaControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PrediccionDemandaService prediccionService;

    // ==========================================
    // TESTS: POST /api/predicciones/generar
    // ==========================================

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /generar - Debe generar predicciones exitosamente")
    void testGenerarPrediccionesExitoso() throws Exception {
        // Given
        GenerarPrediccionRequestDTO request = new GenerarPrediccionRequestDTO();
        request.setSucursalId(1);
        request.setPeriodoAnio(2026);
        request.setPeriodoMes(4);
        request.setMesesHistoricos(6);
        request.setDiasStockSeguridad(7);
        request.setMetodoCalculo("PROMEDIO_MOVIL");

        PrediccionDemandaResponseDTO response = crearPrediccionResponse(
                1, "PROD-001", "Producto Test", 1, "Sucursal Centro",
                2026, 4, "Abril 2026", BigDecimal.valueOf(30.00), BigDecimal.valueOf(32.00),
                50, 11, 43, BigDecimal.valueOf(85.50)
        );

        when(prediccionService.generarPredicciones(any(GenerarPrediccionRequestDTO.class)))
                .thenReturn(Arrays.asList(response));

        // When & Then
        mockMvc.perform(post("/api/predicciones/generar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].skuProducto", is("PROD-001")))
                .andExpect(jsonPath("$[0].demandaPredicha", is(32.00)))
                .andExpect(jsonPath("$[0].cantidadComprar", is(43)))
                .andExpect(jsonPath("$[0].nivelConfianza", is(85.50)));

        verify(prediccionService, times(1)).generarPredicciones(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /generar - Debe validar campos obligatorios")
    void testGenerarPrediccionesValidacionCamposObligatorios() throws Exception {
        // Given - Request con campos nulos
        GenerarPrediccionRequestDTO request = new GenerarPrediccionRequestDTO();
        // No se setean campos obligatorios

        // When & Then
        mockMvc.perform(post("/api/predicciones/generar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(prediccionService, never()).generarPredicciones(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /generar - Debe validar rango de mes (1-12)")
    void testGenerarPrediccionesValidacionMes() throws Exception {
        // Given - Mes inválido
        GenerarPrediccionRequestDTO request = new GenerarPrediccionRequestDTO();
        request.setSucursalId(1);
        request.setPeriodoAnio(2026);
        request.setPeriodoMes(13); // Inválido
        request.setMesesHistoricos(6);
        request.setDiasStockSeguridad(7);
        request.setMetodoCalculo("PROMEDIO_MOVIL");

        // When & Then
        mockMvc.perform(post("/api/predicciones/generar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(prediccionService, never()).generarPredicciones(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /generar - Debe validar año mínimo")
    void testGenerarPrediccionesValidacionAnio() throws Exception {
        // Given - Año inválido
        GenerarPrediccionRequestDTO request = new GenerarPrediccionRequestDTO();
        request.setSucursalId(1);
        request.setPeriodoAnio(1999); // Menor que 2020
        request.setPeriodoMes(4);
        request.setMesesHistoricos(6);
        request.setDiasStockSeguridad(7);
        request.setMetodoCalculo("PROMEDIO_MOVIL");

        // When & Then
        mockMvc.perform(post("/api/predicciones/generar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(prediccionService, never()).generarPredicciones(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /generar - Debe aceptar método de cálculo TENDENCIA_LINEAL")
    void testGenerarPrediccionesTendenciaLineal() throws Exception {
        // Given
        GenerarPrediccionRequestDTO request = new GenerarPrediccionRequestDTO();
        request.setSucursalId(1);
        request.setPeriodoAnio(2026);
        request.setPeriodoMes(4);
        request.setMesesHistoricos(12);
        request.setDiasStockSeguridad(10);
        request.setMetodoCalculo("TENDENCIA_LINEAL");

        PrediccionDemandaResponseDTO response = crearPrediccionResponse(
                1, "PROD-001", "Producto Test", 1, "Sucursal Centro",
                2026, 4, "Abril 2026", BigDecimal.valueOf(40.00), BigDecimal.valueOf(42.50),
                30, 14, 26, BigDecimal.valueOf(78.30)
        );

        when(prediccionService.generarPredicciones(any())).thenReturn(Arrays.asList(response));

        // When & Then
        mockMvc.perform(post("/api/predicciones/generar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$[0].demandaPredicha", is(42.50)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /generar - Debe generar múltiples predicciones cuando skuProducto es null")
    void testGenerarPrediccionesMultiplesProductos() throws Exception {
        // Given
        GenerarPrediccionRequestDTO request = new GenerarPrediccionRequestDTO();
        request.setSucursalId(1);
        request.setPeriodoAnio(2026);
        request.setPeriodoMes(4);
        request.setMesesHistoricos(6);
        request.setDiasStockSeguridad(7);
        request.setMetodoCalculo("PROMEDIO_MOVIL");
        request.setSkuProducto(null); // Todos los productos

        PrediccionDemandaResponseDTO response1 = crearPrediccionResponse(
                1, "PROD-001", "Producto 1", 1, "Sucursal Centro",
                2026, 4, "Abril 2026", BigDecimal.valueOf(30.00), BigDecimal.valueOf(32.00),
                50, 11, 0, BigDecimal.valueOf(85.50)
        );

        PrediccionDemandaResponseDTO response2 = crearPrediccionResponse(
                2, "PROD-002", "Producto 2", 1, "Sucursal Centro",
                2026, 4, "Abril 2026", BigDecimal.valueOf(45.00), BigDecimal.valueOf(48.00),
                20, 16, 44, BigDecimal.valueOf(90.00)
        );

        when(prediccionService.generarPredicciones(any()))
                .thenReturn(Arrays.asList(response1, response2));

        // When & Then
        mockMvc.perform(post("/api/predicciones/generar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].skuProducto", is("PROD-001")))
                .andExpect(jsonPath("$[1].skuProducto", is("PROD-002")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /generar - Debe retornar lista vacía cuando no hay datos históricos")
    void testGenerarPrediccionesSinDatosHistoricos() throws Exception {
        // Given
        GenerarPrediccionRequestDTO request = new GenerarPrediccionRequestDTO();
        request.setSucursalId(1);
        request.setPeriodoAnio(2026);
        request.setPeriodoMes(4);
        request.setMesesHistoricos(6);
        request.setDiasStockSeguridad(7);
        request.setMetodoCalculo("PROMEDIO_MOVIL");
        request.setSkuProducto("PROD-NUEVO");

        when(prediccionService.generarPredicciones(any()))
                .thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(post("/api/predicciones/generar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // ==========================================
    // TESTS: GET /api/predicciones/{id}
    // ==========================================

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /{id} - Debe obtener predicción por ID exitosamente")
    void testObtenerPrediccionPorId() throws Exception {
        // Given
        PrediccionDemandaResponseDTO response = crearPrediccionResponse(
                1, "PROD-001", "Producto Test", 1, "Sucursal Centro",
                2026, 4, "Abril 2026", BigDecimal.valueOf(30.00), BigDecimal.valueOf(32.00),
                50, 11, 43, BigDecimal.valueOf(85.50)
        );

        when(prediccionService.obtenerPrediccion(1)).thenReturn(response);

        // When & Then
        mockMvc.perform(get("/api/predicciones/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.skuProducto", is("PROD-001")))
                .andExpect(jsonPath("$.nombreProducto", is("Producto Test")))
                .andExpect(jsonPath("$.demandaPredicha", is(32.00)))
                .andExpect(jsonPath("$.cantidadComprar", is(43)));

        verify(prediccionService, times(1)).obtenerPrediccion(1);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /{id} - Debe retornar 404 cuando predicción no existe")
    void testObtenerPrediccionNoExiste() throws Exception {
        // Given
        when(prediccionService.obtenerPrediccion(999))
                .thenThrow(new EntityNotFoundException("Predicción no encontrada"));

        // When & Then
        mockMvc.perform(get("/api/predicciones/999"))
                .andExpect(status().isNotFound());

        verify(prediccionService, times(1)).obtenerPrediccion(999);
    }

    // ==========================================
    // TESTS: GET /api/predicciones/producto/{sku}
    // ==========================================

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /producto/{sku} - Debe obtener predicciones de un producto")
    void testObtenerPrediccionesProducto() throws Exception {
        // Given
        PrediccionDemandaResponseDTO pred1 = crearPrediccionResponse(
                1, "PROD-001", "Producto Test", 1, "Sucursal Centro",
                2026, 4, "Abril 2026", BigDecimal.valueOf(30.00), BigDecimal.valueOf(32.00),
                50, 11, 43, BigDecimal.valueOf(85.50)
        );

        PrediccionDemandaResponseDTO pred2 = crearPrediccionResponse(
                2, "PROD-001", "Producto Test", 1, "Sucursal Centro",
                2026, 3, "Marzo 2026", BigDecimal.valueOf(28.00), BigDecimal.valueOf(30.00),
                55, 10, 40, BigDecimal.valueOf(88.00)
        );

        when(prediccionService.obtenerPrediccionesProducto("PROD-001", 1))
                .thenReturn(Arrays.asList(pred1, pred2));

        // When & Then
        mockMvc.perform(get("/api/predicciones/producto/PROD-001")
                        .param("sucursalId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].skuProducto", is("PROD-001")))
                .andExpect(jsonPath("$[0].periodoTexto", is("Abril 2026")))
                .andExpect(jsonPath("$[1].periodoTexto", is("Marzo 2026")));

        verify(prediccionService, times(1))
                .obtenerPrediccionesProducto("PROD-001", 1);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /producto/{sku} - Debe retornar lista vacía si producto no tiene predicciones")
    void testObtenerPrediccionesProductoSinPredicciones() throws Exception {
        // Given
        when(prediccionService.obtenerPrediccionesProducto(anyString(), anyInt()))
                .thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/api/predicciones/producto/PROD-NUEVO")
                        .param("sucursalId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /producto/{sku} - Debe requerir parámetro sucursalId")
    void testObtenerPrediccionesProductoSinSucursalId() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/predicciones/producto/PROD-001"))
                .andExpect(status().isBadRequest());

        verify(prediccionService, never())
                .obtenerPrediccionesProducto(anyString(), anyInt());
    }

    // ==========================================
    // TESTS: GET /api/predicciones/recomendaciones
    // ==========================================

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /recomendaciones - Debe obtener recomendaciones de compra")
    void testObtenerRecomendacionesCompra() throws Exception {
        // Given
        PrediccionDemandaResponseDTO pred1 = crearPrediccionResponse(
                1, "PROD-001", "Producto 1", 1, "Sucursal Centro",
                2026, 4, "Abril 2026", BigDecimal.valueOf(30.00), BigDecimal.valueOf(32.00),
                50, 11, 43, BigDecimal.valueOf(85.50)
        );

        PrediccionDemandaResponseDTO pred2 = crearPrediccionResponse(
                2, "PROD-002", "Producto 2", 1, "Sucursal Centro",
                2026, 4, "Abril 2026", BigDecimal.valueOf(45.00), BigDecimal.valueOf(48.00),
                20, 16, 44, BigDecimal.valueOf(90.00)
        );

        RecomendacionCompraDTO recomendacion = new RecomendacionCompraDTO(
                1,
                "Sucursal Centro",
                2026,
                4,
                "Abril 2026",
                150, // totalProductos
                2,   // productosAComprar
                87,  // unidadesTotalesComprar (43 + 44)
                BigDecimal.ZERO,
                Arrays.asList(pred1, pred2)
        );

        when(prediccionService.obtenerRecomendacionesCompra(1, 2026, 4))
                .thenReturn(recomendacion);

        // When & Then
        mockMvc.perform(get("/api/predicciones/recomendaciones")
                        .param("sucursalId", "1")
                        .param("anio", "2026")
                        .param("mes", "4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sucursalId", is(1)))
                .andExpect(jsonPath("$.nombreSucursal", is("Sucursal Centro")))
                .andExpect(jsonPath("$.periodoTexto", is("Abril 2026")))
                .andExpect(jsonPath("$.totalProductos", is(150)))
                .andExpect(jsonPath("$.productosAComprar", is(2)))
                .andExpect(jsonPath("$.unidadesTotalesComprar", is(87)))
                .andExpect(jsonPath("$.productos", hasSize(2)));

        verify(prediccionService, times(1))
                .obtenerRecomendacionesCompra(1, 2026, 4);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /recomendaciones - Debe validar parámetros requeridos")
    void testObtenerRecomendacionesSinParametros() throws Exception {
        // When & Then - Sin sucursalId
        mockMvc.perform(get("/api/predicciones/recomendaciones")
                        .param("anio", "2026")
                        .param("mes", "4"))
                .andExpect(status().isBadRequest());

        // When & Then - Sin año
        mockMvc.perform(get("/api/predicciones/recomendaciones")
                        .param("sucursalId", "1")
                        .param("mes", "4"))
                .andExpect(status().isBadRequest());

        // When & Then - Sin mes
        mockMvc.perform(get("/api/predicciones/recomendaciones")
                        .param("sucursalId", "1")
                        .param("anio", "2026"))
                .andExpect(status().isBadRequest());

        verify(prediccionService, never())
                .obtenerRecomendacionesCompra(anyInt(), anyInt(), anyInt());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /recomendaciones - Debe retornar recomendación vacía si no hay productos para comprar")
    void testObtenerRecomendacionesSinProductosComprar() throws Exception {
        // Given
        RecomendacionCompraDTO recomendacion = new RecomendacionCompraDTO(
                1,
                "Sucursal Centro",
                2026,
                4,
                "Abril 2026",
                100, // totalProductos
                0,   // productosAComprar
                0,   // unidadesTotalesComprar
                BigDecimal.ZERO,
                Collections.emptyList()
        );

        when(prediccionService.obtenerRecomendacionesCompra(1, 2026, 4))
                .thenReturn(recomendacion);

        // When & Then
        mockMvc.perform(get("/api/predicciones/recomendaciones")
                        .param("sucursalId", "1")
                        .param("anio", "2026")
                        .param("mes", "4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productosAComprar", is(0)))
                .andExpect(jsonPath("$.unidadesTotalesComprar", is(0)))
                .andExpect(jsonPath("$.productos", hasSize(0)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /recomendaciones - Debe retornar 404 cuando sucursal no existe")
    void testObtenerRecomendacionesSucursalNoExiste() throws Exception {
        // Given
        when(prediccionService.obtenerRecomendacionesCompra(999, 2026, 4))
                .thenThrow(new EntityNotFoundException("Sucursal no encontrada"));

        // When & Then
        mockMvc.perform(get("/api/predicciones/recomendaciones")
                        .param("sucursalId", "999")
                        .param("anio", "2026")
                        .param("mes", "4"))
                .andExpect(status().isNotFound());
    }

    // ==========================================
    // HELPERS
    // ==========================================

    private PrediccionDemandaResponseDTO crearPrediccionResponse(
            Integer id, String sku, String nombreProducto, 
            Integer sucursalId, String nombreSucursal,
            Integer anio, Integer mes, String periodoTexto,
            BigDecimal demandaHistorica, BigDecimal demandaPredicha,
            Integer stockActual, Integer stockSeguridad, Integer cantidadComprar,
            BigDecimal nivelConfianza) {
        
        PrediccionDemandaResponseDTO dto = new PrediccionDemandaResponseDTO();
        dto.setId(id);
        dto.setSkuProducto(sku);
        dto.setNombreProducto(nombreProducto);
        dto.setSucursalId(sucursalId);
        dto.setNombreSucursal(nombreSucursal);
        dto.setPeriodoAnio(anio);
        dto.setPeriodoMes(mes);
        dto.setPeriodoTexto(periodoTexto);
        dto.setDemandaHistorica(demandaHistorica);
        dto.setDemandaPredicha(demandaPredicha);
        dto.setStockActual(stockActual);
        dto.setStockSeguridad(stockSeguridad);
        dto.setCantidadComprar(cantidadComprar);
        dto.setNivelConfianza(nivelConfianza);
        dto.setMetodoCalculo("PROMEDIO_MOVIL");
        dto.setFechaCalculo(LocalDate.now());
        return dto;
    }
}
