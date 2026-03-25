package com.nexoohub.almacen.erp.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public class PolizaContableRequest {

    @NotBlank(message = "El número de póliza es requerido")
    private String numeroPoliza;

    @NotNull(message = "La fecha es requerida")
    private LocalDate fecha;

    @NotBlank(message = "El tipo de póliza es requerido (DIARIO, INGRESO, EGRESO)")
    private String tipoPoliza;

    @NotBlank(message = "El concepto de la póliza es requerido")
    private String concepto;

    @NotNull(message = "El usuario que registra es requerido")
    private Integer usuarioId;

    @NotEmpty(message = "La póliza debe tener al menos un par de movimientos")
    @Valid
    private List<MovimientoContableRequest> movimientos;

    // Getters y Setters
    public String getNumeroPoliza() { return numeroPoliza; }
    public void setNumeroPoliza(String numeroPoliza) { this.numeroPoliza = numeroPoliza; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public String getTipoPoliza() { return tipoPoliza; }
    public void setTipoPoliza(String tipoPoliza) { this.tipoPoliza = tipoPoliza; }
    public String getConcepto() { return concepto; }
    public void setConcepto(String concepto) { this.concepto = concepto; }
    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }
    public List<MovimientoContableRequest> getMovimientos() { return movimientos; }
    public void setMovimientos(List<MovimientoContableRequest> movimientos) { this.movimientos = movimientos; }
}
