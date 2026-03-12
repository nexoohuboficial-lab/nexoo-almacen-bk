package com.nexoohub.almacen.metricas.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO de respuesta con métricas financieras consolidadas.
 * 
 * <p>Proporciona un dashboard financiero completo con todas las métricas
 * clave para toma de decisiones.</p>
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
public class MetricaFinancieraResponseDTO {

    private Long id;
    private Integer sucursalId;
    private String nombreSucursal;
    private LocalDate periodoInicio;
    private LocalDate periodoFin;
    private String tipoPeriodo;

    // Métricas de Ventas
    private BigDecimal ventasTotales;
    private Integer numeroVentas;
    private BigDecimal ticketPromedio;
    private Integer clientesUnicos;

    // Métricas de Costos y Utilidad
    private BigDecimal costoVentas;
    private BigDecimal utilidadBruta;
    private BigDecimal margenBrutoPorcentaje;
    private BigDecimal gastosOperativos;
    private BigDecimal utilidadNeta;
    private BigDecimal margenNetoPorcentaje;

    // Métricas de Métodos de Pago
    private BigDecimal ventasEfectivo;
    private BigDecimal ventasCredito;
    private BigDecimal porcentajeEfectivo;
    private BigDecimal porcentajeCredito;

    // Clasificaciones
    private String clasificacionMargen;
    private String saludFinanciera;

    // Constructors

    public MetricaFinancieraResponseDTO() {
    }

    public MetricaFinancieraResponseDTO(
            Long id,
            Integer sucursalId,
            LocalDate periodoInicio,
            LocalDate periodoFin,
            String tipoPeriodo,
            BigDecimal ventasTotales,
            BigDecimal costoVentas,
            BigDecimal utilidadBruta,
            BigDecimal margenBrutoPorcentaje,
            BigDecimal gastosOperativos,
            BigDecimal utilidadNeta,
            BigDecimal margenNetoPorcentaje,
            Integer numeroVentas,
            BigDecimal ticketPromedio,
            Integer clientesUnicos,
            BigDecimal ventasEfectivo,
            BigDecimal ventasCredito
    ) {
        this.id = id;
        this.sucursalId = sucursalId;
        this.periodoInicio = periodoInicio;
        this.periodoFin = periodoFin;
        this.tipoPeriodo = tipoPeriodo;
        this.ventasTotales = ventasTotales;
        this.costoVentas = costoVentas;
        this.utilidadBruta = utilidadBruta;
        this.margenBrutoPorcentaje = margenBrutoPorcentaje;
        this.gastosOperativos = gastosOperativos;
        this.utilidadNeta = utilidadNeta;
        this.margenNetoPorcentaje = margenNetoPorcentaje;
        this.numeroVentas = numeroVentas;
        this.ticketPromedio = ticketPromedio;
        this.clientesUnicos = clientesUnicos;
        this.ventasEfectivo = ventasEfectivo;
        this.ventasCredito = ventasCredito;
    }

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getSucursalId() {
        return sucursalId;
    }

    public void setSucursalId(Integer sucursalId) {
        this.sucursalId = sucursalId;
    }

    public String getNombreSucursal() {
        return nombreSucursal;
    }

    public void setNombreSucursal(String nombreSucursal) {
        this.nombreSucursal = nombreSucursal;
    }

    public LocalDate getPeriodoInicio() {
        return periodoInicio;
    }

    public void setPeriodoInicio(LocalDate periodoInicio) {
        this.periodoInicio = periodoInicio;
    }

    public LocalDate getPeriodoFin() {
        return periodoFin;
    }

    public void setPeriodoFin(LocalDate periodoFin) {
        this.periodoFin = periodoFin;
    }

    public String getTipoPeriodo() {
        return tipoPeriodo;
    }

    public void setTipoPeriodo(String tipoPeriodo) {
        this.tipoPeriodo = tipoPeriodo;
    }

    public BigDecimal getVentasTotales() {
        return ventasTotales;
    }

    public void setVentasTotales(BigDecimal ventasTotales) {
        this.ventasTotales = ventasTotales;
    }

    public Integer getNumeroVentas() {
        return numeroVentas;
    }

    public void setNumeroVentas(Integer numeroVentas) {
        this.numeroVentas = numeroVentas;
    }

    public BigDecimal getTicketPromedio() {
        return ticketPromedio;
    }

    public void setTicketPromedio(BigDecimal ticketPromedio) {
        this.ticketPromedio = ticketPromedio;
    }

    public Integer getClientesUnicos() {
        return clientesUnicos;
    }

    public void setClientesUnicos(Integer clientesUnicos) {
        this.clientesUnicos = clientesUnicos;
    }

    public BigDecimal getCostoVentas() {
        return costoVentas;
    }

    public void setCostoVentas(BigDecimal costoVentas) {
        this.costoVentas = costoVentas;
    }

    public BigDecimal getUtilidadBruta() {
        return utilidadBruta;
    }

    public void setUtilidadBruta(BigDecimal utilidadBruta) {
        this.utilidadBruta = utilidadBruta;
    }

    public BigDecimal getMargenBrutoPorcentaje() {
        return margenBrutoPorcentaje;
    }

    public void setMargenBrutoPorcentaje(BigDecimal margenBrutoPorcentaje) {
        this.margenBrutoPorcentaje = margenBrutoPorcentaje;
    }

    public BigDecimal getGastosOperativos() {
        return gastosOperativos;
    }

    public void setGastosOperativos(BigDecimal gastosOperativos) {
        this.gastosOperativos = gastosOperativos;
    }

    public BigDecimal getUtilidadNeta() {
        return utilidadNeta;
    }

    public void setUtilidadNeta(BigDecimal utilidadNeta) {
        this.utilidadNeta = utilidadNeta;
    }

    public BigDecimal getMargenNetoPorcentaje() {
        return margenNetoPorcentaje;
    }

    public void setMargenNetoPorcentaje(BigDecimal margenNetoPorcentaje) {
        this.margenNetoPorcentaje = margenNetoPorcentaje;
    }

    public BigDecimal getVentasEfectivo() {
        return ventasEfectivo;
    }

    public void setVentasEfectivo(BigDecimal ventasEfectivo) {
        this.ventasEfectivo = ventasEfectivo;
    }

    public BigDecimal getVentasCredito() {
        return ventasCredito;
    }

    public void setVentasCredito(BigDecimal ventasCredito) {
        this.ventasCredito = ventasCredito;
    }

    public BigDecimal getPorcentajeEfectivo() {
        return porcentajeEfectivo;
    }

    public void setPorcentajeEfectivo(BigDecimal porcentajeEfectivo) {
        this.porcentajeEfectivo = porcentajeEfectivo;
    }

    public BigDecimal getPorcentajeCredito() {
        return porcentajeCredito;
    }

    public void setPorcentajeCredito(BigDecimal porcentajeCredito) {
        this.porcentajeCredito = porcentajeCredito;
    }

    public String getClasificacionMargen() {
        return clasificacionMargen;
    }

    public void setClasificacionMargen(String clasificacionMargen) {
        this.clasificacionMargen = clasificacionMargen;
    }

    public String getSaludFinanciera() {
        return saludFinanciera;
    }

    public void setSaludFinanciera(String saludFinanciera) {
        this.saludFinanciera = saludFinanciera;
    }
}
