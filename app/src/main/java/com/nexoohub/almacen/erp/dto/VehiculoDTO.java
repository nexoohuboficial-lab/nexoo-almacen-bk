package com.nexoohub.almacen.erp.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class VehiculoDTO {
    private Integer id;
    private String placas;
    private String marca;
    private String modelo;
    private BigDecimal capacidadKg;
    private String estatus;
    private Integer sucursalId;
    private LocalDateTime createdAt;

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
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
