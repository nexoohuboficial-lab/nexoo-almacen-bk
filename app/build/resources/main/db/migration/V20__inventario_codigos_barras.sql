-- ==================================================================
-- FLYWAY MIGRATION V20: Códigos de Barras / QR - Escaneo Universal
-- NexooHub Almacén
-- ==================================================================
-- Descripción: Tabla para gestionar múltiples códigos de barras (EAN-13,
--   UPC, QR, INTERNO) por producto. Permite que un producto tenga N
--   identificadores de escaneo sin alterar el sku_interno (PK).
-- Autor: IA
-- Fecha: 2026-03-24
-- ==================================================================

-- 1. TABLA NUEVA
CREATE TABLE codigo_barras_producto (
    id              SERIAL PRIMARY KEY,
    sku_interno     VARCHAR(50)  NOT NULL,
    codigo          VARCHAR(100) NOT NULL,
    tipo            VARCHAR(20)  NOT NULL DEFAULT 'EAN13',
    activo          BOOLEAN      NOT NULL DEFAULT TRUE,
    es_principal    BOOLEAN      NOT NULL DEFAULT FALSE,
    fecha_creacion  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_codbar_producto
        FOREIGN KEY (sku_interno) REFERENCES producto_maestro(sku_interno),
    CONSTRAINT uq_codigo_barras
        UNIQUE (codigo)
);

-- 2. ÍNDICES DE OPTIMIZACIÓN
CREATE INDEX idx_codbar_sku    ON codigo_barras_producto(sku_interno);
CREATE INDEX idx_codbar_codigo ON codigo_barras_producto(codigo);
CREATE INDEX idx_codbar_activo ON codigo_barras_producto(activo);

-- ==================================================================
-- FIN DE MIGRACIÓN V20
-- ==================================================================
