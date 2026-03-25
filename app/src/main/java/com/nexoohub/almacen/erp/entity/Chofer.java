package com.nexoohub.almacen.erp.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "chofer")
public class Chofer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nombre_completo", nullable = false, length = 100)
    private String nombreCompleto;

    @Column(nullable = false, unique = true, length = 50)
    private String licencia;

    @Column(name = "vigencia_licencia", nullable = false)
    private LocalDate vigenciaLicencia;

    @Column(length = 20)
    private String telefono;

    @Column(nullable = false, length = 20)
    private String estatus = "ACTIVO";

    @Column(name = "sucursal_id", nullable = false)
    private Integer sucursalId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

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
}
