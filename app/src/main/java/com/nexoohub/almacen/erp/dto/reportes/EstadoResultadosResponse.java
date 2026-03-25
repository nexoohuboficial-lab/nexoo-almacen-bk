package com.nexoohub.almacen.erp.dto.reportes;

import java.math.BigDecimal;

public class EstadoResultadosResponse {
    private String periodo;
    private BigDecimal ingresosNetos;
    private BigDecimal costoVentas;
    private BigDecimal utilidadBruta; // ingresos - costos
    private BigDecimal gastosOperacion;
    private BigDecimal utilidadNeta; // utilidadBruta - gastos

    // Getters y Setters
    public String getPeriodo() { return periodo; }
    public void setPeriodo(String periodo) { this.periodo = periodo; }
    public BigDecimal getIngresosNetos() { return ingresosNetos; }
    public void setIngresosNetos(BigDecimal ingresosNetos) { this.ingresosNetos = ingresosNetos; }
    public BigDecimal getCostoVentas() { return costoVentas; }
    public void setCostoVentas(BigDecimal costoVentas) { this.costoVentas = costoVentas; }
    public BigDecimal getUtilidadBruta() { return utilidadBruta; }
    public void setUtilidadBruta(BigDecimal utilidadBruta) { this.utilidadBruta = utilidadBruta; }
    public BigDecimal getGastosOperacion() { return gastosOperacion; }
    public void setGastosOperacion(BigDecimal gastosOperacion) { this.gastosOperacion = gastosOperacion; }
    public BigDecimal getUtilidadNeta() { return utilidadNeta; }
    public void setUtilidadNeta(BigDecimal utilidadNeta) { this.utilidadNeta = utilidadNeta; }
}
