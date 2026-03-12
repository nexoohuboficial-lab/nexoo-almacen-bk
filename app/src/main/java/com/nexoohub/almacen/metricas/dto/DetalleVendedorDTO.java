package com.nexoohub.almacen.metricas.dto;

import java.math.BigDecimal;

/**
 * DTO con el detalle de rendimiento de un vendedor.
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
public class DetalleVendedorDTO {

    private Integer vendedorId;
    private String nombre;
    private String apellidos;
    private String nombreCompleto;
    private String puesto;
    private Integer sucursalId;
    private String nombreSucursal;

    // Métricas de ventas
    private BigDecimal totalVentas;
    private Integer numeroTransacciones;
    private BigDecimal ticketPromedio;
    private BigDecimal porcentajeVentasTotal;
    private Integer ranking;
    private String clasificacion; // "TOP", "BUENO", "REGULAR", "BAJO"

    // Métricas de clientes
    private Integer clientesAtendidos;
    private Integer clientesNuevosAtendidos;
    private BigDecimal tasaConversion; // % de clientes que compraron

    // Comparación
    private BigDecimal ventasPeriodoAnterior;
    private BigDecimal crecimiento;

    // Comisiones estimadas
    private BigDecimal comisionEstimada;
    private BigDecimal porcentajeComision;

    public DetalleVendedorDTO() {}

    public DetalleVendedorDTO(Integer vendedorId, String nombre, String apellidos) {
        this.vendedorId = vendedorId;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.nombreCompleto = nombre + " " + apellidos;
    }

    // ==================== GETTERS Y SETTERS ====================

    public Integer getVendedorId() {
        return vendedorId;
    }

    public void setVendedorId(Integer vendedorId) {
        this.vendedorId = vendedorId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getPuesto() {
        return puesto;
    }

    public void setPuesto(String puesto) {
        this.puesto = puesto;
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

    public BigDecimal getTotalVentas() {
        return totalVentas;
    }

    public void setTotalVentas(BigDecimal totalVentas) {
        this.totalVentas = totalVentas;
    }

    public Integer getNumeroTransacciones() {
        return numeroTransacciones;
    }

    public void setNumeroTransacciones(Integer numeroTransacciones) {
        this.numeroTransacciones = numeroTransacciones;
    }

    public BigDecimal getTicketPromedio() {
        return ticketPromedio;
    }

    public void setTicketPromedio(BigDecimal ticketPromedio) {
        this.ticketPromedio = ticketPromedio;
    }

    public BigDecimal getPorcentajeVentasTotal() {
        return porcentajeVentasTotal;
    }

    public void setPorcentajeVentasTotal(BigDecimal porcentajeVentasTotal) {
        this.porcentajeVentasTotal = porcentajeVentasTotal;
    }

    public Integer getRanking() {
        return ranking;
    }

    public void setRanking(Integer ranking) {
        this.ranking = ranking;
    }

    public String getClasificacion() {
        return clasificacion;
    }

    public void setClasificacion(String clasificacion) {
        this.clasificacion = clasificacion;
    }

    public Integer getClientesAtendidos() {
        return clientesAtendidos;
    }

    public void setClientesAtendidos(Integer clientesAtendidos) {
        this.clientesAtendidos = clientesAtendidos;
    }

    public Integer getClientesNuevosAtendidos() {
        return clientesNuevosAtendidos;
    }

    public void setClientesNuevosAtendidos(Integer clientesNuevosAtendidos) {
        this.clientesNuevosAtendidos = clientesNuevosAtendidos;
    }

    public BigDecimal getTasaConversion() {
        return tasaConversion;
    }

    public void setTasaConversion(BigDecimal tasaConversion) {
        this.tasaConversion = tasaConversion;
    }

    public BigDecimal getVentasPeriodoAnterior() {
        return ventasPeriodoAnterior;
    }

    public void setVentasPeriodoAnterior(BigDecimal ventasPeriodoAnterior) {
        this.ventasPeriodoAnterior = ventasPeriodoAnterior;
    }

    public BigDecimal getCrecimiento() {
        return crecimiento;
    }

    public void setCrecimiento(BigDecimal crecimiento) {
        this.crecimiento = crecimiento;
    }

    public BigDecimal getComisionEstimada() {
        return comisionEstimada;
    }

    public void setComisionEstimada(BigDecimal comisionEstimada) {
        this.comisionEstimada = comisionEstimada;
    }

    public BigDecimal getPorcentajeComision() {
        return porcentajeComision;
    }

    public void setPorcentajeComision(BigDecimal porcentajeComision) {
        this.porcentajeComision = porcentajeComision;
    }
}
