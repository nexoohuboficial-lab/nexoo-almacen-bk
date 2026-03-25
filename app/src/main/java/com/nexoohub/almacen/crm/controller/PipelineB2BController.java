package com.nexoohub.almacen.crm.controller;

import com.nexoohub.almacen.crm.dto.*;
import com.nexoohub.almacen.crm.service.PipelineB2BService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/crm")
public class PipelineB2BController {

    private final PipelineB2BService pipelineService;

    public PipelineB2BController(PipelineB2BService pipelineService) {
        this.pipelineService = pipelineService;
    }

    // --- PROSPECTOS ---

    @PostMapping("/prospectos")
    public ResponseEntity<ProspectoResponse> crearProspecto(@Valid @RequestBody ProspectoRequest request) {
        return new ResponseEntity<>(pipelineService.crearProspecto(request), HttpStatus.CREATED);
    }

    @GetMapping("/prospectos")
    public ResponseEntity<List<ProspectoResponse>> listarProspectos() {
        return ResponseEntity.ok(pipelineService.listarProspectos());
    }

    @GetMapping("/prospectos/{id}")
    public ResponseEntity<ProspectoResponse> obtenerProspecto(@PathVariable Integer id) {
        return ResponseEntity.ok(pipelineService.obtenerProspecto(id));
    }

    // --- OPORTUNIDADES ---

    @PostMapping("/oportunidades")
    public ResponseEntity<OportunidadVentaResponse> crearOportunidad(@Valid @RequestBody OportunidadVentaRequest request) {
        return new ResponseEntity<>(pipelineService.crearOportunidad(request), HttpStatus.CREATED);
    }

    @PatchMapping("/oportunidades/{id}/etapa")
    public ResponseEntity<OportunidadVentaResponse> cambiarEtapaOportunidad(
            @PathVariable Integer id,
            @Valid @RequestBody CambioEtapaOportunidadRequest request) {
        return ResponseEntity.ok(pipelineService.cambiarEtapaOportunidad(id, request));
    }

    @GetMapping("/prospectos/{prospectoId}/oportunidades")
    public ResponseEntity<List<OportunidadVentaResponse>> listarOportunidadesPorProspecto(
            @PathVariable Integer prospectoId) {
        return ResponseEntity.ok(pipelineService.listarOportunidadesPorProspecto(prospectoId));
    }

    // --- INTERACCIONES ---

    @PostMapping("/interacciones")
    public ResponseEntity<InteraccionCrmResponse> registrarInteraccion(
            @Valid @RequestBody InteraccionCrmRequest request) {
        return new ResponseEntity<>(pipelineService.registrarInteraccion(request), HttpStatus.CREATED);
    }

    @GetMapping("/prospectos/{prospectoId}/interacciones")
    public ResponseEntity<List<InteraccionCrmResponse>> listarInteraccionesPorProspecto(
            @PathVariable Integer prospectoId) {
        return ResponseEntity.ok(pipelineService.listarInteraccionesPorProspecto(prospectoId));
    }
}
