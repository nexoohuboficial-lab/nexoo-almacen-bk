package com.nexoohub.almacen.rentabilidad.service;

import com.nexoohub.almacen.common.exception.BusinessException;
import com.nexoohub.almacen.common.exception.ResourceNotFoundException;
import com.nexoohub.almacen.inventario.entity.InventarioSucursal;
import com.nexoohub.almacen.inventario.repository.InventarioSucursalRepository;
import com.nexoohub.almacen.rentabilidad.dto.*;
import com.nexoohub.almacen.rentabilidad.entity.RentabilidadProducto;
import com.nexoohub.almacen.rentabilidad.entity.RentabilidadVenta;
import com.nexoohub.almacen.rentabilidad.repository.RentabilidadProductoRepository;
import com.nexoohub.almacen.rentabilidad.repository.RentabilidadVentaRepository;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Servicio para análisis de rentabilidad de ventas y productos.
 * 
 * <p>Resuelve la pregunta clave: <strong>¿Cuánto GANAS realmente?</strong></p>
 * 
 * <p>Funcionalidades principales:</p>
 * <ul>
 *   <li>Calcular rentabilidad de ventas individuales</li>
 *   <li>Analizar rentabilidad por producto en períodos</li>
 *   <li>Identificar ventas bajo costo</li>
 *   <li>Generar estadísticas y alertas</li>
 * </ul>
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
@Service
public class RentabilidadService {

    private final RentabilidadVentaRepository rentabilidadVentaRepository;
    private final RentabilidadProductoRepository rentabilidadProductoRepository;
    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final InventarioSucursalRepository inventarioRepository;

    // Umbrales de clasificación de margen
    private static final BigDecimal MARGEN_EXCELENTE = new BigDecimal("30.00");
    private static final BigDecimal MARGEN_BUENO = new BigDecimal("20.00");
    private static final BigDecimal MARGEN_REGULAR = new BigDecimal("10.00");
    private static final BigDecimal MARGEN_BAJO = new BigDecimal("5.00");

    public RentabilidadService(
            RentabilidadVentaRepository rentabilidadVentaRepository,
            RentabilidadProductoRepository rentabilidadProductoRepository,
            VentaRepository ventaRepository,
            DetalleVentaRepository detalleVentaRepository,
            InventarioSucursalRepository inventarioRepository) {
        this.rentabilidadVentaRepository = rentabilidadVentaRepository;
        this.rentabilidadProductoRepository = rentabilidadProductoRepository;
        this.ventaRepository = ventaRepository;
        this.detalleVentaRepository = detalleVentaRepository;
        this.inventarioRepository = inventarioRepository;
    }

