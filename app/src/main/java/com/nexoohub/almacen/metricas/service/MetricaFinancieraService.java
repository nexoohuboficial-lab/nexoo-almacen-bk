package com.nexoohub.almacen.metricas.service;

import com.nexoohub.almacen.metricas.dto.*;
import com.nexoohub.almacen.metricas.entity.MetricaFinanciera;
import com.nexoohub.almacen.metricas.repository.MetricaFinancieraRepository;
import com.nexoohub.almacen.ventas.repository.VentaRepository;
import com.nexoohub.almacen.ventas.repository.DetalleVentaRepository;
import com.nexoohub.almacen.rentabilidad.repository.RentabilidadVentaRepository;
import com.nexoohub.almacen.comisiones.repository.ComisionRepository;
import com.nexoohub.almacen.sucursal.repository.SucursalRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio para calcular y gestionar métricas financieras del negocio.
 * 
 * <p>Proporciona un dashboard financiero completo con métricas clave como:</p>
 * <ul>
 *   <li>Ventas totales y ticket promedio</li>
 *   <li>Costo de ventas (COGS) y utilidad bruta</li>
 *   <li>Márgenes de ganancia (bruto y neto)</li>
 *   <li>Top productos por ingresos</li>
 *   <li>Comparaciones de períodos</li>
 * </ul>
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
@Service
public class MetricaFinancieraService {

    private static final Logger logger = LoggerFactory.getLogger(MetricaFinancieraService.class);

    // Umbrales de clasificación de márgenes
    private static final BigDecimal MARGEN_EXCELENTE = new BigDecimal("30.00");
    private static final BigDecimal MARGEN_BUENO = new BigDecimal("20.00");
    private static final BigDecimal MARGEN_REGULAR = new BigDecimal("10.00");

    // Umbrales de crecimiento
    private static final BigDecimal CRECIMIENTO_MODERADO = new BigDecimal("5.00");

    private final MetricaFinancieraRepository metricaRepository;
    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final RentabilidadVentaRepository rentabilidadRepository;
    private final ComisionRepository comisionRepository;
    private final SucursalRepository sucursalRepository;

    public MetricaFinancieraService(
            MetricaFinancieraRepository metricaRepository,
            VentaRepository ventaRepository,
            DetalleVentaRepository detalleVentaRepository,
            RentabilidadVentaRepository rentabilidadRepository,
            ComisionRepository comisionRepository,
            SucursalRepository sucursalRepository
    ) {
        this.metricaRepository = metricaRepository;
        this.ventaRepository = ventaRepository;
        this.detalleVentaRepository = detalleVentaRepository;
        this.rentabilidadRepository = rentabilidadRepository;
        this.comisionRepository = comisionRepository;
        this.sucursalRepository = sucursalRepository;
    }

    /**
     * Genera análisis financiero completo de un período.
     * 
     * @param request Datos del período a analizar
     * @return Métricas financieras calculadas
     */
    @Transactional
    public MetricaFinancieraResponseDTO generarAnalisisFinanciero(AnalisisFinancieroRequestDTO request) {
        logger.info("Generando análisis financiero para período: {} - {}", 
                request.getFechaInicio(), request.getFechaFin());

        validarPeriodo(request.getFechaInicio(), request.getFechaFin());

        // Calcular métricas en tiempo real
        MetricaFinanciera metrica = calcularMetricas(
                request.getFechaInicio(),
                request.getFechaFin(),
                request.getSucursalId()
        );

        // Guardar snapshot si se solicita
        if (Boolean.TRUE.equals(request.getGuardarSnapshot())) {
            // Eliminar snapshot anterior si existe
            if (metrica.getSucursalId() == null) {
                metricaRepository.deleteByPeriodoInicioAndPeriodoFin(
                        metrica.getPeriodoInicio(),
                        metrica.getPeriodoFin()
                );
            } else {
                metricaRepository.deleteBySucursalIdAndPeriodoInicioAndPeriodoFin(
                        metrica.getSucursalId(),
                        metrica.getPeriodoInicio(),
                        metrica.getPeriodoFin()
                );
            }

            metrica = metricaRepository.save(metrica);
            logger.info("Snapshot de métricas guardado con ID: {}", metrica.getId());
        }

        return construirResponseDTO(metrica);
    }

