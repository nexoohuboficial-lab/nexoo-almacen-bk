-- ==================================================================
-- FLYWAY MIGRATION V13: Facturación Electrónica SAT/CFDI (POS-03)
-- NexooHub Almacén
-- ==================================================================
-- Descripción: Crea tablas para el almacenamiento de facturas 
--              fiscales (UUID, XML, PDF) y configuración de PACs.
-- Autor: IA (NexooHub Development)
-- Fecha: 2026-03-24
-- ==================================================================

-- 1. TABLAS NUEVAS
CREATE TABLE config_pac (
    id                    SERIAL PRIMARY KEY,
    proveedor             VARCHAR(50)   NOT NULL, -- FACTURAPI | SW_SAPIEN | EDICOM
    clave_api             VARCHAR(255)  NOT NULL,
    url_endpoint          VARCHAR(255)  NOT NULL,
    is_activo             BOOLEAN       DEFAULT false,
    entorno               VARCHAR(20)   NOT NULL DEFAULT 'PRUEBAS', -- PRUEBAS | PRODUCCION
    rfc_emisor            VARCHAR(13)   NOT NULL,
    razon_social_emisor   VARCHAR(255)  NOT NULL,
    regimen_fiscal_emisor VARCHAR(10)   NOT NULL,
    codigo_postal_emisor  VARCHAR(5)    NOT NULL,
    fecha_creacion        TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    usuario_creacion      VARCHAR(50),
    fecha_actualizacion   TIMESTAMP              DEFAULT CURRENT_TIMESTAMP,
    usuario_actualizacion VARCHAR(50)
);

CREATE TABLE factura_fiscal (
    id                    SERIAL PRIMARY KEY,
    venta_id              INTEGER       NOT NULL,
    cliente_id            INTEGER       NOT NULL,
    uuid                  VARCHAR(36)   NOT NULL UNIQUE,
    estatus               VARCHAR(20)   NOT NULL, -- TIMBRADA | CANCELADA | ERROR
    fecha_emision         TIMESTAMP     NOT NULL,
    monto_total           NUMERIC(10,2) NOT NULL,
    moneda                VARCHAR(3)    NOT NULL DEFAULT 'MXN',
    uso_cfdi              VARCHAR(5)    NOT NULL,
    metodo_pago           VARCHAR(5)    NOT NULL, -- PUE | PPD
    forma_pago            VARCHAR(5)    NOT NULL,
    rfc_receptor          VARCHAR(13)   NOT NULL,
    razon_social_receptor VARCHAR(255)  NOT NULL,
    codigo_postal_receptor VARCHAR(5)   NOT NULL,
    regimen_fiscal_receptor VARCHAR(10) NOT NULL,
    xml_generado          TEXT,
    url_pdf               VARCHAR(500),
    motivo_cancelacion    VARCHAR(50),
    acuse_cancelacion     TEXT,
    fecha_creacion        TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    usuario_creacion      VARCHAR(50),
    fecha_actualizacion   TIMESTAMP              DEFAULT CURRENT_TIMESTAMP,
    usuario_actualizacion VARCHAR(50)
);

-- 2. DATOS SEMILLA
INSERT INTO config_pac (proveedor, clave_api, url_endpoint, is_activo, entorno, rfc_emisor, razon_social_emisor, regimen_fiscal_emisor, codigo_postal_emisor) 
VALUES ('FACTURAPI', 'sk_test_1234567890', 'https://api.facturapi.io/v2', true, 'PRUEBAS', 'EKU9003173C9', 'EMPRESA PRUEBA SA DE CV', '601', '00000');

-- 4. ÍNDICES DE OPTIMIZACIÓN
CREATE INDEX idx_factura_fiscal_venta   ON factura_fiscal(venta_id);
CREATE INDEX idx_factura_fiscal_cliente ON factura_fiscal(cliente_id);
CREATE INDEX idx_factura_fiscal_uuid    ON factura_fiscal(uuid);
CREATE INDEX idx_factura_fiscal_estatus ON factura_fiscal(estatus);

-- ==================================================================
-- FIN DE MIGRACIÓN V13
-- ==================================================================
