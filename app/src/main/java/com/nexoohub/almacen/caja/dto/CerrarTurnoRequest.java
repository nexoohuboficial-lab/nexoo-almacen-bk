package com.nexoohub.almacen.caja.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/** DTO para cerrar turno y realizar el arqueo Z (POST /api/v1/cajas/{id}/cerrar) */
public class CerrarTurnoRequest {

    @NotNull(message = "El efectivo real contado es obligatorio")
    @DecimalMin(value = "0.00", message = "El efectivo real no puede ser negativo")
    private BigDecimal efectivoReal;

    private String observaciones;

    // Getters y Setters
    public BigDecimal getEfectivoReal() { return efectivoReal; }
    public void setEfectivoReal(BigDecimal efectivoReal) { this.efectivoReal = efectivoReal; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
}
