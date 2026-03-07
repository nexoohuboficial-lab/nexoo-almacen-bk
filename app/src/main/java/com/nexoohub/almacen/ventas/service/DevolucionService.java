package com.nexoohub.almacen.ventas.service;

import com.nexoohub.almacen.inventario.entity.InventarioSucursal;
import com.nexoohub.almacen.inventario.entity.InventarioSucursalId;
import com.nexoohub.almacen.inventario.repository.InventarioSucursalRepository;
import com.nexoohub.almacen.ventas.dto.DevolucionRequestDTO;
import com.nexoohub.almacen.ventas.dto.DevolucionResponseDTO;
import com.nexoohub.almacen.ventas.entity.DetalleDevolucion;
import com.nexoohub.almacen.ventas.entity.DetalleVenta;
import com.nexoohub.almacen.ventas.entity.Devolucion;
import com.nexoohub.almacen.ventas.entity.Venta;
import com.nexoohub.almacen.ventas.repository.DevolucionRepository;
import com.nexoohub.almacen.ventas.repository.VentaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Servicio para gestionar devoluciones de productos.
 * 
 * <p>Este servicio maneja la lógica de negocio para procesar devoluciones,
 * incluyendo la reversión del inventario.</p>
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
@Service
public class DevolucionService {
    
    private final DevolucionRepository devolucionRepository;
    private final VentaRepository ventaRepository;
    private final InventarioSucursalRepository inventarioRepository;
    
    public DevolucionService(
            DevolucionRepository devolucionRepository,
            VentaRepository ventaRepository,
            InventarioSucursalRepository inventarioRepository) {
        this.devolucionRepository = devolucionRepository;
        this.ventaRepository = ventaRepository;
        this.inventarioRepository = inventarioRepository;
    }
    
    /**
     * Procesa una devolución completa o parcial de una venta.
     * 
     * <p><b>Lógica de negocio:</b></p>
     * <ul>
     *   <li>Valida que la venta existe</li>
     *   <li>Valida que los productos fueron parte de la venta original</li>
     *   <li>Valida cantidades (no exceder lo vendido)</li>
     *   <li>Revierte el inventario sumando las cantidades devueltas</li>
     *   <li>Calcula el total a devolver basado en precios originales</li>
     * </ul>
     * 
     * @param request DTO con información de la devolución
     * @return DTO con información de la devolución procesada
     * @throws IllegalArgumentException si hay validaciones fallidas
     */
    @Transactional
    public DevolucionResponseDTO procesarDevolucion(DevolucionRequestDTO request) {
        // 1. Validar que la venta existe
        Venta venta = ventaRepository.findById(request.getVentaId())
                .orElseThrow(() -> new IllegalArgumentException("Venta no encontrada con ID: " + request.getVentaId()));
        
        // 2. Crear mapa de detalles de venta para validación rápida
        Map<String, DetalleVenta> detallesVentaMap = venta.getDetalles().stream()
                .collect(Collectors.toMap(DetalleVenta::getSkuInterno, dv -> dv));
        
        // 3. Crear entidad de devolución
        Devolucion devolucion = new Devolucion();
        devolucion.setVentaId(request.getVentaId());
        devolucion.setSucursalId(request.getSucursalId());
        devolucion.setMotivo(request.getMotivo());
        devolucion.setMetodoReembolso(request.getMetodoReembolso());
        devolucion.setUsuarioAutorizo("SYSTEM"); // TODO: obtener del contexto de seguridad
        
        BigDecimal totalDevolucion = BigDecimal.ZERO;
        
        // 4. Procesar cada item de devolución
        for (DevolucionRequestDTO.ItemDevolucionDTO item : request.getItems()) {
            DetalleVenta detalleVenta = detallesVentaMap.get(item.getSkuInterno());
            
            // Validar que el producto fue vendido en esta venta
            if (detalleVenta == null) {
                throw new IllegalArgumentException("El producto " + item.getSkuInterno() 
                        + " no fue parte de la venta original");
            }
            
            // Validar cantidad (no exceder lo vendido)
            if (item.getCantidad() > detalleVenta.getCantidad()) {
                throw new IllegalArgumentException("La cantidad a devolver (" + item.getCantidad() 
                        + ") excede la cantidad vendida (" + detalleVenta.getCantidad() + ") para " + item.getSkuInterno());
            }
            
            // Crear detalle de devolución
            DetalleDevolucion detalle = new DetalleDevolucion();
            detalle.setDevolucion(devolucion);
            detalle.setSkuInterno(item.getSkuInterno());
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(detalleVenta.getPrecioUnitarioVenta());
            BigDecimal subtotal = detalleVenta.getPrecioUnitarioVenta()
                    .multiply(BigDecimal.valueOf(item.getCantidad()));
            detalle.setSubtotal(subtotal);
            detalle.setMotivoItem(item.getMotivoItem());
            
            devolucion.getDetalles().add(detalle);
            totalDevolucion = totalDevolucion.add(subtotal);
            
            // 5. Reversar inventario (sumar de vuelta las cantidades)
            revertirInventario(item.getSkuInterno(), request.getSucursalId(), item.getCantidad());
        }
        
        devolucion.setTotalDevuelto(totalDevolucion);
        
        // 6. Guardar devolución
        Devolucion devolucionGuardada = devolucionRepository.save(devolucion);
        
        // 7. Construir respuesta
        return mapToResponseDTO(devolucionGuardada);
    }
    
