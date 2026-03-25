package com.nexoohub.almacen.erp.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "devolucion_proveedor_detalle")
public class DevolucionProveedorDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "devolucion_id", nullable = false)
    private DevolucionProveedor devolucion;

    @Column(name = "sku_interno", nullable = false)
    private String skuInterno;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(name = "costo_unitario", precision = 12, scale = 2, nullable = false)
    private BigDecimal costoUnitario;

    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal subtotal;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (subtotal == null && costoUnitario != null && cantidad != null) {
            subtotal = costoUnitario.multiply(new BigDecimal(cantidad));
        }
    }

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public DevolucionProveedor getDevolucion() { return devolucion; }
    public void setDevolucion(DevolucionProveedor devolucion) { this.devolucion = devolucion; }
    public String getSkuInterno() { return skuInterno; }
    public void setSkuInterno(String skuInterno) { this.skuInterno = skuInterno; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    public BigDecimal getCostoUnitario() { return costoUnitario; }
    public void setCostoUnitario(BigDecimal costoUnitario) { this.costoUnitario = costoUnitario; }
    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
