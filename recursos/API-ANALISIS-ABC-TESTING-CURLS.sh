#!/bin/bash
# ============================================================================
# API TESTING - MÓDULO #10: ANÁLISIS ABC DE INVENTARIO
# ============================================================================
# Descripción: CURLs para probar manualmente los endpoints del análisis ABC
# Autor: NexooHub Development Team
# Versión: 1.4.0
# Fecha: 2026-04-01
# ============================================================================
# 
# IMPORTANTE: Configura estas variables antes de ejecutar
# ============================================================================

# Configuración del servidor
BASE_URL="http://localhost:8080"
API_BASE="${BASE_URL}/api/v1/inventario/analisis-abc"

# Token de autenticación (obtener con POST /api/v1/auth/login)
# TOKEN="tu_jwt_token_aqui"
TOKEN=""

# IDs de prueba (ajustar según tu base de datos)
SUCURSAL_ID=1
SKU_PRODUCTO="ACEITE-10W40"

# Fechas de prueba (formato: YYYY-MM-DD)
FECHA_INICIO="2026-01-01"
FECHA_FIN="2026-03-31"

# Colores para output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# ============================================================================
# FUNCIONES AUXILIARES
# ============================================================================

print_header() {
    echo -e "\n${BLUE}============================================================${NC}"
    echo -e "${BLUE}$1${NC}"
    echo -e "${BLUE}============================================================${NC}\n"
}

print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠ $1${NC}"
}

print_error() {
    echo -e "${RED}✗ $1${NC}"
}

check_token() {
    if [ -z "$TOKEN" ]; then
        print_warning "TOKEN no configurado. Algunos endpoints requieren autenticación."
        echo "Configura la variable TOKEN en este script o exporta: export TOKEN='tu_jwt_token'"
        return 1
    fi
    return 0
}

# ============================================================================
# TEST 1: GENERAR NUEVO ANÁLISIS ABC
# ============================================================================
# POST /api/v1/inventario/analisis-abc/generar
# 
# Genera un análisis ABC para el período especificado.
# Clasifica productos según el principio de Pareto (80/20).
# ============================================================================

test_generar_analisis_basico() {
    print_header "TEST 1: Generar análisis ABC - Configuración básica"
    
    curl -X POST "${API_BASE}/generar" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer ${TOKEN}" \
    -d '{
        "sucursalId": '$SUCURSAL_ID',
        "periodoInicio": "'$FECHA_INICIO'",
        "periodoFin": "'$FECHA_FIN'",
        "porcentajeA": 80.0,
        "porcentajeB": 95.0,
        "forzarRegeneracion": false
    }' \
    | json_pp
    
    print_success "Análisis ABC generado para sucursal $SUCURSAL_ID"
}

test_generar_analisis_todas_sucursales() {
    print_header "TEST 2: Generar análisis ABC - Todas las sucursales"
    
    curl -X POST "${API_BASE}/generar" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer ${TOKEN}" \
    -d '{
        "periodoInicio": "'$FECHA_INICIO'",
        "periodoFin": "'$FECHA_FIN'",
        "porcentajeA": 75.0,
        "porcentajeB": 90.0,
        "forzarRegeneracion": true
    }' \
    | json_pp
    
    print_success "Análisis ABC generado para todas las sucursales"
}

test_generar_analisis_personalizado() {
    print_header "TEST 3: Generar análisis ABC - Porcentajes personalizados"
    
    curl -X POST "${API_BASE}/generar" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer ${TOKEN}" \
    -d '{
        "sucursalId": '$SUCURSAL_ID',
        "periodoInicio": "'$FECHA_INICIO'",
        "periodoFin": "'$FECHA_FIN'",
        "porcentajeA": 70.0,
        "porcentajeB": 90.0,
        "forzarRegeneracion": true
    }' \
    | json_pp
    
    print_success "Análisis ABC con porcentajes personalizados (70/90)"
}

# ============================================================================
# TEST 4: OBTENER ÚLTIMO ANÁLISIS DE UNA SUCURSAL
# ============================================================================
# GET /api/v1/inventario/analisis-abc/sucursal/{sucursalId}/ultimo
# 
# Retorna el análisis ABC más reciente para la sucursal especificada.
# ============================================================================

