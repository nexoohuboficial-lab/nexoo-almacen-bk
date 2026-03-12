package com.nexoohub.almacen.metricas.dto;

import java.math.BigDecimal;

/**
 * DTO para top productos por ingresos generados.
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
public class TopProductoIngresoDTO {

    private String skuInterno;
    private String nombreComercial;
    private String marca;
    private String categoria;

    private Integer cantidadVendida;
    private BigDecimal ingresosGenerados; // Total ventas del producto
    private BigDecimal costoTotal;
    private BigDecimal utilidadGenerada;
    private BigDecimal margenPorcentaje;
    private BigDecimal precioPromedioVenta;
    private Integer numeroVentas;

    // Contribution analysis
    private BigDecimal porcentajeIngresosTotal; // % que representa del total de ingresos
    private Integer ranking; // Posición en el top

    // Constructors

    public TopProductoIngresoDTO() {
    }

    public TopProductoIngresoDTO(
            String skuInterno,
            String nombreComercial,
            Integer cantidadVendida,
            BigDecimal ingresosGenerados,
            BigDecimal costoTotal,
            BigDecimal utilidadGenerada,
            BigDecimal margenPorcentaje,
            Integer numeroVentas
    ) {
        this.skuInterno = skuInterno;
        this.nombreComercial = nombreComercial;
        this.cantidadVendida = cantidadVendida;
        this.ingresosGenerados = ingresosGenerados;
        this.costoTotal = costoTotal;
        this.utilidadGenerada = utilidadGenerada;
        this.margenPorcentaje = margenPorcentaje;
        this.numeroVentas = numeroVentas;
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

    public Integer getCantidadVendida() {
        return cantidadVendida;
    }

    public void setCantidadVendida(Integer cantidadVendida) {
        this.cantidadVendida = cantidadVendida;
    }

    public BigDecimal getIngresosGenerados() {
        return ingresosGenerados;
    }

    public void setIngresosGenerados(BigDecimal ingresosGenerados) {
        this.ingresosGenerados = ingresosGenerados;
    }

    public BigDecimal getCostoTotal() {
        return costoTotal;
    }

    public void setCostoTotal(BigDecimal costoTotal) {
        this.costoTotal = costoTotal;
    }

    public BigDecimal getUtilidadGenerada() {
        return utilidadGenerada;
    }

    public void setUtilidadGenerada(BigDecimal utilidadGenerada) {
        this.utilidadGenerada = utilidadGenerada;
    }

    public BigDecimal getMargenPorcentaje() {
        return margenPorcentaje;
    }

    public void setMargenPorcentaje(BigDecimal margenPorcentaje) {
        this.margenPorcentaje = margenPorcentaje;
    }

    public BigDecimal getPrecioPromedioVenta() {
        return precioPromedioVenta;
    }

    public void setPrecioPromedioVenta(BigDecimal precioPromedioVenta) {
        this.precioPromedioVenta = precioPromedioVenta;
    }

    public Integer getNumeroVentas() {
        return numeroVentas;
    }

    public void setNumeroVentas(Integer numeroVentas) {
        this.numeroVentas = numeroVentas;
    }

    public BigDecimal getPorcentajeIngresosTotal() {
        return porcentajeIngresosTotal;
    }

    public void setPorcentajeIngresosTotal(BigDecimal porcentajeIngresosTotal) {
        this.porcentajeIngresosTotal = porcentajeIngresosTotal;
    }

    public Integer getRanking() {
        return ranking;
    }

    public void setRanking(Integer ranking) {
        this.ranking = ranking;
    }
}
