package com.nexoohub.almacen.comisiones.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO de respuesta para comisiones calculadas
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
public class ComisionResponseDTO {
    
    private Integer id;
    private Integer vendedorId;
    private String vendedorNombre;
    private String vendedorPuesto;
    private Integer periodoAnio;
    private Integer periodoMes;
    private String periodoTexto; // "Enero 2026"
    private BigDecimal totalVentas;
    private Integer cantidadVentas;
    private BigDecimal comisionBase;
    private BigDecimal bonos;
    private BigDecimal ajustes;
    private BigDecimal totalComision;
    private String estado;
    private LocalDate fechaAprobacion;
    private LocalDate fechaPago;
    private String usuarioAprobador;
    private String notas;

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getVendedorId() { return vendedorId; }
    public void setVendedorId(Integer vendedorId) { this.vendedorId = vendedorId; }

    public String getVendedorNombre() { return vendedorNombre; }
    public void setVendedorNombre(String vendedorNombre) { this.vendedorNombre = vendedorNombre; }

    public String getVendedorPuesto() { return vendedorPuesto; }
    public void setVendedorPuesto(String vendedorPuesto) { this.vendedorPuesto = vendedorPuesto; }

    public Integer getPeriodoAnio() { return periodoAnio; }
    public void setPeriodoAnio(Integer periodoAnio) { this.periodoAnio = periodoAnio; }

    public Integer getPeriodoMes() { return periodoMes; }
    public void setPeriodoMes(Integer periodoMes) { this.periodoMes = periodoMes; }

    public String getPeriodoTexto() { return periodoTexto; }
    public void setPeriodoTexto(String periodoTexto) { this.periodoTexto = periodoTexto; }

    public BigDecimal getTotalVentas() { return totalVentas; }
    public void setTotalVentas(BigDecimal totalVentas) { this.totalVentas = totalVentas; }

    public Integer getCantidadVentas() { return cantidadVentas; }
    public void setCantidadVentas(Integer cantidadVentas) { this.cantidadVentas = cantidadVentas; }

    public BigDecimal getComisionBase() { return comisionBase; }
    public void setComisionBase(BigDecimal comisionBase) { this.comisionBase = comisionBase; }

    public BigDecimal getBonos() { return bonos; }
    public void setBonos(BigDecimal bonos) { this.bonos = bonos; }

    public BigDecimal getAjustes() { return ajustes; }
    public void setAjustes(BigDecimal ajustes) { this.ajustes = ajustes; }

    public BigDecimal getTotalComision() { return totalComision; }
    public void setTotalComision(BigDecimal totalComision) { this.totalComision = totalComision; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public LocalDate getFechaAprobacion() { return fechaAprobacion; }
    public void setFechaAprobacion(LocalDate fechaAprobacion) { this.fechaAprobacion = fechaAprobacion; }

    public LocalDate getFechaPago() { return fechaPago; }
    public void setFechaPago(LocalDate fechaPago) { this.fechaPago = fechaPago; }

    public String getUsuarioAprobador() { return usuarioAprobador; }
    public void setUsuarioAprobador(String usuarioAprobador) { this.usuarioAprobador = usuarioAprobador; }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }
}
