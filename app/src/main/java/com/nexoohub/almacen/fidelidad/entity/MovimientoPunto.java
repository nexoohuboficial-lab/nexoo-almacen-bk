package com.nexoohub.almacen.fidelidad.entity;

import com.nexoohub.almacen.common.entity.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * Entidad que registra movimientos individuales de puntos.
 * 
 * <p>Permite auditar y rastrear cada acumulación o canje de puntos.</p>
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
@Entity
@Table(name = "movimiento_punto")
public class MovimientoPunto extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "El ID del programa de fidelidad es obligatorio")
    @Column(name = "programa_id", nullable = false)
    private Integer programaId;

    @NotNull(message = "El tipo de movimiento es obligatorio")
    @Column(name = "tipo_movimiento", nullable = false, length = 20)
    private String tipoMovimiento; // ACUMULACION, CANJE

    @NotNull(message = "Los puntos son obligatorios")
    @Column(name = "puntos", nullable = false)
    private Integer puntos;

    @Column(name = "monto_asociado", precision = 10, scale = 2)
    private BigDecimal montoAsociado;

    @Column(name = "venta_id")
    private Integer ventaId;

    @NotBlank(message = "La descripción es obligatoria")
    @Column(name = "descripcion", nullable = false, length = 500)
    private String descripcion;

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getProgramaId() {
        return programaId;
    }

    public void setProgramaId(Integer programaId) {
        this.programaId = programaId;
    }

    public String getTipoMovimiento() {
        return tipoMovimiento;
    }

    public void setTipoMovimiento(String tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }

    public Integer getPuntos() {
        return puntos;
    }

    public void setPuntos(Integer puntos) {
        this.puntos = puntos;
    }

    public BigDecimal getMontoAsociado() {
        return montoAsociado;
    }

    public void setMontoAsociado(BigDecimal montoAsociado) {
        this.montoAsociado = montoAsociado;
    }

    public Integer getVentaId() {
        return ventaId;
    }

    public void setVentaId(Integer ventaId) {
        this.ventaId = ventaId;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
