package com.nexoohub.almacen.erp.dto;

import jakarta.validation.constraints.NotNull;

public class RutaFacturaRequest {

    @NotNull(message = "El ID de la factura a enviar es obligatorio")
    private Integer facturaClienteId;

    private String numeroGuia; // Tracking number (ML, DHL)

    private String urlRastreo; // URL directa al rastreo

    // Getters y Setters
    public Integer getFacturaClienteId() { return facturaClienteId; }
    public void setFacturaClienteId(Integer facturaClienteId) { this.facturaClienteId = facturaClienteId; }
    public String getNumeroGuia() { return numeroGuia; }
    public void setNumeroGuia(String numeroGuia) { this.numeroGuia = numeroGuia; }
    public String getUrlRastreo() { return urlRastreo; }
    public void setUrlRastreo(String urlRastreo) { this.urlRastreo = urlRastreo; }
}
