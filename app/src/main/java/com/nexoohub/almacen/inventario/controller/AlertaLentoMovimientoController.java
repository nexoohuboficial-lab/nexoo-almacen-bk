package com.nexoohub.almacen.inventario.controller;

import com.nexoohub.almacen.inventario.dto.*;
import com.nexoohub.almacen.inventario.service.AlertaLentoMovimientoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestión de alertas de productos de lento movimiento.
 * 
 * <p>Proporciona endpoints para:</p>
 * <ul>
 *   <li>Generar alertas automáticamente detectando productos sin ventas</li>
 *   <li>Consultar alertas por criticidad, sucursal o producto</li>
 *   <li>Calcular costos de inventario inmovilizado</li>
 *   <li>Resolver y eliminar alertas</li>
 * </ul>
 * 
 * @author NexooHub Development Team
 * @since 1.2.0
 */
@RestController
@RequestMapping("/api/alertas/lento-movimiento")
@Tag(name = "Alertas de Lento Movimiento", 
     description = "Endpoints para detectar y gestionar productos de baja rotación")
public class AlertaLentoMovimientoController {

    private final AlertaLentoMovimientoService alertaService;

    public AlertaLentoMovimientoController(AlertaLentoMovimientoService alertaService) {
        this.alertaService = alertaService;
    }

