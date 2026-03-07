package com.nexoohub.almacen.catalogo.dto;

import java.time.LocalDateTime;

/**
 * DTO para respuestas de Cliente.
 * 
 * <p>Evita exponer campos de auditoría (createdBy, updatedBy) y 
 * datos sensibles innecesarios.</p>
 * 
 * <p>Denormaliza tipo de cliente para evitar N+1 queries en el frontend.</p>
 */
public record ClienteResponseDTO(
    Integer id,
    Integer tipoClienteId,
    String tipoClienteNombre,
    String nombre,
    String rfc,
    String telefono,
    String email,
    String direccionFiscal,
    LocalDateTime fechaCreacion,
    LocalDateTime fechaActualizacion
) {}
