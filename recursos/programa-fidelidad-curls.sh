# ====================================================================
# CURLs para Programa de Fidelidad - NexooHub Almacén
# ====================================================================
# Descripción: Colección de comandos cURL para probar la API de
#              programa de fidelidad de clientes.
#
# Autor: NexooHub Development Team
# Fecha: 2026-03-11
#
# IMPORTANTE: Reemplazar {TOKEN} con el JWT obtenido del endpoint de login
#             Reemplazar {CLIENTE_ID} con un ID de cliente válido
# ====================================================================

# Variables globales (reemplazar antes de usar)
BASE_URL="http://localhost:8080/api/v1"
TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
CLIENTE_ID="1"

# ====================================================================
# 1. CREAR PROGRAMA DE FIDELIDAD
# ====================================================================
# Crea un nuevo programa de fidelidad para un cliente

curl -X POST "${BASE_URL}/fidelidad/programa?clienteId=${CLIENTE_ID}" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json"

# Ejemplo de respuesta exitosa (201):
# {
#   "exitoso": true,
#   "mensaje": "Programa de fidelidad creado exitosamente",
#   "data": {
#     "id": 1,
#     "clienteId": 1,
#     "clienteNombre": "Juan Pérez",
#     "puntosAcumulados": 0,
#     "totalCompras": 0.00,
#     "totalCanjeado": 0.00,
#     "activo": true,
#     "fechaCreacion": "2026-03-11T10:30:00",
#     "fechaActualizacion": "2026-03-11T10:30:00"
#   }
# }

# ====================================================================
# 2. CONSULTAR PROGRAMA POR CLIENTE
# ====================================================================
# Obtiene la información del programa de fidelidad de un cliente

curl -X GET "${BASE_URL}/fidelidad/programa/cliente/${CLIENTE_ID}" \
  -H "Authorization: Bearer ${TOKEN}"

# Ejemplo de respuesta exitosa (200):
# {
#   "id": 1,
#   "clienteId": 1,
#   "clienteNombre": "Juan Pérez",
#   "puntosAcumulados": 150,
#   "totalCompras": 1500.00,
#   "totalCanjeado": 0.00,
#   "activo": true,
#   "fechaCreacion": "2026-03-11T10:30:00",
#   "fechaActualizacion": "2026-03-11T14:20:00"
# }

# ====================================================================
# 3. ACUMULAR PUNTOS
# ====================================================================
# Acumula puntos por una compra (1 punto por cada $10 MXN)

curl -X POST "${BASE_URL}/fidelidad/acumular" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "clienteId": 1,
    "montoCompra": 250.00,
    "ventaId": 100,
    "descripcion": "Compra de productos varios"
  }'

# Ejemplo con monto mínimo ($10 = 1 punto)
curl -X POST "${BASE_URL}/fidelidad/acumular" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "clienteId": 1,
    "montoCompra": 10.00,
    "descripcion": "Compra pequeña"
  }'

# Ejemplo con compra grande ($1000 = 100 puntos)
curl -X POST "${BASE_URL}/fidelidad/acumular" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "clienteId": 1,
    "montoCompra": 1000.00,
    "ventaId": 101,
    "descripcion": "Compra mayorista"
  }'

# Ejemplo de respuesta exitosa (200):
# {
#   "exitoso": true,
#   "mensaje": "Puntos acumulados exitosamente",
#   "data": {
#     "id": 1,
#     "clienteId": 1,
#     "clienteNombre": "Juan Pérez",
#     "puntosAcumulados": 25,
#     "totalCompras": 250.00,
#     "totalCanjeado": 0.00,
#     "activo": true,
#     "fechaCreacion": "2026-03-11T10:30:00",
#     "fechaActualizacion": "2026-03-11T11:15:00"
#   }
# }

# ====================================================================
# 4. CANJEAR PUNTOS
# ====================================================================
# Canjea puntos por descuento (100 puntos = $10 MXN)

