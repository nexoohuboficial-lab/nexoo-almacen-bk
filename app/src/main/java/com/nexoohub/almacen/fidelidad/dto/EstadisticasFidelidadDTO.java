package com.nexoohub.almacen.fidelidad.dto;

/**
 * DTO para información de estadísticas del programa de fidelidad.
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
public record EstadisticasFidelidadDTO(
        Long totalProgramasActivos,
        Long totalPuntosEnSistema,
        Integer tasaConversionPuntos,
        Integer tasaConversionDescuento
) {}
