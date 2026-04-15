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
