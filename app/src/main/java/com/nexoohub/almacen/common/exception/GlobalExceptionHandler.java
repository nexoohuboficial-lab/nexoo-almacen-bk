package com.nexoohub.almacen.common.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.nexoohub.almacen.common.ApiResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. Iniciamos el logger profesional
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        // Usamos WARN porque es un error esperado del usuario
        log.warn("Recurso no encontrado: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.NOT_FOUND, "No encontrado", ex.getMessage(), null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        List<String> details = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.toList());
        
        // Logueamos qué campos fallaron para auditoría
        log.warn("Fallo en validación de datos: {}", details);
        
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Datos invalidos", "Datos de entrada inválidos", details);
    }

    @ExceptionHandler(org.springframework.web.bind.MissingServletRequestParameterException.class)
    public ResponseEntity<ApiErrorResponse> handleMissingParams(org.springframework.web.bind.MissingServletRequestParameterException ex) {
        log.warn("Parámetro requerido faltante: {}", ex.getParameterName());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Parametro Faltante", 
                "Parámetro requerido '" + ex.getParameterName() + "' no fue proporcionado", null);
    }

    @ExceptionHandler(jakarta.persistence.EntityNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleEntityNotFound(jakarta.persistence.EntityNotFoundException ex) {
        log.warn("Entidad no encontrada: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.NOT_FOUND, "No encontrado", ex.getMessage(), null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleAll(Exception ex) {
        // 2. ERROR CRÍTICO: Aquí sí mandamos el STACKTRACE completo al log 
        // para que tú puedas ver en qué línea falló, pero el usuario no lo ve.
        log.error("ERROR NO CONTROLADO EN NEXOOHUB: ", ex);
        
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Error Interno", "Ocurrió un error inesperado", null);
    }

    private ResponseEntity<ApiErrorResponse> buildErrorResponse(HttpStatus status, String code, String message, List<String> details) {
        ApiErrorResponse error = new ApiErrorResponse(
            status.value(),
            code,
            message,
            details,
            org.slf4j.MDC.get("traceId"), // Extraemos el ID del contexto actual
            LocalDateTime.now()
        );
        return new ResponseEntity<>(error, status);
    }

    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleDataIntegrityViolation(org.springframework.dao.DataIntegrityViolationException ex) {
        
        // Extraemos el mensaje real de la base de datos (PostgreSQL)
        String rootMsg = ex.getMostSpecificCause().getMessage();
        String mensajeUsuario = "Error de integridad en la base de datos.";

        if (rootMsg != null) {
            if (rootMsg.toLowerCase().contains("duplicate key") || rootMsg.toLowerCase().contains("llave duplicada")) {
                mensajeUsuario = "El registro ya existe en el sistema. No puedes duplicar datos (Ej. SKU o Email duplicado).";
            } else if (rootMsg.toLowerCase().contains("foreign key") || rootMsg.toLowerCase().contains("llave foránea")) {
                mensajeUsuario = "Estás intentando usar un dato (como un ID de sucursal o SKU) que no existe en el catálogo principal.";
            } else if (rootMsg.toLowerCase().contains("not-null") || rootMsg.toLowerCase().contains("nulo")) {
                mensajeUsuario = "Falta un campo obligatorio en la base de datos que no fue enviado.";
            }
        }

        ApiResponse<Object> response = new ApiResponse<>(
            false, 
            mensajeUsuario, 
            null, 
            org.slf4j.MDC.get("traceId"), 
            java.time.LocalDateTime.now()
        );
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(org.springframework.security.authentication.BadCredentialsException.class)
    public ResponseEntity<ApiErrorResponse> handleBadCredentials(org.springframework.security.authentication.BadCredentialsException ex) {
        // Logueamos como WARN porque es un intento fallido, no un error crítico
        log.warn("Intento de login fallido: Credenciales incorrectas");
        
        return buildErrorResponse(
            HttpStatus.UNAUTHORIZED, 
            "Credenciales Invalidas", 
            "El usuario o la contraseña son incorrectos", 
            null
        );
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDenied(org.springframework.security.access.AccessDeniedException ex) {
        log.warn("Acceso denegado: {}", ex.getMessage());
        return buildErrorResponse(
            HttpStatus.FORBIDDEN, 
            "Acceso Denegado", 
            "No tienes permisos suficientes para realizar esta acción", 
            null
        );
    }

    /**
     * Maneja excepciones de stock insuficiente.
     * HTTP 409 CONFLICT - El cliente intenta realizar una operación que no puede completarse.
     */
    @ExceptionHandler(StockInsuficienteException.class)
    public ResponseEntity<ApiErrorResponse> handleStockInsuficiente(StockInsuficienteException ex) {
        log.warn("Stock insuficiente: {} (SKU: {}, Disponible: {}, Solicitado: {})", 
                 ex.getMessage(), ex.getSkuInterno(), ex.getStockDisponible(), ex.getCantidadSolicitada());
        
        return buildErrorResponse(
            HttpStatus.CONFLICT, 
            ex.getErrorCode(), 
            ex.getMessage(), 
            null
        );
    }

    /**
     * Maneja excepciones de recursos duplicados.
     * HTTP 409 CONFLICT - El recurso que se intenta crear ya existe.
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicateResource(DuplicateResourceException ex) {
        log.warn("Recurso duplicado: {} (Tipo: {}, Campo: {}, Valor: {})", 
                 ex.getMessage(), ex.getResourceType(), ex.getFieldName(), ex.getFieldValue());
        
        return buildErrorResponse(
            HttpStatus.CONFLICT, 
            ex.getErrorCode(), 
            ex.getMessage(), 
            null
        );
    }

    /**
     * Maneja excepciones de operaciones inválidas.
     * HTTP 400 BAD REQUEST - La operación solicitada no es válida.
     */
    @ExceptionHandler(InvalidOperationException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidOperation(InvalidOperationException ex) {
        log.warn("Operación inválida: {} (Operación: {}, Razón: {})", 
                 ex.getMessage(), ex.getOperation(), ex.getReason());
        
        return buildErrorResponse(
            HttpStatus.BAD_REQUEST, 
            ex.getErrorCode(), 
            ex.getMessage(), 
            null
        );
    }

    /**
     * Maneja cualquier BusinessException no capturada específicamente.
     * HTTP 400 BAD REQUEST - Errores de validación de lógica de negocio.
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiErrorResponse> handleBusinessException(BusinessException ex) {
        log.warn("Error de validación de negocio: {} (Código: {})", ex.getMessage(), ex.getErrorCode());
        
        return buildErrorResponse(
            HttpStatus.BAD_REQUEST, 
            ex.getErrorCode(), 
            ex.getMessage(), 
            null
        );
    }
}