    /**
     * Calcula y registra la rentabilidad de una venta específica.
     * 
     * @param ventaId ID de la venta a analizar
     * @return Análisis de rentabilidad generado
     * @throws ResourceNotFoundException si la venta no existe
     */
    @Transactional
    public RentabilidadVentaResponseDTO calcularRentabilidadVenta(Integer ventaId) {
        // Validar que la venta existe
        Venta venta = ventaRepository.findById(ventaId)
                .orElseThrow(() -> new ResourceNotFoundException("Venta", ventaId));

        // Validar que no exista ya un análisis previo
        if (rentabilidadVentaRepository.findByVentaId(ventaId).isPresent()) {
            throw new BusinessException("Ya existe un análisis de rentabilidad para la venta " + ventaId);
        }

        // Obtener detalles de la venta
        List<DetalleVenta> detalles = detalleVentaRepository.findByVentaId(ventaId);
        if (detalles.isEmpty()) {
            throw new BusinessException("La venta " + ventaId + " no tiene detalles registrados");
        }

        // Calcular costo total basado en el Costo Promedio Ponderado
        BigDecimal costoTotal = BigDecimal.ZERO;
        for (DetalleVenta detalle : detalles) {
            InventarioSucursal inventario = inventarioRepository
                    .findByIdSkuInternoAndIdSucursalId(detalle.getSkuInterno(), venta.getSucursalId())
                    .orElse(null);

            if (inventario == null) {
                throw new BusinessException(
                    "No se encontró inventario para SKU " + detalle.getSkuInterno() + 
                    " en sucursal " + venta.getSucursalId()
                );
            }

            BigDecimal costoPorUnidad = inventario.getCostoPromedioPonderado();
            BigDecimal costoDetalle = costoPorUnidad.multiply(new BigDecimal(detalle.getCantidad()));
            costoTotal = costoTotal.add(costoDetalle);
        }

        // Calcular métricas
        BigDecimal precioVenta = venta.getTotal();
        BigDecimal utilidadBruta = precioVenta.subtract(costoTotal);
        BigDecimal margenPorcentaje = (precioVenta.compareTo(BigDecimal.ZERO) > 0)
                ? utilidadBruta.divide(precioVenta, 4, RoundingMode.HALF_UP)
                        .multiply(new BigDecimal("100"))
                        .setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        Boolean ventaBajoCosto = utilidadBruta.compareTo(BigDecimal.ZERO) < 0;

        // Crear entidad de rentabilidad
        RentabilidadVenta rentabilidad = new RentabilidadVenta();
        rentabilidad.setVentaId(ventaId);
        rentabilidad.setCostoTotal(costoTotal);
        rentabilidad.setPrecioVentaTotal(precioVenta);
        rentabilidad.setUtilidadBruta(utilidadBruta);
        rentabilidad.setMargenPorcentaje(margenPorcentaje);
        rentabilidad.setVentaBajoCosto(ventaBajoCosto);
        rentabilidad.setCantidadItems(detalles.size());

        RentabilidadVenta guardada = rentabilidadVentaRepository.save(rentabilidad);

        // Construir respuesta
        return construirRentabilidadVentaResponseDTO(guardada, venta);
    }

