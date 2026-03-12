package com.nexoohub.almacen.metricas.dto;

import java.math.BigDecimal;

/**
 * DTO para comparación de períodos (mes actual vs mes anterior, año vs año, etc.)
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
public class ComparacionPeriodosDTO {

    private MetricaFinancieraResponseDTO periodoActual;
    private MetricaFinancieraResponseDTO periodoAnterior;

    // Variaciones absolutas
    private BigDecimal variacionVentas;
    private BigDecimal variacionUtilidad;
    private BigDecimal variacionMargen;
    private BigDecimal variacionTicketPromedio;

    // Variaciones porcentuales (crecimiento)
    private BigDecimal crecimientoVentas; // %
    private BigDecimal crecimientoUtilidad; // %
    private BigDecimal crecimientoClientes; // %
    private BigDecimal crecimientoNumeroVentas; // %

    // Interpretaciones
    private String tendenciaVentas; // CRECIENDO, ESTABLE, DECRECIENDO
    private String tendenciaMargen; // MEJORANDO, ESTABLE, DETERIORANDO
    private String resumenComparativo;

    // Constructors

    public ComparacionPeriodosDTO() {
    }

    public ComparacionPeriodosDTO(
            MetricaFinancieraResponseDTO periodoActual,
            MetricaFinancieraResponseDTO periodoAnterior
    ) {
        this.periodoActual = periodoActual;
        this.periodoAnterior = periodoAnterior;
    }

    // Getters y Setters

    public MetricaFinancieraResponseDTO getPeriodoActual() {
        return periodoActual;
    }

    public void setPeriodoActual(MetricaFinancieraResponseDTO periodoActual) {
        this.periodoActual = periodoActual;
    }

    public MetricaFinancieraResponseDTO getPeriodoAnterior() {
        return periodoAnterior;
    }

    public void setPeriodoAnterior(MetricaFinancieraResponseDTO periodoAnterior) {
        this.periodoAnterior = periodoAnterior;
    }

    public BigDecimal getVariacionVentas() {
        return variacionVentas;
    }

    public void setVariacionVentas(BigDecimal variacionVentas) {
        this.variacionVentas = variacionVentas;
    }

    public BigDecimal getVariacionUtilidad() {
        return variacionUtilidad;
    }

    public void setVariacionUtilidad(BigDecimal variacionUtilidad) {
        this.variacionUtilidad = variacionUtilidad;
    }

    public BigDecimal getVariacionMargen() {
        return variacionMargen;
    }

    public void setVariacionMargen(BigDecimal variacionMargen) {
        this.variacionMargen = variacionMargen;
    }

    public BigDecimal getVariacionTicketPromedio() {
        return variacionTicketPromedio;
    }

    public void setVariacionTicketPromedio(BigDecimal variacionTicketPromedio) {
        this.variacionTicketPromedio = variacionTicketPromedio;
    }

    public BigDecimal getCrecimientoVentas() {
        return crecimientoVentas;
    }

    public void setCrecimientoVentas(BigDecimal crecimientoVentas) {
        this.crecimientoVentas = crecimientoVentas;
    }

    public BigDecimal getCrecimientoUtilidad() {
        return crecimientoUtilidad;
    }

    public void setCrecimientoUtilidad(BigDecimal crecimientoUtilidad) {
        this.crecimientoUtilidad = crecimientoUtilidad;
    }

    public BigDecimal getCrecimientoClientes() {
        return crecimientoClientes;
    }

    public void setCrecimientoClientes(BigDecimal crecimientoClientes) {
        this.crecimientoClientes = crecimientoClientes;
    }

    public BigDecimal getCrecimientoNumeroVentas() {
        return crecimientoNumeroVentas;
    }

    public void setCrecimientoNumeroVentas(BigDecimal crecimientoNumeroVentas) {
        this.crecimientoNumeroVentas = crecimientoNumeroVentas;
    }

    public String getTendenciaVentas() {
        return tendenciaVentas;
    }

    public void setTendenciaVentas(String tendenciaVentas) {
        this.tendenciaVentas = tendenciaVentas;
    }

    public String getTendenciaMargen() {
        return tendenciaMargen;
    }

    public void setTendenciaMargen(String tendenciaMargen) {
        this.tendenciaMargen = tendenciaMargen;
    }

    public String getResumenComparativo() {
        return resumenComparativo;
    }

    public void setResumenComparativo(String resumenComparativo) {
        this.resumenComparativo = resumenComparativo;
    }
}
