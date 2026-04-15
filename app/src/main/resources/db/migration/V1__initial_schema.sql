-- ==================================================================
-- FLYWAY MIGRATION V1: Initial Schema
-- NexooHub Almacén - Schema Completo
-- ==================================================================
-- Descripción: Creación inicial del esquema completo de base de datos
--              para el sistema de gestión de inventarios NexooHub.
--              Incluye todas las tablas, relaciones y datos semilla.
-- Autor: Sistema de Migración Automática
-- Fecha: 2026-03-05
-- ==================================================================

-- 1. CATÁLOGOS BASE Y FINANZAS
CREATE TABLE configuracion_financiera (
    id SERIAL PRIMARY KEY,
    gastos_fijos_mensuales NUMERIC(10, 2) DEFAULT 0.00,
    meta_ventas_mensual NUMERIC(10, 2) DEFAULT 0.00,
    margen_ganancia_base NUMERIC(5, 4) DEFAULT 0.3000,
    comision_tarjeta NUMERIC(5, 4) DEFAULT 0.0350,
    iva NUMERIC(5, 4) DEFAULT 0.1600,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_creacion VARCHAR(50),
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_actualizacion VARCHAR(50)
);

CREATE TABLE categoria (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_creacion VARCHAR(50),
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_actualizacion VARCHAR(50)
);

CREATE TABLE proveedor (
    id SERIAL PRIMARY KEY,
    nombre_empresa VARCHAR(255) NOT NULL,
    rfc VARCHAR(13),
    nombre_contacto VARCHAR(150),
    telefono VARCHAR(20),
    email VARCHAR(100),
    direccion TEXT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_creacion VARCHAR(50),
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_actualizacion VARCHAR(50)
);

CREATE TABLE tipo_cliente (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    descripcion TEXT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_creacion VARCHAR(50),
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_actualizacion VARCHAR(50)
);

CREATE TABLE moto (
    id SERIAL PRIMARY KEY,
    marca VARCHAR(100) NOT NULL,
    modelo VARCHAR(100) NOT NULL,
    cilindrada INTEGER,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_creacion VARCHAR(50),
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_actualizacion VARCHAR(50)
);

-- 2. SUCURSALES Y RH
CREATE TABLE sucursal (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    direccion TEXT,
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_creacion VARCHAR(50),
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_actualizacion VARCHAR(50)
);

CREATE TABLE empleado (
    id SERIAL PRIMARY KEY,
    usuario_id INTEGER UNIQUE,
    nombre VARCHAR(150) NOT NULL,
    apellidos VARCHAR(200),
    nombre_completo VARCHAR(200),
    curp VARCHAR(18) UNIQUE,
    rfc VARCHAR(13) UNIQUE,
    nss VARCHAR(15) UNIQUE,
    puesto VARCHAR(100) NOT NULL,
    departamento VARCHAR(50),
    salario_diario NUMERIC(12,4) DEFAULT 0.00,
    sucursal_id INTEGER NOT NULL REFERENCES sucursal(id),
    fecha_contratacion DATE DEFAULT CURRENT_DATE,
    fecha_ingreso DATE,
    activo BOOLEAN DEFAULT TRUE,
    estatus VARCHAR(20) DEFAULT 'ACTIVO',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_creacion VARCHAR(50),
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_actualizacion VARCHAR(50)
);

CREATE TABLE usuarios (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'ROLE_USER',
    empleado_id INTEGER,
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_creacion VARCHAR(50),
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_actualizacion VARCHAR(50)
);

-- 3. PRODUCTOS Y CLIENTES
CREATE TABLE cliente (
    id SERIAL PRIMARY KEY,
    tipo_cliente_id INTEGER REFERENCES tipo_cliente(id),
    nombre VARCHAR(255) NOT NULL,
    rfc VARCHAR(13),
    telefono VARCHAR(20),
    email VARCHAR(100),
    direccion_fiscal TEXT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_creacion VARCHAR(50),
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_actualizacion VARCHAR(50)
);

CREATE TABLE producto_maestro (
    sku_interno VARCHAR(50) PRIMARY KEY,
    sku_proveedor VARCHAR(50),
    nombre_comercial VARCHAR(200) NOT NULL,
    descripcion TEXT,
    marca VARCHAR(100),
    categoria_id INTEGER REFERENCES categoria(id),
    proveedor_id INTEGER REFERENCES proveedor(id),
    clave_sat VARCHAR(8),
    stock_minimo_global INTEGER DEFAULT 2,
    sensibilidad_precio VARCHAR(20) DEFAULT 'MEDIA',
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_creacion VARCHAR(50),
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_actualizacion VARCHAR(50)
);

