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
