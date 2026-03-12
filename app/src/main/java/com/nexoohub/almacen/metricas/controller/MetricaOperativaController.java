package com.nexoohub.almacen.metricas.controller;

import com.nexoohub.almacen.metricas.dto.AnalisisOperativoRequestDTO;
import com.nexoohub.almacen.metricas.dto.MetricaOperativaResponseDTO;
import com.nexoohub.almacen.metricas.entity.MetricaOperativa;
import com.nexoohub.almacen.metricas.service.MetricaOperativaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

/**
 * Controller REST para gestionar métricas operacionales.
 * 
 * <p>Proporciona endpoints para:</p>
 * <ul>
 *   <li>Generar análisis operativos personalizados</li>
 *   <li>Consultar análisis predefinidos (mes actual, mes anterior)</li>
 *   <li>Guardar snapshots de métricas</li>
 *   <li>Recuperar métricas históricas</li>
 * </ul>
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
@RestController
@RequestMapping("/api/v1/metricas/operativas")
@Tag(name = "Métricas Operativas", description = "Gestión de métricas de eficiencia operativa (traspasos, compras, ventas)")
public class MetricaOperativaController {

    private final MetricaOperativaService metricaService;

    public MetricaOperativaController(MetricaOperativaService metricaService) {
        this.metricaService = metricaService;
    }

    /**
     * Genera análisis operativo personalizado con parámetros específicos.
     * 
     * @param request Parámetros del análisis
     * @return Métricas operacionales calculadas
     */
    @PostMapping("/analisis")
    @Operation(summary = "Análisis operativo personalizado", 
               description = "Genera métricas operacionales para un período específico con opciones de configuración")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'SUPERVISOR', 'AUDITOR')")
    public ResponseEntity<MetricaOperativaResponseDTO> generarAnalisis(
            @Valid @RequestBody AnalisisOperativoRequestDTO request) {
        MetricaOperativaResponseDTO resultado = metricaService.generarAnalisis(request);
        return ResponseEntity.ok(resultado);
    }

    /**
     * Análisis rápido del mes actual (consolidado).
     * 
     * @return Métricas del mes en curso
     */
    @GetMapping("/mes-actual")
    @Operation(summary = "Análisis del mes actual", 
               description = "Genera análisis operativo consolidado del mes en curso")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'SUPERVISOR', 'AUDITOR')")
    public ResponseEntity<MetricaOperativaResponseDTO> analizarMesActual() {
        YearMonth mesActual = YearMonth.now();
        LocalDate inicio = mesActual.atDay(1);
        LocalDate fin = mesActual.atEndOfMonth();

        AnalisisOperativoRequestDTO request = new AnalisisOperativoRequestDTO();
        request.setFechaInicio(inicio);
        request.setFechaFin(fin);
        request.setTipoPeriodo("MENSUAL");
        request.setCompararPeriodoAnterior(true);

        MetricaOperativaResponseDTO resultado = metricaService.generarAnalisis(request);
        return ResponseEntity.ok(resultado);
    }

    /**
     * Análisis rápido del mes anterior (consolidado).
     * 
     * @return Métricas del mes pasado
     */
    @GetMapping("/mes-anterior")
    @Operation(summary = "Análisis del mes anterior", 
               description = "Genera análisis operativo consolidado del mes anterior")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'SUPERVISOR', 'AUDITOR')")
    public ResponseEntity<MetricaOperativaResponseDTO> analizarMesAnterior() {
        YearMonth mesAnterior = YearMonth.now().minusMonths(1);
        LocalDate inicio = mesAnterior.atDay(1);
        LocalDate fin = mesAnterior.atEndOfMonth();

        AnalisisOperativoRequestDTO request = new AnalisisOperativoRequestDTO();
        request.setFechaInicio(inicio);
        request.setFechaFin(fin);
        request.setTipoPeriodo("MENSUAL");

        MetricaOperativaResponseDTO resultado = metricaService.generarAnalisis(request);
        return ResponseEntity.ok(resultado);
    }

    /**
     * Análisis rápido de los últimos 7 días (consolidado).
     * 
     * @return Métricas de la última semana
     */
    @GetMapping("/ultimos-7-dias")
    @Operation(summary = "Análisis de los últimos 7 días", 
               description = "Genera análisis operativo consolidado de la última semana")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'SUPERVISOR', 'AUDITOR')")
    public ResponseEntity<MetricaOperativaResponseDTO> analizarUltimos7Dias() {
        LocalDate fin = LocalDate.now();
        LocalDate inicio = fin.minusDays(6);

        AnalisisOperativoRequestDTO request = new AnalisisOperativoRequestDTO();
        request.setFechaInicio(inicio);
        request.setFechaFin(fin);
        request.setTipoPeriodo("SEMANAL");
        request.setCompararPeriodoAnterior(true);

        MetricaOperativaResponseDTO resultado = metricaService.generarAnalisis(request);
        return ResponseEntity.ok(resultado);
    }

    /**
     * Análisis operativo de una sucursal específica.
     * 
     * @param sucursalId ID de la sucursal
     * @param request Parámetros del análisis
     * @return Métricas de la sucursal
     */
    @PostMapping("/por-sucursal/{sucursalId}")
    @Operation(summary = "Análisis por sucursal", 
               description = "Genera métricas operacionales para una sucursal específica")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'SUPERVISOR', 'AUDITOR')")
    public ResponseEntity<MetricaOperativaResponseDTO> analizarPorSucursal(
            @PathVariable Integer sucursalId,
            @Valid @RequestBody AnalisisOperativoRequestDTO request) {
        request.setSucursalId(sucursalId);
        MetricaOperativaResponseDTO resultado = metricaService.generarAnalisis(request);
        return ResponseEntity.ok(resultado);
    }

    /**
     * Genera análisis y guarda snapshot en la base de datos.
     * 
     * @param request Parámetros del análisis
     * @return Métrica guardada
     */
    @PostMapping("/generar-guardar")
    @Operation(summary = "Generar y guardar métrica", 
               description = "Genera análisis operativo y guarda snapshot en base de datos para consulta histórica")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'SUPERVISOR')")
    public ResponseEntity<MetricaOperativa> generarYGuardar(
            @Valid @RequestBody AnalisisOperativoRequestDTO request) {
        MetricaOperativa metrica = metricaService.guardarMetrica(request);
        return ResponseEntity.ok(metrica);
    }

    /**
     * Obtiene métrica consolidada guardada previamente.
     * 
     * @param fechaInicio Fecha de inicio del período
     * @param fechaFin Fecha de fin del período
     * @return Métrica si existe
     */
    @GetMapping("/consolidado")
    @Operation(summary = "Obtener métrica consolidada guardada", 
               description = "Recupera snapshot de métrica consolidada previamente guardado")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'SUPERVISOR', 'AUDITOR')")
    public ResponseEntity<MetricaOperativa> obtenerMetricaConsolidada(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        return metricaService.obtenerMetricaConsolidada(fechaInicio, fechaFin)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Obtiene historial de métricas consolidadas.
     * 
     * @param fechaHasta Fecha límite (inclusiva)
     * @param limite Número de resultados (default: 10)
     * @return Lista de métricas históricas
     */
    @GetMapping("/historial")
    @Operation(summary = "Historial de métricas consolidadas", 
               description = "Recupera lista de snapshots de métricas históricas")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'SUPERVISOR', 'AUDITOR')")
    public ResponseEntity<List<MetricaOperativa>> obtenerHistorialConsolidado(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaHasta,
            @RequestParam(defaultValue = "10") int limite) {
        LocalDate hasta = fechaHasta != null ? fechaHasta : LocalDate.now();
        List<MetricaOperativa> historial = metricaService.obtenerHistorialConsolidado(hasta, limite);
        return ResponseEntity.ok(historial);
    }

    /**
     * Análisis operativo con detalle de todas las sucursales.
     * 
     * @param request Parámetros del análisis
     * @return Métricas consolidadas con detalle de sucursales
     */
    @PostMapping("/analisis-consolidado-con-detalle")
    @Operation(summary = "Análisis consolidado con detalle de sucursales", 
               description = "Genera análisis operativo consolidado incluyendo métricas individuales de cada sucursal")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'SUPERVISOR', 'AUDITOR')")
    public ResponseEntity<MetricaOperativaResponseDTO> analizarConsolidadoConDetalle(
            @Valid @RequestBody AnalisisOperativoRequestDTO request) {
        request.setIncluirDetalleSucursales(true);
        request.setSucursalId(null); // Forzar análisis consolidado
        MetricaOperativaResponseDTO resultado = metricaService.generarAnalisis(request);
        return ResponseEntity.ok(resultado);
    }
}
