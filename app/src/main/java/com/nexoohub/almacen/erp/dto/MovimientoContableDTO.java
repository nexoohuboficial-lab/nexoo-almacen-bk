package com.nexoohub.almacen.erp.dto;

import java.math.BigDecimal;

public class MovimientoContableDTO {
    private Integer cuentaId;
    private String codigoCuenta;
    private String nombreCuenta;
    private String conceptoDetalle;
    private BigDecimal cargo;
    private BigDecimal abono;

    // Getters y Setters
    public Integer getCuentaId() { return cuentaId; }
    public void setCuentaId(Integer cuentaId) { this.cuentaId = cuentaId; }
    public String getCodigoCuenta() { return codigoCuenta; }
    public void setCodigoCuenta(String codigoCuenta) { this.codigoCuenta = codigoCuenta; }
    public String getNombreCuenta() { return nombreCuenta; }
    public void setNombreCuenta(String nombreCuenta) { this.nombreCuenta = nombreCuenta; }
    public String getConceptoDetalle() { return conceptoDetalle; }
    public void setConceptoDetalle(String conceptoDetalle) { this.conceptoDetalle = conceptoDetalle; }
    public BigDecimal getCargo() { return cargo; }
    public void setCargo(BigDecimal cargo) { this.cargo = cargo; }
    public BigDecimal getAbono() { return abono; }
    public void setAbono(BigDecimal abono) { this.abono = abono; }
}
