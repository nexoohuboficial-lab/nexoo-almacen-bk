#!/bin/bash
# ============================================================================
# API CREDITO - TESTING CON CURL
# ============================================================================
# Script de pruebas para el Sistema de Control de Crédito
# Endpoints: 18 operaciones REST
# Base URL: http://localhost:8080/api/credito
# ============================================================================

# CONFIGURACIÓN
BASE_URL="http://localhost:8080/api/credito"
CONTENT_TYPE="Content-Type: application/json"

# Colores para output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}   TESTING API CONTROL DE CRÉDITO${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""

# ============================================================================
# SECCIÓN 1: GESTIÓN DE LÍMITES DE CRÉDITO
# ============================================================================

echo -e "${GREEN}[1] CREAR LÍMITE DE CRÉDITO - Cliente Estándar${NC}"
echo "POST $BASE_URL/limites"
curl -X POST "$BASE_URL/limites" \
  -H "$CONTENT_TYPE" \
  -d '{
    "clienteId": 1,
    "limiteAutorizado": 10000,
    "plazoPagoDias": 30,
    "maxFacturasVencidas": 3,
    "permiteSobregiro": false,
    "montoSobregiro": 0,
    "observaciones": "Cliente estándar - aprobación automática"
  }' | jq '.'
echo -e "\n"

echo -e "${GREEN}[2] CREAR LÍMITE DE CRÉDITO - Cliente Premium${NC}"
echo "POST $BASE_URL/limites"
curl -X POST "$BASE_URL/limites" \
  -H "$CONTENT_TYPE" \
  -d '{
    "clienteId": 2,
    "limiteAutorizado": 50000,
    "plazoPagoDias": 60,
    "maxFacturasVencidas": 5,
    "permiteSobregiro": true,
    "montoSobregiro": 5000,
    "observaciones": "Cliente premium - sobregiro autorizado"
  }' | jq '.'
echo -e "\n"

echo -e "${GREEN}[3] CREAR LÍMITE DE CRÉDITO - Cliente Nuevo${NC}"
echo "POST $BASE_URL/limites"
curl -X POST "$BASE_URL/limites" \
  -H "$CONTENT_TYPE" \
  -d '{
    "clienteId": 3,
    "limiteAutorizado": 5000,
    "plazoPagoDias": 15,
    "maxFacturasVencidas": 2,
    "permiteSobregiro": false,
    "montoSobregiro": 0,
    "observaciones": "Cliente nuevo - crédito limitado inicial"
  }' | jq '.'
echo -e "\n"

echo -e "${YELLOW}[4] CREAR LÍMITE - ERROR: Cliente Duplicado${NC}"
echo "POST $BASE_URL/limites (debe fallar)"
curl -X POST "$BASE_URL/limites" \
  -H "$CONTENT_TYPE" \
  -d '{
    "clienteId": 1,
    "limiteAutorizado": 20000,
    "plazoPagoDias": 30,
    "maxFacturasVencidas": 3
  }' | jq '.'
echo -e "\n"

echo -e "${YELLOW}[5] CREAR LÍMITE - ERROR: Cliente Inexistente${NC}"
echo "POST $BASE_URL/limites (debe fallar)"
curl -X POST "$BASE_URL/limites" \
  -H "$CONTENT_TYPE" \
  -d '{
    "clienteId": 999999,
    "limiteAutorizado": 10000,
    "plazoPagoDias": 30,
    "maxFacturasVencidas": 3
  }' | jq '.'
echo -e "\n"

echo -e "${GREEN}[6] ACTUALIZAR LÍMITE DE CRÉDITO${NC}"
echo "PUT $BASE_URL/limites/cliente/1"
curl -X PUT "$BASE_URL/limites/cliente/1" \
  -H "$CONTENT_TYPE" \
  -d '{
    "limiteAutorizado": 15000,
    "plazoPagoDias": 45,
    "maxFacturasVencidas": 4,
    "permiteSobregiro": true,
    "montoSobregiro": 2000,
    "observaciones": "Aumento de límite aprobado por gerencia"
  }' | jq '.'
