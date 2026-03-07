package com.nexoohub.almacen.finanzas.dto;

import java.math.BigDecimal;

/**
 * DTO para dashboard ejecutivo.
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
public class DashboardDTO {
    private final BigDecimal ventasHoy;
    private final BigDecimal ventasMes;
    private final Integer productosStockBajo;
    private final Integer devolucionesHoy;
    private final BigDecimal ticketPromedio;
    private final Integer clientesBloqueados;
    private final Integer productosProximoCaducar; // a vencer en 30 días
    
    public DashboardDTO(
            BigDecimal ventasHoy,
            BigDecimal ventasMes,
            Integer productosStockBajo,
            Integer devolucionesHoy,
            BigDecimal ticketPromedio,
            Integer clientesBloqueados,
            Integer productosProximoCaducar) {
        this.ventasHoy = ventasHoy;
        this.ventasMes = ventasMes;
        this.productosStockBajo = productosStockBajo;
        this.devolucionesHoy = devolucionesHoy;
        this.ticketPromedio = ticketPromedio;
        this.clientesBloqueados = clientesBloqueados;
        this.productosProximoCaducar = productosProximoCaducar;
    }
    
    // Getters
    public BigDecimal getVentasHoy() { return ventasHoy; }
    public BigDecimal getVentasMes() { return ventasMes; }
    public Integer getProductosStockBajo() { return productosStockBajo; }
    public Integer getDevolucionesHoy() { return devolucionesHoy; }
    public BigDecimal getTicketPromedio() { return ticketPromedio; }
    public Integer getClientesBloqueados() { return clientesBloqueados; }
    public Integer getProductosProximoCaducar() { return productosProximoCaducar; }
}
