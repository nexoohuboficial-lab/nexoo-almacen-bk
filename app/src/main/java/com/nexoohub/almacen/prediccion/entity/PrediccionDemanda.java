package com.nexoohub.almacen.prediccion.entity;

import com.nexoohub.almacen.common.entity.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entidad que representa una predicción de demanda para un producto.
 * 
 * <p>Almacena el análisis histórico y la proyección futura de demanda
 * para facilitar decisiones de compra.</p>
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
@Entity
@Table(name = "prediccion_demanda", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"sku_producto", "sucursal_id", "periodo_anio", "periodo_mes"}),
       indexes = {
           @Index(name = "idx_pred_sku", columnList = "sku_producto"),
           @Index(name = "idx_pred_sucursal", columnList = "sucursal_id"),
           @Index(name = "idx_pred_periodo", columnList = "periodo_anio, periodo_mes"),
           @Index(name = "idx_pred_fecha_calculo", columnList = "fecha_calculo")
       })
public class PrediccionDemanda extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * SKU del producto analizado
     */
    @NotNull(message = "El SKU del producto es obligatorio")
    @Column(name = "sku_producto", nullable = false, length = 50)
    private String skuProducto;

    /**
     * Sucursal para la que se calcula la predicción
     */
    @NotNull(message = "El ID de sucursal es obligatorio")
    @Column(name = "sucursal_id", nullable = false)
    private Integer sucursalId;

    /**
     * Año del periodo predicho
     */
    @NotNull(message = "El año del periodo es obligatorio")
    @Column(name = "periodo_anio", nullable = false)
    private Integer periodoAnio;

    /**
     * Mes del periodo predicho (1-12)
     */
    @NotNull(message = "El mes del periodo es obligatorio")
    @Min(value = 1, message = "El mes debe estar entre 1 y 12")
    @Column(name = "periodo_mes", nullable = false)
    private Integer periodoMes;

    /**
     * Demanda histórica promedio (unidades por periodo)
     */
    @NotNull(message = "La demanda histórica es obligatoria")
    @Column(name = "demanda_historica", nullable = false, precision = 10, scale = 2)
    private BigDecimal demandaHistorica;

    /**
     * Tendencia calculada (positiva = crecimiento, negativa = decrecimiento)
     */
    @Column(name = "tendencia", precision = 10, scale = 4)
    private BigDecimal tendencia;

    /**
     * Demanda predicha para el periodo (unidades)
     */
    @NotNull(message = "La demanda predicha es obligatoria")
    @Column(name = "demanda_predicha", nullable = false, precision = 10, scale = 2)
    private BigDecimal demandaPredicha;

    /**
     * Stock actual al momento del cálculo
     */
    @Column(name = "stock_actual", nullable = false)
    private Integer stockActual;

    /**
     * Stock de seguridad recomendado (días de inventario)
     */
    @Column(name = "stock_seguridad", nullable = false)
    private Integer stockSeguridad;

    /**
     * Stock sugerido para el periodo
     */
    @Column(name = "stock_sugerido", nullable = false)
    private Integer stockSugerido;

    /**
     * Cantidad recomendada para comprar
     */
    @Column(name = "cantidad_comprar", nullable = false)
    private Integer cantidadComprar;

    /**
     * Nivel de confianza de la predicción (0-100)
     */
    @Column(name = "nivel_confianza", precision = 5, scale = 2)
    private BigDecimal nivelConfianza;

    /**
     * Método usado para el cálculo
     * PROMEDIO_MOVIL, TENDENCIA_LINEAL, ESTACIONAL
     */
    @NotNull(message = "El método de cálculo es obligatorio")
    @Column(name = "metodo_calculo", nullable = false, length = 50)
    private String metodoCalculo;

    /**
     * Número de periodos históricos analizados
     */
    @Column(name = "periodos_analizados")
    private Integer periodosAnalizados;

    /**
     * Fecha en que se realizó el cálculo
     */
    @Column(name = "fecha_calculo", nullable = false)
    private LocalDate fechaCalculo;

    /**
     * Observaciones adicionales del análisis
     */
    @Column(name = "observaciones", length = 500)
    private String observaciones;

    // ==========================================
    // CONSTRUCTORES
    // ==========================================

    public PrediccionDemanda() {
        this.fechaCalculo = LocalDate.now();
    }

    // ==========================================
    // GETTERS Y SETTERS
    // ==========================================

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSkuProducto() {
        return skuProducto;
    }

    public void setSkuProducto(String skuProducto) {
        this.skuProducto = skuProducto;
    }

    public Integer getSucursalId() {
        return sucursalId;
    }

    public void setSucursalId(Integer sucursalId) {
        this.sucursalId = sucursalId;
    }

    public Integer getPeriodoAnio() {
        return periodoAnio;
    }

    public void setPeriodoAnio(Integer periodoAnio) {
        this.periodoAnio = periodoAnio;
    }

    public Integer getPeriodoMes() {
        return periodoMes;
    }

    public void setPeriodoMes(Integer periodoMes) {
        this.periodoMes = periodoMes;
    }

    public BigDecimal getDemandaHistorica() {
        return demandaHistorica;
    }

    public void setDemandaHistorica(BigDecimal demandaHistorica) {
        this.demandaHistorica = demandaHistorica;
    }

    public BigDecimal getTendencia() {
        return tendencia;
    }

    public void setTendencia(BigDecimal tendencia) {
        this.tendencia = tendencia;
    }

    public BigDecimal getDemandaPredicha() {
        return demandaPredicha;
    }

    public void setDemandaPredicha(BigDecimal demandaPredicha) {
        this.demandaPredicha = demandaPredicha;
    }

    public Integer getStockActual() {
        return stockActual;
    }

    public void setStockActual(Integer stockActual) {
        this.stockActual = stockActual;
    }

    public Integer getStockSeguridad() {
        return stockSeguridad;
    }

    public void setStockSeguridad(Integer stockSeguridad) {
        this.stockSeguridad = stockSeguridad;
    }

    public Integer getStockSugerido() {
        return stockSugerido;
    }

    public void setStockSugerido(Integer stockSugerido) {
        this.stockSugerido = stockSugerido;
    }

    public Integer getCantidadComprar() {
        return cantidadComprar;
    }

    public void setCantidadComprar(Integer cantidadComprar) {
        this.cantidadComprar = cantidadComprar;
    }

    public BigDecimal getNivelConfianza() {
        return nivelConfianza;
    }

    public void setNivelConfianza(BigDecimal nivelConfianza) {
        this.nivelConfianza = nivelConfianza;
    }

    public String getMetodoCalculo() {
        return metodoCalculo;
    }

    public void setMetodoCalculo(String metodoCalculo) {
        this.metodoCalculo = metodoCalculo;
    }

    public Integer getPeriodosAnalizados() {
        return periodosAnalizados;
    }

    public void setPeriodosAnalizados(Integer periodosAnalizados) {
        this.periodosAnalizados = periodosAnalizados;
    }

    public LocalDate getFechaCalculo() {
        return fechaCalculo;
    }

    public void setFechaCalculo(LocalDate fechaCalculo) {
        this.fechaCalculo = fechaCalculo;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
