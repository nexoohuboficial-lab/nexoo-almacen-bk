package com.nexoohub.almacen.pos.controller;

import com.nexoohub.almacen.common.ApiResponse;
import com.nexoohub.almacen.pos.dto.CancelacionPagoRequest;
import com.nexoohub.almacen.pos.dto.PagoTarjetaRequest;
import com.nexoohub.almacen.pos.dto.TransaccionBancariaResponse;
import com.nexoohub.almacen.pos.service.TerminalBancariaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controller REST para la integración con Terminales Bancarias (POS-02).
 *
 * <p>Base URL: /api/v1/pos/pagos</p>
 */
@RestController
@RequestMapping("/api/v1/pos/pagos")
@CrossOrigin(origins = "*")
@Tag(name = "Terminales Bancarias", description = "Operaciones de cobro y log con PinPads (POS-02)")
public class TerminalBancariaController {

    private final TerminalBancariaService service;

    public TerminalBancariaController(TerminalBancariaService service) {
        this.service = service;
    }

    @PostMapping("/tarjeta")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'CAJERO', 'VENDEDOR')")
    @Operation(summary = "Enviar cobro a terminal", description = "Inicia instrucción de cobro por tarjeta")
    public ResponseEntity<ApiResponse<TransaccionBancariaResponse>> cobrarConTarjeta(
            @Valid @RequestBody PagoTarjetaRequest request) {
        TransaccionBancariaResponse tx = service.procesarPagoTarjeta(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new ApiResponse<>("Transacción procesada", tx));
    }

    @PostMapping("/cancelar")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'CAJERO')")
    @Operation(summary = "Cancelar cobro previo", description = "Reversa un cobro previamente aprobado")
    public ResponseEntity<ApiResponse<TransaccionBancariaResponse>> cancelarPago(
            @Valid @RequestBody CancelacionPagoRequest request) {
        TransaccionBancariaResponse tx = service.cancelarPago(request);
        return ResponseEntity.ok(new ApiResponse<>("Transacción cancelada", tx));
    }

    @GetMapping("/{referencia}/estatus")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'CAJERO', 'VENDEDOR')")
    @Operation(summary = "Consultar estatus de cobro", description = "Obtiene estado logueado de una transacción")
    public ResponseEntity<ApiResponse<TransaccionBancariaResponse>> consultarEstatus(
            @PathVariable String referencia) {
        TransaccionBancariaResponse tx = service.consultarEstatus(referencia);
        return ResponseEntity.ok(new ApiResponse<>("Consulta exitosa", tx));
    }
}
