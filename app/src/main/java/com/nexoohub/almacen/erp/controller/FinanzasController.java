package com.nexoohub.almacen.erp.controller;

import com.nexoohub.almacen.common.ApiResponse;
import com.nexoohub.almacen.erp.dto.*;
import com.nexoohub.almacen.erp.service.CuentaPorPagarService;
import com.nexoohub.almacen.erp.service.GastoOperativoService;
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
@CrossOrigin(origins = "*")
@Tag(name = "ERP — Finanzas", description = "Cuentas por Pagar, Abonos y Gastos Operativos")
public class FinanzasController {

    private final CuentaPorPagarService cxpService;
    private final GastoOperativoService gastoService;

    public FinanzasController(CuentaPorPagarService cxpService, GastoOperativoService gastoService) {
        this.cxpService = cxpService;
        this.gastoService = gastoService;
    }

    // ============================
    // Cuentas por Pagar
    // ============================

    @GetMapping("/api/v1/cxp")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    @Operation(summary = "Listar CxP pendientes", description = "Retorna facturas de proveedores con días de antigüedad")
    public ResponseEntity<ApiResponse<List<CuentaPorPagarResponse>>> listarCxP() {
        List<CuentaPorPagarResponse> pendientes = cxpService.listarPendientes();
        return ResponseEntity.ok(new ApiResponse<>("Cuentas por pagar pendientes", pendientes));
    }

    @PostMapping("/api/v1/cxp")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    @Operation(summary = "Registrar CxP", description = "Crea una nueva deuda a proveedor por factura de compra a crédito")
    public ResponseEntity<ApiResponse<CuentaPorPagarResponse>> registrarCxP(
            @Valid @RequestBody CuentaPorPagarRequest req) {
        CuentaPorPagarResponse response = cxpService.registrar(req);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new ApiResponse<>("Cuenta por pagar registrada", response));
    }

    @PostMapping("/api/v1/cxp/{id}/pagos")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    @Operation(summary = "Abonar a CxP", description = "Registra un pago parcial o total a una cuenta por pagar")
    public ResponseEntity<ApiResponse<CuentaPorPagarResponse>> abonarCxP(
            @PathVariable Integer id,
            @Valid @RequestBody PagoProveedorRequest req) {
        CuentaPorPagarResponse response = cxpService.abonar(id, req);
        return ResponseEntity.ok(new ApiResponse<>("Abono aplicado correctamente", response));
    }

    // ============================
    // Gastos Operativos
    // ============================

    @PostMapping("/api/v1/finanzas/gastos")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    @Operation(summary = "Registrar gasto", description = "Registra un gasto operativo (renta, luz, nómina, etc.)")
    public ResponseEntity<ApiResponse<GastoOperativoResponse>> registrarGasto(
            @Valid @RequestBody GastoOperativoRequest req) {
        GastoOperativoResponse response = gastoService.registrar(req);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new ApiResponse<>("Gasto registrado", response));
    }

    @GetMapping("/api/v1/finanzas/gastos")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    @Operation(summary = "Listar gastos", description = "Lista gastos operativos por rango de fechas y opcionalmente por sucursal")
    public ResponseEntity<ApiResponse<List<GastoOperativoResponse>>> listarGastos(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(required = false) Integer sucursalId) {
        List<GastoOperativoResponse> gastos = gastoService.listar(desde, hasta, sucursalId);
        return ResponseEntity.ok(new ApiResponse<>("Gastos operativos", gastos));
    }
}
