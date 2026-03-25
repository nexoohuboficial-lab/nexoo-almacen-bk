package com.nexoohub.almacen.crm.dto;

import java.time.LocalDateTime;

public class CampanaMarketingResponse {
    private Integer id;
    private String nombre;
    private String segmentoObjetivo;
    private String canal;
    private String estado;
    private String contenidoPlantilla;
    private LocalDateTime fechaProgramada;
    private LocalDateTime fechaEjecucion;
    private Integer totalDestinatarios;
    private Integer creadoPorUsuarioId;
    private LocalDateTime fechaCreacion;

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
}
