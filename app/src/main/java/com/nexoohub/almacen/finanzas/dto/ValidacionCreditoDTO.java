package com.nexoohub.almacen.finanzas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para validar disponibilidad de crédito.
 * 
 * @author NexooHub Development Team
 * @since 1.2.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidacionCreditoDTO {

    /**
     * Indica si el cliente tiene crédito disponible.
     */
    private Boolean creditoDisponible;

    /**
     * Monto de crédito disponible.
     */
    private BigDecimal montoDisponible;

    /**
     * Límite autorizado del cliente.
     */
    private BigDecimal limiteAutorizado;

    /**
     * Saldo actualmente utilizado.
     */
    private BigDecimal saldoUtilizado;

    /**
     * Monto solicitado para validar.
     */
    private BigDecimal montoSolicitado;

    /**
     * Estado del crédito (ACTIVO, BLOQUEADO, etc.).
     */
    private String estado;

    /**
     * Mensaje descriptivo del resultado de validación.
     */
    private String mensaje;

    /**
     * Código de resultado: OK, LIMITE_EXCEDIDO, BLOQUEADO, SIN_CREDITO
     */
    private String codigo;
}
