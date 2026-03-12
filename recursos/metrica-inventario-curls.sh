#!/bin/bash

# =====================================================
# CURLs de Prueba - Módulo de Métricas de Inventario
# =====================================================
# Módulo: Métricas de Inventario
# Descripción: Scripts para probar análisis de inventario
# Pregunta clave: ¿Cuánto capital tengo inmovilizado? ¿Rota bien mi inventario?
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
echo -e "${BLUE}║   MÉTRICAS DE INVENTARIO - PRUEBAS API REST       ║${NC}"
echo -e "${BLUE}║   Capital Inmovilizado | Rotación | Alertas       ║${NC}"
echo -e "${BLUE}╚════════════════════════════════════════════════════╝${NC}"
echo ""

# =====================================================
# 1. GENERAR ANÁLISIS CONSOLIDADO CON SNAPSHOT
# =====================================================
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${GREEN}1. GENERAR ANÁLISIS CONSOLIDADO (TODAS LAS SUCURSALES)${NC}"
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${YELLOW}Endpoint: POST /api/v1/metricas/inventario/generar${NC}"
echo -e "${YELLOW}Descripción: Calcula métricas consolidadas y guarda snapshot${NC}"
echo ""

curl -X POST "${BASE_URL}/metricas/inventario/generar" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "fechaCorte": "2024-12-31",
    "sucursalId": null,
    "guardarSnapshot": true,
    "diasPeriodoRotacion": 30
  }' \
  -v

echo -e "\n${BLUE}Resultado esperado:${NC}"
echo '{
  "id": 1,
  "fechaCorte": "2024-12-31",
  "sucursalId": null,
  "nombreSucursal": "CONSOLIDADO",
  "totalSkus": 1500,
  "stockDisponibleTotal": 25000,
  "skusConStock": 1425,
  "skusBajoStock": 120,
  "skusSinStock": 75,
  "skusProximosCaducar": 35,
  "porcentajeBajoStock": 8.00,
  "porcentajeSinStock": 5.00,
  "porcentajeProximosCaducar": 2.33,
  "valorTotalInventario": 1500000.00,
  "costoPromedioPonderado": 60.00,
  "valorStockBajo": 45000.00,
  "indiceRotacion": 8.50,
  "diasInventario": 42.94,
  "costoVentasPeriodo": 350000.00,
  "diasPeriodoRotacion": 30,
  "coberturaDias": 42.94,
  "tasaQuiebreStock": 5.00,
  "saludInventario": "SALUDABLE",
  "clasificacionRotacion": "MEDIA",
  "createdAt": "2024-12-31T23:59:59",
  "createdBy": "admin"
}'
echo ""

# =====================================================
# 2. GENERAR ANÁLISIS POR SUCURSAL
# =====================================================
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${GREEN}2. GENERAR ANÁLISIS POR SUCURSAL${NC}"
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${YELLOW}Endpoint: POST /api/v1/metricas/inventario/generar${NC}"
echo -e "${YELLOW}Descripción: Calcula métricas de una sucursal específica${NC}"
echo ""

curl -X POST "${BASE_URL}/metricas/inventario/generar" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "fechaCorte": "2024-12-31",
    "sucursalId": 1,
    "guardarSnapshot": true,
    "diasPeriodoRotacion": 30
  }' \
  -v

echo ""

# =====================================================
# 3. GENERAR ANÁLISIS SIN GUARDAR SNAPSHOT
# =====================================================
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${GREEN}3. ANÁLISIS EN TIEMPO REAL (SIN GUARDAR)${NC}"
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${YELLOW}Endpoint: POST /api/v1/metricas/inventario/generar${NC}"
echo -e "${YELLOW}Descripción: Calcula sin persistir en base de datos${NC}"
echo ""

curl -X POST "${BASE_URL}/metricas/inventario/generar" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "fechaCorte": "2024-12-31",
    "sucursalId": null,
    "guardarSnapshot": false,
    "diasPeriodoRotacion": 30
  }' \
  -v

echo ""

# =====================================================
# 4. CONSULTAR MÉTRICAS (SNAPSHOT O REAL-TIME)
# =====================================================
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${GREEN}4. CONSULTAR MÉTRICAS CONSOLIDADAS${NC}"
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${YELLOW}Endpoint: GET /api/v1/metricas/inventario${NC}"
echo -e "${YELLOW}Descripción: Obtiene snapshot si existe, o calcula en tiempo real${NC}"
echo ""

curl -X GET "${BASE_URL}/metricas/inventario?fechaCorte=2024-12-31&sucursalId=" \
  -H "Authorization: Bearer ${TOKEN}" \
  -v

echo ""

# =====================================================
# 5. CONSULTAR MÉTRICAS POR SUCURSAL
# =====================================================
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${GREEN}5. CONSULTAR MÉTRICAS DE SUCURSAL ESPECÍFICA${NC}"
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${YELLOW}Endpoint: GET /api/v1/metricas/inventario${NC}"
echo ""

curl -X GET "${BASE_URL}/metricas/inventario?fechaCorte=2024-12-31&sucursalId=1" \
  -H "Authorization: Bearer ${TOKEN}" \
  -v

echo ""

# =====================================================
# 6. OBTENER VALOR ACTUAL DEL INVENTARIO
# =====================================================
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${GREEN}6. VALOR ACTUAL DEL INVENTARIO (TIEMPO REAL)${NC}"
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${YELLOW}Endpoint: GET /api/v1/metricas/inventario/valor-actual${NC}"
echo -e "${YELLOW}Descripción: Capital inmovilizado en inventario${NC}"
echo ""

curl -X GET "${BASE_URL}/metricas/inventario/valor-actual" \
  -H "Authorization: Bearer ${TOKEN}" \
  -v

