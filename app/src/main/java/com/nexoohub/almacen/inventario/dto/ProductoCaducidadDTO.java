package com.nexoohub.almacen.inventario.dto;

import java.time.LocalDate;

/**
 * DTO para productos próximos a caducar.
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
public class ProductoCaducidadDTO {
    private final String skuInterno;
    private final String nombreComercial;
    private final Integer sucursalId;
    private final String nombreSucursal;
    private final Integer stockActual;
    private final LocalDate fechaCaducidad;
    private final Integer diasParaCaducar;
    private final String lote;
    
    public ProductoCaducidadDTO(
            String skuInterno,
            String nombreComercial,
            Integer sucursalId,
            String nombreSucursal,
            Integer stockActual,
            LocalDate fechaCaducidad,
            String lote) {
        this.skuInterno = skuInterno;
        this.nombreComercial = nombreComercial;
        this.sucursalId = sucursalId;
        this.nombreSucursal = nombreSucursal;
        this.stockActual = stockActual;
        this.fechaCaducidad = fechaCaducidad;
        this.lote = lote;
        
        // Calcular días para caducar
        if (fechaCaducidad != null) {
            this.diasParaCaducar = (int) java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), fechaCaducidad);
        } else {
            this.diasParaCaducar = null;
        }
    }
    
    // Getters
    public String getSkuInterno() { return skuInterno; }
    public String getNombreComercial() { return nombreComercial; }
    public Integer getSucursalId() { return sucursalId; }
    public String getNombreSucursal() { return nombreSucursal; }
    public Integer getStockActual() { return stockActual; }
    public LocalDate getFechaCaducidad() { return fechaCaducidad; }
    public Integer getDiasParaCaducar() { return diasParaCaducar; }
    public String getLote() { return lote; }
}
