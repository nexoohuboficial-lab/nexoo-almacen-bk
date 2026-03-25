package com.nexoohub.almacen.pos.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class FacturaFiscalResponse {

    private Integer facturaId;
    private Integer ventaId;
    private String uuid;
    private String estatus;
    private LocalDateTime fechaEmision;
    private BigDecimal montoTotal;
    private String moneda;
    private String usoCfdi;
    private String metodoPago;
    private String formaPago;
    private String rfcReceptor;
    private String razonSocialReceptor;
    private String urlPdf;
    private String xmlGenerado;
    private String motivoCancelacion;
    private String acuseCancelacion;

    // Getters y Setters
    public Integer getFacturaId() { return facturaId; }
    public void setFacturaId(Integer facturaId) { this.facturaId = facturaId; }
    public Integer getVentaId() { return ventaId; }
    public void setVentaId(Integer ventaId) { this.ventaId = ventaId; }
    public String getUuid() { return uuid; }
    public void setUuid(String uuid) { this.uuid = uuid; }
    public String getEstatus() { return estatus; }
    public void setEstatus(String estatus) { this.estatus = estatus; }
    public LocalDateTime getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(LocalDateTime fechaEmision) { this.fechaEmision = fechaEmision; }
    public BigDecimal getMontoTotal() { return montoTotal; }
    public void setMontoTotal(BigDecimal montoTotal) { this.montoTotal = montoTotal; }
    public String getMoneda() { return moneda; }
    public void setMoneda(String moneda) { this.moneda = moneda; }
    public String getUsoCfdi() { return usoCfdi; }
    public void setUsoCfdi(String usoCfdi) { this.usoCfdi = usoCfdi; }
    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }
    public String getFormaPago() { return formaPago; }
    public void setFormaPago(String formaPago) { this.formaPago = formaPago; }
    public String getRfcReceptor() { return rfcReceptor; }
    public void setRfcReceptor(String rfcReceptor) { this.rfcReceptor = rfcReceptor; }
    public String getRazonSocialReceptor() { return razonSocialReceptor; }
    public void setRazonSocialReceptor(String razonSocialReceptor) { this.razonSocialReceptor = razonSocialReceptor; }
    public String getUrlPdf() { return urlPdf; }
    public void setUrlPdf(String urlPdf) { this.urlPdf = urlPdf; }
    public String getXmlGenerado() { return xmlGenerado; }
    public void setXmlGenerado(String xmlGenerado) { this.xmlGenerado = xmlGenerado; }
    public String getMotivoCancelacion() { return motivoCancelacion; }
    public void setMotivoCancelacion(String motivoCancelacion) { this.motivoCancelacion = motivoCancelacion; }
    public String getAcuseCancelacion() { return acuseCancelacion; }
    public void setAcuseCancelacion(String acuseCancelacion) { this.acuseCancelacion = acuseCancelacion; }
}
