package com.nexoohub.almacen.erp.dto.reportes;

import java.math.BigDecimal;

public class CuentaBalanzaDTO {
    private String codigo;
    private String nombre;
    private String naturaleza;
    private BigDecimal movimientosCargo;
    private BigDecimal movimientosAbono;
    private BigDecimal saldoFinal;

    // Getters y Setters
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getNaturaleza() { return naturaleza; }
    public void setNaturaleza(String naturaleza) { this.naturaleza = naturaleza; }
    public BigDecimal getMovimientosCargo() { return movimientosCargo; }
    public void setMovimientosCargo(BigDecimal movimientosCargo) { this.movimientosCargo = movimientosCargo; }
    public BigDecimal getMovimientosAbono() { return movimientosAbono; }
    public void setMovimientosAbono(BigDecimal movimientosAbono) { this.movimientosAbono = movimientosAbono; }
    public BigDecimal getSaldoFinal() { return saldoFinal; }
    public void setSaldoFinal(BigDecimal saldoFinal) { this.saldoFinal = saldoFinal; }
}
