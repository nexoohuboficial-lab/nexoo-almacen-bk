package com.nexoohub.almacen.rentabilidad.controller;

import com.nexoohub.almacen.rentabilidad.dto.*;
import com.nexoohub.almacen.rentabilidad.service.RentabilidadService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Controlador REST para análisis de rentabilidad.
 * 
 * <p>Endpoints para responder: <strong>¿Cuánto GANAS realmente?</strong></p>
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
@RestController
@RequestMapping("/api/v1/rentabilidad")
public class RentabilidadController {

    private final RentabilidadService rentabilidadService;

    public RentabilidadController(RentabilidadService rentabilidadService) {
        this.rentabilidadService = rentabilidadService;
    }

    /**
     * Calcula la rentabilidad de una venta específica.
     * 
     * <p>POST /api/v1/rentabilidad/venta/{ventaId}</p>
     * 
     * @param ventaId ID de la venta a analizar
     * @return Análisis de rentabilidad generado
     */
    @PostMapping("/venta/{ventaId}")
    public ResponseEntity<RentabilidadVentaResponseDTO> calcularRentabilidadVenta(
            @PathVariable Integer ventaId) {
        RentabilidadVentaResponseDTO resultado = rentabilidadService.calcularRentabilidadVenta(ventaId);
        return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
    }

    /**
     * Consulta el análisis de rentabilidad de una venta.
     * 
     * <p>GET /api/v1/rentabilidad/venta/{ventaId}</p>
     * 
     * @param ventaId ID de la venta
     * @return Análisis de rentabilidad
     */
    @GetMapping("/venta/{ventaId}")
    public ResponseEntity<RentabilidadVentaResponseDTO> consultarRentabilidadVenta(
            @PathVariable Integer ventaId) {
        RentabilidadVentaResponseDTO resultado = rentabilidadService.consultarPorVenta(ventaId);
        return ResponseEntity.ok(resultado);
    }

    /**
     * Genera análisis agregado de rentabilidad por producto en un período.
     * 
     * <p>POST /api/v1/rentabilidad/productos</p>
     * 
     * <p>Ejemplo request body:</p>
     * <pre>
     * {
     *   "fechaInicio": "2024-01-01",
     *   "fechaFin": "2024-01-31"
     * }
     * </pre>
     * 
     * @param request Período a analizar
     * @return Lista de análisis por producto
     */
    @PostMapping("/productos")
    public ResponseEntity<List<RentabilidadProductoResponseDTO>> generarAnalisisPorProducto(
            @Valid @RequestBody AnalisisRentabilidadRequestDTO request) {
        List<RentabilidadProductoResponseDTO> resultado = 
                rentabilidadService.generarAnalisisPorProducto(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
    }

    /**
     * Obtiene los N productos MÁS rentables en un período.
     * 
     * <p>GET /api/v1/rentabilidad/productos/mas-rentables?fechaInicio=2024-01-01&fechaFin=2024-01-31&limite=10</p>
     * 
     * @param fechaInicio Fecha de inicio del período
     * @param fechaFin Fecha de fin del período
     * @param limite Número máximo de resultados (default: 10)
     * @return Lista de productos más rentables
     */
    @GetMapping("/productos/mas-rentables")
    public ResponseEntity<List<RentabilidadProductoResponseDTO>> obtenerProductosMasRentables(
            @RequestParam LocalDate fechaInicio,
            @RequestParam LocalDate fechaFin,
            @RequestParam(defaultValue = "10") int limite) {
        List<RentabilidadProductoResponseDTO> resultado = 
                rentabilidadService.obtenerProductosMasRentables(fechaInicio, fechaFin, limite);
        return ResponseEntity.ok(resultado);
    }

    /**
     * Obtiene los N productos MENOS rentables en un período.
     * 
     * <p>GET /api/v1/rentabilidad/productos/menos-rentables?fechaInicio=2024-01-01&fechaFin=2024-01-31&limite=10</p>
     * 
     * @param fechaInicio Fecha de inicio del período
     * @param fechaFin Fecha de fin del período
     * @param limite Número máximo de resultados (default: 10)
     * @return Lista de productos menos rentables
     */
    @GetMapping("/productos/menos-rentables")
    public ResponseEntity<List<RentabilidadProductoResponseDTO>> obtenerProductosMenosRentables(
            @RequestParam LocalDate fechaInicio,
            @RequestParam LocalDate fechaFin,
            @RequestParam(defaultValue = "10") int limite) {
        List<RentabilidadProductoResponseDTO> resultado = 
                rentabilidadService.obtenerProductosMenosRentables(fechaInicio, fechaFin, limite);
        return ResponseEntity.ok(resultado);
    }

    /**
     * Obtiene todas las ventas realizadas BAJO COSTO (con pérdida).
     * 
     * <p>GET /api/v1/rentabilidad/ventas/bajo-costo</p>
     * 
     * <p><strong>ALERTA:</strong> Estas ventas generaron pérdidas, no ganancias.</p>
     * 
     * @return Lista de ventas con pérdida
     */
    @GetMapping("/ventas/bajo-costo")
    public ResponseEntity<List<RentabilidadVentaResponseDTO>> obtenerVentasBajoCosto() {
        List<RentabilidadVentaResponseDTO> resultado = rentabilidadService.obtenerVentasBajoCosto();
        return ResponseEntity.ok(resultado);
    }

    /**
     * Obtiene estadísticas generales de rentabilidad en un período.
     * 
     * <p>GET /api/v1/rentabilidad/estadisticas?fechaInicio=2024-01-01&fechaFin=2024-01-31</p>
     * 
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @return Estadísticas agregadas
     */
    @GetMapping("/estadisticas")
    public ResponseEntity<EstadisticasRentabilidadDTO> obtenerEstadisticas(
            @RequestParam LocalDate fechaInicio,
            @RequestParam LocalDate fechaFin) {
        EstadisticasRentabilidadDTO resultado = 
                rentabilidadService.obtenerEstadisticas(fechaInicio, fechaFin);
        return ResponseEntity.ok(resultado);
    }
}
