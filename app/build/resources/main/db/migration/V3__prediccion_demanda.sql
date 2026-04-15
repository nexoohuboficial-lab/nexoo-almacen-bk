-- ==================================================================
-- FLYWAY MIGRATION V3: Predicción de Demanda
-- NexooHub Almacén - Módulo de Predicción de Demanda
-- ==================================================================
-- Descripción: Agrega tabla para almacenar predicciones de demanda
--              - Análisis histórico de ventas
--              - Proyección de demanda futura
--              - Recomendaciones de compra
-- Autor: NexooHub Development Team
-- Fecha: 2026-03-09
-- ==================================================================

-- Tabla de predicciones de demanda
CREATE TABLE prediccion_demanda (
    id SERIAL PRIMARY KEY,
    sku_producto VARCHAR(50) NOT NULL,
    sucursal_id INTEGER NOT NULL,
    periodo_anio INTEGER NOT NULL,
    periodo_mes INTEGER NOT NULL CHECK (periodo_mes >= 1 AND periodo_mes <= 12),
    demanda_historica NUMERIC(10, 2) NOT NULL CHECK (demanda_historica >= 0),
    tendencia NUMERIC(10, 4),
    demanda_predicha NUMERIC(10, 2) NOT NULL CHECK (demanda_predicha >= 0),
    stock_actual INTEGER NOT NULL CHECK (stock_actual >= 0),
    stock_seguridad INTEGER NOT NULL CHECK (stock_seguridad >= 0),
    stock_sugerido INTEGER NOT NULL CHECK (stock_sugerido >= 0),
    cantidad_comprar INTEGER NOT NULL CHECK (cantidad_comprar >= 0),
    nivel_confianza NUMERIC(5, 2) CHECK (nivel_confianza >= 0 AND nivel_confianza <= 100),
    metodo_calculo VARCHAR(50) NOT NULL,
    periodos_analizados INTEGER,
    fecha_calculo DATE NOT NULL,
    observaciones VARCHAR(500),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_creacion VARCHAR(50),
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_actualizacion VARCHAR(50),
    
    -- Constraint único: solo una predicción por producto-sucursal-periodo
    CONSTRAINT uk_prediccion_producto_periodo UNIQUE (sku_producto, sucursal_id, periodo_anio, periodo_mes)
);

-- Comentarios en columnas
COMMENT ON TABLE prediccion_demanda IS 'Almacena predicciones de demanda basadas en análisis histórico';
COMMENT ON COLUMN prediccion_demanda.sku_producto IS 'SKU del producto analizado';
COMMENT ON COLUMN prediccion_demanda.sucursal_id IS 'Sucursal para la que se calcula la predicción';
COMMENT ON COLUMN prediccion_demanda.periodo_anio IS 'Año del periodo predicho';
COMMENT ON COLUMN prediccion_demanda.periodo_mes IS 'Mes del periodo predicho (1-12)';
COMMENT ON COLUMN prediccion_demanda.demanda_historica IS 'Promedio de demanda histórica (unidades/periodo)';
COMMENT ON COLUMN prediccion_demanda.tendencia IS 'Tendencia calculada (positiva=crecimiento, negativa=decrecimiento)';
COMMENT ON COLUMN prediccion_demanda.demanda_predicha IS 'Demanda predicha para el periodo (unidades)';
COMMENT ON COLUMN prediccion_demanda.stock_actual IS 'Stock disponible al momento del cálculo';
COMMENT ON COLUMN prediccion_demanda.stock_seguridad IS 'Stock de seguridad recomendado (unidades)';
COMMENT ON COLUMN prediccion_demanda.stock_sugerido IS 'Stock total sugerido (demanda + seguridad)';
COMMENT ON COLUMN prediccion_demanda.cantidad_comprar IS 'Cantidad recomendada para comprar';
COMMENT ON COLUMN prediccion_demanda.nivel_confianza IS 'Nivel de confianza de la predicción (0-100%)';
COMMENT ON COLUMN prediccion_demanda.metodo_calculo IS 'Método usado: PROMEDIO_MOVIL, TENDENCIA_LINEAL, ESTACIONAL';
COMMENT ON COLUMN prediccion_demanda.periodos_analizados IS 'Número de periodos históricos analizados';
COMMENT ON COLUMN prediccion_demanda.fecha_calculo IS 'Fecha en que se realizó el cálculo';
COMMENT ON COLUMN prediccion_demanda.observaciones IS 'Observaciones adicionales del análisis';

-- Índices para búsqueda eficiente
CREATE INDEX idx_pred_sku ON prediccion_demanda(sku_producto);
CREATE INDEX idx_pred_sucursal ON prediccion_demanda(sucursal_id);
CREATE INDEX idx_pred_periodo ON prediccion_demanda(periodo_anio, periodo_mes);
CREATE INDEX idx_pred_fecha_calculo ON prediccion_demanda(fecha_calculo);
CREATE INDEX idx_pred_cantidad_comprar ON prediccion_demanda(cantidad_comprar) WHERE cantidad_comprar > 0;

-- Comentarios en índices
COMMENT ON INDEX idx_pred_sku IS 'Búsqueda por producto';
COMMENT ON INDEX idx_pred_sucursal IS 'Búsqueda por sucursal';
COMMENT ON INDEX idx_pred_periodo IS 'Búsqueda por periodo';
COMMENT ON INDEX idx_pred_fecha_calculo IS 'Filtrado por fecha de cálculo';
COMMENT ON INDEX idx_pred_cantidad_comprar IS 'Búsqueda de productos que requieren compra';
