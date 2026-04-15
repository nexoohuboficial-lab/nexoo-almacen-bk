-- ==================================================================
-- FLYWAY MIGRATION V2: Comisiones para Vendedores
-- NexooHub Almacén - Módulo de Comisiones
-- ==================================================================
-- Descripción: Agrega tablas para gestionar comisiones de vendedores
--              - Reglas de comisión configurables
--              - Cálculo automático de comisiones por periodo
--              - Control de aprobación y pago
-- Autor: NexooHub Development Team
-- Fecha: 2026-03-09
-- ==================================================================

-- Tabla de reglas de comisión
CREATE TABLE regla_comision (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(500),
    tipo VARCHAR(50) NOT NULL, -- 'PORCENTAJE_VENTA', 'MONTO_FIJO', 'POR_META', 'POR_PRODUCTO'
    puesto VARCHAR(50), -- NULL = aplica a todos los puestos
    porcentaje_comision NUMERIC(5, 4) DEFAULT 0.0000 CHECK (porcentaje_comision >= 0 AND porcentaje_comision <= 1),
    monto_fijo NUMERIC(10, 2) DEFAULT 0.00 CHECK (monto_fijo >= 0),
    meta_mensual NUMERIC(12, 2) CHECK (meta_mensual >= 0),
    bono_meta NUMERIC(10, 2) CHECK (bono_meta >= 0),
    sku_producto VARCHAR(50), -- Para comisiones específicas por producto
    activa BOOLEAN DEFAULT TRUE NOT NULL,
    prioridad INTEGER DEFAULT 1 NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_creacion VARCHAR(50),
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_actualizacion VARCHAR(50)
);

-- Índices para regla_comision
CREATE INDEX idx_regla_comision_puesto ON regla_comision(puesto);
CREATE INDEX idx_regla_comision_activa ON regla_comision(activa);
CREATE INDEX idx_regla_comision_tipo ON regla_comision(tipo);
CREATE INDEX idx_regla_comision_sku ON regla_comision(sku_producto);

-- Tabla de comisiones calculadas
CREATE TABLE comision (
    id SERIAL PRIMARY KEY,
    vendedor_id INTEGER NOT NULL REFERENCES empleados(id) ON DELETE CASCADE,
    periodo_anio INTEGER NOT NULL CHECK (periodo_anio >= 2000 AND periodo_anio <= 2100),
    periodo_mes INTEGER NOT NULL CHECK (periodo_mes >= 1 AND periodo_mes <= 12),
    total_ventas NUMERIC(12, 2) DEFAULT 0.00 NOT NULL CHECK (total_ventas >= 0),
    cantidad_ventas INTEGER DEFAULT 0 NOT NULL,
    comision_base NUMERIC(10, 2) DEFAULT 0.00 NOT NULL CHECK (comision_base >= 0),
    bonos NUMERIC(10, 2) DEFAULT 0.00 CHECK (bonos >= 0),
    ajustes NUMERIC(10, 2) DEFAULT 0.00, -- Puede ser positivo o negativo
    total_comision NUMERIC(10, 2) DEFAULT 0.00 NOT NULL CHECK (total_comision >= 0),
    estado VARCHAR(20) DEFAULT 'PENDIENTE' NOT NULL, -- 'PENDIENTE', 'APROBADA', 'PAGADA', 'RECHAZADA'
    fecha_aprobacion DATE,
    fecha_pago DATE,
    usuario_aprobador VARCHAR(100),
    notas VARCHAR(1000),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_creacion VARCHAR(50),
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_actualizacion VARCHAR(50),
    
    -- Constraint: Un vendedor solo puede tener una comisión por periodo
    CONSTRAINT uk_comision_vendedor_periodo UNIQUE (vendedor_id, periodo_anio, periodo_mes)
);

-- Índices para comision
CREATE INDEX idx_comision_vendedor ON comision(vendedor_id);
CREATE INDEX idx_comision_periodo ON comision(periodo_anio, periodo_mes);
CREATE INDEX idx_comision_estado ON comision(estado);
CREATE INDEX idx_comision_fecha_pago ON comision(fecha_pago);

-- Comentarios en las tablas
COMMENT ON TABLE regla_comision IS 'Define las reglas para cálculo de comisiones de vendedores';
COMMENT ON TABLE comision IS 'Almacena las comisiones calculadas por vendedor y periodo';

COMMENT ON COLUMN regla_comision.tipo IS 'PORCENTAJE_VENTA: % sobre ventas totales | MONTO_FIJO: cantidad fija | POR_META: bono por alcanzar meta | POR_PRODUCTO: % sobre producto específico';
COMMENT ON COLUMN regla_comision.puesto IS 'Si es NULL, la regla aplica a todos los puestos';
COMMENT ON COLUMN regla_comision.prioridad IS 'Orden de aplicación de reglas (menor = mayor prioridad)';

COMMENT ON COLUMN comision.estado IS 'PENDIENTE: calculada pero no aprobada | APROBADA: revisada y aprobada | PAGADA: ya fue pagada al vendedor | RECHAZADA: no procede el pago';
COMMENT ON COLUMN comision.ajustes IS 'Ajustes manuales positivos o negativos al monto calculado';

-- ==================================================================
-- DATOS DE EJEMPLO: Reglas de Comisión por Defecto
-- ==================================================================

-- Regla 1: Comisión del 3% para todos los vendedores
INSERT INTO regla_comision 
    (nombre, descripcion, tipo, puesto, porcentaje_comision, activa, prioridad, usuario_creacion)
VALUES 
    ('Comisión Base Vendedores', 
     'Comisión estándar del 3% sobre ventas totales para todos los vendedores', 
     'PORCENTAJE_VENTA', 
     'Vendedor', 
     0.0300, 
     TRUE, 
     1, 
     'SYSTEM');

-- Regla 2: Comisión del 2% para cajeros
INSERT INTO regla_comision 
    (nombre, descripcion, tipo, puesto, porcentaje_comision, activa, prioridad, usuario_creacion)
VALUES 
    ('Comisión Cajeros', 
     'Comisión del 2% sobre ventas para personal de caja', 
     'PORCENTAJE_VENTA', 
     'Cajero', 
     0.0200, 
     TRUE, 
     1, 
     'SYSTEM');

-- Regla 3: Bono por meta mensual de $50,000
INSERT INTO regla_comision 
    (nombre, descripcion, tipo, puesto, meta_mensual, bono_meta, activa, prioridad, usuario_creacion)
VALUES 
    ('Bono Meta $50K', 
     'Bono de $2,000 por alcanzar meta de ventas de $50,000 mensuales', 
     'POR_META', 
     NULL, -- Aplica a todos
     50000.00, 
     2000.00, 
     TRUE, 
     2, 
     'SYSTEM');

-- Regla 4: Comisión del 5% para gerentes
INSERT INTO regla_comision 
    (nombre, descripcion, tipo, puesto, porcentaje_comision, activa, prioridad, usuario_creacion)
VALUES 
    ('Comisión Gerentes', 
     'Comisión del 5% sobre ventas totales para gerentes', 
     'PORCENTAJE_VENTA', 
     'Gerente', 
     0.0500, 
     TRUE, 
     1, 
     'SYSTEM');

-- ==================================================================
-- FIN DE MIGRACIÓN V2
-- ==================================================================
