package com.nexoohub.almacen.pos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class VentaOfflineSyncDTO {

    @NotBlank(message = "La referencia local de venta es obligatoria")
    private String referenciaLocal;

    @NotNull(message = "El monto total de la venta es obligatorio")
    private BigDecimal montoTotal;

    @NotBlank(message = "El método de pago es obligatorio (EFECTIVO, TARJETA, TRANSFERENCIA)")
    private String metodoDePago;

    @NotNull(message = "La fecha de cobro local es obligatoria")
    private LocalDateTime fechaCobroOffline;

    // Getters y Setters
    public String getReferenciaLocal() { return referenciaLocal; }
    public void setReferenciaLocal(String referenciaLocal) { this.referenciaLocal = referenciaLocal; }
    public BigDecimal getMontoTotal() { return montoTotal; }
    public void setMontoTotal(BigDecimal montoTotal) { this.montoTotal = montoTotal; }
    public String getMetodoDePago() { return metodoDePago; }
    public void setMetodoDePago(String metodoDePago) { this.metodoDePago = metodoDePago; }
    public LocalDateTime getFechaCobroOffline() { return fechaCobroOffline; }
    public void setFechaCobroOffline(LocalDateTime fechaCobroOffline) { this.fechaCobroOffline = fechaCobroOffline; }
}
