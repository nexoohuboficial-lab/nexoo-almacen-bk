package com.nexoohub.almacen.erp.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ChoferDTO {
    private Integer id;
    private String nombreCompleto;
    private String licencia;
    private LocalDate vigenciaLicencia;
    private String telefono;
    private String estatus;
    private Integer sucursalId;
    private LocalDateTime createdAt;

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }
    public String getLicencia() { return licencia; }
    public void setLicencia(String licencia) { this.licencia = licencia; }
    public LocalDate getVigenciaLicencia() { return vigenciaLicencia; }
    public void setVigenciaLicencia(LocalDate vigenciaLicencia) { this.vigenciaLicencia = vigenciaLicencia; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getEstatus() { return estatus; }
    public void setEstatus(String estatus) { this.estatus = estatus; }
    public Integer getSucursalId() { return sucursalId; }
    public void setSucursalId(Integer sucursalId) { this.sucursalId = sucursalId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
