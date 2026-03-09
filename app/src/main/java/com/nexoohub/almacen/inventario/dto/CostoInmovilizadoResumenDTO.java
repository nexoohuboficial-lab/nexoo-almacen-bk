package com.nexoohub.almacen.inventario.dto;

import java.math.BigDecimal;

/**
 * DTO para resumen de costos inmovilizados por alertas de lento movimiento.
 * 
 * <p>Proporciona métricas agregadas para dashboard ejecutivo.</p>
 * 
 * @author NexooHub Development Team
 * @since 1.2.0
 */
public class CostoInmovilizadoResumenDTO {
    private BigDecimal costoTotalInmovilizado;
    private Long alertasAdvertencia;
    private Long alertasCriticas;
    private Long totalProductosAfectados;
    private Integer sucursalId;
    private String nombreSucursal;

    // ==========================================
    // CONSTRUCTOR
    // ==========================================

    public CostoInmovilizadoResumenDTO() {
        this.costoTotalInmovilizado = BigDecimal.ZERO;
        this.alertasAdvertencia =0L;
        this.alertasCriticas = 0L;
        this.totalProductosAfectados = 0L;
    }

    // ==========================================
    // MÉTODOS CALCULADOS
    // ==========================================

    /**
     * Calcula el total de alertas (advertencia + críticas).
     * 
     * @return Total de alertas activas
     */
    public Long getCantidadAlertas() {
        return alertasAdvertencia + alertasCriticas;
    }

    // ==========================================
    // GETTERS Y SETTERS
    // ==========================================

    public BigDecimal getCostoTotalInmovilizado() { return costoTotalInmovilizado; }
    public void setCostoTotalInmovilizado(BigDecimal costoTotalInmovilizado) { 
        this.costoTotalInmovilizado = costoTotalInmovilizado; 
    }

    public Long getAlertasAdvertencia() { return alertasAdvertencia; }
    public void setAlertasAdvertencia(Long alertasAdvertencia) { 
        this.alertasAdvertencia = alertasAdvertencia; 
    }

    public Long getAlertasCriticas() { return alertasCriticas; }
    public void setAlertasCriticas(Long alertasCriticas) { 
        this.alertasCriticas = alertasCriticas; 
    }

    public Long getTotalProductosAfectados() { return totalProductosAfectados; }
    public void setTotalProductosAfectados(Long totalProductosAfectados) { 
        this.totalProductosAfectados = totalProductosAfectados; 
    }

    public Integer getSucursalId() { return sucursalId; }
    public void setSucursalId(Integer sucursalId) { this.sucursalId = sucursalId; }

    public String getNombreSucursal() { return nombreSucursal; }
    public void setNombreSucursal(String nombreSucursal) { this.nombreSucursal = nombreSucursal; }
}
