package com.nexoohub.almacen.crm.dto;

import jakarta.validation.constraints.NotNull;

public class EncuestaNpsRequest {

    @NotNull(message = "El ID de la venta es obligatorio")
    private Integer ventaId;

    // Getters y Setters
    public Integer getVentaId() { return ventaId; }
    public void setVentaId(Integer ventaId) { this.ventaId = ventaId; }
}