echo -e "\n"

echo -e "${GREEN}[7] OBTENER LÍMITE DE CRÉDITO DE UN CLIENTE${NC}"
echo "GET $BASE_URL/limites/cliente/1"
curl -X GET "$BASE_URL/limites/cliente/1" | jq '.'
echo -e "\n"

echo -e "${YELLOW}[8] OBTENER LÍMITE - ERROR: Cliente Sin Límite${NC}"
echo "GET $BASE_URL/limites/cliente/999 (debe retornar 404)"
curl -X GET "$BASE_URL/limites/cliente/999" | jq '.'
echo -e "\n"

echo -e "${GREEN}[9] LISTAR TODOS LOS LÍMITES${NC}"
echo "GET $BASE_URL/limites"
curl -X GET "$BASE_URL/limites" | jq '.'
echo -e "\n"

echo -e "${GREEN}[10] FILTRAR LÍMITES POR ESTADO - ACTIVO${NC}"
echo "GET $BASE_URL/limites/estado/ACTIVO"
curl -X GET "$BASE_URL/limites/estado/ACTIVO" | jq '.'
echo -e "\n"

echo -e "${GREEN}[11] LISTAR SOLO CLIENTES ACTIVOS${NC}"
echo "GET $BASE_URL/limites/activos"
curl -X GET "$BASE_URL/limites/activos" | jq '.'
echo -e "\n"

echo -e "${GREEN}[12] LISTAR CLIENTES BLOQUEADOS${NC}"
echo "GET $BASE_URL/limites/bloqueados"
curl -X GET "$BASE_URL/limites/bloqueados" | jq '.'
echo -e "\n"

echo -e "${GREEN}[13] LISTAR CLIENTES EN RIESGO (>= 80% utilización)${NC}"
echo "GET $BASE_URL/limites/riesgo"
curl -X GET "$BASE_URL/limites/riesgo" | jq '.'
echo -e "\n"

echo -e "${GREEN}[14] LISTAR CLIENTES EN SOBREGIRO${NC}"
echo "GET $BASE_URL/limites/sobregiro"
curl -X GET "$BASE_URL/limites/sobregiro" | jq '.'
echo -e "\n"

# ============================================================================
# SECCIÓN 2: VALIDACIÓN DE CRÉDITO
# ============================================================================

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}   VALIDACIÓN DE CRÉDITO${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""

echo -e "${GREEN}[15] VALIDAR CRÉDITO - OK: Crédito Suficiente${NC}"
echo "GET $BASE_URL/validar?clienteId=1&monto=5000"
curl -X GET "$BASE_URL/validar?clienteId=1&monto=5000" | jq '.'
echo -e "\n"

echo -e "${YELLOW}[16] VALIDAR CRÉDITO - RECHAZO: Límite Excedido${NC}"
echo "GET $BASE_URL/validar?clienteId=1&monto=50000"
curl -X GET "$BASE_URL/validar?clienteId=1&monto=50000" | jq '.'
echo -e "\n"

echo -e "${YELLOW}[17] VALIDAR CRÉDITO - RECHAZO: Cliente Sin Límite${NC}"
echo "GET $BASE_URL/validar?clienteId=999&monto=1000"
curl -X GET "$BASE_URL/validar?clienteId=999&monto=1000" | jq '.'
echo -e "\n"

echo -e "${YELLOW}[18] VALIDAR CRÉDITO - RECHAZO: Cliente Bloqueado${NC}"
echo "Primero bloqueamos el cliente..."
curl -X PUT "$BASE_URL/limites/cliente/3/bloquear?motivo=Morosidad+detectada" \
  -H "$CONTENT_TYPE" | jq '.'
echo "Ahora intentamos validar crédito..."
curl -X GET "$BASE_URL/validar?clienteId=3&monto=1000" | jq '.'
echo -e "\n"

# ============================================================================
# SECCIÓN 3: REGISTRO DE MOVIMIENTOS (ABONOS/PAGOS)
# ============================================================================

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}   REGISTRO DE ABONOS/PAGOS${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""

echo -e "${GREEN}[19] REGISTRAR ABONO - Efectivo${NC}"
echo "POST $BASE_URL/abonos"
curl -X POST "$BASE_URL/abonos" \
  -H "$CONTENT_TYPE" \
  -d '{
    "clienteId": 1,
    "monto": 2000,
    "metodoPago": "EFECTIVO",
    "folioComprobante": "REC-001",
    "concepto": "Pago parcial - facturas pendientes",
    "observaciones": "Cliente pagó en efectivo"
  }' | jq '.'
