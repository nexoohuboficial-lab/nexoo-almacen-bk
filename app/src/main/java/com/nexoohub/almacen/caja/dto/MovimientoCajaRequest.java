package com.nexoohub.almacen.caja.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/** DTO para registrar un movimiento en un turno activo (POST /api/v1/cajas/movimientos) */
public class MovimientoCajaRequest {

    @NotNull(message = "El ID del turno es obligatorio")
    private Integer turnoId;

    /**
     * RETIRO | INGRESO_EXTRA | VENTA_EFECTIVO | VENTA_TARJETA | VENTA_CREDITO
     */
    @NotBlank(message = "El tipo de movimiento es obligatorio")
    private String tipo;

    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor que cero")
    private BigDecimal monto;

    private String concepto;

    /** ID de venta u otro identificador externo */
    private String referencia;

    // Getters y Setters
    public Integer getTurnoId() { return turnoId; }
    public void setTurnoId(Integer turnoId) { this.turnoId = turnoId; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }

    public String getConcepto() { return concepto; }
    public void setConcepto(String concepto) { this.concepto = concepto; }

    public String getReferencia() { return referencia; }
    public void setReferencia(String referencia) { this.referencia = referencia; }
}
