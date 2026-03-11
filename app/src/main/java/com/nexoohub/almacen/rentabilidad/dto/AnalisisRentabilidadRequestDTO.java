package com.nexoohub.almacen.rentabilidad.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * DTO de request para análisis de rentabilidad en un período.
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
public class AnalisisRentabilidadRequestDTO {

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate fechaInicio;

    @NotNull(message = "La fecha de fin es obligatoria")
    private LocalDate fechaFin;

    public AnalisisRentabilidadRequestDTO() {}

    public AnalisisRentabilidadRequestDTO(LocalDate fechaInicio, LocalDate fechaFin) {
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }

    // ==========================================
    // GETTERS Y SETTERS
    // ==========================================

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }
}
