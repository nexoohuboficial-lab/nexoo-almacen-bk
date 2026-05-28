# PowerShell E2E QA Extended Suite — NexooHub Almacen
# Cubre controllers no incluidos en run-qa-flows.ps1

$BASE = "http://localhost:8080"
$results = @()
$dateSuffix = Get-Date -Format "HHmmss"
$timeStr = Get-Date -Format "yyyy-MM-dd"

Write-Host "==============================================" -ForegroundColor Cyan
Write-Host "  NEXOOHUB ALMACEN - EXTENDED QA SUITE" -ForegroundColor Cyan
Write-Host "  Cycles 8-16: Todos los controllers" -ForegroundColor Cyan
Write-Host "==============================================" -ForegroundColor Cyan

# 1. AUTHENTICATE
try {
    $body = "{""username"":""admin"",""password"":""admin123""}"
    $resp = Invoke-WebRequest -Uri "$BASE/api/v1/auth/login" -Method POST -ContentType "application/json" -Body $body -UseBasicParsing -ErrorAction Stop
    $json = $resp.Content | ConvertFrom-Json
    $TOKEN = $json.datos.token
    if (-not $TOKEN) { Write-Host "FAIL: No token!" -ForegroundColor Red; Exit }
    Write-Host "SUCCESS: Authenticated!" -ForegroundColor Green
} catch {
    Write-Host "FAIL: Auth failed! $($_.Exception.Message)" -ForegroundColor Red
    Exit
}

$headers    = @{ "Authorization" = "Bearer $TOKEN"; "Content-Type" = "application/json" }
$headersGet = @{ "Authorization" = "Bearer $TOKEN" }

# 2. BOOTSTRAP IDs from DB
$sucId  = 1; $empId = 1; $clienteId = 1
try {
    $r = Invoke-WebRequest -Uri "$BASE/api/v1/sucursales" -Method GET -Headers $headersGet -UseBasicParsing
    $arr = $r.Content | ConvertFrom-Json
    if ($arr.Count -gt 0) { $sucId = $arr[0].id }

    $r = Invoke-WebRequest -Uri "$BASE/api/v1/empleados/sucursal/$sucId" -Method GET -Headers $headersGet -UseBasicParsing
    $arr = $r.Content | ConvertFrom-Json
    if ($arr.Count -gt 0) { $empId = $arr[0].id }

    $r = Invoke-WebRequest -Uri "$BASE/api/v1/clientes" -Method GET -Headers $headersGet -UseBasicParsing
    $arr = $r.Content | ConvertFrom-Json
    if ($arr.content.Count -gt 0) { $clienteId = $arr.content[0].id }
} catch { Write-Host "Warning bootstrap: usando defaults" -ForegroundColor DarkYellow }

Write-Host "Bootstrap: sucursal=$sucId, empleado=$empId, cliente=$clienteId" -ForegroundColor Gray

# Get a SKU that exists in inventory
$SKU_BASE = $null
try {
    $r = Invoke-WebRequest -Uri "$BASE/api/v1/inventario/sucursales/$sucId" -Method GET -Headers $headersGet -UseBasicParsing
    $inv = $r.Content | ConvertFrom-Json
    if ($inv.content.Count -gt 0) { $SKU_BASE = $inv.content[0].skuInterno }
} catch {}
if (-not $SKU_BASE) { $SKU_BASE = "SKU-QA-140033" }
Write-Host "Using SKU: $SKU_BASE" -ForegroundColor Gray

# Get an existing venta ID
$ventaId = 1
try {
    $r = Invoke-WebRequest -Uri "$BASE/api/v1/ventas" -Method GET -Headers $headersGet -UseBasicParsing -ErrorAction SilentlyContinue
    $v = $r.Content | ConvertFrom-Json
    if ($v.content.Count -gt 0) { $ventaId = $v.content[0].id }
    elseif ($v.Count -gt 0) { $ventaId = $v[0].id }
} catch {}
Write-Host "Using ventaId: $ventaId" -ForegroundColor Gray

# EXECUTION ENGINE
function Run-Step {
    param(
        [string]$Method,
        [string]$Url,
        [string]$Body,
        [string]$Name
    )
    $stepResult = [PSCustomObject]@{
        Name = $Name; Method = $Method; Url = $Url
        Status = $null; Response = ""; IsSuccess = $false; Message = ""
    }
    try {
        $r = if ($Body) {
            Invoke-WebRequest -Uri $Url -Method $Method -Headers $headers -Body $Body -UseBasicParsing -ErrorAction Stop
        } else {
            Invoke-WebRequest -Uri $Url -Method $Method -Headers $headersGet -UseBasicParsing -ErrorAction Stop
        }
        $stepResult.Status    = $r.StatusCode
        $stepResult.Response  = $r.Content
        $stepResult.IsSuccess = ($r.StatusCode -ge 200 -and $r.StatusCode -lt 300)
        if ($stepResult.IsSuccess) { Write-Host "PASS: ($($r.StatusCode)) $Name" -ForegroundColor Green }
        else { Write-Host "WARN: ($($r.StatusCode)) $Name" -ForegroundColor Yellow }
        return $r.Content
    } catch {
        $statusCode = 500; $errBody = ""
        if ($_.Exception.Response) {
            $statusCode = [int]$_.Exception.Response.StatusCode
            try {
                $stream = $_.Exception.Response.GetResponseStream()
                if ($stream) { $reader = New-Object System.IO.StreamReader($stream); $errBody = $reader.ReadToEnd() }
            } catch {}
        }
        $stepResult.Status    = $statusCode
        $stepResult.Response  = $errBody
        $stepResult.Message   = $_.Exception.Message
        $stepResult.IsSuccess = ($statusCode -ne 500)
        if ($statusCode -eq 500) {
            Write-Host "FAIL: ($statusCode) $Name" -ForegroundColor Red
            if ($errBody) { Write-Host "   Body: $($errBody.Substring(0,[Math]::Min(200,$errBody.Length)))" -ForegroundColor DarkRed }
        } else {
            Write-Host "INFO: ($statusCode) $Name - Handled" -ForegroundColor Cyan
        }
        return $errBody
    } finally {
        $script:results += $stepResult
    }
}

