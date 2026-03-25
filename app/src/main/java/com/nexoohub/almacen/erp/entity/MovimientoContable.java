package com.nexoohub.almacen.erp.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "movimiento_contable")
public class MovimientoContable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poliza_id", nullable = false)
    private PolizaContable poliza;

    @Column(name = "cuenta_id", nullable = false)
    private Integer cuentaId;

    @Column(name = "concepto_detalle", length = 255)
    private String conceptoDetalle;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal cargo = BigDecimal.ZERO;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal abono = BigDecimal.ZERO;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public PolizaContable getPoliza() { return poliza; }
    public void setPoliza(PolizaContable poliza) { this.poliza = poliza; }
    public Integer getCuentaId() { return cuentaId; }
    public void setCuentaId(Integer cuentaId) { this.cuentaId = cuentaId; }
    public String getConceptoDetalle() { return conceptoDetalle; }
    public void setConceptoDetalle(String conceptoDetalle) { this.conceptoDetalle = conceptoDetalle; }
    public BigDecimal getCargo() { return cargo; }
    public void setCargo(BigDecimal cargo) { this.cargo = cargo; }
    public BigDecimal getAbono() { return abono; }
    public void setAbono(BigDecimal abono) { this.abono = abono; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
