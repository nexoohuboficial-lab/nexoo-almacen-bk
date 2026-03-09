package com.nexoohub.almacen.cotizaciones.service;

import com.nexoohub.almacen.catalogo.entity.Cliente;
import com.nexoohub.almacen.catalogo.repository.ClienteRepository;
import com.nexoohub.almacen.common.exception.InvalidOperationException;
import com.nexoohub.almacen.common.exception.ResourceNotFoundException;
import com.nexoohub.almacen.common.exception.StockInsuficienteException;
import com.nexoohub.almacen.cotizaciones.dto.*;
import com.nexoohub.almacen.cotizaciones.entity.Cotizacion;
import com.nexoohub.almacen.cotizaciones.entity.DetalleCotizacion;
import com.nexoohub.almacen.cotizaciones.repository.CotizacionRepository;
import com.nexoohub.almacen.cotizaciones.repository.DetalleCotizacionRepository;
import com.nexoohub.almacen.empleados.entity.Empleado;
import com.nexoohub.almacen.empleados.repository.EmpleadoRepository;
import com.nexoohub.almacen.inventario.entity.InventarioSucursal;
import com.nexoohub.almacen.inventario.entity.InventarioSucursalId;
import com.nexoohub.almacen.inventario.entity.ProductoMaestro;
import com.nexoohub.almacen.inventario.repository.InventarioSucursalRepository;
import com.nexoohub.almacen.inventario.repository.ProductoMaestroRepository;
import com.nexoohub.almacen.sucursal.entity.Sucursal;
import com.nexoohub.almacen.sucursal.repository.SucursalRepository;
import com.nexoohub.almacen.ventas.entity.DetalleVenta;
import com.nexoohub.almacen.ventas.entity.Venta;
import com.nexoohub.almacen.ventas.repository.DetalleVentaRepository;
import com.nexoohub.almacen.ventas.repository.VentaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para gestionar cotizaciones (presupuestos).
 * 
 * <p>Gestiona la lógica de negocio relacionada con cotizaciones, incluyendo:</p>
 * <ul>
 *   <li>Creación de cotizaciones con generación automática de folio</li>
 *   <li>Actualización de cotizaciones en estado BORRADOR</li>
 *   <li>Cambio de estados (enviar, aceptar, rechazar)</li>
 *   <li>Conversión de cotizaciones a ventas</li>
 *   <li>Marcado automático de cotizaciones vencidas</li>
 *   <li>Cálculo de estadísticas y reportes</li>
 * </ul>
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
@Service
public class CotizacionService {
    
    private final CotizacionRepository cotizacionRepository;
    private final DetalleCotizacionRepository detalleCotizacionRepository;
    private final ClienteRepository clienteRepository;
    private final SucursalRepository sucursalRepository;
    private final EmpleadoRepository empleadoRepository;
    private final ProductoMaestroRepository productoRepository;
    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final InventarioSucursalRepository inventarioRepository;
    
    public CotizacionService(
            CotizacionRepository cotizacionRepository,
            DetalleCotizacionRepository detalleCotizacionRepository,
            ClienteRepository clienteRepository,
            SucursalRepository sucursalRepository,
            EmpleadoRepository empleadoRepository,
            ProductoMaestroRepository productoRepository,
            VentaRepository ventaRepository,
            DetalleVentaRepository detalleVentaRepository,
            InventarioSucursalRepository inventarioRepository) {
        this.cotizacionRepository = cotizacionRepository;
        this.detalleCotizacionRepository = detalleCotizacionRepository;
        this.clienteRepository = clienteRepository;
        this.sucursalRepository = sucursalRepository;
        this.empleadoRepository = empleadoRepository;
        this.productoRepository = productoRepository;
        this.ventaRepository = ventaRepository;
        this.detalleVentaRepository = detalleVentaRepository;
        this.inventarioRepository = inventarioRepository;
    }
    