# ==========================================
# CYCLE 8: DASHBOARD & SUCURSALES CRUD
# ==========================================
Write-Host "`n=== CYCLE 8: DASHBOARD & SUCURSALES CRUD ===" -ForegroundColor Blue

Run-Step -Method "GET" -Url "$BASE/api/v1/dashboard" -Name "8.1.1 Consultar Dashboard General"

$bodySuc = "{""nombre"":""Sucursal QA Extended $dateSuffix"",""direccion"":""Av. QA 999"",""ciudad"":""CDMX"",""telefono"":""5511223344"",""email"":""sucqa$dateSuffix@nexoo.com""}"
$resp = Run-Step -Method "POST" -Url "$BASE/api/v1/sucursales" -Body $bodySuc -Name "8.2.1 Crear Sucursal"
$ID_SUC_NEW = $null
try { $j = $resp | ConvertFrom-Json; $ID_SUC_NEW = $j.id; if (-not $ID_SUC_NEW) { $ID_SUC_NEW = $j.datos.id } } catch {}

if ($ID_SUC_NEW) {
    $bodySucMod = "{""nombre"":""Sucursal QA Mod $dateSuffix"",""direccion"":""Av. QA Mod 999"",""ciudad"":""CDMX"",""telefono"":""5500001111"",""email"":""sucmod$dateSuffix@nexoo.com""}"
    Run-Step -Method "PUT" -Url "$BASE/api/v1/sucursales/$ID_SUC_NEW" -Body $bodySucMod -Name "8.2.2 Modificar Sucursal"
}
Run-Step -Method "GET" -Url "$BASE/api/v1/sucursales" -Name "8.2.3 Listar Sucursales"

# ==========================================
# CYCLE 9: USUARIOS & ROLES
# ==========================================
Write-Host "`n=== CYCLE 9: USUARIOS & ROLES ===" -ForegroundColor Blue

Run-Step -Method "GET" -Url "$BASE/api/v1/roles" -Name "9.1.1 Listar Roles del Sistema"

$bodyUser = "{""username"":""qauser$dateSuffix"",""password"":""QaPass123!"",""nombre"":""Usuario QA $dateSuffix"",""email"":""qauser$dateSuffix@nexoo.com"",""rol"":""VENDEDOR"",""sucursalId"":$sucId}"
$resp = Run-Step -Method "POST" -Url "$BASE/api/v1/usuarios" -Body $bodyUser -Name "9.2.1 Crear Usuario"
$ID_USER = $null
try { $j = $resp | ConvertFrom-Json; $ID_USER = $j.id; if (-not $ID_USER) { $ID_USER = $j.datos.id } } catch {}

Run-Step -Method "GET" -Url "$BASE/api/v1/usuarios" -Name "9.2.2 Listar Usuarios"

# ==========================================
# CYCLE 10: PRODUCTOS - OPERACIONES AVANZADAS
# ==========================================
Write-Host "`n=== CYCLE 10: PRODUCTOS AVANZADO ===" -ForegroundColor Blue

Run-Step -Method "GET" -Url "$BASE/api/v1/productos/search?q=QA" -Name "10.1.1 Buscar Productos por Texto"
Run-Step -Method "GET" -Url "$BASE/api/v1/productos/$SKU_BASE" -Name "10.1.2 Consultar Producto por SKU"
Run-Step -Method "GET" -Url "$BASE/api/v1/productos/mostrador" -Name "10.1.3 Consultar Productos para Mostrador"

# Registrar codigo de barras
$bodyBarcode = "{""codigoBarras"":""7501234567890"",""tipo"":""EAN13""}"
Run-Step -Method "POST" -Url "$BASE/api/v1/inventario/productos/$SKU_BASE/codigos-barras" -Body $bodyBarcode -Name "10.2.1 Agregar Codigo de Barras"
Run-Step -Method "GET" -Url "$BASE/api/v1/inventario/productos/$SKU_BASE/codigos-barras" -Name "10.2.2 Consultar Codigos de Barras"

# Escaneo de producto
$bodyScan = "{""codigo"":""7501234567890""}"
Run-Step -Method "GET" -Url "$BASE/api/v1/inventario/productos/buscar-por-codigo?codigo=7501234567890" -Name "10.2.3 Buscar Producto por Codigo de Barras"

