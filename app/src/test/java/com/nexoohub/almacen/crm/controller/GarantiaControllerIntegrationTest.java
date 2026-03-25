package com.nexoohub.almacen.crm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexoohub.almacen.crm.dto.ResolucionGarantiaRequest;
import com.nexoohub.almacen.crm.dto.TicketGarantiaRequest;
import com.nexoohub.almacen.crm.entity.TicketGarantia;
import com.nexoohub.almacen.crm.repository.TicketGarantiaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false) // Deshabilita la seguridad en tests genéricos si no hay interceptores rígidos
@ActiveProfiles("test")
@Transactional
class GarantiaControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TicketGarantiaRepository ticketGarantiaRepository;

    private TicketGarantia ticketExistente;

    @BeforeEach
    void setUp() {
        ticketExistente = new TicketGarantia();
        ticketExistente.setClienteId(200);
        ticketExistente.setVentaId(300);
        ticketExistente.setSkuProducto("SKU-INT-123");
        ticketExistente.setEstado("ABIERTO");
        ticketExistente.setMotivoReclamo("Falla inicial integración");
        ticketExistente = ticketGarantiaRepository.saveAndFlush(ticketExistente);
    }

    @Test
    void abrirTicket_DebeRetornarCreated() throws Exception {
        TicketGarantiaRequest request = new TicketGarantiaRequest();
        request.setVentaId(400);
        request.setClienteId(200);
        request.setSkuProducto("NUEVO-SKU-999");
        request.setNumeroSerie("SERIE1234");
        request.setMotivoReclamo("No enciende al usar batería");
        request.setUsuarioAperturaId(99);

        mockMvc.perform(post("/api/crm/garantias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.skuProducto", is("NUEVO-SKU-999")))
                .andExpect(jsonPath("$.estado", is("ABIERTO")))
                .andExpect(jsonPath("$.motivoReclamo", is("No enciende al usar batería")));
    }

    @Test
    void obtenerTicketsPorCliente_DebeRetornarLista() throws Exception {
        mockMvc.perform(get("/api/crm/garantias/cliente/200"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].skuProducto", is("SKU-INT-123")));
    }

    @Test
    void cambiarEstado_DebeRetornarEstadoActualizado() throws Exception {
        mockMvc.perform(put("/api/crm/garantias/{ticketId}/estado", ticketExistente.getId())
                        .param("nuevoEstado", "EN_REVISION")
                        .param("comentario", "Se pasa a revisión técnica en lab")
                        .param("usuarioId", "101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado", is("EN_REVISION")));
    }

    @Test
    void resolverTicket_DebeFinalizarTicketConFalloYEstado() throws Exception {
        ResolucionGarantiaRequest resolucion = new ResolucionGarantiaRequest();
        resolucion.setTipoResolucion("REPARACION");
        resolucion.setNotasInternas("Soldadura aplicada a placa base");
        resolucion.setUsuarioResolucionId(102);

        mockMvc.perform(put("/api/crm/garantias/{ticketId}/resolver", ticketExistente.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resolucion)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado", is("RESUELTO")))
                .andExpect(jsonPath("$.resolucion", is("REPARACION")))
                .andExpect(jsonPath("$.notasInternas", is("Soldadura aplicada a placa base")));
    }
}
