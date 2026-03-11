package com.nexoohub.almacen.fidelidad.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * DTO para solicitud de canje de puntos.
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
public record CanjearPuntosRequestDTO(
        @NotNull(message = "El ID del cliente es obligatorio")
        Integer clienteId,

        @NotNull(message = "Los puntos a canjear son obligatorios")
        @Positive(message = "Los puntos deben ser positivos")
        Integer puntosACanjear,

        Integer ventaId,

        String descripcion
) {}