# Compatibilidad de motos
Run-Step -Method "GET" -Url "$BASE/api/v1/productos/$SKU_BASE/compatibilidad" -Name "10.3.1 Consultar Compatibilidad de Moto"

# ==========================================
# CYCLE 11: INVENTARIO AVANZADO - TRASPASOS, ABC, AUDITORÍA
# ==========================================
Write-Host "`n=== CYCLE 11: INVENTARIO AVANZADO ===" -ForegroundColor Blue

# Traspaso entre sucursales (si hay mas de una)
$sucDestino = if ($ID_SUC_NEW) { $ID_SUC_NEW } else { 1 }
$bodyTraspaso = "{""sucursalOrigenId"":$sucId,""sucursalDestinoId"":$sucDestino,""skuInterno"":""$SKU_BASE"",""cantidad"":2,""observaciones"":""Traspaso QA Extended""}"
Run-Step -Method "POST" -Url "$BASE/api/v1/inventario/traspasos" -Body $bodyTraspaso -Name "11.1.1 Registrar Traspaso de Inventario"

# Análisis ABC
$bodyABC = "{""sucursalId"":$sucId,""periodoMeses"":3}"
Run-Step -Method "POST" -Url "$BASE/api/v1/inventario/analisis-abc/generar" -Body $bodyABC -Name "11.2.1 Generar Analisis ABC de Inventario"
Run-Step -Method "GET" -Url "$BASE/api/v1/inventario/analisis-abc/sucursal/$sucId/ultimo" -Name "11.2.2 Consultar Ultimo Analisis ABC"
Run-Step -Method "GET" -Url "$BASE/api/v1/inventario/analisis-abc/sucursal/$sucId/resumen" -Name "11.2.3 Consultar Resumen ABC"
Run-Step -Method "GET" -Url "$BASE/api/v1/inventario/analisis-abc/sucursal/$sucId/clasificacion/A" -Name "11.2.4 Consultar Productos Clase A"

# Alertas lento movimiento
Run-Step -Method "POST" -Url "$BASE/api/alertas/lento-movimiento/generar" -Body "{}" -Name "11.3.1 Generar Alertas Lento Movimiento"
Run-Step -Method "GET" -Url "$BASE/api/alertas/lento-movimiento/sucursal/$sucId" -Name "11.3.2 Alertas por Sucursal"
Run-Step -Method "GET" -Url "$BASE/api/alertas/lento-movimiento/criticas" -Name "11.3.3 Alertas Criticas"
Run-Step -Method "GET" -Url "$BASE/api/alertas/lento-movimiento/costo-inmovilizado" -Name "11.3.4 Costo Inmovilizado"

# Caducidad
Run-Step -Method "GET" -Url "$BASE/api/v1/inventario/caducidad/vencidos" -Name "11.4.1 Consultar Productos Vencidos"

# Stock bajo por sucursal
Run-Step -Method "GET" -Url "$BASE/api/v1/inventario/alertas/stock-bajo/sucursales/$sucId" -Name "11.4.2 Stock Bajo por Sucursal"

# ==========================================
# CYCLE 12: PRECIOS & AUDITORÍA
# ==========================================
Write-Host "`n=== CYCLE 12: PRECIOS & AUDITORÍA ===" -ForegroundColor Blue

# Actualización masiva de precios
$bodyPrecio = "{""skuInterno"":""$SKU_BASE"",""sucursalId"":$sucId,""precioVenta"":299.99,""precioLista"":320.00,""motivo"":""Ajuste QA Extended""}"
Run-Step -Method "POST" -Url "$BASE/api/v1/actualizacion-precios" -Body $bodyPrecio -Name "12.1.1 Actualizar Precio de Producto"
Run-Step -Method "GET" -Url "$BASE/api/v1/actualizacion-precios" -Name "12.1.2 Listar Actualizaciones de Precios"

# Precios especiales
$bodyPrecEsp = "{""skuInterno"":""$SKU_BASE"",""clienteId"":$clienteId,""precio"":250.00,""fechaInicio"":""2026-05-01"",""fechaFin"":""2026-12-31"",""activo"":true}"
$resp = Run-Step -Method "POST" -Url "$BASE/api/v1/precios-especiales" -Body $bodyPrecEsp -Name "12.2.1 Registrar Precio Especial para Cliente"
$ID_PRECIO_ESP = $null
try { $j = $resp | ConvertFrom-Json; $ID_PRECIO_ESP = $j.id; if (-not $ID_PRECIO_ESP) { $ID_PRECIO_ESP = $j.datos.id } } catch {}

Run-Step -Method "GET" -Url "$BASE/api/v1/precios-especiales" -Name "12.2.2 Listar Precios Especiales"

# Auditoría de precios
Run-Step -Method "GET" -Url "$BASE/api/v1/auditoria/precios/producto/$SKU_BASE" -Name "12.3.1 Auditoria de Precios por SKU"
Run-Step -Method "GET" -Url "$BASE/api/v1/auditoria/precios/periodo?fechaInicio=2026-01-01T00:00:00&fechaFin=2026-05-26T23:59:59" -Name "12.3.2 Auditoria de Precios por Periodo"
Run-Step -Method "GET" -Url "$BASE/api/v1/auditoria/precios/significativos" -Name "12.3.3 Cambios de Precio Significativos"

