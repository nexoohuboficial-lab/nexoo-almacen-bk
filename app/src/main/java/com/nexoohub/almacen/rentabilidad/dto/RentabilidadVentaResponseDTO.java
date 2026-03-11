package com.nexoohub.almacen.rentabilidad.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de respuesta para análisis de rentabilidad de una venta.
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
public class RentabilidadVentaResponseDTO {
    private Long id;
    private Integer ventaId;
    private LocalDateTime fechaVenta;
    private String clienteNombre;
    private String sucursalNombre;
    private BigDecimal costoTotal;
    private BigDecimal precioVentaTotal;
    private BigDecimal utilidadBruta;
    private BigDecimal margenPorcentaje;
    private Boolean ventaBajoCosto;
    private Integer cantidadItems;
    private String alertaCalidad; // "EXCELENTE", "BUENA", "REGULAR", "BAJA", "PERDIDA"

    public RentabilidadVentaResponseDTO() {}

    public RentabilidadVentaResponseDTO(
            Long id, Integer ventaId, LocalDateTime fechaVenta, 
            String clienteNombre, String sucursalNombre,
            BigDecimal costoTotal, BigDecimal precioVentaTotal,
            BigDecimal utilidadBruta, BigDecimal margenPorcentaje,
            Boolean ventaBajoCosto, Integer cantidadItems, String alertaCalidad) {
        this.id = id;
        this.ventaId = ventaId;
        this.fechaVenta = fechaVenta;
        this.clienteNombre = clienteNombre;
        this.sucursalNombre = sucursalNombre;
        this.costoTotal = costoTotal;
        this.precioVentaTotal = precioVentaTotal;
        this.utilidadBruta = utilidadBruta;
        this.margenPorcentaje = margenPorcentaje;
        this.ventaBajoCosto = ventaBajoCosto;
        this.cantidadItems = cantidadItems;
        this.alertaCalidad = alertaCalidad;
    }

    // ==========================================
    // GETTERS Y SETTERS
    // ==========================================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getVentaId() { return ventaId; }
    public void setVentaId(Integer ventaId) { this.ventaId = ventaId; }

    public LocalDateTime getFechaVenta() { return fechaVenta; }
    public void setFechaVenta(LocalDateTime fechaVenta) { this.fechaVenta = fechaVenta; }

    public String getClienteNombre() { return clienteNombre; }
    public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }

    public String getSucursalNombre() { return sucursalNombre; }
    public void setSucursalNombre(String sucursalNombre) { this.sucursalNombre = sucursalNombre; }

    public BigDecimal getCostoTotal() { return costoTotal; }
    public void setCostoTotal(BigDecimal costoTotal) { this.costoTotal = costoTotal; }

    public BigDecimal getPrecioVentaTotal() { return precioVentaTotal; }
    public void setPrecioVentaTotal(BigDecimal precioVentaTotal) { this.precioVentaTotal = precioVentaTotal; }

    public BigDecimal getUtilidadBruta() { return utilidadBruta; }
    public void setUtilidadBruta(BigDecimal utilidadBruta) { this.utilidadBruta = utilidadBruta; }

    public BigDecimal getMargenPorcentaje() { return margenPorcentaje; }
    public void setMargenPorcentaje(BigDecimal margenPorcentaje) { this.margenPorcentaje = margenPorcentaje; }

    public Boolean getVentaBajoCosto() { return ventaBajoCosto; }
    public void setVentaBajoCosto(Boolean ventaBajoCosto) { this.ventaBajoCosto = ventaBajoCosto; }

    public Integer getCantidadItems() { return cantidadItems; }
    public void setCantidadItems(Integer cantidadItems) { this.cantidadItems = cantidadItems; }

    public String getAlertaCalidad() { return alertaCalidad; }
    public void setAlertaCalidad(String alertaCalidad) { this.alertaCalidad = alertaCalidad; }
}
