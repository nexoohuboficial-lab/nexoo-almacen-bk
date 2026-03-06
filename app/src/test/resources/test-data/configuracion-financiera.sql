-- Insertar configuración financiera con ID=1 para tests
INSERT INTO configuracion_financiera (id, iva, margen_ganancia_base, gastos_fijos_mensuales, meta_ventas_mensual, comision_tarjeta, fecha_creacion, usuario_creacion)
VALUES (1, 0.16, 0.30, 15000.00, 150000.00, 0.03, CURRENT_TIMESTAMP, 'test');
