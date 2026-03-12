package com.nexoohub.almacen.metricas.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO de respuesta con métricas operacionales completas.
 * 
 * <p>Contiene todas las métricas calculadas para el período solicitado,
 * organizadas en secciones de:</p>
 * <ul>
 *   <li>Información del período</li>
 *   <li>Métricas de traspasos</li>
 *   <li>Métricas de compras</li>
 *   <li>Métricas de ventas</li>
 *   <li>Indicadores de eficiencia</li>
 *   <li>Comparación con período anterior (opcional)</li>
 *   <li>Detalle de sucursales (opcional)</li>
 * </ul>
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
public class MetricaOperativaResponseDTO {

    // ==========================================
    // INFORMACIÓN DEL PERÍODO
    // ==========================================

    /**
     * Fecha de inicio del período analizado.
     */
    private LocalDate periodoInicio;

    /**
     * Fecha de fin del período analizado.
     */
    private LocalDate periodoFin;

    /**
     * Tipo de período: DIARIO, SEMANAL, MENSUAL, TRIMESTRAL, ANUAL.
     */
    private String tipoPeriodo;

    /**
     * Número de días del período.
     */
    private Integer diasPeriodo;

    /**
     * ID de la sucursal (null = consolidado).
     */
    private Integer sucursalId;

    /**
     * Nombre de la sucursal.
     */
    private String nombreSucursal;

    // ==========================================
    // MÉTRICAS DE TRASPASOS
    // ==========================================

    private ResumenTraspasosDTO traspasos;

    // ==========================================
    // MÉTRICAS DE COMPRAS
    // ==========================================

    private ResumenComprasDTO compras;

    // ==========================================
    // MÉTRICAS DE VENTAS
    // ==========================================

    private ResumenVentasDTO ventas;

    // ==========================================
    // INDICADORES DE EFICIENCIA
    // ==========================================

    private IndicadoresEficienciaDTO eficiencia;

    // ==========================================
    // COMPARACIÓN CON PERÍODO ANTERIOR
    // ==========================================

    /**
     * Comparación con el período anterior (si se solicitó).
     */
    private ComparacionPeriodoDTO comparacion;

    // ==========================================
    // DETALLE DE SUCURSALES
    // ==========================================

    /**
     * Lista de métricas por sucursal (en análisis consolidado).
     */
    private List<DetalleSucursalDTO> detalleSucursales;

    // ==========================================
    // DTOs ANIDADOS
    // ==========================================

    /**
     * DTO con resumen de traspasos.
     */
    public static class ResumenTraspasosDTO {
        private Integer totalTraspasos;
        private Integer unidadesEntrada;
        private Integer unidadesSalida;
        private Integer unidadesNeto;
        private BigDecimal promedioUnidadesPorTraspaso;
        private String tendenciaTraspaso; // ENTRADA_NETA, SALIDA_NETA, EQUILIBRADO

        public ResumenTraspasosDTO() {
        }

        public ResumenTraspasosDTO(Integer totalTraspasos, Integer unidadesEntrada, Integer unidadesSalida,
                                    Integer unidadesNeto, BigDecimal promedioUnidadesPorTraspaso, String tendenciaTraspaso) {
            this.totalTraspasos = totalTraspasos;
            this.unidadesEntrada = unidadesEntrada;
            this.unidadesSalida = unidadesSalida;
            this.unidadesNeto = unidadesNeto;
            this.promedioUnidadesPorTraspaso = promedioUnidadesPorTraspaso;
            this.tendenciaTraspaso = tendenciaTraspaso;
        }

        public Integer getTotalTraspasos() {
            return totalTraspasos;
        }

        public void setTotalTraspasos(Integer totalTraspasos) {
            this.totalTraspasos = totalTraspasos;
        }

        public Integer getUnidadesEntrada() {
            return unidadesEntrada;
        }

        public void setUnidadesEntrada(Integer unidadesEntrada) {
            this.unidadesEntrada = unidadesEntrada;
        }

        public Integer getUnidadesSalida() {
            return unidadesSalida;
        }

        public void setUnidadesSalida(Integer unidadesSalida) {
            this.unidadesSalida = unidadesSalida;
        }

        public Integer getUnidadesNeto() {
            return unidadesNeto;
        }

        public void setUnidadesNeto(Integer unidadesNeto) {
            this.unidadesNeto = unidadesNeto;
        }

        public BigDecimal getPromedioUnidadesPorTraspaso() {
            return promedioUnidadesPorTraspaso;
        }

        public void setPromedioUnidadesPorTraspaso(BigDecimal promedioUnidadesPorTraspaso) {
            this.promedioUnidadesPorTraspaso = promedioUnidadesPorTraspaso;
        }

        public String getTendenciaTraspaso() {
            return tendenciaTraspaso;
        }

        public void setTendenciaTraspaso(String tendenciaTraspaso) {
            this.tendenciaTraspaso = tendenciaTraspaso;
        }
    }

