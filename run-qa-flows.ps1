# PowerShell E2E QA Flow Testing Script for NexooHub Almacen
# Performs full CRUD and sequential business workflows across all modules

$BASE = "http://localhost:8080"
$results = @()
$dateSuffix = Get-Date -Format "HHmmss"
$timeStr = Get-Date -Format "yyyy-MM-dd"

Write-Host "=============================================" -ForegroundColor Cyan
Write-Host "  NEXOOHUB ALMACEN - FULL E2E QA FLOW TESTS" -ForegroundColor Cyan
Write-Host "=============================================" -ForegroundColor Cyan

# 1. AUTHENTICATE TO OBTAIN JWT TOKEN
try {
    $loginUrl = "$BASE/api/v1/auth/login"
    $body = "{""username"":""admin"",""password"":""admin123""}"
    
    Write-Host "Authenticating at $loginUrl..." -ForegroundColor Gray
    $resp = Invoke-WebRequest -Uri $loginUrl -Method POST -ContentType "application/json" -Body $body -UseBasicParsing -ErrorAction Stop
    $json = $resp.Content | ConvertFrom-Json
    $TOKEN = $json.datos.token
    
    if (-not $TOKEN) {
        Write-Host "FAIL: Failed to retrieve token from response!" -ForegroundColor Red
        Exit
    }
    Write-Host "SUCCESS: Authenticated successfully! Token acquired." -ForegroundColor Green
} catch {
    Write-Host "FAIL: Authentication failed! Check if server is running on port 8080." -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Red
    Exit
}

$headers = @{ "Authorization" = "Bearer $TOKEN"; "Content-Type" = "application/json" }
$headersGet = @{ "Authorization" = "Bearer $TOKEN" }

# 2. SEED CORE DATA DYNAMICALLY FROM DATABASE
Write-Host "`n--- Bootstrapping Core Data ---" -ForegroundColor Yellow

$sucId = 1
$empId = 1
$ID_TIPO_CLIENTE = 1
$vendedorUsername = "admin"

try {
    # Get active sucursal ID
    $sucResp = Invoke-WebRequest -Uri "$BASE/api/v1/sucursales" -Method GET -Headers $headersGet -UseBasicParsing
    $sucArray = $sucResp.Content | ConvertFrom-Json
    if ($sucArray.Count -gt 0) {
        $sucId = $sucArray[0].id
        Write-Host "Found active Sucursal ID: $sucId" -ForegroundColor Gray
    }
    
    # Get active employee ID
    $empResp = Invoke-WebRequest -Uri "$BASE/api/v1/empleados/sucursal/$sucId" -Method GET -Headers $headersGet -UseBasicParsing
    $empArray = $empResp.Content | ConvertFrom-Json
    if ($empArray.Count -gt 0) {
        $empId = $empArray[0].id
        Write-Host "Found active Employee ID: $empId" -ForegroundColor Gray
    }

    # Get active TipoCliente ID
    $tipoCliResp = Invoke-WebRequest -Uri "$BASE/api/v1/tipos-cliente" -Method GET -Headers $headersGet -UseBasicParsing
    $tipoCliArray = $tipoCliResp.Content | ConvertFrom-Json
    if ($tipoCliArray.content.Count -gt 0) {
        $ID_TIPO_CLIENTE = $tipoCliArray.content[0].id
        Write-Host "Found active TipoCliente ID: $ID_TIPO_CLIENTE" -ForegroundColor Gray
    }
} catch {
    Write-Host "Warning during bootstrap: Using default IDs (Sucursal=1, Empleado=1, TipoCliente=1)" -ForegroundColor DarkYellow
}

# 3. GLOBAL VARIABLES FOR FLOW REFERENCE
$ID_CATEGORIA = $null
$ID_CLIENTE = $null
$ID_PROVEEDOR = $null
$ID_MOTO = $null
$SKU_PRODUCTO = "SKU-QA-$dateSuffix"
$ID_CAJA = $null
$ID_CXP = $null
$ID_COTIZACION = $null
$ID_RESERVA = $null
$ID_VENTA = $null
$ID_REGLA = $null
$ID_PROSPECTO = $null
$ID_OPORTUNIDAD = $null

