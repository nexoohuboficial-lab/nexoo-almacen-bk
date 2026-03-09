-- =====================================================
-- SCRIPT SQL: SISTEMA DE CONTROL DE CRÉDITO
-- Base de datos: PostgreSQL 15+
-- Autor: NexooHub Development Team
-- Versión: 1.2.0
-- =====================================================

-- =====================================================
-- TABLAS
-- =====================================================

-- Tabla: limite_credito
-- Descripción: Almacena los límites de crédito configurados por cliente
CREATE TABLE IF NOT EXISTS limite_credito (
    id SERIAL PRIMARY KEY,
    cliente_id INTEGER NOT NULL UNIQUE,
    limite_autorizado NUMERIC(12,2) NOT NULL CHECK (limite_autorizado >= 0),
    saldo_utilizado NUMERIC(12,2) NOT NULL DEFAULT 0 CHECK (saldo_utilizado >= 0),
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO' CHECK (estado IN ('ACTIVO', 'BLOQUEADO', 'SUSPENDIDO', 'INACTIVO')),
    plazo_pago_dias INTEGER DEFAULT 30 CHECK (plazo_pago_dias > 0),
    max_facturas_vencidas INTEGER DEFAULT 3,
    permite_sobregiro BOOLEAN DEFAULT FALSE,
    monto_sobregiro NUMERIC(12,2) DEFAULT 0 CHECK (monto_sobregiro >= 0),
    fecha_revision DATE,
    observaciones TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    FOREIGN KEY (cliente_id) REFERENCES cliente(id) ON DELETE RESTRICT
);

COMMENT ON TABLE limite_credito IS 'Límites de crédito configurados por cliente con control de saldo y bloqueos';
COMMENT ON COLUMN limite_credito.limite_autorizado IS 'Monto máximo de crédito que el cliente puede usar';
COMMENT ON COLUMN limite_credito.saldo_utilizado IS 'Saldo actualmente utilizado por el cliente';
COMMENT ON COLUMN limite_credito.estado IS 'Estado del crédito: ACTIVO, BLOQUEADO, SUSPENDIDO, INACTIVO';
COMMENT ON COLUMN limite_credito.plazo_pago_dias IS 'Número de días de plazo para pago';
COMMENT ON COLUMN limite_credito.max_facturas_vencidas IS 'Máximo de facturas vencidas antes de bloquear';
COMMENT ON COLUMN limite_credito.permite_sobregiro IS 'Permite exceder temporalmente el límite';
COMMENT ON COLUMN limite_credito.monto_sobregiro IS 'Monto máximo de sobregiro permitido';

-- Tabla: historial_credito
-- Descripción: Registra todos los movimientos de crédito (cargos, abonos, ajustes)
CREATE TABLE IF NOT EXISTS historial_credito (
    id SERIAL PRIMARY KEY,
    cliente_id INTEGER NOT NULL,
    venta_id INTEGER,
    tipo_movimiento VARCHAR(20) NOT NULL CHECK (tipo_movimiento IN ('CARGO', 'ABONO', 'AJUSTE', 'BLOQUEO', 'DESBLOQUEO')),
    monto NUMERIC(12,2) NOT NULL CHECK (monto > 0),
    saldo_resultante NUMERIC(12,2) NOT NULL,
    metodo_pago VARCHAR(30),
    folio_comprobante VARCHAR(50),
    concepto VARCHAR(500) NOT NULL,
    observaciones TEXT,
    fecha_movimiento TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    usuario_registro VARCHAR(100),
    FOREIGN KEY (cliente_id) REFERENCES cliente(id) ON DELETE RESTRICT,
    FOREIGN KEY (venta_id) REFERENCES venta(id) ON DELETE SET NULL
);

COMMENT ON TABLE historial_credito IS 'Historial completo de movimientos de crédito de clientes';
COMMENT ON COLUMN historial_credito.tipo_movimiento IS 'Tipo: CARGO (venta), ABONO (pago), AJUSTE, BLOQUEO, DESBLOQUEO';
COMMENT ON COLUMN historial_credito.monto IS 'Monto del movimiento (siempre positivo)';
COMMENT ON COLUMN historial_credito.saldo_resultante IS 'Saldo del cliente después del movimiento';
COMMENT ON COLUMN historial_credito.metodo_pago IS 'Método usado en abonos: EFECTIVO, TRANSFERENCIA, CHEQUE, TARJETA';

