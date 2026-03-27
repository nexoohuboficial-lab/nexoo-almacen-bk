# 🔗 COLECCIÓN COMPLETA DE CURLs - NexooHub Almacén

> **Sistema de Gestión de Inventarios Multi-Sucursal**  
> Versión: 1.0.0  
> Base URL: `http://localhost:8080/api/v1`  
> Última actualización: Marzo 12, 2026

---

## 📖 Índice

1. [Autenticación y Seguridad](#1-autenticación-y-seguridad)
2. [Gestión de Usuarios](#2-gestión-de-usuarios)
3. [Catálogo - Categorías](#3-catálogo---categorías)
4. [Catálogo - Clientes](#4-catálogo---clientes)
5. [Catálogo - Tipos de Cliente](#5-catálogo---tipos-de-cliente)
6. [Catálogo - Proveedores](#6-catálogo---proveedores)
7. [Catálogo - Motos](#7-catálogo---motos)
8. [Catálogo - Compatibilidad](#8-catálogo---compatibilidad)
9. [Catálogo - Precios Especiales](#9-catálogo---precios-especiales)
10. [Catálogo - Morosidad](#10-catálogo---morosidad)
11. [Inventario - Productos](#11-inventario---productos)
12. [Inventario - Gestión](#12-inventario---gestión)
13. [Inventario - Traspasos](#13-inventario---traspasos)
14. [Inventario - Caducidad](#14-inventario---caducidad)
15. [Inventario - Análisis ABC](#15-inventario---análisis-abc)
16. [Inventario - Alertas Lento Movimiento](#16-inventario---alertas-lento-movimiento)
17. [Ventas](#17-ventas)
18. [Ventas - Reservas](#18-ventas---reservas)
19. [Ventas - Devoluciones](#19-ventas---devoluciones)
20. [Compras](#20-compras)
21. [Cotizaciones](#21-cotizaciones)
22. [Sucursales](#22-sucursales)
23. [Empleados](#23-empleados)
24. [Comisiones](#24-comisiones)
25. [Finanzas - Configuración](#25-finanzas---configuración)
26. [Finanzas - Crédito](#26-finanzas---crédito)
27. [Finanzas - Dashboard](#27-finanzas---dashboard)
28. [Finanzas - Auditoría de Precios](#28-finanzas---auditoría-de-precios)
29. [Rentabilidad](#29-rentabilidad)
30. [Métricas - Financieras](#30-métricas---financieras)
31. [Métricas - Inventario](#31-métricas---inventario)
32. [Métricas - Operativas](#32-métricas---operativas)
33. [Métricas - Venta y Cliente](#33-métricas---venta-y-cliente)
34. [Programa de Fidelidad](#34-programa-de-fidelidad)
35. [Predicción de Demanda](#35-predicción-de-demanda)

---

## 🔐 Variables de Entorno

```bash
# Base URL
export BASE_URL="http://localhost:8080/api/v1"

# Credenciales por defecto (dev/test)
export ADMIN_USER="admin"
export ADMIN_PASS="admin123"

# Token JWT (se obtiene después del login)
export TOKEN="eyJhbGciOiJI..."
```

---

## 1. Autenticación y Seguridad

### 🔑 Login - Obtener Token JWT

```bash
curl -X POST "${BASE_URL}/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

**Respuesta Exitosa (200):**
```json
{
  "exitoso": true,
  "mensaje": "Autenticación exitosa",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "username": "admin",
    "role": "ROLE_ADMIN",
    "expiresIn": 86400000
  }
}
```

**Guardar Token:**
```bash
export TOKEN=$(curl -s -X POST "${BASE_URL}/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}' \
  | jq -r '.data.token')
```

---

## 2. Gestión de Usuarios

### 👤 Crear Usuario

```bash
curl -X POST "${BASE_URL}/usuarios" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "vendedor01",
    "password": "pass123",
    "role": "ROLE_USER"
  }'
```

### 📋 Listar Usuarios

```bash
curl -X GET "${BASE_URL}/usuarios" \
  -H "Authorization: Bearer ${TOKEN}"
```

### 🔍 Buscar Usuario por ID

```bash
curl -X GET "${BASE_URL}/usuarios/1" \
  -H "Authorization: Bearer ${TOKEN}"
```

### ✏️ Actualizar Usuario

```bash
curl -X PUT "${BASE_URL}/usuarios/1" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "vendedor01_updated",
    "password": "newpass123",
    "role": "ROLE_ADMIN"
  }'
```

### 🗑️ Eliminar Usuario

```bash
curl -X DELETE "${BASE_URL}/usuarios/1" \
  -H "Authorization: Bearer ${TOKEN}"
```

---

## 3. Catálogo - Categorías

### 📦 Crear Categoría

```bash
curl -X POST "${BASE_URL}/catalogo/categorias" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Aceites y Lubricantes",
    "descripcion": "Aceites para motor, transmisión y lubricantes"
  }'
```

### 📋 Listar Categorías

```bash
curl -X GET "${BASE_URL}/catalogo/categorias" \
  -H "Authorization: Bearer ${TOKEN}"
```

### 🔍 Buscar Categoría por ID

```bash
curl -X GET "${BASE_URL}/catalogo/categorias/1" \
  -H "Authorization: Bearer ${TOKEN}"
```

### ✏️ Actualizar Categoría

```bash
curl -X PUT "${BASE_URL}/catalogo/categorias/1" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Aceites Premium",
    "descripcion": "Aceites sintéticos de alta calidad"
  }'
```

### 🗑️ Eliminar Categoría

```bash
curl -X DELETE "${BASE_URL}/catalogo/categorias/1" \
  -H "Authorization: Bearer ${TOKEN}"
```

---

## 4. Catálogo - Clientes

### 👥 Crear Cliente

```bash
curl -X POST "${BASE_URL}/catalogo/clientes" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Juan Pérez García",
    "email": "juan.perez@email.com",
    "telefono": "5512345678",
    "rfc": "PEGJ850315ABC",
    "tipoClienteId": 1,
    "direccion": "Av. Insurgentes 123, CDMX"
  }'
```

### 📋 Listar Clientes (con paginación)

```bash
curl -X GET "${BASE_URL}/catalogo/clientes?page=0&size=10" \
  -H "Authorization: Bearer ${TOKEN}"
```

### 🔍 Buscar Cliente por ID

```bash
curl -X GET "${BASE_URL}/catalogo/clientes/1" \
  -H "Authorization: Bearer ${TOKEN}"
```

### 🔍 Buscar Cliente por RFC

```bash
curl -X GET "${BASE_URL}/catalogo/clientes/rfc/PEGJ850315ABC" \
  -H "Authorization: Bearer ${TOKEN}"
```

### ✏️ Actualizar Cliente

```bash
curl -X PUT "${BASE_URL}/catalogo/clientes/1" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Juan Pérez García",
    "email": "nuevo.email@email.com",
    "telefono": "5587654321",
    "rfc": "PEGJ850315ABC",
    "tipoClienteId": 2,
    "direccion": "Nueva dirección 456"
  }'
```

### 🗑️ Eliminar Cliente

```bash
curl -X DELETE "${BASE_URL}/catalogo/clientes/1" \
  -H "Authorization: Bearer ${TOKEN}"
```

---

## 5. Catálogo - Tipos de Cliente

### 🏷️ Crear Tipo de Cliente

```bash
curl -X POST "${BASE_URL}/catalogo/tipos-cliente" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Cliente VIP",
    "descuento": 15.0
  }'
```

### 📋 Listar Tipos de Cliente

```bash
curl -X GET "${BASE_URL}/catalogo/tipos-cliente" \
  -H "Authorization: Bearer ${TOKEN}"
```

### 🔍 Buscar Tipo de Cliente por ID

```bash
curl -X GET "${BASE_URL}/catalogo/tipos-cliente/1" \
  -H "Authorization: Bearer ${TOKEN}"
```

---

## 6. Catálogo - Proveedores

### 🏭 Crear Proveedor

```bash
curl -X POST "${BASE_URL}/catalogo/proveedores" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Refacciones SA de CV",
    "contacto": "Carlos Martínez",
    "telefono": "5555555555",
    "email": "ventas@refacciones.com",
    "rfc": "REF990101ABC"
  }'
```

### 📋 Listar Proveedores

```bash
curl -X GET "${BASE_URL}/catalogo/proveedores" \
  -H "Authorization: Bearer ${TOKEN}"
```

### 🔍 Buscar Proveedor por ID

```bash
curl -X GET "${BASE_URL}/catalogo/proveedores/1" \
  -H "Authorization: Bearer ${TOKEN}"
```

### ✏️ Actualizar Proveedor

```bash
curl -X PUT "${BASE_URL}/catalogo/proveedores/1" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Refacciones Premium SA",
    "contacto": "Carlos Martínez Jr",
    "telefono": "5566666666",
    "email": "contacto@refaccionespremium.com",
    "rfc": "REF990101ABC"
  }'
```

### 🗑️ Eliminar Proveedor

```bash
curl -X DELETE "${BASE_URL}/catalogo/proveedores/1" \
  -H "Authorization: Bearer ${TOKEN}"
```

---

## 7. Catálogo - Motos

### 🏍️ Crear Moto

```bash
curl -X POST "${BASE_URL}/catalogo/motos" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "marca": "Honda",
    "modelo": "CBR 600RR",
    "cilindraje": 600,
    "anioInicio": 2013,
    "anioFin": 2023
  }'
```

### 📋 Listar Motos

```bash
curl -X GET "${BASE_URL}/catalogo/motos" \
  -H "Authorization: Bearer ${TOKEN}"
```

### 🔍 Buscar Moto por ID

```bash
curl -X GET "${BASE_URL}/catalogo/motos/1" \
  -H "Authorization: Bearer ${TOKEN}"
```

### 🔍 Filtrar por Marca

```bash
curl -X GET "${BASE_URL}/catalogo/motos?marca=Honda" \
  -H "Authorization: Bearer ${TOKEN}"
```

### ✏️ Actualizar Moto

```bash
curl -X PUT "${BASE_URL}/catalogo/motos/1" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "marca": "Honda",
    "modelo": "CBR 600RR Sport",
    "cilindraje": 600,
    "anioInicio": 2013,
    "anioFin": 2024
  }'
```

---

## 8. Catálogo - Compatibilidad

### 🔗 Registrar Compatibilidad Producto-Moto

```bash
curl -X POST "${BASE_URL}/catalogo/compatibilidad" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "productoId": 1,
    "motoId": 1,
    "observaciones": "Compatible directo, sin adaptaciones"
  }'
```

### 📋 Listar Compatibilidades

```bash
curl -X GET "${BASE_URL}/catalogo/compatibilidad" \
  -H "Authorization: Bearer ${TOKEN}"
```

### 🔍 Compatibilidad por Producto

```bash
curl -X GET "${BASE_URL}/catalogo/compatibilidad/producto/1" \
  -H "Authorization: Bearer ${TOKEN}"
```

### 🔍 Compatibilidad por Moto

```bash
curl -X GET "${BASE_URL}/catalogo/compatibilidad/moto/1" \
  -H "Authorization: Bearer ${TOKEN}"
```

### 🗑️ Eliminar Compatibilidad

```bash
curl -X DELETE "${BASE_URL}/catalogo/compatibilidad/producto/1/moto/1" \
  -H "Authorization: Bearer ${TOKEN}"
```

---

## 9. Catálogo - Precios Especiales

### 💰 Crear Precio Especial

```bash
curl -X POST "${BASE_URL}/catalogo/precios-especiales" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "productoId": 1,
    "tipoClienteId": 1,
    "precioEspecial": 450.00,
    "fechaInicio": "2026-03-01",
    "fechaFin": "2026-06-30"
  }'
```

### 📋 Listar Precios Especiales

```bash
curl -X GET "${BASE_URL}/catalogo/precios-especiales" \
  -H "Authorization: Bearer ${TOKEN}"
```

### 🔍 Precios por Producto

```bash
curl -X GET "${BASE_URL}/catalogo/precios-especiales/producto/1" \
  -H "Authorization: Bearer ${TOKEN}"
```

### 🔍 Precio Vigente

```bash
curl -X GET "${BASE_URL}/catalogo/precios-especiales/producto/1/tipo-cliente/1" \
  -H "Authorization: Bearer ${TOKEN}"
```

---

## 10. Catálogo - Morosidad

### 📉 Registrar Cliente Moroso

```bash
curl -X POST "${BASE_URL}/catalogo/morosidad" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "clienteId": 1,
    "montoAdeudado": 5000.00,
    "diasVencidos": 45,
    "observaciones": "3 facturas vencidas"
  }'
```

### 📋 Listar Clientes Morosos

```bash
curl -X GET "${BASE_URL}/catalogo/morosidad" \
  -H "Authorization: Bearer ${TOKEN}"
```

### 🔍 Consultar Morosidad de Cliente

```bash
curl -X GET "${BASE_URL}/catalogo/morosidad/cliente/1" \
  -H "Authorization: Bearer ${TOKEN}"
```

### ✅ Liquidar Deuda

```bash
curl -X PUT "${BASE_URL}/catalogo/morosidad/1/liquidar" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "montoPagado": 5000.00,
    "observaciones": "Pago completo"
  }'
```

---

## 11. Inventario - Productos

### 📦 Crear Producto

```bash
curl -X POST "${BASE_URL}/productos" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "skuInterno": "ACEITE-CASTROL-10W40-1L",
    "nombre": "Aceite Castrol GTX 10W-40",
    "descripcion": "Aceite mineral para motores 4T",
    "categoriaId": 1,
    "proveedorId": 1,
    "precioCompra": 120.00,
    "precioVenta": 180.00,
    "stockMinimo": 10,
    "stockMaximo": 100,
    "unidadMedida": "LITRO",
    "ubicacionAlmacen": "PASILLO-A-ESTANTE-3",
    "fechaCaducidad": "2027-12-31",
    "lote": "LOT2026-001"
  }'
```

### 📋 Listar Productos (paginado)

```bash
curl -X GET "${BASE_URL}/productos?page=0&size=20" \
  -H "Authorization: Bearer ${TOKEN}"
```

### 🔍 Buscar Productos (omnicanal)

```bash
curl -X GET "${BASE_URL}/productos/search?q=aceite&categoriaId=1&page=0&size=10" \
  -H "Authorization: Bearer ${TOKEN}"
```

### 🔍 Buscar por SKU

```bash
curl -X GET "${BASE_URL}/productos/sku/ACEITE-CASTROL-10W40-1L" \
  -H "Authorization: Bearer ${TOKEN}"
```

### 🔍 Buscar por ID

```bash
curl -X GET "${BASE_URL}/productos/1" \
  -H "Authorization: Bearer ${TOKEN}"
```

### ✏️ Actualizar Producto

```bash
curl -X PUT "${BASE_URL}/productos/1" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "skuInterno": "ACEITE-CASTROL-10W40-1L",
    "nombre": "Aceite Castrol GTX 10W-40 Premium",
    "descripcion": "Aceite mineral de alta calidad para motores 4T",
    "categoriaId": 1,
    "proveedorId": 1,
    "precioCompra": 125.00,
    "precioVenta": 190.00,
    "stockMinimo": 15,
    "stockMaximo": 120,
    "unidadMedida": "LITRO",
    "ubicacionAlmacen": "PASILLO-A-ESTANTE-3",
    "fechaCaducidad": "2027-12-31",
    "lote": "LOT2026-002"
  }'
```

### 🗑️ Eliminar Producto

```bash
curl -X DELETE "${BASE_URL}/productos/1" \
  -H "Authorization: Bearer ${TOKEN}"
```

---

## 12. Inventario - Gestión

### 📊 Consultar Stock por Sucursal

```bash
curl -X GET "${BASE_URL}/inventario/sucursal/1/producto/1" \
  -H "Authorization: Bearer ${TOKEN}"
```

### 📥 Entrada de Inventario

```bash
curl -X POST "${BASE_URL}/inventario/entrada" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "sucursalId": 1,
    "productoId": 1,
    "cantidad": 50,
    "compraId": 1,
    "observaciones": "Entrada por compra directa"
  }'
```

### 📤 Salida de Inventario (ajuste manual)

```bash
curl -X POST "${BASE_URL}/inventario/salida" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "sucursalId": 1,
    "productoId": 1,
    "cantidad": 5,
    "motivo": "AJUSTE_INVENTARIO",
    "observaciones": "Merma por producto dañado"
  }'
```

### 📋 Historial de Movimientos

```bash
curl -X GET "${BASE_URL}/inventario/movimientos/producto/1?page=0&size=20" \
  -H "Authorization: Bearer ${TOKEN}"
```

### 🔍 Movimientos por Sucursal

```bash
curl -X GET "${BASE_URL}/inventario/movimientos/sucursal/1?fechaInicio=2026-01-01&fechaFin=2026-03-31" \
  -H "Authorization: Bearer ${TOKEN}"
```

---

## 13. Inventario - Traspasos

### 🔄 Crear Traspaso Entre Sucursales

```bash
curl -X POST "${BASE_URL}/inventario/traspasos" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "sucursalOrigenId": 1,
    "sucursalDestinoId": 2,
    "productoId": 1,
    "cantidad": 20,
    "motivo": "Reabastecimiento sucursal secundaria",
    "observaciones": "Urgente - stock bajo en sucursal 2"
  }'
```

### 📋 Listar Traspasos

```bash
curl -X GET "${BASE_URL}/inventario/traspasos?page=0&size=10" \
  -H "Authorization: Bearer ${TOKEN}"
```

### 🔍 Consultar Traspaso

```bash
curl -X GET "${BASE_URL}/inventario/traspasos/1" \
  -H "Authorization: Bearer ${TOKEN}"
```

### ✅ Confirmar Recepción de Traspaso

```bash
curl -X PUT "${BASE_URL}/inventario/traspasos/1/confirmar" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "observaciones": "Recibido en buen estado"
  }'
```

### ❌ Cancelar Traspaso

```bash
curl -X PUT "${BASE_URL}/inventario/traspasos/1/cancelar" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "motivo": "Error en cantidad solicitada"
  }'
```

---

## 14. Inventario - Caducidad

### ⚠️ Listar Productos Próximos a Caducar

```bash
curl -X GET "${BASE_URL}/inventario/caducidad/proximos?diasAnticipacion=30" \
  -H "Authorization: Bearer ${TOKEN}"
```

### 🚨 Listar Productos Caducados

```bash
curl -X GET "${BASE_URL}/inventario/caducidad/caducados" \
  -H "Authorization: Bearer ${TOKEN}"
```

### 📊 Reporte de Caducidad por Sucursal

```bash
curl -X GET "${BASE_URL}/inventario/caducidad/sucursal/1" \
  -H "Authorization: Bearer ${TOKEN}"
```

### 🗑️ Registrar Baja por Caducidad

```bash
curl -X POST "${BASE_URL}/inventario/caducidad/registrar-baja" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "sucursalId": 1,
    "productoId": 1,
    "cantidad": 5,
    "observaciones": "Producto caducado - lote LOT2024-001"
  }'
```

---

## 15. Inventario - Análisis ABC

### 📊 Generar Análisis ABC

```bash
curl -X POST "${BASE_URL}/inventario/analisis-abc/generar" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "sucursalId": 1,
    "fechaInicio": "2026-01-01",
    "fechaFin": "2026-03-31",
    "porcentajeA": 80.0,
    "porcentajeB": 15.0
  }'
```

### 📋 Listar Análisis ABC

```bash
curl -X GET "${BASE_URL}/inventario/analisis-abc?page=0&size=10" \
  -H "Authorization: Bearer ${TOKEN}"
```

### 🔍 Consultar Análisis ABC

```bash
curl -X GET "${BASE_URL}/inventario/analisis-abc/1" \
  -H "Authorization: Bearer ${TOKEN}"
```

### 📊 Productos por Clasificación

```bash
curl -X GET "${BASE_URL}/inventario/analisis-abc/1/productos/A" \
  -H "Authorization: Bearer ${TOKEN}"
```

---

## 16. Inventario - Alertas Lento Movimiento

### 🐌 Generar Alerta de Lento Movimiento

```bash
curl -X POST "${BASE_URL}/inventario/alertas-lento-movimiento/generar" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "sucursalId": 1,
    "diasSinMovimiento": 90
  }'
```

### 📋 Listar Alertas Activas

```bash
curl -X GET "${BASE_URL}/inventario/alertas-lento-movimiento?activas=true" \
  -H "Authorization: Bearer ${TOKEN}"
```

### 🔍 Alertas por Sucursal

```bash
curl -X GET "${BASE_URL}/inventario/alertas-lento-movimiento/sucursal/1" \
  -H "Authorization: Bearer ${TOKEN}"
```

### ✅ Resolver Alerta

```bash
curl -X PUT "${BASE_URL}/inventario/alertas-lento-movimiento/1/resolver" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "accionTomada": "Descuento aplicado - producto vendido",
    "observaciones": "Se liquidó el stock con promoción 30% OFF"
  }'
```

---

## 17. Ventas

### 🛒 Crear Venta

```bash
curl -X POST "${BASE_URL}/ventas" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "sucursalId": 1,
    "clienteId": 1,
    "empleadoId": 1,
    "metodoPago": "TARJETA_CREDITO",
    "detalles": [
      {
        "productoId": 1,
        "cantidad": 2,
        "precioUnitario": 180.00,
        "descuento": 0.0
      },
      {
        "productoId": 2,
        "cantidad": 1,
        "precioUnitario": 450.00,
        "descuento": 10.0
      }
    ],
    "observaciones": "Cliente preferente - aplicar puntos fidelidad"
  }'
```

### 📋 Listar Ventas

```bash
curl -X GET "${BASE_URL}/ventas?page=0&size=20" \
  -H "Authorization: Bearer ${TOKEN}"
```

### 🔍 Consultar Venta

```bash
curl -X GET "${BASE_URL}/ventas/1" \
  -H "Authorization: Bearer ${TOKEN}"
```

### 📊 Ventas por Sucursal

```bash
curl -X GET "${BASE_URL}/ventas/sucursal/1?fechaInicio=2026-03-01&fechaFin=2026-03-31" \
  -H "Authorization: Bearer ${TOKEN}"
```

### 📊 Ventas por Cliente

```bash
curl -X GET "${BASE_URL}/ventas/cliente/1?page=0&size=10" \
  -H "Authorization: Bearer ${TOKEN}"
```

### 📊 Ventas por Empleado (comisiones)

```bash
curl -X GET "${BASE_URL}/ventas/empleado/1?fechaInicio=2026-03-01&fechaFin=2026-03-31" \
  -H "Authorization: Bearer ${TOKEN}"
```

### ❌ Cancelar Venta

```bash
curl -X PUT "${BASE_URL}/ventas/1/cancelar" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "motivo": "Error en pedido - cliente solicitó cancelación"
  }'
```

---

## 18. Ventas - Reservas

### 📝 Crear Reserva

```bash
curl -X POST "${BASE_URL}/ventas/reservas" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "sucursalId": 1,
    "clienteId": 1,
    "productoId": 1,
    "cantidad": 3,
    "anticipo": 500.00,
    "diasVigencia": 7,
    "observaciones": "Cliente recogerá el viernes"
  }'
```

### 📋 Listar Reservas

```bash
curl -X GET "${BASE_URL}/ventas/reservas?page=0&size=10" \
  -H "Authorization: Bearer ${TOKEN}"
```

### 🔍 Consultar Reserva

```bash
curl -X GET "${BASE_URL}/ventas/reservas/1" \
  -H "Authorization: Bearer ${TOKEN}"
```

### ✅ Confirmar Entrega de Reserva

```bash
curl -X PUT "${BASE_URL}/ventas/reservas/1/entregar" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "ventaId": 1,
    "observaciones": "Entrega completada"
  }'
```

### ❌ Cancelar Reserva

```bash
curl -X PUT "${BASE_URL}/ventas/reservas/1/cancelar" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "motivo": "Cliente no recogió en tiempo",
    "devolucionAnticipo": true
  }'
```

---

## 19. Ventas - Devoluciones

### 🔙 Crear Devolución

```bash
curl -X POST "${BASE_URL}/ventas/devoluciones" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "ventaId": 1,
    "detalles": [
      {
        "detalleVentaId": 1,
        "cantidadDevuelta": 1,
        "motivo": "PRODUCTO_DEFECTUOSO",
        "observaciones": "Empaque dañado"
      }
    ]
  }'
```

### 📋 Listar Devoluciones

```bash
curl -X GET "${BASE_URL}/ventas/devoluciones?page=0&size=10" \
  -H "Authorization: Bearer ${TOKEN}"
```

### 🔍 Consultar Devolución

```bash
curl -X GET "${BASE_URL}/ventas/devoluciones/1" \
  -H "Authorization: Bearer ${TOKEN}"
```

### ✅ Aprobar Devolución

```bash
curl -X PUT "${BASE_URL}/ventas/devoluciones/1/aprobar" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "montoReembolsado": 180.00,
    "observaciones": "Reembolso procesado"
  }'
```

---

## 20. Compras

### 📥 Crear Orden de Compra

```bash
curl -X POST "${BASE_URL}/compras" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "proveedorId": 1,
    "sucursalId": 1,
    "detalles": [
      {
        "productoId": 1,
        "cantidad": 100,
        "precioCompra": 120.00
      },
      {
        "productoId": 2,
        "cantidad": 50,
        "precioCompra": 400.00
      }
    ],
    "observaciones": "Orden urgente - stock bajo"
  }'
```

### 📋 Listar Compras

```bash
curl -X GET "${BASE_URL}/compras?page=0&size=20" \
  -H "Authorization: Bearer ${TOKEN}"
```

### 🔍 Consultar Compra

```bash
curl -X GET "${BASE_URL}/compras/1" \
  -H "Authorization: Bearer ${TOKEN}"
```

### 📊 Compras por Proveedor

```bash
curl -X GET "${BASE_URL}/compras/proveedor/1?fechaInicio=2026-01-01&fechaFin=2026-03-31" \
  -H "Authorization: Bearer ${TOKEN}"
```

### ✅ Confirmar Recepción de Compra

```bash
curl -X PUT "${BASE_URL}/compras/1/recibir" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "observaciones": "Recibido completo y en buen estado"
  }'