    /**
     * Consulta métricas financieras históricas de un período.
     * 
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @param sucursalId ID sucursal (opcional)
     * @return Métricas almacenadas o calculadas en tiempo real
     */
    @Transactional(readOnly = true)
    public MetricaFinancieraResponseDTO consultarMetricas(
            LocalDate fechaInicio,
            LocalDate fechaFin,
            Integer sucursalId
    ) {
        logger.info("Consultando métricas para período: {} - {}, sucursal: {}", 
                fechaInicio, fechaFin, sucursalId);

        // Intentar obtener snapshot guardado
        Optional<MetricaFinanciera> metricaOpt;
        if (sucursalId == null) {
            metricaOpt = metricaRepository.findBySucursalIdIsNullAndPeriodoInicioAndPeriodoFin(
                    fechaInicio, fechaFin
            );
        } else {
            metricaOpt = metricaRepository.findBySucursalIdAndPeriodoInicioAndPeriodoFin(
                    sucursalId, fechaInicio, fechaFin
            );
        }

        // Si existe snapshot, devolverlo
        if (metricaOpt.isPresent()) {
            logger.info("Métricas encontradas en snapshot ID: {}", metricaOpt.get().getId());
            return construirResponseDTO(metricaOpt.get());
        }

        // Si no existe, calcular en tiempo real
        logger.info("Snapshot no encontrado. Calculando métricas en tiempo real...");
        MetricaFinanciera metrica = calcularMetricas(fechaInicio, fechaFin, sucursalId);
        return construirResponseDTO(metrica);
    }

    /**
     * Compara métricas de dos períodos (ej: mes actual vs mes anterior).
     * 
     * @param periodoActualInicio Inicio período actual
     * @param periodoActualFin Fin período actual
     * @param periodoAnteriorInicio Inicio período anterior
     * @param periodoAnteriorFin Fin período anterior
     * @param sucursalId ID sucursal (opcional)
     * @return Comparación detallada con crecimiento
     */
    @Transactional(readOnly = true)
    public ComparacionPeriodosDTO compararPeriodos(
            LocalDate periodoActualInicio,
            LocalDate periodoActualFin,
            LocalDate periodoAnteriorInicio,
            LocalDate periodoAnteriorFin,
            Integer sucursalId
    ) {
        logger.info("Comparando períodos: [{}->{}] vs [{}->{}]",
                periodoActualInicio, periodoActualFin, periodoAnteriorInicio, periodoAnteriorFin);

        MetricaFinancieraResponseDTO actual = consultarMetricas(
                periodoActualInicio, periodoActualFin, sucursalId
        );

        MetricaFinancieraResponseDTO anterior = consultarMetricas(
                periodoAnteriorInicio, periodoAnteriorFin, sucursalId
        );

        return calcularComparacion(actual, anterior);
    }

