package com.nexoohub.almacen.erp.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class EmpleadoDTO {

    private Integer id;
    private Integer usuarioId;
    private Integer sucursalId;
    private String nombreCompleto;
    private String curp;
    private String rfc;
    private String nss;
    private String departamento;
    private String puesto;
    private BigDecimal salarioDiario;
    private LocalDate fechaIngreso;
    private String estatus;
    private LocalDateTime createdAt;

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
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