    /**
     * Genera alertas de productos de lento movimiento.
     * 
     * <p>Analiza el inventario y detecta productos sin ventas en los últimos N días.</p>
     * 
     * @param request Configuración de generación (umbral de días, sucursal)
     * @return Lista de alertas generadas/actualizadas
     */
    @PostMapping("/generar")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'SUPERVISOR')")
    @Operation(
        summary = "Genera alertas de lento movimiento",
        description = "Detecta productos sin ventas en los últimos N días y crea/actualiza alertas. " +
                      "Se pueden generar para todas las sucursales o solo una específica."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Alertas generadas exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    public ResponseEntity<List<AlertaLentoMovimientoResponseDTO>> generarAlertas(
            @Valid @RequestBody GenerarAlertasRequestDTO request) {
        List<AlertaLentoMovimientoResponseDTO> alertas = alertaService.generarAlertas(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(alertas);
    }

    /**
     * Obtiene todas las alertas activas del sistema.
     * 
     * @return Lista de alertas no resueltas
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'SUPERVISOR', 'AUDITOR')")
    @Operation(
        summary = "Lista todas las alertas activas",
        description = "Retorna todas las alertas de lento movimiento que no han sido resueltas."
    )
    @ApiResponse(responseCode = "200", description = "Alertas obtenidas exitosamente")
    public ResponseEntity<List<AlertaLentoMovimientoResponseDTO>> obtenerAlertasActivas() {
        return ResponseEntity.ok(alertaService.obtenerAlertasActivas());
    }

    /**
     * Obtiene una alerta específica por ID.
     * 
     * @param id ID de la alerta
     * @return Información de la alerta
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'SUPERVISOR', 'AUDITOR')")
    @Operation(
        summary = "Obtiene una alerta por ID",
        description = "Retorna la información completa de una alerta específica."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Alerta encontrada"),
        @ApiResponse(responseCode = "404", description = "Alerta no encontrada")
    })
    public ResponseEntity<AlertaLentoMovimientoResponseDTO> obtenerAlertaPorId(
            @Parameter(description = "ID de la alerta", required = true)
            @PathVariable Integer id) {
        return ResponseEntity.ok(alertaService.obtenerAlertaPorId(id));
    }

    /**
     * Obtiene alertas activas de una sucursal específica.
     * 
     * @param sucursalId ID de la sucursal
     * @return Lista de alertas de la sucursal
     */
    @GetMapping("/sucursal/{sucursalId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'SUPERVISOR', 'AUDITOR')")
    @Operation(
        summary = "Lista alertas por sucursal",
        description = "Retorna todas las alertas activas de una sucursal específica."
    )
    @ApiResponse(responseCode = "200", description = "Alertas obtenidas exitosamente")
    public ResponseEntity<List<AlertaLentoMovimientoResponseDTO>> obtenerAlertasPorSucursal(
            @Parameter(description = "ID de la sucursal", required = true)
            @PathVariable Integer sucursalId) {
        return ResponseEntity.ok(alertaService.obtenerAlertasPorSucursal(sucursalId));
    }

    /**
     * Obtiene solo las alertas en estado CRITICO (más de 60 días sin venta).
     * 
     * @return Lista de alertas críticas
     */
    @GetMapping("/criticas")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'SUPERVISOR', 'AUDITOR')")
    @Operation(
        summary = "Lista alertas críticas",
        description = "Retorna solo las alertas con más de 60 días sin venta (estado CRITICO)."
    )
    @ApiResponse(responseCode = "200", description = "Alertas críticas obtenidas")
    public ResponseEntity<List<AlertaLentoMovimientoResponseDTO>> obtenerAlertasCriticas() {
        return ResponseEntity.ok(alertaService.obtenerAlertasCriticas());
    }

    /**
     * Obtiene alertas activas para un producto específico en todas las sucursales.
     * 
     * @param skuInterno SKU del producto
     * @return Lista de alertas del producto
     */
    @GetMapping("/producto/{skuInterno}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'SUPERVISOR', 'AUDITOR')")
    @Operation(
        summary = "Lista alertas por producto",
        description = "Retorna todas las alertas activas de un producto en todas las sucursales."
    )
    @ApiResponse(responseCode = "200", description = "Alertas del producto obtenidas")
    public ResponseEntity<List<AlertaLentoMovimientoResponseDTO>> obtenerAlertasPorProducto(
            @Parameter(description = "SKU interno del producto", required = true)
            @PathVariable String skuInterno) {
        return ResponseEntity.ok(alertaService.obtenerAlertasPorProducto(skuInterno));
    }

    /**
     * Calcula el resumen de costos inmovilizados por alertas.
     * 
     * @param sucursalId ID de sucursal (opcional, null para global)
     * @return DTO con métricas agregadas
     */
    @GetMapping("/costo-inmovilizado")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'AUDITOR')")
    @Operation(
        summary = "Calcula costo de inventario inmovilizado",
        description = "Retorna el costo total del inventario con bajo movimiento. " +
                      "Puede filtrarse por sucursal o calcular global."
    )
    @ApiResponse(responseCode = "200", description = "Resumen de costos calculado")
    public ResponseEntity<CostoInmovilizadoResumenDTO> calcularCostoInmovilizado(
            @Parameter(description = "ID de sucursal (opcional, para filtrar)")
            @RequestParam(required = false) Integer sucursalId) {
        return ResponseEntity.ok(alertaService.calcularCostoInmovilizado(sucursalId));
    }

    /**
     * Marca una alerta como resuelta con la acción correctiva aplicada.
     * 
     * @param id ID de la alerta
     * @param request DTO con acción tomada y observaciones
     * @return Alerta resuelta
     */
    @PutMapping("/{id}/resolver")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'SUPERVISOR')")
    @Operation(
        summary = "Resuelve una alerta",
        description = "Marca una alerta como resuelta indicando la acción correctiva aplicada " +
                      "(LIQUIDACION, PROMOCION, TRANSFERENCIA, DESCONTINUADO, NINGUNA)."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Alerta resuelta exitosamente"),
        @ApiResponse(responseCode = "404", description = "Alerta no encontrada"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    public ResponseEntity<AlertaLentoMovimientoResponseDTO> resolverAlerta(
            @Parameter(description = "ID de la alerta", required = true)
            @PathVariable Integer id,
            @Valid @RequestBody ResolverAlertaRequestDTO request) {
        return ResponseEntity.ok(alertaService.resolverAlerta(id, request));
    }

    /**
     * Elimina una alerta específica.
     * 
     * @param id ID de la alerta
     * @return Respuesta sin contenido
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(
        summary = "Elimina una alerta",
        description = "Elimina permanentemente una alerta del sistema."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Alerta eliminada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Alerta no encontrada")
    })
    public ResponseEntity<Void> eliminarAlerta(
            @Parameter(description = "ID de la alerta", required = true)
            @PathVariable Integer id) {
        alertaService.eliminarAlerta(id);
        return ResponseEntity.noContent().build();
    }
}
