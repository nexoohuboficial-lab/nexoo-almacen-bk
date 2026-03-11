package com.nexoohub.almacen.comisiones.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * DTO para crear o actualizar reglas de comisión
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
public class ReglaComisionRequestDTO {

    @NotBlank(message = "El nombre de la regla es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombre;

    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    private String descripcion;

    @NotBlank(message = "El tipo de comisión es obligatorio")
    @Pattern(regexp = "PORCENTAJE_VENTA|MONTO_FIJO|POR_META|POR_PRODUCTO", 
             message = "Tipo inválido. Debe ser: PORCENTAJE_VENTA, MONTO_FIJO, POR_META, o POR_PRODUCTO")
    private String tipo;

    @Size(max = 50, message = "El puesto no puede exceder 50 caracteres")
    private String puesto; // null = aplica a todos

    @DecimalMin(value = "0.00", message = "El porcentaje no puede ser negativo")
    @DecimalMax(value = "1.00", message = "El porcentaje no puede ser mayor a 100%")
    private BigDecimal porcentajeComision;

    @DecimalMin(value = "0.00", message = "El monto fijo no puede ser negativo")
    private BigDecimal montoFijo;

    @DecimalMin(value = "0.00", message = "La meta mensual no puede ser negativa")
    private BigDecimal metaMensual;

    @DecimalMin(value = "0.00", message = "El bono no puede ser negativo")
    private BigDecimal bonoMeta;

    @Size(max = 50, message = "El SKU no puede exceder 50 caracteres")
    private String skuProducto;

    @NotNull(message = "El estado activo es obligatorio")
    private Boolean activa;

    @Min(value = 1, message = "La prioridad debe ser al menos 1")
    private Integer prioridad;

    // Getters y Setters
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
}
