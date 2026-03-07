package com.nexoohub.almacen.catalogo.dto;

import java.time.LocalDateTime;

/**
 * DTO para respuestas de Proveedor.
 * 
 * <p>Evita exponer campos de auditoría.</p>
 */
public record ProveedorResponseDTO(
    Integer id,
    String nombreEmpresa,
    String rfc,
    String nombreContacto,
    String telefono,
    String email,
    String direccion,
    LocalDateTime fechaCreacion,
    LocalDateTime fechaActualizacion
) {}
