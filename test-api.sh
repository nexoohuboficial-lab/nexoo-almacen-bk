#!/bin/bash

BASE_URL="http://localhost:8080/api/v1"
ADMIN_USER="admin"
ADMIN_PASS="admin123"

echo "--- 🔐 1. LOGIN ---"
LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/auth/login" -H "Content-Type: application/json" -d "{\"username\": \"$ADMIN_USER\", \"password\": \"$ADMIN_PASS\"}")
TOKEN=$(echo $LOGIN_RESPONSE | sed -n 's/.*"token":"\([^"]*\)".*/\1/p')
AUTH_H="Authorization: Bearer $TOKEN"
JSON_H="Content-Type: application/json"

echo -e "\n--- 📚 2. REGISTRAR PRODUCTO ---"
curl -s -X POST "$BASE_URL/productos" -H "$AUTH_H" -H "$JSON_H" -d '{"skuInterno": "ACEITE-PRUEBA", "nombreComercial": "Aceite Sintético", "categoriaId": 1, "proveedorId": 1}' | grep -q "skuInterno" && echo "✅ Producto creado"

echo -e "\n--- 💰 3. INGRESO DE MERCANCÍA (COMPRA) ---"
# Llamamos a /compras/ingreso según tu controlador
curl -s -X POST "$BASE_URL/compras/ingreso" -H "$AUTH_H" -H "$JSON_H" -d '{
  "proveedorId": 1,
  "sucursalDestinoId": 1,
  "folioFacturaProveedor": "FACT-001",
  "preciosIncluyenIva": false,
  "detalles": [{"skuInterno": "ACEITE-PRUEBA", "cantidad": 20, "costoUnitario": 120.00}]
}' | grep -q "folioInterno" && echo "✅ Stock inyectado correctamente"

echo -e "\n--- 🛒 4. REALIZAR VENTA ---"
# Llamamos a /ventas según tu controlador
curl -s -X POST "$BASE_URL/ventas" -H "$AUTH_H" -H "$JSON_H" -d '{
  "sucursalId": 1,
  "clienteId": 1,
  "metodoPago": "EFECTIVO",
  "items": [{"skuInterno": "ACEITE-PRUEBA", "cantidad": 2}]
}' | grep -q "id" && echo "✅ Venta realizada con éxito"

echo -e "\n--- 🏆 PROYECTO VALIDADO ---"