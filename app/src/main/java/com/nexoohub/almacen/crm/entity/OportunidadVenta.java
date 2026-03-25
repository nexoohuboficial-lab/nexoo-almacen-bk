package com.nexoohub.almacen.crm.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "oportunidad_venta")
public class OportunidadVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "prospecto_id", insertable = false, updatable = false)
    private Integer prospectoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prospecto_id")
    private Prospecto prospecto;

    @Column(name = "titulo", nullable = false, length = 255)
    private String titulo;

    @Column(name = "valor_proyectado", precision = 15, scale = 2)
    private BigDecimal valorProyectado = BigDecimal.ZERO;

    @Column(name = "etapa", nullable = false, length = 50)
    private String etapa = "PROSPECTO";

    @Column(name = "fecha_cierre_estimada")
    private LocalDate fechaCierreEstimada;

    @Column(name = "probabilidad_porcentaje")
    private Integer probabilidadPorcentaje = 0;

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
        if(this.etapa == null) this.etapa = "PROSPECTO";
        if(this.valorProyectado == null) this.valorProyectado = BigDecimal.ZERO;
        if(this.probabilidadPorcentaje == null) this.probabilidadPorcentaje = 0;
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
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public BigDecimal getValorProyectado() { return valorProyectado; }
    public void setValorProyectado(BigDecimal valorProyectado) { this.valorProyectado = valorProyectado; }
    public String getEtapa() { return etapa; }
    public void setEtapa(String etapa) { this.etapa = etapa; }
    public LocalDate getFechaCierreEstimada() { return fechaCierreEstimada; }
    public void setFechaCierreEstimada(LocalDate fechaCierreEstimada) { this.fechaCierreEstimada = fechaCierreEstimada; }
    public Integer getProbabilidadPorcentaje() { return probabilidadPorcentaje; }
    public void setProbabilidadPorcentaje(Integer probabilidadPorcentaje) { this.probabilidadPorcentaje = probabilidadPorcentaje; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public String getUsuarioCreacion() { return usuarioCreacion; }
    public void setUsuarioCreacion(String usuarioCreacion) { this.usuarioCreacion = usuarioCreacion; }
    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }
    public String getUsuarioActualizacion() { return usuarioActualizacion; }
    public void setUsuarioActualizacion(String usuarioActualizacion) { this.usuarioActualizacion = usuarioActualizacion; }
}
