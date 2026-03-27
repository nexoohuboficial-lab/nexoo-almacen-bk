package com.nexoohub.almacen.adquisiciones.dto;

public record ResultadoActualizacionResponse(
    int totalProcesados,
    int actualizadosExitosamente,
    int fallidos,
    java.util.List<ErrorDetalle> errores
) {
    public record ErrorDetalle(
        Long catalogoId,
        String mensajeError
    ) {}
}
