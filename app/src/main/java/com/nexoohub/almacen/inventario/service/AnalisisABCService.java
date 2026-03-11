package com.nexoohub.almacen.inventario.service;

import com.nexoohub.almacen.common.exception.BusinessException;
import com.nexoohub.almacen.common.exception.ResourceNotFoundException;
import com.nexoohub.almacen.inventario.dto.AnalisisABCRequestDTO;
import com.nexoohub.almacen.inventario.dto.AnalisisABCResumenDTO;
import com.nexoohub.almacen.inventario.dto.AnalisisABCResponseDTO;
import com.nexoohub.almacen.inventario.entity.AnalisisABC;
import com.nexoohub.almacen.inventario.entity.InventarioSucursal;
import com.nexoohub.almacen.inventario.entity.ProductoMaestro;
import com.nexoohub.almacen.inventario.mapper.AnalisisABCMapper;
import com.nexoohub.almacen.inventario.repository.AnalisisABCRepository;
import com.nexoohub.almacen.inventario.repository.InventarioSucursalRepository;
import com.nexoohub.almacen.inventario.repository.ProductoMaestroRepository;
import com.nexoohub.almacen.sucursal.entity.Sucursal;
import com.nexoohub.almacen.sucursal.repository.SucursalRepository;
import com.nexoohub.almacen.ventas.entity.DetalleVenta;
import com.nexoohub.almacen.ventas.entity.Venta;
import com.nexoohub.almacen.ventas.repository.VentaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio para análisis ABC de inventario.
 * 
 * <p>Implementa el análisis ABC (Principio de Pareto 80/20) para clasificar
 * productos según su valor en ventas.</p>
 * 
 * @author NexooHub Development Team
 * @version 1.0
 */
@Slf4j
@Service
public class AnalisisABCService {

    @Autowired
    private AnalisisABCRepository analisisRepository;

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private InventarioSucursalRepository inventarioRepository;

    @Autowired
    private ProductoMaestroRepository productoRepository;

    @Autowired
    private SucursalRepository sucursalRepository;

    @Autowired
    private AnalisisABCMapper mapper;

    /**
     * Genera análisis ABC para una sucursal o todas las sucursales.
     * 
     * @param request Parámetros del análisis
     * @return Lista de productos analizados
     */
    @Transactional
    public List<AnalisisABCResponseDTO> generarAnalisisABC(AnalisisABCRequestDTO request) {
        log.info("Iniciando análisis ABC para periodo {}-{}", 
                request.getPeriodoInicio(), request.getPeriodoFin());

        validarPeriodo(request);

        List<Integer> sucursales = determinarSucursales(request.getSucursalId());
        List<AnalisisABC> todosAnalisis = new ArrayList<>();

        for (Integer sucursalId : sucursales) {
            if (!request.getForzarRegeneracion() && 
                analisisRepository.existsBySucursalIdAndFechaAnalisis(sucursalId, LocalDate.now())) {
                log.info("Ya existe análisis para sucursal {} en fecha actual, obteniendo existente", sucursalId);
                List<AnalisisABC> existentes = analisisRepository
                        .findBySucursalIdAndFechaAnalisis(sucursalId, LocalDate.now());
                todosAnalisis.addAll(existentes);
                continue;
            }

            List<AnalisisABC> analisisSucursal = analizarSucursal(
                    sucursalId,
                    request.getPeriodoInicio(),
                    request.getPeriodoFin(),
                    request.getPorcentajeA(),
                    request.getPorcentajeB()
            );
            todosAnalisis.addAll(analisisSucursal);
        }

        // Convertir a DTOs con información enriquecida
        return todosAnalisis.stream()
                .map(a -> {
                    ProductoMaestro producto = productoRepository.findById(a.getSkuProducto()).orElse(null);
                    Sucursal sucursal = sucursalRepository.findById(a.getSucursalId()).orElse(null);
                    return mapper.toDTO(a, producto, sucursal);
                })
                .collect(Collectors.toList());
    }

