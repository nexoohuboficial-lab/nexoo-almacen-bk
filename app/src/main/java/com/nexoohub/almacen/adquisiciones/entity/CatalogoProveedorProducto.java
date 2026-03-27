package com.nexoohub.almacen.adquisiciones.entity;

import com.nexoohub.almacen.catalogo.entity.Proveedor;
import com.nexoohub.almacen.inventario.entity.ProductoMaestro;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "catalogo_proveedor_producto", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"proveedor_id", "producto_sku"})
})
public class CatalogoProveedorProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proveedor_id", nullable = false)
    private Proveedor proveedor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_sku", nullable = false)
    private ProductoMaestro producto;

    @Column(name = "proveedor_codigo_producto", length = 100)
    private String proveedorCodigoProducto;

    @Column(name = "precio_compra_actual", precision = 12, scale = 2, nullable = false)
    private BigDecimal precioCompraActual;

    @Column(name = "moneda", length = 3, nullable = false)
    private String moneda;

    @Column(name = "disponibilidad", nullable = false)
    private Boolean disponibilidad = true;

    @Column(name = "tiempo_entrega_dias")
    private Integer tiempoEntregaDias;

    @Column(name = "ultima_actualizacion", nullable = false)
    private LocalDateTime ultimaActualizacion;

    @Column(name = "precio_venta_sugerido_proveedor", precision = 12, scale = 2)
    private BigDecimal precioVentaSugeridoProveedor;

    @Column(name = "ultima_compra_costo", precision = 12, scale = 2)
    private BigDecimal ultimaCompraCosto;

    @Column(name = "ultima_compra_fecha")
    private LocalDateTime ultimaCompraFecha;

    @PrePersist
    @PreUpdate
    public void preUpdate() {
        ultimaActualizacion = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Proveedor getProveedor() { return proveedor; }
    public void setProveedor(Proveedor proveedor) { this.proveedor = proveedor; }

    public ProductoMaestro getProducto() { return producto; }
    public void setProducto(ProductoMaestro producto) { this.producto = producto; }

    public String getProveedorCodigoProducto() { return proveedorCodigoProducto; }
    public void setProveedorCodigoProducto(String proveedorCodigoProducto) { this.proveedorCodigoProducto = proveedorCodigoProducto; }

    public BigDecimal getPrecioCompraActual() { return precioCompraActual; }
    public void setPrecioCompraActual(BigDecimal precioCompraActual) { this.precioCompraActual = precioCompraActual; }

    public String getMoneda() { return moneda; }
    public void setMoneda(String moneda) { this.moneda = moneda; }

    public Boolean getDisponibilidad() { return disponibilidad; }
    public void setDisponibilidad(Boolean disponibilidad) { this.disponibilidad = disponibilidad; }

    public Integer getTiempoEntregaDias() { return tiempoEntregaDias; }
    public void setTiempoEntregaDias(Integer tiempoEntregaDias) { this.tiempoEntregaDias = tiempoEntregaDias; }

    public LocalDateTime getUltimaActualizacion() { return ultimaActualizacion; }
    public void setUltimaActualizacion(LocalDateTime ultimaActualizacion) { this.ultimaActualizacion = ultimaActualizacion; }

    public BigDecimal getPrecioVentaSugeridoProveedor() { return precioVentaSugeridoProveedor; }
    public void setPrecioVentaSugeridoProveedor(BigDecimal precioVentaSugeridoProveedor) { this.precioVentaSugeridoProveedor = precioVentaSugeridoProveedor; }

    public BigDecimal getUltimaCompraCosto() { return ultimaCompraCosto; }
    public void setUltimaCompraCosto(BigDecimal ultimaCompraCosto) { this.ultimaCompraCosto = ultimaCompraCosto; }

    public LocalDateTime getUltimaCompraFecha() { return ultimaCompraFecha; }
    public void setUltimaCompraFecha(LocalDateTime ultimaCompraFecha) { this.ultimaCompraFecha = ultimaCompraFecha; }
}
