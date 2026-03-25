-- ==================================================================
-- FLYWAY MIGRATION V17: Logística y Entregas (ERP-03)
-- Soporta Flotilla Propia y Paqueterías (Mercado Libre, DHL)
-- ==================================================================

-- 1. Vehículos (Flotilla propia)
CREATE TABLE vehiculo (
    id                  SERIAL PRIMARY KEY,
    placas              VARCHAR(20)  NOT NULL UNIQUE,
    marca               VARCHAR(50)  NOT NULL,
    modelo              VARCHAR(50)  NOT NULL,
    capacidad_kg        NUMERIC(10,2),
    estatus             VARCHAR(20)  NOT NULL DEFAULT 'ACTIVO', -- ACTIVO, MANTENIMIENTO, BAJA
    sucursal_id         INTEGER      NOT NULL,
    created_at          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 2. Choferes (Flotilla propia)
CREATE TABLE chofer (
    id                  SERIAL PRIMARY KEY,
    nombre_completo     VARCHAR(100) NOT NULL,
    licencia            VARCHAR(50)  NOT NULL UNIQUE,
    vigencia_licencia   DATE         NOT NULL,
    telefono            VARCHAR(20),
    estatus             VARCHAR(20)  NOT NULL DEFAULT 'ACTIVO', -- ACTIVO, INACTIVO
    sucursal_id         INTEGER      NOT NULL,
    created_at          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 3. Ruta de Entrega (Agrupador de envíos por día/chofer o por paquetería externa)
CREATE TABLE ruta_entrega (
    id                  SERIAL PRIMARY KEY,
    codigo_ruta         VARCHAR(50)  NOT NULL UNIQUE,
    fecha_programada    DATE         NOT NULL,
    
    -- Si es flotilla propia:
    chofer_id           INTEGER      REFERENCES chofer(id),
    vehiculo_id         INTEGER      REFERENCES vehiculo(id),
    
    -- Si es paquetería externa (ej. contenedor que se lleva DHL):
    es_paqueteria       BOOLEAN      NOT NULL DEFAULT FALSE,
    proveedor_envio     VARCHAR(50), -- MERCADO_LIBRE, DHL, FEDEX, ESTAFETA
    
    estatus             VARCHAR(20)  NOT NULL DEFAULT 'PENDIENTE', -- PENDIENTE, EN_TRANSITO, COMPLETADA, CANCELADA
    observaciones       TEXT,
    usuario_id          INTEGER      NOT NULL,
    created_at          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 4. Facturas / Envíos asignados a la ruta
CREATE TABLE ruta_factura (
    id                  SERIAL PRIMARY KEY,
    ruta_id             INTEGER      NOT NULL REFERENCES ruta_entrega(id) ON DELETE CASCADE,
    factura_cliente_id  INTEGER      NOT NULL, -- Referencia a la factura/venta a entregar
    
    -- Rastreo individual por paquete
    numero_guia         VARCHAR(100), -- Número de tracking (ej. ML123456789)
    url_rastreo         VARCHAR(255),
    
    estatus_entrega     VARCHAR(20)  NOT NULL DEFAULT 'PENDIENTE', -- PENDIENTE, ENTREGADO, RECHAZADO
    fecha_entrega       TIMESTAMP,
    firma_recibido      VARCHAR(100), -- Nombre de quien recibe
    created_at          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_ruta_fecha ON ruta_entrega(fecha_programada);
CREATE INDEX idx_ruta_chofer ON ruta_entrega(chofer_id);
CREATE INDEX idx_ruta_factura_ruta ON ruta_factura(ruta_id);
CREATE INDEX idx_ruta_factura_guia ON ruta_factura(numero_guia);

-- ==================================================================
-- FIN DE MIGRACIÓN V17
-- ==================================================================
