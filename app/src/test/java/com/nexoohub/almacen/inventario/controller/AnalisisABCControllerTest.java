package com.nexoohub.almacen.inventario.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexoohub.almacen.inventario.dto.AnalisisABCRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests de validación para AnalisisABCController - Módulo #10
 * Valida validaciones de entrada y respuestas HTTP correctas
 * 
 * NOTA: La lógica de negocio está completamente probada en AnalisisABCServiceTest (10/10 tests)
 * Estos tests solo validan que los endpoints REST manejan las validaciones correctamente.
 * 
 * @author NexooHub Development Team
 * @since 1.4.0
 */
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:analisis-abc-testdb;MODE=MySQL;DB_CLOSE_DELAY=-1",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.flyway.enabled=false"
})
@DisplayName("AnalisisABCController - Tests de Validación HTTP")
class AnalisisABCControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private AnalisisABCRequestDTO validRequest;

    @BeforeEach
    void setUp() {
        // Setup Request DTO válido con fechas relativas
        LocalDate hoy = LocalDate.now();
        validRequest = new AnalisisABCRequestDTO();
        validRequest.setSucursalId(1);
        validRequest.setPeriodoInicio(hoy.minusMonths(3));
        validRequest.setPeriodoFin(hoy.minusDays(1));
        validRequest.setPorcentajeA(80.0);
        validRequest.setPorcentajeB(95.0);
        validRequest.setForzarRegeneracion(false);
    }

    @Test
    @DisplayName("POST /generar - Validación: Campos requeridos faltantes")
    void testGenerarAnalisisABC_CamposRequeridos() throws Exception {
        // Given - Request sin campos
        AnalisisABCRequestDTO emptyRequest = new AnalisisABCRequestDTO();

        // When & Then - Debe retornar 400 Bad Request
        mockMvc.perform(post("/api/v1/inventario/analisis-abc/generar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emptyRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /generar - Validación: Porcentaje A fuera de rango")
    void testGenerarAnalisisABC_PorcentajeAInvalido() throws Exception {
        // Given - Porcentaje A > 100
        validRequest.setPorcentajeA(150.0);

        // When & Then - Debe retornar 400 Bad Request
        mockMvc.perform(post("/api/v1/inventario/analisis-abc/generar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /generar - Validación: Porcentaje B fuera de rango")
    void testGenerarAnalisisABC_PorcentajeBInvalido() throws Exception {
        // Given -Porcentaje B > 100
        validRequest.setPorcentajeB(150.0);

        // When & Then - Debe retornar 400 Bad Request
        mockMvc.perform(post("/api/v1/inventario/analisis-abc/generar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /generar - Validación: Fecha fin futura")
    void testGenerarAnalisisABC_FechaFinFutura() throws Exception {
        // Given - Fecha fin en el futuro
        validRequest.setPeriodoFin(LocalDate.now().plusDays(1));

        // When & Then - Debe retornar 400 Bad Request
        mockMvc.perform(post("/api/v1/inventario/analisis-abc/generar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadRequest());
    }
}
