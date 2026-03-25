package com.nexoohub.almacen.crm.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "campana_marketing")
public class CampanaMarketing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(name = "segmento_objetivo", nullable = false, length = 100)
    private String segmentoObjetivo;

    @Column(nullable = false, length = 20)
    private String canal; // EMAIL, SMS, WHATSAPP

    @Column(nullable = false, length = 20)
    private String estado; // BORRADOR, EN_PROGRESO, FINALIZADA

    @Column(name = "contenido_plantilla", nullable = false, columnDefinition = "TEXT")
    private String contenidoPlantilla;

    @Column(name = "fecha_programada")
    private LocalDateTime fechaProgramada;

    @Column(name = "fecha_ejecucion")
    private LocalDateTime fechaEjecucion;

    @Column(name = "total_destinatarios")
    private Integer totalDestinatarios = 0;

    @Column(name = "creado_por_usuario_id", nullable = false)
    private Integer creadoPorUsuarioId;

    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "usuario_creacion", length = 50)
    private String usuarioCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @Column(name = "usuario_actualizacion", length = 50)
    private String usuarioActualizacion;

    @OneToMany(mappedBy = "campana", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LogEnvioMensaje> logsEnvio = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        this.fechaCreacion = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getSegmentoObjetivo() {
        return segmentoObjetivo;
    }

    public void setSegmentoObjetivo(String segmentoObjetivo) {
        this.segmentoObjetivo = segmentoObjetivo;
    }

    public String getCanal() {
        return canal;
    }

    public void setCanal(String canal) {
        this.canal = canal;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getContenidoPlantilla() {
        return contenidoPlantilla;
    }

    public void setContenidoPlantilla(String contenidoPlantilla) {
        this.contenidoPlantilla = contenidoPlantilla;
    }

    public LocalDateTime getFechaProgramada() {
        return fechaProgramada;
    }

    public void setFechaProgramada(LocalDateTime fechaProgramada) {
        this.fechaProgramada = fechaProgramada;
    }

    public LocalDateTime getFechaEjecucion() {
        return fechaEjecucion;
    }

    public void setFechaEjecucion(LocalDateTime fechaEjecucion) {
        this.fechaEjecucion = fechaEjecucion;
    }

    public Integer getTotalDestinatarios() {
        return totalDestinatarios;
    }

    public void setTotalDestinatarios(Integer totalDestinatarios) {
        this.totalDestinatarios = totalDestinatarios;
    }

    public Integer getCreadoPorUsuarioId() {
        return creadoPorUsuarioId;
    }

    public void setCreadoPorUsuarioId(Integer creadoPorUsuarioId) {
        this.creadoPorUsuarioId = creadoPorUsuarioId;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getUsuarioCreacion() {
        return usuarioCreacion;
    }

    public void setUsuarioCreacion(String usuarioCreacion) {
        this.usuarioCreacion = usuarioCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public String getUsuarioActualizacion() {
        return usuarioActualizacion;
    }

    public void setUsuarioActualizacion(String usuarioActualizacion) {
        this.usuarioActualizacion = usuarioActualizacion;
    }

    public List<LogEnvioMensaje> getLogsEnvio() {
        return logsEnvio;
    }

    public void setLogsEnvio(List<LogEnvioMensaje> logsEnvio) {
        this.logsEnvio = logsEnvio;
    }
}
