package com.nexoohub.almacen.erp.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "recibo_nomina_detalle")
public class ReciboNominaDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recibo_id", nullable = false)
    private ReciboNomina recibo;

    /** PERCEPCION, DEDUCCION */
    @Column(name = "tipo_concepto", nullable = false, length = 20)
    private String tipoConcepto;

    @Column(name = "clave_sat", length = 10)
    private String claveSat;

    @Column(nullable = false, length = 100)
    private String descripcion;

    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal importe;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public ReciboNomina getRecibo() { return recibo; }
    public void setRecibo(ReciboNomina recibo) { this.recibo = recibo; }
    public String getTipoConcepto() { return tipoConcepto; }
    public void setTipoConcepto(String tipoConcepto) { this.tipoConcepto = tipoConcepto; }
    public String getClaveSat() { return claveSat; }
    public void setClaveSat(String claveSat) { this.claveSat = claveSat; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public BigDecimal getImporte() { return importe; }
    public void setImporte(BigDecimal importe) { this.importe = importe; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
