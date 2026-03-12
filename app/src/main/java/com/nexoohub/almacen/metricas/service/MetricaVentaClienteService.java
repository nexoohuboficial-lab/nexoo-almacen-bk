package com.nexoohub.almacen.metricas.service;

import com.nexoohub.almacen.catalogo.entity.Cliente;
import com.nexoohub.almacen.catalogo.repository.ClienteRepository;
import com.nexoohub.almacen.empleados.entity.Empleado;
import com.nexoohub.almacen.empleados.repository.EmpleadoRepository;
import com.nexoohub.almacen.metricas.dto.*;
import com.nexoohub.almacen.metricas.entity.MetricaVentaCliente;
import com.nexoohub.almacen.metricas.repository.MetricaVentaClienteRepository;
import com.nexoohub.almacen.sucursal.repository.SucursalRepository;
import com.nexoohub.almacen.ventas.entity.Venta;
import com.nexoohub.almacen.ventas.repository.VentaRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio para cálculo y gestión de métricas de ventas y clientes.
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
@Service
@Transactional
public class MetricaVentaClienteService {

    private static final Logger logger = LoggerFactory.getLogger(MetricaVentaClienteService.class);

    private final MetricaVentaClienteRepository metricaRepository;
    private final VentaRepository ventaRepository;
    private final ClienteRepository clienteRepository;
    private final EmpleadoRepository empleadoRepository;
    private final SucursalRepository sucursalRepository;

    // Porcentaje de comisión por defecto
    private static final BigDecimal COMISION_DEFAULT = new BigDecimal("0.03"); // 3%

    public MetricaVentaClienteService(
            MetricaVentaClienteRepository metricaRepository,
            VentaRepository ventaRepository,
            ClienteRepository clienteRepository,
            EmpleadoRepository empleadoRepository,
            SucursalRepository sucursalRepository) {
        this.metricaRepository = metricaRepository;
        this.ventaRepository = ventaRepository;
        this.clienteRepository = clienteRepository;
        this.empleadoRepository = empleadoRepository;
        this.sucursalRepository = sucursalRepository;
    }

