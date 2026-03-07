package com.nexoohub.almacen.sucursal.dto;

import java.time.LocalDateTime;

/**
 * DTO para respuestas de Sucursal.
 * 
 * <p>Evita exponer campos de auditoría (usuarioCreacion, usuarioActualizacion).</p>
 */
public record SucursalResponseDTO(
    Integer id,
    String nombre,
    String direccion,
    Boolean activo,
    LocalDateTime fechaCreacion,
    LocalDateTime fechaActualizacion
) {}