    /**
     * Obtiene top N productos por ingresos generados en un período.
     * 
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @param sucursalId ID sucursal (opcional)
     * @param limite Número de productos a retornar
     * @return Lista de top productos
     */
    @Transactional(readOnly = true)
    public List<TopProductoIngresoDTO> obtenerTopProductosPorIngresos(
            LocalDate fechaInicio,
            LocalDate fechaFin,
            Integer sucursalId,
            int limite
    ) {
        logger.info("Obteniendo top {} productos por ingresos: {} - {}", 
                limite, fechaInicio, fechaFin);

        // Query SQL nativo para agregar ingresos por producto
        LocalDateTime fechaInicioTime = fechaInicio.atStartOfDay();
        LocalDateTime fechaFinTime = fechaFin.atTime(23, 59, 59);

        // TODO: Implementar query nativo en repository si es necesario
        // Por ahora, calcular programáticamente desde detalles de venta

        // Obtener ventas del período
        List<Object[]> ventas;
        if (sucursalId == null) {
            ventas = ventaRepository.findVentasEnPeriodo(fechaInicioTime, fechaFinTime);
        } else {
            ventas = ventaRepository.findVentasEnPeriodoPorSucursal(
                    fechaInicioTime, fechaFinTime, sucursalId
            );
        }

        // Extraer IDs de ventas
        List<Integer> ventaIds = ventas.stream()
                .map(v -> (Integer) v[0])
                .collect(Collectors.toList());

        if (ventaIds.isEmpty()) {
            return Collections.emptyList();
        }

        // Obtener detalles y agrupar por SKU
        List<Object[]> detalles = detalleVentaRepository.findDetallesPorVentasConProducto(ventaIds);

        Map<String, TopProductoIngresoDTO> productoMap = new HashMap<>();

        for (Object[] detalle : detalles) {
            String sku = (String) detalle[0];
            String nombre = (String) detalle[1];
            Integer cantidad = (Integer) detalle[2];
            BigDecimal subtotal = (BigDecimal) detalle[4];

            TopProductoIngresoDTO dto = productoMap.computeIfAbsent(sku, k -> {
                TopProductoIngresoDTO newDto = new TopProductoIngresoDTO();
                newDto.setSkuInterno(sku);
                newDto.setNombreComercial(nombre);
                newDto.setCantidadVendida(0);
                newDto.setIngresosGenerados(BigDecimal.ZERO);
                newDto.setNumeroVentas(0);
                return newDto;
            });

            dto.setCantidadVendida(dto.getCantidadVendida() + cantidad);
            dto.setIngresosGenerados(dto.getIngresosGenerados().add(subtotal));
            dto.setNumeroVentas(dto.getNumeroVentas() + 1);
        }

        // Ordenar por ingresos DESC y limitar
        List<TopProductoIngresoDTO> topProductos = productoMap.values().stream()
                .sorted(Comparator.comparing(TopProductoIngresoDTO::getIngresosGenerados).reversed())
                .limit(limite)
                .collect(Collectors.toList());

        // Calcular porcentajes y rankings
        BigDecimal ingresosTogales = topProductos.stream()
                .map(TopProductoIngresoDTO::getIngresosGenerados)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int ranking = 1;
        for (TopProductoIngresoDTO dto : topProductos) {
            dto.setRanking(ranking++);

            if (ingresosTogales.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal porcentaje = dto.getIngresosGenerados()
                        .divide(ingresosTogales, 4, RoundingMode.HALF_UP)
                        .multiply(new BigDecimal("100"));
                dto.setPorcentajeIngresosTotal(porcentaje);
            }

            // Calcular precio promedio
            if (dto.getCantidadVendida() > 0) {
                BigDecimal precioPromedio = dto.getIngresosGenerados()
                        .divide(new BigDecimal(dto.getCantidadVendida()), 2, RoundingMode.HALF_UP);
                dto.setPrecioPromedioVenta(precioPromedio);
            }
        }

        logger.info("Top {} productos calculados correctamente", topProductos.size());
        return topProductos;
    }

    /**
     * Obtiene histórico de métricas (últimos N períodos).
     * 
     * @param sucursalId ID sucursal (opcional)
     * @param limite Número máximo de resultados
     * @return Lista de métricas históricas
     */
    @Transactional(readOnly = true)
    public List<MetricaFinancieraResponseDTO> obtenerHistoricoMetricas(Integer sucursalId, int limite) {
        logger.info("Obteniendo histórico de métricas para sucursal: {}, limite: {}", sucursalId, limite);

        List<MetricaFinanciera> metricas;
        if (sucursalId == null) {
            metricas = metricaRepository.findBySucursalIdIsNullOrderByPeriodoFinDesc();
        } else {
            metricas = metricaRepository.findBySucursalIdOrderByPeriodoFinDesc(sucursalId);
        }

        return metricas.stream()
                .limit(limite)
                .map(this::construirResponseDTO)
                .collect(Collectors.toList());
    }

    // ==================== MÉTODOS PRIVADOS ====================

