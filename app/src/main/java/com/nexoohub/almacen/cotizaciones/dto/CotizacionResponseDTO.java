package com.nexoohub.almacen.cotizaciones.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para responder con los datos de una cotización
 */
public class CotizacionResponseDTO {
    
    private Long id;
    private String folio;
    private Integer clienteId;
    private String nombreCliente;
    private Integer sucursalId;
    private String nombreSucursal;
    private Integer vendedorId;
    private String nombreVendedor;
    private String estado;
    private LocalDateTime fechaCotizacion;
    private LocalDate fechaValidez;
    private BigDecimal total;
    private BigDecimal subtotal;
    private BigDecimal iva;
    private BigDecimal descuentoTotal;
    private String notas;
    private String terminosCondiciones;
    private String observacionesInternas;
    private LocalDateTime fechaAceptacion;
    private LocalDateTime fechaRechazo;
    private String motivoRechazo;
    private Integer ventaId;
    private LocalDateTime fechaConversion;
    private Boolean vencida;
    private Boolean puedeConvertirse;
    private List<DetalleCotizacionDTO> detalles;
    
    // Constructores
    
    public CotizacionResponseDTO() {
    }
    
    // Getters y Setters
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getFolio() {
        return folio;
    }
    
    public void setFolio(String folio) {
        this.folio = folio;
    }
    
    public Integer getClienteId() {
        return clienteId;
    }
    
    public void setClienteId(Integer clienteId) {
        this.clienteId = clienteId;
    }
    
    public String getNombreCliente() {
        return nombreCliente;
    }
    
    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }
    
    public Integer getSucursalId() {
        return sucursalId;
    }
    
    public void setSucursalId(Integer sucursalId) {
        this.sucursalId = sucursalId;
    }
    
    public String getNombreSucursal() {
        return nombreSucursal;
    }
    
    public void setNombreSucursal(String nombreSucursal) {
        this.nombreSucursal = nombreSucursal;
    }
    
    public Integer getVendedorId() {
        return vendedorId;
    }
    
    public void setVendedorId(Integer vendedorId) {
        this.vendedorId = vendedorId;
    }
    
    public String getNombreVendedor() {
        return nombreVendedor;
    }
    
    public void setNombreVendedor(String nombreVendedor) {
        this.nombreVendedor = nombreVendedor;
    }
    
    public String getEstado() {
        return estado;
    }
    
    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    public LocalDateTime getFechaCotizacion() {
        return fechaCotizacion;
    }
    
    public void setFechaCotizacion(LocalDateTime fechaCotizacion) {
        this.fechaCotizacion = fechaCotizacion;
    }
    
    public LocalDate getFechaValidez() {
        return fechaValidez;
    }
    
    public void setFechaValidez(LocalDate fechaValidez) {
        this.fechaValidez = fechaValidez;
    }
    
    public BigDecimal getTotal() {
        return total;
    }
    
    public void setTotal(BigDecimal total) {
        this.total = total;
    }
    
    public BigDecimal getSubtotal() {
        return subtotal;
    }
    
    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
    
    public BigDecimal getIva() {
        return iva;
    }
    
    public void setIva(BigDecimal iva) {
        this.iva = iva;
    }
    
    public BigDecimal getDescuentoTotal() {
        return descuentoTotal;
    }
    
    public void setDescuentoTotal(BigDecimal descuentoTotal) {
        this.descuentoTotal = descuentoTotal;
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
    
    public LocalDateTime getFechaAceptacion() {
        return fechaAceptacion;
    }
    
    public void setFechaAceptacion(LocalDateTime fechaAceptacion) {
        this.fechaAceptacion = fechaAceptacion;
    }
    
    public LocalDateTime getFechaRechazo() {
        return fechaRechazo;
    }
    
    public void setFechaRechazo(LocalDateTime fechaRechazo) {
        this.fechaRechazo = fechaRechazo;
    }
    
    public String getMotivoRechazo() {
        return motivoRechazo;
    }
    
    public void setMotivoRechazo(String motivoRechazo) {
        this.motivoRechazo = motivoRechazo;
    }
    
    public Integer getVentaId() {
        return ventaId;
    }
    
    public void setVentaId(Integer ventaId) {
        this.ventaId = ventaId;
    }
    
    public LocalDateTime getFechaConversion() {
        return fechaConversion;
    }
    
    public void setFechaConversion(LocalDateTime fechaConversion) {
        this.fechaConversion = fechaConversion;
    }
    
    public Boolean getVencida() {
        return vencida;
    }
    
    public void setVencida(Boolean vencida) {
        this.vencida = vencida;
    }
    
    public Boolean getPuedeConvertirse() {
        return puedeConvertirse;
    }
    
    public void setPuedeConvertirse(Boolean puedeConvertirse) {
        this.puedeConvertirse = puedeConvertirse;
    }
    
    public List<DetalleCotizacionDTO> getDetalles() {
        return detalles;
    }
    
    public void setDetalles(List<DetalleCotizacionDTO> detalles) {
        this.detalles = detalles;
    }
}
