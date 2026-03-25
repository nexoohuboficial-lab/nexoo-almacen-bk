package com.nexoohub.almacen.crm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CambioEtapaOportunidadRequest {

    @NotBlank(message = "La etapa es obligatoria")
    @Size(max = 50)
    private String etapa;

    private Integer probabilidadPorcentaje;

    // Getters y Setters
    public String getEtapa() { return etapa; }
    public void setEtapa(String etapa) { this.etapa = etapa; }
    public Integer getProbabilidadPorcentaje() { return probabilidadPorcentaje; }
    public void setProbabilidadPorcentaje(Integer probabilidadPorcentaje) { this.probabilidadPorcentaje = probabilidadPorcentaje; }
}
