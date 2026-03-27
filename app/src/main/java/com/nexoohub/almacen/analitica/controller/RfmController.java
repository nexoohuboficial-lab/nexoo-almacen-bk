package com.nexoohub.almacen.analitica.controller;

import com.nexoohub.almacen.analitica.dto.RfmCalcularResponse;
import com.nexoohub.almacen.analitica.dto.RfmClienteResponse;
import com.nexoohub.almacen.analitica.dto.RfmSegmentoStatsResponse;
import com.nexoohub.almacen.analitica.service.RfmService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/analitica/rfm")
@Tag(name = "Analytics RFM", description = "Endpoints para la segmentación inteligente de clientes")
public class RfmController {

    private final RfmService rfmService;

    public RfmController(RfmService rfmService) {
        this.rfmService = rfmService;
    }

    @Operation(summary = "Ejecutar Motor RFM", description = "Calcula la recencia, frecuencia y monto masivo de todos los clientes con facturas y actualiza sus segmentos en la base de datos.")
    @PostMapping("/calcular")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<RfmCalcularResponse> calcularRfmMasivo() {
        RfmCalcularResponse response = rfmService.calcularRfmMasivo();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Pobladores de Segmentos RFM", description = "Recupera la cuenta de cuántos clientes hay en cada categoría (Campeón, Leal, Riesgo, etc).")
    @GetMapping("/segmentos")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    public ResponseEntity<List<RfmSegmentoStatsResponse>> obtenerEstadisticasRfm() {
        List<RfmSegmentoStatsResponse> stats = rfmService.agruparClientesPorSegmento();
        return ResponseEntity.ok(stats);
    }

    @Operation(summary = "Consulta RFM por Cliente", description = "Revisa los scores 1-5 y el segmento asignado de un cliente específico.")
    @GetMapping("/cliente/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    public ResponseEntity<RfmClienteResponse> consultarSegmentoCliente(@PathVariable Integer id) {
        RfmClienteResponse rfm = rfmService.obtenerRfmPorCliente(id);
        return ResponseEntity.ok(rfm);
    }
}
