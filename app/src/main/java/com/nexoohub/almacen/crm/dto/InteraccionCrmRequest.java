package com.nexoohub.almacen.crm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class InteraccionCrmRequest {

    private Integer prospectoId;
    private Integer oportunidadId;

    @NotBlank(message = "El tipo de interacción es obligatorio")
    @Size(max = 50)
    private String tipoInteraccion;

    @NotBlank(message = "El resumen es obligatorio")
    @Size(max = 500)
    private String resumen;

    private String detalles;

    // Getters y Setters
    public Integer getProspectoId() { return prospectoId; }
    public void setProspectoId(Integer prospectoId) { this.prospectoId = prospectoId; }
    public Integer getOportunidadId() { return oportunidadId; }
    public void setOportunidadId(Integer oportunidadId) { this.oportunidadId = oportunidadId; }
    public String getTipoInteraccion() { return tipoInteraccion; }
    public void setTipoInteraccion(String tipoInteraccion) { this.tipoInteraccion = tipoInteraccion; }
    public String getResumen() { return resumen; }
    public void setResumen(String resumen) { this.resumen = resumen; }
    public String getDetalles() { return detalles; }
    public void setDetalles(String detalles) { this.detalles = detalles; }
}