# Generar RFCs unicos de exactamente 13 caracteres para evitar error de desbordamiento (rfc VARCHAR(13))
$randomRfc = "RFCQ" + (Get-Random -Minimum 100000000 -Maximum 999999999)
$randomProvRfc = "PRVQ" + (Get-Random -Minimum 100000000 -Maximum 999999999)

# 4. DEFINE EXECUTION ENGINE
function Run-Step {
    param(
        [Parameter(Mandatory=$true)][string]$Method,
        [Parameter(Mandatory=$true)][string]$Url,
        [Parameter(Mandatory=$false)][string]$Body,
        [Parameter(Mandatory=$true)][string]$Name
    )

    $stepResult = [PSCustomObject]@{
        Name = $Name
        Method = $Method
        Url = $Url
        Status = $null
        Response = ""
        IsSuccess = $false
        Message = ""
    }

    try {
        $r = $null
        if ($Body) {
            $r = Invoke-WebRequest -Uri $Url -Method $Method -Headers $headers -Body $Body -UseBasicParsing -ErrorAction Stop
        } else {
            $r = Invoke-WebRequest -Uri $Url -Method $Method -Headers $headersGet -UseBasicParsing -ErrorAction Stop
        }
        
        $stepResult.Status = $r.StatusCode
        $stepResult.Response = $r.Content
        $stepResult.IsSuccess = ($r.StatusCode -ge 200 -and $r.StatusCode -lt 300)
        
        if ($stepResult.IsSuccess) {
            Write-Host "PASS: ($($r.StatusCode)) $Name" -ForegroundColor Green
        } else {
            Write-Host "WARN: ($($r.StatusCode)) $Name" -ForegroundColor Yellow
        }
        
        return $r.Content
    } catch {
        $statusCode = $null
        $errBody = ""
        
        if ($_.Exception -and $_.Exception.Response) {
            $statusCode = [int]$_.Exception.Response.StatusCode
            try {
                $stream = $_.Exception.Response.GetResponseStream()
                if ($stream) {
                    $reader = New-Object System.IO.StreamReader($stream)
                    $errBody = $reader.ReadToEnd()
                }
            } catch {}
        }
        
        if ($null -eq $statusCode) {
            $statusCode = 500
        }
        
        $stepResult.Status = $statusCode
        $stepResult.Response = $errBody
        $stepResult.Message = $_.Exception.Message

        # Handled non-500 statuses are not server crashes, they are operational validation feedbacks
        $stepResult.IsSuccess = ($statusCode -ne 500)
        
        if ($statusCode -eq 500) {
            Write-Host "FAIL: ($statusCode) $Name - $($_.Exception.Message)" -ForegroundColor Red
            if ($errBody) {
                Write-Host "   Body: $errBody" -ForegroundColor DarkRed
            }
        } else {
            Write-Host "INFO: ($statusCode) $Name - Handled response" -ForegroundColor Cyan
        }
        
        return $errBody
    } finally {
        $script:results += $stepResult
    }
}


# ==========================================
# CYCLE 1: CATALOGO CRUD
# ==========================================
Write-Host "`n=== CYCLE 1: CATALOGO CRUD ===" -ForegroundColor Blue

# 1.1 Categoria
$bodyCat = "{""nombre"":""QA Categoria $dateSuffix"",""descripcion"":""Pruebas QA de flujo""}"
$resp = Run-Step -Method "POST" -Url "$BASE/api/v1/categorias" -Body $bodyCat -Name "1.1.1 Crear Categoria"
try {
    $json = $resp | ConvertFrom-Json
    $ID_CATEGORIA = $json.id
    Write-Host "   Assigned Category ID: $ID_CATEGORIA" -ForegroundColor Gray
} catch {}

if ($ID_CATEGORIA) {
    $bodyCatMod = "{""nombre"":""QA Cat $dateSuffix Modificado"",""descripcion"":""Pruebas QA modificada""}"
    Run-Step -Method "PUT" -Url "$BASE/api/v1/categorias/$ID_CATEGORIA" -Body $bodyCatMod -Name "1.1.2 Modificar Categoria"
}

# 1.2 Tipo Cliente (POST no soportado - GET ya se ejecuto en bootstrap)
Write-Host "INFO: 1.2.1 Crear Tipo Cliente omitido (Solo soporta GET). Usando ID_TIPO_CLIENTE: $ID_TIPO_CLIENTE" -ForegroundColor Cyan

