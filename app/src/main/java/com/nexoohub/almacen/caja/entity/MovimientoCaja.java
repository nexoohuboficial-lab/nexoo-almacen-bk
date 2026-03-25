package com.nexoohub.almacen.caja.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad que representa un movimiento dentro de un turno de caja.
 * Tipos: RETIRO, INGRESO_EXTRA, VENTA_EFECTIVO, VENTA_TARJETA, VENTA_CREDITO
 *
 * @author NexooHub Development Team
 * @since 1.0 — POS-01
 */
@Entity
@Table(name = "movimiento_caja", indexes = {
    @Index(name = "idx_movimiento_caja_turno", columnList = "turno_id"),
    @Index(name = "idx_movimiento_caja_tipo",  columnList = "tipo"),
    @Index(name = "idx_movimiento_caja_fecha", columnList = "fecha_movimiento")
})
public class MovimientoCaja {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "turno_id", nullable = false, insertable = false, updatable = false)
    private Integer turnoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "turno_id", nullable = false)
    private TurnoCaja turno;

    /**
     * Tipo de movimiento:
     * RETIRO | INGRESO_EXTRA | VENTA_EFECTIVO | VENTA_TARJETA | VENTA_CREDITO
     */
    @Column(name = "tipo", nullable = false, length = 30)
    private String tipo;

    @Column(name = "monto", nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @Column(name = "concepto", length = 255)
    private String concepto;

    /** Referencia externa: ID de venta, número de autorización bancaria, etc. */
    @Column(name = "referencia", length = 100)
    private String referencia;

    @Column(name = "fecha_movimiento", nullable = false)
    private LocalDateTime fechaMovimiento;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "usuario_creacion", length = 50)
    private String usuarioCreacion;

    @PrePersist
    protected void onCreate() {
        if (fechaCreacion   == null) fechaCreacion   = LocalDateTime.now();
        if (fechaMovimiento == null) fechaMovimiento = LocalDateTime.now();
    }

    // ==========================================
    // GETTERS Y SETTERS
    // ==========================================
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getTurnoId() { return turnoId; }
    public void setTurnoId(Integer turnoId) { this.turnoId = turnoId; }

    public TurnoCaja getTurno() { return turno; }
    public void setTurno(TurnoCaja turno) { this.turno = turno; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }

    public String getConcepto() { return concepto; }
    public void setConcepto(String concepto) { this.concepto = concepto; }

    public String getReferencia() { return referencia; }
    public void setReferencia(String referencia) { this.referencia = referencia; }

    public LocalDateTime getFechaMovimiento() { return fechaMovimiento; }
    public void setFechaMovimiento(LocalDateTime fechaMovimiento) { this.fechaMovimiento = fechaMovimiento; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public String getUsuarioCreacion() { return usuarioCreacion; }
    public void setUsuarioCreacion(String usuarioCreacion) { this.usuarioCreacion = usuarioCreacion; }
}
