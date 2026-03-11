package com.nexoohub.almacen.prediccion.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO con resumen de recomendaciones de compra.
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
public class RecomendacionCompraDTO {

    private Integer sucursalId;
    private String nombreSucursal;
    private Integer periodoAnio;
    private Integer periodoMes;
    private String periodoTexto;
    private Integer totalProductos;
    private Integer productosAComprar;
    private Integer unidadesTotalesComprar;
    private BigDecimal costoEstimadoCompra;
    private List<PrediccionDemandaResponseDTO> productos;

    // ==========================================
    // CONSTRUCTORES
    // ==========================================

    public RecomendacionCompraDTO() {
    }

    public RecomendacionCompraDTO(Integer sucursalId, String nombreSucursal, 
                                  Integer periodoAnio, Integer periodoMes, String periodoTexto,
                                  Integer totalProductos, Integer productosAComprar,
                                  Integer unidadesTotalesComprar, BigDecimal costoEstimadoCompra,
                                  List<PrediccionDemandaResponseDTO> productos) {
        this.sucursalId = sucursalId;
        this.nombreSucursal = nombreSucursal;
        this.periodoAnio = periodoAnio;
        this.periodoMes = periodoMes;
        this.periodoTexto = periodoTexto;
        this.totalProductos = totalProductos;
        this.productosAComprar = productosAComprar;
        this.unidadesTotalesComprar = unidadesTotalesComprar;
        this.costoEstimadoCompra = costoEstimadoCompra;
        this.productos = productos;
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

    public Integer getTotalProductos() {
        return totalProductos;
    }

    public void setTotalProductos(Integer totalProductos) {
        this.totalProductos = totalProductos;
    }

    public Integer getProductosAComprar() {
        return productosAComprar;
    }

    public void setProductosAComprar(Integer productosAComprar) {
        this.productosAComprar = productosAComprar;
    }

    public Integer getUnidadesTotalesComprar() {
        return unidadesTotalesComprar;
    }

    public void setUnidadesTotalesComprar(Integer unidadesTotalesComprar) {
        this.unidadesTotalesComprar = unidadesTotalesComprar;
    }

    public BigDecimal getCostoEstimadoCompra() {
        return costoEstimadoCompra;
    }

    public void setCostoEstimadoCompra(BigDecimal costoEstimadoCompra) {
        this.costoEstimadoCompra = costoEstimadoCompra;
    }

    public List<PrediccionDemandaResponseDTO> getProductos() {
        return productos;
    }

    public void setProductos(List<PrediccionDemandaResponseDTO> productos) {
        this.productos = productos;
    }
}
