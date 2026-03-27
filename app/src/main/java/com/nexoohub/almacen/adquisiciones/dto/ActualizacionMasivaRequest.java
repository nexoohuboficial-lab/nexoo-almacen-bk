package com.nexoohub.almacen.adquisiciones.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record ActualizacionMasivaRequest(
    @NotEmpty(message = "Debe proporcionar al menos un artículo para actualizar")
    List<ItemActualizacionMasiva> items,
    
    String motivoGeneral
) {
    public record ItemActualizacionMasiva(
        Long catalogoId,
        java.math.BigDecimal precioCostoNuevo,
        String motivoEspecifico
    ) {}
}
