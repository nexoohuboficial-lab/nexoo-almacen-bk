package com.nexoohub.almacen.common.dto;

/**
 * DTO para respuestas de Usuario (sin exponer el password)
 */
public class UsuarioDTO {

    private Long id;
    private String username;
    private String role;
    private Integer empleadoId;
    private String usuarioCreacion;
    private String usuarioActualizacion;

    // Constructores
    public UsuarioDTO() {}

    public UsuarioDTO(Long id, String username, String role, Integer empleadoId, String usuarioCreacion, String usuarioActualizacion) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.empleadoId = empleadoId;
        this.usuarioCreacion = usuarioCreacion;
        this.usuarioActualizacion = usuarioActualizacion;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Integer getEmpleadoId() {
        return empleadoId;
    }

    public void setEmpleadoId(Integer empleadoId) {
        this.empleadoId = empleadoId;
    }

    public String getUsuarioCreacion() {
        return usuarioCreacion;
    }

    public void setUsuarioCreacion(String usuarioCreacion) {
        this.usuarioCreacion = usuarioCreacion;
    }

    public String getUsuarioActualizacion() {
        return usuarioActualizacion;
    }

    public void setUsuarioActualizacion(String usuarioActualizacion) {
        this.usuarioActualizacion = usuarioActualizacion;
    }
}
