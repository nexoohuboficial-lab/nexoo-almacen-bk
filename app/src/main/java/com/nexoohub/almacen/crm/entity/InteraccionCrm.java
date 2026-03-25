package com.nexoohub.almacen.crm.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "interaccion_crm")
public class InteraccionCrm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "prospecto_id", insertable = false, updatable = false)
    private Integer prospectoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prospecto_id")
    private Prospecto prospecto;

    @Column(name = "oportunidad_id", insertable = false, updatable = false)
    private Integer oportunidadId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oportunidad_id")
    private OportunidadVenta oportunidadVenta;

    @Column(name = "tipo_interaccion", nullable = false, length = 50)
    private String tipoInteraccion;

    @Column(name = "resumen", nullable = false, length = 500)
    private String resumen;

    @Column(name = "detalles", columnDefinition = "TEXT")
    private String detalles;

    @Column(name = "fecha_interaccion", nullable = false)
    private LocalDateTime fechaInteraccion;

    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "usuario_creacion", length = 50)
    private String usuarioCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @Column(name = "usuario_actualizacion", length = 50)
    private String usuarioActualizacion;

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
        if(this.fechaInteraccion == null) this.fechaInteraccion = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getProspectoId() { return prospectoId; }
    public void setProspectoId(Integer prospectoId) { this.prospectoId = prospectoId; }
    public Prospecto getProspecto() { return prospecto; }
    public void setProspecto(Prospecto prospecto) { this.prospecto = prospecto; }
    public Integer getOportunidadId() { return oportunidadId; }
    public void setOportunidadId(Integer oportunidadId) { this.oportunidadId = oportunidadId; }
    public OportunidadVenta getOportunidadVenta() { return oportunidadVenta; }
    public void setOportunidadVenta(OportunidadVenta oportunidadVenta) { this.oportunidadVenta = oportunidadVenta; }
    public String getTipoInteraccion() { return tipoInteraccion; }
    public void setTipoInteraccion(String tipoInteraccion) { this.tipoInteraccion = tipoInteraccion; }
    public String getResumen() { return resumen; }
    public void setResumen(String resumen) { this.resumen = resumen; }
    public String getDetalles() { return detalles; }
    public void setDetalles(String detalles) { this.detalles = detalles; }
    public LocalDateTime getFechaInteraccion() { return fechaInteraccion; }
    public void setFechaInteraccion(LocalDateTime fechaInteraccion) { this.fechaInteraccion = fechaInteraccion; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public String getUsuarioCreacion() { return usuarioCreacion; }
    public void setUsuarioCreacion(String usuarioCreacion) { this.usuarioCreacion = usuarioCreacion; }
    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }
    public String getUsuarioActualizacion() { return usuarioActualizacion; }
    public void setUsuarioActualizacion(String usuarioActualizacion) { this.usuarioActualizacion = usuarioActualizacion; }
}
