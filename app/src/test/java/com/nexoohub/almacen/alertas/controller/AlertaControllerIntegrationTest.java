package com.nexoohub.almacen.alertas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexoohub.almacen.alertas.dto.AlertaResponse;
import com.nexoohub.almacen.alertas.dto.ConfigurarAlertaRequest;
import com.nexoohub.almacen.alertas.entity.TipoAlerta;
import com.nexoohub.almacen.alertas.service.AlertaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@org.springframework.boot.test.context.SpringBootTest
@org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
@org.springframework.test.context.ActiveProfiles("test")
@org.springframework.transaction.annotation.Transactional
@DisplayName("AlertaController — Pruebas de Integración")
class AlertaControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AlertaService alertaService;



    private AlertaResponse buildAlertaResponse() {
        com.nexoohub.almacen.alertas.entity.AlertaSistema alerta = new com.nexoohub.almacen.alertas.entity.AlertaSistema();
        alerta.setId(1);
        alerta.setTipo(TipoAlerta.STOCK_BAJO);
        alerta.setMensaje("⚠️ Stock bajo: Producto X en sucursal Central");
        alerta.setSucursalId(1);
        alerta.setUsuarioDestinoId(1);
        alerta.setResuelta(false);
        alerta.setLeida(false);
        alerta.setFechaCreacion(LocalDateTime.now());
        return AlertaResponse.from(alerta);
    }

    // ──────────────────────── GET /mis-alertas/{usuarioId} ───────────────────

    @Test
    @DisplayName("GET /mis-alertas/{id} → 200 OK si autenticado")
    @WithMockUser(username = "vendedor", roles = {"VENDEDOR"})
    void misAlertas_autenticado_retorna200() throws Exception {
        when(alertaService.listarNoLeidas(1)).thenReturn(List.of(buildAlertaResponse()));

        mockMvc.perform(get("/api/v1/alertas/mis-alertas/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].tipo").value("STOCK_BAJO"))
                .andExpect(jsonPath("$[0].resuelta").value(false));
    }

    @Test
    @DisplayName("GET /mis-alertas/{id} → 403 si no autenticado")
    void misAlertas_sinAuth_retorna403() throws Exception {
        mockMvc.perform(get("/api/v1/alertas/mis-alertas/1"))
                .andExpect(status().isForbidden());
    }

    // ──────────────────────── GET /sucursal/{id} ─────────────────────────────

    @Test
    @DisplayName("GET /sucursal/{id} → 200 OK si ADMIN o GERENTE")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void alertasPorSucursal_admin_retorna200() throws Exception {
        when(alertaService.listarNoResueltasPorSucursal(1)).thenReturn(List.of(buildAlertaResponse()));

        mockMvc.perform(get("/api/v1/alertas/sucursal/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].sucursalId").value(1));
    }

    @Test
    @DisplayName("GET /sucursal/{id} → 403 si rol VENDEDOR")
    @WithMockUser(username = "vendedor", roles = {"VENDEDOR"})
    void alertasPorSucursal_vendedor_retorna403() throws Exception {
        mockMvc.perform(get("/api/v1/alertas/sucursal/1"))
                .andExpect(status().isForbidden());
    }

    // ──────────────────────── GET /badge/{usuarioId} ─────────────────────────

    @Test
    @DisplayName("GET /badge/{id} → retorna conteo no leídas")
    @WithMockUser(username = "vendedor")
    void badge_retornaConteo() throws Exception {
        when(alertaService.contarNoLeidas(anyInt())).thenReturn(5L);

        mockMvc.perform(get("/api/v1/alertas/badge/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.noLeidas").value(5));
    }

    // ─────────────────────── PUT /resolver ───────────────────────────────────

    @Test
    @DisplayName("PUT /resolver → 204 No Content si ADMIN")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void resolver_admin_retorna204() throws Exception {
        doNothing().when(alertaService).marcarResuelta(1);

        mockMvc.perform(put("/api/v1/alertas/1/resolver")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(alertaService, times(1)).marcarResuelta(1);
    }

    @Test
    @DisplayName("PUT /resolver → 403 si rol VENDEDOR")
    @WithMockUser(username = "vendedor", roles = {"VENDEDOR"})
    void resolver_vendedor_retorna403() throws Exception {
        mockMvc.perform(put("/api/v1/alertas/1/resolver")
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    // ─────────────────────── POST /configurar-sucursal ───────────────────────

    @Test
    @DisplayName("POST /configurar-sucursal → 200 OK si ADMIN")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void configurarSucursal_admin_retorna200() throws Exception {
        ConfigurarAlertaRequest request = new ConfigurarAlertaRequest();

        // Para acceder a los setters necesitamos usar reflection dado el @NoArgsConstructor privado
        // en su lugar creamos el JSON directamente
        String requestJson = """
                {
                    "sucursalId": 1,
                    "stockMinimo": 10,
                    "diasVencimientoCxC": 30,
                    "porcentajeMetaAlerta": 60
                }""";

        doNothing().when(alertaService).configurarSucursal(any());

        mockMvc.perform(post("/api/v1/alertas/configurar-sucursal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Configuración de alertas actualizada para la sucursal 1"));
    }

    @Test
    @DisplayName("POST /configurar-sucursal → 403 si no es ADMIN")
    @WithMockUser(username = "gerente", roles = {"GERENTE"})
    void configurarSucursal_gerente_retorna403() throws Exception {
        mockMvc.perform(post("/api/v1/alertas/configurar-sucursal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sucursalId\":1}")
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }
}
