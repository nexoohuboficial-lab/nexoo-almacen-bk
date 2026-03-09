package com.nexoohub.almacen.finanzas.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para crear o actualizar un límite de crédito.
 * 
 * @author NexooHub Development Team
 * @since 1.2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LimiteCreditoRequestDTO {

    /**
     * ID del cliente (obligatorio al crear).
     */
    @NotNull(message = "El cliente es obligatorio")
    private Integer clienteId;

    /**
     * Monto máximo autorizado para crédito.
     */
    @NotNull(message = "El límite autorizado es obligatorio")
    @PositiveOrZero(message = "El límite debe ser mayor o igual a cero")
    private BigDecimal limiteAutorizado;

    /**
     * Plazo de pago en días (default: 30).
     */
    @Positive(message = "El plazo debe ser mayor a cero")
    private Integer plazoPagoDias = 30;

    /**
     * Número máximo de facturas vencidas antes de bloquear (default: 3).
     */
    @PositiveOrZero(message = "El máximo de facturas debe ser mayor o igual a cero")
    private Integer maxFacturasVencidas = 3;

    /**
     * Indica si permite sobregiro temporal (default: false).
     */
    private Boolean permiteSobregiro = false;

    /**
     * Monto máximo de sobregiro permitido (default: 0).
     */
    @PositiveOrZero(message = "El monto de sobregiro no puede ser negativo")
    private BigDecimal montoSobregiro = BigDecimal.ZERO;

    /**
     * Observaciones sobre la configuración del crédito.
     */
    private String observaciones;
}
