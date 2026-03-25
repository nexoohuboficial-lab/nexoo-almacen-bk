package com.nexoohub.almacen.crm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexoohub.almacen.crm.dto.CambioEtapaOportunidadRequest;
import com.nexoohub.almacen.crm.dto.InteraccionCrmRequest;
import com.nexoohub.almacen.crm.dto.OportunidadVentaRequest;
import com.nexoohub.almacen.crm.dto.ProspectoRequest;
import com.nexoohub.almacen.crm.repository.InteraccionCrmRepository;
import com.nexoohub.almacen.crm.repository.OportunidadVentaRepository;
import com.nexoohub.almacen.crm.repository.ProspectoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.security.test.context.support.WithMockUser;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser(username = "admin", roles = {"ADMIN"})
class PipelineB2BIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProspectoRepository prospectoRepository;

    @Autowired
    private OportunidadVentaRepository oportunidadRepository;

    @Autowired
    private InteraccionCrmRepository interaccionRepository;

    @BeforeEach
    void setup() {
        interaccionRepository.deleteAll();
        oportunidadRepository.deleteAll();
        prospectoRepository.deleteAll();
    }

    @Test
    void testFlujoCompletoB2B() throws Exception {
        // 1. Crear Prospecto
        ProspectoRequest prospectoReq = new ProspectoRequest();
        prospectoReq.setEmpresa("TechCorp Inversiones");
        prospectoReq.setRfc("TCI991212XYZ");
        prospectoReq.setContactoPrincipal("Juan Pérez");
        prospectoReq.setCorreo("juan@techcorp.com");
        prospectoReq.setTelefono("555-1234");
        prospectoReq.setNotas("Cliente interesado en refacciones de montacargas");

        String prospectoResponse = mockMvc.perform(post("/api/v1/crm/prospectos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(prospectoReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.empresa", is("TechCorp Inversiones")))
                .andExpect(jsonPath("$.estatusViabilidad", is("NUEVO")))
                .andReturn().getResponse().getContentAsString();

        Integer prospectoId = objectMapper.readTree(prospectoResponse).get("id").asInt();

        // 2. Crear Oportunidad Venta
        OportunidadVentaRequest oportunidadReq = new OportunidadVentaRequest();
        oportunidadReq.setProspectoId(prospectoId);
        oportunidadReq.setTitulo("Venta 5 Montacargas Toyota");
        oportunidadReq.setValorProyectado(new BigDecimal("1500000.00"));
        oportunidadReq.setEtapa("DESCUBRIMIENTO");
        oportunidadReq.setFechaCierreEstimada(LocalDate.now().plusMonths(1));
        oportunidadReq.setProbabilidadPorcentaje(20);

        String oportunidadResponse = mockMvc.perform(post("/api/v1/crm/oportunidades")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(oportunidadReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.titulo", is("Venta 5 Montacargas Toyota")))
                .andReturn().getResponse().getContentAsString();

        Integer oportunidadId = objectMapper.readTree(oportunidadResponse).get("id").asInt();

        // Verificar cambio de estado del prospecto al crear oportunidad ("NUEVO" -> "EN_PROGRESO")
        mockMvc.perform(get("/api/v1/crm/prospectos/{id}", prospectoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estatusViabilidad", is("EN_PROGRESO")));

        // Verificar que se creó interacción automática al crear oportunidad
        mockMvc.perform(get("/api/v1/crm/prospectos/{id}/interacciones", prospectoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].tipoInteraccion", is("CREACION_OPORTUNIDAD")));

        // 3. Registrar Interacción Manual
        InteraccionCrmRequest interaccionReq = new InteraccionCrmRequest();
        interaccionReq.setProspectoId(prospectoId);
        interaccionReq.setOportunidadId(oportunidadId);
        interaccionReq.setTipoInteraccion("REUNION_PRESENCIAL");
        interaccionReq.setResumen("Revisión de catálogo en sitio");
        interaccionReq.setDetalles("Se mostró el catálogo Toyota. Muy interesados.");

        mockMvc.perform(post("/api/v1/crm/interacciones")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(interaccionReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.tipoInteraccion", is("REUNION_PRESENCIAL")));

        // 4. Cambiar etapa de la oportunidad a CERRADA_GANADA
        CambioEtapaOportunidadRequest etapaReq = new CambioEtapaOportunidadRequest();
        etapaReq.setEtapa("CERRADA_GANADA");
        etapaReq.setProbabilidadPorcentaje(100);

        mockMvc.perform(patch("/api/v1/crm/oportunidades/{id}/etapa", oportunidadId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(etapaReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.etapa", is("CERRADA_GANADA")))
                .andExpect(jsonPath("$.probabilidadPorcentaje", is(100)));

        // 5. Verificar que el prospecto se convirtió en CLIENTE al ganar la oportunidad
        mockMvc.perform(get("/api/v1/crm/prospectos/{id}", prospectoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estatusViabilidad", is("CLIENTE")));
    }
}
