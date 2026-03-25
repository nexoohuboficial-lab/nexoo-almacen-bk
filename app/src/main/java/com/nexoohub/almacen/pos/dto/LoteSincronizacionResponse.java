package com.nexoohub.almacen.pos.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class LoteSincronizacionResponse {

    private String codigoLote;
    private String estatus;
    private Integer totalVentas;
    private Integer ventasProcesadas;
    private BigDecimal montoTotalLote;
    private LocalDateTime fechaSincronizacion;
    private Integer intentos;
    private String erroresDetalle;

    // Getters y Setters
    public String getCodigoLote() { return codigoLote; }
    public void setCodigoLote(String codigoLote) { this.codigoLote = codigoLote; }
    public String getEstatus() { return estatus; }
    public void setEstatus(String estatus) { this.estatus = estatus; }
    public Integer getTotalVentas() { return totalVentas; }
    public void setTotalVentas(Integer totalVentas) { this.totalVentas = totalVentas; }
    public Integer getVentasProcesadas() { return ventasProcesadas; }
    public void setVentasProcesadas(Integer ventasProcesadas) { this.ventasProcesadas = ventasProcesadas; }
    public BigDecimal getMontoTotalLote() { return montoTotalLote; }
    public void setMontoTotalLote(BigDecimal montoTotalLote) { this.montoTotalLote = montoTotalLote; }
    public LocalDateTime getFechaSincronizacion() { return fechaSincronizacion; }
    public void setFechaSincronizacion(LocalDateTime fechaSincronizacion) { this.fechaSincronizacion = fechaSincronizacion; }
    public Integer getIntentos() { return intentos; }
    public void setIntentos(Integer intentos) { this.intentos = intentos; }
    public String getErroresDetalle() { return erroresDetalle; }
    public void setErroresDetalle(String erroresDetalle) { this.erroresDetalle = erroresDetalle; }
}
