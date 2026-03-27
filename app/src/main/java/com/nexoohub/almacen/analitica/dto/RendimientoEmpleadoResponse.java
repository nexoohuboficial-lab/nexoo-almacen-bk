package com.nexoohub.almacen.analitica.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class RendimientoEmpleadoResponse {

    private Integer empleadoId;
    private String  nombreEmpleado;
    private String  puesto;
    private Integer mes;
    private Integer anio;

    // Ventas
    private Integer    totalVentas;
    private BigDecimal montoTotalVentas;
    private BigDecimal ticketPromedio;

    // Cotizaciones / Conversión
    private Integer    totalCotizaciones;
    private Integer    cotizacionesConvertidas;
    private BigDecimal tasaConversion;

    // Devoluciones
    private Integer    totalDevoluciones;
    private BigDecimal montoDevoluciones;
    private BigDecimal tasaDevolucion;

    // Productividad
    private Integer    horaPico;

    private LocalDateTime fechaCalculo;

    // ── Getters & Setters ─────────────────────────────────────────
    public Integer getEmpleadoId() { return empleadoId; }
    public void setEmpleadoId(Integer empleadoId) { this.empleadoId = empleadoId; }

    public String getNombreEmpleado() { return nombreEmpleado; }
    public void setNombreEmpleado(String nombreEmpleado) { this.nombreEmpleado = nombreEmpleado; }

    public String getPuesto() { return puesto; }
    public void setPuesto(String puesto) { this.puesto = puesto; }

    public Integer getMes() { return mes; }
    public void setMes(Integer mes) { this.mes = mes; }

    public Integer getAnio() { return anio; }
    public void setAnio(Integer anio) { this.anio = anio; }

    public Integer getTotalVentas() { return totalVentas; }
    public void setTotalVentas(Integer totalVentas) { this.totalVentas = totalVentas; }

    public BigDecimal getMontoTotalVentas() { return montoTotalVentas; }
    public void setMontoTotalVentas(BigDecimal montoTotalVentas) { this.montoTotalVentas = montoTotalVentas; }

    public BigDecimal getTicketPromedio() { return ticketPromedio; }
    public void setTicketPromedio(BigDecimal ticketPromedio) { this.ticketPromedio = ticketPromedio; }

    public Integer getTotalCotizaciones() { return totalCotizaciones; }
    public void setTotalCotizaciones(Integer totalCotizaciones) { this.totalCotizaciones = totalCotizaciones; }

    public Integer getCotizacionesConvertidas() { return cotizacionesConvertidas; }
    public void setCotizacionesConvertidas(Integer cotizacionesConvertidas) { this.cotizacionesConvertidas = cotizacionesConvertidas; }

    public BigDecimal getTasaConversion() { return tasaConversion; }
    public void setTasaConversion(BigDecimal tasaConversion) { this.tasaConversion = tasaConversion; }

    public Integer getTotalDevoluciones() { return totalDevoluciones; }
    public void setTotalDevoluciones(Integer totalDevoluciones) { this.totalDevoluciones = totalDevoluciones; }

    public BigDecimal getMontoDevoluciones() { return montoDevoluciones; }
    public void setMontoDevoluciones(BigDecimal montoDevoluciones) { this.montoDevoluciones = montoDevoluciones; }

    public BigDecimal getTasaDevolucion() { return tasaDevolucion; }
    public void setTasaDevolucion(BigDecimal tasaDevolucion) { this.tasaDevolucion = tasaDevolucion; }

    public Integer getHoraPico() { return horaPico; }
    public void setHoraPico(Integer horaPico) { this.horaPico = horaPico; }

    public LocalDateTime getFechaCalculo() { return fechaCalculo; }
    public void setFechaCalculo(LocalDateTime fechaCalculo) { this.fechaCalculo = fechaCalculo; }
}