CREATE TABLE historial_precio (
    id SERIAL PRIMARY KEY,
    sku_interno VARCHAR(50) REFERENCES producto_maestro(sku_interno),
    costo_base NUMERIC(10, 2) NOT NULL,
    precio_ponderado NUMERIC(10, 2) NOT NULL,
    precio_final_publico NUMERIC(10, 2) NOT NULL,
    precio_publico_proveedor NUMERIC(10, 2) DEFAULT 0.00,
    fecha_calculo TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_creacion VARCHAR(50)
);

CREATE TABLE precio_especial (
    id SERIAL PRIMARY KEY,
    sku_interno VARCHAR(50) REFERENCES producto_maestro(sku_interno),
    tipo_cliente_id INTEGER REFERENCES tipo_cliente(id),
    precio_fijo NUMERIC(10, 2) NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_creacion VARCHAR(50),
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_actualizacion VARCHAR(50)
);

CREATE TABLE compatibilidad_producto (
    id SERIAL PRIMARY KEY,
    sku_interno VARCHAR(50) REFERENCES producto_maestro(sku_interno),
    moto_id INTEGER REFERENCES moto(id),
    anio_inicio INTEGER,
    anio_fin INTEGER,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_creacion VARCHAR(50),
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_actualizacion VARCHAR(50)
);

CREATE TABLE inventario_sucursal (
    sucursal_id INTEGER REFERENCES sucursal(id),
    sku_interno VARCHAR(50) REFERENCES producto_maestro(sku_interno),
    stock_actual INTEGER NOT NULL DEFAULT 0,
    stock_minimo_sucursal INTEGER DEFAULT 0,
    costo_promedio_ponderado NUMERIC(10, 2) DEFAULT 0.00,
    ubicacion_pasillo VARCHAR(100),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_creacion VARCHAR(50),
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_actualizacion VARCHAR(50),
    PRIMARY KEY (sucursal_id, sku_interno)
);

-- 4. TRANSACCIONALES
CREATE TABLE compra (
    id SERIAL PRIMARY KEY,
    proveedor_id INTEGER REFERENCES proveedor(id),
    folio_factura_proveedor VARCHAR(100),
    sucursal_id INTEGER REFERENCES sucursal(id),
    total_compra NUMERIC(12, 2),
    fecha_compra TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_creacion VARCHAR(50)
);

CREATE TABLE detalle_compra (
    id SERIAL PRIMARY KEY,
    compra_id INTEGER REFERENCES compra(id),
    sku_interno VARCHAR(50) REFERENCES producto_maestro(sku_interno),
    cantidad INTEGER NOT NULL,
    costo_unitario_compra NUMERIC(10, 2) NOT NULL
);

