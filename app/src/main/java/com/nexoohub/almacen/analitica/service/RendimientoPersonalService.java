package com.nexoohub.almacen.analitica.service;

import com.nexoohub.almacen.analitica.dto.RendimientoEmpleadoResponse;
import com.nexoohub.almacen.analitica.entity.ReporteRendimientoEmpleado;
import com.nexoohub.almacen.analitica.repository.ReporteRendimientoEmpleadoRepository;
import com.nexoohub.almacen.cotizaciones.entity.Cotizacion;
import com.nexoohub.almacen.cotizaciones.repository.CotizacionRepository;
import com.nexoohub.almacen.empleados.entity.Empleado;
import com.nexoohub.almacen.empleados.repository.EmpleadoRepository;
import com.nexoohub.almacen.ventas.entity.Devolucion;
import com.nexoohub.almacen.ventas.entity.Venta;
import com.nexoohub.almacen.ventas.repository.DevolucionRepository;
import com.nexoohub.almacen.ventas.repository.VentaRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Servicio analítico ANA-04: calcula y persiste snapshots mensuales
 * de KPIs de rendimiento para cada vendedor activo.
 *
 * KPIs calculados:
 *   - Total de ventas (conteo y monto)
 *   - Ticket promedio
 *   - Tasa de conversión (cotizaciones → ventas, en %)
 *   - Tasa de devoluciones (devoluciones / ventas, en %)
 *   - Hora pico (hora del día con más ventas, 0-23)
 */
@Service
public class RendimientoPersonalService {

    private static final Logger log = LoggerFactory.getLogger(RendimientoPersonalService.class);

    private final EmpleadoRepository empleadoRepository;
    private final VentaRepository ventaRepository;
    private final CotizacionRepository cotizacionRepository;
    private final DevolucionRepository devolucionRepository;
    private final ReporteRendimientoEmpleadoRepository reporteRepository;

    public RendimientoPersonalService(
            EmpleadoRepository empleadoRepository,
            VentaRepository ventaRepository,
            CotizacionRepository cotizacionRepository,
            DevolucionRepository devolucionRepository,
            ReporteRendimientoEmpleadoRepository reporteRepository) {
        this.empleadoRepository = empleadoRepository;
        this.ventaRepository = ventaRepository;
        this.cotizacionRepository = cotizacionRepository;
        this.devolucionRepository = devolucionRepository;
        this.reporteRepository = reporteRepository;
    }

    // ──────────────────────────────────────────────────────────────────
    // CÁLCULO MASIVO
    // ──────────────────────────────────────────────────────────────────

    /**
     * Calcula y persiste/actualiza el snapshot de rendimiento de TODOS
     * los empleados activos para el mes y año indicados.
     *
     * @param mes  1-12
     * @param anio año (ej. 2026)
     * @return lista de DTOs con los resultados calculados
     */
    @Transactional
    public List<RendimientoEmpleadoResponse> calcularRendimientoMensual(int mes, int anio) {
        log.info("Iniciando cálculo ANA-04: rendimiento de personal para {}/{}", mes, anio);

        // Rango del mes como LocalDateTime
        YearMonth ym = YearMonth.of(anio, mes);
        LocalDateTime inicio = ym.atDay(1).atStartOfDay();
        LocalDateTime fin    = ym.atEndOfMonth().atTime(23, 59, 59);

        List<Empleado> empleados = empleadoRepository.findByActivoTrue();
        log.info("Empleados activos encontrados: {}", empleados.size());

        return empleados.stream()
                .map(emp -> calcularYPersistirEmpleado(emp, mes, anio, inicio, fin))
                .collect(Collectors.toList());
    }

    // ──────────────────────────────────────────────────────────────────
    // CONSULTAS
    // ──────────────────────────────────────────────────────────────────

    /**
     * Devuelve el dashboard de un periodo: todos los vendedores
     * ordenados por monto total de ventas descendente.
     */
    @Transactional(readOnly = true)
    public List<RendimientoEmpleadoResponse> obtenerDashboard(int mes, int anio) {
        return reporteRepository
                .findByAnioAndMesOrderByMontoTotalVentasDesc(anio, mes)
                .stream()
                .map(r -> mapToDto(r, null))
                .collect(Collectors.toList());
    }

    /**
     * Devuelve el historial de snapshots mensuales de un empleado
     * (los más recientes primero).
     */
    @Transactional(readOnly = true)
    public List<RendimientoEmpleadoResponse> obtenerTendenciaEmpleado(Integer empleadoId) {
        Empleado empleado = empleadoRepository.findById(empleadoId)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado: " + empleadoId));

        return reporteRepository
                .findByEmpleadoIdOrderByAnioDescMesDesc(empleadoId)
                .stream()
                .map(r -> mapToDto(r, empleado))
                .collect(Collectors.toList());
    }

    // ──────────────────────────────────────────────────────────────────
    // CÁLCULO INDIVIDUAL
    // ──────────────────────────────────────────────────────────────────