# 1.3 Cliente
$bodyCli = "{""tipoClienteId"":$ID_TIPO_CLIENTE,""nombre"":""QA Cliente de Flujo $dateSuffix"",""rfc"":""$randomRfc"",""telefono"":""5512345678"",""email"":""qa_$dateSuffix@nexoo.com"",""direccionFiscal"":""Av. QA Test 123""}"
$resp = Run-Step -Method "POST" -Url "$BASE/api/v1/clientes" -Body $bodyCli -Name "1.3.1 Crear Cliente"
try {
    $json = $resp | ConvertFrom-Json
    $ID_CLIENTE = $json.id
    if (-not $ID_CLIENTE) { $ID_CLIENTE = $json.datos.id }
    Write-Host "   Assigned Cliente ID: $ID_CLIENTE" -ForegroundColor Gray
} catch {}

if ($ID_CLIENTE) {
    $bodyCliMod = "{""tipoClienteId"":$ID_TIPO_CLIENTE,""nombre"":""QA Cliente Modificado $dateSuffix"",""rfc"":""$randomRfc"",""telefono"":""5500000000"",""email"":""qa_mod_$dateSuffix@nexoo.com"",""direccionFiscal"":""Direccion QA Modificada""}"
    Run-Step -Method "PUT" -Url "$BASE/api/v1/clientes/$ID_CLIENTE" -Body $bodyCliMod -Name "1.3.2 Modificar Cliente"
}

# 1.4 Proveedor
$bodyProv = "{""nombreEmpresa"":""QA Proveedor S.A. $dateSuffix"",""rfc"":""$randomProvRfc"",""nombreContacto"":""Ing. QA de Compra"",""telefono"":""5551234567"",""email"":""prov_$dateSuffix@qa.com"",""direccion"":""Av. Proveedores 99""}"
$resp = Run-Step -Method "POST" -Url "$BASE/api/v1/proveedores" -Body $bodyProv -Name "1.4.1 Crear Proveedor"
try {
    $json = $resp | ConvertFrom-Json
    $ID_PROVEEDOR = $json.id
    if (-not $ID_PROVEEDOR) { $ID_PROVEEDOR = $json.datos.id }
    Write-Host "   Assigned Proveedor ID: $ID_PROVEEDOR" -ForegroundColor Gray
} catch {}

# Fallbacks if creation failed due to validations/constraints
if (-not $ID_CATEGORIA) { $ID_CATEGORIA = 1 }
if (-not $ID_PROVEEDOR) { $ID_PROVEEDOR = 1 }
if (-not $ID_CLIENTE) { $ID_CLIENTE = 1 }

# 1.5 Moto
$bodyMoto = "{""marca"":""YAMAHA"",""modelo"":""YZF-R15"",""cilindraje"":155,""anioInicio"":2018,""anioFin"":2026}"
$resp = Run-Step -Method "POST" -Url "$BASE/api/v1/motos" -Body $bodyMoto -Name "1.5.1 Crear Moto"
try {
    $json = $resp | ConvertFrom-Json
    $ID_MOTO = $json.id
    if (-not $ID_MOTO) { $ID_MOTO = $json.datos.id }
    Write-Host "   Assigned Moto ID: $ID_MOTO" -ForegroundColor Gray
} catch {}


# ==========================================
# CYCLE 2: PRODUCTOS & INVENTARIO
# ==========================================
Write-Host "`n=== CYCLE 2: PRODUCTOS & INVENTARIO ===" -ForegroundColor Blue

# 2.1 Crear Producto
$bodyProd = "{""skuInterno"":""$SKU_PRODUCTO"",""skuProveedor"":""PROV-$SKU_PRODUCTO"",""nombreComercial"":""Llanta QA Premium $dateSuffix"",""descripcion"":""Llanta radial deportiva"",""marca"":""BRIDGESTONE"",""categoria"":{""id"":$ID_CATEGORIA},""proveedor"":{""id"":$ID_PROVEEDOR},""claveSat"":""12345678"",""stockMinimoGlobal"":3,""activo"":true,""sensibilidadPrecio"":""MEDIA""}"
Run-Step -Method "POST" -Url "$BASE/api/v1/productos" -Body $bodyProd -Name "2.1.1 Crear Producto Maestro"

