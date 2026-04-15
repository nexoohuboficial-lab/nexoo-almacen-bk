-- ==================================================================
-- FLYWAY MIGRATION V11: Módulo de Caja y Arqueos (POS-01)
-- NexooHub Almacén
-- ==================================================================
-- Descripción: Crea las tablas turno_caja y movimiento_caja para el
--              control de apertura de turnos, movimientos de efectivo
--              y arqueos de cierre (Arqueo Z).
-- Autor: IA (NexooHub Development)
-- Fecha: 2026-03-24
-- ==================================================================

-- 1. TABLAS NUEVAS

CREATE TABLE turno_caja (
    id                    SERIAL PRIMARY KEY,
    sucursal_id           INTEGER        NOT NULL,
    empleado_id           INTEGER        NOT NULL,
    fondo_inicial         NUMERIC(10,2)  NOT NULL DEFAULT 0.00,
    total_ventas_efectivo NUMERIC(10,2)  NOT NULL DEFAULT 0.00,
    total_ventas_tarjeta  NUMERIC(10,2)  NOT NULL DEFAULT 0.00,
    total_ventas_credito  NUMERIC(10,2)  NOT NULL DEFAULT 0.00,
    total_retiros         NUMERIC(10,2)  NOT NULL DEFAULT 0.00,
    total_ingresos_extra  NUMERIC(10,2)  NOT NULL DEFAULT 0.00,
    efectivo_esperado     NUMERIC(10,2)  NOT NULL DEFAULT 0.00,
    efectivo_real         NUMERIC(10,2)           DEFAULT NULL,
    diferencia            NUMERIC(10,2)           DEFAULT NULL,
    estado                VARCHAR(20)    NOT NULL DEFAULT 'ABIERTO',
                          -- ABIERTO | CERRADO
    observaciones         TEXT,
    fecha_apertura        TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_cierre          TIMESTAMP               DEFAULT NULL,
    fecha_creacion        TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    usuario_creacion      VARCHAR(50),
    fecha_actualizacion   TIMESTAMP               DEFAULT CURRENT_TIMESTAMP,
    usuario_actualizacion VARCHAR(50)
);

COMMENT ON TABLE turno_caja IS 'Registro de turnos de apertura/cierre de caja por empleado y sucursal';
COMMENT ON COLUMN turno_caja.efectivo_esperado IS 'Fondo inicial + ventas en efectivo + ingresos extra - retiros';
COMMENT ON COLUMN turno_caja.efectivo_real IS 'Efectivo físico contado por el empleado al cierre (arqueo Z)';
COMMENT ON COLUMN turno_caja.diferencia IS 'efectivo_real - efectivo_esperado (puede ser negativo)';

CREATE TABLE movimiento_caja (
    id               SERIAL PRIMARY KEY,
    turno_id         INTEGER       NOT NULL REFERENCES turno_caja(id),
    tipo             VARCHAR(30)   NOT NULL,
                     -- RETIRO | INGRESO_EXTRA | VENTA_EFECTIVO | VENTA_TARJETA | VENTA_CREDITO
    monto            NUMERIC(10,2) NOT NULL,
    concepto         VARCHAR(255),
    referencia       VARCHAR(100), -- ID venta, número de referencia bancaria, etc.
    fecha_movimiento TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_creacion   TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    usuario_creacion VARCHAR(50)
);

COMMENT ON TABLE movimiento_caja IS 'Detalle de cada movimiento de efectivo dentro de un turno de caja';

-- 2. DATOS SEMILLA (ninguno requerido para este módulo)

-- 3. REINICIO DE SECUENCIAS (ninguno requerido)

-- 4. ÍNDICES DE OPTIMIZACIÓN

CREATE INDEX idx_turno_caja_sucursal   ON turno_caja(sucursal_id);
CREATE INDEX idx_turno_caja_empleado   ON turno_caja(empleado_id);
CREATE INDEX idx_turno_caja_estado     ON turno_caja(estado);
CREATE INDEX idx_turno_caja_apertura   ON turno_caja(fecha_apertura);

CREATE INDEX idx_movimiento_caja_turno ON movimiento_caja(turno_id);
CREATE INDEX idx_movimiento_caja_tipo  ON movimiento_caja(tipo);
CREATE INDEX idx_movimiento_caja_fecha ON movimiento_caja(fecha_movimiento);

-- ==================================================================
-- FIN DE MIGRACIÓN V11
-- ==================================================================