# Comparador de precios
Run-Step -Method "GET" -Url "$BASE/api/v1/comparador-precios?skuInterno=$SKU_BASE" -Name "12.4.1 Comparar Precios con Proveedores"

# ==========================================
# CYCLE 13: RENTABILIDAD & MÉTRICAS
# ==========================================
Write-Host "`n=== CYCLE 13: RENTABILIDAD & METRICAS ===" -ForegroundColor Blue

# Rentabilidad de venta
Run-Step -Method "POST" -Url "$BASE/api/v1/rentabilidad/venta/$ventaId" -Body $null -Name "13.1.1 Calcular Rentabilidad de Venta"
Run-Step -Method "GET" -Url "$BASE/api/v1/rentabilidad/venta/$ventaId" -Name "13.1.2 Consultar Rentabilidad de Venta"

# Rentabilidad de productos
$bodyRentProd = "{""sucursalId"":$sucId,""fechaInicio"":""2026-01-01"",""fechaFin"":""2026-12-31""}"
Run-Step -Method "POST" -Url "$BASE/api/v1/rentabilidad/productos" -Body $bodyRentProd -Name "13.1.3 Calcular Rentabilidad de Productos"
Run-Step -Method "GET" -Url "$BASE/api/v1/rentabilidad/productos/mas-rentables" -Name "13.1.4 Productos Mas Rentables"
Run-Step -Method "GET" -Url "$BASE/api/v1/rentabilidad/productos/menos-rentables" -Name "13.1.5 Productos Menos Rentables"
Run-Step -Method "GET" -Url "$BASE/api/v1/rentabilidad/ventas/bajo-costo" -Name "13.1.6 Ventas con Bajo Costo"
Run-Step -Method "GET" -Url "$BASE/api/v1/rentabilidad/estadisticas" -Name "13.1.7 Estadisticas Generales Rentabilidad"

# Métricas Financieras
$bodyMetFin = "{""sucursalId"":$sucId,""fechaInicio"":""2026-01-01"",""fechaFin"":""2026-05-25"",""guardarSnapshot"":true}"
Run-Step -Method "POST" -Url "$BASE/api/v1/metricas-financieras/analisis" -Body $bodyMetFin -Name "13.2.1 Generar Analisis Financiero"
Run-Step -Method "GET" -Url "$BASE/api/v1/metricas-financieras" -Name "13.2.2 Consultar Metricas Financieras"
Run-Step -Method "GET" -Url "$BASE/api/v1/metricas-financieras/top-productos" -Name "13.2.3 Top Productos por Metricas Financieras"
Run-Step -Method "GET" -Url "$BASE/api/v1/metricas-financieras/dashboard-ejecutivo" -Name "13.2.4 Dashboard Ejecutivo Financiero"
Run-Step -Method "GET" -Url "$BASE/api/v1/metricas-financieras/health" -Name "13.2.5 Health Check Metricas Financieras"

# Métricas de Inventario
$bodyMetInv = "{""sucursalId"":$sucId}"
Run-Step -Method "POST" -Url "$BASE/api/v1/metricas/inventario/generar" -Body $bodyMetInv -Name "13.3.1 Generar Metricas de Inventario"
Run-Step -Method "GET" -Url "$BASE/api/v1/metricas/inventario" -Name "13.3.2 Consultar Metricas de Inventario"
Run-Step -Method "GET" -Url "$BASE/api/v1/metricas/inventario/valor-actual" -Name "13.3.3 Valor Actual del Inventario"
Run-Step -Method "GET" -Url "$BASE/api/v1/metricas/inventario/productos/bajo-stock" -Name "13.3.4 Productos Bajo Stock"
Run-Step -Method "GET" -Url "$BASE/api/v1/metricas/inventario/productos/sin-stock" -Name "13.3.5 Productos Sin Stock"

# Métricas Operativas
$bodyMetOp = "{""sucursalId"":$sucId,""fechaInicio"":""2026-01-01"",""fechaFin"":""2026-12-31""}"
Run-Step -Method "POST" -Url "$BASE/api/v1/metricas/operativas/analisis" -Body $bodyMetOp -Name "13.4.1 Analisis Metricas Operativas"
Run-Step -Method "GET" -Url "$BASE/api/v1/metricas/operativas/mes-actual" -Name "13.4.2 Metricas Operativas Mes Actual"
Run-Step -Method "GET" -Url "$BASE/api/v1/metricas/operativas/consolidado" -Name "13.4.3 Consolidado Operativo"

# Métricas Venta-Cliente
$bodyMetVC = "{""sucursalId"":$sucId,""fechaInicio"":""2026-01-01"",""fechaFin"":""2026-05-25"",""tipoPeriodo"":""MENSUAL"",""compararPeriodoAnterior"":true,""incluirDetalleVendedores"":true,""incluirDetalleClientes"":true}"
Run-Step -Method "POST" -Url "$BASE/api/metricas/ventas-clientes/analisis" -Body $bodyMetVC -Name "13.5.1 Analisis Metricas Venta-Cliente"
Run-Step -Method "GET" -Url "$BASE/api/metricas/ventas-clientes/mes-actual" -Name "13.5.2 Metricas Venta-Cliente Mes Actual"

