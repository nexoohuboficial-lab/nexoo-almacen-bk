package com.nexoohub.almacen.inventario.dto;

import java.math.BigDecimal;

public class InventarioSucursalDTO {
    private String skuInterno;
    private String nombreComercial;
    private Integer stockActual;
    private BigDecimal costoPromedioPonderado;

    // Constructor exacto para que JPA pueda inyectar los datos directamente
    public InventarioSucursalDTO(String skuInterno, String nombreComercial, Integer stockActual, BigDecimal costoPromedioPonderado) {
        this.skuInterno = skuInterno;
        this.nombreComercial = nombreComercial;
        this.stockActual = stockActual;
        this.costoPromedioPonderado = costoPromedioPonderado;
    }

    // Getters
    public String getSkuInterno() { return skuInterno; }
    public String getNombreComercial() { return nombreComercial; }
    public Integer getStockActual() { return stockActual; }
    public BigDecimal getCostoPromedioPonderado() { return costoPromedioPonderado; }
}