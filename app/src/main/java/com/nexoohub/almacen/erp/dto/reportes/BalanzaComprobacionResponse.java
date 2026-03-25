package com.nexoohub.almacen.erp.dto.reportes;

import java.math.BigDecimal;
import java.util.List;

public class BalanzaComprobacionResponse {
    private String periodo;
    private BigDecimal totalCargos;
    private BigDecimal totalAbonos;
    private List<CuentaBalanzaDTO> cuentas;
    private boolean cuadrada; // Si cargos == abonos

    // Getters y Setters
    public String getPeriodo() { return periodo; }
    public void setPeriodo(String periodo) { this.periodo = periodo; }
    public BigDecimal getTotalCargos() { return totalCargos; }
    public void setTotalCargos(BigDecimal totalCargos) { this.totalCargos = totalCargos; }
    public BigDecimal getTotalAbonos() { return totalAbonos; }
    public void setTotalAbonos(BigDecimal totalAbonos) { this.totalAbonos = totalAbonos; }
    public List<CuentaBalanzaDTO> getCuentas() { return cuentas; }
    public void setCuentas(List<CuentaBalanzaDTO> cuentas) { this.cuentas = cuentas; }
    public boolean isCuadrada() { return cuadrada; }
    public void setCuadrada(boolean cuadrada) { this.cuadrada = cuadrada; }
}
