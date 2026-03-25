package com.nexoohub.almacen.pos.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad que representa un batch o lote de ventas sincronizadas 
 * desde una caja offline.
 *
 * @author NexooHub Development Team
 * @since 1.0 — POS-04
 */
@Entity
@Table(name = "lote_sincronizacion")
public class LoteSincronizacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "codigo_lote", nullable = false, unique = true, length = 50)
    private String codigoLote;

    @Column(name = "sucursal_id", nullable = false)
    private Integer sucursalId;

    @Column(name = "caja_id", nullable = false)
    private Integer cajaId;

    @Column(name = "usuario_id", nullable = false)
    private Integer usuarioId;

    @Column(name = "fecha_generacion", nullable = false)
    private LocalDateTime fechaGeneracion;

    /** PENDIENTE | PROCESADO | FALLIDO */
    @Column(name = "estatus", nullable = false, length = 20)
    private String estatus;

    @Column(name = "total_ventas", nullable = false)
    private Integer totalVentas = 0;

    @Column(name = "ventas_procesadas", nullable = false)
    private Integer ventasProcesadas = 0;

    @Column(name = "monto_total_lote", nullable = false, precision = 10, scale = 2)
    private BigDecimal montoTotalLote = BigDecimal.ZERO;

    @Column(name = "payload_json", nullable = false, columnDefinition = "TEXT")
    private String payloadJson;

    @Column(name = "errores_detalle", columnDefinition = "TEXT")
    private String erroresDetalle;

    @Column(name = "fecha_sincronizacion", nullable = false)
    private LocalDateTime fechaSincronizacion;

    @Column(name = "intentos", nullable = false)
    private Integer intentos = 1;

    @PrePersist
    protected void onCreate() {
        if (fechaSincronizacion == null) {
            fechaSincronizacion = LocalDateTime.now();
        }
    }

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getCodigoLote() { return codigoLote; }
    public void setCodigoLote(String codigoLote) { this.codigoLote = codigoLote; }
    public Integer getSucursalId() { return sucursalId; }
    public void setSucursalId(Integer sucursalId) { this.sucursalId = sucursalId; }
    public Integer getCajaId() { return cajaId; }
    public void setCajaId(Integer cajaId) { this.cajaId = cajaId; }
    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }
    public LocalDateTime getFechaGeneracion() { return fechaGeneracion; }
    public void setFechaGeneracion(LocalDateTime fechaGeneracion) { this.fechaGeneracion = fechaGeneracion; }
    public String getEstatus() { return estatus; }
    public void setEstatus(String estatus) { this.estatus = estatus; }
    public Integer getTotalVentas() { return totalVentas; }
    public void setTotalVentas(Integer totalVentas) { this.totalVentas = totalVentas; }
    public Integer getVentasProcesadas() { return ventasProcesadas; }
    public void setVentasProcesadas(Integer ventasProcesadas) { this.ventasProcesadas = ventasProcesadas; }
    public BigDecimal getMontoTotalLote() { return montoTotalLote; }
    public void setMontoTotalLote(BigDecimal montoTotalLote) { this.montoTotalLote = montoTotalLote; }
    public String getPayloadJson() { return payloadJson; }
    public void setPayloadJson(String payloadJson) { this.payloadJson = payloadJson; }
    public String getErroresDetalle() { return erroresDetalle; }
    public void setErroresDetalle(String erroresDetalle) { this.erroresDetalle = erroresDetalle; }
    public LocalDateTime getFechaSincronizacion() { return fechaSincronizacion; }
    public void setFechaSincronizacion(LocalDateTime fechaSincronizacion) { this.fechaSincronizacion = fechaSincronizacion; }
    public Integer getIntentos() { return intentos; }
    public void setIntentos(Integer intentos) { this.intentos = intentos; }
}
