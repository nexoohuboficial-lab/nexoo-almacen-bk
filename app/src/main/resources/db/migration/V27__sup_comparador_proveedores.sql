-- ==================================================================
-- FLYWAY MIGRATION V27: SUP-01 Comparador de Precios de Proveedores
-- NexooHub Almacén
-- ==================================================================
-- Descripción: Catálogo de productos por proveedor con cálculo de precios
--              técnicos y comparación de costos.
-- Autor: IA
-- Fecha: 2026-03-26
-- ==================================================================

-- 1. TABLAS NUEVAS
CREATE TABLE catalogo_proveedor_producto (
    id SERIAL PRIMARY KEY,
    proveedor_id INTEGER REFERENCES proveedor(id),
    sku_interno VARCHAR(50) REFERENCES producto_maestro(sku_interno),
    sku_proveedor VARCHAR(100),
    precio_costo NUMERIC(10,2) NOT NULL,
    precio_venta_sugerido_proveedor NUMERIC(10,2),
    precios_incluyen_iva BOOLEAN DEFAULT FALSE,
    disponible BOOLEAN DEFAULT TRUE,
    fecha_actualizacion_precio TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    costo_sin_iva NUMERIC(10,2),
    precio_tecnico_calculado NUMERIC(10,2),
    precio_redondeado NUMERIC(10,2),
    margen_estimado NUMERIC(5,4),
    ultima_compra_costo NUMERIC(10,2),
    ultima_compra_fecha TIMESTAMP,
    notas TEXT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_creacion VARCHAR(50),
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_actualizacion VARCHAR(50),
    UNIQUE (proveedor_id, sku_interno)
);

-- 2. ÍNDICES DE OPTIMIZACIÓN
CREATE INDEX idx_catalogo_proveedor_producto_proveedor ON catalogo_proveedor_producto(proveedor_id);
CREATE INDEX idx_catalogo_proveedor_producto_sku ON catalogo_proveedor_producto(sku_interno);
CREATE INDEX idx_catalogo_proveedor_producto_disponible ON catalogo_proveedor_producto(disponible);

-- ==================================================================
-- FIN DE MIGRACIÓN V27
-- ==================================================================
