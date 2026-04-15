-- ==================================================================
-- FLYWAY MIGRATION V28: SUP-02 Actualización Masiva de Precios de Proveedores
-- NexooHub Almacén
-- ==================================================================
-- Descripción: Historial de cambios de precios del catálogo de proveedores
-- Autor: IA
-- Fecha: 2026-03-26
-- ==================================================================

-- 1. TABLAS NUEVAS
CREATE TABLE historial_precio_proveedor (
    id SERIAL PRIMARY KEY,
    catalogo_id INTEGER NOT NULL REFERENCES catalogo_proveedor_producto(id),
    precio_costo_anterior NUMERIC(12,2),
    precio_costo_nuevo NUMERIC(12,2) NOT NULL,
    variacion_porcentual NUMERIC(5,2),
    motivo VARCHAR(255),
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_actualizacion VARCHAR(50)
);

-- 2. ÍNDICES DE OPTIMIZACIÓN
CREATE INDEX idx_historial_precio_proveedor_catalogo ON historial_precio_proveedor(catalogo_id);

-- ==================================================================
-- FIN DE MIGRACIÓN V28
-- ==================================================================