curl -X POST "${BASE_URL}/fidelidad/canjear" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "clienteId": 1,
    "puntosACanjear": 100,
    "ventaId": 102,
    "descripcion": "Canje por descuento en compra"
  }'

# Ejemplo de canje de 200 puntos ($20 MXN de descuento)
curl -X POST "${BASE_URL}/fidelidad/canjear" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "clienteId": 1,
    "puntosACanjear": 200,
    "descripcion": "Descuento aplicado"
  }'

# Ejemplo de respuesta exitosa (200):
# {
#   "exitoso": true,
#   "mensaje": "Puntos canjeados exitosamente",
#   "data": {
#     "id": 1,
#     "clienteId": 1,
#     "clienteNombre": "Juan Pérez",
#     "puntosAcumulados": 50,
#     "totalCompras": 1500.00,
#     "totalCanjeado": 10.00,
#     "activo": true,
#     "fechaCreacion": "2026-03-11T10:30:00",
#     "fechaActualizacion": "2026-03-11T15:45:00"
#   }
# }

# ====================================================================
# 5. OBTENER HISTORIAL DE MOVIMIENTOS
# ====================================================================
# Obtiene el historial completo de acumulaciones y canjes

curl -X GET "${BASE_URL}/fidelidad/historial/cliente/${CLIENTE_ID}" \
  -H "Authorization: Bearer ${TOKEN}"

# Ejemplo de respuesta exitosa (200):
# [
#   {
#     "id": 1,
#     "programaId": 1,
#     "tipoMovimiento": "ACUMULACION",
#     "puntos": 25,
#     "montoAsociado": 250.00,
#     "ventaId": 100,
#     "descripcion": "Compra de productos varios",
#     "fechaCreacion": "2026-03-11T11:15:00"
#   },
#   {
#     "id": 2,
#     "programaId": 1,
#     "tipoMovimiento": "CANJE",
#     "puntos": -100,
#     "montoAsociado": 10.00,
#     "ventaId": 102,
#     "descripcion": "Canje de 100 puntos por $10.00 MXN",
#     "fechaCreacion": "2026-03-11T15:45:00"
#   }
# ]

# ====================================================================
# 6. CALCULAR DESCUENTO POR PUNTOS
# ====================================================================
# Calcula cuánto descuento equivale a una cantidad de puntos

# Calcular descuento de 100 puntos
curl -X GET "${BASE_URL}/fidelidad/calcular-descuento?puntos=100" \
  -H "Authorization: Bearer ${TOKEN}"

# Calcular descuento de 250 puntos
curl -X GET "${BASE_URL}/fidelidad/calcular-descuento?puntos=250" \
  -H "Authorization: Bearer ${TOKEN}"

# Calcular descuento de 50 puntos (menos del mínimo)
curl -X GET "${BASE_URL}/fidelidad/calcular-descuento?puntos=50" \
  -H "Authorization: Bearer ${TOKEN}"

# Ejemplo de respuesta exitosa (200):
# {
#   "puntos": 200,
#   "descuentoMXN": 20.00
# }

# ====================================================================
# 7. OBTENER ESTADÍSTICAS DEL SISTEMA
# ====================================================================
# Obtiene estadísticas generales del programa de fidelidad

curl -X GET "${BASE_URL}/fidelidad/estadisticas" \
  -H "Authorization: Bearer ${TOKEN}"

# Ejemplo de respuesta exitosa (200):
# {
#   "totalProgramasActivos": 150,
#   "totalPuntosEnSistema": 50000,
#   "tasaConversionPuntos": 10,
#   "tasaConversionDescuento": 100
# }

# ====================================================================
# 8. DESACTIVAR PROGRAMA
# ====================================================================
# Desactiva el programa de fidelidad de un cliente

curl -X PATCH "${BASE_URL}/fidelidad/programa/cliente/${CLIENTE_ID}/desactivar" \
  -H "Authorization: Bearer ${TOKEN}"

