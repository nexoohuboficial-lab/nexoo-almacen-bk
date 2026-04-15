-- =====================================================
-- Migración: Métricas de Inventario
-- Versión: V8
-- Descripción: Tabla para métricas de inventario, rotación y capital inmovilizado
-- Autor: NexooHub Development Team
-- Fecha: 2025-01-11
-- =====================================================

-- =====================================================
-- TABLA: metrica_inventario
-- Propósito: Almacena snapshots de métricas de inventario consolidadas o por sucursal
-- Responde: ¿Cuánto capital tengo inmovilizado? ¿Rota bien mi inventario?
-- =====================================================
CREATE TABLE metrica_inventario (
    id BIGSERIAL PRIMARY KEY,
    fecha_corte DATE NOT NULL,
    sucursal_id INTEGER,
    nombre_sucursal VARCHAR(200),
    
    -- Métricas de Stock
    total_skus INTEGER NOT NULL DEFAULT 0,
    stock_disponible_total INTEGER NOT NULL DEFAULT 0,
    skus_bajo_stock INTEGER NOT NULL DEFAULT 0,
    skus_sin_stock INTEGER NOT NULL DEFAULT 0,
    skus_proximos_caducar INTEGER NOT NULL DEFAULT 0,
    
    -- Métricas de Valor (Capital Inmovilizado)
    valor_total_inventario NUMERIC(15,2) NOT NULL DEFAULT 0.00,
    costo_promedio_ponderado NUMERIC(15,2) NOT NULL DEFAULT 0.00,
    valor_stock_bajo NUMERIC(15,2) NOT NULL DEFAULT 0.00,
    
    -- Métricas de Rotación
    indice_rotacion NUMERIC(10,4) NOT NULL DEFAULT 0.0000,
    dias_inventario NUMERIC(10,2) NOT NULL DEFAULT 0.00,
    costo_ventas_periodo NUMERIC(15,2) NOT NULL DEFAULT 0.00,
    dias_periodo_rotacion INTEGER NOT NULL DEFAULT 30,
    
    -- Métricas de Eficiencia
    cobertura_dias NUMERIC(10,2),
    exactitud_porcentaje NUMERIC(5,2),
    tasa_quiebre_stock NUMERIC(5,2) NOT NULL DEFAULT 0.00,
    
    -- Clasificaciones
    salud_inventario VARCHAR(30) NOT NULL,
    clasificacion_rotacion VARCHAR(30) NOT NULL,
    
    -- Campos de auditoría
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    usuario_creacion VARCHAR(100),
    usuario_actualizacion VARCHAR(100),
    
    -- Foreign Key
    CONSTRAINT fk_metrica_inventario_sucursal FOREIGN KEY (sucursal_id) 
        REFERENCES sucursal(id) ON DELETE CASCADE,
    
    -- Restricción: Solo un snapshot por fecha y sucursal
    CONSTRAINT uk_metrica_inventario_fecha_sucursal UNIQUE (fecha_corte, sucursal_id)
);

-- =====================================================
-- ÍNDICES para optimización de consultas
-- =====================================================

-- Índice por fecha descendente (snapshots más recientes primero)
CREATE INDEX idx_metrica_inventario_fecha ON metrica_inventario (fecha_corte DESC);

-- Índice por sucursal para consultas filtradas
CREATE INDEX idx_metrica_inventario_sucursal ON metrica_inventario (sucursal_id);

-- Índice por valor total (top sucursales con más capital inmovilizado)
CREATE INDEX idx_metrica_inventario_valor_desc ON metrica_inventario (valor_total_inventario DESC);

-- Índice por rotación (identifica inventarios de lenta rotación)
CREATE INDEX idx_metrica_inventario_rotacion_desc ON metrica_inventario (indice_rotacion DESC);

-- =====================================================
-- COMENTARIOS en la tabla
-- =====================================================

COMMENT ON TABLE metrica_inventario IS 'Métricas consolidadas de inventario: capital inmovilizado, rotación, stock, eficiencia';
COMMENT ON COLUMN metrica_inventario.fecha_corte IS 'Fecha del snapshot de inventario';
COMMENT ON COLUMN metrica_inventario.sucursal_id IS 'ID de sucursal (NULL = consolidado de todas las sucursales)';
COMMENT ON COLUMN metrica_inventario.valor_total_inventario IS 'Capital total inmovilizado en inventario';
COMMENT ON COLUMN metrica_inventario.indice_rotacion IS 'Índice de rotación anualizado (COGS / Valor Inventario)';
COMMENT ON COLUMN metrica_inventario.dias_inventario IS 'Días promedio que dura el inventario (365 / Rotación)';
COMMENT ON COLUMN metrica_inventario.tasa_quiebre_stock IS 'Porcentaje de SKUs sin stock';
COMMENT ON COLUMN metrica_inventario.salud_inventario IS 'Clasificación: SALUDABLE, ACEPTABLE, REQUIERE_ATENCION, CRITICA';
COMMENT ON COLUMN metrica_inventario.clasificacion_rotacion IS 'Clasificación rotación: ALTA (>=12), MEDIA (>=6), BAJA (>=3), MUY_BAJA (<3)';
