package com.nexoohub.almacen.erp.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity(name = "ErpEmpleado")
@Table(name = "empleado")
public class Empleado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "usuario_id", unique = true)
    private Integer usuarioId;

    @Column(name = "sucursal_id", nullable = false)
    private Integer sucursalId;

    @Column(name = "nombre_completo", nullable = false, length = 100)
    private String nombreCompleto;

    @Column(unique = true, length = 18)
    private String curp;

    @Column(unique = true, length = 13)
    private String rfc;

    @Column(unique = true, length = 15)
    private String nss;

    @Column(nullable = false, length = 50)
    private String departamento;

    @Column(nullable = false, length = 50)
    private String puesto;

    @Column(name = "salario_diario", precision = 12, scale = 4, nullable = false)
    private BigDecimal salarioDiario = BigDecimal.ZERO;

    @Column(name = "fecha_ingreso", nullable = false)
    private LocalDate fechaIngreso;

    /** ACTIVO, INACTIVO */
    @Column(nullable = false, length = 20)
    private String estatus = "ACTIVO";

    @Column(name = "created_at", nullable = false, updatable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }
    public Integer getSucursalId() { return sucursalId; }
    public void setSucursalId(Integer sucursalId) { this.sucursalId = sucursalId; }
    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }
    public String getCurp() { return curp; }
    public void setCurp(String curp) { this.curp = curp; }
    public String getRfc() { return rfc; }
    public void setRfc(String rfc) { this.rfc = rfc; }
    public String getNss() { return nss; }
    public void setNss(String nss) { this.nss = nss; }
    public String getDepartamento() { return departamento; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }
    public String getPuesto() { return puesto; }
    public void setPuesto(String puesto) { this.puesto = puesto; }
    public BigDecimal getSalarioDiario() { return salarioDiario; }
    public void setSalarioDiario(BigDecimal salarioDiario) { this.salarioDiario = salarioDiario; }
    public LocalDate getFechaIngreso() { return fechaIngreso; }
    public void setFechaIngreso(LocalDate fechaIngreso) { this.fechaIngreso = fechaIngreso; }
    public String getEstatus() { return estatus; }
    public void setEstatus(String estatus) { this.estatus = estatus; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
