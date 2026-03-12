-- =====================================================
-- Migración: Métricas Financieras Consolidadas
-- Versión: V7
-- Descripción: Tabla para métricas financieras y KPIs ejecutivos
-- Autor: NexooHub Development Team
-- Fecha: 2025-01-11
-- =====================================================

-- =====================================================
-- TABLA: metrica_financiera
-- Propósito: Almacena snapshots de métricas financieras consolidadas por período
-- Responde: ¿Qué tan rentable es REALMENTE mi negocio?
-- =====================================================
CREATE TABLE metrica_financiera (
    id BIGSERIAL PRIMARY KEY,
    sucursal_id INTEGER,
    periodo_inicio DATE NOT NULL,
    periodo_fin DATE NOT NULL,
    tipo_periodo VARCHAR(20) NOT NULL,
    
    -- Métricas de Ventas
    ventas_totales NUMERIC(15,2) NOT NULL DEFAULT 0.00,
    numero_ventas INTEGER NOT NULL DEFAULT 0,
    ticket_promedio NUMERIC(15,2) NOT NULL DEFAULT 0.00,
    clientes_unicos INTEGER NOT NULL DEFAULT 0,
    
    -- Métodos de Pago
    ventas_efectivo NUMERIC(15,2) NOT NULL DEFAULT 0.00,
    ventas_credito NUMERIC(15,2) NOT NULL DEFAULT 0.00,
    
    -- Costos y Rentabilidad
    costo_ventas NUMERIC(15,2) NOT NULL DEFAULT 0.00,
    utilidad_bruta NUMERIC(15,2) NOT NULL DEFAULT 0.00,
    margen_bruto_porcentaje NUMERIC(5,2) NOT NULL DEFAULT 0.00,
    
    -- Gastos y Utilidad Neta
    gastos_operativos NUMERIC(15,2) NOT NULL DEFAULT 0.00,
    utilidad_neta NUMERIC(15,2) NOT NULL DEFAULT 0.00,
    margen_neto_porcentaje NUMERIC(5,2) NOT NULL DEFAULT 0.00,
    
    -- Campos de auditoría
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    
    -- Foreign Key
    CONSTRAINT fk_metrica_financiera_sucursal FOREIGN KEY (sucursal_id) 
        REFERENCES sucursal(id) ON DELETE CASCADE,
    
    -- Constraints de validación
    CONSTRAINT chk_metrica_financiera_periodo_valido 
        CHECK (periodo_fin >= periodo_inicio),
    CONSTRAINT chk_metrica_financiera_ventas_positivas 
        CHECK (ventas_totales >= 0),
    CONSTRAINT chk_metrica_financiera_costo_positivo 
        CHECK (costo_ventas >= 0),
    CONSTRAINT chk_metrica_financiera_gastos_positivos 
        CHECK (gastos_operativos >= 0),
    CONSTRAINT chk_metrica_financiera_numero_ventas_positivo 
        CHECK (numero_ventas >= 0),
    CONSTRAINT chk_metrica_financiera_clientes_positivos 
        CHECK (clientes_unicos >= 0),
    CONSTRAINT chk_metrica_financiera_tipo_periodo_valido 
        CHECK (tipo_periodo IN ('DIARIO', 'SEMANAL', 'MENSUAL', 'TRIMESTRAL', 'ANUAL', 'PERSONALIZADO')),
    
    -- Constraint único: Por período + sucursal
    CONSTRAINT uk_metrica_financiera_periodo_sucursal 
        UNIQUE (sucursal_id, periodo_inicio, periodo_fin)
);

-- Comentarios de documentación
COMMENT ON TABLE metrica_financiera IS 'Métricas financieras consolidadas - Dashboard Ejecutivo de KPIs';
COMMENT ON COLUMN metrica_financiera.sucursal_id IS 'Sucursal específica o NULL para métricas consolidadas de toda la empresa';
COMMENT ON COLUMN metrica_financiera.periodo_inicio IS 'Fecha de inicio del período analizado';
COMMENT ON COLUMN metrica_financiera.periodo_fin IS 'Fecha de fin del período analizado';
COMMENT ON COLUMN metrica_financiera.tipo_periodo IS 'Clasificación del período: DIARIO, SEMANAL, MENSUAL, TRIMESTRAL, ANUAL, PERSONALIZADO';

COMMENT ON COLUMN metrica_financiera.ventas_totales IS 'Suma total de ingresos por ventas en el período';
COMMENT ON COLUMN metrica_financiera.numero_ventas IS 'Cantidad de transacciones de venta realizadas';
COMMENT ON COLUMN metrica_financiera.ticket_promedio IS 'Valor promedio por transacción: Ventas Totales / Número de Ventas';
COMMENT ON COLUMN metrica_financiera.clientes_unicos IS 'Número de clientes diferentes que compraron';

COMMENT ON COLUMN metrica_financiera.ventas_efectivo IS 'Total de ventas pagadas en efectivo';
COMMENT ON COLUMN metrica_financiera.ventas_credito IS 'Total de ventas pagadas a crédito';

COMMENT ON COLUMN metrica_financiera.costo_ventas IS 'Costo de Bienes Vendidos (COGS) - Suma de costos promedio ponderado de productos vendidos';
COMMENT ON COLUMN metrica_financiera.utilidad_bruta IS 'Utilidad Bruta: Ventas Totales - Costo de Ventas';
COMMENT ON COLUMN metrica_financiera.margen_bruto_porcentaje IS 'Margen de Utilidad Bruta %: (Utilidad Bruta / Ventas Totales) × 100';

