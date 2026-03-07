package com.nexoohub.almacen.ventas.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO de respuesta para devoluciones procesadas.
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
public class DevolucionResponseDTO {
    private final Integer id;
    private final Integer ventaId;
    private final String motivo;
    private final BigDecimal totalDevuelto;
    private final String metodoReembolso;
    private final LocalDateTime fechaDevolucion;
    private final String usuarioAutorizo;
    private final List<DetalleDTO> detalles;
    
    public DevolucionResponseDTO(
            Integer id,
            Integer ventaId,
            String motivo,
            BigDecimal totalDevuelto,
            String metodoReembolso,
            LocalDateTime fechaDevolucion,
            String usuarioAutorizo,
            List<DetalleDTO> detalles) {
        this.id = id;
        this.ventaId = ventaId;
        this.motivo = motivo;
        this.totalDevuelto = totalDevuelto;
        this.metodoReembolso = metodoReembolso;
        this.fechaDevolucion = fechaDevolucion;
        this.usuarioAutorizo = usuarioAutorizo;
        this.detalles = detalles;
    }
    
    public static class DetalleDTO {
        private final String skuInterno;
        private final String nombreProducto;
        private final Integer cantidad;
        private final BigDecimal precioUnitario;
        private final BigDecimal subtotal;
        
        public DetalleDTO(String skuInterno, String nombreProducto, Integer cantidad, 
                         BigDecimal precioUnitario, BigDecimal subtotal) {
            this.skuInterno = skuInterno;
            this.nombreProducto = nombreProducto;
            this.cantidad = cantidad;
            this.precioUnitario = precioUnitario;
            this.subtotal = subtotal;
        }
        
        public String getSkuInterno() { return skuInterno; }
        public String getNombreProducto() { return nombreProducto; }
        public Integer getCantidad() { return cantidad; }
        public BigDecimal getPrecioUnitario() { return precioUnitario; }
        public BigDecimal getSubtotal() { return subtotal; }
    }
    
    // Getters
    public Integer getId() { return id; }
    public Integer getVentaId() { return ventaId; }
    public String getMotivo() { return motivo; }
    public BigDecimal getTotalDevuelto() { return totalDevuelto; }
    public String getMetodoReembolso() { return metodoReembolso; }
    public LocalDateTime getFechaDevolucion() { return fechaDevolucion; }
    public String getUsuarioAutorizo() { return usuarioAutorizo; }
    public List<DetalleDTO> getDetalles() { return detalles; }
}
