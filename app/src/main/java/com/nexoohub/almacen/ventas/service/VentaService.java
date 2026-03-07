package com.nexoohub.almacen.ventas.service;

import com.nexoohub.almacen.catalogo.entity.Cliente;
import com.nexoohub.almacen.catalogo.entity.PrecioEspecial;
import com.nexoohub.almacen.catalogo.repository.ClienteRepository;
import com.nexoohub.almacen.catalogo.repository.PrecioEspecialRepository;
import com.nexoohub.almacen.common.entity.Usuario;
import com.nexoohub.almacen.common.exception.ResourceNotFoundException;
import com.nexoohub.almacen.common.exception.StockInsuficienteException;
import com.nexoohub.almacen.common.repository.UsuarioRepository;
import com.nexoohub.almacen.finanzas.entity.HistorialPrecio;
import com.nexoohub.almacen.finanzas.repository.HistorialPrecioRepository;
import com.nexoohub.almacen.inventario.entity.InventarioSucursal;
import com.nexoohub.almacen.inventario.entity.InventarioSucursalId;
import com.nexoohub.almacen.inventario.repository.InventarioSucursalRepository;
import com.nexoohub.almacen.ventas.dto.VentaRequestDTO;
import com.nexoohub.almacen.ventas.entity.DetalleVenta;
import com.nexoohub.almacen.ventas.entity.Venta;
import com.nexoohub.almacen.ventas.repository.DetalleVentaRepository;
import com.nexoohub.almacen.ventas.repository.VentaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Servicio para el procesamiento de ventas.
 * 
 * <p>Gestiona la lógica de negocio relacionada con ventas, incluyendo:</p>
 * <ul>
 *   <li>Validación de stock disponible</li>
 *   <li>Aplicación de precios dinámicos según tipo de cliente</li>
 *   <li>Actualización automática de inventario</li>
 *   <li>Registro de detalles de venta</li>
 * </ul>
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
@Service
public class VentaService {

    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final InventarioSucursalRepository inventarioRepository;
    private final HistorialPrecioRepository historialPrecioRepository;
    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;
    private final PrecioEspecialRepository precioEspecialRepository;

    /**
     * Constructor con inyección de dependencias.
     * 
     * @param ventaRepository Repositorio de ventas
     * @param detalleVentaRepository Repositorio de detalles de venta
     * @param inventarioRepository Repositorio de inventario
     * @param historialPrecioRepository Repositorio de historial de precios
     * @param usuarioRepository Repositorio de usuarios
     * @param clienteRepository Repositorio de clientes
     * @param precioEspecialRepository Repositorio de precios especiales
     */
    public VentaService(
            VentaRepository ventaRepository,
            DetalleVentaRepository detalleVentaRepository,
            InventarioSucursalRepository inventarioRepository,
            HistorialPrecioRepository historialPrecioRepository,
            UsuarioRepository usuarioRepository,
            ClienteRepository clienteRepository,
            PrecioEspecialRepository precioEspecialRepository) {
        this.ventaRepository = ventaRepository;
        this.detalleVentaRepository = detalleVentaRepository;
        this.inventarioRepository = inventarioRepository;
        this.historialPrecioRepository = historialPrecioRepository;
        this.usuarioRepository = usuarioRepository;
        this.clienteRepository = clienteRepository;
        this.precioEspecialRepository = precioEspecialRepository;
    }

