package com.nexoohub.almacen.crm.dto;

import java.time.LocalDateTime;

public class InteraccionCrmResponse {

    private Integer id;
    private Integer prospectoId;
    private Integer oportunidadId;
    private String tipoInteraccion;
    private String resumen;
    private String detalles;
    private LocalDateTime fechaInteraccion;
    private LocalDateTime fechaCreacion;

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
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
    public LocalDateTime getFechaInteraccion() { return fechaInteraccion; }
    public void setFechaInteraccion(LocalDateTime fechaInteraccion) { this.fechaInteraccion = fechaInteraccion; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}
