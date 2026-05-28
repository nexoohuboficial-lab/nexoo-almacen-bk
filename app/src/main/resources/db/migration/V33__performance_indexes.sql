-- Flyway Migration: V33__performance_indexes.sql
-- Description: Add index optimizations for PostgreSQL database to boost search, joins, and dashboard rendering performance.

-- 1. Búsqueda y Filtros de Productos (Catálogo y Mostrador)
CREATE INDEX IF NOT EXISTS idx_producto_busqueda ON producto_maestro(nombre_comercial, marca);
CREATE INDEX IF NOT EXISTS idx_producto_sensibilidad ON producto_maestro(sensibilidad_precio);

-- 2. Inventario y JOINs de Sucursal (Lento Movimiento, Stock Bajo, CPP)
CREATE INDEX IF NOT EXISTS idx_inventario_sucursal_sku ON inventario_sucursal(sku_interno);
CREATE INDEX IF NOT EXISTS idx_inventario_sucursal_id ON inventario_sucursal(sucursal_id);

-- 3. Clientes y Crédito (Búsqueda por RFC, Nombre y Límites de Crédito)
CREATE INDEX IF NOT EXISTS idx_cliente_rfc ON cliente(rfc);
CREATE INDEX IF NOT EXISTS idx_cliente_nombre ON cliente(nombre);
CREATE INDEX IF NOT EXISTS idx_cliente_bloqueado ON cliente(bloqueado);

-- 4. CRM y Oportunidades B2B (Pipeline y Prospectos)
CREATE INDEX IF NOT EXISTS idx_crm_oportunidad_prospecto ON oportunidad_venta(prospecto_id);
CREATE INDEX IF NOT EXISTS idx_crm_oportunidad_etapa ON oportunidad_venta(etapa);

-- 5. Compras y Auditoría de Variación de Costos
CREATE INDEX IF NOT EXISTS idx_historial_precio_sku ON historial_precio(sku_interno);
CREATE INDEX IF NOT EXISTS idx_detalle_compra_sku ON detalle_compra(sku_interno);
CREATE INDEX IF NOT EXISTS idx_detalle_orden_compra_sku ON detalle_orden_compra(sku_interno);

-- 6. Métricas y Ventas (Para agilizar reportes mensuales y tableros financieros)
CREATE INDEX IF NOT EXISTS idx_venta_fecha ON venta(fecha_venta);
CREATE INDEX IF NOT EXISTS idx_turno_caja_estado ON turno_caja(estado);
