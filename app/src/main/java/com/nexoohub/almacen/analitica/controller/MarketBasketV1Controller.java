package com.nexoohub.almacen.analitica.controller;

import com.nexoohub.almacen.analitica.dto.ReglaAsociacionDTO;
import com.nexoohub.almacen.analitica.mapper.MarketBasketMapper;
import com.nexoohub.almacen.analitica.repository.ReglaAsociacionProductosRepository;
import com.nexoohub.almacen.analitica.service.MarketBasketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/market-basket")
@RequiredArgsConstructor
@Tag(name = "Market Basket Analysis", description = "Endpoints para el análisis de canasta de compra (Apriori)")
public class MarketBasketV1Controller {

    private final MarketBasketService marketBasketService;
    private final ReglaAsociacionProductosRepository reglaRepository;
    private final MarketBasketMapper mapper;

    @PostMapping("/analizar")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Ejecutar análisis de canasta de compra", description = "Corre el algoritmo Apriori para generar reglas de asociación entre productos")
    public ResponseEntity<Map<String, String>> analizar(
            @RequestParam(defaultValue = "0.01") double minSoporte,
            @RequestParam(defaultValue = "0.1") double minConfianza) {
        
        marketBasketService.calcularReglasAsociacion(minSoporte, minConfianza);
        
        return ResponseEntity.ok(Map.of(
            "status", "success",
            "message", "Análisis de canasta de compra finalizado con éxito."
        ));
    }

    @GetMapping("/resultados")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    @Operation(summary = "Listar reglas de asociación", description = "Retorna todas las reglas de asociación calculadas en el último análisis")
    public ResponseEntity<List<ReglaAsociacionDTO>> obtenerResultados() {
        List<ReglaAsociacionDTO> resultados = reglaRepository.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(resultados);
    }
}