test_obtener_ultimo_analisis() {
    print_header "TEST 4: Obtener último análisis ABC de sucursal $SUCURSAL_ID"
    
    curl -X GET "${API_BASE}/sucursal/${SUCURSAL_ID}/ultimo" \
    -H "Authorization: Bearer ${TOKEN}" \
    | json_pp
    
    print_success "Último análisis recuperado"
}

# ============================================================================
# TEST 5: OBTENER PRODUCTOS POR CLASIFICACIÓN
# ============================================================================
# GET /api/v1/inventario/analisis-abc/sucursal/{sucursalId}/clasificacion/{A|B|C}
# 
# Retorna productos filtrados por clasificación ABC.
# - Clase A: Productos de alta rotación y alto valor (gestión prioritaria)
# - Clase B: Productos de importancia media (control moderado)
# - Clase C: Productos de bajo valor relativo (minimizar inventario)
# ============================================================================

test_obtener_productos_clase_a() {
    print_header "TEST 5: Obtener productos CLASE A - Alta prioridad"
    
    curl -X GET "${API_BASE}/sucursal/${SUCURSAL_ID}/clasificacion/A" \
    -H "Authorization: Bearer ${TOKEN}" \
    | json_pp
    
    print_success "Productos clase A recuperados (alto valor, ~80% de ventas)"
}

test_obtener_productos_clase_b() {
    print_header "TEST 6: Obtener productos CLASE B - Importancia media"
    
    curl -X GET "${API_BASE}/sucursal/${SUCURSAL_ID}/clasificacion/B" \
    -H "Authorization: Bearer ${TOKEN}" \
    | json_pp
    
    print_success "Productos clase B recuperados (valor medio, ~15% de ventas)"
}

test_obtener_productos_clase_c() {
    print_header "TEST 7: Obtener productos CLASE C - Bajo valor"
    
    curl -X GET "${API_BASE}/sucursal/${SUCURSAL_ID}/clasificacion/C" \
    -H "Authorization: Bearer ${TOKEN}" \
    | json_pp
    
    print_success "Productos clase C recuperados (bajo valor, ~5% de ventas)"
}

# ============================================================================
# TEST 8: OBTENER RESUMEN ESTADÍSTICO
# ============================================================================
# GET /api/v1/inventario/analisis-abc/sucursal/{sucursalId}/resumen
# 
# Retorna estadísticas agregadas del último análisis ABC:
# - Total de productos analizados
# - Distribución por clasificación (A/B/C)
# - Valores totales y porcentajes
# - Top productos de clase A
# - Indicadores de rotación de inventario
# ============================================================================

test_obtener_resumen() {
    print_header "TEST 8: Obtener resumen estadístico del análisis ABC"
    
    curl -X GET "${API_BASE}/sucursal/${SUCURSAL_ID}/resumen" \
    -H "Authorization: Bearer ${TOKEN}" \
    | json_pp
    
    print_success "Resumen estadístico completo recuperado"
}

# ============================================================================
# TEST 9: CASOS DE ERROR
# ============================================================================

test_error_fechas_invalidas() {
    print_header "TEST 9: Error - Fechas inválidas (fin antes de inicio)"
    
    curl -X POST "${API_BASE}/generar" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer ${TOKEN}" \
    -d '{
        "sucursalId": '$SUCURSAL_ID',
        "periodoInicio": "2026-12-31",
        "periodoFin": "2026-01-01",
        "porcentajeA": 80.0,
        "porcentajeB": 95.0
    }' \
    | json_pp
    
    print_warning "Debe retornar error 400 - BusinessException"
}

test_error_porcentajes_invalidos() {
    print_header "TEST 10: Error - Porcentajes inválidos (B menor que A)"
    
    curl -X POST "${API_BASE}/generar" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer ${TOKEN}" \
    -d '{
        "sucursalId": '$SUCURSAL_ID',
        "periodoInicio": "'$FECHA_INICIO'",
        "periodoFin": "'$FECHA_FIN'",
        "porcentajeA": 95.0,
        "porcentajeB": 80.0
    }' \
    | json_pp
    
    print_warning "Debe retornar error 400 - BusinessException"
}

