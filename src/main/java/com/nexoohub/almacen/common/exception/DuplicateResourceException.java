package com.nexoohub.almacen.common.exception;

/**
 * Excepción lanzada cuando se intenta crear un recurso que ya existe.
 * 
 * <p>Casos de uso típicos:</p>
 * <ul>
 *   <li>Username duplicado al crear usuario</li>
 *   <li>SKU duplicado al registrar producto</li>
 *   <li>RFC duplicado al crear cliente</li>
 * </ul>
 * 
 * <p><b>HTTP Status recomendado:</b> 409 CONFLICT</p>
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
public class DuplicateResourceException extends BusinessException {

    private final String resourceType;
    private final String fieldName;
    private final Object fieldValue;

    /**
     * Constructor con mensaje simple.
     * 
     * @param message Mensaje descriptivo del conflicto
     */
    public DuplicateResourceException(String message) {
        super(message, "DUPLICATE_RESOURCE");
        this.resourceType = "UNKNOWN";
        this.fieldName = "UNKNOWN";
        this.fieldValue = null;
    }

    /**
     * Constructor con detalles del recurso duplicado.
     * 
     * @param resourceType Tipo de recurso (ej: "Usuario", "Producto")
     * @param fieldName Campo que está duplicado (ej: "username", "sku")
     * @param fieldValue Valor duplicado
     */
    public DuplicateResourceException(String resourceType, String fieldName, Object fieldValue) {
        super(String.format("%s con %s '%s' ya existe en el sistema", resourceType, fieldName, fieldValue), 
              "DUPLICATE_RESOURCE");
        this.resourceType = resourceType;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    /**
     * Constructor con mensaje personalizado y detalles.
     * 
     * @param message Mensaje personalizado
     * @param resourceType Tipo de recurso
     * @param fieldName Campo duplicado
     * @param fieldValue Valor duplicado
     */
    public DuplicateResourceException(String message, String resourceType, String fieldName, Object fieldValue) {
        super(message, "DUPLICATE_RESOURCE");
        this.resourceType = resourceType;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    public String getResourceType() {
        return resourceType;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Object getFieldValue() {
        return fieldValue;
    }
}
