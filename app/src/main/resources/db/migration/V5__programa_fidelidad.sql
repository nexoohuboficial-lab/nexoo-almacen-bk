-- ==================================================================
-- FLYWAY MIGRATION V5: Programa de Fidelidad
-- NexooHub Almacén - Módulo de Lealtad de Clientes
-- ==================================================================
-- Descripción: Creación de tablas para el programa de fidelidad
--              que permite acumular y canjear puntos por compras.
-- Autor: NexooHub Development Team
-- Fecha: 2026-03-11
-- ==================================================================

-- ====================================
-- TABLA: programa_fidelidad
-- ====================================
-- Almacena el saldo de puntos acumulados por cliente.
-- Reglas de negocio:
--   - 1 punto por cada $10 MXN de compra
--   - 100 puntos = $10 MXN de descuento
--   - Los puntos no caducan
-- ====================================
CREATE TABLE programa_fidelidad (
    id SERIAL PRIMARY KEY,
    cliente_id INTEGER NOT NULL UNIQUE,
    puntos_acumulados INTEGER NOT NULL DEFAULT 0,
    total_compras NUMERIC(10, 2) NOT NULL DEFAULT 0.00,
    total_canjeado NUMERIC(10, 2) NOT NULL DEFAULT 0.00,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    
    -- Auditoría
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_creacion VARCHAR(50),
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_actualizacion VARCHAR(50),
    
    -- Foreign key
    CONSTRAINT fk_programa_cliente FOREIGN KEY (cliente_id) 
        REFERENCES cliente(id) ON DELETE CASCADE,
    
    -- Constraints
    CONSTRAINT ck_puntos_no_negativos CHECK (puntos_acumulados >= 0),
    CONSTRAINT ck_total_compras_positivo CHECK (total_compras >= 0),
    CONSTRAINT ck_total_canjeado_positivo CHECK (total_canjeado >= 0)
);

-- Índices para mejorar rendimiento
CREATE INDEX idx_programa_cliente ON programa_fidelidad(cliente_id);
CREATE INDEX idx_programa_activo ON programa_fidelidad(activo);

-- ====================================
-- TABLA: movimiento_punto
-- ====================================
-- Registra cada acumulación o canje de puntos.
-- Permite auditoría completa de movimientos.
-- ====================================
CREATE TABLE movimiento_punto (
    id SERIAL PRIMARY KEY,
    programa_id INTEGER NOT NULL,
    tipo_movimiento VARCHAR(20) NOT NULL, -- ACUMULACION, CANJE
    puntos INTEGER NOT NULL,
    monto_asociado NUMERIC(10, 2),
    venta_id INTEGER,
    descripcion VARCHAR(500) NOT NULL,
    
    -- Auditoría
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_creacion VARCHAR(50),
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_actualizacion VARCHAR(50),
    
    -- Foreign keys
    CONSTRAINT fk_movimiento_programa FOREIGN KEY (programa_id) 
        REFERENCES programa_fidelidad(id) ON DELETE CASCADE,
    CONSTRAINT fk_movimiento_venta FOREIGN KEY (venta_id) 
        REFERENCES venta(id) ON DELETE SET NULL,
    
    -- Constraints
    CONSTRAINT ck_tipo_movimiento_valido 
        CHECK (tipo_movimiento IN ('ACUMULACION', 'CANJE'))
);

-- Índices para consultas frecuentes
CREATE INDEX idx_movimiento_programa ON movimiento_punto(programa_id);
CREATE INDEX idx_movimiento_tipo ON movimiento_punto(tipo_movimiento);
CREATE INDEX idx_movimiento_venta ON movimiento_punto(venta_id);
CREATE INDEX idx_movimiento_fecha ON movimiento_punto(fecha_creacion);

-- ====================================
-- COMENTARIOS EN TABLAS
--====================================
COMMENT ON TABLE programa_fidelidad IS 'Almacena el saldo de puntos acumulados por cliente';
COMMENT ON COLUMN programa_fidelidad.puntos_acumulados IS 'Puntos disponibles para canjear';
COMMENT ON COLUMN programa_fidelidad.total_compras IS 'Suma total de compras que generaron puntos';
COMMENT ON COLUMN programa_fidelidad.total_canjeado IS 'Monto total canjeado en descuentos';

COMMENT ON TABLE movimiento_punto IS 'Auditoría de acumulaciones y canjes de puntos';
COMMENT ON COLUMN movimiento_punto.tipo_movimiento IS 'ACUMULACION o CANJE';
COMMENT ON COLUMN movimiento_punto.puntos IS 'Puntos del movimiento (positivo=acumulación, negativo=canje)';
COMMENT ON COLUMN movimiento_punto.monto_asociado IS 'Monto de compra (acumulación) o descuento (canje)';

-- ====================================
-- FIN DE MIGRACIÓN
-- ====================================
