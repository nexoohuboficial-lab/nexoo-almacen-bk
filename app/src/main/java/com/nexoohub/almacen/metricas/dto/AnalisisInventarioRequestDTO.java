package com.nexoohub.almacen.metricas.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * DTO de solicitud para generar análisis de inventario.
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
public class AnalisisInventarioRequestDTO {

    @NotNull(message = "La fecha de corte es obligatoria")
    private LocalDate fechaCorte;

    private Integer sucursalId; // NULL = consolidado

    @NotNull(message = "El indicador de guardado es obligatorio")
    private Boolean guardarSnapshot = true;

    private Integer diasPeriodoRotacion = 30; // Período para calcular rotación (default 30 días)

    // Constructor vacío
    public AnalisisInventarioRequestDTO() {}

    // Constructor completo
    public AnalisisInventarioRequestDTO(LocalDate fechaCorte, Integer sucursalId, Boolean guardarSnapshot, Integer diasPeriodoRotacion) {
        this.fechaCorte = fechaCorte;
        this.sucursalId = sucursalId;
        this.guardarSnapshot = guardarSnapshot;
        this.diasPeriodoRotacion = diasPeriodoRotacion;
    }

    // Getters y Setters
    public LocalDate getFechaCorte() {
        return fechaCorte;
    }

    public void setFechaCorte(LocalDate fechaCorte) {
        this.fechaCorte = fechaCorte;
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

    public Integer getDiasPeriodoRotacion() {
        return diasPeriodoRotacion;
    }

    public void setDiasPeriodoRotacion(Integer diasPeriodoRotacion) {
        this.diasPeriodoRotacion = diasPeriodoRotacion;
    }
}
