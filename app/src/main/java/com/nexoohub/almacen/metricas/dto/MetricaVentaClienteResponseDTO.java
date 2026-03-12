package com.nexoohub.almacen.metricas.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO de respuesta con métricas consolidadas de ventas y clientes.
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
public class MetricaVentaClienteResponseDTO {

    // ==================== INFORMACIÓN DEL PERÍODO ====================

    private LocalDate periodoInicio;
    private LocalDate periodoFin;
    private String tipoPeriodo;
    private Integer sucursalId;
    private String nombreSucursal;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String fechaGeneracion;

    // ==================== MÉTRICAS DE VENTAS ====================

    /**
     * Resumen general de ventas.
     */
    private ResumenVentasDTO resumenVentas;

    /**
     * Resumen de clientes.
     */
    private ResumenClientesDTO resumenClientes;

    /**
     * Resumen de vendedores.
     */
    private ResumenVendedoresDTO resumenVendedores;

    /**
     * Detalle de métodos de pago.
     */
    private MetodosPagoDTO metodosPago;

    /**
     * Comparación con período anterior (si aplica).
     */
    private ComparacionPeriodoDTO comparacion;

    /**
     * Detalle de top vendedores.
     */
    private List<DetalleVendedorDTO> topVendedores;

    /**
     * Detalle de top clientes.
     */
    private List<DetalleClienteDTO> topClientes;

    // ==================== INNER DTOs ====================

    /**
     * Resumen general de ventas.
     */
    public static class ResumenVentasDTO {
        private BigDecimal totalVentas;
        private Integer numeroTransacciones;
        private BigDecimal ticketPromedio;
        private BigDecimal ventaPromedioDia;
        private Integer diasPeriodo;

        public ResumenVentasDTO() {}

        public ResumenVentasDTO(BigDecimal totalVentas, Integer numeroTransacciones, 
                               BigDecimal ticketPromedio, BigDecimal ventaPromedioDia, 
                               Integer diasPeriodo) {
            this.totalVentas = totalVentas;
            this.numeroTransacciones = numeroTransacciones;
            this.ticketPromedio = ticketPromedio;
            this.ventaPromedioDia = ventaPromedioDia;
            this.diasPeriodo = diasPeriodo;
        }

        public BigDecimal getTotalVentas() { return totalVentas; }
        public void setTotalVentas(BigDecimal totalVentas) { this.totalVentas = totalVentas; }

        public Integer getNumeroTransacciones() { return numeroTransacciones; }
        public void setNumeroTransacciones(Integer numeroTransacciones) { 
            this.numeroTransacciones = numeroTransacciones; 
        }

        public BigDecimal getTicketPromedio() { return ticketPromedio; }
        public void setTicketPromedio(BigDecimal ticketPromedio) { this.ticketPromedio = ticketPromedio; }

        public BigDecimal getVentaPromedioDia() { return ventaPromedioDia; }
        public void setVentaPromedioDia(BigDecimal ventaPromedioDia) { 
            this.ventaPromedioDia = ventaPromedioDia; 
        }

        public Integer getDiasPeriodo() { return diasPeriodo; }
        public void setDiasPeriodo(Integer diasPeriodo) { this.diasPeriodo = diasPeriodo; }
    }

    /**
     * Resumen de clientes.
     */
    public static class ResumenClientesDTO {
        private Integer totalClientesActivos;
        private Integer clientesNuevos;
        private Integer clientesRecurrentes;
        private Integer clientesInactivos;
        private BigDecimal tasaRetencion;
        private BigDecimal valorVidaCliente;
        private BigDecimal frecuenciaCompra;

        public ResumenClientesDTO() {}

        public Integer getTotalClientesActivos() { return totalClientesActivos; }
        public void setTotalClientesActivos(Integer totalClientesActivos) { 
            this.totalClientesActivos = totalClientesActivos; 
        }

        public Integer getClientesNuevos() { return clientesNuevos; }
        public void setClientesNuevos(Integer clientesNuevos) { this.clientesNuevos = clientesNuevos; }

        public Integer getClientesRecurrentes() { return clientesRecurrentes; }
        public void setClientesRecurrentes(Integer clientesRecurrentes) { 
            this.clientesRecurrentes = clientesRecurrentes; 
        }

