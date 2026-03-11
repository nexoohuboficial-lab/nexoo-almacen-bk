package com.nexoohub.almacen.comisiones.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO para resumen de comisiones por periodo
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
public class ResumenComisionesDTO {
    
    private String periodo; // "Enero 2026"
    private Integer cantidadVendedores;
    private BigDecimal totalComisiones;
    private BigDecimal totalVentas;
    private Integer cantidadComisionesPendientes;
    private Integer cantidadComisionesAprobadas;
    private Integer cantidadComisionesPagadas;
    private List<ComisionResponseDTO> detalles;

    // Getters y Setters
    public String getPeriodo() { return periodo; }
    public void setPeriodo(String periodo) { this.periodo = periodo; }

    public Integer getCantidadVendedores() { return cantidadVendedores; }
    public void setCantidadVendedores(Integer cantidadVendedores) { this.cantidadVendedores = cantidadVendedores; }

    public BigDecimal getTotalComisiones() { return totalComisiones; }
    public void setTotalComisiones(BigDecimal totalComisiones) { this.totalComisiones = totalComisiones; }

    public BigDecimal getTotalVentas() { return totalVentas; }
    public void setTotalVentas(BigDecimal totalVentas) { this.totalVentas = totalVentas; }

    public Integer getCantidadComisionesPendientes() { return cantidadComisionesPendientes; }
    public void setCantidadComisionesPendientes(Integer cantidadComisionesPendientes) { 
        this.cantidadComisionesPendientes = cantidadComisionesPendientes; 
    }

    public Integer getCantidadComisionesAprobadas() { return cantidadComisionesAprobadas; }
    public void setCantidadComisionesAprobadas(Integer cantidadComisionesAprobadas) { 
        this.cantidadComisionesAprobadas = cantidadComisionesAprobadas; 
    }

    public Integer getCantidadComisionesPagadas() { return cantidadComisionesPagadas; }
    public void setCantidadComisionesPagadas(Integer cantidadComisionesPagadas) { 
        this.cantidadComisionesPagadas = cantidadComisionesPagadas; 
    }

    public List<ComisionResponseDTO> getDetalles() { return detalles; }
    public void setDetalles(List<ComisionResponseDTO> detalles) { this.detalles = detalles; }
}