    /**
     * DTO con resumen de compras.
     */
    public static class ResumenComprasDTO {
        private Integer totalCompras;
        private Integer unidadesCompradas;
        private BigDecimal gastoTotal;
        private BigDecimal compraPromedio;
        private BigDecimal frecuenciaCompras; // compras por día

        public ResumenComprasDTO() {
        }

        public ResumenComprasDTO(Integer totalCompras, Integer unidadesCompradas, BigDecimal gastoTotal,
                                  BigDecimal compraPromedio, BigDecimal frecuenciaCompras) {
            this.totalCompras = totalCompras;
            this.unidadesCompradas = unidadesCompradas;
            this.gastoTotal = gastoTotal;
            this.compraPromedio = compraPromedio;
            this.frecuenciaCompras = frecuenciaCompras;
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

        public BigDecimal getGastoTotal() {
            return gastoTotal;
        }

        public void setGastoTotal(BigDecimal gastoTotal) {
            this.gastoTotal = gastoTotal;
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
    }

    /**
     * DTO con resumen de ventas.
     */
    public static class ResumenVentasDTO {
        private Integer totalVentas;
        private Integer unidadesVendidas;
        private BigDecimal ingresoTotal;
        private BigDecimal ventaPromedio;
        private BigDecimal frecuenciaVentas; // ventas por día

        public ResumenVentasDTO() {
        }

        public ResumenVentasDTO(Integer totalVentas, Integer unidadesVendidas, BigDecimal ingresoTotal,
                                 BigDecimal ventaPromedio, BigDecimal frecuenciaVentas) {
            this.totalVentas = totalVentas;
            this.unidadesVendidas = unidadesVendidas;
            this.ingresoTotal = ingresoTotal;
            this.ventaPromedio = ventaPromedio;
            this.frecuenciaVentas = frecuenciaVentas;
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

        public BigDecimal getIngresoTotal() {
            return ingresoTotal;
        }

        public void setIngresoTotal(BigDecimal ingresoTotal) {
            this.ingresoTotal = ingresoTotal;
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
    }

    /**
     * DTO con indicadores de eficiencia y productividad.
     */
    public static class IndicadoresEficienciaDTO {
        private BigDecimal ratioEntradaSalida; // (compras+traspasoEntrada)/(ventas+traspasoSalida)
        private BigDecimal productividadDiariaVentas; // ingreso/día
        private BigDecimal tasaRotacionInventario; // unidadesVendidas/totalMovido
        private Integer totalOperaciones; // traspasos + compras + ventas
        private BigDecimal operacionesPorDia;
        private String clasificacionActividad; // ALTO, MEDIO, BAJO
        private String balanceOperacional; // POSITIVO, NEGATIVO, EQUILIBRADO

        public IndicadoresEficienciaDTO() {
        }

        public IndicadoresEficienciaDTO(BigDecimal ratioEntradaSalida, BigDecimal productividadDiariaVentas,
                                         BigDecimal tasaRotacionInventario, Integer totalOperaciones,
                                         BigDecimal operacionesPorDia, String clasificacionActividad,
                                         String balanceOperacional) {
            this.ratioEntradaSalida = ratioEntradaSalida;
            this.productividadDiariaVentas = productividadDiariaVentas;
            this.tasaRotacionInventario = tasaRotacionInventario;
            this.totalOperaciones = totalOperaciones;
            this.operacionesPorDia = operacionesPorDia;
            this.clasificacionActividad = clasificacionActividad;
            this.balanceOperacional = balanceOperacional;
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

        public BigDecimal getOperacionesPorDia() {
            return operacionesPorDia;
        }

        public void setOperacionesPorDia(BigDecimal operacionesPorDia) {
            this.operacionesPorDia = operacionesPorDia;
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

    /**
     * DTO con comparación con el período anterior.
     */
    public static class ComparacionPeriodoDTO {
        private Integer operacionesAnterior;
        private Integer operacionesActual;
        private BigDecimal crecimientoOperaciones; // %
        private BigDecimal crecimientoVentas; // %
        private BigDecimal crecimientoCompras; // %
        private String tendencia; // CRECIMIENTO, DECRECIMIENTO, ESTABLE

        public ComparacionPeriodoDTO() {
        }

        public ComparacionPeriodoDTO(Integer operacionesAnterior, Integer operacionesActual,
                                      BigDecimal crecimientoOperaciones, BigDecimal crecimientoVentas,
                                      BigDecimal crecimientoCompras, String tendencia) {
            this.operacionesAnterior = operacionesAnterior;
            this.operacionesActual = operacionesActual;
            this.crecimientoOperaciones = crecimientoOperaciones;
            this.crecimientoVentas = crecimientoVentas;
            this.crecimientoCompras = crecimientoCompras;
            this.tendencia = tendencia;
        }

        public Integer getOperacionesAnterior() {
            return operacionesAnterior;
        }

        public void setOperacionesAnterior(Integer operacionesAnterior) {
            this.operacionesAnterior = operacionesAnterior;
        }

        public Integer getOperacionesActual() {
            return operacionesActual;
        }

        public void setOperacionesActual(Integer operacionesActual) {
            this.operacionesActual = operacionesActual;
        }

        public BigDecimal getCrecimientoOperaciones() {
            return crecimientoOperaciones;
        }

        public void setCrecimientoOperaciones(BigDecimal crecimientoOperaciones) {
            this.crecimientoOperaciones = crecimientoOperaciones;
        }

        public BigDecimal getCrecimientoVentas() {
            return crecimientoVentas;
        }

        public void setCrecimientoVentas(BigDecimal crecimientoVentas) {
            this.crecimientoVentas = crecimientoVentas;
        }

        public BigDecimal getCrecimientoCompras() {
            return crecimientoCompras;
        }

        public void setCrecimientoCompras(BigDecimal crecimientoCompras) {
            this.crecimientoCompras = crecimientoCompras;
        }

        public String getTendencia() {
            return tendencia;
        }

        public void setTendencia(String tendencia) {
            this.tendencia = tendencia;
        }
    }

    /**
     * DTO con detalle de una sucursal.
     */
    public static class DetalleSucursalDTO {
        private Integer sucursalId;
        private String nombreSucursal;
        private Integer totalOperaciones;
        private Integer totalVentas;
        private Integer totalCompras;
        private Integer totalTraspasos;
        private BigDecimal ingresoVentas;
        private String clasificacionActividad;

        public DetalleSucursalDTO() {
        }

        public DetalleSucursalDTO(Integer sucursalId, String nombreSucursal, Integer totalOperaciones,
                                   Integer totalVentas, Integer totalCompras, Integer totalTraspasos,
                                   BigDecimal ingresoVentas, String clasificacionActividad) {
            this.sucursalId = sucursalId;
            this.nombreSucursal = nombreSucursal;
            this.totalOperaciones = totalOperaciones;
            this.totalVentas = totalVentas;
            this.totalCompras = totalCompras;
            this.totalTraspasos = totalTraspasos;
            this.ingresoVentas = ingresoVentas;
            this.clasificacionActividad = clasificacionActividad;
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

        public Integer getTotalOperaciones() {
            return totalOperaciones;
        }

        public void setTotalOperaciones(Integer totalOperaciones) {
            this.totalOperaciones = totalOperaciones;
        }

        public Integer getTotalVentas() {
            return totalVentas;
        }

        public void setTotalVentas(Integer totalVentas) {
            this.totalVentas = totalVentas;
        }

        public Integer getTotalCompras() {
            return totalCompras;
        }

        public void setTotalCompras(Integer totalCompras) {
            this.totalCompras = totalCompras;
        }

        public Integer getTotalTraspasos() {
            return totalTraspasos;
        }

        public void setTotalTraspasos(Integer totalTraspasos) {
            this.totalTraspasos = totalTraspasos;
        }

        public BigDecimal getIngresoVentas() {
            return ingresoVentas;
        }

        public void setIngresoVentas(BigDecimal ingresoVentas) {
            this.ingresoVentas = ingresoVentas;
        }

        public String getClasificacionActividad() {
            return clasificacionActividad;
        }

        public void setClasificacionActividad(String clasificacionActividad) {
            this.clasificacionActividad = clasificacionActividad;
        }
    }

    // ==========================================
    // GETTERS Y SETTERS PRINCIPALES
    // ==========================================

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

    public Integer getDiasPeriodo() {
        return diasPeriodo;
    }

    public void setDiasPeriodo(Integer diasPeriodo) {
        this.diasPeriodo = diasPeriodo;
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

    public ResumenTraspasosDTO getTraspasos() {
        return traspasos;
    }

    public void setTraspasos(ResumenTraspasosDTO traspasos) {
        this.traspasos = traspasos;
    }

    public ResumenComprasDTO getCompras() {
        return compras;
    }

    public void setCompras(ResumenComprasDTO compras) {
        this.compras = compras;
    }

    public ResumenVentasDTO getVentas() {
        return ventas;
    }

    public void setVentas(ResumenVentasDTO ventas) {
        this.ventas = ventas;
    }

    public IndicadoresEficienciaDTO getEficiencia() {
        return eficiencia;
    }

    public void setEficiencia(IndicadoresEficienciaDTO eficiencia) {
        this.eficiencia = eficiencia;
    }

    public ComparacionPeriodoDTO getComparacion() {
        return comparacion;
    }

    public void setComparacion(ComparacionPeriodoDTO comparacion) {
        this.comparacion = comparacion;
    }

    public List<DetalleSucursalDTO> getDetalleSucursales() {
        return detalleSucursales;
    }

    public void setDetalleSucursales(List<DetalleSucursalDTO> detalleSucursales) {
        this.detalleSucursales = detalleSucursales;
    }
}