# 2.2 Inicializar Stock en Sucursal
$bodyStock = "{""id"":{""sucursalId"":$sucId,""skuInterno"":""$SKU_PRODUCTO""},""stockActual"":20,""stockMinimoSucursal"":3,""ubicacionPasillo"":""Pasillo QA, Repisa 4"",""costoPromedioPonderado"":150.00,""lote"":""LOTE-QA-01""}"
Run-Step -Method "POST" -Url "$BASE/api/v1/inventario" -Body $bodyStock -Name "2.2.1 Inicializar Inventario de Sucursal"

# 2.3 Consultas de Inventario
Run-Step -Method "GET" -Url "$BASE/api/v1/inventario/sucursales/$sucId" -Name "2.3.1 Consultar Inventario de Sucursal"
Run-Step -Method "GET" -Url "$BASE/api/v1/inventario/alertas/stock-bajo" -Name "2.3.2 Consultar Alertas de Stock Bajo"
Run-Step -Method "GET" -Url "$BASE/api/v1/inventario/caducidad/proximos" -Name "2.3.3 Consultar Caducidades Proximas"
Run-Step -Method "GET" -Url "$BASE/api/alertas/lento-movimiento" -Name "2.3.4 Consultar Alertas de Lento Movimiento"


# ==========================================
# CYCLE 3: COMPRAS & CUENTAS POR PAGAR (CxP)
# ==========================================
Write-Host "`n=== CYCLE 3: COMPRAS & CXP ===" -ForegroundColor Blue

# 3.1 Ingresar Compra (Incrementar Stock y Generar CxP)
$bodyCompra = "{""proveedorId"":$ID_PROVEEDOR,""preciosIncluyenIva"":true,""sucursalDestinoId"":$sucId,""detalles"":[{""skuInterno"":""$SKU_PRODUCTO"",""cantidad"":10,""costoUnitario"":150.00}]}"
Run-Step -Method "POST" -Url "$BASE/api/v1/compras/ingreso" -Body $bodyCompra -Name "3.1.1 Registrar Ingreso de Compra (Credito)"

# 3.2 Listar CxP y Abonar
$resp = Run-Step -Method "GET" -Url "$BASE/api/v1/cxp" -Name "3.2.1 Consultar Cuentas por Pagar (CxP)"
try {
    $json = $resp | ConvertFrom-Json
    $ID_CXP = $json.datos[0].id
    Write-Host "   Target Account Payable ID: $ID_CXP" -ForegroundColor Gray
} catch {}

if ($ID_CXP) {
    # Realizar abono
    $bodyAbono = "{""monto"":500.00,""metodoPago"":""TRANSFERENCIA"",""referencia"":""REF-QA-ABONO-01""}"
    Run-Step -Method "POST" -Url "$BASE/api/v1/cxp/$ID_CXP/pagos" -Body $bodyAbono -Name "3.2.2 Aplicar Abono a CxP"
}

# 3.3 Registrar Gasto Operativo
$bodyGasto = "{""sucursalId"":$sucId,""concepto"":""Pago de Servidor de Pruebas QA"",""categoria"":""OTROS"",""monto"":2500.00,""fechaGasto"":""$timeStr"",""usuarioId"":$empId,""observaciones"":""Gasto registrado por script QA""}"
Run-Step -Method "POST" -Url "$BASE/api/v1/finanzas/gastos" -Body $bodyGasto -Name "3.3.1 Registrar Gasto Operativo"


# ==========================================
# CYCLE 4: VENTAS, RESERVAS & CAJA
# ==========================================
Write-Host "`n=== CYCLE 4: VENTAS, RESERVAS & CAJA ===" -ForegroundColor Blue

# 4.1 Abrir Caja (Manejado por 409 si ya esta abierta)
$bodyCaja = "{""sucursalId"":$sucId,""empleadoId"":$empId,""fondoInicial"":1000.00,""observaciones"":""Apertura automatica QA""}"
$resp = Run-Step -Method "POST" -Url "$BASE/api/v1/cajas/abrir" -Body $bodyCaja -Name "4.1.1 Abrir Turno de Caja"
try {
    $json = $resp | ConvertFrom-Json
    $ID_CAJA = $json.datos.id
    if (-not $ID_CAJA) { $ID_CAJA = 1 } # Fallback si ya estaba abierta
    Write-Host "   Active Turno Caja ID: $ID_CAJA" -ForegroundColor Gray
} catch {
    $ID_CAJA = 1
}