# Ejemplo de respuesta exitosa (200):
# {
#   "exitoso": true,
#   "mensaje": "Programa de fidelidad desactivado"
# }

# ====================================================================
# 9. REACTIVAR PROGRAMA
# ====================================================================
# Reactiva el programa de fidelidad de un cliente

curl -X PATCH "${BASE_URL}/fidelidad/programa/cliente/${CLIENTE_ID}/reactivar" \
  -H "Authorization: Bearer ${TOKEN}"

# Ejemplo de respuesta exitosa (200):
# {
#   "exitoso": true,
#   "mensaje": "Programa de fidelidad reactivado"
# }

# ====================================================================
# CASOS DE ERROR COMUNES
# ====================================================================

# Error 400: Cliente ya tiene programa
curl -X POST "${BASE_URL}/fidelidad/programa?clienteId=${CLIENTE_ID}" \
  -H "Authorization: Bearer ${TOKEN}"
# Respuesta: {
#   "timestamp": "2026-03-11T10:30:00",
#   "status": 400,
#   "error": "Bad Request",
#   "message": "El cliente ya tiene un programa de fidelidad activo",
#   "path": "/api/v1/fidelidad/programa"
# }

# Error 400: Monto insuficiente para generar puntos
curl -X POST "${BASE_URL}/fidelidad/acumular" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "clienteId": 1,
    "montoCompra": 5.00
  }'
# Respuesta: {
#   "message": "El monto de compra es insuficiente para generar puntos (mínimo $10 MXN)"
# }

# Error 400: Puntos insuficientes para canjear
curl -X POST "${BASE_URL}/fidelidad/canjear" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "clienteId": 1,
    "puntosACanjear": 500
  }'
# Respuesta: {
#   "message": "Puntos insuficientes. Disponibles: 150"
# }

# Error 400: Menos del mínimo requerido para canjear
curl -X POST "${BASE_URL}/fidelidad/canjear" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "clienteId": 1,
    "puntosACanjear": 50
  }'
# Respuesta: {
#   "message": "Se requieren mínimo 100 puntos para canjear"
# }

# Error 404: Cliente no encontrado
curl -X POST "${BASE_URL}/fidelidad/programa?clienteId=999999" \
  -H "Authorization: Bearer ${TOKEN}"
# Respuesta: {
#   "timestamp": "2026-03-11T10:30:00",
#   "status": 404,
#   "error": "Not Found",
#   "message": "Cliente no encontrado con ID: 999999",
#   "path": "/api/v1/fidelidad/programa"
# }

# Error 404: Cliente sin programa de fidelidad
curl -X GET "${BASE_URL}/fidelidad/programa/cliente/999" \
  -H "Authorization: Bearer ${TOKEN}"
# Respuesta: {
#   "message": "El cliente no tiene programa de fidelidad"
# }

# ====================================================================
# NOTAS IMPORTANTES
# ====================================================================
# 1. Reglas de negocio:
#    - 1 punto por cada $10 MXN de compra
#    - 100 puntos = $10 MXN de descuento (ratio 10:1)
#    - Mínimo 100 puntos para canjear
#    - Los puntos no caducan
#
# 2. Estados del programa:
#    - activo: true  -> Puede acumular y canjear puntos
#    - activo: false -> No puede realizar operaciones
#
# 3. Tipos de movimiento:
#    - ACUMULACION: Puntos positivos por compras
#    - CANJE: Puntos negativos por descuentos aplicados
#
# 4. Validaciones:
#    - Monto mínimo para acumular: $10.00 MXN
#    - Puntos mínimos para canjear: 100
#    - Un cliente solo puede tener un programa activo
#
# 5. Seguridad:
#    - Todos los endpoints requieren autenticación JWT
#    - Token debe incluirse en header: Authorization: Bearer {token}
#
# ====================================================================
# FIN DEL DOCUMENTO
# ====================================================================
