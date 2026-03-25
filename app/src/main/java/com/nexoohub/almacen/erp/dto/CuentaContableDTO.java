package com.nexoohub.almacen.erp.dto;

public class CuentaContableDTO {
    private Integer id;
    private String codigo;
    private String nombre;
    private String tipoCuenta;
    private String naturaleza;
    private Integer nivel;
    private Integer cuentaPadreId;
    private Boolean activa;

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getTipoCuenta() { return tipoCuenta; }
    public void setTipoCuenta(String tipoCuenta) { this.tipoCuenta = tipoCuenta; }
    public String getNaturaleza() { return naturaleza; }
    public void setNaturaleza(String naturaleza) { this.naturaleza = naturaleza; }
    public Integer getNivel() { return nivel; }
    public void setNivel(Integer nivel) { this.nivel = nivel; }
    public Integer getCuentaPadreId() { return cuentaPadreId; }
    public void setCuentaPadreId(Integer cuentaPadreId) { this.cuentaPadreId = cuentaPadreId; }
    public Boolean getActiva() { return activa; }
    public void setActiva(Boolean activa) { this.activa = activa; }
}
