package com.nexoohub.almacen.analitica.controller;

import com.nexoohub.almacen.analitica.dto.RendimientoEmpleadoResponse;
import com.nexoohub.almacen.analitica.service.RendimientoPersonalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Controlador REST ANA-04 — Analytics de Rendimiento de Personal.
 *
 * Endpoints:
 *   POST /api/v1/analitica/personal/calcular              — ADMIN: dispara cálculo masivo
 *   GET  /api/v1/analitica/personal/rendimiento           — Vendedor/Admin: dashboard del periodo
 *   GET  /api/v1/analitica/personal/{id}/tendencia        — Vendedor/Admin: evolución mensual
 */
@RestController
@RequestMapping("/api/v1/analitica/personal")
@Tag(name = "ANA-04 · Rendimiento de Personal", description = "KPIs avanzados de rendimiento por vendedor")
public class RendimientoPersonalController {

    private final RendimientoPersonalService service;

    public RendimientoPersonalController(RendimientoPersonalService service) {
        this.service = service;
    }

    // ──────────────────────────────────────────────────────────────────
    // POST /calcular  (solo ADMIN)
    // ──────────────────────────────────────────────────────────────────

    @PostMapping("/calcular")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Calcula el rendimiento de personal para un periodo",
        description = "Proceso masivo: recorre todos los empleados activos y persiste los KPIs mensuales. " +
                      "Si ya existía un snapshot para ese mes/año, lo sobreescribe con datos actualizados. " +
                      "Requiere rol ADMIN.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Cálculo completado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado: se requiere ADMIN")
        }
    )
    public ResponseEntity<List<RendimientoEmpleadoResponse>> calcular(
            @RequestParam(defaultValue = "0") int mes,
            @RequestParam(defaultValue = "0") int anio) {

        // Si no se especifican, usar el mes y año actuales
        if (mes == 0) mes = LocalDate.now().getMonthValue();
        if (anio == 0) anio = LocalDate.now().getYear();

        List<RendimientoEmpleadoResponse> resultado = service.calcularRendimientoMensual(mes, anio);
        return ResponseEntity.ok(resultado);
    }

    // ──────────────────────────────────────────────────────────────────
    // GET /rendimiento  — Dashboard del periodo
    // ──────────────────────────────────────────────────────────────────

    @GetMapping("/rendimiento")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    @Operation(
        summary = "Dashboard de KPIs por vendedor del periodo",
        description = "Devuelve la lista de todos los vendedores con sus KPIs calculados para el mes/año " +
                      "indicado, ordenados de mayor a menor monto de ventas.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Lista de rendimientos obtenida"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
        }
    )
    public ResponseEntity<List<RendimientoEmpleadoResponse>> getDashboard(
            @RequestParam(defaultValue = "0") int mes,
            @RequestParam(defaultValue = "0") int anio) {

        if (mes == 0) mes = LocalDate.now().getMonthValue();
        if (anio == 0) anio = LocalDate.now().getYear();

        return ResponseEntity.ok(service.obtenerDashboard(mes, anio));
    }

    // ──────────────────────────────────────────────────────────────────
    // GET /{empleadoId}/tendencia  — Evolución histórica de un empleado
    // ──────────────────────────────────────────────────────────────────

    @GetMapping("/{empleadoId}/tendencia")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    @Operation(
        summary = "Evolución mensual de un empleado",
        description = "Lista todos los snapshots de rendimiento disponibles para el empleado, " +
                      "del más reciente al más antiguo. Útil para gráficas de tendencia.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Historial obtenido"),
            @ApiResponse(responseCode = "404", description = "Empleado no encontrado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
        }
    )
    public ResponseEntity<List<RendimientoEmpleadoResponse>> getTendencia(
            @PathVariable Integer empleadoId) {
        return ResponseEntity.ok(service.obtenerTendenciaEmpleado(empleadoId));
    }
}
