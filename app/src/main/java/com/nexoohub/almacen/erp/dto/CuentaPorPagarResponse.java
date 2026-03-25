package com.nexoohub.almacen.erp.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CuentaPorPagarResponse {
    private Integer id;
    private Integer proveedorId;
    private String numeroFactura;
    private String descripcion;
    private BigDecimal montoTotal;
    private BigDecimal montoPagado;
    private BigDecimal saldoPendiente;
    private LocalDate fechaFactura;
    private LocalDate fechaVencimiento;
    private String estatus;
    private Long diasAntiguedad;

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getProveedorId() { return proveedorId; }
    public void setProveedorId(Integer proveedorId) { this.proveedorId = proveedorId; }
    public String getNumeroFactura() { return numeroFactura; }
    public void setNumeroFactura(String numeroFactura) { this.numeroFactura = numeroFactura; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public BigDecimal getMontoTotal() { return montoTotal; }
    public void setMontoTotal(BigDecimal montoTotal) { this.montoTotal = montoTotal; }
    public BigDecimal getMontoPagado() { return montoPagado; }
    public void setMontoPagado(BigDecimal montoPagado) { this.montoPagado = montoPagado; }
    public BigDecimal getSaldoPendiente() { return saldoPendiente; }
    public void setSaldoPendiente(BigDecimal saldoPendiente) { this.saldoPendiente = saldoPendiente; }
    public LocalDate getFechaFactura() { return fechaFactura; }
    public void setFechaFactura(LocalDate fechaFactura) { this.fechaFactura = fechaFactura; }
    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }
    public String getEstatus() { return estatus; }
    public void setEstatus(String estatus) { this.estatus = estatus; }
    public Long getDiasAntiguedad() { return diasAntiguedad; }
    public void setDiasAntiguedad(Long diasAntiguedad) { this.diasAntiguedad = diasAntiguedad; }
}
