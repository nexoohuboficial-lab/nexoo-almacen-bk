package com.nexoohub.almacen.rentabilidad.entity;

import com.nexoohub.almacen.inventario.entity.ProductoMaestro;
import com.nexoohub.almacen.common.entity.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entidad que almacena análisis agregado de rentabilidad por producto en un período.
 * 
 * <p>Permite responder preguntas como:</p>
 * <ul>
 *   <li>¿Cuáles son los productos MÁS rentables?</li>
 *   <li>¿Cuáles productos se venden con pérdida?</li>
 *   <li>¿Qué margen promedio tiene cada producto?</li>
 * </ul>
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
@Entity
@Table(name = "rentabilidad_producto", indexes = {
    @Index(name = "idx_rentab_prod_periodo", columnList = "periodo_inicio, periodo_fin"),
    @Index(name = "idx_rentab_prod_margen", columnList = "margen_promedio_porcentaje DESC"),
    @Index(name = "idx_rentab_prod_utilidad", columnList = "utilidad_total_generada DESC")
})
public class RentabilidadProducto extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El SKU interno es obligatorio")
    @Column(name = "sku_interno", nullable = false, length = 50)
    private String skuInterno;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sku_interno", insertable = false, updatable = false)
    private ProductoMaestro producto;

    /**
     * Fecha de inicio del período analizado.
     */
    @NotNull(message = "El período de inicio es obligatorio")
    @Column(name = "periodo_inicio", nullable = false)
    private LocalDate periodoInicio;

    /**
     * Fecha de fin del período analizado.
     */
    @NotNull(message = "El período de fin es obligatorio")
    @Column(name = "periodo_fin", nullable = false)
    private LocalDate periodoFin;

    /**
     * Cantidad total de unidades vendidas en el período.
     */
    @NotNull(message = "La cantidad vendida es obligatoria")
    @Min(value = 0, message = "La cantidad vendida no puede ser negativa")
    @Column(name = "cantidad_vendida", nullable = false)
    private Integer cantidadVendida;

    /**
     * Costo promedio unitario del producto en el período.
     * Se calcula con base en el Costo Promedio Ponderado.
     */
    @NotNull(message = "El costo promedio unitario es obligatorio")
    @DecimalMin(value = "0.0", message = "El costo promedio no puede ser negativo")
    @Column(name = "costo_promedio_unitario", nullable = false, precision = 15, scale = 2)
    private BigDecimal costoPromedioUnitario;

    /**
     * Precio promedio de venta del producto en el período.
     */
    @NotNull(message = "El precio promedio de venta es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio promedio debe ser mayor a cero")
    @Column(name = "precio_promedio_venta", nullable = false, precision = 15, scale = 2)
    private BigDecimal precioPromedioVenta;

    /**
     * Utilidad total generada por este producto en el período.
     * Fórmula: (Precio Promedio - Costo Promedio) * Cantidad Vendida
     */
    @NotNull(message = "La utilidad total generada es obligatoria")
    @Column(name = "utilidad_total_generada", nullable = false, precision = 15, scale = 2)
    private BigDecimal utilidadTotalGenerada;

    /**
     * Margen de utilidad promedio porcentual.
     * Fórmula: ((Precio Promedio - Costo Promedio) / Precio Promedio) * 100
     */
    @NotNull(message = "El margen promedio porcentaje es obligatorio")
    @Column(name = "margen_promedio_porcentaje", nullable = false, precision = 5, scale = 2)
    private BigDecimal margenPromedioPorcentaje;

    /**
     * Número de ventas en las que apareció el producto.
     */
    @Column(name = "numero_ventas")
    private Integer numeroVentas;

    // ==========================================
    // GETTERS Y SETTERS
    // ==========================================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSkuInterno() { return skuInterno; }
    public void setSkuInterno(String skuInterno) { this.skuInterno = skuInterno; }

    public ProductoMaestro getProducto() { return producto; }
    public void setProducto(ProductoMaestro producto) { this.producto = producto; }

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

    public BigDecimal getMargenPromedioPorcentaje() { return margenPromedioPorcentaje; }
    public void setMargenPromedioPorcentaje(BigDecimal margenPromedioPorcentaje) { 
        this.margenPromedioPorcentaje = margenPromedioPorcentaje; 
    }

    public Integer getNumeroVentas() { return numeroVentas; }
    public void setNumeroVentas(Integer numeroVentas) { this.numeroVentas = numeroVentas; }
}