    /**
     * Procesa una venta completa incluyendo validación de stock, cálculo de precios
     * y actualización de inventario.
     * 
     * @param request DTO con los datos de la venta (cliente, sucursal, items)
     * @param vendedorUsername Username del vendedor que procesa la venta
     * @return Venta procesada con folio generado y total calculado
     * @throws ResourceNotFoundException si el vendedor, cliente o producto no existen
     * @throws StockInsuficienteException si no hay inventario suficiente
     */
    @Transactional
    public Venta procesarVenta(VentaRequestDTO request, String vendedorUsername) {
        
        Usuario usuario = usuarioRepository.findByUsername(vendedorUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", vendedorUsername));

        // 1. OBTENEMOS AL CLIENTE PARA SABER SU TIPO (Público, Taller, Mayorista)
        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", request.getClienteId()));

        Venta venta = new Venta();
        venta.setClienteId(cliente.getId());
        venta.setSucursalId(request.getSucursalId());
        venta.setVendedorId(usuario.getId().intValue()); 
        venta.setMetodoPago(request.getMetodoPago());
        venta.setTotal(BigDecimal.ZERO);
        
        Venta ventaGuardada = ventaRepository.save(venta);
        BigDecimal totalVenta = BigDecimal.ZERO;

        for (VentaRequestDTO.ItemVentaDTO item : request.getItems()) {
            
            // A) Verificar Existencias
            InventarioSucursalId idInv = new InventarioSucursalId(request.getSucursalId(), item.getSkuInterno());
            InventarioSucursal inventario = inventarioRepository.findById(idInv)
                    .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Producto '%s' no existe en sucursal %d", item.getSkuInterno(), request.getSucursalId()),
                        "Producto", 
                        item.getSkuInterno()
                    ));

            if (inventario.getStockActual() < item.getCantidad()) {
                throw new StockInsuficienteException(
                    item.getSkuInterno(), 
                    request.getSucursalId(),
                    inventario.getStockActual(), 
                    item.getCantidad()
                );
            }

            // B) Obtener el precio público base
            HistorialPrecio ultimoPrecio = historialPrecioRepository.findTopBySkuInternoOrderByFechaCalculoDesc(item.getSkuInterno())
                    .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Producto '%s' no tiene precio configurado", item.getSkuInterno()),
                        "HistorialPrecio",
                        item.getSkuInterno()
                    ));

            // ==========================================
            // C) LÓGICA DE PRECIO DINÁMICO (LA MAGIA)
            // ==========================================
            BigDecimal precioFinalAplicado = ultimoPrecio.getPrecioFinalPublico(); // Asumimos precio público por defecto

            Optional<PrecioEspecial> precioEsp = precioEspecialRepository
                    .findBySkuInternoAndTipoClienteId(item.getSkuInterno(), cliente.getTipoClienteId());

            if (precioEsp.isPresent()) {
                precioFinalAplicado = precioEsp.get().getPrecioFijo(); // ¡Cambiamos al precio de Taller!
            }
            // ==========================================

            // D) Restar del inventario
            inventario.setStockActual(inventario.getStockActual() - item.getCantidad());
            inventarioRepository.save(inventario);

            // E) Calcular descuento especial si aplica
            BigDecimal precioFinal = precioFinalAplicado;
            BigDecimal descuentoEspecial = BigDecimal.ZERO;
            BigDecimal porcentajeDescuento = BigDecimal.ZERO;
            
            if (item.getPrecioOfertaEspecial() != null && 
                item.getPrecioOfertaEspecial().compareTo(precioFinalAplicado) < 0) {
                // El vendedor aplicó un descuento adicional
                descuentoEspecial = precioFinalAplicado.subtract(item.getPrecioOfertaEspecial());
                porcentajeDescuento = descuentoEspecial
                    .divide(precioFinalAplicado, 4, java.math.RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"))
                    .setScale(2, java.math.RoundingMode.HALF_UP);
                precioFinal = item.getPrecioOfertaEspecial();
            }

            // F) Registrar detalle de venta con el precio correcto y descuento
            DetalleVenta detalle = new DetalleVenta();
            detalle.setVentaId(ventaGuardada.getId());
            detalle.setSkuInterno(item.getSkuInterno());
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitarioVenta(precioFinal);
            detalle.setDescuentoEspecial(descuentoEspecial);
            detalle.setPorcentajeDescuento(porcentajeDescuento);
            detalleVentaRepository.save(detalle);

            // G) Acumular total
            BigDecimal subtotal = precioFinal.multiply(new BigDecimal(item.getCantidad()));
            totalVenta = totalVenta.add(subtotal);
        }

        ventaGuardada.setTotal(totalVenta);
        return ventaRepository.save(ventaGuardada);
    }
}