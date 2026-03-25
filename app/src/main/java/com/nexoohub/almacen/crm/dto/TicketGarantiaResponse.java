package com.nexoohub.almacen.crm.dto;

import java.time.LocalDateTime;
import java.util.List;

public class TicketGarantiaResponse {
    
    private Integer id;
    private Integer ventaId;
    private Integer clienteId;
    private String skuProducto;
    private String numeroSerie;
    private String motivoReclamo;
    private String estado;
    private String resolucion;
    private String notasInternas;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    
    // Lista anidada del historial de este ticket
    private List<HistorialGarantiaDTO> historial;

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getVentaId() { return ventaId; }
    public void setVentaId(Integer ventaId) { this.ventaId = ventaId; }
    public Integer getClienteId() { return clienteId; }
    public void setClienteId(Integer clienteId) { this.clienteId = clienteId; }
    public String getSkuProducto() { return skuProducto; }
    public void setSkuProducto(String skuProducto) { this.skuProducto = skuProducto; }
    public String getNumeroSerie() { return numeroSerie; }
    public void setNumeroSerie(String numeroSerie) { this.numeroSerie = numeroSerie; }
    public String getMotivoReclamo() { return motivoReclamo; }
    public void setMotivoReclamo(String motivoReclamo) { this.motivoReclamo = motivoReclamo; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getResolucion() { return resolucion; }
    public void setResolucion(String resolucion) { this.resolucion = resolucion; }
    public String getNotasInternas() { return notasInternas; }
    public void setNotasInternas(String notasInternas) { this.notasInternas = notasInternas; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }
    public List<HistorialGarantiaDTO> getHistorial() { return historial; }
    public void setHistorial(List<HistorialGarantiaDTO> historial) { this.historial = historial; }
}