    /**
     * Crea una nueva cotización
     * @param request datos de la cotización
     * @return cotización creada con folio generado
     */
    @Transactional
    public CotizacionResponseDTO crearCotizacion(CotizacionRequestDTO request) {
        // Validar que existan las entidades relacionadas
        Cliente cliente = clienteRepository.findById(request.getClienteId())
            .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con ID: " + request.getClienteId()));
        
        Sucursal sucursal = sucursalRepository.findById(request.getSucursalId())
            .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada con ID: " + request.getSucursalId()));
        
        Empleado vendedor = null;
        if (request.getVendedorId() != null) {
            vendedor = empleadoRepository.findById(request.getVendedorId())
                .orElseThrow(() -> new ResourceNotFoundException("Vendedor no encontrado con ID: " + request.getVendedorId()));
        }
        
        // Crear la cotización
        Cotizacion cotizacion = new Cotizacion();
        cotizacion.setFolio(generarFolio());
        cotizacion.setClienteId(request.getClienteId());
        cotizacion.setCliente(cliente);
        cotizacion.setSucursalId(request.getSucursalId());
        cotizacion.setSucursal(sucursal);
        if (request.getVendedorId() != null) {
            cotizacion.setVendedorId(request.getVendedorId());
            cotizacion.setVendedor(vendedor);
        }
        cotizacion.setFechaValidez(request.getFechaValidez());
        cotizacion.setNotas(request.getNotas());
        cotizacion.setTerminosCondiciones(request.getTerminosCondiciones());
        cotizacion.setObservacionesInternas(request.getObservacionesInternas());
        
        // Crear los detalles
        List<DetalleCotizacion> detalles = new ArrayList<>();
        for (DetalleCotizacionDTO detalleDTO : request.getDetalles()) {
            // Buscar el producto
            ProductoMaestro producto = productoRepository.findById(detalleDTO.getSkuInterno())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con SKU: " + detalleDTO.getSkuInterno()));
            
            DetalleCotizacion detalle = new DetalleCotizacion();
            detalle.setCotizacion(cotizacion);
            detalle.setSkuInterno(detalleDTO.getSkuInterno());
            detalle.setProducto(producto);
            detalle.setCantidad(detalleDTO.getCantidad());
            detalle.setPrecioUnitario(detalleDTO.getPrecioUnitario());
            detalle.setDescuentoEspecial(detalleDTO.getDescuentoEspecial() != null ? detalleDTO.getDescuentoEspecial() : BigDecimal.ZERO);
            detalle.setPorcentajeDescuento(detalleDTO.getPorcentajeDescuento() != null ? detalleDTO.getPorcentajeDescuento() : BigDecimal.ZERO);
            detalle.setNotas(detalleDTO.getNotas());
            detalles.add(detalle);
        }
        
        cotizacion.setDetalles(detalles);
        
        // Calcular totales
        cotizacion.calcularTotales();
        
        // Guardar
        Cotizacion cotizacionGuardada = cotizacionRepository.save(cotizacion);
        
        // Retornar DTO
        return mapearAResponseDTO(cotizacionGuardada);
    }
    
    /**
     * Actualiza una cotización existente (solo en estado BORRADOR)
     * @param id ID de la cotización
     * @param request nuevos datos
     * @return cotización actualizada
     */
    @Transactional
    public CotizacionResponseDTO actualizarCotizacion(Long id, CotizacionRequestDTO request) {
        Cotizacion cotizacion = cotizacionRepository.findWithDetallesById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Cotización no encontrada con ID: " + id));
        
        if (!cotizacion.esEditable()) {
            throw new InvalidOperationException("Solo se pueden editar cotizaciones en estado BORRADOR");
        }
        
        // Validar entidades relacionadas
        Cliente cliente = clienteRepository.findById(request.getClienteId())
            .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con ID: " + request.getClienteId()));
        
        Sucursal sucursal = sucursalRepository.findById(request.getSucursalId())
            .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada con ID: " + request.getSucursalId()));
        
        Empleado vendedor = null;
        if (request.getVendedorId() != null) {
            vendedor = empleadoRepository.findById(request.getVendedorId())
                .orElseThrow(() -> new ResourceNotFoundException("Vendedor no encontrado con ID: " + request.getVendedorId()));
        }
        
        // Actualizar campos
        cotizacion.setClienteId(request.getClienteId());
        cotizacion.setCliente(cliente);
        cotizacion.setSucursalId(request.getSucursalId());
        cotizacion.setSucursal(sucursal);
        if (request.getVendedorId() != null) {
            cotizacion.setVendedorId(request.getVendedorId());
            cotizacion.setVendedor(vendedor);
        } else {
            cotizacion.setVendedorId(null);
            cotizacion.setVendedor(null);
        }
        cotizacion.setFechaValidez(request.getFechaValidez());
        cotizacion.setNotas(request.getNotas());
        cotizacion.setTerminosCondiciones(request.getTerminosCondiciones());
        cotizacion.setObservacionesInternas(request.getObservacionesInternas());
        
        // Actualizar detalles - NO reemplazar la colección, sino limpiarla y agregar nuevos
        // Esto evita el error "orphanRemoval" de Hibernate
        cotizacion.getDetalles().clear();
        
        // Crear nuevos detalles
        for (DetalleCotizacionDTO detalleDTO : request.getDetalles()) {
            ProductoMaestro producto = productoRepository.findById(detalleDTO.getSkuInterno())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con SKU: " + detalleDTO.getSkuInterno()));
            
            DetalleCotizacion detalle = new DetalleCotizacion();
            detalle.setCotizacion(cotizacion);
            detalle.setSkuInterno(detalleDTO.getSkuInterno());
            detalle.setProducto(producto);
            detalle.setCantidad(detalleDTO.getCantidad());
            detalle.setPrecioUnitario(detalleDTO.getPrecioUnitario());
            detalle.setDescuentoEspecial(detalleDTO.getDescuentoEspecial() != null ? detalleDTO.getDescuentoEspecial() : BigDecimal.ZERO);
            detalle.setPorcentajeDescuento(detalleDTO.getPorcentajeDescuento() != null ? detalleDTO.getPorcentajeDescuento() : BigDecimal.ZERO);
            detalle.setNotas(detalleDTO.getNotas());
            cotizacion.getDetalles().add(detalle);
        }
        
        // Recalcular totales
        cotizacion.calcularTotales();
        
        // Guardar
        Cotizacion cotizacionActualizada = cotizacionRepository.save(cotizacion);
        
        return mapearAResponseDTO(cotizacionActualizada);
    }
    
