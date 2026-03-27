package com.nexoohub.almacen.alertas.controller;

import com.nexoohub.almacen.alertas.dto.AlertaResponse;
import com.nexoohub.almacen.alertas.dto.ConfigNotificacionRequest;
import com.nexoohub.almacen.alertas.dto.ConfigurarAlertaRequest;
import com.nexoohub.almacen.alertas.service.AlertaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controlador REST del Motor de Alertas y Notificaciones (PRO-01).
 *
 * <p>Endpoints:
 * <ul>
 *   <li>GET  /api/v1/alertas/mis-alertas          → alertas no leídas del usuario autenticado.</li>
 *   <li>GET  /api/v1/alertas/sucursal/{id}         → alertas no resueltas de una sucursal (ADMIN/GERENTE).</li>
 *   <li>PUT  /api/v1/alertas/{id}/resolver         → marcar alerta como resuelta (ADMIN/GERENTE).</li>
 *   <li>POST /api/v1/alertas/configurar-sucursal   → configurar umbrales por sucursal (ADMIN).</li>
 *   <li>POST /api/v1/alertas/configurar-canal      → configurar canal de notificación del usuario.</li>
 *   <li>GET  /api/v1/alertas/badge/{usuarioId}     → conteo de alertas no leídas para badge en UI.</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/v1/alertas")
@RequiredArgsConstructor
public class AlertaController {

    private final AlertaService alertaService;

    // ───────────────────────── CONSULTAS ──────────────────────────────────────

    /**
     * Alertas no leídas del usuario autenticado.
     * Todos los roles autenticados pueden consultar sus propias alertas.
     */
    @GetMapping("/mis-alertas/{usuarioId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<AlertaResponse>> misAlertas(@PathVariable Integer usuarioId) {
        List<AlertaResponse> alertas = alertaService.listarNoLeidas(usuarioId);
        return ResponseEntity.ok(alertas);
    }

    /**
     * Alertas no resueltas de una sucursal (para panel de administración).
     */
    @GetMapping("/sucursal/{sucursalId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<List<AlertaResponse>> alertasPorSucursal(@PathVariable Integer sucursalId) {
        List<AlertaResponse> alertas = alertaService.listarNoResueltasPorSucursal(sucursalId);
        return ResponseEntity.ok(alertas);
    }

    /**
     * Conteo de alertas no leídas de un usuario (para badge en la UI).
     */
    @GetMapping("/badge/{usuarioId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Long>> badge(@PathVariable Integer usuarioId) {
        long count = alertaService.contarNoLeidas(usuarioId);
        return ResponseEntity.ok(Map.of("noLeidas", count));
    }

    // ───────────────────────── ACCIONES ───────────────────────────────────────

    /**
     * Marca una alerta como resuelta (cierre definitivo).
     */
    @PutMapping("/{alertaId}/resolver")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<Void> resolver(@PathVariable Integer alertaId) {
        alertaService.marcarResuelta(alertaId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Marca una alerta como leída.
     */
    @PutMapping("/{alertaId}/leer")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> marcarLeida(@PathVariable Integer alertaId) {
        alertaService.marcarLeida(alertaId);
        return ResponseEntity.noContent().build();
    }

    // ───────────────────────── CONFIGURACIÓN ──────────────────────────────────

    /**
     * Configura los umbrales de alerta (stock mínimo, días CxC, % meta) para una sucursal.
     * Solo ADMIN puede modificar estos parámetros.
     */
    @PostMapping("/configurar-sucursal")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> configurarSucursal(
            @Valid @RequestBody ConfigurarAlertaRequest request) {
        alertaService.configurarSucursal(request);
        return ResponseEntity.ok(Map.of(
                "mensaje", "Configuración de alertas actualizada para la sucursal " + request.getSucursalId()));
    }

    /**
     * Registra o actualiza el canal de notificación preferido del usuario.
     * Cualquier usuario autenticado puede configurar su propio canal.
     */
    @PostMapping("/configurar-canal")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, String>> configurarCanal(
            @Valid @RequestBody ConfigNotificacionRequest request) {
        alertaService.configurarCanalUsuario(request);
        return ResponseEntity.ok(Map.of(
                "mensaje", "Canal de notificación actualizado: " + request.getCanal().name()));
    }
}
