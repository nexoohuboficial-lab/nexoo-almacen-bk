package com.nexoohub.almacen.inventario.entity;

import com.nexoohub.almacen.common.entity.AuditableEntity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "inventario_sucursal")
public class InventarioSucursal extends AuditableEntity {

    @EmbeddedId
    private InventarioSucursalId id;

    private Integer stockActual = 0;

    private Integer stockMinimoSucursal = 1;

    private String ubicacionPasillo; // Ej. "Anaquel 4, Repisa B"

    @Column(name = "costo_promedio_ponderado")
    private java.math.BigDecimal costoPromedioPonderado = java.math.BigDecimal.ZERO;

// ... y sus Getters y Setters correspondientes ...
    public java.math.BigDecimal getCostoPromedioPonderado() { return costoPromedioPonderado; }
    public void setCostoPromedioPonderado(java.math.BigDecimal costoPromedioPonderado) { 
        this.costoPromedioPonderado = costoPromedioPonderado; 
    }
    // ==========================================
    // GETTERS Y SETTERS
    // ==========================================

    public InventarioSucursalId getId() { return id; }
    public void setId(InventarioSucursalId id) { this.id = id; }

    public Integer getStockActual() { return stockActual; }
    public void setStockActual(Integer stockActual) { this.stockActual = stockActual; }

    public Integer getStockMinimoSucursal() { return stockMinimoSucursal; }
    public void setStockMinimoSucursal(Integer stockMinimoSucursal) { this.stockMinimoSucursal = stockMinimoSucursal; }

    public String getUbicacionPasillo() { return ubicacionPasillo; }
    public void setUbicacionPasillo(String ubicacionPasillo) { this.ubicacionPasillo = ubicacionPasillo; }
}
