package com.nexoohub.almacen.catalogo.dto;

import java.time.LocalDateTime;

/**
 * DTO para respuestas de CompatibilidadProducto.
 * 
 * <p>Denormaliza información de producto y moto para mejorar UX.</p>
 */
public record CompatibilidadResponseDTO(
    Integer id,
    String skuInterno,
    String nombreProducto,
    Integer motoId,
    String marcaMoto,
    String modeloMoto,
    Integer anioInicio,
    Integer anioFin,
    LocalDateTime fechaCreacion
) {}
