package com.nexoohub.almacen.metricas.entity;

import com.nexoohub.almacen.common.entity.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entidad que almacena métricas operacionales consolidadas.
 * 
 * <p>Esta entidad registra snapshots periódicos de la eficiencia operativa,
 * midiendo el rendimiento de:</p>
 * <ul>
 *   <li><strong>Traspasos:</strong> Volumen y frecuencia de movimientos entre sucursales</li>
 *   <li><strong>Compras:</strong> Frecuencia, volumen y gasto en adquisiciones</li>
 *   <li><strong>Ventas:</strong> Frecuencia, volumen e ingresos generados</li>
 *   <li><strong>Eficiencia:</strong> Ratios operacionales y productividad</li>
 * </ul>
 * 
 * <p>Responde preguntas clave como:</p>
 * <ul>
 *   <li>¿Cuánto movimiento hay entre sucursales?</li>
 *   <li>¿Qué tan frecuentes son nuestras compras?</li>
 *   <li>¿Cuál es la velocidad de rotación del inventario?</li>
 *   <li>¿Estamos siendo eficientes en las operaciones?</li>
 * </ul>
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
@Entity
@Table(
    name = "metrica_operativa",
    indexes = {
        @Index(name = "idx_metrica_operativa_periodo", columnList = "periodo_inicio DESC, periodo_fin DESC"),
        @Index(name = "idx_metrica_operativa_sucursal", columnList = "sucursal_id"),
        @Index(name = "idx_metrica_operativa_tipo", columnList = "tipo_periodo"),
        @Index(name = "idx_metrica_operativa_ventas", columnList = "total_ventas DESC")
    }
)
public class MetricaOperativa extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ==========================================
    // PERÍODO DE LA MÉTRICA
    // ==========================================

    /**
     * Fecha de inicio del período analizado.
     */
    @NotNull(message = "La fecha de inicio del período es obligatoria")
    @Column(name = "periodo_inicio", nullable = false)
    private LocalDate periodoInicio;

    /**
     * Fecha de fin del período analizado (inclusiva).
     */
    @NotNull(message = "La fecha de fin del período es obligatoria")
    @Column(name = "periodo_fin", nullable = false)
    private LocalDate periodoFin;

    /**
     * Tipo de período: DIARIO, SEMANAL, MENSUAL, TRIMESTRAL, ANUAL.
     */
    @NotNull(message = "El tipo de período es obligatorio")
    @Column(name = "tipo_periodo", nullable = false, length = 20)
    private String tipoPeriodo;

    /**
     * ID de la sucursal analizada. NULL = métrica consolidada de todas las sucursales.
     */
    @Column(name = "sucursal_id")
    private Integer sucursalId;

    /**
     * Nombre de la sucursal para fácil identificación.
     */
    @Column(name = "nombre_sucursal", length = 100)
    private String nombreSucursal;

    /**
     * Número de días del período (calculado).
     */
    @NotNull
    @Min(1)
    @Column(name = "dias_periodo", nullable = false)
    private Integer diasPeriodo;

    // ==========================================
    // MÉTRICAS DE TRASPASOS
    // ==========================================

    /**
     * Número total de traspasos realizados (conteo de rastreoIds únicos).
     */
    @Column(name = "total_traspasos")
    private Integer totalTraspasos;

    /**
     * Unidades recibidas por traspasos (ENTRADA_TRASPASO).
     */
    @Column(name = "unidades_traspaso_entrada")
    private Integer unidadesTraspasoEntrada;

    /**
     * Unidades enviadas por traspasos (SALIDA_TRASPASO).
     */
    @Column(name = "unidades_traspaso_salida")
    private Integer unidadesTraspasoSalida;

    /**
     * Balance neto de traspasos (entrada - salida).
     */
    @Column(name = "unidades_traspaso_neto")
    private Integer unidadesTraspasoNeto;

    // ==========================================
    // MÉTRICAS DE COMPRAS
    // ==========================================

    /**
     * Número total de compras registradas.
     */
    @Column(name = "total_compras")
    private Integer totalCompras;

    /**
     * Total de unidades compradas (suma de detalles de compra).
     */
    @Column(name = "unidades_compradas")
    private Integer unidadesCompradas;

    /**
     * Gasto total en compras (suma de totalCompra).
     */
    @Column(name = "gasto_total_compras", precision = 15, scale = 2)
    private BigDecimal gastoTotalCompras;

    /**
     * Compra promedio (gastoTotal / totalCompras).
     */
    @Column(name = "compra_promedio", precision = 15, scale = 2)
    private BigDecimal compraPromedio;

    /**
     * Frecuencia de compras (compras por día).
     */
    @Column(name = "frecuencia_compras", precision = 10, scale = 2)
    private BigDecimal frecuenciaCompras;

    // ==========================================
    // MÉTRICAS DE VENTAS
    // ==========================================

    /**
     * Número total de ventas realizadas.
     */
    @Column(name = "total_ventas")
    private Integer totalVentas;

    /**
     * Total de unidades vendidas (suma de detalles de venta).
     */
    @Column(name = "unidades_vendidas")
    private Integer unidadesVendidas;

    /**
     * Ingreso total por ventas (suma de total de ventas).
     */
    @Column(name = "ingreso_total_ventas", precision = 15, scale = 2)
    private BigDecimal ingresoTotalVentas;

    /**
     * Venta promedio (ingresoTotal / totalVentas).
     */
    @Column(name = "venta_promedio", precision = 15, scale = 2)
    private BigDecimal ventaPromedio;

    /**
     * Frecuencia de ventas (ventas por día).
     */
    @Column(name = "frecuencia_ventas", precision = 10, scale = 2)
    private BigDecimal frecuenciaVentas;

    // ==========================================
    // MÉTRICAS DE EFICIENCIA Y PRODUCTIVIDAD
    // ==========================================

    /**
     * Ratio de entrada/salida: (compras + traspasoEntrada) / (ventas + traspasoSalida).
     * > 1 = más entrada que salida (acumulando inventario)
     * < 1 = más salida que entrada (reduciendo inventario)
     */
    @Column(name = "ratio_entrada_salida", precision = 10, scale = 4)
    private BigDecimal ratioEntradaSalida;

    /**
     * Productividad de ventas (ingreso por día).
     */
    @Column(name = "productividad_diaria_ventas", precision = 15, scale = 2)
    private BigDecimal productividadDiariaVentas;

    /**
     * Tasa de rotación de inventario (unidades vendidas / unidades totales movidas).
     */
    @Column(name = "tasa_rotacion_inventario", precision = 10, scale = 4)
    private BigDecimal tasaRotacionInventario;

    /**
     * Total de movimientos operacionales (traspasos + compras + ventas).
     */
    @Column(name = "total_operaciones")
    private Integer totalOperaciones;

    /**
     * Promedio de operaciones por día.
     */
    @Column(name = "operaciones_promedio_dia", precision = 10, scale = 2)
    private BigDecimal operacionesPoDia;

    // ==========================================
    // INDICADORES DE RENDIMIENTO
    // ==========================================

    /**
     * Clasificación del período: ALTO (>75 operaciones/día), MEDIO (25-75), BAJO (<25).
     */
    @Column(name = "clasificacion_actividad", length = 20)
    private String clasificacionActividad;

    /**
     * Balance operacional: POSITIVO (más entradas), NEGATIVO (más salidas), EQUILIBRADO.
     */
    @Column(name = "balance_operacional", length = 20)
    private String balanceOperacional;

    // ==========================================
    // GETTERS Y SETTERS
    // ==========================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getPeriodoInicio() {
        return periodoInicio;
    }

    public void setPeriodoInicio(LocalDate periodoInicio) {
        this.periodoInicio = periodoInicio;
    }

    public LocalDate getPeriodoFin() {
        return periodoFin;
    }

    public void setPeriodoFin(LocalDate periodoFin) {
        this.periodoFin = periodoFin;
    }

    public String getTipoPeriodo() {
        return tipoPeriodo;
    }

    public void setTipoPeriodo(String tipoPeriodo) {
        this.tipoPeriodo = tipoPeriodo;
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

    public Integer getDiasPeriodo() {
        return diasPeriodo;
    }

    public void setDiasPeriodo(Integer diasPeriodo) {
        this.diasPeriodo = diasPeriodo;
    }

    public Integer getTotalTraspasos() {
        return totalTraspasos;
    }

    public void setTotalTraspasos(Integer totalTraspasos) {
        this.totalTraspasos = totalTraspasos;
    }

    public Integer getUnidadesTraspasoEntrada() {
        return unidadesTraspasoEntrada;
    }

    public void setUnidadesTraspasoEntrada(Integer unidadesTraspasoEntrada) {
        this.unidadesTraspasoEntrada = unidadesTraspasoEntrada;
    }

    public Integer getUnidadesTraspasoSalida() {
        return unidadesTraspasoSalida;
    }

    public void setUnidadesTraspasoSalida(Integer unidadesTraspasoSalida) {
        this.unidadesTraspasoSalida = unidadesTraspasoSalida;
    }

    public Integer getUnidadesTraspasoNeto() {
        return unidadesTraspasoNeto;
    }

    public void setUnidadesTraspasoNeto(Integer unidadesTraspasoNeto) {
        this.unidadesTraspasoNeto = unidadesTraspasoNeto;
    }

    public Integer getTotalCompras() {
        return totalCompras;
    }

    public void setTotalCompras(Integer totalCompras) {
        this.totalCompras = totalCompras;
    }

    public Integer getUnidadesCompradas() {
        return unidadesCompradas;
    }

    public void setUnidadesCompradas(Integer unidadesCompradas) {
        this.unidadesCompradas = unidadesCompradas;
    }

    public BigDecimal getGastoTotalCompras() {
        return gastoTotalCompras;
    }

    public void setGastoTotalCompras(BigDecimal gastoTotalCompras) {
        this.gastoTotalCompras = gastoTotalCompras;
    }

    public BigDecimal getCompraPromedio() {
        return compraPromedio;
    }

    public void setCompraPromedio(BigDecimal compraPromedio) {
        this.compraPromedio = compraPromedio;
    }

    public BigDecimal getFrecuenciaCompras() {
        return frecuenciaCompras;
    }

    public void setFrecuenciaCompras(BigDecimal frecuenciaCompras) {
        this.frecuenciaCompras = frecuenciaCompras;
    }

    public Integer getTotalVentas() {
        return totalVentas;
    }

    public void setTotalVentas(Integer totalVentas) {
        this.totalVentas = totalVentas;
    }

    public Integer getUnidadesVendidas() {
        return unidadesVendidas;
    }

    public void setUnidadesVendidas(Integer unidadesVendidas) {
        this.unidadesVendidas = unidadesVendidas;
    }

    public BigDecimal getIngresoTotalVentas() {
        return ingresoTotalVentas;
    }

    public void setIngresoTotalVentas(BigDecimal ingresoTotalVentas) {
        this.ingresoTotalVentas = ingresoTotalVentas;
    }

    public BigDecimal getVentaPromedio() {
        return ventaPromedio;
    }

    public void setVentaPromedio(BigDecimal ventaPromedio) {
        this.ventaPromedio = ventaPromedio;
    }

    public BigDecimal getFrecuenciaVentas() {
        return frecuenciaVentas;
    }

    public void setFrecuenciaVentas(BigDecimal frecuenciaVentas) {
        this.frecuenciaVentas = frecuenciaVentas;
    }

    public BigDecimal getRatioEntradaSalida() {
        return ratioEntradaSalida;
    }

    public void setRatioEntradaSalida(BigDecimal ratioEntradaSalida) {
        this.ratioEntradaSalida = ratioEntradaSalida;
    }

    public BigDecimal getProductividadDiariaVentas() {
        return productividadDiariaVentas;
    }

    public void setProductividadDiariaVentas(BigDecimal productividadDiariaVentas) {
        this.productividadDiariaVentas = productividadDiariaVentas;
    }

    public BigDecimal getTasaRotacionInventario() {
        return tasaRotacionInventario;
    }

    public void setTasaRotacionInventario(BigDecimal tasaRotacionInventario) {
        this.tasaRotacionInventario = tasaRotacionInventario;
    }

    public Integer getTotalOperaciones() {
        return totalOperaciones;
    }

    public void setTotalOperaciones(Integer totalOperaciones) {
        this.totalOperaciones = totalOperaciones;
    }

    public BigDecimal getOperacionesPoDia() {
        return operacionesPoDia;
    }

    public void setOperacionesPoDia(BigDecimal operacionesPoDia) {
        this.operacionesPoDia = operacionesPoDia;
    }

    public String getClasificacionActividad() {
        return clasificacionActividad;
    }

    public void setClasificacionActividad(String clasificacionActividad) {
        this.clasificacionActividad = clasificacionActividad;
    }

    public String getBalanceOperacional() {
        return balanceOperacional;
    }

    public void setBalanceOperacional(String balanceOperacional) {
        this.balanceOperacional = balanceOperacional;
    }
}
