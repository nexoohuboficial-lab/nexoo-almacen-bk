package com.nexoohub.almacen.common.exception;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ApiErrorResponse(
    @JsonProperty("estatus") int status,
    @JsonProperty("codigoError") String errorCode,
    @JsonProperty("mensaje") String message,
    @JsonProperty("detalles") List<String> details,
    @JsonProperty("rastreoId") String traceId, // <--- Nuevo campo
    @JsonProperty("fechaHora") LocalDateTime timestamp
) {}
