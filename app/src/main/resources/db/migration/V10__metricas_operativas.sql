-- ================================================
-- Migración V10: Métricas Operacionales
-- ================================================
-- Descripción: Tabla para almacenar snapshots de métricas operacionales que miden
--              la eficiencia de procesos: traspasos, compras, ventas y ratios de productividad.
-- 
-- Propósito:   Responder preguntas ejecutivas como:
--              - ¿Cuánto movimiento hay entre sucursales?
--              - ¿Qué tan eficientes son nuestras operaciones?
--              - ¿Cuál es la velocidad de rotación del inventario?
--              - ¿Estamos balanceados en entradas y salidas?
-- ================================================

-- Crear tabla metrica_operativa
CREATE TABLE metrica_operativa (
    -- Identificador único
    id BIGSERIAL PRIMARY KEY,
    
    -- ==========================================
    -- PERÍODO DE LA MÉTRICA
    -- ==========================================
    periodo_inicio DATE NOT NULL,
    periodo_fin DATE NOT NULL,
    tipo_periodo VARCHAR(20) NOT NULL CHECK (tipo_periodo IN ('DIARIO', 'SEMANAL', 'MENSUAL', 'TRIMESTRAL', 'ANUAL', 'PERSONALIZADO')),
    sucursal_id INTEGER REFERENCES sucursal(id),
    nombre_sucursal VARCHAR(100),
    dias_periodo INTEGER NOT NULL CHECK (dias_periodo >= 1),
    
    -- ==========================================
    -- MÉTRICAS DE TRASPASOS
    -- ==========================================
    total_traspasos INTEGER DEFAULT 0,
    unidades_traspaso_entrada INTEGER DEFAULT 0,
    unidades_traspaso_salida INTEGER DEFAULT 0,
    unidades_traspaso_neto INTEGER DEFAULT 0,
    
    -- ==========================================
    -- MÉTRICAS DE COMPRAS
    -- ==========================================
    total_compras INTEGER DEFAULT 0,
    unidades_compradas INTEGER DEFAULT 0,
    gasto_total_compras DECIMAL(15,2) DEFAULT 0,
    compra_promedio DECIMAL(15,2) DEFAULT 0,
    frecuencia_compras DECIMAL(10,2) DEFAULT 0,
    
    -- ==========================================
    -- MÉTRICAS DE VENTAS
    -- ==========================================
    total_ventas INTEGER DEFAULT 0,
    unidades_vendidas INTEGER DEFAULT 0,
    ingreso_total_ventas DECIMAL(15,2) DEFAULT 0,
    venta_promedio DECIMAL(15,2) DEFAULT 0,
    frecuencia_ventas DECIMAL(10,2) DEFAULT 0,
    
    -- ==========================================
    -- INDICADORES DE EFICIENCIA Y PRODUCTIVIDAD
    -- ==========================================
    ratio_entrada_salida DECIMAL(10,4) DEFAULT 0,
    productividad_diaria_ventas DECIMAL(15,2) DEFAULT 0,
    tasa_rotacion_inventario DECIMAL(10,4) DEFAULT 0,
    total_operaciones INTEGER DEFAULT 0,
    operaciones_promedio_dia DECIMAL(10,2) DEFAULT 0,
    clasificacion_actividad VARCHAR(20) CHECK (clasificacion_actividad IN ('ALTO', 'MEDIO', 'BAJO')),
    balance_operacional VARCHAR(20) CHECK (balance_operacional IN ('POSITIVO', 'NEGATIVO', 'EQUILIBRADO')),
    
    -- ==========================================
    -- AUDITORÍA
    -- ==========================================
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_creacion VARCHAR(100),
    usuario_actualizacion VARCHAR(100)
);

-- ==========================================
-- ÍNDICES PARA OPTIMIZACIÓN DE CONSULTAS
-- ==========================================

-- Índice para búsquedas por período (más recientes primero)
CREATE INDEX idx_metrica_operativa_periodo ON metrica_operativa(periodo_inicio DESC, periodo_fin DESC);

-- Índice para búsquedas por sucursal
CREATE INDEX idx_metrica_operativa_sucursal ON metrica_operativa(sucursal_id);

-- Índice para filtros por tipo de período
CREATE INDEX idx_metrica_operativa_tipo ON metrica_operativa(tipo_periodo);

-- Índice para ordenar por volumen de ventas
CREATE INDEX idx_metrica_operativa_ventas ON metrica_operativa(total_ventas DESC);

-- ==========================================
-- CONSTRAINTS ADICIONALES
-- ==========================================

-- Validar que fecha de fin sea posterior o igual a fecha de inicio
ALTER TABLE metrica_operativa 
    ADD CONSTRAINT chk_metrica_operativa_periodo_valido 
    CHECK (periodo_fin >= periodo_inicio);

-- Validar que valores de conteo no sean negativos
ALTER TABLE metrica_operativa 
    ADD CONSTRAINT chk_metrica_operativa_traspasos_positivos 
    CHECK (total_traspasos >= 0 AND unidades_traspaso_entrada >= 0 AND unidades_traspaso_salida >= 0);

ALTER TABLE metrica_operativa 
    ADD CONSTRAINT chk_metrica_operativa_compras_positivas 
    CHECK (total_compras >= 0 AND unidades_compradas >= 0 AND gasto_total_compras >= 0);

ALTER TABLE metrica_operativa 
    ADD CONSTRAINT chk_metrica_operativa_ventas_positivas 
    CHECK (total_ventas >= 0 AND unidades_vendidas >= 0 AND ingreso_total_ventas >= 0);

ALTER TABLE metrica_operativa 
    ADD CONSTRAINT chk_metrica_operativa_operaciones_positivas 
    CHECK (total_operaciones >= 0);

