package com.nexoohub.almacen.catalogo.dto;

import java.time.LocalDateTime;

/**
 * DTO para respuestas de TipoCliente.
 * 
 * <p>Evita exponer campos de auditoría.</p>
 */
public record TipoClienteResponseDTO(
    Integer id,
    String nombre,
    String descripcion,
    LocalDateTime fechaCreacion
) {}
