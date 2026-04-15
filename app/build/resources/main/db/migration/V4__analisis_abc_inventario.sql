-- ==================================================================
-- FLYWAY MIGRATION V4: Análisis ABC de Inventario
-- NexooHub Almacén - Módulo de Análisis ABC
-- ==================================================================
-- Descripción: Agrega tabla para almacenar análisis ABC de inventario
--              - Clasificación de productos según su valor (Pareto 80/20)
--              - Seguimiento de rotación y valor de productos
--              - Recomendaciones de gestión según clasificación
-- Autor: NexooHub Development Team
-- Fecha: 2026-03-09
-- ==================================================================

-- Tabla de análisis ABC
CREATE TABLE analisis_abc (
    id SERIAL PRIMARY KEY,
    sku_producto VARCHAR(50) NOT NULL,
    sucursal_id INTEGER NOT NULL,
    clasificacion VARCHAR(1) NOT NULL CHECK (clasificacion IN ('A', 'B', 'C')),
    periodo_inicio DATE NOT NULL,
    periodo_fin DATE NOT NULL,
    cantidad_vendida INTEGER NOT NULL DEFAULT 0 CHECK (cantidad_vendida >= 0),
    valor_ventas NUMERIC(12, 2) NOT NULL DEFAULT 0.00 CHECK (valor_ventas >= 0),
    porcentaje_valor NUMERIC(5, 2) NOT NULL DEFAULT 0.00 CHECK (porcentaje_valor >= 0 AND porcentaje_valor <= 100),
    porcentaje_acumulado NUMERIC(5, 2) NOT NULL DEFAULT 0.00 CHECK (porcentaje_acumulado >= 0 AND porcentaje_acumulado <= 100),
    stock_actual INTEGER NOT NULL DEFAULT 0 CHECK (stock_actual >= 0),
    valor_stock NUMERIC(12, 2) DEFAULT 0.00 CHECK (valor_stock >= 0),
    rotacion_inventario NUMERIC(10, 4) DEFAULT 0.0000,
    fecha_analisis DATE NOT NULL,
    observaciones TEXT,
    fecha_creacion DATE NOT NULL DEFAULT CURRENT_DATE,
    usuario_creacion VARCHAR(50),
    
    -- Restricciones
    CONSTRAINT chk_periodo_valido CHECK (periodo_inicio <= periodo_fin)
);

-- Índices para mejorar rendimiento de consultas
CREATE INDEX idx_analisis_abc_sucursal ON analisis_abc(sucursal_id);
CREATE INDEX idx_analisis_abc_clasificacion ON analisis_abc(clasificacion);
CREATE INDEX idx_analisis_abc_fecha ON analisis_abc(fecha_analisis);
CREATE INDEX idx_analisis_abc_sku_sucursal ON analisis_abc(sku_producto, sucursal_id);
CREATE INDEX idx_analisis_abc_sucursal_fecha ON analisis_abc(sucursal_id, fecha_analisis);
CREATE INDEX idx_analisis_abc_valor_ventas ON analisis_abc(valor_ventas DESC);

-- Comentarios de documentación
COMMENT ON TABLE analisis_abc IS 'Almacena análisis ABC de inventario para clasificar productos según su valor (Principio de Pareto 80/20)';
COMMENT ON COLUMN analisis_abc.clasificacion IS 'Clasificación del producto: A (alto valor), B (valor medio), C (bajo valor)';
COMMENT ON COLUMN analisis_abc.porcentaje_valor IS 'Porcentaje que representa del valor total de ventas';
COMMENT ON COLUMN analisis_abc.porcentaje_acumulado IS 'Porcentaje acumulado hasta este producto (determina clasificación)';
COMMENT ON COLUMN analisis_abc.rotacion_inventario IS 'Rotación de inventario: valor_ventas / valor_stock';
COMMENT ON COLUMN analisis_abc.observaciones IS 'Observaciones y recomendaciones automáticas según clasificación';
