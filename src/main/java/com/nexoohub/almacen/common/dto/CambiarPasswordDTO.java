package com.nexoohub.almacen.common.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para cambiar la contraseña de un usuario.
 * Requiere la contraseña actual por seguridad.
 */
public class CambiarPasswordDTO {

    @NotBlank(message = "La contraseña actual es obligatoria")
    private String oldPassword;

    @NotBlank(message = "La nueva contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String newPassword;

    // Constructores
    public CambiarPasswordDTO() {}

    public CambiarPasswordDTO(String oldPassword, String newPassword) {
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }

    // Getters y Setters
    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