```

---

## 21. Cotizaciones

### 📝 Crear Cotización

```bash
curl -X POST "${BASE_URL}/cotizaciones" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "clienteId": 1,
    "empleadoId": 1,
    "detalles": [
      {
        "productoId": 1,
        "cantidad": 10,
        "precioUnitario": 180.00,
        "descuento": 5.0
      }
    ],
    "vigenciaDias": 15,
    "observaciones": "Cotización para pedido mensual"
  }'
```

### 📋 Listar Cotizaciones

```bash
curl -X GET "${BASE_URL}/cotizaciones?page=0&size=10" \
  -H "Authorization: Bearer ${TOKEN}"
```

### 🔍 Consultar Cotización

```bash
curl -X GET "${BASE_URL}/cotizaciones/1" \
  -H "Authorization: Bearer ${TOKEN}"
```

### ✅ Convertir a Venta

```bash
curl -X POST "${BASE_URL}/cotizaciones/1/convertir-venta" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "sucursalId": 1,
    "metodoPago": "TRANSFERENCIA"
  }'
```

---

## 22. Sucursales

### 🏢 Crear Sucursal

```bash
curl -X POST "${BASE_URL}/sucursales" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Sucursal Centro",
    "direccion": "Av. Reforma 123, CDMX",
    "telefono": "5512341234",
    "email": "centro@nexoohub.com",
    "responsable": "María González"
  }'
