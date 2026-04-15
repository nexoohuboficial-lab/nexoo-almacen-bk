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
