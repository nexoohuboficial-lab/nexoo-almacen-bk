package com.nexoohub.almacen.analitica.controller;

import com.nexoohub.almacen.analitica.dto.ChurnClienteResponse;
import com.nexoohub.almacen.analitica.entity.PrediccionChurnCliente;
import com.nexoohub.almacen.analitica.repository.PrediccionChurnClienteRepository;
import com.nexoohub.almacen.analitica.service.ChurnPredictionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/analitica/churn")
@RequiredArgsConstructor
@Tag(name = "Analytics - Churn", description = "Predicción de Fuga de Clientes (Churn)")
public class ChurnPredictionController {

    private final ChurnPredictionService churnPredictionService;
    private final PrediccionChurnClienteRepository churnRepository;

    @Operation(summary = "Calcular Riesgo Churn", description = "Calcula el score de fuga para todos los clientes en base a su historial. Requiere Rol ADMIN.")
    @PostMapping("/calcular")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> calcularChurnMasivo() {
        churnPredictionService.calcularRiesgoChurnGlobal();
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Obtener Clientes en Riesgo", description = "Lista los clientes con un score de riesgo mayor o igual a 70. Permite a Vendedores llamarlos.")
    @GetMapping("/en-riesgo")
    public ResponseEntity<List<ChurnClienteResponse>> obtenerClientesEnRiesgo() {
        List<PrediccionChurnCliente> clientesEnRiesgo = churnRepository.findByScoreRiesgoGreaterThanEqualOrderByScoreRiesgoDesc(70);
        
        List<ChurnClienteResponse> respuestas = clientesEnRiesgo.stream()
                .map(churn -> ChurnClienteResponse.builder()
                        .clienteId(churn.getClienteId())
                        .nombreCliente(churn.getCliente() != null ? churn.getCliente().getNombre() : "Desconocido")
                        .telefono(churn.getCliente() != null ? churn.getCliente().getTelefono() : "Sin teléfono")
                        .email(churn.getCliente() != null ? churn.getCliente().getEmail() : "Sin email")
                        .scoreRiesgo(churn.getScoreRiesgo())
                        .factoresRiesgo(churn.getFactoresRiesgo())
                        .diasSinComprar(churn.getDiasSinComprar())
                        .frecuenciaPromedioDias(churn.getFrecuenciaPromedioDias())
                        .fechaAnalisis(churn.getFechaAnalisis())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(respuestas);
    }
}