```

### 📋 Listar Sucursales

```bash
curl -X GET "${BASE_URL}/sucursales" \
  -H "Authorization: Bearer ${TOKEN}"
```

### 🔍 Consultar Sucursal

```bash
curl -X GET "${BASE_URL}/sucursales/1" \
  -H "Authorization: Bearer ${TOKEN}"
```

### ✏️ Actualizar Sucursal

```bash
curl -X PUT "${BASE_URL}/sucursales/1" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Sucursal Centro Histórico",
    "direccion": "Av. Reforma 123, CDMX",
    "telefono": "5512345678",
    "email": "centro.historico@nexoohub.com",
    "responsable": "María González Pérez"
  }'
```

---

## 23. Empleados

### 👷 Crear Empleado

```bash
curl -X POST "${BASE_URL}/empleados" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Carlos Ramírez",
    "puesto": "Vendedor",
    "sucursalId": 1,
    "telefono": "5523456789",
    "email": "carlos.ramirez@nexoohub.com",
    "fechaIngreso": "2026-01-15",
    "salarioBase": 8000.00,
    "comisionVentas": 3.0
  }'
```

### 📋 Listar Empleados

```bash
curl -X GET "${BASE_URL}/empleados?page=0&size=20" \
  -H "Authorization: Bearer ${TOKEN}"
