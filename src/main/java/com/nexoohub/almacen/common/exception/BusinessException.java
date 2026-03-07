package com.nexoohub.almacen.common.exception;

/**
 * Excepción base para errores de lógica de negocio.
 * 
 * <p>Todas las excepciones personalizadas del dominio deben extender esta clase.
 * Permite capturar errores de negocio de manera uniforme en el GlobalExceptionHandler.</p>
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
public class BusinessException extends RuntimeException {

    private final String errorCode;

    /**
     * Constructor con mensaje de error.
     * 
     * @param message Mensaje descriptivo del error
     */
    public BusinessException(String message) {
        super(message);
        this.errorCode = "BUSINESS_ERROR";
    }

    /**
     * Constructor con mensaje y código de error personalizado.
     * 
     * @param message Mensaje descriptivo del error
     * @param errorCode Código único del error para trazabilidad
     */
    public BusinessException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * Constructor con mensaje y causa raíz.
     * 
     * @param message Mensaje descriptivo del error
     * @param cause Excepción que causó este error
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "BUSINESS_ERROR";
    }

    /**
     * Constructor completo con mensaje, código y causa.
     * 
     * @param message Mensaje descriptivo del error
     * @param errorCode Código único del error
     * @param cause Excepción que causó este error
     */
    public BusinessException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    /**
     * Obtiene el código de error asociado.
     * 
     * @return Código de error único
     */
    public String getErrorCode() {
        return errorCode;
    }
}
