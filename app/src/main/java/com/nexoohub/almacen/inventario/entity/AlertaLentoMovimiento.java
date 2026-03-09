package com.nexoohub.almacen.inventario.entity;

import com.nexoohub.almacen.common.entity.AuditableEntity;
import com.nexoohub.almacen.sucursal.entity.Sucursal;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entidad que representa una alerta de producto de lento movimiento.
 * 
 * <p>Un producto se considera de lento movimiento cuando no ha tenido ventas
 * durante un periodo configurable (ej. 30, 60, 90 días). Esta entidad permite
 * trackear productos con baja rotación para tomar acciones correctivas como:</p>
 * <ul>
 *   <li>Liquidaciones</li>
 *   <li>Promociones</li>
 *   <li>Transferencias a otras sucursales</li>
 *   <li>Descontinuación del producto</li>
 * </ul>
 * 
 * @author NexooHub Development Team
 * @since 1.2.0
 */
@Entity
@Table(name = "alerta_lento_movimiento", indexes = {
    @Index(name = "idx_alm_sucursal", columnList = "sucursal_id"),
    @Index(name = "idx_alm_sku", columnList = "sku_interno"),
    @Index(name = "idx_alm_estado", columnList = "estado_alerta"),
    @Index(name = "idx_alm_fecha_deteccion", columnList = "fecha_deteccion")
})
public class AlertaLentoMovimiento extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "El SKU interno es obligatorio")
    @Column(name = "sku_interno", length = 50, nullable = false)
    private String skuInterno;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sku_interno", insertable = false, updatable = false)
    private ProductoMaestro producto;

    @NotNull(message = "La sucursal es obligatoria")
    @Column(name = "sucursal_id", nullable = false)
    private Integer sucursalId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sucursal_id", insertable = false, updatable = false)
    private Sucursal sucursal;

    @NotNull(message = "Los días sin venta son obligatorios")
    @Min(value = 0, message = "Los días sin venta no pueden ser negativos")
    @Column(name = "dias_sin_venta", nullable = false)
    private Integer diasSinVenta;

    @Column(name = "ultima_venta")
    private LocalDate ultimaVenta;

    @NotNull(message = "El stock actual es obligatorio")
    @Min(value = 0, message = "El stock actual no puede ser negativo")
    @Column(name = "stock_actual", nullable = false)
    private Integer stockActual;

    @NotNull(message = "El costo inmovilizado es obligatorio")
    @DecimalMin(value = "0.0", message = "El costo inmovilizado no puede ser negativo")
    @Column(name = "costo_inmovilizado", precision = 10, scale = 2, nullable = false)
    private BigDecimal costoInmovilizado = BigDecimal.ZERO;

    @NotBlank(message = "El estado de la alerta es obligatorio")
    @Column(name = "estado_alerta", length = 20, nullable = false)
    private String estadoAlerta; // ADVERTENCIA (30-60 días), CRITICO (>60 días), RESUELTA

    @NotNull(message = "La fecha de detección es obligatoria")
    @Column(name = "fecha_deteccion", nullable = false)
    private LocalDate fechaDeteccion;

    @Column(name = "fecha_resolucion")
    private LocalDate fechaResolucion;

    @Column(name = "accion_tomada", length = 100)
    private String accionTomada; // LIQUIDACION, PROMOCION, TRANSFERENCIA, DESCONTINUADO, NINGUNA

    @Column(name = "observaciones", length = 500)
    private String observaciones;

    @Column(name = "resuelto", nullable = false)
    private Boolean resuelto = false;

    // ==========================================
    // MÉTODOS DE NEGOCIO
    // ==========================================

    /**
     * Calcula el costo inmovilizado basado en stock actual y costo promedio ponderado.
     * 
     * @param cpp Costo Promedio Ponderado del producto en la sucursal
     */
    public void calcularCostoInmovilizado(BigDecimal cpp) {
        if (cpp != null && stockActual != null) {
            this.costoInmovilizado = cpp.multiply(new BigDecimal(stockActual));
        }
    }

    /**
     * Determina el nivel de alerta basado en los días sin venta.
     * 
     * <ul>
     *   <li>ADVERTENCIA: 30-60 días sin venta</li>
     *   <li>CRITICO: más de 60 días sin venta</li>
     * </ul>
     */
    public void determinarEstadoAlerta() {
        if (diasSinVenta != null) {
            if (diasSinVenta >= 60) {
                this.estadoAlerta = "CRITICO";
            } else if (diasSinVenta >= 30) {
                this.estadoAlerta = "ADVERTENCIA";
            } else {
                this.estadoAlerta = "NORMAL";
            }
        }
    }

    /**
     * Marca la alerta como resuelta con la acción tomada.
     * 
     * @param accion Acción correctiva aplicada
     * @param observaciones Notas adicionales
     */
    public void resolver(String accion, String observaciones) {
        this.resuelto = true;
        this.estadoAlerta = "RESUELTA";
        this.fechaResolucion = LocalDate.now();
        this.accionTomada = accion;
        this.observaciones = observaciones;
    }

    // ==========================================
    // GETTERS Y SETTERS
    // ==========================================

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getSkuInterno() { return skuInterno; }
    public void setSkuInterno(String skuInterno) { this.skuInterno = skuInterno; }

    public ProductoMaestro getProducto() { return producto; }
    public void setProducto(ProductoMaestro producto) { this.producto = producto; }

    public Integer getSucursalId() { return sucursalId; }
    public void setSucursalId(Integer sucursalId) { this.sucursalId = sucursalId; }

    public Sucursal getSucursal() { return sucursal; }
    public void setSucursal(Sucursal sucursal) { this.sucursal = sucursal; }

    public Integer getDiasSinVenta() { return diasSinVenta; }
    public void setDiasSinVenta(Integer diasSinVenta) { this.diasSinVenta = diasSinVenta; }

    public LocalDate getUltimaVenta() { return ultimaVenta; }
    public void setUltimaVenta(LocalDate ultimaVenta) { this.ultimaVenta = ultimaVenta; }

    public Integer getStockActual() { return stockActual; }
    public void setStockActual(Integer stockActual) { this.stockActual = stockActual; }

    public BigDecimal getCostoInmovilizado() { return costoInmovilizado; }
    public void setCostoInmovilizado(BigDecimal costoInmovilizado) { 
        this.costoInmovilizado = costoInmovilizado; 
    }

    public String getEstadoAlerta() { return estadoAlerta; }
    public void setEstadoAlerta(String estadoAlerta) { this.estadoAlerta = estadoAlerta; }

    public LocalDate getFechaDeteccion() { return fechaDeteccion; }
    public void setFechaDeteccion(LocalDate fechaDeteccion) { this.fechaDeteccion = fechaDeteccion; }

    public LocalDate getFechaResolucion() { return fechaResolucion; }
    public void setFechaResolucion(LocalDate fechaResolucion) { 
        this.fechaResolucion = fechaResolucion; 
    }

    public String getAccionTomada() { return accionTomada; }
    public void setAccionTomada(String accionTomada) { this.accionTomada = accionTomada; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public Boolean getResuelto() { return resuelto; }
    public void setResuelto(Boolean resuelto) { this.resuelto = resuelto; }
}
