package com.nexoohub.almacen.inventario.dto;

import java.math.BigDecimal;

public class ProductoResumenDTO {
    private String skuInterno;
    private String nombreComercial;
    private String categoriaNombre;
    private BigDecimal precioVenta;
    private Integer stockActual;
    private String sensibilidad;

    // Constructor necesario para la consulta JPQL
    public ProductoResumenDTO(String skuInterno, String nombreComercial, String categoriaNombre, 
                              BigDecimal precioVenta, Integer stockActual, String sensibilidad) {
        this.skuInterno = skuInterno;
        this.nombreComercial = nombreComercial;
        this.categoriaNombre = categoriaNombre;
        this.precioVenta = precioVenta;
        this.stockActual = stockActual;
        this.sensibilidad = sensibilidad;
    }

    // Getters
    public String getSkuInterno() { return skuInterno; }
    public String getNombreComercial() { return nombreComercial; }
    public String getCategoriaNombre() { return categoriaNombre; }
    public BigDecimal getPrecioVenta() { return precioVenta; }
    public Integer getStockActual() { return stockActual; }
    public String getSensibilidad() { return sensibilidad; }
}