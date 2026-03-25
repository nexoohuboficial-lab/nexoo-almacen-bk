package com.nexoohub.almacen.inventario.dto;

import jakarta.validation.constraints.NotBlank;

public class CodigoBarrasRequest {

    @NotBlank(message = "El código de barras es requerido")
    private String codigo;

    @NotBlank(message = "El tipo de código es requerido")
    private String tipo; // EAN13, QR, UPC, INTERNO

    private Boolean esPrincipal = false;

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public Boolean getEsPrincipal() { return esPrincipal; }
    public void setEsPrincipal(Boolean esPrincipal) { this.esPrincipal = esPrincipal; }
}