-- =====================================================
-- ÍNDICES PARA OPTIMIZACIÓN
-- =====================================================

-- Índices en limite_credito
CREATE INDEX idx_limite_credito_cliente ON limite_credito(cliente_id);
CREATE INDEX idx_limite_credito_estado ON limite_credito(estado);
CREATE INDEX idx_limite_credito_revision ON limite_credito(fecha_revision);
CREATE INDEX idx_limite_credito_sobregiro ON limite_credito(saldo_utilizado, limite_autorizado) WHERE saldo_utilizado > limite_autorizado;

-- Índices en historial_credito
CREATE INDEX idx_historial_credito_cliente ON historial_credito(cliente_id);
CREATE INDEX idx_historial_credito_fecha ON historial_credito(fecha_movimiento DESC);
CREATE INDEX idx_historial_credito_tipo ON historial_credito(tipo_movimiento);
CREATE INDEX idx_historial_credito_venta ON historial_credito(venta_id);
CREATE INDEX idx_historial_credito_cliente_fecha ON historial_credito(cliente_id, fecha_movimiento DESC);
CREATE INDEX idx_historial_credito_cliente_tipo ON historial_credito(cliente_id, tipo_movimiento);

-- =====================================================
-- FUNCIONES Y TRIGGERS
-- =====================================================

-- Función: actualizar updated_at automáticamente
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger: limite_credito updated_at
DROP TRIGGER IF EXISTS trigger_limite_credito_updated_at ON limite_credito;
CREATE TRIGGER trigger_limite_credito_updated_at
    BEFORE UPDATE ON limite_credito
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

-- =====================================================
-- DATOS DE EJEMPLO
-- =====================================================

-- IMPORTANTE: Asegúrate de que existan clientes antes de insertar límites

-- Límites de crédito ejemplo
INSERT INTO limite_credito (cliente_id, limite_autorizado, saldo_utilizado, estado, plazo_pago_dias, max_facturas_vencidas, permite_sobregiro, monto_sobregiro, fecha_revision, observaciones, created_by) VALUES
-- Cliente 1: Público general con límite estándar
(1, 10000.00, 3500.00, 'ACTIVO', 30, 3, FALSE, 0, CURRENT_DATE, 'Cliente nuevo con límite estándar', 'SYSTEM'),

-- Cliente 2: Cliente premium
(2, 50000.00, 12000.00, 'ACTIVO', 45, 5, TRUE, 5000.00, CURRENT_DATE, 'Cliente premium con sobregiro autorizado', 'SYSTEM'),

-- Cliente 3: Cliente en riesgo
(3, 15000.00, 13500.00, 'ACTIVO', 30, 3, FALSE, 0, CURRENT_DATE - INTERVAL '90 days', 'Cliente con alta utilización, requiere monitoreo', 'SYSTEM'),

-- Cliente 4: Cliente bloqueado 
(4, 8000.00, 9500.00, 'BLOQUEADO', 30, 3, FALSE, 0, CURRENT_DATE, 'Bloqueado por exceder límite de crédito', 'SYSTEM'),

-- Cliente 5: Cliente con crédito suspendido
(5, 20000.00, 5000.00, 'SUSPENDIDO', 30, 3, FALSE, 0, CURRENT_DATE, 'Suspendido temporalmente por revisión de cuenta', 'SYSTEM')
ON CONFLICT (cliente_id) DO NOTHING;

-- Historial de movimientos ejemplo
-- Cliente 1: Movimientos mixtos
INSERT INTO historial_credito (cliente_id, venta_id, tipo_movimiento, monto, saldo_resultante, metodo_pago, folio_comprobante, concepto, fecha_movimiento, usuario_registro) VALUES
(1, NULL, 'CARGO', 2000.00, 2000.00, NULL, NULL, 'Venta a crédito #1001', CURRENT_TIMESTAMP - INTERVAL '15 days', 'vendedor1'),
(1, NULL, 'ABONO', 500.00, 1500.00, 'EFECTIVO', 'REC-001', 'Pago parcial', CURRENT_TIMESTAMP - INTERVAL '10 days', 'cajero1'),
(1, NULL, 'CARGO', 2500.00, 4000.00, NULL, NULL, 'Venta a crédito #1015', CURRENT_TIMESTAMP - INTERVAL '5 days', 'vendedor2'),
(1, NULL, 'ABONO', 500.00, 3500.00, 'TRANSFERENCIA', 'TRF-20240301-001', 'Abono a cuenta', CURRENT_TIMESTAMP - INTERVAL '2 days', 'cajero1');

