package com.nexoohub.almacen.metricas.service;

import com.nexoohub.almacen.compras.entity.Compra;
import com.nexoohub.almacen.compras.entity.DetalleCompra;
import com.nexoohub.almacen.compras.repository.CompraRepository;
import com.nexoohub.almacen.compras.repository.DetalleCompraRepository;
import com.nexoohub.almacen.inventario.entity.MovimientoInventario;
import com.nexoohub.almacen.inventario.repository.MovimientoInventarioRepository;
import com.nexoohub.almacen.metricas.dto.AnalisisOperativoRequestDTO;
import com.nexoohub.almacen.metricas.dto.MetricaOperativaResponseDTO;
import com.nexoohub.almacen.metricas.entity.MetricaOperativa;
import com.nexoohub.almacen.metricas.repository.MetricaOperativaRepository;
import com.nexoohub.almacen.sucursal.entity.Sucursal;
import com.nexoohub.almacen.sucursal.repository.SucursalRepository;
import com.nexoohub.almacen.ventas.entity.DetalleVenta;
import com.nexoohub.almacen.ventas.entity.Venta;
import com.nexoohub.almacen.ventas.repository.DetalleVentaRepository;
import com.nexoohub.almacen.ventas.repository.VentaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para gestionar métricas operacionales.
 * 
 * <p>Calcula métricas de eficiencia operativa basadas en:</p>
 * <ul>
 *   <li>Traspasos entre sucursales (MovimientoInventario)</li>
 *   <li>Compras a proveedores (Compra, DetalleCompra)</li>
 *   <li>Ventas a clientes (Venta, DetalleVenta)</li>
 * </ul>
 * 
 * <p>Genera indicadores de rendimiento como:</p>
 * <ul>
 *   <li>Ratio entrada/salida de inventario</li>
 *   <li>Productividad diaria de ventas</li>
 *   <li>Tasa de rotación de inventario</li>
 *   <li>Clasificación de actividad operacional</li>
 * </ul>
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
@Service
public class MetricaOperativaService {

    private final MetricaOperativaRepository metricaRepository;
    private final MovimientoInventarioRepository movimientoRepository;
    private final CompraRepository compraRepository;
    private final DetalleCompraRepository detalleCompraRepository;
    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final SucursalRepository sucursalRepository;

    public MetricaOperativaService(
            MetricaOperativaRepository metricaRepository,
            MovimientoInventarioRepository movimientoRepository,
            CompraRepository compraRepository,
            DetalleCompraRepository detalleCompraRepository,
            VentaRepository ventaRepository,
            DetalleVentaRepository detalleVentaRepository,
            SucursalRepository sucursalRepository) {
        this.metricaRepository = metricaRepository;
        this.movimientoRepository = movimientoRepository;
        this.compraRepository = compraRepository;
        this.detalleCompraRepository = detalleCompraRepository;
        this.ventaRepository = ventaRepository;
        this.detalleVentaRepository = detalleVentaRepository;
        this.sucursalRepository = sucursalRepository;
    }

