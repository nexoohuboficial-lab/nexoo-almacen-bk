package com.nexoohub.almacen.metricas.controller;

import com.nexoohub.almacen.metricas.dto.AnalisisVentaClienteRequestDTO;
import com.nexoohub.almacen.metricas.dto.MetricaVentaClienteResponseDTO;
import com.nexoohub.almacen.metricas.entity.MetricaVentaCliente;
import com.nexoohub.almacen.metricas.service.MetricaVentaClienteService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Controlador REST para métricas de ventas y clientes.
 * 
 * <p>Endpoints para consultar rendimiento del equipo, comportamiento de clientes,
 * y análisis de ventas por período.</p>
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
@RestController
@RequestMapping("/api/metricas/ventas-clientes")
@Validated
public class MetricaVentaClienteController {

    private static final Logger logger = LoggerFactory.getLogger(MetricaVentaClienteController.class);

    private final MetricaVentaClienteService service;

    public MetricaVentaClienteController(MetricaVentaClienteService service) {
        this.service = service;
    }

    /**
     * Genera el análisis completo de ventas y clientes para un período.
     * 
     * POST /api/metricas/ventas-clientes/analisis
     * 
     * @param request Parámetros del análisis
     * @return Análisis completo con métricas de ventas, clientes y vendedores
     */
    @PostMapping("/analisis")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'AUDITOR')")
    public ResponseEntity<MetricaVentaClienteResponseDTO> generarAnalisis(
            @Valid @RequestBody AnalisisVentaClienteRequestDTO request) {
        
        logger.info("POST /analisis - Generando análisis de ventas y clientes: {}", request);
        
        MetricaVentaClienteResponseDTO response = service.generarAnalisis(request);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene la métrica consolidada de un período específico.
     * 
     * GET /api/metricas/ventas-clientes/consolidado?fechaInicio=2024-01-01&fechaFin=2024-01-31
     * 
     * @param fechaInicio Fecha de inicio del período
     * @param fechaFin Fecha de fin del período
     * @return Métrica consolidada guardada
     */
    @GetMapping("/consolidado")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'AUDITOR')")
    public ResponseEntity<MetricaVentaCliente> obtenerMetricaConsolidada(
            @RequestParam LocalDate fechaInicio,
            @RequestParam LocalDate fechaFin) {
        
        logger.info("GET /consolidado - Consultando métrica consolidada: {} a {}", 
                fechaInicio, fechaFin);
        
        Optional<MetricaVentaCliente> metrica = service.obtenerMetricaConsolidada(fechaInicio, fechaFin);
        
        return metrica.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Obtiene el historial de métricas consolidadas.
     * 
     * GET /api/metricas/ventas-clientes/historial?tipoPeriodo=MENSUAL&fechaHasta=2024-12-31
     * 
     * @param tipoPeriodo Tipo de período (DIARIO, SEMANAL, MENSUAL, etc.)
     * @param fechaHasta Fecha límite
     * @return Lista de métricas históricas
     */
    @GetMapping("/historial")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'AUDITOR')")
    public ResponseEntity<List<MetricaVentaCliente>> obtenerHistorial(
            @RequestParam(defaultValue = "MENSUAL") String tipoPeriodo,
            @RequestParam LocalDate fechaHasta) {
        
        logger.info("GET /historial - Consultando historial: {} hasta {}", tipoPeriodo, fechaHasta);
        
        List<MetricaVentaCliente> historial = service.obtenerHistorialConsolidado(tipoPeriodo, fechaHasta);
        
        return ResponseEntity.ok(historial);
    }

    /**
     * Genera y guarda el análisis de ventas y clientes.
     * 
     * POST /api/metricas/ventas-clientes/generar-guardar
     * 
     * @param request Parámetros del análisis
     * @return Métrica guardada
     */
    @PostMapping("/generar-guardar")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    public ResponseEntity<MetricaVentaCliente> generarYGuardar(
            @Valid @RequestBody AnalisisVentaClienteRequestDTO request) {
        
        logger.info("POST /generar-guardar - Generando y guardando métrica: {}", request);
        
        MetricaVentaClienteResponseDTO response = service.generarAnalisis(request);
        
        // Convertir response a entidad y guardar
        MetricaVentaCliente metrica = new MetricaVentaCliente();
        metrica.setPeriodoInicio(response.getPeriodoInicio());
        metrica.setPeriodoFin(response.getPeriodoFin());
        metrica.setTipoPeriodo(response.getTipoPeriodo());
        metrica.setSucursalId(response.getSucursalId());
        metrica.setNombreSucursal(response.getNombreSucursal());
        
        // Copiar métricas de ventas
        if (response.getResumenVentas() != null) {
            metrica.setTotalVentas(response.getResumenVentas().getTotalVentas());
            metrica.setNumeroTransacciones(response.getResumenVentas().getNumeroTransacciones());
            metrica.setTicketPromedio(response.getResumenVentas().getTicketPromedio());
            metrica.setVentaPromedioDia(response.getResumenVentas().getVentaPromedioDia());
        }
        
        // Copiar métricas de clientes
        if (response.getResumenClientes() != null) {
            metrica.setTotalClientesActivos(response.getResumenClientes().getTotalClientesActivos());
            metrica.setClientesNuevos(response.getResumenClientes().getClientesNuevos());
            metrica.setClientesRecurrentes(response.getResumenClientes().getClientesRecurrentes());
            metrica.setClientesInactivos(response.getResumenClientes().getClientesInactivos());
            metrica.setTasaRetencion(response.getResumenClientes().getTasaRetencion());
            metrica.setValorVidaCliente(response.getResumenClientes().getValorVidaCliente());
            metrica.setFrecuenciaCompra(response.getResumenClientes().getFrecuenciaCompra());
        }
        
        // Copiar métricas de vendedores
        if (response.getResumenVendedores() != null) {
            metrica.setTotalVendedores(response.getResumenVendedores().getTotalVendedores());
            metrica.setTopVendedorId(response.getResumenVendedores().getTopVendedorId());
            metrica.setTopVendedorNombre(response.getResumenVendedores().getTopVendedorNombre());
            metrica.setTopVendedorVentas(response.getResumenVendedores().getTopVendedorVentas());
            metrica.setTopVendedorTransacciones(response.getResumenVendedores().getTopVendedorTransacciones());
            metrica.setVentaPromedioVendedor(response.getResumenVendedores().getVentaPromedioVendedor());
        }
        
        // Copiar métodos de pago
        if (response.getMetodosPago() != null) {
            metrica.setVentasEfectivo(response.getMetodosPago().getVentasEfectivo());
            metrica.setVentasTarjeta(response.getMetodosPago().getVentasTarjeta());
            metrica.setVentasCredito(response.getMetodosPago().getVentasCredito());
            metrica.setPorcentajeEfectivo(response.getMetodosPago().getPorcentajeEfectivo());
        }
        
        // Copiar comparación
        if (response.getComparacion() != null) {
            metrica.setCrecimientoVsAnterior(response.getComparacion().getCrecimiento());
        }
        
        MetricaVentaCliente metricaGuardada = service.guardarMetrica(metrica);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(metricaGuardada);
    }

    /**
     * Genera análisis rápido del mes actual.
     * 
     * GET /api/metricas/ventas-clientes/mes-actual
     * 
     * @return Análisis del mes en curso
     */
    @GetMapping("/mes-actual")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'AUDITOR')")
    public ResponseEntity<MetricaVentaClienteResponseDTO> analizarMesActual() {
        logger.info("GET /mes-actual - Analizando mes actual");
        
        LocalDate hoy = LocalDate.now();
        LocalDate inicioMes = hoy.withDayOfMonth(1);
        LocalDate finMes = hoy.withDayOfMonth(hoy.lengthOfMonth());
        
        AnalisisVentaClienteRequestDTO request = new AnalisisVentaClienteRequestDTO();
        request.setFechaInicio(inicioMes);
        request.setFechaFin(finMes);
        request.setTipoPeriodo("MENSUAL");
        request.setCompararPeriodoAnterior(true);
        request.setIncluirDetalleVendedores(true);
        request.setIncluirDetalleClientes(true);
        request.setLimitTopVendedores(5);
        request.setLimitTopClientes(10);
        
        MetricaVentaClienteResponseDTO response = service.generarAnalisis(request);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Genera análisis del mes anterior.
     * 
     * GET /api/metricas/ventas-clientes/mes-anterior
     * 
     * @return Análisis del mes pasado
     */
    @GetMapping("/mes-anterior")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'AUDITOR')")
    public ResponseEntity<MetricaVentaClienteResponseDTO> analizarMesAnterior() {
        logger.info("GET /mes-anterior - Analizando mes anterior");
        
        LocalDate hoy = LocalDate.now();
        LocalDate inicioMesAnterior = hoy.minusMonths(1).withDayOfMonth(1);
        LocalDate finMesAnterior = hoy.minusMonths(1).withDayOfMonth(
                hoy.minusMonths(1).lengthOfMonth());
        
        AnalisisVentaClienteRequestDTO request = new AnalisisVentaClienteRequestDTO();
        request.setFechaInicio(inicioMesAnterior);
        request.setFechaFin(finMesAnterior);
        request.setTipoPeriodo("MENSUAL");
        request.setCompararPeriodoAnterior(true);
        request.setIncluirDetalleVendedores(true);
        request.setIncluirDetalleClientes(true);
        request.setLimitTopVendedores(5);
        request.setLimitTopClientes(10);
        
        MetricaVentaClienteResponseDTO response = service.generarAnalisis(request);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Genera análisis de los últimos 7 días.
     * 
     * GET /api/metricas/ventas-clientes/ultimos-7-dias
     * 
     * @return Análisis semanal
     */
    @GetMapping("/ultimos-7-dias")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'AUDITOR')")
    public ResponseEntity<MetricaVentaClienteResponseDTO> analizarUltimos7Dias() {
        logger.info("GET /ultimos-7-dias - Analizando última semana");
        
        LocalDate hoy = LocalDate.now();
        LocalDate hace7Dias = hoy.minusDays(7);
        
        AnalisisVentaClienteRequestDTO request = new AnalisisVentaClienteRequestDTO();
        request.setFechaInicio(hace7Dias);
        request.setFechaFin(hoy);
        request.setTipoPeriodo("SEMANAL");
        request.setCompararPeriodoAnterior(true);
        request.setIncluirDetalleVendedores(true);
        request.setIncluirDetalleClientes(true);
        request.setLimitTopVendedores(3);
        request.setLimitTopClientes(5);
        
        MetricaVentaClienteResponseDTO response = service.generarAnalisis(request);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Genera análisis por sucursal para un período.
     * 
     * POST /api/metricas/ventas-clientes/por-sucursal/{sucursalId}
     * 
     * @param sucursalId ID de la sucursal
     * @param request Parámetros del análisis
     * @return Análisis de la sucursal
     */
    @PostMapping("/por-sucursal/{sucursalId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'AUDITOR')")
    public ResponseEntity<MetricaVentaClienteResponseDTO> analizarPorSucursal(
            @PathVariable Integer sucursalId,
            @Valid @RequestBody AnalisisVentaClienteRequestDTO request) {
        
        logger.info("POST /por-sucursal/{} - Analizando sucursal", sucursalId);
        
        request.setSucursalId(sucursalId);
        MetricaVentaClienteResponseDTO response = service.generarAnalisis(request);
        
        return ResponseEntity.ok(response);
    }
}
