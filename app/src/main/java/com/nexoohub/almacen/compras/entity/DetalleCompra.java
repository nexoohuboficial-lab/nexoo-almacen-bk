package com.nexoohub.almacen.compras.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "detalle_compra")
public class DetalleCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "compra_id")
    private Integer compraId;

    @Column(name = "sku_interno")
    private String skuInterno;

    private Integer cantidad;

    @Column(name = "costo_unitario_compra")
    private BigDecimal costoUnitarioCompra;

    // ==========================================
    // GETTERS Y SETTERS
    // ==========================================
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getCompraId() { return compraId; }
    public void setCompraId(Integer compraId) { this.compraId = compraId; }

    public String getSkuInterno() { return skuInterno; }
    public void setSkuInterno(String skuInterno) { this.skuInterno = skuInterno; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public BigDecimal getCostoUnitarioCompra() { return costoUnitarioCompra; }
    public void setCostoUnitarioCompra(BigDecimal costoUnitarioCompra) { this.costoUnitarioCompra = costoUnitarioCompra; }
}