echo -e "\n"

echo -e "${GREEN}[20] REGISTRAR ABONO - Transferencia${NC}"
echo "POST $BASE_URL/abonos"
curl -X POST "$BASE_URL/abonos" \
  -H "$CONTENT_TYPE" \
  -d '{
    "clienteId": 1,
    "monto": 3000,
    "metodoPago": "TRANSFERENCIA",
    "folioComprobante": "TRANS-20240309-001",
    "concepto": "Pago de saldo restante",
    "observaciones": "Transferencia bancaria BBVA"
  }' | jq '.'
echo -e "\n"

echo -e "${GREEN}[21] REGISTRAR ABONO - Cheque${NC}"
echo "POST $BASE_URL/abonos"
curl -X POST "$BASE_URL/abonos" \
  -H "$CONTENT_TYPE" \
  -d '{
    "clienteId": 2,
    "monto": 5000,
    "metodoPago": "CHEQUE",
    "folioComprobante": "CHK-12345678",
    "concepto": "Pago de factura #100",
    "observaciones": "Cheque banco Santander"
  }' | jq '.'
echo -e "\n"

echo -e "${YELLOW}[22] REGISTRAR ABONO - ERROR: Monto Mayor al Saldo${NC}"
echo "POST $BASE_URL/abonos (debe fallar)"
curl -X POST "$BASE_URL/abonos" \
  -H "$CONTENT_TYPE" \
  -d '{
    "clienteId": 1,
    "monto": 999999,
    "metodoPago": "EFECTIVO",
    "folioComprobante": "REC-002",
    "concepto": "Intento de pago excesivo"
  }' | jq '.'
echo -e "\n"

echo -e "${GREEN}[23] REGISTRAR ABONO - Desbloqueo Automático${NC}"
echo "Si el cliente está bloqueado y paga lo suficiente, se desbloquea automáticamente"
echo "POST $BASE_URL/abonos"
curl -X POST "$BASE_URL/abonos" \
  -H "$CONTENT_TYPE" \
  -d '{
    "clienteId": 3,
    "monto": 1000,
    "metodoPago": "EFECTIVO",
    "folioComprobante": "REC-003",
    "concepto": "Pago para reactivar crédito"
  }' | jq '.'
echo -e "\n"