    private RendimientoEmpleadoResponse calcularYPersistirEmpleado(
            Empleado emp, int mes, int anio,
            LocalDateTime inicio, LocalDateTime fin) {

        Integer empId = emp.getId();

        // ── Ventas ────────────────────────────────────────────────────
        List<Venta> ventas = ventaRepository.findVentasByVendedorAndPeriodo(empId, inicio, fin);
        int totalVentas = ventas.size();
        BigDecimal montoTotal = ventas.stream()
                .map(Venta::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal ticketPromedio = totalVentas > 0
                ? montoTotal.divide(BigDecimal.valueOf(totalVentas), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        // ── Cotizaciones / Conversión ─────────────────────────────────
        List<Cotizacion> cotizaciones = cotizacionRepository.findByVendedorId(empId)
                .stream()
                .filter(c -> {
                    LocalDateTime fc = c.getFechaCotizacion();
                    return fc != null && !fc.isBefore(inicio) && !fc.isAfter(fin);
                })
                .collect(Collectors.toList());
        int totalCotizaciones = cotizaciones.size();
        int convertidas = (int) cotizaciones.stream()
                .filter(c -> "CONVERTIDA".equalsIgnoreCase(c.getEstado()))
                .count();
        BigDecimal tasaConversion = totalCotizaciones > 0
                ? BigDecimal.valueOf(convertidas * 100.0 / totalCotizaciones)
                        .setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        // ── Devoluciones ──────────────────────────────────────────────
        // Devoluciones que corresponden a ventas de este vendedor en el periodo
        List<Integer> ventaIds = ventas.stream().map(Venta::getId).collect(Collectors.toList());
        List<Devolucion> devoluciones = devolucionRepository.findBySucursalAndFecha(null, inicio, fin)
                .stream()
                .filter(d -> ventaIds.contains(d.getVentaId()))
                .collect(Collectors.toList());
        int totalDevoluciones = devoluciones.size();
        BigDecimal montoDevoluciones = devoluciones.stream()
                .map(Devolucion::getTotalDevuelto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal tasaDevolucion = totalVentas > 0
                ? BigDecimal.valueOf(totalDevoluciones * 100.0 / totalVentas)
                        .setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        // ── Hora Pico ─────────────────────────────────────────────────
        Integer horaPico = null;
        if (totalVentas > 0) {
            List<Object[]> horasRows = ventaRepository
                    .findHoraPicoByVendedorAndPeriodo(empId, inicio, fin);
            if (!horasRows.isEmpty()) {
                horaPico = ((Number) horasRows.get(0)[0]).intValue();
            }
        }

        // ── Persistencia (INSERT o UPDATE) ────────────────────────────
        ReporteRendimientoEmpleado reporte = reporteRepository
                .findByEmpleadoIdAndMesAndAnio(empId, mes, anio)
                .orElse(new ReporteRendimientoEmpleado());

        reporte.setEmpleadoId(empId);
        reporte.setMes(mes);
        reporte.setAnio(anio);
        reporte.setTotalVentas(totalVentas);
        reporte.setMontoTotalVentas(montoTotal);
        reporte.setTicketPromedio(ticketPromedio);
        reporte.setTotalCotizaciones(totalCotizaciones);
        reporte.setCotizacionesConvertidas(convertidas);
        reporte.setTasaConversion(tasaConversion);
        reporte.setTotalDevoluciones(totalDevoluciones);
        reporte.setMontoDevoluciones(montoDevoluciones);
        reporte.setTasaDevolucion(tasaDevolucion);
        reporte.setHoraPico(horaPico);

        reporteRepository.save(reporte);

        log.info("Snapshot ANA-04 guardado: empleado={} mes={}/{} ventas={} conversion={}%",
                empId, mes, anio, totalVentas, tasaConversion);

        return mapToDto(reporte, emp);
    }

    // ──────────────────────────────────────────────────────────────────
    // MAPPER  Entity → DTO
    // ──────────────────────────────────────────────────────────────────

    private RendimientoEmpleadoResponse mapToDto(ReporteRendimientoEmpleado r, Empleado emp) {
        RendimientoEmpleadoResponse dto = new RendimientoEmpleadoResponse();
        dto.setEmpleadoId(r.getEmpleadoId());
        dto.setMes(r.getMes());
        dto.setAnio(r.getAnio());
        dto.setTotalVentas(r.getTotalVentas());
        dto.setMontoTotalVentas(r.getMontoTotalVentas());
        dto.setTicketPromedio(r.getTicketPromedio());
        dto.setTotalCotizaciones(r.getTotalCotizaciones());
        dto.setCotizacionesConvertidas(r.getCotizacionesConvertidas());
        dto.setTasaConversion(r.getTasaConversion());
        dto.setTotalDevoluciones(r.getTotalDevoluciones());
        dto.setMontoDevoluciones(r.getMontoDevoluciones());
        dto.setTasaDevolucion(r.getTasaDevolucion());
        dto.setHoraPico(r.getHoraPico());
        dto.setFechaCalculo(r.getFechaCalculo());

        if (emp != null) {
            dto.setNombreEmpleado(emp.getNombre() + " " + emp.getApellidos());
            dto.setPuesto(emp.getPuesto());
        }
        return dto;
    }
}