# ==========================================
# CYCLE 14: CONTABILIDAD & FINANZAS PARAMETROS
# ==========================================
Write-Host "`n=== CYCLE 14: CONTABILIDAD & FINANZAS ===" -ForegroundColor Blue

Run-Step -Method "GET" -Url "$BASE/api/v1/contabilidad/cuentas" -Name "14.1.1 Listar Cuentas Contables"

$bodyPoliza = "{""tipo"":""INGRESO"",""concepto"":""Venta QA Extended $dateSuffix"",""monto"":500.00,""fecha"":""$timeStr"",""sucursalId"":$sucId}"
Run-Step -Method "POST" -Url "$BASE/api/v1/contabilidad/polizas" -Body $bodyPoliza -Name "14.1.2 Registrar Poliza Contable"
Run-Step -Method "GET" -Url "$BASE/api/v1/contabilidad/polizas" -Name "14.1.3 Consultar Polizas Contables"

Run-Step -Method "GET" -Url "$BASE/api/v1/contabilidad/reportes/balanza" -Name "14.1.4 Reporte Balanza de Comprobacion"
Run-Step -Method "GET" -Url "$BASE/api/v1/contabilidad/reportes/estado-resultados" -Name "14.1.5 Reporte Estado de Resultados"

# Configuracion Financiera
Run-Step -Method "GET" -Url "$BASE/api/v1/finanzas/parametros" -Name "14.2.1 Consultar Parametros Financieros"

$bodyParamFin = "{""iva"":0.16,""margenGananciaBase"":0.35,""gastosFijosMensuales"":5000.00,""metaVentasMensual"":50000.00,""comisionTarjeta"":0.03}"
Run-Step -Method "PUT" -Url "$BASE/api/v1/finanzas/parametros" -Body $bodyParamFin -Name "14.2.2 Actualizar Parametros Financieros"

# Metrica Financiera comparacion
$bodyCompFin = "{""sucursalesIds"":[$sucId],""fechaInicio"":""2026-01-01"",""fechaFin"":""2026-12-31""}"
Run-Step -Method "POST" -Url "$BASE/api/v1/metricas-financieras/comparacion" -Body $bodyCompFin -Name "14.3.1 Comparacion Financiera entre Sucursales"

# Gastos listado
Run-Step -Method "GET" -Url "$BASE/api/v1/finanzas/gastos" -Name "14.4.1 Listar Gastos Operativos"

# Metrica Financiera por sucursal
Run-Step -Method "GET" -Url "$BASE/api/v1/metricas-financieras/sucursal/$sucId" -Name "14.4.2 Metricas Financieras por Sucursal"

# ==========================================
# CYCLE 15: LOGÍSTICA, NÓMINA & RENDIMIENTO
# ==========================================
Write-Host "`n=== CYCLE 15: LOGISTICA, NOMINA & RENDIMIENTO ===" -ForegroundColor Blue

# Logística
Run-Step -Method "GET" -Url "$BASE/api/v1/logistica/vehiculos" -Name "15.1.1 Listar Vehiculos de Reparto"
Run-Step -Method "GET" -Url "$BASE/api/v1/logistica/choferes" -Name "15.1.2 Listar Choferes"

$bodyRuta = "{""vehiculoId"":1,""choferId"":1,""fecha"":""$timeStr"",""origen"":""Bodega Central"",""destino"":""Cliente Final"",""observaciones"":""Ruta QA Extended""}"
$resp = Run-Step -Method "POST" -Url "$BASE/api/v1/logistica/rutas" -Body $bodyRuta -Name "15.1.3 Crear Ruta de Reparto"
$ID_RUTA = $null
try { $j = $resp | ConvertFrom-Json; $ID_RUTA = $j.id; if (-not $ID_RUTA) { $ID_RUTA = $j.datos.id } } catch {}
Run-Step -Method "GET" -Url "$BASE/api/v1/logistica/rutas" -Name "15.1.4 Listar Rutas de Reparto"

if ($ID_RUTA) {
    $bodyEstRuta = "{""estatus"":""EN_CAMINO""}"
    Run-Step -Method "PATCH" -Url "$BASE/api/v1/logistica/rutas/$ID_RUTA/estatus" -Body $bodyEstRuta -Name "15.1.5 Actualizar Estatus de Ruta"
}

# Nómina
$bodyNomEmpl = "{""nombreCompleto"":""Empleado QA Nomina $dateSuffix"",""puesto"":""VENDEDOR"",""sucursalId"":$sucId,""salarioDiario"":500.00,""departamento"":""Ventas"",""estatus"":""ACTIVO""}"
Run-Step -Method "POST" -Url "$BASE/api/v1/nomina/empleados" -Body $bodyNomEmpl -Name "15.2.1 Registrar Empleado en Nomina"
Run-Step -Method "GET" -Url "$BASE/api/v1/nomina/empleados" -Name "15.2.2 Listar Empleados en Nomina"

