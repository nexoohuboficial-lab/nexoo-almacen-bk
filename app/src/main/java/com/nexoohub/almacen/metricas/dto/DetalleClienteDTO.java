package com.nexoohub.almacen.metricas.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO con el detalle de comportamiento y valor de un cliente.
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
public class DetalleClienteDTO {

    private Integer clienteId;
    private String nombre;
    private String rfc;
    private String telefono;
    private String email;
    private Integer tipoClienteId;
    private String tipoClienteNombre;

    // Estado del cliente
    private Boolean bloqueado;
    private BigDecimal saldoPendiente;
    private String estadoCliente; // "ACTIVO", "BLOQUEADO", "CON_DEUDA"

    // Fechas
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaRegistro;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaUltimaCompra;

    // Métricas de compras
    private Integer numeroCompras;
    private BigDecimal totalCompras;
    private BigDecimal ticketPromedio;
    private Integer diasDesdeUltimaCompra;
    private BigDecimal frecuenciaCompra; // Compras por mes
    
    // Clasificación
    private String segmento; // "VIP", "FRECUENTE", "REGULAR", "OCASIONAL", "NUEVO"
    private Integer ranking;
    private BigDecimal porcentajeTotalVentas;

    // Valor de vida del cliente (LTV)
    private BigDecimal valorVidaTotal; // Total histórico gastado
    private BigDecimal valorVidaPeriodo; // Total en el período analizado
    private Integer mesesComoCliente;

    // Preferencias
    private String metodoPagoPreferido;
    private Integer ventasEfectivo;
    private Integer ventasTarjeta;
    private Integer ventasCredito;

    // Relación con vendedores
    private Integer ventasConVendedor;
    private String vendedorPrincipalNombre;

    public DetalleClienteDTO() {}

    public DetalleClienteDTO(Integer clienteId, String nombre) {
        this.clienteId = clienteId;
        this.nombre = nombre;
    }

    // ==================== GETTERS Y SETTERS ====================

    public Integer getClienteId() {
        return clienteId;
    }

    public void setClienteId(Integer clienteId) {
        this.clienteId = clienteId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRfc() {
        return rfc;
    }

    public void setRfc(String rfc) {
        this.rfc = rfc;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getTipoClienteId() {
        return tipoClienteId;
    }

    public void setTipoClienteId(Integer tipoClienteId) {
        this.tipoClienteId = tipoClienteId;
    }

    public String getTipoClienteNombre() {
        return tipoClienteNombre;
    }

    public void setTipoClienteNombre(String tipoClienteNombre) {
        this.tipoClienteNombre = tipoClienteNombre;
    }

    public Boolean getBloqueado() {
        return bloqueado;
    }

    public void setBloqueado(Boolean bloqueado) {
        this.bloqueado = bloqueado;
    }

    public BigDecimal getSaldoPendiente() {
        return saldoPendiente;
    }

    public void setSaldoPendiente(BigDecimal saldoPendiente) {
        this.saldoPendiente = saldoPendiente;
    }

    public String getEstadoCliente() {
        return estadoCliente;
    }

    public void setEstadoCliente(String estadoCliente) {
        this.estadoCliente = estadoCliente;
    }

    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDate fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public LocalDate getFechaUltimaCompra() {
        return fechaUltimaCompra;
    }

    public void setFechaUltimaCompra(LocalDate fechaUltimaCompra) {
        this.fechaUltimaCompra = fechaUltimaCompra;
    }

    public Integer getNumeroCompras() {
        return numeroCompras;
    }

    public void setNumeroCompras(Integer numeroCompras) {
        this.numeroCompras = numeroCompras;
    }

    public BigDecimal getTotalCompras() {
        return totalCompras;
    }

    public void setTotalCompras(BigDecimal totalCompras) {
        this.totalCompras = totalCompras;
    }

    public BigDecimal getTicketPromedio() {
        return ticketPromedio;
    }

    public void setTicketPromedio(BigDecimal ticketPromedio) {
        this.ticketPromedio = ticketPromedio;
    }

    public Integer getDiasDesdeUltimaCompra() {
        return diasDesdeUltimaCompra;
    }

    public void setDiasDesdeUltimaCompra(Integer diasDesdeUltimaCompra) {
        this.diasDesdeUltimaCompra = diasDesdeUltimaCompra;
    }

    public BigDecimal getFrecuenciaCompra() {
        return frecuenciaCompra;
    }

    public void setFrecuenciaCompra(BigDecimal frecuenciaCompra) {
        this.frecuenciaCompra = frecuenciaCompra;
    }

    public String getSegmento() {
        return segmento;
    }

    public void setSegmento(String segmento) {
        this.segmento = segmento;
    }

    public Integer getRanking() {
        return ranking;
    }

    public void setRanking(Integer ranking) {
        this.ranking = ranking;
    }

    public BigDecimal getPorcentajeTotalVentas() {
        return porcentajeTotalVentas;
    }

    public void setPorcentajeTotalVentas(BigDecimal porcentajeTotalVentas) {
        this.porcentajeTotalVentas = porcentajeTotalVentas;
    }

    public BigDecimal getValorVidaTotal() {
        return valorVidaTotal;
    }

    public void setValorVidaTotal(BigDecimal valorVidaTotal) {
        this.valorVidaTotal = valorVidaTotal;
    }

    public BigDecimal getValorVidaPeriodo() {
        return valorVidaPeriodo;
    }

    public void setValorVidaPeriodo(BigDecimal valorVidaPeriodo) {
        this.valorVidaPeriodo = valorVidaPeriodo;
    }

    public Integer getMesesComoCliente() {
        return mesesComoCliente;
    }

    public void setMesesComoCliente(Integer mesesComoCliente) {
        this.mesesComoCliente = mesesComoCliente;
    }

    public String getMetodoPagoPreferido() {
        return metodoPagoPreferido;
    }

    public void setMetodoPagoPreferido(String metodoPagoPreferido) {
        this.metodoPagoPreferido = metodoPagoPreferido;
    }

    public Integer getVentasEfectivo() {
        return ventasEfectivo;
    }

    public void setVentasEfectivo(Integer ventasEfectivo) {
        this.ventasEfectivo = ventasEfectivo;
    }

    public Integer getVentasTarjeta() {
        return ventasTarjeta;
    }

    public void setVentasTarjeta(Integer ventasTarjeta) {
        this.ventasTarjeta = ventasTarjeta;
    }

    public Integer getVentasCredito() {
        return ventasCredito;
    }

    public void setVentasCredito(Integer ventasCredito) {
        this.ventasCredito = ventasCredito;
    }

    public Integer getVentasConVendedor() {
        return ventasConVendedor;
    }

    public void setVentasConVendedor(Integer ventasConVendedor) {
        this.ventasConVendedor = ventasConVendedor;
    }

    public String getVendedorPrincipalNombre() {
        return vendedorPrincipalNombre;
    }

    public void setVendedorPrincipalNombre(String vendedorPrincipalNombre) {
        this.vendedorPrincipalNombre = vendedorPrincipalNombre;
    }
}
