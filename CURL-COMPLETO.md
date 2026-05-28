# Colección Completa de CURLs — NexooHub Almacén

> **Rutas verificadas contra el código fuente**  
> Base URL: `http://localhost:8080`  
> Actualización: Mayo 2026

---

## Índice

1. [Autenticación y Usuarios](#1-autenticación-y-usuarios)
2. [Catálogo — Categorías](#2-catálogo--categorías)
3. [Catálogo — Clientes y Morosidad](#3-catálogo--clientes-y-morosidad)
4. [Catálogo — Tipos de Cliente](#4-catálogo--tipos-de-cliente)
5. [Catálogo — Proveedores](#5-catálogo--proveedores)
6. [Catálogo — Motos](#6-catálogo--motos)
7. [Catálogo — Compatibilidad](#7-catálogo--compatibilidad)
8. [Catálogo — Precios Especiales](#8-catálogo--precios-especiales)
9. [Inventario — Productos](#9-inventario--productos)
10. [Inventario — Stock y Movimientos](#10-inventario--stock-y-movimientos)
11. [Inventario — Códigos de Barras](#11-inventario--códigos-de-barras)
12. [Inventario — Traspasos](#12-inventario--traspasos)
13. [Inventario — Caducidad](#13-inventario--caducidad)
14. [Inventario — Análisis ABC](#14-inventario--análisis-abc)
15. [Inventario — Alertas de Lento Movimiento](#15-inventario--alertas-de-lento-movimiento)
16. [Ventas](#16-ventas)
17. [Ventas — Reservas](#17-ventas--reservas)
18. [Ventas — Devoluciones](#18-ventas--devoluciones)
19. [Cotizaciones](#19-cotizaciones)
20. [Compras](#20-compras)
21. [Adquisiciones — Comparador](#21-adquisiciones--comparador)
22. [Adquisiciones — Actualización de Precios](#22-adquisiciones--actualización-de-precios)
23. [Adquisiciones — Órdenes de Compra](#23-adquisiciones--órdenes-de-compra)
24. [Caja](#24-caja)
25. [POS — Facturación CFDI](#25-pos--facturación-cfdi)
26. [POS — Terminal Bancaria](#26-pos--terminal-bancaria)
27. [POS — Sincronización Offline](#27-pos--sincronización-offline)
28. [Sucursales](#28-sucursales)
29. [Empleados](#29-empleados)
30. [Comisiones y Reglas](#30-comisiones-y-reglas)
31. [Metas de Ventas (RH)](#31-metas-de-ventas-rh)
32. [Finanzas — Dashboard](#32-finanzas--dashboard)
33. [Finanzas — Configuración](#33-finanzas--configuración)
34. [Finanzas — Auditoría de Precios](#34-finanzas--auditoría-de-precios)
35. [Crédito y Cobranza](#35-crédito-y-cobranza)
36. [ERP — CxP y Gastos](#36-erp--cxp-y-gastos)
37. [ERP — Contabilidad](#37-erp--contabilidad)
38. [ERP — Logística](#38-erp--logística)
39. [ERP — Nómina](#39-erp--nómina)
40. [ERP — Devoluciones a Proveedor](#40-erp--devoluciones-a-proveedor)
41. [CRM — Pipeline B2B](#41-crm--pipeline-b2b)
42. [CRM — Garantías](#42-crm--garantías)
43. [CRM — NPS](#43-crm--nps)
44. [CRM — Marketing](#44-crm--marketing)
45. [Métricas Financieras](#45-métricas-financieras)
46. [Métricas de Inventario](#46-métricas-de-inventario)
47. [Métricas Operativas](#47-métricas-operativas)
48. [Métricas Ventas y Clientes](#48-métricas-ventas-y-clientes)
49. [Rentabilidad](#49-rentabilidad)
50. [Analítica — RFM](#50-analítica--rfm)
51. [Analítica — Churn](#51-analítica--churn)
52. [Analítica — Market Basket](#52-analítica--market-basket)
53. [Analítica — Rendimiento Personal](#53-analítica--rendimiento-personal)
54. [Fidelidad](#54-fidelidad)
55. [Predicción de Demanda](#55-predicción-de-demanda)
56. [Alertas del Sistema](#56-alertas-del-sistema)

---

## Variables de Entorno

```bash
export BASE="http://localhost:8080"
export TOKEN=""  # Se llena después del login

# Login y guardar token automáticamente
export TOKEN=$(curl -s -X POST "$BASE/api/v1/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}' \
  | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

echo "Token: ${TOKEN:0:30}..."
```

---

## 1. Autenticación y Usuarios

### Login

```bash
curl -X POST "$BASE/api/v1/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

### Listar Usuarios

```bash
curl -X GET "$BASE/api/v1/usuarios" \
  -H "Authorization: Bearer $TOKEN"
```

### Obtener Usuario por ID

```bash
curl -X GET "$BASE/api/v1/usuarios/1" \
  -H "Authorization: Bearer $TOKEN"
```

### Crear Usuario

```bash
curl -X POST "$BASE/api/v1/usuarios" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "vendedor01",
    "password": "pass123",
    "nombre": "Ana López",
    "email": "ana@nexoohub.com"
  }'
```

### Actualizar Usuario

```bash
curl -X PUT "$BASE/api/v1/usuarios/2" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Ana López García",
    "email": "ana.garcia@nexoohub.com"
  }'
```

### Cambiar Contraseña

```bash
curl -X PUT "$BASE/api/v1/usuarios/2/password" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"passwordActual":"pass123","passwordNuevo":"nuevaPass456"}'
```

### Eliminar Usuario

```bash
curl -X DELETE "$BASE/api/v1/usuarios/2" \
  -H "Authorization: Bearer $TOKEN"
```

### Crear Rol

```bash
curl -X POST "$BASE/api/v1/admin/roles" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "GERENTE_VENTAS",
    "descripcion": "Acceso a indicadores y aprobaciones",
    "permisos": ["LEER_VENTAS","CREAR_VENTAS","APROBAR_DEVOLUCION"]
  }'
```

### Asignar Rol a Usuario

```bash
curl -X POST "$BASE/api/v1/admin/usuarios/2/roles" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"rolNombre":"GERENTE_VENTAS"}'
```

### Ver Permisos de Usuario

```bash
curl -X GET "$BASE/api/v1/admin/usuarios/1/permisos" \
  -H "Authorization: Bearer $TOKEN"
```

---

## 2. Catálogo — Categorías

> **Ruta base:** `/api/v1/categorias`

### Listar Categorías

```bash
curl -X GET "$BASE/api/v1/categorias" \
  -H "Authorization: Bearer $TOKEN"
```

### Crear Categoría

```bash
curl -X POST "$BASE/api/v1/categorias" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Aceites y Lubricantes","descripcion":"Aceites motor 4T, transmisión y lubricantes"}'
```

### Actualizar Categoría

```bash
curl -X PUT "$BASE/api/v1/categorias/1" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Aceites Premium","descripcion":"Aceites sintéticos de alta calidad"}'
```

---

## 3. Catálogo — Clientes y Morosidad

> **Ruta base:** `/api/v1/clientes`

### Listar Clientes

```bash
curl -X GET "$BASE/api/v1/clientes?page=0&size=20" \
  -H "Authorization: Bearer $TOKEN"
```

### Crear Cliente

```bash
curl -X POST "$BASE/api/v1/clientes" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Juan Pérez García",
    "email": "juan@email.com",
    "telefono": "5512345678",
    "rfc": "PEGJ850315ABC",
    "tipoClienteId": 1,
    "direccion": "Av. Insurgentes 123, CDMX"
  }'
```

### Actualizar Cliente

```bash
curl -X PUT "$BASE/api/v1/clientes/1" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Juan Pérez García",
    "email": "nuevo@email.com",
    "telefono": "5587654321",
    "rfc": "PEGJ850315ABC",
    "tipoClienteId": 2,
    "direccion": "Nueva dirección 456"
  }'
```

### Listar Clientes Bloqueados

```bash
curl -X GET "$BASE/api/v1/clientes/bloqueados" \
  -H "Authorization: Bearer $TOKEN"
```

### Listar Clientes Morosos

```bash
curl -X GET "$BASE/api/v1/clientes/morosos" \
  -H "Authorization: Bearer $TOKEN"
```

### Bloquear Cliente

```bash
curl -X POST "$BASE/api/v1/clientes/1/bloquear?motivo=Facturas+vencidas+60+dias" \
  -H "Authorization: Bearer $TOKEN"
```

### Desbloquear Cliente

```bash
curl -X POST "$BASE/api/v1/clientes/1/desbloquear" \
  -H "Authorization: Bearer $TOKEN"
```

### Registrar Pago (desbloquea automáticamente si es suficiente)

```bash
curl -X POST "$BASE/api/v1/clientes/1/registrar-pago?monto=5000.00" \
  -H "Authorization: Bearer $TOKEN"
```

---

## 4. Catálogo — Tipos de Cliente

```bash
curl -X GET "$BASE/api/v1/tipos-cliente" \
  -H "Authorization: Bearer $TOKEN"
```

---

## 5. Catálogo — Proveedores

> **Ruta base:** `/api/v1/proveedores`

### Listar Proveedores

```bash
curl -X GET "$BASE/api/v1/proveedores" \
  -H "Authorization: Bearer $TOKEN"
```

### Crear Proveedor

```bash
curl -X POST "$BASE/api/v1/proveedores" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Refacciones SA de CV",
    "contacto": "Carlos Martínez",
    "telefono": "5555555555",
    "email": "ventas@refacciones.com",
    "rfc": "REF990101ABC"
  }'
```

### Actualizar Proveedor

```bash
curl -X PUT "$BASE/api/v1/proveedores/1" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Refacciones Premium SA",
    "contacto": "Carlos Martínez Jr",
    "telefono": "5566666666",
    "email": "contacto@refaccionespremium.com",
    "rfc": "REF990101ABC"
  }'
```

---

## 6. Catálogo — Motos

> **Ruta base:** `/api/v1/motos`

### Listar Motos

```bash
curl -X GET "$BASE/api/v1/motos" \
  -H "Authorization: Bearer $TOKEN"
```

### Crear Moto

```bash
curl -X POST "$BASE/api/v1/motos" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "marca": "Honda",
    "modelo": "CBR 600RR",
    "cilindraje": 600,
    "anioInicio": 2013,
    "anioFin": 2024
  }'
```

### Actualizar Moto

```bash
curl -X PUT "$BASE/api/v1/motos/1" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "marca": "Honda",
    "modelo": "CBR 600RR Sport Edition",
    "cilindraje": 600,
    "anioInicio": 2013,
    "anioFin": 2025
  }'
```

---

## 7. Catálogo — Compatibilidad

> **Ruta base:** `/api/v1/compatibilidad`

### Compatibilidad por Producto (SKU)

```bash
curl -X GET "$BASE/api/v1/compatibilidad/producto/ACEITE-CASTROL-10W40-1L" \
  -H "Authorization: Bearer $TOKEN"
```

### Compatibilidad por Moto

```bash
curl -X GET "$BASE/api/v1/compatibilidad/moto/1" \
  -H "Authorization: Bearer $TOKEN"
```

### Registrar Compatibilidad

```bash
curl -X POST "$BASE/api/v1/compatibilidad" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "skuInterno": "ACEITE-CASTROL-10W40-1L",
    "motoId": 1,
    "observaciones": "Compatible directo, sin adaptaciones"
  }'
```

---

## 8. Catálogo — Precios Especiales

> **Ruta base:** `/api/v1/precios-especiales`  
> ⚠️ Solo acepta POST y DELETE. El método GET no está soportado.

### Crear Precio Especial

```bash
curl -X POST "$BASE/api/v1/precios-especiales" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "skuInterno": "ACEITE-CASTROL-10W40-1L",
    "tipoClienteId": 1,
    "precioEspecial": 450.00,
    "fechaInicio": "2026-06-01",
    "fechaFin": "2026-12-31"
  }'
```

### Eliminar Precio Especial

```bash
curl -X DELETE "$BASE/api/v1/precios-especiales/1" \
  -H "Authorization: Bearer $TOKEN"
```

---

## 9. Inventario — Productos

> **Ruta base:** `/api/v1/productos`  
> ⚠️ No existe `GET /api/v1/productos`. Usa `/search` o `/{sku}` para consultar.

### Crear Producto

```bash
curl -X POST "$BASE/api/v1/productos" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "skuInterno": "ACEITE-CASTROL-10W40-1L",
    "nombreComercial": "Aceite Castrol GTX 10W-40",
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

### Buscar Productos (texto libre + filtros opcionales)

```bash
curl -X GET "$BASE/api/v1/productos/search?q=aceite&categoriaId=1" \
  -H "Authorization: Bearer $TOKEN"
```

### Obtener Producto por SKU

```bash
curl -X GET "$BASE/api/v1/productos/ACEITE-CASTROL-10W40-1L" \
  -H "Authorization: Bearer $TOKEN"
```

### Actualizar Producto

```bash
curl -X PUT "$BASE/api/v1/productos/ACEITE-CASTROL-10W40-1L" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "nombreComercial": "Aceite Castrol GTX 10W-40 Premium",
    "precioCompra": 125.00,
    "precioVenta": 195.00,
    "stockMinimo": 15,
    "stockMaximo": 120
  }'
```

### Eliminar Producto

```bash
curl -X DELETE "$BASE/api/v1/productos/ACEITE-CASTROL-10W40-1L" \
  -H "Authorization: Bearer $TOKEN"
```

### Productos para Mostrador (POS)

```bash
curl -X GET "$BASE/api/v1/productos/mostrador" \
  -H "Authorization: Bearer $TOKEN"
```

---

## 10. Inventario — Stock y Movimientos

> **Ruta base:** `/api/v1/inventario`

### Inicializar/Ajustar Stock en Sucursal

```bash
curl -X POST "$BASE/api/v1/inventario" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "sucursalId": 1,
    "skuInterno": "ACEITE-CASTROL-10W40-1L",
    "stockActual": 50,
    "costoPromedio": 120.00
  }'
```

### Consultar Stock de una Sucursal

```bash
curl -X GET "$BASE/api/v1/inventario/sucursales/1" \
  -H "Authorization: Bearer $TOKEN"
```

### Alertas Globales de Stock Bajo

```bash
curl -X GET "$BASE/api/v1/inventario/alertas/stock-bajo" \
  -H "Authorization: Bearer $TOKEN"
```

### Alertas de Stock Bajo por Sucursal

```bash
curl -X GET "$BASE/api/v1/inventario/alertas/stock-bajo/sucursales/1" \
  -H "Authorization: Bearer $TOKEN"
```

---

## 11. Inventario — Códigos de Barras

### Buscar Producto por Código de Barras

```bash
curl -X GET "$BASE/api/v1/inventario/productos/buscar-por-codigo?codigo=7501234567890" \
  -H "Authorization: Bearer $TOKEN"
```

### Escaneo Rápido

```bash
curl -X POST "$BASE/api/v1/inventario/escaneo" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"codigoBarras":"7501234567890","sucursalId":1}'
```

### Agregar Código de Barras a Producto

```bash
curl -X POST "$BASE/api/v1/inventario/productos/ACEITE-CASTROL-10W40-1L/codigos-barras" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"codigo":"7501234567890","descripcion":"EAN-13 principal"}'
```

### Listar Códigos de Barras de un Producto

```bash
curl -X GET "$BASE/api/v1/inventario/productos/ACEITE-CASTROL-10W40-1L/codigos-barras" \
  -H "Authorization: Bearer $TOKEN"
```

### Eliminar Código de Barras

```bash
curl -X DELETE "$BASE/api/v1/inventario/codigos-barras/1" \
  -H "Authorization: Bearer $TOKEN"
```

---

## 12. Inventario — Traspasos

### Crear Traspaso entre Sucursales

```bash
curl -X POST "$BASE/api/v1/inventario/traspasos" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "sucursalOrigenId": 1,
    "sucursalDestinoId": 2,
    "skuInterno": "ACEITE-CASTROL-10W40-1L",
    "cantidad": 20,
    "motivo": "Reabastecimiento sucursal secundaria",
    "observaciones": "Urgente - stock bajo en sucursal 2"
  }'
```

---

## 13. Inventario — Caducidad

### Productos Próximos a Caducar

```bash
curl -X GET "$BASE/api/v1/inventario/caducidad/proximos" \
  -H "Authorization: Bearer $TOKEN"
```

### Productos Ya Vencidos

```bash
curl -X GET "$BASE/api/v1/inventario/caducidad/vencidos" \
  -H "Authorization: Bearer $TOKEN"
```

---

## 14. Inventario — Análisis ABC

### Generar Análisis ABC

```bash
curl -X POST "$BASE/api/v1/inventario/analisis-abc/generar" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "sucursalId": 1,
    "fechaInicio": "2026-01-01",
    "fechaFin": "2026-05-31"
  }'
```

### Último Análisis de una Sucursal

```bash
curl -X GET "$BASE/api/v1/inventario/analisis-abc/sucursal/1/ultimo" \
  -H "Authorization: Bearer $TOKEN"
```

### Productos de Clase A

```bash
curl -X GET "$BASE/api/v1/inventario/analisis-abc/sucursal/1/clasificacion/A" \
  -H "Authorization: Bearer $TOKEN"
```

### Resumen ABC

```bash
curl -X GET "$BASE/api/v1/inventario/analisis-abc/sucursal/1/resumen" \
  -H "Authorization: Bearer $TOKEN"
```

---

## 15. Inventario — Alertas de Lento Movimiento

> **Ruta base:** `/api/alertas/lento-movimiento`

### Listar Alertas Activas

```bash
curl -X GET "$BASE/api/alertas/lento-movimiento" \
  -H "Authorization: Bearer $TOKEN"
```

### Generar Alertas de Lento Movimiento

```bash
curl -X POST "$BASE/api/alertas/lento-movimiento/generar" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"sucursalId":1,"diasSinMovimiento":90}'
```

### Alertas por Sucursal

```bash
curl -X GET "$BASE/api/alertas/lento-movimiento/sucursal/1" \
  -H "Authorization: Bearer $TOKEN"
```

### Alertas Críticas

```bash
curl -X GET "$BASE/api/alertas/lento-movimiento/criticas" \
  -H "Authorization: Bearer $TOKEN"
```

### Costo Total Inmovilizado

```bash
curl -X GET "$BASE/api/alertas/lento-movimiento/costo-inmovilizado" \
  -H "Authorization: Bearer $TOKEN"
```

### Resolver Alerta

```bash
curl -X PUT "$BASE/api/alertas/lento-movimiento/1/resolver" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"accionTomada":"Descuento 30% aplicado — stock liquidado","observaciones":"Promociónde fin de temporada"}'
```

---

## 16. Ventas

### Registrar Venta Directa

```bash
curl -X POST "$BASE/api/v1/ventas" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "sucursalId": 1,
    "clienteId": 1,
    "empleadoId": 1,
    "metodoPago": "EFECTIVO",
    "detalles": [
      {
        "skuInterno": "ACEITE-CASTROL-10W40-1L",
        "cantidad": 2,
        "precioUnitario": 180.00,
        "descuento": 0.0
      }
    ],
    "observaciones": "Venta mostrador"
  }'
```

---

## 17. Ventas — Reservas

> **Ruta base:** `/api/v1/reservas`

### Crear Reserva / Apartado

```bash
curl -X POST "$BASE/api/v1/reservas" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "sucursalId": 1,
    "clienteId": 1,
    "skuInterno": "ACEITE-CASTROL-10W40-1L",
    "cantidad": 3,
    "anticipo": 200.00,
    "diasVigencia": 7,
    "observaciones": "Cliente pasa a recoger el viernes"
  }'
```

### Listar Reservas

```bash
curl -X GET "$BASE/api/v1/reservas" \
  -H "Authorization: Bearer $TOKEN"
```

### Consultar Reserva

```bash
curl -X GET "$BASE/api/v1/reservas/1" \
  -H "Authorization: Bearer $TOKEN"
```

### Reservas de un Cliente

```bash
curl -X GET "$BASE/api/v1/reservas/cliente/1" \
  -H "Authorization: Bearer $TOKEN"
```

### Reservas por Estado

```bash
curl -X GET "$BASE/api/v1/reservas/estado/PENDIENTE" \
  -H "Authorization: Bearer $TOKEN"
```

### Próximas a Vencer

```bash
curl -X GET "$BASE/api/v1/reservas/proximas-vencer" \
  -H "Authorization: Bearer $TOKEN"
```

### Completar / Entregar Reserva

```bash
curl -X PUT "$BASE/api/v1/reservas/1/completar" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"observaciones":"Entregado en sucursal centro"}'
```

### Cancelar Reserva

```bash
curl -X PUT "$BASE/api/v1/reservas/1/cancelar" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"motivo":"Cliente no recogió en tiempo"}'
```

---

## 18. Ventas — Devoluciones

> **Ruta base:** `/api/v1/devoluciones`

### Registrar Devolución

```bash
curl -X POST "$BASE/api/v1/devoluciones" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "ventaId": 1,
    "motivo": "Producto defectuoso",
    "detalles": [
      {
        "detalleVentaId": 1,
        "cantidadDevuelta": 1,
        "observaciones": "Empaque dañado al momento de abrir"
      }
    ]
  }'
```

### Consultar Devolución

```bash
curl -X GET "$BASE/api/v1/devoluciones/1" \
  -H "Authorization: Bearer $TOKEN"
```

### Devoluciones de una Venta

```bash
curl -X GET "$BASE/api/v1/devoluciones/venta/1" \
  -H "Authorization: Bearer $TOKEN"
```

---

## 19. Cotizaciones

> **Ruta base:** `/api/cotizaciones` *(sin `/v1/`)*

### Crear Cotización

```bash
curl -X POST "$BASE/api/cotizaciones" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "clienteId": 1,
    "empleadoId": 1,
    "detalles": [
      {
        "skuInterno": "ACEITE-CASTROL-10W40-1L",
        "cantidad": 10,
        "precioUnitario": 180.00,
        "descuento": 5.0
      }
    ],
    "vigenciaDias": 15,
    "observaciones": "Cotización para pedido mensual"
  }'
```

### Listar Cotizaciones

```bash
curl -X GET "$BASE/api/cotizaciones" \
  -H "Authorization: Bearer $TOKEN"
```

### Consultar Cotización

```bash
curl -X GET "$BASE/api/cotizaciones/1" \
  -H "Authorization: Bearer $TOKEN"
```

### Buscar por Folio

```bash
curl -X GET "$BASE/api/cotizaciones/folio/COT-2026-0001" \
  -H "Authorization: Bearer $TOKEN"
```

### Convertir a Venta

```bash
curl -X POST "$BASE/api/cotizaciones/1/convertir-venta" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"sucursalId":1,"metodoPago":"TRANSFERENCIA"}'
```

### Cotizaciones Próximas a Vencer

```bash
curl -X GET "$BASE/api/cotizaciones/vencimiento/proximas" \
  -H "Authorization: Bearer $TOKEN"
```

### Estadísticas de Cotizaciones

```bash
curl -X GET "$BASE/api/cotizaciones/estadisticas" \
  -H "Authorization: Bearer $TOKEN"
```

### Marcar Vencidas (Batch)

```bash
curl -X POST "$BASE/api/cotizaciones/marcar-vencidas" \
  -H "Authorization: Bearer $TOKEN"
```

---

## 20. Compras

### Registrar Ingreso de Mercancía

```bash
curl -X POST "$BASE/api/v1/compras/ingreso" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "proveedorId": 1,
    "sucursalId": 1,
    "esCredito": true,
    "diasCredito": 30,
    "detalles": [
      {
        "skuInterno": "ACEITE-CASTROL-10W40-1L",
        "cantidad": 100,
        "precioUnitario": 120.00
      }
    ],
    "observaciones": "Pedido mensual regular"
  }'
```

---

## 21. Adquisiciones — Comparador

> **Ruta base:** `/api/sup/comparador`

### Comparar Precios de Proveedores para un Producto

```bash
curl -X GET "$BASE/api/sup/comparador/producto/ACEITE-CASTROL-10W40-1L" \
  -H "Authorization: Bearer $TOKEN"
```

---

## 22. Adquisiciones — Actualización de Precios

> **Ruta base:** `/api/v1/comparador/catalogo`

### Actualizar Precio de un Ítem del Catálogo

```bash
curl -X PUT "$BASE/api/v1/comparador/catalogo/5/precio" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"nuevoPrecio":118.50,"motivo":"Actualización lista dic-2026"}'
```

### Actualización Masiva de Precios

```bash
curl -X POST "$BASE/api/v1/comparador/catalogo/actualizar-masivo" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '[
    {"skuInterno":"ACEITE-CASTROL-10W40-1L","nuevoPrecio":118.50},
    {"skuInterno":"FRENO-DISCO-CBR600-F","nuevoPrecio":350.00}
  ]'
```

### Historial de Precios de un Ítem

```bash
curl -X GET "$BASE/api/v1/comparador/catalogo/5/historial" \
  -H "Authorization: Bearer $TOKEN"
```

---

## 23. Adquisiciones — Órdenes de Compra

> **Ruta base:** `/api/v1/oc`

### Listar Órdenes de Compra

```bash
curl -X GET "$BASE/api/v1/oc" \
  -H "Authorization: Bearer $TOKEN"
```

### Agregar Producto al Carrito

```bash
curl -X POST "$BASE/api/v1/oc/carrito/agregar" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"catalogoId":5,"cantidad":100}'
```

### Ver Carrito Actual

```bash
curl -X GET "$BASE/api/v1/oc/carrito" \
  -H "Authorization: Bearer $TOKEN"
```

### Quitar Producto del Carrito

```bash
curl -X DELETE "$BASE/api/v1/oc/carrito/5" \
  -H "Authorization: Bearer $TOKEN"
```

### Generar Orden de Compra desde el Carrito

```bash
curl -X POST "$BASE/api/v1/oc/generar" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"sucursalId":1,"proveedorId":1}'
```

### Cambiar Estado de OC

```bash
curl -X PATCH "$BASE/api/v1/oc/1/estado" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"estado":"ENVIADA"}'
```

### Confirmar Recepción de OC

```bash
curl -X POST "$BASE/api/v1/oc/1/recibir" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"observaciones":"Recibido completo y en buen estado"}'
```

### Exportar OC a Excel

```bash
curl -X GET "$BASE/api/v1/oc/1/exportar-excel" \
  -H "Authorization: Bearer $TOKEN" \
  --output "orden-compra-1.xlsx"
```

---

## 24. Caja

> **Ruta base:** `/api/v1/cajas`

### Abrir Turno de Caja

```bash
curl -X POST "$BASE/api/v1/cajas/abrir" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"sucursalId":1,"empleadoId":1,"fondoInicial":1000.00}'
```

### Registrar Movimiento de Caja

```bash
curl -X POST "$BASE/api/v1/cajas/movimientos" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "turnoCajaId": 1,
    "tipo": "ENTRADA",
    "monto": 500.00,
    "concepto": "Fondo adicional",
    "observaciones": "Refuerzo aprobado por gerente"
  }'
```

### Cerrar Turno (Arqueo Z)

```bash
curl -X POST "$BASE/api/v1/cajas/1/cerrar" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"efectivoContado":8750.00,"observaciones":"Sin diferencias"}'
```

### Resumen del Turno

```bash
curl -X GET "$BASE/api/v1/cajas/1/resumen" \
  -H "Authorization: Bearer $TOKEN"
```

---

## 25. POS — Facturación CFDI

> **Ruta base:** `/api/v1/facturacion`

### Timbrar CFDI

```bash
curl -X POST "$BASE/api/v1/facturacion/timbrar" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "ventaId": 1,
    "rfcReceptor": "PEGJ850315ABC",
    "usoCfdi": "G01",
    "metodoPago": "PUE",
    "formaPago": "01"
  }'
```

### Cancelar CFDI

```bash
curl -X POST "$BASE/api/v1/facturacion/1/cancelar" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"motivo":"01","folioSustitucion":""}'
```

### Facturas de un Cliente

```bash
curl -X GET "$BASE/api/v1/facturacion/cliente/1" \
  -H "Authorization: Bearer $TOKEN"
```

---

## 26. POS — Terminal Bancaria

> **Ruta base:** `/api/v1/pos/pagos`

### Cobrar con Tarjeta

```bash
curl -X POST "$BASE/api/v1/pos/pagos/tarjeta" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"ventaId":1,"monto":360.00,"tipoTarjeta":"DEBITO"}'
```

### Estado de Transacción

```bash
curl -X GET "$BASE/api/v1/pos/pagos/REF-20260527-001/estatus" \
  -H "Authorization: Bearer $TOKEN"
```

---

## 27. POS — Sincronización Offline

> **Ruta base:** `/api/v1/sincronizacion`

### Subir Lote de Ventas Offline

```bash
curl -X POST "$BASE/api/v1/sincronizacion/lote" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"sucursalId":2,"ventas":[{"ventaLocalId":"OFF-001","...":"..."}]}'
```

### Ver Lotes Pendientes

```bash
curl -X GET "$BASE/api/v1/sincronizacion/pendientes" \
  -H "Authorization: Bearer $TOKEN"
```

---

## 28. Sucursales

> **Ruta base:** `/api/v1/sucursales`

### Listar Sucursales

```bash
curl -X GET "$BASE/api/v1/sucursales" \
  -H "Authorization: Bearer $TOKEN"
```

### Crear Sucursal

```bash
curl -X POST "$BASE/api/v1/sucursales" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Sucursal Centro",
    "direccion": "Av. Reforma 123, CDMX",
    "telefono": "5512341234",
    "email": "centro@nexoohub.com",
    "responsable": "María González"
  }'
```

### Actualizar Sucursal

```bash
curl -X PUT "$BASE/api/v1/sucursales/1" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Sucursal Centro Histórico",
    "telefono": "5512345678",
    "responsable": "María González Pérez"
  }'
```

### Eliminar Sucursal

```bash
curl -X DELETE "$BASE/api/v1/sucursales/1" \
  -H "Authorization: Bearer $TOKEN"
```

---

## 29. Empleados

> **Ruta base:** `/api/v1/empleados`

### Crear Empleado

```bash
curl -X POST "$BASE/api/v1/empleados" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Carlos Ramírez",
    "puesto": "Vendedor",
    "sucursalId": 1,
    "telefono": "5523456789",
    "email": "carlos@nexoohub.com",
    "fechaIngreso": "2026-01-15",
    "salarioBase": 8000.00,
    "comisionVentas": 3.0
  }'
```

### Empleados de una Sucursal

```bash
curl -X GET "$BASE/api/v1/empleados/sucursal/1" \
  -H "Authorization: Bearer $TOKEN"
```

### Eliminar Empleado

```bash
curl -X DELETE "$BASE/api/v1/empleados/2" \
  -H "Authorization: Bearer $TOKEN"
```

---

## 30. Comisiones y Reglas

> **Ruta base:** `/api/comisiones` *(sin `/v1/`)*

### Listar Reglas de Comisión

```bash
curl -X GET "$BASE/api/comisiones/reglas" \
  -H "Authorization: Bearer $TOKEN"
```

### Crear Regla de Comisión

```bash
curl -X POST "$BASE/api/comisiones/reglas" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Comisión Vendedor Estándar",
    "descripcion": "3% sobre ventas del mes",
    "tipo": "PORCENTAJE",
    "porcentaje": 3.0
  }'
```

### Calcular Comisiones del Periodo

```bash
curl -X POST "$BASE/api/comisiones/calcular?anio=2026&mes=5" \
  -H "Authorization: Bearer $TOKEN"
```

### Resumen de Comisiones

```bash
curl -X GET "$BASE/api/comisiones/resumen?anio=2026&mes=5" \
  -H "Authorization: Bearer $TOKEN"
```

### Comisiones de un Vendedor

```bash
curl -X GET "$BASE/api/comisiones/vendedor/1" \
  -H "Authorization: Bearer $TOKEN"
```

### Aprobar Pago de Comisión

```bash
curl -X PUT "$BASE/api/comisiones/1/aprobar" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"observaciones":"Aprobado para nómina de mayo 2026"}'
```

---

## 31. Metas de Ventas (RH)

> **Ruta base:** `/api/v1/rh/metas`

### Asignar Meta a Empleado

```bash
curl -X POST "$BASE/api/v1/rh/metas" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "empleadoId": 1,
    "mes": 6,
    "anio": 2026,
    "metaMonto": 80000.00
  }'
```

### Progreso de Meta

```bash
curl -X GET "$BASE/api/v1/rh/metas/1/progreso" \
  -H "Authorization: Bearer $TOKEN"
```

---

## 32. Finanzas — Dashboard

```bash
curl -X GET "$BASE/api/v1/dashboard" \
  -H "Authorization: Bearer $TOKEN"
```

---

## 33. Finanzas — Configuración

> **Ruta base:** `/api/v1/finanzas/parametros`

### Obtener Configuración Vigente

```bash
curl -X GET "$BASE/api/v1/finanzas/parametros" \
  -H "Authorization: Bearer $TOKEN"
```

### Actualizar Configuración

```bash
curl -X PUT "$BASE/api/v1/finanzas/parametros" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "margenUtilidad": 45.0,
    "descuentoMaximo": 20.0,
    "comisionVendedor": 3.0,
    "iva": 16.0,
    "diasCreditoDefault": 30
  }'
```

---

## 34. Finanzas — Auditoría de Precios

> **Ruta base:** `/api/v1/auditoria/precios`

### Historial de Precios de un Producto

```bash
curl -X GET "$BASE/api/v1/auditoria/precios/producto/ACEITE-CASTROL-10W40-1L" \
  -H "Authorization: Bearer $TOKEN"
```

### Cambios en un Periodo

```bash
curl -X GET "$BASE/api/v1/auditoria/precios/periodo?fechaInicio=2026-01-01T00:00:00&fechaFin=2026-05-31T23:59:59" \
  -H "Authorization: Bearer $TOKEN"
```

### Cambios Significativos

```bash
curl -X GET "$BASE/api/v1/auditoria/precios/significativos" \
  -H "Authorization: Bearer $TOKEN"
```

---

## 35. Crédito y Cobranza

> **Ruta base:** `/api/credito` *(sin `/v1/`)*

### Crear Límite de Crédito

```bash
curl -X POST "$BASE/api/credito/limites" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "clienteId": 1,
    "limiteCredito": 50000.00,
    "diasCredito": 30,
    "observaciones": "Cliente con buen historial de pago"
  }'
```

### Consultar Límite de un Cliente

```bash
curl -X GET "$BASE/api/credito/limites/cliente/1" \
  -H "Authorization: Bearer $TOKEN"
```

### Validar si Cliente Puede Comprar a Crédito

```bash
curl -X GET "$BASE/api/credito/validar?clienteId=1&monto=5000.00" \
  -H "Authorization: Bearer $TOKEN"
```

### Registrar Abono

```bash
curl -X POST "$BASE/api/credito/abonos" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "clienteId": 1,
    "monto": 10000.00,
    "metodoPago": "TRANSFERENCIA",
    "referencia": "TRF-20260527-001",
    "observaciones": "Abono parcial factura mayo"
  }'
```

### Bloquear Crédito de Cliente

```bash
curl -X PUT "$BASE/api/credito/limites/cliente/1/bloquear" \
  -H "Authorization: Bearer $TOKEN"
```

### Historial de Crédito

```bash
curl -X GET "$BASE/api/credito/historial/1" \
  -H "Authorization: Bearer $TOKEN"
```

### Clientes en Riesgo

```bash
curl -X GET "$BASE/api/credito/limites/riesgo" \
  -H "Authorization: Bearer $TOKEN"
```

### Clientes con Sobregiro

```bash
curl -X GET "$BASE/api/credito/limites/sobregiro" \
  -H "Authorization: Bearer $TOKEN"
```

---

## 36. ERP — CxP y Gastos

### Listar Cuentas por Pagar

```bash
curl -X GET "$BASE/api/v1/cxp" \
  -H "Authorization: Bearer $TOKEN"
```

### Registrar Pago de CxP

```bash
curl -X POST "$BASE/api/v1/cxp/1/pagos" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"monto":15000.00,"metodoPago":"TRANSFERENCIA","referencia":"TRF-PROV-001"}'
```

### Registrar Gasto Operativo

```bash
curl -X POST "$BASE/api/v1/finanzas/gastos" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "concepto": "Pago de servicios (luz, agua, internet)",
    "monto": 3500.00,
    "categoria": "SERVICIOS",
    "sucursalId": 1,
    "fecha": "2026-05-27"
  }'
```

### Listar Gastos

```bash
curl -X GET "$BASE/api/v1/finanzas/gastos" \
  -H "Authorization: Bearer $TOKEN"
```

---

## 37. ERP — Contabilidad

> **Ruta base:** `/api/v1/contabilidad`

### Catálogo de Cuentas

```bash
curl -X GET "$BASE/api/v1/contabilidad/cuentas" \
  -H "Authorization: Bearer $TOKEN"
```

### Crear Póliza Contable

```bash
curl -X POST "$BASE/api/v1/contabilidad/polizas" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "tipo": "INGRESO",
    "descripcion": "Cierre de ventas mayo 2026",
    "movimientos": [
      {"cuentaId":1,"debe":50000.00,"haber":0},
      {"cuentaId":4,"debe":0,"haber":50000.00}
    ]
  }'
```

### Balanza de Comprobación

```bash
curl -X GET "$BASE/api/v1/contabilidad/reportes/balanza" \
  -H "Authorization: Bearer $TOKEN"
```

### Estado de Resultados

```bash
curl -X GET "$BASE/api/v1/contabilidad/reportes/estado-resultados" \
  -H "Authorization: Bearer $TOKEN"
```

---

## 38. ERP — Logística

> **Ruta base:** `/api/v1/logistica`

### Listar Flota Vehicular

```bash
curl -X GET "$BASE/api/v1/logistica/vehiculos" \
  -H "Authorization: Bearer $TOKEN"
```

### Listar Choferes

```bash
curl -X GET "$BASE/api/v1/logistica/choferes" \
  -H "Authorization: Bearer $TOKEN"
```

### Crear Ruta de Entrega

```bash
curl -X POST "$BASE/api/v1/logistica/rutas" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "vehiculoId": 1,
    "choferId": 1,
    "fecha": "2026-05-28",
    "observaciones": "Entregas zona norte CDMX"
  }'
```

### Asignar Facturas a Ruta

```bash
curl -X POST "$BASE/api/v1/logistica/rutas/1/facturas" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"facturaIds":[1,2,3]}'
```

### Actualizar Estatus de Ruta

```bash
curl -X PATCH "$BASE/api/v1/logistica/rutas/1/estatus" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"estatus":"EN_CAMINO"}'
```

---

## 39. ERP — Nómina

> **Ruta base:** `/api/v1/nomina`

### Registrar Empleado en Nómina

```bash
curl -X POST "$BASE/api/v1/nomina/empleados" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "empleadoId": 1,
    "salarioBruto": 12000.00,
    "periodicidad": "QUINCENAL",
    "tipoPago": "TRANSFERENCIA"
  }'
```

### Crear Periodo de Nómina

```bash
curl -X POST "$BASE/api/v1/nomina/periodos" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"fechaInicio":"2026-05-16","fechaFin":"2026-05-31","descripcion":"2a quincena mayo 2026"}'
```

### Generar Recibos del Periodo

```bash
curl -X POST "$BASE/api/v1/nomina/periodos/1/generar" \
  -H "Authorization: Bearer $TOKEN"
```

### Ver Recibo de Nómina

```bash
curl -X GET "$BASE/api/v1/nomina/recibos/1" \
  -H "Authorization: Bearer $TOKEN"
```

---

## 40. ERP — Devoluciones a Proveedor

> **Ruta base:** `/api/v1/devoluciones/proveedores`

### Crear Devolución a Proveedor

```bash
curl -X POST "$BASE/api/v1/devoluciones/proveedores" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "proveedorId": 1,
    "detalles": [
      {
        "skuInterno": "ACEITE-CASTROL-10W40-1L",
        "cantidad": 5,
        "motivo": "Producto con defecto de fábrica"
      }
    ],
    "observaciones": "Solicitamos nota de crédito"
  }'
```

### Aplicar Devolución (genera movimiento de inventario)

```bash
curl -X POST "$BASE/api/v1/devoluciones/proveedores/1/aplicar" \
  -H "Authorization: Bearer $TOKEN"
```

---

## 41. CRM — Pipeline B2B

> **Ruta base:** `/api/v1/crm`

### Registrar Prospecto

```bash
curl -X POST "$BASE/api/v1/crm/prospectos" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "empresa": "Taller Moto Express",
    "contactoPrincipal": "Luis García",
    "telefono": "5598765432",
    "email": "luis@motoexpress.com",
    "rfc": "MOE200101ABC"
  }'
```

### Crear Oportunidad de Negocio

```bash
curl -X POST "$BASE/api/v1/crm/oportunidades" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "prospectoId": 1,
    "titulo": "Suministro mensual de aceites",
    "valorProyectado": 25000.00,
    "etapa": "PROPUESTA",
    "probabilidad": 60
  }'
```

### Avanzar Etapa del Pipeline

```bash
curl -X PATCH "$BASE/api/v1/crm/oportunidades/1/etapa" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"nuevaEtapa":"NEGOCIACION","observaciones":"Enviamos propuesta revisada"}'
```

### Registrar Interacción

```bash
curl -X POST "$BASE/api/v1/crm/interacciones" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "prospectoId": 1,
    "tipoInteraccion": "LLAMADA",
    "resumen": "Cliente interesado en descuento por volumen"
  }'
```

---

## 42. CRM — Garantías

> **Ruta base:** `/api/crm/garantias` *(sin `/v1/`)*

### Crear Ticket de Garantía

```bash
curl -X POST "$BASE/api/crm/garantias" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "clienteId": 1,
    "ventaId": 1,
    "skuInterno": "ACEITE-CASTROL-10W40-1L",
    "descripcionProblema": "Aceite con partículas extrañas"
  }'
```

### Listar Tickets de Garantía

```bash
curl -X GET "$BASE/api/crm/garantias" \
  -H "Authorization: Bearer $TOKEN"
```

### Resolver Garantía

```bash
curl -X PUT "$BASE/api/crm/garantias/1/resolver" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"resolucion":"Reemplazo de producto","observaciones":"Lote con defecto, reemplazado sin costo"}'
```

---

## 43. CRM — NPS

> **Ruta base:** `/api/v1/crm/nps`

### Crear Encuesta NPS

```bash
curl -X POST "$BASE/api/v1/crm/nps/encuestas" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"clienteId":1,"ventaId":1}'
```

### Registrar Respuesta NPS

```bash
curl -X POST "$BASE/api/v1/crm/nps/respuestas" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"encuestaId":1,"puntuacion":9,"comentario":"Excelente atención y precios competitivos"}'
```

### Dashboard NPS

```bash
curl -X GET "$BASE/api/v1/crm/nps/dashboard" \
  -H "Authorization: Bearer $TOKEN"
```

---

## 44. CRM — Marketing

> **Ruta base:** `/api/v1/marketing/campanas`

### Crear Campaña

```bash
curl -X POST "$BASE/api/v1/marketing/campanas" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Promoción Fin de Temporada",
    "tipo": "EMAIL",
    "segmento": "CLIENTES_VIP",
    "mensaje": "Descuento exclusivo del 20% este fin de semana"
  }'
```

### Ejecutar Campaña

```bash
curl -X POST "$BASE/api/v1/marketing/campanas/1/ejecutar" \
  -H "Authorization: Bearer $TOKEN"
```

### Métricas de la Campaña

```bash
curl -X GET "$BASE/api/v1/marketing/campanas/1/metricas" \
  -H "Authorization: Bearer $TOKEN"
```

---

## 45. Métricas Financieras

> **Ruta base:** `/api/v1/metricas-financieras`

### Dashboard Ejecutivo Financiero

```bash
curl -X GET "$BASE/api/v1/metricas-financieras/dashboard-ejecutivo" \
  -H "Authorization: Bearer $TOKEN"
```

### Generar Análisis Financiero

```bash
curl -X POST "$BASE/api/v1/metricas-financieras/analisis" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"sucursalId":1,"fechaInicio":"2026-05-01","fechaFin":"2026-05-31"}'
```

### Top Productos por Ingreso

```bash
curl -X GET "$BASE/api/v1/metricas-financieras/top-productos" \
  -H "Authorization: Bearer $TOKEN"
```

### Health Check

```bash
curl -X GET "$BASE/api/v1/metricas-financieras/health" \
  -H "Authorization: Bearer $TOKEN"
```

---

## 46. Métricas de Inventario

> **Ruta base:** `/api/v1/metricas/inventario`

### Valor Actual del Inventario

```bash
curl -X GET "$BASE/api/v1/metricas/inventario/valor-actual" \
  -H "Authorization: Bearer $TOKEN"
```

### Productos en Stock Bajo

```bash
curl -X GET "$BASE/api/v1/metricas/inventario/productos/bajo-stock" \
  -H "Authorization: Bearer $TOKEN"
```

### Productos Sin Stock

```bash
curl -X GET "$BASE/api/v1/metricas/inventario/productos/sin-stock" \
  -H "Authorization: Bearer $TOKEN"
```

---

## 47. Métricas Operativas

> **Ruta base:** `/api/v1/metricas/operativas`

### Métricas del Mes Actual

```bash
curl -X GET "$BASE/api/v1/metricas/operativas/mes-actual" \
  -H "Authorization: Bearer $TOKEN"
```

### Comparativo Mes Anterior

```bash
curl -X GET "$BASE/api/v1/metricas/operativas/mes-anterior" \
  -H "Authorization: Bearer $TOKEN"
```

### Últimos 7 Días

```bash
curl -X GET "$BASE/api/v1/metricas/operativas/ultimos-7-dias" \
  -H "Authorization: Bearer $TOKEN"
```

### Consolidado General

```bash
curl -X GET "$BASE/api/v1/metricas/operativas/consolidado" \
  -H "Authorization: Bearer $TOKEN"
```

---

## 48. Métricas Ventas y Clientes

> **Ruta base:** `/api/metricas/ventas-clientes` *(sin `/v1/`)*

### Mes Actual

```bash
curl -X GET "$BASE/api/metricas/ventas-clientes/mes-actual" \
  -H "Authorization: Bearer $TOKEN"
```

### Consolidado

```bash
curl -X GET "$BASE/api/metricas/ventas-clientes/consolidado" \
  -H "Authorization: Bearer $TOKEN"
```

### Análisis Personalizado

```bash
curl -X POST "$BASE/api/metricas/ventas-clientes/analisis" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"sucursalId":1,"fechaInicio":"2026-05-01","fechaFin":"2026-05-31"}'
```

---

## 49. Rentabilidad

> **Ruta base:** `/api/v1/rentabilidad`

### Calcular Rentabilidad de Productos

```bash
curl -X POST "$BASE/api/v1/rentabilidad/productos" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"sucursalId":1,"fechaInicio":"2026-05-01","fechaFin":"2026-05-31"}'
```

### Top Productos Más Rentables

```bash
curl -X GET "$BASE/api/v1/rentabilidad/productos/mas-rentables?top=10&sucursalId=1" \
  -H "Authorization: Bearer $TOKEN"
```

### Productos con Margen Bajo

```bash
curl -X GET "$BASE/api/v1/rentabilidad/productos/menos-rentables?top=10&sucursalId=1" \
  -H "Authorization: Bearer $TOKEN"
```

### Ventas con Bajo Costo

```bash
curl -X GET "$BASE/api/v1/rentabilidad/ventas/bajo-costo" \
  -H "Authorization: Bearer $TOKEN"
```

### Estadísticas Generales

```bash
curl -X GET "$BASE/api/v1/rentabilidad/estadisticas?sucursalId=1" \
  -H "Authorization: Bearer $TOKEN"
```

---

## 50. Analítica — RFM

> **Ruta base:** `/api/v1/analitica/rfm`

### Calcular RFM Masivo

```bash
curl -X POST "$BASE/api/v1/analitica/rfm/calcular" \
  -H "Authorization: Bearer $TOKEN"
```

### Ver Segmentos RFM

```bash
curl -X GET "$BASE/api/v1/analitica/rfm/segmentos" \
  -H "Authorization: Bearer $TOKEN"
```

### RFM de un Cliente

```bash
curl -X GET "$BASE/api/v1/analitica/rfm/cliente/1" \
  -H "Authorization: Bearer $TOKEN"
```

---

## 51. Analítica — Churn

> **Ruta base:** `/api/v1/analitica/churn`

### Calcular Riesgo de Abandono

```bash
curl -X POST "$BASE/api/v1/analitica/churn/calcular" \
  -H "Authorization: Bearer $TOKEN"
```

### Clientes en Riesgo de Abandono

```bash
curl -X GET "$BASE/api/v1/analitica/churn/en-riesgo" \
  -H "Authorization: Bearer $TOKEN"
```

---

## 52. Analítica — Market Basket

> **Ruta base:** `/api/v1/analitica/canasta`

### Calcular Reglas de Asociación

```bash
curl -X POST "$BASE/api/v1/analitica/canasta/calcular" \
  -H "Authorization: Bearer $TOKEN"
```

### Sugerencias para un SKU

```bash
curl -X GET "$BASE/api/v1/analitica/canasta/ACEITE-CASTROL-10W40-1L" \
  -H "Authorization: Bearer $TOKEN"
```

---

## 53. Analítica — Rendimiento Personal

> **Ruta base:** `/api/v1/analitica/personal`

### Calcular Rendimiento

```bash
curl -X POST "$BASE/api/v1/analitica/personal/calcular" \
  -H "Authorization: Bearer $TOKEN"
```

### Ranking General

```bash
curl -X GET "$BASE/api/v1/analitica/personal/rendimiento" \
  -H "Authorization: Bearer $TOKEN"
```

### Tendencia de un Empleado

```bash
curl -X GET "$BASE/api/v1/analitica/personal/1/tendencia" \
  -H "Authorization: Bearer $TOKEN"
```

---

## 54. Fidelidad

> **Ruta base:** `/api/v1/fidelidad`

### Crear Programa de Fidelidad para Cliente

```bash
curl -X POST "$BASE/api/v1/fidelidad/programa" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"clienteId":1,"nivel":"BRONCE"}'
```

### Consultar Programa del Cliente

```bash
curl -X GET "$BASE/api/v1/fidelidad/programa/cliente/1" \
  -H "Authorization: Bearer $TOKEN"
```

### Acumular Puntos

```bash
curl -X POST "$BASE/api/v1/fidelidad/acumular" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "clienteId": 1,
    "montoCompra": 500.00,
    "ventaId": 1,
    "descripcion": "Compra en sucursal centro"
  }'
```

### Canjear Puntos

```bash
curl -X POST "$BASE/api/v1/fidelidad/canjear" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "clienteId": 1,
    "puntosACanjear": 100,
    "ventaId": 2,
    "descripcion": "Descuento aplicado en venta"
  }'
```

### Historial de Movimientos

```bash
curl -X GET "$BASE/api/v1/fidelidad/historial/cliente/1" \
  -H "Authorization: Bearer $TOKEN"
```

### Estadísticas del Programa

```bash
curl -X GET "$BASE/api/v1/fidelidad/estadisticas" \
  -H "Authorization: Bearer $TOKEN"
```

---

## 55. Predicción de Demanda

> **Ruta base:** `/api/predicciones` *(sin `/v1/`)*

### Generar Predicción

```bash
curl -X POST "$BASE/api/predicciones/generar" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "skuProducto": "ACEITE-CASTROL-10W40-1L",
    "sucursalId": 1,
    "periodos": 3,
    "algoritmo": "PROMEDIO_MOVIL"
  }'
```

### Predicción por Producto

```bash
curl -X GET "$BASE/api/predicciones/producto/ACEITE-CASTROL-10W40-1L" \
  -H "Authorization: Bearer $TOKEN"
```

### Recomendaciones de Reorden

```bash
curl -X GET "$BASE/api/predicciones/recomendaciones" \
  -H "Authorization: Bearer $TOKEN"
```

---

## 56. Alertas del Sistema

> **Ruta base:** `/api/v1/alertas`

### Mis Alertas (por usuario)

```bash
curl -X GET "$BASE/api/v1/alertas/mis-alertas/1" \
  -H "Authorization: Bearer $TOKEN"
```

### Alertas de una Sucursal

```bash
curl -X GET "$BASE/api/v1/alertas/sucursal/1" \
  -H "Authorization: Bearer $TOKEN"
```

### Contador de Alertas No Leídas (badge)

```bash
curl -X GET "$BASE/api/v1/alertas/badge/1" \
  -H "Authorization: Bearer $TOKEN"
```

### Marcar Alerta como Leída

```bash
curl -X PUT "$BASE/api/v1/alertas/1/leer" \
  -H "Authorization: Bearer $TOKEN"
```

### Resolver Alerta

```bash
curl -X PUT "$BASE/api/v1/alertas/1/resolver" \
  -H "Authorization: Bearer $TOKEN"
```

### Suscribirse al Stream SSE (Alertas en Tiempo Real)

```bash
curl -N \
  -H "Accept: text/event-stream" \
  -H "Authorization: Bearer $TOKEN" \
  "$BASE/api/v1/alertas/stream/SSE"
```

---

## Notas Importantes

### Prefijos de Rutas

Algunos módulos usan prefijos distintos. Aquí el mapa de referencia:

| Módulo | Prefijo |
|---|---|
| La mayoría | `/api/v1/` |
| Cotizaciones | `/api/cotizaciones` |
| Comisiones | `/api/comisiones` |
| Crédito | `/api/credito` |
| Garantías CRM | `/api/crm/garantias` |
| Métricas ventas-clientes | `/api/metricas/ventas-clientes` |
| Predicciones | `/api/predicciones` |
| Alertas lento movimiento | `/api/alertas/lento-movimiento` |
| Comparador precios SUP | `/api/sup/comparador` |

### Códigos de Respuesta HTTP

| Código | Significado |
|---|---|
| 200 | Operación exitosa |
| 201 | Recurso creado correctamente |
| 400 | Datos inválidos o regla de negocio no cumplida |
| 401 | Token ausente o expirado |
| 403 | Sin permisos |
| 404 | Recurso no encontrado |
| 405 | Método HTTP no soportado en ese endpoint |
| 409 | Conflicto (duplicado, stock insuficiente, crédito insuficiente) |
| 500 | Error interno del servidor (0 registrados en producción) |

### Paginación

Los endpoints con listas soportan paginación con `?page=0&size=20`. Por defecto se devuelven los primeros 20 registros.

### Links Útiles

- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **OpenAPI JSON:** http://localhost:8080/v3/api-docs
- **Actuator Health:** http://localhost:8080/actuator/health

---

*© 2026 NexooHub Development Team*
