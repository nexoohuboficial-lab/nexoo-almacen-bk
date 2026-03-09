package com.nexoohub.almacen.cotizaciones.dto;

import java.math.BigDecimal;

/**
 * DTO para responder con estadísticas de cotizaciones
 */
public class EstadisticasCotizacionDTO {
    
    private Long totalCotizaciones;
    private Long cotizacionesBorrador;
    private Long cotizacionesEnviadas;
    private Long cotizacionesAceptadas;
    private Long cotizacionesRechazadas;
    private Long cotizacionesVencidas;
    private Long cotizacionesConvertidas;
    
    private BigDecimal valorTotalBorrador;
    private BigDecimal valorTotalEnviadas;
    private BigDecimal valorTotalAceptadas;
    private BigDecimal valorTotalConvertidas;
    
    private Double tasaConversion; // Porcentaje de cotizaciones convertidas vs total
    private Double tasaAceptacion; // Porcentaje de cotizaciones aceptadas vs enviadas
    private Double tasaRechazo; // Porcentaje de cotizaciones rechazadas vs enviadas
    
    // Constructores
    
    public EstadisticasCotizacionDTO() {
    }
    
    // Getters y Setters
    
    public Long getTotalCotizaciones() {
        return totalCotizaciones;
    }
    
    public void setTotalCotizaciones(Long totalCotizaciones) {
        this.totalCotizaciones = totalCotizaciones;
    }
    
    public Long getCotizacionesBorrador() {
        return cotizacionesBorrador;
    }
    
    public void setCotizacionesBorrador(Long cotizacionesBorrador) {
        this.cotizacionesBorrador = cotizacionesBorrador;
    }
    
    public Long getCotizacionesEnviadas() {
        return cotizacionesEnviadas;
    }
    
    public void setCotizacionesEnviadas(Long cotizacionesEnviadas) {
        this.cotizacionesEnviadas = cotizacionesEnviadas;
    }
    
    public Long getCotizacionesAceptadas() {
        return cotizacionesAceptadas;
    }
    
    public void setCotizacionesAceptadas(Long cotizacionesAceptadas) {
        this.cotizacionesAceptadas = cotizacionesAceptadas;
    }
    
    public Long getCotizacionesRechazadas() {
        return cotizacionesRechazadas;
    }
    
    public void setCotizacionesRechazadas(Long cotizacionesRechazadas) {
        this.cotizacionesRechazadas = cotizacionesRechazadas;
    }
    
    public Long getCotizacionesVencidas() {
        return cotizacionesVencidas;
    }
    
    public void setCotizacionesVencidas(Long cotizacionesVencidas) {
        this.cotizacionesVencidas = cotizacionesVencidas;
    }
    
    public Long getCotizacionesConvertidas() {
        return cotizacionesConvertidas;
    }
    
    public void setCotizacionesConvertidas(Long cotizacionesConvertidas) {
        this.cotizacionesConvertidas = cotizacionesConvertidas;
    }
    
    public BigDecimal getValorTotalBorrador() {
        return valorTotalBorrador;
    }
    
    public void setValorTotalBorrador(BigDecimal valorTotalBorrador) {
        this.valorTotalBorrador = valorTotalBorrador;
    }
    
    public BigDecimal getValorTotalEnviadas() {
        return valorTotalEnviadas;
    }
    
    public void setValorTotalEnviadas(BigDecimal valorTotalEnviadas) {
        this.valorTotalEnviadas = valorTotalEnviadas;
    }
    
    public BigDecimal getValorTotalAceptadas() {
        return valorTotalAceptadas;
    }
    
    public void setValorTotalAceptadas(BigDecimal valorTotalAceptadas) {
        this.valorTotalAceptadas = valorTotalAceptadas;
    }
    
    public BigDecimal getValorTotalConvertidas() {
        return valorTotalConvertidas;
    }
    
    public void setValorTotalConvertidas(BigDecimal valorTotalConvertidas) {
        this.valorTotalConvertidas = valorTotalConvertidas;
    }
    
    public Double getTasaConversion() {
        return tasaConversion;
    }
    
    public void setTasaConversion(Double tasaConversion) {
        this.tasaConversion = tasaConversion;
    }
    
    public Double getTasaAceptacion() {
        return tasaAceptacion;
    }
    
    public void setTasaAceptacion(Double tasaAceptacion) {
        this.tasaAceptacion = tasaAceptacion;
    }
    
    public Double getTasaRechazo() {
        return tasaRechazo;
    }
    
    public void setTasaRechazo(Double tasaRechazo) {
        this.tasaRechazo = tasaRechazo;
    }
}
