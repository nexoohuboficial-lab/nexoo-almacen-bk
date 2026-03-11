#!/bin/bash

# =====================================================
# CURLs de Prueba - Módulo de Rentabilidad
# =====================================================
# Módulo: Rentabilidad por Venta/Producto
# Descripción: Scripts para probar análisis de rentabilidad
# Pregunta clave: ¿Cuánto GANAS realmente?
# =====================================================

# Variables de configuración
BASE_URL="http://localhost:8080/api/v1"
TOKEN="eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTYzMDAwMDAwMCwiZXhwIjoxNjMwMDg2NDAwfQ.XXXXX"

# Colores para output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${BLUE}╔════════════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║   MÓDULO DE RENTABILIDAD - PRUEBAS API REST       ║${NC}"
echo -e "${BLUE}║   ¿Cuánto GANAS realmente?                        ║${NC}"
echo -e "${BLUE}╚════════════════════════════════════════════════════╝${NC}"
echo ""

# =====================================================
# 1. CALCULAR RENTABILIDAD DE UNA VENTA
# =====================================================
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${GREEN}1. CALCULAR RENTABILIDAD DE UNA VENTA${NC}"
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${YELLOW}Endpoint: POST /api/v1/rentabilidad/venta/{ventaId}${NC}"
echo -e "${YELLOW}Descripción: Analiza cuánto se GANÓ en una venta específica${NC}"
echo ""

curl -X POST "${BASE_URL}/rentabilidad/venta/1" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -v

echo -e "\n${BLUE}Resultado esperado:${NC}"
echo '{
  "id": 1,
  "ventaId": 1,
  "fechaVenta": "2024-03-11T14:30:00",
  "clienteNombre": "Juan Pérez",
  "sucursalNombre": "Sucursal Centro",
  "costoTotal": 600.00,
  "precioVentaTotal": 1000.00,
  "utilidadBruta": 400.00,
  "margenPorcentaje": 40.00,
  "ventaBajoCosto": false,
  "cantidadItems": 3,
  "alertaCalidad": "EXCELENTE"
}'
echo ""

# =====================================================
# 2. CONSULTAR RENTABILIDAD DE UNA VENTA
# =====================================================
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${GREEN}2. CONSULTAR RENTABILIDAD EXISTENTE${NC}"
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${YELLOW}Endpoint: GET /api/v1/rentabilidad/venta/{ventaId}${NC}"
echo ""

curl -X GET "${BASE_URL}/rentabilidad/venta/1" \
  -H "Authorization: Bearer ${TOKEN}" \
  -v

echo ""

# =====================================================
# 3. GENERAR ANÁLISIS POR PRODUCTO EN UN PERÍODO
# =====================================================
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${GREEN}3. ANÁLISIS DE RENTABILIDAD POR PRODUCTO${NC}"
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${YELLOW}Endpoint: POST /api/v1/rentabilidad/productos${NC}"
echo -e "${YELLOW}Descripción: Genera análisis agregado de rentabilidad por producto${NC}"
echo ""

curl -X POST "${BASE_URL}/rentabilidad/productos" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "fechaInicio": "2024-01-01",
    "fechaFin": "2024-01-31"
  }' \
  -v

echo -e "\n${BLUE}Resultado esperado (array):${NC}"
echo '[
  {
    "id": 1,
    "skuInterno": "SKU001",
    "nombreComercial": "Producto A",
    "marca": "MARCA-X",
    "periodoInicio": "2024-01-01",
    "periodoFin": "2024-01-31",
    "cantidadVendida": 150,
    "costoPromedioUnitario": 30.00,
    "precioPromedioVenta": 50.00,
    "utilidadTotalGenerada": 3000.00,
    "utilidadPorUnidad": 20.00,
    "margenPromedioPorcentaje": 40.00,
    "numeroVentas": 25,
    "clasificacionRentabilidad": "MUY_RENTABLE"
  }
]'
echo ""

# =====================================================
# 4. TOP 10 PRODUCTOS MÁS RENTABLES
# =====================================================
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${GREEN}4. TOP 10 PRODUCTOS MÁS RENTABLES${NC}"
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${YELLOW}Endpoint: GET /api/v1/rentabilidad/productos/mas-rentables${NC}"
echo -e "${YELLOW}Descripción: Productos que generan MÁS utilidad${NC}"
echo ""

curl -X GET "${BASE_URL}/rentabilidad/productos/mas-rentables?fechaInicio=2024-01-01&fechaFin=2024-01-31&limite=10" \
  -H "Authorization: Bearer ${TOKEN}" \
  -v

echo ""

# =====================================================
# 5. TOP 10 PRODUCTOS MENOS RENTABLES
# =====================================================
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${GREEN}5. TOP 10 PRODUCTOS MENOS RENTABLES${NC}"
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${YELLOW}Endpoint: GET /api/v1/rentabilidad/productos/menos-rentables${NC}"
echo -e "${YELLOW}Descripción: Productos con menor rentabilidad o pérdida${NC}"
echo ""

curl -X GET "${BASE_URL}/rentabilidad/productos/menos-rentables?fechaInicio=2024-01-01&fechaFin=2024-01-31&limite=10" \
  -H "Authorization: Bearer ${TOKEN}" \
  -v

echo ""

# =====================================================
# 6. VENTAS BAJO COSTO (CON PÉRDIDA) ⚠️
# =====================================================
echo -e "${RED}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${RED}6. ⚠️ ALERTA: VENTAS CON PÉRDIDA ⚠️${NC}"
echo -e "${RED}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${YELLOW}Endpoint: GET /api/v1/rentabilidad/ventas/bajo-costo${NC}"
echo -e "${YELLOW}Descripción: Ventas donde Precio < Costo (PÉRDIDA)${NC}"
echo ""

