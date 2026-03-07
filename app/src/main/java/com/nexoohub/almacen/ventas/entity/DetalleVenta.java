package com.nexoohub.almacen.ventas.entity;

import com.nexoohub.almacen.inventario.entity.ProductoMaestro;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;

@Entity
@Table(name = "detalle_venta")
public class DetalleVenta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "venta_id", insertable = false, updatable = false)
    private Integer ventaId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venta_id")
    private Venta venta;

    @Column(name = "sku_interno", insertable = false, updatable = false)
    private String skuInterno;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sku_interno")
    private ProductoMaestro producto;

    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad;

    @Column(name = "precio_unitario_venta")
    @DecimalMin(value = "0.01", message = "El precio unitario debe ser mayor a cero")
    private BigDecimal precioUnitarioVenta;

    // Getters y Setters...
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public Integer getVentaId() { return ventaId; }
    public void setVentaId(Integer ventaId) { this.ventaId = ventaId; }
    public Venta getVenta() { return venta; }
    public void setVenta(Venta venta) { this.venta = venta; }
    
    public String getSkuInterno() { return skuInterno; }
    public void setSkuInterno(String skuInterno) { this.skuInterno = skuInterno; }
    public ProductoMaestro getProducto() { return producto; }
    public void setProducto(ProductoMaestro producto) { this.producto = producto; }
    
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    public BigDecimal getPrecioUnitarioVenta() { return precioUnitarioVenta; }
    public void setPrecioUnitarioVenta(BigDecimal precioUnitarioVenta) { this.precioUnitarioVenta = precioUnitarioVenta; }
}