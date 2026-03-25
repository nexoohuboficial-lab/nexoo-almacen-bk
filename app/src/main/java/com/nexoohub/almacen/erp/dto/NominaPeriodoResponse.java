package com.nexoohub.almacen.erp.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class NominaPeriodoResponse {
    private Integer id;
    private String nombre;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String tipoPeriodo;
    private String estatus;
    private Integer usuarioId;
    private LocalDateTime createdAt;
    
    // El count de cuántos recibos generados tiene
    private Integer cantidadRecibosGenerados;

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }
    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }
    public String getTipoPeriodo() { return tipoPeriodo; }
    public void setTipoPeriodo(String tipoPeriodo) { this.tipoPeriodo = tipoPeriodo; }
    public String getEstatus() { return estatus; }
    public void setEstatus(String estatus) { this.estatus = estatus; }
    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public Integer getCantidadRecibosGenerados() { return cantidadRecibosGenerados; }
    public void setCantidadRecibosGenerados(Integer cantidadRecibosGenerados) { this.cantidadRecibosGenerados = cantidadRecibosGenerados; }
}