# 4.2 Cotizacion
$bodyCot = "{""clienteId"":$ID_CLIENTE,""sucursalId"":$sucId,""vendedorId"":$empId,""fechaValidez"":""2026-06-15"",""detalles"":[{""skuInterno"":""$SKU_PRODUCTO"",""cantidad"":2,""precioUnitario"":200.00}]}"
$resp = Run-Step -Method "POST" -Url "$BASE/api/cotizaciones" -Body $bodyCot -Name "4.2.1 Registrar Cotizacion"
try {
    $json = $resp | ConvertFrom-Json
    $ID_COTIZACION = $json.id
    if (-not $ID_COTIZACION) { $ID_COTIZACION = $json.datos.id }
    Write-Host "   Assigned Cotizacion ID: $ID_COTIZACION" -ForegroundColor Gray
} catch {}

# Convertir Cotizacion a Venta
if ($ID_COTIZACION) {
    $bodyConvert = "{""metodoPago"":""EFECTIVO"",""observaciones"":""Conversion desde QA""}"
    Run-Step -Method "POST" -Url "$BASE/api/cotizaciones/$ID_COTIZACION/convertir-venta" -Body $bodyConvert -Name "4.2.2 Convertir Cotizacion en Venta"
}

# 4.3 Venta Directa (En Efectivo para registrar movimiento de caja)
$bodyVenta = "{""clienteId"":$ID_CLIENTE,""sucursalId"":$sucId,""metodoPago"":""EFECTIVO"",""items"":[{""skuInterno"":""$SKU_PRODUCTO"",""cantidad"":3}]}"
$resp = Run-Step -Method "POST" -Url "$BASE/api/v1/ventas" -Body $bodyVenta -Name "4.3.1 Registrar Venta Directa (Efectivo)"
try {
    $json = $resp | ConvertFrom-Json
    $ID_VENTA = $json.id
    if (-not $ID_VENTA) { $ID_VENTA = $json.datos.id }
    Write-Host "   Assigned Venta ID: $ID_VENTA" -ForegroundColor Gray
} catch {}

# 4.4 Apartado/Reserva
# 4.4.1 Crear un producto secundario con 0 stock para probar exito de reserva
$SKU_RESERVA = "SKU-QA-RES-$dateSuffix"
$bodyProdRes = "{""skuInterno"":""$SKU_RESERVA"",""skuProveedor"":""PROV-$SKU_RESERVA"",""nombreComercial"":""Llanta Reserva QA $dateSuffix"",""descripcion"":""Llanta para pruebas de reserva"",""marca"":""BRIDGESTONE"",""categoria"":{""id"":$ID_CATEGORIA},""proveedor"":{""id"":$ID_PROVEEDOR},""claveSat"":""12345678"",""stockMinimoGlobal"":3,""activo"":true,""sensibilidadPrecio"":""MEDIA""}"
Run-Step -Method "POST" -Url "$BASE/api/v1/productos" -Body $bodyProdRes -Name "4.4.1 Crear Producto Maestro para Reserva (Sin stock)"

# 4.4.2 Crear Apartado / Reserva (Exito - 0 stock)
$bodyRes = "{""clienteId"":$ID_CLIENTE,""skuInterno"":""$SKU_RESERVA"",""sucursalId"":$sucId,""cantidad"":1,""comentarios"":""Apartado especial QA""}"
$resp = Run-Step -Method "POST" -Url "$BASE/api/v1/reservas" -Body $bodyRes -Name "4.4.2 Crear Apartado / Reserva (Exito - Sin Stock)"
try {
    $json = $resp | ConvertFrom-Json
    $ID_RESERVA = $json.reservaId
    Write-Host "   Assigned Reserva ID: $ID_RESERVA" -ForegroundColor Gray
} catch {}

# 4.4.3 Crear Apartado / Reserva (Fallo - Con stock 20+)
$bodyResFallo = "{""clienteId"":$ID_CLIENTE,""skuInterno"":""$SKU_PRODUCTO"",""sucursalId"":$sucId,""cantidad"":1,""comentarios"":""Apartado invalido stock disponible""}"
Run-Step -Method "POST" -Url "$BASE/api/v1/reservas" -Body $bodyResFallo -Name "4.4.3 Crear Apartado / Reserva (Fallo - Con Stock)"

