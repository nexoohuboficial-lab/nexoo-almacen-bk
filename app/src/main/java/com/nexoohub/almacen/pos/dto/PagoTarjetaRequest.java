package com.nexoohub.almacen.pos.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class PagoTarjetaRequest {

    @NotBlank(message = "La referencia de la venta es obligatoria")
    private String referenciaVenta;

    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor a cero")
    private BigDecimal monto;

    @NotBlank(message = "El ID de la terminal es obligatorio")
    private String terminalId;

    public String getReferenciaVenta() { return referenciaVenta; }
    public void setReferenciaVenta(String referenciaVenta) { this.referenciaVenta = referenciaVenta; }

    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }

    public String getTerminalId() { return terminalId; }
    public void setTerminalId(String terminalId) { this.terminalId = terminalId; }
}
