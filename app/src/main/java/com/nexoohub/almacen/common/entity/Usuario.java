package com.nexoohub.almacen.common.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "usuarios")
public class Usuario extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Column(unique = true, nullable = false)
    private String username;

    @NotBlank(message = "La contraseña es obligatoria")
    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role = "ROLE_USER"; 

    // ¡NUEVO!: El enlace con la persona física
    @Column(name = "empleado_id")
    private Integer empleadoId;

    // ==========================================
    // GETTERS Y SETTERS
    // ==========================================
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Integer getEmpleadoId() { return empleadoId; }
    public void setEmpleadoId(Integer empleadoId) { this.empleadoId = empleadoId; }

    @ManyToMany(fetch = jakarta.persistence.FetchType.EAGER)
    @JoinTable(
        name = "usuario_rol",
        joinColumns = @JoinColumn(name = "usuario_id"),
        inverseJoinColumns = @JoinColumn(name = "rol_id")
    )
    private java.util.Set<Rol> roles = new java.util.HashSet<>();

    @ManyToMany(fetch = jakarta.persistence.FetchType.LAZY)
    @JoinTable(
        name = "usuario_sucursal",
        joinColumns = @JoinColumn(name = "usuario_id"),
        inverseJoinColumns = @JoinColumn(name = "sucursal_id")
    )
    private java.util.Set<com.nexoohub.almacen.sucursal.entity.Sucursal> sucursalesPermitidas = new java.util.HashSet<>();

    public java.util.Set<Rol> getRoles() { return roles; }
    public void setRoles(java.util.Set<Rol> roles) { this.roles = roles; }

    public java.util.Set<com.nexoohub.almacen.sucursal.entity.Sucursal> getSucursalesPermitidas() { return sucursalesPermitidas; }
    public void setSucursalesPermitidas(java.util.Set<com.nexoohub.almacen.sucursal.entity.Sucursal> sucursalesPermitidas) { this.sucursalesPermitidas = sucursalesPermitidas; }
}