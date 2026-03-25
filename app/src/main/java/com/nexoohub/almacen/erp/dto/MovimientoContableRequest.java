package com.nexoohub.almacen.erp.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class MovimientoContableRequest {

    @NotNull(message = "La cuenta contable es requerida")
    private Integer cuentaId;

    private String conceptoDetalle;

    @NotNull(message = "El cargo es requerido (puede ser 0)")
    @DecimalMin(value = "0.00", message = "No se permiten cargos negativos")
    private BigDecimal cargo;

    @NotNull(message = "El abono es requerido (puede ser 0)")
    @DecimalMin(value = "0.00", message = "No se permiten abonos negativos")
    private BigDecimal abono;

    // Getters y Setters
    public Integer getCuentaId() { return cuentaId; }
    public void setCuentaId(Integer cuentaId) { this.cuentaId = cuentaId; }
    public String getConceptoDetalle() { return conceptoDetalle; }
    public void setConceptoDetalle(String conceptoDetalle) { this.conceptoDetalle = conceptoDetalle; }
    public BigDecimal getCargo() { return cargo; }
    public void setCargo(BigDecimal cargo) { this.cargo = cargo; }
    public BigDecimal getAbono() { return abono; }
    public void setAbono(BigDecimal abono) { this.abono = abono; }
}