        public Integer getClientesInactivos() { return clientesInactivos; }
        public void setClientesInactivos(Integer clientesInactivos) { 
            this.clientesInactivos = clientesInactivos; 
        }

        public BigDecimal getTasaRetencion() { return tasaRetencion; }
        public void setTasaRetencion(BigDecimal tasaRetencion) { this.tasaRetencion = tasaRetencion; }

        public BigDecimal getValorVidaCliente() { return valorVidaCliente; }
        public void setValorVidaCliente(BigDecimal valorVidaCliente) { 
            this.valorVidaCliente = valorVidaCliente; 
        }

        public BigDecimal getFrecuenciaCompra() { return frecuenciaCompra; }
        public void setFrecuenciaCompra(BigDecimal frecuenciaCompra) { 
            this.frecuenciaCompra = frecuenciaCompra; 
        }
    }

    /**
     * Resumen de vendedores.
     */
    public static class ResumenVendedoresDTO {
        private Integer totalVendedores;
        private Integer topVendedorId;
        private String topVendedorNombre;
        private BigDecimal topVendedorVentas;
        private Integer topVendedorTransacciones;
        private BigDecimal ventaPromedioVendedor;

        public ResumenVendedoresDTO() {}

        public Integer getTotalVendedores() { return totalVendedores; }
        public void setTotalVendedores(Integer totalVendedores) { 
            this.totalVendedores = totalVendedores; 
        }

        public Integer getTopVendedorId() { return topVendedorId; }
        public void setTopVendedorId(Integer topVendedorId) { this.topVendedorId = topVendedorId; }

        public String getTopVendedorNombre() { return topVendedorNombre; }
        public void setTopVendedorNombre(String topVendedorNombre) { 
            this.topVendedorNombre = topVendedorNombre; 
        }

        public BigDecimal getTopVendedorVentas() { return topVendedorVentas; }
        public void setTopVendedorVentas(BigDecimal topVendedorVentas) { 
            this.topVendedorVentas = topVendedorVentas; 
        }

        public Integer getTopVendedorTransacciones() { return topVendedorTransacciones; }
        public void setTopVendedorTransacciones(Integer topVendedorTransacciones) { 
            this.topVendedorTransacciones = topVendedorTransacciones; 
        }

        public BigDecimal getVentaPromedioVendedor() { return ventaPromedioVendedor; }
        public void setVentaPromedioVendedor(BigDecimal ventaPromedioVendedor) { 
            this.ventaPromedioVendedor = ventaPromedioVendedor; 
        }
    }

    /**
     * Detalle de métodos de pago.
     */
    public static class MetodosPagoDTO {
        private BigDecimal ventasEfectivo;
        private BigDecimal ventasTarjeta;
        private BigDecimal ventasCredito;
        private BigDecimal porcentajeEfectivo;
        private BigDecimal porcentajeTarjeta;
        private BigDecimal porcentajeCredito;

        public MetodosPagoDTO() {}

        public BigDecimal getVentasEfectivo() { return ventasEfectivo; }
        public void setVentasEfectivo(BigDecimal ventasEfectivo) { 
            this.ventasEfectivo = ventasEfectivo; 
        }

        public BigDecimal getVentasTarjeta() { return ventasTarjeta; }
        public void setVentasTarjeta(BigDecimal ventasTarjeta) { 
            this.ventasTarjeta = ventasTarjeta; 
        }

        public BigDecimal getVentasCredito() { return ventasCredito; }
        public void setVentasCredito(BigDecimal ventasCredito) { 
            this.ventasCredito = ventasCredito; 
        }

        public BigDecimal getPorcentajeEfectivo() { return porcentajeEfectivo; }
        public void setPorcentajeEfectivo(BigDecimal porcentajeEfectivo) { 
            this.porcentajeEfectivo = porcentajeEfectivo; 
        }

        public BigDecimal getPorcentajeTarjeta() { return porcentajeTarjeta; }
        public void setPorcentajeTarjeta(BigDecimal porcentajeTarjeta) { 
            this.porcentajeTarjeta = porcentajeTarjeta; 
        }

