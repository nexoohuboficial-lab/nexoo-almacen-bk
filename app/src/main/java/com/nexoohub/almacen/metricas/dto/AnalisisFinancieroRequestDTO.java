package com.nexoohub.almacen.metricas.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * DTO para solicitar análisis financiero de un período específico.
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
public class AnalisisFinancieroRequestDTO {

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate fechaInicio;

    @NotNull(message = "La fecha de fin es obligatoria")
    private LocalDate fechaFin;

    /**
     * ID de sucursal (opcional). Si es null, devuelve métricas consolidadas.
     */
    private Integer sucursalId;

    /**
     * Si true, guarda el snapshot en la tabla metrica_financiera
     */
    private Boolean guardarSnapshot = true;

    // Constructors

    public AnalisisFinancieroRequestDTO() {
    }

    public AnalisisFinancieroRequestDTO(LocalDate fechaInicio, LocalDate fechaFin) {
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }

    // Getters y Setters

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public Integer getSucursalId() {
        return sucursalId;
    }

    public void setSucursalId(Integer sucursalId) {
        this.sucursalId = sucursalId;
    }

    public Boolean getGuardarSnapshot() {
        return guardarSnapshot;
    }

    public void setGuardarSnapshot(Boolean guardarSnapshot) {
        this.guardarSnapshot = guardarSnapshot;
    }
}
