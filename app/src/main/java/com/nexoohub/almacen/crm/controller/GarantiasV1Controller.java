package com.nexoohub.almacen.crm.controller;

import com.nexoohub.almacen.crm.dto.ResolucionGarantiaRequest;
import com.nexoohub.almacen.crm.dto.TicketGarantiaRequest;
import com.nexoohub.almacen.crm.dto.TicketGarantiaResponse;
import com.nexoohub.almacen.crm.service.GarantiaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/garantias")
@RequiredArgsConstructor
@Tag(name = "Garantías V1", description = "Módulo de gestión y resolución de garantías de productos")
public class GarantiasV1Controller {

    private final GarantiaService garantiaService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR', 'AUDITOR')")
    @Operation(summary = "Listar todas las garantías", description = "Recupera la lista de todos los tickets de garantía en el sistema")
    public ResponseEntity<List<TicketGarantiaResponse>> listarGarantias() {
        return ResponseEntity.ok(garantiaService.listarTodos());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    @Operation(summary = "Abrir ticket de garantía", description = "Registra un nuevo reclamo o ticket de garantía para un producto")
    public ResponseEntity<TicketGarantiaResponse> abrirTicket(@Valid @RequestBody TicketGarantiaRequest request) {
        TicketGarantiaResponse response = garantiaService.abrirTicket(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/venta/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR', 'AUDITOR')")
    @Operation(summary = "Obtener tickets por venta", description = "Lista todos los tickets de garantía vinculados a una venta específica")
    public ResponseEntity<List<TicketGarantiaResponse>> obtenerTicketsPorVenta(@PathVariable("id") Integer ventaId) {
        return ResponseEntity.ok(garantiaService.getHistorialVenta(ventaId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR', 'AUDITOR')")
    @Operation(summary = "Obtener ticket por ID", description = "Recupera la información detallada de un ticket de garantía")
    public ResponseEntity<TicketGarantiaResponse> obtenerTicketPorId(@PathVariable("id") Integer ticketId) {
        return ResponseEntity.ok(garantiaService.getTicketPorId(ticketId));
    }

    @PutMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Actualizar estado del ticket", description = "Cambia el estado de un ticket de garantía (ej. EN_REVISION, CERRADO)")
    public ResponseEntity<TicketGarantiaResponse> cambiarEstado(
            @PathVariable("id") Integer ticketId,
            @RequestParam String nuevoEstado,
            @RequestParam String comentario,
            @RequestParam Integer usuarioId) {
            
        TicketGarantiaResponse response = garantiaService.cambiarEstado(ticketId, nuevoEstado, comentario, usuarioId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/resolver")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Resolver ticket de garantía", description = "Dictamina la resolución del ticket (CAMBIO_PIEZA, DEVOLUCION_DINERO, etc.)")
    public ResponseEntity<TicketGarantiaResponse> resolverTicket(
            @PathVariable("id") Integer ticketId,
            @Valid @RequestBody ResolucionGarantiaRequest request) {
            
        TicketGarantiaResponse response = garantiaService.resolverTicket(ticketId, request);
        return ResponseEntity.ok(response);
    }
}
