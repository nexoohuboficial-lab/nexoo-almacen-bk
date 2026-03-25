package com.nexoohub.almacen.erp.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class GastoOperativoResponse {
    private Integer id;
    private String concepto;
    private String categoria;
    private BigDecimal monto;
    private LocalDate fechaGasto;
    private Integer sucursalId;
    private Integer usuarioId;
    private String comprobanteRef;
    private String observaciones;

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
}