curl -X GET "${BASE_URL}/rentabilidad/ventas/bajo-costo" \
  -H "Authorization: Bearer ${TOKEN}" \
  -v

echo -e "\n${RED}Resultado esperado (ventas con pérdida):${NC}"
echo '[
  {
    "id": 5,
    "ventaId": 205,
    "fechaVenta": "2024-03-10T10:15:00",
    "clienteNombre": "Cliente XYZ",
    "costoTotal": 1200.00,
    "precioVentaTotal": 1000.00,
    "utilidadBruta": -200.00,
    "margenPorcentaje": -20.00,
    "ventaBajoCosto": true,
    "cantidadItems": 2,
    "alertaCalidad": "PERDIDA"
  }
]'
echo ""

# =====================================================
# 7. ESTADÍSTICAS GENERALES DE RENTABILIDAD
# =====================================================
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${GREEN}7. ESTADÍSTICAS GENERALES${NC}"
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${YELLOW}Endpoint: GET /api/v1/rentabilidad/estadisticas${NC}"
echo -e "${YELLOW}Descripción: Dashboard de rentabilidad del período${NC}"
echo ""

curl -X GET "${BASE_URL}/rentabilidad/estadisticas?fechaInicio=2024-01-01&fechaFin=2024-01-31" \
  -H "Authorization: Bearer ${TOKEN}" \
  -v

echo -e "\n${BLUE}Resultado esperado:${NC}"
echo '{
  "utilidadTotalPeriodo": 50000.00,
  "margenPromedioPorcentaje": 35.50,
  "totalVentasAnalizadas": 100,
  "ventasBajoCosto": 5,
  "porcentajeVentasBajoCosto": 5.00,
  "ventaMasRentable": 2000.00,
  "ventaMenosRentable": -150.00,
  "productoMasRentable": "SKU-BEST-001",
  "productoMenosRentable": "SKU-WORST-099"
}'
echo ""

# =====================================================
# CASOS DE ERROR
# =====================================================
echo -e "${RED}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${RED}CASOS DE ERROR${NC}"
echo -e "${RED}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"

# Error 1: Venta no existe
echo -e "\n${YELLOW}ERROR 1: Venta no existe (404)${NC}"
curl -X POST "${BASE_URL}/rentabilidad/venta/99999" \
  -H "Authorization: Bearer ${TOKEN}" \
  -v

# Error 2: Análisis ya existe
echo -e "\n${YELLOW}ERROR 2: Análisis ya existe (400)${NC}"
curl -X POST "${BASE_URL}/rentabilidad/venta/1" \
  -H "Authorization: Bearer ${TOKEN}" \
  -v

# Error 3: Período inválido (fechaFin anterior a fechaInicio)
echo -e "\n${YELLOW}ERROR 3: Período inválido (400)${NC}"
curl -X POST "${BASE_URL}/rentabilidad/productos" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "fechaInicio": "2024-12-31",
    "fechaFin": "2024-01-01"
  }' \
  -v

# Error 4: Sin autorización (401)
echo -e "\n${YELLOW}ERROR 4: Sin autorización (401)${NC}"
curl -X GET "${BASE_URL}/rentabilidad/estadisticas?fechaInicio=2024-01-01&fechaFin=2024-01-31" \
  -v

echo ""

# =====================================================
# FLUJO COMPLETO DE PRUEBA
# =====================================================
echo -e "${BLUE}╔════════════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║   FLUJO COMPLETO DE PRUEBA                        ║${NC}"
echo -e "${BLUE}╚════════════════════════════════════════════════════╝${NC}"
echo ""
echo "1. Crear una venta normal (usar endpoint de ventas)"
echo "2. Calcular su rentabilidad: POST /api/v1/rentabilidad/venta/{ventaId}"
echo "3. Verificar que alertaCalidad sea EXCELENTE, BUENA, REGULAR, BAJA o PERDIDA"
echo "4. Generar análisis por producto del mes: POST /api/v1/rentabilidad/productos"
echo "5. Consultar top 10 productos más rentables"
echo "6. Revisar alertas de ventas bajo costo"
echo "7. Analizar estadísticas generales del período"
echo ""

# =====================================================
# MÉTRICAS CLAVE A MONITOREAR
# =====================================================
echo -e "${YELLOW}╔════════════════════════════════════════════════════╗${NC}"
echo -e "${YELLOW}║   MÉTRICAS CLAVE A MONITOREAR                     ║${NC}"
echo -e "${YELLOW}╚════════════════════════════════════════════════════╝${NC}"
echo ""
echo "✓ Margen promedio >= 20% → SALUDABLE"
echo "⚠ Margen promedio < 10% → REVISAR PRECIOS"
echo "✗ Ventas bajo costo > 5% → ALERTA CRÍTICA"
echo ""
echo "Fórmulas:"
echo "• Utilidad Bruta = Precio Venta - Costo Total"
echo "• Margen % = (Utilidad / Precio Venta) × 100"
echo "• Venta Bajo Costo = Utilidad < 0"
echo ""

echo -e "${GREEN}╔════════════════════════════════════════════════════╗${NC}"
echo -e "${GREEN}║   PRUEBAS COMPLETADAS                             ║${NC}"
echo -e "${GREEN}╚════════════════════════════════════════════════════╝${NC}"
