package com.nexoohub.almacen.adquisiciones.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AgregarAlCarritoRequest {
    
    @NotNull(message = "El id del catálogo es obligatorio")
    private Integer catalogoId;
    
    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser mayor a 0")
    private Integer cantidad;
}
