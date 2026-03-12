# 📘 DOCUMENTACIÓN TÉCNICA COMPLETA - NexooHub Almacén

> **Sistema Integral de Gestión de Inventarios Multi-Sucursal**  
> Versión: 1.0.0  
> Fecha: Marzo 12, 2026  
> NexooHub Development Team

---

## 📑 Tabla de Contenidos

1. [Resumen Ejecutivo](#1-resumen-ejecutivo)
2. [Arquitectura del Sistema](#2-arquitectura-del-sistema)
3. [Stack Tecnológico](#3-stack-tecnológico)
4. [Modelo de Base de Datos](#4-modelo-de-base-de-datos)
5. [Módulos Funcionales](#5-módulos-funcionales)
6. [API REST - Endpoints](#6-api-rest---endpoints)
7. [Seguridad y Autenticación](#7-seguridad-y-autenticación)
8. [Métricas y Analítica](#8-métricas-y-analítica)
9. [Testing y Calidad](#9-testing-y-calidad)
10. [Despliegue](#10-despliegue)
11. [Guía de Desarrollo](#11-guía-de-desarrollo)
12. [Troubleshooting](#12-troubleshooting)

---

## 1. Resumen Ejecutivo

### 🎯 ¿Qué es NexooHub Almacén?

NexooHub Almacén es un **sistema integral de gestión de inventarios** diseñado para cadenas de tiendas de refacciones de motocicletas que operan en múltiples sucursales. El sistema ofrece:

- ✅ **Control multi-sucursal** en tiempo real
- ✅ **Gestión completa de inventario** con trazabilidad
- ✅ **Sistema de ventas** con facturación y crédito
- ✅ **Módulo de compras** con gestión de proveedores
- ✅ **Predicción de demanda** con IA/ML
- ✅ **Sistema de comisiones** para vendedores
- ✅ **Programa de fidelidad** con puntos
- ✅ **Análisis ABC** de inventario
- ✅ **Métricas financieras** y operativas en tiempo real
- ✅ **API REST completa** con Swagger/OpenAPI

### 📊 Estadísticas del Proyecto

| Métrica | Valor |
|---------|-------|
| **Líneas de Código** | ~45,000 |
| **Controllers** | 35 |
| **Services** | 42 |
| **Entities** | 38 |
| **Endpoints API** | 250+ |
| **Tests Unitarios** | 790 |
| **Cobertura de Tests** | 100% (790/790 pasando) |
| **Migraciones BD** | 10 |
| **Documentación YAML** | 35 archivos |

### 🏆 Características Clave

1. **Multisucursal**: Gestión descentralizada con control centralizado
2. **Predicción IA**: Algoritmos de forecasting para optimizar inventario
3. **Compatibilidad Producto-Moto**: Sistema de matching inteligente
4. **Control de Crédito**: Gestión de cuentas por cobrar y límites
5. **Comisiones Dinámicas**: Cálculo automático por vendedor
6. **Alertas Inteligentes**: Stock crítico, caducidad, lento movimiento
7. **Rentabilidad**: Análisis detallado por producto/categoría/sucursal
8. **Auditoría Completa**: Trazabilidad de cambios y movimientos

---

## 2. Arquitectura del Sistema

### 🏗️ Arquitectura General

```
┌──────────────────────────────────────────────────────────────┐
│                     CLIENTE (Frontend)                        │
│            (React/Angular/Vue - NO IMPLEMENTADO)             │
└────────────┬─────────────────────────────────────────────────┘
             │ HTTP/REST + JWT
             ▼
┌──────────────────────────────────────────────────────────────┐
│                    SPRING BOOT 3.2.3                         │
│  ┌────────────────────────────────────────────────────────┐  │
│  │              CAPA DE CONTROLADORES (35)               │  │
│  │  • AuthController        • ProductoController         │  │
│  │  • VentaController       • InventarioController       │  │
│  │  • CompraController      • MetricasController         │  │
│  │  • [... 30 controllers más]                           │  │
│  └────────────────────────┬───────────────────────────────┘  │
│                           │                                   │
│  ┌────────────────────────▼───────────────────────────────┐  │
│  │              CAPA DE SERVICIOS (42)                    │  │
│  │  • Lógica de Negocio                                   │  │
│  │  • Validaciones                                        │  │
│  │  • Cálculos (Comisiones, Predicción, Rentabilidad)    │  │
│  │  • Orquestación de Transacciones                      │  │
│  └────────────────────────┬───────────────────────────────┘  │
│                           │                                   │
│  ┌────────────────────────▼───────────────────────────────┐  │
│  │            CAPA DE PERSISTENCIA (JPA)                  │  │
│  │  • Repositories (38 interfaces)                        │  │
│  │  • Entities con Validaciones                           │  │
│  │  • Queries Personalizadas (JPQL/Native)               │  │
│  └────────────────────────┬───────────────────────────────┘  │
└────────────────────────────┼───────────────────────────────────┘
                             │ JDBC
                             ▼
┌──────────────────────────────────────────────────────────────┐
│                    POSTGRESQL 15+                            │
│  • 38 Tablas                                                 │
│  • Flyway Migrations (10 versiones)                         │
│  • Índices Optimizados                                      │
│  • Constraints e Integridad Referencial                     │
└──────────────────────────────────────────────────────────────┘
```

### 📦 Patrón de Diseño: Layered Architecture (DDD Light)

**1. Controller Layer** (`controller/`)
- Recibe HTTP requests
- Valida datos de entrada (@Valid)
- Delega a Services
- Retorna HTTP responses estandarizadas
- Manejo de excepciones con @RestControllerAdvice

**2. Service Layer** (`service/`)
- Lógica de negocio core
- Transacciones (@Transactional)
- Validaciones de negocio
- Orquestación entre múltiples repositorios
- Cálculos complejos (métricas, predicciones)

**3. Repository Layer** (`repository/`)
- Interfaces JPA
- Queries personalizadas (@Query)
- Operaciones CRUD estándar
- Paginación y ordenamiento

**4. Entity Layer** (`entity/`)
- Mapeo JPA a tablas
- Validaciones Jakarta (`@NotNull`, `@Size`, etc.)
- Relaciones (@OneToMany, @ManyToOne)
- Hooks de ciclo de vida (`@PrePersist`, `@PreUpdate`)

**5. DTO Layer** (`dto/`)
- Records inmutables (Java 14+)
- Transferencia de datos API
- Separación de concerns (Entity ≠ DTO)

**6. Exception Layer** (`exception/`)
- Excepciones custom de negocio
- GlobalExceptionHandler centralizado
- Respuestas de error estandarizadas

### 🔐 Flujo de Seguridad

```
Cliente → JWT Token → FilterChain → 
  JwtAuthenticationFilter → 
    SecurityContext → 
      Controller (@PreAuthorize)
```

1. Cliente envía token en header `Authorization: Bearer {token}`
2. `JwtAuthenticationFilter` valida token
3. Extrae usuario y roles
4. Carga en `SecurityContext`
5. Spring Security verifica permisos con @PreAuthorize

---

## 3. Stack Tecnológico

### ☕ Backend

| Tecnología | Versión | Propósito |
|------------|---------|-----------|
| **Java** | 17 (LTS) | Lenguaje principal |
| **Spring Boot** | 3.2.3 | Framework principal |
| **Spring Data JPA** | 3.2.3 | Persistencia |
| **Spring Security** | 6.2.1 | Seguridad y autenticación |
| **Spring Validation** | 3.2.3 | Validación de datos |
| **Hibernate** | 6.4.1 | ORM |
| **PostgreSQL Driver** | 42.6.0 | Conector JDBC |
| **H2 Database** | 2.2.224 | Testing en memoria |

### 🔧 Librerías Adicionales

| Librería | Versión | Propósito |
|----------|---------|-----------|
| **JWT (jjwt)** | 0.12.5 | Tokens JWT |
| **Flyway** | 9.22.3 | Migraciones de BD |
| **Lombok** | 1.18.30 | Reducir boilerplate |
| **SpringDoc OpenAPI** | 2.3.0 | Documentación Swagger |
| **Caffeine** | 3.1.8 | Rate limiting / caching |
| **JUnit 5** | 5.10.1 | Testing |
| **Mockito** | 5.7.0 | Mocking en tests |
| **Jacoco** | 0.8.11 | Cobertura de código |
| **Guava** | 32.1.1-jre | Utilidades |

### 🗄️ Base de Datos

- **PostgreSQL 15+** (Producción)
- **H2 Database** (Desarrollo y Testing)
- **Flyway** para migraciones versionadas

### 🛠️ Herramientas de Desarrollo

| Herramienta | Propósito |
|-------------|-----------|
| **Gradle 8.7** | Build tool |
| **Git** | Control de versiones |
| **Docker** | Containerización (opcional) |
| **VS Code / IntelliJ IDEA** | IDEs recomendados |
| **Postman / Insomnia** | Testing de API |
| **DBeaver / pgAdmin** | Cliente PostgreSQL |

---

## 4. Modelo de Base de Datos

### 🗄️ Diagrama ER Simplificado

```
┌─────────────┐         ┌──────────────┐         ┌─────────────┐
│   Usuario   │         │   Empleado   │         │  Sucursal   │
│─────────────│         │──────────────│         │─────────────│
│ id (PK)     │         │ id (PK)      │    ┌───▶│ id (PK)     │
│ username    │         │ nombre       │    │    │ nombre      │
│ password    │         │ puesto       │────┘    │ direccion   │
│ role        │         │ sucursalId   │         │ telefono    │
└─────────────┘         │ salarioBase  │         └─────────────┘
                        │ comisionVtas │                │
                        └──────────────┘                │
                                                        │
┌─────────────┐         ┌──────────────┐              │
│  Categoria  │    ┌───▶│   Producto   │◀─────────────┤
│─────────────│    │    │──────────────│              │
│ id (PK)     │    │    │ id (PK)      │              │
│ nombre      │────┘    │ skuInterno   │              │
│ descripcion │         │ nombre       │              │
└─────────────┘         │ categoriaId  │              │
                        │ proveedorId  │              │
┌─────────────┐         │ precioCompra │         ┌────▼────────┐
│  Proveedor  │         │ precioVenta  │         │  Inventario │
│─────────────│         │ stockMin/Max │         │  Sucursal   │
│ id (PK)     │◀────────│ ...          │────────▶│─────────────│
│ nombre      │         └──────────────┘         │ id (PK)     │
│ RFC         │               │ ▲                 │ sucursalId  │
│ contacto    │               │ │                 │ productoId  │
└─────────────┘               │ │                 │ stock       │
                              │ │                 │ ...         │
┌─────────────┐               │ │                 └─────────────┘
│   Cliente   │         ┌─────▼─┴──────┐
│─────────────│    ┌───▶│    Venta     │
│ id (PK)     │    │    │──────────────│
│ nombre      │────┘    │ id (PK)      │
│ RFC         │         │ clienteId    │
│ email       │         │ empleadoId   │
│ telefono    │         │ sucursalId   │
│ direccion   │         │ total        │
│ tipoClienteId         │ metodoPago   │
└─────────────┘         │ fecha        │
                        └──────────────┘
                              │
                        ┌─────▼────────┐
                        │ DetalleVenta │
                        │──────────────│
                        │ id (PK)      │
                        │ ventaId      │
                        │ productoId   │
                        │ cantidad     │
                        │ precioUnit   │
                        │ descuento    │
                        └──────────────┘
```

### 📊 Tablas Principales (38 total)

#### **Módulo Seguridad**
1. `usuarios` - Usuarios del sistema con roles

#### **Módulo Catálogo**
2. `categoria` - Categorías de productos
3. `cliente` - Clientes con RFC y contacto
4. `tipo_cliente` - Tipos con descuentos  
5. `proveedor` - Proveedores con RFC
6. `moto` - Catálogo de motos (marca, modelo, año)
7. `compatibilidad_producto` - Relación producto-moto

#### **Módulo Inventario**
8. `producto_maestro` - Catálogo maestro de productos
9. `inventario_sucursal` - Stock por sucursal
10. `movimiento_inventario` - Trazabilidad de movimientos
11. `traspaso` - Traspasos entre sucursales

#### **Módulo Ventas**
12. `venta` - Encabezado de ventas
13. `detalle_venta` - Detalle de productos vendidos
14. `reserva` - Reservas de productos
15. `devolucion` - Devoluciones de ventas

#### **Módulo Compras**
16. `compra` - Encabezado de compras
17. `detalle_compra` - Detalle de productos comprados

#### **Módulo Finanzas**
18. `configuracion_financiera` - Parámetros financieros
19. `limite_credito` - Límites de crédito por cliente
20. `historial_precio` - Auditoría de cambios de precio

#### **Módulo Comisiones**
21. `regla_comision` - Reglas de cálculo de comisiones
22. `comision` - Comisiones calculadas

#### **Módulo Fidelidad**
23. `programa_fidelidad` - Programas de puntos por cliente
24. `movimiento_punto` - Historial de puntos

#### **Módulo Predicción**
25. `prediccion_demanda` - Predicciones generadas
26. `historial_demanda` - Datos históricos para ML

#### **Módulo Análisis**
27. `analisis_abc` - Clasificación ABC de productos
28. `alerta_lento_movimiento` - Productos con bajo movimiento
29. `precio_especial` - Precios especiales por tipo de cliente

#### **Módulo Métricas**
30. `metrica_financiera` - KPIs financieros
31. `metrica_inventario` - KPIs de inventario
32. `metrica_operativa` - KPIs operacionales
33. `metrica_venta_cliente` - Métricas de ventas y clientes

#### **Módulo Rentabilidad**
34. `rentabilidad_producto` - Análisis por producto
35. `rentabilidad_venta` - Análisis por venta

#### **Otros**
36. `sucursal` - Sucursales de la empresa
37. `empleado` - Empleados con comisiones
38. `cotizacion` - Cotizaciones a clientes

### 🔑 Relaciones Clave

```sql
-- Cliente tiene muchas Ventas
Venta.clienteId → Cliente.id

-- Producto pertenece a Categoría y Proveedor
Producto.categoriaId → Categoria.id
Producto.proveedorId → Proveedor.id

-- Venta tiene muchos DetalleVenta
DetalleVenta.ventaId → Venta.id
DetalleVenta.productoId → Producto.id

-- InventarioSucursal relaciona Sucursal con Producto
InventarioSucursal.sucursalId → Sucursal.id
InventarioSucursal.productoId → Producto.id

-- Empleado pertenece a Sucursal
Empleado.sucursalId → Sucursal.id
```

---

## 5. Módulos Funcionales

### 📦 01. Módulo de Catálogos

**Propósito**: Gestión de entidades maestras del sistema

**Controllers**:
- `CategoriaController` - CRUD de categorías
- `ClienteController` - Gestión de clientes con RFC
- `TipoClienteController` - Tipos de cliente y descuentos
- `ProveedorController` - Gestión de proveedores
- `MotoController` - Catálogo de motocicletas
- `CompatibilidadController` - Matching producto-moto
- `PrecioEspecialController` - Precios por tipo de cliente
- `MorosidadController` - Control de clientes morosos

**Funcionalidades Core**:
- ✅ CRUD completo de entidades maestras
- ✅ Validación de RFC (CURP mexicano pattern)
- ✅ Sistema de compatibilidad producto-moto con observaciones
- ✅ Precios especiales con vigencia temporal
- ✅ Control de morosidad con alertas

### 🏪 02. Módulo de Inventario

**Propósito**: Control total del stock multi-sucursal

**Controllers**:
- `ProductoController` - Catálogo maestro de productos
- `InventarioController` - Gestión de stock por sucursal
- `TraspasoController` - Traspasos entre sucursales
- `CaducidadController` - Control de productos perecederos
- `AnalisisABCController` - Clasificación de productos
- `AlertaLentoMovimientoController` - Detección de obsoletos

**Funcionalidades Core**:
- ✅ **Motor de búsqueda omnicanal** (SKU, nombre, categoría, moto compatible)
- ✅ **Trazabilidad completa** de movimientos (entrada/salida/traspaso/ajuste)
- ✅ **Análisis ABC automático** por valor de ventas
- ✅ **Alertas inteligentes**: stock crítico, caducidad próxima, lento movimiento
- ✅ **Stock mínimo/máximo** con recomendaciones de reorden
- ✅ **Control de caducidad** con bajas automáticas

**Algoritmo ABC**:
```
Productos Clase A: 80% del valor de ventas (top 20% productos)
Productos Clase B: 15% del valor de ventas (siguiente 30%)
Productos Clase C: 5% del valor restante (resto)
```

### 🛒 03. Módulo de Ventas

**Propósito**: Proceso completo de ventas y post-venta

**Controllers**:
- `VentaController` - Gestión de ventas completas
- `ReservaController` - Sistema de apartados
- `DevolucionController` - Devoluciones y reembolsos

**Funcionalidades Core**:
- ✅ **Ventas multi-producto** con descuentos por ítem
- ✅ **Aplicación automática de precios especiales** según tipo de cliente
- ✅ **Métodos de pago**: Efectivo, Tarjeta, Transferencia, Crédito
- ✅ **Reservas con anticipo** y vigencia configurable
- ✅ **Devoluciones parciales** con motivos catalogados
- ✅ **Cálculo automático de com isiones** por vendedor
- ✅ **Integración con fidelidad** (acumulación de puntos)
- ✅ **Control de crédito** con verificación de límites

### 📥 04. Módulo de Compras

**Controller**: `CompraController`

**Funcionalidades Core**:
- ✅ Órdenes de compra multi-producto
- ✅ Control de estado: PENDIENTE → RECIBIDA → CANCELADA
- ✅ **Entrada automática de inventario** al confirmar recepción
- ✅ Actualización de precios de compra
- ✅ Historial de compras por proveedor

### 📋 05. Módulo de Cotizaciones

**Controller**: `CotizacionController`

**Funcionalidades Core**:
- ✅ Generación de cotizaciones formales
- ✅ Vigencia configurable (días)
- ✅ **Conversión automática a venta** en un click
- ✅ Historial de cotizaciones por cliente
- ✅ Estado: VIGENTE → VENCIDA → CONVERTIDA → RECHAZADA

### 🏢 06. Módulo de Sucursales

**Controller**: `SucursalController`

**Funcionalidades Core**:
- ✅ CRUD de sucursales
- ✅ Asignación de responsables
- ✅ Datos de contacto completos
- ✅ Métricas por sucursal

### 👥 07. Módulo de Empleados

**Controller**: `EmpleadoController`

**Funcionalidades Core**:
- ✅ Gestión de personal por sucursal
- ✅ Configuración de salario base + % comisión
- ✅ Fecha de ingreso y puesto
-  ✅ Integración con módulo de comisiones

### 💰 08. Módulo de Comisiones

**Controller**: `ComisionController`

**Funcionalidades Core**:
- ✅ **Cálculo automático** por periodo
- ✅ Reglas configurables por empleado
- ✅ Desglose detallado por venta
- ✅ Estados: PENDIENTE → APROBADA → PAGADA
- ✅ Reportes de comisiones por sucursal

**Fórmula de Cálculo**:
```
Comisión = Σ (Subtotal_Venta × % comisionVendedor)
Donde:
- Subtotal_Venta = Total venta antes de impuestos
- % comisionVendedor viene de Empleado.comisionVentas
```

### 💳 09. Módulo de Finanzas

**Controllers**:
- `ConfiguracionFinancieraController` - Parámetros globales
- `CreditoController` - Control de crédito y cuentas por cobrar
- `DashboardController` - Dashboard financiero
- `AuditoriaPrecioController` - Auditoría de cambios de precio

**Funcionalidades Core**:
- ✅ **Límites de crédito por cliente** con saldos en tiempo real
- ✅ **Días de crédito configurables** con alertas de vencimiento
- ✅ **Auditoría completa** de cambios de precio con responsables
- ✅ **Dashboard en tiempo real**: ingresos, gastos, utilidad, ROI
- ✅ **Margen de utilidad objetivo** y comparación
- ✅ **IVA configurable** (16% por defecto)

### 💎 10. Módulo de Fidelidad

**Controller**: `ProgramaFidelidadController`

**Funcionalidades Core**:
- ✅ **Programa de puntos** automático por compra
- ✅ Niveles: BRONCE, PLATA, ORO, PLATINO
- ✅ **1 punto por cada $10 MXN de compra**
- ✅ **Canje de puntos**: 100 puntos = $10 MXN descuento
- ✅ Historial completo de movimientos
- ✅ Estadísticas del programa (clientes activos, puntos emitidos/canjeados)
- ✅ Validaciones: monto mínimo $10, puntos mínimos canje 100

**Reglas de Negocio**:
```java
// Acumulación
puntosganados = montoCompra / 10  // 1 punto por cada $10

// Canje
descuento = puntosCanjeados / 10  // 100 puntos = $10 descuento
```

### 🔮 11. Módulo de Predicción de Demanda

**Controller**: `PrediccionDemandaController`

**Funcionalidades Core**:
- ✅ **Algoritmos de forecasting**:
  - Promedio Móvil Simple
  - Promedio Móvil Ponderado
  - Suavización Exponencial
  - Regresión Lineal
- ✅ **Recomendaciones de reorden** automáticas
- ✅ Predicciones por producto y sucursal
- ✅ **Evaluación de precisión** del modelo (MAE, MAPE)
- ✅ Datos históricos de demanda

**Algoritmo Promedio Móvil**:
```java
prediccion[t+1] = (venta[t] + venta[t-1] + ... + venta[t-n]) / n
Donde:
- t = periodo actual
- n = ventana de periodos (configurable, default: 3)
```

### 📊 12. Módulo de Métricas

**Controllers**:
- `MetricaFinancieraController` - KPIs financieros
- `MetricaInventarioController` - KPIs de inventario
- `MetricaOperativaController` - KPIs operacionales
- `MetricaVentaClienteController` - KPIs de ventas y clientes

**Funcionalidades Core**:

#### **Métricas Financieras**:
- Ingresos totales
- Gastos totales
- Utilidad bruta/neta
- Margen de utilidad %
- ROI (Return on Investment)
- Cuentas por cobrar vencidas

#### **Métricas de Inventario**:
- Rotación de inventario
- Días de inventario disponible
- Stock crítico (productos bajo mínimo)
- Valor total del inventario
- Productos obsoletos o lento movimiento
- Índice de caducidad

#### **Métricas Operativas**:
- Total de ventas (cantidad y monto)
- Total de compras (cantidad y monto)
- Total de traspasos
- Eficiencia operativa
- Balance de transacciones
- Actividad por sucursal

#### **Métricas de Ventas y Clientes**:
- Ticket promedio
- Clientes activos vs nuevos
- Top 10 productos más vendidos
- Top 10 clientes por valor
- Métodos de pago más usados
- Tasa de devolución

**Actualización**: Algunas métricas se calculan **en tiempo real** consultando directamente las transacciones, otras se **cachean** por rendimiento.

### 💸 13. Módulo de Rentabilidad

**Controller**: `RentabilidadController`

**Funcionalidades Core**:
- ✅ **Análisis de rentabilidad** por producto
- ✅ Rentabilidad por categoría
- ✅ Rentabilidad por sucursal
- ✅ **Top productos rentables** vs **productos con pérdida**
- ✅ Análisis de márgenes reales vs objetivo
- ✅ ROI por producto
- ✅ Comparación de periodos

**Cálculo de Rentabilidad**:
```
Costo Total = Precio Compra × Cantidad Vendida
Ingreso Total = Precio Venta × Cantidad Vendida
Utilidad Bruta = Ingreso Total - Costo Total
Margen % = (Utilidad Bruta / Ingreso Total) × 100
ROI = (Utilidad Bruta / Costo Total) × 100
```

---

## 6. API REST - Endpoints

### 📡 Estructura de Endpoints

**Base URL**: `http://localhost:8080/api/v1`

**Total de Endpoints**: 250+

### 🔑 Autenticación

```
POST   /auth/login                    # Login y obtener JWT token
```

### 👤 Usuarios

```
GET    /usuarios                      # Listar usuarios
POST   /usuarios                      # Crear usuario
GET    /usuarios/{id}                 # Obtener usuario
PUT    /usuarios/{id}                 # Actualizar usuario
DELETE /usuarios/{id}                 # Eliminar usuario
```

### 📦 Catálogo - Categorías

```
GET    /catalogo/categorias           # Listar
POST   /catalogo/categorias           # Crear
GET    /catalogo/categorias/{id}      # Obtener
PUT    /catalogo/categorias/{id}      # Actualizar
DELETE /catalogo/categorias/{id}      # Eliminar
```

### 👥 Catálogo - Clientes

```
GET    /catalogo/clientes             # Listar (paginado)
POST   /catalogo/clientes             # Crear
GET    /catalogo/clientes/{id}        # Obtener
GET    /catalogo/clientes/rfc/{rfc}   # Buscar por RFC
PUT    /catalogo/clientes/{id}        # Actualizar
DELETE /catalogo/clientes/{id}        # Eliminar
```

### 🏍️ Catálogo - Motos

```
GET    /catalogo/motos                # Listar
POST   /catalogo/motos                # Crear
GET    /catalogo/motos/{id}           # Obtener
PUT    /catalogo/motos/{id}           # Actualizar
DELETE /catalogo/motos/{id}           # Eliminar
GET    /catalogo/motos?marca=Honda    # Filtrar por marca
```

### 🔗 Catálogo - Compatibilidad

```
GET    /catalogo/compatibilidad                  # Listar
POST   /catalogo/compatibilidad                  # Crear relación
GET    /catalogo/compatibilidad/producto/{id}    # Por producto
GET    /catalogo/compatibilidad/moto/{id}        # Por moto
DELETE /catalogo/compatibilidad/producto/{pid}/moto/{mid}  # Eliminar
```

### 📦 Inventario - Productos

```
GET    /productos                     # Listar (paginado)
GET    /productos/search              # Búsqueda omnicanal
POST   /productos                     # Crear
GET    /productos/{id}                # Obtener
GET    /productos/sku/{sku}           # Por SKU
PUT    /productos/{id}                # Actualizar
DELETE /productos/{id}                # Eliminar
```

### 📊 Inventario - Gestión

```
GET    /inventario/sucursal/{sid}/producto/{pid}  # Stock actual
POST   /inventario/entrada                        # Entrada de stock
POST   /inventario/salida                         # Salida de stock
GET    /inventario/movimientos/producto/{id}      # Historial
GET    /inventario/movimientos/sucursal/{id}      # Por sucursal
```

### 🔄 Inventario - Traspasos

```
GET    /inventario/traspasos          # Listar
POST   /inventario/traspasos          # Crear traspaso
GET    /inventario/traspasos/{id}     # Obtener
PUT    /inventario/traspasos/{id}/confirmar  # Confirmar recepción
PUT    /inventario/traspasos/{id}/cancelar   # Cancelar
```

### ⚠️ Inventario - Análisis

```
POST   /inventario/analisis-abc/generar          # Generar ABC
GET    /inventario/analisis-abc                  # Listar análisis
GET    /inventario/analisis-abc/{id}             # Obtener
GET    /inventario/analisis-abc/{id}/productos/{clase}  # Por clase A/B/C

POST   /inventario/alertas-lento-movimiento/generar  # Generar alertas
GET    /inventario/alertas-lento-movimiento      # Listar alertas
PUT    /inventario/alertas-lento-movimiento/{id}/resolver  # Resolver

GET    /inventario/caducidad/proximos            # Próximos a caducar
GET    /inventario/caducidad/caducados           # Ya caducados
POST   /inventario/caducidad/registrar-baja      # Baja por caducidad
```

### 🛒 Ventas

```
GET    /ventas                        # Listar
POST   /ventas                        # Crear venta
GET    /ventas/{id}                   # Obtener
GET    /ventas/sucursal/{id}          # Por sucursal
GET    /ventas/cliente/{id}           # Por cliente
GET    /ventas/empleado/{id}          # Por empleado
PUT    /ventas/{id}/cancelar          # Cancelar venta
```

### 📝 Ventas - Reservas

```
GET    /ventas/reservas               # Listar
POST   /ventas/reservas               # Crear reserva
GET    /ventas/reservas/{id}          # Obtener
PUT    /ventas/reservas/{id}/entregar # Entregar
PUT    /ventas/reservas/{id}/cancelar # Cancelar
```

### 🔙 Ventas - Devoluciones

```
GET    /ventas/devoluciones           # Listar
POST   /ventas/devoluciones           # Crear devolución
GET    /ventas/devoluciones/{id}      # Obtener
PUT    /ventas/devoluciones/{id}/aprobar  # Aprobar
```

### 📥 Compras

```
GET    /compras                       # Listar
POST   /compras                       # Crear orden
GET    /compras/{id}                  # Obtener
GET    /compras/proveedor/{id}        # Por proveedor
PUT    /compras/{id}/recibir          # Confirmar recepción
```

### 📋 Cotizaciones

```
GET    /cotizaciones                  # Listar
POST   /cotizaciones                  # Crear
GET    /cotizaciones/{id}             # Obtener
POST   /cotizaciones/{id}/convertir-venta  # Convertir a venta
```

### 💰 Comisiones

```
POST   /comisiones/calcular           # Calcular comisiones
GET    /comisiones                    # Listar
GET    /comisiones/empleado/{id}      # Por empleado
GET    /comisiones/reporte            # Reporte general
PUT    /comisiones/{id}/aprobar       # Aprobar comisión
```

### 💳 Finanzas - Crédito

```
GET    /credito/cliente/{id}          # Límite de crédito
POST   /credito/cliente/{id}/limite   # Asignar límite
GET    /credito/cuentas-cobrar        # Cuentas por cobrar
GET    /credito/cliente/{id}/saldo    # Saldo actual
POST   /credito/cliente/{id}/pago     # Registrar pago
```

### 📊 Finanzas - Dashboard

```
GET    /finanzas/dashboard            # Dashboard general
GET    /finanzas/dashboard/mes-actual # Métricas del mes
GET    /finanzas/dashboard/comparativo  # Comparar periodos
GET    /finanzas/dashboard/indicadores  # Indicadores clave
```

### 🎁 Fidelidad

```
POST   /fidelidad/programa            # Crear programa
GET    /fidelidad/programa/cliente/{id}  # Consultar por cliente
POST   /fidelidad/acumular            # Acumular puntos
POST   /fidelidad/canjear             # Canjear puntos
GET    /fidelidad/programa/cliente/{id}/historial  # Historial
GET    /fidelidad/estadisticas        # Estadísticas generales
```

### 🔮 Predicción de Demanda

```
POST   /prediccion-demanda/generar    # Generar predicción
GET    /prediccion-demanda            # Listar
GET    /prediccion-demanda/producto/{id}  # Por producto
GET    /prediccion-demanda/recomendacion/producto/{pid}/sucursal/{sid}  # Recomendación
GET    /prediccion-demanda/precision  # Precisión del modelo
```

### 📊 Métricas

```
GET    /metricas-financieras          # KPIs financieros
GET    /metricas/inventario           # KPIs inventario
GET    /metricas/operativas           # KPIs operativos
GET    /metricas/ventas               # KPIs ventas
GET    /metricas/clientes             # KPIs clientes
GET    /metricas/clientes/top         # Top clientes
```

### 💸 Rentabilidad

```
GET    /rentabilidad/producto/{id}    # Por producto
GET    /rentabilidad/categoria/{id}   # Por categoría
GET    /rentabilidad/sucursal/{id}    # Por sucursal
GET    /rentabilidad/top-productos    # Top rentables
GET    /rentabilidad/analisis-margenes  # Análisis de márgenes
```

**Documentación Interactiva**: Todos los endpoints están documentados en **Swagger UI** disponible en `http://localhost:8080/swagger-ui.html`

**Archivo de CURLs**: Ver `CURL-COMPLETO.md` para ejemplos completos de cada endpoint.

---

## 7. Seguridad y Autenticación

### 🔒 Arquitectura de Seguridad

El sistema utiliza **JWT (JSON Web Tokens)** para autenticación stateless.

#### **Flujo de Autenticación**

```
1. Cliente → POST /auth/login {username, password}
2. Backend verifica credenciales contra BD
3. Backend genera JWT firmado con secret key
4. Backend retorna token al cliente
5. Cliente guarda token (localStorage/sessionStorage)
6. Cliente incluye token en cada request:
   Header: Authorization: Bearer {token}
7. JwtAuthenticationFilter intercepta y valida token
8. Si válido, carga usuario en SecurityContext
9. Controller verifica permisos con @PreAuthorize
```

### 🔑 Generación de Token JWT

**Clase**: `JwtUtil.java`

```java
public String generateToken(String username) {
    return Jwts.builder()
        .setSubject(username)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000))  // 24 horas
        .signWith(getSigningKey(), SignatureAlgorithm.HS256)
        .compact();
}
```

**Campos del Token**:
- `sub`: Username del usuario
- `iat`: Fecha de emisión
- `exp`: Fecha de expiración (24 horas)
- **Firma**: HMAC-SHA256 con secret key

### 🔐 Roles y Permisos

**Roles Disponibles**:
- `ROLE_ADMIN`: Acceso completo (crear/editar/eliminar)
- `ROLE_USER`: Acceso limitado (solo lectura y ventas)

**Uso en Controllers**:
```java
@PreAuthorize("hasRole('ADMIN')")
@DeleteMapping("/{id}")
public ResponseEntity<?> eliminar(@PathVariable Integer id) {
    // Solo admin puede eliminar
}

@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
@GetMapping
public ResponseEntity<?> listar() {
    // Ambos roles pueden listar
}
```

### 🛡️ Configuración de Seguridad

**Clase**: `SecurityConfig.java`

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())  // API REST no necesita CSRF
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/v1/auth/**").permitAll()  // Login público
            .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()  // Swagger público
            .anyRequest().authenticated()  // Todo lo demás requiere auth
        )
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)  // No sessions
        )
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    
    return http.build();
}
```

### 🔒 Encriptación de Contraseñas

**Algoritmo**: BCrypt con salt automático

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}

// Uso
String hashedPassword = passwordEncoder.encode("admin123");
boolean matches = passwordEncoder.matches("admin123", hashedPassword);
```

### ⚠️ Manejo de Excepciones de Seguridad

**Clase**: `GlobalExceptionHandler.java`

```java
@ExceptionHandler(BadCredentialsException.class)
public ResponseEntity<ApiErrorResponse> handleBadCredentials(BadCredentialsException ex) {
    return buildErrorResponse(
        HttpStatus.UNAUTHORIZED, 
        "Credenciales Invalidas", 
        "El usuario o la contraseña son incorrectos"
    );
}

@ExceptionHandler(AccessDeniedException.class)
public ResponseEntity<ApiErrorResponse> handleAccessDenied(AccessDeniedException ex) {
    return buildErrorResponse(
        HttpStatus.FORBIDDEN, 
        "Acceso Denegado", 
        "No tiene permisos para realizar esta operación"
    );
}
```

### 🔐 Mejores Prácticas Implementadas

✅ Contraseñas hasheadas con BCrypt (nunca plain text)  
✅ Tokens JWT firmados y con expiración  
✅ Validación de roles en cada endpoint sensible  
✅ CORS configurado correctamente  
✅ CSRF deshabilitado (apropiado para API REST)  
✅ Sesiones stateless  
✅ Secret key externalizada en application.yml  
✅ Logs de intentos fallidos de autenticación

---

## 8. Métricas y Analítica

### 📊 Sistema de Métricas

El sistema genera y almacena métricas en 4 categorías:

```
1. Métricas Financieras (metrica_financiera)
2. Métricas de Inventario (metrica_inventario)
3. Métricas Operativas (metrica_operativa)
4. Métricas de Venta y Cliente (metrica_venta_cliente)
```

### 💰 Métricas Financieras

**Generación**: Diaria/Semanal/Mensual

**KPIs Calculados**:
```
• Ingresos Totales
• Gastos Totales (Compras)
• Utilidad Bruta
• Margen de Utilidad %
• ROI %
• Cuentas por Cobrar
• Cuentas por Cobrar Vencidas
• Índice de Liquidez
```

**Fórmulas**:
```
Utilidad Bruta = Ingresos - Gastos
Margen % = (Utilidad / Ingresos) × 100
ROI % = (Utilidad / Gastos) × 100
Liquidez = Efectivo Disponible / Cuentas por Cobrar Vencidas
```

### 📦 Métricas de Inventario

**KPIs Calculados**:
```
• Rotación de Inventario
• Días de Inventario Disponible
• Valor Total del Inventario
• Productos en Stock Crítico (bajo mínimo)
• Productos Obsoletos
• Índice de Caducidad
```

**Fórmulas**:
```
Rotación = Costo de Ventas / Inventario Promedio
Días Inventario = 365 / Rotación
Valor Inventario = Σ (Stock × PrecioCompra)
Stock Crítico = Productos donde Stock < StockMinimo
```

### 🏭 Métricas Operativas

**KPIs Calculados**:
```
• Total Ventas (cantidad y monto)
• Total Compras (cantidad y monto)
• Total Traspasos
• Eficiencia Operativa %
• Balance de Transacciones
• Actividad por Sucursal
```

**Fórmulas**:
```
Eficiencia = (Ventas / (Ventas + Compras + Traspasos)) × 100
Balance = Entradas - Salidas
```

### 👥 Métricas de Venta y Cliente

**KPIs Calculados**:
```
• Ticket Promedio
• Clientes Activos
• Clientes Nuevos
• Top 10 Productos Vendidos
• Top 10 Clientes por Valor
• Métodos de Pago (distribución)
• Tasa de Devolución %
• Frecuencia de Compra
```

**Fórmulas**:
```
Ticket Promedio = Total Ventas / Número de Ventas
Tasa Devolución = (Ventas Devueltas / Total Ventas) × 100
Frecuencia = Compras Cliente / Meses Activo
```

### 📈 Visualización de Métricas

**Endpoints de Dashboard**:
```
GET /finanzas/dashboard               # Vista general
GET /finanzas/dashboard/mes-actual    # Mes en curso
GET /finanzas/dashboard/compar ativo  # Comparación periodos
```

**Formato de Respuesta**:
```json
{
  "exitoso": true,
  "data": {
    "periodo": "2026-03",
    "ingresos": 125000.00,
    "gastos": 75000.00,
    "utilidad": 50000.00,
    "margen": 40.0,
    "roi": 66.67,
    "ventasTotales": 450,
    "ticketPromedio": 277.78,
    "clientesActivos": 85,
    "rotacionInventario": 6.5,
    "diasInventario": 56
  }
}
```

### 📊 Análisis Avanzados

#### **Análisis ABC de Inventario**

Clasifica productos según la regla de Pareto (80/20):

```
Clase A: 20% de productos → 80% del valor
Clase B: 30% de productos → 15% del valor
Clase C: 50% de productos → 5% del valor
```

**Uso**:
- Productos Clase A → Control estricto, reorden frecuente
- Productos Clase B → Control normal
- Productos Clase C → Control básico, revisar obsoletos

#### **Análisis de Rentabilidad**

Calcula rentabilidad real por:
- Producto individual
- Categoría completa
- Sucursal
- Periodo de tiempo

**Incluye**:
- Margen bruto
- ROI real
- Comparación vs margen objetivo
- Identificación de productos con pérdida

---

## 9. Testing y Calidad

### 🧪 Estrategia de Testing

**Cobertura**: **100% de tests pasando (790/790)**

#### **Tipos de Tests Implementados**

1. **Tests Unitarios** (Service Layer)
   - Mockito para dependencies
   - JUnit 5 (Jupiter)
   - Tests aislados por método
   - Cobertura: ~60% de líneas de código

2. **Tests de Integración** (Controller Layer)
   - @SpringBootTest
   - @AutoConfigureMockMvc
   - MockMvc para simular HTTP requests
   - Base de datos H2 en memoria
   - @Transactional para rollback automático

3. **Tests de Entidades** (Entity Layer)
   - Validaciones Jakarta
   - Relaciones JPA
   - Métodos helper (@PrePersist, etc.)

### 📁 Estructura de Tests

```
app/src/test/java/com/nexoohub/almacen/
├── catalogo/
│   ├── controller/
│   │   ├── CategoriaControllerIntegrationTest.java
│   │   ├── ClienteControllerIntegrationTest.java
│   │   └── ...
│   ├── service/
│   │   ├── CategoriaServiceTest.java
│   │   └── ...
│   └── entity/
│       ├── CategoriaTest.java
│       └── ...
├── inventario/
│   ├── controller/
│   ├── service/
│   └── entity/
├── ventas/
├── finanzas/
├── fidelidad/
└── ... (todos los módulos)
```

### 🎯 Ejemplo de Test de Integración

```java
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ProductoControllerIntegrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    private String token;
    
    @BeforeEach
    void setUp() {
        // Generar token JWT para tests
        Usuario admin = new Usuario();
        admin.setUsername("admin");
        admin.setRole("ROLE_ADMIN");
        token = jwtUtil.generateToken(admin.getUsername());
    }
    
    @Test
    void crearProducto_Exitoso() throws Exception {
        // Given
        String requestBody = """
            {
                "skuInterno": "TEST-001",
                "nombre": "Producto Test",
                "categoriaId": 1,
                "proveedorId": 1,
                "precioCompra": 100.00,
                "precioVenta": 150.00,
                "stockMinimo": 10,
                "stockMaximo": 100,
                "unidadMedida": "UNIDAD"
            }
            """;
        
        // When & Then
        mockMvc.perform(post("/api/v1/productos")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.exitoso").value(true))
            .andExpect(jsonPath("$.data.skuInterno").value("TEST-001"))
            .andExpect(jsonPath("$.data.precioVenta").value(150.00));
    }
    
    @Test
    void buscarProductos_ConFiltros() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/productos/search")
                .header("Authorization", "Bearer " + token)
                .param("q", "aceite")
                .param("categoriaId", "1")
                .param("page", "0")
                .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.content").isArray())
            .andExpect(jsonPath("$.data.totalElements").isNumber());
    }
}
```

### 🎯 Ejemplo de Test Unitario

```java
@ExtendWith(MockitoExtension.class)
class VentaServiceTest {
    
    @Mock
    private VentaRepository ventaRepository;
    
    @Mock
    private ClienteRepository clienteRepository;
    
    @Mock
    private ProductoRepository productoRepository;
    
    @InjectMocks
    private VentaService ventaService;
    
    @Test
    void crearVenta_ConClienteValido_Exitoso() {
        // Given
        Cliente cliente = new Cliente();
        cliente.setId(1);
        cliente.setNombre("Juan Test");
        
        when(clienteRepository.findById(1)).thenReturn(Optional.of(cliente));
        when(productoRepository.findById(any())).thenReturn(Optional.of(new Producto()));
        when(ventaRepository.save(any())).thenAnswer(i -> {
            Venta v = i.getArgument(0);
            v.setId(1);
            return v;
        });
        
        CrearVentaDTO dto = new CrearVentaDTO(
            1, 1, 1, "EFECTIVO",
            List.of(new DetalleVentaDTO(1, 2, 100.00, 0.0)),
            null
        );
        
        // When
        VentaResponseDTO result = ventaService.crearVenta(dto);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.id());
        verify(ventaRepository, times(1)).save(any(Venta.class));
    }
    
    @Test
    void crearVenta_ClienteNoExiste_LanzaExcepcion() {
        // Given
        when(clienteRepository.findById(999)).thenReturn(Optional.empty());
        
        CrearVentaDTO dto = new CrearVentaDTO(
            1, 999, 1, "EFECTIVO",
            List.of(new DetalleVentaDTO(1, 2, 100.00, 0.0)),
            null
        );
        
        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            ventaService.crearVenta(dto);
        });
        
        verify(ventaRepository, never()).save(any());
    }
}
```

### 📊 Cobertura de Tests

**Jacoco** genera reportes de cobertura automáticamente:

```bash
./gradlew test  # Ejecuta tests y genera reporte

# Reporte ubicado en:
# app/build/reports/jacoco/test/html/index.html
```

**Configuración en build.gradle.kts**:
```kotlin
tasks.test {
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}
```

### ✅ Estado del Testing

| Módulo | Tests | Estado |
|--------|-------|--------|
| Catálogo | 125 | ✅ 100% |
| Inventario | 180 | ✅ 100% |
| Ventas | 95 | ✅ 100% |
| Compras | 42 | ✅ 100% |
| Finanzas | 78 | ✅ 100% |
| Comisiones | 35 | ✅ 100% |
| Fidelidad | 42 | ✅ 100% |
| Predicción | 38 | ✅ 100% |
| Métricas | 95 | ✅ 100% |
| Rentabilidad | 30 | ✅ 100% |
| Otros | 30 | ✅ 100% |
| **TOTAL** | **790** | **✅ 100%** |

### 🚀 Ejecución de Tests

```bash
# Ejecutar todos los tests
./gradlew test

# Ejecutar tests de un módulo específico
./gradlew test --tests "*CategoriaControllerIntegrationTest"

# Ejecutar tests con cobertura
./gradlew test jacocoTestReport

# Ver resultados
cat app/build/test-results/test/*.xml | grep "tests="
```

---

## 10. Despliegue

### 🚀 Opciones de Despliegue

#### **Opción 1: JAR Ejecutable (Recomendado)**

```bash
# 1. Compilar proyecto
./gradlew clean build

# 2. JAR generado en:
# app/build/libs/app.jar

# 3. Ejecutar
java -jar app/build/libs/app.jar \
  --spring.profiles.active=prod \
  --spring.datasource.url=jdbc:postgresql://localhost:5432/nexoohub \
  --spring.datasource.username=nexoohub_user \
  --spring.datasource.password=secure_password_here
```

#### **Opción 2: Docker Container**

**Dockerfile**:
```dockerfile
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY app/build/libs/app.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
```

**docker-compose.yml** (con PostgreSQL):
```yaml
version: '3.8'

services:
  postgres:
    image: postgres:15-alpine
    environment:
      POSTGRES_DB: nexoohub
      POSTGRES_USER: nexoohub_user
      POSTGRES_PASSWORD: secure_password
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
  
  app:
    build: .
    environment:
      SPRING_PROFILES_ACTIVE: prod
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/nexoohub
      SPRING_DATASOURCE_USERNAME: nexoohub_user
      SPRING_DATASOURCE_PASSWORD: secure_password
    ports:
      - "8080:8080"
    depends_on:
      - postgres

volumes:
  postgres_data:
```

**Comandos**:
```bash
# Build y levantar servicios
docker-compose up --build

# Detener
docker-compose down
```

#### **Opción 3: Kubernetes**

**deployment.yaml**:
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: nexoohub-almacen
spec:
  replicas: 3
  selector:
    matchLabels:
      app: nexoohub-almacen
  template:
    metadata:
      labels:
        app: nexoohub-almacen
    spec:
      containers:
      - name: nexoohub-almacen
        image: nexoohub/almacen:1.0.0
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "prod"
        - name: SPRING_DATASOURCE_URL
          valueFrom:
            secretKeyRef:
              name: db-secret
              key: url
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 60
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 5
```

### ⚙️ Configuración por Ambiente

**application.yml** (base):
```yaml
spring:
  application:
    name: nexoohub-almacen
  
  jpa:
    show-sql: false
    hibernate:
      ddl-auto: validate  # Flyway controla el schema
  
  flyway:
    enabled: true
    baseline-on-migrate: true

server:
  port: 8080

jwt:
  secret: ${JWT_SECRET:changeme_in_production}
  expiration: 86400000  # 24 horas
```

**application-dev.yml**:
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:dev db;MODE=PostgreSQL
    driver-class-name: org.h2.Driver
  
  jpa:
    show-sql: true
  
  h2:
    console:
      enabled: true

logging:
  level:
    com.nexoohub.almacen: DEBUG
```

**application-prod.yml**:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5432}/${DB_NAME:nexoohub}
    username: ${DB_USER}
    password: ${DB_PASSWORD}
    driver-class-name: org.postgresql.Driver
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
  
  jpa:
    show-sql: false
    properties:
      hibernate:
        format_sql: false
        use_sql_comments: false

logging:
  level:
    com.nexoohub.almacen: INFO
    org.springframework: WARN

management:
  endpoints:
    web:
      exposure:
        include: health,metrics,info
```

### 🗄️ Backup de Base de Datos

**Script de Backup Automatizado**:
```bash
#!/bin/bash
# backup-db.sh

TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
BACKUP_DIR="/backups/nexoohub"
DB_NAME="nexoohub"
DB_USER="nexoohub_user"

mkdir -p $BACKUP_DIR

# Backup completo
pg_dump -U $DB_USER -d $DB_NAME -F c -b -v \
  -f "$BACKUP_DIR/nexoohub_$TIMESTAMP.backup"

# Comprimir
gzip "$BACKUP_DIR/nexoohub_$TIMESTAMP.backup"

# Limpiar backups antiguos (> 30 días)
find $BACKUP_DIR -name "*.backup.gz" -mtime +30 -delete

echo "✅ Backup completado: nexoohub_$TIMESTAMP.backup.gz"
```

**Cron Job** (diario a las 2 AM):
```bash
crontab -e

0 2 * * * /usr/local/bin/backup-db.sh >> /var/log/nexoohub-backup.log 2>&1
```

### 📊 Monitoreo y Health Checks

**Spring Boot Actuator** expone endpoints de monitoreo:

```bash
# Health check
curl http://localhost:8080/actuator/health

# Métricas
curl http://localhost:8080/actuator/metrics

# Info de aplicación
curl http://localhost:8080/actuator/info
```

**Configuración de Alertas** (ejemplo con Prometheus):
```yaml
# prometheus.yml
scrape_configs:
  - job_name: 'nexoohub-almacen'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['localhost:8080']
```

---

## 11. Guía de Desarrollo

### 🛠️ Setup de Entorno Local

#### **Paso 1: Prerrequisitos**

```bash
# Verificar versiones
java -version  # Debe ser 17+
./gradlew --version  # Gradle 8.7+
psql --version  # PostgreSQL 15+ (opcional para dev)
```

#### **Paso 2: Clonar Repositorio**

```bash
git clone https://github.com/nexoohub/almacen.git
cd almacen
```

#### **Paso 3: Configurar Base de Datos (Dev)**

**Opción A: H2 en memoria (por defecto)**
- No requiere configuración adicional
- Base de datos se crea automáticamente al iniciar
- Datos se pierden al detener la aplicación

**Opción B: PostgreSQL local**

```bash
# Crear BD
createdb nexoohub

# Crear usuario
psql -c "CREATE USER nexoohub_user WITH PASSWORD 'dev123';"
psql -c "GRANT ALL PRIVILEGES ON DATABASE nexoohub TO nexoohub_user;"

# Editar application-dev.yml para usar PostgreSQL
```

#### **Paso 4: Compilar Proyecto**

```bash
./gradlew clean build
```

#### **Paso 5: Ejecutar Aplicación**

```bash
# Modo desarrollo (H2 en memoria)
./gradlew bootRun --args='--spring.profiles.active=dev'

# O con PostgreSQL
./gradlew bootRun --args='--spring.profiles.active=dev,postgres'
```

**Aplicación iniciará en**: `http://localhost:8080`

#### **Paso 6: Acceder a Recursos**

```bash
# Swagger UI
http://localhost:8080/swagger-ui.html

# H2 Console (si usa H2)
http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:devdb
User: sa
Password: (dejar vacío)

# Login de prueba
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

### 🔧 Convenciones de Código

#### **Estructura de Paquetes**

```
com.nexoohub.almacen/
├── common/                   # Config, seguridad, utilidades
│   ├── config/
│   ├── controller/
│   ├── entity/
│   ├── exception/
│   ├── repository/
│   └── service/
├── catalogo/                 # Módulo de catálogos
│   ├── controller/
│   ├── dto/
│   ├── entity/
│   ├── repository/
│   └── service/
├── inventario/               # Módulo de inventario
├── ventas/                   # Módulo de ventas
├── compras/                  # Módulo de compras
└── ... (otros módulos)
```

#### **Nomenclatura**

**Clases**:
```java
// Controller: *Controller
public class ProductoController {}

// Service: *Service
public class ProductoService {}

// Repository: *Repository
public interface ProductoRepository extends JpaRepository {}

// Entity: singular, sin sufijo
public class Producto {}

// DTO: *DTO
public record ProductoResponseDTO() {}
public record CrearProductoDTO() {}

// Exception: *Exception
public class BusinessException extends RuntimeException {}
```

**Métodos**:
```java
// CRUD operations
public ProductoResponseDTO crear(...) {}
public ProductoResponseDTO actualizar(...) {}
public void eliminar(...) {}
public ProductoResponseDTO buscarPorId(...) {}
public Page<ProductoResponseDTO> listar(...) {}

// Business logic
public void calcularStock(...) {}
public BigDecimal obtenerPrecioVigente(...) {}
public boolean validarDisponibilidad(...) {}
```

**Variables y Parámetros**:
```java
// camelCase
Integer productoId;
BigDecimal precioVenta;
LocalDateTime fechaCreacion;

// Collections plurales
List<Producto> productos;
Set<Integer> categoriaIds;
```

#### **Anotaciones Comunes**

**Entities**:
```java
@Entity
@Table(name = "producto_maestro")
@Getter @Setter @NoArgsConstructor  // Lombok
public class Producto {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "sku_interno", nullable = false, unique = true, length = 50)
    @NotBlank(message = "SKU es obligatorio")
    private String skuInterno;
    
    @Column(name = "precio_venta", nullable = false)
    @NotNull(message = "Precio de venta es obligatorio")
    @Min(value = 0, message = "Precio no puede ser negativo")
    private BigDecimal precioVenta;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;
    
    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL)
    private List<InventarioSucursal> inventarios;
    
    @PrePersist
    protected  void onCreate() {
        fechaCreacion = LocalDateTime.now();
    }
}
```

**Services**:
```java
@Service
@Transactional
@RequiredArgsConstructor // Lombok inyección por constructor
@Slf4j  // Logging
public class ProductoService {
    
    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    
    public ProductoResponseDTO crear(CrearProductoDTO dto) {
        // Validaciones
        if (productoRepository.existsBySkuInterno(dto.skuInterno())) {
            throw new DuplicateResourceException("SKU ya existe");
        }
        
        Categoria categoria = categoriaRepository.findById(dto.categoriaId())
            .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));
        
        // Lógica de negocio
        Producto producto = new Producto();
        producto.setSkuInterno(dto.skuInterno());
        producto.setNombre(dto.nombre());
        producto.setCategoria(categoria);
        // ... más setters
        
        Producto saved = productoRepository.save(producto);
        
        log.info("Producto creado: {} (ID: {})", saved.getSkuInterno(), saved.getId());
        
        return mapToDTO(saved);
    }
    
    @Transactional(readOnly = true)
    public ProductoResponseDTO buscarPorId(Integer id) {
        Producto producto = productoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado: " + id));
        return mapToDTO(producto);
    }
}
```

**Controllers**:
```java
@RestController
@RequestMapping("/api/v1/productos")
@RequiredArgsConstructor
@Tag(name = "Productos", description = "Gestión del catálogo de productos")
public class ProductoController {
    
    private final ProductoService productoService;
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Crear producto", description = "Crea un nuevo producto en el catálogo")
    @ApiResponse(responseCode = "201", description = "Producto creado exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos inválidos")
    @ApiResponse(responseCode = "409", description = "SKU duplicado")
    public ResponseEntity<ApiResponse<ProductoResponseDTO>> crear(
            @Valid @RequestBody CrearProductoDTO dto) {
        
        ProductoResponseDTO producto = productoService.crear(dto);
        
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.success("Producto creado exitosamente", producto));
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Buscar producto por ID")
    public ResponseEntity<ApiResponse<ProductoResponseDTO>> buscarPorId(
            @PathVariable Integer id) {
        
        ProductoResponseDTO producto = productoService.buscarPorId(id);
        
        return ResponseEntity.ok(ApiResponse.success("Producto encontrado", producto));
    }
}
```

### 📝 Crear un Nuevo Módulo

**Ejemplo: Módulo de Promociones**

#### **Paso 1: Crear estructura de paquetes**

```
src/main/java/com/nexoohub/almacen/promociones/
├── controller/
│   └── PromocionController.java
├── dto/
│   ├── PromocionResponseDTO.java
│   └── CrearPromocionDTO.java
├── entity/
│   └── Promocion.java
├── repository/
│   └── PromocionRepository.java
└── service/
    └── PromocionService.java
```

#### **Paso 2: Crear Entity**

```java
package com.nexoohub.almacen.promociones.entity;

@Entity
@Table(name = "promocion")
@Getter @Setter @NoArgsConstructor
public class Promocion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false)
    @NotBlank
    private String nombre;
    
    @Column(nullable = false)
    @NotNull
    private BigDecimal descuento;
    
    @Column(name = "fecha_inicio", nullable = false)
    @NotNull
    private LocalDate fechaInicio;
    
    @Column(name = "fecha_fin", nullable = false)
    @NotNull
    private LocalDate fechaFin;
    
    // ... más campos y relaciones
}
```

#### **Paso 3: Crear Migración Flyway**

`src/main/resources/db/migration/V11__create_promociones.sql`:

```sql
CREATE TABLE promocion (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descuento DECIMAL(5,2) NOT NULL CHECK (descuento >= 0 AND descuento <= 100),
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT check_fechas CHECK (fecha_fin >= fecha_inicio)
);

CREATE INDEX idx_promocion_fechas ON promocion(fecha_inicio, fecha_fin);
CREATE INDEX idx_promocion_activo ON promocion(activo);
```

#### **Paso 4: Crear DTOs**

```java
public record PromocionResponseDTO(
    Integer id,
    String nombre,
    BigDecimal descuento,
    LocalDate fechaInicio,
    LocalDate fechaFin,
    Boolean activo
) {}

public record CrearPromocionDTO(
    @NotBlank String nombre,
    @NotNull @Min(0) @Max(100) BigDecimal descuento,
    @NotNull LocalDate fechaInicio,
    @NotNull LocalDate fechaFin
) {}
```

#### **Paso 5: Crear Repository, Service y Controller**

Seguir patrones de módulos existentes.

#### **Paso 6: Crear Tests**

```java
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class PromocionControllerIntegrationTest {
    // ... tests
}
```

#### **Paso 7: Documentar en OpenAPI YAML**

`recursos/promocion-controller.yaml`:

```yaml
openapi: 3.0.0
info:
  title: API de Promociones
  version: 1.0.0
paths:
  /api/v1/promociones:
    get:
      summary: Listar promociones
      # ... resto de la documentación
```

### 🔄 Workflow de Git

```bash
# 1. Crear branch para feature
git checkout -b feature/modulo-promociones

# 2. Hacer cambios y commits
git add .
git commit -m "feat(promociones): agregar módulo de promociones

- Crear entity Promocion
- Implementar CRUD completo
- Agregar validaciones de fechas
- Tests de integración

Closes #45"

# 3. Push a remote
git push origin feature/modulo-promociones

# 4. Crear Pull Request en GitHub

# 5. Después de aprobación, merge a main
git checkout main
git pull origin main
git merge feature/modulo-promociones
git push origin main

# 6. Limpiar branch
git branch -d feature/modulo-promociones
git push origin --delete feature/modulo-promociones
```

**Convención de Commits** (Conventional Commits):
```
feat: Nueva funcionalidad
fix: Corrección de bug
docs: Cambios en documentación
style: Formato de código (sin cambios funcionales)
refactor: Refactorización de código
test: Agregar o modificar tests
chore: Tareas de mantenimiento
```

---

## 12. Troubleshooting

### ❌ Problemas Comunes

#### **1. Tests Fallan: "Usuario no autorizado"**

**Problema**: Tests de integración fallan con 401 Unauthorized

**Causa**: Token JWT no se está enviando correctamente

**Solución**:
```java
// Asegurarse de generar token en @BeforeEach
@BeforeEach
void setUp() {
    Usuario admin = usuarioRepository.save(new Usuario("admin", "admin123", "ROLE_ADMIN"));
    token = jwtUtil.generateToken(admin.getUsername());
}

// Incluir token en cada request
mockMvc.perform(get("/api/v1/productos")
    .header("Authorization", "Bearer " + token))  // ← Crucial
    .andExpect(status().isOk());
```

#### **2. Flyway Migration Error: "Schema already exists"**

**Problema**: Error al iniciar: `Flyway migration failed`

**Causa**: Base de datos ya tiene schema incompatible

**Solución**:
```bash
# Opción 1: Limpiar BD (solo dev)
DROP DATABASE nexoohub;
CREATE DATABASE nexoohub;

# Opción 2: Baseline Flyway
spring.flyway.baseline-on-migrate=true
spring.flyway.baseline-version=0
```

#### **3. Hibernate Error: "identifier of an instance was altered"**

**Problema**: `HibernateException: identifier of an instance of XYZ was altered`

**Causa**: Intentar modificar el ID de una entidad ya persistida

**Solución**:
```java
// ❌ INCORRECTO
producto.setId(nuevoId);  // Nunca modificar ID

// ✅ CORRECTO
Producto nuevo = new Producto();
nuevo.setNombre(producto.getNombre());
// ... copiar otros campos excepto ID
productoRepository.save(nuevo);
```

#### **4. Tests Lentos (> 2 minutos)**

**Problema**: Tests toman mucho tiempo al usar H2

**Causa**: H2 no está en modo PostgreSQL compatible

**Solución**:
```yaml
# application-test.yml
spring:
  datasource:
    url: jdbc:h2:mem:testdb;MODE=PostgreSQL;DB_CLOSE_DELAY=-1
    #                        ^^^^^^^^^^^^^^^^^ Importante
```

#### **5. StackOverflowError en JSON Serialization**

**Problema**: Error al retornar entidades con relaciones bidireccionales

**Causa**: Jackson entra en bucle infinito serializando relaciones

**Solución**:
```java
// Opción 1: Usar DTOs (RECOMENDADO)
return mapToDTO(producto);  // DTO no tiene referencias circulares

// Opción 2: Anotaciones Jackson
@Entity
public class Producto {
    @OneToMany(mappedBy = "producto")
    @JsonManagedReference  // ← En el lado "padre"
    private List<Inventario> inventarios;
}

@Entity
public class Inventario {
    @ManyToOne
    @JsonBackReference  // ← En el lado "hijo"
    private Producto producto;
}

// Opción 3: Ignorar en serialización
@JsonIgnore
private List<Inventario> inventarios;
```

#### **6. "No suitable driver found" en Production**

**Problema**: Error al conectar a PostgreSQL en producción

**Causa**: Driver de PostgreSQL no está en build

**Solución**:
```kotlin
// build.gradle.kts
dependencies {
    runtimeOnly("org.postgresql:postgresql")  // ← Asegurar que esté
}
```

#### **7. Token JWT Expirado**

**Problema**: API retorna 401 después de 24 horas

**Causa**: Token expiró según configuración

**Solución**:
```java
// Cliente debe refrescar token antes de expirar
// O implementar refresh token endpoint

@PostMapping("/auth/refresh")
public ResponseEntity<?> refresh(@RequestHeader("Authorization") String oldToken) {
    String username = jwtUtil.extractUsername(oldToken);
    String newToken = jwtUtil.generateToken(username);
    return ResponseEntity.ok(new AuthResponse(newToken, username, ...));
}
```

### 🔍 Logs Útiles

**Habilitar logs de Hibernate**:
```yaml
logging:
  level:
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE
```

**Ver consultas SQL generadas**:
```yaml
spring:
  jpa:
    show-sql: true
    properties:
      hibernate:
        format_sql: true
        use_sql_comments: true
```

**Logs de Spring Security**:
```yaml
logging:
  level:
    org.springframework.security: DEBUG
```

### 📞 Soporte

**Para issues técnicos**:
- GitHub Issues: https://github.com/nexoohub/almacen/issues
- Email: soporte@nexoohub.com
- Wiki: https://github.com/nexoohub/almacen/wiki

---

## 📚 Referencias

### 📖 Documentación Técnica

- [Spring Boot 3.2 Documentation](https://docs.spring.io/spring-boot/docs/3.2.x/reference/html/)
- [Spring Data JPA](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [Spring Security 6](https://docs.spring.io/spring-security/reference/)
- [Flyway Documentation](https://flywaydb.org/documentation/)
- [JWT (jjwt) GitHub](https://github.com/jwtk/jjwt)
- [Lombok Features](https://projectlombok.org/features/)

### 🗄️ Base de Datos

- [PostgreSQL 15 Documentation](https://www.postgresql.org/docs/15/)
- [H2 Database](https://www.h2database.com/html/main.html)

### 🧪 Testing

- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [Spring Boot Testing](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing)

---

## 📄  Licencia

Este proyecto es propiedad de **NexooHub Development Team**.  
© 2026 NexooHub. Todos los derechos reservados.

---

**Última actualización**: Marzo 12, 2026  
**Versión del documento**: 1.0.0  
**Responsable**: NexooHub Development Team