    /**
     * Calcula todas las métricas financieras de un período.
     */
    private MetricaFinanciera calcularMetricas(
            LocalDate fechaInicio,
            LocalDate fechaFin,
            Integer sucursalId
    ) {
        LocalDateTime fechaInicioTime = fechaInicio.atStartOfDay();
        LocalDateTime fechaFinTime = fechaFin.atTime(23, 59, 59);

        MetricaFinanciera metrica = new MetricaFinanciera();
        metrica.setSucursalId(sucursalId);
        metrica.setPeriodoInicio(fechaInicio);
        metrica.setPeriodoFin(fechaFin);
        metrica.setTipoPeriodo(determinarTipoPeriodo(fechaInicio, fechaFin));

        // 1. Calcular ventas totales y conteo
        BigDecimal ventasTotales;
        Long numeroVentas;
        Long clientesUnicos;

        if (sucursalId == null) {
            ventasTotales = ventaRepository.calcularVentasTotalesPeriodo(fechaInicioTime, fechaFinTime);
            numeroVentas = ventaRepository.contarVentasPeriodo(fechaInicioTime, fechaFinTime);
            clientesUnicos = ventaRepository.contarClientesUnicosPeriodo(fechaInicioTime, fechaFinTime);
        } else {
            ventasTotales = ventaRepository.calcularVentasTotalesPeriodoPorSucursal(
                    fechaInicioTime, fechaFinTime, sucursalId
            );
            numeroVentas = ventaRepository.contarVentasPeriodoPorSucursal(
                    fechaInicioTime, fechaFinTime, sucursalId
            );
            clientesUnicos = ventaRepository.contarClientesUnicosPeriodoPorSucursal(
                    fechaInicioTime, fechaFinTime, sucursalId
            );
        }

        metrica.setVentasTotales(ventasTotales != null ? ventasTotales : BigDecimal.ZERO);
        metrica.setNumeroVentas(numeroVentas != null ? numeroVentas.intValue() : 0);
        metrica.setClientesUnicos(clientesUnicos != null ? clientesUnicos.intValue() : 0);

        // 2. Calcular ticket promedio
        if (metrica.getNumeroVentas() > 0) {
            BigDecimal ticketPromedio = metrica.getVentasTotales()
                    .divide(new BigDecimal(metrica.getNumeroVentas()), 2, RoundingMode.HALF_UP);
            metrica.setTicketPromedio(ticketPromedio);
        } else {
            metrica.setTicketPromedio(BigDecimal.ZERO);
        }

        // 3. Calcular costo de ventas desde rentabilidad
        BigDecimal costoVentas = rentabilidadRepository.calcularCostoTotalPeriodo(fechaInicio, fechaFin);
        metrica.setCostoVentas(costoVentas != null ? costoVentas : BigDecimal.ZERO);

        // 4. Calcular utilidad bruta
        BigDecimal utilidadBruta = metrica.getVentasTotales().subtract(metrica.getCostoVentas());
        metrica.setUtilidadBruta(utilidadBruta);

        // 5. Calcular margen bruto %
        if (metrica.getVentasTotales().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal margenBruto = utilidadBruta
                    .divide(metrica.getVentasTotales(), 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));
            metrica.setMargenBrutoPorcentaje(margenBruto);
        } else {
            metrica.setMargenBrutoPorcentaje(BigDecimal.ZERO);
        }

        // 6. Calcular gastos operativos (comisiones)
        // Las comisiones se almacenan por mes (año/mes), por lo que necesitamos convertir las fechas
        Integer anioInicio = fechaInicio.getYear();
        Integer mesInicio = fechaInicio.getMonthValue();
        Integer anioFin = fechaFin.getYear();
        Integer mesFin = fechaFin.getMonthValue();
        
        BigDecimal gastosOperativos = comisionRepository.calcularComisionesTotalesPeriodo(
                anioInicio, mesInicio, anioFin, mesFin
        );
        metrica.setGastosOperativos(gastosOperativos != null ? gastosOperativos : BigDecimal.ZERO);

        // 7. Calcular utilidad neta
        BigDecimal utilidadNeta = utilidadBruta.subtract(metrica.getGastosOperativos());
        metrica.setUtilidadNeta(utilidadNeta);

