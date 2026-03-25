package com.nexoohub.almacen.crm.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class OportunidadVentaResponse {

    private Integer id;
    private Integer prospectoId;
    private String titulo;
    private BigDecimal valorProyectado;
    private String etapa;
    private LocalDate fechaCierreEstimada;
    private Integer probabilidadPorcentaje;
    private LocalDateTime fechaCreacion;

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getProspectoId() { return prospectoId; }
    public void setProspectoId(Integer prospectoId) { this.prospectoId = prospectoId; }
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
}
