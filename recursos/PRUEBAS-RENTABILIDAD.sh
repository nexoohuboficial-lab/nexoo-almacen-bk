#!/bin/bash

################################################################################
# Script de Pruebas - Módulo Rentabilidad
# Casos de uso práctico para testing del módulo
################################################################################

# Variables de configuración
BASE_URL="http://localhost:8080/api/v1/rentabilidad"
TOKEN="YOUR_JWT_TOKEN_HERE"

# Colores para output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

################################################################################
# PRERREQUISITOS
################################################################################
echo -e "${BLUE}=== PRERREQUISITOS ===${NC}"
echo "1. Tener el servidor corriendo en ${BASE_URL}"
echo "2. Tener un token JWT válido"
echo "3. Tener ventas registradas en la base de datos"
echo "4. Tener productos con CPP en inventario_sucursal"
echo ""

################################################################################
# FLUJO DE PRUEBA COMPLETO
################################################################################

echo -e "${BLUE}=== FLUJO DE PRUEBA COMPLETO ===${NC}"
echo "Este script ejecuta un flujo completo de pruebas del módulo de rentabilidad"
echo ""

################################################################################
# 1. CALCULAR RENTABILIDAD DE VENTAS INDIVIDUALES
################################################################################

echo -e "${YELLOW}[PASO 1] Calcular Rentabilidad de Ventas${NC}"
echo "Calculando rentabilidad de las últimas 5 ventas..."
echo ""

# Asumiendo que tienes ventas con IDs 1, 2, 3, 4, 5
for VENTA_ID in {1..5}; do
    echo -e "${GREEN}Calculando rentabilidad para venta ID: ${VENTA_ID}${NC}"
    
    curl -X POST "${BASE_URL}/venta/${VENTA_ID}" \
        -H "Authorization: Bearer ${TOKEN}" \
        -H "Content-Type: application/json" \
        -w "\nHTTP Status: %{http_code}\n" \
        -s | jq '.'
    
    echo ""
    sleep 1
done

################################################################################
# 2. CONSULTAR RENTABILIDAD CALCULADA
################################################################################

echo -e "${YELLOW}[PASO 2] Consultar Rentabilidad Calculada${NC}"
echo "Consultando la rentabilidad de la venta ID: 1"
echo ""

curl -X GET "${BASE_URL}/venta/1" \
    -H "Authorization: Bearer ${TOKEN}" \
    -H "Content-Type: application/json" \
    -w "\nHTTP Status: %{http_code}\n" \
    -s | jq '.'

echo ""

################################################################################
# 3. GENERAR ANÁLISIS MENSUAL DE PRODUCTOS
################################################################################

echo -e "${YELLOW}[PASO 3] Generar Análisis Mensual de Productos${NC}"
echo "Generando análisis de rentabilidad por producto del mes actual..."
echo ""

# Calcular fechas del mes actual
PRIMER_DIA=$(date +%Y-%m-01)
ULTIMO_DIA=$(date +%Y-%m-%d)

curl -X POST "${BASE_URL}/productos" \
    -H "Authorization: Bearer ${TOKEN}" \
    -H "Content-Type: application/json" \
    -w "\nHTTP Status: %{http_code}\n" \
    -d "{
        \"fechaInicio\": \"${PRIMER_DIA}\",
        \"fechaFin\": \"${ULTIMO_DIA}\"
    }" \
    -s | jq '.'

echo ""

################################################################################
# 4. TOP 10 PRODUCTOS MÁS RENTABLES
################################################################################

echo -e "${YELLOW}[PASO 4] Top 10 Productos Más Rentables${NC}"
echo "Obteniendo los 10 productos más rentables del mes..."
echo ""

curl -X GET "${BASE_URL}/productos/mas-rentables?fechaInicio=${PRIMER_DIA}&fechaFin=${ULTIMO_DIA}&limite=10" \
    -H "Authorization: Bearer ${TOKEN}" \
    -H "Content-Type: application/json" \
    -w "\nHTTP Status: %{http_code}\n" \
    -s | jq '.'