CREATE TABLE venta (
    id SERIAL PRIMARY KEY,
    cliente_id INTEGER REFERENCES cliente(id),
    sucursal_id INTEGER REFERENCES sucursal(id),
    vendedor_id INTEGER REFERENCES usuarios(id),
    metodo_pago VARCHAR(50),
    total NUMERIC(10, 2) DEFAULT 0.00,
    fecha_venta TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE detalle_venta (
    id SERIAL PRIMARY KEY,
    venta_id INTEGER REFERENCES venta(id),
    sku_interno VARCHAR(50) REFERENCES producto_maestro(sku_interno),
    cantidad INTEGER NOT NULL,
    precio_unitario_venta NUMERIC(10, 2) NOT NULL
);

-- 5. AUDITORÍA DE MOVIMIENTOS
CREATE TABLE movimiento_inventario (
    id SERIAL PRIMARY KEY,
    sucursal_id INTEGER REFERENCES sucursal(id),
    sku_interno VARCHAR(50) REFERENCES producto_maestro(sku_interno),
    tipo_movimiento VARCHAR(50) NOT NULL,
    cantidad INTEGER NOT NULL,
    rastreo_id VARCHAR(50),
    comentarios TEXT,
    fecha_movimiento TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_creacion VARCHAR(50)
);

-- ==================================================================
-- 6. DATOS SEMILLA (SEED DATA)
-- ==================================================================

-- Configuración Financiera Base
INSERT INTO configuracion_financiera (id, margen_ganancia_base, iva, usuario_creacion)
VALUES (1, 0.3500, 0.1600, 'SISTEMA');

-- Categoría Base
INSERT INTO categoria (id, nombre, usuario_creacion)
VALUES (1, 'Refacciones Motor', 'SISTEMA');

-- Proveedor Base
INSERT INTO proveedor (id, nombre_empresa, usuario_creacion)
VALUES (1, 'Refaccionaria Mayoreo Moto S.A.', 'SISTEMA');

-- Tipos de Cliente
INSERT INTO tipo_cliente (id, nombre, usuario_creacion)
VALUES 
    (1, 'Público General', 'SISTEMA'),
    (2, 'Taller Mecánico', 'SISTEMA'),
    (3, 'Mayorista', 'SISTEMA');

-- Sucursal Matriz
INSERT INTO sucursal (id, nombre, activo, usuario_creacion)
VALUES (1, 'Sucursal Matriz', true, 'SISTEMA');

-- Empleado Administrador
INSERT INTO empleado (id, nombre, apellidos, puesto, sucursal_id, usuario_creacion)
VALUES (1, 'Administrador', 'Sistema', 'Administrador General', 1, 'SISTEMA');

-- Usuario Administrador (password: admin123)
-- Hash BCrypt: $2a$10$k/mIluuONgJLE8efmX6Cse4/k8aUv5dvUqsCjJmxXKELm6ZMPZqsm
INSERT INTO usuarios (id, username, password, role, empleado_id, usuario_creacion)
VALUES (1, 'admin', '$2a$10$k/mIluuONgJLE8efmX6Cse4/k8aUv5dvUqsCjJmxXKELm6ZMPZqsm', 'ROLE_ADMIN', 1, 'SISTEMA');

-- Cliente Genérico para Venta Mostrador
INSERT INTO cliente (id, tipo_cliente_id, nombre, rfc, usuario_creacion)
VALUES (1, 1, 'Venta Mostrador', 'XAXX010101000', 'SISTEMA');

-- ==================================================================
-- 7. REINICIO DE SECUENCIAS
-- ==================================================================
SELECT setval('configuracion_financiera_id_seq', 1, true);
SELECT setval('categoria_id_seq', 1, true);
SELECT setval('proveedor_id_seq', 1, true);
SELECT setval('tipo_cliente_id_seq', 3, true);
SELECT setval('sucursal_id_seq', 1, true);
SELECT setval('empleado_id_seq', 1, true);
SELECT setval('usuarios_id_seq', 1, true);
SELECT setval('cliente_id_seq', 1, true);

-- ==================================================================
-- 8. ÍNDICES PARA OPTIMIZACIÓN DE CONSULTAS
-- ==================================================================

-- Índices para búsquedas frecuentes en ventas
CREATE INDEX idx_venta_sucursal_fecha ON venta(sucursal_id, fecha_venta DESC);
CREATE INDEX idx_venta_cliente ON venta(cliente_id);
CREATE INDEX idx_venta_vendedor ON venta(vendedor_id);

-- Índices para búsquedas frecuentes en compras
CREATE INDEX idx_compra_proveedor_fecha ON compra(proveedor_id, fecha_compra DESC);
CREATE INDEX idx_compra_sucursal ON compra(sucursal_id);

-- Índices para inventario
CREATE INDEX idx_inventario_sku ON inventario_sucursal(sku_interno);
CREATE INDEX idx_inventario_sucursal ON inventario_sucursal(sucursal_id);

-- Índices para historial de precios
CREATE INDEX idx_historial_precio_sku_fecha ON historial_precio(sku_interno, fecha_calculo DESC);

-- Índices para precios especiales
CREATE INDEX idx_precio_especial_sku_tipo ON precio_especial(sku_interno, tipo_cliente_id);

-- Índices para productos
CREATE INDEX idx_producto_categoria ON producto_maestro(categoria_id);
CREATE INDEX idx_producto_proveedor ON producto_maestro(proveedor_id);
CREATE INDEX idx_producto_nombre ON producto_maestro(nombre_comercial);
CREATE INDEX idx_producto_marca ON producto_maestro(marca);
CREATE INDEX idx_producto_activo ON producto_maestro(activo);

-- Índices para proveedores (búsqueda por nombre)
CREATE INDEX idx_proveedor_nombre ON proveedor(nombre_empresa);

-- Índices para categorías (búsqueda por nombre)
CREATE INDEX idx_categoria_nombre ON categoria(nombre);

-- Índices para motos (búsqueda avanzada)
CREATE INDEX idx_moto_marca ON moto(marca);
CREATE INDEX idx_moto_modelo ON moto(modelo);
CREATE INDEX idx_moto_cilindrada ON moto(cilindrada);
CREATE INDEX idx_moto_marca_modelo_cilindrada ON moto(marca, modelo, cilindrada);

-- Índices para compatibilidad (búsquedas de productos por moto)
CREATE INDEX idx_compatibilidad_sku ON compatibilidad_producto(sku_interno);
CREATE INDEX idx_compatibilidad_moto ON compatibilidad_producto(moto_id);
CREATE INDEX idx_compatibilidad_anios ON compatibilidad_producto(anio_inicio, anio_fin);

-- Índices para movimientos de inventario
CREATE INDEX idx_movimiento_sucursal_fecha ON movimiento_inventario(sucursal_id, fecha_movimiento DESC);
CREATE INDEX idx_movimiento_rastreo ON movimiento_inventario(rastreo_id);

-- ==================================================================
-- FIN DE MIGRACIÓN V1
-- ==================================================================
