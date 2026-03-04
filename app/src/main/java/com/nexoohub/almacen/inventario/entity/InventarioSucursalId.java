package com.nexoohub.almacen.inventario.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class InventarioSucursalId implements Serializable {

    @Column(name = "sucursal_id")
    private Integer sucursalId;

    @Column(name = "sku_interno")
    private String skuInterno;

    // JPA exige un constructor vacío
    public InventarioSucursalId() {}

    public InventarioSucursalId(Integer sucursalId, String skuInterno) {
        this.sucursalId = sucursalId;
        this.skuInterno = skuInterno;
    }

    // ==========================================
    // GETTERS Y SETTERS
    // ==========================================
    public Integer getSucursalId() { return sucursalId; }
    public void setSucursalId(Integer sucursalId) { this.sucursalId = sucursalId; }

    public String getSkuInterno() { return skuInterno; }
    public void setSkuInterno(String skuInterno) { this.skuInterno = skuInterno; }

    // ==========================================
    // REGLA DE ORO: EQUALS y HASHCODE
    // (JPA los necesita para comparar si dos llaves son iguales)
    // ==========================================
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InventarioSucursalId that = (InventarioSucursalId) o;
        return Objects.equals(sucursalId, that.sucursalId) && 
               Objects.equals(skuInterno, that.skuInterno);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sucursalId, skuInterno);
    }
}