        public BigDecimal getPorcentajeCredito() { return porcentajeCredito; }
        public void setPorcentajeCredito(BigDecimal porcentajeCredito) { 
            this.porcentajeCredito = porcentajeCredito; 
        }
    }

    /**
     * Comparación con período anterior.
     */
    public static class ComparacionPeriodoDTO {
        private BigDecimal ventasAnterior;
        private BigDecimal ventasActual;
        private BigDecimal crecimiento;
        private String tendencia; // "CRECIMIENTO", "DECRECIMIENTO", "ESTABLE"
        private Integer transaccionesAnterior;
        private Integer transaccionesActual;

        public ComparacionPeriodoDTO() {}

        public BigDecimal getVentasAnterior() { return ventasAnterior; }
        public void setVentasAnterior(BigDecimal ventasAnterior) { 
            this.ventasAnterior = ventasAnterior; 
        }

        public BigDecimal getVentasActual() { return ventasActual; }
        public void setVentasActual(BigDecimal ventasActual) { this.ventasActual = ventasActual; }

        public BigDecimal getCrecimiento() { return crecimiento; }
        public void setCrecimiento(BigDecimal crecimiento) { this.crecimiento = crecimiento; }

        public String getTendencia() { return tendencia; }
        public void setTendencia(String tendencia) { this.tendencia = tendencia; }

        public Integer getTransaccionesAnterior() { return transaccionesAnterior; }
        public void setTransaccionesAnterior(Integer transaccionesAnterior) { 
            this.transaccionesAnterior = transaccionesAnterior; 
        }

        public Integer getTransaccionesActual() { return transaccionesActual; }
        public void setTransaccionesActual(Integer transaccionesActual) { 
            this.transaccionesActual = transaccionesActual; 
        }
    }

    // ==================== GETTERS Y SETTERS ====================

    public LocalDate getPeriodoInicio() { return periodoInicio; }
    public void setPeriodoInicio(LocalDate periodoInicio) { this.periodoInicio = periodoInicio; }

    public LocalDate getPeriodoFin() { return periodoFin; }
    public void setPeriodoFin(LocalDate periodoFin) { this.periodoFin = periodoFin; }

    public String getTipoPeriodo() { return tipoPeriodo; }
    public void setTipoPeriodo(String tipoPeriodo) { this.tipoPeriodo = tipoPeriodo; }

    public Integer getSucursalId() { return sucursalId; }
    public void setSucursalId(Integer sucursalId) { this.sucursalId = sucursalId; }

    public String getNombreSucursal() { return nombreSucursal; }
    public void setNombreSucursal(String nombreSucursal) { this.nombreSucursal = nombreSucursal; }

    public String getFechaGeneracion() { return fechaGeneracion; }
    public void setFechaGeneracion(String fechaGeneracion) { this.fechaGeneracion = fechaGeneracion; }

    public ResumenVentasDTO getResumenVentas() { return resumenVentas; }
    public void setResumenVentas(ResumenVentasDTO resumenVentas) { this.resumenVentas = resumenVentas; }

    public ResumenClientesDTO getResumenClientes() { return resumenClientes; }
    public void setResumenClientes(ResumenClientesDTO resumenClientes) { 
        this.resumenClientes = resumenClientes; 
    }

    public ResumenVendedoresDTO getResumenVendedores() { return resumenVendedores; }
    public void setResumenVendedores(ResumenVendedoresDTO resumenVendedores) { 
        this.resumenVendedores = resumenVendedores; 
    }

    public MetodosPagoDTO getMetodosPago() { return metodosPago; }
    public void setMetodosPago(MetodosPagoDTO metodosPago) { this.metodosPago = metodosPago; }

    public ComparacionPeriodoDTO getComparacion() { return comparacion; }
    public void setComparacion(ComparacionPeriodoDTO comparacion) { this.comparacion = comparacion; }

    public List<DetalleVendedorDTO> getTopVendedores() { return topVendedores; }
    public void setTopVendedores(List<DetalleVendedorDTO> topVendedores) { 
        this.topVendedores = topVendedores; 
    }

    public List<DetalleClienteDTO> getTopClientes() { return topClientes; }
    public void setTopClientes(List<DetalleClienteDTO> topClientes) { 
        this.topClientes = topClientes; 
    }
}
