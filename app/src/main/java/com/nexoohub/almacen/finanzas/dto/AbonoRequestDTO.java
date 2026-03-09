package com.nexoohub.almacen.finanzas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para registrar un abono/pago de crédito.
 * 
 * @author NexooHub Development Team
 * @since 1.2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AbonoRequestDTO {

    /**
     * ID del cliente que realiza el pago.
     */
    @NotNull(message = "El cliente es obligatorio")
    private Integer clienteId;

    /**
     * Monto del pago.
     */
    @NotNull(message = "El monto es obligatorio")
    @Positive(message = "El monto debe ser mayor a cero")
    private BigDecimal monto;

    /**
     * Método de pago utilizado.
     * Ejemplos: EFECTIVO, TRANSFERENCIA, CHEQUE, TARJETA
     */
    @NotBlank(message = "El método de pago es obligatorio")
    private String metodoPago;

    /**
     * Folio del comprobante de pago.
     * Ejemplo: número de cheque, referencia de transferencia.
     */
    private String folioComprobante;

    /**
     * Concepto del pago.
     * Default: "Pago recibido"
     */
    private String concepto;

    /**
     * Observaciones adicionales.
     */
    private String observaciones;
}
