package com.nexoohub.almacen.prediccion.controller;

import com.nexoohub.almacen.prediccion.dto.GenerarPrediccionRequestDTO;
import com.nexoohub.almacen.prediccion.dto.PrediccionDemandaResponseDTO;
import com.nexoohub.almacen.prediccion.dto.RecomendacionCompraDTO;
import com.nexoohub.almacen.prediccion.service.PrediccionDemandaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para gestionar predicciones de demanda.
 * 
 * <p>Proporciona endpoints para:</p>
 * <ul>
 *   <li>Generar predicciones basadas en histórico</li>
 *   <li>Consultar predicciones existentes</li>
 *   <li>Obtener recomendaciones de compra</li>
 * </ul>
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
@RestController
@RequestMapping("/api/predicciones")
@Tag(name = "Predicción de Demanda", description = "Gestión de predicciones de demanda")
public class PrediccionDemandaController {

    private final PrediccionDemandaService prediccionService;

    public PrediccionDemandaController(PrediccionDemandaService prediccionService) {
        this.prediccionService = prediccionService;
    }

    // ==========================================
    // GENERAR PREDICCIONES
    // ==========================================

    @PostMapping("/generar")
    @Operation(
        summary = "Generar predicciones de demanda",
        description = "Analiza histórico de ventas y genera predicciones para un periodo futuro"
    )
    public ResponseEntity<List<PrediccionDemandaResponseDTO>> generarPredicciones(
            @Valid @RequestBody GenerarPrediccionRequestDTO request) {
        List<PrediccionDemandaResponseDTO> predicciones = prediccionService
                .generarPredicciones(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(predicciones);
    }

    // ==========================================
    // CONSULTAR PREDICCIONES
    // ==========================================

    @GetMapping("/{id}")
    @Operation(
        summary = "Obtener predicción por ID",
        description = "Devuelve los detalles de una predicción específica"
    )
    public ResponseEntity<PrediccionDemandaResponseDTO> obtenerPrediccion(
            @Parameter(description = "ID de la predicción") @PathVariable Integer id) {
        PrediccionDemandaResponseDTO prediccion = prediccionService.obtenerPrediccion(id);
        return ResponseEntity.ok(prediccion);
    }

    @GetMapping("/producto/{skuProducto}")
    @Operation(
        summary = "Obtener predicciones de un producto",
        description = "Devuelve todas las predicciones de un producto en una sucursal"
    )
    public ResponseEntity<List<PrediccionDemandaResponseDTO>> obtenerPrediccionesProducto(
            @Parameter(description = "SKU del producto") @PathVariable String skuProducto,
            @Parameter(description = "ID de la sucursal") @RequestParam Integer sucursalId) {
        List<PrediccionDemandaResponseDTO> predicciones = prediccionService
                .obtenerPrediccionesProducto(skuProducto, sucursalId);
        return ResponseEntity.ok(predicciones);
    }

    // ==========================================
    // RECOMENDACIONES DE COMPRA
    // ==========================================

    @GetMapping("/recomendaciones")
    @Operation(
        summary = "Obtener recomendaciones de compra",
        description = "Devuelve lista de productos que necesitan reorden para un periodo"
    )
    public ResponseEntity<RecomendacionCompraDTO> obtenerRecomendacionesCompra(
            @Parameter(description = "ID de la sucursal") @RequestParam Integer sucursalId,
            @Parameter(description = "Año del periodo") @RequestParam Integer anio,
            @Parameter(description = "Mes del periodo (1-12)") @RequestParam Integer mes) {
        RecomendacionCompraDTO recomendacion = prediccionService
                .obtenerRecomendacionesCompra(sucursalId, anio, mes);
        return ResponseEntity.ok(recomendacion);
    }
}
