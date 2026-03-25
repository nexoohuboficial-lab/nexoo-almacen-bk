package com.nexoohub.almacen.inventario.dto;

import java.time.LocalDateTime;

public class CodigoBarrasResponse {

    private Integer id;
    private String skuInterno;
    private String codigo;
    private String tipo;
    private Boolean activo;
    private Boolean esPrincipal;
    private LocalDateTime fechaCreacion;

    // ==========================================
    // GETTERS Y SETTERS
    // ==========================================

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getSkuInterno() { return skuInterno; }
    public void setSkuInterno(String skuInterno) { this.skuInterno = skuInterno; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public Boolean getEsPrincipal() { return esPrincipal; }
    public void setEsPrincipal(Boolean esPrincipal) { this.esPrincipal = esPrincipal; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}
