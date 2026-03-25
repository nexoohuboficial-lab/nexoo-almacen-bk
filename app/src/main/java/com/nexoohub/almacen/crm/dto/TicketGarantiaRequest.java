package com.nexoohub.almacen.crm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class TicketGarantiaRequest {

    // ventaId y clienteId son opcionales ya que alguien puede traer un producto directo sin ticket de sistema antiguo
    private Integer ventaId;
    private Integer clienteId;

    @NotBlank(message = "El SKU del producto es obligatorio")
    @Size(max = 100, message = "El SKU no puede exceder 100 caracteres")
    private String skuProducto;

    @Size(max = 100, message = "El número de serie no puede exceder 100 caracteres")
    private String numeroSerie;

    @NotBlank(message = "El motivo del reclamo es obligatorio")
    private String motivoReclamo;

    @NotNull(message = "El ID del usuario que registra el reclamo es obligatorio")
    private Integer usuarioAperturaId;

    // Getters y Setters
    public Integer getVentaId() { return ventaId; }
    public void setVentaId(Integer ventaId) { this.ventaId = ventaId; }
    public Integer getClienteId() { return clienteId; }
    public void setClienteId(Integer clienteId) { this.clienteId = clienteId; }
    public String getSkuProducto() { return skuProducto; }
    public void setSkuProducto(String skuProducto) { this.skuProducto = skuProducto; }
    public String getNumeroSerie() { return numeroSerie; }
    public void setNumeroSerie(String numeroSerie) { this.numeroSerie = numeroSerie; }
    public String getMotivoReclamo() { return motivoReclamo; }
    public void setMotivoReclamo(String motivoReclamo) { this.motivoReclamo = motivoReclamo; }
    public Integer getUsuarioAperturaId() { return usuarioAperturaId; }
    public void setUsuarioAperturaId(Integer usuarioAperturaId) { this.usuarioAperturaId = usuarioAperturaId; }
}
