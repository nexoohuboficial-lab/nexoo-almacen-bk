-- =====================================================
-- Migraci\u00f3n: Rentabilidad por Venta/Producto
-- Versi\u00f3n: V6
-- Descripci\u00f3n: Tablas para an\u00e1lisis de rentabilidad
-- Autor: NexooHub Development Team
-- Fecha: 2025-01-11
-- =====================================================

-- =====================================================
-- TABLA: rentabilidad_venta
-- Prop\u00f3sito: Almacena an\u00e1lisis de rentabilidad de ventas individuales
-- Responde: \u00bfCu\u00e1nto GANAMOS en cada venta?
-- =====================================================
CREATE TABLE rentabilidad_venta (
    id BIGSERIAL PRIMARY KEY,
    venta_id INTEGER NOT NULL UNIQUE,
    costo_total NUMERIC(15,2) NOT NULL DEFAULT 0.00,
    precio_venta_total NUMERIC(15,2) NOT NULL,
    utilidad_bruta NUMERIC(15,2) NOT NULL,
    margen_porcentaje NUMERIC(5,2) NOT NULL,
    venta_bajo_costo BOOLEAN NOT NULL DEFAULT FALSE,
    cantidad_items INTEGER,
    
    -- Campos de auditor\u00eda
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    
    -- Foreign Key
    CONSTRAINT fk_rentabilidad_venta_venta FOREIGN KEY (venta_id) 
        REFERENCES venta(id) ON DELETE CASCADE,
    
    -- Constraints de validaci\u00f3n
    CONSTRAINT chk_rentabilidad_venta_costo_positivo 
        CHECK (costo_total >= 0),
    CONSTRAINT chk_rentabilidad_venta_precio_positivo 
        CHECK (precio_venta_total > 0)
);

-- Comentarios de documentaci\u00f3n
COMMENT ON TABLE rentabilidad_venta IS 'An\u00e1lisis de rentabilidad por venta individual - \u00bfCu\u00e1nto GANAMOS?';
COMMENT ON COLUMN rentabilidad_venta.venta_id IS 'ID de la venta analizada (relaci\u00f3n 1:1)';
COMMENT ON COLUMN rentabilidad_venta.costo_total IS 'Suma del costo promedio ponderado de todos los productos vendidos';
COMMENT ON COLUMN rentabilidad_venta.precio_venta_total IS 'Precio total de la venta (\u00bfcu\u00e1nto se cobr\u00f3?)';
COMMENT ON COLUMN rentabilidad_venta.utilidad_bruta IS 'Ganancia o p\u00e9rdida: Precio - Costo';
COMMENT ON COLUMN rentabilidad_venta.margen_porcentaje IS 'Margen de rentabilidad porcentual: (Utilidad / Precio) * 100';
COMMENT ON COLUMN rentabilidad_venta.venta_bajo_costo IS 'TRUE si la venta gener\u00f3 p\u00e9rdida (vendido bajo costo)';
COMMENT ON COLUMN rentabilidad_venta.cantidad_items IS 'N\u00famero de productos diferentes en la venta';

-- \u00cdndices para consultas frecuentes
CREATE INDEX idx_rentabilidad_venta_venta_id ON rentabilidad_venta(venta_id);
CREATE INDEX idx_rentabilidad_venta_bajo_costo ON rentabilidad_venta(venta_bajo_costo) WHERE venta_bajo_costo = TRUE;
CREATE INDEX idx_rentabilidad_venta_margen ON rentabilidad_venta(margen_porcentaje DESC);
CREATE INDEX idx_rentabilidad_venta_utilidad ON rentabilidad_venta(utilidad_bruta DESC);


