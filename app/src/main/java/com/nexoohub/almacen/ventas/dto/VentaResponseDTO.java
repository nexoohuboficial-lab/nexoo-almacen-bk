package com.nexoohub.almacen.ventas.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para respuestas de Venta.
 * 
 * <p>Evita serializar relaciones LAZY (Cliente, Sucursal, Empleado) 
 * y proporciona una estructura optimizada para el frontend.</p>
 * 
 * <p>Seguridad: No expone datos sensibles de empleados ni clientes.</p>
 */
public record VentaResponseDTO(
    Integer id,
    Integer clienteId,
    String clienteNombre,
    Integer sucursalId,
    String sucursalNombre,
    Integer vendedorId,
    String vendedorNombre,
    String metodoPago,
    BigDecimal total,
    LocalDateTime fechaVenta,
    List<DetalleVentaDTO> detalles
) {
    /**
     * DTO para detalle de venta anidado.
     */
    public record DetalleVentaDTO(
        Integer id,
        String skuInterno,
        String nombreProducto,
        Integer cantidad,
        BigDecimal precioUnitario,
        BigDecimal subtotal
    ) {}
}
