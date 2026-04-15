-- ==================================================================
-- FLYWAY MIGRATION V19: Devoluciones a Proveedor (ERP-05)
-- ==================================================================

-- 1. Cabecera de la devolución
CREATE TABLE devolucion_proveedor (
    id                  SERIAL PRIMARY KEY,
    proveedor_id        INTEGER      NOT NULL REFERENCES proveedor_cxp(id),
    sucursal_id         INTEGER      NOT NULL,
    usuario_id          INTEGER      NOT NULL,
    fecha               DATE         NOT NULL DEFAULT CURRENT_DATE,
    motivo              VARCHAR(255) NOT NULL,
    estatus             VARCHAR(20)  NOT NULL DEFAULT 'CREADA', -- CREADA, APLICADA, CANCELADA
    total               NUMERIC(12,2) NOT NULL DEFAULT 0.00,
    created_at          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 2. Detalle de la devolución (productos mermados/devueltos)
CREATE TABLE devolucion_proveedor_detalle (
    id                  SERIAL PRIMARY KEY,
    devolucion_id       INTEGER      NOT NULL REFERENCES devolucion_proveedor(id) ON DELETE CASCADE,
    sku_interno         VARCHAR(50)  NOT NULL,
    cantidad            INTEGER      NOT NULL,
    costo_unitario      NUMERIC(12,2) NOT NULL,
    subtotal            NUMERIC(12,2) NOT NULL,
    created_at          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_devolucion_proveedor_prov ON devolucion_proveedor(proveedor_id);
CREATE INDEX idx_devolucion_proveedor_sucursal ON devolucion_proveedor(sucursal_id);
CREATE INDEX idx_devolucion_proveedor_det_dev ON devolucion_proveedor_detalle(devolucion_id);

-- ==================================================================
-- FIN DE MIGRACIÓN V19
-- ==================================================================