$bodyPeriodo = "{""nombre"":""Quincena QA $dateSuffix"",""tipo"":""QUINCENAL"",""fechaInicio"":""2026-05-01"",""fechaFin"":""2026-05-15"",""sucursalId"":$sucId}"
$resp = Run-Step -Method "POST" -Url "$BASE/api/v1/nomina/periodos" -Body $bodyPeriodo -Name "15.2.3 Crear Periodo de Nomina"
$ID_PERIODO = $null
try { $j = $resp | ConvertFrom-Json; $ID_PERIODO = $j.id; if (-not $ID_PERIODO) { $ID_PERIODO = $j.datos.id } } catch {}
Run-Step -Method "GET" -Url "$BASE/api/v1/nomina/periodos" -Name "15.2.4 Listar Periodos de Nomina"

if ($ID_PERIODO) {
    Run-Step -Method "POST" -Url "$BASE/api/v1/nomina/periodos/$ID_PERIODO/generar" -Body $null -Name "15.2.5 Generar Recibos del Periodo"
}

# Rendimiento personal
Run-Step -Method "GET" -Url "$BASE/api/v1/rendimiento-personal" -Name "15.3.1 Consultar Rendimiento Personal"
Run-Step -Method "GET" -Url "$BASE/api/v1/rendimiento-personal/empleado/$empId" -Name "15.3.2 Rendimiento por Empleado"

# ==========================================
# CYCLE 16: CRM AVANZADO, FIDELIDAD & NPS
# ==========================================
Write-Host "`n=== CYCLE 16: CRM AVANZADO, FIDELIDAD & NPS ===" -ForegroundColor Blue

# Pipeline B2B
Run-Step -Method "GET" -Url "$BASE/api/v1/crm/prospectos" -Name "16.1.1 Listar Prospectos B2B"
Run-Step -Method "GET" -Url "$BASE/api/v1/crm/prospectos/1" -Name "16.1.2 Detalle de Prospecto B2B"
Run-Step -Method "GET" -Url "$BASE/api/v1/crm/prospectos/1/oportunidades" -Name "16.1.3 Oportunidades del Prospecto"
Run-Step -Method "GET" -Url "$BASE/api/v1/crm/prospectos/1/interacciones" -Name "16.1.4 Interacciones del Prospecto"

$bodyEtapa = "{""etapa"":""NEGOCIACION"",""probabilidadPorcentaje"":75,""notas"":""Avance confirmado en QA""}"
Run-Step -Method "PATCH" -Url "$BASE/api/v1/crm/oportunidades/1/etapa" -Body $bodyEtapa -Name "16.1.5 Avanzar Etapa de Oportunidad"

# Campañas de Marketing
$bodyCampana = "{""nombre"":""Campaña QA $dateSuffix"",""tipo"":""EMAIL"",""fechaInicio"":""2026-06-01"",""fechaFin"":""2026-06-30"",""presupuesto"":5000.00,""objetivo"":""Capturar leads del mercado QA""}"
$resp = Run-Step -Method "POST" -Url "$BASE/api/v1/marketing/campanas" -Body $bodyCampana -Name "16.2.1 Crear Campana de Marketing"
$ID_CAMPANA = $null
try { $j = $resp | ConvertFrom-Json; $ID_CAMPANA = $j.id; if (-not $ID_CAMPANA) { $ID_CAMPANA = $j.datos.id } } catch {}

Run-Step -Method "GET" -Url "$BASE/api/v1/marketing/campanas" -Name "16.2.2 Listar Campanas de Marketing"

# NPS
$bodyEncuesta = "{""clienteId"":$clienteId,""ventaId"":$ventaId,""tipoEncuesta"":""POSTVENTA""}"
Run-Step -Method "POST" -Url "$BASE/api/v1/nps/encuestas" -Body $bodyEncuesta -Name "16.3.1 Crear Encuesta NPS"
Run-Step -Method "GET" -Url "$BASE/api/v1/nps/dashboard" -Name "16.3.2 Dashboard NPS"

$bodyRespuesta = "{""encuestaId"":1,""puntuacion"":9,""comentario"":""Excelente servicio, muy rapido.""}"
Run-Step -Method "POST" -Url "$BASE/api/v1/nps/respuestas" -Body $bodyRespuesta -Name "16.3.3 Registrar Respuesta NPS"

# Programa de Fidelidad
$bodyProgFid = "{""clienteId"":$clienteId,""nivel"":""BRONCE"",""puntos"":0}"
Run-Step -Method "POST" -Url "$BASE/api/v1/fidelidad/programa" -Body $bodyProgFid -Name "16.4.1 Inscribir Cliente en Programa Fidelidad"
Run-Step -Method "GET" -Url "$BASE/api/v1/fidelidad/programa/cliente/$clienteId" -Name "16.4.2 Consultar Programa Cliente"

$bodyAcumular = "{""clienteId"":$clienteId,""ventaId"":$ventaId,""puntos"":100}"
Run-Step -Method "POST" -Url "$BASE/api/v1/fidelidad/acumular" -Body $bodyAcumular -Name "16.4.3 Acumular Puntos de Fidelidad"
Run-Step -Method "GET" -Url "$BASE/api/v1/fidelidad/historial/cliente/$clienteId" -Name "16.4.4 Historial Puntos Fidelidad"
Run-Step -Method "GET" -Url "$BASE/api/v1/fidelidad/estadisticas" -Name "16.4.5 Estadisticas del Programa Fidelidad"

# ==========================================
# CYCLE 17: ORDENES DE COMPRA & DEVOLUCIONES PROVEEDOR
# ==========================================
Write-Host "`n=== CYCLE 17: ORDENES COMPRA & DEV PROVEEDOR ===" -ForegroundColor Blue

