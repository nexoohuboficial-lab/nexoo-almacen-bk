package com.nexoohub.almacen.metricas.controller;

import com.nexoohub.almacen.metricas.dto.*;
import com.nexoohub.almacen.metricas.service.MetricaFinancieraService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para métricas financieras y análisis ejecutivo.
 * 
 * <p>Endpoints para responder: <strong>¿Qué tan rentable es REALMENTE mi negocio?</strong></p>
 * 
 * <p>Proporciona KPIs financieros clave:</p>
 * <ul>
 *   <li>Ventas Totales, Ticket Promedio, Clientes Únicos</li>
 *   <li>Costo de Ventas (COGS), Utilidad Bruta, Margen Bruto %</li>
 *   <li>Gastos Operativos, Utilidad Neta, Margen Neto %</li>
 *   <li>Análisis de métodos de pago (Efectivo/Crédito)</li>
 *   <li>Top productos por ingresos</li>
 *   <li>Comparación de períodos con crecimiento %</li>
 *   <li>Clasificación automática de salud financiera</li>
 * </ul>
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
@RestController
@RequestMapping("/api/v1/metricas-financieras")
public class MetricaFinancieraController {

    private final MetricaFinancieraService metricaFinancieraService;

    public MetricaFinancieraController(MetricaFinancieraService metricaFinancieraService) {
        this.metricaFinancieraService = metricaFinancieraService;
    }

