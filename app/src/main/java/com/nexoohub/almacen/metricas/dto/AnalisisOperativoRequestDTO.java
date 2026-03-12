package com.nexoohub.almacen.metricas.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * DTO de solicitud para generar análisis de métricas operacionales.
 * 
 * <p>Permite configurar el período de análisis y las opciones de cálculo.</p>
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
public class AnalisisOperativoRequestDTO {

    /**
     * Fecha de inicio del período a analizar.
     */
    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate fechaInicio;

    /**
     * Fecha de fin del período a analizar (inclusiva).
     */
    @NotNull(message = "La fecha de fin es obligatoria")
    private LocalDate fechaFin;

    /**
     * ID de sucursal específica a analizar. 
     * Si es null, se genera análisis consolidado de todas las sucursales.
     */
    private Integer sucursalId;

    /**
     * Tipo de período para clasificación: DIARIO, SEMANAL, MENSUAL, TRIMESTRAL, ANUAL.
     */
    private String tipoPeriodo;

    /**
     * Si es true, incluye detalle de sucursales individuales (para análisis consolidado).
     */
    private Boolean incluirDetalleSucursales = false;

    /**
     * Si es true, incluye comparación con el período anterior.
     */
    private Boolean compararPeriodoAnterior = false;

    /**
     * Límite de sucursales top a retornar en análisis consolidado.
     */
    private Integer limitTopSucursales = 5;

    // ==========================================
    // CONSTRUCTOR
    // ==========================================

    public AnalisisOperativoRequestDTO() {
    }

    public AnalisisOperativoRequestDTO(LocalDate fechaInicio, LocalDate fechaFin, Integer sucursalId, String tipoPeriodo) {
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.sucursalId = sucursalId;
        this.tipoPeriodo = tipoPeriodo;
    }

    // ==========================================
    // GETTERS Y SETTERS
    // ==========================================

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

    public String getTipoPeriodo() {
        return tipoPeriodo;
    }

    public void setTipoPeriodo(String tipoPeriodo) {
        this.tipoPeriodo = tipoPeriodo;
    }

    public Boolean getIncluirDetalleSucursales() {
        return incluirDetalleSucursales;
    }

    public void setIncluirDetalleSucursales(Boolean incluirDetalleSucursales) {
        this.incluirDetalleSucursales = incluirDetalleSucursales;
    }

    public Boolean getCompararPeriodoAnterior() {
        return compararPeriodoAnterior;
    }

    public void setCompararPeriodoAnterior(Boolean compararPeriodoAnterior) {
        this.compararPeriodoAnterior = compararPeriodoAnterior;
    }

    public Integer getLimitTopSucursales() {
        return limitTopSucursales;
    }

    public void setLimitTopSucursales(Integer limitTopSucursales) {
        this.limitTopSucursales = limitTopSucursales;
    }
}
