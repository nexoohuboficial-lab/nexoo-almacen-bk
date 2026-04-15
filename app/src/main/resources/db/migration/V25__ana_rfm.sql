-- ==================================================================
-- FLYWAY MIGRATION V25: Analytics RFM - Segmentación de Clientes
-- NexooHub Almacén
-- ==================================================================
-- Descripción: Tabla para almacenar la métrica y calificación RFM de los clientes
-- Autor: IA
-- Fecha: 2026-03-25
-- ==================================================================

-- 1. TABLAS NUEVAS
CREATE TABLE segmento_rfm_cliente (
    id SERIAL PRIMARY KEY,
    cliente_id INTEGER NOT NULL REFERENCES cliente(id),
    recencia_dias INTEGER NOT NULL,
    frecuencia_compras INTEGER NOT NULL,
    monto_gastado DECIMAL(14,2) NOT NULL,
    score_r INTEGER NOT NULL,
    score_f INTEGER NOT NULL,
    score_m INTEGER NOT NULL,
    segmento VARCHAR(50) NOT NULL,
    fecha_calculo TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_creacion VARCHAR(50),
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_actualizacion VARCHAR(50),
    UNIQUE(cliente_id)
);

-- 2. ÍNDICES DE OPTIMIZACIÓN
CREATE INDEX idx_segmento_rfm_cliente_id ON segmento_rfm_cliente(cliente_id);
CREATE INDEX idx_segmento_rfm_segmento ON segmento_rfm_cliente(segmento);

-- ==================================================================
-- FIN DE MIGRACIÓN V25
-- ==================================================================
