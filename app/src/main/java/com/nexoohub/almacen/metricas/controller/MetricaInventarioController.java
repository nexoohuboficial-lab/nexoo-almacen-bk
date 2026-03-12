package com.nexoohub.almacen.metricas.controller;

import com.nexoohub.almacen.metricas.dto.AnalisisInventarioRequestDTO;
import com.nexoohub.almacen.metricas.dto.MetricaInventarioResponseDTO;
import com.nexoohub.almacen.metricas.dto.ProductoInventarioDTO;
import com.nexoohub.almacen.metricas.service.MetricaInventarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * API REST para consulta y gestión de métricas de inventario.
 * 
 * <p><strong>Objetivo Ejecutivo:</strong></p>
 * <ul>
 *   <li>📊 ¿Cuánto capital tengo inmovilizado en inventario?</li>
 *   <li>🔄 ¿Rota bien mi inventario? ¿Cuántos días dura?</li>
 *   <li>⚠️ ¿Qué productos están sin stock o próximos a caducar?</li>
 *   <li>📈 Monitoreo de salud del inventario</li>
 * </ul>
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
@RestController
@RequestMapping("/api/v1/metricas/inventario")
@Tag(name = "Métrica Inventario", description = "API para análisis de capital inmovilizado, rotación y salud de inventario")
@Validated
public class MetricaInventarioController {

    private static final Logger logger = LoggerFactory.getLogger(MetricaInventarioController.class);
    private final MetricaInventarioService metricaInventarioService;

    public MetricaInventarioController(MetricaInventarioService metricaInventarioService) {
        this.metricaInventarioService = metricaInventarioService;
    }

    // ==================== GENERACIÓN Y CONSULTA DE MÉTRICAS ====================