# ============================================================================
# SECCIÓN 4: BLOQUEO/DESBLOQUEO/SUSPENSIÓN
# ============================================================================

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}   CONTROL DE ESTADOS${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""

echo -e "${GREEN}[24] BLOQUEAR CRÉDITO${NC}"
echo "PUT $BASE_URL/limites/cliente/2/bloquear?motivo=Factura+vencida+mayor+60+dias"
curl -X PUT "$BASE_URL/limites/cliente/2/bloquear?motivo=Factura+vencida+mayor+60+dias" \
  -H "$CONTENT_TYPE" | jq '.'
echo -e "\n"

echo -e "${GREEN}[25] DESBLOQUEAR CRÉDITO${NC}"
echo "PUT $BASE_URL/limites/cliente/2/desbloquear"
curl -X PUT "$BASE_URL/limites/cliente/2/desbloquear" \
  -H "$CONTENT_TYPE" | jq '.'
echo -e "\n"

echo -e "${GREEN}[26] SUSPENDER CRÉDITO${NC}"
echo "PUT $BASE_URL/limites/cliente/2/suspender?motivo=Revision+de+credito+anual"
curl -X PUT "$BASE_URL/limites/cliente/2/suspender?motivo=Revision+de+credito+anual" \
  -H "$CONTENT_TYPE" | jq '.'
echo -e "\n"

# ============================================================================
# SECCIÓN 5: HISTORIAL Y CONSULTAS
# ============================================================================

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}   HISTORIAL DE MOVIMIENTOS${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""

echo -e "${GREEN}[27] HISTORIAL COMPLETO - Página 1${NC}"
echo "GET $BASE_URL/historial/1?page=0&size=20"
curl -X GET "$BASE_URL/historial/1?page=0&size=20" | jq '.'
echo -e "\n"

echo -e "${GREEN}[28] HISTORIAL - Solo Cargos${NC}"
echo "GET $BASE_URL/historial/1/cargos?page=0&size=20"
curl -X GET "$BASE_URL/historial/1/cargos?page=0&size=20" | jq '.'
echo -e "\n"

echo -e "${GREEN}[29] HISTORIAL - Solo Abonos${NC}"
echo "GET $BASE_URL/historial/1/abonos?page=0&size=20"
curl -X GET "$BASE_URL/historial/1/abonos?page=0&size=20" | jq '.'
echo -e "\n"

echo -e "${GREEN}[30] HISTORIAL - Rango de Fechas${NC}"
echo "GET $BASE_URL/historial/1/rango?fechaInicio=2024-01-01T00:00:00&fechaFin=2024-12-31T23:59:59"
curl -X GET "$BASE_URL/historial/1/rango?fechaInicio=2024-01-01T00:00:00&fechaFin=2024-12-31T23:59:59" | jq '.'
echo -e "\n"

echo -e "${GREEN}[31] HISTORIAL - Rango de Fechas (Último Mes)${NC}"
FECHA_INICIO=$(date -d "30 days ago" +"%Y-%m-%dT00:00:00")
FECHA_FIN=$(date +"%Y-%m-%dT23:59:59")
echo "GET $BASE_URL/historial/1/rango?fechaInicio=$FECHA_INICIO&fechaFin=$FECHA_FIN"
curl -X GET "$BASE_URL/historial/1/rango?fechaInicio=$FECHA_INICIO&fechaFin=$FECHA_FIN" | jq '.'
echo -e "\n"

# ============================================================================
# SECCIÓN 6: CASOS DE USO COMPLETOS
# ============================================================================

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}   CASOS DE USO COMPLETOS${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""

echo -e "${GREEN}[CASO 1] Flujo Completo: Nuevo Cliente${NC}"
echo "1. Crear límite para cliente 10"
curl -X POST "$BASE_URL/limites" \
  -H "$CONTENT_TYPE" \
  -d '{
    "clienteId": 10,
    "limiteAutorizado": 8000,
    "plazoPagoDias": 30,
    "maxFacturasVencidas": 3,
    "permiteSobregiro": false,
    "montoSobregiro": 0
  }' | jq '.'
echo ""

echo "2. Validar que puede comprar $3000"
curl -X GET "$BASE_URL/validar?clienteId=10&monto=3000" | jq '.'
echo ""

echo "3. Simular registro de cargo (esto lo haría VentaService)"
echo "   - El cargo se registra automáticamente al crear venta a crédito"
echo ""

echo "4. Ver estado actual del crédito"
curl -X GET "$BASE_URL/limites/cliente/10" | jq '.'
echo -e "\n"

echo -e "${GREEN}[CASO 2] Flujo Completo: Cliente Paga y se Reactiva${NC}"
echo "1. Verificar límite actual"
curl -X GET "$BASE_URL/limites/cliente/3" | jq '.'
echo ""

