package com.nexoohub.almacen.comisiones.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class ProgresoMetaResponse {
    private Integer empleadoId;
    private String nombreEmpleado;
    private Integer mes;
    private Integer anio;
    
    private BigDecimal montoMeta;
    private BigDecimal montoVentasAcumuladas;
    private BigDecimal porcentajeLogro;
    
    private BigDecimal porcentajeComisionAplicable;
    private BigDecimal comisionProyectadaVentas;
    
    private String tierLogrado; // Nombre de la regla escalonada aplicable
    private LocalDateTime fechaUltimoCalculo;
}
