package com.nexoohub.almacen.erp.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "cuenta_contable")
public class CuentaContable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 20)
    private String codigo;

    @Column(nullable = false, length = 100)
    private String nombre;

    /** ACTIVO, PASIVO, CAPITAL, INGRESO, GASTO */
    @Column(name = "tipo_cuenta", nullable = false, length = 20)
    private String tipoCuenta;

    /** DEUDORA, ACREEDORA */
    @Column(nullable = false, length = 15)
    private String naturaleza;

    @Column(nullable = false)
    private Integer nivel = 1;

    @Column(name = "cuenta_padre_id")
    private Integer cuentaPadreId;

    @Column(nullable = false)
    private Boolean activa = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

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
    public LocalDateTime getCreatedAt() { return createdAt; }
}