```

### 🔍 Consultar Empleado

```bash
curl -X GET "${BASE_URL}/empleados/1" \
  -H "Authorization: Bearer ${TOKEN}"
```

### 📊 Empleados por Sucursal

```bash
curl -X GET "${BASE_URL}/empleados/sucursal/1" \
  -H "Authorization: Bearer ${TOKEN}"
```

### ✏️ Actualizar Empleado

```bash
curl -X PUT "${BASE_URL}/empleados/1" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Carlos Ramírez López",
    "puesto": "Supervisor de Ventas",
    "sucursalId": 1,
    "telefono": "5523456789",
    "email": "carlos.ramirez@nexoohub.com",
    "fechaIngreso": "2026-01-15",
    "salarioBase": 12000.00,
    "comisionVentas": 5.0
  }'
```

---

## 24. Comisiones

### 💵 Calcular Comisiones de Periodo

```bash
curl -X POST "${BASE_URL}/comisiones/calcular" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "empleadoId": 1,
    "fechaInicio": "2026-03-01",
    "fechaFin": "2026-03-31"
  }'
```

### 📋 Listar Comisiones

```bash
curl -X GET "${BASE_URL}/comisiones?page=0&size=10" \
  -H "Authorization: Bearer ${TOKEN}"
```

### 🔍 Comisiones por Empleado

```bash
curl -X GET "${BASE_URL}/comisiones/empleado/1?periodo=2026-03" \
  -H "Authorization: Bearer ${TOKEN}"
