package com.nexoohub.almacen.inventario.controller;

import com.nexoohub.almacen.inventario.dto.AnalisisABCRequestDTO;
import com.nexoohub.almacen.inventario.dto.AnalisisABCResumenDTO;
import com.nexoohub.almacen.inventario.dto.AnalisisABCResponseDTO;
import com.nexoohub.almacen.inventario.service.AnalisisABCService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Controlador REST para análisis ABC de inventario - Módulo #10.
 * Implementa el principio de Pareto (80/20) para clasificar productos
 * según su valor en ventas.
 * 
 * @author NexooHub Development Team
 * @version 1.4.0
 */
@RestController
@RequestMapping("/api/v1/inventario/analisis-abc")
@Tag(name = "Análisis ABC", description = "Endpoints para análisis ABC de inventario según principio de Pareto")
@Slf4j
public class AnalisisABCController {

    @Autowired
    private AnalisisABCService analisisService;

    /**
     * Genera análisis ABC para un periodo determinado.
     * Clasifica productos en A (80% del valor), B (15% del valor) y C (5% del valor).
     * 
     * @param request Parámetros del análisis (sucursalId, fechas, porcentajes)
     * @return Lista de productos analizados con clasificación ABC
     */
    @PostMapping("/generar")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'SUPERVISOR')")
    @Operation(summary = "Generar análisis ABC", 
               description = "Genera análisis ABC clasificando productos según el principio de Pareto (80/20). " +
                           "Clase A: productos de alto valor (~80% ventas), B: valor medio (~15%), C: bajo valor (~5%)")
    public ResponseEntity<List<AnalisisABCResponseDTO>> generarAnalisis(
            @Valid @RequestBody AnalisisABCRequestDTO request) {
        
        log.info("📊 POST /api/v1/inventario/analisis-abc/generar - Generando análisis ABC");
        log.debug("Request: {}", request);
        
        List<AnalisisABCResponseDTO> resultado = analisisService.generarAnalisisABC(request);
        
        log.info("✅ Análisis ABC generado exitosamente: {} productos analizados", resultado.size());
        return ResponseEntity.ok(resultado);
    }

    /**
     * Obtiene el análisis ABC más reciente de una sucursal.
     * 
     * @param sucursalId ID de la sucursal
     * @return Lista de productos del último análisis
     */
    @GetMapping("/sucursal/{sucursalId}/ultimo")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'SUPERVISOR', 'AUDITOR')")
    @Operation(summary = "Obtener último análisis", 
               description = "Retorna el análisis ABC más reciente generado para la sucursal especificada")
    public ResponseEntity<List<AnalisisABCResponseDTO>> obtenerUltimoAnalisis(
            @Parameter(description = "ID de la sucursal", required = true)
            @PathVariable Integer sucursalId) {
        
        log.info("📋 GET /api/v1/inventario/analisis-abc/sucursal/{}/ultimo", sucursalId);
        
        List<AnalisisABCResponseDTO> resultado = analisisService.obtenerUltimoAnalisis(sucursalId);
        
        log.info("✅ Último análisis recuperado: {} productos", resultado.size());
        return ResponseEntity.ok(resultado);
    }

    /**
     * Obtiene productos de una clasificación específica del último análisis.
     * 
     * @param sucursalId ID de la sucursal
     * @param clasificacion 'A' (alta prioridad), 'B' (media) o 'C' (baja)
     * @return Lista de productos filtrados por clasificación
     */
    @GetMapping("/sucursal/{sucursalId}/clasificacion/{clasificacion}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'SUPERVISOR', 'AUDITOR')")
    @Operation(summary = "Obtener productos por clasificación ABC", 
               description = "Filtra productos del último análisis por clasificación: A (alta rotación y valor), B (importancia media), C (bajo valor)")
    public ResponseEntity<List<AnalisisABCResponseDTO>> obtenerPorClasificacion(
            @Parameter(description = "ID de la sucursal", required = true)
            @PathVariable Integer sucursalId,
            @Parameter(description = "Clasificación ABC: A, B o C", required = true)
            @Pattern(regexp = "[ABC]", message = "Clasificación debe ser A, B o C")
            @PathVariable String clasificacion) {
        
        log.info("🔍 GET /api/v1/inventario/analisis-abc/sucursal/{}/clasificacion/{}", sucursalId, clasificacion);
        
        List<AnalisisABCResponseDTO> resultado = analisisService
                .obtenerPorClasificacion(sucursalId, clasificacion.toUpperCase());
        
        log.info("✅ Productos clase {} recuperados: {} items", clasificacion.toUpperCase(), resultado.size());
        return ResponseEntity.ok(resultado);
    }

    /**
     * Obtiene resumen estadístico del análisis ABC.
     * Incluye: total productos, distribución A/B/C, valores, rotación, top productos.
     * 
     * @param sucursalId ID de la sucursal
     * @return Resumen con estadísticas agregadas y top productos de clase A
     */
    @GetMapping("/sucursal/{sucursalId}/resumen")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'SUPERVISOR', 'AUDITOR')")
    @Operation(summary = "Obtener resumen estadístico", 
               description = "Retorna estadísticas agregadas del último análisis ABC: totales por clasificación, valores, rotación e indicadores clave")
    public ResponseEntity<AnalisisABCResumenDTO> obtenerResumen(
            @Parameter(description = "ID de la sucursal", required = true)
            @PathVariable Integer sucursalId) {
        
        log.info("📈 GET /api/v1/inventario/analisis-abc/sucursal/{}/resumen", sucursalId);
        
        AnalisisABCResumenDTO resumen = analisisService.obtenerResumen(sucursalId);
        
        log.info("✅ Resumen generado: {} productos totales", resumen.getTotalProductos());
        return ResponseEntity.ok(resumen);
    }
}