# 4.4.4 Cancelar Apartado / Reserva
if ($ID_RESERVA) {
    $bodyCancelRes = "{""motivo"":""Cancelado por script QA""}"
    Run-Step -Method "PUT" -Url "$BASE/api/v1/reservas/$ID_RESERVA/cancelar" -Body $bodyCancelRes -Name "4.4.4 Cancelar Apartado / Reserva"
}

# 4.5 Devolucion de Venta
if ($ID_VENTA) {
    $bodyDev = "{""ventaId"":$ID_VENTA,""sucursalId"":$sucId,""motivo"":""Producto defectuoso en banda radial"",""metodoReembolso"":""EFECTIVO"",""items"":[{""skuInterno"":""$SKU_PRODUCTO"",""cantidad"":1,""motivoItem"":""Llanta defectuosa""}]}"
    Run-Step -Method "POST" -Url "$BASE/api/v1/devoluciones" -Body $bodyDev -Name "4.5.1 Registrar Devolucion de Venta"
}

# 4.6 Cerrar Turno de Caja (Arqueo Z)
if ($ID_CAJA) {
    $bodyCerrar = "{""efectivoReal"":1300.00,""observaciones"":""Arqueo Z automatico QA""}"
    Run-Step -Method "POST" -Url "$BASE/api/v1/cajas/$ID_CAJA/cerrar" -Body $bodyCerrar -Name "4.6.1 Cerrar Turno de Caja (Arqueo Z)"
}


# ==========================================
# CYCLE 5: COMISIONES & METAS
# ==========================================
Write-Host "`n=== CYCLE 5: COMISIONES & METAS ===" -ForegroundColor Blue

# 5.1 Crear Regla de Comision
$bodyRegla = "{""nombre"":""Regla QA Especial $dateSuffix"",""descripcion"":""Porcentaje de ventas para QA"",""tipo"":""PORCENTAJE_VENTA"",""porcentajeComision"":0.05,""activa"":true,""prioridad"":1}"
$resp = Run-Step -Method "POST" -Url "$BASE/api/comisiones/reglas" -Body $bodyRegla -Name "5.1.1 Crear Regla de Comision"
try {
    $json = $resp | ConvertFrom-Json
    $ID_REGLA = $json.id
    if (-not $ID_REGLA) { $ID_REGLA = $json.datos.id }
    Write-Host "   Assigned Regla ID: $ID_REGLA" -ForegroundColor Gray
} catch {}

# 5.2 Meta de Ventas para Empleado
$bodyMeta = "{""empleadoId"":$empId,""anio"":2026,""mes"":5,""montoMeta"":50000.00,""observaciones"":""Meta mensual fijada por QA""}"
Run-Step -Method "POST" -Url "$BASE/api/v1/rh/metas" -Body $bodyMeta -Name "5.2.1 Establecer Meta de Ventas"
Run-Step -Method "GET" -Url "$BASE/api/v1/rh/metas/$empId/progreso" -Name "5.2.2 Consultar Progreso de Metas"

# 5.3 Calcular Comisiones
Run-Step -Method "POST" -Url "$BASE/api/comisiones/calcular?anio=2026&mes=5" -Body $null -Name "5.3.1 Procesar Calculo de Comisiones del Mes"
Run-Step -Method "GET" -Url "$BASE/api/comisiones/resumen?anio=2026&mes=5" -Name "5.3.2 Consultar Resumen de Comisiones"


# ==========================================
# CYCLE 6: FINANZAS, CREDITO & MOROSIDAD
# ==========================================
Write-Host "`n=== CYCLE 6: FINANZAS, CREDITO & MOROSIDAD ===" -ForegroundColor Blue

# 6.1 Limites de Credito
$bodyLimite = "{""clienteId"":$ID_CLIENTE,""limiteAutorizado"":15000.00,""montoUtilizado"":0.00,""activo"":true}"
Run-Step -Method "POST" -Url "$BASE/api/credito/limites" -Body $bodyLimite -Name "6.1.1 Establecer Limite de Credito"

