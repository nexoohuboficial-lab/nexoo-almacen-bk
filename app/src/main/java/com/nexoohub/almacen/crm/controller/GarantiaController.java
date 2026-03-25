package com.nexoohub.almacen.crm.controller;

import com.nexoohub.almacen.crm.dto.ResolucionGarantiaRequest;
import com.nexoohub.almacen.crm.dto.TicketGarantiaRequest;
import com.nexoohub.almacen.crm.dto.TicketGarantiaResponse;
import com.nexoohub.almacen.crm.service.GarantiaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/crm/garantias")
@RequiredArgsConstructor
public class GarantiaController {

    private final GarantiaService garantiaService;

    @PostMapping
    public ResponseEntity<TicketGarantiaResponse> abrirTicket(@Valid @RequestBody TicketGarantiaRequest request) {
        TicketGarantiaResponse response = garantiaService.abrirTicket(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<TicketGarantiaResponse>> obtenerTicketsPorCliente(@PathVariable Integer clienteId) {
        return ResponseEntity.ok(garantiaService.getHistorialCliente(clienteId));
    }

    @GetMapping("/{ticketId}")
    public ResponseEntity<TicketGarantiaResponse> obtenerTicketPorId(@PathVariable Integer ticketId) {
        return ResponseEntity.ok(garantiaService.getTicketPorId(ticketId));
    }

    @PutMapping("/{ticketId}/estado")
    public ResponseEntity<TicketGarantiaResponse> cambiarEstado(
            @PathVariable Integer ticketId,
            @RequestParam String nuevoEstado,
            @RequestParam String comentario,
            @RequestParam Integer usuarioId) {
            
        TicketGarantiaResponse response = garantiaService.cambiarEstado(ticketId, nuevoEstado, comentario, usuarioId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{ticketId}/resolver")
    public ResponseEntity<TicketGarantiaResponse> resolverTicket(
            @PathVariable Integer ticketId,
            @Valid @RequestBody ResolucionGarantiaRequest request) {
            
        TicketGarantiaResponse response = garantiaService.resolverTicket(ticketId, request);
        return ResponseEntity.ok(response);
    }
}
