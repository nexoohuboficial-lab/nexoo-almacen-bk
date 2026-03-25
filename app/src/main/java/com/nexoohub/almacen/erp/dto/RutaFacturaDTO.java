package com.nexoohub.almacen.erp.dto;

import java.time.LocalDateTime;

public class RutaFacturaDTO {
    private Integer id;
    private Integer facturaClienteId;
    private String numeroGuia;
    private String urlRastreo;
    private String estatusEntrega;
    private LocalDateTime fechaEntrega;
    private String firmaRecibido;

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getFacturaClienteId() { return facturaClienteId; }
    public void setFacturaClienteId(Integer facturaClienteId) { this.facturaClienteId = facturaClienteId; }
    public String getNumeroGuia() { return numeroGuia; }
    public void setNumeroGuia(String numeroGuia) { this.numeroGuia = numeroGuia; }
    public String getUrlRastreo() { return urlRastreo; }
    public void setUrlRastreo(String urlRastreo) { this.urlRastreo = urlRastreo; }
    public String getEstatusEntrega() { return estatusEntrega; }
    public void setEstatusEntrega(String estatusEntrega) { this.estatusEntrega = estatusEntrega; }
    public LocalDateTime getFechaEntrega() { return fechaEntrega; }
    public void setFechaEntrega(LocalDateTime fechaEntrega) { this.fechaEntrega = fechaEntrega; }
    public String getFirmaRecibido() { return firmaRecibido; }
    public void setFirmaRecibido(String firmaRecibido) { this.firmaRecibido = firmaRecibido; }
}