    /**
     * Obtiene una cotización por ID
     * @param id ID de la cotización
     * @return cotización encontrada
     */
    @Transactional(readOnly = true)
    public CotizacionResponseDTO obtenerCotizacionPorId(Long id) {
        Cotizacion cotizacion = cotizacionRepository.findWithDetallesById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Cotización no encontrada con ID: " + id));
        
        return mapearAResponseDTO(cotizacion);
    }
    
    /**
     * Obtiene una cotización por folio
     * @param folio folio de la cotización
     * @return cotización encontrada
     */
    @Transactional(readOnly = true)
    public CotizacionResponseDTO obtenerCotizacionPorFolio(String folio) {
        Cotizacion cotizacion = cotizacionRepository.findWithDetallesByFolio(folio)
            .orElseThrow(() -> new ResourceNotFoundException("Cotización no encontrada con folio: " + folio));
        
        return mapearAResponseDTO(cotizacion);
    }
    
    /**
     * Lista todas las cotizaciones con paginación
     * @param pageable configuración de paginación
     * @return página de cotizaciones
     */
    @Transactional(readOnly = true)
    public Page<CotizacionResponseDTO> listarCotizaciones(Pageable pageable) {
        return cotizacionRepository.findAll(pageable)
            .map(this::mapearAResponseDTO);
    }
    
    /**
     * Busca cotizaciones con filtros múltiples
     * @param clienteId ID del cliente (opcional)
     * @param sucursalId ID de la sucursal (opcional)
     * @param vendedorId ID del vendedor (opcional)
     * @param estado estado de la cotización (opcional)
     * @param fechaInicio fecha de inicio (opcional)
     * @param fechaFin fecha de fin (opcional)
     * @param pageable configuración de paginación
     * @return página de cotizaciones filtradas
     */
    @Transactional(readOnly = true)
    public Page<CotizacionResponseDTO> buscarConFiltros(
            Integer clienteId,
            Integer sucursalId,
            Integer vendedorId,
            String estado,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            Pageable pageable) {
        
        return cotizacionRepository.buscarConFiltros(
            clienteId, sucursalId, vendedorId, estado, fechaInicio, fechaFin, pageable
        ).map(this::mapearAResponseDTO);
    }
    