    /**
     * Genera análisis completo de inventario con opción de guardar snapshot.
     * 
     * @param request Parámetros de análisis
     * @return Métricas calculadas
     */
    @PostMapping("/generar")
    @Operation(
        summary = "Generar análisis de inventario",
        description = "Calcula métricas completas: valor total, rotación, stock disponible, alertas. Opcionalmente guarda snapshot histórico."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Análisis generado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Parámetros inválidos", content = @Content),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content)
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'SUPERVISOR')")
    public ResponseEntity<MetricaInventarioResponseDTO> generarAnalisisInventario(
            @Valid @RequestBody AnalisisInventarioRequestDTO request) {
        
        logger.info("POST /api/v1/metricas/inventario/generar - Fecha: {}, Sucursal: {}, Guardar: {}", 
                request.getFechaCorte(), request.getSucursalId(), request.getGuardarSnapshot());

        MetricaInventarioResponseDTO response = metricaInventarioService.generarAnalisisInventario(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Consulta métricas de inventario (snapshot o cálculo en tiempo real).
     * 
     * @param fechaCorte Fecha del análisis
     * @param sucursalId ID sucursal (null = consolidado)
     * @return Métricas de inventario
     */
    @GetMapping
    @Operation(
        summary = "Consultar métricas de inventario",
        description = "Obtiene snapshot histórico si existe, o calcula en tiempo real. Si sucursalId es null, devuelve consolidado."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Métricas obtenidas exitosamente"),
        @ApiResponse(responseCode = "400", description = "Parámetros inválidos", content = @Content)
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'SUPERVISOR', 'AUDITOR', 'ALMACENISTA')")
    public ResponseEntity<MetricaInventarioResponseDTO> consultarMetricas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @Parameter(description = "Fecha de corte (formato: YYYY-MM-DD)", example = "2024-12-31")
            LocalDate fechaCorte,

            @RequestParam(required = false)
            @Parameter(description = "ID de sucursal (null = consolidado)")
            Integer sucursalId) {

        logger.info("GET /api/v1/metricas/inventario - Fecha: {}, Sucursal: {}", fechaCorte, sucursalId);

        MetricaInventarioResponseDTO response = metricaInventarioService.consultarMetricas(fechaCorte, sucursalId);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene el valor actual del inventario sin generar snapshot.
     * 
     * @param sucursalId ID sucursal (null = consolidado)
     * @return Valor total del inventario
     */
    @GetMapping("/valor-actual")
    @Operation(
        summary = "Valor actual del inventario",
        description = "Calcula el valor total del inventario en tiempo real (sin guardar snapshot)."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Valor calculado exitosamente")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'SUPERVISOR', 'AUDITOR', 'ALMACENISTA')")
    public ResponseEntity<BigDecimal> obtenerValorActual(
            @RequestParam(required = false)
            @Parameter(description = "ID de sucursal (null = consolidado)")
            Integer sucursalId) {

        logger.info("GET /api/v1/metricas/inventario/valor-actual - Sucursal: {}", sucursalId);

        BigDecimal valor = metricaInventarioService.calcularValorInventarioActual(sucursalId);
        return ResponseEntity.ok(valor);
    }

    // ==================== PRODUCTOS CON ALERTAS ====================

    /**
     * Obtiene productos con stock bajo mínimo.
     * 
     * @param sucursalId ID sucursal (null = todas)
     * @param limite Número máximo de resultados (default: 50)
     * @return Lista de productos con bajo stock
     */
    @GetMapping("/productos/bajo-stock")
    @Operation(
        summary = "Productos con stock bajo mínimo",
        description = "Lista productos cuyo stock actual está por debajo del mínimo configurado."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'SUPERVISOR', 'ALMACENISTA')")
    public ResponseEntity<List<ProductoInventarioDTO>> obtenerProductosBajoStock(
            @RequestParam(required = false)
            @Parameter(description = "ID de sucursal (null = todas)")
            Integer sucursalId,

            @RequestParam(defaultValue = "50")
            @Parameter(description = "Número máximo de resultados")
            int limite) {

        logger.info("GET /api/v1/metricas/inventario/productos/bajo-stock - Sucursal: {}, Límite: {}", 
                sucursalId, limite);

        List<ProductoInventarioDTO> productos = metricaInventarioService.obtenerProductosBajoStock(sucursalId, limite);
        return ResponseEntity.ok(productos);
    }

    /**
     * Obtiene productos sin stock (quiebre).
     * 
     * @param sucursalId ID sucursal (null = todas)
     * @param limite Número máximo de resultados (default: 50)
     * @return Lista de productos sin stock
     */
    @GetMapping("/productos/sin-stock")
    @Operation(
        summary = "Productos sin stock (quiebre)",
        description = "Lista productos con stockActual = 0, indicando quiebre de stock."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'SUPERVISOR', 'ALMACENISTA')")
    public ResponseEntity<List<ProductoInventarioDTO>> obtenerProductosSinStock(
            @RequestParam(required = false)
            @Parameter(description = "ID de sucursal (null = todas)")
            Integer sucursalId,

            @RequestParam(defaultValue = "50")
            @Parameter(description = "Número máximo de resultados")
            int limite) {

        logger.info("GET /api/v1/metricas/inventario/productos/sin-stock - Sucursal: {}, Límite: {}", 
                sucursalId, limite);

        List<ProductoInventarioDTO> productos = metricaInventarioService.obtenerProductosSinStock(sucursalId, limite);
        return ResponseEntity.ok(productos);
    }

    /**
     * Obtiene productos próximos a caducar.
     * 
     * @param sucursalId ID sucursal (null = todas)
     * @param diasAnticipacion Días de anticipación (default: 30)
     * @param limite Número máximo de resultados (default: 50)
     * @return Lista de productos próximos a caducar
     */
    @GetMapping("/productos/proximos-caducar")
    @Operation(
        summary = "Productos próximos a caducar",
        description = "Lista productos cuya fecha de caducidad está dentro del período de anticipación configurado."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'SUPERVISOR', 'ALMACENISTA')")
    public ResponseEntity<List<ProductoInventarioDTO>> obtenerProductosProximosCaducar(
            @RequestParam(required = false)
            @Parameter(description = "ID de sucursal (null = todas)")
            Integer sucursalId,

            @RequestParam(defaultValue = "30")
            @Parameter(description = "Días de anticipación para alerta")
            int diasAnticipacion,

            @RequestParam(defaultValue = "50")
            @Parameter(description = "Número máximo de resultados")
            int limite) {

        logger.info("GET /api/v1/metricas/inventario/productos/proximos-caducar - Sucursal: {}, Días: {}, Límite: {}", 
                sucursalId, diasAnticipacion, limite);

        List<ProductoInventarioDTO> productos = metricaInventarioService.obtenerProductosProximosCaducar(
                sucursalId, diasAnticipacion, limite);
        return ResponseEntity.ok(productos);
    }

    // ==================== HISTÓRICOS ====================

    /**
     * Obtiene histórico de métricas de inventario.
     * 
     * @param sucursalId ID sucursal (null = consolidado)
     * @param limite Número máximo de snapshots (default: 12)
     * @return Lista histórica de métricas
     */
    @GetMapping("/historico")
    @Operation(
        summary = "Histórico de métricas de inventario",
        description = "Obtiene snapshots históricos ordenados por fecha descendente. Útil para análisis de tendencias."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Histórico obtenido exitosamente")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'SUPERVISOR', 'AUDITOR')")
    public ResponseEntity<List<MetricaInventarioResponseDTO>> obtenerHistoricoMetricas(
            @RequestParam(required = false)
            @Parameter(description = "ID de sucursal (null = consolidado)")
            Integer sucursalId,

            @RequestParam(defaultValue = "12")
            @Parameter(description = "Número máximo de snapshots a devolver")
            int limite) {

        logger.info("GET /api/v1/metricas/inventario/historico - Sucursal: {}, Límite: {}", sucursalId, limite);

        List<MetricaInventarioResponseDTO> historico = metricaInventarioService.obtenerHistoricoMetricas(sucursalId, limite);
        return ResponseEntity.ok(historico);
    }
}
