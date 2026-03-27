package com.nexoohub.almacen.crm.dto;

import java.time.LocalDateTime;

public class EncuestaNpsResponse {

    private Integer id;
    private Integer ventaId;
    private Integer clienteId;
    private String enlaceUnico;
    private String estado;
    private LocalDateTime fechaEnvio;
    private LocalDateTime fechaExpiracion;

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public Integer getVentaId() { return ventaId; }
    public void setVentaId(Integer ventaId) { this.ventaId = ventaId; }
    
    public Integer getClienteId() { return clienteId; }
    public void setClienteId(Integer clienteId) { this.clienteId = clienteId; }
    
    public String getEnlaceUnico() { return enlaceUnico; }
    public void setEnlaceUnico(String enlaceUnico) { this.enlaceUnico = enlaceUnico; }
    
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    
    public LocalDateTime getFechaEnvio() { return fechaEnvio; }
    public void setFechaEnvio(LocalDateTime fechaEnvio) { this.fechaEnvio = fechaEnvio; }
    
    public LocalDateTime getFechaExpiracion() { return fechaExpiracion; }
    public void setFechaExpiracion(LocalDateTime fechaExpiracion) { this.fechaExpiracion = fechaExpiracion; }
}