```

### 📊 Reporte de Comisiones General

```bash
curl -X GET "${BASE_URL}/comisiones/reporte?fechaInicio=2026-01-01&fechaFin=2026-03-31" \
  -H "Authorization: Bearer ${TOKEN}"
```

### ✅ Aprobar Pago de Comisión

```bash
curl -X PUT "${BASE_URL}/comisiones/1/aprobar" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "observaciones": "Pago procesado - periodo marzo 2026"
  }'
```

---

## 25. Finanzas - Configuración

### ⚙️ Crear Configuración Financiera

```bash
curl -X POST "${BASE_URL}/finanzas/configuracion" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "margenUtilidad": 45.0,
    "descuentoMaximo": 20.0,
    "comisionVendedor": 3.0,
    "iva": 16.0,
    "diasCreditoDefault": 30,
    "montoMinimoCredito": 1000.00,
    "montoMaximoCredito": 50000.00
  }'
```

### 📋 Obtener Configuración Vigente

```bash
curl -X GET "${BASE_URL}/finanzas/configuracion/vigente" \
  -H "Authorization: Bearer ${TOKEN}"
```

### 📊 Historial de Configuraciones

```bash
curl -X GET "${BASE_URL}/finanzas/configuracion/historial" \
  -H "Authorization: Bearer ${TOKEN}"
