package com.nexoohub.almacen.cotizaciones.entity;

import com.nexoohub.almacen.inventario.entity.ProductoMaestro;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Entidad que representa una línea de detalle dentro de una cotización.
 * Cada detalle corresponde a un producto específico con su cantidad, precio y descuentos.
 */
@Entity
@Table(name = "detalle_cotizacion", indexes = {
    @Index(name = "idx_detalle_cotizacion_cotizacion", columnList = "cotizacion_id"),
    @Index(name = "idx_detalle_cotizacion_producto", columnList = "sku_interno")
})
public class DetalleCotizacion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "cotizacion_id", insertable = false, updatable = false)
    private Long cotizacionId;
    
    @NotNull(message = "La cotización es obligatoria")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cotizacion_id", nullable = false)
    private Cotizacion cotizacion;
    
    @NotBlank(message = "El SKU interno es obligatorio")
    @Column(name = "sku_interno", insertable = false, updatable = false)
    private String skuInterno;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sku_interno", nullable = false)
    private ProductoMaestro producto;
    
    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    @Column(nullable = false)
    private Integer cantidad;
    
    @NotNull(message = "El precio unitario es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio unitario debe ser mayor a cero")
    @Column(name = "precio_unitario", nullable = false, precision = 15, scale = 2)
    private BigDecimal precioUnitario;
    
    @DecimalMin(value = "0.00", message = "El descuento especial no puede ser negativo")
    @Column(name = "descuento_especial", precision = 15, scale = 2)
    private BigDecimal descuentoEspecial = BigDecimal.ZERO;
    
    @DecimalMin(value = "0.00", message = "El porcentaje de descuento no puede ser negativo")
    @Column(name = "porcentaje_descuento", precision = 5, scale = 2)
    private BigDecimal porcentajeDescuento = BigDecimal.ZERO;
    
    @Column(name = "notas", length = 500)
    private String notas;
    
    /**
     * Calcula el importe total de esta línea de detalle
     * @return precio_unitario * cantidad - descuento_especial
     */
    public BigDecimal calcularImporte() {
        BigDecimal importeBase = precioUnitario.multiply(new BigDecimal(cantidad));
        BigDecimal descuento = descuentoEspecial != null ? descuentoEspecial : BigDecimal.ZERO;
        return importeBase.subtract(descuento);
    }
    
    /**
     * Calcula el descuento aplicado basado en el porcentaje
     * @return importe_base * (porcentaje_descuento / 100)
     */
    public BigDecimal calcularDescuentoPorPorcentaje() {
        if (porcentajeDescuento == null || porcentajeDescuento.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal importeBase = precioUnitario.multiply(new BigDecimal(cantidad));
        return importeBase.multiply(porcentajeDescuento.divide(new BigDecimal("100")));
    }
    
    /**
     * Calcula el precio unitario neto después del descuento
     * @return precio_unitario - (descuento_especial / cantidad)
     */
    public BigDecimal calcularPrecioUnitarioNeto() {
        BigDecimal descuento = descuentoEspecial != null ? descuentoEspecial : BigDecimal.ZERO;
        BigDecimal descuentoPorUnidad = descuento.divide(new BigDecimal(cantidad), 2, java.math.RoundingMode.HALF_UP);
        return precioUnitario.subtract(descuentoPorUnidad);
    }
    
    // Getters y Setters
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getCotizacionId() {
        return cotizacionId;
    }
    
    public void setCotizacionId(Long cotizacionId) {
        this.cotizacionId = cotizacionId;
    }
    
    public Cotizacion getCotizacion() {
        return cotizacion;
    }
    
    public void setCotizacion(Cotizacion cotizacion) {
        this.cotizacion = cotizacion;
    }
    
    public String getSkuInterno() {
        return skuInterno;
    }
    
    public void setSkuInterno(String skuInterno) {
        this.skuInterno = skuInterno;
    }
    
    public ProductoMaestro getProducto() {
        return producto;
    }
    
    public void setProducto(ProductoMaestro producto) {
        this.producto = producto;
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
}
