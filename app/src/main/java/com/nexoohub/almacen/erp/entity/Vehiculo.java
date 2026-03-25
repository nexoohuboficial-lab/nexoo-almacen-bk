package com.nexoohub.almacen.erp.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "vehiculo")
public class Vehiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 20)
    private String placas;

    @Column(nullable = false, length = 50)
    private String marca;

    @Column(nullable = false, length = 50)
    private String modelo;

    @Column(name = "capacidad_kg", precision = 10, scale = 2)
    private BigDecimal capacidadKg;

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
    public String getPlacas() { return placas; }
    public void setPlacas(String placas) { this.placas = placas; }
    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }
    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }
    public BigDecimal getCapacidadKg() { return capacidadKg; }
    public void setCapacidadKg(BigDecimal capacidadKg) { this.capacidadKg = capacidadKg; }
    public String getEstatus() { return estatus; }
    public void setEstatus(String estatus) { this.estatus = estatus; }
    public Integer getSucursalId() { return sucursalId; }
    public void setSucursalId(Integer sucursalId) { this.sucursalId = sucursalId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