$provId = 1
try {
    $r = Invoke-WebRequest -Uri "$BASE/api/v1/proveedores" -Method GET -Headers $headersGet -UseBasicParsing
    $arr = $r.Content | ConvertFrom-Json
    if ($arr.content.Count -gt 0) { $provId = $arr.content[0].id }
    elseif ($arr.Count -gt 0) { $provId = $arr[0].id }
} catch {}

# Orden de compra
$bodyOC = "{""proveedorId"":$provId,""sucursalDestinoId"":$sucId,""fechaEntregaEstimada"":""2026-06-15"",""detalles"":[{""skuInterno"":""$SKU_BASE"",""cantidad"":5,""costoUnitario"":145.00}],""observaciones"":""OC QA Extended""}"
$resp = Run-Step -Method "POST" -Url "$BASE/api/v1/ordenes-compra" -Body $bodyOC -Name "17.1.1 Crear Orden de Compra"
$ID_OC = $null
try { $j = $resp | ConvertFrom-Json; $ID_OC = $j.id; if (-not $ID_OC) { $ID_OC = $j.datos.id } } catch {}

Run-Step -Method "GET" -Url "$BASE/api/v1/ordenes-compra" -Name "17.1.2 Listar Ordenes de Compra"

# Devolución a proveedor
$bodyDevProv = "{""proveedorId"":$provId,""sucursalId"":$sucId,""motivo"":""Producto dañado en transporte QA"",""detalles"":[{""skuInterno"":""$SKU_BASE"",""cantidad"":1,""costo"":145.00}]}"
$resp = Run-Step -Method "POST" -Url "$BASE/api/v1/devoluciones/proveedores" -Body $bodyDevProv -Name "17.2.1 Crear Devolucion a Proveedor"
$ID_DEV_PROV = $null
try { $j = $resp | ConvertFrom-Json; $ID_DEV_PROV = $j.id; if (-not $ID_DEV_PROV) { $ID_DEV_PROV = $j.datos.id } } catch {}

Run-Step -Method "GET" -Url "$BASE/api/v1/devoluciones/proveedores" -Name "17.2.2 Listar Devoluciones a Proveedor"

if ($ID_DEV_PROV) {
    Run-Step -Method "POST" -Url "$BASE/api/v1/devoluciones/proveedores/$ID_DEV_PROV/aplicar" -Body $null -Name "17.2.3 Aplicar Devolucion al Proveedor"
}

# ==========================================
# CYCLE 18: TERMINAL BANCARIA & GARANTÍAS & MOROSIDAD
# ==========================================
Write-Host "`n=== CYCLE 18: TERMINAL, GARANTIAS & MOROSIDAD ===" -ForegroundColor Blue

# Terminal bancaria / POS
$bodyPago = "{""ventaId"":$ventaId,""monto"":100.00,""numeroTarjeta"":""****1234"",""tipo"":""DEBITO"",""referencia"":""REF-QA-POS-$dateSuffix""}"
$resp = Run-Step -Method "POST" -Url "$BASE/api/v1/pos/pagos/tarjeta" -Body $bodyPago -Name "18.1.1 Procesar Pago con Terminal Bancaria"
$ID_REF_POS = "REF-QA-POS-$dateSuffix"
Run-Step -Method "GET" -Url "$BASE/api/v1/pos/pagos/$ID_REF_POS/estatus" -Name "18.1.2 Consultar Estatus de Pago POS"

# Garantías
$bodyGar = "{""ventaId"":$ventaId,""skuInterno"":""$SKU_BASE"",""mesesGarantia"":12,""observaciones"":""Garantia registrada en prueba QA""}"
Run-Step -Method "POST" -Url "$BASE/api/v1/garantias" -Body $bodyGar -Name "18.2.1 Registrar Garantia de Producto"
Run-Step -Method "GET" -Url "$BASE/api/v1/garantias" -Name "18.2.2 Listar Garantias"
Run-Step -Method "GET" -Url "$BASE/api/v1/garantias/venta/$ventaId" -Name "18.2.3 Consultar Garantias por Venta"

# Morosidad
Run-Step -Method "GET" -Url "$BASE/api/v1/morosidad" -Name "18.3.1 Listar Clientes Morosos"
Run-Step -Method "GET" -Url "$BASE/api/v1/morosidad/reporte" -Name "18.3.2 Reporte de Morosidad"

# Crédito avanzado
Run-Step -Method "GET" -Url "$BASE/api/credito/limites" -Name "18.4.1 Listar Limites de Credito"
Run-Step -Method "GET" -Url "$BASE/api/credito/limites/activos" -Name "18.4.2 Limites de Credito Activos"
Run-Step -Method "GET" -Url "$BASE/api/credito/limites/riesgo" -Name "18.4.3 Limites de Credito en Riesgo"
Run-Step -Method "GET" -Url "$BASE/api/credito/historial/$clienteId" -Name "18.4.4 Historial de Credito del Cliente"
Run-Step -Method "GET" -Url "$BASE/api/credito/limites/cliente/$clienteId" -Name "18.4.5 Limite de Credito del Cliente"

