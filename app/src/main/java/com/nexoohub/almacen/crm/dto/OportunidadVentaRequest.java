package com.nexoohub.almacen.crm.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public class OportunidadVentaRequest {

    @NotNull(message = "El ID del prospecto es obligatorio")
    private Integer prospectoId;

    @NotBlank(message = "El título es obligatorio")
    @Size(max = 255)
    private String titulo;

    private BigDecimal valorProyectado;

    @Size(max = 50)
    private String etapa;

    private LocalDate fechaCierreEstimada;

    @Min(0)
    @Max(100)
    private Integer probabilidadPorcentaje;

    // Getters y Setters
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
}
