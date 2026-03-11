-- ============================================================================
-- SCRIPT MAESTRO NEXOOHUB v5.0 (CONSOLIDADO COMPLETO)
-- ============================================================================
-- Descripción: Script completo para inicialización de base de datos
-- Base de datos: PostgreSQL 15+ (compatible con JPA/Hibernate)
-- Autor: NexooHub Development Team
-- Fecha: 2026-03-09
-- Versión: 5.0.0
-- 
-- CONTENIDO CONSOLIDADO:
-- - Schema base (configuración, catálogos, productos, clientes, ventas)
-- - Módulo de Cotizaciones (Presupuestos)
-- - Módulo de Reservas/Apartados
-- - Módulo de Control de Crédito
-- - Módulo de Alertas de Lento Movimiento
-- - Módulo de Comisiones para Vendedores (V2)
-- - Módulo de Predicción de Demanda (V3)
-- ============================================================================

-- ==================================================================
-- SCRIPT MAESTRO NEXOOHUB v4.0 (CONSOLIDADO + MÃ“DULOS COMPLETOS)
-- ==================================================================
-- DescripciÃ³n: Script completo para inicializaciÃ³n de base de datos
-- Base de datos: PostgreSQL 15+ (compatible con JPA/Hibernate)
-- Autor: NexooHub Development Team
-- Fecha: 2026-03-09
-- VersiÃ³n: 4.0.0
-- 
-- CONTENIDO:
-- - Schema base (configuraciÃ³n, catÃ¡logos, productos, clientes)
-- - MÃ³dulo de Compras y Ventas
-- - MÃ³dulo de Devoluciones
-- - MÃ³dulo de Cotizaciones (Presupuestos)
-- - MÃ³dulo de Reservas/Apartados
-- - MÃ³dulo de Control de CrÃ©dito
-- - MÃ³dulo de Alertas de Lento Movimiento
-- - Stored Procedures, Triggers, Views, Events
-- ==================================================================

-- ==================================================================
-- SECCIÃ“N 0: LIMPIEZA PROFUNDA
-- ==================================================================
-- Eliminar todos los objetos existentes en orden de dependencia

-- MÃ³dulos nuevos (orden inverso de dependencias)
DROP TABLE IF EXISTS analisis_abc CASCADE;
DROP TABLE IF EXISTS prediccion_demanda CASCADE;
DROP TABLE IF EXISTS comision CASCADE;
DROP TABLE IF EXISTS regla_comision CASCADE;
DROP TABLE IF EXISTS alerta_lento_movimiento CASCADE;
DROP TABLE IF EXISTS historial_credito CASCADE;
DROP TABLE IF EXISTS limite_credito CASCADE;
DROP TABLE IF EXISTS reserva CASCADE;
DROP TABLE IF EXISTS detalle_cotizacion CASCADE;
DROP TABLE IF EXISTS cotizacion CASCADE;

-- Tablas transaccionales principales
DROP TABLE IF EXISTS detalle_devolucion CASCADE;
DROP TABLE IF EXISTS devolucion CASCADE;
DROP TABLE IF EXISTS detalle_venta CASCADE;
DROP TABLE IF EXISTS venta CASCADE;
DROP TABLE IF EXISTS detalle_compra CASCADE;
DROP TABLE IF EXISTS compra CASCADE;
DROP TABLE IF EXISTS historial_precio CASCADE;
DROP TABLE IF EXISTS precio_especial CASCADE;
DROP TABLE IF EXISTS compatibilidad_producto CASCADE;
DROP TABLE IF EXISTS inventario_sucursal CASCADE;
DROP TABLE IF EXISTS producto_maestro CASCADE;
DROP TABLE IF EXISTS cliente CASCADE;
DROP TABLE IF EXISTS usuario CASCADE;
DROP TABLE IF EXISTS empleado CASCADE;
DROP TABLE IF EXISTS sucursal CASCADE;
DROP TABLE IF EXISTS moto CASCADE;
DROP TABLE IF EXISTS tipo_cliente CASCADE;
DROP TABLE IF EXISTS proveedor CASCADE;
DROP TABLE IF EXISTS categoria CASCADE;
DROP TABLE IF EXISTS configuracion_financiera CASCADE;

-- 1. CATÃLOGOS BASE Y FINANZAS
CREATE TABLE configuracion_financiera (
    id SERIAL PRIMARY KEY,
    gastos_fijos_mensuales NUMERIC(10, 2) DEFAULT 0.00,
    meta_ventas_mensual NUMERIC(10, 2) DEFAULT 0.00,
    margen_ganancia_base NUMERIC(5, 4) DEFAULT 0.3000,
    comision_tarjeta NUMERIC(5, 4) DEFAULT 0.0350,
    iva NUMERIC(5, 4) DEFAULT 0.1600,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP, usuario_creacion VARCHAR(50),
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP, usuario_actualizacion VARCHAR(50)
);

CREATE TABLE categoria (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP, usuario_creacion VARCHAR(50),
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP, usuario_actualizacion VARCHAR(50)
);

CREATE TABLE proveedor (
    id SERIAL PRIMARY KEY,
    nombre_empresa VARCHAR(255) NOT NULL,
    rfc VARCHAR(13),
    nombre_contacto VARCHAR(150),
    telefono VARCHAR(20),
    email VARCHAR(100),
    direccion TEXT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP, usuario_creacion VARCHAR(50),
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP, usuario_actualizacion VARCHAR(50)
);

CREATE TABLE tipo_cliente (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    descripcion TEXT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP, usuario_creacion VARCHAR(50),
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP, usuario_actualizacion VARCHAR(50)
);

CREATE TABLE moto (
    id SERIAL PRIMARY KEY,
    marca VARCHAR(100) NOT NULL,
    modelo VARCHAR(100) NOT NULL,
    cilindrada INTEGER,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP, usuario_creacion VARCHAR(50),
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP, usuario_actualizacion VARCHAR(50)
);

-- 2. SUCURSALES Y RH
CREATE TABLE sucursal (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    direccion TEXT,
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP, usuario_creacion VARCHAR(50),
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP, usuario_actualizacion VARCHAR(50)
);

CREATE TABLE empleado (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    puesto VARCHAR(100),
    fecha_contratacion DATE DEFAULT CURRENT_DATE,
    sucursal_id INTEGER REFERENCES sucursal(id),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP, usuario_creacion VARCHAR(50),
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP, usuario_actualizacion VARCHAR(50)
);

CREATE TABLE usuario (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'ROLE_USER', -- Usamos 'role' para que coincida con tu Entity
    empleado_id INTEGER REFERENCES empleado(id),
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP, usuario_creacion VARCHAR(50),
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP, usuario_actualizacion VARCHAR(50)
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
    bloqueado BOOLEAN DEFAULT FALSE,
    saldo_pendiente NUMERIC(10, 2) DEFAULT 0.00,
    motivo_bloqueo VARCHAR(500),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP, usuario_creacion VARCHAR(50),
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP, usuario_actualizacion VARCHAR(50)
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
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP, usuario_creacion VARCHAR(50),
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP, usuario_actualizacion VARCHAR(50)
);

CREATE TABLE historial_precio (
    id SERIAL PRIMARY KEY,
    sku_interno VARCHAR(50) REFERENCES producto_maestro(sku_interno),
    costo_base NUMERIC(10, 2) NOT NULL,
    precio_ponderado NUMERIC(10, 2) NOT NULL,
    precio_final_publico NUMERIC(10, 2) NOT NULL,
    precio_publico_proveedor NUMERIC(10, 2) DEFAULT 0.00,
    precio_anterior NUMERIC(10, 2),
    porcentaje_cambio NUMERIC(5, 2),
    razon_cambio VARCHAR(500),
    fecha_calculo TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_creacion VARCHAR(50)
);

-- NUEVA: TABLA DE PRECIOS ESPECIALES
CREATE TABLE precio_especial (
    id SERIAL PRIMARY KEY,
    sku_interno VARCHAR(50) REFERENCES producto_maestro(sku_interno),
    tipo_cliente_id INTEGER REFERENCES tipo_cliente(id),
    precio_fijo NUMERIC(10, 2) NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP, usuario_creacion VARCHAR(50),
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP, usuario_actualizacion VARCHAR(50)
);

CREATE TABLE compatibilidad_producto (
    id SERIAL PRIMARY KEY,
    sku_interno VARCHAR(50) REFERENCES producto_maestro(sku_interno),
    moto_id INTEGER REFERENCES moto(id),
    anio_inicio INTEGER,
    anio_fin INTEGER,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP, usuario_creacion VARCHAR(50),
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP, usuario_actualizacion VARCHAR(50)
);

CREATE TABLE inventario_sucursal (
    sucursal_id INTEGER REFERENCES sucursal(id),
    sku_interno VARCHAR(50) REFERENCES producto_maestro(sku_interno),
    stock_actual INTEGER NOT NULL DEFAULT 0,
    stock_minimo_sucursal INTEGER DEFAULT 0,
    costo_promedio_ponderado NUMERIC(10, 2) DEFAULT 0.00,
    ubicacion_pasillo VARCHAR(100),
    fecha_caducidad DATE,
    lote VARCHAR(100),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP, usuario_creacion VARCHAR(50),
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP, usuario_actualizacion VARCHAR(50),
    PRIMARY KEY (sucursal_id, sku_interno)
);

