package com.nexoohub.almacen.compras.entity;

import com.nexoohub.almacen.catalogo.entity.Proveedor;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "compra")
public class Compra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "proveedor_id", insertable = false, updatable = false)
    private Integer proveedorId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proveedor_id")
    private Proveedor proveedor;

    @Column(name = "folio_factura_proveedor")
    private String folioFacturaProveedor;

    @Column(name = "total_compra")
    private BigDecimal totalCompra;

    @Column(name = "fecha_compra", updatable = false)
    private LocalDateTime fechaCompra;

    @Column(name = "usuario_creacion")
    private String usuarioCreacion;
    
    @OneToMany(mappedBy = "compra", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<DetalleCompra> detalles = new ArrayList<>();

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
    public Proveedor getProveedor() { return proveedor; }
    public void setProveedor(Proveedor proveedor) { this.proveedor = proveedor; }

    public String getFolioFacturaProveedor() { return folioFacturaProveedor; }
    public void setFolioFacturaProveedor(String folioFacturaProveedor) { this.folioFacturaProveedor = folioFacturaProveedor; }

    public BigDecimal getTotalCompra() { return totalCompra; }
    public void setTotalCompra(BigDecimal totalCompra) { this.totalCompra = totalCompra; }

    public LocalDateTime getFechaCompra() { return fechaCompra; }
    public void setFechaCompra(LocalDateTime fechaCompra) { this.fechaCompra = fechaCompra; }

    public String getUsuarioCreacion() { return usuarioCreacion; }
    public void setUsuarioCreacion(String usuarioCreacion) { this.usuarioCreacion = usuarioCreacion; }
    
    public List<DetalleCompra> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleCompra> detalles) { this.detalles = detalles; }
}