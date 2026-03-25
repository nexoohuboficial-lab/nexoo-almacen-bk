package com.nexoohub.almacen.erp.controller;

import com.nexoohub.almacen.common.ApiResponse;
import com.nexoohub.almacen.erp.dto.CuentaContableDTO;
import com.nexoohub.almacen.erp.dto.PolizaContableRequest;
import com.nexoohub.almacen.erp.dto.PolizaContableResponse;
import com.nexoohub.almacen.erp.dto.reportes.BalanzaComprobacionResponse;
import com.nexoohub.almacen.erp.dto.reportes.EstadoResultadosResponse;
import com.nexoohub.almacen.erp.service.ContabilidadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/contabilidad")
@CrossOrigin(origins = "*")
@Tag(name = "ERP — Contabilidad General", description = "Gestión de Catálogo de Cuentas, Pólizas, Balanza y Resultados")
public class ContabilidadController {

    private final ContabilidadService contabilidadService;

    public ContabilidadController(ContabilidadService contabilidadService) {
        this.contabilidadService = contabilidadService;
    }

    // ============================
    // Cuentas Contables
    // ============================

    @GetMapping("/cuentas")
    @PreAuthorize("hasAnyRole('ADMIN', 'CONTADOR', 'SUPERVISOR')")
    @Operation(summary = "Catálogo de cuentas", description = "Lista cuentas activas ordenadas por código contable")
    public ResponseEntity<ApiResponse<List<CuentaContableDTO>>> listarCuentas() {
        return ResponseEntity.ok(new ApiResponse<>("Catálogo de Cuentas Contables", contabilidadService.listarCuentas()));
    }

    // ============================
    // Pólizas Contables
    // ============================

    @PostMapping("/polizas")
    @PreAuthorize("hasAnyRole('ADMIN', 'CONTADOR')")
    @Operation(summary = "Crear póliza contable", description = "Registra una póliza de diario/ingreso/egreso aplicando partida doble estricta")
    public ResponseEntity<ApiResponse<PolizaContableResponse>> registrarPoliza(
            @Valid @RequestBody PolizaContableRequest req) {
        PolizaContableResponse resp = contabilidadService.registrarPoliza(req);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new ApiResponse<>("Póliza contable registrada exitosamente", resp));
    }

    @GetMapping("/polizas")
    @PreAuthorize("hasAnyRole('ADMIN', 'CONTADOR', 'SUPERVISOR')")
    @Operation(summary = "Listar pólizas", description = "Lista las pólizas contables registradas en un rango de fechas")
    public ResponseEntity<ApiResponse<List<PolizaContableResponse>>> listarPolizas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return ResponseEntity.ok(new ApiResponse<>("Pólizas encontradas", contabilidadService.listarPolizas(desde, hasta)));
    }

    // ============================
    // Reportes Contables
    // ============================

    @GetMapping("/reportes/balanza")
    @PreAuthorize("hasAnyRole('ADMIN', 'CONTADOR', 'DIRECTOR')")
    @Operation(summary = "Balanza de comprobación", description = "Genera reporte de saldos de todas las cuentas con validación de cuadre total")
    public ResponseEntity<ApiResponse<BalanzaComprobacionResponse>> generarBalanza(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return ResponseEntity.ok(new ApiResponse<>("Balanza de comprobación", contabilidadService.generarBalanza(desde, hasta)));
    }

    @GetMapping("/reportes/estado-resultados")
    @PreAuthorize("hasAnyRole('ADMIN', 'CONTADOR', 'DIRECTOR')")
    @Operation(summary = "Estado de Resultados (P&L)", description = "Calcula ingresos menos costos y gastos del periodo especificado para obtener la Utilidad Neta")
    public ResponseEntity<ApiResponse<EstadoResultadosResponse>> generarEstadoResultados(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return ResponseEntity.ok(new ApiResponse<>("Estado de Resultados", contabilidadService.generarEstadoResultados(desde, hasta)));
    }
}
