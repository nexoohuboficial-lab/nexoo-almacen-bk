#!/bin/bash

# =====================================================
# CURLs de Prueba: API de Métricas de Ventas y Clientes
# =====================================================
# Descripción: Ejemplos de CURLs para probar los endpoints
#              de métricas de ventas, clientes y vendedores.
# =====================================================

BASE_URL="http://localhost:8080"

echo "==========================================="
echo "API MÉTRICAS DE VENTAS Y CLIENTES - PRUEBAS"
echo "==========================================="

# =====================================================
# 1. ANÁLISIS COMPLETO DEL MES ACTUAL
# =====================================================
echo ""
echo "1. Análisis del mes actual:"
curl -X GET "${BASE_URL}/api/metricas/ventas-clientes/mes-actual" \
  -H "Content-Type: application/json" \
  | json_pp

# =====================================================
# 2. ANÁLISIS DEL MES ANTERIOR
# =====================================================
echo ""
echo "2. Análisis del mes anterior:"
curl -X GET "${BASE_URL}/api/metricas/ventas-clientes/mes-anterior" \
  -H "Content-Type: application/json" \
  | json_pp

# =====================================================
# 3. ANÁLISIS DE LOS ÚLTIMOS 7 DÍAS
# =====================================================
echo ""
echo "3. Análisis de los últimos 7 días:"
curl -X GET "${BASE_URL}/api/metricas/ventas-clientes/ultimos-7-dias" \
  -H "Content-Type: application/json" \
  | json_pp

# =====================================================
# 4. ANÁLISIS PERSONALIZADO CON COMPARACIÓN
# =====================================================
echo ""
echo "4. Análisis personalizado enero 2024 con comparación:"
curl -X POST "${BASE_URL}/api/metricas/ventas-clientes/analisis" \
  -H "Content-Type: application/json" \
  -d '{
    "fechaInicio": "2024-01-01",
    "fechaFin": "2024-01-31",
    "tipoPeriodo": "MENSUAL",
    "compararPeriodoAnterior": true,
    "incluirDetalleVendedores": true,
    "incluirDetalleClientes": true,
    "limitTopVendedores": 5,
    "limitTopClientes": 10
  }' \
  | json_pp

# =====================================================
# 5. ANÁLISIS TRIMESTRAL
# =====================================================
echo ""
echo "5. Análisis trimestral Q1 2024:"
curl -X POST "${BASE_URL}/api/metricas/ventas-clientes/analisis" \
  -H "Content-Type: application/json" \
  -d '{
    "fechaInicio": "2024-01-01",
    "fechaFin": "2024-03-31",
    "tipoPeriodo": "TRIMESTRAL",
    "compararPeriodoAnterior": true,
    "incluirDetalleVendedores": true,
    "incluirDetalleClientes": true,
    "limitTopVendedores": 10,
    "limitTopClientes": 20
  }' \
  | json_pp

# =====================================================
# 6. ANÁLISIS POR SUCURSAL ESPECÍFICA
# =====================================================
echo ""
echo "6. Análisis de sucursal 1 (enero 2024):"
curl -X POST "${BASE_URL}/api/metricas/ventas-clientes/por-sucursal/1" \
  -H "Content-Type: application/json" \
  -d '{
    "fechaInicio": "2024-01-01",
    "fechaFin": "2024-01-31",
    "tipoPeriodo": "MENSUAL",
    "compararPeriodoAnterior": false,
    "incluirDetalleVendedores": true,
    "incluirDetalleClientes": true,
    "limitTopVendedores": 3,
    "limitTopClientes": 5
  }' \
  | json_pp

# =====================================================
# 7. ANÁLISIS RÁPIDO SIN DETALLE (SOLO RESUMEN)
# =====================================================
echo ""
echo "7. Análisis rápido sin detalle:"
curl -X POST "${BASE_URL}/api/metricas/ventas-clientes/analisis" \
  -H "Content-Type: application/json" \
  -d '{
    "fechaInicio": "2024-01-01",
    "fechaFin": "2024-01-31",
    "tipoPeriodo": "MENSUAL",
    "compararPeriodoAnterior": false,
    "incluirDetalleVendedores": false,
    "incluirDetalleClientes": false
  }' \
  | json_pp

# =====================================================
# 8. GENERAR Y GUARDAR ANÁLISIS
# =====================================================
echo ""
echo "8. Generar y guardar análisis en base de datos:"
curl -X POST "${BASE_URL}/api/metricas/ventas-clientes/generar-guardar" \
  -H "Content-Type: application/json" \
  -d '{
    "fechaInicio": "2024-01-01",
    "fechaFin": "2024-01-31",
    "tipoPeriodo": "MENSUAL",
    "compararPeriodoAnterior": true,
    "incluirDetalleVendedores": true,
    "incluirDetalleClientes": true,
    "limitTopVendedores": 5,
    "limitTopClientes": 10
  }' \
  | json_pp

