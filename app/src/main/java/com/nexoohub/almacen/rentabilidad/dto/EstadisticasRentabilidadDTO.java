package com.nexoohub.almacen.rentabilidad.dto;

import java.math.BigDecimal;

/**
 * DTO con estadísticas generales de rentabilidad del negocio.
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
public class EstadisticasRentabilidadDTO {
    private BigDecimal utilidadTotalPeriodo;
    private BigDecimal margenPromedioPorcentaje;
    private Long totalVentasAnalizadas;
    private Long ventasBajoCosto;
    private BigDecimal porcentajeVentasBajoCosto;
    private BigDecimal ventaMasRentable;
    private BigDecimal ventaMenosRentable;
    private String productoMasRentable;
    private String productoMenosRentable;

    public EstadisticasRentabilidadDTO() {}

    public EstadisticasRentabilidadDTO(
            BigDecimal utilidadTotalPeriodo, BigDecimal margenPromedioPorcentaje,
            Long totalVentasAnalizadas, Long ventasBajoCosto,
            BigDecimal porcentajeVentasBajoCosto, BigDecimal ventaMasRentable,
            BigDecimal ventaMenosRentable, String productoMasRentable,
            String productoMenosRentable) {
        this.utilidadTotalPeriodo = utilidadTotalPeriodo;
        this.margenPromedioPorcentaje = margenPromedioPorcentaje;
        this.totalVentasAnalizadas = totalVentasAnalizadas;
        this.ventasBajoCosto = ventasBajoCosto;
        this.porcentajeVentasBajoCosto = porcentajeVentasBajoCosto;
        this.ventaMasRentable = ventaMasRentable;
        this.ventaMenosRentable = ventaMenosRentable;
        this.productoMasRentable = productoMasRentable;
        this.productoMenosRentable = productoMenosRentable;
    }

    // ==========================================
    // GETTERS Y SETTERS
    // ==========================================

    public BigDecimal getUtilidadTotalPeriodo() { return utilidadTotalPeriodo; }
    public void setUtilidadTotalPeriodo(BigDecimal utilidadTotalPeriodo) { 
        this.utilidadTotalPeriodo = utilidadTotalPeriodo; 
    }

    public BigDecimal getMargenPromedioPorcentaje() { return margenPromedioPorcentaje; }
    public void setMargenPromedioPorcentaje(BigDecimal margenPromedioPorcentaje) { 
        this.margenPromedioPorcentaje = margenPromedioPorcentaje; 
    }

    public Long getTotalVentasAnalizadas() { return totalVentasAnalizadas; }
    public void setTotalVentasAnalizadas(Long totalVentasAnalizadas) { 
        this.totalVentasAnalizadas = totalVentasAnalizadas; 
    }

    public Long getVentasBajoCosto() { return ventasBajoCosto; }
    public void setVentasBajoCosto(Long ventasBajoCosto) { this.ventasBajoCosto = ventasBajoCosto; }

    public BigDecimal getPorcentajeVentasBajoCosto() { return porcentajeVentasBajoCosto; }
    public void setPorcentajeVentasBajoCosto(BigDecimal porcentajeVentasBajoCosto) { 
        this.porcentajeVentasBajoCosto = porcentajeVentasBajoCosto; 
    }

    public BigDecimal getVentaMasRentable() { return ventaMasRentable; }
    public void setVentaMasRentable(BigDecimal ventaMasRentable) { 
        this.ventaMasRentable = ventaMasRentable; 
    }

    public BigDecimal getVentaMenosRentable() { return ventaMenosRentable; }
    public void setVentaMenosRentable(BigDecimal ventaMenosRentable) { 
        this.ventaMenosRentable = ventaMenosRentable; 
    }

    public String getProductoMasRentable() { return productoMasRentable; }
    public void setProductoMasRentable(String productoMasRentable) { 
        this.productoMasRentable = productoMasRentable; 
    }

    public String getProductoMenosRentable() { return productoMenosRentable; }
    public void setProductoMenosRentable(String productoMenosRentable) { 
        this.productoMenosRentable = productoMenosRentable; 
    }
}
