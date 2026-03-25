package com.nexoohub.almacen.crm.controller;

import com.nexoohub.almacen.crm.dto.CampanaMarketingRequest;
import com.nexoohub.almacen.crm.dto.CampanaMarketingResponse;
import com.nexoohub.almacen.crm.dto.CampanaMetricasResponse;
import com.nexoohub.almacen.crm.service.CampanaMarketingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/marketing/campanas")
public class CampanaMarketingController {

    private final CampanaMarketingService campanaService;

    public CampanaMarketingController(CampanaMarketingService campanaService) {
        this.campanaService = campanaService;
    }

    @PostMapping
    public ResponseEntity<CampanaMarketingResponse> crearCampana(@Valid @RequestBody CampanaMarketingRequest request) {
        return new ResponseEntity<>(campanaService.crearCampana(request), HttpStatus.CREATED);
    }

    @PostMapping("/{id}/ejecutar")
    public ResponseEntity<CampanaMarketingResponse> ejecutarCampana(@PathVariable Integer id) {
        return ResponseEntity.ok(campanaService.ejecutarCampana(id));
    }

    @GetMapping("/{id}/metricas")
    public ResponseEntity<CampanaMetricasResponse> obtenerMetricas(@PathVariable Integer id) {
        return ResponseEntity.ok(campanaService.obtenerMetricas(id));
    }
}
