-- ==================================================================
-- FLYWAY MIGRATION V16: Tesorería y Contabilidad (ERP-02)
-- NexooHub Almacén
-- ==================================================================

-- 1. Catálogo de Cuentas Contables (Activo, Pasivo, Capital, Ingresos, Gastos)
CREATE TABLE cuenta_contable (
    id                  SERIAL PRIMARY KEY,
    codigo              VARCHAR(20)   NOT NULL UNIQUE, -- ej. 100-01, 200-01
    nombre              VARCHAR(100)  NOT NULL,
    tipo_cuenta         VARCHAR(20)   NOT NULL, -- ACTIVO, PASIVO, CAPITAL, INGRESO, GASTO
    naturaleza          VARCHAR(15)   NOT NULL, -- DEUDORA, ACREEDORA
    nivel               INTEGER       NOT NULL DEFAULT 1, -- Para agrupaciones jerárquicas
    cuenta_padre_id     INTEGER REFERENCES cuenta_contable(id),
    activa              BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_cuenta_codigo ON cuenta_contable(codigo);
CREATE INDEX idx_cuenta_tipo   ON cuenta_contable(tipo_cuenta);

-- 2. Póliza Contable (Cabecera)
CREATE TABLE poliza_contable (
    id                  SERIAL PRIMARY KEY,
    numero_poliza       VARCHAR(50)   NOT NULL UNIQUE,
    fecha               DATE          NOT NULL,
    tipo_poliza         VARCHAR(20)   NOT NULL, -- DIARIO, INGRESO, EGRESO
    concepto            VARCHAR(255)  NOT NULL,
    total_cargo         NUMERIC(14,2) NOT NULL DEFAULT 0.00,
    total_abono         NUMERIC(14,2) NOT NULL DEFAULT 0.00,
    estatus             VARCHAR(20)   NOT NULL DEFAULT 'APLICADA', -- APLICADA, CANCELADA
    usuario_id          INTEGER       NOT NULL,
    created_at          TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_poliza_fecha ON poliza_contable(fecha);

-- 3. Movimiento de Póliza (Detalle - Partida doble)
CREATE TABLE movimiento_contable (
    id                  SERIAL PRIMARY KEY,
    poliza_id           INTEGER       NOT NULL REFERENCES poliza_contable(id) ON DELETE CASCADE,
    cuenta_id           INTEGER       NOT NULL REFERENCES cuenta_contable(id),
    concepto_detalle    VARCHAR(255),
    cargo               NUMERIC(14,2) NOT NULL DEFAULT 0.00,
    abono               NUMERIC(14,2) NOT NULL DEFAULT 0.00,
    created_at          TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_movimiento_poliza ON movimiento_contable(poliza_id);
CREATE INDEX idx_movimiento_cuenta ON movimiento_contable(cuenta_id);

-- ==================================================================
-- Inserción del Catálogo Básico para Pruebas y Arranque de Sistema
-- ==================================================================
INSERT INTO cuenta_contable (codigo, nombre, tipo_cuenta, naturaleza, nivel) VALUES
('100',  'Activo Circulante',      'ACTIVO',  'DEUDORA',   1),
('101',  'Bancos Nacionales',      'ACTIVO',  'DEUDORA',   2),
('102',  'Caja General',           'ACTIVO',  'DEUDORA',   2),
('105',  'Clientes',               'ACTIVO',  'DEUDORA',   2),
('200',  'Pasivo a Corto Plazo',   'PASIVO',  'ACREEDORA', 1),
('201',  'Proveedores',            'PASIVO',  'ACREEDORA', 2),
('300',  'Capital Contable',       'CAPITAL', 'ACREEDORA', 1),
('400',  'Ingresos',               'INGRESO', 'ACREEDORA', 1),
('401',  'Ventas Netas',           'INGRESO', 'ACREEDORA', 2),
('500',  'Costos',                 'GASTO',   'DEUDORA',   1),
('501',  'Costo de Ventas',        'GASTO',   'DEUDORA',   2),
('600',  'Gastos Operativos',      'GASTO',   'DEUDORA',   1),
('601',  'Gastos de Administración', 'GASTO', 'DEUDORA',   2),
('602',  'Gastos de Venta',        'GASTO',   'DEUDORA',   2);

-- ==================================================================
-- FIN DE MIGRACIÓN V16
-- ==================================================================
