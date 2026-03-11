package com.nexoohub.almacen.comisiones.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de respuesta para reglas de comisión
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
public class ReglaComisionResponseDTO {
    
    private Integer id;
    private String nombre;
    private String descripcion;
    private String tipo;
    private String puesto;
    private BigDecimal porcentajeComision;
    private BigDecimal montoFijo;
    private BigDecimal metaMensual;
    private BigDecimal bonoMeta;
    private String skuProducto;
    private Boolean activa;
    private Integer prioridad;
    private LocalDateTime fechaCreacion;
    private String usuarioCreacion;

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getPuesto() { return puesto; }
    public void setPuesto(String puesto) { this.puesto = puesto; }

    public BigDecimal getPorcentajeComision() { return porcentajeComision; }
    public void setPorcentajeComision(BigDecimal porcentajeComision) { this.porcentajeComision = porcentajeComision; }

    public BigDecimal getMontoFijo() { return montoFijo; }
    public void setMontoFijo(BigDecimal montoFijo) { this.montoFijo = montoFijo; }

    public BigDecimal getMetaMensual() { return metaMensual; }
    public void setMetaMensual(BigDecimal metaMensual) { this.metaMensual = metaMensual; }

    public BigDecimal getBonoMeta() { return bonoMeta; }
    public void setBonoMeta(BigDecimal bonoMeta) { this.bonoMeta = bonoMeta; }

    public String getSkuProducto() { return skuProducto; }
    public void setSkuProducto(String skuProducto) { this.skuProducto = skuProducto; }

    public Boolean getActiva() { return activa; }
    public void setActiva(Boolean activa) { this.activa = activa; }

    public Integer getPrioridad() { return prioridad; }
    public void setPrioridad(Integer prioridad) { this.prioridad = prioridad; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public String getUsuarioCreacion() { return usuarioCreacion; }
    public void setUsuarioCreacion(String usuarioCreacion) { this.usuarioCreacion = usuarioCreacion; }
}
