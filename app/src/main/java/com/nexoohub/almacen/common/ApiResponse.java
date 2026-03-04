package com.nexoohub.almacen.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

/**
 * Envoltorio universal para todas las respuestas exitosas de NexooHub.
 */
public record ApiResponse<T>(
    @JsonProperty("exitoso") boolean success,
    @JsonProperty("mensaje") String message,
    @JsonProperty("datos") T data,
    @JsonProperty("rastreoId") String traceId, // <--- Nuevo campo
    @JsonProperty("fechaHora") LocalDateTime timestamp
) {
    public ApiResponse(String message, T data) {
        this(true, message, data, org.slf4j.MDC.get("traceId"), LocalDateTime.now());
    }
}