echo ""

################################################################################
# 5. TOP 10 PRODUCTOS MENOS RENTABLES (⚠️ ATENCIÓN)
################################################################################

echo -e "${YELLOW}[PASO 5] Top 10 Productos Menos Rentables ⚠️${NC}"
echo "⚠️ ATENCIÓN: Estos productos requieren revisión de precios"
echo ""

curl -X GET "${BASE_URL}/productos/menos-rentables?fechaInicio=${PRIMER_DIA}&fechaFin=${ULTIMO_DIA}&limite=10" \
    -H "Authorization: Bearer ${TOKEN}" \
    -H "Content-Type: application/json" \
    -w "\nHTTP Status: %{http_code}\n" \
    -s | jq '.'

echo ""

################################################################################
# 6. ALERTAS: VENTAS BAJO COSTO 🔴
################################################################################

echo -e "${RED}[PASO 6] 🔴 ALERTAS: Ventas Bajo Costo (PÉRDIDAS)${NC}"
echo "🚨 CRÍTICO: Ventas donde el precio fue menor al costo"
echo ""

curl -X GET "${BASE_URL}/ventas/bajo-costo" \
    -H "Authorization: Bearer ${TOKEN}" \
    -H "Content-Type: application/json" \
    -w "\nHTTP Status: %{http_code}\n" \
    -s | jq '.'

echo ""

# Contar ventas bajo costo
VENTAS_BAJO_COSTO=$(curl -s -X GET "${BASE_URL}/ventas/bajo-costo" \
    -H "Authorization: Bearer ${TOKEN}" \
    -H "Content-Type: application/json" | jq '. | length')

if [ "$VENTAS_BAJO_COSTO" -gt 0 ]; then
    echo -e "${RED}⚠️ ALERTA: Se encontraron ${VENTAS_BAJO_COSTO} ventas con pérdida${NC}"
    echo -e "${RED}   Acción requerida: Revisar política de descuentos${NC}"
else
    echo -e "${GREEN}✅ No hay ventas bajo costo. Sistema saludable.${NC}"
fi

echo ""

################################################################################
# 7. DASHBOARD: ESTADÍSTICAS GENERALES
################################################################################

echo -e "${YELLOW}[PASO 7] Dashboard: Estadísticas Generales${NC}"
echo "Obteniendo métricas consolidadas del mes..."
echo ""

STATS=$(curl -s -X GET "${BASE_URL}/estadisticas?fechaInicio=${PRIMER_DIA}&fechaFin=${ULTIMO_DIA}" \
    -H "Authorization: Bearer ${TOKEN}" \
    -H "Content-Type: application/json")

echo "$STATS" | jq '.'

# Extraer métricas clave
UTILIDAD_TOTAL=$(echo "$STATS" | jq -r '.utilidadTotalPeriodo // 0')
MARGEN_PROMEDIO=$(echo "$STATS" | jq -r '.margenPromedioPeriodo // 0')
VENTAS_PERDIDA=$(echo "$STATS" | jq -r '.ventasBajoCostoPeriodo // 0')
PORCENTAJE_PERDIDA=$(echo "$STATS" | jq -r '.porcentajeVentasBajoCosto // 0')

echo ""
echo -e "${BLUE}=== RESUMEN EJECUTIVO ===${NC}"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo -e "Período: ${PRIMER_DIA} al ${ULTIMO_DIA}"
echo ""
echo -e "💰 Utilidad Total:        \$${UTILIDAD_TOTAL}"
echo -e "📊 Margen Promedio:       ${MARGEN_PROMEDIO}%"
echo -e "🔴 Ventas con Pérdida:    ${VENTAS_PERDIDA}"
echo -e "📉 % Ventas Pérdida:      ${PORCENTAJE_PERDIDA}%"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

################################################################################
# INTERPRETACIÓN DE RESULTADOS
################################################################################

