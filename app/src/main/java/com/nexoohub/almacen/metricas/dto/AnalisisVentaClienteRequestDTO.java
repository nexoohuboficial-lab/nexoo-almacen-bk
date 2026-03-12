package com.nexoohub.almacen.metricas.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

/**
 * DTO de entrada para solicitudes de análisis de ventas y clientes.
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
public class AnalisisVentaClienteRequestDTO {

    /**
     * Fecha de inicio del período a analizar.
     */
    @NotNull(message = "La fecha de inicio es obligatoria")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaInicio;

    /**
     * Fecha de fin del período a analizar.
     */
    @NotNull(message = "La fecha de fin es obligatoria")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaFin;

    /**
     * ID de sucursal (NULL = consolidado de todas las sucursales).
     */
    private Integer sucursalId;

    /**
     * Tipo de período: DIARIO, SEMANAL, MENSUAL, TRIMESTRAL, ANUAL.
     */
    private String tipoPeriodo = "MENSUAL";

    /**
     * Incluir comparación con período anterior.
     */
    private Boolean compararPeriodoAnterior = true;

    /**
     * Incluir detalle de vendedores.
     */
    private Boolean incluirDetalleVendedores = true;

    /**
     * Incluir detalle de clientes.
     */
    private Boolean incluirDetalleClientes = true;

    /**
     * Límite para top vendedores (default: 5).
     */
    private Integer limitTopVendedores = 5;

    /**
     * Límite para top clientes (default: 10).
     */
    private Integer limitTopClientes = 10;

    // ==================== GETTERS Y SETTERS ====================

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

    public Boolean getCompararPeriodoAnterior() {
        return compararPeriodoAnterior;
    }

    public void setCompararPeriodoAnterior(Boolean compararPeriodoAnterior) {
        this.compararPeriodoAnterior = compararPeriodoAnterior;
    }

    public Boolean getIncluirDetalleVendedores() {
        return incluirDetalleVendedores;
    }

    public void setIncluirDetalleVendedores(Boolean incluirDetalleVendedores) {
        this.incluirDetalleVendedores = incluirDetalleVendedores;
    }

    public Boolean getIncluirDetalleClientes() {
        return incluirDetalleClientes;
    }

    public void setIncluirDetalleClientes(Boolean incluirDetalleClientes) {
        this.incluirDetalleClientes = incluirDetalleClientes;
    }

    public Integer getLimitTopVendedores() {
        return limitTopVendedores;
    }

    public void setLimitTopVendedores(Integer limitTopVendedores) {
        this.limitTopVendedores = limitTopVendedores;
    }

    public Integer getLimitTopClientes() {
        return limitTopClientes;
    }

    public void setLimitTopClientes(Integer limitTopClientes) {
        this.limitTopClientes = limitTopClientes;
    }
}
