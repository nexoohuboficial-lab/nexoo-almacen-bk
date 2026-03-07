package com.nexoohub.almacen.finanzas.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para auditoría de cambios de precio.
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
public class AuditoriaPrecioDTO {
    private final Integer id;
    private final String skuInterno;
    private final String nombreProducto;
    private final BigDecimal precioAnterior;
    private final BigDecimal precioNuevo;
    private final BigDecimal porcentajeCambio;
    private final String razonCambio;
    private final LocalDateTime fechaCambio;
    private final String usuarioResponsable;
    
    public AuditoriaPrecioDTO(
            Integer id,
            String skuInterno,
            String nombreProducto,
            BigDecimal precioAnterior,
            BigDecimal precioNuevo,
            BigDecimal porcentajeCambio,
            String razonCambio,
            LocalDateTime fechaCambio,
            String usuarioResponsable) {
        this.id = id;
        this.skuInterno = skuInterno;
        this.nombreProducto = nombreProducto;
        this.precioAnterior = precioAnterior;
        this.precioNuevo = precioNuevo;
        this.porcentajeCambio = porcentajeCambio;
        this.razonCambio = razonCambio;
        this.fechaCambio = fechaCambio;
        this.usuarioResponsable = usuarioResponsable;
    }
    
    // Getters
    public Integer getId() { return id; }
    public String getSkuInterno() { return skuInterno; }
    public String getNombreProducto() { return nombreProducto; }
    public BigDecimal getPrecioAnterior() { return precioAnterior; }
    public BigDecimal getPrecioNuevo() { return precioNuevo; }
    public BigDecimal getPorcentajeCambio() { return porcentajeCambio; }
    public String getRazonCambio() { return razonCambio; }
    public LocalDateTime getFechaCambio() { return fechaCambio; }
    public String getUsuarioResponsable() { return usuarioResponsable; }
}
