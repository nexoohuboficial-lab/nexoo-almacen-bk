package com.nexoohub.almacen.prediccion.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO de respuesta para predicción de demanda.
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
public class PrediccionDemandaResponseDTO {

    private Integer id;
    private String skuProducto;
    private String nombreProducto;
    private Integer sucursalId;
    private String nombreSucursal;
    private Integer periodoAnio;
    private Integer periodoMes;
    private String periodoTexto; // "Enero 2026"
    private BigDecimal demandaHistorica;
    private BigDecimal tendencia;
    private BigDecimal demandaPredicha;
    private Integer stockActual;
    private Integer stockSeguridad;
    private Integer stockSugerido;
    private Integer cantidadComprar;
    private BigDecimal nivelConfianza;
    private String metodoCalculo;
    private Integer periodosAnalizados;
    private LocalDate fechaCalculo;
    private String observaciones;

    // ==========================================
    // CONSTRUCTORES
    // ==========================================

    public PrediccionDemandaResponseDTO() {
    }

    public PrediccionDemandaResponseDTO(Integer id, String skuProducto, String nombreProducto, 
                                        Integer sucursalId, String nombreSucursal,
                                        Integer periodoAnio, Integer periodoMes, String periodoTexto,
                                        BigDecimal demandaHistorica, BigDecimal tendencia, 
                                        BigDecimal demandaPredicha, Integer stockActual, 
                                        Integer stockSeguridad, Integer stockSugerido, 
                                        Integer cantidadComprar, BigDecimal nivelConfianza,
                                        String metodoCalculo, Integer periodosAnalizados, 
                                        LocalDate fechaCalculo, String observaciones) {
        this.id = id;
        this.skuProducto = skuProducto;
        this.nombreProducto = nombreProducto;
        this.sucursalId = sucursalId;
        this.nombreSucursal = nombreSucursal;
        this.periodoAnio = periodoAnio;
        this.periodoMes = periodoMes;
        this.periodoTexto = periodoTexto;
        this.demandaHistorica = demandaHistorica;
        this.tendencia = tendencia;
        this.demandaPredicha = demandaPredicha;
        this.stockActual = stockActual;
        this.stockSeguridad = stockSeguridad;
        this.stockSugerido = stockSugerido;
        this.cantidadComprar = cantidadComprar;
        this.nivelConfianza = nivelConfianza;
        this.metodoCalculo = metodoCalculo;
        this.periodosAnalizados = periodosAnalizados;
        this.fechaCalculo = fechaCalculo;
        this.observaciones = observaciones;
    }

    // ==========================================
    // GETTERS Y SETTERS
    // ==========================================

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSkuProducto() {
        return skuProducto;
    }

    public void setSkuProducto(String skuProducto) {
        this.skuProducto = skuProducto;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
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

    public Integer getPeriodoAnio() {
        return periodoAnio;
    }

    public void setPeriodoAnio(Integer periodoAnio) {
        this.periodoAnio = periodoAnio;
    }

    public Integer getPeriodoMes() {
        return periodoMes;
    }

    public void setPeriodoMes(Integer periodoMes) {
        this.periodoMes = periodoMes;
    }

    public String getPeriodoTexto() {
        return periodoTexto;
    }

    public void setPeriodoTexto(String periodoTexto) {
        this.periodoTexto = periodoTexto;
    }

    public BigDecimal getDemandaHistorica() {
        return demandaHistorica;
    }

    public void setDemandaHistorica(BigDecimal demandaHistorica) {
        this.demandaHistorica = demandaHistorica;
    }

    public BigDecimal getTendencia() {
        return tendencia;
    }

    public void setTendencia(BigDecimal tendencia) {
        this.tendencia = tendencia;
    }

    public BigDecimal getDemandaPredicha() {
        return demandaPredicha;
    }

    public void setDemandaPredicha(BigDecimal demandaPredicha) {
        this.demandaPredicha = demandaPredicha;
    }

    public Integer getStockActual() {
        return stockActual;
    }

    public void setStockActual(Integer stockActual) {
        this.stockActual = stockActual;
    }

    public Integer getStockSeguridad() {
        return stockSeguridad;
    }

    public void setStockSeguridad(Integer stockSeguridad) {
        this.stockSeguridad = stockSeguridad;
    }

    public Integer getStockSugerido() {
        return stockSugerido;
    }

    public void setStockSugerido(Integer stockSugerido) {
        this.stockSugerido = stockSugerido;
    }

    public Integer getCantidadComprar() {
        return cantidadComprar;
    }

    public void setCantidadComprar(Integer cantidadComprar) {
        this.cantidadComprar = cantidadComprar;
    }

    public BigDecimal getNivelConfianza() {
        return nivelConfianza;
    }

    public void setNivelConfianza(BigDecimal nivelConfianza) {
        this.nivelConfianza = nivelConfianza;
    }

    public String getMetodoCalculo() {
        return metodoCalculo;
    }

    public void setMetodoCalculo(String metodoCalculo) {
        this.metodoCalculo = metodoCalculo;
    }

    public Integer getPeriodosAnalizados() {
        return periodosAnalizados;
    }

    public void setPeriodosAnalizados(Integer periodosAnalizados) {
        this.periodosAnalizados = periodosAnalizados;
    }

    public LocalDate getFechaCalculo() {
        return fechaCalculo;
    }

    public void setFechaCalculo(LocalDate fechaCalculo) {
        this.fechaCalculo = fechaCalculo;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