-- Cliente 2: Cliente premium con múltiples movimientos
INSERT INTO historial_credito (cliente_id, venta_id, tipo_movimiento, monto, saldo_resultante, metodo_pago, folio_comprobante, concepto, fecha_movimiento, usuario_registro) VALUES
(2, NULL, 'CARGO', 15000.00, 15000.00, NULL, NULL, 'Venta a crédito #1002', CURRENT_TIMESTAMP - INTERVAL '30 days', 'vendedor1'),
(2, NULL, 'ABONO', 5000.00, 10000.00, 'CHEQUE', 'CHK-12345', 'Pago de factura', CURRENT_TIMESTAMP - INTERVAL '20 days', 'cajero2'),
(2, NULL, 'CARGO', 8000.00, 18000.00, NULL, NULL, 'Venta a crédito #1025', CURRENT_TIMESTAMP - INTERVAL '10 days', 'vendedor1'),
(2, NULL, 'ABONO', 6000.00, 12000.00, 'TRANSFERENCIA', 'TRF-20240305-002', 'Abono mensual', CURRENT_TIMESTAMP - INTERVAL '3 days', 'cajero1');

-- Cliente 3: Cliente en riesgo
INSERT INTO historial_credito (cliente_id, venta_id, tipo_movimiento, monto, saldo_resultante, metodo_pago, folio_comprobante, concepto, fecha_movimiento, usuario_registro) VALUES
(3, NULL, 'CARGO', 10000.00, 10000.00, NULL, NULL, 'Venta a crédito #1003', CURRENT_TIMESTAMP - INTERVAL '60 days', 'vendedor2'),
(3, NULL, 'CARGO', 3500.00, 13500.00, NULL, NULL, 'Venta a crédito #1018', CURRENT_TIMESTAMP - INTERVAL '40 days', 'vendedor1');

-- Cliente 4: Cliente bloqueado por sobregiro
INSERT INTO historial_credito (cliente_id, venta_id, tipo_movimiento, monto, saldo_resultante, metodo_pago, folio_comprobante, concepto, fecha_movimiento, usuario_registro) VALUES
(4, NULL, 'CARGO', 5000.00, 5000.00, NULL, NULL, 'Venta a crédito #1004', CURRENT_TIMESTAMP - INTERVAL '45 days', 'vendedor1'),
(4, NULL, 'CARGO', 3000.00, 8000.00, NULL, NULL, 'Venta a crédito #1012', CURRENT_TIMESTAMP - INTERVAL '30 days', 'vendedor2'),
(4, NULL, 'CARGO', 1500.00, 9500.00, NULL, NULL, 'Venta a crédito #1022', CURRENT_TIMESTAMP - INTERVAL '15 days', 'vendedor1'),
(4, NULL, 'BLOQUEO', 0, 9500.00, NULL, NULL, 'Crédito bloqueado por exceder límite', CURRENT_TIMESTAMP - INTERVAL '10 days', 'SYSTEM');

-- Cliente 5: Cliente suspendido
INSERT INTO historial_credito (cliente_id, venta_id, tipo_movimiento, monto, saldo_resultante, metodo_pago, folio_comprobante, concepto, fecha_movimiento, usuario_registro) VALUES
(5, NULL, 'CARGO', 5000.00, 5000.00, NULL, NULL, 'Venta a crédito #1005', CURRENT_TIMESTAMP - INTERVAL '20 days', 'vendedor1'),
(5, NULL, 'BLOQUEO', 0, 5000.00, NULL, NULL, 'Crédito suspendido para revisión', CURRENT_TIMESTAMP - INTERVAL '5 days', 'gerente1');

-- =====================================================
-- VISTAS ÚTILES
-- =====================================================

