package com.nexoohub.almacen.inventario.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para solicitar la generación de alertas de productos de lento movimiento.
 * 
 * @author NexooHub Development Team
 * @since 1.2.0
 */
public class GenerarAlertasRequestDTO {
    
    @NotNull(message = "El umbral de días sin venta es obligatorio")
    @Min(value = 1, message = "El umbral debe ser al menos 1 día")
    private Integer diasSinVentaMinimo;
    
    private Integer sucursalId; // Null = todas las sucursales
    
    private Boolean soloActualizarExistentes = false;

    // ==========================================
    // GETTERS Y SETTERS
    // ==========================================

    public Integer getDiasSinVentaMinimo() { return diasSinVentaMinimo; }
    public void setDiasSinVentaMinimo(Integer diasSinVentaMinimo) { 
        this.diasSinVentaMinimo = diasSinVentaMinimo; 
    }

    public Integer getSucursalId() { return sucursalId; }
    public void setSucursalId(Integer sucursalId) { this.sucursalId = sucursalId; }

    public Boolean getSoloActualizarExistentes() { return soloActualizarExistentes; }
    public void setSoloActualizarExistentes(Boolean soloActualizarExistentes) { 
        this.soloActualizarExistentes = soloActualizarExistentes; 
    }
}
