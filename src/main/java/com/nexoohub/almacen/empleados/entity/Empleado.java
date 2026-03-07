package com.nexoohub.almacen.empleados.entity;

import com.nexoohub.almacen.common.entity.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Table(name = "empleados")
public class Empleado extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "Los apellidos son obligatorios")
    private String apellidos;

    @NotBlank(message = "El puesto es obligatorio")
    private String puesto; // Ej: "Cajero", "Gerente", "Mecánico"

    @NotNull(message = "La sucursal asignada es obligatoria")
    @Column(name = "sucursal_id")
    private Integer sucursalId; // ¿En qué tienda trabaja?

    @Column(name = "fecha_contratacion")
    private LocalDate fechaContratacion;

    @Column(name = "activo")
    private Boolean activo = true;

    // ==========================================
    // GETTERS Y SETTERS
    // ==========================================
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getPuesto() { return puesto; }
    public void setPuesto(String puesto) { this.puesto = puesto; }

    public Integer getSucursalId() { return sucursalId; }
    public void setSucursalId(Integer sucursalId) { this.sucursalId = sucursalId; }

    public LocalDate getFechaContratacion() { return fechaContratacion; }
    public void setFechaContratacion(LocalDate fechaContratacion) { this.fechaContratacion = fechaContratacion; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}