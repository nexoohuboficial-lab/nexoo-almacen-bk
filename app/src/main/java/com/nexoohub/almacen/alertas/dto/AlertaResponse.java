package com.nexoohub.almacen.alertas.dto;

import com.nexoohub.almacen.alertas.entity.AlertaSistema;
import com.nexoohub.almacen.alertas.entity.TipoAlerta;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para las alertas del sistema.
 */
@Getter
public class AlertaResponse {

    private final Integer id;
    private final TipoAlerta tipo;
    private final String mensaje;
    private final Integer sucursalId;
    private final Integer usuarioDestinoId;
    private final boolean resuelta;
    private final boolean leida;
    private final LocalDateTime fechaCreacion;

    private AlertaResponse(Integer id, TipoAlerta tipo, String mensaje,
                           Integer sucursalId, Integer usuarioDestinoId,
                           boolean resuelta, boolean leida, LocalDateTime fechaCreacion) {
        this.id = id;
        this.tipo = tipo;
        this.mensaje = mensaje;
        this.sucursalId = sucursalId;
        this.usuarioDestinoId = usuarioDestinoId;
        this.resuelta = resuelta;
        this.leida = leida;
        this.fechaCreacion = fechaCreacion;
    }

    public static AlertaResponse from(AlertaSistema alerta) {
        return new AlertaResponse(
                alerta.getId(),
                alerta.getTipo(),
                alerta.getMensaje(),
                alerta.getSucursalId(),
                alerta.getUsuarioDestinoId(),
                alerta.isResuelta(),
                alerta.isLeida(),
                alerta.getFechaCreacion()
        );
    }
}
