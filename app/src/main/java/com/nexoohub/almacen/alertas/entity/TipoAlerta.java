package com.nexoohub.almacen.alertas.entity;

/**
 * Tipos de alerta que puede generar el sistema automáticamente.
 */
public enum TipoAlerta {
    STOCK_BAJO,
    CXC_VENCIDA,
    CHURN_RIESGO,
    META_EN_RIESGO,
    PRODUCTO_POR_CADUCAR,
    PRECIO_PROVEEDOR_CAMBIO
}