    /**
     * Genera el análisis completo de ventas y clientes para un período.
     */
    public MetricaVentaClienteResponseDTO generarAnalisis(AnalisisVentaClienteRequestDTO request) {
        logger.info("Generando análisis de ventas y clientes: {}", request);

        LocalDateTime inicio = request.getFechaInicio().atStartOfDay();
        LocalDateTime fin = request.getFechaFin().atTime(23, 59, 59);

        // Obtener ventas del período
        List<Venta> ventas;
        if (request.getSucursalId() != null) {
            ventas = ventaRepository.findByFechaVentaBetween(inicio, fin).stream()
                    .filter(v -> v.getSucursalId().equals(request.getSucursalId()))
                    .collect(Collectors.toList());
        } else {
            ventas = ventaRepository.findByFechaVentaBetween(inicio, fin);
        }

        MetricaVentaClienteResponseDTO response = new MetricaVentaClienteResponseDTO();
        response.setPeriodoInicio(request.getFechaInicio());
        response.setPeriodoFin(request.getFechaFin());
        response.setTipoPeriodo(request.getTipoPeriodo());
        response.setSucursalId(request.getSucursalId());
        response.setFechaGeneracion(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        // Nombre de sucursal
        if (request.getSucursalId() != null) {
            sucursalRepository.findById(request.getSucursalId())
                    .ifPresent(s -> response.setNombreSucursal(s.getNombre()));
        } else {
            response.setNombreSucursal("CONSOLIDADO");
        }

        // Calcular métricas
        response.setResumenVentas(calcularResumenVentas(ventas, request.getFechaInicio(), request.getFechaFin()));
        response.setResumenClientes(calcularResumenClientes(ventas, request.getFechaInicio()));
        response.setResumenVendedores(calcularResumenVendedores(ventas));
        response.setMetodosPago(calcularMetodosPago(ventas));

        // Comparación con período anterior
        if (Boolean.TRUE.equals(request.getCompararPeriodoAnterior())) {
            response.setComparacion(calcularComparacion(request, ventas));
        }

        // Detalle de vendedores
        if (Boolean.TRUE.equals(request.getIncluirDetalleVendedores())) {
            response.setTopVendedores(calcularDetalleVendedores(ventas, request));
        }

        // Detalle de clientes
        if (Boolean.TRUE.equals(request.getIncluirDetalleClientes())) {
            response.setTopClientes(calcularDetalleClientes(ventas, request));
        }

        return response;
    }

    /**
     * Calcula el resumen general de ventas.
     */
    private MetricaVentaClienteResponseDTO.ResumenVentasDTO calcularResumenVentas(
            List<Venta> ventas, LocalDate fechaInicio, LocalDate fechaFin) {
        
        BigDecimal totalVentas = ventas.stream()
                .map(Venta::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Integer numeroTransacciones = ventas.size();

        BigDecimal ticketPromedio = BigDecimal.ZERO;
        if (numeroTransacciones > 0) {
            ticketPromedio = totalVentas.divide(
                    new BigDecimal(numeroTransacciones), 2, RoundingMode.HALF_UP);
        }

        long diasPeriodo = ChronoUnit.DAYS.between(fechaInicio, fechaFin) + 1;
        BigDecimal ventaPromedioDia = BigDecimal.ZERO;
        if (diasPeriodo > 0) {
            ventaPromedioDia = totalVentas.divide(
                    new BigDecimal(diasPeriodo), 2, RoundingMode.HALF_UP);
        }

        return new MetricaVentaClienteResponseDTO.ResumenVentasDTO(
                totalVentas, numeroTransacciones, ticketPromedio, 
                ventaPromedioDia, (int) diasPeriodo);
    }

    /**
     * Calcula el resumen de clientes.
     */
    private MetricaVentaClienteResponseDTO.ResumenClientesDTO calcularResumenClientes(
            List<Venta> ventas, LocalDate fechaInicio) {
        
        MetricaVentaClienteResponseDTO.ResumenClientesDTO resumen = 
                new MetricaVentaClienteResponseDTO.ResumenClientesDTO();

        // Clientes únicos en el período
        Set<Integer> clientesActivos = ventas.stream()
                .map(Venta::getClienteId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        resumen.setTotalClientesActivos(clientesActivos.size());

        // Clientes nuevos (registrados en el período)
        int clientesNuevos = 0;
        int clientesRecurrentes = 0;

        for (Integer clienteId : clientesActivos) {
            Optional<Cliente> clienteOpt = clienteRepository.findById(clienteId);
            if (clienteOpt.isPresent()) {
                Cliente cliente = clienteOpt.get();
                LocalDate fechaRegistro = cliente.getFechaCreacion() != null ? 
                        cliente.getFechaCreacion().toLocalDate() : null;
                
                if (fechaRegistro != null && !fechaRegistro.isBefore(fechaInicio)) {
                    clientesNuevos++;
                } else {
                    clientesRecurrentes++;
                }
            }
        }

        resumen.setClientesNuevos(clientesNuevos);
        resumen.setClientesRecurrentes(clientesRecurrentes);
        resumen.setClientesInactivos(0); // Se calcularía con historial completo

        // Valor de vida del cliente
        if (clientesActivos.size() > 0) {
            BigDecimal totalVentas = ventas.stream()
                    .map(Venta::getTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal valorVida = totalVentas.divide(
                    new BigDecimal(clientesActivos.size()), 2, RoundingMode.HALF_UP);
            resumen.setValorVidaCliente(valorVida);
        }

        // Frecuencia de compra
        if (clientesActivos.size() > 0) {
            BigDecimal frecuencia = new BigDecimal(ventas.size()).divide(
                    new BigDecimal(clientesActivos.size()), 2, RoundingMode.HALF_UP);
            resumen.setFrecuenciaCompra(frecuencia);
        }

        // Tasa de retención (simplificada)
        if (clientesActivos.size() > 0 && clientesRecurrentes > 0) {
            BigDecimal tasa = new BigDecimal(clientesRecurrentes)
                    .multiply(new BigDecimal("100"))
                    .divide(new BigDecimal(clientesActivos.size()), 2, RoundingMode.HALF_UP);
            resumen.setTasaRetencion(tasa);
        }

        return resumen;
    }

    /**
     * Calcula el resumen de vendedores.
     */
    private MetricaVentaClienteResponseDTO.ResumenVendedoresDTO calcularResumenVendedores(List<Venta> ventas) {
        MetricaVentaClienteResponseDTO.ResumenVendedoresDTO resumen = 
                new MetricaVentaClienteResponseDTO.ResumenVendedoresDTO();

        // Vendedores únicos
        Set<Integer> vendedores = ventas.stream()
                .map(Venta::getVendedorId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        resumen.setTotalVendedores(vendedores.size());

        // Agrupar ventas por vendedor
        Map<Integer, List<Venta>> ventasPorVendedor = ventas.stream()
                .filter(v -> v.getVendedorId() != null)
                .collect(Collectors.groupingBy(Venta::getVendedorId));

        // Encontrar top vendedor
        Integer topVendedorId = null;
        BigDecimal maxVentas = BigDecimal.ZERO;

        for (Map.Entry<Integer, List<Venta>> entry : ventasPorVendedor.entrySet()) {
            BigDecimal totalVendedor = entry.getValue().stream()
                    .map(Venta::getTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            if (totalVendedor.compareTo(maxVentas) > 0) {
                maxVentas = totalVendedor;
                topVendedorId = entry.getKey();
            }
        }

        if (topVendedorId != null) {
            resumen.setTopVendedorId(topVendedorId);
            resumen.setTopVendedorVentas(maxVentas);
            resumen.setTopVendedorTransacciones(ventasPorVendedor.get(topVendedorId).size());

            empleadoRepository.findById(topVendedorId).ifPresent(emp ->
                    resumen.setTopVendedorNombre(emp.getNombre() + " " + emp.getApellidos()));
        }

        // Venta promedio por vendedor
        if (vendedores.size() > 0) {
            BigDecimal totalVentas = ventas.stream()
                    .map(Venta::getTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal promedioVendedor = totalVentas.divide(
                    new BigDecimal(vendedores.size()), 2, RoundingMode.HALF_UP);
            resumen.setVentaPromedioVendedor(promedioVendedor);
        }

        return resumen;
    }

    /**
     * Calcula la distribución por métodos de pago.
     */
    private MetricaVentaClienteResponseDTO.MetodosPagoDTO calcularMetodosPago(List<Venta> ventas) {
        MetricaVentaClienteResponseDTO.MetodosPagoDTO metodos = 
                new MetricaVentaClienteResponseDTO.MetodosPagoDTO();

        BigDecimal ventasEfectivo = ventas.stream()
                .filter(v -> "EFECTIVO".equalsIgnoreCase(v.getMetodoPago()))
                .map(Venta::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal ventasTarjeta = ventas.stream()
                .filter(v -> v.getMetodoPago() != null && 
                        (v.getMetodoPago().contains("TARJETA") || 
                         v.getMetodoPago().contains("DEBITO")))
                .map(Venta::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal ventasCredito = ventas.stream()
                .filter(v -> "CREDITO".equalsIgnoreCase(v.getMetodoPago()))
                .map(Venta::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        metodos.setVentasEfectivo(ventasEfectivo);
        metodos.setVentasTarjeta(ventasTarjeta);
        metodos.setVentasCredito(ventasCredito);

        BigDecimal totalVentas = ventas.stream()
                .map(Venta::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalVentas.compareTo(BigDecimal.ZERO) > 0) {
            metodos.setPorcentajeEfectivo(ventasEfectivo.multiply(new BigDecimal("100"))
                    .divide(totalVentas, 2, RoundingMode.HALF_UP));
            metodos.setPorcentajeTarjeta(ventasTarjeta.multiply(new BigDecimal("100"))
                    .divide(totalVentas, 2, RoundingMode.HALF_UP));
            metodos.setPorcentajeCredito(ventasCredito.multiply(new BigDecimal("100"))
                    .divide(totalVentas, 2, RoundingMode.HALF_UP));
        }

        return metodos;
    }

    /**
     * Calcula la comparación con el período anterior.
     */
    private MetricaVentaClienteResponseDTO.ComparacionPeriodoDTO calcularComparacion(
            AnalisisVentaClienteRequestDTO request, List<Venta> ventasActuales) {
        
        MetricaVentaClienteResponseDTO.ComparacionPeriodoDTO comparacion = 
                new MetricaVentaClienteResponseDTO.ComparacionPeriodoDTO();

        // Calcular período anterior
        long diasPeriodo = ChronoUnit.DAYS.between(request.getFechaInicio(), request.getFechaFin());
        LocalDate inicioAnterior = request.getFechaInicio().minusDays(diasPeriodo + 1);
        LocalDate finAnterior = request.getFechaInicio().minusDays(1);

        LocalDateTime inicioAnteriorDT = inicioAnterior.atStartOfDay();
        LocalDateTime finAnteriorDT = finAnterior.atTime(23, 59, 59);

        List<Venta> ventasAnteriores = ventaRepository.findByFechaVentaBetween(inicioAnteriorDT, finAnteriorDT);

        if (request.getSucursalId() != null) {
            Integer sucursalId = request.getSucursalId();
            ventasAnteriores = ventasAnteriores.stream()
                    .filter(v -> v.getSucursalId().equals(sucursalId))
                    .collect(Collectors.toList());
        }

        BigDecimal totalAnterior = ventasAnteriores.stream()
                .map(Venta::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalActual = ventasActuales.stream()
                .map(Venta::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        comparacion.setVentasAnterior(totalAnterior);
        comparacion.setVentasActual(totalActual);
        comparacion.setTransaccionesAnterior(ventasAnteriores.size());
        comparacion.setTransaccionesActual(ventasActuales.size());

        // Cálculo de crecimiento
        if (totalAnterior.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal diferencia = totalActual.subtract(totalAnterior);
            BigDecimal crecimiento = diferencia.multiply(new BigDecimal("100"))
                    .divide(totalAnterior, 2, RoundingMode.HALF_UP);
            comparacion.setCrecimiento(crecimiento);

            if (crecimiento.compareTo(new BigDecimal("5")) >= 0) {
                comparacion.setTendencia("CRECIMIENTO");
            } else if (crecimiento.compareTo(new BigDecimal("-5")) <= 0) {
                comparacion.setTendencia("DECRECIMIENTO");
            } else {
                comparacion.setTendencia("ESTABLE");
            }
        } else {
            comparacion.setTendencia("CRECIMIENTO");
        }

        return comparacion;
    }

    /**
     * Calcula el detalle de vendedores.
     */
    private List<DetalleVendedorDTO> calcularDetalleVendedores(
            List<Venta> ventas, AnalisisVentaClienteRequestDTO request) {
        
        Map<Integer, List<Venta>> ventasPorVendedor = ventas.stream()
                .filter(v -> v.getVendedorId() != null)
                .collect(Collectors.groupingBy(Venta::getVendedorId));

        BigDecimal totalVentasGeneral = ventas.stream()
                .map(Venta::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<DetalleVendedorDTO> detalles = new ArrayList<>();

        for (Map.Entry<Integer, List<Venta>> entry : ventasPorVendedor.entrySet()) {
            Integer vendedorId = entry.getKey();
            List<Venta> ventasVendedor = entry.getValue();

            Optional<Empleado> empleadoOpt = empleadoRepository.findById(vendedorId);
            if (empleadoOpt.isEmpty()) continue;

            Empleado empleado = empleadoOpt.get();
            DetalleVendedorDTO detalle = new DetalleVendedorDTO(
                    vendedorId, empleado.getNombre(), empleado.getApellidos());

            detalle.setPuesto(empleado.getPuesto());
            detalle.setSucursalId(empleado.getSucursalId());

            // Sucursal
            if (empleado.getSucursalId() != null) {
                sucursalRepository.findById(empleado.getSucursalId())
                        .ifPresent(s -> detalle.setNombreSucursal(s.getNombre()));
            }

            // Métricas de ventas
            BigDecimal totalVentas = ventasVendedor.stream()
                    .map(Venta::getTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            detalle.setTotalVentas(totalVentas);
            detalle.setNumeroTransacciones(ventasVendedor.size());

            BigDecimal ticketPromedio = totalVentas.divide(
                    new BigDecimal(ventasVendedor.size()), 2, RoundingMode.HALF_UP);
            detalle.setTicketPromedio(ticketPromedio);

            // Porcentaje del total
            if (totalVentasGeneral.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal porcentaje = totalVentas.multiply(new BigDecimal("100"))
                        .divide(totalVentasGeneral, 2, RoundingMode.HALF_UP);
                detalle.setPorcentajeVentasTotal(porcentaje);
            }

            // Clientes atendidos
            Set<Integer> clientesAtendidos = ventasVendedor.stream()
                    .map(Venta::getClienteId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            detalle.setClientesAtendidos(clientesAtendidos.size());

            // Comisión estimada
            BigDecimal comision = totalVentas.multiply(COMISION_DEFAULT)
                    .setScale(2, RoundingMode.HALF_UP);
            detalle.setComisionEstimada(comision);
            detalle.setPorcentajeComision(COMISION_DEFAULT.multiply(new BigDecimal("100")));

            detalles.add(detalle);
        }

        // Ordenar por total de ventas y asignar ranking y clasificación
        detalles.sort(Comparator.comparing(DetalleVendedorDTO::getTotalVentas).reversed());

        BigDecimal promedioVentas = BigDecimal.ZERO;
        if (!detalles.isEmpty()) {
            BigDecimal sumaVentas = detalles.stream()
                    .map(DetalleVendedorDTO::getTotalVentas)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            promedioVentas = sumaVentas.divide(new BigDecimal(detalles.size()), 2, RoundingMode.HALF_UP);
        }

        for (int i = 0; i < detalles.size(); i++) {
            DetalleVendedorDTO detalle = detalles.get(i);
            detalle.setRanking(i + 1);

            // Clasificación
            BigDecimal ventasVendedor = detalle.getTotalVentas();
            if (ventasVendedor.compareTo(promedioVentas.multiply(new BigDecimal("1.5"))) >= 0) {
                detalle.setClasificacion("TOP");
            } else if (ventasVendedor.compareTo(promedioVentas) >= 0) {
                detalle.setClasificacion("BUENO");
            } else if (ventasVendedor.compareTo(promedioVentas.multiply(new BigDecimal("0.7"))) >= 0) {
                detalle.setClasificacion("REGULAR");
            } else {
                detalle.setClasificacion("BAJO");
            }
        }

        // Limitar al top N
        return detalles.stream()
                .limit(request.getLimitTopVendedores())
                .collect(Collectors.toList());
    }

    /**
     * Calcula el detalle de clientes.
     */
    private List<DetalleClienteDTO> calcularDetalleClientes(
            List<Venta> ventas, AnalisisVentaClienteRequestDTO request) {
        
        Map<Integer, List<Venta>> ventasPorCliente = ventas.stream()
                .filter(v -> v.getClienteId() != null)
                .collect(Collectors.groupingBy(Venta::getClienteId));

        BigDecimal totalVentasGeneral = ventas.stream()
                .map(Venta::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<DetalleClienteDTO> detalles = new ArrayList<>();

        for (Map.Entry<Integer, List<Venta>> entry : ventasPorCliente.entrySet()) {
            Integer clienteId = entry.getKey();
            List<Venta> ventasCliente = entry.getValue();

            Optional<Cliente> clienteOpt = clienteRepository.findById(clienteId);
            if (clienteOpt.isEmpty()) continue;

            Cliente cliente = clienteOpt.get();
            DetalleClienteDTO detalle = new DetalleClienteDTO(clienteId, cliente.getNombre());

            detalle.setRfc(cliente.getRfc());
            detalle.setTelefono(cliente.getTelefono());
            detalle.setEmail(cliente.getEmail());
            detalle.setBloqueado(cliente.getBloqueado());
            detalle.setSaldoPendiente(cliente.getSaldoPendiente());

            // Estado
            if (Boolean.TRUE.equals(cliente.getBloqueado())) {
                detalle.setEstadoCliente("BLOQUEADO");
            } else if (cliente.getSaldoPendiente() != null && 
                       cliente.getSaldoPendiente().compareTo(BigDecimal.ZERO) > 0) {
                detalle.setEstadoCliente("CON_DEUDA");
            } else {
                detalle.setEstadoCliente("ACTIVO");
            }

            // Fecha de registro
            if (cliente.getFechaCreacion() != null) {
                detalle.setFechaRegistro(cliente.getFechaCreacion().toLocalDate());
            }

            // Métricas de compras
            BigDecimal totalCompras = ventasCliente.stream()
                    .map(Venta::getTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            detalle.setTotalCompras(totalCompras);
            detalle.setNumeroCompras(ventasCliente.size());

            BigDecimal ticketPromedio = totalCompras.divide(
                    new BigDecimal(ventasCliente.size()), 2, RoundingMode.HALF_UP);
            detalle.setTicketPromedio(ticketPromedio);

            // Porcentaje del total
            if (totalVentasGeneral.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal porcentaje = totalCompras.multiply(new BigDecimal("100"))
                        .divide(totalVentasGeneral, 2, RoundingMode.HALF_UP);
                detalle.setPorcentajeTotalVentas(porcentaje);
            }

            // Fecha última compra
            Optional<Venta> ultimaVenta = ventasCliente.stream()
                    .max(Comparator.comparing(Venta::getFechaVenta));
            ultimaVenta.ifPresent(v -> {
                detalle.setFechaUltimaCompra(v.getFechaVenta().toLocalDate());
                long diasDesde = ChronoUnit.DAYS.between(v.getFechaVenta().toLocalDate(), LocalDate.now());
                detalle.setDiasDesdeUltimaCompra((int) diasDesde);
            });

            // Valor de vida (en este período)
            detalle.setValorVidaPeriodo(totalCompras);

            // Segmentación
            if (ventasCliente.size() >= 10) {
                detalle.setSegmento("VIP");
            } else if (ventasCliente.size() >= 3) {
                detalle.setSegmento("FRECUENTE");
            } else if (ventasCliente.size() == 2) {
                detalle.setSegmento("REGULAR");
            } else if (detalle.getFechaRegistro() != null && 
                       !detalle.getFechaRegistro().isBefore(request.getFechaInicio())) {
                detalle.setSegmento("NUEVO");
            } else {
                detalle.setSegmento("OCASIONAL");
            }

            // Método de pago preferido
            Map<String, Long> metodosPago = ventasCliente.stream()
                    .filter(v -> v.getMetodoPago() != null)
                    .collect(Collectors.groupingBy(Venta::getMetodoPago, Collectors.counting()));
            
            if (!metodosPago.isEmpty()) {
                String metodoPref = Collections.max(metodosPago.entrySet(), 
                        Map.Entry.comparingByValue()).getKey();
                detalle.setMetodoPagoPreferido(metodoPref);
            }

            detalles.add(detalle);
        }

        // Ordenar por total de compras y asignar ranking
        detalles.sort(Comparator.comparing(DetalleClienteDTO::getTotalCompras).reversed());

        for (int i = 0; i < detalles.size(); i++) {
            detalles.get(i).setRanking(i + 1);
        }

        // Limitar al top N
        return detalles.stream()
                .limit(request.getLimitTopClientes())
                .collect(Collectors.toList());
    }

    /**
     * Guarda una métrica calculada.
     */
    public MetricaVentaCliente guardarMetrica(MetricaVentaCliente metrica) {
        logger.info("Guardando métrica de ventas y clientes: período {} a {}", 
                metrica.getPeriodoInicio(), metrica.getPeriodoFin());
        return metricaRepository.save(metrica);
    }

    /**
     * Obtiene la métrica consolidada de un período.
     */
    public Optional<MetricaVentaCliente> obtenerMetricaConsolidada(LocalDate fechaInicio, LocalDate fechaFin) {
        return metricaRepository.findMetricaConsolidada(fechaInicio, fechaFin);
    }

    /**
     * Obtiene el historial de métricas consolidadas.
     */
    public List<MetricaVentaCliente> obtenerHistorialConsolidado(String tipoPeriodo, LocalDate fechaHasta) {
        return metricaRepository.findHistorialConsolidado(tipoPeriodo, fechaHasta);
    }
}