# ==========================================
# CYCLE 19: SINCRONIZACIÓN, FACTURACIÓN & MARKET BASKET
# ==========================================
Write-Host "`n=== CYCLE 19: SINCRONIZACION, FACTURACION & MARKET BASKET ===" -ForegroundColor Blue

# Sincronización offline
Run-Step -Method "GET" -Url "$BASE/api/v1/sincronizacion/pendientes" -Name "19.1.1 Consultar Registros Pendientes de Sincronizar"

$bodySincLote = "{""registros"":[{""tipo"":""VENTA"",""datos"":{""id"":$ventaId},""timestamp"":""$timeStr""}]}"
Run-Step -Method "POST" -Url "$BASE/api/v1/sincronizacion/lote" -Body $bodySincLote -Name "19.1.2 Sincronizar Lote Offline"

# Facturación CFDI
$bodyFactura = "{""ventaId"":$ventaId,""receptorRfc"":""XAXX010101000"",""usoCfdi"":""G03"",""formaPago"":""01""}"
$resp = Run-Step -Method "POST" -Url "$BASE/api/v1/facturacion/timbrar" -Body $bodyFactura -Name "19.2.1 Timbrar CFDI de Venta"
$ID_FACTURA = $null
try { $j = $resp | ConvertFrom-Json; $ID_FACTURA = $j.id; if (-not $ID_FACTURA) { $ID_FACTURA = $j.datos.id } } catch {}
Run-Step -Method "GET" -Url "$BASE/api/v1/facturacion/cliente/$clienteId" -Name "19.2.2 Facturas por Cliente"

# Market Basket Analysis
$bodyMBA = "{""sucursalId"":$sucId,""periodoMeses"":3,""minSoporte"":0.1,""minConfianza"":0.5}"
Run-Step -Method "POST" -Url "$BASE/api/v1/market-basket/analizar" -Body $bodyMBA -Name "19.3.1 Analisis Market Basket"
Run-Step -Method "GET" -Url "$BASE/api/v1/market-basket/resultados" -Name "19.3.2 Resultados Market Basket"

# Predicciones avanzadas
Run-Step -Method "GET" -Url "$BASE/api/predicciones/recomendaciones" -Name "19.4.1 Recomendaciones de Compra (Predicciones)"
Run-Step -Method "GET" -Url "$BASE/api/predicciones/producto/$SKU_BASE" -Name "19.4.2 Prediccion por Producto"

# ==========================================
# SUMMARY & REPORTS
# ==========================================
Write-Host "`n==============================================" -ForegroundColor Cyan
Write-Host "       EXTENDED QA SUITE COMPLETED" -ForegroundColor Cyan
Write-Host "==============================================" -ForegroundColor Cyan

$total  = $results.Count
$passed = ($results | Where-Object { $_.IsSuccess -eq $true }).Count
$failed = ($results | Where-Object { $_.IsSuccess -eq $false }).Count

Write-Host "Total Steps Executed : $total" -ForegroundColor Gray
Write-Host "Operational (2xx/4xx): $passed" -ForegroundColor Green
if ($failed -gt 0) {
    Write-Host "Server Crashes (500) : $failed" -ForegroundColor Red
} else {
    Write-Host "Server Crashes (500) : 0" -ForegroundColor Green
}

# Save JSON
$jsonPath = Join-Path -Path $PSScriptRoot -ChildPath "qa-extended-results.json"
$results | ConvertTo-Json -Depth 5 | Out-File -FilePath $jsonPath -Encoding utf8
Write-Host "Saved JSON: $jsonPath" -ForegroundColor DarkGray

# Generate extended markdown report in project root
$mdPath = "c:\Users\danie\OneDrive\Documentos\Vscode\NexxoHub\Proyectos\nexoo-almacen-bk\reporte-qa-extendido.md"
$md = @"
# Reporte QA Extendido (Cycles 8-19) — NexooHub Almacen

Generado el: $(Get-Date -Format "yyyy-MM-dd HH:mm:ss")
Total Pasos Evaluados: **$total**
Exitosos u Operacionales: **$passed**
Caidas Criticas (500): **$failed**

---

## Detalle de Pasos

| Paso | Metodo | URL | HTTP | Estado |
|---|---|---|---|---|
"@

foreach ($r in $results) {
    $icon = if ($r.Status -eq 500) { "FAIL" } elseif ($r.IsSuccess) { "PASS" } else { "INFO" }
    $md += "`n| $($r.Name) | $($r.Method) | $($r.Url) | $($r.Status) | $icon |"
}

$md += "`n`n---`n`n## Errores Criticos (500)`n"
$c = 1
foreach ($r in $results) {
    if ($r.Status -eq 500) {
        $md += "`n### Incidencia ${c}: $($r.Name)"
        $md += "`n- **Endpoint:** $($r.Method) $($r.Url)"
        $md += "`n- **Detalle:** $($r.Message)"
        $md += "`n- **Respuesta:** $($r.Response.Substring(0,[Math]::Min(300,$r.Response.Length)))"
        $md += "`n---"
        $c++
    }
}
if ($failed -eq 0) { $md += "`n### No se registraron errores 500. Sistema estable." }

$md | Out-File -FilePath $mdPath -Encoding utf8
Write-Host "Saved Extended QA Report: $mdPath" -ForegroundColor DarkGray