COMMENT ON COLUMN metrica_financiera.gastos_operativos IS 'Gastos operativos del período (comisiones pagadas a vendedores)';
COMMENT ON COLUMN metrica_financiera.utilidad_neta IS 'Utilidad Neta: Utilidad Bruta - Gastos Operativos';
COMMENT ON COLUMN metrica_financiera.margen_neto_porcentaje IS 'Margen de Utilidad Neta %: (Utilidad Neta / Ventas Totales) × 100';

-- Índices para consultas frecuentes
CREATE INDEX idx_metrica_financiera_periodo 
    ON metrica_financiera(periodo_inicio, periodo_fin);

CREATE INDEX idx_metrica_financiera_periodo_fin_desc 
    ON metrica_financiera(periodo_fin DESC);

CREATE INDEX idx_metrica_financiera_sucursal 
    ON metrica_financiera(sucursal_id);

CREATE INDEX idx_metrica_financiera_margen_desc 
    ON metrica_financiera(margen_bruto_porcentaje DESC);

CREATE INDEX idx_metrica_financiera_ventas_desc 
    ON metrica_financiera(ventas_totales DESC);

-- Índice compuesto para búsquedas consolidadas (sin sucursal)
CREATE INDEX idx_metrica_financiera_consolidado 
    ON metrica_financiera(periodo_inicio, periodo_fin) 
    WHERE sucursal_id IS NULL;

-- =====================================================
-- COMENTARIOS ADICIONALES DE NEGOCIO
-- =====================================================

COMMENT ON CONSTRAINT uk_metrica_financiera_periodo_sucursal ON metrica_financiera IS 
'Garantiza que no se dupliquen snapshots para el mismo período y sucursal';

COMMENT ON CONSTRAINT chk_metrica_financiera_tipo_periodo_valido ON metrica_financiera IS 
'Tipos de período válidos:
- DIARIO: 1 día
- SEMANAL: 7 días
- MENSUAL: ~30 días
- TRIMESTRAL: ~90 días
- ANUAL: ~365 días
- PERSONALIZADO: Cualquier otro rango';

-- =====================================================
-- REGLAS DE NEGOCIO IMPLEMENTADAS
-- =====================================================
-- 1. Utilidad Bruta = Ventas Totales - Costo de Ventas
-- 2. Margen Bruto % = (Utilidad Bruta / Ventas Totales) × 100
-- 3. Ticket Promedio = Ventas Totales / Número de Ventas
-- 4. Utilidad Neta = Utilidad Bruta - Gastos Operativos
-- 5. Margen Neto % = (Utilidad Neta / Ventas Totales) × 100
--
-- CLASIFICACIÓN DE MÁRGENES:
-- - EXCELENTE: >= 30%
-- - BUENO: >= 20%
-- - REGULAR: >= 10%
-- - BAJO: >= 0%
-- - NEGATIVO: < 0%
--
-- SALUD FINANCIERA:
-- - SALUDABLE: Margen >= 20% + Utilidad Neta > 0 + Ventas > 0
-- - ACEPTABLE: Utilidad Neta > 0 + Ventas > 0
-- - REQUIERE_ATENCION: Ventas > 0 pero margen bajo
-- - CRITICA: Sin ventas o utilidad neta negativa
-- =====================================================

-- =====================================================
-- DATOS DE EJEMPLO PARA TESTING
-- =====================================================
-- NOTA: Estos INSERT son solo para testing local.
-- En producción, los datos se generan desde la aplicación.
-- =====================================================

-- Ejemplo 1: Métricas consolidadas de enero 2024 (todas las sucursales)
INSERT INTO metrica_financiera (
    sucursal_id, periodo_inicio, periodo_fin, tipo_periodo,
    ventas_totales, numero_ventas, ticket_promedio, clientes_unicos,
    ventas_efectivo, ventas_credito,
    costo_ventas, utilidad_bruta, margen_bruto_porcentaje,
    gastos_operativos, utilidad_neta, margen_neto_porcentaje,
    created_by
) VALUES (
    NULL, '2024-01-01', '2024-01-31', 'MENSUAL',
    500000.00, 250, 2000.00, 120,
    300000.00, 200000.00,
    350000.00, 150000.00, 30.00,
    25000.00, 125000.00, 25.00,
    'SYSTEM'
);

-- Ejemplo 2: Métricas de sucursal específica (ID 1)
INSERT INTO metrica_financiera (
    sucursal_id, periodo_inicio, periodo_fin, tipo_periodo,
    ventas_totales, numero_ventas, ticket_promedio, clientes_unicos,
    ventas_efectivo, ventas_credito,
    costo_ventas, utilidad_bruta, margen_bruto_porcentaje,
    gastos_operativos, utilidad_neta, margen_neto_porcentaje,
    created_by
) VALUES (
    1, '2024-01-01', '2024-01-31', 'MENSUAL',
    250000.00, 150, 1666.67, 75,
    150000.00, 100000.00,
    175000.00, 75000.00, 30.00,
    12500.00, 62500.00, 25.00,
    'SYSTEM'
);

-- =====================================================
-- FIN DE MIGRACIÓN V7
-- =====================================================
