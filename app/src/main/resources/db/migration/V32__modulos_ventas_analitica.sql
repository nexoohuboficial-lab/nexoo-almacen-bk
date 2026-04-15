-- ==================================================================
-- FLYWAY MIGRATION V32: Tablas de Módulos de Ventas y Analítica
-- NexooHub Almacén
-- ==================================================================
-- Descripción: Crea las tablas que faltaban para los módulos de
--              devoluciones, cotizaciones, reservas, crédito y analítica.
-- Autor: IA
-- Fecha: 2026-04-15
-- ==================================================================

-- =============================================
-- 1. DEVOLUCIONES
-- =============================================
CREATE TABLE devolucion (
    id SERIAL PRIMARY KEY,
    venta_id INTEGER NOT NULL,
    sucursal_id INTEGER NOT NULL,
    motivo VARCHAR(500) NOT NULL,
    total_devuelto NUMERIC(12, 2) NOT NULL,
    metodo_reembolso VARCHAR(30) NOT NULL, -- EFECTIVO, TARJETA, NOTA_CREDITO
    fecha_devolucion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    usuario_autorizo VARCHAR(100),
    CONSTRAINT fk_devolucion_venta FOREIGN KEY (venta_id) REFERENCES venta(id),
    CONSTRAINT fk_devolucion_sucursal FOREIGN KEY (sucursal_id) REFERENCES sucursal(id)
);

CREATE TABLE detalle_devolucion (
    id SERIAL PRIMARY KEY,
    devolucion_id INTEGER NOT NULL,
    sku_interno VARCHAR(50) NOT NULL,
    cantidad INTEGER NOT NULL,
    precio_unitario NUMERIC(12, 2) NOT NULL,
    subtotal NUMERIC(12, 2) NOT NULL,
    motivo_item VARCHAR(300),
    CONSTRAINT fk_detalle_devolucion FOREIGN KEY (devolucion_id) REFERENCES devolucion(id) ON DELETE CASCADE,
    CONSTRAINT fk_detalle_devolucion_sku FOREIGN KEY (sku_interno) REFERENCES producto_maestro(sku_interno)
);

-- =============================================
-- 2. COTIZACIONES
-- =============================================
CREATE TABLE cotizacion (
    id BIGSERIAL PRIMARY KEY,
    folio VARCHAR(50) NOT NULL UNIQUE,
    cliente_id INTEGER NOT NULL,
    sucursal_id INTEGER NOT NULL,
    vendedor_id INTEGER,
    estado VARCHAR(20) NOT NULL, -- BORRADOR, ENVIADA, ACEPTADA, RECHAZADA, VENCIDA, CONVERTIDA
    fecha_cotizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_validez DATE NOT NULL,
    total NUMERIC(15, 2) NOT NULL,
    subtotal NUMERIC(15, 2),
    iva NUMERIC(15, 2),
    descuento_total NUMERIC(15, 2) DEFAULT 0.00,
    notas VARCHAR(1000),
    terminos_condiciones VARCHAR(2000),
    observaciones_internas VARCHAR(1000),
    fecha_aceptacion TIMESTAMP,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_creacion VARCHAR(50),
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_actualizacion VARCHAR(50),
    CONSTRAINT fk_cotizacion_cliente FOREIGN KEY (cliente_id) REFERENCES cliente(id),
    CONSTRAINT fk_cotizacion_sucursal FOREIGN KEY (sucursal_id) REFERENCES sucursal(id),
    CONSTRAINT fk_cotizacion_empleado FOREIGN KEY (vendedor_id) REFERENCES empleado(id)
);

CREATE TABLE detalle_cotizacion (
    id BIGSERIAL PRIMARY KEY,
    cotizacion_id BIGINT NOT NULL,
    sku_interno VARCHAR(50) NOT NULL,
    cantidad INTEGER NOT NULL,
    precio_unitario NUMERIC(15, 2) NOT NULL,
    descuento_especial NUMERIC(15, 2) DEFAULT 0.00,
    porcentaje_descuento NUMERIC(5, 2) DEFAULT 0.00,
    notas VARCHAR(500),
    CONSTRAINT fk_detalle_cotizacion FOREIGN KEY (cotizacion_id) REFERENCES cotizacion(id) ON DELETE CASCADE,
    CONSTRAINT fk_detalle_cotizacion_sku FOREIGN KEY (sku_interno) REFERENCES producto_maestro(sku_interno)
);

