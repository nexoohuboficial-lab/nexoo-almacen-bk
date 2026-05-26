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
    id BIGSERIAL PRIMARY KEY,
    proveedor_id INTEGER NOT NULL REFERENCES proveedor(id),
    producto_sku VARCHAR(50) NOT NULL REFERENCES producto_maestro(sku_interno),
    proveedor_codigo_producto VARCHAR(100),
    precio_compra_actual NUMERIC(12,2) NOT NULL,
    moneda VARCHAR(3) NOT NULL,
    disponibilidad BOOLEAN NOT NULL DEFAULT TRUE,
    tiempo_entrega_dias INTEGER,
    ultima_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    precio_venta_sugerido_proveedor NUMERIC(12,2),
    ultima_compra_costo NUMERIC(12,2),
    ultima_compra_fecha TIMESTAMP,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_creacion VARCHAR(50),
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_actualizacion VARCHAR(50),
    UNIQUE (proveedor_id, producto_sku)
);

-- 2. ÍNDICES DE OPTIMIZACIÓN
CREATE INDEX idx_catalogo_proveedor_producto_proveedor ON catalogo_proveedor_producto(proveedor_id);
CREATE INDEX idx_catalogo_proveedor_producto_sku ON catalogo_proveedor_producto(producto_sku);
CREATE INDEX idx_catalogo_proveedor_producto_disponible ON catalogo_proveedor_producto(disponibilidad);

-- ==================================================================
-- FIN DE MIGRACIÓN V27
-- ==================================================================
