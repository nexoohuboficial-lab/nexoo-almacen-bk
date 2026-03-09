-- ============================================================================
-- SCRIPT SQL: Alertas de Productos de Lento Movimiento
-- Descripción: Crea tabla y datos de prueba para alertas de baja rotación
-- Versión: 1.2.0
-- Fecha: 2025-03-09
-- ============================================================================

-- ==================================================
-- TABLA: alerta_lento_movimiento
-- ==================================================
-- Almacena alertas de productos sin ventas en X días
-- Permite tracking de productos de baja rotación

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
    
    -- Auditoría (herencia de AuditableEntity)
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    
    -- Claves foráneas
    FOREIGN KEY (sku_interno) REFERENCES producto_maestro(sku_interno) ON DELETE CASCADE,
    FOREIGN KEY (sucursal_id) REFERENCES sucursales(id) ON DELETE CASCADE
);

-- ==================================================
-- ÍNDICES PARA OPTIMIZACIÓN DE QUERIES
-- ==================================================
CREATE INDEX idx_alm_sucursal ON alerta_lento_movimiento(sucursal_id);
CREATE INDEX idx_alm_sku ON alerta_lento_movimiento(sku_interno);
CREATE INDEX idx_alm_estado ON alerta_lento_movimiento(estado_alerta);
CREATE INDEX idx_alm_fecha_deteccion ON alerta_lento_movimiento(fecha_deteccion);
CREATE INDEX idx_alm_resuelto ON alerta_lento_movimiento(resuelto);
CREATE INDEX idx_alm_dias_sin_venta ON alerta_lento_movimiento(dias_sin_venta DESC);

-- Índice compuesto para búsquedas comunes
CREATE INDEX idx_alm_sucursal_resuelto ON alerta_lento_movimiento(sucursal_id, resuelto);
CREATE UNIQUE INDEX idx_alm_sku_sucursal_activa ON alerta_lento_movimiento(sku_interno, sucursal_id) 
WHERE resuelto = false; -- Evita duplicados de alertas activas

-- ==================================================
-- COMENTARIOS EN LA BASE DE DATOS
-- ==================================================
COMMENT ON TABLE alerta_lento_movimiento IS 'Alertas de productos con baja rotación de inventario';
COMMENT ON COLUMN alerta_lento_movimiento.dias_sin_venta IS 'Días transcurridos desde la última venta';
COMMENT ON COLUMN alerta_lento_movimiento.costo_inmovilizado IS 'Stock actual × CPP del producto';
COMMENT ON COLUMN alerta_lento_movimiento.estado_alerta IS 'ADVERTENCIA (30-60 días), CRITICO (>60 días), RESUELTA';
COMMENT ON COLUMN alerta_lento_movimiento.accion_tomada IS 'LIQUIDACION, PROMOCION, TRANSFERENCIA, DESCONTINUADO, NINGUNA';

-- ==================================================
-- DATOS DE PRUEBA (Opcional - Solo para desarrollo)
-- ==================================================

-- Insertar algunas alertas de ejemplo
-- Nota: Asume que existen productos y sucursales con IDs 1, 2, 3

-- Alerta ADVERTENCIA (45 días sin venta)
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

-- Alerta CRITICO (75 días sin venta)
(
    'SUSP-005', 1, 75, CURRENT_DATE - INTERVAL '75 days',
    8, 6400.00, 'CRITICO',
    CURRENT_DATE - INTERVAL '5 days', false
),

-- Alerta ADVERTENCIA (35 días)
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

-- Actualizar estadísticas de la tabla
ANALYZE alerta_lento_movimiento;

-- ==================================================
-- STORED PROCEDURE (Opcional): Generar Alertas Automáticamente
-- ==================================================
-- Este procedimiento puede ser llamado por un CRON job diario

DELIMITER $$

CREATE PROCEDURE sp_generar_alertas_lento_movimiento(
    IN p_dias_minimos INT
)
BEGIN
    -- Detectar productos sin ventas en los últimos N días
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
-- EVENTO PROGRAMADO (Opcional): Ejecución Diaria Automática
-- ==================================================
-- Genera alertas automáticamente cada día a las 6:00 AM

CREATE EVENT IF NOT EXISTS evt_generar_alertas_diarias
ON SCHEDULE EVERY 1 DAY
STARTS CURRENT_DATE + INTERVAL 6 HOUR
DO
    CALL sp_generar_alertas_lento_movimiento(30);

-- Para habilitar el event scheduler (si está desactivado):
-- SET GLOBAL event_scheduler = ON;

-- ==================================================
-- LIMPIEZA PERIÓDICA (Opcional)
-- ==================================================
-- Eliminar alertas resueltas más antiguas que 1 año

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

-- Vista: Alertas críticas con información completa
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
-- PERMISOS (Opcional - ajustar según usuarios)
-- ==================================================

-- Para usuario de aplicación
GRANT SELECT, INSERT, UPDATE, DELETE ON alerta_lento_movimiento TO 'nexoo_app'@'localhost';
GRANT SELECT ON v_alertas_criticas TO 'nexoo_app'@'localhost';
GRANT SELECT ON v_costo_inmovilizado_por_sucursal TO 'nexoo_app'@'localhost';

-- Para usuario de reportes (solo lectura)
GRANT SELECT ON alerta_lento_movimiento TO 'nexoo_reportes'@'localhost';
GRANT SELECT ON v_alertas_criticas TO 'nexoo_reportes'@'localhost';
GRANT SELECT ON v_costo_inmovilizado_por_sucursal TO 'nexoo_reportes'@'localhost';

-- ==================================================
-- FIN DEL SCRIPT
-- ==================================================
-- Para ejecutar este script:
-- mysql -u root -p nexoo_almacen < alerta_lento_movimiento.sql
-- ==================================================