-- 4. TRANSACCIONALES
CREATE TABLE compra (
    id SERIAL PRIMARY KEY,
    proveedor_id INTEGER REFERENCES proveedor(id),
    folio_factura_proveedor VARCHAR(100),
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
    vendedor_id INTEGER REFERENCES usuario(id),
    metodo_pago VARCHAR(50),
    total NUMERIC(10, 2) DEFAULT 0.00,
    fecha_venta TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE detalle_venta (
    id SERIAL PRIMARY KEY,
    venta_id INTEGER REFERENCES venta(id),
    sku_interno VARCHAR(50) REFERENCES producto_maestro(sku_interno),
    cantidad INTEGER NOT NULL,
    precio_unitario_venta NUMERIC(10, 2) NOT NULL,
    descuento_especial NUMERIC(10, 2) DEFAULT 0.00,
    porcentaje_descuento NUMERIC(5, 2) DEFAULT 0.00
);

-- 5. DEVOLUCIONES
CREATE TABLE devolucion (
    id SERIAL PRIMARY KEY,
    venta_id INTEGER REFERENCES venta(id),
    sucursal_id INTEGER REFERENCES sucursal(id),
    motivo VARCHAR(500) NOT NULL,
    total_devuelto NUMERIC(10, 2) NOT NULL,
    metodo_reembolso VARCHAR(50) NOT NULL, -- EFECTIVO, TARJETA, NOTA_CREDITO
    fecha_devolucion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_autorizo VARCHAR(100)
);

CREATE TABLE detalle_devolucion (
    id SERIAL PRIMARY KEY,
    devolucion_id INTEGER REFERENCES devolucion(id),
    sku_interno VARCHAR(50) REFERENCES producto_maestro(sku_interno),
    cantidad INTEGER NOT NULL,
    precio_unitario NUMERIC(10, 2) NOT NULL,
    subtotal NUMERIC(10, 2) NOT NULL,
    motivo_item VARCHAR(300)
);

-- ==================================================================
-- 1. POBLACIÃ“N DE DATOS SEMILLA
-- ==================================================================
INSERT INTO configuracion_financiera (id, margen_ganancia_base, iva, usuario_creacion) VALUES (1, 0.3500, 0.1600, 'SISTEMA');
INSERT INTO categoria (id, nombre, usuario_creacion) VALUES (1, 'Refacciones Motor', 'SISTEMA');
INSERT INTO proveedor (id, nombre_empresa, usuario_creacion) VALUES (1, 'Refaccionaria Mayoreo Moto S.A.', 'SISTEMA');
INSERT INTO tipo_cliente (id, nombre, usuario_creacion) VALUES (1, 'PÃºblico General', 'SISTEMA'), (2, 'Taller MecÃ¡nico', 'SISTEMA');
INSERT INTO sucursal (id, nombre, activo, usuario_creacion) VALUES (1, 'Sucursal Matriz', true, 'SISTEMA');
INSERT INTO empleado (id, nombre, sucursal_id, usuario_creacion) VALUES (1, 'Administrador NexooHub', 1, 'SISTEMA');
INSERT INTO usuario (id, username, password, role, empleado_id, usuario_creacion) 
VALUES (1, 'admin', '$2a$10$k/mIluuONgJLE8efmX6Cse4/k8aUv5dvUqsCjJmxXKELm6ZMPZqsm', 'ADMIN', 1, 'SISTEMA');
INSERT INTO cliente (id, tipo_cliente_id, nombre, rfc, usuario_creacion) VALUES (1, 1, 'Venta Mostrador', 'XAXX010101000', 'SISTEMA');

-- REINICIO DE SECUENCIAS
SELECT setval('configuracion_financiera_id_seq', 1);
SELECT setval('categoria_id_seq', 1);
SELECT setval('proveedor_id_seq', 1);
SELECT setval('tipo_cliente_id_seq', 2);
SELECT setval('sucursal_id_seq', 1);
SELECT setval('empleado_id_seq', 1);
SELECT setval('usuario_id_seq', 1);
SELECT setval('cliente_id_seq', 1);


-- ============================================================================
-- MÓDULO: COTIZACIONES (PRESUPUESTOS)
-- ============================================================================

-- ============================================================================
-- SCRIPT SQL: GestiÃ³n de Cotizaciones (Presupuestos)
-- DescripciÃ³n: Crea tablas y objetos de BD para el mÃ³dulo de cotizaciones
-- VersiÃ³n: 1.0.0
-- Fecha: 2026-03-09
-- Autor: NexooHub Development Team
-- ============================================================================

-- ==================================================
-- TABLA: cotizacion
-- ==================================================
-- Almacena las cotizaciones (presupuestos) generadas para clientes
-- Una cotizacion puede convertirse en una venta real

CREATE TABLE IF NOT EXISTS cotizacion (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    folio VARCHAR(50) NOT NULL UNIQUE,
    cliente_id INT NOT NULL,
    sucursal_id INT NOT NULL,
    vendedor_id INT,
    estado VARCHAR(20) NOT NULL DEFAULT 'BORRADOR' CHECK (estado IN ('BORRADOR', 'ENVIADA', 'ACEPTADA', 'RECHAZADA', 'VENCIDA', 'CONVERTIDA')),
    fecha_cotizacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_validez DATE NOT NULL,
    total DECIMAL(15,2) NOT NULL DEFAULT 0.00 CHECK (total >= 0),
    subtotal DECIMAL(15,2) DEFAULT 0.00 CHECK (subtotal >= 0),
    iva DECIMAL(15,2) DEFAULT 0.00 CHECK (iva >= 0),
    descuento_total DECIMAL(15,2) DEFAULT 0.00 CHECK (descuento_total >= 0),
    notas VARCHAR(1000),
    terminos_condiciones VARCHAR(2000),
    observaciones_internas VARCHAR(1000),
    fecha_aceptacion DATETIME,
    fecha_rechazo DATETIME,
    motivo_rechazo VARCHAR(500),
    venta_id INT,
    fecha_conversion DATETIME,
    
    -- Claves forÃ¡neas
    FOREIGN KEY (cliente_id) REFERENCES cliente(id) ON DELETE RESTRICT,
    FOREIGN KEY (sucursal_id) REFERENCES sucursales(id) ON DELETE RESTRICT,
    FOREIGN KEY (vendedor_id) REFERENCES empleado(id) ON DELETE SET NULL,
    FOREIGN KEY (venta_id) REFERENCES venta(id) ON DELETE SET NULL,
    
    -- Constraints adicionales
    CONSTRAINT chk_fechas_logicas CHECK (fecha_validez >= DATE(fecha_cotizacion)),
    CONSTRAINT chk_aceptacion CHECK (estado != 'ACEPTADA' OR fecha_aceptacion IS NOT NULL),
    CONSTRAINT chk_rechazo CHECK (estado != 'RECHAZADA' OR (fecha_rechazo IS NOT NULL AND motivo_rechazo IS NOT NULL)),
    CONSTRAINT chk_conversion CHECK (estado != 'CONVERTIDA' OR (venta_id IS NOT NULL AND fecha_conversion IS NOT NULL))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==================================================
-- TABLA: detalle_cotizacion
-- ==================================================
-- Almacena las lÃ­neas de detalle de cada cotizaciÃ³n
-- Cada lÃ­nea corresponde a un producto con cantidad, precio y descuentos

CREATE TABLE IF NOT EXISTS detalle_cotizacion (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cotizacion_id BIGINT NOT NULL,
    sku_interno VARCHAR(50) NOT NULL,
    cantidad INT NOT NULL CHECK (cantidad > 0),
    precio_unitario DECIMAL(15,2) NOT NULL CHECK (precio_unitario > 0),
    descuento_especial DECIMAL(15,2) DEFAULT 0.00 CHECK (descuento_especial >= 0),
    porcentaje_descuento DECIMAL(5,2) DEFAULT 0.00 CHECK (porcentaje_descuento >= 0 AND porcentaje_descuento <= 100),
    notas VARCHAR(500),
    
    -- Claves forÃ¡neas
    FOREIGN KEY (cotizacion_id) REFERENCES cotizacion(id) ON DELETE CASCADE,
    FOREIGN KEY (sku_interno) REFERENCES producto_maestro(sku_interno) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==================================================
-- ÃNDICES PARA OPTIMIZACIÃ“N DE QUERIES
-- ==================================================

-- Ãndices en tabla cotizacion
CREATE INDEX idx_cot_folio ON cotizacion(folio);
CREATE INDEX idx_cot_cliente ON cotizacion(cliente_id);
CREATE INDEX idx_cot_sucursal ON cotizacion(sucursal_id);
CREATE INDEX idx_cot_vendedor ON cotizacion(vendedor_id);
CREATE INDEX idx_cot_estado ON cotizacion(estado);
CREATE INDEX idx_cot_fecha_cotizacion ON cotizacion(fecha_cotizacion DESC);
CREATE INDEX idx_cot_fecha_validez ON cotizacion(fecha_validez);
CREATE INDEX idx_cot_venta ON cotizacion(venta_id);

-- Ãndices compuestos para consultas frecuentes
CREATE INDEX idx_cot_sucursal_estado ON cotizacion(sucursal_id, estado);
CREATE INDEX idx_cot_cliente_estado ON cotizacion(cliente_id, estado);
CREATE INDEX idx_cot_estado_fecha ON cotizacion(estado, fecha_validez);
CREATE INDEX idx_cot_sucursal_fecha ON cotizacion(sucursal_id, fecha_cotizacion);

-- Ãndices en tabla detalle_cotizacion
CREATE INDEX idx_det_cot_cotizacion ON detalle_cotizacion(cotizacion_id);
CREATE INDEX idx_det_cot_producto ON detalle_cotizacion(sku_interno);

-- Ãndice para reportes de productos mÃ¡s cotizados
CREATE INDEX idx_det_cot_producto_cantidad ON detalle_cotizacion(sku_interno, cantidad);

-- ==================================================
-- COMENTARIOS EN LA BASE DE DATOS
-- ==================================================

COMMENT ON TABLE cotizacion IS 'Cotizaciones o presupuestos generados para clientes. Pueden convertirse en ventas reales.';
COMMENT ON COLUMN cotizacion.folio IS 'Folio Ãºnico con formato COT-YYYY-NNNN generado automÃ¡ticamente';
COMMENT ON COLUMN cotizacion.estado IS 'BORRADOR: editable | ENVIADA: enviada al cliente | ACEPTADA: aceptada por cliente | RECHAZADA: rechazada | VENCIDA: fecha validez expirada | CONVERTIDA: ya es venta';
COMMENT ON COLUMN cotizacion.fecha_validez IS 'Fecha hasta la cual la cotizaciÃ³n es vÃ¡lida. DespuÃ©s se marca como VENCIDA';
COMMENT ON COLUMN cotizacion.total IS 'Total de la cotizaciÃ³n incluyendo IVA y descuentos';
COMMENT ON COLUMN cotizacion.venta_id IS 'ID de la venta creada cuando la cotizaciÃ³n se convierte';
COMMENT ON COLUMN cotizacion.observaciones_internas IS 'Observaciones no visibles al cliente (uso interno)';

COMMENT ON TABLE detalle_cotizacion IS 'LÃ­neas de detalle de cada cotizaciÃ³n con productos, cantidades y precios';
COMMENT ON COLUMN detalle_cotizacion.descuento_especial IS 'Descuento en pesos aplicado a esta lÃ­nea';
COMMENT ON COLUMN detalle_cotizacion.porcentaje_descuento IS 'Porcentaje de descuento (alternativo al descuento especial)';

-- ==================================================
-- DATOS DE PRUEBA (Opcional - Solo para desarrollo)
-- ==================================================

-- Insertar cotizaciones de ejemplo
-- Nota: Asume existencia de clientes, sucursales, empleados y productos

-- CotizaciÃ³n BORRADOR
INSERT INTO cotizacion (
    folio, cliente_id, sucursal_id, vendedor_id, estado,
    fecha_cotizacion, fecha_validez, subtotal, iva, descuento_total, total,
    notas, terminos_condiciones
) VALUES (
    'COT-2026-0001', 1, 1, 1, 'BORRADOR',
    NOW(), DATE_ADD(CURDATE(), INTERVAL 15 DAY),
    30000.00, 4800.00, 500.00, 34300.00,
    'Cliente solicita descuento por volumen',
    'VÃ¡lido por 15 dÃ­as. Precios sujetos a disponibilidad de stock.'
);

-- Detalles de la cotizaciÃ³n BORRADOR
INSERT INTO detalle_cotizacion (
    cotizacion_id, sku_interno, cantidad, precio_unitario, descuento_especial, notas
) VALUES 
    (LAST_INSERT_ID(), 'MOT-YAM-001', 2, 15000.00, 500.00, 'Color negro preferido'),
    (LAST_INSERT_ID(), 'ACC-HLM-001', 1, 850.00, 0.00, NULL);

-- CotizaciÃ³n ENVIADA
INSERT INTO cotizacion (
    folio, cliente_id, sucursal_id, vendedor_id, estado,
    fecha_cotizacion, fecha_validez, subtotal, iva, descuento_total, total,
    notas, terminos_condiciones
) VALUES (
    'COT-2026-0002', 2, 1, 1, 'ENVIADA',
    DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_ADD(CURDATE(), INTERVAL 13 DAY),
    25000.00, 4000.00, 0.00, 29000.00,
    'CotizaciÃ³n enviada por correo electrÃ³nico',
    'Precios incluyen IVA. Tiempo de entrega: 5 dÃ­as hÃ¡biles.'
);

-- Detalles de la cotizaciÃ³n ENVIADA
INSERT INTO detalle_cotizacion (
    cotizacion_id, sku_interno, cantidad, precio_unitario
) VALUES 
    (LAST_INSERT_ID(), 'MOT-CRF-002', 1, 25000.00);

-- CotizaciÃ³n ACEPTADA (lista para convertir)
INSERT INTO cotizacion (
    folio, cliente_id, sucursal_id, vendedor_id, estado,
    fecha_cotizacion, fecha_validez, fecha_aceptacion,
    subtotal, iva, descuento_total, total,
    notas, terminos_condiciones
) VALUES (
    'COT-2026-0003', 3, 1, 2, 'ACEPTADA',
    DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_ADD(CURDATE(), INTERVAL 10 DAY),
    DATE_SUB(NOW(), INTERVAL 1 DAY),
    18000.00, 2880.00, 300.00, 20580.00,
    'Cliente aceptÃ³ cotizaciÃ³n vÃ­a telefÃ³nica',
    'Entrega inmediata. GarantÃ­a de 1 aÃ±o.'
);

-- Detalles de la cotizaciÃ³n ACEPTADA
INSERT INTO detalle_cotizacion (
    cotizacion_id, sku_interno, cantidad, precio_unitario, descuento_especial
) VALUES 
    (LAST_INSERT_ID(), 'REF-BRK-001', 3, 6000.00, 300.00);

-- CotizaciÃ³n RECHAZADA
INSERT INTO cotizacion (
    folio, cliente_id, sucursal_id, vendedor_id, estado,
    fecha_cotizacion, fecha_validez, fecha_rechazo, motivo_rechazo,
    subtotal, iva, descuento_total, total,
    notas
) VALUES (
    'COT-2026-0004', 4, 2, 3, 'RECHAZADA',
    DATE_SUB(NOW(), INTERVAL 7 DAY), DATE_SUB(CURDATE(), INTERVAL 1 DAY),
    DATE_SUB(NOW(), INTERVAL 3 DAY), 'Cliente encontrÃ³ mejor precio con la competencia',
    35000.00, 5600.00, 0.00, 40600.00,
    'CotizaciÃ³n rechazada. Cliente mencionÃ³ competidor con precio 5% menor.'
);

-- CotizaciÃ³n VENCIDA
INSERT INTO cotizacion (
    folio, cliente_id, sucursal_id, vendedor_id, estado,
    fecha_cotizacion, fecha_validez, subtotal, iva, total
) VALUES (
    'COT-2025-0150', 5, 1, 1, 'VENCIDA',
    DATE_SUB(NOW(), INTERVAL 30 DAY), DATE_SUB(CURDATE(), INTERVAL 5 DAY),
    20000.00, 3200.00, 23200.00
);

-- CotizaciÃ³n CONVERTIDA (ya es venta)
INSERT INTO cotizacion (
    folio, cliente_id, sucursal_id, vendedor_id, estado,
    fecha_cotizacion, fecha_validez, fecha_aceptacion, fecha_conversion, venta_id,
    subtotal, iva, descuento_total, total,
    notas
) VALUES (
    'COT-2026-0005', 1, 1, 1, 'CONVERTIDA',
    DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_ADD(CURDATE(), INTERVAL 5 DAY),
    DATE_SUB(NOW(), INTERVAL 8 DAY), DATE_SUB(NOW(), INTERVAL 7 DAY), 1,
    50000.00, 8000.00, 1000.00, 57000.00,
    'CotizaciÃ³n convertida exitosamente a venta. Factura emitida.'
);

-- ==================================================
-- STORED PROCEDURES
-- ==================================================

DELIMITER $$

-- ----------------------------------------------
-- Procedimiento: Generar Folio AutomÃ¡tico
-- ----------------------------------------------
-- Genera el siguiente folio disponible para el aÃ±o actual
-- Formato: COT-YYYY-NNNN

CREATE PROCEDURE sp_generar_folio_cotizacion(
    OUT p_folio VARCHAR(50)
)
BEGIN
    DECLARE v_anio INT;
    DECLARE v_ultimo_numero INT;
    DECLARE v_siguiente_numero INT;
    
    SET v_anio = YEAR(CURDATE());
    
    -- Obtener el Ãºltimo nÃºmero del aÃ±o actual
    SELECT COALESCE(
        MAX(CAST(SUBSTRING(folio, 10) AS UNSIGNED)), 
        0
    ) INTO v_ultimo_numero
    FROM cotizacion
    WHERE folio LIKE CONCAT('COT-', v_anio, '-%');
    
    SET v_siguiente_numero = v_ultimo_numero + 1;
    
    -- Generar folio con formato COT-YYYY-NNNN (padding de 4 dÃ­gitos)
    SET p_folio = CONCAT('COT-', v_anio, '-', LPAD(v_siguiente_numero, 4, '0'));
END$$

-- ----------------------------------------------
-- Procedimiento: Marcar Cotizaciones Vencidas
-- ----------------------------------------------
-- Busca cotizaciones cuya fecha de validez ya pasÃ³
-- y las marca como VENCIDAS automÃ¡ticamente

CREATE PROCEDURE sp_marcar_cotizaciones_vencidas()
BEGIN
    DECLARE v_registros_actualizados INT DEFAULT 0;
    
    -- Actualizar cotizaciones vencidas
    UPDATE cotizacion
    SET estado = 'VENCIDA'
    WHERE fecha_validez < CURDATE()
      AND estado NOT IN ('CONVERTIDA', 'RECHAZADA', 'VENCIDA');
    
    SET v_registros_actualizados = ROW_COUNT();
    
    -- Retornar cantidad de registros actualizados
    SELECT v_registros_actualizados AS cotizaciones_vencidas;
END$$

-- ----------------------------------------------
-- Procedimiento: Obtener EstadÃ­sticas de Cotizaciones
-- ----------------------------------------------
-- Retorna mÃ©tricas generales del proceso de cotizaciÃ³n

CREATE PROCEDURE sp_estadisticas_cotizaciones()
BEGIN
    SELECT 
        COUNT(*) AS total_cotizaciones,
        SUM(CASE WHEN estado = 'BORRADOR' THEN 1 ELSE 0 END) AS borradores,
        SUM(CASE WHEN estado = 'ENVIADA' THEN 1 ELSE 0 END) AS enviadas,
        SUM(CASE WHEN estado = 'ACEPTADA' THEN 1 ELSE 0 END) AS aceptadas,
        SUM(CASE WHEN estado = 'RECHAZADA' THEN 1 ELSE 0 END) AS rechazadas,
        SUM(CASE WHEN estado = 'VENCIDA' THEN 1 ELSE 0 END) AS vencidas,
        SUM(CASE WHEN estado = 'CONVERTIDA' THEN 1 ELSE 0 END) AS convertidas,
        SUM(CASE WHEN estado = 'BORRADOR' THEN total ELSE 0 END) AS valor_borradores,
        SUM(CASE WHEN estado = 'ENVIADA' THEN total ELSE 0 END) AS valor_enviadas,
        SUM(CASE WHEN estado = 'ACEPTADA' THEN total ELSE 0 END) AS valor_aceptadas,
        SUM(CASE WHEN estado = 'CONVERTIDA' THEN total ELSE 0 END) AS valor_convertidas,
        ROUND(
            (SUM(CASE WHEN estado = 'CONVERTIDA' THEN 1 ELSE 0 END) * 100.0 / NULLIF(COUNT(*), 0)), 
            2
        ) AS tasa_conversion_porcentaje
    FROM cotizacion;
END$$

-- ----------------------------------------------
-- Procedimiento: Productos MÃ¡s Cotizados
-- ----------------------------------------------
-- Retorna los productos mÃ¡s cotizados en un periodo

CREATE PROCEDURE sp_productos_mas_cotizados(
    IN p_fecha_inicio DATE,
    IN p_fecha_fin DATE,
    IN p_limite INT
)
BEGIN
    SELECT 
        dc.sku_interno,
        pm.nombre_comercial,
        SUM(dc.cantidad) AS cantidad_total_cotizada,
        COUNT(DISTINCT dc.cotizacion_id) AS veces_cotizado,
        SUM(dc.cantidad * dc.precio_unitario) AS valor_total_cotizado
    FROM detalle_cotizacion dc
    INNER JOIN cotizacion c ON dc.cotizacion_id = c.id
    INNER JOIN producto_maestro pm ON dc.sku_interno = pm.sku_interno
    WHERE c.fecha_cotizacion BETWEEN p_fecha_inicio AND p_fecha_fin
      AND c.estado IN ('ENVIADA', 'ACEPTADA', 'CONVERTIDA')
    GROUP BY dc.sku_interno, pm.nombre_comercial
    ORDER BY cantidad_total_cotizada DESC
    LIMIT p_limite;
END$$

DELIMITER ;

-- ==================================================
-- TRIGGERS
-- ==================================================

DELIMITER $$

-- ----------------------------------------------
-- Trigger: Validar Estado Antes de Actualizar
-- ----------------------------------------------
-- Evita transiciones de estado invÃ¡lidas

CREATE TRIGGER trg_cotizacion_validar_estado
BEFORE UPDATE ON cotizacion
FOR EACH ROW
BEGIN
    -- No permitir ediciones si ya estÃ¡ CONVERTIDA
    IF OLD.estado = 'CONVERTIDA' AND NEW.estado != 'CONVERTIDA' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'No se puede cambiar el estado de una cotizaciÃ³n ya convertida en venta';
    END IF;
    
    -- Validar transiciÃ³n BORRADOR -> ENVIADA
    IF OLD.estado = 'BORRADOR' AND NEW.estado NOT IN ('BORRADOR', 'ENVIADA', 'RECHAZADA') THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Desde BORRADOR solo se puede cambiar a ENVIADA o RECHAZADA';
    END IF;
    
    -- Validar transiciÃ³n ENVIADA -> ACEPTADA
    IF OLD.estado = 'ENVIADA' AND NEW.estado = 'ACEPTADA' THEN
        SET NEW.fecha_aceptacion = NOW();
    END IF;
    
    -- Validar motivo al rechazar
    IF NEW.estado = 'RECHAZADA' AND (NEW.motivo_rechazo IS NULL OR NEW.motivo_rechazo = '') THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El motivo de rechazo es obligatorio';
    END IF;
    
    -- Registrar fecha de rechazo
    IF NEW.estado = 'RECHAZADA' AND OLD.estado != 'RECHAZADA' THEN
        SET NEW.fecha_rechazo = NOW();
    END IF;
END$$

-- ----------------------------------------------
-- Trigger: AuditorÃ­a de ConversiÃ³n a Venta
-- ----------------------------------------------
-- Registra en log cuando una cotizaciÃ³n se convierte en venta

CREATE TRIGGER trg_cotizacion_conversion
AFTER UPDATE ON cotizacion
FOR EACH ROW
BEGIN
    IF NEW.estado = 'CONVERTIDA' AND OLD.estado != 'CONVERTIDA' THEN
        -- Si existe tabla de auditorÃ­a, registrar el evento
        INSERT INTO auditoria_log (
            entidad, entidad_id, accion, descripcion, 
            valor_anterior, valor_nuevo, fecha_evento
        ) VALUES (
            'cotizacion', NEW.id, 'CONVERSION',
            CONCAT('CotizaciÃ³n ', NEW.folio, ' convertida en venta ID ', NEW.venta_id),
            OLD.estado, NEW.estado, NOW()
        );
    END IF;
END$$

DELIMITER ;

-- ==================================================
-- VISTAS ÃšTILES
-- ==================================================

-- Vista: Cotizaciones Activas (no convertidas, no vencidas, no rechazadas)
CREATE OR REPLACE VIEW v_cotizaciones_activas AS
SELECT 
    c.id,
    c.folio,
    c.cliente_id,
    cl.nombre AS nombre_cliente,
    c.sucursal_id,
    s.nombre AS nombre_sucursal,
    c.estado,
    c.fecha_cotizacion,
    c.fecha_validez,
    DATEDIFF(c.fecha_validez, CURDATE()) AS dias_para_vencer,
    c.total,
    CASE 
        WHEN DATEDIFF(c.fecha_validez, CURDATE()) <= 3 THEN 'URGENTE'
        WHEN DATEDIFF(c.fecha_validez, CURDATE()) <= 7 THEN 'PROXIMO'
        ELSE 'NORMAL'
    END AS prioridad
FROM cotizacion c
INNER JOIN cliente cl ON c.cliente_id = cl.id
INNER JOIN sucursales s ON c.sucursal_id = s.id
WHERE c.estado NOT IN ('CONVERTIDA', 'RECHAZADA', 'VENCIDA')
  AND c.fecha_validez >= CURDATE();

-- Vista: Cotizaciones Pendientes de ConversiÃ³n
CREATE OR REPLACE VIEW v_cotizaciones_pendientes_conversion AS
SELECT 
    c.id,
    c.folio,
    c.cliente_id,
    cl.nombre AS nombre_cliente,
    cl.telefono AS telefono_cliente,
    c.vendedor_id,
    CONCAT(e.nombres, ' ', e.apellidos) AS nombre_vendedor,
    c.fecha_cotizacion,
    c.fecha_validez,
    c.total,
    c.estado
FROM cotizacion c
INNER JOIN cliente cl ON c.cliente_id = cl.id
LEFT JOIN empleado e ON c.vendedor_id = e.id
WHERE c.estado IN ('ENVIADA', 'ACEPTADA')
  AND c.fecha_validez >= CURDATE()
  AND c.venta_id IS NULL
ORDER BY c.fecha_validez ASC;

-- Vista: Reporte de ConversiÃ³n por Vendedor
CREATE OR REPLACE VIEW v_conversion_por_vendedor AS
SELECT 
    e.id AS vendedor_id,
    CONCAT(e.nombres, ' ', e.apellidos) AS vendedor,
    COUNT(*) AS total_cotizaciones,
    SUM(CASE WHEN c.estado = 'CONVERTIDA' THEN 1 ELSE 0 END) AS cotizaciones_convertidas,
    SUM(CASE WHEN c.estado = 'RECHAZADA' THEN 1 ELSE 0 END) AS cotizaciones_rechazadas,
    ROUND(
        (SUM(CASE WHEN c.estado = 'CONVERTIDA' THEN 1 ELSE 0 END) * 100.0 / COUNT(*)),
        2
    ) AS tasa_conversion,
    SUM(CASE WHEN c.estado = 'CONVERTIDA' THEN c.total ELSE 0 END) AS valor_vendido
FROM cotizacion c
INNER JOIN empleado e ON c.vendedor_id = e.id
WHERE c.fecha_cotizacion >= DATE_SUB(CURDATE(), INTERVAL 90 DAY)
GROUP BY e.id, e.nombres, e.apellidos
ORDER BY cotizaciones_convertidas DESC;

-- ==================================================
-- JOBS PROGRAMADOS (Ejemplo para MySQL Events)
-- ==================================================
-- Nota: Requiere que el Event Scheduler estÃ© habilitado
-- SET GLOBAL event_scheduler = ON;

DELIMITER $$

-- Job: Marcar cotizaciones vencidas diariamente a las 00:01
CREATE EVENT IF NOT EXISTS evt_marcar_cotizaciones_vencidas
ON SCHEDULE EVERY 1 DAY
STARTS CURRENT_DATE + INTERVAL 1 DAY + INTERVAL 1 MINUTE
DO
BEGIN
    CALL sp_marcar_cotizaciones_vencidas();
END$$

DELIMITER ;

-- Actualizar estadÃ­sticas de las tablas
ANALYZE TABLE cotizacion;
ANALYZE TABLE detalle_cotizacion;

-- ==================================================
-- FIN DEL SCRIPT
-- ==================================================

-- Verificar creaciÃ³n exitosa
SELECT 'Script SQL de Cotizaciones ejecutado exitosamente' AS resultado;
SELECT COUNT(*) AS cotizaciones_ejemplo FROM cotizacion;
SELECT COUNT(*) AS detalles_ejemplo FROM detalle_cotizacion;


-- ============================================================================
-- MÓDULO: RESERVAS/APARTADOS
-- ============================================================================

-- ============================================================================
-- SISTEMA DE RESERVAS/APARTADOS - NEXOOHUB ALMACÃ‰N
-- ============================================================================
-- DescripciÃ³n: Script SQL para crear tabla de reservas y datos de ejemplo
-- Autor: NexooHub Development Team
-- VersiÃ³n: 1.1.0
-- Fecha: Marzo 2026
-- ============================================================================

-- ============================================================================
-- 1. CREACIÃ“N DE TABLA reserva
-- ============================================================================

CREATE TABLE IF NOT EXISTS reserva (
    id SERIAL PRIMARY KEY,
    cliente_id INTEGER NOT NULL,
    sku_interno VARCHAR(50) NOT NULL,
    sucursal_id INTEGER NOT NULL,
    cantidad INTEGER NOT NULL CHECK (cantidad > 0),
    estado VARCHAR(20) NOT NULL CHECK (estado IN ('PENDIENTE', 'NOTIFICADA', 'COMPLETADA', 'VENCIDA', 'CANCELADA')),
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_notificacion TIMESTAMP,
    fecha_vencimiento TIMESTAMP NOT NULL,
    fecha_finalizacion TIMESTAMP,
    venta_id INTEGER,
    comentarios VARCHAR(500),
    usuario_registro VARCHAR(100) NOT NULL,
    
    -- Claves forÃ¡neas
    CONSTRAINT fk_reserva_cliente FOREIGN KEY (cliente_id) REFERENCES cliente(id),
    CONSTRAINT fk_reserva_producto FOREIGN KEY (sku_interno) REFERENCES producto_maestro(sku_interno),
    CONSTRAINT fk_reserva_sucursal FOREIGN KEY (sucursal_id) REFERENCES sucursal(id),
    CONSTRAINT fk_reserva_venta FOREIGN KEY (venta_id) REFERENCES venta(id)
);

-- ============================================================================
-- 2. ÃNDICES PARA OPTIMIZACIÃ“N DE CONSULTAS
-- ============================================================================

-- Ãndice para bÃºsquedas por cliente (historial de reservas del cliente)
CREATE INDEX IF NOT EXISTS idx_reserva_cliente 
ON reserva(cliente_id, fecha_creacion DESC);

-- Ãndice para bÃºsquedas por estado (listar reservas pendientes, notificadas, etc.)
CREATE INDEX IF NOT EXISTS idx_reserva_estado 
ON reserva(estado, fecha_creacion DESC);

-- Ãndice para bÃºsquedas de reservas por producto y sucursal
-- CrÃ­tico para notificar cuando llega mercancÃ­a
CREATE INDEX IF NOT EXISTS idx_reserva_producto_sucursal 
ON reserva(sku_interno, sucursal_id, estado, fecha_creacion);

-- Ãndice para identificar reservas vencidas (tarea programada)
CREATE INDEX IF NOT EXISTS idx_reserva_vencimiento 
ON reserva(estado, fecha_vencimiento);

-- Ãndice para reservas prÃ³ximas a vencer (alertas)
CREATE INDEX IF NOT EXISTS idx_reserva_notificada_vencimiento 
ON reserva(estado, fecha_vencimiento) 
WHERE estado = 'NOTIFICADA';

-- ============================================================================
-- 3. COMENTARIOS EN TABLA Y COLUMNAS (DOCUMENTACIÃ“N)
-- ============================================================================

COMMENT ON TABLE reserva IS 'Reservas/apartados de productos cuando no hay stock disponible';

COMMENT ON COLUMN reserva.id IS 'ID Ãºnico de la reserva';
COMMENT ON COLUMN reserva.cliente_id IS 'Cliente que realizÃ³ la reserva';
COMMENT ON COLUMN reserva.sku_interno IS 'Producto reservado';
COMMENT ON COLUMN reserva.sucursal_id IS 'Sucursal donde se recogerÃ¡ el producto';
COMMENT ON COLUMN reserva.cantidad IS 'Cantidad de unidades reservadas';
COMMENT ON COLUMN reserva.estado IS 'Estado: PENDIENTE, NOTIFICADA, COMPLETADA, VENCIDA, CANCELADA';
COMMENT ON COLUMN reserva.fecha_creacion IS 'Fecha de creaciÃ³n de la reserva';
COMMENT ON COLUMN reserva.fecha_notificacion IS 'Fecha en que se notificÃ³ al cliente (mercancÃ­a disponible)';
COMMENT ON COLUMN reserva.fecha_vencimiento IS 'Fecha lÃ­mite para recoger el producto';
COMMENT ON COLUMN reserva.fecha_finalizacion IS 'Fecha de completado/cancelaciÃ³n';
COMMENT ON COLUMN reserva.venta_id IS 'ID de venta generada al completar la reserva';
COMMENT ON COLUMN reserva.comentarios IS 'Observaciones del cliente o sistema';
COMMENT ON COLUMN reserva.usuario_registro IS 'Usuario que registrÃ³ la reserva';

-- ============================================================================
-- 4. DATOS DE EJEMPLO (TESTING)
-- ============================================================================

-- Escenario 1: Reserva PENDIENTE (esperando mercancÃ­a)
-- Cliente solicita balatas Yamaha que no hay en stock
INSERT INTO reserva (
    cliente_id, sku_interno, sucursal_id, cantidad, estado,
    fecha_creacion, fecha_vencimiento, comentarios, usuario_registro
) VALUES (
    1, -- Asumiendo cliente ID=1 existe
    'BAL-YAM-R15-001',
    1, -- Sucursal Centro
    2,
    'PENDIENTE',
    NOW(),
    NOW() + INTERVAL '7 days',
    'Cliente urgente - llamar cuando llegue la mercancÃ­a',
    'admin'
);

-- Escenario 2: Reserva NOTIFICADA (mercancÃ­a llegÃ³, cliente notificado)
-- Cliente fue notificado hace 2 dÃ­as
INSERT INTO reserva (
    cliente_id, sku_interno, sucursal_id, cantidad, estado,
    fecha_creacion, fecha_notificacion, fecha_vencimiento, 
    comentarios, usuario_registro
) VALUES (
    2,
    'ACE-MOT-10W40-001',
    1,
    5,
    'NOTIFICADA',
    NOW() - INTERVAL '5 days',
    NOW() - INTERVAL '2 days',
    NOW() + INTERVAL '5 days',
    'Cliente mayorista - confirmar antes de recoger',
    'vendedor1'
);

-- Escenario 3: Reserva COMPLETADA (convertida a venta)
-- Cliente recogiÃ³ el producto hace 1 dÃ­a
INSERT INTO reserva (
    cliente_id, sku_interno, sucursal_id, cantidad, estado,
    fecha_creacion, fecha_notificacion, fecha_vencimiento, 
    fecha_finalizacion, venta_id, comentarios, usuario_registro
) VALUES (
    3,
    'CAS-INT-XL-001',
    2, -- Sucursal Norte
    1,
    'COMPLETADA',
    NOW() - INTERVAL '10 days',
    NOW() - INTERVAL '3 days',
    NOW() + INTERVAL '4 days',
    NOW() - INTERVAL '1 day',
    42, -- ID de venta generada
    'Reserva completada exitosamente',
    'vendedor2'
);

-- Escenario 4: Reserva VENCIDA (cliente no recogiÃ³ a tiempo)
-- Reserva creada hace 15 dÃ­as, venciÃ³ hace 3 dÃ­as
INSERT INTO reserva (
    cliente_id, sku_interno, sucursal_id, cantidad, estado,
    fecha_creacion, fecha_vencimiento, fecha_finalizacion,
    comentarios, usuario_registro
) VALUES (
    4,
    'LLA-MOT-17-001',
    1,
    4,
    'VENCIDA',
    NOW() - INTERVAL '15 days',
    NOW() - INTERVAL '3 days',
    NOW() - INTERVAL '3 days',
    'Cliente no contestÃ³ llamadas | Sistema marcÃ³ como VENCIDA automÃ¡ticamente',
    'admin'
);

-- Escenario 5: Reserva CANCELADA (cliente ya no la necesita)
-- Cliente cancelÃ³ hace 5 dÃ­as
INSERT INTO reserva (
    cliente_id, sku_interno, sucursal_id, cantidad, estado,
    fecha_creacion, fecha_vencimiento, fecha_finalizacion,
    comentarios, usuario_registro
) VALUES (
    1,
    'FIL-AIR-KAW-001',
    2,
    3,
    'CANCELADA',
    NOW() - INTERVAL '8 days',
    NOW() + INTERVAL '6 days',
    NOW() - INTERVAL '5 days',
    'Cliente solicitÃ³ cancelaciÃ³n | CANCELACIÃ“N: Cliente comprÃ³ en otra tienda',
    'admin'
);

-- Escenario 6: Reserva NOTIFICADA prÃ³xima a vencer (alerta)
-- MercancÃ­a disponible, vence maÃ±ana
INSERT INTO reserva (
    cliente_id, sku_interno, sucursal_id, cantidad, estado,
    fecha_creacion, fecha_notificacion, fecha_vencimiento,
    comentarios, usuario_registro
) VALUES (
    5,
    'ESP-RET-MOT-001',
    1,
    2,
    'NOTIFICADA',
    NOW() - INTERVAL '6 days',
    NOW() - INTERVAL '1 day',
    NOW() + INTERVAL '1 day',
    'URGENTE: Cliente tiene solo 1 dÃ­a para recoger',
    'vendedor1'
);

-- ============================================================================
-- 5. CONSULTAS ÃšTILES PARA TESTING
-- ============================================================================

-- Verificar datos insertados
-- SELECT * FROM reserva ORDER BY fecha_creacion DESC;

-- Listar reservas pendientes por producto
-- SELECT 
--     r.id, r.cliente_id, c.nombre as cliente, 
--     r.sku_interno, p.nombre_comercial as producto,
--     r.cantidad, r.estado, r.fecha_creacion
-- FROM reserva r
-- JOIN cliente c ON r.cliente_id = c.id
-- JOIN producto_maestro p ON r.sku_interno = p.sku_interno
-- WHERE r.estado = 'PENDIENTE'
-- ORDER BY r.fecha_creacion;

-- Buscar reservas vencidas (no procesadas)
-- SELECT * FROM reserva 
-- WHERE estado = 'PENDIENTE' 
-- AND fecha_vencimiento < NOW();

-- Reservas prÃ³ximas a vencer (Ãºltimos 2 dÃ­as)
-- SELECT 
--     r.id, c.nombre as cliente, p.nombre_comercial as producto,
--     r.fecha_vencimiento,
--     EXTRACT(DAY FROM (r.fecha_vencimiento - NOW())) as dias_restantes
-- FROM reserva r
-- JOIN cliente c ON r.cliente_id = c.id
-- JOIN producto_maestro p ON r.sku_interno = p.sku_interno
-- WHERE r.estado = 'NOTIFICADA'
-- AND r.fecha_vencimiento BETWEEN NOW() AND NOW() + INTERVAL '2 days';

-- Contar reservas activas por cliente
-- SELECT 
--     c.id, c.nombre, 
--     COUNT(r.id) as reservas_activas
-- FROM cliente c
-- LEFT JOIN reserva r ON c.id = r.cliente_id 
-- WHERE r.estado IN ('PENDIENTE', 'NOTIFICADA')
-- GROUP BY c.id, c.nombre
-- ORDER BY reservas_activas DESC;

-- ============================================================================
-- FIN DEL SCRIPT
-- ============================================================================


-- ============================================================================
-- MÓDULO: CONTROL DE CRÉDITO
-- ============================================================================

-- =====================================================
-- SCRIPT SQL: SISTEMA DE CONTROL DE CRÃ‰DITO
-- Base de datos: PostgreSQL 15+
-- Autor: NexooHub Development Team
-- VersiÃ³n: 1.2.0
-- =====================================================

-- =====================================================
-- TABLAS
-- =====================================================

-- Tabla: limite_credito
-- DescripciÃ³n: Almacena los lÃ­mites de crÃ©dito configurados por cliente
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

COMMENT ON TABLE limite_credito IS 'LÃ­mites de crÃ©dito configurados por cliente con control de saldo y bloqueos';
COMMENT ON COLUMN limite_credito.limite_autorizado IS 'Monto mÃ¡ximo de crÃ©dito que el cliente puede usar';
COMMENT ON COLUMN limite_credito.saldo_utilizado IS 'Saldo actualmente utilizado por el cliente';
COMMENT ON COLUMN limite_credito.estado IS 'Estado del crÃ©dito: ACTIVO, BLOQUEADO, SUSPENDIDO, INACTIVO';
COMMENT ON COLUMN limite_credito.plazo_pago_dias IS 'NÃºmero de dÃ­as de plazo para pago';
COMMENT ON COLUMN limite_credito.max_facturas_vencidas IS 'MÃ¡ximo de facturas vencidas antes de bloquear';
COMMENT ON COLUMN limite_credito.permite_sobregiro IS 'Permite exceder temporalmente el lÃ­mite';
COMMENT ON COLUMN limite_credito.monto_sobregiro IS 'Monto mÃ¡ximo de sobregiro permitido';

-- Tabla: historial_credito
-- DescripciÃ³n: Registra todos los movimientos de crÃ©dito (cargos, abonos, ajustes)
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

COMMENT ON TABLE historial_credito IS 'Historial completo de movimientos de crÃ©dito de clientes';
COMMENT ON COLUMN historial_credito.tipo_movimiento IS 'Tipo: CARGO (venta), ABONO (pago), AJUSTE, BLOQUEO, DESBLOQUEO';
COMMENT ON COLUMN historial_credito.monto IS 'Monto del movimiento (siempre positivo)';
COMMENT ON COLUMN historial_credito.saldo_resultante IS 'Saldo del cliente despuÃ©s del movimiento';
COMMENT ON COLUMN historial_credito.metodo_pago IS 'MÃ©todo usado en abonos: EFECTIVO, TRANSFERENCIA, CHEQUE, TARJETA';

-- =====================================================
-- ÃNDICES PARA OPTIMIZACIÃ“N
-- =====================================================

-- Ãndices en limite_credito
CREATE INDEX idx_limite_credito_cliente ON limite_credito(cliente_id);
CREATE INDEX idx_limite_credito_estado ON limite_credito(estado);
CREATE INDEX idx_limite_credito_revision ON limite_credito(fecha_revision);
CREATE INDEX idx_limite_credito_sobregiro ON limite_credito(saldo_utilizado, limite_autorizado) WHERE saldo_utilizado > limite_autorizado;

-- Ãndices en historial_credito
CREATE INDEX idx_historial_credito_cliente ON historial_credito(cliente_id);
CREATE INDEX idx_historial_credito_fecha ON historial_credito(fecha_movimiento DESC);
CREATE INDEX idx_historial_credito_tipo ON historial_credito(tipo_movimiento);
CREATE INDEX idx_historial_credito_venta ON historial_credito(venta_id);
CREATE INDEX idx_historial_credito_cliente_fecha ON historial_credito(cliente_id, fecha_movimiento DESC);
CREATE INDEX idx_historial_credito_cliente_tipo ON historial_credito(cliente_id, tipo_movimiento);

-- =====================================================
-- FUNCIONES Y TRIGGERS
-- =====================================================

-- FunciÃ³n: actualizar updated_at automÃ¡ticamente
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

-- IMPORTANTE: AsegÃºrate de que existan clientes antes de insertar lÃ­mites

-- LÃ­mites de crÃ©dito ejemplo
INSERT INTO limite_credito (cliente_id, limite_autorizado, saldo_utilizado, estado, plazo_pago_dias, max_facturas_vencidas, permite_sobregiro, monto_sobregiro, fecha_revision, observaciones, created_by) VALUES
-- Cliente 1: PÃºblico general con lÃ­mite estÃ¡ndar
(1, 10000.00, 3500.00, 'ACTIVO', 30, 3, FALSE, 0, CURRENT_DATE, 'Cliente nuevo con lÃ­mite estÃ¡ndar', 'SYSTEM'),

-- Cliente 2: Cliente premium
(2, 50000.00, 12000.00, 'ACTIVO', 45, 5, TRUE, 5000.00, CURRENT_DATE, 'Cliente premium con sobregiro autorizado', 'SYSTEM'),

-- Cliente 3: Cliente en riesgo
(3, 15000.00, 13500.00, 'ACTIVO', 30, 3, FALSE, 0, CURRENT_DATE - INTERVAL '90 days', 'Cliente con alta utilizaciÃ³n, requiere monitoreo', 'SYSTEM'),

-- Cliente 4: Cliente bloqueado 
(4, 8000.00, 9500.00, 'BLOQUEADO', 30, 3, FALSE, 0, CURRENT_DATE, 'Bloqueado por exceder lÃ­mite de crÃ©dito', 'SYSTEM'),

-- Cliente 5: Cliente con crÃ©dito suspendido
(5, 20000.00, 5000.00, 'SUSPENDIDO', 30, 3, FALSE, 0, CURRENT_DATE, 'Suspendido temporalmente por revisiÃ³n de cuenta', 'SYSTEM')
ON CONFLICT (cliente_id) DO NOTHING;

-- Historial de movimientos ejemplo
-- Cliente 1: Movimientos mixtos
INSERT INTO historial_credito (cliente_id, venta_id, tipo_movimiento, monto, saldo_resultante, metodo_pago, folio_comprobante, concepto, fecha_movimiento, usuario_registro) VALUES
(1, NULL, 'CARGO', 2000.00, 2000.00, NULL, NULL, 'Venta a crÃ©dito #1001', CURRENT_TIMESTAMP - INTERVAL '15 days', 'vendedor1'),
(1, NULL, 'ABONO', 500.00, 1500.00, 'EFECTIVO', 'REC-001', 'Pago parcial', CURRENT_TIMESTAMP - INTERVAL '10 days', 'cajero1'),
(1, NULL, 'CARGO', 2500.00, 4000.00, NULL, NULL, 'Venta a crÃ©dito #1015', CURRENT_TIMESTAMP - INTERVAL '5 days', 'vendedor2'),
(1, NULL, 'ABONO', 500.00, 3500.00, 'TRANSFERENCIA', 'TRF-20240301-001', 'Abono a cuenta', CURRENT_TIMESTAMP - INTERVAL '2 days', 'cajero1');

-- Cliente 2: Cliente premium con mÃºltiples movimientos
INSERT INTO historial_credito (cliente_id, venta_id, tipo_movimiento, monto, saldo_resultante, metodo_pago, folio_comprobante, concepto, fecha_movimiento, usuario_registro) VALUES
(2, NULL, 'CARGO', 15000.00, 15000.00, NULL, NULL, 'Venta a crÃ©dito #1002', CURRENT_TIMESTAMP - INTERVAL '30 days', 'vendedor1'),
(2, NULL, 'ABONO', 5000.00, 10000.00, 'CHEQUE', 'CHK-12345', 'Pago de factura', CURRENT_TIMESTAMP - INTERVAL '20 days', 'cajero2'),
(2, NULL, 'CARGO', 8000.00, 18000.00, NULL, NULL, 'Venta a crÃ©dito #1025', CURRENT_TIMESTAMP - INTERVAL '10 days', 'vendedor1'),
(2, NULL, 'ABONO', 6000.00, 12000.00, 'TRANSFERENCIA', 'TRF-20240305-002', 'Abono mensual', CURRENT_TIMESTAMP - INTERVAL '3 days', 'cajero1');

-- Cliente 3: Cliente en riesgo
INSERT INTO historial_credito (cliente_id, venta_id, tipo_movimiento, monto, saldo_resultante, metodo_pago, folio_comprobante, concepto, fecha_movimiento, usuario_registro) VALUES
(3, NULL, 'CARGO', 10000.00, 10000.00, NULL, NULL, 'Venta a crÃ©dito #1003', CURRENT_TIMESTAMP - INTERVAL '60 days', 'vendedor2'),
(3, NULL, 'CARGO', 3500.00, 13500.00, NULL, NULL, 'Venta a crÃ©dito #1018', CURRENT_TIMESTAMP - INTERVAL '40 days', 'vendedor1');

-- Cliente 4: Cliente bloqueado por sobregiro
INSERT INTO historial_credito (cliente_id, venta_id, tipo_movimiento, monto, saldo_resultante, metodo_pago, folio_comprobante, concepto, fecha_movimiento, usuario_registro) VALUES
(4, NULL, 'CARGO', 5000.00, 5000.00, NULL, NULL, 'Venta a crÃ©dito #1004', CURRENT_TIMESTAMP - INTERVAL '45 days', 'vendedor1'),
(4, NULL, 'CARGO', 3000.00, 8000.00, NULL, NULL, 'Venta a crÃ©dito #1012', CURRENT_TIMESTAMP - INTERVAL '30 days', 'vendedor2'),
(4, NULL, 'CARGO', 1500.00, 9500.00, NULL, NULL, 'Venta a crÃ©dito #1022', CURRENT_TIMESTAMP - INTERVAL '15 days', 'vendedor1'),
(4, NULL, 'BLOQUEO', 0, 9500.00, NULL, NULL, 'CrÃ©dito bloqueado por exceder lÃ­mite', CURRENT_TIMESTAMP - INTERVAL '10 days', 'SYSTEM');

-- Cliente 5: Cliente suspendido
INSERT INTO historial_credito (cliente_id, venta_id, tipo_movimiento, monto, saldo_resultante, metodo_pago, folio_comprobante, concepto, fecha_movimiento, usuario_registro) VALUES
(5, NULL, 'CARGO', 5000.00, 5000.00, NULL, NULL, 'Venta a crÃ©dito #1005', CURRENT_TIMESTAMP - INTERVAL '20 days', 'vendedor1'),
(5, NULL, 'BLOQUEO', 0, 5000.00, NULL, NULL, 'CrÃ©dito suspendido para revisiÃ³n', CURRENT_TIMESTAMP - INTERVAL '5 days', 'gerente1');

-- =====================================================
-- VISTAS ÃšTILES
-- =====================================================

-- Vista: resumen de crÃ©dito por cliente
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

COMMENT ON VIEW v_resumen_credito IS 'Vista consolidada del estado de crÃ©dito por cliente';

-- Vista: clientes en riesgo (>= 80% utilizaciÃ³n)
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

COMMENT ON VIEW v_clientes_riesgo IS 'Clientes con utilizaciÃ³n >= 80% que requieren monitoreo';

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

COMMENT ON VIEW v_clientes_sobregiro IS 'Clientes que excedieron su lÃ­mite de crÃ©dito';

-- =====================================================
-- CONSULTAS ÃšTILES
-- =====================================================

-- Consulta: Resumen general del sistema de crÃ©dito
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


-- ============================================================================
-- MÓDULO: ALERTAS DE LENTO MOVIMIENTO
-- ============================================================================

-- ============================================================================
-- SCRIPT SQL: Alertas de Productos de Lento Movimiento
-- DescripciÃ³n: Crea tabla y datos de prueba para alertas de baja rotaciÃ³n
-- VersiÃ³n: 1.2.0
-- Fecha: 2025-03-09
-- ============================================================================

-- ==================================================
-- TABLA: alerta_lento_movimiento
-- ==================================================
-- Almacena alertas de productos sin ventas en X dÃ­as
-- Permite tracking de productos de baja rotaciÃ³n

CREATE TABLE IF NOT EXISTS alerta_lento_movimiento (
    id SERIAL PRIMARY KEY,
    sku_interno VARCHAR(50) NOT NULL,
    sucursal_id INT NOT NULL,
    dias_sin_venta INT NOT NULL CHECK (dias_sin_venta >= 0),
    ultima_venta DATE,
    stock_actual INT NOT NULL CHECK (stock_actual >= 0),
    costo_inmovilizado DECIMAL(10,2) NOT NULL DEFAULT 0.00 CHECK (costo_inmovilizado >= 0),
    estado_alerta VARCHAR(20) NOT NULL CHECK (estado_alerta IN ('ADVERTENCIA', 'CRITICO', 'RESUELTA')),
    fecha_deteccion DATE NOT NULL,
    fecha_resolucion DATE,
    accion_tomada VARCHAR(100),
    observaciones VARCHAR(500),
    resuelto BOOLEAN NOT NULL DEFAULT false,
    
    -- AuditorÃ­a (herencia de AuditableEntity)
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    
    -- Claves forÃ¡neas
    FOREIGN KEY (sku_interno) REFERENCES producto_maestro(sku_interno) ON DELETE CASCADE,
    FOREIGN KEY (sucursal_id) REFERENCES sucursales(id) ON DELETE CASCADE
);

-- ==================================================
-- ÃNDICES PARA OPTIMIZACIÃ“N DE QUERIES
-- ==================================================
CREATE INDEX idx_alm_sucursal ON alerta_lento_movimiento(sucursal_id);
CREATE INDEX idx_alm_sku ON alerta_lento_movimiento(sku_interno);
CREATE INDEX idx_alm_estado ON alerta_lento_movimiento(estado_alerta);
CREATE INDEX idx_alm_fecha_deteccion ON alerta_lento_movimiento(fecha_deteccion);
CREATE INDEX idx_alm_resuelto ON alerta_lento_movimiento(resuelto);
CREATE INDEX idx_alm_dias_sin_venta ON alerta_lento_movimiento(dias_sin_venta DESC);

-- Ãndice compuesto para bÃºsquedas comunes
CREATE INDEX idx_alm_sucursal_resuelto ON alerta_lento_movimiento(sucursal_id, resuelto);
CREATE UNIQUE INDEX idx_alm_sku_sucursal_activa ON alerta_lento_movimiento(sku_interno, sucursal_id) 
WHERE resuelto = false; -- Evita duplicados de alertas activas

-- ==================================================
-- COMENTARIOS EN LA BASE DE DATOS
-- ==================================================
COMMENT ON TABLE alerta_lento_movimiento IS 'Alertas de productos con baja rotaciÃ³n de inventario';
COMMENT ON COLUMN alerta_lento_movimiento.dias_sin_venta IS 'DÃ­as transcurridos desde la Ãºltima venta';
COMMENT ON COLUMN alerta_lento_movimiento.costo_inmovilizado IS 'Stock actual Ã— CPP del producto';
COMMENT ON COLUMN alerta_lento_movimiento.estado_alerta IS 'ADVERTENCIA (30-60 dÃ­as), CRITICO (>60 dÃ­as), RESUELTA';
COMMENT ON COLUMN alerta_lento_movimiento.accion_tomada IS 'LIQUIDACION, PROMOCION, TRANSFERENCIA, DESCONTINUADO, NINGUNA';

-- ==================================================
-- DATOS DE PRUEBA (Opcional - Solo para desarrollo)
-- ==================================================

-- Insertar algunas alertas de ejemplo
-- Nota: Asume que existen productos y sucursales con IDs 1, 2, 3

-- Alerta ADVERTENCIA (45 dÃ­as sin venta)
INSERT INTO alerta_lento_movimiento (
    sku_interno, sucursal_id, dias_sin_venta, ultima_venta, 
    stock_actual, costo_inmovilizado, estado_alerta, 
    fecha_deteccion, resuelto
) VALUES 
(
    'BRK-001', 1, 45, CURRENT_DATE - INTERVAL '45 days',
    12, 3600.00, 'ADVERTENCIA', 
    CURRENT_DATE - INTERVAL '2 days', false
),

-- Alerta CRITICO (75 dÃ­as sin venta)
(
    'SUSP-005', 1, 75, CURRENT_DATE - INTERVAL '75 days',
    8, 6400.00, 'CRITICO',
    CURRENT_DATE - INTERVAL '5 days', false
),

-- Alerta ADVERTENCIA (35 dÃ­as)
(
    'FLT-020', 2, 35, CURRENT_DATE - INTERVAL '35 days',
    5, 750.00, 'ADVERTENCIA',
    CURRENT_DATE - INTERVAL '1 day', false
),

-- Alerta RESUELTA (fue liquidada)
(
    'CHN-010', 1, 90, CURRENT_DATE - INTERVAL '120 days',
    0, 0.00, 'RESUELTA',
    CURRENT_DATE - INTERVAL '30 days', true
);

-- Actualizar estadÃ­sticas de la tabla
ANALYZE alerta_lento_movimiento;

-- ==================================================
-- STORED PROCEDURE (Opcional): Generar Alertas AutomÃ¡ticamente
-- ==================================================
-- Este procedimiento puede ser llamado por un CRON job diario

DELIMITER $$

CREATE PROCEDURE sp_generar_alertas_lento_movimiento(
    IN p_dias_minimos INT
)
BEGIN
    -- Detectar productos sin ventas en los Ãºltimos N dÃ­as
    INSERT INTO alerta_lento_movimiento (
        sku_interno, sucursal_id, dias_sin_venta, ultima_venta,
        stock_actual, costo_inmovilizado, estado_alerta,
        fecha_deteccion, resuelto
    )
    SELECT 
        inv.sku_interno,
        inv.sucursal_id,
        DATEDIFF(CURRENT_DATE, MAX(dv.fecha_venta)) as dias_sin_venta,
        MAX(dv.fecha_venta) as ultima_venta,
        inv.stock_actual,
        inv.stock_actual * inv.costo_promedio_ponderado as costo_inmovilizado,
        CASE 
            WHEN DATEDIFF(CURRENT_DATE, MAX(dv.fecha_venta)) >= 60 THEN 'CRITICO'
            WHEN DATEDIFF(CURRENT_DATE, MAX(dv.fecha_venta)) >= 30 THEN 'ADVERTENCIA'
            ELSE 'NORMAL'
        END as estado_alerta,
        CURRENT_DATE as fecha_deteccion,
        false as resuelto
    FROM inventario_sucursal inv
    LEFT JOIN detalle_venta dv ON inv.sku_interno = dv.sku_interno
    WHERE inv.stock_actual > 0
    GROUP BY inv.sku_interno, inv.sucursal_id, inv.stock_actual, inv.costo_promedio_ponderado
    HAVING 
        (MAX(dv.fecha_venta) IS NULL OR MAX(dv.fecha_venta) < DATE_SUB(CURRENT_DATE, INTERVAL p_dias_minimos DAY))
        AND NOT EXISTS (
            SELECT 1 FROM alerta_lento_movimiento alm 
            WHERE alm.sku_interno = inv.sku_interno 
              AND alm.sucursal_id = inv.sucursal_id 
              AND alm.resuelto = false
        );
END$$

DELIMITER ;

-- ==================================================
-- EVENTO PROGRAMADO (Opcional): EjecuciÃ³n Diaria AutomÃ¡tica
-- ==================================================
-- Genera alertas automÃ¡ticamente cada dÃ­a a las 6:00 AM

CREATE EVENT IF NOT EXISTS evt_generar_alertas_diarias
ON SCHEDULE EVERY 1 DAY
STARTS CURRENT_DATE + INTERVAL 6 HOUR
DO
    CALL sp_generar_alertas_lento_movimiento(30);

-- Para habilitar el event scheduler (si estÃ¡ desactivado):
-- SET GLOBAL event_scheduler = ON;

-- ==================================================
-- LIMPIEZA PERIÃ“DICA (Opcional)
-- ==================================================
-- Eliminar alertas resueltas mÃ¡s antiguas que 1 aÃ±o

CREATE EVENT IF NOT EXISTS evt_limpiar_alertas_antiguas
ON SCHEDULE EVERY 1 MONTH
STARTS CURRENT_DATE + INTERVAL 1 MONTH
DO
    DELETE FROM alerta_lento_movimiento 
    WHERE resuelto = true 
      AND fecha_resolucion < DATE_SUB(CURRENT_DATE, INTERVAL 1 YEAR);

-- ==================================================
-- VISTAS (Opcional): Consultas frecuentes optimizadas
-- ==================================================

-- Vista: Alertas crÃ­ticas con informaciÃ³n completa
CREATE OR REPLACE VIEW v_alertas_criticas AS
SELECT 
    a.id,
    a.sku_interno,
    p.nombre_comercial,
    p.marca,
    a.sucursal_id,
    s.nombre as sucursal_nombre,
    a.dias_sin_venta,
    a.stock_actual,
    a.costo_inmovilizado,
    a.estado_alerta,
    a.fecha_deteccion
FROM alerta_lento_movimiento a
JOIN producto_maestro p ON a.sku_interno = p.sku_interno
JOIN sucursales s ON a.sucursal_id = s.id
WHERE a.estado_alerta = 'CRITICO' AND a.resuelto = false
ORDER BY a.dias_sin_venta DESC, a.costo_inmovilizado DESC;

-- Vista: Resumen de costos por sucursal
CREATE OR REPLACE VIEW v_costo_inmovilizado_por_sucursal AS
SELECT 
    s.id as sucursal_id,
    s.nombre as sucursal_nombre,
    COUNT(*) as total_alertas,
    SUM(CASE WHEN a.estado_alerta = 'ADVERTENCIA' THEN 1 ELSE 0 END) as alertas_advertencia,
    SUM(CASE WHEN a.estado_alerta = 'CRITICO' THEN 1 ELSE 0 END) as alertas_criticas,
    SUM(a.costo_inmovilizado) as costo_total_inmovilizado
FROM sucursales s
LEFT JOIN alerta_lento_movimiento a ON s.id = a.sucursal_id AND a.resuelto = false
GROUP BY s.id, s.nombre
ORDER BY costo_total_inmovilizado DESC;

-- ==================================================
-- FIN MÓDULO DE ALERTAS DE LENTO MOVIMIENTO
-- ==================================================

-- ============================================================================
-- MÓDULO V2: COMISIONES PARA VENDEDORES
-- ============================================================================
-- Descripción: Agrega tablas para gestionar comisiones de vendedores
--              - Reglas de comisión configurables
--              - Cálculo automático de comisiones por periodo
--              - Control de aprobación y pago
-- Fecha: 2026-03-09
-- ============================================================================

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

-- ==================================================
-- FIN MÓDULO DE COMISIONES
-- ==================================================

-- ============================================================================
-- MÓDULO V3: PREDICCIÓN DE DEMANDA
-- ============================================================================
-- Descripción: Agrega tabla para almacenar predicciones de demanda
--              - Análisis histórico de ventas
--              - Proyección de demanda futura
--              - Recomendaciones de compra
-- Fecha: 2026-03-09
-- ============================================================================

-- Tabla de predicciones de demanda
CREATE TABLE prediccion_demanda (
    id SERIAL PRIMARY KEY,
    sku_producto VARCHAR(50) NOT NULL,
    sucursal_id INTEGER NOT NULL,
    periodo_anio INTEGER NOT NULL,
    periodo_mes INTEGER NOT NULL CHECK (periodo_mes >= 1 AND periodo_mes <= 12),
    demanda_historica NUMERIC(10, 2) NOT NULL CHECK (demanda_historica >= 0),
    tendencia NUMERIC(10, 4),
    demanda_predicha NUMERIC(10, 2) NOT NULL CHECK (demanda_predicha >= 0),
    stock_actual INTEGER NOT NULL CHECK (stock_actual >= 0),
    stock_seguridad INTEGER NOT NULL CHECK (stock_seguridad >= 0),
    stock_sugerido INTEGER NOT NULL CHECK (stock_sugerido >= 0),
    cantidad_comprar INTEGER NOT NULL CHECK (cantidad_comprar >= 0),
    nivel_confianza NUMERIC(5, 2) CHECK (nivel_confianza >= 0 AND nivel_confianza <= 100),
    metodo_calculo VARCHAR(50) NOT NULL,
    periodos_analizados INTEGER,
    fecha_calculo DATE NOT NULL,
    observaciones VARCHAR(500),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_creacion VARCHAR(50),
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_actualizacion VARCHAR(50),
    
    -- Constraint único: solo una predicción por producto-sucursal-periodo
    CONSTRAINT uk_prediccion_producto_periodo UNIQUE (sku_producto, sucursal_id, periodo_anio, periodo_mes)
);

-- Comentarios en columnas
COMMENT ON TABLE prediccion_demanda IS 'Almacena predicciones de demanda basadas en análisis histórico';
COMMENT ON COLUMN prediccion_demanda.sku_producto IS 'SKU del producto analizado';
COMMENT ON COLUMN prediccion_demanda.sucursal_id IS 'Sucursal para la que se calcula la predicción';
COMMENT ON COLUMN prediccion_demanda.periodo_anio IS 'Año del periodo predicho';
COMMENT ON COLUMN prediccion_demanda.periodo_mes IS 'Mes del periodo predicho (1-12)';
COMMENT ON COLUMN prediccion_demanda.demanda_historica IS 'Promedio de demanda histórica (unidades/periodo)';
COMMENT ON COLUMN prediccion_demanda.tendencia IS 'Tendencia calculada (positiva=crecimiento, negativa=decrecimiento)';
COMMENT ON COLUMN prediccion_demanda.demanda_predicha IS 'Demanda predicha para el periodo (unidades)';
COMMENT ON COLUMN prediccion_demanda.stock_actual IS 'Stock disponible al momento del cálculo';
COMMENT ON COLUMN prediccion_demanda.stock_seguridad IS 'Stock de seguridad recomendado (unidades)';
COMMENT ON COLUMN prediccion_demanda.stock_sugerido IS 'Stock total sugerido (demanda + seguridad)';
COMMENT ON COLUMN prediccion_demanda.cantidad_comprar IS 'Cantidad recomendada para comprar';
COMMENT ON COLUMN prediccion_demanda.nivel_confianza IS 'Nivel de confianza de la predicción (0-100%)';
COMMENT ON COLUMN prediccion_demanda.metodo_calculo IS 'Método usado: PROMEDIO_MOVIL, TENDENCIA_LINEAL, ESTACIONAL';
COMMENT ON COLUMN prediccion_demanda.periodos_analizados IS 'Número de periodos históricos analizados';
COMMENT ON COLUMN prediccion_demanda.fecha_calculo IS 'Fecha en que se realizó el cálculo';
COMMENT ON COLUMN prediccion_demanda.observaciones IS 'Observaciones adicionales del análisis';

-- Índices para búsqueda eficiente
CREATE INDEX idx_pred_sku ON prediccion_demanda(sku_producto);
CREATE INDEX idx_pred_sucursal ON prediccion_demanda(sucursal_id);
CREATE INDEX idx_pred_periodo ON prediccion_demanda(periodo_anio, periodo_mes);
CREATE INDEX idx_pred_fecha_calculo ON prediccion_demanda(fecha_calculo);
CREATE INDEX idx_pred_cantidad_comprar ON prediccion_demanda(cantidad_comprar) WHERE cantidad_comprar > 0;

-- Comentarios en índices
COMMENT ON INDEX idx_pred_sku IS 'Búsqueda por producto';
COMMENT ON INDEX idx_pred_sucursal IS 'Búsqueda por sucursal';
COMMENT ON INDEX idx_pred_periodo IS 'Búsqueda por periodo';
COMMENT ON INDEX idx_pred_fecha_calculo IS 'Filtrado por fecha de cálculo';
COMMENT ON INDEX idx_pred_cantidad_comprar IS 'Búsqueda de productos que requieren compra';

-- ==================================================
-- FIN MÓDULO DE PREDICCIÓN DE DEMANDA
-- ==================================================

-- ==================================================================
-- MÓDULO #10: ANÁLISIS ABC DE INVENTARIO
-- ==================================================================
-- Descripción: Clasificación de productos según su valor (Principio Pareto 80/20)
--              - Clase A: ~20% productos, ~80% valor
--              - Clase B: ~30% productos, ~15% valor
--              - Clase C: ~50% productos, ~5% valor
-- Autor: NexooHub Development Team
-- Fecha: 2026-03-09
-- ==================================================================

CREATE TABLE analisis_abc (
    id SERIAL PRIMARY KEY,
    sku_producto VARCHAR(50) NOT NULL,
    sucursal_id INTEGER NOT NULL,
    clasificacion VARCHAR(1) NOT NULL CHECK (clasificacion IN ('A', 'B', 'C')),
    periodo_inicio DATE NOT NULL,
    periodo_fin DATE NOT NULL,
    cantidad_vendida INTEGER NOT NULL DEFAULT 0 CHECK (cantidad_vendida >= 0),
    valor_ventas NUMERIC(12, 2) NOT NULL DEFAULT 0.00 CHECK (valor_ventas >= 0),
    porcentaje_valor NUMERIC(5, 2) NOT NULL DEFAULT 0.00 CHECK (porcentaje_valor >= 0 AND porcentaje_valor <= 100),
    porcentaje_acumulado NUMERIC(5, 2) NOT NULL DEFAULT 0.00 CHECK (porcentaje_acumulado >= 0 AND porcentaje_acumulado <= 100),
    stock_actual INTEGER NOT NULL DEFAULT 0 CHECK (stock_actual >= 0),
    valor_stock NUMERIC(12, 2) DEFAULT 0.00 CHECK (valor_stock >= 0),
    rotacion_inventario NUMERIC(10, 4) DEFAULT 0.0000,
    fecha_analisis DATE NOT NULL,
    observaciones TEXT,
    fecha_creacion DATE NOT NULL DEFAULT CURRENT_DATE,
    usuario_creacion VARCHAR(50),
    
    -- Restricciones
    CONSTRAINT chk_periodo_valido CHECK (periodo_inicio <= periodo_fin)
);

-- Índices para optimización de consultas
CREATE INDEX idx_analisis_abc_sucursal ON analisis_abc(sucursal_id);
CREATE INDEX idx_analisis_abc_clasificacion ON analisis_abc(clasificacion);
CREATE INDEX idx_analisis_abc_fecha ON analisis_abc(fecha_analisis);
CREATE INDEX idx_analisis_abc_sku_sucursal ON analisis_abc(sku_producto, sucursal_id);
CREATE INDEX idx_analisis_abc_sucursal_fecha ON analisis_abc(sucursal_id, fecha_analisis);
CREATE INDEX idx_analisis_abc_valor_ventas ON analisis_abc(valor_ventas DESC);

-- Comentarios de documentación
COMMENT ON TABLE analisis_abc IS 'Análisis ABC de inventario - Clasificación de productos según valor (Pareto 80/20)';
COMMENT ON COLUMN analisis_abc.clasificacion IS 'A=alto valor (80%), B=medio (15%), C=bajo (5%)';
COMMENT ON COLUMN analisis_abc.porcentaje_valor IS 'Porcentaje del valor total de ventas';
COMMENT ON COLUMN analisis_abc.porcentaje_acumulado IS 'Porcentaje acumulado (determina clasificación)';
COMMENT ON COLUMN analisis_abc.rotacion_inventario IS 'Rotación: valor_ventas / valor_stock';
COMMENT ON COLUMN analisis_abc.observaciones IS 'Recomendaciones automáticas según clasificación';

-- ==================================================
-- FIN MÓDULO DE ANÁLISIS ABC
-- ==================================================

-- ==================================================
-- FIN DEL SCRIPT CONSOLIDADO
-- ==================================================
-- Para ejecutar este script en PostgreSQL:
-- psql -U postgres -d nexoo_almacen -f nexxo-hub-bd.sql
-- ==================================================
