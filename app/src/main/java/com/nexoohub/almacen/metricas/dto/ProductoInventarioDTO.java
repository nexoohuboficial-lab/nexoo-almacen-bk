package com.nexoohub.almacen.metricas.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO con información de productos en inventario con stock.
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
public class ProductoInventarioDTO {

    private String skuInterno;
    private String nombreComercial;
    private String marca;
    private String categoria;
    private Integer stockActual;
    private Integer stockMinimo;
    private BigDecimal costoPromedioPonderado;
    private BigDecimal valorInventario; // stockActual × costoPromedioPonderado
    private LocalDate fechaCaducidad;
    private String ubicacionPasillo;
    private Integer sucursalId;
    private String nombreSucursal;
    private String estadoAlerta; // OK, BAJO_STOCK, SIN_STOCK, PROXIMO_CADUCAR

    // Constructor vacío
    public ProductoInventarioDTO() {}

    // Constructor completo
    public ProductoInventarioDTO(String skuInterno, String nombreComercial, String marca, String categoria,
                                  Integer stockActual, Integer stockMinimo, BigDecimal costoPromedioPonderado,
                                  BigDecimal valorInventario, LocalDate fechaCaducidad, String ubicacionPasillo,
                                  Integer sucursalId, String nombreSucursal, String estadoAlerta) {
        this.skuInterno = skuInterno;
        this.nombreComercial = nombreComercial;
        this.marca = marca;
        this.categoria = categoria;
        this.stockActual = stockActual;
        this.stockMinimo = stockMinimo;
        this.costoPromedioPonderado = costoPromedioPonderado;
        this.valorInventario = valorInventario;
        this.fechaCaducidad = fechaCaducidad;
        this.ubicacionPasillo = ubicacionPasillo;
        this.sucursalId = sucursalId;
        this.nombreSucursal = nombreSucursal;
        this.estadoAlerta = estadoAlerta;
    }

    // Getters y Setters
    public String getSkuInterno() {
        return skuInterno;
    }

    public void setSkuInterno(String skuInterno) {
        this.skuInterno = skuInterno;
    }

    public String getNombreComercial() {
        return nombreComercial;
    }

    public void setNombreComercial(String nombreComercial) {
        this.nombreComercial = nombreComercial;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public Integer getStockActual() {
        return stockActual;
    }

    public void setStockActual(Integer stockActual) {
        this.stockActual = stockActual;
    }

    public Integer getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(Integer stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public BigDecimal getCostoPromedioPonderado() {
        return costoPromedioPonderado;
    }

    public void setCostoPromedioPonderado(BigDecimal costoPromedioPonderado) {
        this.costoPromedioPonderado = costoPromedioPonderado;
    }

    public BigDecimal getValorInventario() {
        return valorInventario;
    }

    public void setValorInventario(BigDecimal valorInventario) {
        this.valorInventario = valorInventario;
    }

    public LocalDate getFechaCaducidad() {
        return fechaCaducidad;
    }

    public void setFechaCaducidad(LocalDate fechaCaducidad) {
        this.fechaCaducidad = fechaCaducidad;
    }

    public String getUbicacionPasillo() {
        return ubicacionPasillo;
    }

    public void setUbicacionPasillo(String ubicacionPasillo) {
        this.ubicacionPasillo = ubicacionPasillo;
    }

    public Integer getSucursalId() {
        return sucursalId;
    }

    public void setSucursalId(Integer sucursalId) {
        this.sucursalId = sucursalId;
    }

    public String getNombreSucursal() {
        return nombreSucursal;
    }

    public void setNombreSucursal(String nombreSucursal) {
        this.nombreSucursal = nombreSucursal;
    }

    public String getEstadoAlerta() {
        return estadoAlerta;
    }

    public void setEstadoAlerta(String estadoAlerta) {
        this.estadoAlerta = estadoAlerta;
    }
}
