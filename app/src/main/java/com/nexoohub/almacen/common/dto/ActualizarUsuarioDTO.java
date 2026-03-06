package com.nexoohub.almacen.common.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO para actualizar datos de un usuario (excepto password)
 */
public class ActualizarUsuarioDTO {

    @NotBlank(message = "El nombre de usuario es obligatorio")
    private String username;

    private String role;
    private Integer empleadoId;

    // Constructores
    public ActualizarUsuarioDTO() {}

    public ActualizarUsuarioDTO(String username, String role, Integer empleadoId) {
        this.username = username;
        this.role = role;
        this.empleadoId = empleadoId;
    }

    // Getters y Setters
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
}
