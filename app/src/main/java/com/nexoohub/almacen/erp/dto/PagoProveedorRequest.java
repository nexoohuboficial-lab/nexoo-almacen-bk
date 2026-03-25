package com.nexoohub.almacen.erp.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public class PagoProveedorRequest {

    @NotNull(message = "El monto del abono es requerido")
    @DecimalMin(value = "0.01", message = "El abono debe ser mayor a 0")
    private BigDecimal montoAbono;

    @NotBlank(message = "El método de pago es requerido (TRANSFERENCIA, CHEQUE, EFECTIVO)")
    private String metodoPago;

    private String referenciaPago;

    @NotNull(message = "La fecha de pago es requerida")
    private LocalDate fechaPago;

    private String observaciones;

    // Getters y Setters
    public BigDecimal getMontoAbono() { return montoAbono; }
    public void setMontoAbono(BigDecimal montoAbono) { this.montoAbono = montoAbono; }
    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }
    public String getReferenciaPago() { return referenciaPago; }
    public void setReferenciaPago(String referenciaPago) { this.referenciaPago = referenciaPago; }
    public LocalDate getFechaPago() { return fechaPago; }
    public void setFechaPago(LocalDate fechaPago) { this.fechaPago = fechaPago; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
}