-- ==========================================
-- TRIGGER PARA ACTUALIZAR FECHA_ACTUALIZACION
-- ==========================================

CREATE OR REPLACE FUNCTION actualizar_fecha_actualizacion_metrica_operativa()
RETURNS TRIGGER AS $$
BEGIN
    NEW.fecha_actualizacion = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_metrica_operativa_fecha_actualizacion
    BEFORE UPDATE ON metrica_operativa
    FOR EACH ROW
    EXECUTE FUNCTION actualizar_fecha_actualizacion_metrica_operativa();

-- ==========================================
-- COMENTARIOS DESCRIPTIVOS
-- ==========================================

COMMENT ON TABLE metrica_operativa IS 'Snapshots de métricas operacionales para medir eficiencia de procesos';

COMMENT ON COLUMN metrica_operativa.periodo_inicio IS 'Fecha de inicio del período analizado';
COMMENT ON COLUMN metrica_operativa.periodo_fin IS 'Fecha de fin del período analízado (inclusiva)';
COMMENT ON COLUMN metrica_operativa.tipo_periodo IS 'Tipo de período: DIARIO, SEMANAL, MENSUAL, TRIMESTRAL, ANUAL, PERSONALIZADO';
COMMENT ON COLUMN metrica_operativa.sucursal_id IS 'ID de sucursal (NULL = métrica consolidada de todas las sucursales)';
COMMENT ON COLUMN metrica_operativa.dias_periodo IS 'Número de días del período calculado';

COMMENT ON COLUMN metrica_operativa.total_traspasos IS 'Número total de traspasos realizados (rastreoIds únicos)';
COMMENT ON COLUMN metrica_operativa.unidades_traspaso_entrada IS 'Unidades recibidas por traspasos (ENTRADA_TRASPASO)';
COMMENT ON COLUMN metrica_operativa.unidades_traspaso_salida IS 'Unidades enviadas por traspasos (SALIDA_TRASPASO)';
COMMENT ON COLUMN metrica_operativa.unidades_traspaso_neto IS 'Balance neto de traspasos (entrada - salida)';

COMMENT ON COLUMN metrica_operativa.total_compras IS 'Número total de compras registradas';
COMMENT ON COLUMN metrica_operativa.unidades_compradas IS 'Total de unidades compradas (suma de detalles)';
COMMENT ON COLUMN metrica_operativa.gasto_total_compras IS 'Gasto total en compras del período';
COMMENT ON COLUMN metrica_operativa.compra_promedio IS 'Compra promedio (gasto total / número de compras)';
COMMENT ON COLUMN metrica_operativa.frecuencia_compras IS 'Frecuencia de compras (compras por día)';

COMMENT ON COLUMN metrica_operativa.total_ventas IS 'Número total de ventas realizadas';
COMMENT ON COLUMN metrica_operativa.unidades_vendidas IS 'Total de unidades vendidas (suma de detalles)';
COMMENT ON COLUMN metrica_operativa.ingreso_total_ventas IS 'Ingreso total por ventas del período';
COMMENT ON COLUMN metrica_operativa.venta_promedio IS 'Venta promedio (ingreso total / número de ventas)';
COMMENT ON COLUMN metrica_operativa.frecuencia_ventas IS 'Frecuencia de ventas (ventas por día)';

COMMENT ON COLUMN metrica_operativa.ratio_entrada_salida IS 'Ratio de entrada vs salida: (compras+traspasoEntrada)/(ventas+traspasoSalida). >1 = acumulando inventario, <1 = reduciendo inventario';
COMMENT ON COLUMN metrica_operativa.productividad_diaria_ventas IS 'Productividad de ventas (ingreso por día)';
COMMENT ON COLUMN metrica_operativa.tasa_rotacion_inventario IS 'Tasa de rotación: unidades vendidas / total de unidades movidas';
COMMENT ON COLUMN metrica_operativa.total_operaciones IS 'Total de operaciones (traspasos + compras + ventas)';
COMMENT ON COLUMN metrica_operativa.operaciones_promedio_dia IS 'Promedio de operaciones por día';
COMMENT ON COLUMN metrica_operativa.clasificacion_actividad IS 'Clasificación del período: ALTO (>75 op/día), MEDIO (25-75), BAJO (<25)';
COMMENT ON COLUMN metrica_operativa.balance_operacional IS 'Balance: POSITIVO (más entradas), NEGATIVO (más salidas), EQUILIBRADO';

-- ==========================================
-- DATOS DE EJEMPLO (OPCIONAL, COMENTADO)
-- ==========================================

-- Ejemplo de métrica consolidada mensual:
-- INSERT INTO metrica_operativa (
--     periodo_inicio, periodo_fin, tipo_periodo, dias_periodo,
--     total_traspasos, unidades_traspaso_entrada, unidades_traspaso_salida, unidades_traspaso_neto,
--     total_compras, unidades_compradas, gasto_total_compras, compra_promedio, frecuencia_compras,
--     total_ventas, unidades_vendidas, ingreso_total_ventas, venta_promedio, frecuencia_ventas,
--     ratio_entrada_salida, productividad_diaria_ventas, tasa_rotacion_inventario,
--     total_operaciones, operaciones_promedio_dia, clasificacion_actividad, balance_operacional
-- ) VALUES (
--     '2024-01-01', '2024-01-31', 'MENSUAL', 31,
--     45, 1200, 1000, 200,
--     12, 5000, 125000.00, 10416.67, 0.39,
--     380, 4500, 450000.00, 1184.21, 12.26,
--     1.15, 14516.13, 0.72,
--     437, 14.10, 'MEDIO', 'POSITIVO'
-- );