    /**
     * Analiza una sucursal específica.
     */
    private List<AnalisisABC> analizarSucursal(
            Integer sucursalId,
            LocalDate periodoInicio,
            LocalDate periodoFin,
            Double porcentajeA,
            Double porcentajeB) {

        log.info("Analizando sucursal {}", sucursalId);

        // 1. Obtener ventas del periodo
        LocalDateTime inicio = periodoInicio.atStartOfDay();
        LocalDateTime fin = periodoFin.atTime(23, 59, 59);
        List<Venta> ventas = ventaRepository.findByFechaRangoConDetalles(inicio, fin);

        // 2. Calcular valor de ventas por producto
        Map<String, DatosProducto> datosProductos = calcularVentasPorProducto(ventas, sucursalId);

        if (datosProductos.isEmpty()) {
            log.warn("No hay ventas en el periodo para sucursal {}", sucursalId);
            return Collections.emptyList();
        }

        // 3. Calcular valor total
        BigDecimal valorTotal = datosProductos.values().stream()
                .map(DatosProducto::getValorVentas)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 4. Ordenar por valor (descendente) y calcular porcentajes
        List<DatosProducto> productosOrdenados = datosProductos.values().stream()
                .sorted((a, b) -> b.getValorVentas().compareTo(a.getValorVentas()))
                .collect(Collectors.toList());

        BigDecimal acumulado = BigDecimal.ZERO;
        for (DatosProducto producto : productosOrdenados) {
            producto.setPorcentajeValor(
                    producto.getValorVentas()
                            .multiply(new BigDecimal("100"))
                            .divide(valorTotal, 2, RoundingMode.HALF_UP)
            );
            acumulado = acumulado.add(producto.getPorcentajeValor());
            producto.setPorcentajeAcumulado(acumulado);
        }

        // 5. Clasificar productos
        List<AnalisisABC> analisis = clasificarProductos(
                productosOrdenados,
                sucursalId,
                periodoInicio,
                periodoFin,
                porcentajeA,
                porcentajeB
        );

        // 6. Guardar resultados
        List<AnalisisABC> guardados = analisisRepository.saveAll(analisis);
        log.info("Análisis ABC completado para sucursal {}: {} productos analizados", 
                sucursalId, guardados.size());

        return guardados;
    }

    /**
     * Calcula ventas por producto.
     */
    private Map<String, DatosProducto> calcularVentasPorProducto(List<Venta> ventas, Integer sucursalId) {
        Map<String, DatosProducto> datosProductos = new HashMap<>();

        for (Venta venta : ventas) {
            if (!venta.getSucursalId().equals(sucursalId)) {
                continue;
            }

            for (DetalleVenta detalle : venta.getDetalles()) {
                String sku = detalle.getSkuInterno();
                DatosProducto datos = datosProductos.computeIfAbsent(sku, k -> new DatosProducto(sku));

                datos.setCantidadVendida(datos.getCantidadVendida() + detalle.getCantidad());
                
                BigDecimal valorDetalle = detalle.getPrecioUnitarioVenta()
                        .multiply(new BigDecimal(detalle.getCantidad()));
                datos.setValorVentas(datos.getValorVentas().add(valorDetalle));
            }
        }

        return datosProductos;
    }

    /**
     * Clasifica productos en A, B, C según porcentajes acumulados.
     */
    private List<AnalisisABC> clasificarProductos(
            List<DatosProducto> productos,
            Integer sucursalId,
            LocalDate periodoInicio,
            LocalDate periodoFin,
            Double porcentajeA,
            Double porcentajeB) {

        List<AnalisisABC> analisis = new ArrayList<>();
        LocalDate hoy = LocalDate.now();

        for (DatosProducto datos : productos) {
            AnalisisABC abc = new AnalisisABC();
            abc.setSkuProducto(datos.getSkuProducto());
            abc.setSucursalId(sucursalId);
            abc.setPeriodoInicio(periodoInicio);
            abc.setPeriodoFin(periodoFin);
            abc.setCantidadVendida(datos.getCantidadVendida());
            abc.setValorVentas(datos.getValorVentas());
            abc.setPorcentajeValor(datos.getPorcentajeValor());
            abc.setPorcentajeAcumulado(datos.getPorcentajeAcumulado());
            abc.setFechaAnalisis(hoy);

            // Determinar clasificación
            double acum = datos.getPorcentajeAcumulado().doubleValue();
            if (acum <= porcentajeA) {
                abc.setClasificacion("A");
            } else if (acum <= porcentajeB) {
                abc.setClasificacion("B");
            } else {
                abc.setClasificacion("C");
            }

            // Obtener datos de inventario actual
            inventarioRepository.findByIdSkuInternoAndIdSucursalId(datos.getSkuProducto(), sucursalId)
                    .ifPresent(inv -> {
                        abc.setStockActual(inv.getStockActual());
                        BigDecimal valorStock = inv.getCostoPromedioPonderado()
                                .multiply(new BigDecimal(inv.getStockActual()));
                        abc.setValorStock(valorStock);

                        // Calcular rotación de inventario
                        if (inv.getStockActual() > 0) {
                            BigDecimal rotacion = datos.getValorVentas()
                                    .divide(valorStock, 4, RoundingMode.HALF_UP);
                            abc.setRotacionInventario(rotacion);
                        }
                    });

            abc.generarObservaciones();
            analisis.add(abc);
        }

        return analisis;
    }

