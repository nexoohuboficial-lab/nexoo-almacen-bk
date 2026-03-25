-- ==================================================================
-- FLYWAY MIGRATION V15: Cuentas por Pagar y Gastos Operativos (ERP-01)
-- NexooHub Almacén
-- ==================================================================

-- 1. Facturas de proveedor pendientes de pago
CREATE TABLE cuenta_por_pagar (
    id                  SERIAL PRIMARY KEY,
    proveedor_id        INTEGER       NOT NULL,
    numero_factura      VARCHAR(50)   NOT NULL,
    descripcion         VARCHAR(255),
    monto_total         NUMERIC(12,2) NOT NULL,
    monto_pagado        NUMERIC(12,2) NOT NULL DEFAULT 0.00,
    saldo_pendiente     NUMERIC(12,2) NOT NULL,
    fecha_factura       DATE          NOT NULL,
    fecha_vencimiento   DATE          NOT NULL,
    estatus             VARCHAR(20)   NOT NULL DEFAULT 'PENDIENTE', -- PENDIENTE | PARCIAL | PAGADA
    sucursal_id         INTEGER,
    created_at          TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_cxp_proveedor   ON cuenta_por_pagar(proveedor_id);
CREATE INDEX idx_cxp_estatus     ON cuenta_por_pagar(estatus);
CREATE INDEX idx_cxp_vencimiento ON cuenta_por_pagar(fecha_vencimiento);

-- 2. Abonos / pagos aplicados a una CxP
CREATE TABLE pago_proveedor (
    id                  SERIAL PRIMARY KEY,
    cuenta_por_pagar_id INTEGER       NOT NULL REFERENCES cuenta_por_pagar(id),
    monto_abono         NUMERIC(12,2) NOT NULL,
    metodo_pago         VARCHAR(30)   NOT NULL, -- TRANSFERENCIA, CHEQUE, EFECTIVO
    referencia_pago     VARCHAR(80),
    fecha_pago          DATE          NOT NULL,
    observaciones       VARCHAR(255),
    created_at          TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_pago_prov_cxp ON pago_proveedor(cuenta_por_pagar_id);

-- 3. Gastos operativos (renta, luz, nómina, transporte, etc.)
CREATE TABLE gasto_operativo (
    id                  SERIAL PRIMARY KEY,
    concepto            VARCHAR(150)  NOT NULL,
    categoria           VARCHAR(50)   NOT NULL, -- RENTA, SERVICIOS, NOMINA, TRANSPORTE, OTROS
    monto               NUMERIC(12,2) NOT NULL,
    fecha_gasto         DATE          NOT NULL,
    sucursal_id         INTEGER,
    usuario_id          INTEGER       NOT NULL,
    comprobante_ref     VARCHAR(100),
    observaciones       VARCHAR(255),
    created_at          TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_gasto_fecha     ON gasto_operativo(fecha_gasto);
CREATE INDEX idx_gasto_categoria ON gasto_operativo(categoria);

-- ==================================================================
-- FIN DE MIGRACIÓN V15
-- ==================================================================
