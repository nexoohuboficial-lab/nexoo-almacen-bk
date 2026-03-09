-- ============================================================================
-- SISTEMA DE RESERVAS/APARTADOS - NEXOOHUB ALMACÉN
-- ============================================================================
-- Descripción: Script SQL para crear tabla de reservas y datos de ejemplo
-- Autor: NexooHub Development Team
-- Versión: 1.1.0
-- Fecha: Marzo 2026
-- ============================================================================

-- ============================================================================
-- 1. CREACIÓN DE TABLA reserva
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
    
    -- Claves foráneas
    CONSTRAINT fk_reserva_cliente FOREIGN KEY (cliente_id) REFERENCES cliente(id),
    CONSTRAINT fk_reserva_producto FOREIGN KEY (sku_interno) REFERENCES producto_maestro(sku_interno),
    CONSTRAINT fk_reserva_sucursal FOREIGN KEY (sucursal_id) REFERENCES sucursal(id),
    CONSTRAINT fk_reserva_venta FOREIGN KEY (venta_id) REFERENCES venta(id)
);

-- ============================================================================
-- 2. ÍNDICES PARA OPTIMIZACIÓN DE CONSULTAS
-- ============================================================================

-- Índice para búsquedas por cliente (historial de reservas del cliente)
CREATE INDEX IF NOT EXISTS idx_reserva_cliente 
ON reserva(cliente_id, fecha_creacion DESC);

-- Índice para búsquedas por estado (listar reservas pendientes, notificadas, etc.)
CREATE INDEX IF NOT EXISTS idx_reserva_estado 
ON reserva(estado, fecha_creacion DESC);

-- Índice para búsquedas de reservas por producto y sucursal
-- Crítico para notificar cuando llega mercancía
CREATE INDEX IF NOT EXISTS idx_reserva_producto_sucursal 
ON reserva(sku_interno, sucursal_id, estado, fecha_creacion);

-- Índice para identificar reservas vencidas (tarea programada)
CREATE INDEX IF NOT EXISTS idx_reserva_vencimiento 
ON reserva(estado, fecha_vencimiento);

-- Índice para reservas próximas a vencer (alertas)
CREATE INDEX IF NOT EXISTS idx_reserva_notificada_vencimiento 
ON reserva(estado, fecha_vencimiento) 
WHERE estado = 'NOTIFICADA';

-- ============================================================================
-- 3. COMENTARIOS EN TABLA Y COLUMNAS (DOCUMENTACIÓN)
-- ============================================================================

COMMENT ON TABLE reserva IS 'Reservas/apartados de productos cuando no hay stock disponible';

COMMENT ON COLUMN reserva.id IS 'ID único de la reserva';
COMMENT ON COLUMN reserva.cliente_id IS 'Cliente que realizó la reserva';
COMMENT ON COLUMN reserva.sku_interno IS 'Producto reservado';
COMMENT ON COLUMN reserva.sucursal_id IS 'Sucursal donde se recogerá el producto';
COMMENT ON COLUMN reserva.cantidad IS 'Cantidad de unidades reservadas';
COMMENT ON COLUMN reserva.estado IS 'Estado: PENDIENTE, NOTIFICADA, COMPLETADA, VENCIDA, CANCELADA';
COMMENT ON COLUMN reserva.fecha_creacion IS 'Fecha de creación de la reserva';
COMMENT ON COLUMN reserva.fecha_notificacion IS 'Fecha en que se notificó al cliente (mercancía disponible)';
COMMENT ON COLUMN reserva.fecha_vencimiento IS 'Fecha límite para recoger el producto';
COMMENT ON COLUMN reserva.fecha_finalizacion IS 'Fecha de completado/cancelación';
COMMENT ON COLUMN reserva.venta_id IS 'ID de venta generada al completar la reserva';
COMMENT ON COLUMN reserva.comentarios IS 'Observaciones del cliente o sistema';
COMMENT ON COLUMN reserva.usuario_registro IS 'Usuario que registró la reserva';

-- ============================================================================
-- 4. DATOS DE EJEMPLO (TESTING)
-- ============================================================================

-- Escenario 1: Reserva PENDIENTE (esperando mercancía)
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
    'Cliente urgente - llamar cuando llegue la mercancía',
    'admin'
);

-- Escenario 2: Reserva NOTIFICADA (mercancía llegó, cliente notificado)
-- Cliente fue notificado hace 2 días
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
-- Cliente recogió el producto hace 1 día
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

-- Escenario 4: Reserva VENCIDA (cliente no recogió a tiempo)
-- Reserva creada hace 15 días, venció hace 3 días
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
    'Cliente no contestó llamadas | Sistema marcó como VENCIDA automáticamente',
    'admin'
);

-- Escenario 5: Reserva CANCELADA (cliente ya no la necesita)
-- Cliente canceló hace 5 días
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
    'Cliente solicitó cancelación | CANCELACIÓN: Cliente compró en otra tienda',
    'admin'
);

-- Escenario 6: Reserva NOTIFICADA próxima a vencer (alerta)
-- Mercancía disponible, vence mañana
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
    'URGENTE: Cliente tiene solo 1 día para recoger',
    'vendedor1'
);

-- ============================================================================
-- 5. CONSULTAS ÚTILES PARA TESTING
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

-- Reservas próximas a vencer (últimos 2 días)
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
