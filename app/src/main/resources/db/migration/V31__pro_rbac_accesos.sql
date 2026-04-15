-- ==================================================================
-- FLYWAY MIGRATION V31: PRO-03 Control de Acceso Basado en Roles (RBAC)
-- NexooHub Almacén
-- ==================================================================
-- Descripción: Crea las tablas de roles, permisos y relaciones usuario-rol,
--              usuario-sucursal para el control de acceso granular.
-- Autor: IA
-- Fecha: 2026-03-26
-- ==================================================================

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

-- ==================================================================
-- SEEDS Y DATOS BASE
-- ==================================================================

-- A) Crear roles base
INSERT INTO rol (nombre, descripcion) VALUES
('ROLE_ADMIN', 'Administrador Global del Sistema'),
('ROLE_GERENTE_SUCURSAL', 'Responsable completo de una sucursal'),
('ROLE_VENDEDOR', 'Vendedor de mostrador'),
('ROLE_CAJERO', 'Cajero POS'),
('ROLE_ALMACENISTA', 'Control de Inventario');

-- B) Crear permisos semilla
INSERT INTO permiso (nombre, descripcion, modulo) VALUES
('ACCESO_GLOBAL', 'Acceso irrestricto', 'ROOT'),
('LEER_VENTA', 'Ver historial de ventas de sus sucursales', 'POS'),
('CREAR_VENTA', 'Realizar una venta nueva', 'POS'),
('LEER_COMPRA', 'Ver las compras de reabastecimiento', 'COMPRAS'),
('CREAR_COMPRA', 'Registrar ingresos de mercancía', 'COMPRAS'),
('GESTIONAR_PRECIOS', 'Actualizar catálogos del proveedor', 'COMPRAS'),
('GESTIONAR_METAS', 'Asignar cuotas (OKR) al personal', 'RH'),
('LEER_REPORTES', 'Acceso a tableros de analítica y BI', 'ANALITICA');

-- C) Asignar todos los permisos al ROLE_ADMIN
INSERT INTO rol_permiso (rol_id, permiso_id)
SELECT r.id, p.id FROM rol r CROSS JOIN permiso p WHERE r.nombre = 'ROLE_ADMIN';

-- D) Asignar permisos al ROLE_GERENTE_SUCURSAL
INSERT INTO rol_permiso (rol_id, permiso_id)
SELECT r.id, p.id FROM rol r CROSS JOIN permiso p
WHERE r.nombre = 'ROLE_GERENTE_SUCURSAL'
  AND p.nombre IN ('LEER_VENTA', 'CREAR_VENTA', 'LEER_COMPRA', 'LEER_REPORTES');

-- E) Migrar usuario admin al nuevo sistema de roles
INSERT INTO usuario_rol (usuario_id, rol_id)
SELECT u.id, r.id
FROM usuarios u
JOIN rol r ON u.role = r.nombre
WHERE u.id = 1;

-- ==================================================================
-- FIN DE MIGRACIÓN V31
-- ==================================================================
