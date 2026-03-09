package com.nexoohub.almacen.cotizaciones.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * DTO para representar un detalle (línea de item) de una cotización
 */
public class DetalleCotizacionDTO {
    
    private Long id;
    
    @NotBlank(message = "El SKU interno es obligatorio")
    private String skuInterno;
    
    private String nombreProducto; // Solo para respuesta
    
    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad;
    
    @NotNull(message = "El precio unitario es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio unitario debe ser mayor a cero")
    private BigDecimal precioUnitario;
    
    @DecimalMin(value = "0.00", message = "El descuento especial no puede ser negativo")
    private BigDecimal descuentoEspecial = BigDecimal.ZERO;
    
    @DecimalMin(value = "0.00", message = "El porcentaje de descuento no puede ser negativo")
    private BigDecimal porcentajeDescuento = BigDecimal.ZERO;
    
    private String notas;
    
    private BigDecimal importe; // Solo para respuesta (cantidad * precio - descuento)
    
    // Constructores
    
    public DetalleCotizacionDTO() {
    }
    
    public DetalleCotizacionDTO(String skuInterno, Integer cantidad, BigDecimal precioUnitario) {
        this.skuInterno = skuInterno;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }
    
    // Getters y Setters
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getSkuInterno() {
        return skuInterno;
    }
    
    public void setSkuInterno(String skuInterno) {
        this.skuInterno = skuInterno;
    }
    
    public String getNombreProducto() {
        return nombreProducto;
    }
    
    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }
    
    public Integer getCantidad() {
        return cantidad;
    }
    
    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
    
    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }
    
    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }
    
    public BigDecimal getDescuentoEspecial() {
        return descuentoEspecial;
    }
    
    public void setDescuentoEspecial(BigDecimal descuentoEspecial) {
        this.descuentoEspecial = descuentoEspecial;
    }
    
    public BigDecimal getPorcentajeDescuento() {
        return porcentajeDescuento;
    }
    
    public void setPorcentajeDescuento(BigDecimal porcentajeDescuento) {
        this.porcentajeDescuento = porcentajeDescuento;
    }
    
    public String getNotas() {
        return notas;
    }
    
    public void setNotas(String notas) {
        this.notas = notas;
    }
    
    public BigDecimal getImporte() {
        return importe;
    }
    
    public void setImporte(BigDecimal importe) {
        this.importe = importe;
    }
}
