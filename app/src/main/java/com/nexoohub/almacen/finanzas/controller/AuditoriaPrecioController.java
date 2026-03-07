package com.nexoohub.almacen.finanzas.controller;

import com.nexoohub.almacen.finanzas.dto.AuditoriaPrecioDTO;
import com.nexoohub.almacen.finanzas.repository.HistorialPrecioRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controlador para auditoría avanzada de cambios de precio.
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
@RestController
@RequestMapping("/api/v1/auditoria/precios")
public class AuditoriaPrecioController {
    
    private final HistorialPrecioRepository historialPrecioRepository;
    
    public AuditoriaPrecioController(HistorialPrecioRepository historialPrecioRepository) {
        this.historialPrecioRepository = historialPrecioRepository;
    }
    
    /**
     * Obtiene el historial de cambios de precio de un producto.
     * 
     * @param skuInterno SKU del producto
     * @return historial de cambios
     */
    @GetMapping("/producto/{skuInterno}")
    public ResponseEntity<List<AuditoriaPrecioDTO>> obtenerHistorialProducto(
            @PathVariable String skuInterno) {
        List<AuditoriaPrecioDTO> historial = historialPrecioRepository.obtenerHistorialPorProducto(skuInterno);
        return ResponseEntity.ok(historial);
    }
    
    /**
     * Obtiene cambios de precio en un rango de fechas.
     * 
     * @param fechaInicio fecha inicial (formato: yyyy-MM-dd'T'HH:mm:ss)
     * @param fechaFin fecha final (formato: yyyy-MM-dd'T'HH:mm:ss)
     * @return cambios en el rango
     */
    @GetMapping("/periodo")
    public ResponseEntity<List<AuditoriaPrecioDTO>> obtenerCambiosPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        List<AuditoriaPrecioDTO> cambios = historialPrecioRepository.obtenerCambiosPorFecha(fechaInicio, fechaFin);
        return ResponseEntity.ok(cambios);
    }
    
    /**
     * Obtiene cambios de precio significativos (superiores a un porcentaje).
     * 
     * @param porcentaje porcentaje mínimo de cambio (por defecto 10%)
     * @return cambios significativos
     */
    @GetMapping("/significativos")
    public ResponseEntity<List<AuditoriaPrecioDTO>> obtenerCambiosSignificativos(
            @RequestParam(defaultValue = "10.0") Double porcentaje) {
        List<AuditoriaPrecioDTO> cambios = historialPrecioRepository.obtenerCambiosSignificativos(
                java.math.BigDecimal.valueOf(porcentaje));
        return ResponseEntity.ok(cambios);
    }
}
