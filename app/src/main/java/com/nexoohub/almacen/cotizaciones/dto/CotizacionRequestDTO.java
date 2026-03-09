package com.nexoohub.almacen.cotizaciones.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO para crear o actualizar una cotización
 */
public class CotizacionRequestDTO {
    
    @NotNull(message = "El cliente es obligatorio")
    private Integer clienteId;
    
    @NotNull(message = "La sucursal es obligatoria")
    private Integer sucursalId;
    
    private Integer vendedorId;
    
    @NotNull(message = "La fecha de validez es obligatoria")
    private LocalDate fechaValidez;
    
    private String notas;
    
    private String terminosCondiciones;
    
    private String observacionesInternas;
    
    @NotEmpty(message = "Debe incluir al menos un detalle en la cotización")
    @Valid
    private List<DetalleCotizacionDTO> detalles;
    
    // Constructores
    
    public CotizacionRequestDTO() {
    }
    
    public CotizacionRequestDTO(Integer clienteId, Integer sucursalId, LocalDate fechaValidez, List<DetalleCotizacionDTO> detalles) {
        this.clienteId = clienteId;
        this.sucursalId = sucursalId;
        this.fechaValidez = fechaValidez;
        this.detalles = detalles;
    }
    
    // Getters y Setters
    
    public Integer getClienteId() {
        return clienteId;
    }
    
    public void setClienteId(Integer clienteId) {
        this.clienteId = clienteId;
    }
    
    public Integer getSucursalId() {
        return sucursalId;
    }
    
    public void setSucursalId(Integer sucursalId) {
        this.sucursalId = sucursalId;
    }
    
    public Integer getVendedorId() {
        return vendedorId;
    }
    
    public void setVendedorId(Integer vendedorId) {
        this.vendedorId = vendedorId;
    }
    
    public LocalDate getFechaValidez() {
        return fechaValidez;
    }
    
    public void setFechaValidez(LocalDate fechaValidez) {
        this.fechaValidez = fechaValidez;
    }
    
    public String getNotas() {
        return notas;
    }
    
    public void setNotas(String notas) {
        this.notas = notas;
    }
    
    public String getTerminosCondiciones() {
        return terminosCondiciones;
    }
    
    public void setTerminosCondiciones(String terminosCondiciones) {
        this.terminosCondiciones = terminosCondiciones;
    }
    
    public String getObservacionesInternas() {
        return observacionesInternas;
    }
    
    public void setObservacionesInternas(String observacionesInternas) {
        this.observacionesInternas = observacionesInternas;
    }
    
    public List<DetalleCotizacionDTO> getDetalles() {
        return detalles;
    }
    
    public void setDetalles(List<DetalleCotizacionDTO> detalles) {
        this.detalles = detalles;
    }
}
