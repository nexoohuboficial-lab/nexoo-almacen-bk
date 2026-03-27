package com.nexoohub.almacen.analitica.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexoohub.almacen.analitica.dto.RfmCalcularResponse;
import com.nexoohub.almacen.analitica.dto.RfmClienteResponse;
import com.nexoohub.almacen.analitica.dto.RfmSegmentoStatsResponse;
import com.nexoohub.almacen.analitica.service.RfmService;
import com.nexoohub.almacen.common.exception.ResourceNotFoundException;
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

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@org.springframework.test.context.ActiveProfiles("test")
@org.springframework.transaction.annotation.Transactional
public class RfmControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RfmService rfmService;

    @BeforeEach
    void setUp() {
        Mockito.reset(rfmService);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void calcularRfmMasivo_ConRolAdmin_RetornaOk() throws Exception {
        RfmCalcularResponse mockResponse = new RfmCalcularResponse("OK", 5);
        given(rfmService.calcularRfmMasivo()).willReturn(mockResponse);

        mockMvc.perform(post("/api/v1/analitica/rfm/calcular")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("OK"))
                .andExpect(jsonPath("$.clientesEvaluados").value(5));
    }

    @Test
    void calcularRfmMasivo_SinAuth_RetornaForbidden() throws Exception {
        mockMvc.perform(post("/api/v1/analitica/rfm/calcular")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "VENDEDOR")
    void consultarSegmentoCliente_ConRolVendedor_RetornaRfmCliente() throws Exception {
        RfmClienteResponse response = new RfmClienteResponse();
        response.setClienteId(10);
        response.setNombreCliente("Empresa SA");
        response.setSegmento("CAMPEON");
        response.setScoreR(5);
        response.setScoreF(5);
        response.setScoreM(5);

        given(rfmService.obtenerRfmPorCliente(10)).willReturn(response);

        mockMvc.perform(get("/api/v1/analitica/rfm/cliente/10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clienteId").value(10))
                .andExpect(jsonPath("$.segmento").value("CAMPEON"))
                .andExpect(jsonPath("$.scoreR").value(5));
    }

    @Test
    @WithMockUser(roles = "VENDEDOR")
    void consultarSegmentoCliente_ClienteInexistente_RetornaNotFound() throws Exception {
        given(rfmService.obtenerRfmPorCliente(anyInt()))
                .willThrow(new ResourceNotFoundException("No existen métricas"));

        mockMvc.perform(get("/api/v1/analitica/rfm/cliente/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "GERENTE")
    void obtenerEstadisticasRfm_ConRolGerente_RetornaLista() throws Exception {
        RfmSegmentoStatsResponse stat1 = new RfmSegmentoStatsResponse("CAMPEON", 10L);
        RfmSegmentoStatsResponse stat2 = new RfmSegmentoStatsResponse("EN_RIESGO", 5L);

        given(rfmService.agruparClientesPorSegmento()).willReturn(Arrays.asList(stat1, stat2));

        mockMvc.perform(get("/api/v1/analitica/rfm/segmentos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].segmento").value("CAMPEON"))
                .andExpect(jsonPath("$.[0].cantidad").value(10))
                .andExpect(jsonPath("$.[1].segmento").value("EN_RIESGO"))
                .andExpect(jsonPath("$.[1].cantidad").value(5));
    }
}
