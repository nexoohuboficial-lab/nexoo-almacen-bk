package com.nexoohub.almacen.erp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class RutaEntregaRequest {

    @NotBlank(message = "El código de ruta es requerido")
    private String codigoRuta;

    @NotNull(message = "La fecha programada es requerida")
    private LocalDate fechaProgramada;

    // Campos para Flotilla Propia
    private Integer choferId;
    private Integer vehiculoId;

    // Campos para Paquetería Externa
    @NotNull(message = "Debe indicar si la ruta se realiza por paquetería externa")
    private Boolean esPaqueteria;

    private String proveedorEnvio; // Ej: MERCADO_LIBRE, DHL, FEDEX

    private String observaciones;

    @NotNull(message = "El ID de usuario que registra es requerido")
    private Integer usuarioId;

    // Getters y Setters
    public String getCodigoRuta() { return codigoRuta; }
    public void setCodigoRuta(String codigoRuta) { this.codigoRuta = codigoRuta; }
    public LocalDate getFechaProgramada() { return fechaProgramada; }
    public void setFechaProgramada(LocalDate fechaProgramada) { this.fechaProgramada = fechaProgramada; }
    public Integer getChoferId() { return choferId; }
    public void setChoferId(Integer choferId) { this.choferId = choferId; }
    public Integer getVehiculoId() { return vehiculoId; }
    public void setVehiculoId(Integer vehiculoId) { this.vehiculoId = vehiculoId; }
    public Boolean getEsPaqueteria() { return esPaqueteria; }
    public void setEsPaqueteria(Boolean esPaqueteria) { this.esPaqueteria = esPaqueteria; }
    public String getProveedorEnvio() { return proveedorEnvio; }
    public void setProveedorEnvio(String proveedorEnvio) { this.proveedorEnvio = proveedorEnvio; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }
}
