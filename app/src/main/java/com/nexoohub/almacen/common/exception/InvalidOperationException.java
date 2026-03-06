package com.nexoohub.almacen.common.exception;

/**
 * Excepción lanzada cuando se intenta realizar una operación inválida o no permitida.
 * 
 * <p>Ejemplos de uso:</p>
 * <ul>
 *   <li>Traspaso entre la misma sucursal (origen == destino)</li>
 *   <li>Modificar un precio con configuración financiera inexistente</li>
 *   <li>Procesar venta con método de pago inválido</li>
 *   <li>Eliminar un registro con relaciones activas</li>
 * </ul>
 * 
 * <p><b>HTTP Status recomendado:</b> 400 BAD REQUEST o 422 UNPROCESSABLE ENTITY</p>
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
public class InvalidOperationException extends BusinessException {

    private final String operation;
    private final String reason;

    /**
     * Constructor con mensaje simple.
     * 
     * @param message Mensaje descriptivo de la operación inválida
     */
    public InvalidOperationException(String message) {
        super(message, "INVALID_OPERATION");
        this.operation = "UNKNOWN";
        this.reason = message;
    }

    /**
     * Constructor con operación y razón específicas.
     * 
     * @param operation Nombre de la operación (ej: "TRASPASO", "ELIMINAR_CATEGORIA")
     * @param reason Motivo por el cual la operación es inválida
     */
    public InvalidOperationException(String operation, String reason) {
        super(String.format("Operación '%s' no permitida: %s", operation, reason), "INVALID_OPERATION");
        this.operation = operation;
        this.reason = reason;
    }

    /**
     * Constructor con mensaje personalizado, operación y razón.
     * 
     * @param message Mensaje personalizado
     * @param operation Nombre de la operación
     * @param reason Motivo de invalidez
     */
    public InvalidOperationException(String message, String operation, String reason) {
        super(message, "INVALID_OPERATION");
        this.operation = operation;
        this.reason = reason;
    }

    public String getOperation() {
        return operation;
    }

    public String getReason() {
        return reason;
    }
}
