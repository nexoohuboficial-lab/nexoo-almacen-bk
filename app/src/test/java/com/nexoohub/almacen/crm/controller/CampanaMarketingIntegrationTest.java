package com.nexoohub.almacen.crm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexoohub.almacen.crm.dto.CampanaMarketingRequest;
import com.nexoohub.almacen.crm.entity.CampanaMarketing;
import com.nexoohub.almacen.crm.repository.CampanaMarketingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class CampanaMarketingIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CampanaMarketingRepository campanaRepository;

    private CampanaMarketing campanaGuardada;

    @BeforeEach
    void setUp() {
        campanaRepository.deleteAll();
        
        CampanaMarketing c = new CampanaMarketing();
        c.setNombre("Campaña de Prueba Integración");
        c.setSegmentoObjetivo("TODOS");
        c.setCanal("EMAIL");
        c.setEstado("BORRADOR");
        c.setContenidoPlantilla("Hola, este es un mensaje.");
        c.setCreadoPorUsuarioId(1);
        c.setFechaProgramada(LocalDateTime.now().plusDays(1));
        campanaGuardada = campanaRepository.save(c);
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void crearCampana_Exitosa() throws Exception {
        CampanaMarketingRequest request = new CampanaMarketingRequest();
        request.setNombre("Campaña Nueva");
        request.setSegmentoObjetivo("MOROSOS");
        request.setCanal("WHATSAPP");
        request.setContenidoPlantilla("Estimado cliente, regularice su pago.");
        request.setCreadoPorUsuarioId(2);

        mockMvc.perform(post("/api/v1/marketing/campanas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Campaña Nueva"))
                .andExpect(jsonPath("$.estado").value("BORRADOR"));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void ejecutarCampana_Exitosa() throws Exception {
        mockMvc.perform(post("/api/v1/marketing/campanas/" + campanaGuardada.getId() + "/ejecutar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("FINALIZADA"))
                .andExpect(jsonPath("$.totalDestinatarios").exists());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void obtenerMetricas_Existente() throws Exception {
        // Ejecutamos primero para que se generen métricas o logs
        mockMvc.perform(post("/api/v1/marketing/campanas/" + campanaGuardada.getId() + "/ejecutar"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/marketing/campanas/" + campanaGuardada.getId() + "/metricas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.campanaId").value(campanaGuardada.getId()))
                .andExpect(jsonPath("$.estado").value("FINALIZADA"))
                .andExpect(jsonPath("$.totalEnviados").exists());
    }
}
