package com.nexoohub.almacen.inventario.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

/**
 * Cada fila del CSV/JSON de importación masiva.
 * Incluye datos del producto + códigos de barras.
 */
public class ImportacionMasivaItemRequest {

    @NotBlank(message = "El SKU interno es requerido")
    private String skuInterno;

    private String skuProveedor;

    @NotBlank(message = "El nombre comercial es requerido")
    private String nombreComercial;

    private String descripcion;
    private String marca;
    private Integer categoriaId;
    private Integer proveedorId;
    private String claveSat;
    private Integer stockMinimoGlobal = 2;

    /** Lista de códigos de barras a vincular */
    private List<CodigoBarrasRequest> codigosBarras;

    // ==========================================
    // GETTERS Y SETTERS
    // ==========================================

    public String getSkuInterno() { return skuInterno; }
    public void setSkuInterno(String skuInterno) { this.skuInterno = skuInterno; }
    public String getSkuProveedor() { return skuProveedor; }
    public void setSkuProveedor(String skuProveedor) { this.skuProveedor = skuProveedor; }
    public String getNombreComercial() { return nombreComercial; }
    public void setNombreComercial(String nombreComercial) { this.nombreComercial = nombreComercial; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }
    public Integer getCategoriaId() { return categoriaId; }
    public void setCategoriaId(Integer categoriaId) { this.categoriaId = categoriaId; }
    public Integer getProveedorId() { return proveedorId; }
    public void setProveedorId(Integer proveedorId) { this.proveedorId = proveedorId; }
    public String getClaveSat() { return claveSat; }
    public void setClaveSat(String claveSat) { this.claveSat = claveSat; }
    public Integer getStockMinimoGlobal() { return stockMinimoGlobal; }
    public void setStockMinimoGlobal(Integer stockMinimoGlobal) { this.stockMinimoGlobal = stockMinimoGlobal; }
    public List<CodigoBarrasRequest> getCodigosBarras() { return codigosBarras; }
    public void setCodigosBarras(List<CodigoBarrasRequest> codigosBarras) { this.codigosBarras = codigosBarras; }
}
