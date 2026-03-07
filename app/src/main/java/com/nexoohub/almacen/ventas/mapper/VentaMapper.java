package com.nexoohub.almacen.ventas.mapper;

import com.nexoohub.almacen.ventas.dto.VentaResponseDTO;
import com.nexoohub.almacen.ventas.dto.VentaResponseDTO.DetalleVentaDTO;
import com.nexoohub.almacen.ventas.entity.DetalleVenta;
import com.nexoohub.almacen.ventas.entity.Venta;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Mapper para convertir Venta entity a DTOs.
 * 
 * <p>Maneja conversión de relaciones LAZY sin causar excepciones.</p>
 */
@Component
public class VentaMapper {

    /**
     * Convierte una entidad Venta a DTO de respuesta.
     * 
     * @param entity La venta con detalles cargados
     * @return DTO con datos denormalizados
     */
    public VentaResponseDTO toResponseDTO(Venta entity) {
        if (entity == null) {
            return null;
        }
        
        return new VentaResponseDTO(
            entity.getId(),
            entity.getClienteId(),
            entity.getCliente() != null ? entity.getCliente().getNombre() : null,
            entity.getSucursalId(),
            entity.getSucursal() != null ? entity.getSucursal().getNombre() : null,
            entity.getVendedorId(),
            entity.getVendedor() != null ? entity.getVendedor().getNombre() : null,
            entity.getMetodoPago(),
            entity.getTotal(),
            entity.getFechaVenta(),
            entity.getDetalles() != null 
                ? entity.getDetalles().stream()
                    .map(this::toDetalleDTO)
                    .collect(Collectors.toList())
                : null
        );
    }

    /**
     * Convierte DetalleVenta a DTO anidado.
     */
    private DetalleVentaDTO toDetalleDTO(DetalleVenta detalle) {
        var precioUnitario = detalle.getPrecioUnitarioVenta();
        var cantidad = detalle.getCantidad();
        var subtotal = (precioUnitario != null && cantidad != null) 
            ? precioUnitario.multiply(new java.math.BigDecimal(cantidad))
            : java.math.BigDecimal.ZERO;
            
        return new DetalleVentaDTO(
            detalle.getId(),
            detalle.getSkuInterno(),
            detalle.getProducto() != null 
                ? detalle.getProducto().getNombreComercial() 
                : null,
            cantidad,
            precioUnitario,
            subtotal
        );
    }
}
