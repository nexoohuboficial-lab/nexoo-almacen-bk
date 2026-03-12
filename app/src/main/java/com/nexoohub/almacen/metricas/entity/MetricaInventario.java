package com.nexoohub.almacen.metricas.entity;

import com.nexoohub.almacen.common.entity.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entidad para métricas consolidadas de inventario.
 * 
 * <p>Almacena snapshots de métricas clave de inventario para análisis de:</p>
 * <ul>
 *   <li>Capital inmovilizado en inventario</li>
 *   <li>Rotación de inventario y eficiencia</li>
 *   <li>Stock disponible y alertas</li>
 *   <li>Valor por sucursal/categoría/proveedor</li>
 * </ul>
 * 
 * <p>Responde: <strong>¿Cuánto capital tengo inmovilizado? ¿Rota bien mi inventario?</strong></p>
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
@Entity
@Table(name = "metrica_inventario", indexes = {
    @Index(name = "idx_metrica_inventario_fecha", columnList = "fecha_corte DESC"),
    @Index(name = "idx_metrica_inventario_sucursal", columnList = "sucursal_id"),
    @Index(name = "idx_metrica_inventario_valor_desc", columnList = "valor_total_inventario DESC"),
    @Index(name = "idx_metrica_inventario_rotacion_desc", columnList = "indice_rotacion DESC")
})
public class MetricaInventario extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Fecha de corte del snapshot de inventario.
     */
    @NotNull(message = "La fecha de corte es obligatoria")
    @Column(name = "fecha_corte", nullable = false)
    private LocalDate fechaCorte;

    /**
     * ID de sucursal (NULL = consolidado de todas las sucursales).
     */
    @Column(name = "sucursal_id")
    private Integer sucursalId;

    /**
     * Nombre de la sucursal (desnormalizado para performance).
     */
    @Column(name = "nombre_sucursal", length = 255)
    private String nombreSucursal;

    // ==================== MÉTRICAS DE STOCK ====================

    /**
     * Total de SKUs únicos en inventario (productos diferentes).
     */
    @Min(value = 0, message = "El total de SKUs no puede ser negativo")
    @Column(name = "total_skus", nullable = false)
    private Integer totalSkus = 0;

    /**
     * Suma de unidades disponibles en stock.
     */
    @Min(value = 0, message = "El stock disponible total no puede ser negativo")
    @Column(name = "stock_disponible_total", nullable = false)
    private Integer stockDisponibleTotal = 0;

    /**
     * SKUs con stock bajo mínimo (reabastecimiento urgente).
     */
    @Min(value = 0, message = "Los SKUs con stock bajo no pueden ser negativos")
    @Column(name = "skus_bajo_stock", nullable = false)
    private Integer skusBajoStock = 0;

    /**
     * SKUs sin stock (quiebre de inventario).
     */
    @Min(value = 0, message = "Los SKUs sin stock no pueden ser negativos")
    @Column(name = "skus_sin_stock", nullable = false)
    private Integer skusSinStock = 0;

    /**
     * SKUs próximos a caducar (en los próximos 30 días).
     */
    @Min(value = 0, message = "Los SKUs próximos a caducar no pueden ser negativos")
    @Column(name = "skus_proximos_caducar", nullable = false)
    private Integer skusProximosCaducar = 0;

    // ==================== MÉTRICAS DE VALOR ====================

    /**
     * Valor total del inventario (stockActual × costoPromedioPonderado).
     * Representa capital inmovilizado en inventario.
     */
    @NotNull(message = "El valor total del inventario es obligatorio")
    @Column(name = "valor_total_inventario", nullable = false, precision = 15, scale = 2)
    private BigDecimal valorTotalInventario = BigDecimal.ZERO;

    /**
     * Costo promedio ponderado del inventario total.
     */
    @Column(name = "costo_promedio_ponderado", precision = 15, scale = 2)
    private BigDecimal costoPromedioPonderado = BigDecimal.ZERO;

    /**
     * Valor del stock bajo mínimo (capital en productos que necesitan reposición).
     */
    @Column(name = "valor_stock_bajo", precision = 15, scale = 2)
    private BigDecimal valorStockBajo = BigDecimal.ZERO;

    // ==================== MÉTRICAS DE ROTACIÓN ====================

    /**
     * Índice de rotación de inventario = Costo de Ventas / Valor Promedio Inventario.
     * Indica cuántas veces se renueva el inventario en un período.
     * Ejemplo: 12 = el inventario se renueva 12 veces al año.
     */
    @Column(name = "indice_rotacion", precision = 10, scale = 2)
    private BigDecimal indiceRotacion = BigDecimal.ZERO;

    /**
     * Días promedio de inventario (DIO) = 365 / Rotación.
     * Indica cuántos días tarda en venderse el inventario promedio.
     */
    @Column(name = "dias_inventario", precision = 10, scale = 2)
    private BigDecimal diasInventario = BigDecimal.ZERO;

    /**
     * Costo de ventas del período usado para calcular rotación.
     */
    @Column(name = "costo_ventas_periodo", precision = 15, scale = 2)
    private BigDecimal costoVentasPeriodo = BigDecimal.ZERO;

    /**
     * Período en días usado para calcular rotación (ejemplo: 30, 90, 365).
     */
    @Column(name = "dias_periodo_rotacion")
    private Integer diasPeriodoRotacion = 30;

    // ==================== MÉTRICAS DE EFICIENCIA ====================

    /**
     * Cobertura de stock en días (stock actual / ventas diarias promedio).
     * Indica para cuántos días alcanza el stock actual.
     */
    @Column(name = "cobertura_dias", precision = 10, scale = 2)
    private BigDecimal coberturaDias = BigDecimal.ZERO;

    /**
     * Porcentaje de exactitud de inventario (opcional, se usa con conteos físicos).
     */
    @Column(name = "exactitud_porcentaje", precision = 5, scale = 2)
    private BigDecimal exactitudPorcentaje = BigDecimal.ZERO;

    /**
     * Tasa de quiebre de stock (% SKUs sin stock vs total).
     */
    @Column(name = "tasa_quiebre_stock", precision = 5, scale = 2)
    private BigDecimal tasaQuiebreStock = BigDecimal.ZERO;

    // ==================== CLASIFICACIONES ====================

    /**
     * Clasificación de salud del inventario: SALUDABLE, ACEPTABLE, REQUIERE_ATENCION, CRITICA.
     */
    @Column(name = "salud_inventario", length = 30)
    private String saludInventario;

    /**
     * Clasificación de rotación: ALTA, MEDIA, BAJA, MUY_BAJA.
     */
    @Column(name = "clasificacion_rotacion", length = 20)
    private String clasificacionRotacion;

    // ==================== GETTERS Y SETTERS ====================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getFechaCorte() {
        return fechaCorte;
    }

    public void setFechaCorte(LocalDate fechaCorte) {
        this.fechaCorte = fechaCorte;
    }

    public Integer getSucursalId() {
        return sucursalId;
    }

    public void setSucursalId(Integer sucursalId) {
        this.sucursalId = sucursalId;
    }

    public String getNombreSucursal() {
        return nombreSucursal;
    }

    public void setNombreSucursal(String nombreSucursal) {
        this.nombreSucursal = nombreSucursal;
    }

    public Integer getTotalSkus() {
        return totalSkus;
    }

    public void setTotalSkus(Integer totalSkus) {
        this.totalSkus = totalSkus;
    }

    public Integer getStockDisponibleTotal() {
        return stockDisponibleTotal;
    }

    public void setStockDisponibleTotal(Integer stockDisponibleTotal) {
        this.stockDisponibleTotal = stockDisponibleTotal;
    }

    public Integer getSkusBajoStock() {
        return skusBajoStock;
    }

    public void setSkusBajoStock(Integer skusBajoStock) {
        this.skusBajoStock = skusBajoStock;
    }

    public Integer getSkusSinStock() {
        return skusSinStock;
    }

    public void setSkusSinStock(Integer skusSinStock) {
        this.skusSinStock = skusSinStock;
    }

    public Integer getSkusProximosCaducar() {
        return skusProximosCaducar;
    }

    public void setSkusProximosCaducar(Integer skusProximosCaducar) {
        this.skusProximosCaducar = skusProximosCaducar;
    }

    public BigDecimal getValorTotalInventario() {
        return valorTotalInventario;
    }

    public void setValorTotalInventario(BigDecimal valorTotalInventario) {
        this.valorTotalInventario = valorTotalInventario;
    }

    public BigDecimal getCostoPromedioPonderado() {
        return costoPromedioPonderado;
    }

    public void setCostoPromedioPonderado(BigDecimal costoPromedioPonderado) {
        this.costoPromedioPonderado = costoPromedioPonderado;
    }

    public BigDecimal getValorStockBajo() {
        return valorStockBajo;
    }

    public void setValorStockBajo(BigDecimal valorStockBajo) {
        this.valorStockBajo = valorStockBajo;
    }

    public BigDecimal getIndiceRotacion() {
        return indiceRotacion;
    }

    public void setIndiceRotacion(BigDecimal indiceRotacion) {
        this.indiceRotacion = indiceRotacion;
    }

    public BigDecimal getDiasInventario() {
        return diasInventario;
    }

    public void setDiasInventario(BigDecimal diasInventario) {
        this.diasInventario = diasInventario;
    }

    public BigDecimal getCostoVentasPeriodo() {
        return costoVentasPeriodo;
    }

    public void setCostoVentasPeriodo(BigDecimal costoVentasPeriodo) {
        this.costoVentasPeriodo = costoVentasPeriodo;
    }

    public Integer getDiasPeriodoRotacion() {
        return diasPeriodoRotacion;
    }

    public void setDiasPeriodoRotacion(Integer diasPeriodoRotacion) {
        this.diasPeriodoRotacion = diasPeriodoRotacion;
    }

    public BigDecimal getCoberturaDias() {
        return coberturaDias;
    }

    public void setCoberturaDias(BigDecimal coberturaDias) {
        this.coberturaDias = coberturaDias;
    }

    public BigDecimal getExactitudPorcentaje() {
        return exactitudPorcentaje;
    }

    public void setExactitudPorcentaje(BigDecimal exactitudPorcentaje) {
        this.exactitudPorcentaje = exactitudPorcentaje;
    }

    public BigDecimal getTasaQuiebreStock() {
        return tasaQuiebreStock;
    }

    public void setTasaQuiebreStock(BigDecimal tasaQuiebreStock) {
        this.tasaQuiebreStock = tasaQuiebreStock;
    }

    public String getSaludInventario() {
        return saludInventario;
    }

    public void setSaludInventario(String saludInventario) {
        this.saludInventario = saludInventario;
    }

    public String getClasificacionRotacion() {
        return clasificacionRotacion;
    }

    public void setClasificacionRotacion(String clasificacionRotacion) {
        this.clasificacionRotacion = clasificacionRotacion;
    }
}
