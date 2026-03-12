package com.nexoohub.almacen.metricas.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO de respuesta con métricas consolidadas de inventario.
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
public class MetricaInventarioResponseDTO {

    private Long id;
    private LocalDate fechaCorte;
    private Integer sucursalId;
    private String nombreSucursal;

    // Métricas de Stock
    private Integer totalSkus;
    private Integer stockDisponibleTotal;
    private Integer skusBajoStock;
    private Integer skusSinStock;
    private Integer skusProximosCaducar;

    // Métricas de Valor
    private BigDecimal valorTotalInventario;
    private BigDecimal costoPromedioPonderado;
    private BigDecimal valorStockBajo;

    // Métricas de Rotación
    private BigDecimal indiceRotacion;
    private BigDecimal diasInventario;
    private BigDecimal costoVentasPeriodo;
    private Integer diasPeriodoRotacion;

    // Métricas de Eficiencia
    private BigDecimal coberturaDias;
    private BigDecimal exactitudPorcentaje;
    private BigDecimal tasaQuiebreStock;

    // Clasificaciones
    private String saludInventario;
    private String clasificacionRotacion;

    // Porcentajes calculados
    private BigDecimal porcentajeBajoStock;
    private BigDecimal porcentajeSinStock;
    private BigDecimal porcentajeProximosCaducar;

    // Totales enriquecidos
    private Integer skusConStock; // totalSkus - skusSinStock

    // Auditoría
    private LocalDateTime createdAt;
    private String createdBy;

    // Constructor vacío
    public MetricaInventarioResponseDTO() {}

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getFechaCorte() {
        return fechaCorte;
    }

    public void setFechaCorte(LocalDate fechaCorte) {
        this.fechaCorte = fechaCorte;
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

    public Integer getTotalSkus() {
        return totalSkus;
    }

    public void setTotalSkus(Integer totalSkus) {
        this.totalSkus = totalSkus;
    }

    public Integer getStockDisponibleTotal() {
        return stockDisponibleTotal;
    }

    public void setStockDisponibleTotal(Integer stockDisponibleTotal) {
        this.stockDisponibleTotal = stockDisponibleTotal;
    }

    public Integer getSkusBajoStock() {
        return skusBajoStock;
    }

    public void setSkusBajoStock(Integer skusBajoStock) {
        this.skusBajoStock = skusBajoStock;
    }

    public Integer getSkusSinStock() {
        return skusSinStock;
    }

    public void setSkusSinStock(Integer skusSinStock) {
        this.skusSinStock = skusSinStock;
    }

    public Integer getSkusProximosCaducar() {
        return skusProximosCaducar;
    }

    public void setSkusProximosCaducar(Integer skusProximosCaducar) {
        this.skusProximosCaducar = skusProximosCaducar;
    }

    public BigDecimal getValorTotalInventario() {
        return valorTotalInventario;
    }

    public void setValorTotalInventario(BigDecimal valorTotalInventario) {
        this.valorTotalInventario = valorTotalInventario;
    }

    public BigDecimal getCostoPromedioPonderado() {
        return costoPromedioPonderado;
    }

    public void setCostoPromedioPonderado(BigDecimal costoPromedioPonderado) {
        this.costoPromedioPonderado = costoPromedioPonderado;
    }

    public BigDecimal getValorStockBajo() {
        return valorStockBajo;
    }

    public void setValorStockBajo(BigDecimal valorStockBajo) {
        this.valorStockBajo = valorStockBajo;
    }

    public BigDecimal getIndiceRotacion() {
        return indiceRotacion;
    }

    public void setIndiceRotacion(BigDecimal indiceRotacion) {
        this.indiceRotacion = indiceRotacion;
    }

    public BigDecimal getDiasInventario() {
        return diasInventario;
    }

    public void setDiasInventario(BigDecimal diasInventario) {
        this.diasInventario = diasInventario;
    }

    public BigDecimal getCostoVentasPeriodo() {
        return costoVentasPeriodo;
    }

    public void setCostoVentasPeriodo(BigDecimal costoVentasPeriodo) {
        this.costoVentasPeriodo = costoVentasPeriodo;
    }

    public Integer getDiasPeriodoRotacion() {
        return diasPeriodoRotacion;
    }

    public void setDiasPeriodoRotacion(Integer diasPeriodoRotacion) {
        this.diasPeriodoRotacion = diasPeriodoRotacion;
    }

    public BigDecimal getCoberturaDias() {
        return coberturaDias;
    }

    public void setCoberturaDias(BigDecimal coberturaDias) {
        this.coberturaDias = coberturaDias;
    }

    public BigDecimal getExactitudPorcentaje() {
        return exactitudPorcentaje;
    }

    public void setExactitudPorcentaje(BigDecimal exactitudPorcentaje) {
        this.exactitudPorcentaje = exactitudPorcentaje;
    }

    public BigDecimal getTasaQuiebreStock() {
        return tasaQuiebreStock;
    }

    public void setTasaQuiebreStock(BigDecimal tasaQuiebreStock) {
        this.tasaQuiebreStock = tasaQuiebreStock;
    }

    public String getSaludInventario() {
        return saludInventario;
    }

    public void setSaludInventario(String saludInventario) {
        this.saludInventario = saludInventario;
    }

    public String getClasificacionRotacion() {
        return clasificacionRotacion;
    }

    public void setClasificacionRotacion(String clasificacionRotacion) {
        this.clasificacionRotacion = clasificacionRotacion;
    }

    public BigDecimal getPorcentajeBajoStock() {
        return porcentajeBajoStock;
    }

    public void setPorcentajeBajoStock(BigDecimal porcentajeBajoStock) {
        this.porcentajeBajoStock = porcentajeBajoStock;
    }

    public BigDecimal getPorcentajeSinStock() {
        return porcentajeSinStock;
    }

    public void setPorcentajeSinStock(BigDecimal porcentajeSinStock) {
        this.porcentajeSinStock = porcentajeSinStock;
    }

    public BigDecimal getPorcentajeProximosCaducar() {
        return porcentajeProximosCaducar;
    }

    public void setPorcentajeProximosCaducar(BigDecimal porcentajeProximosCaducar) {
        this.porcentajeProximosCaducar = porcentajeProximosCaducar;
    }

    public Integer getSkusConStock() {
        return skusConStock;
    }

    public void setSkusConStock(Integer skusConStock) {
        this.skusConStock = skusConStock;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
}