test_error_clasificacion_invalida() {
    print_header "TEST 11: Error - Clasificación inválida (debe ser A, B o C)"
    
    curl -X GET "${API_BASE}/sucursal/${SUCURSAL_ID}/clasificacion/X" \
    -H "Authorization: Bearer ${TOKEN}" \
    | json_pp
    
    print_warning "Debe retornar error 400 - Validation error"
}

test_error_campos_requeridos() {
    print_header "TEST 12: Error - Campos requeridos faltantes"
    
    curl -X POST "${API_BASE}/generar" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer ${TOKEN}" \
    -d '{
        "sucursalId": '$SUCURSAL_ID'
    }' \
    | json_pp
    
    print_warning "Debe retornar error 400 - Validation error (periodoInicio/Fin requeridos)"
}

# ============================================================================
# TEST 13: ANÁLISIS CON DATOS DE EJEMPLO
# ============================================================================

test_escenario_completo() {
    print_header "TEST 13: Escenario completo - Flujo de trabajo típico"
    
    echo -e "${YELLOW}Paso 1:${NC} Generar análisis ABC..."
    curl -s -X POST "${API_BASE}/generar" \
    -H "Content-Type: application/json" \
    -H "Authorization: Bearer ${TOKEN}" \
    -d '{
        "sucursalId": '$SUCURSAL_ID',
        "periodoInicio": "'$FECHA_INICIO'",
        "periodoFin": "'$FECHA_FIN'",
        "porcentajeA": 80.0,
        "porcentajeB": 95.0
    }' > /dev/null
    print_success "Análisis generado"
    
    echo -e "\n${YELLOW}Paso 2:${NC} Consultar resumen..."
    curl -s -X GET "${API_BASE}/sucursal/${SUCURSAL_ID}/resumen" \
    -H "Authorization: Bearer ${TOKEN}" \
    | json_pp | head -20
    print_success "Resumen consultado"
    
    echo -e "\n${YELLOW}Paso 3:${NC} Obtener productos prioritarios (Clase A)..."
    curl -s -X GET "${API_BASE}/sucursal/${SUCURSAL_ID}/clasificacion/A" \
    -H "Authorization: Bearer ${TOKEN}" \
    | json_pp | head -20
    print_success "Productos clase A listados"
    
    echo -e "\n${YELLOW}Paso 4:${NC} Verificar último análisis..."
    curl -s -X GET "${API_BASE}/sucursal/${SUCURSAL_ID}/ultimo" \
    -H "Authorization: Bearer ${TOKEN}" \
    | json_pp | head -15
    print_success "Último análisis verificado"
    
    print_success "Flujo completo ejecutado"
}

# ============================================================================
# MENÚ INTERACTIVO
# ============================================================================

show_menu() {
    echo -e "\n${BLUE}╔════════════════════════════════════════════════════════════╗${NC}"
    echo -e "${BLUE}║   TESTING MENU - MÓDULO #10: ANÁLISIS ABC DE INVENTARIO   ║${NC}"
    echo -e "${BLUE}╚════════════════════════════════════════════════════════════╝${NC}\n"
    
    echo "Configuración actual:"
    echo "  • Base URL: $BASE_URL"
    echo "  • Sucursal ID: $SUCURSAL_ID"
    echo "  • Período: $FECHA_INICIO → $FECHA_FIN"
    if [ -z "$TOKEN" ]; then
        echo -e "  • Token: ${RED}NO CONFIGURADO${NC}"
    else
        echo -e "  • Token: ${GREEN}CONFIGURADO${NC}"
    fi
    
    echo -e "\n${YELLOW}Tests de Generación de Análisis:${NC}"
    echo "  1) Generar análisis básico (Sucursal específica)"
    echo "  2) Generar análisis para todas las sucursales"
    echo "  3) Generar análisis con porcentajes personalizados"
    
    echo -e "\n${YELLOW}Tests de Consulta:${NC}"
    echo "  4) Obtener último análisis de sucursal"
    echo "  5) Obtener productos CLASE A (alta prioridad)"
    echo "  6) Obtener productos CLASE B (importancia media)"
    echo "  7) Obtener productos CLASE C (bajo valor)"
    echo "  8) Obtener resumen estadístico completo"
    
    echo -e "\n${YELLOW}Tests de Validación y Errores:${NC}"
    echo "  9) Error: Fechas inválidas"
    echo " 10) Error: Porcentajes inválidos"
    echo " 11) Error: Clasificación inválida"
    echo " 12) Error: Campos requeridos"
    
    echo -e "\n${YELLOW}Tests de Integración:${NC}"
    echo " 13) Escenario completo (flujo de trabajo típico)"
    
    echo -e "\n${YELLOW}Opciones:${NC}"
    echo " 99) Ejecutar TODOS los tests"
    echo "  0) Salir"
    
    echo -e "\n${BLUE}Selecciona una opción:${NC} "
}

