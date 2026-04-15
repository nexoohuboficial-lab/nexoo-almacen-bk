-- ==================================================================
-- FLYWAY MIGRATION V12: Terminales Bancarias (POS-02)
-- NexooHub Almacén
-- ==================================================================
-- Descripción: Crea la tabla para registrar la comunicación y logs
--              de transacciones con terminales bancarias (PinPads).
-- Autor: IA (NexooHub Development)
-- Fecha: 2026-03-24
-- ==================================================================

-- 1. TABLAS NUEVAS
CREATE TABLE log_transaccion_bancaria (
    id                    SERIAL PRIMARY KEY,
    referencia_venta      VARCHAR(50)   NOT NULL,
    monto                 NUMERIC(10,2) NOT NULL,
    tipo_operacion        VARCHAR(20)   NOT NULL, -- VENTA | DEVOLUCION | CANCELACION
    estatus               VARCHAR(20)   NOT NULL, -- PROCESANDO | APROBADO | RECHAZADO | CANCELADO | ERROR
    autorizacion_banco    VARCHAR(50)            DEFAULT NULL,
    terminal_id           VARCHAR(50)   NOT NULL,
    tarjeta_terminacion   VARCHAR(4)             DEFAULT NULL,
    marca_tarjeta         VARCHAR(20)            DEFAULT NULL, -- VISA | MASTERCARD | AMEX
    mensaje_respuesta     VARCHAR(255)           DEFAULT NULL,
    xml_request           TEXT,
    xml_response          TEXT,
    fecha_transaccion     TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_creacion        TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    usuario_creacion      VARCHAR(50),
    fecha_actualizacion   TIMESTAMP              DEFAULT CURRENT_TIMESTAMP,
    usuario_actualizacion VARCHAR(50)
);

-- 4. ÍNDICES DE OPTIMIZACIÓN
CREATE INDEX idx_log_trans_banc_ref     ON log_transaccion_bancaria(referencia_venta);
CREATE INDEX idx_log_trans_banc_estatus ON log_transaccion_bancaria(estatus);
CREATE INDEX idx_log_trans_banc_fecha   ON log_transaccion_bancaria(fecha_transaccion);

-- ==================================================================
-- FIN DE MIGRACIÓN V12
-- ==================================================================
