-- ==================================================================
-- FLYWAY MIGRATION V30: PRO-02 Metas y Comisiones Escalables por Objetivos
-- NexooHub Almacén
-- ==================================================================
-- Descripción: Tablas para el sistema de metas OKR y comisiones
--              escalonadas por porcentaje de logro para empleados.
-- Autor: IA
-- Fecha: 2026-03-26
-- ==================================================================

-- 1. TABLAS NUEVAS

-- Reglas de comisión según rango de cumplimiento de meta
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

-- Meta de ventas mensual asignada a cada empleado
CREATE TABLE meta_ventas_empleado (
    id SERIAL PRIMARY KEY,
    empleado_id INT NOT NULL,
    mes INT NOT NULL,  -- 1 al 12
    anio INT NOT NULL,
    monto_meta DECIMAL(12,2) NOT NULL,              -- Cuota que debe alcanzar
    monto_ventas_actual DECIMAL(12,2) DEFAULT 0.00, -- Vendido hasta ahora
    fecha_asignacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_creacion VARCHAR(50) NOT NULL,
    fecha_actualizacion TIMESTAMP,
    usuario_actualizacion VARCHAR(50)
);

-- 2. FOREIGN KEYS
ALTER TABLE meta_ventas_empleado ADD CONSTRAINT fk_meta_ventas_empleado FOREIGN KEY (empleado_id) REFERENCES empleado(id);

-- 3. ÍNDICES DE OPTIMIZACIÓN
-- Un empleado solo puede tener 1 meta activa por mes y año
CREATE UNIQUE INDEX idx_meta_empleado_mes_anio ON meta_ventas_empleado(empleado_id, mes, anio);
CREATE INDEX idx_regla_comision_activo ON regla_comision_escalonada(activo);

-- ==================================================================
-- FIN DE MIGRACIÓN V30
-- ==================================================================
