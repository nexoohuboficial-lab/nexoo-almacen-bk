package com.nexoohub.almacen.metricas.entity;

import com.nexoohub.almacen.common.entity.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entidad que almacena métricas financieras consolidadas por período.
 * 
 * <p>Proporciona un snapshot de la salud financiera del negocio,
 * incluyendo ventas, costos, utilidades y márgenes.</p>
 * 
 * <p><strong>Métricas clave:</strong></p>
 * <ul>
 *   <li>Ventas Totales: Ingresos brutos del período</li>
 *   <li>Costo de Ventas (COGS): Costo de mercancía vendida</li>
 *   <li>Utilidad Bruta: Ventas - COGS</li>
 *   <li>Margen Bruto %: (Utilidad / Ventas) × 100</li>
 *   <li>Ticket Promedio: Ventas / Número de transacciones</li>
 * </ul>
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
@Entity
@Table(name = "metrica_financiera", indexes = {
    @Index(name = "idx_metrica_financiera_periodo", columnList = "periodo_inicio, periodo_fin"),
    @Index(name = "idx_metrica_financiera_periodo_fin", columnList = "periodo_fin DESC"),
    @Index(name = "idx_metrica_financiera_sucursal", columnList = "sucursal_id"),
    @Index(name = "idx_metrica_financiera_margen", columnList = "margen_bruto_porcentaje DESC")
})
public class MetricaFinanciera extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * ID de la sucursal (null = métricas consolidadas de todas las sucursales)
     */
    @Column(name = "sucursal_id")
    private Integer sucursalId;

    /**
     * Fecha de inicio del período de análisis
     */
    @NotNull(message = "La fecha de inicio es obligatoria")
    @Column(name = "periodo_inicio", nullable = false)
    private LocalDate periodoInicio;

    /**
     * Fecha de fin del período de análisis
     */
    @NotNull(message = "La fecha de fin es obligatoria")
    @Column(name = "periodo_fin", nullable = false)
    private LocalDate periodoFin;

    /**
     * Suma total de todas las ventas (venta.total) del período
     */
    @NotNull(message = "Las ventas totales son obligatorias")
    @DecimalMin(value = "0.0", message = "Las ventas totales no pueden ser negativas")
    @Column(name = "ventas_totales", nullable = false, precision = 15, scale = 2)
    private BigDecimal ventasTotales;

    /**
     * Suma del costo de mercancía vendida (COGS - Cost of Goods Sold)
     * Obtenido de rentabilidad_venta.costo_total
     */
    @NotNull(message = "El costo de ventas es obligatorio")
    @DecimalMin(value = "0.0", message = "El costo de ventas no puede ser negativo")
    @Column(name = "costo_ventas", nullable = false, precision = 15, scale = 2)
    private BigDecimal costoVentas;

    /**
     * Utilidad bruta del período (Ventas - COGS)
     */
    @NotNull(message = "La utilidad bruta es obligatoria")
    @Column(name = "utilidad_bruta", nullable = false, precision = 15, scale = 2)
    private BigDecimal utilidadBruta;

    /**
     * Margen de utilidad bruta en porcentaje
     * Fórmula: (Utilidad Bruta / Ventas Totales) × 100
     */
    @NotNull(message = "El margen bruto es obligatorio")
    @Column(name = "margen_bruto_porcentaje", nullable = false, precision = 5, scale = 2)
    private BigDecimal margenBrutoPorcentaje;

    /**
     * Total de gastos operativos del período (comisiones, devoluciones, etc.)
     */
    @Column(name = "gastos_operativos", precision = 15, scale = 2)
    private BigDecimal gastosOperativos;

    /**
     * Utilidad neta después de gastos operativos
     * Fórmula: Utilidad Bruta - Gastos Operativos
     */
    @Column(name = "utilidad_neta", precision = 15, scale = 2)
    private BigDecimal utilidadNeta;

    /**
     * Margen de utilidad neta en porcentaje
     * Fórmula: (Utilidad Neta / Ventas Totales) × 100
     */
    @Column(name = "margen_neto_porcentaje", precision = 5, scale = 2)
    private BigDecimal margenNetoPorcentaje;

    /**
     * Número total de ventas (transacciones) del período
     */
    @NotNull(message = "El número de ventas es obligatorio")
    @Column(name = "numero_ventas", nullable = false)
    private Integer numeroVentas;

    /**
     * Ticket promedio por venta
     * Fórmula: Ventas Totales / Número de Ventas
     */
    @NotNull(message = "El ticket promedio es obligatorio")
    @Column(name = "ticket_promedio", nullable = false, precision = 15, scale = 2)
    private BigDecimal ticketPromedio;

    /**
     * Número de clientes únicos que realizaron compras en el período
     */
    @Column(name = "clientes_unicos")
    private Integer clientesUnicos;

    /**
     * Ventas por crédito (método_pago = 'CREDITO')
     */
    @Column(name = "ventas_credito", precision = 15, scale = 2)
    private BigDecimal ventasCredito;

    /**
     * Ventas en efectivo (método_pago = 'EFECTIVO')
     */
    @Column(name = "ventas_efectivo", precision = 15, scale = 2)
    private BigDecimal ventasEfectivo;

    /**
     * Tipo de período: DIARIO, SEMANAL, MENSUAL, TRIMESTRAL, ANUAL, PERSONALIZADO
     */
    @Column(name = "tipo_periodo", length = 20)
    private String tipoPeriodo;

    // Constructor vacío
    public MetricaFinanciera() {}

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getSucursalId() {
        return sucursalId;
    }

    public void setSucursalId(Integer sucursalId) {
        this.sucursalId = sucursalId;
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

    public BigDecimal getVentasTotales() {
        return ventasTotales;
    }

    public void setVentasTotales(BigDecimal ventasTotales) {
        this.ventasTotales = ventasTotales;
    }

    public BigDecimal getCostoVentas() {
        return costoVentas;
    }

    public void setCostoVentas(BigDecimal costoVentas) {
        this.costoVentas = costoVentas;
    }

    public BigDecimal getUtilidadBruta() {
        return utilidadBruta;
    }

    public void setUtilidadBruta(BigDecimal utilidadBruta) {
        this.utilidadBruta = utilidadBruta;
    }

    public BigDecimal getMargenBrutoPorcentaje() {
        return margenBrutoPorcentaje;
    }

    public void setMargenBrutoPorcentaje(BigDecimal margenBrutoPorcentaje) {
        this.margenBrutoPorcentaje = margenBrutoPorcentaje;
    }

    public BigDecimal getGastosOperativos() {
        return gastosOperativos;
    }

    public void setGastosOperativos(BigDecimal gastosOperativos) {
        this.gastosOperativos = gastosOperativos;
    }

    public BigDecimal getUtilidadNeta() {
        return utilidadNeta;
    }

    public void setUtilidadNeta(BigDecimal utilidadNeta) {
        this.utilidadNeta = utilidadNeta;
    }

    public BigDecimal getMargenNetoPorcentaje() {
        return margenNetoPorcentaje;
    }

    public void setMargenNetoPorcentaje(BigDecimal margenNetoPorcentaje) {
        this.margenNetoPorcentaje = margenNetoPorcentaje;
    }

    public Integer getNumeroVentas() {
        return numeroVentas;
    }

    public void setNumeroVentas(Integer numeroVentas) {
        this.numeroVentas = numeroVentas;
    }

    public BigDecimal getTicketPromedio() {
        return ticketPromedio;
    }

    public void setTicketPromedio(BigDecimal ticketPromedio) {
        this.ticketPromedio = ticketPromedio;
    }

    public Integer getClientesUnicos() {
        return clientesUnicos;
    }

    public void setClientesUnicos(Integer clientesUnicos) {
        this.clientesUnicos = clientesUnicos;
    }

    public BigDecimal getVentasCredito() {
        return ventasCredito;
    }

    public void setVentasCredito(BigDecimal ventasCredito) {
        this.ventasCredito = ventasCredito;
    }

    public BigDecimal getVentasEfectivo() {
        return ventasEfectivo;
    }

    public void setVentasEfectivo(BigDecimal ventasEfectivo) {
        this.ventasEfectivo = ventasEfectivo;
    }

    public String getTipoPeriodo() {
        return tipoPeriodo;
    }

    public void setTipoPeriodo(String tipoPeriodo) {
        this.tipoPeriodo = tipoPeriodo;
    }
}
