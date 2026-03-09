package com.nexoohub.almacen.cotizaciones.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO para solicitar la conversión de una cotización en venta
 */
public class ConvertirVentaRequestDTO {
    
    @NotBlank(message = "El método de pago es obligatorio")
    private String metodoPago; // EFECTIVO, TARJETA, TRANSFERENCIA, CREDITO
    
    private String observaciones;
    
    // Constructores
    
    public ConvertirVentaRequestDTO() {
    }
    
    public ConvertirVentaRequestDTO(String metodoPago) {
        this.metodoPago = metodoPago;
    }
    
    // Getters y Setters
    
    public String getMetodoPago() {
        return metodoPago;
    }
    
    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }
    
    public String getObservaciones() {
        return observaciones;
    }
    
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
