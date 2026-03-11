package com.nexoohub.almacen.comisiones.entity;

import com.nexoohub.almacen.common.entity.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Entidad que define las reglas de comisión para empleados/vendedores.
 * Permite configurar diferentes tipos de comisiones: por venta, por meta, por producto, etc.
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
@Entity
@Table(name = "regla_comision", indexes = {
    @Index(name = "idx_regla_comision_puesto", columnList = "puesto"),
    @Index(name = "idx_regla_comision_activa", columnList = "activa")
})
public class ReglaComision extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "El nombre de la regla es obligatorio")
    @Column(name = "nombre", length = 100)
    private String nombre;

    @Column(name = "descripcion", length = 500)
    private String descripcion;

    @NotBlank(message = "El tipo de comisión es obligatorio")
    @Column(name = "tipo")
    private String tipo; // "PORCENTAJE_VENTA", "MONTO_FIJO", "POR_META", "POR_PRODUCTO"

    /**
     * Puesto al que aplica esta regla. 
     * Si es null, aplica a todos los puestos.
     */
    @Column(name = "puesto", length = 50)
    private String puesto;

    /**
     * Porcentaje de comisión sobre el total de la venta.
     * Ejemplo: 0.05 = 5% de comisión
     */
    @DecimalMin(value = "0.00", message = "El porcentaje no puede ser negativo")
    @DecimalMax(value = "1.00", message = "El porcentaje no puede ser mayor a 100%")
    @Column(name = "porcentaje_comision", precision = 5, scale = 4)
    private BigDecimal porcentajeComision = BigDecimal.ZERO;

    /**
     * Monto fijo de comisión (si aplica).
     */
    @DecimalMin(value = "0.00", message = "El monto fijo no puede ser negativo")
    @Column(name = "monto_fijo", precision = 10, scale = 2)
    private BigDecimal montoFijo = BigDecimal.ZERO;

    /**
     * Meta de ventas mensual para activar bonos.
     */
    @DecimalMin(value = "0.00", message = "La meta no puede ser negativa")
    @Column(name = "meta_mensual", precision = 12, scale = 2)
    private BigDecimal metaMensual;

    /**
     * Bono adicional por cumplir meta.
     */
    @DecimalMin(value = "0.00", message = "El bono no puede ser negativo")
    @Column(name = "bono_meta", precision = 10, scale = 2)
    private BigDecimal bonoMeta;

    /**
     * SKU de producto específico (si la comisión es por producto).
     */
    @Column(name = "sku_producto", length = 50)
    private String skuProducto;

    @NotNull(message = "El estado activo es obligatorio")
    @Column(name = "activa")
    private Boolean activa = true;

    @Column(name = "prioridad")
    private Integer prioridad = 1; // Para ordenar reglas si hay conflictos

    // ==========================================
    // GETTERS Y SETTERS
    // ==========================================
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
}