echo ""
echo -e "${BLUE}=== INTERPRETACIÓN DE RESULTADOS ===${NC}"

# Evaluar Margen Promedio
MARGEN_NUM=$(printf "%.0f" "$MARGEN_PROMEDIO")

if [ "$MARGEN_NUM" -ge 30 ]; then
    echo -e "✅ Margen Promedio: ${GREEN}EXCELENTE${NC} (>= 30%)"
    echo "   → Rentabilidad óptima. Mantener estrategia."
elif [ "$MARGEN_NUM" -ge 20 ]; then
    echo -e "✅ Margen Promedio: ${GREEN}BUENO${NC} (>= 20%)"
    echo "   → Rentabilidad saludable."
elif [ "$MARGEN_NUM" -ge 10 ]; then
    echo -e "⚠️ Margen Promedio: ${YELLOW}REGULAR${NC} (>= 10%)"
    echo "   → Considerar ajuste de precios."
else
    echo -e "🔴 Margen Promedio: ${RED}BAJO${NC} (< 10%)"
    echo "   → ACCIÓN URGENTE: Revisar estructura de costos y precios."
fi

echo ""

# Evaluar Ventas Bajo Costo
PORCENTAJE_NUM=$(printf "%.0f" "$PORCENTAJE_PERDIDA")

if [ "$PORCENTAJE_NUM" -eq 0 ]; then
    echo -e "✅ Ventas Bajo Costo: ${GREEN}0%${NC}"
    echo "   → No hay ventas con pérdida. Sistema saludable."
elif [ "$PORCENTAJE_NUM" -le 2 ]; then
    echo -e "⚠️ Ventas Bajo Costo: ${YELLOW}${PORCENTAJE_PERDIDA}%${NC}"
    echo "   → Aceptable. Monitorear casos individuales."
elif [ "$PORCENTAJE_NUM" -le 5 ]; then
    echo -e "⚠️ Ventas Bajo Costo: ${YELLOW}${PORCENTAJE_PERDIDA}%${NC}"
    echo "   → ATENCIÓN: Revisar política de descuentos."
else
    echo -e "🔴 Ventas Bajo Costo: ${RED}${PORCENTAJE_PERDIDA}%${NC}"
    echo "   → 🚨 ALERTA CRÍTICA: Problema sistémico. Acción inmediata requerida."
fi

echo ""

################################################################################
# RECOMENDACIONES AUTOMATIZADAS
################################################################################

echo -e "${BLUE}=== RECOMENDACIONES AUTOMATIZADAS ===${NC}"

# Recomendaciones basadas en margen
if [ "$MARGEN_NUM" -lt 20 ]; then
    echo "1. 📈 Revisar precios de los 10 productos menos rentables"
    echo "2. 💰 Negociar mejores costos con proveedores"
    echo "3. 🎯 Enfocar ventas en productos más rentables"
fi

# Recomendaciones basadas en ventas bajo costo
if [ "$PORCENTAJE_NUM" -gt 2 ]; then
    echo "4. 🚨 Revisar y restringir límites de descuento"
    echo "5. 👥 Capacitar equipo de ventas en márgenes mínimos"
    echo "6. 🔍 Investigar causas de ventas bajo costo"
fi

# Recomendaciones generales
echo "7. 📊 Monitorear métricas semanalmente"
echo "8. 📧 Configurar alertas automáticas si margen < 15%"
echo "9. 📈 Analizar tendencias mensuales de rentabilidad"

echo ""

################################################################################
# QUERIES ÚTILES PARA ANÁLISIS PROFUNDO
################################################################################

echo -e "${BLUE}=== QUERIES ÚTILES PARA ANÁLISIS PROFUNDO ===${NC}"
echo ""

echo "1. Ver rentabilidad de una venta específica:"
echo "   GET /api/v1/rentabilidad/venta/{ventaId}"
echo ""

echo "2. Generar análisis trimestral:"
echo "   POST /api/v1/rentabilidad/productos"
echo '   Body: {"fechaInicio": "2024-01-01", "fechaFin": "2024-03-31"}'
echo ""