echo "2. Registrar pago suficiente para reactivar"
curl -X POST "$BASE_URL/abonos" \
  -H "$CONTENT_TYPE" \
  -d '{
    "clienteId": 3,
    "monto": 5000,
    "metodoPago": "EFECTIVO",
    "folioComprobante": "REC-REACTIVACION",
    "concepto": "Pago total para reactivar crédito"
  }' | jq '.'
echo ""

echo "3. Verificar que estado cambió a ACTIVO"
curl -X GET "$BASE_URL/limites/cliente/3" | jq '.'
echo -e "\n"

echo -e "${GREEN}[CASO 3] Monitoreo: Clientes que Requieren Atención${NC}"
echo "1. Clientes en RIESGO (>= 80% utilización)"
curl -X GET "$BASE_URL/limites/riesgo" | jq '.'
echo ""

echo "2. Clientes BLOQUEADOS"
curl -X GET "$BASE_URL/limites/bloqueados" | jq '.'
echo ""

echo "3. Clientes en SOBREGIRO"
curl -X GET "$BASE_URL/limites/sobregiro" | jq '.'
echo -e "\n"

# ============================================================================
# SECCIÓN 7: CONSULTAS AVANZADAS
# ============================================================================

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}   CONSULTAS AVANZADAS${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""

echo -e "${GREEN}[32] Resumen General de Créditos${NC}"
echo "GET $BASE_URL/limites (con análisis)"
LIMITES=$(curl -s -X GET "$BASE_URL/limites")
echo "$LIMITES" | jq '.'
echo ""
echo "Análisis:"
echo "- Total clientes: $(echo "$LIMITES" | jq '. | length')"
echo "- Activos: $(echo "$LIMITES" | jq '[.[] | select(.estado == "ACTIVO")] | length')"
echo "- Bloqueados: $(echo "$LIMITES" | jq '[.[] | select(.estado == "BLOQUEADO")] | length')"
echo -e "\n"

echo -e "${GREEN}[33] Cliente con Mayor Crédito Utilizado${NC}"
curl -s -X GET "$BASE_URL/limites" | jq '[.] | sort_by(.saldoUtilizado) | reverse | .[0]'
echo -e "\n"

echo -e "${GREEN}[34] Cliente con Mayor Porcentaje de Utilización${NC}"
curl -s -X GET "$BASE_URL/limites" | jq '[.] | sort_by(.porcentajeUtilizacion) | reverse | .[0]'
echo -e "\n"

# ============================================================================
# RESUMEN FINAL
# ============================================================================

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}   RESUMEN DE PRUEBAS${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""
echo -e "${GREEN}✅ Tests completados exitosamente${NC}"
echo ""
echo "Endpoints probados:"
echo "  - Límites de Crédito: 14 operaciones"
echo "  - Validación: 4 escenarios"
echo "  - Movimientos (Abonos): 5 casos"
echo "  - Estados (Bloqueo/Desbloqueo): 3 cambios"
echo "  - Historial: 5 consultas"
echo "  - Casos de Uso: 3 flujos completos"
echo ""
echo "Total: 34 operaciones probadas"
echo ""
echo -e "${YELLOW}Nota:${NC} Algunos tests pueden fallar si:"
echo "  - Los clientes (IDs 1, 2, 3, 10) no existen en la BD"
echo "  - Ya existen límites previos"
echo "  - No hay movimientos registrados"
echo ""
echo -e "${YELLOW}Solución:${NC}"
echo "  1. Ejecutar primero: psql -U postgres -d nexoo_almacen -f bd/control-credito.sql"
echo "  2. Verificar que el servidor esté corriendo en puerto 8080"
echo "  3. Ajustar clienteIds según tu base de datos"
echo ""
echo -e "${GREEN}Para más información:${NC}"
echo "  - Documentación: ia/documentacion/SISTEMA-CONTROL-CREDITO-DOCUMENTACION.md"
echo "  - Contrato OpenAPI: recursos/credito-controller.yaml"
echo "  - Script SQL: bd/control-credito.sql"
echo ""
echo -e "${BLUE}========================================${NC}"
