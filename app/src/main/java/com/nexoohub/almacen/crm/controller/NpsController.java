package com.nexoohub.almacen.crm.controller;

import com.nexoohub.almacen.crm.dto.EncuestaNpsRequest;
import com.nexoohub.almacen.crm.dto.EncuestaNpsResponse;
import com.nexoohub.almacen.crm.dto.NpsDashboardResponse;
import com.nexoohub.almacen.crm.dto.RespuestaNpsRequest;
import com.nexoohub.almacen.crm.service.NpsService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/crm/nps")
public class NpsController {

    private final NpsService npsService;

    public NpsController(NpsService npsService) {
        this.npsService = npsService;
    }

    @PostMapping("/encuestas")
    @PreAuthorize("hasAnyRole('VENDEDOR', 'ADMIN', 'GERENTE')")
    public ResponseEntity<EncuestaNpsResponse> crearEncuesta(@Valid @RequestBody EncuestaNpsRequest request) {
        EncuestaNpsResponse response = npsService.crearEncuesta(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/respuestas")
    // Endpoint público (el cliente responde usando el enlace único)
    public ResponseEntity<java.util.Map<String, String>> registrarRespuesta(@Valid @RequestBody RespuestaNpsRequest request) {
        npsService.registrarRespuesta(request);
        return ResponseEntity.ok(java.util.Map.of("mensaje", "¡Gracias por tus comentarios! Respuesta registrada exitosamente"));
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<NpsDashboardResponse> obtenerDashboard(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        NpsDashboardResponse dashboard = npsService.obtenerDashboard(inicio, fin);
        return ResponseEntity.ok(dashboard);
    }
}
