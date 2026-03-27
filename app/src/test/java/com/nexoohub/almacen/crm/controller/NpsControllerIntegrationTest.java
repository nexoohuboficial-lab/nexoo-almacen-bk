package com.nexoohub.almacen.crm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexoohub.almacen.crm.dto.EncuestaNpsRequest;
import com.nexoohub.almacen.crm.dto.NpsDashboardResponse;
import com.nexoohub.almacen.crm.dto.RespuestaNpsRequest;
import com.nexoohub.almacen.crm.service.NpsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class NpsControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private NpsService npsService;

    @Test
    @WithMockUser(roles = "VENDEDOR")
    void crearEncuesta_ShouldReturn201() throws Exception {
        EncuestaNpsRequest request = new EncuestaNpsRequest();
        request.setVentaId(1);

        com.nexoohub.almacen.crm.dto.EncuestaNpsResponse response = new com.nexoohub.almacen.crm.dto.EncuestaNpsResponse();
        response.setEnlaceUnico("UUID-TEST");

        when(npsService.crearEncuesta(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/crm/nps/encuestas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.enlaceUnico").value("UUID-TEST"));
    }

    @Test
    void registrarRespuesta_PublicEndpoint_ShouldReturn200() throws Exception {
        RespuestaNpsRequest request = new RespuestaNpsRequest();
        request.setEnlaceUnico("UUID-TEST");
        request.setScore(10);
        request.setComentarios("Excelente");

        // Sin @WithMockUser porque es público
        mockMvc.perform(post("/api/v1/crm/nps/respuestas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("¡Gracias por tus comentarios! Respuesta registrada exitosamente"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void obtenerDashboard_ShouldReturn200() throws Exception {
        NpsDashboardResponse dashboard = new NpsDashboardResponse();
        dashboard.setScoreNps(new BigDecimal("75.00"));
        dashboard.setTotalRespuestas(100);
        
        when(npsService.obtenerDashboard(any(), any())).thenReturn(dashboard);
        
        mockMvc.perform(get("/api/v1/crm/nps/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.scoreNps").value(75.00))
                .andExpect(jsonPath("$.totalRespuestas").value(100));
    }
}
