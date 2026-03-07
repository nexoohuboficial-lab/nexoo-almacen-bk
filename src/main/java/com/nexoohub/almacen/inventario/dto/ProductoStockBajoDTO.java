package com.nexoohub.almacen.inventario.dto;

/**
 * DTO para productos con stock por debajo del mínimo.
 * 
 * <p>Utilizado en reportes y alertas de reabastecimiento.</p>
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
public class ProductoStockBajoDTO {
    private final String skuInterno;
    private final String nombreComercial;
    private final String marca;
    private final Integer sucursalId;
    private final String nombreSucursal;
    private final Integer stockActual;
    private final Integer stockMinimo;
    private final Integer diferencia; // stockMinimo - stockActual
    
    public ProductoStockBajoDTO(
            String skuInterno, 
            String nombreComercial,
            String marca,
            Integer sucursalId,
            String nombreSucursal,
            Integer stockActual,
            Integer stockMinimo) {
        this.skuInterno = skuInterno;
        this.nombreComercial = nombreComercial;
        this.marca = marca;
        this.sucursalId = sucursalId;
        this.nombreSucursal = nombreSucursal;
        this.stockActual = stockActual;
        this.stockMinimo = stockMinimo;
        this.diferencia = stockMinimo - stockActual;
    }
    
    // Getters
    public String getSkuInterno() { return skuInterno; }
    public String getNombreComercial() { return nombreComercial; }
    public String getMarca() { return marca; }
    public Integer getSucursalId() { return sucursalId; }
    public String getNombreSucursal() { return nombreSucursal; }
    public Integer getStockActual() { return stockActual; }
    public Integer getStockMinimo() { return stockMinimo; }
    public Integer getDiferencia() { return diferencia; }
}
