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

-- 2. FOREIGN KEYS
ALTER TABLE encuesta_nps ADD CONSTRAINT fk_encuesta_nps_venta FOREIGN KEY (venta_id) REFERENCES venta(id);
ALTER TABLE encuesta_nps ADD CONSTRAINT fk_encuesta_nps_cliente FOREIGN KEY (cliente_id) REFERENCES cliente(id);
ALTER TABLE respuesta_nps ADD CONSTRAINT fk_respuesta_nps_encuesta FOREIGN KEY (encuesta_id) REFERENCES encuesta_nps(id);

-- 3. ÍNDICES DE OPTIMIZACIÓN
CREATE INDEX idx_encuesta_nps_venta_id ON encuesta_nps(venta_id);
CREATE INDEX idx_encuesta_nps_cliente_id ON encuesta_nps(cliente_id);
CREATE UNIQUE INDEX idx_encuesta_nps_enlace_unico ON encuesta_nps(enlace_unico);
CREATE UNIQUE INDEX idx_respuesta_nps_encuesta_id ON respuesta_nps(encuesta_id);

-- ==================================================================
-- FIN DE MIGRACIÓN V24
-- ==================================================================