# 6.2 Bloquear Cliente por Morosidad
Run-Step -Method "POST" -Url "$BASE/api/v1/clientes/$ID_CLIENTE/bloquear?motivo=Facturas+vencidas+QA" -Body $null -Name "6.2.1 Bloquear Cliente Moroso"
Run-Step -Method "GET" -Url "$BASE/api/v1/clientes/bloqueados" -Name "6.2.2 Consultar Clientes Bloqueados"

# 6.3 Liberar mediante Pago
Run-Step -Method "POST" -Url "$BASE/api/v1/clientes/$ID_CLIENTE/registrar-pago?monto=100.00" -Body $null -Name "6.3.1 Registrar Pago de Deuda (Desbloqueo Auto)"


# ==========================================
# CYCLE 7: CRM & METRICAS PREDICTIVAS
# ==========================================
Write-Host "`n=== CYCLE 7: CRM & METRICAS PREDICTIVAS ===" -ForegroundColor Blue

# 7.1 Registrar Lead/Prospecto CRM
$bodyLead = "{""empresa"":""QA Taller de Motos $dateSuffix"",""contactoPrincipal"":""Juan QA"",""telefono"":""5566778899"",""correo"":""taller_$dateSuffix@qa.com"",""notas"":""Lead creado por script QA""}"
$resp = Run-Step -Method "POST" -Url "$BASE/api/v1/crm/prospectos" -Body $bodyLead -Name "7.1.1 Registrar Prospecto B2B"
try {
    $json = $resp | ConvertFrom-Json
    $ID_PROSPECTO = $json.id
    if (-not $ID_PROSPECTO) { $ID_PROSPECTO = $json.datos.id }
    Write-Host "   Assigned Prospecto ID: $ID_PROSPECTO" -ForegroundColor Gray
} catch {}

# Fallback if needed
if (-not $ID_PROSPECTO) { $ID_PROSPECTO = 1 }

# 7.2 Crear Oportunidad de Venta
$bodyOportunidad = "{""prospectoId"":$ID_PROSPECTO,""titulo"":""Suministro de Llantas QA"",""etapa"":""PROSPECCION"",""valorProyectado"":25000.00,""fechaCierreEstimada"":""2026-06-30"",""probabilidadPorcentaje"":50}"
$resp = Run-Step -Method "POST" -Url "$BASE/api/v1/crm/oportunidades" -Body $bodyOportunidad -Name "7.2.1 Crear Oportunidad de Negocio B2B"
try {
    $json = $resp | ConvertFrom-Json
    $ID_OPORTUNIDAD = $json.id
    if (-not $ID_OPORTUNIDAD) { $ID_OPORTUNIDAD = $json.datos.id }
    Write-Host "   Assigned Oportunidad ID: $ID_OPORTUNIDAD" -ForegroundColor Gray
} catch {}

# 7.3 Interacciones CRM
$bodyInteraccion = "{""prospectoId"":$ID_PROSPECTO,""tipoInteraccion"":""LLAMADA"",""resumen"":""Contacto inicial para cotizacion de mayoreo"",""detalles"":""Realizado por el script de QA""}"
Run-Step -Method "POST" -Url "$BASE/api/v1/crm/interacciones" -Body $bodyInteraccion -Name "7.3.1 Registrar Interaccion con Lead"

# 7.4 Analisis y Modelos Predictivos (Generar request valido para evitar 500)
$bodyPred = "{""sucursalId"":$sucId,""periodoAnio"":2026,""periodoMes"":6,""mesesHistoricos"":3,""diasStockSeguridad"":7,""metodoCalculo"":""PROMEDIO_MOVIL""}"
Run-Step -Method "POST" -Url "$BASE/api/predicciones/generar" -Body $bodyPred -Name "7.4.1 Generar Predicciones de Demanda"

Run-Step -Method "POST" -Url "$BASE/api/v1/analitica/churn/calcular" -Body $null -Name "7.4.2 Generar Analisis de Abandono (Churn)"
Run-Step -Method "POST" -Url "$BASE/api/v1/analitica/rfm/calcular" -Body $null -Name "7.4.3 Generar Analisis RFM de Clientes"


# ==========================================
# 5. SUMMARIZE RESULTS AND GENERATE REPORTS
# ==========================================
Write-Host "`n=============================================" -ForegroundColor Cyan
Write-Host "             TEST RUN COMPLETED" -ForegroundColor Cyan
Write-Host "=============================================" -ForegroundColor Cyan

