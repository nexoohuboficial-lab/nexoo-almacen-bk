package com.nexoohub.almacen.crm.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "ticket_garantia")
public class TicketGarantia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "venta_id")
    private Integer ventaId;

    @Column(name = "cliente_id")
    private Integer clienteId;

    @Column(name = "sku_producto", nullable = false)
    private String skuProducto;

    @Column(name = "numero_serie")
    private String numeroSerie;

    @Column(name = "motivo_reclamo", nullable = false, columnDefinition = "TEXT")
    private String motivoReclamo;

    @Column(nullable = false, length = 50)
    private String estado = "ABIERTO";

    @Column(length = 50)
    private String resolucion;

    @Column(name = "notas_internas", columnDefinition = "TEXT")
    private String notasInternas;

    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "usuario_creacion", length = 50)
    private String usuarioCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @Column(name = "usuario_actualizacion", length = 50)
    private String usuarioActualizacion;

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<HistorialGarantia> historial = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getVentaId() { return ventaId; }
    public void setVentaId(Integer ventaId) { this.ventaId = ventaId; }
    public Integer getClienteId() { return clienteId; }
    public void setClienteId(Integer clienteId) { this.clienteId = clienteId; }
    public String getSkuProducto() { return skuProducto; }
    public void setSkuProducto(String skuProducto) { this.skuProducto = skuProducto; }
    public String getNumeroSerie() { return numeroSerie; }
    public void setNumeroSerie(String numeroSerie) { this.numeroSerie = numeroSerie; }
    public String getMotivoReclamo() { return motivoReclamo; }
    public void setMotivoReclamo(String motivoReclamo) { this.motivoReclamo = motivoReclamo; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getResolucion() { return resolucion; }
    public void setResolucion(String resolucion) { this.resolucion = resolucion; }
    public String getNotasInternas() { return notasInternas; }
    public void setNotasInternas(String notasInternas) { this.notasInternas = notasInternas; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public String getUsuarioCreacion() { return usuarioCreacion; }
    public void setUsuarioCreacion(String usuarioCreacion) { this.usuarioCreacion = usuarioCreacion; }
    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }
    public String getUsuarioActualizacion() { return usuarioActualizacion; }
    public void setUsuarioActualizacion(String usuarioActualizacion) { this.usuarioActualizacion = usuarioActualizacion; }
    public List<HistorialGarantia> getHistorial() { return historial; }
    public void setHistorial(List<HistorialGarantia> historial) { this.historial = historial; }
}