run_all_tests() {
    print_header "EJECUTANDO TODOS LOS TESTS"
    
    if ! check_token; then
        print_error "Abortando: TOKEN requerido para ejecutar todos los tests"
        return 1
    fi
    
    test_generar_analisis_basico
    sleep 2
    test_generar_analisis_todas_sucursales
    sleep 2
    test_generar_analisis_personalizado
    sleep 2
    test_obtener_ultimo_analisis
    sleep 2
    test_obtener_productos_clase_a
    sleep 2
    test_obtener_productos_clase_b
    sleep 2
    test_obtener_productos_clase_c
    sleep 2
    test_obtener_resumen
    sleep 2
    test_error_fechas_invalidas
    sleep 2
    test_error_porcentajes_invalidos
    sleep 2
    test_error_clasificacion_invalida
    sleep 2
    test_error_campos_requeridos
    sleep 2
    test_escenario_completo
    
    print_success "Todos los tests ejecutados"
}

# ============================================================================
# MAIN
# ============================================================================

main() {
    # Verificar dependencias
    if ! command -v json_pp &> /dev/null; then
        print_warning "json_pp no encontrado. Instalando sugerencia: apt-get install libjson-perl"
        print_warning "Los resultados no se formatearán automáticamente"
    fi
    
    # Si se pasa argumento --all, ejecutar todos
    if [ "$1" == "--all" ]; then
        run_all_tests
        exit 0
    fi
    
    # Si se pasa argumento numérico, ejecutar test específico
    if [ -n "$1" ] && [ "$1" -eq "$1" ] 2>/dev/null; then
        case $1 in
            1) test_generar_analisis_basico ;;
            2) test_generar_analisis_todas_sucursales ;;
            3) test_generar_analisis_personalizado ;;
            4) test_obtener_ultimo_analisis ;;
            5) test_obtener_productos_clase_a ;;
            6) test_obtener_productos_clase_b ;;
            7) test_obtener_productos_clase_c ;;
            8) test_obtener_resumen ;;
            9) test_error_fechas_invalidas ;;
            10) test_error_porcentajes_invalidos ;;
            11) test_error_clasificacion_invalida ;;
            12) test_error_campos_requeridos ;;
            13) test_escenario_completo ;;
            99) run_all_tests ;;
            *) print_error "Opción inválida: $1" ;;
        esac
        exit 0
    fi
    
    # Menú interactivo
    while true; do
        show_menu
        read -r option
        
        case $option in
            1) test_generar_analisis_basico ;;
            2) test_generar_analisis_todas_sucursales ;;
            3) test_generar_analisis_personalizado ;;
            4) test_obtener_ultimo_analisis ;;
            5) test_obtener_productos_clase_a ;;
            6) test_obtener_productos_clase_b ;;
            7) test_obtener_productos_clase_c ;;
            8) test_obtener_resumen ;;
            9) test_error_fechas_invalidas ;;
            10) test_error_porcentajes_invalidos ;;
            11) test_error_clasificacion_invalida ;;
            12) test_error_campos_requeridos ;;
            13) test_escenario_completo ;;
            99) run_all_tests ;;
            0) 
                print_success "Saliendo..."
                exit 0
                ;;
            *)
                print_error "Opción inválida: $option"
                sleep 2
                ;;
        esac
        
        echo -e "\n${YELLOW}Presiona ENTER para continuar...${NC}"
        read -r
    done
}

# Ejecutar main con todos los argumentos
main "$@"
