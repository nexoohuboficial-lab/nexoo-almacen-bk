package com.nexoohub.almacen.fidelidad.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/**
 * DTO para solicitud de acumulación de puntos.
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
public record AcumularPuntosRequestDTO(
        @NotNull(message = "El ID del cliente es obligatorio")
        Integer clienteId,

        @NotNull(message = "El monto de compra es obligatorio")
        @Positive(message = "El monto debe ser positivo")
        BigDecimal montoCompra,

        Integer ventaId,

        String descripcion
) {}
