package com.nexoohub.almacen.compras.entity;

import com.nexoohub.almacen.inventario.entity.ProductoMaestro;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;

@Entity
@Table(name = "detalle_compra")
public class DetalleCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "compra_id", insertable = false, updatable = false)
    private Integer compraId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compra_id")
    private Compra compra;

    @Column(name = "sku_interno", insertable = false, updatable = false)
    private String skuInterno;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sku_interno")
    private ProductoMaestro producto;

    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad;

    @Column(name = "costo_unitario_compra")
    @DecimalMin(value = "0.01", message = "El costo unitario debe ser mayor a cero")
    private BigDecimal costoUnitarioCompra;

    // ==========================================
    // GETTERS Y SETTERS
    // ==========================================
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getCompraId() { return compraId; }
    public void setCompraId(Integer compraId) { this.compraId = compraId; }
    public Compra getCompra() { return compra; }
    public void setCompra(Compra compra) { this.compra = compra; }

    public String getSkuInterno() { return skuInterno; }
    public void setSkuInterno(String skuInterno) { this.skuInterno = skuInterno; }
    public ProductoMaestro getProducto() { return producto; }
    public void setProducto(ProductoMaestro producto) { this.producto = producto; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public BigDecimal getCostoUnitarioCompra() { return costoUnitarioCompra; }
    public void setCostoUnitarioCompra(BigDecimal costoUnitarioCompra) { this.costoUnitarioCompra = costoUnitarioCompra; }
}