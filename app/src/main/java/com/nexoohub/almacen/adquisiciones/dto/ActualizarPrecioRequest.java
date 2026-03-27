package com.nexoohub.almacen.adquisiciones.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record ActualizarPrecioRequest(
    @NotNull(message = "El nuevo precio de costo es requerido")
    @Min(value = 0, message = "El precio de costo no puede ser negativo")
    BigDecimal precioCostoNuevo,

    String motivo
) {}
