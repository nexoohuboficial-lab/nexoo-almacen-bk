package com.nexoohub.almacen.analitica.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexoohub.almacen.analitica.entity.PrediccionChurnCliente;
import com.nexoohub.almacen.analitica.repository.PrediccionChurnClienteRepository;
import com.nexoohub.almacen.analitica.service.ChurnPredictionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ChurnPredictionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChurnPredictionService churnPredictionService;

    @MockBean
    private PrediccionChurnClienteRepository churnRepository;

    @Test
    @WithMockUser(roles = "ADMIN")
    void calcularChurnMasivo_Success() throws Exception {
        doNothing().when(churnPredictionService).calcularRiesgoChurnGlobal();

        mockMvc.perform(post("/api/v1/analitica/churn/calcular"))
                .andExpect(status().isOk());

        verify(churnPredictionService, times(1)).calcularRiesgoChurnGlobal();
    }

    @Test
    @WithMockUser(roles = "VENDEDOR")
    void calcularChurnMasivo_ForbiddenForVendedor() throws Exception {
        mockMvc.perform(post("/api/v1/analitica/churn/calcular"))
                .andExpect(status().isForbidden());
        
        verify(churnPredictionService, never()).calcularRiesgoChurnGlobal();
    }

    @Test
    @WithMockUser(roles = "VENDEDOR")
    void obtenerClientesEnRiesgo_Success() throws Exception {
        PrediccionChurnCliente pcc = new PrediccionChurnCliente();
        pcc.setClienteId(1);
        pcc.setScoreRiesgo(85);
        pcc.setFactoresRiesgo("Riesgo Alto");
        
        when(churnRepository.findByScoreRiesgoGreaterThanEqualOrderByScoreRiesgoDesc(70)).thenReturn(Collections.singletonList(pcc));

        mockMvc.perform(get("/api/v1/analitica/churn/en-riesgo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].clienteId").value(1))
                .andExpect(jsonPath("$[0].scoreRiesgo").value(85))
                .andExpect(jsonPath("$[0].factoresRiesgo").value("Riesgo Alto"));
    }
}