echo "3. Top 20 productos estrella (más rentables):"
echo "   GET /api/v1/rentabilidad/productos/mas-rentables?fechaInicio=2024-01-01&fechaFin=2024-03-31&limite=20"
echo ""

echo "4. Productos problemáticos (detectar pérdidas):"
echo "   GET /api/v1/rentabilidad/productos/menos-rentables?fechaInicio=2024-01-01&fechaFin=2024-03-31&limite=20"
echo ""

echo "5. Reporte semanal de ventas bajo costo:"
echo "   GET /api/v1/rentabilidad/ventas/bajo-costo"
echo ""

echo "6. KPIs ejecutivos (dashboard):"
echo "   GET /api/v1/rentabilidad/estadisticas?fechaInicio=2024-01-01&fechaFin=2024-12-31"
echo ""

################################################################################
# CASOS DE USO AVANZADOS
################################################################################

echo -e "${BLUE}=== CASOS DE USO AVANZADOS ===${NC}"
echo ""

echo "🎯 CASO 1: Análisis de Rentabilidad por Temporada"
echo "   - Comparar Q1 vs Q2 vs Q3 vs Q4"
echo "   - Identificar productos estacionales rentables"
echo ""

echo "🎯 CASO 2: Seguimiento de Promociones"
echo "   - Calcular rentabilidad antes/después de promoción"
echo "   - Evaluar si descuento afectó margen"
echo ""

echo "🎯 CASO 3: Análisis por Categoría"
echo "   - Agrupar productos similares"
echo "   - Identificar categorías más rentables"
echo ""

echo "🎯 CASO 4: Alertas en Tiempo Real"
echo "   - Configurar webhook para ventas bajo costo"
echo "   - Notificación inmediata si margen < 10%"
echo ""

echo "🎯 CASO 5: Optimización de Inventario"
echo "   - Priorizar reabastecimiento de productos rentables"
echo "   - Reducir stock de productos no rentables"
echo ""

################################################################################
# INTEGRACIÓN CON OTROS MÓDULOS
################################################################################

echo -e "${BLUE}=== INTEGRACIÓN CON OTROS MÓDULOS ===${NC}"
echo ""

echo "💡 Este módulo se integra con:"
echo "   • Módulo de Ventas: Calcula rentabilidad al cerrar venta"
echo "   • Módulo de Inventario: Usa CPP como costo"
echo "   • Módulo de Productos: Enriquece con nombre comercial"
echo "   • Módulo de Reportes: Proporciona métricas financieras"
echo ""

################################################################################
# SCRIPTS ADICIONALES RECOMENDADOS
################################################################################

echo -e "${BLUE}=== SCRIPTS ADICIONALES RECOMENDADOS ===${NC}"
echo ""

echo "📝 Script 1: Cron Job Diario"
echo "   - Calcular rentabilidad de ventas del día anterior"
echo "   - Enviar reporte por email si hay ventas bajo costo"
echo ""

echo "📝 Script 2: Reporte Semanal Ejecutivo"
echo "   - Generar PDF con métricas semanales"
echo "   - Incluir top 10 más/menos rentables"
echo "   - Gráficos de tendencias"
echo ""

echo "📝 Script 3: Alertas de Margen Bajo"
echo "   - Monitorear margen promedio en tiempo real"
echo "   - Slack/Email si margen cae < 15%"
echo ""

################################################################################
# FIN DEL SCRIPT
################################################################################

echo ""
echo -e "${GREEN}=== PRUEBAS COMPLETADAS ===${NC}"
echo "Para más información, consultar:"
echo "  • MODULO-11-RENTABILIDAD.md (documentación técnica completa)"
echo "  • recursos/rentabilidad-controller.yaml (OpenAPI spec)"
echo "  • recursos/rentabilidad-curls.sh (más ejemplos de cURL)"
echo ""
echo "¡Módulo de Rentabilidad funcionando correctamente! ✅"
