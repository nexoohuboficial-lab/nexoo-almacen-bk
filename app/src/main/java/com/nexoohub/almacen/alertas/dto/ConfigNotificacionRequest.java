package com.nexoohub.almacen.alertas.dto;

import com.nexoohub.almacen.alertas.entity.CanalNotificacion;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Request para que un usuario configure su canal de notificación preferido.
 */
@Getter
@NoArgsConstructor
public class ConfigNotificacionRequest {

    @NotNull(message = "El ID de usuario es obligatorio")
    private Integer usuarioId;

    @NotNull(message = "El canal es obligatorio")
    private CanalNotificacion canal;

    @Email(message = "El email destino no tiene formato válido")
    private String emailDestino;

    private String telegramChatId;
}
