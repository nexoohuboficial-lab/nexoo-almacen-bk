package com.nexoohub.almacen.caja.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad que representa un turno de caja abierto por un empleado en una sucursal.
 * Controla el ciclo completo: apertura → movimientos → arqueo (cierre Z).
 *
 * @author NexooHub Development Team
 * @since 1.0 — POS-01
 */
@Entity
@Table(name = "turno_caja", indexes = {
    @Index(name = "idx_turno_caja_sucursal", columnList = "sucursal_id"),
    @Index(name = "idx_turno_caja_empleado", columnList = "empleado_id"),
    @Index(name = "idx_turno_caja_estado",   columnList = "estado")
})
public class TurnoCaja {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "sucursal_id", nullable = false)
    private Integer sucursalId;

    @Column(name = "empleado_id", nullable = false)
    private Integer empleadoId;

    @Column(name = "fondo_inicial", nullable = false, precision = 10, scale = 2)
    private BigDecimal fondoInicial = BigDecimal.ZERO;

    @Column(name = "total_ventas_efectivo", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalVentasEfectivo = BigDecimal.ZERO;

    @Column(name = "total_ventas_tarjeta", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalVentasTarjeta = BigDecimal.ZERO;

    @Column(name = "total_ventas_credito", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalVentasCredito = BigDecimal.ZERO;

    @Column(name = "total_retiros", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalRetiros = BigDecimal.ZERO;

    @Column(name = "total_ingresos_extra", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalIngresosExtra = BigDecimal.ZERO;

    /** Calculado: fondoInicial + ventasEfectivo + ingresosExtra - retiros */
    @Column(name = "efectivo_esperado", nullable = false, precision = 10, scale = 2)
    private BigDecimal efectivoEsperado = BigDecimal.ZERO;

    /** Lo que el empleado cuenta físicamente al cerrar */
    @Column(name = "efectivo_real", precision = 10, scale = 2)
    private BigDecimal efectivoReal;

    /** efectivoReal - efectivoEsperado (puede ser negativo: faltante) */
    @Column(name = "diferencia", precision = 10, scale = 2)
    private BigDecimal diferencia;

    /** ABIERTO | CERRADO */
    @Column(name = "estado", nullable = false, length = 20)
    private String estado = "ABIERTO";

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "fecha_apertura", nullable = false, updatable = false)
    private LocalDateTime fechaApertura;

    @Column(name = "fecha_cierre")
    private LocalDateTime fechaCierre;

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
        if (fechaCreacion  == null) fechaCreacion  = LocalDateTime.now();
        if (fechaApertura  == null) fechaApertura  = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }

    // ==========================================
    // GETTERS Y SETTERS
    // ==========================================
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getSucursalId() { return sucursalId; }
    public void setSucursalId(Integer sucursalId) { this.sucursalId = sucursalId; }

    public Integer getEmpleadoId() { return empleadoId; }
    public void setEmpleadoId(Integer empleadoId) { this.empleadoId = empleadoId; }

    public BigDecimal getFondoInicial() { return fondoInicial; }
    public void setFondoInicial(BigDecimal fondoInicial) { this.fondoInicial = fondoInicial; }

    public BigDecimal getTotalVentasEfectivo() { return totalVentasEfectivo; }
    public void setTotalVentasEfectivo(BigDecimal totalVentasEfectivo) { this.totalVentasEfectivo = totalVentasEfectivo; }

    public BigDecimal getTotalVentasTarjeta() { return totalVentasTarjeta; }
    public void setTotalVentasTarjeta(BigDecimal totalVentasTarjeta) { this.totalVentasTarjeta = totalVentasTarjeta; }

    public BigDecimal getTotalVentasCredito() { return totalVentasCredito; }
    public void setTotalVentasCredito(BigDecimal totalVentasCredito) { this.totalVentasCredito = totalVentasCredito; }

    public BigDecimal getTotalRetiros() { return totalRetiros; }
    public void setTotalRetiros(BigDecimal totalRetiros) { this.totalRetiros = totalRetiros; }

    public BigDecimal getTotalIngresosExtra() { return totalIngresosExtra; }
    public void setTotalIngresosExtra(BigDecimal totalIngresosExtra) { this.totalIngresosExtra = totalIngresosExtra; }

    public BigDecimal getEfectivoEsperado() { return efectivoEsperado; }
    public void setEfectivoEsperado(BigDecimal efectivoEsperado) { this.efectivoEsperado = efectivoEsperado; }

    public BigDecimal getEfectivoReal() { return efectivoReal; }
    public void setEfectivoReal(BigDecimal efectivoReal) { this.efectivoReal = efectivoReal; }

    public BigDecimal getDiferencia() { return diferencia; }
    public void setDiferencia(BigDecimal diferencia) { this.diferencia = diferencia; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public LocalDateTime getFechaApertura() { return fechaApertura; }
    public void setFechaApertura(LocalDateTime fechaApertura) { this.fechaApertura = fechaApertura; }

    public LocalDateTime getFechaCierre() { return fechaCierre; }
    public void setFechaCierre(LocalDateTime fechaCierre) { this.fechaCierre = fechaCierre; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public String getUsuarioCreacion() { return usuarioCreacion; }
    public void setUsuarioCreacion(String usuarioCreacion) { this.usuarioCreacion = usuarioCreacion; }

    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }

    public String getUsuarioActualizacion() { return usuarioActualizacion; }
    public void setUsuarioActualizacion(String usuarioActualizacion) { this.usuarioActualizacion = usuarioActualizacion; }
}
