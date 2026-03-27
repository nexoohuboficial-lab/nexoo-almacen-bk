package com.nexoohub.almacen.alertas.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Request para configurar los umbrales de alerta de una sucursal.
 */
@Getter
@NoArgsConstructor
public class ConfigurarAlertaRequest {

    @NotNull(message = "El ID de sucursal es obligatorio")
    private Integer sucursalId;

    @Min(value = 1, message = "El stock mínimo debe ser al menos 1")
    @Max(value = 1000, message = "El stock mínimo no puede superar 1000")
    private int stockMinimo = 5;

    @Min(value = 1, message = "Los días de vencimiento deben ser al menos 1")
    @Max(value = 365, message = "Los días de vencimiento no pueden superar 365")
    private int diasVencimientoCxC = 30;

    @Min(value = 1, message = "El porcentaje de alerta debe ser al menos 1")
    @Max(value = 99, message = "El porcentaje de alerta debe ser menor al 100%")
    private int porcentajeMetaAlerta = 60;
}
