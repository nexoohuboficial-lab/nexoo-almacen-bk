package com.nexoohub.almacen.common.exception;

/**
 * Excepción lanzada cuando un recurso solicitado no existe en el sistema.
 * 
 * <p>Se utiliza para casos como: producto no encontrado, usuario no encontrado,
 * cliente no existe, etc.</p>
 * 
 * <p><b>HTTP Status recomendado:</b> 404 NOT FOUND</p>
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
public class ResourceNotFoundException extends BusinessException {

    private final String resourceType;
    private final Object resourceId;

    /**
     * Constructor con mensaje simple.
     * 
     * @param message Mensaje descriptivo del recurso no encontrado
     */
    public ResourceNotFoundException(String message) {
        super(message, "RESOURCE_NOT_FOUND");
        this.resourceType = "UNKNOWN";
        this.resourceId = null;
    }

    /**
     * Constructor con tipo de recurso e identificador.
     * 
     * @param resourceType Tipo de recurso (ej: "Usuario", "Producto", "Cliente")
     * @param resourceId Identificador del recurso no encontrado
     */
    public ResourceNotFoundException(String resourceType, Object resourceId) {
        super(String.format("%s con ID '%s' no fue encontrado", resourceType, resourceId), "RESOURCE_NOT_FOUND");
        this.resourceType = resourceType;
        this.resourceId = resourceId;
    }

    /**
     * Constructor completo con mensaje personalizado, tipo e ID.
     * 
     * @param message Mensaje personalizado
     * @param resourceType Tipo de recurso
     * @param resourceId Identificador del recurso
     */
    public ResourceNotFoundException(String message, String resourceType, Object resourceId) {
        super(message, "RESOURCE_NOT_FOUND");
        this.resourceType = resourceType;
        this.resourceId = resourceId;
    }

    /**
     * Obtiene el tipo de recurso no encontrado.
     * 
     * @return Tipo de recurso (ej: "Usuario", "Producto")
     */
    public String getResourceType() {
        return resourceType;
    }

    /**
     * Obtiene el identificador del recurso no encontrado.
     * 
     * @return ID del recurso
     */
    public Object getResourceId() {
        return resourceId;
    }
}