-- =============================================
-- 3. RESERVAS / APARTADOS
-- =============================================
CREATE TABLE reserva (
    id SERIAL PRIMARY KEY,
    cliente_id INTEGER NOT NULL,
    sku_interno VARCHAR(50) NOT NULL,
    sucursal_id INTEGER NOT NULL,
    cantidad INTEGER NOT NULL CHECK (cantidad > 0),
    estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE', -- PENDIENTE, NOTIFICADA, COMPLETADA, VENCIDA, CANCELADA
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_notificacion TIMESTAMP,
    fecha_vencimiento TIMESTAMP NOT NULL,
    fecha_finalizacion TIMESTAMP,
    venta_id INTEGER,
    comentarios VARCHAR(500),
    usuario_creacion VARCHAR(100),
    CONSTRAINT fk_reserva_cliente FOREIGN KEY (cliente_id) REFERENCES cliente(id),
    CONSTRAINT fk_reserva_sku FOREIGN KEY (sku_interno) REFERENCES producto_maestro(sku_interno),
    CONSTRAINT fk_reserva_sucursal FOREIGN KEY (sucursal_id) REFERENCES sucursal(id),
    CONSTRAINT fk_reserva_venta FOREIGN KEY (venta_id) REFERENCES venta(id)
);

-- =============================================
-- 4. CRÉDITO
-- =============================================
CREATE TABLE limite_credito (
    id SERIAL PRIMARY KEY,
    cliente_id INTEGER NOT NULL UNIQUE,
    limite_autorizado NUMERIC(12, 2) NOT NULL,
    saldo_utilizado NUMERIC(12, 2) NOT NULL DEFAULT 0.00,
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO', -- ACTIVO, BLOQUEADO, SUSPENDIDO, INACTIVO
    plazo_pago_dias INTEGER DEFAULT 30,
    max_facturas_vencidas INTEGER DEFAULT 3,
    permite_sobregiro BOOLEAN DEFAULT FALSE,
    monto_sobregiro NUMERIC(12, 2) DEFAULT 0.00,
    fecha_revision DATE,
    observaciones VARCHAR(1000),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_creacion VARCHAR(50),
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_actualizacion VARCHAR(50),
    CONSTRAINT fk_limite_credito_cliente FOREIGN KEY (cliente_id) REFERENCES cliente(id)
);

CREATE TABLE historial_credito (
    id SERIAL PRIMARY KEY,
    cliente_id INTEGER NOT NULL,
    venta_id INTEGER,
    tipo_movimiento VARCHAR(20) NOT NULL, -- CARGO, ABONO, AJUSTE, BLOQUEO, DESBLOQUEO
    monto NUMERIC(12, 2) NOT NULL,
    saldo_resultante NUMERIC(12, 2) NOT NULL,
    metodo_pago VARCHAR(30),
    folio_comprobante VARCHAR(50),
    concepto VARCHAR(500) NOT NULL,
    observaciones VARCHAR(1000),
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    usuario_creacion VARCHAR(50),
    CONSTRAINT fk_historial_credito_cliente FOREIGN KEY (cliente_id) REFERENCES cliente(id),
    CONSTRAINT fk_historial_credito_venta FOREIGN KEY (venta_id) REFERENCES venta(id)
);

-- =============================================
-- 5. ANALÍTICA
-- =============================================
CREATE TABLE prediccion_churn_cliente (
    id SERIAL PRIMARY KEY,
    cliente_id INTEGER NOT NULL,
    score_riesgo INTEGER NOT NULL CHECK (score_riesgo >= 0 AND score_riesgo <= 100),
    dias_sin_comprar INTEGER,
    frecuencia_promedio_dias INTEGER,
    factores_riesgo VARCHAR(500),
    fecha_analisis TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_creacion VARCHAR(50),
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_actualizacion VARCHAR(50),
    CONSTRAINT fk_churn_cliente FOREIGN KEY (cliente_id) REFERENCES cliente(id)
);

