package com.nexoohub.almacen.caja.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/** DTO de respuesta con el resumen completo de un turno de caja (GET /api/v1/cajas/{id}/resumen) */
public class ResumenTurnoResponse {

    private Integer turnoId;
    private Integer sucursalId;
    private Integer empleadoId;
    private String estado;
    private LocalDateTime fechaApertura;
    private LocalDateTime fechaCierre;

    // Totales del turno
    private BigDecimal fondoInicial;
    private BigDecimal totalVentasEfectivo;
    private BigDecimal totalVentasTarjeta;
    private BigDecimal totalVentasCredito;
    private BigDecimal totalRetiros;
    private BigDecimal totalIngresosExtra;

    // Arqueo Z
    private BigDecimal efectivoEsperado;
    private BigDecimal efectivoReal;
    private BigDecimal diferencia;

    private String estadoArqueo; // OK | SOBRANTE | FALTANTE

    private String observaciones;
    private List<MovimientoResumen> movimientos;

    // ---- Inner DTO para lista de movimientos ----
    public static class MovimientoResumen {
        private Integer id;
        private String tipo;
        private BigDecimal monto;
        private String concepto;
        private String referencia;
        private LocalDateTime fechaMovimiento;

        // Getters y Setters
        public Integer getId() { return id; }
        public void setId(Integer id) { this.id = id; }
        public String getTipo() { return tipo; }
        public void setTipo(String tipo) { this.tipo = tipo; }
        public BigDecimal getMonto() { return monto; }
        public void setMonto(BigDecimal monto) { this.monto = monto; }
        public String getConcepto() { return concepto; }
        public void setConcepto(String concepto) { this.concepto = concepto; }
        public String getReferencia() { return referencia; }
        public void setReferencia(String referencia) { this.referencia = referencia; }
        public LocalDateTime getFechaMovimiento() { return fechaMovimiento; }
        public void setFechaMovimiento(LocalDateTime fechaMovimiento) { this.fechaMovimiento = fechaMovimiento; }
    }

    // Getters y Setters principales
    public Integer getTurnoId() { return turnoId; }
    public void setTurnoId(Integer turnoId) { this.turnoId = turnoId; }
    public Integer getSucursalId() { return sucursalId; }
    public void setSucursalId(Integer sucursalId) { this.sucursalId = sucursalId; }
    public Integer getEmpleadoId() { return empleadoId; }
    public void setEmpleadoId(Integer empleadoId) { this.empleadoId = empleadoId; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public LocalDateTime getFechaApertura() { return fechaApertura; }
    public void setFechaApertura(LocalDateTime fechaApertura) { this.fechaApertura = fechaApertura; }
    public LocalDateTime getFechaCierre() { return fechaCierre; }
    public void setFechaCierre(LocalDateTime fechaCierre) { this.fechaCierre = fechaCierre; }
    public BigDecimal getFondoInicial() { return fondoInicial; }
    public void setFondoInicial(BigDecimal fondoInicial) { this.fondoInicial = fondoInicial; }
    public BigDecimal getTotalVentasEfectivo() { return totalVentasEfectivo; }
    public void setTotalVentasEfectivo(BigDecimal totalVentasEfectivo) { this.totalVentasEfectivo = totalVentasEfectivo; }
    public BigDecimal getTotalVentasTarjeta() { return totalVentasTarjeta; }
    public void setTotalVentasTarjeta(BigDecimal totalVentasTarjeta) { this.totalVentasTarjeta = totalVentasTarjeta; }
    public BigDecimal getTotalVentasCredito() { return totalVentasCredito; }
    public void setTotalVentasCredito(BigDecimal totalVentasCredito) { this.totalVentasCredito = totalVentasCredito; }
    public BigDecimal getTotalRetiros() { return totalRetiros; }
    public void setTotalRetiros(BigDecimal totalRetiros) { this.totalRetiros = totalRetiros; }
    public BigDecimal getTotalIngresosExtra() { return totalIngresosExtra; }
    public void setTotalIngresosExtra(BigDecimal totalIngresosExtra) { this.totalIngresosExtra = totalIngresosExtra; }
    public BigDecimal getEfectivoEsperado() { return efectivoEsperado; }
    public void setEfectivoEsperado(BigDecimal efectivoEsperado) { this.efectivoEsperado = efectivoEsperado; }
    public BigDecimal getEfectivoReal() { return efectivoReal; }
    public void setEfectivoReal(BigDecimal efectivoReal) { this.efectivoReal = efectivoReal; }
    public BigDecimal getDiferencia() { return diferencia; }
    public void setDiferencia(BigDecimal diferencia) { this.diferencia = diferencia; }
    public String getEstadoArqueo() { return estadoArqueo; }
    public void setEstadoArqueo(String estadoArqueo) { this.estadoArqueo = estadoArqueo; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public List<MovimientoResumen> getMovimientos() { return movimientos; }
    public void setMovimientos(List<MovimientoResumen> movimientos) { this.movimientos = movimientos; }
}
