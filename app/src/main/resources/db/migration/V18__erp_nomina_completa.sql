-- ==================================================================
-- FLYWAY MIGRATION V18: Nómina Completa y RRHH (ERP-04)
-- ==================================================================

-- 1. Empleados (indepentientes del login 'users', aunque pueden vincularse si entran al sistema)
CREATE TABLE empleado (
    id                  SERIAL PRIMARY KEY,
    usuario_id          INTEGER      UNIQUE,           -- ID en tabla users, si tiene acceso al sistema
    sucursal_id         INTEGER      NOT NULL,
    nombre_completo     VARCHAR(100) NOT NULL,
    curp                VARCHAR(18)  UNIQUE,
    rfc                 VARCHAR(13)  UNIQUE,
    nss                 VARCHAR(15)  UNIQUE,
    departamento        VARCHAR(50)  NOT NULL,
    puesto              VARCHAR(50)  NOT NULL,
    salario_diario      NUMERIC(12,4) NOT NULL DEFAULT 0.00,
    fecha_ingreso       DATE         NOT NULL,
    estatus             VARCHAR(20)  NOT NULL DEFAULT 'ACTIVO', -- ACTIVO, INACTIVO
    created_at          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 2. Periodo de Nómina (Ej: Quincena 1 Enero 2026, Semana 12, etc.)
CREATE TABLE nomina_periodo (
    id                  SERIAL PRIMARY KEY,
    nombre              VARCHAR(100) NOT NULL,         -- "1ra Quincena Ene 2026"
    fecha_inicio        DATE         NOT NULL,
    fecha_fin           DATE         NOT NULL,
    tipo_periodo        VARCHAR(20)  NOT NULL,         -- SEMANAL, QUINCENAL, MENSUAL
    estatus             VARCHAR(20)  NOT NULL DEFAULT 'BORRADOR', -- BORRADOR, PAGADA, CANCELADA
    usuario_id          INTEGER      NOT NULL,         -- Quien generó el periodo
    created_at          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 3. Recibo de Nómina (El consolidado para 1 empleado en 1 periodo)
CREATE TABLE recibo_nomina (
    id                  SERIAL PRIMARY KEY,
    periodo_id          INTEGER      NOT NULL REFERENCES nomina_periodo(id),
    empleado_id         INTEGER      NOT NULL REFERENCES empleado(id),
    dias_trabajados     NUMERIC(4,2) NOT NULL DEFAULT 15.00,
    total_percepciones  NUMERIC(12,2) NOT NULL DEFAULT 0.00,
    total_deducciones   NUMERIC(12,2) NOT NULL DEFAULT 0.00,
    neto_pagar          NUMERIC(12,2) NOT NULL DEFAULT 0.00,
    metodo_pago         VARCHAR(50)  NOT NULL DEFAULT 'TRANSFERENCIA',
    created_at          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(periodo_id, empleado_id) -- Un empleado tiene máximo un recibo por periodo regular
);

-- 4. Detalle de Recibo (Conceptos de Percepción y Deducción)
CREATE TABLE recibo_nomina_detalle (
    id                  SERIAL PRIMARY KEY,
    recibo_id           INTEGER      NOT NULL REFERENCES recibo_nomina(id) ON DELETE CASCADE,
    tipo_concepto       VARCHAR(20)  NOT NULL,         -- PERCEPCION, DEDUCCION
    clave_sat           VARCHAR(10),                   -- Ej. "001" para sueldo, "002" para ISR
    descripcion         VARCHAR(100) NOT NULL,
    importe             NUMERIC(12,2) NOT NULL,
    created_at          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_empleado_sucursal ON empleado(sucursal_id);
CREATE INDEX idx_nomina_periodo_fechas ON nomina_periodo(fecha_inicio, fecha_fin);
CREATE INDEX idx_recibo_nomina_empleado ON recibo_nomina(empleado_id);
CREATE INDEX idx_recibo_detalle_recibo ON recibo_nomina_detalle(recibo_id);

-- ==================================================================
-- FIN DE MIGRACIÓN V18
-- ==================================================================
