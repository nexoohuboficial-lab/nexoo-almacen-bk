package com.nexoohub.almacen.erp.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class DevolucionProveedorRequest {

    @NotNull(message = "El ID del proveedor es requerido")
    private Integer proveedorId;

    @NotNull(message = "El ID de la sucursal es requerido")
    private Integer sucursalId;

    @NotNull(message = "El ID del usuario es requerido")
    private Integer usuarioId;

    @NotBlank(message = "El motivo de la devolución es requerido")
    private String motivo;

    @NotEmpty(message = "Debe enviar al menos un detalle a devolver")
    @Valid
    private List<DevolucionProveedorDetalleRequest> detalles;

    public Integer getProveedorId() { return proveedorId; }
    public void setProveedorId(Integer proveedorId) { this.proveedorId = proveedorId; }
    public Integer getSucursalId() { return sucursalId; }
    public void setSucursalId(Integer sucursalId) { this.sucursalId = sucursalId; }
    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }
    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
    public List<DevolucionProveedorDetalleRequest> getDetalles() { return detalles; }
    public void setDetalles(List<DevolucionProveedorDetalleRequest> detalles) { this.detalles = detalles; }
}