    /**
     * Genera análisis operativo completo para un período.
     * 
     * @param request Parámetros del análisis
     * @return DTO con métricas operacionales calculadas
     */
    @Transactional(readOnly = true)
    public MetricaOperativaResponseDTO generarAnalisis(AnalisisOperativoRequestDTO request) {
        MetricaOperativaResponseDTO response = new MetricaOperativaResponseDTO();

        // Información del período
        response.setPeriodoInicio(request.getFechaInicio());
        response.setPeriodoFin(request.getFechaFin());
        response.setTipoPeriodo(request.getTipoPeriodo() != null ? request.getTipoPeriodo() : "PERSONALIZADO");
        response.setDiasPeriodo((int) ChronoUnit.DAYS.between(request.getFechaInicio(), request.getFechaFin()) + 1);
        response.setSucursalId(request.getSucursalId());

        // Si es análisis por sucursal, obtener nombre
        if (request.getSucursalId() != null) {
            sucursalRepository.findById(request.getSucursalId())
                    .ifPresent(sucursal -> response.setNombreSucursal(sucursal.getNombre()));
        }

        // Calcular métricas principales
        LocalDateTime fechaInicioDateTime = request.getFechaInicio().atStartOfDay();
        LocalDateTime fechaFinDateTime = request.getFechaFin().atTime(23, 59, 59);

        response.setTraspasos(calcularMetricasTraspasos(fechaInicioDateTime, fechaFinDateTime, request.getSucursalId()));
        response.setCompras(calcularMetricasCompras(fechaInicioDateTime, fechaFinDateTime, request.getSucursalId()));
        response.setVentas(calcularMetricasVentas(fechaInicioDateTime, fechaFinDateTime, request.getSucursalId()));
        response.setEficiencia(calcularIndicadoresEficiencia(
                response.getTraspasos(),
                response.getCompras(),
                response.getVentas(),
                response.getDiasPeriodo()
        ));

        // Comparación con período anterior (si se solicitó)
        if (Boolean.TRUE.equals(request.getCompararPeriodoAnterior())) {
            response.setComparacion(calcularComparacion(request, response));
        }

        // Detalle de sucursales (si se solicitó y es análisis consolidado)
        if (Boolean.TRUE.equals(request.getIncluirDetalleSucursales()) && request.getSucursalId() == null) {
            response.setDetalleSucursales(calcularDetalleSucursales(
                    fechaInicioDateTime,
                    fechaFinDateTime,
                    request.getLimitTopSucursales()
            ));
        }

        return response;
    }

