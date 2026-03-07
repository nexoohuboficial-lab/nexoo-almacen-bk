package com.nexoohub.almacen.catalogo.dto;

import java.time.LocalDateTime;

/**
 * DTO para respuestas de Categoria.
 * 
 * <p>Evita exponer campos de auditoría.</p>
 */
public record CategoriaResponseDTO(
    Integer id,
    String nombre,
    String descripcion,
    LocalDateTime fechaCreacion,
    LocalDateTime fechaActualizacion
) {}
