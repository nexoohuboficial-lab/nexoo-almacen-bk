package com.nexoohub.almacen.metricas.entity;

import com.nexoohub.almacen.common.entity.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entidad para métricas consolidadas de ventas y clientes.
 * 
 * <p>Almacena snapshots de métricas clave de ventas y clientes para análisis de:</p>
 * <ul>
 *   <li>Rendimiento del equipo de ventas</li>
 *   <li>Comportamiento y retención de clientes</li>
 *   <li>Desempeño de vendedores</li>
 *   <li>Tendencias de venta por período</li>
 * </ul>
 * 
 * <p>Responde: <strong>¿Cómo está el rendimiento del equipo? ¿Qué clientes compran más?</strong></p>
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
@Entity
@Table(name = "metrica_venta_cliente", indexes = {
    @Index(name = "idx_metrica_venta_periodo", columnList = "periodo_inicio DESC, periodo_fin DESC"),
    @Index(name = "idx_metrica_venta_sucursal", columnList = "sucursal_id"),
    @Index(name = "idx_metrica_venta_total_desc", columnList = "total_ventas DESC"),
    @Index(name = "idx_metrica_venta_clientes_desc", columnList = "total_clientes_activos DESC")
})
public class MetricaVentaCliente extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ==================== PERÍODO ====================

    /**
     * Fecha de inicio del período analizado.
     */
    @NotNull(message = "La fecha de inicio del período es obligatoria")
    @Column(name = "periodo_inicio", nullable = false)
    private LocalDate periodoInicio;

    /**
     * Fecha de fin del período analizado.
     */
    @NotNull(message = "La fecha de fin del período es obligatoria")
    @Column(name = "periodo_fin", nullable = false)
    private LocalDate periodoFin;

    /**
     * Tipo de período: DIARIO, SEMANAL, MENSUAL, TRIMESTRAL, ANUAL.
     */
    @NotNull(message = "El tipo de período es obligatorio")
    @Column(name = "tipo_periodo", length = 20, nullable = false)
    private String tipoPeriodo;

    /**
     * ID de sucursal (NULL = consolidado de todas las sucursales).
     */
    @Column(name = "sucursal_id")
    private Integer sucursalId;

    /**
     * Nombre de la sucursal o "CONSOLIDADO".
     */
    @Column(name = "nombre_sucursal", length = 200)
    private String nombreSucursal;

    // ==================== MÉTRICAS DE VENTAS ====================

    /**
     * Total de ventas del período en valor monetario.
     */
    @NotNull
    @Min(value = 0, message = "El total de ventas no puede ser negativo")
    @Column(name = "total_ventas", precision = 15, scale = 2, nullable = false)
    private BigDecimal totalVentas = BigDecimal.ZERO;

    /**
     * Número total de transacciones (ventas) en el período.
     */
    @NotNull
    @Min(value = 0, message = "El número de transacciones no puede ser negativo")
    @Column(name = "numero_transacciones", nullable = false)
    private Integer numeroTransacciones = 0;

    /**
     * Ticket promedio (total ventas / número de transacciones).
     */
    @NotNull
    @Column(name = "ticket_promedio", precision = 15, scale = 2, nullable = false)
    private BigDecimal ticketPromedio = BigDecimal.ZERO;

    /**
     * Venta promedio por día del período.
     */
    @NotNull
    @Column(name = "venta_promedio_dia", precision = 15, scale = 2, nullable = false)
    private BigDecimal ventaPromedioDia = BigDecimal.ZERO;

    /**
     * Comparación con período anterior (porcentaje de crecimiento).
     * Positivo = crecimiento, Negativo = decrecimiento.
     */
    @Column(name = "crecimiento_vs_anterior", precision = 10, scale = 2)
    private BigDecimal crecimientoVsAnterior;

    // ==================== MÉTRICAS DE CLIENTES ====================

    /**
     * Total de clientes únicos que compraron en el período.
     */
    @NotNull
    @Min(value = 0, message = "Total clientes no puede ser negativo")
    @Column(name = "total_clientes_activos", nullable = false)
    private Integer totalClientesActivos = 0;

    /**
     * Nuevos clientes (primera compra en este período).
     */
    @NotNull
    @Min(value = 0, message = "Nuevos clientes no puede ser negativo")
    @Column(name = "clientes_nuevos", nullable = false)
    private Integer clientesNuevos = 0;

    /**
     * Clientes recurrentes (compraron antes y vuelven a comprar).
     */
    @NotNull
    @Min(value = 0, message = "Clientes recurrentes no puede ser negativo")
    @Column(name = "clientes_recurrentes", nullable = false)
    private Integer clientesRecurrentes = 0;

    /**
     * Clientes que no compraron en este período pero sí antes.
     */
    @NotNull
    @Min(value = 0, message = "Clientes inactivos no puede ser negativo")
    @Column(name = "clientes_inactivos", nullable = false)
    private Integer clientesInactivos = 0;

    /**
     * Tasa de retención de clientes (%).
     * (Clientes Recurrentes / Total Período Anterior) × 100
     */
    @Column(name = "tasa_retencion", precision = 5, scale = 2)
    private BigDecimal tasaRetencion;

    /**
     * Valor de vida promedio del cliente (LTV).
     * Total Ventas / Total Clientes Activos.
     */
    @Column(name = "valor_vida_cliente", precision = 15, scale = 2)
    private BigDecimal valorVidaCliente;

    /**
     * Frecuencia de compra promedio.
     * Número Transacciones / Total Clientes Activos.
     */
    @Column(name = "frecuencia_compra", precision = 10, scale = 2)
    private BigDecimal frecuenciaCompra;

    // ==================== MÉTRICAS DE EMPLEADOS (VENDEDORES) ====================

    /**
     * Total de vendedores activos en el período.
     */
    @NotNull
    @Min(value = 0, message = "Total vendedores no puede ser negativo")
    @Column(name = "total_vendedores", nullable = false)
    private Integer totalVendedores = 0;

    /**
     * ID del vendedor con mejor desempeño.
     */
    @Column(name = "top_vendedor_id")
    private Integer topVendedorId;

    /**
     * Nombre del vendedor con mejor desempeño.
     */
    @Column(name = "top_vendedor_nombre", length = 200)
    private String topVendedorNombre;

    /**
     * Total de ventas del top vendedor.
     */
    @Column(name = "top_vendedor_ventas", precision = 15, scale = 2)
    private BigDecimal topVendedorVentas;

    /**
     * Número de transacciones del top vendedor.
     */
    @Column(name = "top_vendedor_transacciones")
    private Integer topVendedorTransacciones;

    /**
     * Venta promedio por vendedor.
     */
    @Column(name = "venta_promedio_vendedor", precision = 15, scale = 2)
    private BigDecimal ventaPromedioVendedor;

    // ==================== MÉTRICAS POR MÉTODO DE PAGO ====================

    /**
     * Total de ventas pagadas en efectivo.
     */
    @NotNull
    @Column(name = "ventas_efectivo", precision = 15, scale = 2, nullable = false)
    private BigDecimal ventasEfectivo = BigDecimal.ZERO;

    /**
     * Total de ventas pagadas con tarjeta (crédito/débito).
     */
    @NotNull
    @Column(name = "ventas_tarjeta", precision = 15, scale = 2, nullable = false)
    private BigDecimal ventasTarjeta = BigDecimal.ZERO;

    /**
     * Total de ventas a crédito (pendientes de pago).
     */
    @NotNull
    @Column(name = "ventas_credito", precision = 15, scale = 2, nullable = false)
    private BigDecimal ventasCredito = BigDecimal.ZERO;

    /**
     * Porcentaje de ventas efectivo vs total.
     */
    @Column(name = "porcentaje_efectivo", precision = 5, scale = 2)
    private BigDecimal porcentajeEfectivo;

    // ==================== GETTERS Y SETTERS ====================

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

    public BigDecimal getTotalVentas() {
        return totalVentas;
    }

    public void setTotalVentas(BigDecimal totalVentas) {
        this.totalVentas = totalVentas;
    }

    public Integer getNumeroTransacciones() {
        return numeroTransacciones;
    }

    public void setNumeroTransacciones(Integer numeroTransacciones) {
        this.numeroTransacciones = numeroTransacciones;
    }

    public BigDecimal getTicketPromedio() {
        return ticketPromedio;
    }

    public void setTicketPromedio(BigDecimal ticketPromedio) {
        this.ticketPromedio = ticketPromedio;
    }

    public BigDecimal getVentaPromedioDia() {
        return ventaPromedioDia;
    }

    public void setVentaPromedioDia(BigDecimal ventaPromedioDia) {
        this.ventaPromedioDia = ventaPromedioDia;
    }

    public BigDecimal getCrecimientoVsAnterior() {
        return crecimientoVsAnterior;
    }

    public void setCrecimientoVsAnterior(BigDecimal crecimientoVsAnterior) {
        this.crecimientoVsAnterior = crecimientoVsAnterior;
    }

    public Integer getTotalClientesActivos() {
        return totalClientesActivos;
    }

    public void setTotalClientesActivos(Integer totalClientesActivos) {
        this.totalClientesActivos = totalClientesActivos;
    }

    public Integer getClientesNuevos() {
        return clientesNuevos;
    }

    public void setClientesNuevos(Integer clientesNuevos) {
        this.clientesNuevos = clientesNuevos;
    }

    public Integer getClientesRecurrentes() {
        return clientesRecurrentes;
    }

    public void setClientesRecurrentes(Integer clientesRecurrentes) {
        this.clientesRecurrentes = clientesRecurrentes;
    }

    public Integer getClientesInactivos() {
        return clientesInactivos;
    }

    public void setClientesInactivos(Integer clientesInactivos) {
        this.clientesInactivos = clientesInactivos;
    }

    public BigDecimal getTasaRetencion() {
        return tasaRetencion;
    }

    public void setTasaRetencion(BigDecimal tasaRetencion) {
        this.tasaRetencion = tasaRetencion;
    }

    public BigDecimal getValorVidaCliente() {
        return valorVidaCliente;
    }

    public void setValorVidaCliente(BigDecimal valorVidaCliente) {
        this.valorVidaCliente = valorVidaCliente;
    }

    public BigDecimal getFrecuenciaCompra() {
        return frecuenciaCompra;
    }

    public void setFrecuenciaCompra(BigDecimal frecuenciaCompra) {
        this.frecuenciaCompra = frecuenciaCompra;
    }

    public Integer getTotalVendedores() {
        return totalVendedores;
    }

    public void setTotalVendedores(Integer totalVendedores) {
        this.totalVendedores = totalVendedores;
    }

    public Integer getTopVendedorId() {
        return topVendedorId;
    }

    public void setTopVendedorId(Integer topVendedorId) {
        this.topVendedorId = topVendedorId;
    }

    public String getTopVendedorNombre() {
        return topVendedorNombre;
    }

    public void setTopVendedorNombre(String topVendedorNombre) {
        this.topVendedorNombre = topVendedorNombre;
    }

    public BigDecimal getTopVendedorVentas() {
        return topVendedorVentas;
    }

    public void setTopVendedorVentas(BigDecimal topVendedorVentas) {
        this.topVendedorVentas = topVendedorVentas;
    }

    public Integer getTopVendedorTransacciones() {
        return topVendedorTransacciones;
    }

    public void setTopVendedorTransacciones(Integer topVendedorTransacciones) {
        this.topVendedorTransacciones = topVendedorTransacciones;
    }

    public BigDecimal getVentaPromedioVendedor() {
        return ventaPromedioVendedor;
    }

    public void setVentaPromedioVendedor(BigDecimal ventaPromedioVendedor) {
        this.ventaPromedioVendedor = ventaPromedioVendedor;
    }

    public BigDecimal getVentasEfectivo() {
        return ventasEfectivo;
    }

    public void setVentasEfectivo(BigDecimal ventasEfectivo) {
        this.ventasEfectivo = ventasEfectivo;
    }

    public BigDecimal getVentasTarjeta() {
        return ventasTarjeta;
    }

    public void setVentasTarjeta(BigDecimal ventasTarjeta) {
        this.ventasTarjeta = ventasTarjeta;
    }

    public BigDecimal getVentasCredito() {
        return ventasCredito;
    }

    public void setVentasCredito(BigDecimal ventasCredito) {
        this.ventasCredito = ventasCredito;
    }

    public BigDecimal getPorcentajeEfectivo() {
        return porcentajeEfectivo;
    }

    public void setPorcentajeEfectivo(BigDecimal porcentajeEfectivo) {
        this.porcentajeEfectivo = porcentajeEfectivo;
    }
}
