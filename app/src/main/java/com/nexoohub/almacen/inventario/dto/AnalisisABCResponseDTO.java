package com.nexoohub.almacen.inventario.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO de respuesta para análisis ABC.
 * 
 * @author NexooHub Development Team
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalisisABCResponseDTO {

    private Integer id;
    private String skuProducto;
    private String nombreProducto;
    private Integer sucursalId;
    private String nombreSucursal;
    private String clasificacion;
    private LocalDate periodoInicio;
    private LocalDate periodoFin;
    private Integer cantidadVendida;
    private BigDecimal valorVentas;
    private BigDecimal porcentajeValor;
    private BigDecimal porcentajeAcumulado;
    private Integer stockActual;
    private BigDecimal valorStock;
    private BigDecimal rotacionInventario;
    private LocalDate fechaAnalisis;
    private String observaciones;
}