        // 8. Calcular margen neto %
        if (metrica.getVentasTotales().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal margenNeto = utilidadNeta
                    .divide(metrica.getVentasTotales(), 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));
            metrica.setMargenNetoPorcentaje(margenNeto);
        } else {
            metrica.setMargenNetoPorcentaje(BigDecimal.ZERO);
        }

        // 9. Calcular ventas por método de pago
        BigDecimal ventasEfectivo = ventaRepository.calcularVentasPorMetodoPago(
                fechaInicioTime, fechaFinTime, "EFECTIVO", sucursalId
        );
        BigDecimal ventasCredito = ventaRepository.calcularVentasPorMetodoPago(
                fechaInicioTime, fechaFinTime, "CREDITO", sucursalId
        );

        metrica.setVentasEfectivo(ventasEfectivo != null ? ventasEfectivo : BigDecimal.ZERO);
        metrica.setVentasCredito(ventasCredito != null ? ventasCredito : BigDecimal.ZERO);

        logger.info("Métricas calculadas - Ventas: {}, Utilidad: {}, Margen: {}%",
                metrica.getVentasTotales(), metrica.getUtilidadBruta(), metrica.getMargenBrutoPorcentaje());

        return metrica;
    }

    /**
     * Construye el DTO de respuesta desde la entidad.
     */
    private MetricaFinancieraResponseDTO construirResponseDTO(MetricaFinanciera metrica) {
        MetricaFinancieraResponseDTO dto = new MetricaFinancieraResponseDTO(
                metrica.getId(),
                metrica.getSucursalId(),
                metrica.getPeriodoInicio(),
                metrica.getPeriodoFin(),
                metrica.getTipoPeriodo(),
                metrica.getVentasTotales(),
                metrica.getCostoVentas(),
                metrica.getUtilidadBruta(),
                metrica.getMargenBrutoPorcentaje(),
                metrica.getGastosOperativos(),
                metrica.getUtilidadNeta(),
                metrica.getMargenNetoPorcentaje(),
                metrica.getNumeroVentas(),
                metrica.getTicketPromedio(),
                metrica.getClientesUnicos(),
                metrica.getVentasEfectivo(),
                metrica.getVentasCredito()
        );

        // Enriquecer con nombre de sucursal si aplica
        if (metrica.getSucursalId() != null) {
            sucursalRepository.findById(metrica.getSucursalId())
                    .ifPresent(s -> dto.setNombreSucursal(s.getNombre()));
        }

        // Calcular porcentajes de métodos de pago
        if (metrica.getVentasTotales().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal porcEfectivo = metrica.getVentasEfectivo()
                    .divide(metrica.getVentasTotales(), 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));
            BigDecimal porcCredito = metrica.getVentasCredito()
                    .divide(metrica.getVentasTotales(), 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));

            dto.setPorcentajeEfectivo(porcEfectivo);
            dto.setPorcentajeCredito(porcCredito);
        }

        // Clasificar margen
        dto.setClasificacionMargen(clasificarMargen(metrica.getMargenBrutoPorcentaje()));

        // Evaluar salud financiera
        dto.setSaludFinanciera(evaluarSaludFinanciera(metrica));

        return dto;
    }

    /**
     * Calcula comparación entre dos períodos.
     */
    private ComparacionPeriodosDTO calcularComparacion(
            MetricaFinancieraResponseDTO actual,
            MetricaFinancieraResponseDTO anterior
    ) {
        ComparacionPeriodosDTO comparacion = new ComparacionPeriodosDTO(actual, anterior);

        // Variaciones absolutas
        comparacion.setVariacionVentas(
                actual.getVentasTotales().subtract(anterior.getVentasTotales())
        );
        comparacion.setVariacionUtilidad(
                actual.getUtilidadBruta().subtract(anterior.getUtilidadBruta())
        );
        comparacion.setVariacionMargen(
                actual.getMargenBrutoPorcentaje().subtract(anterior.getMargenBrutoPorcentaje())
        );
        comparacion.setVariacionTicketPromedio(
                actual.getTicketPromedio().subtract(anterior.getTicketPromedio())
        );

        // Variaciones porcentuales (crecimiento)
        comparacion.setCrecimientoVentas(
                calcularCrecimientoPorcentual(anterior.getVentasTotales(), actual.getVentasTotales())
        );
        comparacion.setCrecimientoUtilidad(
                calcularCrecimientoPorcentual(anterior.getUtilidadBruta(), actual.getUtilidadBruta())
        );
        comparacion.setCrecimientoClientes(
                calcularCrecimientoPorcentual(
                        new BigDecimal(anterior.getClientesUnicos()),
                        new BigDecimal(actual.getClientesUnicos())
                )
        );
        comparacion.setCrecimientoNumeroVentas(
                calcularCrecimientoPorcentual(
                        new BigDecimal(anterior.getNumeroVentas()),
                        new BigDecimal(actual.getNumeroVentas())
                )
        );

        // Tendencias
        comparacion.setTendenciaVentas(determinarTendencia(comparacion.getCrecimientoVentas()));
        comparacion.setTendenciaMargen(determinarTendencia(comparacion.getVariacionMargen()));

        // Resumen
        comparacion.setResumenComparativo(generarResumenComparativo(comparacion));

        return comparacion;
    }

    /**
     * Calcula crecimiento porcentual entre dos valores.
     */
    private BigDecimal calcularCrecimientoPorcentual(BigDecimal anterior, BigDecimal actual) {
        if (anterior.compareTo(BigDecimal.ZERO) == 0) {
            return actual.compareTo(BigDecimal.ZERO) > 0 ? new BigDecimal("100") : BigDecimal.ZERO;
        }

        return actual.subtract(anterior)
                .divide(anterior, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));
    }

    /**
     * Clasifica el margen de ganancia.
     */
    private String clasificarMargen(BigDecimal margen) {
        if (margen.compareTo(MARGEN_EXCELENTE) >= 0) {
            return "EXCELENTE";
        } else if (margen.compareTo(MARGEN_BUENO) >= 0) {
            return "BUENO";
        } else if (margen.compareTo(MARGEN_REGULAR) >= 0) {
            return "REGULAR";
        } else if (margen.compareTo(BigDecimal.ZERO) >= 0) {
            return "BAJO";
        } else {
            return "NEGATIVO";
        }
    }

    /**
     * Evalúa la salud financiera general.
     */
    private String evaluarSaludFinanciera(MetricaFinanciera metrica) {
        boolean margenSaludable = metrica.getMargenBrutoPorcentaje().compareTo(MARGEN_BUENO) >= 0;
        boolean utilidadPositiva = metrica.getUtilidadNeta().compareTo(BigDecimal.ZERO) > 0;
        boolean ventasSignificativas = metrica.getVentasTotales().compareTo(BigDecimal.ZERO) > 0;

        if (margenSaludable && utilidadPositiva && ventasSignificativas) {
            return "SALUDABLE";
        } else if (utilidadPositiva && ventasSignificativas) {
            return "ACEPTABLE";
        } else if (ventasSignificativas) {
            return "REQUIERE_ATENCION";
        } else {
            return "CRITICA";
        }
    }

    /**
     * Determina tendencia basada en crecimiento.
     */
    private String determinarTendencia(BigDecimal variacion) {
        if (variacion.compareTo(CRECIMIENTO_MODERADO) > 0) {
            return "CRECIENDO";
        } else if (variacion.compareTo(CRECIMIENTO_MODERADO.negate()) < 0) {
            return "DECRECIENDO";
        } else {
            return "ESTABLE";
        }
    }

    /**
     * Genera resumen comparativo en texto.
     */
    private String generarResumenComparativo(ComparacionPeriodosDTO comp) {
        StringBuilder resumen = new StringBuilder();

        resumen.append(String.format("Ventas %s %.2f%%. ",
                comp.getCrecimientoVentas().compareTo(BigDecimal.ZERO) >= 0 ? "↑" : "↓",
                comp.getCrecimientoVentas().abs()));

        resumen.append(String.format("Utilidad %s %.2f%%. ",
                comp.getCrecimientoUtilidad().compareTo(BigDecimal.ZERO) >= 0 ? "↑" : "↓",
                comp.getCrecimientoUtilidad().abs()));

        resumen.append(String.format("Margen %s %.2f puntos porcentuales.",
                comp.getVariacionMargen().compareTo(BigDecimal.ZERO) >= 0 ? "mejoró" : "disminuyó",
                comp.getVariacionMargen().abs()));

        return resumen.toString();
    }

    /**
     * Determina el tipo de período basado en duración.
     */
    private String determinarTipoPeriodo(LocalDate inicio, LocalDate fin) {
        long dias = ChronoUnit.DAYS.between(inicio, fin) + 1;

        if (dias == 1) return "DIARIO";
        if (dias == 7) return "SEMANAL";
        if (dias >= 28 && dias <= 31) return "MENSUAL";
        if (dias >= 89 && dias <= 92) return "TRIMESTRAL";
        if (dias >= 365 && dias <= 366) return "ANUAL";

        return "PERSONALIZADO";
    }

    /**
     * Valida que el período sea correcto.
     */
    private void validarPeriodo(LocalDate inicio, LocalDate fin) {
        if (inicio.isAfter(fin)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }

        if (fin.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de fin no puede ser futura");
        }
    }
}
