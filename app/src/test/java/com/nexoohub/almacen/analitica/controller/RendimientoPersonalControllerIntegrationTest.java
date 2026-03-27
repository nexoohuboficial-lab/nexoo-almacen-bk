package com.nexoohub.almacen.analitica.controller;

import com.nexoohub.almacen.analitica.dto.RendimientoEmpleadoResponse;
import com.nexoohub.almacen.analitica.service.RendimientoPersonalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@org.springframework.test.context.ActiveProfiles("test")
@org.springframework.transaction.annotation.Transactional
public class RendimientoPersonalControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RendimientoPersonalService rendimientoService;

    @BeforeEach
    void setUp() {
        Mockito.reset(rendimientoService);
    }

    // ──────────────────────────────────────────────────────────────────
    // POST /calcular
    // ──────────────────────────────────────────────────────────────────

    @Test
    @WithMockUser(roles = "ADMIN")
    void calcular_ConRolAdmin_RetornaOk() throws Exception {
        RendimientoEmpleadoResponse r1 = buildResponse(1, 10, new BigDecimal("5000.00"));
        RendimientoEmpleadoResponse r2 = buildResponse(2,  5, new BigDecimal("2000.00"));
        given(rendimientoService.calcularRendimientoMensual(3, 2026)).willReturn(Arrays.asList(r1, r2));

        mockMvc.perform(post("/api/v1/analitica/personal/calcular")
                        .param("mes", "3")
                        .param("anio", "2026")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].empleadoId").value(1))
                .andExpect(jsonPath("$[0].totalVentas").value(10))
                .andExpect(jsonPath("$[1].empleadoId").value(2));
    }

    @Test
    void calcular_SinAuth_RetornaForbidden() throws Exception {
        mockMvc.perform(post("/api/v1/analitica/personal/calcular")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "VENDEDOR")
    void calcular_ConRolVendedor_RetornaForbidden() throws Exception {
        mockMvc.perform(post("/api/v1/analitica/personal/calcular")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    // ──────────────────────────────────────────────────────────────────
    // GET /rendimiento
    // ──────────────────────────────────────────────────────────────────

    @Test
    @WithMockUser(roles = "ADMIN")
    void getDashboard_ConRolAdmin_RetornaLista() throws Exception {
        RendimientoEmpleadoResponse r = buildResponse(5, 8, new BigDecimal("3200.00"));
        given(rendimientoService.obtenerDashboard(3, 2026)).willReturn(Collections.singletonList(r));

        mockMvc.perform(get("/api/v1/analitica/personal/rendimiento")
                        .param("mes", "3")
                        .param("anio", "2026"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].empleadoId").value(5))
                .andExpect(jsonPath("$[0].totalVentas").value(8));
    }

    @Test
    @WithMockUser(roles = "VENDEDOR")
    void getDashboard_ConRolVendedor_RetornaOk() throws Exception {
        given(rendimientoService.obtenerDashboard(anyInt(), anyInt()))
                .willReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/analitica/personal/rendimiento"))
                .andExpect(status().isOk());
    }

    // ──────────────────────────────────────────────────────────────────
    // GET /{empleadoId}/tendencia
    // ──────────────────────────────────────────────────────────────────

    @Test
    @WithMockUser(roles = "ADMIN")
    void getTendencia_ConRolAdmin_RetornaHistorial() throws Exception {
        RendimientoEmpleadoResponse snap1 = buildResponse(3, 12, new BigDecimal("6000.00"));
        RendimientoEmpleadoResponse snap2 = buildResponse(3,  9, new BigDecimal("4500.00"));
        given(rendimientoService.obtenerTendenciaEmpleado(3)).willReturn(Arrays.asList(snap1, snap2));

        mockMvc.perform(get("/api/v1/analitica/personal/3/tendencia"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].totalVentas").value(12))
                .andExpect(jsonPath("$[1].totalVentas").value(9));
    }

    @Test
    void getTendencia_SinAuth_RetornaForbidden() throws Exception {
        mockMvc.perform(get("/api/v1/analitica/personal/1/tendencia"))
                .andExpect(status().isForbidden());
    }

    // ──────────────────────────────────────────────────────────────────
    // HELPER
    // ──────────────────────────────────────────────────────────────────

    private RendimientoEmpleadoResponse buildResponse(int empId, int ventas, BigDecimal monto) {
        RendimientoEmpleadoResponse r = new RendimientoEmpleadoResponse();
        r.setEmpleadoId(empId);
        r.setTotalVentas(ventas);
        r.setMontoTotalVentas(monto);
        r.setTasaConversion(new BigDecimal("50.00"));
        r.setTasaDevolucion(new BigDecimal("5.00"));
        r.setHoraPico(14);
        return r;
    }
}
