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
