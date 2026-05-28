package com.nexoohub.almacen.crm.controller;

import com.nexoohub.almacen.crm.dto.EncuestaNpsRequest;
import com.nexoohub.almacen.crm.dto.EncuestaNpsResponse;
import com.nexoohub.almacen.crm.dto.NpsDashboardResponse;
import com.nexoohub.almacen.crm.dto.RespuestaNpsRequest;
import com.nexoohub.almacen.crm.service.NpsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/nps")
@RequiredArgsConstructor
@Tag(name = "NPS V1", description = "Endpoints para la generación de encuestas Net Promoter Score y registro de respuestas")
public class NpsV1Controller {

    private final NpsService npsService;

    @PostMapping("/encuestas")
    @PreAuthorize("hasAnyRole('VENDEDOR', 'ADMIN', 'GERENTE')")
    @Operation(summary = "Crear encuesta NPS", description = "Genera un enlace único de encuesta NPS para una venta realizada")
    public ResponseEntity<EncuestaNpsResponse> crearEncuesta(@Valid @RequestBody EncuestaNpsRequest request) {
        EncuestaNpsResponse response = npsService.crearEncuesta(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/respuestas")
    @Operation(summary = "Registrar respuesta NPS", description = "Endpoint público para que el cliente califique el servicio (score 0-10)")
    public ResponseEntity<Map<String, String>> registrarRespuesta(@Valid @RequestBody RespuestaNpsRequest request) {
        npsService.registrarRespuesta(request);
        return ResponseEntity.ok(Map.of("mensaje", "¡Gracias por tus comentarios! Respuesta registrada exitosamente"));
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Obtener dashboard NPS", description = "Calcula el Net Promoter Score global y el porcentaje de Promotores, Pasivos y Detractores")
    public ResponseEntity<NpsDashboardResponse> obtenerDashboard(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        NpsDashboardResponse dashboard = npsService.obtenerDashboard(inicio, fin);
        return ResponseEntity.ok(dashboard);
    }
}