    /**
     * Obtiene el análisis de rentabilidad de una venta.
     * 
     * @param ventaId ID de la venta
     * @return Análisis de rentabilidad
     * @throws ResourceNotFoundException si no existe análisis para esa venta
     */
    @Transactional(readOnly = true)
    public RentabilidadVentaResponseDTO consultarPorVenta(Integer ventaId) {
        RentabilidadVenta rentabilidad = rentabilidadVentaRepository.findByVentaId(ventaId)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "No existe análisis de rentabilidad para la venta " + ventaId
                ));

        Venta venta = ventaRepository.findById(ventaId)
                .orElseThrow(() -> new ResourceNotFoundException("Venta", ventaId));

        return construirRentabilidadVentaResponseDTO(rentabilidad, venta);
    }

    /**
     * Genera análisis agregado de rentabilidad por producto en un período.
     * 
     * @param request Objeto con fechaInicio y fechaFin
     * @return Lista de análisis por producto
     */
    @Transactional
    public List<RentabilidadProductoResponseDTO> generarAnalisisPorProducto(
            AnalisisRentabilidadRequestDTO request) {

        LocalDate fechaInicio = request.getFechaInicio();
        LocalDate fechaFin = request.getFechaFin();

        // Validar período
        if (fechaFin.isBefore(fechaInicio)) {
            throw new BusinessException("La fecha de fin no puede ser anterior a la fecha de inicio");
        }

        // Eliminar análisis previo del mismo período para regenerarlo
        rentabilidadProductoRepository.deleteByPeriodoInicioAndPeriodoFin(fechaInicio, fechaFin);

        // Obtener ventas del período
        LocalDateTime inicioDateTime = fechaInicio.atStartOfDay();
        LocalDateTime finDateTime = fechaFin.atTime(23, 59, 59);
        List<Venta> ventasPeriodo = ventaRepository.findByFechaVentaBetween(inicioDateTime, finDateTime);

        if (ventasPeriodo.isEmpty()) {
            throw new BusinessException("No existen ventas en el período especificado");
        }

        // Agrupar detalles por SKU
        List<Integer> idsVentas = ventasPeriodo.stream()
                .map(Venta::getId)
                .collect(Collectors.toList());

        List<DetalleVenta> todosDetalles = detalleVentaRepository.findByVentaIdIn(idsVentas);

        Map<String, List<DetalleVenta>> detallesPorSku = todosDetalles.stream()
                .collect(Collectors.groupingBy(DetalleVenta::getSkuInterno));

        // Generar análisis por cada producto
        List<RentabilidadProducto> analisisGenerados = new ArrayList<>();
        for (Map.Entry<String, List<DetalleVenta>> entry : detallesPorSku.entrySet()) {
            String skuInterno = entry.getKey();
            List<DetalleVenta> detalles = entry.getValue();

            // Calcular métricas agregadas
            int cantidadTotal = detalles.stream()
                    .mapToInt(DetalleVenta::getCantidad)
                    .sum();

            BigDecimal sumaPreciosVenta = detalles.stream()
                    .map(d -> d.getPrecioUnitarioVenta().multiply(new BigDecimal(d.getCantidad())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal precioPromedioVenta = sumaPreciosVenta
                    .divide(new BigDecimal(cantidadTotal), 2, RoundingMode.HALF_UP);

            // Obtener costo promedio del inventario (usando primera sucursal encontrada)
            BigDecimal costoPromedioUnitario = obtenerCostoPromedioProducto(skuInterno);

            BigDecimal utilidadPorUnidad = precioPromedioVenta.subtract(costoPromedioUnitario);
            BigDecimal utilidadTotal = utilidadPorUnidad.multiply(new BigDecimal(cantidadTotal));

            BigDecimal margenPromedio = (precioPromedioVenta.compareTo(BigDecimal.ZERO) > 0)
                    ? utilidadPorUnidad.divide(precioPromedioVenta, 4, RoundingMode.HALF_UP)
                            .multiply(new BigDecimal("100"))
                            .setScale(2, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;

            int numeroVentas = (int) detalles.stream()
                    .map(DetalleVenta::getVentaId)
                    .distinct()
                    .count();

            // Crear entidad
            RentabilidadProducto analisis = new RentabilidadProducto();
            analisis.setSkuInterno(skuInterno);
            analisis.setPeriodoInicio(fechaInicio);
            analisis.setPeriodoFin(fechaFin);
            analisis.setCantidadVendida(cantidadTotal);
            analisis.setCostoPromedioUnitario(costoPromedioUnitario);
            analisis.setPrecioPromedioVenta(precioPromedioVenta);
            analisis.setUtilidadTotalGenerada(utilidadTotal);
            analisis.setMargenPromedioPorcentaje(margenPromedio);
            analisis.setNumeroVentas(numeroVentas);

            analisisGenerados.add(rentabilidadProductoRepository.save(analisis));
        }

        // Construir respuesta
        return analisisGenerados.stream()
                .map(this::construirRentabilidadProductoResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene los N productos más rentables en un período.
     * 
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @param limite Número máximo de resultados
     * @return Lista de productos más rentables
     */
    @Transactional(readOnly = true)
    public List<RentabilidadProductoResponseDTO> obtenerProductosMasRentables(
            LocalDate fechaInicio, LocalDate fechaFin, int limite) {
        
        List<RentabilidadProducto> productos = rentabilidadProductoRepository
                .obtenerProductosMasRentables(fechaInicio, fechaFin, limite);

        return productos.stream()
                .limit(limite)
                .map(this::construirRentabilidadProductoResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene los N productos menos rentables en un período.
     * 
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @param limite Número máximo de resultados
     * @return Lista de productos menos rentables
     */
    @Transactional(readOnly = true)
    public List<RentabilidadProductoResponseDTO> obtenerProductosMenosRentables(
            LocalDate fechaInicio, LocalDate fechaFin, int limite) {
        
        List<RentabilidadProducto> productos = rentabilidadProductoRepository
                .obtenerProductosMenosRentables(fechaInicio, fechaFin, limite);

        return productos.stream()
                .limit(limite)
                .map(this::construirRentabilidadProductoResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todas las ventas realizadas bajo costo (con pérdida).
     * 
     * @return Lista de ventas con pérdida
     */
    @Transactional(readOnly = true)
    public List<RentabilidadVentaResponseDTO> obtenerVentasBajoCosto() {
        List<RentabilidadVenta> ventas = rentabilidadVentaRepository.findByVentaBajoCostoTrue();
        
        return ventas.stream()
                .map(rv -> {
                    Venta venta = ventaRepository.findById(rv.getVentaId()).orElse(null);
                    return construirRentabilidadVentaResponseDTO(rv, venta);
                })
                .collect(Collectors.toList());
    }

    /**
     * Calcula estadísticas generales de rentabilidad en un período.
     * 
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @return Estadísticas agregadas
     */
    @Transactional(readOnly = true)
    public EstadisticasRentabilidadDTO obtenerEstadisticas(LocalDate fechaInicio, LocalDate fechaFin) {
        LocalDateTime inicioDateTime = fechaInicio.atStartOfDay();
        LocalDateTime finDateTime = fechaFin.atTime(23, 59, 59);

        BigDecimal utilidadTotal = rentabilidadVentaRepository
                .calcularUtilidadTotalPeriodo(inicioDateTime, finDateTime);

        BigDecimal margenPromedio = rentabilidadVentaRepository
                .calcularMargenPromedioPeriodo(inicioDateTime, finDateTime);

        Long totalVentas = ventaRepository.countByFechaVentaBetween(inicioDateTime, finDateTime);

        Long ventasBajoCosto = rentabilidadVentaRepository
                .contarVentasBajoCostoPeriodo(inicioDateTime, finDateTime);

        BigDecimal porcentajeBajoCosto = (totalVentas > 0)
                ? new BigDecimal(ventasBajoCosto)
                        .divide(new BigDecimal(totalVentas), 4, RoundingMode.HALF_UP)
                        .multiply(new BigDecimal("100"))
                        .setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        // Obtener extremos
        List<RentabilidadVenta> masRentables = rentabilidadVentaRepository.obtenerVentasMasRentables(1);
        List<RentabilidadVenta> menosRentables = rentabilidadVentaRepository.obtenerVentasMenosRentables(1);

        BigDecimal ventaMasRentable = !masRentables.isEmpty() 
                ? masRentables.get(0).getUtilidadBruta() 
                : BigDecimal.ZERO;
        BigDecimal ventaMenosRentable = !menosRentables.isEmpty() 
                ? menosRentables.get(0).getUtilidadBruta() 
                : BigDecimal.ZERO;

        // Obtener productos extremos
        List<RentabilidadProducto> prodMasRentables = rentabilidadProductoRepository
                .obtenerProductosMasRentables(fechaInicio, fechaFin, 1);
        List<RentabilidadProducto> prodMenosRentables = rentabilidadProductoRepository
                .obtenerProductosMenosRentables(fechaInicio, fechaFin, 1);

        String productoMasRentable = !prodMasRentables.isEmpty()
                ? prodMasRentables.get(0).getSkuInterno()
                : "N/A";
        String productoMenosRentable = !prodMenosRentables.isEmpty()
                ? prodMenosRentables.get(0).getSkuInterno()
                : "N/A";

        return new EstadisticasRentabilidadDTO(
            utilidadTotal, margenPromedio, totalVentas, ventasBajoCosto,
            porcentajeBajoCosto, ventaMasRentable, ventaMenosRentable,
            productoMasRentable, productoMenosRentable
        );
    }

    // ==========================================
    // MÉTODOS PRIVADOS DE UTILIDAD
    // ==========================================

    private BigDecimal obtenerCostoPromedioProducto(String skuInterno) {
        List<InventarioSucursal> inventarios = inventarioRepository.findByIdSkuInterno(skuInterno);
        
        if (inventarios.isEmpty()) {
            return BigDecimal.ZERO;
        }

        // Usar el costo promedio ponderado de la primera sucursal encontrada
        // (podría mejorarse calculando un promedio ponderado entre todas las sucursales)
        return inventarios.get(0).getCostoPromedioPonderado();
    }

    private RentabilidadVentaResponseDTO construirRentabilidadVentaResponseDTO(
            RentabilidadVenta rentabilidad, Venta venta) {
        
        String alerta = clasificarCalidadVenta(rentabilidad.getMargenPorcentaje());
        String clienteNombre = (venta != null && venta.getCliente() != null) 
                ? venta.getCliente().getNombre() 
                : null;
        String sucursalNombre = (venta != null && venta.getSucursal() != null) 
                ? venta.getSucursal().getNombre() 
                : null;
        LocalDateTime fechaVenta = (venta != null) ? venta.getFechaVenta() : null;

        return new RentabilidadVentaResponseDTO(
            rentabilidad.getId(),
            rentabilidad.getVentaId(),
            fechaVenta,
            clienteNombre,
            sucursalNombre,
            rentabilidad.getCostoTotal(),
            rentabilidad.getPrecioVentaTotal(),
            rentabilidad.getUtilidadBruta(),
            rentabilidad.getMargenPorcentaje(),
            rentabilidad.getVentaBajoCosto(),
            rentabilidad.getCantidadItems(),
            alerta
        );
    }

    private RentabilidadProductoResponseDTO construirRentabilidadProductoResponseDTO(
            RentabilidadProducto analisis) {
        
        String nombreComercial = (analisis.getProducto() != null) 
                ? analisis.getProducto().getNombreComercial() 
                : null;
        String marca = (analisis.getProducto() != null) 
                ? analisis.getProducto().getMarca() 
                : null;

        BigDecimal utilidadPorUnidad = (analisis.getCantidadVendida() != null && analisis.getCantidadVendida() > 0)
                ? analisis.getUtilidadTotalGenerada()
                        .divide(new BigDecimal(analisis.getCantidadVendida()), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        String clasificacion = clasificarRentabilidadProducto(analisis.getMargenPromedioPorcentaje());

        return new RentabilidadProductoResponseDTO(
            analisis.getId(),
            analisis.getSkuInterno(),
            nombreComercial,
            marca,
            analisis.getPeriodoInicio(),
            analisis.getPeriodoFin(),
            analisis.getCantidadVendida(),
            analisis.getCostoPromedioUnitario(),
            analisis.getPrecioPromedioVenta(),
            analisis.getUtilidadTotalGenerada(),
            utilidadPorUnidad,
            analisis.getMargenPromedioPorcentaje(),
            analisis.getNumeroVentas(),
            clasificacion
        );
    }

    private String clasificarCalidadVenta(BigDecimal margen) {
        if (margen.compareTo(MARGEN_EXCELENTE) >= 0) return "EXCELENTE";
        if (margen.compareTo(MARGEN_BUENO) >= 0) return "BUENA";
        if (margen.compareTo(MARGEN_REGULAR) >= 0) return "REGULAR";
        if (margen.compareTo(MARGEN_BAJO) >= 0) return "BAJA";
        return "PERDIDA";
    }

    private String clasificarRentabilidadProducto(BigDecimal margen) {
        if (margen.compareTo(MARGEN_EXCELENTE) >= 0) return "MUY_RENTABLE";
        if (margen.compareTo(MARGEN_BUENO) >= 0) return "RENTABLE";
        if (margen.compareTo(MARGEN_BAJO) >= 0) return "POCO_RENTABLE";
        return "NO_RENTABLE";
    }
}
