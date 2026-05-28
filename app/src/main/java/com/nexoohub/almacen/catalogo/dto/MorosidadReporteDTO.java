package com.nexoohub.almacen.catalogo.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO para el reporte completo de morosidad.
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
public class MorosidadReporteDTO {
    private final Integer totalClientesMorosos;
    private final BigDecimal totalDeuda;
    private final BigDecimal deudaPromedio;
    private final BigDecimal deudaMaxima;
    private final List<ClienteBloqueadoDTO> clientes;

    public MorosidadReporteDTO(
            Integer totalClientesMorosos,
            BigDecimal totalDeuda,
            BigDecimal deudaPromedio,
            BigDecimal deudaMaxima,
            List<ClienteBloqueadoDTO> clientes) {
        this.totalClientesMorosos = totalClientesMorosos;
        this.totalDeuda = totalDeuda;
        this.deudaPromedio = deudaPromedio;
        this.deudaMaxima = deudaMaxima;
        this.clientes = clientes;
    }

    // Getters
    public Integer getTotalClientesMorosos() { return totalClientesMorosos; }
    public BigDecimal getTotalDeuda() { return totalDeuda; }
    public BigDecimal getDeudaPromedio() { return deudaPromedio; }
    public BigDecimal getDeudaMaxima() { return deudaMaxima; }
    public List<ClienteBloqueadoDTO> getClientes() { return clientes; }
}