```

### ✏️ Actualizar Configuración

```bash
curl -X PUT "${BASE_URL}/finanzas/configuracion/1" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "margenUtilidad": 50.0,
    "descuentoMaximo": 25.0,
    "comisionVendedor": 4.0,
    "iva": 16.0,
    "diasCreditoDefault": 45,
    "montoMinimoCredito": 1500.00,
    "montoMaximoCredito": 75000.00
  }'
```

---

## 26. Finanzas - Crédito

### 💳 Consultar Límite de Crédito

```bash
curl -X GET "${BASE_URL}/credito/cliente/1" \
  -H "Authorization: Bearer ${TOKEN}"
```

### 💰 Asignar/Actualizar Límite

```bash
curl -X POST "${BASE_URL}/credito/cliente/1/limite" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "limiteCredito": 50000.00,
    "diasCredito": 30,
    "observaciones": "Cliente con buen historial"
  }'
```

### 📊 Reporte de Cuentas por Cobrar

```bash
curl -X GET "${BASE_URL}/credito/cuentas-cobrar" \
  -H "Authorization: Bearer ${TOKEN}"
```

### 🔍 Saldo Actual del Cliente

```bash
curl -X GET "${BASE_URL}/credito/cliente/1/saldo" \
  -H "Authorization: Bearer ${TOKEN}"
```

### 💵 Registrar Pago

```bash
curl -X POST "${BASE_URL}/credito/cliente/1/pago" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "monto": 5000.00,
    "metodoPago": "TRANSFERENCIA",
    "referencia": "REF-20260312-001",
    "observaciones": "Pago parcial factura 123"
  }'
```

---

## 27. Finanzas - Dashboard

### 📊 Dashboard General

```bash
curl -X GET "${BASE_URL}/finanzas/dashboard" \
  -H "Authorization: Bearer ${TOKEN}"
```

### 📈 Métricas del Mes

```bash
curl -X GET "${BASE_URL}/finanzas/dashboard/mes-actual" \
  -H "Authorization: Bearer ${TOKEN}"
```

### 📊 Comparativo Periodos

```bash
curl -X GET "${BASE_URL}/finanzas/dashboard/comparativo?periodo1=2026-02&periodo2=2026-03" \
  -H "Authorization: Bearer ${TOKEN}"
```

### 📉 Indicadores Financieros

```bash
curl -X GET "${BASE_URL}/finanzas/dashboard/indicadores" \
  -H "Authorization: Bearer ${TOKEN}"
```

---

## 28. Finanzas - Auditoría de Precios

### 📝 Registrar Cambio de Precio

```bash
curl -X POST "${BASE_URL}/finanzas/auditoria-precios" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "productoId": 1,
    "precioAnterior": 180.00,
    "precioNuevo": 200.00,
    "motivo": "Aumento costo proveedor",
    "autorizadoPor": "Gerente General"
  }'
```

### 📋 Historial de Cambios

```bash
curl -X GET "${BASE_URL}/finanzas/auditoria-precios/producto/1" \
  -H "Authorization: Bearer ${TOKEN}"
```

### 📊 Reporte de Auditoría

```bash
curl -X GET "${BASE_URL}/finanzas/auditoria-precios/reporte?fechaInicio=2026-01-01&fechaFin=2026-03-31" \
  -H "Authorization: Bearer ${TOKEN}"
```

---

## 29. Rentabilidad

### 📊 Calcular Rentabilidad de Producto

```bash
curl -X GET "${BASE_URL}/rentabilidad/producto/1?fechaInicio=2026-01-01&fechaFin=2026-03-31" \
  -H "Authorization: Bearer ${TOKEN}"
```

### 📊 Rentabilidad por Categoría

```bash
curl -X GET "${BASE_URL}/rentabilidad/categoria/1?fechaInicio=2026-01-01&fechaFin=2026-03-31" \
  -H "Authorization: Bearer ${TOKEN}"
```

### 📊 Rentabilidad por Sucursal

```bash
curl -X GET "${BASE_URL}/rentabilidad/sucursal/1?fechaInicio=2026-01-01&fechaFin=2026-03-31" \
  -H "Authorization: Bearer ${TOKEN}"
```

### 📊 Top Productos Más Rentables

```bash
curl -X GET "${BASE_URL}/rentabilidad/top-productos?top=10&fechaInicio=2026-01-01&fechaFin=2026-03-31" \
  -H "Authorization: Bearer ${TOKEN}"
```

### 📊 Análisis de Márgenes

```bash
curl -X GET "${BASE_URL}/rentabilidad/analisis-margenes?fechaInicio=2026-01-01&fechaFin=2026-03-31" \
  -H "Authorization: Bearer ${TOKEN}"
```

---

## 30. Métricas - Financieras

### 📊 Obtener Métricas Financieras

```bash
curl -X GET "${BASE_URL}/metricas-financieras?fechaInicio=2026-01-01&fechaFin=2026-03-31" \
  -H "Authorization: Bearer ${TOKEN}"
```

### 📈 Resumen Financiero

```bash
curl -X GET "${BASE_URL}/metricas-financieras/resumen" \
  -H "Authorization: Bearer ${TOKEN}"
```

### 📊 Gráfica de Ingresos

```bash
curl -X GET "${BASE_URL}/metricas-financieras/grafica-ingresos?periodo=2026-03" \
  -H "Authorization: Bearer ${TOKEN}"
