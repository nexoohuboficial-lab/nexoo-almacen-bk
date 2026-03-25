package com.nexoohub.almacen.pos.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad para registrar el log de transacciones bancarias (PinPad).
 *
 * @author NexooHub Development Team
 * @since 1.0 — POS-02
 */
@Entity
@Table(name = "log_transaccion_bancaria", indexes = {
    @Index(name = "idx_log_trans_banc_ref", columnList = "referencia_venta"),
    @Index(name = "idx_log_trans_banc_estatus", columnList = "estatus"),
    @Index(name = "idx_log_trans_banc_fecha", columnList = "fecha_transaccion")
})
public class LogTransaccionBancaria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "referencia_venta", nullable = false, length = 50)
    private String referenciaVenta;

    @Column(name = "monto", nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    /** VENTA | DEVOLUCION | CANCELACION */
    @Column(name = "tipo_operacion", nullable = false, length = 20)
    private String tipoOperacion;

    /** PROCESANDO | APROBADO | RECHAZADO | CANCELADO | ERROR */
    @Column(name = "estatus", nullable = false, length = 20)
    private String estatus;

    @Column(name = "autorizacion_banco", length = 50)
    private String autorizacionBanco;

    @Column(name = "terminal_id", nullable = false, length = 50)
    private String terminalId;

    @Column(name = "tarjeta_terminacion", length = 4)
    private String tarjetaTerminacion;

    @Column(name = "marca_tarjeta", length = 20)
    private String marcaTarjeta;

    @Column(name = "mensaje_respuesta", length = 255)
    private String mensajeRespuesta;

    @Column(name = "xml_request", columnDefinition = "TEXT")
    private String xmlRequest;

    @Column(name = "xml_response", columnDefinition = "TEXT")
    private String xmlResponse;

    @Column(name = "fecha_transaccion", nullable = false)
    private LocalDateTime fechaTransaccion;

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
        if (fechaTransaccion == null) fechaTransaccion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getReferenciaVenta() { return referenciaVenta; }
    public void setReferenciaVenta(String referenciaVenta) { this.referenciaVenta = referenciaVenta; }

    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }

    public String getTipoOperacion() { return tipoOperacion; }
    public void setTipoOperacion(String tipoOperacion) { this.tipoOperacion = tipoOperacion; }

    public String getEstatus() { return estatus; }
    public void setEstatus(String estatus) { this.estatus = estatus; }

    public String getAutorizacionBanco() { return autorizacionBanco; }
    public void setAutorizacionBanco(String autorizacionBanco) { this.autorizacionBanco = autorizacionBanco; }

    public String getTerminalId() { return terminalId; }
    public void setTerminalId(String terminalId) { this.terminalId = terminalId; }

    public String getTarjetaTerminacion() { return tarjetaTerminacion; }
    public void setTarjetaTerminacion(String tarjetaTerminacion) { this.tarjetaTerminacion = tarjetaTerminacion; }

    public String getMarcaTarjeta() { return marcaTarjeta; }
    public void setMarcaTarjeta(String marcaTarjeta) { this.marcaTarjeta = marcaTarjeta; }

    public String getMensajeRespuesta() { return mensajeRespuesta; }
    public void setMensajeRespuesta(String mensajeRespuesta) { this.mensajeRespuesta = mensajeRespuesta; }

    public String getXmlRequest() { return xmlRequest; }
    public void setXmlRequest(String xmlRequest) { this.xmlRequest = xmlRequest; }

    public String getXmlResponse() { return xmlResponse; }
    public void setXmlResponse(String xmlResponse) { this.xmlResponse = xmlResponse; }

    public LocalDateTime getFechaTransaccion() { return fechaTransaccion; }
    public void setFechaTransaccion(LocalDateTime fechaTransaccion) { this.fechaTransaccion = fechaTransaccion; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public String getUsuarioCreacion() { return usuarioCreacion; }
    public void setUsuarioCreacion(String usuarioCreacion) { this.usuarioCreacion = usuarioCreacion; }

    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }

    public String getUsuarioActualizacion() { return usuarioActualizacion; }
    public void setUsuarioActualizacion(String usuarioActualizacion) { this.usuarioActualizacion = usuarioActualizacion; }
}
