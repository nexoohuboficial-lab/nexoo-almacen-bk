package com.nexoohub.almacen.empleados.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO para respuestas de Empleado.
 * 
 * <p>Evita exponer campos de auditoría y permite denormalizar 
 * información de la sucursal.</p>
 */
public record EmpleadoResponseDTO(
    Integer id,
    String nombre,
    String apellidos,
    String puesto,
    Integer sucursalId,
    String sucursalNombre,
    LocalDate fechaContratacion,
    Boolean activo,
    LocalDateTime fechaCreacion
) {}
