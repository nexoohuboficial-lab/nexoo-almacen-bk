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
