package com.nexoohub.almacen.catalogo.dto;

import java.time.LocalDateTime;

/**
 * DTO para respuestas de Moto.
 * 
 * <p>Evita exponer campos de auditoría.</p>
 */
public record MotoResponseDTO(
    Integer id,
    String marca,
    String modelo,
    Integer cilindrada,
    LocalDateTime fechaCreacion,
    LocalDateTime fechaActualizacion
) {}
