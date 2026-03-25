package com.nexoohub.almacen.pos.dto;

import jakarta.validation.constraints.NotBlank;

public class CancelacionPagoRequest {

    @NotBlank(message = "La referencia de la venta original es obligatoria")
    private String referenciaVenta;

    @NotBlank(message = "El ID de la terminal es obligatorio")
    private String terminalId;

    public String getReferenciaVenta() { return referenciaVenta; }
    public void setReferenciaVenta(String referenciaVenta) { this.referenciaVenta = referenciaVenta; }

    public String getTerminalId() { return terminalId; }
    public void setTerminalId(String terminalId) { this.terminalId = terminalId; }
}