    /**
     * Calcula métricas de traspasos basadas en movimientos de inventario.
     * 
     * @param inicio Fecha/hora de inicio
     * @param fin Fecha/hora de fin
     * @param sucursalId ID de sucursal (null = consolidado)
     * @return DTO con resumen de traspasos
     */
    private MetricaOperativaResponseDTO.ResumenTraspasosDTO calcularMetricasTraspasos(
            LocalDateTime inicio, LocalDateTime fin, Integer sucursalId) {

        List<MovimientoInventario> movimientos;

        if (sucursalId != null) {
            // Movimientos de la sucursal específica
            movimientos = movimientoRepository.findAll().stream()
                    .filter(m -> m.getFechaMovimiento() != null &&
                            !m.getFechaMovimiento().isBefore(inicio) &&
                            !m.getFechaMovimiento().isAfter(fin) &&
                            m.getSucursalId().equals(sucursalId) &&
                            (m.getTipoMovimiento().equals("ENTRADA_TRASPASO") ||
                                    m.getTipoMovimiento().equals("SALIDA_TRASPASO")))
                    .collect(Collectors.toList());
        } else {
            // Todos los traspasos
            movimientos = movimientoRepository.findAll().stream()
                    .filter(m -> m.getFechaMovimiento() != null &&
                            !m.getFechaMovimiento().isBefore(inicio) &&
                            !m.getFechaMovimiento().isAfter(fin) &&
                            (m.getTipoMovimiento().equals("ENTRADA_TRASPASO") ||
                                    m.getTipoMovimiento().equals("SALIDA_TRASPASO")))
                    .collect(Collectors.toList());
        }

        // Contar traspasos únicos (por rastreoId)
        long totalTraspasos = movimientos.stream()
                .map(MovimientoInventario::getRastreoId)
                .filter(rastreoId -> rastreoId != null)
                .distinct()
                .count();

        int unidadesEntrada = movimientos.stream()
                .filter(m -> "ENTRADA_TRASPASO".equals(m.getTipoMovimiento()))
                .mapToInt(MovimientoInventario::getCantidad)
                .sum();

        int unidadesSalida = movimientos.stream()
                .filter(m -> "SALIDA_TRASPASO".equals(m.getTipoMovimiento()))
                .mapToInt(MovimientoInventario::getCantidad)
                .sum();

        int unidadesNeto = unidadesEntrada - unidadesSalida;

        BigDecimal promedioUnidadesPorTraspaso = totalTraspasos > 0
                ? BigDecimal.valueOf(unidadesEntrada + unidadesSalida)
                .divide(BigDecimal.valueOf(totalTraspasos), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        String tendenciaTraspaso;
        if (unidadesNeto > 50) {
            tendenciaTraspaso = "ENTRADA_NETA";
        } else if (unidadesNeto < -50) {
            tendenciaTraspaso = "SALIDA_NETA";
        } else {
            tendenciaTraspaso = "EQUILIBRADO";
        }

        return new MetricaOperativaResponseDTO.ResumenTraspasosDTO(
                (int) totalTraspasos,
                unidadesEntrada,
                unidadesSalida,
                unidadesNeto,
                promedioUnidadesPorTraspaso,
                tendenciaTraspaso
        );
    }

    /**
     * Calcula métricas de compras.
     * 
     * @param inicio Fecha/hora de inicio
     * @param fin Fecha/hora de fin
     * @param sucursalId ID de sucursal (null = consolidado, nota: compras no tienen sucursal en el modelo actual)
     * @return DTO con resumen de compras
     */
    private MetricaOperativaResponseDTO.ResumenComprasDTO calcularMetricasCompras(
            LocalDateTime inicio, LocalDateTime fin, Integer sucursalId) {

        // Nota: Las compras generalmente son a nivel empresa, no por sucursal
        // Si se requiere filtrar por sucursal, necesitaríamos agregar ese campo a Compra

        List<Compra> compras = compraRepository.findAll().stream()
                .filter(c -> c.getFechaCompra() != null &&
                        !c.getFechaCompra().isBefore(inicio) &&
                        !c.getFechaCompra().isAfter(fin))
                .collect(Collectors.toList());

        int totalCompras = compras.size();

        BigDecimal gastoTotal = compras.stream()
                .map(Compra::getTotalCompra)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calcular unidades compradas (sumar cantidades de detalles)
        List<Integer> compraIds = compras.stream()
                .map(Compra::getId)
                .collect(Collectors.toList());

        int unidadesCompradas = 0;
        if (!compraIds.isEmpty()) {
            unidadesCompradas = detalleCompraRepository.findAll().stream()
                    .filter(d -> compraIds.contains(d.getCompraId()))
                    .mapToInt(DetalleCompra::getCantidad)
                    .sum();
        }

        BigDecimal compraPromedio = totalCompras > 0
                ? gastoTotal.divide(BigDecimal.valueOf(totalCompras), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        int diasPeriodo = (int) ChronoUnit.DAYS.between(inicio.toLocalDate(), fin.toLocalDate()) + 1;
        BigDecimal frecuenciaCompras = diasPeriodo > 0
                ? BigDecimal.valueOf(totalCompras).divide(BigDecimal.valueOf(diasPeriodo), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        return new MetricaOperativaResponseDTO.ResumenComprasDTO(
                totalCompras,
                unidadesCompradas,
                gastoTotal,
                compraPromedio,
                frecuenciaCompras
        );
    }

    /**
     * Calcula métricas de ventas.
     * 
     * @param inicio Fecha/hora de inicio
     * @param fin Fecha/hora de fin
     * @param sucursalId ID de sucursal (null = consolidado)
     * @return DTO con resumen de ventas
     */
    private MetricaOperativaResponseDTO.ResumenVentasDTO calcularMetricasVentas(
            LocalDateTime inicio, LocalDateTime fin, Integer sucursalId) {

        List<Venta> ventas;

        if (sucursalId != null) {
            ventas = ventaRepository.findAll().stream()
                    .filter(v -> v.getFechaVenta() != null &&
                            !v.getFechaVenta().isBefore(inicio) &&
                            !v.getFechaVenta().isAfter(fin) &&
                            sucursalId.equals(v.getSucursalId()))
                    .collect(Collectors.toList());
        } else {
            ventas = ventaRepository.findAll().stream()
                    .filter(v -> v.getFechaVenta() != null &&
                            !v.getFechaVenta().isBefore(inicio) &&
                            !v.getFechaVenta().isAfter(fin))
                    .collect(Collectors.toList());
        }

        int totalVentas = ventas.size();

        BigDecimal ingresoTotal = ventas.stream()
                .map(Venta::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calcular unidades vendidas
        List<Integer> ventaIds = ventas.stream()
                .map(Venta::getId)
                .collect(Collectors.toList());

        int unidadesVendidas = 0;
        if (!ventaIds.isEmpty()) {
            unidadesVendidas = detalleVentaRepository.findAll().stream()
                    .filter(d -> ventaIds.contains(d.getVentaId()))
                    .mapToInt(DetalleVenta::getCantidad)
                    .sum();
        }

        BigDecimal ventaPromedio = totalVentas > 0
                ? ingresoTotal.divide(BigDecimal.valueOf(totalVentas), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        int diasPeriodo = (int) ChronoUnit.DAYS.between(inicio.toLocalDate(), fin.toLocalDate()) + 1;
        BigDecimal frecuenciaVentas = diasPeriodo > 0
                ? BigDecimal.valueOf(totalVentas).divide(BigDecimal.valueOf(diasPeriodo), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        return new MetricaOperativaResponseDTO.ResumenVentasDTO(
                totalVentas,
                unidadesVendidas,
                ingresoTotal,
                ventaPromedio,
                frecuenciaVentas
        );
    }

    /**
     * Calcula indicadores de eficiencia y productividad.
     * 
     * @param traspasos Resumen de traspasos
     * @param compras Resumen de compras
     * @param ventas Resumen de ventas
     * @param diasPeriodo Número de días del período
     * @return DTO con indicadores de eficiencia
     */
    private MetricaOperativaResponseDTO.IndicadoresEficienciaDTO calcularIndicadoresEficiencia(
            MetricaOperativaResponseDTO.ResumenTraspasosDTO traspasos,
            MetricaOperativaResponseDTO.ResumenComprasDTO compras,
            MetricaOperativaResponseDTO.ResumenVentasDTO ventas,
            Integer diasPeriodo) {

        // Ratio entrada/salida
        int totalEntradas = compras.getUnidadesCompradas() + traspasos.getUnidadesEntrada();
        int totalSalidas = ventas.getUnidadesVendidas() + traspasos.getUnidadesSalida();

        BigDecimal ratioEntradaSalida = totalSalidas > 0
                ? BigDecimal.valueOf(totalEntradas)
                .divide(BigDecimal.valueOf(totalSalidas), 4, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        // Productividad diaria de ventas
        BigDecimal productividadDiariaVentas = diasPeriodo > 0
                ? ventas.getIngresoTotal().divide(BigDecimal.valueOf(diasPeriodo), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        // Tasa de rotación de inventario
        int totalMovido = totalEntradas + totalSalidas;
        BigDecimal tasaRotacionInventario = totalMovido > 0
                ? BigDecimal.valueOf(ventas.getUnidadesVendidas())
                .divide(BigDecimal.valueOf(totalMovido), 4, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        // Total de operaciones
        int totalOperaciones = traspasos.getTotalTraspasos() + compras.getTotalCompras() + ventas.getTotalVentas();

        BigDecimal operacionesPorDia = diasPeriodo > 0
                ? BigDecimal.valueOf(totalOperaciones).divide(BigDecimal.valueOf(diasPeriodo), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        // Clasificación de actividad
        String clasificacionActividad;
        if (operacionesPorDia.compareTo(BigDecimal.valueOf(75)) >= 0) {
            clasificacionActividad = "ALTO";
        } else if (operacionesPorDia.compareTo(BigDecimal.valueOf(25)) >= 0) {
            clasificacionActividad = "MEDIO";
        } else {
            clasificacionActividad = "BAJO";
        }

        // Balance operacional
        String balanceOperacional;
        if (ratioEntradaSalida.compareTo(BigDecimal.valueOf(1.1)) > 0) {
            balanceOperacional = "POSITIVO"; // Más entradas que salidas
        } else if (ratioEntradaSalida.compareTo(BigDecimal.valueOf(0.9)) < 0) {
            balanceOperacional = "NEGATIVO"; // Más salidas que entradas
        } else {
            balanceOperacional = "EQUILIBRADO";
        }

        return new MetricaOperativaResponseDTO.IndicadoresEficienciaDTO(
                ratioEntradaSalida,
                productividadDiariaVentas,
                tasaRotacionInventario,
                totalOperaciones,
                operacionesPorDia,
                clasificacionActividad,
                balanceOperacional
        );
    }

    /**
     * Calcula comparación con el período anterior.
     * 
     * @param request Request original
     * @param actual Métricas del período actual
     * @return DTO con comparación
     */
    private MetricaOperativaResponseDTO.ComparacionPeriodoDTO calcularComparacion(
            AnalisisOperativoRequestDTO request,
            MetricaOperativaResponseDTO actual) {

        // Calcular fechas del período anterior
        long diasDiferencia = ChronoUnit.DAYS.between(request.getFechaInicio(), request.getFechaFin());
        LocalDate anteriorInicio = request.getFechaInicio().minusDays(diasDiferencia + 1);
        LocalDate anteriorFin = request.getFechaInicio().minusDays(1);

        // Generar análisis del período anterior
        AnalisisOperativoRequestDTO requestAnterior = new AnalisisOperativoRequestDTO();
        requestAnterior.setFechaInicio(anteriorInicio);
        requestAnterior.setFechaFin(anteriorFin);
        requestAnterior.setSucursalId(request.getSucursalId());
        requestAnterior.setTipoPeriodo(request.getTipoPeriodo());
        requestAnterior.setCompararPeriodoAnterior(false); // Evitar recursión

        MetricaOperativaResponseDTO anterior = generarAnalisis(requestAnterior);

        // Calcular crecimientos
        int operacionesAnterior = anterior.getEficiencia().getTotalOperaciones();
        int operacionesActual = actual.getEficiencia().getTotalOperaciones();

        BigDecimal crecimientoOperaciones = operacionesAnterior > 0
                ? BigDecimal.valueOf(operacionesActual - operacionesAnterior)
                .divide(BigDecimal.valueOf(operacionesAnterior), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;

        BigDecimal crecimientoVentas = anterior.getVentas().getIngresoTotal().compareTo(BigDecimal.ZERO) > 0
                ? actual.getVentas().getIngresoTotal().subtract(anterior.getVentas().getIngresoTotal())
                .divide(anterior.getVentas().getIngresoTotal(), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;

        BigDecimal crecimientoCompras = anterior.getCompras().getGastoTotal().compareTo(BigDecimal.ZERO) > 0
                ? actual.getCompras().getGastoTotal().subtract(anterior.getCompras().getGastoTotal())
                .divide(anterior.getCompras().getGastoTotal(), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;

        String tendencia;
        if (crecimientoOperaciones.compareTo(BigDecimal.valueOf(10)) > 0) {
            tendencia = "CRECIMIENTO";
        } else if (crecimientoOperaciones.compareTo(BigDecimal.valueOf(-10)) < 0) {
            tendencia = "DECRECIMIENTO";
        } else {
            tendencia = "ESTABLE";
        }

        return new MetricaOperativaResponseDTO.ComparacionPeriodoDTO(
                operacionesAnterior,
                operacionesActual,
                crecimientoOperaciones,
                crecimientoVentas,
                crecimientoCompras,
                tendencia
        );
    }

    /**
     * Calcula detalle de sucursales individuales.
     * 
     * @param inicio Fecha/hora inicio
     * @param fin Fecha/hora fin
     * @param limite Número de sucursales a retornar
     * @return Lista de sucursales con métricas
     */
    private List<MetricaOperativaResponseDTO.DetalleSucursalDTO> calcularDetalleSucursales(
            LocalDateTime inicio, LocalDateTime fin, Integer limite) {

        List<Sucursal> sucursales = sucursalRepository.findAll();
        List<MetricaOperativaResponseDTO.DetalleSucursalDTO> detalle = new ArrayList<>();

        for (Sucursal sucursal : sucursales) {
            // Generar métricas para cada sucursal
            AnalisisOperativoRequestDTO requestSucursal = new AnalisisOperativoRequestDTO();
            requestSucursal.setFechaInicio(inicio.toLocalDate());
            requestSucursal.setFechaFin(fin.toLocalDate());
            requestSucursal.setSucursalId(sucursal.getId());
            requestSucursal.setCompararPeriodoAnterior(false);

            MetricaOperativaResponseDTO metricaSucursal = generarAnalisis(requestSucursal);

            MetricaOperativaResponseDTO.DetalleSucursalDTO dto = new MetricaOperativaResponseDTO.DetalleSucursalDTO(
                    sucursal.getId(),
                    sucursal.getNombre(),
                    metricaSucursal.getEficiencia().getTotalOperaciones(),
                    metricaSucursal.getVentas().getTotalVentas(),
                    metricaSucursal.getCompras().getTotalCompras(),
                    metricaSucursal.getTraspasos().getTotalTraspasos(),
                    metricaSucursal.getVentas().getIngresoTotal(),
                    metricaSucursal.getEficiencia().getClasificacionActividad()
            );

            detalle.add(dto);
        }

        // Ordenar por total de operaciones (descendente) y limitar
        return detalle.stream()
                .sorted((a, b) -> b.getTotalOperaciones().compareTo(a.getTotalOperaciones()))
                .limit(limite != null ? limite : 5)
                .collect(Collectors.toList());
    }

    /**
     * Guarda una métrica operativa (snapshot) en la base de datos.
     * 
     * @param request Parámetros del análisis
     * @return Métrica guardada
     */
    @Transactional
    public MetricaOperativa guardarMetrica(AnalisisOperativoRequestDTO request) {
        // Generar análisis
        MetricaOperativaResponseDTO analisis = generarAnalisis(request);

        // Crear entidad
        MetricaOperativa metrica = new MetricaOperativa();
        metrica.setPeriodoInicio(analisis.getPeriodoInicio());
        metrica.setPeriodoFin(analisis.getPeriodoFin());
        metrica.setTipoPeriodo(analisis.getTipoPeriodo());
        metrica.setSucursalId(analisis.getSucursalId());
        metrica.setNombreSucursal(analisis.getNombreSucursal());
        metrica.setDiasPeriodo(analisis.getDiasPeriodo());

        // Traspasos
        metrica.setTotalTraspasos(analisis.getTraspasos().getTotalTraspasos());
        metrica.setUnidadesTraspasoEntrada(analisis.getTraspasos().getUnidadesEntrada());
        metrica.setUnidadesTraspasoSalida(analisis.getTraspasos().getUnidadesSalida());
        metrica.setUnidadesTraspasoNeto(analisis.getTraspasos().getUnidadesNeto());

        // Compras
        metrica.setTotalCompras(analisis.getCompras().getTotalCompras());
        metrica.setUnidadesCompradas(analisis.getCompras().getUnidadesCompradas());
        metrica.setGastoTotalCompras(analisis.getCompras().getGastoTotal());
        metrica.setCompraPromedio(analisis.getCompras().getCompraPromedio());
        metrica.setFrecuenciaCompras(analisis.getCompras().getFrecuenciaCompras());

        // Ventas
        metrica.setTotalVentas(analisis.getVentas().getTotalVentas());
        metrica.setUnidadesVendidas(analisis.getVentas().getUnidadesVendidas());
        metrica.setIngresoTotalVentas(analisis.getVentas().getIngresoTotal());
        metrica.setVentaPromedio(analisis.getVentas().getVentaPromedio());
        metrica.setFrecuenciaVentas(analisis.getVentas().getFrecuenciaVentas());

        // Eficiencia
        metrica.setRatioEntradaSalida(analisis.getEficiencia().getRatioEntradaSalida());
        metrica.setProductividadDiariaVentas(analisis.getEficiencia().getProductividadDiariaVentas());
        metrica.setTasaRotacionInventario(analisis.getEficiencia().getTasaRotacionInventario());
        metrica.setTotalOperaciones(analisis.getEficiencia().getTotalOperaciones());
        metrica.setOperacionesPoDia(analisis.getEficiencia().getOperacionesPorDia());
        metrica.setClasificacionActividad(analisis.getEficiencia().getClasificacionActividad());
        metrica.setBalanceOperacional(analisis.getEficiencia().getBalanceOperacional());

        return metricaRepository.save(metrica);
    }

    /**
     * Obtiene una métrica consolidada guardada previamente.
     * 
     * @param inicio Fecha de inicio del período
     * @param fin Fecha de fin del período
     * @return Optional con la métrica si existe
     */
    @Transactional(readOnly = true)
    public java.util.Optional<MetricaOperativa> obtenerMetricaConsolidada(LocalDate inicio, LocalDate fin) {
        return metricaRepository.findMetricaConsolidada(inicio, fin);
    }

    /**
     * Obtiene historial de métricas consolidadas.
     * 
     * @param hasta Fecha límite
     * @param limite Número de resultados
     * @return Lista de métricas históricas
     */
    @Transactional(readOnly = true)
    public List<MetricaOperativa> obtenerHistorialConsolidado(LocalDate hasta, int limite) {
        List<MetricaOperativa> todas = metricaRepository.findHistorialConsolidado(hasta);
        return todas.stream().limit(limite).toList();
    }
}