```

---

## 31. Métricas - Inventario

### 📊 Métricas Generales de Inventario

```bash
curl -X GET "${BASE_URL}/metricas/inventario" \
  -H "Authorization: Bearer ${TOKEN}"
```

### 📊 Rotación de Inventario

```bash
curl -X GET "${BASE_URL}/metricas/inventario/rotacion?fechaInicio=2026-01-01&fechaFin=2026-03-31" \
  -H "Authorization: Bearer ${TOKEN}"
```

### 📊 Stock Crítico

```bash
curl -X GET "${BASE_URL}/metricas/inventario/stock-critico" \
  -H "Authorization: Bearer ${TOKEN}"
```

### 📊 Valor del Inventario

```bash
curl -X GET "${BASE_URL}/metricas/inventario/valor" \
  -H "Authorization: Bearer ${TOKEN}"
```

---

## 32. Métricas - Operativas

### 📊 Métricas Operativas Generales

```bash
curl -X GET "${BASE_URL}/metricas/operativas?fechaInicio=2026-01-01&fechaFin=2026-03-31" \
  -H "Authorization: Bearer ${TOKEN}"
```

### 📊 Eficiencia Operativa

```bash
curl -X GET "${BASE_URL}/metricas/operativas/eficiencia" \
  -H "Authorization: Bearer ${TOKEN}"
```

### 📊 Balance de Transacciones

```bash
curl -X GET "${BASE_URL}/metricas/operativas/balance?periodo=2026-03" \
  -H "Authorization: Bearer ${TOKEN}"
```

---

## 33. Métricas - Venta y Cliente

### 📊 Métricas de Ventas

```bash
curl -X GET "${BASE_URL}/metricas/ventas?fechaInicio=2026-01-01&fech aFin=2026-03-31" \
  -H "Authorization: Bearer ${TOKEN}"
```

### 📊 Métricas de Clientes

```bash
curl -X GET "${BASE_URL}/metricas/clientes" \
  -H "Authorization: Bearer ${TOKEN}"
```

### 📊 Top Clientes

```bash
curl -X GET "${BASE_URL}/metricas/clientes/top?top=10&fechaInicio=2026-01-01&fechaFin=2026-03-31" \
  -H "Authorization: Bearer ${TOKEN}"
```

### 📊 Análisis de Métodos de Pago

```bash
curl -X GET "${BASE_URL}/metricas/ventas/metodos-pago?fechaInicio=2026-01-01&fechaFin=2026-03-31" \
  -H "Authorization: Bearer ${TOKEN}"
```

---

## 34. Programa de Fidelidad

### 🎁 Crear Programa

```bash
curl -X POST "${BASE_URL}/fidelidad/programa" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "clienteId": 1,
    "nivel": "BRONCE"
  }'
```

### 🔍 Consultar Programa por Cliente

```bash
curl -X GET "${BASE_URL}/fidelidad/programa/cliente/1" \
  -H "Authorization: Bearer ${TOKEN}"
```

### ➕ Acumular Puntos

```bash
curl -X POST "${BASE_URL}/fidelidad/acumular" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "clienteId": 1,
    "montoCompra": 500.00,
    "ventaId": 1,
    "descripcion": "Compra en sucursal centro"
  }'
```

### 🎁 Canjear Puntos

```bash
curl -X POST "${BASE_URL}/fidelidad/canjear" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "clienteId": 1,
    "puntosACanjear": 100,
    "ventaId": 2,
    "descripcion": "Descuento aplicado en venta"
  }'
```

### 📊 Historial de Movimientos

```bash
curl -X GET "${BASE_URL}/fidelidad/programa/cliente/1/historial" \
  -H "Authorization: Bearer ${TOKEN}"
```

### 📊 Estadísticas del Sistema

```bash
curl -X GET "${BASE_URL}/fidelidad/estadisticas" \
  -H "Authorization: Bearer ${TOKEN}"
```

---

## 35. Predicción de Demand a

### 🔮 Generar Predicción

```bash
curl -X POST "${BASE_URL}/prediccion-demanda/generar" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "productoId": 1,
    "sucursalId": 1,
    "periodos": 12,
    "algoritmo": "PROMEDIO_MOVIL"
  }'
```

### 📋 Listar Predicciones

```bash
curl -X GET "${BASE_URL}/prediccion-demanda?page=0&size=10" \
  -H "Authorization: Bearer ${TOKEN}"
```

### 🔍 Predicción por Producto

```bash
curl -X GET "${BASE_URL}/prediccion-demanda/producto/1" \
  -H "Authorization: Bearer ${TOKEN}"
```

### 📊 Recomendación de Reorden

```bash
curl -X GET "${BASE_URL}/prediccion-demanda/recomendacion/producto/1/sucursal/1" \
  -H "Authorization: Bearer ${TOKEN}"
```

### 📊 Precisión del Modelo

```bash
curl -X GET "${BASE_URL}/prediccion-demanda/precision?fechaInicio=2026-01-01&fechaFin=2026-03-31" \
  -H "Authorization: Bearer ${TOKEN}"
```

---

## 🔧 Utilidades y Scripts

### Script para Pruebas Completas

```bash
#!/bin/bash
# test-api-complete.sh

