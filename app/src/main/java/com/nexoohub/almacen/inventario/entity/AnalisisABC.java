package com.nexoohub.almacen.inventario.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entidad para almacenar el análisis ABC de inventario.
 * 
 * <p>Clasificación de productos según su valor (Principio de Pareto 80/20):
 * <ul>
 *   <li>Clase A: ~20% de productos que representan ~80% del valor</li>
 *   <li>Clase B: ~30% de productos que representan ~15% del valor</li>
 *   <li>Clase C: ~50% de productos que representan ~5% del valor</li>
 * </ul>
 * </p>
 * 
 * @author NexooHub Development Team
 * @version 1.0
 */
@Entity
@Table(name = "analisis_abc", indexes = {
    @Index(name = "idx_analisis_abc_sucursal", columnList = "sucursal_id"),
    @Index(name = "idx_analisis_abc_clasificacion", columnList = "clasificacion"),
    @Index(name = "idx_analisis_abc_fecha", columnList = "fecha_analisis"),
    @Index(name = "idx_analisis_abc_sku_sucursal", columnList = "sku_producto, sucursal_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalisisABC {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * SKU del producto analizado
     */
    @Column(name = "sku_producto", nullable = false, length = 50)
    private String skuProducto;

    /**
     * ID de la sucursal donde se realiza el análisis
     */
    @Column(name = "sucursal_id", nullable = false)
    private Integer sucursalId;

    /**
     * Clasificación ABC: 'A', 'B', 'C'
     */
    @Column(name = "clasificacion", nullable = false, length = 1)
    private String clasificacion;

    /**
     * Fecha de inicio del periodo de análisis
     */
    @Column(name = "periodo_inicio", nullable = false)
    private LocalDate periodoInicio;

    /**
     * Fecha fin del periodo de análisis
     */
    @Column(name = "periodo_fin", nullable = false)
    private LocalDate periodoFin;

    /**
     * Cantidad total vendida en el periodo
     */
    @Column(name = "cantidad_vendida", nullable = false)
    private Integer cantidadVendida = 0;

    /**
     * Valor total de las ventas del producto (cantidad * precio)
     */
    @Column(name = "valor_ventas", nullable = false, precision = 12, scale = 2)
    private BigDecimal valorVentas = BigDecimal.ZERO;

    /**
     * Porcentaje que representa del valor total de ventas
     */
    @Column(name = "porcentaje_valor", nullable = false, precision = 5, scale = 2)
    private BigDecimal porcentajeValor = BigDecimal.ZERO;

    /**
     * Porcentaje acumulado hasta este producto (para determinar clasificación)
     */
    @Column(name = "porcentaje_acumulado", nullable = false, precision = 5, scale = 2)
    private BigDecimal porcentajeAcumulado = BigDecimal.ZERO;

    /**
     * Stock actual del producto en la sucursal
     */
    @Column(name = "stock_actual", nullable = false)
    private Integer stockActual = 0;

    /**
     * Valor del stock actual (stock * costo promedio)
     */
    @Column(name = "valor_stock", precision = 12, scale = 2)
    private BigDecimal valorStock = BigDecimal.ZERO;

    /**
     * Rotación de inventario (ventas / stock promedio)
     */
    @Column(name = "rotacion_inventario", precision = 10, scale = 4)
    private BigDecimal rotacionInventario = BigDecimal.ZERO;

    /**
     * Fecha en que se realizó el análisis
     */
    @Column(name = "fecha_analisis", nullable = false)
    private LocalDate fechaAnalisis;

    /**
     * Observaciones adicionales sobre el producto
     */
    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    /**
     * Fecha de creación del registro
     */
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDate fechaCreacion;

    /**
     * Usuario que creó el registro
     */
    @Column(name = "usuario_creacion", length = 50)
    private String usuarioCreacion;

    @PrePersist
    protected void onCreate() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDate.now();
        }
        if (fechaAnalisis == null) {
            fechaAnalisis = LocalDate.now();
        }
    }

    /**
     * Genera observaciones automáticas según la clasificación
     */
    public void generarObservaciones() {
        StringBuilder obs = new StringBuilder();
        
        switch (clasificacion) {
            case "A":
                obs.append("Producto de alta rotación y valor. ");
                obs.append("Requiere gestión prioritaria y stock óptimo. ");
                if (rotacionInventario.compareTo(new BigDecimal("4")) >= 0) {
                    obs.append("Excelente rotación de inventario. ");
                }
                break;
            case "B":
                obs.append("Producto de importancia media. ");
                obs.append("Control moderado de inventario requerido. ");
                break;
            case "C":
                obs.append("Producto de bajo valor relativo. ");
                obs.append("Minimizar inventario y revisar continuidad. ");
                if (rotacionInventario.compareTo(new BigDecimal("1")) < 0) {
                    obs.append("Baja rotación - considerar descontinuar. ");
                }
                break;
        }
        
        this.observaciones = obs.toString().trim();
    }
}
