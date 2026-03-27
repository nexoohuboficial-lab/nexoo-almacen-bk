package com.nexoohub.almacen.analitica.controller;

import com.nexoohub.almacen.analitica.dto.ReglaAsociacionDTO;
import com.nexoohub.almacen.analitica.dto.SugerenciaCanastaResponseDTO;
import com.nexoohub.almacen.analitica.mapper.MarketBasketMapper;
import com.nexoohub.almacen.analitica.repository.ReglaAsociacionProductosRepository;
import com.nexoohub.almacen.analitica.service.MarketBasketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/analitica/canasta")
@RequiredArgsConstructor
public class MarketBasketController {

    private final MarketBasketService marketBasketService;
    private final ReglaAsociacionProductosRepository reglaRepository;
    private final MarketBasketMapper mapper;

    @PostMapping("/calcular")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<Map<String, String>> recalcularReglas(
            @RequestParam(defaultValue = "0.01") double minSoporte,
            @RequestParam(defaultValue = "0.1") double minConfianza) {
        
        marketBasketService.calcularReglasAsociacion(minSoporte, minConfianza);
        
        return ResponseEntity.ok(Map.of(
            "status", "success",
            "message", "Algoritmo Market Basket ejecutado exitosamente. Se han actualizado las reglas de asociación."
        ));
    }

    @GetMapping("/{sku}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'VENDEDOR')")
    public ResponseEntity<SugerenciaCanastaResponseDTO> obtenerSugerencias(
            @PathVariable String sku) {
        
        List<ReglaAsociacionDTO> recomendaciones = reglaRepository
                .findTop5BySkuOrigenOrderByConfianzaDescLiftDesc(sku)
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
                
        SugerenciaCanastaResponseDTO response = new SugerenciaCanastaResponseDTO(sku, recomendaciones);
        return ResponseEntity.ok(response);
    }
}
