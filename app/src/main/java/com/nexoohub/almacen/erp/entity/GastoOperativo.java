package com.nexoohub.almacen.erp.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "gasto_operativo")
public class GastoOperativo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 150)
    private String concepto;

    /** RENTA, SERVICIOS, NOMINA, TRANSPORTE, OTROS */
    @Column(nullable = false, length = 50)
    private String categoria;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal monto;

    @Column(name = "fecha_gasto", nullable = false)
    private LocalDate fechaGasto;

    @Column(name = "sucursal_id")
    private Integer sucursalId;

    @Column(name = "usuario_id", nullable = false)
    private Integer usuarioId;

    @Column(name = "comprobante_ref", length = 100)
    private String comprobanteRef;

    @Column(length = 255)
    private String observaciones;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getConcepto() { return concepto; }
    public void setConcepto(String concepto) { this.concepto = concepto; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }
    public LocalDate getFechaGasto() { return fechaGasto; }
    public void setFechaGasto(LocalDate fechaGasto) { this.fechaGasto = fechaGasto; }
    public Integer getSucursalId() { return sucursalId; }
    public void setSucursalId(Integer sucursalId) { this.sucursalId = sucursalId; }
    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }
    public String getComprobanteRef() { return comprobanteRef; }
    public void setComprobanteRef(String comprobanteRef) { this.comprobanteRef = comprobanteRef; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
