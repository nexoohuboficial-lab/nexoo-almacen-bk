package com.nexoohub.almacen.finanzas.dto;

import java.math.BigDecimal;

/**
 * DTO para reportes de ventas.
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
public class ReporteVentasDTO {
    private final String periodo; // Día, semana, mes
    private final Integer totalVentas;
    private final BigDecimal montoTotal;
    private final BigDecimal ticketPromedio;
    private final String productoMasVendido;
    private final Integer cantidadProductoMasVendido;
    
    public ReporteVentasDTO(
            String periodo,
            Integer totalVentas,
            BigDecimal montoTotal,
            BigDecimal ticketPromedio,
            String productoMasVendido,
            Integer cantidadProductoMasVendido) {
        this.periodo = periodo;
        this.totalVentas = totalVentas;
        this.montoTotal = montoTotal;
        this.ticketPromedio = ticketPromedio;
        this.productoMasVendido = productoMasVendido;
        this.cantidadProductoMasVendido = cantidadProductoMasVendido;
    }
    
    // Getters
    public String getPeriodo() { return periodo; }
    public Integer getTotalVentas() { return totalVentas; }
    public BigDecimal getMontoTotal() { return montoTotal; }
    public BigDecimal getTicketPromedio() { return ticketPromedio; }
    public String getProductoMasVendido() { return productoMasVendido; }
    public Integer getCantidadProductoMasVendido() { return cantidadProductoMasVendido; }
}