# 1. Login y obtener token
echo "=== 1. LOGIN ==="
TOKEN=$(curl -s -X POST "${BASE_URL}/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}' \
  | jq -r '.data.token')

echo "Token: ${TOKEN:0:20}..."

# 2. Crear categoría
echo -e "\n=== 2. CREAR CATEGORÍA ==="
CATEGORIA_ID=$(curl -s -X POST "${BASE_URL}/catalogo/categorias" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Test Aceites","descripcion":"Categoría de prueba"}' \
  | jq -r '.data.id')

echo "Categoría creada: ID ${CATEGORIA_ID}"

# 3. Crear proveedor
echo -e "\n=== 3. CREAR PROVEEDOR ==="
PROVEEDOR_ID=$(curl -s -X POST "${BASE_URL}/catalogo/proveedores" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Test Proveedor","contacto":"Juan Test","telefono":"5555555555","email":"test@test.com","rfc":"TEST990101ABC"}' \
  | jq -r '.data.id')

echo "Proveedor creado: ID ${PROVEEDOR_ID}"

# 4. Crear producto
echo -e "\n=== 4. CREAR PRODUCTO ==="
PRODUCTO_ID=$(curl -s -X POST "${BASE_URL}/productos" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d "{\"skuInterno\":\"TEST-001\",\"nombre\":\"Producto Test\",\"categoriaId\":${CATEGORIA_ID},\"proveedorId\":${PROVEEDOR_ID},\"precioCompra\":100.00,\"precioVenta\":150.00,\"stockMinimo\":10,\"stockMaximo\":100,\"unidadMedida\":\"UNIDAD\"}" \
  | jq -r '.data.id')

echo "Producto creado: ID ${PRODUCTO_ID}"

# 5. Listar productos
echo -e "\n=== 5. LISTAR PRODUCTOS ==="
curl -s -X GET "${BASE_URL}/productos?page=0&size=5" \
  -H "Authorization: Bearer ${TOKEN}" \
  | jq '.data.content[] | {id, nombre, precioVenta}'

echo -e "\n✅ Test completado exitosamente"
```

### Limpiar Datos de Prueba

```bash
#!/bin/bash
# clean-test-data.sh

TOKEN=$(curl -s -X POST "${BASE_URL}/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}' \
  | jq -r '.data.token')

# Eliminar recursos de prueba (ajustar IDs según necesidad)
echo "Limpiando datos de prueba..."

curl -X DELETE "${BASE_URL}/productos/999" -H "Authorization: Bearer ${TOKEN}"
curl -X DELETE "${BASE_URL}/catalogo/categorias/999" -H "Authorization: Bearer ${TOKEN}"
curl -X DELETE "${BASE_URL}/catalogo/proveedores/999" -H "Authorization: Bearer ${TOKEN}"

echo "✅ Limpieza completada"
```

---

## 📝 Notas Importantes

### Autenticación
- Todos los endpoints (excepto `/auth/login`) requieren token JWT
- El token expira en 24 horas por defecto
- Header requerido: `Authorization: Bearer {TOKEN}`

### Códigos de Respuesta HTTP
- **200 OK**: Operación exitosa
- **201 Created**: Recurso creado exitosamente
- **400 Bad Request**: Datos inválidos o faltantes
- **401 Unauthorized**: Token inválido o expirado
- **403 Forbidden**: Sin permisos para la operación
- **404 Not Found**: Recurso no encontrado
- **409 Conflict**: Conflicto (ej. duplicado, stock insuficiente)
- **500 Internal Server Error**: Error del servidor

### Formato de Respuesta Estándar

```json
{
  "exitoso": true,
  "mensaje": "Operación exitosa",
  "data": { },
  "traceId": "abc123...",
  "timestamp": "2026-03-12T10:30:00"
}
```

### Paginación
- Parámetros: `?page=0&size=20`
- Por defecto: `page=0, size=10`
- Máximo: `size=100`

---

## 📚 Documentación Adicional

- **API Docs Interactiva**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs
- **Repositorio GitHub**: https://github.com/nexoohub/almacen
- **Wiki**: https://github.com/nexoohub/almacen/wiki

---

**© 2026 NexooHub Development Team**

---

## Módulos de Proveedores y Compras (SUP)

### Comparación de Precios de Proveedores (SUP-01)
``bash
curl -X GET 'http://localhost:8080/api/v1/adquisiciones/comparador?skuInterno=MOT-001&sucursalId=1' \
  -H 'Authorization: Bearer <TOKEN>'
``

### Actualización Masiva de Precios (SUP-02)
``bash
curl -X PATCH 'http://localhost:8080/api/v1/adquisiciones/precios/masivo/excel' \
  -H 'Authorization: Bearer <TOKEN>' \
  -H 'Content-Type: multipart/form-data' \
  -F 'file=@"/ruta/al/catalogo_proveedor.xlsx"'
``

### Carrito de Compras de Proveedor y Generar OC Excel (SUP-03)
``bash
curl -X POST 'http://localhost:8080/api/v1/adquisiciones/carrito/agregar' \
  -H 'Authorization: Bearer <TOKEN>' \
  -H 'Content-Type: application/json' \
  -d '{
    "catalogoId": 5,
    "cantidad": 100
  }'

curl -X POST 'http://localhost:8080/api/v1/adquisiciones/ordenes-compra/generar?sucursalId=1&proveedorId=2' \
  -H 'Authorization: Bearer <TOKEN>'
``

## Módulos RH y Seguridad (PRO)

### Asignar Regla de Comisión y Metas (PRO-02)
``bash
curl -X POST 'http://localhost:8080/api/v1/rh/metas' \
  -H 'Authorization: Bearer <TOKEN>' \
  -H 'Content-Type: application/json' \
  -d '{
    "empleadoId": 12,
    "mes": 3,
    "anio": 2026,
    "metaMonto": 50000.00
  }'
``

### Roles de Seguridad RBAC - Crear Rol (PRO-03)
``bash
curl -X POST 'http://localhost:8080/api/v1/admin/roles' \
  -H 'Authorization: Bearer <TOKEN>' \
  -H 'Content-Type: application/json' \
  -d '{
    "nombre": "GERENTE_VENTAS",
    "descripcion": "Acceso a indicadores",
    "permisos": ["LEER_VENTAS", "CREAR_VENTAS", "APROBAR_DEVOLUCION"]
  }'
``

## Módulo Analítico y Rendimiento (ANA-01)
```bash
curl -X GET 'http://localhost:8080/api/v1/rh/metricas/ranking?fechaInicio=2026-03-01' \
  -H 'Authorization: Bearer <TOKEN>'
```

## Módulo de Centro de Notificaciones y Alertas (PRO-01)

### Suscribirse al Canal de Alertas (SSE / WebSockets)
```bash
curl -N -H "Accept: text/event-stream" \
  -H "Authorization: Bearer <TOKEN>" \
  http://localhost:8080/api/v1/alertas/stream/SSE
```

### Consultar Alertas No Leídas del Empleado
```bash
curl -X GET 'http://localhost:8080/api/v1/alertas/mis-alertas/1' \
  -H 'Authorization: Bearer <TOKEN>'
```

### Marcar Alerta como Leída
```bash
curl -X PUT 'http://localhost:8080/api/v1/alertas/1/leida' \
  -H 'Authorization: Bearer <TOKEN>'
```

