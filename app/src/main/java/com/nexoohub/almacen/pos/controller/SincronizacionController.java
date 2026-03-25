package com.nexoohub.almacen.pos.controller;

import com.nexoohub.almacen.common.ApiResponse;
import com.nexoohub.almacen.pos.dto.LoteSincronizacionResponse;
import com.nexoohub.almacen.pos.dto.SyncLoteRequest;
import com.nexoohub.almacen.pos.service.SincronizacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para gestionar Sincronización Offline (POS-04).
 */
@RestController
@RequestMapping("/api/v1/sincronizacion")
@CrossOrigin(origins = "*")
@Tag(name = "Sincronización Offline", description = "Endpoints para subir y conciliar lotes de cajas sin conexión")
public class SincronizacionController {

    private final SincronizacionService sincronizacionService;

    public SincronizacionController(SincronizacionService sincronizacionService) {
        this.sincronizacionService = sincronizacionService;
    }

    @PostMapping("/lote")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'CAJERO')")
    @Operation(summary = "Sincronizar lote de ventas", description = "Guarda las ventas realizadas offline cuando regresa la conexión")
    public ResponseEntity<ApiResponse<LoteSincronizacionResponse>> recibirLote(
            @Valid @RequestBody SyncLoteRequest request) {
        LoteSincronizacionResponse response = sincronizacionService.procesarLote(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new ApiResponse<>("Lote recibido y procesado", response));
    }

    @PostMapping("/reintentar")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    @Operation(summary = "Reintentar lote", description = "Reintenta procesar un lote marcado como FALLIDO")
    public ResponseEntity<ApiResponse<LoteSincronizacionResponse>> reintentarLote(
            @RequestParam String codigoLote) {
        LoteSincronizacionResponse response = sincronizacionService.reintentarLote(codigoLote);
        return ResponseEntity.ok(new ApiResponse<>("Reintento de lote ejecutado", response));
    }

    @GetMapping("/pendientes")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    @Operation(summary = "Lotes fallidos o pendientes", description = "Devuelve los lotes que requieren conciliación manual")
    public ResponseEntity<ApiResponse<List<LoteSincronizacionResponse>>> listarPendientes() {
        List<LoteSincronizacionResponse> fallidos = sincronizacionService.listarLotesFallidos();
        return ResponseEntity.ok(new ApiResponse<>("Lista de lotes fallidos", fallidos));
    }
}
