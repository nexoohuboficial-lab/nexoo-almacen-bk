package com.nexoohub.almacen.common.exception;

/**
 * Excepción personalizada para cuando no se encuentra un recurso.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}