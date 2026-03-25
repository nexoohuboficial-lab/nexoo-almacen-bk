package com.nexoohub.almacen.caja.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/** DTO para abrir un turno de caja (POST /api/v1/cajas/abrir) */
public class AbrirTurnoRequest {

    @NotNull(message = "El ID de sucursal es obligatorio")
    private Integer sucursalId;

    @NotNull(message = "El ID de empleado es obligatorio")
    private Integer empleadoId;

    @NotNull(message = "El fondo inicial es obligatorio")
    @DecimalMin(value = "0.00", message = "El fondo inicial no puede ser negativo")
    private BigDecimal fondoInicial;

    private String observaciones;

    // Getters y Setters
    public Integer getSucursalId() { return sucursalId; }
    public void setSucursalId(Integer sucursalId) { this.sucursalId = sucursalId; }

    public Integer getEmpleadoId() { return empleadoId; }
    public void setEmpleadoId(Integer empleadoId) { this.empleadoId = empleadoId; }

    public BigDecimal getFondoInicial() { return fondoInicial; }
    public void setFondoInicial(BigDecimal fondoInicial) { this.fondoInicial = fondoInicial; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
}
