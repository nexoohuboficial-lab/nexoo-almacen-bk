-- ==================================================================
-- FLYWAY MIGRATION V14: Sincronización Offline (POS-04)
-- NexooHub Almacén
-- ==================================================================
-- Descripción: Crea la tabla de control de lotes de sincronización
--              para subida de ventas offline.
-- Autor: IA (NexooHub Development)
-- Fecha: 2026-03-24
-- ==================================================================

CREATE TABLE lote_sincronizacion (
    id                    SERIAL PRIMARY KEY,
    codigo_lote           VARCHAR(50)   NOT NULL UNIQUE,
    sucursal_id           INTEGER       NOT NULL,
    caja_id               INTEGER       NOT NULL,
    usuario_id            INTEGER       NOT NULL,
    fecha_generacion      TIMESTAMP     NOT NULL, -- Cuándo se generó el lote en la caja local
    estatus               VARCHAR(20)   NOT NULL, -- PROCESADO | FALLIDO | PENDIENTE
    total_ventas          INTEGER       NOT NULL DEFAULT 0,
    ventas_procesadas     INTEGER       NOT NULL DEFAULT 0,
    monto_total_lote      NUMERIC(10,2) NOT NULL DEFAULT 0.00,
    payload_json          TEXT          NOT NULL, -- Respaldo del array JSON enviado
    errores_detalle       TEXT,                   -- Log de por qué falló
    fecha_sincronizacion  TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    intentos              INTEGER       NOT NULL DEFAULT 1
);

CREATE INDEX idx_lote_sync_sucursal ON lote_sincronizacion(sucursal_id);
CREATE INDEX idx_lote_sync_estatus  ON lote_sincronizacion(estatus);

-- ==================================================================
-- FIN DE MIGRACIÓN V14
-- ==================================================================
