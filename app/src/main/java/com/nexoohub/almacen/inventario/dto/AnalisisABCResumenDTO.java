package com.nexoohub.almacen.inventario.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO con resumen del análisis ABC por sucursal.
 * 
 * @author NexooHub Development Team
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalisisABCResumenDTO {

    private Integer sucursalId;
    private String nombreSucursal;
    private LocalDate fechaAnalisis;
    private LocalDate periodoInicio;
    private LocalDate periodoFin;
    private Integer totalProductos;
    private BigDecimal valorTotalVentas;
    
    private ResumenClasificacion clasificacionA;
    private ResumenClasificacion clasificacionB;
    private ResumenClasificacion clasificacionC;
    
    private List<AnalisisABCResponseDTO> productosClaseA;

    /**
     * Resumen por clasificación
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResumenClasificacion {
        private Integer cantidadProductos;
        private BigDecimal valorVentas;
        private BigDecimal porcentajeValor;
        private BigDecimal porcentajeProductos;
    }
}