echo -e "\n${BLUE}Resultado esperado:${NC}"
echo '1500000.00'
echo ""

# =====================================================
# 7. PRODUCTOS CON STOCK BAJO
# =====================================================
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${GREEN}7. PRODUCTOS CON STOCK BAJO MÍNIMO${NC}"
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${YELLOW}Endpoint: GET /api/v1/metricas/inventario/productos/bajo-stock${NC}"
echo -e "${YELLOW}Descripción: Lista productos que requieren reorden${NC}"
echo ""

curl -X GET "${BASE_URL}/metricas/inventario/productos/bajo-stock?sucursalId=1&limite=10" \
  -H "Authorization: Bearer ${TOKEN}" \
  -v

echo -e "\n${BLUE}Resultado esperado (array):${NC}"
echo '[
  {
    "skuInterno": "PROD-001",
    "nombreComercial": "Aceite Motor 20W-50",
    "marca": "Castrol",
    "categoria": "Lubricantes",
    "sucursalId": 1,
    "nombreSucursal": "Sucursal Centro",
    "stockActual": 15,
    "stockMinimo": 50,
    "costoPromedioPonderado": 45.00,
    "valorInventario": 675.00,
    "fechaCaducidad": "2025-06-30",
    "ubicacionPasillo": "A-12-03",
    "estadoAlerta": "BAJO_STOCK"
  }
]'
echo ""

# =====================================================
# 8. PRODUCTOS SIN STOCK (QUIEBRE)
# =====================================================
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${GREEN}8. PRODUCTOS SIN STOCK (QUIEBRE)${NC}"
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${YELLOW}Endpoint: GET /api/v1/metricas/inventario/productos/sin-stock${NC}"
echo -e "${YELLOW}Descripción: Productos que necesitan abastecimiento URGENTE${NC}"
echo ""

curl -X GET "${BASE_URL}/metricas/inventario/productos/sin-stock?limite=10" \
  -H "Authorization: Bearer ${TOKEN}" \
  -v

echo ""

# =====================================================
# 9. PRODUCTOS PRÓXIMOS A CADUCAR
# =====================================================
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${GREEN}9. PRODUCTOS PRÓXIMOS A CADUCAR${NC}"
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${YELLOW}Endpoint: GET /api/v1/metricas/inventario/productos/proximos-caducar${NC}"
echo -e "${YELLOW}Descripción: Productos que caducan en los próximos 30 días${NC}"
echo ""

curl -X GET "${BASE_URL}/metricas/inventario/productos/proximos-caducar?diasAnticipacion=30&limite=10" \
  -H "Authorization: Bearer ${TOKEN}" \
  -v

echo ""

# =====================================================
# 10. HISTÓRICO DE MÉTRICAS (TENDENCIAS)
# =====================================================
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${GREEN}10. HISTÓRICO DE MÉTRICAS CONSOLIDADAS${NC}"
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${YELLOW}Endpoint: GET /api/v1/metricas/inventario/historico${NC}"
echo -e "${YELLOW}Descripción: Obtiene snapshots históricos para análisis de tendencias${NC}"
echo ""

curl -X GET "${BASE_URL}/metricas/inventario/historico?limite=12" \
  -H "Authorization: Bearer ${TOKEN}" \
  -v

echo ""

# =====================================================
# 11. HISTÓRICO POR SUCURSAL
# =====================================================
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${GREEN}11. HISTÓRICO DE SUCURSAL ESPECÍFICA${NC}"
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${YELLOW}Endpoint: GET /api/v1/metricas/inventario/historico${NC}"
echo ""

curl -X GET "${BASE_URL}/metricas/inventario/historico?sucursalId=1&limite=12" \
  -H "Authorization: Bearer ${TOKEN}" \
  -v

echo ""

# =====================================================
# 12. ANÁLISIS CON PERÍODO DE ROTACIÓN PERSONALIZADO
# =====================================================
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${GREEN}12. ANÁLISIS CON PERÍODO PERSONALIZADO (90 DÍAS)${NC}"
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${YELLOW}Endpoint: POST /api/v1/metricas/inventario/generar${NC}"
echo -e "${YELLOW}Descripción: Calcula rotación basado en últimos 90 días${NC}"
echo ""

curl -X POST "${BASE_URL}/metricas/inventario/generar" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "fechaCorte": "2024-12-31",
    "sucursalId": null,
    "guardarSnapshot": true,
    "diasPeriodoRotacion": 90
  }' \
  -v

echo ""

# =====================================================
# RESUMEN DE ENDPOINTS
# =====================================================
echo -e "${BLUE}╔════════════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║              RESUMEN DE ENDPOINTS                  ║${NC}"
echo -e "${BLUE}╚════════════════════════════════════════════════════╝${NC}"
echo -e "${YELLOW}POST   /api/v1/metricas/inventario/generar${NC} .................... Genera análisis"
echo -e "${YELLOW}GET    /api/v1/metricas/inventario${NC} ............................ Consulta métricas"
echo -e "${YELLOW}GET    /api/v1/metricas/inventario/valor-actual${NC} ............... Valor actual"
echo -e "${YELLOW}GET    /api/v1/metricas/inventario/productos/bajo-stock${NC} ....... Stock bajo"
echo -e "${YELLOW}GET    /api/v1/metricas/inventario/productos/sin-stock${NC} ........ Quiebre"
echo -e "${YELLOW}GET    /api/v1/metricas/inventario/productos/proximos-caducar${NC} . Próx. caducar"
echo -e "${YELLOW}GET    /api/v1/metricas/inventario/historico${NC} .................. Tendencias"
echo ""

echo -e "${GREEN}✅ Pruebas completadas - Módulo de Métricas de Inventario${NC}"
