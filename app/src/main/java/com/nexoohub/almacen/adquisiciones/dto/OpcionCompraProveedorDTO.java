package com.nexoohub.almacen.adquisiciones.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OpcionCompraProveedorDTO {
    private Integer proveedorId;
    private String nombreProveedor;
    private String codigoArticuloProveedor;
    private BigDecimal precioCompraCotizado;
    private String moneda;
    private Integer tiempoEstimadoEntregaDias;
    private BigDecimal precioSugeridoVenta;
    private boolean mejorOpcion;
    private LocalDateTime ultimaActualizacion;

    // Constructores
    public OpcionCompraProveedorDTO() {}

    // Getters y Setters
    public Integer getProveedorId() { return proveedorId; }
    public void setProveedorId(Integer proveedorId) { this.proveedorId = proveedorId; }

    public String getNombreProveedor() { return nombreProveedor; }
    public void setNombreProveedor(String nombreProveedor) { this.nombreProveedor = nombreProveedor; }

    public String getCodigoArticuloProveedor() { return codigoArticuloProveedor; }
    public void setCodigoArticuloProveedor(String codigoArticuloProveedor) { this.codigoArticuloProveedor = codigoArticuloProveedor; }

    public BigDecimal getPrecioCompraCotizado() { return precioCompraCotizado; }
    public void setPrecioCompraCotizado(BigDecimal precioCompraCotizado) { this.precioCompraCotizado = precioCompraCotizado; }

    public String getMoneda() { return moneda; }
    public void setMoneda(String moneda) { this.moneda = moneda; }

    public Integer getTiempoEstimadoEntregaDias() { return tiempoEstimadoEntregaDias; }
    public void setTiempoEstimadoEntregaDias(Integer tiempoEstimadoEntregaDias) { this.tiempoEstimadoEntregaDias = tiempoEstimadoEntregaDias; }

    public BigDecimal getPrecioSugeridoVenta() { return precioSugeridoVenta; }
    public void setPrecioSugeridoVenta(BigDecimal precioSugeridoVenta) { this.precioSugeridoVenta = precioSugeridoVenta; }

    public boolean isMejorOpcion() { return mejorOpcion; }
    public void setMejorOpcion(boolean mejorOpcion) { this.mejorOpcion = mejorOpcion; }

    public LocalDateTime getUltimaActualizacion() { return ultimaActualizacion; }
    public void setUltimaActualizacion(LocalDateTime ultimaActualizacion) { this.ultimaActualizacion = ultimaActualizacion; }
}
