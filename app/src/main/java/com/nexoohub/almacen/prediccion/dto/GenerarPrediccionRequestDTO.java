package com.nexoohub.almacen.prediccion.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * DTO de solicitud para generar predicciones de demanda.
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
public class GenerarPrediccionRequestDTO {

    /**
     * Sucursal para la que se generan las predicciones
     */
    @NotNull(message = "El ID de sucursal es obligatorio")
    private Integer sucursalId;

    /**
     * Año del periodo a predecir
     */
    @NotNull(message = "El año es obligatorio")
    @Min(value = 2020, message = "El año debe ser mayor a 2020")
    @Max(value = 2050, message = "El año debe ser menor a 2050")
    private Integer periodoAnio;

    /**
     * Mes del periodo a predecir (1-12)
     */
    @NotNull(message = "El mes es obligatorio")
    @Min(value = 1, message = "El mes debe estar entre 1 y 12")
    @Max(value = 12, message = "El mes debe estar entre 1 y 12")
    private Integer periodoMes;

    /**
     * Número de meses históricos a analizar (3-12)
     */
    @Min(value = 2, message = "Debe analizar al menos 2 meses")
    @Max(value = 24, message = "No se pueden analizar más de 24 meses")
    private Integer mesesHistoricos = 6;

    /**
     * Días de stock de seguridad (0-90)
     */
    @Min(value = 0, message = "El stock de seguridad no puede ser negativo")
    @Max(value = 90, message = "El stock de seguridad no puede superar 90 días")
    private Integer diasStockSeguridad = 7;

    /**
     * Método de cálculo a usar
     * PROMEDIO_MOVIL, TENDENCIA_LINEAL, ESTACIONAL
     */
    private String metodoCalculo = "PROMEDIO_MOVIL";

    /**
     * SKU específico (opcional, si se omite se calculan todos)
     */
    private String skuProducto;

    // ==========================================
    // CONSTRUCTORES
    // ==========================================

    public GenerarPrediccionRequestDTO() {
    }

    public GenerarPrediccionRequestDTO(Integer sucursalId, Integer periodoAnio, Integer periodoMes) {
        this.sucursalId = sucursalId;
        this.periodoAnio = periodoAnio;
        this.periodoMes = periodoMes;
    }

    // ==========================================
    // GETTERS Y SETTERS
    // ==========================================

    public Integer getSucursalId() {
        return sucursalId;
    }

    public void setSucursalId(Integer sucursalId) {
        this.sucursalId = sucursalId;
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

    public Integer getMesesHistoricos() {
        return mesesHistoricos;
    }

    public void setMesesHistoricos(Integer mesesHistoricos) {
        this.mesesHistoricos = mesesHistoricos;
    }

    public Integer getDiasStockSeguridad() {
        return diasStockSeguridad;
    }

    public void setDiasStockSeguridad(Integer diasStockSeguridad) {
        this.diasStockSeguridad = diasStockSeguridad;
    }

    public String getMetodoCalculo() {
        return metodoCalculo;
    }

    public void setMetodoCalculo(String metodoCalculo) {
        this.metodoCalculo = metodoCalculo;
    }

    public String getSkuProducto() {
        return skuProducto;
    }

    public void setSkuProducto(String skuProducto) {
        this.skuProducto = skuProducto;
    }
}
