package com.nexoohub.almacen.inventario.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class EscaneoRequest {

    @NotBlank(message = "El código escaneado es requerido")
    private String codigo;

    @NotBlank(message = "El contexto de escaneo es requerido")
    private String contexto; // COMPRA, VENTA, INVENTARIO, GARANTIA

    @NotNull(message = "La sucursal es requerida")
    private Integer sucursalId;

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getContexto() { return contexto; }
    public void setContexto(String contexto) { this.contexto = contexto; }
    public Integer getSucursalId() { return sucursalId; }
    public void setSucursalId(Integer sucursalId) { this.sucursalId = sucursalId; }
}
