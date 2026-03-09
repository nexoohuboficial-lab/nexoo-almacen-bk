package com.nexoohub.almacen.cotizaciones.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO para cambiar el estado de una cotización
 * (enviar, aceptar, rechazar)
 */
public class CambiarEstadoRequestDTO {
    
    @NotBlank(message = "El nuevo estado es obligatorio")
    private String nuevoEstado; // ENVIADA, ACEPTADA, RECHAZADA
    
    private String motivo; // Obligatorio solo para RECHAZADA
    
    // Constructores
    
    public CambiarEstadoRequestDTO() {
    }
    
    public CambiarEstadoRequestDTO(String nuevoEstado) {
        this.nuevoEstado = nuevoEstado;
    }
    
    public CambiarEstadoRequestDTO(String nuevoEstado, String motivo) {
        this.nuevoEstado = nuevoEstado;
        this.motivo = motivo;
    }
    
    // Getters y Setters
    
    public String getNuevoEstado() {
        return nuevoEstado;
    }
    
    public void setNuevoEstado(String nuevoEstado) {
        this.nuevoEstado = nuevoEstado;
    }
    
    public String getMotivo() {
        return motivo;
    }
    
    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}
