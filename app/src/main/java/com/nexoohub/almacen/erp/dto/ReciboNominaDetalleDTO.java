package com.nexoohub.almacen.erp.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ReciboNominaDetalleDTO {
    private Integer id;
    private String tipoConcepto;
    private String claveSat;
    private String descripcion;
    private BigDecimal importe;
    private LocalDateTime createdAt;

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getTipoConcepto() { return tipoConcepto; }
    public void setTipoConcepto(String tipoConcepto) { this.tipoConcepto = tipoConcepto; }
    public String getClaveSat() { return claveSat; }
    public void setClaveSat(String claveSat) { this.claveSat = claveSat; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public BigDecimal getImporte() { return importe; }
    public void setImporte(BigDecimal importe) { this.importe = importe; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