    /**
     * Cambia el estado de una cotización
     * @param id ID de la cotización
     * @param request datos del cambio de estado
     * @return cotización actualizada
     */
    @Transactional
    public CotizacionResponseDTO cambiarEstado(Long id, CambiarEstadoRequestDTO request) {
        Cotizacion cotizacion = cotizacionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Cotización no encontrada con ID: " + id));
        
        String nuevoEstado = request.getNuevoEstado().toUpperCase();
        
        switch (nuevoEstado) {
            case "ENVIADA":
                cotizacion.marcarComoEnviada();
                break;
            case "ACEPTADA":
                cotizacion.marcarComoAceptada();
                break;
            case "RECHAZADA":
                if (request.getMotivo() == null || request.getMotivo().trim().isEmpty()) {
                    throw new InvalidOperationException("El motivo es obligatorio al rechazar una cotización");
                }
                cotizacion.marcarComoRechazada(request.getMotivo());
                break;
            default:
                throw new InvalidOperationException("Estado no válido: " + nuevoEstado + ". Estados permitidos: ENVIADA, ACEPTADA, RECHAZADA");
        }
        
        Cotizacion cotizacionActualizada = cotizacionRepository.save(cotizacion);
        
        return mapearAResponseDTO(cotizacionActualizada);
    }
    
    /**
     * Convierte una cotización en venta
     * @param id ID de la cotización
     * @param request datos de la conversión (método de pago)
     * @return ID de la venta creada
     */
    @Transactional
    public Integer convertirAVenta(Long id, ConvertirVentaRequestDTO request) {
        Cotizacion cotizacion = cotizacionRepository.findWithDetallesById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Cotización no encontrada con ID: " + id));
        
        if (!cotizacion.puedeConvertirseEnVenta()) {
            throw new InvalidOperationException("La cotización no puede ser convertida en venta. Estado: " + cotizacion.getEstado());
        }
        
        // Validar stock disponible para todos los productos
        for (DetalleCotizacion detalle : cotizacion.getDetalles()) {
            InventarioSucursalId inventarioId = new InventarioSucursalId(
                cotizacion.getSucursalId(),
                detalle.getSkuInterno()
            );
            
            InventarioSucursal inventario = inventarioRepository.findById(inventarioId)
                .orElseThrow(() -> new StockInsuficienteException(
                    "Producto " + detalle.getSkuInterno() + " no disponible en la sucursal"
                ));
            
            if (inventario.getStockActual() < detalle.getCantidad()) {
                throw new StockInsuficienteException(
                    "Stock insuficiente para " + detalle.getProducto().getNombreComercial() + 
                    ". Disponible: " + inventario.getStockActual() + ", Requerido: " + detalle.getCantidad()
                );
            }
        }
        
        // Crear la venta
        Venta venta = new Venta();
        venta.setClienteId(cotizacion.getClienteId());
        venta.setSucursalId(cotizacion.getSucursalId());
        venta.setVendedorId(cotizacion.getVendedorId());
        venta.setMetodoPago(request.getMetodoPago());
        venta.setTotal(cotizacion.getTotal());
        
        Venta ventaGuardada = ventaRepository.save(venta);
        
        // Crear detalles de venta y actualizar inventario
        for (DetalleCotizacion detalleCot : cotizacion.getDetalles()) {
            DetalleVenta detalleVenta = new DetalleVenta();
            detalleVenta.setVentaId(ventaGuardada.getId());
            detalleVenta.setSkuInterno(detalleCot.getSkuInterno());
            detalleVenta.setCantidad(detalleCot.getCantidad());
            detalleVenta.setPrecioUnitarioVenta(detalleCot.getPrecioUnitario());
            detalleVenta.setDescuentoEspecial(detalleCot.getDescuentoEspecial());
            detalleVenta.setPorcentajeDescuento(detalleCot.getPorcentajeDescuento());
            
            detalleVentaRepository.save(detalleVenta);
            
            // Actualizar inventario
            InventarioSucursalId inventarioId = new InventarioSucursalId(
                cotizacion.getSucursalId(),
                detalleCot.getSkuInterno()
            );
            
            InventarioSucursal inventario = inventarioRepository.findById(inventarioId).get();
            inventario.setStockActual(inventario.getStockActual() - detalleCot.getCantidad());
            inventarioRepository.save(inventario);
        }
        
        // Marcar la cotización como convertida
        cotizacion.marcarComoConvertida(ventaGuardada.getId());
        cotizacionRepository.save(cotizacion);
        
        return ventaGuardada.getId();
    }
    
    /**
     * Marca automáticamente las cotizaciones vencidas
     * @return cantidad de cotizaciones marcadas como vencidas
     */
    @Transactional
    public int marcarCotizacionesVencidas() {
        List<Cotizacion> vencidas = cotizacionRepository.findVencidas(LocalDate.now());
        
        for (Cotizacion cotizacion : vencidas) {
            cotizacion.setEstado("VENCIDA");
        }
        
        cotizacionRepository.saveAll(vencidas);
        
        return vencidas.size();
    }
    
