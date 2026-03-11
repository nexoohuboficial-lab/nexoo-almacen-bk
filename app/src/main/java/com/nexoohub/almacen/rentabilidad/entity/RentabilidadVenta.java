package com.nexoohub.almacen.rentabilidad.entity;

import com.nexoohub.almacen.ventas.entity.Venta;
import com.nexoohub.almacen.common.entity.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Entidad que almacena el análisis de rentabilidad de una venta.
 * 
 * <p>Calcula y registra cuánto se GANÓ realmente en cada venta,
 * comparando el precio de venta contra el costo de los productos vendidos.</p>
 * 
 * <p>Fórmula clave: <strong>Utilidad Bruta = Precio Venta - Costo Total</strong></p>
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
@Entity
@Table(name = "rentabilidad_venta")
public class RentabilidadVenta extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El ID de la venta es obligatorio")
    @Column(name = "venta_id", unique = true, nullable = false)
    private Integer ventaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venta_id", insertable = false, updatable = false)
    private Venta venta;

    /**
     * Suma del costo promedio ponderado de todos los productos vendidos.
     * Representa cuánto nos costó la mercancía vendida.
     */
    @NotNull(message = "El costo total es obligatorio")
    @DecimalMin(value = "0.0", message = "El costo total no puede ser negativo")
    @Column(name = "costo_total", nullable = false, precision = 15, scale = 2)
    private BigDecimal costoTotal;

    /**
     * Precio total de la venta (igual a Venta.total).
     * Representa cuánto se cobró al cliente.
     */
    @NotNull(message = "El precio de venta es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio de venta debe ser mayor a cero")
    @Column(name = "precio_venta_total", nullable = false, precision = 15, scale = 2)
    private BigDecimal precioVentaTotal;

    /**
     * Utilidad bruta de la venta (Precio - Costo).
     * Valor positivo = ganancia, valor negativo = pérdida.
     */
    @NotNull(message = "La utilidad bruta es obligatoria")
    @Column(name = "utilidad_bruta", nullable = false, precision = 15, scale = 2)
    private BigDecimal utilidadBruta;

    /**
     * Margen de utilidad porcentual ((Utilidad / Precio) * 100).
     * Ejemplo: 25.50 representa un margen del 25.50%
     */
    @NotNull(message = "El margen porcentaje es obligatorio")
    @Column(name = "margen_porcentaje", nullable = false, precision = 5, scale = 2)
    private BigDecimal margenPorcentaje;

    /**
     * Marca de alerta si la venta se realizó por debajo del costo.
     */
    @NotNull(message = "La bandera de venta bajo costo es obligatoria")
    @Column(name = "venta_bajo_costo", nullable = false)
    private Boolean ventaBajoCosto;

    /**
     * Número de productos diferentes vendidos en esta venta.
     */
    @Column(name = "cantidad_items")
    private Integer cantidadItems;

    // ==========================================
    // GETTERS Y SETTERS
    // ==========================================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getVentaId() { return ventaId; }
    public void setVentaId(Integer ventaId) { this.ventaId = ventaId; }

    public Venta getVenta() { return venta; }
    public void setVenta(Venta venta) { this.venta = venta; }

    public BigDecimal getCostoTotal() { return costoTotal; }
    public void setCostoTotal(BigDecimal costoTotal) { this.costoTotal = costoTotal; }

    public BigDecimal getPrecioVentaTotal() { return precioVentaTotal; }
    public void setPrecioVentaTotal(BigDecimal precioVentaTotal) { this.precioVentaTotal = precioVentaTotal; }

    public BigDecimal getUtilidadBruta() { return utilidadBruta; }
    public void setUtilidadBruta(BigDecimal utilidadBruta) { this.utilidadBruta = utilidadBruta; }

    public BigDecimal getMargenPorcentaje() { return margenPorcentaje; }
    public void setMargenPorcentaje(BigDecimal margenPorcentaje) { this.margenPorcentaje = margenPorcentaje; }

    public Boolean getVentaBajoCosto() { return ventaBajoCosto; }
    public void setVentaBajoCosto(Boolean ventaBajoCosto) { this.ventaBajoCosto = ventaBajoCosto; }

    public Integer getCantidadItems() { return cantidadItems; }
    public void setCantidadItems(Integer cantidadItems) { this.cantidadItems = cantidadItems; }
}