    /**
     * Revierte el inventario sumando las cantidades devueltas.
     * 
     * @param skuInterno SKU del producto
     * @param sucursalId ID de la sucursal
     * @param cantidad cantidad a devolver al inventario
     */
    private void revertirInventario(String skuInterno, Integer sucursalId, Integer cantidad) {
        InventarioSucursalId id = new InventarioSucursalId(sucursalId, skuInterno);
        InventarioSucursal inventario = inventarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontró inventario para SKU " + skuInterno + " en sucursal " + sucursalId));
        
        inventario.setStockActual(inventario.getStockActual() + cantidad);
        inventarioRepository.save(inventario);
    }
    
    /**
     * Obtiene todas las devoluciones de una venta.
     * 
     * @param ventaId ID de la venta
     * @return lista de devoluciones
     */
    @Transactional(readOnly = true)
    public List<DevolucionResponseDTO> obtenerDevolucionesPorVenta(Integer ventaId) {
        List<Devolucion> devoluciones = devolucionRepository.findByVentaId(ventaId);
        return devoluciones.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtiene una devolución por su ID.
     * 
     * @param id ID de la devolución
     * @return DTO con información de la devolución
     */
    @Transactional(readOnly = true)
    public DevolucionResponseDTO obtenerDevolucionPorId(Integer id) {
        Devolucion devolucion = devolucionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Devolución no encontrada con ID: " + id));
        return mapToResponseDTO(devolucion);
    }
    
    /**
     * Mapea una entidad Devolucion a DTO de respuesta.
     */
    private DevolucionResponseDTO mapToResponseDTO(Devolucion devolucion) {
        List<DevolucionResponseDTO.DetalleDTO> detallesDTO = devolucion.getDetalles().stream()
                .map(detalle -> new DevolucionResponseDTO.DetalleDTO(
                        detalle.getSkuInterno(),
                        "Producto " + detalle.getSkuInterno(), // TODO: obtener nombre real del producto
                        detalle.getCantidad(),
                        detalle.getPrecioUnitario(),
                        detalle.getSubtotal()
                ))
                .collect(Collectors.toList());
        
        return new DevolucionResponseDTO(
                devolucion.getId(),
                devolucion.getVentaId(),
                devolucion.getMotivo(),
                devolucion.getTotalDevuelto(),
                devolucion.getMetodoReembolso(),
                devolucion.getFechaDevolucion(),
                devolucion.getUsuarioAutorizo(),
                detallesDTO
        );
    }
}
