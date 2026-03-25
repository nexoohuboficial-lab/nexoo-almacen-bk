package com.nexoohub.almacen.erp.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ruta_entrega")
public class RutaEntrega {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "codigo_ruta", nullable = false, unique = true, length = 50)
    private String codigoRuta;

    @Column(name = "fecha_programada", nullable = false)
    private LocalDate fechaProgramada;

    @Column(name = "chofer_id")
    private Integer choferId;

    @Column(name = "vehiculo_id")
    private Integer vehiculoId;

    @Column(name = "es_paqueteria", nullable = false)
    private Boolean esPaqueteria = false;

    @Column(name = "proveedor_envio", length = 50)
    private String proveedorEnvio;

    /** PENDIENTE, EN_TRANSITO, COMPLETADA, CANCELADA */
    @Column(nullable = false, length = 20)
    private String estatus = "PENDIENTE";

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "usuario_id", nullable = false)
    private Integer usuarioId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "ruta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RutaFactura> facturas = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public void addFactura(RutaFactura rf) {
        facturas.add(rf);
        rf.setRuta(this);
    }

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getCodigoRuta() { return codigoRuta; }
    public void setCodigoRuta(String codigoRuta) { this.codigoRuta = codigoRuta; }
    public LocalDate getFechaProgramada() { return fechaProgramada; }
    public void setFechaProgramada(LocalDate fechaProgramada) { this.fechaProgramada = fechaProgramada; }
    public Integer getChoferId() { return choferId; }
    public void setChoferId(Integer choferId) { this.choferId = choferId; }
    public Integer getVehiculoId() { return vehiculoId; }
    public void setVehiculoId(Integer vehiculoId) { this.vehiculoId = vehiculoId; }
    public Boolean getEsPaqueteria() { return esPaqueteria; }
    public void setEsPaqueteria(Boolean esPaqueteria) { this.esPaqueteria = esPaqueteria; }
    public String getProveedorEnvio() { return proveedorEnvio; }
    public void setProveedorEnvio(String proveedorEnvio) { this.proveedorEnvio = proveedorEnvio; }
    public String getEstatus() { return estatus; }
    public void setEstatus(String estatus) { this.estatus = estatus; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public List<RutaFactura> getFacturas() { return facturas; }
}
