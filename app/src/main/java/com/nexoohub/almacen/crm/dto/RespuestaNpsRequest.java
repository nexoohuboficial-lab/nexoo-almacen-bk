package com.nexoohub.almacen.crm.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class RespuestaNpsRequest {

    @NotBlank(message = "El enlace único es obligatorio")
    private String enlaceUnico;

    @NotNull(message = "El puntaje es obligatorio")
    @Min(value = 0, message = "El puntaje mínimo es 0")
    @Max(value = 10, message = "El puntaje máximo es 10")
    private Integer score;

    private String comentarios;

    // Getters y Setters
    public String getEnlaceUnico() { return enlaceUnico; }
    public void setEnlaceUnico(String enlaceUnico) { this.enlaceUnico = enlaceUnico; }
    
    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }
    
    public String getComentarios() { return comentarios; }
    public void setComentarios(String comentarios) { this.comentarios = comentarios; }
}
