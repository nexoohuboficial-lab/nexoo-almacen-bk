package com.nexoohub.almacen.erp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class DevolucionProveedorDetalleRequest {
    @NotBlank(message = "El SKU del producto es requerido")
    private String skuInterno;

    @NotNull(message = "La cantidad es requerida")
    private Integer cantidad;

    @NotNull(message = "El costo unitario es requerido")
    private BigDecimal costoUnitario;

    public String getSkuInterno() { return skuInterno; }
    public void setSkuInterno(String skuInterno) { this.skuInterno = skuInterno; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    public BigDecimal getCostoUnitario() { return costoUnitario; }
    public void setCostoUnitario(BigDecimal costoUnitario) { this.costoUnitario = costoUnitario; }
}
