package com.nexoohub.almacen.ventas.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa una devolución de productos.
 * 
 * <p>Una devolución está asociada a una venta original y revierte el inventario.</p>
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
@Entity
@Table(name = "devolucion")
public class Devolucion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "venta_id", nullable = false)
    private Integer ventaId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venta_id", insertable = false, updatable = false)
    private Venta venta;
    
    @Column(name = "sucursal_id", nullable = false)
    private Integer sucursalId;
    
    @Column(nullable = false, length = 500)
    private String motivo; // Ej: "Producto defectuoso", "No cumple especificaciones"
    
    @Column(nullable = false)
    private BigDecimal totalDevuelto;
    
    @Column(name = "metodo_reembolso", nullable = false)
    private String metodoReembolso; // EFECTIVO, TARJETA, NOTA_CREDITO
    
    @Column(name = "fecha_devolucion", nullable = false, updatable = false)
    private LocalDateTime fechaDevolucion;
    
    @Column(name = "usuario_autorizo", length = 100)
    private String usuarioAutorizo;
    
    @OneToMany(mappedBy = "devolucion", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DetalleDevolucion> detalles = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        fechaDevolucion = LocalDateTime.now();
    }
    
    // ==========================================
    // GETTERS Y SETTERS
    // ==========================================
    
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public Integer getVentaId() { return ventaId; }
    public void setVentaId(Integer ventaId) { this.ventaId = ventaId; }
    
    public Venta getVenta() { return venta; }
    public void setVenta(Venta venta) { this.venta = venta; }
    
    public Integer getSucursalId() { return sucursalId; }
    public void setSucursalId(Integer sucursalId) { this.sucursalId = sucursalId; }
    
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
    
    public BigDecimal getTotalDevuelto() { return totalDevuelto; }
    public void setTotalDevuelto(BigDecimal totalDevuelto) { this.totalDevuelto = totalDevuelto; }
    
    public String getMetodoReembolso() { return metodoReembolso; }
    public void setMetodoReembolso(String metodoReembolso) { this.metodoReembolso = metodoReembolso; }
    
    public LocalDateTime getFechaDevolucion() { return fechaDevolucion; }
    public void setFechaDevolucion(LocalDateTime fechaDevolucion) { this.fechaDevolucion = fechaDevolucion; }
    
    public String getUsuarioAutorizo() { return usuarioAutorizo; }
    public void setUsuarioAutorizo(String usuarioAutorizo) { this.usuarioAutorizo = usuarioAutorizo; }
    
    public List<DetalleDevolucion> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleDevolucion> detalles) { this.detalles = detalles; }
}