    /**
     * Obtiene el análisis ABC más reciente por sucursal.
     * 
     * @param sucursalId ID de la sucursal
     * @return Lista de productos analizados
     */
    @Transactional(readOnly = true)
    public List<AnalisisABCResponseDTO> obtenerUltimoAnalisis(Integer sucursalId) {
        if (!sucursalRepository.existsById(sucursalId)) {
            throw new ResourceNotFoundException("Sucursal no encontrada: " + sucursalId);
        }

        List<AnalisisABC> analisis = analisisRepository.findUltimoAnalisisBySucursal(sucursalId);
        
        if (analisis.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No hay análisis ABC disponible para la sucursal " + sucursalId);
        }

        return analisis.stream()
                .map(a -> {
                    ProductoMaestro producto = productoRepository.findById(a.getSkuProducto()).orElse(null);
                    Sucursal sucursal = sucursalRepository.findById(a.getSucursalId()).orElse(null);
                    return mapper.toDTO(a, producto, sucursal);
                })
                .collect(Collectors.toList());
    }

    /**
     * Obtiene productos por clasificación.
     * 
     * @param sucursalId ID de la sucursal
     * @param clasificacion 'A', 'B' o 'C'
     * @return Lista de productos de la clasificación
     */
    @Transactional(readOnly = true)
    public List<AnalisisABCResponseDTO> obtenerPorClasificacion(Integer sucursalId, String clasificacion) {
        if (!clasificacion.matches("[ABC]")) {
            throw new BusinessException("Clasificación inválida: " + clasificacion + ". Use A, B o C");
        }

        // Obtener fecha del último análisis
        List<AnalisisABC> ultimos = analisisRepository.findUltimoAnalisisBySucursal(sucursalId);
        if (ultimos.isEmpty()) {
            throw new ResourceNotFoundException("No hay análisis disponible para la sucursal " + sucursalId);
        }

        LocalDate fechaAnalisis = ultimos.get(0).getFechaAnalisis();
        List<AnalisisABC> analisis = analisisRepository
                .findBySucursalIdAndClasificacionAndFechaAnalisis(sucursalId, clasificacion, fechaAnalisis);

        return analisis.stream()
                .map(a -> {
                    ProductoMaestro producto = productoRepository.findById(a.getSkuProducto()).orElse(null);
                    Sucursal sucursal = sucursalRepository.findById(a.getSucursalId()).orElse(null);
                    return mapper.toDTO(a, producto, sucursal);
                })
                .collect(Collectors.toList());
    }

