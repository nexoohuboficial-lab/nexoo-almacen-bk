package com.nexoohub.almacen.inventario.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO para solicitud de análisis ABC.
 * 
 * @author NexooHub Development Team
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalisisABCRequestDTO {

    /**
     * ID de la sucursal a analizar (null = todas las sucursales)
     */
    private Integer sucursalId;

    /**
     * Fecha de inicio del periodo de análisis
     */
    @NotNull(message = "La fecha de inicio es obligatoria")
    @PastOrPresent(message = "La fecha de inicio no puede ser futura")
    private LocalDate periodoInicio;

    /**
     * Fecha fin del periodo de análisis
     */
    @NotNull(message = "La fecha fin es obligatoria")
    @PastOrPresent(message = "La fecha fin no puede ser futura")
    private LocalDate periodoFin;

    /**
     * Porcentaje para clasificación A (por defecto 80%)
     */
    @DecimalMin(value = "50.0", message = "El porcentaje A debe ser al menos 50%")
    @DecimalMax(value = "95.0", message = "El porcentaje A no puede ser mayor a 95%")
    private Double porcentajeA = 80.0;

    /**
     * Porcentaje para clasificación B (por defecto 95%)
     */
    @DecimalMin(value = "80.0", message = "El porcentaje B debe ser al menos 80%")
    @DecimalMax(value = "99.0", message = "El porcentaje B no puede ser mayor a 99%")
    private Double porcentajeB = 95.0;

    /**
     * Forzar regeneración si ya existe análisis para la fecha
     */
    private Boolean forzarRegeneracion = false;
}
