package com.nexoohub.almacen.crm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class CampanaMarketingRequest {

    @NotBlank(message = "El nombre de la campaña es obligatorio")
    private String nombre;

    @NotBlank(message = "El segmento objetivo es obligatorio")
    private String segmentoObjetivo;

    @NotBlank(message = "El canal (EMAIL, SMS, WHATSAPP) es obligatorio")
    private String canal;

    @NotBlank(message = "El contenido o plantilla es obligatorio")
    private String contenidoPlantilla;

    private LocalDateTime fechaProgramada;

    @NotNull(message = "El ID del usuario creador es obligatorio")
    private Integer creadoPorUsuarioId;

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

    public Integer getCreadoPorUsuarioId() {
        return creadoPorUsuarioId;
    }

    public void setCreadoPorUsuarioId(Integer creadoPorUsuarioId) {
        this.creadoPorUsuarioId = creadoPorUsuarioId;
    }
}
