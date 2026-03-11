package com.nexoohub.almacen.comisiones.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * DTO para aprobar/rechazar comisiones
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
public class AprobarComisionRequestDTO {

    @NotNull(message = "El estado es obligatorio")
    @Pattern(regexp = "APROBADA|RECHAZADA", 
             message = "Estado inválido. Debe ser: APROBADA o RECHAZADA")
    private String nuevoEstado;

    private String notas;

    // Getters y Setters
    public String getNuevoEstado() { return nuevoEstado; }
    public void setNuevoEstado(String nuevoEstado) { this.nuevoEstado = nuevoEstado; }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }
}
