package com.nexoohub.almacen.inventario.dto;

import java.math.BigDecimal;

public class EscaneoResponse {

    /** ENCONTRADO, PRODUCTO_DESCONOCIDO */
    private String resultado;

    private String skuInterno;
    private String nombreComercial;
    private String marca;
    private Integer stockEnSucursal;
    private BigDecimal precioVigente;

    /** AGREGAR_A_COMPRA, REGISTRAR_NUEVO_PRODUCTO, VINCULAR_A_PRODUCTO_EXISTENTE */
    private String accionSugerida;

    private String codigoEscaneado;

    // ==========================================
    // GETTERS Y SETTERS
    // ==========================================

    public String getResultado() { return resultado; }
    public void setResultado(String resultado) { this.resultado = resultado; }

    public String getSkuInterno() { return skuInterno; }
    public void setSkuInterno(String skuInterno) { this.skuInterno = skuInterno; }

    public String getNombreComercial() { return nombreComercial; }
    public void setNombreComercial(String nombreComercial) { this.nombreComercial = nombreComercial; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public Integer getStockEnSucursal() { return stockEnSucursal; }
    public void setStockEnSucursal(Integer stockEnSucursal) { this.stockEnSucursal = stockEnSucursal; }

    public BigDecimal getPrecioVigente() { return precioVigente; }
    public void setPrecioVigente(BigDecimal precioVigente) { this.precioVigente = precioVigente; }

    public String getAccionSugerida() { return accionSugerida; }
    public void setAccionSugerida(String accionSugerida) { this.accionSugerida = accionSugerida; }

    public String getCodigoEscaneado() { return codigoEscaneado; }
    public void setCodigoEscaneado(String codigoEscaneado) { this.codigoEscaneado = codigoEscaneado; }
}
