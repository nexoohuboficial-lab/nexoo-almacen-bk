package com.nexoohub.almacen.crm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class ResolucionGarantiaRequest {

    @NotBlank(message = "La resolución es obligatoria")
    @Pattern(regexp = "^(CAMBIO_PIEZA|DEVOLUCION_DINERO|REPARACION|RECHAZADO)$", 
             message = "Resolución inválida. Solo: CAMBIO_PIEZA, DEVOLUCION_DINERO, REPARACION, RECHAZADO")
    private String tipoResolucion;

    @NotBlank(message = "Debe proporcionar una justificación o comentario técnico para la resolución")
    private String notasInternas;

    @NotNull(message = "El ID del usuario que autoriza la resolución es obligatorio")
    private Integer usuarioResolucionId;

    // Getters y Setters
    public String getTipoResolucion() { return tipoResolucion; }
    public void setTipoResolucion(String tipoResolucion) { this.tipoResolucion = tipoResolucion; }
    public String getNotasInternas() { return notasInternas; }
    public void setNotasInternas(String notasInternas) { this.notasInternas = notasInternas; }
    public Integer getUsuarioResolucionId() { return usuarioResolucionId; }
    public void setUsuarioResolucionId(Integer usuarioResolucionId) { this.usuarioResolucionId = usuarioResolucionId; }
}
