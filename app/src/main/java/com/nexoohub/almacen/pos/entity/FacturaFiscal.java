package com.nexoohub.almacen.pos.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad que representa la factura electrónica timbrada (CFDI) asociada a una venta.
 *
 * @author NexooHub Development Team
 * @since 1.0 — POS-03
 */
@Entity
@Table(name = "factura_fiscal", indexes = {
    @Index(name = "idx_factura_fiscal_venta", columnList = "venta_id"),
    @Index(name = "idx_factura_fiscal_cliente", columnList = "cliente_id"),
    @Index(name = "idx_factura_fiscal_uuid", columnList = "uuid", unique = true),
    @Index(name = "idx_factura_fiscal_estatus", columnList = "estatus")
})
public class FacturaFiscal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "venta_id", nullable = false)
    private Integer ventaId;

    @Column(name = "cliente_id", nullable = false)
    private Integer clienteId;

    @Column(name = "uuid", nullable = false, length = 36, unique = true)
    private String uuid;

    /** TIMBRADA | CANCELADA | ERROR */
    @Column(name = "estatus", nullable = false, length = 20)
    private String estatus;

    @Column(name = "fecha_emision", nullable = false)
    private LocalDateTime fechaEmision;

    @Column(name = "monto_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal montoTotal;

    @Column(name = "moneda", nullable = false, length = 3)
    private String moneda = "MXN";

    @Column(name = "uso_cfdi", nullable = false, length = 5)
    private String usoCfdi;

    /** PUE (Pago en una sola exhibición) | PPD (Pago en parcialidades o diferido) */
    @Column(name = "metodo_pago", nullable = false, length = 5)
    private String metodoPago;

    /** Forma de pago SAT: 01 (Efectivo), 03 (Transferencia), 04 (Tarjeta), etc. */
    @Column(name = "forma_pago", nullable = false, length = 5)
    private String formaPago;

    // Datos del Receptor (Cliente) copiados al momento de timbrar
    @Column(name = "rfc_receptor", nullable = false, length = 13)
    private String rfcReceptor;

    @Column(name = "razon_social_receptor", nullable = false, length = 255)
    private String razonSocialReceptor;

    @Column(name = "codigo_postal_receptor", nullable = false, length = 5)
    private String codigoPostalReceptor;

    @Column(name = "regimen_fiscal_receptor", nullable = false, length = 10)
    private String regimenFiscalReceptor;

    @Column(name = "xml_generado", columnDefinition = "TEXT")
    private String xmlGenerado;

    @Column(name = "url_pdf", length = 500)
    private String urlPdf;

    /** Motivo SAT: 01, 02, 03, 04... */
    @Column(name = "motivo_cancelacion", length = 50)
    private String motivoCancelacion;

    @Column(name = "acuse_cancelacion", columnDefinition = "TEXT")
    private String acuseCancelacion;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "usuario_creacion", length = 50)
    private String usuarioCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @Column(name = "usuario_actualizacion", length = 50)
    private String usuarioActualizacion;

    @PrePersist
    protected void onCreate() {
        if (fechaCreacion == null) fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getVentaId() { return ventaId; }
    public void setVentaId(Integer ventaId) { this.ventaId = ventaId; }
    public Integer getClienteId() { return clienteId; }
    public void setClienteId(Integer clienteId) { this.clienteId = clienteId; }
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
    public String getCodigoPostalReceptor() { return codigoPostalReceptor; }
    public void setCodigoPostalReceptor(String codigoPostalReceptor) { this.codigoPostalReceptor = codigoPostalReceptor; }
    public String getRegimenFiscalReceptor() { return regimenFiscalReceptor; }
    public void setRegimenFiscalReceptor(String regimenFiscalReceptor) { this.regimenFiscalReceptor = regimenFiscalReceptor; }
    public String getXmlGenerado() { return xmlGenerado; }
    public void setXmlGenerado(String xmlGenerado) { this.xmlGenerado = xmlGenerado; }
    public String getUrlPdf() { return urlPdf; }
    public void setUrlPdf(String urlPdf) { this.urlPdf = urlPdf; }
    public String getMotivoCancelacion() { return motivoCancelacion; }
    public void setMotivoCancelacion(String motivoCancelacion) { this.motivoCancelacion = motivoCancelacion; }
    public String getAcuseCancelacion() { return acuseCancelacion; }
    public void setAcuseCancelacion(String acuseCancelacion) { this.acuseCancelacion = acuseCancelacion; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public String getUsuarioCreacion() { return usuarioCreacion; }
    public void setUsuarioCreacion(String usuarioCreacion) { this.usuarioCreacion = usuarioCreacion; }
    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }
    public String getUsuarioActualizacion() { return usuarioActualizacion; }
    public void setUsuarioActualizacion(String usuarioActualizacion) { this.usuarioActualizacion = usuarioActualizacion; }
}
