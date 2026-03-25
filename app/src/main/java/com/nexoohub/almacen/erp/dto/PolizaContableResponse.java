package com.nexoohub.almacen.erp.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class PolizaContableResponse {
    private Integer id;
    private String numeroPoliza;
    private LocalDate fecha;
    private String tipoPoliza;
    private String concepto;
    private BigDecimal totalCargo;
    private BigDecimal totalAbono;
    private String estatus;
    private List<MovimientoContableDTO> movimientos;

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNumeroPoliza() { return numeroPoliza; }
    public void setNumeroPoliza(String numeroPoliza) { this.numeroPoliza = numeroPoliza; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public String getTipoPoliza() { return tipoPoliza; }
    public void setTipoPoliza(String tipoPoliza) { this.tipoPoliza = tipoPoliza; }
    public String getConcepto() { return concepto; }
    public void setConcepto(String concepto) { this.concepto = concepto; }
    public BigDecimal getTotalCargo() { return totalCargo; }
    public void setTotalCargo(BigDecimal totalCargo) { this.totalCargo = totalCargo; }
    public BigDecimal getTotalAbono() { return totalAbono; }
    public void setTotalAbono(BigDecimal totalAbono) { this.totalAbono = totalAbono; }
    public String getEstatus() { return estatus; }
    public void setEstatus(String estatus) { this.estatus = estatus; }
    public List<MovimientoContableDTO> getMovimientos() { return movimientos; }
    public void setMovimientos(List<MovimientoContableDTO> movimientos) { this.movimientos = movimientos; }
}