-- =====================================================
-- TABLA: rentabilidad_producto
-- Prop\u00f3sito: An\u00e1lisis agregado de rentabilidad por producto en un per\u00edodo
-- Responde: \u00bfQu\u00e9 productos son MÁS/MENOS rentables?
-- =====================================================
CREATE TABLE rentabilidad_producto (
    id BIGSERIAL PRIMARY KEY,
    sku_interno VARCHAR(50) NOT NULL,
    periodo_inicio DATE NOT NULL,
    periodo_fin DATE NOT NULL,
    cantidad_vendida INTEGER NOT NULL DEFAULT 0,
    costo_promedio_unitario NUMERIC(15,2) NOT NULL DEFAULT 0.00,
    precio_promedio_venta NUMERIC(15,2) NOT NULL,
    utilidad_total_generada NUMERIC(15,2) NOT NULL,
    margen_promedio_porcentaje NUMERIC(5,2) NOT NULL,
    numero_ventas INTEGER,
    
    -- Campos de auditor\u00eda
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    
    -- Foreign Key
    CONSTRAINT fk_rentabilidad_producto_sku FOREIGN KEY (sku_interno) 
        REFERENCES producto_maestro(sku_interno) ON DELETE CASCADE,
    
    -- Constraints de validaci\u00f3n
    CONSTRAINT chk_rentabilidad_producto_cantidad_positiva 
        CHECK (cantidad_vendida >= 0),
    CONSTRAINT chk_rentabilidad_producto_costo_positivo 
        CHECK (costo_promedio_unitario >= 0),
    CONSTRAINT chk_rentabilidad_producto_precio_positivo 
        CHECK (precio_promedio_venta > 0),
    CONSTRAINT chk_rentabilidad_producto_periodo_valido 
        CHECK (periodo_fin >= periodo_inicio)
);

-- Comentarios de documentaci\u00f3n
COMMENT ON TABLE rentabilidad_producto IS 'An\u00e1lisis agregado de rentabilidad por producto en per\u00edodos';
COMMENT ON COLUMN rentabilidad_producto.sku_interno IS 'SKU del producto analizado';
COMMENT ON COLUMN rentabilidad_producto.periodo_inicio IS 'Fecha de inicio del per\u00edodo analizado';
COMMENT ON COLUMN rentabilidad_producto.periodo_fin IS 'Fecha de fin del per\u00edodo analizado';
COMMENT ON COLUMN rentabilidad_producto.cantidad_vendida IS 'Total de unidades vendidas en el per\u00edodo';
COMMENT ON COLUMN rentabilidad_producto.costo_promedio_unitario IS 'Costo promedio del producto (basado en CPP)';
COMMENT ON COLUMN rentabilidad_producto.precio_promedio_venta IS 'Precio promedio al que se vendi\u00f3 el producto';
COMMENT ON COLUMN rentabilidad_producto.utilidad_total_generada IS 'GANANCIA TOTAL generada por este producto: (Precio - Costo) * Cantidad';
COMMENT ON COLUMN rentabilidad_producto.margen_promedio_porcentaje IS 'Margen porcentual promedio del producto';
COMMENT ON COLUMN rentabilidad_producto.numero_ventas IS 'N\u00famero de ventas en las que apareci\u00f3 el producto';

-- \u00cdndices para consultas frecuentes
CREATE INDEX idx_rentabilidad_producto_sku ON rentabilidad_producto(sku_interno);
CREATE INDEX idx_rentabilidad_producto_periodo ON rentabilidad_producto(periodo_inicio, periodo_fin);
CREATE INDEX idx_rentabilidad_producto_margen ON rentabilidad_producto(margen_promedio_porcentaje DESC);
CREATE INDEX idx_rentabilidad_producto_utilidad ON rentabilidad_producto(utilidad_total_generada DESC);

-- \u00cdndice compuesto para b\u00fasquedas por producto en per\u00edodo espec\u00edfico
CREATE INDEX idx_rentabilidad_producto_sku_periodo 
    ON rentabilidad_producto(sku_interno, periodo_inicio, periodo_fin);

-- =====================================================
-- FIN DE MIGRACI\u00d3N V6
-- =====================================================
