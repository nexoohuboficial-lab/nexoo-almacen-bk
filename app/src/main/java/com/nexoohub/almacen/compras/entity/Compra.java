package com.nexoohub.almacen.compras.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "compra")
public class Compra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "proveedor_id")
    private Integer proveedorId;

    @Column(name = "folio_factura_proveedor")
    private String folioFacturaProveedor;

    @Column(name = "total_compra")
    private BigDecimal totalCompra;

    @Column(name = "fecha_compra", updatable = false)
    private LocalDateTime fechaCompra;

    @Column(name = "usuario_creacion")
    private String usuarioCreacion;

    @PrePersist
    public void prePersist() {
        this.fechaCompra = LocalDateTime.now();
    }

    // ==========================================
    // GETTERS Y SETTERS
    // ==========================================
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getProveedorId() { return proveedorId; }
    public void setProveedorId(Integer proveedorId) { this.proveedorId = proveedorId; }

    public String getFolioFacturaProveedor() { return folioFacturaProveedor; }
    public void setFolioFacturaProveedor(String folioFacturaProveedor) { this.folioFacturaProveedor = folioFacturaProveedor; }

    public BigDecimal getTotalCompra() { return totalCompra; }
    public void setTotalCompra(BigDecimal totalCompra) { this.totalCompra = totalCompra; }

    public LocalDateTime getFechaCompra() { return fechaCompra; }
    public void setFechaCompra(LocalDateTime fechaCompra) { this.fechaCompra = fechaCompra; }

    public String getUsuarioCreacion() { return usuarioCreacion; }
    public void setUsuarioCreacion(String usuarioCreacion) { this.usuarioCreacion = usuarioCreacion; }
}