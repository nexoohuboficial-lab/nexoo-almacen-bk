package com.nexoohub.almacen.erp.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public class GastoOperativoRequest {

    @NotBlank(message = "El concepto es requerido")
    private String concepto;

    @NotBlank(message = "La categoría es requerida (RENTA, SERVICIOS, NOMINA, TRANSPORTE, OTROS)")
    private String categoria;

    @NotNull(message = "El monto es requerido")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor a 0")
    private BigDecimal monto;

    @NotNull(message = "La fecha de gasto es requerida")
    private LocalDate fechaGasto;

    private Integer sucursalId;

    @NotNull(message = "El usuario es requerido")
    private Integer usuarioId;

    private String comprobanteRef;
    private String observaciones;

    // Getters y Setters
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
