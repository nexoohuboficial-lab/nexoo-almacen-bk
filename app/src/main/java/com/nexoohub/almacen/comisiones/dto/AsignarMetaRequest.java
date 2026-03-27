package com.nexoohub.almacen.comisiones.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AsignarMetaRequest {
    
    @NotNull(message = "El empleado es obligatorio")
    private Integer empleadoId;

    @NotNull(message = "El mes es obligatorio")
    @Min(value = 1, message = "Mes inválido")
    @Max(value = 12, message = "Mes inválido")
    private Integer mes;

    @NotNull(message = "El año es obligatorio")
    @Min(value = 2020, message = "Año inválido")
    private Integer anio;

    @NotNull(message = "El monto meta es obligatorio")
    @Positive(message = "La meta debe ser mayor a 0")
    private BigDecimal montoMeta;
}
