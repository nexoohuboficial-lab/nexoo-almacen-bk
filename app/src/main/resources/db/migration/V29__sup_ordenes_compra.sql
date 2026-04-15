-- ==================================================================
-- FLYWAY MIGRATION V29: SUP-03 Carrito de Compra y Órdenes de Compra
-- NexooHub Almacén
-- ==================================================================
-- Descripción: Gestión del carrito de compra a proveedores y generación
--              de órdenes de compra formales.
-- Autor: IA
-- Fecha: 2026-03-26
-- ==================================================================

-- 1. Tabla: sesion_carrito_compra
-- Almacena los productos que un usuario ADMIN ha agregado a su carrito.
CREATE TABLE sesion_carrito_compra (
    id SERIAL PRIMARY KEY,
    usuario_id INT NOT NULL,
    catalogo_id INT NOT NULL,
    sku_interno VARCHAR(50) NOT NULL,
    proveedor_id INT NOT NULL,
    cantidad INT NOT NULL CHECK (cantidad > 0),
    fecha_agregado TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_carrito_catalogo FOREIGN KEY (catalogo_id) REFERENCES catalogo_proveedor_producto(id) ON DELETE CASCADE,
    CONSTRAINT fk_carrito_proveedor FOREIGN KEY (proveedor_id) REFERENCES proveedor(id) ON DELETE CASCADE,
    CONSTRAINT uq_usuario_catalogo UNIQUE(usuario_id, catalogo_id)
);

-- 2. Tabla: orden_compra_proveedor (Cabecera)
-- Almacena la orden de compra ya generada, lista para exportar/enviar.
CREATE TABLE orden_compra_proveedor (
    id SERIAL PRIMARY KEY,
    folio VARCHAR(50) NOT NULL UNIQUE,  -- Ej. OC-2024-00001
    proveedor_id INT NOT NULL,
    sucursal_id INT NOT NULL,
    estado VARCHAR(30) NOT NULL, -- BORRADOR, ENVIADA, CONFIRMADA, RECIBIDA, CANCELADA
    total_estimado NUMERIC(12, 2) NOT NULL DEFAULT 0,
    notas TEXT,
    fecha_envio TIMESTAMP,
    fecha_esperada_entrega TIMESTAMP,
    activo BOOLEAN NOT NULL DEFAULT true,
    creado_por VARCHAR(50) NOT NULL,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modificado_por VARCHAR(50),
    fecha_modificacion TIMESTAMP,
    CONSTRAINT fk_oc_proveedor FOREIGN KEY (proveedor_id) REFERENCES proveedor(id),
    CONSTRAINT fk_oc_sucursal FOREIGN KEY (sucursal_id) REFERENCES sucursal(id)
);

-- 3. Tabla: detalle_orden_compra (Líneas de la Orden)
CREATE TABLE detalle_orden_compra (
    id SERIAL PRIMARY KEY,
    orden_compra_id INT NOT NULL,
    sku_interno VARCHAR(50) NOT NULL,
    sku_proveedor VARCHAR(50),
    nombre_producto VARCHAR(150) NOT NULL,
    cantidad INT NOT NULL CHECK (cantidad > 0),
    precio_costo_unitario NUMERIC(10, 2) NOT NULL,
    precio_venta_sugerido NUMERIC(10, 2),
    subtotal NUMERIC(12, 2) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT true,
    creado_por VARCHAR(50) NOT NULL,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modificado_por VARCHAR(50),
    fecha_modificacion TIMESTAMP,
    CONSTRAINT fk_detalle_oc FOREIGN KEY (orden_compra_id) REFERENCES orden_compra_proveedor(id) ON DELETE CASCADE
);

-- 4. ÍNDICES DE OPTIMIZACIÓN
CREATE INDEX idx_sesion_carrito_usuario ON sesion_carrito_compra(usuario_id);
CREATE INDEX idx_sesion_carrito_proveedor ON sesion_carrito_compra(proveedor_id);
CREATE INDEX idx_orden_compra_proveedor ON orden_compra_proveedor(proveedor_id);
CREATE INDEX idx_orden_compra_sucursal ON orden_compra_proveedor(sucursal_id);
CREATE INDEX idx_orden_compra_estado ON orden_compra_proveedor(estado);
CREATE INDEX idx_detalle_orden_compra_orden ON detalle_orden_compra(orden_compra_id);

-- ==================================================================
-- FIN DE MIGRACIÓN V29
-- ==================================================================