    /**
     * Genera un análisis financiero completo para un período.
     * 
     * <p>POST /api/v1/metricas-financieras/analisis</p>
     * 
     * <p>Calcula todas las métricas financieras para el período especificado.
     * Opcionalmente guarda un snapshot para consultas históricas.</p>
     * 
     * <p>Ejemplo request body:</p>
     * <pre>
     * {
     *   "fechaInicio": "2024-01-01",
     *   "fechaFin": "2024-01-31",
     *   "sucursalId": null,
     *   "guardarSnapshot": true
     * }
     * </pre>
     * 
     * @param request Parámetros del análisis
     * @return Métricas financieras calculadas
     */
    @PostMapping("/analisis")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'AUDITOR')")
    public ResponseEntity<MetricaFinancieraResponseDTO> generarAnalisisFinanciero(
            @Valid @RequestBody AnalisisFinancieroRequestDTO request) {
        MetricaFinancieraResponseDTO resultado = metricaFinancieraService.generarAnalisisFinanciero(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
    }

    /**
     * Consulta métricas financieras de un período.
     * 
     * <p>GET /api/v1/metricas-financieras?fechaInicio=2024-01-01&fechaFin=2024-01-31&sucursalId=1</p>
     * 
     * <p>Si existe un snapshot guardado, lo retorna. Si no existe, 
     * calcula las métricas en tiempo real.</p>
     * 
     * @param fechaInicio Fecha de inicio del período
     * @param fechaFin Fecha de fin del período
     * @param sucursalId ID de la sucursal (opcional, null = consolidado)
     * @return Métricas financieras
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'AUDITOR')")
    public ResponseEntity<MetricaFinancieraResponseDTO> consultarMetricas(
            @RequestParam LocalDate fechaInicio,
            @RequestParam LocalDate fechaFin,
            @RequestParam(required = false) Integer sucursalId) {
        MetricaFinancieraResponseDTO resultado = metricaFinancieraService.consultarMetricas(
                fechaInicio, fechaFin, sucursalId
        );
        return ResponseEntity.ok(resultado);
    }

    /**
     * Compara métricas financieras entre dos períodos.
     * 
     * <p>POST /api/v1/metricas-financieras/comparacion</p>
     * 
     * <p>Calcula el crecimiento porcentual de ventas, utilidad, margen y ticket promedio.
     * Determina tendencias automáticamente (CRECIENDO/ESTABLE/DECRECIENDO).</p>
     * 
     * <p>Ejemplo request body:</p>
     * <pre>
     * {
     *   "fechaInicioPeriodo1": "2024-01-01",
     *   "fechaFinPeriodo1": "2024-01-31",
     *   "fechaInicioPeriodo2": "2023-01-01",
     *   "fechaFinPeriodo2": "2023-01-31",
     *   "sucursalId": null
     * }
     * </pre>
     * 
     * @param fechaInicioPeriodo1 Fecha inicio período actual
     * @param fechaFinPeriodo1 Fecha fin período actual
     * @param fechaInicioPeriodo2 Fecha inicio período anterior
     * @param fechaFinPeriodo2 Fecha fin período anterior
     * @param sucursalId ID sucursal (opcional)
     * @return Comparación de períodos con variaciones y crecimientos
     */
    @PostMapping("/comparacion")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'AUDITOR')")
    public ResponseEntity<ComparacionPeriodosDTO> compararPeriodos(
            @RequestParam LocalDate fechaInicioPeriodo1,
            @RequestParam LocalDate fechaFinPeriodo1,
            @RequestParam LocalDate fechaInicioPeriodo2,
            @RequestParam LocalDate fechaFinPeriodo2,
            @RequestParam(required = false) Integer sucursalId) {
        ComparacionPeriodosDTO resultado = metricaFinancieraService.compararPeriodos(
                fechaInicioPeriodo1, fechaFinPeriodo1,
                fechaInicioPeriodo2, fechaFinPeriodo2,
                sucursalId
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
    }

    /**
     * Obtiene los N productos con mayores ingresos en un período.
     * 
     * <p>GET /api/v1/metricas-financieras/top-productos?fechaInicio=2024-01-01&fechaFin=2024-01-31&limite=10</p>
     * 
     * <p>Analiza los productos que más ingresos generaron, incluyendo:
     * cantidad vendida, ingresos, costos, utilidad, margen %, precio promedio,
     * número de ventas, y porcentaje del total de ingresos.</p>
     * 
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @param sucursalId ID sucursal (opcional)
     * @param limite Número máximo de productos (default: 10)
     * @return Lista de top productos ordenados por ingresos DESC
     */
    @GetMapping("/top-productos")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'AUDITOR')")
    public ResponseEntity<List<TopProductoIngresoDTO>> obtenerTopProductosPorIngresos(
            @RequestParam LocalDate fechaInicio,
            @RequestParam LocalDate fechaFin,
            @RequestParam(required = false) Integer sucursalId,
            @RequestParam(defaultValue = "10") int limite) {
        List<TopProductoIngresoDTO> resultado = metricaFinancieraService.obtenerTopProductosPorIngresos(
                fechaInicio, fechaFin, sucursalId, limite
        );
        return ResponseEntity.ok(resultado);
    }

    /**
     * Obtiene el histórico de métricas financieras guardadas.
     * 
     * <p>GET /api/v1/metricas-financieras/historico?sucursalId=1&limite=12</p>
     * 
     * <p>Retorna los snapshots guardados ordenados por fecha descendente.
     * Útil para gráficos de evolución temporal.</p>
     * 
     * @param sucursalId ID sucursal (opcional, null = consolidado)
     * @param limite Número máximo de resultados (default: 12)
     * @return Lista de métricas históricas
     */
    @GetMapping("/historico")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'AUDITOR')")
    public ResponseEntity<List<MetricaFinancieraResponseDTO>> obtenerHistoricoMetricas(
            @RequestParam(required = false) Integer sucursalId,
            @RequestParam(defaultValue = "12") int limite) {
        List<MetricaFinancieraResponseDTO> resultado = 
                metricaFinancieraService.obtenerHistoricoMetricas(sucursalId, limite);
        return ResponseEntity.ok(resultado);
    }

    /**
     * Dashboard ejecutivo consolidado (todas las sucursales).
     * 
     * <p>GET /api/v1/metricas-financieras/dashboard-ejecutivo?fechaInicio=2024-01-01&fechaFin=2024-01-31</p>
     * 
     * <p>Métricas consolidadas de toda la empresa, sin filtro de sucursal.
     * Ideal para reportes ejecutivos y toma de decisiones estratégicas.</p>
     * 
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @return Métricas consolidadas
     */
    @GetMapping("/dashboard-ejecutivo")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<MetricaFinancieraResponseDTO> obtenerDashboardEjecutivo(
            @RequestParam LocalDate fechaInicio,
            @RequestParam LocalDate fechaFin) {
        MetricaFinancieraResponseDTO resultado = metricaFinancieraService.consultarMetricas(
                fechaInicio, fechaFin, null
        );
        return ResponseEntity.ok(resultado);
    }

    /**
     * Obtiene métricas financieras de una sucursal específica.
     * 
     * <p>GET /api/v1/metricas-financieras/sucursal/{sucursalId}?fechaInicio=2024-01-01&fechaFin=2024-01-31</p>
     * 
     * <p>Métricas filtradas por sucursal para comparación de desempeño entre sucursales.</p>
     * 
     * @param sucursalId ID de la sucursal
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @return Métricas de la sucursal
     */
    @GetMapping("/sucursal/{sucursalId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'SUPERVISOR', 'AUDITOR')")
    public ResponseEntity<MetricaFinancieraResponseDTO> obtenerMetricasPorSucursal(
            @PathVariable Integer sucursalId,
            @RequestParam LocalDate fechaInicio,
            @RequestParam LocalDate fechaFin) {
        MetricaFinancieraResponseDTO resultado = metricaFinancieraService.consultarMetricas(
                fechaInicio, fechaFin, sucursalId
        );
        return ResponseEntity.ok(resultado);
    }

    /**
     * Endpoint de salud del módulo de métricas financieras.
     * 
     * <p>GET /api/v1/metricas-financieras/health</p>
     * 
     * @return Status del servicio
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "Metricas Financieras");
        health.put("version", "1.0");
        health.put("features", List.of(
                "Analisis financiero consolidado",
                "Metricas por sucursal",
                "Comparacion de periodos",
                "Top productos por ingresos",
                "Historico de metricas",
                "Clasificacion automatica de salud financiera"
        ));
        return ResponseEntity.ok(health);
    }
}
