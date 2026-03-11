package com.nexoohub.almacen.fidelidad.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para respuesta de programa de fidelidad.
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
public record ProgramaFidelidadResponseDTO(
        Integer id,
        Integer clienteId,
        String clienteNombre,
        Integer puntosAcumulados,
        BigDecimal totalCompras,
        BigDecimal totalCanjeado,
        Boolean activo,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaActualizacion
) {}