# =====================================================
# 9. CONSULTAR MÉTRICA CONSOLIDADA GUARDADA
# =====================================================
echo ""
echo "9. Consultar métrica consolidada guardada (enero 2024):"
curl -X GET "${BASE_URL}/api/metricas/ventas-clientes/consolidado?fechaInicio=2024-01-01&fechaFin=2024-01-31" \
  -H "Content-Type: application/json" \
  | json_pp

# =====================================================
# 10. OBTENER HISTORIAL DE MÉTRICAS MENSUALES
# =====================================================
echo ""
echo "10. Historial de métricas mensuales hasta diciembre 2024:"
curl -X GET "${BASE_URL}/api/metricas/ventas-clientes/historial?tipoPeriodo=MENSUAL&fechaHasta=2024-12-31" \
  -H "Content-Type: application/json" \
  | json_pp

# =====================================================
# 11. ANÁLISIS SEMANAL
# =====================================================
echo ""
echo "11. Análisis semanal (15-21 enero):"
curl -X POST "${BASE_URL}/api/metricas/ventas-clientes/analisis" \
  -H "Content-Type: application/json" \
  -d '{
    "fechaInicio": "2024-01-15",
    "fechaFin": "2024-01-21",
    "tipoPeriodo": "SEMANAL",
    "compararPeriodoAnterior": true,
    "incluirDetalleVendedores": true,
    "incluirDetalleClientes": true,
    "limitTopVendedores": 3,
    "limitTopClientes": 5
  }' \
  | json_pp

# =====================================================
# 12. ANÁLISIS CONSOLIDADO (TODAS LAS SUCURSALES)
# =====================================================
echo ""
echo "12. Análisis consolidado todas las sucursales:"
curl -X POST "${BASE_URL}/api/metricas/ventas-clientes/analisis" \
  -H "Content-Type: application/json" \
  -d '{
    "fechaInicio": "2024-01-01",
    "fechaFin": "2024-01-31",
    "sucursalId": null,
    "tipoPeriodo": "MENSUAL",
    "compararPeriodoAnterior": true,
    "incluirDetalleVendedores": true,
    "incluirDetalleClientes": true,
    "limitTopVendedores": 10,
    "limitTopClientes": 20
  }' \
  | json_pp

# =====================================================
# 13. ANÁLISIS SOLO TOP 3 VENDEDORES
# =====================================================
echo ""
echo "13. Análisis top 3 vendedores únicamente:"
curl -X POST "${BASE_URL}/api/metricas/ventas-clientes/analisis" \
  -H "Content-Type: application/json" \
  -d '{
    "fechaInicio": "2024-01-01",
    "fechaFin": "2024-01-31",
    "tipoPeriodo": "MENSUAL",
    "compararPeriodoAnterior": false,
    "incluirDetalleVendedores": true,
    "incluirDetalleClientes": false,
    "limitTopVendedores": 3
  }' \
  | json_pp

# =====================================================
# 14. ANÁLISIS SOLO TOP 5 CLIENTES
# =====================================================
echo ""
echo "14. Análisis top 5 clientes únicamente:"
curl -X POST "${BASE_URL}/api/metricas/ventas-clientes/analisis" \
  -H "Content-Type: application/json" \
  -d '{
    "fechaInicio": "2024-01-01",
    "fechaFin": "2024-01-31",
    "tipoPeriodo": "MENSUAL",
    "compararPeriodoAnterior": false,
    "incluirDetalleVendedores": false,
    "incluirDetalleClientes": true,
    "limitTopClientes": 5
  }' \
  | json_pp

# =====================================================
# 15. ANÁLISIS ANUAL 2024
# =====================================================
echo ""
echo "15. Análisis anual 2024 completo:"
curl -X POST "${BASE_URL}/api/metricas/ventas-clientes/analisis" \
  -H "Content-Type: application/json" \
  -d '{
    "fechaInicio": "2024-01-01",
    "fechaFin": "2024-12-31",
    "tipoPeriodo": "ANUAL",
    "compararPeriodoAnterior": true,
    "incluirDetalleVendedores": true,
    "incluirDetalleClientes": true,
    "limitTopVendedores": 20,
    "limitTopClientes": 50
  }' \
  | json_pp

echo ""
echo "==========================================="
echo "PRUEBAS COMPLETADAS"
echo "==========================================="