    /**
     * Obtiene resumen del análisis ABC.
     * 
     * @param sucursalId ID de la sucursal
     * @return Resumen con estadísticas
     */
    @Transactional(readOnly = true)
    public AnalisisABCResumenDTO obtenerResumen(Integer sucursalId) {
        Sucursal sucursal = sucursalRepository.findById(sucursalId)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada: " + sucursalId));

        List<AnalisisABC> analisis = analisisRepository.findUltimoAnalisisBySucursal(sucursalId);
        if (analisis.isEmpty()) {
            throw new ResourceNotFoundException("No hay análisis disponible para la sucursal " + sucursalId);
        }

        AnalisisABC primero = analisis.get(0);
        List<Object[]> resumen = analisisRepository.resumenPorClasificacion(
                sucursalId, primero.getFechaAnalisis());

        AnalisisABCResumenDTO dto = new AnalisisABCResumenDTO();
        dto.setSucursalId(sucursalId);
        dto.setNombreSucursal(sucursal.getNombre());
        dto.setFechaAnalisis(primero.getFechaAnalisis());
        dto.setPeriodoInicio(primero.getPeriodoInicio());
        dto.setPeriodoFin(primero.getPeriodoFin());
        dto.setTotalProductos(analisis.size());

        BigDecimal valorTotal = analisis.stream()
                .map(AnalisisABC::getValorVentas)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        dto.setValorTotalVentas(valorTotal);

        // Procesar resumen por clasificación
        for (Object[] fila : resumen) {
            String clasif = (String) fila[0];
            BigDecimal valor = (BigDecimal) fila[1];
            Long cantidad = (Long) fila[2];

            BigDecimal porcentajeValor = valor.multiply(new BigDecimal("100"))
                    .divide(valorTotal, 2, RoundingMode.HALF_UP);
            BigDecimal porcentajeProductos = new BigDecimal(cantidad)
                    .multiply(new BigDecimal("100"))
                    .divide(new BigDecimal(analisis.size()), 2, RoundingMode.HALF_UP);

            AnalisisABCResumenDTO.ResumenClasificacion resClasif = 
                    new AnalisisABCResumenDTO.ResumenClasificacion(
                            cantidad.intValue(), valor, porcentajeValor, porcentajeProductos);

            switch (clasif) {
                case "A": dto.setClasificacionA(resClasif); break;
                case "B": dto.setClasificacionB(resClasif); break;
                case "C": dto.setClasificacionC(resClasif); break;
            }
        }

        // Agregar top productos clase A
        List<AnalisisABCResponseDTO> productosA = obtenerPorClasificacion(sucursalId, "A");
        dto.setProductosClaseA(productosA);

        return dto;
    }

    /**
     * Valida el periodo del análisis.
     */
    private void validarPeriodo(AnalisisABCRequestDTO request) {
        if (request.getPeriodoInicio().isAfter(request.getPeriodoFin())) {
            throw new BusinessException("La fecha de inicio no puede ser posterior a la fecha fin");
        }

        if (request.getPorcentajeA() >= request.getPorcentajeB()) {
            throw new BusinessException("El porcentaje A debe ser menor que el porcentaje B");
        }
    }

    /**
     * Determina las sucursales a analizar.
     */
    private List<Integer> determinarSucursales(Integer sucursalId) {
        if (sucursalId != null) {
            if (!sucursalRepository.existsById(sucursalId)) {
                throw new ResourceNotFoundException("Sucursal no encontrada: " + sucursalId);
            }
            return List.of(sucursalId);
        }

        return sucursalRepository.findAll().stream()
                .map(Sucursal::getId)
                .collect(Collectors.toList());
    }

    /**
     * Clase interna para acumular datos de producto durante el análisis.
     */
    private static class DatosProducto {
        private final String skuProducto;
        private Integer cantidadVendida = 0;
        private BigDecimal valorVentas = BigDecimal.ZERO;
        private BigDecimal porcentajeValor = BigDecimal.ZERO;
        private BigDecimal porcentajeAcumulado = BigDecimal.ZERO;

        public DatosProducto(String skuProducto) {
            this.skuProducto = skuProducto;
        }

        public String getSkuProducto() { return skuProducto; }
        public Integer getCantidadVendida() { return cantidadVendida; }
        public void setCantidadVendida(Integer cantidadVendida) { this.cantidadVendida = cantidadVendida; }
        public BigDecimal getValorVentas() { return valorVentas; }
        public void setValorVentas(BigDecimal valorVentas) { this.valorVentas = valorVentas; }
        public BigDecimal getPorcentajeValor() { return porcentajeValor; }
        public void setPorcentajeValor(BigDecimal porcentajeValor) { this.porcentajeValor = porcentajeValor; }
        public BigDecimal getPorcentajeAcumulado() { return porcentajeAcumulado; }
        public void setPorcentajeAcumulado(BigDecimal porcentajeAcumulado) { this.porcentajeAcumulado = porcentajeAcumulado; }
    }
}
