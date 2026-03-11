package com.nexoohub.almacen.fidelidad.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para respuesta de movimiento de puntos.
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
public record MovimientoPuntoResponseDTO(
        Integer id,
        Integer programaId,
        String tipoMovimiento,
        Integer puntos,
        BigDecimal montoAsociado,
        Integer ventaId,
        String descripcion,
        LocalDateTime fechaCreacion
) {}
