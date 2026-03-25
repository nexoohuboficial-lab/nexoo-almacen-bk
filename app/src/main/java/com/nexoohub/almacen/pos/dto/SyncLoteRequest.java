package com.nexoohub.almacen.pos.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

public class SyncLoteRequest {

    @NotBlank(message = "El identificador correlativo del lote (código lote) es obligatorio")
    private String codigoLote;

    @NotNull(message = "Sucursal ID requerida")
    private Integer sucursalId;

    @NotNull(message = "Caja ID requerida")
    private Integer cajaId;

    @NotNull(message = "Usuario ID requerido")
    private Integer usuarioId;

    @NotNull(message = "Fecha de generación local requerida")
    private LocalDateTime fechaGeneracion;

    @NotEmpty(message = "El lote debe contener al menos una venta")
    @Valid
    private List<VentaOfflineSyncDTO> ventas;

    // Getters y Setters
    public String getCodigoLote() { return codigoLote; }
    public void setCodigoLote(String codigoLote) { this.codigoLote = codigoLote; }
    public Integer getSucursalId() { return sucursalId; }
    public void setSucursalId(Integer sucursalId) { this.sucursalId = sucursalId; }
    public Integer getCajaId() { return cajaId; }
    public void setCajaId(Integer cajaId) { this.cajaId = cajaId; }
    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }
    public LocalDateTime getFechaGeneracion() { return fechaGeneracion; }
    public void setFechaGeneracion(LocalDateTime fechaGeneracion) { this.fechaGeneracion = fechaGeneracion; }
    public List<VentaOfflineSyncDTO> getVentas() { return ventas; }
    public void setVentas(List<VentaOfflineSyncDTO> ventas) { this.ventas = ventas; }
}
