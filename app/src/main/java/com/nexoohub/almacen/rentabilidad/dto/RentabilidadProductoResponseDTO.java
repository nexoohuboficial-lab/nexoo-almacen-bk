package com.nexoohub.almacen.rentabilidad.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO de respuesta para análisis de rentabilidad por producto.
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
public class RentabilidadProductoResponseDTO {
    private Long id;
    private String skuInterno;
    private String nombreComercial;
    private String marca;
    private LocalDate periodoInicio;
    private LocalDate periodoFin;
    private Integer cantidadVendida;
    private BigDecimal costoPromedioUnitario;
    private BigDecimal precioPromedioVenta;
    private BigDecimal utilidadTotalGenerada;
    private BigDecimal utilidadPorUnidad; // Calculado: utilidadTotal / cantidadVendida
    private BigDecimal margenPromedioPorcentaje;
    private Integer numeroVentas;
    private String clasificacionRentabilidad; // "MUY_RENTABLE", "RENTABLE", "POCO_RENTABLE", "NO_RENTABLE"

    public RentabilidadProductoResponseDTO() {}

    public RentabilidadProductoResponseDTO(
            Long id, String skuInterno, String nombreComercial, String marca,
            LocalDate periodoInicio, LocalDate periodoFin, Integer cantidadVendida,
            BigDecimal costoPromedioUnitario, BigDecimal precioPromedioVenta,
            BigDecimal utilidadTotalGenerada, BigDecimal utilidadPorUnidad,
            BigDecimal margenPromedioPorcentaje, Integer numeroVentas,
            String clasificacionRentabilidad) {
        this.id = id;
        this.skuInterno = skuInterno;
        this.nombreComercial = nombreComercial;
        this.marca = marca;
        this.periodoInicio = periodoInicio;
        this.periodoFin = periodoFin;
        this.cantidadVendida = cantidadVendida;
        this.costoPromedioUnitario = costoPromedioUnitario;
        this.precioPromedioVenta = precioPromedioVenta;
        this.utilidadTotalGenerada = utilidadTotalGenerada;
        this.utilidadPorUnidad = utilidadPorUnidad;
        this.margenPromedioPorcentaje = margenPromedioPorcentaje;
        this.numeroVentas = numeroVentas;
        this.clasificacionRentabilidad = clasificacionRentabilidad;
    }

    // ==========================================
    // GETTERS Y SETTERS
    // ==========================================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSkuInterno() { return skuInterno; }
    public void setSkuInterno(String skuInterno) { this.skuInterno = skuInterno; }

    public String getNombreComercial() { return nombreComercial; }
    public void setNombreComercial(String nombreComercial) { this.nombreComercial = nombreComercial; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public LocalDate getPeriodoInicio() { return periodoInicio; }
    public void setPeriodoInicio(LocalDate periodoInicio) { this.periodoInicio = periodoInicio; }

    public LocalDate getPeriodoFin() { return periodoFin; }
    public void setPeriodoFin(LocalDate periodoFin) { this.periodoFin = periodoFin; }

    public Integer getCantidadVendida() { return cantidadVendida; }
    public void setCantidadVendida(Integer cantidadVendida) { this.cantidadVendida = cantidadVendida; }

    public BigDecimal getCostoPromedioUnitario() { return costoPromedioUnitario; }
    public void setCostoPromedioUnitario(BigDecimal costoPromedioUnitario) { 
        this.costoPromedioUnitario = costoPromedioUnitario; 
    }

    public BigDecimal getPrecioPromedioVenta() { return precioPromedioVenta; }
    public void setPrecioPromedioVenta(BigDecimal precioPromedioVenta) { 
        this.precioPromedioVenta = precioPromedioVenta; 
    }

    public BigDecimal getUtilidadTotalGenerada() { return utilidadTotalGenerada; }
    public void setUtilidadTotalGenerada(BigDecimal utilidadTotalGenerada) { 
        this.utilidadTotalGenerada = utilidadTotalGenerada; 
    }

    public BigDecimal getUtilidadPorUnidad() { return utilidadPorUnidad; }
    public void setUtilidadPorUnidad(BigDecimal utilidadPorUnidad) { 
        this.utilidadPorUnidad = utilidadPorUnidad; 
    }

    public BigDecimal getMargenPromedioPorcentaje() { return margenPromedioPorcentaje; }
    public void setMargenPromedioPorcentaje(BigDecimal margenPromedioPorcentaje) { 
        this.margenPromedioPorcentaje = margenPromedioPorcentaje; 
    }

    public Integer getNumeroVentas() { return numeroVentas; }
    public void setNumeroVentas(Integer numeroVentas) { this.numeroVentas = numeroVentas; }

    public String getClasificacionRentabilidad() { return clasificacionRentabilidad; }
    public void setClasificacionRentabilidad(String clasificacionRentabilidad) { 
        this.clasificacionRentabilidad = clasificacionRentabilidad; 
    }
}
