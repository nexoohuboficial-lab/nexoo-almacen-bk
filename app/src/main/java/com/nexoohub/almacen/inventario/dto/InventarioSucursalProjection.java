package com.nexoohub.almacen.inventario.dto;

import java.math.BigDecimal;

/**
 * Proyección de interfaz para consultas nativas de inventario.
 * Spring Data automáticamente implementa esta interfaz mapeando los nombres de columnas de SQL a los métodos getter.
 */
public interface InventarioSucursalProjection {
    String getSkuInterno();
    String getNombreComercial();
    Integer getStockActual();
    BigDecimal getCostoPromedioPonderado();
}