CREATE TABLE regla_asociacion_productos (
    id BIGSERIAL PRIMARY KEY,
    sku_origen VARCHAR(50) NOT NULL,
    sku_destino VARCHAR(50) NOT NULL,
    soporte DOUBLE PRECISION NOT NULL,
    confianza DOUBLE PRECISION NOT NULL,
    lift DOUBLE PRECISION NOT NULL,
    creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE reporte_rendimiento_empleado (
    id SERIAL PRIMARY KEY,
    empleado_id INTEGER NOT NULL,
    mes INTEGER NOT NULL,
    anio INTEGER NOT NULL,
    total_ventas INTEGER DEFAULT 0,
    monto_total_ventas NUMERIC(12, 2) DEFAULT 0.00,
    ticket_promedio NUMERIC(12, 2) DEFAULT 0.00,
    total_cotizaciones INTEGER DEFAULT 0,
    cotizaciones_convertidas INTEGER DEFAULT 0,
    tasa_conversion NUMERIC(5, 2) DEFAULT 0.00,
    total_devoluciones INTEGER DEFAULT 0,
    monto_devoluciones NUMERIC(12, 2) DEFAULT 0.00,
    tasa_devolucion NUMERIC(5, 2) DEFAULT 0.00,
    hora_pico INTEGER,
    fecha_calculo TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_rendimiento_empleado FOREIGN KEY (empleado_id) REFERENCES empleado(id)
);

-- =============================================
-- 6. INVENTARIO - ALERTAS LENTO MOVIMIENTO
-- =============================================
CREATE TABLE alerta_lento_movimiento (
    id SERIAL PRIMARY KEY,
    sku_interno VARCHAR(50) NOT NULL,
    sucursal_id INTEGER NOT NULL,
    dias_sin_venta INTEGER NOT NULL,
    ultima_venta DATE,
    stock_actual INTEGER NOT NULL,
    costo_inmovilizado NUMERIC(10, 2) NOT NULL DEFAULT 0.00,
    estado_alerta VARCHAR(20) NOT NULL, -- ADVERTENCIA, CRITICO, RESUELTA
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_creacion VARCHAR(50),
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_actualizacion VARCHAR(50),
    CONSTRAINT fk_alerta_lm_sku FOREIGN KEY (sku_interno) REFERENCES producto_maestro(sku_interno),
    CONSTRAINT fk_alerta_lm_sucursal FOREIGN KEY (sucursal_id) REFERENCES sucursal(id)
);

-- =============================================
-- 7. ÍNDICES DE OPTIMIZACIÓN
-- =============================================
CREATE INDEX idx_devolucion_venta ON devolucion(venta_id);
CREATE INDEX idx_devolucion_sucursal ON devolucion(sucursal_id);
CREATE INDEX idx_detalle_devolucion_devolucion ON detalle_devolucion(devolucion_id);

CREATE INDEX idx_cotizacion_cliente ON cotizacion(cliente_id);
CREATE INDEX idx_cotizacion_sucursal ON cotizacion(sucursal_id);
CREATE INDEX idx_cotizacion_estado ON cotizacion(estado);
CREATE INDEX idx_cotizacion_fecha ON cotizacion(fecha_cotizacion);
CREATE INDEX idx_cotizacion_validez ON cotizacion(fecha_validez);
CREATE INDEX idx_detalle_cotizacion_cotizacion ON detalle_cotizacion(cotizacion_id);
CREATE INDEX idx_detalle_cotizacion_producto ON detalle_cotizacion(sku_interno);

CREATE INDEX idx_reserva_cliente ON reserva(cliente_id);
CREATE INDEX idx_reserva_sku ON reserva(sku_interno);
CREATE INDEX idx_reserva_sucursal ON reserva(sucursal_id);
CREATE INDEX idx_reserva_estado ON reserva(estado);

CREATE INDEX idx_historial_credito_cliente ON historial_credito(cliente_id);
CREATE INDEX idx_historial_credito_tipo ON historial_credito(tipo_movimiento);

CREATE INDEX idx_prediccion_churn_cliente ON prediccion_churn_cliente(cliente_id);
CREATE INDEX idx_prediccion_churn_score ON prediccion_churn_cliente(score_riesgo);

CREATE UNIQUE INDEX idx_reporte_rendimiento_emp_mes_anio ON reporte_rendimiento_empleado(empleado_id, mes, anio);

CREATE INDEX idx_alerta_lm_sucursal ON alerta_lento_movimiento(sucursal_id);
CREATE INDEX idx_alerta_lm_sku ON alerta_lento_movimiento(sku_interno);
CREATE INDEX idx_alerta_lm_estado ON alerta_lento_movimiento(estado_alerta);
CREATE INDEX idx_alerta_lm_fecha ON alerta_lento_movimiento(fecha_creacion);

-- ==================================================================
-- FIN DE MIGRACIÓN V32
-- ==================================================================