    /**
     * Obtiene cotizaciones próximas a vencer
     * @param diasAnticipacion días de anticipación para alertar
     * @return lista de cotizaciones próximas a vencer
     */
    @Transactional(readOnly = true)
    public List<CotizacionResponseDTO> obtenerProximasAVencer(int diasAnticipacion) {
        LocalDate fechaActual = LocalDate.now();
        LocalDate fechaLimite = fechaActual.plusDays(diasAnticipacion);
        
        List<Cotizacion> cotizaciones = cotizacionRepository.findProximasAVencer(fechaActual, fechaLimite);
        
        return cotizaciones.stream()
            .map(this::mapearAResponseDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Obtiene cotizaciones pendientes de conversión
     * @return lista de cotizaciones listas para convertir a venta
     */
    @Transactional(readOnly = true)
    public List<CotizacionResponseDTO> obtenerPendientesDeConversion() {
        List<Cotizacion> cotizaciones = cotizacionRepository.findPendientesDeConversion(LocalDate.now());
        
        return cotizaciones.stream()
            .map(this::mapearAResponseDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Obtiene estadísticas generales de cotizaciones
     * @return estadísticas de cotizaciones
     */
    @Transactional(readOnly = true)
    public EstadisticasCotizacionDTO obtenerEstadisticas() {
        EstadisticasCotizacionDTO estadisticas = new EstadisticasCotizacionDTO();
        
        // Contar por estado
        estadisticas.setCotizacionesBorrador(cotizacionRepository.countByEstado("BORRADOR"));
        estadisticas.setCotizacionesEnviadas(cotizacionRepository.countByEstado("ENVIADA"));
        estadisticas.setCotizacionesAceptadas(cotizacionRepository.countByEstado("ACEPTADA"));
        estadisticas.setCotizacionesRechazadas(cotizacionRepository.countByEstado("RECHAZADA"));
        estadisticas.setCotizacionesVencidas(cotizacionRepository.countVencidas(LocalDate.now()));
        estadisticas.setCotizacionesConvertidas(cotizacionRepository.countByEstado("CONVERTIDA"));
        
        // Total
        Long total = estadisticas.getCotizacionesBorrador() +
                     estadisticas.getCotizacionesEnviadas() +
                     estadisticas.getCotizacionesAceptadas() +
                     estadisticas.getCotizacionesRechazadas() +
                     estadisticas.getCotizacionesVencidas() +
                     estadisticas.getCotizacionesConvertidas();
        estadisticas.setTotalCotizaciones(total);
        
        // Calcular valores totales
        estadisticas.setValorTotalBorrador(cotizacionRepository.calcularTotalPorEstado("BORRADOR"));
        estadisticas.setValorTotalEnviadas(cotizacionRepository.calcularTotalPorEstado("ENVIADA"));
        estadisticas.setValorTotalAceptadas(cotizacionRepository.calcularTotalPorEstado("ACEPTADA"));
        estadisticas.setValorTotalConvertidas(cotizacionRepository.calcularTotalPorEstado("CONVERTIDA"));
        
        // Calcular tasas
        if (total > 0) {
            estadisticas.setTasaConversion((estadisticas.getCotizacionesConvertidas() * 100.0) / total);
        }
        
        Long enviadas = estadisticas.getCotizacionesEnviadas() + estadisticas.getCotizacionesAceptadas() + 
                       estadisticas.getCotizacionesRechazadas() + estadisticas.getCotizacionesConvertidas();
        if (enviadas > 0) {
            estadisticas.setTasaAceptacion((estadisticas.getCotizacionesAceptadas() * 100.0) / enviadas);
            estadisticas.setTasaRechazo((estadisticas.getCotizacionesRechazadas() * 100.0) / enviadas);
        }
        
        return estadisticas;
    }
    
    /**
     * Elimina una cotización (solo en estado BORRADOR)
     * @param id ID de la cotización
     */
    @Transactional
    public void eliminarCotizacion(Long id) {
        Cotizacion cotizacion = cotizacionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Cotización no encontrada con ID: " + id));
        
        if (!cotizacion.esEditable()) {
            throw new InvalidOperationException("Solo se pueden eliminar cotizaciones en estado BORRADOR");
        }
        
        cotizacionRepository.delete(cotizacion);
    }
    
    // Métodos auxiliares
    
    /**
     * Genera un folio único para la cotización
     * Formato: COT-YYYY-NNNN
     * @return folio generado
     */
    private String generarFolio() {
        int anioActual = Year.now().getValue();
        String prefijo = "COT-" + anioActual + "-%";
        
        String ultimoFolio = cotizacionRepository.findUltimoFolioPorAnio(prefijo).orElse(null);
        
        int siguienteNumero = 1;
        if (ultimoFolio != null) {
            String numeroStr = ultimoFolio.substring(ultimoFolio.lastIndexOf('-') + 1);
            siguienteNumero = Integer.parseInt(numeroStr) + 1;
        }
        
        return String.format("COT-%d-%04d", anioActual, siguienteNumero);
    }
    
    /**
     * Mapea una entidad Cotizacion a un DTO de respuesta
     * @param cotizacion entidad a mapear
     * @return DTO de respuesta
     */
    private CotizacionResponseDTO mapearAResponseDTO(Cotizacion cotizacion) {
        CotizacionResponseDTO dto = new CotizacionResponseDTO();
        
        dto.setId(cotizacion.getId());
        dto.setFolio(cotizacion.getFolio());
        dto.setClienteId(cotizacion.getClienteId());
        dto.setSucursalId(cotizacion.getSucursalId());
        dto.setVendedorId(cotizacion.getVendedorId());
        dto.setEstado(cotizacion.getEstado());
        dto.setFechaCotizacion(cotizacion.getFechaCotizacion());
        dto.setFechaValidez(cotizacion.getFechaValidez());
        dto.setTotal(cotizacion.getTotal());
        dto.setSubtotal(cotizacion.getSubtotal());
        dto.setIva(cotizacion.getIva());
        dto.setDescuentoTotal(cotizacion.getDescuentoTotal());
        dto.setNotas(cotizacion.getNotas());
        dto.setTerminosCondiciones(cotizacion.getTerminosCondiciones());
        dto.setObservacionesInternas(cotizacion.getObservacionesInternas());
        dto.setFechaAceptacion(cotizacion.getFechaAceptacion());
        dto.setFechaRechazo(cotizacion.getFechaRechazo());
        dto.setMotivoRechazo(cotizacion.getMotivoRechazo());
        dto.setVentaId(cotizacion.getVentaId());
        dto.setFechaConversion(cotizacion.getFechaConversion());
        dto.setVencida(cotizacion.estaVencida());
        dto.setPuedeConvertirse(cotizacion.puedeConvertirseEnVenta());
        
        // Mapear nombres de entidades relacionadas si están cargadas
        if (cotizacion.getCliente() != null) {
            dto.setNombreCliente(cotizacion.getCliente().getNombre());
        }
        if (cotizacion.getSucursal() != null) {
            dto.setNombreSucursal(cotizacion.getSucursal().getNombre());
        }
        if (cotizacion.getVendedor() != null) {
            dto.setNombreVendedor(cotizacion.getVendedor().getNombre() + " " + cotizacion.getVendedor().getApellidos());
        }
        
        // Mapear detalles
        if (cotizacion.getDetalles() != null && !cotizacion.getDetalles().isEmpty()) {
            List<DetalleCotizacionDTO> detallesDTO = cotizacion.getDetalles().stream()
                .map(this::mapearDetalleADTO)
                .collect(Collectors.toList());
            dto.setDetalles(detallesDTO);
        }
        
        return dto;
    }
    
    /**
     * Mapea un detalle de cotización a DTO
     * @param detalle entidad de detalle
     * @return DTO de detalle
     */
    private DetalleCotizacionDTO mapearDetalleADTO(DetalleCotizacion detalle) {
        DetalleCotizacionDTO dto = new DetalleCotizacionDTO();
        
        dto.setId(detalle.getId());
        dto.setSkuInterno(detalle.getSkuInterno());
        dto.setCantidad(detalle.getCantidad());
        dto.setPrecioUnitario(detalle.getPrecioUnitario());
        dto.setDescuentoEspecial(detalle.getDescuentoEspecial());
        dto.setPorcentajeDescuento(detalle.getPorcentajeDescuento());
        dto.setNotas(detalle.getNotas());
        dto.setImporte(detalle.calcularImporte());
        
        if (detalle.getProducto() != null) {
            dto.setNombreProducto(detalle.getProducto().getNombreComercial());
        }
        
        return dto;
    }
}
