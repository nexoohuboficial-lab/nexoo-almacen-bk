package com.nexoohub.almacen.finanzas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de respuesta con información de un movimiento de crédito.
 * 
 * @author NexooHub Development Team
 * @since 1.2.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistorialCreditoResponseDTO {

    /**
     * ID del movimiento.
     */
    private Integer id;

    /**
     * ID del cliente.
     */
    private Integer clienteId;

    /**
     * Nombre del cliente.
     */
    private String clienteNombre;

    /**
     * ID de la venta asociada (solo en CARGO).
     */
    private Integer ventaId;

    /**
     * Tipo de movimiento (CARGO, ABONO, AJUSTE, etc.).
     */
    private String tipoMovimiento;

    /**
     * Monto del movimiento.
     */
    private BigDecimal monto;

    /**
     * Saldo después del movimiento.
     */
    private BigDecimal saldoResultante;

    /**
     * Método de pago (solo en ABONO).
     */
    private String metodoPago;

    /**
     * Folio del comprobante (solo en ABONO).
     */
    private String folioComprobante;

    /**
     * Concepto del movimiento.
     */
    private String concepto;

    /**
     * Observaciones adicionales.
     */
    private String observaciones;

    /**
     * Fecha del movimiento.
     */
    private LocalDateTime fechaMovimiento;

    /**
     * Usuario que registró el movimiento.
     */
    private String usuarioRegistro;
}
