package com.nexoohub.almacen.inventario.entity;

import com.nexoohub.almacen.common.entity.AuditableEntity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.DecimalMin;

@Entity
@Table(name = "inventario_sucursal")
public class InventarioSucursal extends AuditableEntity {

    @EmbeddedId
    private InventarioSucursalId id;

    @Min(value = 0, message = "El stock actual no puede ser negativo")
    private Integer stockActual = 0;

    @Min(value = 0, message = "El stock mínimo no puede ser negativo")
    private Integer stockMinimoSucursal = 1;

    private String ubicacionPasillo; // Ej. "Anaquel 4, Repisa B"

    @Column(name = "costo_promedio_ponderado")
    @DecimalMin(value = "0.0", message = "El costo promedio ponderado no puede ser negativo")
    private java.math.BigDecimal costoPromedioPonderado = java.math.BigDecimal.ZERO;

    @Column(name = "fecha_caducidad")
    private java.time.LocalDate fechaCaducidad; // Fecha de caducidad del lote

    @Column(name = "lote", length = 100)
    private String lote; // Número de lote para trazabilidad

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
    public void setStockActual(Integer stockActual) {
        if (stockActual != null && stockActual < 0) {
            throw new IllegalArgumentException("El stock actual no puede ser negativo: " + stockActual);
        }
        this.stockActual = stockActual;
    }

    public Integer getStockMinimoSucursal() { return stockMinimoSucursal; }
    public void setStockMinimoSucursal(Integer stockMinimoSucursal) { this.stockMinimoSucursal = stockMinimoSucursal; }

    public String getUbicacionPasillo() { return ubicacionPasillo; }
    public void setUbicacionPasillo(String ubicacionPasillo) { this.ubicacionPasillo = ubicacionPasillo; }

    public java.time.LocalDate getFechaCaducidad() { return fechaCaducidad; }
    public void setFechaCaducidad(java.time.LocalDate fechaCaducidad) { this.fechaCaducidad = fechaCaducidad; }

    public String getLote() { return lote; }
    public void setLote(String lote) { this.lote = lote; }
}