-- Vista: resumen de crédito por cliente
CREATE OR REPLACE VIEW v_resumen_credito AS
SELECT 
    c.id AS cliente_id,
    c.nombre AS cliente_nombre,
    c.rfc,
    lc.limite_autorizado,
    lc.saldo_utilizado,
    (lc.limite_autorizado - lc.saldo_utilizado) AS credito_disponible,
    ROUND((lc.saldo_utilizado * 100.0 / NULLIF(lc.limite_autorizado, 0)), 2) AS porcentaje_utilizacion,
    lc.estado,
    lc.plazo_pago_dias,
    lc.fecha_revision,
    COUNT(hc.id) AS total_movimientos,
    MAX(hc.fecha_movimiento) AS ultimo_movimiento
FROM cliente c
INNER JOIN limite_credito lc ON c.id = lc.cliente_id
LEFT JOIN historial_credito hc ON c.id = hc.cliente_id
GROUP BY c.id, c.nombre, c.rfc, lc.limite_autorizado, lc.saldo_utilizado, lc.estado, lc.plazo_pago_dias, lc.fecha_revision;

COMMENT ON VIEW v_resumen_credito IS 'Vista consolidada del estado de crédito por cliente';

-- Vista: clientes en riesgo (>= 80% utilización)
CREATE OR REPLACE VIEW v_clientes_riesgo AS
SELECT 
    cliente_id,
    cliente_nombre,
    rfc,
    limite_autorizado,
    saldo_utilizado,
    credito_disponible,
    porcentaje_utilizacion,
    estado
FROM v_resumen_credito
WHERE porcentaje_utilizacion >= 80 AND estado = 'ACTIVO'
ORDER BY porcentaje_utilizacion DESC;

COMMENT ON VIEW v_clientes_riesgo IS 'Clientes con utilización >= 80% que requieren monitoreo';

-- Vista: clientes en sobregiro
CREATE OR REPLACE VIEW v_clientes_sobregiro AS
SELECT 
    c.id AS cliente_id,
    c.nombre AS cliente_nombre,
    c.rfc,
    lc.limite_autorizado,
    lc.saldo_utilizado,
    (lc.saldo_utilizado - lc.limite_autorizado) AS monto_sobregiro,
    lc.estado,
    lc.permite_sobregiro,
    lc.monto_sobregiro AS sobregiro_autorizado
FROM cliente c
INNER JOIN limite_credito lc ON c.id = lc.cliente_id
WHERE lc.saldo_utilizado > lc.limite_autorizado
ORDER BY (lc.saldo_utilizado - lc.limite_autorizado) DESC;

COMMENT ON VIEW v_clientes_sobregiro IS 'Clientes que excedieron su límite de crédito';

-- =====================================================
-- CONSULTAS ÚTILES
-- =====================================================

-- Consulta: Resumen general del sistema de crédito
/*
SELECT 
    COUNT(*) AS total_clientes,
    COUNT(CASE WHEN estado = 'ACTIVO' THEN 1 END) AS clientes_activos,
    COUNT(CASE WHEN estado = 'BLOQUEADO' THEN 1 END) AS clientes_bloqueados,
    SUM(limite_autorizado) AS credito_total_autorizado,
    SUM(saldo_utilizado) AS credito_total_utilizado,
    SUM(limite_autorizado - saldo_utilizado) AS credito_total_disponible,
    ROUND(AVG((saldo_utilizado * 100.0 / NULLIF(limite_autorizado, 0))), 2) AS promedio_utilizacion
FROM limite_credito;
*/

-- Consulta: Top 10 clientes con mayor deuda
/*
SELECT 
    c.nombre,
    c.rfc,
    lc.saldo_utilizado,
    lc.limite_autorizado,
    lc.estado
FROM cliente c
INNER JOIN limite_credito lc ON c.id = lc.cliente_id
ORDER BY lc.saldo_utilizado DESC
LIMIT 10;
*/

-- Consulta: Movimientos del mes actual
/*
SELECT 
    c.nombre AS cliente,
    hc.tipo_movimiento,
    hc.monto,
    hc.concepto,
    hc.fecha_movimiento
FROM historial_credito hc
INNER JOIN cliente c ON hc.cliente_id = c.id
WHERE DATE_TRUNC('month', hc.fecha_movimiento) = DATE_TRUNC('month', CURRENT_DATE)
ORDER BY hc.fecha_movimiento DESC;
*/

-- =====================================================
-- FIN DEL SCRIPT
-- =====================================================