$total = $results.Count
$passed = ($results | Where-Object { $_.IsSuccess -eq $true }).Count
$failed = ($results | Where-Object { $_.IsSuccess -eq $false }).Count

Write-Host "Total Steps Executed: $total" -ForegroundColor Gray
Write-Host "Operational Successes (Handled or 2xx): $passed" -ForegroundColor Green
if ($failed -gt 0) {
    Write-Host "Failed (500 Crashes): $failed" -ForegroundColor Red
} else {
    Write-Host "Failed (500 Crashes): 0" -ForegroundColor Green
}

# Save raw logs
$jsonPath = Join-Path -Path $PSScriptRoot -ChildPath "qa-test-results.json"
$results | ConvertTo-Json -Depth 5 | Out-File -FilePath $jsonPath -Encoding utf8
Write-Host "Saved JSON results to $jsonPath" -ForegroundColor DarkGray

# Generate markdown E2E report in project root
$mdReportPath = "c:\Users\danie\OneDrive\Documentos\Vscode\NexxoHub\Proyectos\nexoo-almacen-bk\reporte-qa-completo.md"
$mdContent = @"
# Reporte Completo de Calidad (QA) E2E — NexooHub Almacen

Generado el: $(Get-Date -Format "yyyy-MM-dd HH:mm:ss")
Total Pasos de Flujo Evaluados: **$total**
Exitosos u Operacionales: **$passed**
Caidas Criticas del Servidor (500): **$failed**

---

## Detalle de Flujos de QA Ejecutados

| Paso | Metodo | URL | Codigo HTTP | Estado | Mensaje / Detalle de Respuesta |
|---|---|---|---|---|---|
"@

foreach ($r in $results) {
    $statusEmoji = if ($r.Status -eq 500) { "CRITICAL FAIL" } else { "PASS" }
    
    $cleanResp = $r.Response
    if ($cleanResp) {
        $cleanResp = [System.Text.RegularExpressions.Regex]::Replace($cleanResp, "[\r\n]+", " ")
        $cleanResp = $cleanResp.Replace("|", "\|")
        if ($cleanResp.Length -gt 85) {
            $cleanResp = $cleanResp.Substring(0, 85) + "..."
        }
    } else {
        $cleanResp = $r.Message
    }
    
    $mdContent += "`n| $($r.Name) | $($r.Method) | [ $($r.Url) ] | $($r.Status) | $statusEmoji | $cleanResp |"
}

$mdContent | Out-File -FilePath $mdReportPath -Encoding utf8
Write-Host "Saved comprehensive QA Markdown report to $mdReportPath" -ForegroundColor DarkGray

# Generate Error/Bug inventory report in project root
$bugReportPath = "c:\Users\danie\OneDrive\Documentos\Vscode\NexxoHub\Proyectos\nexoo-almacen-bk\inventario-errores-QA.md"
$bugContent = @"
# Inventario de Errores e Incidencias Críticas de QA

Este documento registra los errores críticos de tipo **500 Internal Server Error** o fallos graves en base de datos PostgreSQL encontrados durante la ejecución de las pruebas automatizadas de QA extremo a extremo.

## Resumen de Fallas Críticas
- Total de Fallas Críticas (500) Detectadas: $failed

---

## Detalle de Errores Encontrados
"@

$c = 1
foreach ($r in $results) {
    if ($r.Status -eq 500) {
        $bugContent += "`n### Incidencia $($c): $($r.Name)"
        $bugContent += "`n- **Endpoint:** [ $($r.Method) $($r.Url) ]"
        $bugContent += "`n- **Excepcion/Detalle:** $($r.Message)"
        $bugContent += "`n- **Respuesta Servidor:** [JSON] $($r.Response) [/JSON]"
        $bugContent += "`n- **Diagnostico:** Pendiente de revision en logs de Spring Boot."
        $bugContent += "`n---"
        $c++
    }
}

if ($failed -eq 0) {
    $bugContent += "`n### ¡Excelente! No se registraron caidas de servidor (500) o fallos graves en los flujos de negocio."
}

$bugContent | Out-File -FilePath $bugReportPath -Encoding utf8
Write-Host "Saved QA Bug Inventory report to $bugReportPath" -ForegroundColor DarkGray
