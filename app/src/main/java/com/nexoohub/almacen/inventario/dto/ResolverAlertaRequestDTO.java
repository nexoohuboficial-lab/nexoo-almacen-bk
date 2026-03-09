package com.nexoohub.almacen.inventario.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para resolver una alerta de lento movimiento.
 * 
 * @author NexooHub Development Team
 * @since 1.2.0
 */
public class ResolverAlertaRequestDTO {
    
    @NotBlank(message = "La acción tomada es obligatoria")
    @Size(max = 100, message = "La acción no puede exceder 100 caracteres")
    private String accionTomada; // LIQUIDACION, PROMOCION, TRANSFERENCIA, DESCONTINUADO, NINGUNA
    
    @Size(max = 500, message = "Las observaciones no pueden exceder 500 caracteres")
    private String observaciones;

    // ==========================================
    // GETTERS Y SETTERS
    // ==========================================

    public String getAccionTomada() { return accionTomada; }
    public void setAccionTomada(String accionTomada) { this.accionTomada = accionTomada; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
}
