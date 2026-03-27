-- DRAFT DE SCHEMA CONSOLIDADO


-- ===========================
-- FROM V1__initial_schema.sql
-- ===========================
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
    condiciones_pago VARCHAR(100),
    dias_entrega_estimado INTEGER,
    activo BOOLEAN DEFAULT TRUE,
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

-- [V1 empleado removed in favor of V18]

CREATE TABLE usuario (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'ROLE_USER',
    empleado_id INTEGER REFERENCES empleado(id),
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
INSERT INTO usuario (id, username, password, role, empleado_id, usuario_creacion)
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
SELECT setval('usuario_id_seq', 1, true);
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

-- ===========================
-- FROM V2__comisiones_vendedores.sql
-- ===========================
-- ==================================================================
-- FLYWAY MIGRATION V2: Comisiones para Vendedores
-- NexooHub Almacén - Módulo de Comisiones
-- ==================================================================
-- Descripción: Agrega tablas para gestionar comisiones de vendedores
--              - Reglas de comisión configurables
--              - Cálculo automático de comisiones por periodo
--              - Control de aprobación y pago
-- Autor: NexooHub Development Team
-- Fecha: 2026-03-09
-- ==================================================================

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
    sku_producto VARCHAR(50) REFERENCES producto_maestro(sku_interno), -- Para comisiones específicas por producto
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
    vendedor_id INTEGER NOT NULL REFERENCES empleado(id) ON DELETE CASCADE,
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

-- ==================================================================
-- FIN DE MIGRACIÓN V2
-- ==================================================================

-- ===========================
-- FROM V3__prediccion_demanda.sql
-- ===========================
-- ==================================================================
-- FLYWAY MIGRATION V3: Predicción de Demanda
-- NexooHub Almacén - Módulo de Predicción de Demanda
-- ==================================================================
-- Descripción: Agrega tabla para almacenar predicciones de demanda
--              - Análisis histórico de ventas
--              - Proyección de demanda futura
--              - Recomendaciones de compra
-- Autor: NexooHub Development Team
-- Fecha: 2026-03-09
-- ==================================================================

-- Tabla de predicciones de demanda
CREATE TABLE prediccion_demanda (
    id SERIAL PRIMARY KEY,
    sku_producto VARCHAR(50) NOT NULL REFERENCES producto_maestro(sku_interno),
    sucursal_id INTEGER NOT NULL REFERENCES sucursal(id),
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

-- ===========================
-- FROM V4__analisis_abc_inventario.sql
-- ===========================
-- ==================================================================
-- FLYWAY MIGRATION V4: Análisis ABC de Inventario
-- NexooHub Almacén - Módulo de Análisis ABC
-- ==================================================================
-- Descripción: Agrega tabla para almacenar análisis ABC de inventario
--              - Clasificación de productos según su valor (Pareto 80/20)
--              - Seguimiento de rotación y valor de productos
--              - Recomendaciones de gestión según clasificación
-- Autor: NexooHub Development Team
-- Fecha: 2026-03-09
-- ==================================================================

-- Tabla de análisis ABC
CREATE TABLE analisis_abc (
    id SERIAL PRIMARY KEY,
    sku_producto VARCHAR(50) NOT NULL REFERENCES producto_maestro(sku_interno),
    sucursal_id INTEGER NOT NULL REFERENCES sucursal(id),
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

-- Índices para mejorar rendimiento de consultas
CREATE INDEX idx_analisis_abc_sucursal ON analisis_abc(sucursal_id);
CREATE INDEX idx_analisis_abc_clasificacion ON analisis_abc(clasificacion);
CREATE INDEX idx_analisis_abc_fecha ON analisis_abc(fecha_analisis);
CREATE INDEX idx_analisis_abc_sku_sucursal ON analisis_abc(sku_producto, sucursal_id);
CREATE INDEX idx_analisis_abc_sucursal_fecha ON analisis_abc(sucursal_id, fecha_analisis);
CREATE INDEX idx_analisis_abc_valor_ventas ON analisis_abc(valor_ventas DESC);

-- Comentarios de documentación
COMMENT ON TABLE analisis_abc IS 'Almacena análisis ABC de inventario para clasificar productos según su valor (Principio de Pareto 80/20)';
COMMENT ON COLUMN analisis_abc.clasificacion IS 'Clasificación del producto: A (alto valor), B (valor medio), C (bajo valor)';
COMMENT ON COLUMN analisis_abc.porcentaje_valor IS 'Porcentaje que representa del valor total de ventas';
COMMENT ON COLUMN analisis_abc.porcentaje_acumulado IS 'Porcentaje acumulado hasta este producto (determina clasificación)';
COMMENT ON COLUMN analisis_abc.rotacion_inventario IS 'Rotación de inventario: valor_ventas / valor_stock';
COMMENT ON COLUMN analisis_abc.observaciones IS 'Observaciones y recomendaciones automáticas según clasificación';

-- ===========================
-- FROM V5__programa_fidelidad.sql
-- ===========================
-- ==================================================================
-- FLYWAY MIGRATION V5: Programa de Fidelidad
-- NexooHub Almacén - Módulo de Lealtad de Clientes
-- ==================================================================
-- Descripción: Creación de tablas para el programa de fidelidad
--              que permite acumular y canjear puntos por compras.
-- Autor: NexooHub Development Team
-- Fecha: 2026-03-11
-- ==================================================================

-- ====================================
-- TABLA: programa_fidelidad
-- ====================================
-- Almacena el saldo de puntos acumulados por cliente.
-- Reglas de negocio:
--   - 1 punto por cada $10 MXN de compra
--   - 100 puntos = $10 MXN de descuento
--   - Los puntos no caducan
-- ====================================
CREATE TABLE programa_fidelidad (
    id SERIAL PRIMARY KEY,
    cliente_id INTEGER NOT NULL UNIQUE,
    puntos_acumulados INTEGER NOT NULL DEFAULT 0,
    total_compras NUMERIC(10, 2) NOT NULL DEFAULT 0.00,
    total_canjeado NUMERIC(10, 2) NOT NULL DEFAULT 0.00,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    
    -- Auditoría
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_creacion VARCHAR(50),
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_actualizacion VARCHAR(50),
    
    -- Foreign key
    CONSTRAINT fk_programa_cliente FOREIGN KEY (cliente_id) 
        REFERENCES cliente(id) ON DELETE CASCADE,
    
    -- Constraints
    CONSTRAINT ck_puntos_no_negativos CHECK (puntos_acumulados >= 0),
    CONSTRAINT ck_total_compras_positivo CHECK (total_compras >= 0),
    CONSTRAINT ck_total_canjeado_positivo CHECK (total_canjeado >= 0)
);

-- Índices para mejorar rendimiento
CREATE INDEX idx_programa_cliente ON programa_fidelidad(cliente_id);
CREATE INDEX idx_programa_activo ON programa_fidelidad(activo);

-- ====================================
-- TABLA: movimiento_punto
-- ====================================
-- Registra cada acumulación o canje de puntos.
-- Permite auditoría completa de movimientos.
-- ====================================
CREATE TABLE movimiento_punto (
    id SERIAL PRIMARY KEY,
    programa_id INTEGER NOT NULL,
    tipo_movimiento VARCHAR(20) NOT NULL, -- ACUMULACION, CANJE
    puntos INTEGER NOT NULL,
    monto_asociado NUMERIC(10, 2),
    venta_id INTEGER,
    descripcion VARCHAR(500) NOT NULL,
    
    -- Auditoría
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_creacion VARCHAR(50),
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_actualizacion VARCHAR(50),
    
    -- Foreign keys
    CONSTRAINT fk_movimiento_programa FOREIGN KEY (programa_id) 
        REFERENCES programa_fidelidad(id) ON DELETE CASCADE,
    CONSTRAINT fk_movimiento_venta FOREIGN KEY (venta_id) 
        REFERENCES venta(id) ON DELETE SET NULL,
    
    -- Constraints
    CONSTRAINT ck_tipo_movimiento_valido 
        CHECK (tipo_movimiento IN ('ACUMULACION', 'CANJE'))
);

-- Índices para consultas frecuentes
CREATE INDEX idx_movimiento_programa ON movimiento_punto(programa_id);
CREATE INDEX idx_movimiento_tipo ON movimiento_punto(tipo_movimiento);
CREATE INDEX idx_movimiento_venta ON movimiento_punto(venta_id);
CREATE INDEX idx_movimiento_fecha ON movimiento_punto(fecha_creacion);

-- ====================================
-- COMENTARIOS EN TABLAS
--====================================
COMMENT ON TABLE programa_fidelidad IS 'Almacena el saldo de puntos acumulados por cliente';
COMMENT ON COLUMN programa_fidelidad.puntos_acumulados IS 'Puntos disponibles para canjear';
COMMENT ON COLUMN programa_fidelidad.total_compras IS 'Suma total de compras que generaron puntos';
COMMENT ON COLUMN programa_fidelidad.total_canjeado IS 'Monto total canjeado en descuentos';

COMMENT ON TABLE movimiento_punto IS 'Auditoría de acumulaciones y canjes de puntos';
COMMENT ON COLUMN movimiento_punto.tipo_movimiento IS 'ACUMULACION o CANJE';
COMMENT ON COLUMN movimiento_punto.puntos IS 'Puntos del movimiento (positivo=acumulación, negativo=canje)';
COMMENT ON COLUMN movimiento_punto.monto_asociado IS 'Monto de compra (acumulación) o descuento (canje)';

-- ====================================
-- FIN DE MIGRACIÓN
-- ====================================

-- ===========================
-- FROM V6__rentabilidad_ventas_productos.sql
-- ===========================
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

-- ===========================
-- FROM V7__metricas_financieras.sql
-- ===========================
-- =====================================================
-- Migración: Métricas Financieras Consolidadas
-- Versión: V7
-- Descripción: Tabla para métricas financieras y KPIs ejecutivos
-- Autor: NexooHub Development Team
-- Fecha: 2025-01-11
-- =====================================================

-- =====================================================
-- TABLA: metrica_financiera
-- Propósito: Almacena snapshots de métricas financieras consolidadas por período
-- Responde: ¿Qué tan rentable es REALMENTE mi negocio?
-- =====================================================
CREATE TABLE metrica_financiera (
    id BIGSERIAL PRIMARY KEY,
    sucursal_id INTEGER,
    periodo_inicio DATE NOT NULL,
    periodo_fin DATE NOT NULL,
    tipo_periodo VARCHAR(20) NOT NULL,
    
    -- Métricas de Ventas
    ventas_totales NUMERIC(15,2) NOT NULL DEFAULT 0.00,
    numero_ventas INTEGER NOT NULL DEFAULT 0,
    ticket_promedio NUMERIC(15,2) NOT NULL DEFAULT 0.00,
    clientes_unicos INTEGER NOT NULL DEFAULT 0,
    
    -- Métodos de Pago
    ventas_efectivo NUMERIC(15,2) NOT NULL DEFAULT 0.00,
    ventas_credito NUMERIC(15,2) NOT NULL DEFAULT 0.00,
    
    -- Costos y Rentabilidad
    costo_ventas NUMERIC(15,2) NOT NULL DEFAULT 0.00,
    utilidad_bruta NUMERIC(15,2) NOT NULL DEFAULT 0.00,
    margen_bruto_porcentaje NUMERIC(5,2) NOT NULL DEFAULT 0.00,
    
    -- Gastos y Utilidad Neta
    gastos_operativos NUMERIC(15,2) NOT NULL DEFAULT 0.00,
    utilidad_neta NUMERIC(15,2) NOT NULL DEFAULT 0.00,
    margen_neto_porcentaje NUMERIC(5,2) NOT NULL DEFAULT 0.00,
    
    -- Campos de auditoría
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    
    -- Foreign Key
    CONSTRAINT fk_metrica_financiera_sucursal FOREIGN KEY (sucursal_id) 
        REFERENCES sucursal(id) ON DELETE CASCADE,
    
    -- Constraints de validación
    CONSTRAINT chk_metrica_financiera_periodo_valido 
        CHECK (periodo_fin >= periodo_inicio),
    CONSTRAINT chk_metrica_financiera_ventas_positivas 
        CHECK (ventas_totales >= 0),
    CONSTRAINT chk_metrica_financiera_costo_positivo 
        CHECK (costo_ventas >= 0),
    CONSTRAINT chk_metrica_financiera_gastos_positivos 
        CHECK (gastos_operativos >= 0),
    CONSTRAINT chk_metrica_financiera_numero_ventas_positivo 
        CHECK (numero_ventas >= 0),
    CONSTRAINT chk_metrica_financiera_clientes_positivos 
        CHECK (clientes_unicos >= 0),
    CONSTRAINT chk_metrica_financiera_tipo_periodo_valido 
        CHECK (tipo_periodo IN ('DIARIO', 'SEMANAL', 'MENSUAL', 'TRIMESTRAL', 'ANUAL', 'PERSONALIZADO')),
    
    -- Constraint único: Por período + sucursal
    CONSTRAINT uk_metrica_financiera_periodo_sucursal 
        UNIQUE (sucursal_id, periodo_inicio, periodo_fin)
);

-- Comentarios de documentación
COMMENT ON TABLE metrica_financiera IS 'Métricas financieras consolidadas - Dashboard Ejecutivo de KPIs';
COMMENT ON COLUMN metrica_financiera.sucursal_id IS 'Sucursal específica o NULL para métricas consolidadas de toda la empresa';
COMMENT ON COLUMN metrica_financiera.periodo_inicio IS 'Fecha de inicio del período analizado';
COMMENT ON COLUMN metrica_financiera.periodo_fin IS 'Fecha de fin del período analizado';
COMMENT ON COLUMN metrica_financiera.tipo_periodo IS 'Clasificación del período: DIARIO, SEMANAL, MENSUAL, TRIMESTRAL, ANUAL, PERSONALIZADO';

COMMENT ON COLUMN metrica_financiera.ventas_totales IS 'Suma total de ingresos por ventas en el período';
COMMENT ON COLUMN metrica_financiera.numero_ventas IS 'Cantidad de transacciones de venta realizadas';
COMMENT ON COLUMN metrica_financiera.ticket_promedio IS 'Valor promedio por transacción: Ventas Totales / Número de Ventas';
COMMENT ON COLUMN metrica_financiera.clientes_unicos IS 'Número de clientes diferentes que compraron';

COMMENT ON COLUMN metrica_financiera.ventas_efectivo IS 'Total de ventas pagadas en efectivo';
COMMENT ON COLUMN metrica_financiera.ventas_credito IS 'Total de ventas pagadas a crédito';

COMMENT ON COLUMN metrica_financiera.costo_ventas IS 'Costo de Bienes Vendidos (COGS) - Suma de costos promedio ponderado de productos vendidos';
COMMENT ON COLUMN metrica_financiera.utilidad_bruta IS 'Utilidad Bruta: Ventas Totales - Costo de Ventas';
COMMENT ON COLUMN metrica_financiera.margen_bruto_porcentaje IS 'Margen de Utilidad Bruta %: (Utilidad Bruta / Ventas Totales) × 100';

COMMENT ON COLUMN metrica_financiera.gastos_operativos IS 'Gastos operativos del período (comisiones pagadas a vendedores)';
COMMENT ON COLUMN metrica_financiera.utilidad_neta IS 'Utilidad Neta: Utilidad Bruta - Gastos Operativos';
COMMENT ON COLUMN metrica_financiera.margen_neto_porcentaje IS 'Margen de Utilidad Neta %: (Utilidad Neta / Ventas Totales) × 100';

-- Índices para consultas frecuentes
CREATE INDEX idx_metrica_financiera_periodo 
    ON metrica_financiera(periodo_inicio, periodo_fin);

CREATE INDEX idx_metrica_financiera_periodo_fin_desc 
    ON metrica_financiera(periodo_fin DESC);

CREATE INDEX idx_metrica_financiera_sucursal 
    ON metrica_financiera(sucursal_id);

CREATE INDEX idx_metrica_financiera_margen_desc 
    ON metrica_financiera(margen_bruto_porcentaje DESC);

CREATE INDEX idx_metrica_financiera_ventas_desc 
    ON metrica_financiera(ventas_totales DESC);

-- Índice compuesto para búsquedas consolidadas (sin sucursal)
CREATE INDEX idx_metrica_financiera_consolidado 
    ON metrica_financiera(periodo_inicio, periodo_fin) 
    WHERE sucursal_id IS NULL;

-- =====================================================
-- COMENTARIOS ADICIONALES DE NEGOCIO
-- =====================================================

COMMENT ON CONSTRAINT uk_metrica_financiera_periodo_sucursal ON metrica_financiera IS 
'Garantiza que no se dupliquen snapshots para el mismo período y sucursal';

COMMENT ON CONSTRAINT chk_metrica_financiera_tipo_periodo_valido ON metrica_financiera IS 
'Tipos de período válidos:
- DIARIO: 1 día
- SEMANAL: 7 días
- MENSUAL: ~30 días
- TRIMESTRAL: ~90 días
- ANUAL: ~365 días
- PERSONALIZADO: Cualquier otro rango';

-- =====================================================
-- REGLAS DE NEGOCIO IMPLEMENTADAS
-- =====================================================
-- 1. Utilidad Bruta = Ventas Totales - Costo de Ventas
-- 2. Margen Bruto % = (Utilidad Bruta / Ventas Totales) × 100
-- 3. Ticket Promedio = Ventas Totales / Número de Ventas
-- 4. Utilidad Neta = Utilidad Bruta - Gastos Operativos
-- 5. Margen Neto % = (Utilidad Neta / Ventas Totales) × 100
--
-- CLASIFICACIÓN DE MÁRGENES:
-- - EXCELENTE: >= 30%
-- - BUENO: >= 20%
-- - REGULAR: >= 10%
-- - BAJO: >= 0%
-- - NEGATIVO: < 0%
--
-- SALUD FINANCIERA:
-- - SALUDABLE: Margen >= 20% + Utilidad Neta > 0 + Ventas > 0
-- - ACEPTABLE: Utilidad Neta > 0 + Ventas > 0
-- - REQUIERE_ATENCION: Ventas > 0 pero margen bajo
-- - CRITICA: Sin ventas o utilidad neta negativa
-- =====================================================

-- =====================================================
-- DATOS DE EJEMPLO PARA TESTING
-- =====================================================
-- NOTA: Estos INSERT son solo para testing local.
-- En producción, los datos se generan desde la aplicación.
-- =====================================================

-- Ejemplo 1: Métricas consolidadas de enero 2024 (todas las sucursales)
INSERT INTO metrica_financiera (
    sucursal_id, periodo_inicio, periodo_fin, tipo_periodo,
    ventas_totales, numero_ventas, ticket_promedio, clientes_unicos,
    ventas_efectivo, ventas_credito,
    costo_ventas, utilidad_bruta, margen_bruto_porcentaje,
    gastos_operativos, utilidad_neta, margen_neto_porcentaje,
    created_by
) VALUES (
    NULL, '2024-01-01', '2024-01-31', 'MENSUAL',
    500000.00, 250, 2000.00, 120,
    300000.00, 200000.00,
    350000.00, 150000.00, 30.00,
    25000.00, 125000.00, 25.00,
    'SYSTEM'
);

-- Ejemplo 2: Métricas de sucursal específica (ID 1)
INSERT INTO metrica_financiera (
    sucursal_id, periodo_inicio, periodo_fin, tipo_periodo,
    ventas_totales, numero_ventas, ticket_promedio, clientes_unicos,
    ventas_efectivo, ventas_credito,
    costo_ventas, utilidad_bruta, margen_bruto_porcentaje,
    gastos_operativos, utilidad_neta, margen_neto_porcentaje,
    created_by
) VALUES (
    1, '2024-01-01', '2024-01-31', 'MENSUAL',
    250000.00, 150, 1666.67, 75,
    150000.00, 100000.00,
    175000.00, 75000.00, 30.00,
    12500.00, 62500.00, 25.00,
    'SYSTEM'
);

-- =====================================================
-- FIN DE MIGRACIÓN V7
-- =====================================================

-- ===========================
-- FROM V8__metricas_inventario.sql
-- ===========================
-- =====================================================
-- Migración: Métricas de Inventario
-- Versión: V8
-- Descripción: Tabla para métricas de inventario, rotación y capital inmovilizado
-- Autor: NexooHub Development Team
-- Fecha: 2025-01-11
-- =====================================================

-- =====================================================
-- TABLA: metrica_inventario
-- Propósito: Almacena snapshots de métricas de inventario consolidadas o por sucursal
-- Responde: ¿Cuánto capital tengo inmovilizado? ¿Rota bien mi inventario?
-- =====================================================
CREATE TABLE metrica_inventario (
    id BIGSERIAL PRIMARY KEY,
    fecha_corte DATE NOT NULL,
    sucursal_id INTEGER,
    nombre_sucursal VARCHAR(200),
    
    -- Métricas de Stock
    total_skus INTEGER NOT NULL DEFAULT 0,
    stock_disponible_total INTEGER NOT NULL DEFAULT 0,
    skus_bajo_stock INTEGER NOT NULL DEFAULT 0,
    skus_sin_stock INTEGER NOT NULL DEFAULT 0,
    skus_proximos_caducar INTEGER NOT NULL DEFAULT 0,
    
    -- Métricas de Valor (Capital Inmovilizado)
    valor_total_inventario NUMERIC(15,2) NOT NULL DEFAULT 0.00,
    costo_promedio_ponderado NUMERIC(15,2) NOT NULL DEFAULT 0.00,
    valor_stock_bajo NUMERIC(15,2) NOT NULL DEFAULT 0.00,
    
    -- Métricas de Rotación
    indice_rotacion NUMERIC(10,4) NOT NULL DEFAULT 0.0000,
    dias_inventario NUMERIC(10,2) NOT NULL DEFAULT 0.00,
    costo_ventas_periodo NUMERIC(15,2) NOT NULL DEFAULT 0.00,
    dias_periodo_rotacion INTEGER NOT NULL DEFAULT 30,
    
    -- Métricas de Eficiencia
    cobertura_dias NUMERIC(10,2),
    exactitud_porcentaje NUMERIC(5,2),
    tasa_quiebre_stock NUMERIC(5,2) NOT NULL DEFAULT 0.00,
    
    -- Clasificaciones
    salud_inventario VARCHAR(30) NOT NULL,
    clasificacion_rotacion VARCHAR(30) NOT NULL,
    
    -- Campos de auditoría
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    usuario_creacion VARCHAR(100),
    usuario_actualizacion VARCHAR(100),
    
    -- Foreign Key
    CONSTRAINT fk_metrica_inventario_sucursal FOREIGN KEY (sucursal_id) 
        REFERENCES sucursal(id) ON DELETE CASCADE,
    
    -- Restricción: Solo un snapshot por fecha y sucursal
    CONSTRAINT uk_metrica_inventario_fecha_sucursal UNIQUE (fecha_corte, sucursal_id)
);

-- =====================================================
-- ÍNDICES para optimización de consultas
-- =====================================================

-- Índice por fecha descendente (snapshots más recientes primero)
CREATE INDEX idx_metrica_inventario_fecha ON metrica_inventario (fecha_corte DESC);

-- Índice por sucursal para consultas filtradas
CREATE INDEX idx_metrica_inventario_sucursal ON metrica_inventario (sucursal_id);

-- Índice por valor total (top sucursales con más capital inmovilizado)
CREATE INDEX idx_metrica_inventario_valor_desc ON metrica_inventario (valor_total_inventario DESC);

-- Índice por rotación (identifica inventarios de lenta rotación)
CREATE INDEX idx_metrica_inventario_rotacion_desc ON metrica_inventario (indice_rotacion DESC);

-- =====================================================
-- COMENTARIOS en la tabla
-- =====================================================

COMMENT ON TABLE metrica_inventario IS 'Métricas consolidadas de inventario: capital inmovilizado, rotación, stock, eficiencia';
COMMENT ON COLUMN metrica_inventario.fecha_corte IS 'Fecha del snapshot de inventario';
COMMENT ON COLUMN metrica_inventario.sucursal_id IS 'ID de sucursal (NULL = consolidado de todas las sucursales)';
COMMENT ON COLUMN metrica_inventario.valor_total_inventario IS 'Capital total inmovilizado en inventario';
COMMENT ON COLUMN metrica_inventario.indice_rotacion IS 'Índice de rotación anualizado (COGS / Valor Inventario)';
COMMENT ON COLUMN metrica_inventario.dias_inventario IS 'Días promedio que dura el inventario (365 / Rotación)';
COMMENT ON COLUMN metrica_inventario.tasa_quiebre_stock IS 'Porcentaje de SKUs sin stock';
COMMENT ON COLUMN metrica_inventario.salud_inventario IS 'Clasificación: SALUDABLE, ACEPTABLE, REQUIERE_ATENCION, CRITICA';
COMMENT ON COLUMN metrica_inventario.clasificacion_rotacion IS 'Clasificación rotación: ALTA (>=12), MEDIA (>=6), BAJA (>=3), MUY_BAJA (<3)';

-- ===========================
-- FROM V9__metricas_venta_cliente.sql
-- ===========================
-- =====================================================
-- MIGRACIÓN V9: Métricas de Ventas y Clientes
-- =====================================================
-- Descripción: Crea la tabla para almacenar métricas consolidadas
--              de ventas, clientes y vendedores por período.
-- Responde: ¿Cómo está el rendimiento del equipo? ¿Qué clientes compran más?
-- =====================================================

-- Tabla: metrica_venta_cliente
CREATE TABLE metrica_venta_cliente (
    id BIGSERIAL PRIMARY KEY,
    
    -- Período
    periodo_inicio DATE NOT NULL,
    periodo_fin DATE NOT NULL,
    tipo_periodo VARCHAR(20) NOT NULL, -- DIARIO, SEMANAL, MENSUAL, TRIMESTRAL, ANUAL
    sucursal_id INTEGER,
    nombre_sucursal VARCHAR(200),
    
    -- Métricas de Ventas
    total_ventas DECIMAL(15,2) NOT NULL DEFAULT 0,
    numero_transacciones INTEGER NOT NULL DEFAULT 0,
    ticket_promedio DECIMAL(15,2) NOT NULL DEFAULT 0,
    venta_promedio_dia DECIMAL(15,2) NOT NULL DEFAULT 0,
    crecimiento_vs_anterior DECIMAL(10,2),
    
    -- Métricas de Clientes
    total_clientes_activos INTEGER NOT NULL DEFAULT 0,
    clientes_nuevos INTEGER NOT NULL DEFAULT 0,
    clientes_recurrentes INTEGER NOT NULL DEFAULT 0,
    clientes_inactivos INTEGER NOT NULL DEFAULT 0,
    tasa_retencion DECIMAL(5,2),
    valor_vida_cliente DECIMAL(15,2),
    frecuencia_compra DECIMAL(10,2),
    
    -- Métricas de Empleados (Vendedores)
    total_vendedores INTEGER NOT NULL DEFAULT 0,
    top_vendedor_id INTEGER,
    top_vendedor_nombre VARCHAR(200),
    top_vendedor_ventas DECIMAL(15,2),
    top_vendedor_transacciones INTEGER,
    venta_promedio_vendedor DECIMAL(15,2),
    
    -- Métricas por Método de Pago
    ventas_efectivo DECIMAL(15,2) NOT NULL DEFAULT 0,
    ventas_tarjeta DECIMAL(15,2) NOT NULL DEFAULT 0,
    ventas_credito DECIMAL(15,2) NOT NULL DEFAULT 0,
    porcentaje_efectivo DECIMAL(5,2),
    
    -- Auditoría
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    usuario_creacion VARCHAR(100),
    usuario_actualizacion VARCHAR(100),
    
    -- Constraints
    CONSTRAINT chk_periodo CHECK (periodo_fin >= periodo_inicio),
    CONSTRAINT chk_total_ventas CHECK (total_ventas >= 0),
    CONSTRAINT chk_numero_transacciones CHECK (numero_transacciones >= 0),
    CONSTRAINT chk_clientes_activos CHECK (total_clientes_activos >= 0),
    CONSTRAINT chk_clientes_nuevos CHECK (clientes_nuevos >= 0),
    CONSTRAINT chk_clientes_recurrentes CHECK (clientes_recurrentes >= 0),
    CONSTRAINT chk_vendedores CHECK (total_vendedores >= 0)
);

-- Índices para optimización de consultas
CREATE INDEX idx_metrica_venta_periodo ON metrica_venta_cliente (periodo_inicio DESC, periodo_fin DESC);
CREATE INDEX idx_metrica_venta_sucursal ON metrica_venta_cliente (sucursal_id);
CREATE INDEX idx_metrica_venta_total_desc ON metrica_venta_cliente (total_ventas DESC);
CREATE INDEX idx_metrica_venta_clientes_desc ON metrica_venta_cliente (total_clientes_activos DESC);

-- Comentarios de la tabla
COMMENT ON TABLE metrica_venta_cliente IS 'Métricas consolidadas de ventas, clientes y vendedores por período';

-- Comentarios de columnas clave
COMMENT ON COLUMN metrica_venta_cliente.periodo_inicio IS 'Fecha de inicio del período analizado';
COMMENT ON COLUMN metrica_venta_cliente.periodo_fin IS 'Fecha de fin del período analizado';
COMMENT ON COLUMN metrica_venta_cliente.tipo_periodo IS 'Tipo de período: DIARIO, SEMANAL, MENSUAL, TRIMESTRAL, ANUAL';
COMMENT ON COLUMN metrica_venta_cliente.sucursal_id IS 'ID de sucursal (NULL = consolidado de todas las sucursales)';
COMMENT ON COLUMN metrica_venta_cliente.total_ventas IS 'Total de ventas del período en valor monetario';
COMMENT ON COLUMN metrica_venta_cliente.numero_transacciones IS 'Número total de transacciones (ventas)';
COMMENT ON COLUMN metrica_venta_cliente.total_clientes_activos IS 'Total de clientes únicos que compraron';
COMMENT ON COLUMN metrica_venta_cliente.clientes_nuevos IS 'Nuevos clientes (primera compra en este período)';
COMMENT ON COLUMN metrica_venta_cliente.clientes_recurrentes IS 'Clientes que compraron antes y vuelven a comprar';
COMMENT ON COLUMN metrica_venta_cliente.tasa_retencion IS 'Tasa de retención de clientes (%)';
COMMENT ON COLUMN metrica_venta_cliente.valor_vida_cliente IS 'Valor de vida promedio del cliente (LTV)';
COMMENT ON COLUMN metrica_venta_cliente.total_vendedores IS 'Total de vendedores activos en el período';
COMMENT ON COLUMN metrica_venta_cliente.top_vendedor_id IS 'ID del vendedor con mejor desempeño';
COMMENT ON COLUMN metrica_venta_cliente.top_vendedor_ventas IS 'Total de ventas del top vendedor';
COMMENT ON COLUMN metrica_venta_cliente.ventas_efectivo IS 'Total de ventas pagadas en efectivo';
COMMENT ON COLUMN metrica_venta_cliente.ventas_tarjeta IS 'Total de ventas pagadas con tarjeta';
COMMENT ON COLUMN metrica_venta_cliente.ventas_credito IS 'Total de ventas a crédito';
COMMENT ON COLUMN metrica_venta_cliente.crecimiento_vs_anterior IS 'Comparación con período anterior (% de crecimiento)';

-- Trigger para actualizar fecha_actualizacion automáticamente
CREATE OR REPLACE FUNCTION actualizar_fecha_actualizacion_metrica_venta_cliente()
RETURNS TRIGGER AS $$
BEGIN
    NEW.fecha_actualizacion = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_actualizar_fecha_metrica_venta_cliente
BEFORE UPDATE ON metrica_venta_cliente
FOR EACH ROW
EXECUTE FUNCTION actualizar_fecha_actualizacion_metrica_venta_cliente();

-- =====================================================
-- FIN DE MIGRACIÓN V9
-- =====================================================

-- ===========================
-- FROM V10__metricas_operativas.sql
-- ===========================
-- ================================================
-- Migración V10: Métricas Operacionales
-- ================================================
-- Descripción: Tabla para almacenar snapshots de métricas operacionales que miden
--              la eficiencia de procesos: traspasos, compras, ventas y ratios de productividad.
-- 
-- Propósito:   Responder preguntas ejecutivas como:
--              - ¿Cuánto movimiento hay entre sucursales?
--              - ¿Qué tan eficientes son nuestras operaciones?
--              - ¿Cuál es la velocidad de rotación del inventario?
--              - ¿Estamos balanceados en entradas y salidas?
-- ================================================

-- Crear tabla metrica_operativa
CREATE TABLE metrica_operativa (
    -- Identificador único
    id BIGSERIAL PRIMARY KEY,
    
    -- ==========================================
    -- PERÍODO DE LA MÉTRICA
    -- ==========================================
    periodo_inicio DATE NOT NULL,
    periodo_fin DATE NOT NULL,
    tipo_periodo VARCHAR(20) NOT NULL CHECK (tipo_periodo IN ('DIARIO', 'SEMANAL', 'MENSUAL', 'TRIMESTRAL', 'ANUAL', 'PERSONALIZADO')),
    sucursal_id INTEGER REFERENCES sucursal(id),
    nombre_sucursal VARCHAR(100),
    dias_periodo INTEGER NOT NULL CHECK (dias_periodo >= 1),
    
    -- ==========================================
    -- MÉTRICAS DE TRASPASOS
    -- ==========================================
    total_traspasos INTEGER DEFAULT 0,
    unidades_traspaso_entrada INTEGER DEFAULT 0,
    unidades_traspaso_salida INTEGER DEFAULT 0,
    unidades_traspaso_neto INTEGER DEFAULT 0,
    
    -- ==========================================
    -- MÉTRICAS DE COMPRAS
    -- ==========================================
    total_compras INTEGER DEFAULT 0,
    unidades_compradas INTEGER DEFAULT 0,
    gasto_total_compras DECIMAL(15,2) DEFAULT 0,
    compra_promedio DECIMAL(15,2) DEFAULT 0,
    frecuencia_compras DECIMAL(10,2) DEFAULT 0,
    
    -- ==========================================
    -- MÉTRICAS DE VENTAS
    -- ==========================================
    total_ventas INTEGER DEFAULT 0,
    unidades_vendidas INTEGER DEFAULT 0,
    ingreso_total_ventas DECIMAL(15,2) DEFAULT 0,
    venta_promedio DECIMAL(15,2) DEFAULT 0,
    frecuencia_ventas DECIMAL(10,2) DEFAULT 0,
    
    -- ==========================================
    -- INDICADORES DE EFICIENCIA Y PRODUCTIVIDAD
    -- ==========================================
    ratio_entrada_salida DECIMAL(10,4) DEFAULT 0,
    productividad_diaria_ventas DECIMAL(15,2) DEFAULT 0,
    tasa_rotacion_inventario DECIMAL(10,4) DEFAULT 0,
    total_operaciones INTEGER DEFAULT 0,
    operaciones_promedio_dia DECIMAL(10,2) DEFAULT 0,
    clasificacion_actividad VARCHAR(20) CHECK (clasificacion_actividad IN ('ALTO', 'MEDIO', 'BAJO')),
    balance_operacional VARCHAR(20) CHECK (balance_operacional IN ('POSITIVO', 'NEGATIVO', 'EQUILIBRADO')),
    
    -- ==========================================
    -- AUDITORÍA
    -- ==========================================
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_creacion VARCHAR(100),
    usuario_actualizacion VARCHAR(100),
    CONSTRAINT chk_metrica_operativa_periodo_valido CHECK (periodo_fin >= periodo_inicio),
    CONSTRAINT chk_metrica_operativa_traspasos_positivos CHECK (total_traspasos >= 0 AND unidades_traspaso_entrada >= 0 AND unidades_traspaso_salida >= 0),
    CONSTRAINT chk_metrica_operativa_compras_positivas CHECK (total_compras >= 0 AND unidades_compradas >= 0 AND gasto_total_compras >= 0),
    CONSTRAINT chk_metrica_operativa_ventas_positivas CHECK (total_ventas >= 0 AND unidades_vendidas >= 0 AND ingreso_total_ventas >= 0),
    CONSTRAINT chk_metrica_operativa_operaciones_positivas CHECK (total_operaciones >= 0)
);

-- ==========================================
-- ÍNDICES PARA OPTIMIZACIÓN DE CONSULTAS
-- ==========================================

-- Índice para búsquedas por período (más recientes primero)
CREATE INDEX idx_metrica_operativa_periodo ON metrica_operativa(periodo_inicio DESC, periodo_fin DESC);

-- Índice para búsquedas por sucursal
CREATE INDEX idx_metrica_operativa_sucursal ON metrica_operativa(sucursal_id);

-- Índice para filtros por tipo de período
CREATE INDEX idx_metrica_operativa_tipo ON metrica_operativa(tipo_periodo);

-- Índice para ordenar por volumen de ventas
CREATE INDEX idx_metrica_operativa_ventas ON metrica_operativa(total_ventas DESC);

-- ==========================================
-- CONSTRAINTS ADICIONALES
-- ==========================================

-- Validar que fecha de fin sea posterior o igual a fecha de inicio


-- Validar que valores de conteo no sean negativos








-- ==========================================
-- TRIGGER PARA ACTUALIZAR FECHA_ACTUALIZACION
-- ==========================================

CREATE OR REPLACE FUNCTION actualizar_fecha_actualizacion_metrica_operativa()
RETURNS TRIGGER AS $$
BEGIN
    NEW.fecha_actualizacion = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_metrica_operativa_fecha_actualizacion
    BEFORE UPDATE ON metrica_operativa
    FOR EACH ROW
    EXECUTE FUNCTION actualizar_fecha_actualizacion_metrica_operativa();

-- ==========================================
-- COMENTARIOS DESCRIPTIVOS
-- ==========================================

COMMENT ON TABLE metrica_operativa IS 'Snapshots de métricas operacionales para medir eficiencia de procesos';

COMMENT ON COLUMN metrica_operativa.periodo_inicio IS 'Fecha de inicio del período analizado';
COMMENT ON COLUMN metrica_operativa.periodo_fin IS 'Fecha de fin del período analízado (inclusiva)';
COMMENT ON COLUMN metrica_operativa.tipo_periodo IS 'Tipo de período: DIARIO, SEMANAL, MENSUAL, TRIMESTRAL, ANUAL, PERSONALIZADO';
COMMENT ON COLUMN metrica_operativa.sucursal_id IS 'ID de sucursal (NULL = métrica consolidada de todas las sucursales)';
COMMENT ON COLUMN metrica_operativa.dias_periodo IS 'Número de días del período calculado';

COMMENT ON COLUMN metrica_operativa.total_traspasos IS 'Número total de traspasos realizados (rastreoIds únicos)';
COMMENT ON COLUMN metrica_operativa.unidades_traspaso_entrada IS 'Unidades recibidas por traspasos (ENTRADA_TRASPASO)';
COMMENT ON COLUMN metrica_operativa.unidades_traspaso_salida IS 'Unidades enviadas por traspasos (SALIDA_TRASPASO)';
COMMENT ON COLUMN metrica_operativa.unidades_traspaso_neto IS 'Balance neto de traspasos (entrada - salida)';

COMMENT ON COLUMN metrica_operativa.total_compras IS 'Número total de compras registradas';
COMMENT ON COLUMN metrica_operativa.unidades_compradas IS 'Total de unidades compradas (suma de detalles)';
COMMENT ON COLUMN metrica_operativa.gasto_total_compras IS 'Gasto total en compras del período';
COMMENT ON COLUMN metrica_operativa.compra_promedio IS 'Compra promedio (gasto total / número de compras)';
COMMENT ON COLUMN metrica_operativa.frecuencia_compras IS 'Frecuencia de compras (compras por día)';

COMMENT ON COLUMN metrica_operativa.total_ventas IS 'Número total de ventas realizadas';
COMMENT ON COLUMN metrica_operativa.unidades_vendidas IS 'Total de unidades vendidas (suma de detalles)';
COMMENT ON COLUMN metrica_operativa.ingreso_total_ventas IS 'Ingreso total por ventas del período';
COMMENT ON COLUMN metrica_operativa.venta_promedio IS 'Venta promedio (ingreso total / número de ventas)';
COMMENT ON COLUMN metrica_operativa.frecuencia_ventas IS 'Frecuencia de ventas (ventas por día)';

COMMENT ON COLUMN metrica_operativa.ratio_entrada_salida IS 'Ratio de entrada vs salida: (compras+traspasoEntrada)/(ventas+traspasoSalida). >1 = acumulando inventario, <1 = reduciendo inventario';
COMMENT ON COLUMN metrica_operativa.productividad_diaria_ventas IS 'Productividad de ventas (ingreso por día)';
COMMENT ON COLUMN metrica_operativa.tasa_rotacion_inventario IS 'Tasa de rotación: unidades vendidas / total de unidades movidas';
COMMENT ON COLUMN metrica_operativa.total_operaciones IS 'Total de operaciones (traspasos + compras + ventas)';
COMMENT ON COLUMN metrica_operativa.operaciones_promedio_dia IS 'Promedio de operaciones por día';
COMMENT ON COLUMN metrica_operativa.clasificacion_actividad IS 'Clasificación del período: ALTO (>75 op/día), MEDIO (25-75), BAJO (<25)';
COMMENT ON COLUMN metrica_operativa.balance_operacional IS 'Balance: POSITIVO (más entradas), NEGATIVO (más salidas), EQUILIBRADO';

-- ==========================================
-- DATOS DE EJEMPLO (OPCIONAL, COMENTADO)
-- ==========================================

-- Ejemplo de métrica consolidada mensual:
-- INSERT INTO metrica_operativa (
--     periodo_inicio, periodo_fin, tipo_periodo, dias_periodo,
--     total_traspasos, unidades_traspaso_entrada, unidades_traspaso_salida, unidades_traspaso_neto,
--     total_compras, unidades_compradas, gasto_total_compras, compra_promedio, frecuencia_compras,
--     total_ventas, unidades_vendidas, ingreso_total_ventas, venta_promedio, frecuencia_ventas,
--     ratio_entrada_salida, productividad_diaria_ventas, tasa_rotacion_inventario,
--     total_operaciones, operaciones_promedio_dia, clasificacion_actividad, balance_operacional
-- ) VALUES (
--     '2024-01-01', '2024-01-31', 'MENSUAL', 31,
--     45, 1200, 1000, 200,
--     12, 5000, 125000.00, 10416.67, 0.39,
--     380, 4500, 450000.00, 1184.21, 12.26,
--     1.15, 14516.13, 0.72,
--     437, 14.10, 'MEDIO', 'POSITIVO'
-- );

-- ===========================
-- FROM V11__Create_Market_Basket_Table.sql
-- ===========================
CREATE TABLE regla_asociacion_productos (
    id SERIAL PRIMARY KEY,
    sku_origen VARCHAR(100) NOT NULL,
    sku_destino VARCHAR(100) NOT NULL,
    soporte DECIMAL(5, 4) NOT NULL,
    confianza DECIMAL(5, 4) NOT NULL,
    lift DECIMAL(8, 4) NOT NULL,
    creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_regla_sku_origen ON regla_asociacion_productos(sku_origen);
CREATE INDEX idx_regla_lift ON regla_asociacion_productos(lift DESC);
CREATE INDEX idx_regla_confianza ON regla_asociacion_productos(confianza DESC);

-- ===========================
-- FROM V11__modulo_caja_arqueos.sql
-- ===========================
-- ==================================================================
-- FLYWAY MIGRATION V11: Módulo de Caja y Arqueos (POS-01)
-- NexooHub Almacén
-- ==================================================================
-- Descripción: Crea las tablas turno_caja y movimiento_caja para el
--              control de apertura de turnos, movimientos de efectivo
--              y arqueos de cierre (Arqueo Z).
-- Autor: IA (NexooHub Development)
-- Fecha: 2026-03-24
-- ==================================================================

-- 1. TABLAS NUEVAS

CREATE TABLE turno_caja (
    id                    SERIAL PRIMARY KEY,
    sucursal_id           INTEGER        NOT NULL,
    empleado_id           INTEGER        NOT NULL,
    fondo_inicial         NUMERIC(10,2)  NOT NULL DEFAULT 0.00,
    total_ventas_efectivo NUMERIC(10,2)  NOT NULL DEFAULT 0.00,
    total_ventas_tarjeta  NUMERIC(10,2)  NOT NULL DEFAULT 0.00,
    total_ventas_credito  NUMERIC(10,2)  NOT NULL DEFAULT 0.00,
    total_retiros         NUMERIC(10,2)  NOT NULL DEFAULT 0.00,
    total_ingresos_extra  NUMERIC(10,2)  NOT NULL DEFAULT 0.00,
    efectivo_esperado     NUMERIC(10,2)  NOT NULL DEFAULT 0.00,
    efectivo_real         NUMERIC(10,2)           DEFAULT NULL,
    diferencia            NUMERIC(10,2)           DEFAULT NULL,
    estado                VARCHAR(20)    NOT NULL DEFAULT 'ABIERTO',
                          -- ABIERTO | CERRADO
    observaciones         TEXT,
    fecha_apertura        TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_cierre          TIMESTAMP               DEFAULT NULL,
    fecha_creacion        TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    usuario_creacion      VARCHAR(50),
    fecha_actualizacion   TIMESTAMP               DEFAULT CURRENT_TIMESTAMP,
    usuario_actualizacion VARCHAR(50)
);

COMMENT ON TABLE turno_caja IS 'Registro de turnos de apertura/cierre de caja por empleado y sucursal';
COMMENT ON COLUMN turno_caja.efectivo_esperado IS 'Fondo inicial + ventas en efectivo + ingresos extra - retiros';
COMMENT ON COLUMN turno_caja.efectivo_real IS 'Efectivo físico contado por el empleado al cierre (arqueo Z)';
COMMENT ON COLUMN turno_caja.diferencia IS 'efectivo_real - efectivo_esperado (puede ser negativo)';

CREATE TABLE movimiento_caja (
    id               SERIAL PRIMARY KEY,
    turno_id         INTEGER       NOT NULL REFERENCES turno_caja(id),
    tipo             VARCHAR(30)   NOT NULL,
                     -- RETIRO | INGRESO_EXTRA | VENTA_EFECTIVO | VENTA_TARJETA | VENTA_CREDITO
    monto            NUMERIC(10,2) NOT NULL,
    concepto         VARCHAR(255),
    referencia       VARCHAR(100), -- ID venta, número de referencia bancaria, etc.
    fecha_movimiento TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_creacion   TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    usuario_creacion VARCHAR(50)
);

COMMENT ON TABLE movimiento_caja IS 'Detalle de cada movimiento de efectivo dentro de un turno de caja';

-- 2. DATOS SEMILLA (ninguno requerido para este módulo)

-- 3. REINICIO DE SECUENCIAS (ninguno requerido)

-- 4. ÍNDICES DE OPTIMIZACIÓN

CREATE INDEX idx_turno_caja_sucursal   ON turno_caja(sucursal_id);
CREATE INDEX idx_turno_caja_empleado   ON turno_caja(empleado_id);
CREATE INDEX idx_turno_caja_estado     ON turno_caja(estado);
CREATE INDEX idx_turno_caja_apertura   ON turno_caja(fecha_apertura);

CREATE INDEX idx_movimiento_caja_turno ON movimiento_caja(turno_id);
CREATE INDEX idx_movimiento_caja_tipo  ON movimiento_caja(tipo);
CREATE INDEX idx_movimiento_caja_fecha ON movimiento_caja(fecha_movimiento);

-- ==================================================================
-- FIN DE MIGRACIÓN V11
-- ==================================================================

-- ===========================
-- FROM V11__modulo_prediccion_churn.sql
-- ===========================
-- ==================================================================
-- FLYWAY MIGRATION V11: Predicción de Churn (Fuga de Clientes)
-- NexooHub Almacén
-- ==================================================================
-- Descripción: Tabla para almacenar el score de riesgo de fuga
--              de los clientes y los factores matemáticos asociados.
-- Autor: IA
-- Fecha: 2026-03-25
-- ==================================================================

-- 1. TABLAS NUEVAS
CREATE TABLE prediccion_churn_cliente (
    id SERIAL PRIMARY KEY,
    cliente_id INTEGER NOT NULL,
    score_riesgo INTEGER NOT NULL DEFAULT 0,
    dias_sin_comprar INTEGER,
    frecuencia_promedio_dias INTEGER,
    factores_riesgo VARCHAR(500),
    fecha_analisis TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_creacion VARCHAR(50),
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_actualizacion VARCHAR(50),
    CONSTRAINT fk_churn_cliente FOREIGN KEY (cliente_id) REFERENCES cliente(id)
);

-- 4. ÍNDICES DE OPTIMIZACIÓN
CREATE INDEX idx_prediccion_churn_cliente_id ON prediccion_churn_cliente(cliente_id);
CREATE INDEX idx_prediccion_churn_score ON prediccion_churn_cliente(score_riesgo);

-- ==================================================================
-- FIN DE MIGRACIÓN V11
-- ==================================================================

-- ===========================
-- FROM V12__terminales_bancarias.sql
-- ===========================
-- ==================================================================
-- FLYWAY MIGRATION V12: Terminales Bancarias (POS-02)
-- NexooHub Almacén
-- ==================================================================
-- Descripción: Crea la tabla para registrar la comunicación y logs
--              de transacciones con terminales bancarias (PinPads).
-- Autor: IA (NexooHub Development)
-- Fecha: 2026-03-24
-- ==================================================================

-- 1. TABLAS NUEVAS
CREATE TABLE log_transaccion_bancaria (
    id                    SERIAL PRIMARY KEY,
    referencia_venta      VARCHAR(50)   NOT NULL,
    monto                 NUMERIC(10,2) NOT NULL,
    tipo_operacion        VARCHAR(20)   NOT NULL, -- VENTA | DEVOLUCION | CANCELACION
    estatus               VARCHAR(20)   NOT NULL, -- PROCESANDO | APROBADO | RECHAZADO | CANCELADO | ERROR
    autorizacion_banco    VARCHAR(50)            DEFAULT NULL,
    terminal_id           VARCHAR(50)   NOT NULL,
    tarjeta_terminacion   VARCHAR(4)             DEFAULT NULL,
    marca_tarjeta         VARCHAR(20)            DEFAULT NULL, -- VISA | MASTERCARD | AMEX
    mensaje_respuesta     VARCHAR(255)           DEFAULT NULL,
    xml_request           TEXT,
    xml_response          TEXT,
    fecha_transaccion     TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_creacion        TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    usuario_creacion      VARCHAR(50),
    fecha_actualizacion   TIMESTAMP              DEFAULT CURRENT_TIMESTAMP,
    usuario_actualizacion VARCHAR(50)
);

-- 4. ÍNDICES DE OPTIMIZACIÓN
CREATE INDEX idx_log_trans_banc_ref     ON log_transaccion_bancaria(referencia_venta);
CREATE INDEX idx_log_trans_banc_estatus ON log_transaccion_bancaria(estatus);
CREATE INDEX idx_log_trans_banc_fecha   ON log_transaccion_bancaria(fecha_transaccion);

-- ==================================================================
-- FIN DE MIGRACIÓN V12
-- ==================================================================

-- ===========================
-- FROM V13__facturacion_electronica.sql
-- ===========================
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

-- ===========================
-- FROM V14__sincronizacion_offline.sql
-- ===========================
-- ==================================================================
-- FLYWAY MIGRATION V14: Sincronización Offline (POS-04)
-- NexooHub Almacén
-- ==================================================================
-- Descripción: Crea la tabla de control de lotes de sincronización
--              para subida de ventas offline.
-- Autor: IA (NexooHub Development)
-- Fecha: 2026-03-24
-- ==================================================================

CREATE TABLE lote_sincronizacion (
    id                    SERIAL PRIMARY KEY,
    codigo_lote           VARCHAR(50)   NOT NULL UNIQUE,
    sucursal_id           INTEGER       NOT NULL,
    caja_id               INTEGER       NOT NULL,
    usuario_id            INTEGER       NOT NULL,
    fecha_generacion      TIMESTAMP     NOT NULL, -- Cuándo se generó el lote en la caja local
    estatus               VARCHAR(20)   NOT NULL, -- PROCESADO | FALLIDO | PENDIENTE
    total_ventas          INTEGER       NOT NULL DEFAULT 0,
    ventas_procesadas     INTEGER       NOT NULL DEFAULT 0,
    monto_total_lote      NUMERIC(10,2) NOT NULL DEFAULT 0.00,
    payload_json          TEXT          NOT NULL, -- Respaldo del array JSON enviado
    errores_detalle       TEXT,                   -- Log de por qué falló
    fecha_sincronizacion  TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    intentos              INTEGER       NOT NULL DEFAULT 1
);

CREATE INDEX idx_lote_sync_sucursal ON lote_sincronizacion(sucursal_id);
CREATE INDEX idx_lote_sync_estatus  ON lote_sincronizacion(estatus);

-- ==================================================================
-- FIN DE MIGRACIÓN V14
-- ==================================================================

-- ===========================
-- FROM V15__cuentas_por_pagar_gastos.sql
-- ===========================
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

-- ===========================
-- FROM V16__tesoreria_contabilidad.sql
-- ===========================
-- ==================================================================
-- FLYWAY MIGRATION V16: Tesorería y Contabilidad (ERP-02)
-- NexooHub Almacén
-- ==================================================================

-- 1. Catálogo de Cuentas Contables (Activo, Pasivo, Capital, Ingresos, Gastos)
CREATE TABLE cuenta_contable (
    id                  SERIAL PRIMARY KEY,
    codigo              VARCHAR(20)   NOT NULL UNIQUE, -- ej. 100-01, 200-01
    nombre              VARCHAR(100)  NOT NULL,
    tipo_cuenta         VARCHAR(20)   NOT NULL, -- ACTIVO, PASIVO, CAPITAL, INGRESO, GASTO
    naturaleza          VARCHAR(15)   NOT NULL, -- DEUDORA, ACREEDORA
    nivel               INTEGER       NOT NULL DEFAULT 1, -- Para agrupaciones jerárquicas
    cuenta_padre_id     INTEGER REFERENCES cuenta_contable(id),
    activa              BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_cuenta_codigo ON cuenta_contable(codigo);
CREATE INDEX idx_cuenta_tipo   ON cuenta_contable(tipo_cuenta);

-- 2. Póliza Contable (Cabecera)
CREATE TABLE poliza_contable (
    id                  SERIAL PRIMARY KEY,
    numero_poliza       VARCHAR(50)   NOT NULL UNIQUE,
    fecha               DATE          NOT NULL,
    tipo_poliza         VARCHAR(20)   NOT NULL, -- DIARIO, INGRESO, EGRESO
    concepto            VARCHAR(255)  NOT NULL,
    total_cargo         NUMERIC(14,2) NOT NULL DEFAULT 0.00,
    total_abono         NUMERIC(14,2) NOT NULL DEFAULT 0.00,
    estatus             VARCHAR(20)   NOT NULL DEFAULT 'APLICADA', -- APLICADA, CANCELADA
    usuario_id          INTEGER       NOT NULL,
    created_at          TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_poliza_fecha ON poliza_contable(fecha);

-- 3. Movimiento de Póliza (Detalle - Partida doble)
CREATE TABLE movimiento_contable (
    id                  SERIAL PRIMARY KEY,
    poliza_id           INTEGER       NOT NULL REFERENCES poliza_contable(id) ON DELETE CASCADE,
    cuenta_id           INTEGER       NOT NULL REFERENCES cuenta_contable(id),
    concepto_detalle    VARCHAR(255),
    cargo               NUMERIC(14,2) NOT NULL DEFAULT 0.00,
    abono               NUMERIC(14,2) NOT NULL DEFAULT 0.00,
    created_at          TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_movimiento_poliza ON movimiento_contable(poliza_id);
CREATE INDEX idx_movimiento_cuenta ON movimiento_contable(cuenta_id);

-- ==================================================================
-- Inserción del Catálogo Básico para Pruebas y Arranque de Sistema
-- ==================================================================
INSERT INTO cuenta_contable (codigo, nombre, tipo_cuenta, naturaleza, nivel) VALUES
('100',  'Activo Circulante',      'ACTIVO',  'DEUDORA',   1),
('101',  'Bancos Nacionales',      'ACTIVO',  'DEUDORA',   2),
('102',  'Caja General',           'ACTIVO',  'DEUDORA',   2),
('105',  'Clientes',               'ACTIVO',  'DEUDORA',   2),
('200',  'Pasivo a Corto Plazo',   'PASIVO',  'ACREEDORA', 1),
('201',  'Proveedores',            'PASIVO',  'ACREEDORA', 2),
('300',  'Capital Contable',       'CAPITAL', 'ACREEDORA', 1),
('400',  'Ingresos',               'INGRESO', 'ACREEDORA', 1),
('401',  'Ventas Netas',           'INGRESO', 'ACREEDORA', 2),
('500',  'Costos',                 'GASTO',   'DEUDORA',   1),
('501',  'Costo de Ventas',        'GASTO',   'DEUDORA',   2),
('600',  'Gastos Operativos',      'GASTO',   'DEUDORA',   1),
('601',  'Gastos de Administración', 'GASTO', 'DEUDORA',   2),
('602',  'Gastos de Venta',        'GASTO',   'DEUDORA',   2);

-- ==================================================================
-- FIN DE MIGRACIÓN V16
-- ==================================================================

-- ===========================
-- FROM V17__logistica_entregas.sql
-- ===========================
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

-- ===========================
-- FROM V18__erp_nomina_completa.sql
-- ===========================
-- ==================================================================
-- FLYWAY MIGRATION V18: Nómina Completa y RRHH (ERP-04)
-- ==================================================================

-- 1. Empleados (indepentientes del login 'users', aunque pueden vincularse si entran al sistema)
CREATE TABLE empleado (
    id                  SERIAL PRIMARY KEY,
    usuario_id          INTEGER UNIQUE,
    sucursal_id         INTEGER NOT NULL REFERENCES sucursal(id),
    nombre              VARCHAR(150) NOT NULL,
    apellidos           VARCHAR(200),
    nombre_completo     VARCHAR(200),
    curp                VARCHAR(18) UNIQUE,
    rfc                 VARCHAR(13) UNIQUE,
    nss                 VARCHAR(15) UNIQUE,
    puesto              VARCHAR(100) NOT NULL,
    departamento        VARCHAR(50),
    salario_diario      NUMERIC(12,4) DEFAULT 0,
    fecha_contratacion  DATE DEFAULT CURRENT_DATE,
    fecha_ingreso       DATE,
    activo              BOOLEAN DEFAULT TRUE,
    estatus             VARCHAR(20) DEFAULT 'ACTIVO',
    fecha_creacion      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_creacion    VARCHAR(50),
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_actualizacion VARCHAR(50)
);

-- 2. Periodo de Nómina (Ej: Quincena 1 Enero 2026, Semana 12, etc.)
CREATE TABLE nomina_periodo (
    id                  SERIAL PRIMARY KEY,
    nombre              VARCHAR(100) NOT NULL,         -- "1ra Quincena Ene 2026"
    fecha_inicio        DATE         NOT NULL,
    fecha_fin           DATE         NOT NULL,
    tipo_periodo        VARCHAR(20)  NOT NULL,         -- SEMANAL, QUINCENAL, MENSUAL
    estatus             VARCHAR(20)  NOT NULL DEFAULT 'BORRADOR', -- BORRADOR, PAGADA, CANCELADA
    usuario_id          INTEGER      NOT NULL,         -- Quien generó el periodo
    created_at          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 3. Recibo de Nómina (El consolidado para 1 empleado en 1 periodo)
CREATE TABLE recibo_nomina (
    id                  SERIAL PRIMARY KEY,
    periodo_id          INTEGER      NOT NULL REFERENCES nomina_periodo(id),
    empleado_id         INTEGER      NOT NULL REFERENCES empleado(id),
    dias_trabajados     NUMERIC(4,2) NOT NULL DEFAULT 15.00,
    total_percepciones  NUMERIC(12,2) NOT NULL DEFAULT 0.00,
    total_deducciones   NUMERIC(12,2) NOT NULL DEFAULT 0.00,
    neto_pagar          NUMERIC(12,2) NOT NULL DEFAULT 0.00,
    metodo_pago         VARCHAR(50)  NOT NULL DEFAULT 'TRANSFERENCIA',
    created_at          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(periodo_id, empleado_id) -- Un empleado tiene máximo un recibo por periodo regular
);

-- 4. Detalle de Recibo (Conceptos de Percepción y Deducción)
CREATE TABLE recibo_nomina_detalle (
    id                  SERIAL PRIMARY KEY,
    recibo_id           INTEGER      NOT NULL REFERENCES recibo_nomina(id) ON DELETE CASCADE,
    tipo_concepto       VARCHAR(20)  NOT NULL,         -- PERCEPCION, DEDUCCION
    clave_sat           VARCHAR(10),                   -- Ej. "001" para sueldo, "002" para ISR
    descripcion         VARCHAR(100) NOT NULL,
    importe             NUMERIC(12,2) NOT NULL,
    created_at          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_empleado_sucursal ON empleado(sucursal_id);
CREATE INDEX idx_nomina_periodo_fechas ON nomina_periodo(fecha_inicio, fecha_fin);
CREATE INDEX idx_recibo_nomina_empleado ON recibo_nomina(empleado_id);
CREATE INDEX idx_recibo_detalle_recibo ON recibo_nomina_detalle(recibo_id);

-- ==================================================================
-- FIN DE MIGRACIÓN V18
-- ==================================================================

-- ===========================
-- FROM V19__erp_devoluciones_proveedor.sql
-- ===========================
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

-- ===========================
-- FROM V20__inventario_codigos_barras.sql
-- ===========================
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

-- ===========================
-- FROM V20__modulo_rendimiento_personal.sql
-- ===========================
-- ==================================================================
-- FLYWAY MIGRATION V20: Módulo ANA-04 - Analytics de Rendimiento de Personal
-- NexooHub Almacén
-- ==================================================================
-- Descripción: Crea la tabla de snapshots mensuales para los KPIs
--              de rendimiento de cada vendedor/empleado:
--              conversión, devoluciones, ticket promedio, hora pico.
-- Autor: IA (Antigravity)
-- Fecha: 2026-03-25
-- ==================================================================

-- 1. TABLA PRINCIPAL
CREATE TABLE reporte_rendimiento_empleado (
    id                       SERIAL PRIMARY KEY,
    empleado_id              INTEGER NOT NULL,
    mes                      INTEGER NOT NULL CHECK (mes BETWEEN 1 AND 12),
    anio                     INTEGER NOT NULL CHECK (anio >= 2000),

    -- KPIs de Ventas
    total_ventas             INTEGER DEFAULT 0,
    monto_total_ventas       NUMERIC(15,2) DEFAULT 0.00,
    ticket_promedio          NUMERIC(15,2) DEFAULT 0.00,

    -- KPIs de Conversión (Cotizaciones)
    total_cotizaciones       INTEGER DEFAULT 0,
    cotizaciones_convertidas INTEGER DEFAULT 0,
    tasa_conversion          NUMERIC(5,2) DEFAULT 0.00,  -- porcentaje 0.00 a 100.00

    -- KPIs de Calidad (Devoluciones)
    total_devoluciones       INTEGER DEFAULT 0,
    monto_devoluciones       NUMERIC(15,2) DEFAULT 0.00,
    tasa_devolucion          NUMERIC(5,2) DEFAULT 0.00,  -- porcentaje 0.00 a 100.00

    -- KPIs de Productividad
    hora_pico                INTEGER CHECK (hora_pico BETWEEN 0 AND 23), -- hora del día (0-23)

    -- Auditoría
    fecha_calculo            TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_creacion           TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_creacion         VARCHAR(50) DEFAULT 'SISTEMA',
    fecha_actualizacion      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_actualizacion    VARCHAR(50) DEFAULT 'SISTEMA',

    -- Restricción: solo 1 snapshot por empleado por mes/año
    CONSTRAINT uq_rendimiento_empleado_periodo UNIQUE (empleado_id, mes, anio)
);

-- 4. ÍNDICES DE OPTIMIZACIÓN
CREATE INDEX idx_rend_empleado_id ON reporte_rendimiento_empleado(empleado_id);
CREATE INDEX idx_rend_periodo ON reporte_rendimiento_empleado(anio, mes);
CREATE INDEX idx_rend_empleado_periodo ON reporte_rendimiento_empleado(empleado_id, anio, mes);

-- ==================================================================
-- FIN DE MIGRACIÓN V20
-- ==================================================================

-- ===========================
-- FROM V21__crm_garantias_tickets.sql
-- ===========================
-- ==================================================================
-- FLYWAY MIGRATION V21: CRM-01 Garantías y Tickets de Soporte
-- NexooHub Almacén
-- ==================================================================
-- Descripción: Tablas para el rastreo y resolución de garantías/tickets
-- Autor: IA
-- Fecha: 2026-03-24
-- ==================================================================

-- 1. TABLA: ticket_garantia
CREATE TABLE ticket_garantia (
    id SERIAL PRIMARY KEY,
    -- Referencias opcionales de venta y cliente para ligar el historial
    venta_id INT,
    cliente_id INT,
    
    -- Referencias físicas del producto defectuoso
    sku_producto VARCHAR(100) NOT NULL,
    numero_serie VARCHAR(100),
    
    -- Motivo del reclamo ingresado por el cliente
    motivo_reclamo TEXT NOT NULL,
    
    -- ABIERTO, EN_REVISION, RESUELTO, CERRADO
    estado VARCHAR(50) NOT NULL DEFAULT 'ABIERTO',
    
    -- Opcional (se llena al cerrar el ticket): CAMBIO_PIEZA, DEVOLUCION_DINERO, REPARACION, RECHAZADO
    resolucion VARCHAR(50),
    notas_internas TEXT,
    
    -- Timestamps
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_creacion VARCHAR(50),
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_actualizacion VARCHAR(50)
);

-- 2. TABLA: historial_garantia (Seguimiento de cambios en el ticket)
CREATE TABLE historial_garantia (
    id SERIAL PRIMARY KEY,
    ticket_id INT NOT NULL REFERENCES ticket_garantia(id) ON DELETE CASCADE,
    
    estado_anterior VARCHAR(50),
    estado_nuevo VARCHAR(50) NOT NULL,
    comentario TEXT NOT NULL,
    usuario_id INT NOT NULL, -- El asesor que hace el cambio
    
    -- Timestamps fijos
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. ÍNDICES DE OPTIMIZACIÓN
CREATE INDEX idx_ticket_garantia_venta_id ON ticket_garantia(venta_id);
CREATE INDEX idx_ticket_garantia_cliente_id ON ticket_garantia(cliente_id);
CREATE INDEX idx_ticket_garantia_sku ON ticket_garantia(sku_producto);
CREATE INDEX idx_historial_garantia_ticket_id ON historial_garantia(ticket_id);

-- ==================================================================
-- FIN DE MIGRACIÓN V21
-- ==================================================================

-- ===========================
-- FROM V22__crm_pipeline_b2b.sql
-- ===========================
-- ==================================================================
-- FLYWAY MIGRATION V22: CRM-02 Embudo de Ventas B2B
-- NexooHub Almacén
-- ==================================================================
-- Descripción: Creación de tablas base para Pipeline y prospectos
-- Autor: IA
-- Fecha: 2026-03-24
-- ==================================================================

-- 1. TABLA: PROSPECTO
CREATE TABLE prospecto (
    id SERIAL PRIMARY KEY,
    empresa VARCHAR(255) NOT NULL,
    rfc VARCHAR(20),
    contacto_principal VARCHAR(150),
    correo VARCHAR(255),
    telefono VARCHAR(50),
    estatus_viabilidad VARCHAR(50) DEFAULT 'NUEVO',
    notas TEXT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_creacion VARCHAR(50),
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_actualizacion VARCHAR(50)
);

-- 2. TABLA: OPORTUNIDAD_VENTA
CREATE TABLE oportunidad_venta (
    id SERIAL PRIMARY KEY,
    prospecto_id INTEGER NOT NULL REFERENCES prospecto(id),
    titulo VARCHAR(255) NOT NULL,
    valor_proyectado DECIMAL(15,2) DEFAULT 0.00,
    etapa VARCHAR(50) NOT NULL DEFAULT 'PROSPECTO',
    fecha_cierre_estimada DATE,
    probabilidad_porcentaje INTEGER DEFAULT 0 CHECK (probabilidad_porcentaje >= 0 AND probabilidad_porcentaje <= 100),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_creacion VARCHAR(50),
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_actualizacion VARCHAR(50)
);

-- 3. TABLA: INTERACCION_CRM
CREATE TABLE interaccion_crm (
    id SERIAL PRIMARY KEY,
    prospecto_id INTEGER REFERENCES prospecto(id),
    oportunidad_id INTEGER REFERENCES oportunidad_venta(id),
    tipo_interaccion VARCHAR(50) NOT NULL, -- LLAMADA, CORREO, VISITA, NOTA
    resumen VARCHAR(500) NOT NULL,
    detalles TEXT,
    fecha_interaccion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_creacion VARCHAR(50),
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_actualizacion VARCHAR(50)
);

-- 4. ÍNDICES DE OPTIMIZACIÓN
CREATE INDEX idx_prospecto_empresa ON prospecto(empresa);
CREATE INDEX idx_oportunidad_prospecto ON oportunidad_venta(prospecto_id);
CREATE INDEX idx_interaccion_prospecto ON interaccion_crm(prospecto_id);
CREATE INDEX idx_interaccion_oportunidad ON interaccion_crm(oportunidad_id);

-- ==================================================================
-- FIN DE MIGRACIÓN V22
-- ==================================================================

-- ===========================
-- FROM V23__crm_marketing_campanas.sql
-- ===========================
-- ==================================================================
-- FLYWAY MIGRATION V23: Automatización Marketing y Campañas
-- NexooHub Almacén
-- ==================================================================
-- Descripción: Tablas para gestionar campañas masivas de marketing
--              y el log individual de cada mensaje enviado a clientes.
-- Autor: IA
-- Fecha: 2026-03-24
-- ==================================================================

-- 1. TABLAS NUEVAS
CREATE TABLE campana_marketing (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    segmento_objetivo VARCHAR(100) NOT NULL,
    canal VARCHAR(20) NOT NULL,
    estado VARCHAR(20) NOT NULL,
    contenido_plantilla TEXT NOT NULL,
    fecha_programada TIMESTAMP,
    fecha_ejecucion TIMESTAMP,
    total_destinatarios INTEGER DEFAULT 0,
    creado_por_usuario_id INTEGER NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_creacion VARCHAR(50),
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_actualizacion VARCHAR(50)
);

CREATE TABLE log_envio_mensaje (
    id SERIAL PRIMARY KEY,
    campana_id INTEGER NOT NULL,
    cliente_id INTEGER NOT NULL,
    telefono_destino VARCHAR(50),
    email_destino VARCHAR(255),
    estado_envio VARCHAR(20) NOT NULL,
    mensaje_error TEXT,
    fecha_envio TIMESTAMP NOT NULL,
    fecha_entrega TIMESTAMP,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_creacion VARCHAR(50),
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_actualizacion VARCHAR(50),
    CONSTRAINT fk_log_mensaje_campana FOREIGN KEY (campana_id) REFERENCES campana_marketing(id),
    CONSTRAINT fk_log_mensaje_cliente FOREIGN KEY (cliente_id) REFERENCES cliente(id)
);

-- 4. ÍNDICES DE OPTIMIZACIÓN
CREATE INDEX idx_campana_estado ON campana_marketing(estado);
CREATE INDEX idx_log_mens_campana ON log_envio_mensaje(campana_id);
CREATE INDEX idx_log_mens_cliente ON log_envio_mensaje(cliente_id);

-- ==================================================================
-- FIN DE MIGRACIÓN V23
-- ==================================================================

-- ===========================
-- FROM V24__crm_nps_encuestas.sql
-- ===========================
-- ==================================================================
-- FLYWAY MIGRATION V24: Módulo CRM-04 Encuestas NPS
-- NexooHub Almacén
-- ==================================================================
-- Descripción: Tablas para encuestas de satisfacción y sus respuestas
-- Autor: IA
-- Fecha: 2026-03-25
-- ==================================================================

-- 1. TABLAS NUEVAS
CREATE TABLE encuesta_nps (
    id SERIAL PRIMARY KEY,
    venta_id INTEGER NOT NULL,
    cliente_id INTEGER NOT NULL,
    enlace_unico VARCHAR(255) NOT NULL,
    estado VARCHAR(50) DEFAULT 'ENVIADA', -- ENVIADA, RESPONDIDA, EXPIRADA
    fecha_envio TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_expiracion TIMESTAMP,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_creacion VARCHAR(50),
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_actualizacion VARCHAR(50)
);

CREATE TABLE respuesta_nps (
    id SERIAL PRIMARY KEY,
    encuesta_id INTEGER NOT NULL,
    score INTEGER NOT NULL CHECK (score >= 0 AND score <= 10),
    clasificacion VARCHAR(20) NOT NULL, -- PROMOTOR, PASIVO, DETRACTOR
    comentarios TEXT,
    fecha_respuesta TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_creacion VARCHAR(50),
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_actualizacion VARCHAR(50)
);

-- 4. ÍNDICES DE OPTIMIZACIÓN
CREATE INDEX idx_encuesta_nps_venta_id ON encuesta_nps(venta_id);
CREATE INDEX idx_encuesta_nps_cliente_id ON encuesta_nps(cliente_id);
CREATE UNIQUE INDEX idx_encuesta_nps_enlace_unico ON encuesta_nps(enlace_unico);
CREATE UNIQUE INDEX idx_respuesta_nps_encuesta_id ON respuesta_nps(encuesta_id);

-- ==================================================================
-- FIN DE MIGRACIÓN V24
-- ==================================================================

-- ===========================
-- FROM V25__ana_rfm.sql
-- ===========================
-- ==================================================================
-- FLYWAY MIGRATION V25: Analytics RFM - Segmentación de Clientes
-- NexooHub Almacén
-- ==================================================================
-- Descripción: Tabla para almacenar la métrica y calificación RFM de los clientes
-- Autor: IA
-- Fecha: 2026-03-25
-- ==================================================================

-- 1. TABLAS NUEVAS
CREATE TABLE segmento_rfm_cliente (
    id SERIAL PRIMARY KEY,
    cliente_id INTEGER NOT NULL REFERENCES cliente(id),
    recencia_dias INTEGER NOT NULL,
    frecuencia_compras INTEGER NOT NULL,
    monto_gastado DECIMAL(14,2) NOT NULL,
    score_r INTEGER NOT NULL,
    score_f INTEGER NOT NULL,
    score_m INTEGER NOT NULL,
    segmento VARCHAR(50) NOT NULL,
    fecha_calculo TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_creacion VARCHAR(50),
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_actualizacion VARCHAR(50),
    UNIQUE(cliente_id)
);

-- 4. ÍNDICES DE OPTIMIZACIÓN
CREATE INDEX idx_segmento_rfm_cliente_id ON segmento_rfm_cliente(cliente_id);
CREATE INDEX idx_segmento_rfm_segmento ON segmento_rfm_cliente(segmento);

-- ==================================================================
-- FIN DE MIGRACIÓN V25
-- ==================================================================

-- ===========================
-- FROM V26__pro_alertas_notificaciones.sql
-- ===========================
-- ==================================================================
-- FLYWAY MIGRATION V26: PRO-01 Motor de Alertas y Notificaciones Internas
-- NexooHub Almacén
-- ==================================================================
-- Descripción: Crea las tablas alerta_sistema, configuracion_alerta y
--              config_notificacion para el motor de alertas automáticas.
-- Autor: IA
-- Fecha: 2026-03-25
-- ==================================================================

-- 1. TABLAS NUEVAS

-- Alertas generadas por el sistema (stock bajo, CxC vencida, etc.)
CREATE TABLE alerta_sistema (
    id                  SERIAL PRIMARY KEY,
    tipo                VARCHAR(30)  NOT NULL,   -- STOCK_BAJO | CXC_VENCIDA | CHURN_RIESGO | META_EN_RIESGO
    mensaje             VARCHAR(500) NOT NULL,
    sucursal_id         INTEGER,
    usuario_destino_id  INTEGER,                  -- FK lógica al usuario autenticado
    resuelta            BOOLEAN      NOT NULL DEFAULT FALSE,
    leida               BOOLEAN      NOT NULL DEFAULT FALSE,
    fecha_creacion      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP             DEFAULT CURRENT_TIMESTAMP,
    usuario_creacion    VARCHAR(50),
    usuario_actualizacion VARCHAR(50)
);

-- Configuración de umbrales por sucursal (qué niveles disparan alertas)
CREATE TABLE configuracion_alerta (
    id                      SERIAL PRIMARY KEY,
    sucursal_id             INTEGER      NOT NULL UNIQUE,
    stock_minimo            INTEGER      NOT NULL DEFAULT 5,    -- unidades por debajo de las cuales se alerta
    dias_vencimiento_cxc    INTEGER      NOT NULL DEFAULT 30,   -- días de retraso para alertar CxC
    porcentaje_meta_alerta  INTEGER      NOT NULL DEFAULT 60,   -- % de avance de meta para alertar
    activo                  BOOLEAN      NOT NULL DEFAULT TRUE,
    fecha_creacion      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP             DEFAULT CURRENT_TIMESTAMP,
    usuario_creacion    VARCHAR(50),
    usuario_actualizacion VARCHAR(50)
);

-- Canal de notificación preferido por usuario
CREATE TABLE config_notificacion (
    id              SERIAL PRIMARY KEY,
    usuario_id      INTEGER      NOT NULL UNIQUE,
    canal           VARCHAR(20)  NOT NULL DEFAULT 'GMAIL',   -- GMAIL | TELEGRAM | AMBOS
    email_destino   VARCHAR(255),
    telegram_chat_id VARCHAR(100),
    activo          BOOLEAN      NOT NULL DEFAULT TRUE,
    fecha_creacion      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP             DEFAULT CURRENT_TIMESTAMP,
    usuario_creacion    VARCHAR(50),
    usuario_actualizacion VARCHAR(50)
);

-- 2. DATOS SEMILLA — configuración por defecto para sucursal 1
INSERT INTO configuracion_alerta (sucursal_id, stock_minimo, dias_vencimiento_cxc, porcentaje_meta_alerta)
VALUES (1, 5, 30, 60);

-- 3. ÍNDICES DE OPTIMIZACIÓN
CREATE INDEX idx_alerta_sistema_usuario      ON alerta_sistema(usuario_destino_id);
CREATE INDEX idx_alerta_sistema_tipo         ON alerta_sistema(tipo);
CREATE INDEX idx_alerta_sistema_leida        ON alerta_sistema(leida);
CREATE INDEX idx_alerta_sistema_sucursal     ON alerta_sistema(sucursal_id);
CREATE INDEX idx_config_notificacion_usuario ON config_notificacion(usuario_id);

-- ==================================================================
-- FIN DE MIGRACIÓN V26
-- ==================================================================

-- ==================================================================
-- MÓDULOS DE PROVEEDORES (SUP-01, SUP-02, SUP-03)
-- ==================================================================

-- SUP-01
CREATE TABLE catalogo_proveedor_producto (
    id SERIAL PRIMARY KEY,
    proveedor_id INTEGER REFERENCES proveedor(id),
    sku_interno VARCHAR(50) REFERENCES producto_maestro(sku_interno),
    sku_proveedor VARCHAR(100),
    precio_costo NUMERIC(10,2) NOT NULL,
    precio_venta_sugerido_proveedor NUMERIC(10,2),
    precios_incluyen_iva BOOLEAN DEFAULT FALSE,
    disponible BOOLEAN DEFAULT TRUE,
    fecha_actualizacion_precio TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    costo_sin_iva NUMERIC(10,2),
    precio_tecnico_calculado NUMERIC(10,2),
    precio_redondeado NUMERIC(10,2),
    margen_estimado NUMERIC(5,4),
    ultima_compra_costo NUMERIC(10,2),
    ultima_compra_fecha TIMESTAMP,
    notas TEXT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_creacion VARCHAR(50),
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_actualizacion VARCHAR(50),
    UNIQUE (proveedor_id, sku_interno)
);


-- ==================================================================
-- FLYWAY MIGRATION V2: Módulo SUP-02 Actualización Masiva Precios
-- NexooHub Almacén
-- ==================================================================
-- Descripción: Tabla para el historial de cambios de precios del catálogo de proveedores
-- Autor: IA
-- Fecha: 2026-03-26
-- ==================================================================

-- 1. TABLAS NUEVAS
CREATE TABLE historial_precio_proveedor (
    id SERIAL PRIMARY KEY,
    catalogo_id INTEGER NOT NULL REFERENCES catalogo_proveedor_producto(id),
    precio_costo_anterior NUMERIC(12,2),
    precio_costo_nuevo NUMERIC(12,2) NOT NULL,
    variacion_porcentual NUMERIC(5,2),
    motivo VARCHAR(255),
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_actualizacion VARCHAR(50)
);

-- 4. ÍNDICES DE OPTIMIZACIÓN
CREATE INDEX idx_historial_precio_proveedor_catalogo ON historial_precio_proveedor(catalogo_id);

-- ==================================================================
-- FIN DE MIGRACIÓN V2
-- ==================================================================

-- =========================================================================
-- NexooHub Almacén - Migración Base de Datos
-- Módulo: SUP-03 (Carrito de Compra y Órdenes de Compra)
-- =========================================================================

-- 1. Tabla: sesion_carrito_compra
-- Almacena los productos que un usuario 'ADMIN' ha agregado a su carrito, vinculándolos al catálogo del proveedor.
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
-- Almacena la orden de compra ya generada, lista para exportar/enviar y procesar la recepción.
CREATE TABLE orden_compra_proveedor (
    id SERIAL PRIMARY KEY,
    folio VARCHAR(50) NOT NULL UNIQUE,  -- Ej. OC-2024-00001
    proveedor_id INT NOT NULL,
    sucursal_id INT NOT NULL, -- La sucursal que emite la orden
    estado VARCHAR(30) NOT NULL, -- Enum: BORRADOR, ENVIADA, CONFIRMADA, RECIBIDA, CANCELADA
    total_estimado NUMERIC(12, 2) NOT NULL DEFAULT 0,
    notas TEXT,
    fecha_envio TIMESTAMP,
    fecha_esperada_entrega TIMESTAMP,
    -- Campos Auditables
    activo BOOLEAN NOT NULL DEFAULT true,
    creado_por VARCHAR(50) NOT NULL,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modificado_por VARCHAR(50),
    fecha_modificacion TIMESTAMP,
    CONSTRAINT fk_oc_proveedor FOREIGN KEY (proveedor_id) REFERENCES proveedor(id),
    CONSTRAINT fk_oc_sucursal FOREIGN KEY (sucursal_id) REFERENCES sucursal(id)
);

-- 3. Tabla: detalle_orden_compra (Detalle / Líneas)
-- Almacena los artículos específicos atados a una Orden de Compra.
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
    -- Campos Auditables
    activo BOOLEAN NOT NULL DEFAULT true,
    creado_por VARCHAR(50) NOT NULL,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modificado_por VARCHAR(50),
    fecha_modificacion TIMESTAMP,
    CONSTRAINT fk_detalle_oc FOREIGN KEY (orden_compra_id) REFERENCES orden_compra_proveedor(id) ON DELETE CASCADE
);

-- V30__pro_metas_comisiones.sql
-- Módulo PRO-02: Metas y Comisiones Escalables por Objetivos (OKR)

CREATE TABLE regla_comision_escalonada (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    porcentaje_minimo_logro DECIMAL(5,2) NOT NULL, -- Ej. 80.00
    porcentaje_maximo_logro DECIMAL(5,2) NOT NULL, -- Ej. 100.00
    porcentaje_comision DECIMAL(5,2) NOT NULL,     -- Ej. 2.00 (2% de las ventas)
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_creacion VARCHAR(50) NOT NULL,
    fecha_actualizacion TIMESTAMP,
    usuario_actualizacion VARCHAR(50)
);

CREATE TABLE meta_ventas_empleado (
    id SERIAL PRIMARY KEY,
    empleado_id INT NOT NULL,
    mes INT NOT NULL,  -- 1 al 12
    anio INT NOT NULL,
    monto_meta DECIMAL(12,2) NOT NULL,         -- Cuota que debe alcanzar
    monto_ventas_actual DECIMAL(12,2) DEFAULT 0.00, -- Vendido hasta ahora
    fecha_asignacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_creacion VARCHAR(50) NOT NULL,
    fecha_actualizacion TIMESTAMP,
    usuario_actualizacion VARCHAR(50)
);

-- Hacemos que un empleado solo tenga 1 meta activa por mes y año
CREATE UNIQUE INDEX idx_meta_empleado_mes_anio ON meta_ventas_empleado(empleado_id, mes, anio);

-- V31__pro_rbac_accesos.sql
-- Módulo PRO-03: Control de Acceso Basado en Roles (RBAC) y Sucursales Permitidas

-- 1. Tabla de roles lógicos
CREATE TABLE rol (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(50) UNIQUE NOT NULL, -- Ej. ROLE_ADMIN, ROLE_GERENTE_SUCURSAL
    descripcion VARCHAR(200),
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_creacion VARCHAR(50) DEFAULT 'system'
);

-- 2. Tabla de permisos atómicos
CREATE TABLE permiso (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) UNIQUE NOT NULL, -- Ej. VER_VENTAS, CREAR_ORDEN_COMPRA
    descripcion VARCHAR(200),
    modulo VARCHAR(50) -- Ej. 'COMPRAS', 'POS', 'RH'
);

-- 3. Tabla puente Rol -> Permiso
CREATE TABLE rol_permiso (
    rol_id INT NOT NULL,
    permiso_id INT NOT NULL,
    PRIMARY KEY (rol_id, permiso_id),
    FOREIGN KEY (rol_id) REFERENCES rol(id) ON DELETE CASCADE,
    FOREIGN KEY (permiso_id) REFERENCES permiso(id) ON DELETE CASCADE
);

-- 4. Tabla puente Usuario -> Rol
CREATE TABLE usuario_rol (
    usuario_id INT NOT NULL,
    rol_id INT NOT NULL,
    PRIMARY KEY (usuario_id, rol_id),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (rol_id) REFERENCES rol(id) ON DELETE CASCADE
);

-- 5. Tabla puente Usuario -> Sucursal Limitada
CREATE TABLE usuario_sucursal (
    usuario_id INT NOT NULL,
    sucursal_id INT NOT NULL,
    PRIMARY KEY (usuario_id, sucursal_id),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (sucursal_id) REFERENCES sucursal(id) ON DELETE CASCADE
);


-- ==========================================
-- SEEDS Y MIGRACIÓN DE DATOS (TRANSICIÓN DE COLUMNA 'ROLE')
-- ==========================================

-- A) Crear roles base que existían
INSERT INTO rol (nombre, descripcion) VALUES
('ROLE_ADMIN', 'Administrador Global del Sistema'),
('ROLE_GERENTE_SUCURSAL', 'Responsable completo de una sucursal'),
('ROLE_VENDEDOR', 'Vendedor de mostrador'),
('ROLE_CAJERO', 'Cajero POS'),
('ROLE_ALMACENISTA', 'Control de Inventario');

-- B) Crear permisos semilla (Core features)
INSERT INTO permiso (nombre, descripcion, modulo) VALUES
('ACCESO_GLOBAL', 'Acceso irrestricto', 'ROOT'),
('LEER_VENTA', 'Ver historial de ventas de sus sucursales', 'POS'),
('CREAR_VENTA', 'Realizar una venta nueva', 'POS'),
('LEER_COMPRA', 'Ver las compras de reabastecimiento', 'COMPRAS'),
('CREAR_COMPRA', 'Registrar ingresos de mercancía', 'COMPRAS'),
('GESTIONAR_PRECIOS', 'Actualizar catálogos del proveedor', 'COMPRAS'),
('GESTIONAR_METAS', 'Asignar cuotas (OKR) al personal', 'RH'),
('LEER_REPORTES', 'Acceso a tableros de analítica y BI', 'ANALITICA');

-- C) Asignar permisos al ADMIN (Tiene TODOS)
INSERT INTO rol_permiso (rol_id, permiso_id)
SELECT r.id, p.id FROM rol r CROSS JOIN permiso p WHERE r.nombre = 'ROLE_ADMIN';

-- D) Asignar permisos comunes al Gerente
INSERT INTO rol_permiso (rol_id, permiso_id)
SELECT r.id, p.id FROM rol r CROSS JOIN permiso p
WHERE r.nombre = 'ROLE_GERENTE_SUCURSAL'
  AND p.nombre IN ('LEER_VENTA', 'CREAR_VENTA', 'LEER_COMPRA', 'LEER_REPORTES');

-- E) Migrar los strings locales de 'role' en la tabla de usuarios hacia la tabla pivote usuario_rol
INSERT INTO usuario_rol (usuario_id, rol_id)
SELECT u.id, r.id 
FROM usuarios u
JOIN rol r ON u.role = r.nombre;

-- Opcional (Si el sistema crece a futuro, se haría DROP COLUMN role de 'usuarios'.
-- Lo mantendremos hasta culminar la integración estricta en el ORM Java para no quebrar sistemas de terceros).
