package com.nexoohub.almacen.erp.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "poliza_contable")
public class PolizaContable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "numero_poliza", nullable = false, unique = true, length = 50)
    private String numeroPoliza;

    @Column(nullable = false)
    private LocalDate fecha;

    /** DIARIO, INGRESO, EGRESO */
    @Column(name = "tipo_poliza", nullable = false, length = 20)
    private String tipoPoliza;

    @Column(nullable = false, length = 255)
    private String concepto;

    @Column(name = "total_cargo", nullable = false, precision = 14, scale = 2)
    private BigDecimal totalCargo = BigDecimal.ZERO;

    @Column(name = "total_abono", nullable = false, precision = 14, scale = 2)
    private BigDecimal totalAbono = BigDecimal.ZERO;

    @Column(nullable = false, length = 20)
    private String estatus = "APLICADA";

    @Column(name = "usuario_id", nullable = false)
    private Integer usuarioId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "poliza", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MovimientoContable> movimientos = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public void addMovimiento(MovimientoContable m) {
        movimientos.add(m);
        m.setPoliza(this);
    }

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNumeroPoliza() { return numeroPoliza; }
    public void setNumeroPoliza(String numeroPoliza) { this.numeroPoliza = numeroPoliza; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public String getTipoPoliza() { return tipoPoliza; }
    public void setTipoPoliza(String tipoPoliza) { this.tipoPoliza = tipoPoliza; }
    public String getConcepto() { return concepto; }
    public void setConcepto(String concepto) { this.concepto = concepto; }
    public BigDecimal getTotalCargo() { return totalCargo; }
    public void setTotalCargo(BigDecimal totalCargo) { this.totalCargo = totalCargo; }
    public BigDecimal getTotalAbono() { return totalAbono; }
    public void setTotalAbono(BigDecimal totalAbono) { this.totalAbono = totalAbono; }
    public String getEstatus() { return estatus; }
    public void setEstatus(String estatus) { this.estatus = estatus; }
    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public List<MovimientoContable> getMovimientos() { return movimientos; }
}
