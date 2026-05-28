# DOCUMENTACIÓN TÉCNICA COMPLETA — NexooHub Almacén

> **Sistema Integral de Gestión Multi-Sucursal para Refacciones de Motocicletas**  
> Versión: 1.0.0 | Actualización: Mayo 2026 | NexooHub Development Team

---

## Tabla de Contenidos

1. [Resumen Ejecutivo](#1-resumen-ejecutivo)
2. [Arquitectura del Sistema](#2-arquitectura-del-sistema)
3. [Stack Tecnológico](#3-stack-tecnológico)
4. [Estructura del Código Fuente](#4-estructura-del-código-fuente)
5. [Base de Datos — Esquema Completo](#5-base-de-datos--esquema-completo)
6. [Módulos Funcionales y Endpoints Detallados](#6-módulos-funcionales-y-endpoints-detallados)
7. [Seguridad y Autenticación](#7-seguridad-y-autenticación)
8. [Caché y Performance](#8-caché-y-performance)
9. [Notificaciones y Alertas Automáticas](#9-notificaciones-y-alertas-automáticas)
10. [Testing y Calidad](#10-testing-y-calidad)
11. [Despliegue y Configuración Completa](#11-despliegue-y-configuración-completa)
12. [Consideraciones y Deuda Técnica](#12-consideraciones-y-deuda-técnica)

---

## 1. Resumen Ejecutivo

NexooHub Almacén es una API REST construida con Spring Boot 3.2.3 / Java 21 que cubre la operación completa de una cadena de tiendas de refacciones de motocicletas: catálogos, inventario multi-sucursal, ventas, compras, CRM, ERP (contabilidad, logística, nómina), analítica predictiva y reporting financiero.

### Métricas del Proyecto

| Métrica | Valor |
|---|---|
| Módulos de negocio | 21 |
| Controladores REST (`*Controller.java`) | 65 archivos |
| Servicios de negocio | 50+ |
| Entidades JPA | 60+ |
| Migraciones Flyway | 33 (V1–V33) |
| Endpoints documentados | 250+ |
| Tests (JUnit 5 + Jupiter) | 950+ — **100% pasando** |
| Cobertura JaCoCo (core lógico) | **68% instrucciones / 50% ramas** |
| Índices PostgreSQL declarados | **39** (25 en V1 + 14 en V33) |
| Clase principal | `com.nexoohub.almacen.NexooHAlmacenApplication` |

### Respuesta Estándar (Éxito)

La clase `ApiResponse<T>` es un Java record:

```java
public record ApiResponse<T>(
    @JsonProperty("exitoso")   boolean success,
    @JsonProperty("mensaje")   String message,
    @JsonProperty("datos")     T data,
    @JsonProperty("rastreoId") String traceId,   // Inyectado por LogFilter via MDC
    @JsonProperty("fechaHora") LocalDateTime timestamp
)
```

Ejemplo de respuesta:

```json
{
  "exitoso": true,
  "mensaje": "Producto encontrado",
  "datos": { "skuInterno": "ACEITE-CASTROL-10W40-1L", "nombreComercial": "Castrol GTX 10W-40" },
  "rastreoId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "fechaHora": "2026-05-27T10:30:00"
}
```

### Respuesta Estándar (Error)

La clase `ApiErrorResponse` para errores:

```json
{
  "estatus": 404,
  "codigoError": "No encontrado",
  "mensaje": "El SKU ACEITE-CASTROL-10W40-1L no existe.",
  "detalles": null,
  "rastreoId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "fechaHora": "2026-05-27T10:30:00"
}
```

---

## 2. Arquitectura del Sistema

```
┌─────────────────────────────────────────────────────────┐
│                    CLIENTE (HTTP :8080)                  │
└─────────────────────────┬───────────────────────────────┘
                          │
┌─────────────────────────▼───────────────────────────────┐
│              SPRING BOOT 3.2.3 APPLICATION               │
│                                                          │
│  ┌──────────────────────────────────────────────────┐    │
│  │  LogFilter (MDC traceId) + JwtAuthenticationFilter│    │
│  └──────────────────────────────────────────────────┘    │
│  ┌──────────────────────────────────────────────────┐    │
│  │  SecurityFilterChain  (Spring Security 6)         │    │
│  │  BCryptPasswordEncoder + CORS configurable        │    │
│  └──────────────────────────────────────────────────┘    │
│  ┌──────────────────────────────────────────────────┐    │
│  │   @RestController  (65 controladores)             │    │
│  │   Respuesta: ApiResponse<T> / ApiErrorResponse    │    │
│  └──────────────────────┬───────────────────────────┘    │
│  ┌──────────────────────▼───────────────────────────┐    │
│  │   @Service  (lógica de negocio, @Transactional)   │    │
│  │   VentaService, CreditoService, AlertaScheduler…  │    │
│  └──────────────────────┬───────────────────────────┘    │
│  ┌──────────────────────▼───────────────────────────┐    │
│  │   JpaRepository + Specifications + Native SQL     │    │
│  │   H2 (tests) / PostgreSQL 15 (producción)         │    │
│  └──────────────────────────────────────────────────┘    │
│  ┌──────────────────────────────────────────────────┐    │
│  │   Scheduler (@Scheduled) — 4 cron jobs            │    │
│  └──────────────────────────────────────────────────┘    │
└─────────────────────────┬───────────────────────────────┘
                          │ JDBC / PostgreSQL Driver
┌─────────────────────────▼───────────────────────────────┐
│   PostgreSQL 15-alpine  (nexoohub_almacen DB)            │
│   Flyway V1–V33 — ddl-auto: validate                     │
└─────────────────────────────────────────────────────────┘
```

### Patrón de Capas por Módulo

Cada módulo sigue estrictamente el patrón:

```
Controller → Service → Repository (→ Specification si hay búsqueda dinámica)
     ↓            ↓
   DTO          Entity  →  AuditableEntity (fechaCreacion, usuarioCreacion, etc.)
   DTO          Mapper (conversión manual Entity ↔ DTO)
```

- **`AuditableEntity`** (`@MappedSuperclass`): Clase base abstracta con `@EntityListeners(AuditingEntityListener.class)`. Provee 4 campos auditados automáticamente: `fechaCreacion`, `usuarioCreacion`, `fechaActualizacion`, `usuarioActualizacion`. Todos se mapean a columnas de la BD (`fecha_creacion`, `usuario_creacion`, etc.).

- **`ProductoMaestroSpecification`**: Implementa el patrón `Specification<T>` de Spring Data JPA para búsqueda dinámica multi-criterio con hasta 16 parámetros opcionales.

- **`InventarioSucursalId`**: Clave compuesta (`@Embeddable`) para la tabla `inventario_sucursal` (PK: `sucursal_id + sku_interno`).

### Configuración de Seguridad — Clases Clave

| Clase | Función |
|---|---|
| `SecurityConfig` | `@EnableWebSecurity @EnableMethodSecurity(prePostEnabled = true)`. Configura cadena de filtros, CORS, endpoints públicos |
| `JwtUtil` | Genera y valida tokens HMAC-SHA256. Valida secret en `@PostConstruct` |
| `JwtAuthenticationFilter` | Intercepta cada request, extrae Bearer token, setea `SecurityContext` |
| `AuditConfig` | `@EnableJpaAuditing` — activa los campos `@CreatedBy`, `@CreatedDate` |
| `AuditorAwareImpl` | Lee `SecurityContextHolder` para obtener el `username` del token JWT actual |
| `LogFilter` | Inyecta un UUID v4 como `traceId` en MDC para correlacionar logs y respuestas |
| `CacheConfig` | Manejador de errores de caché (graceful degradation) |
| `OpenApiConfig` | Configura Swagger con autenticación Bearer JWT |
| `RedisConfig` | **DESHABILITADO** — comentado completamente, no se carga en Spring |

---

## 3. Stack Tecnológico

| Componente | Versión | Nota |
|---|---|---|
| Java | **21** (LTS) | Toolchain en `build.gradle.kts`: `JavaLanguageVersion.of(21)` |
| Spring Boot | **3.2.3** | Plugins: `org.springframework.boot:3.2.3`, `io.spring.dependency-management:1.1.4` |
| Spring Security | 6.x (SM) | `@EnableMethodSecurity(prePostEnabled = true)`, `BCryptPasswordEncoder` |
| PostgreSQL Driver | SM | `runtimeOnly("org.postgresql:postgresql")` |
| Flyway | SM | `implementation("org.flywaydb:flyway-core")` |
| JJWT | **0.12.5** | `jjwt-api:0.12.5`, `jjwt-impl:0.12.5`, `jjwt-jackson:0.12.5` |
| SpringDoc OpenAPI | **2.3.0** | `springdoc-openapi-starter-webmvc-ui:2.3.0` |
| Caffeine | **3.1.8** | `com.github.ben-manes.caffeine:caffeine:3.1.8` |
| Apache POI | **5.2.5** | `poi-ooxml:5.2.5` (Excel para OC) |
| Lombok | SM | `compileOnly` + `annotationProcessor` |
| JUnit 5 | **5.10.1** | `useJUnitJupiter("5.10.1")` en `testing.suites` |
| JaCoCo | SM | Plugin `jacoco` en Gradle |
| H2 | SM | `runtimeOnly("com.h2database:h2")` — solo para tests |
| Guava | 33.0.0-jre | `implementation(libs.guava)` vía `gradle/libs.versions.toml` |
| Spring Mail | SM | `spring-boot-starter-mail` (Gmail SMTP) |
| Docker Compose | 3.8 | 2 servicios: `postgres-db` + `java-backend` |
| Gradle | **8.7** (Kotlin DSL) | Build system, `settings.gradle.kts` |

> **SM** = versión gestionada por Spring Boot 3.2.3 BOM (Bill of Materials).

### Flags Especiales del Compilador

```kotlin
// build.gradle.kts — CRÍTICO para Spring @RequestParam / @PathVariable
tasks.named<JavaCompile>("compileJava") {
    options.compilerArgs.add("-parameters")
}
tasks.named<JavaCompile>("compileTestJava") {
    options.compilerArgs.add("-parameters")
}
```

Sin `-parameters`, Spring no puede resolver los nombres de parámetros en métodos y los endpoints con `@RequestParam` o `@PathVariable` fallan.

---

## 4. Estructura del Código Fuente

```
app/src/main/java/com/nexoohub/almacen/
│
├── NexooHAlmacenApplication.java            # @SpringBootApplication, main()
│
├── common/                                  # Infraestructura transversal
│   ├── config/
│   │   ├── SecurityConfig.java              # @EnableWebSecurity @EnableMethodSecurity
│   │   ├── JwtUtil.java                     # HMAC-SHA256, generar/validar tokens
│   │   ├── JwtAuthenticationFilter.java     # OncePerRequestFilter, extrae Bearer
│   │   ├── AuditConfig.java                 # @EnableJpaAuditing
│   │   ├── AuditorAwareImpl.java            # Lee username del SecurityContext
│   │   ├── CacheConfig.java                 # CachingConfigurer: graceful degradation
│   │   ├── OpenApiConfig.java               # Swagger: bearerAuth scheme
│   │   ├── LogFilter.java                   # MDC traceId (UUID v4 por request)
│   │   └── RedisConfig.java                 # [DESHABILITADO] comentado + @ConditionalOnProperty
│   ├── controller/
│   │   ├── AuthController.java              # POST /api/v1/auth/login
│   │   ├── UsuarioController.java           # CRUD /api/v1/usuarios
│   │   └── RolesController.java             # /api/v1/admin/roles, asignación
│   ├── dto/
│   │   ├── LoginRequest.java                # record: username, password
│   │   └── AuthResponse.java                # record: token
│   ├── entity/
│   │   ├── AuditableEntity.java             # @MappedSuperclass con 4 campos auditados
│   │   ├── Usuario.java                     # tabla: usuarios, @ManyToMany Rol, Sucursal
│   │   ├── Rol.java                         # tabla: rol
│   │   └── Permiso.java                     # tabla: permiso
│   ├── exception/
│   │   ├── GlobalExceptionHandler.java      # @RestControllerAdvice, 10 handlers
│   │   ├── BusinessException.java
│   │   ├── ResourceNotFoundException.java   # 404
│   │   ├── DuplicateResourceException.java  # 409
│   │   ├── StockInsuficienteException.java  # 409
│   │   ├── CreditoInsuficienteException.java# 409
│   │   └── InvalidOperationException.java  # 400
│   ├── repository/
│   │   └── UsuarioRepository.java           # findByUsername(String)
│   └── ApiResponse.java                     # record universal de respuesta
│
├── catalogo/                                # Categorías, Clientes, Proveedores, Motos, etc.
│   └── controller/
│       ├── CategoriaController.java         # /api/v1/categorias
│       ├── ClienteController.java           # /api/v1/clientes
│       ├── MorosidadController.java         # /api/v1/clientes (morosidad)
│       ├── MorosidadV1Controller.java       # [HEREDADO] duplicado
│       ├── TipoClienteController.java       # /api/v1/tipos-cliente
│       ├── ProveedorController.java         # /api/v1/proveedores
│       ├── MotoController.java              # /api/v1/motos
│       ├── CompatibilidadController.java    # /api/v1/compatibilidad
│       └── PrecioEspecialController.java    # /api/v1/precios-especiales
│
├── inventario/                              # Inventario multi-sucursal
│   ├── controller/
│   │   ├── ProductoController.java          # /api/v1/productos (Specification pattern)
│   │   ├── InventarioController.java        # /api/v1/inventario (stock, alertas)
│   │   ├── CodigoBarrasController.java      # /api/v1/inventario/codigos-barras
│   │   ├── TraspasoController.java          # /api/v1/inventario/traspasos
│   │   ├── CaducidadController.java         # /api/v1/inventario/caducidad
│   │   ├── AnalisisAbcController.java       # /api/v1/inventario/analisis-abc
│   │   └── AlertaLentoMovimientoController.java # /api/alertas/lento-movimiento
│   ├── repository/
│   │   ├── ProductoMaestroRepository.java   # JpaSpecificationExecutor<ProductoMaestro>
│   │   └── InventarioSucursalRepository.java# Queries JPQL + Native SQL paginado
│   └── specification/
│       └── ProductoMaestroSpecification.java# 16 criterios de búsqueda dinámica
│
├── ventas/                                  # Ventas, Reservas, Devoluciones
├── compras/                                 # Ingresos de mercancía
├── cotizaciones/                            # Cotizaciones
├── adquisiciones/                           # Comparador, Actualiz. masiva, OC
├── caja/                                    # Turnos, Arqueos
├── pos/                                     # CFDI, Terminal, Offline sync
├── crm/                                     # Pipeline B2B, Garantías, NPS, Mktg
├── finanzas/                                # Dashboard, Crédito, Config, Auditoría
├── erp/                                     # Contabilidad, Logística, Nómina, CxP
├── comisiones/                              # Reglas de comisión y metas
├── metricas/                                # 4 tipos de métricas calculadas
├── rentabilidad/                            # Análisis de rentabilidad
├── analitica/                               # RFM, Churn, Market Basket, Rendimiento
├── fidelidad/                               # Programa de puntos
├── prediccion/                              # Predicción de demanda
├── alertas/                                 # Motor de notificaciones (Scheduler + API)
├── sucursal/                                # Gestión de sucursales
└── empleados/                               # Empleados operativos
```

### Estructura de Tests

```
app/src/test/java/com/nexoohub/almacen/
├── common/       # AuthController, UsuarioController, RolesController tests
├── catalogo/     # ClienteController, CategoriaController, etc.
├── inventario/   # ProductoController, InventarioController, etc.
├── ventas/       # VentaController, ReservaController, DevolucionController
├── cotizaciones/ # CotizacionController integration tests
└── ... (un paquete espejo por cada módulo de negocio)
```

Los tests de integración usan H2 en-memoria con `spring.jpa.hibernate.ddl-auto=create-drop`, sin necesidad de PostgreSQL activo.

---

## 5. Base de Datos — Esquema Completo

### Motor y Gestión

- Motor: **PostgreSQL 15-alpine** (Docker)
- Esquema gestionado exclusivamente por **Flyway** (`ddl-auto: validate` — solo valida, nunca crea ni altera)
- Base de datos: `nexoohub_almacen`
- Usuario BD: `nexoohub_user`
- Migrations location: `classpath:db/migration` (archivos `V{n}__{nombre}.sql`)
- `baseline-on-migrate: true`, `baseline-version: 0`, `validate-on-migrate: true`

### Migraciones Flyway — V1 a V33

| Versión | Archivo | Tablas Creadas / Acción |
|---|---|---|
| V1 | `V1__initial_schema.sql` | `configuracion_financiera`, `categoria`, `proveedor`, `tipo_cliente`, `moto`, `sucursal`, `empleado`, `usuarios`, `cliente`, `producto_maestro`, `historial_precio`, `precio_especial`, `compatibilidad_producto`, `inventario_sucursal`, `compra`, `detalle_compra`, `venta`, `detalle_venta`, `movimiento_inventario` + datos semilla + 25 índices |
| V2 | `V2__comisiones_vendedores.sql` | Tablas de comisiones y vendedores |
| V3 | `V3__prediccion_demanda.sql` | `prediccion_demanda` |
| V4 | `V4__analisis_abc_inventario.sql` | `analisis_abc` |
| V5 | `V5__programa_fidelidad.sql` | `programa_fidelidad`, `movimiento_punto` |
| V6 | `V6__rentabilidad_ventas_productos.sql` | `rentabilidad_venta`, `rentabilidad_producto` |
| V7 | `V7__metricas_financieras.sql` | `metrica_financiera` |
| V8 | `V8__metricas_inventario.sql` | `metrica_inventario` |
| V9 | `V9__metricas_venta_cliente.sql` | `metrica_venta_cliente` |
| V10 | `V10__metricas_operativas.sql` | `metrica_operativa` |
| V11 | `V11__caja_arqueos.sql` | `turno_caja`, `movimiento_caja` |
| V12 | `V12__pos_terminales.sql` | `terminal_bancaria`, `transaccion_tarjeta` |
| V13 | `V13__pos_cfdi.sql` | `factura_fiscal`, `detalle_factura` |
| V14 | `V14__pos_offline_sync.sql` | `lote_sincronizacion`, `venta_offline` |
| V15 | `V15__erp_cxp_gastos.sql` | `cuenta_por_pagar`, `gasto_operativo` |
| V16 | `V16__erp_contabilidad.sql` | `cuenta_contable`, `poliza_contable`, `movimiento_contable` |
| V17 | `V17__erp_logistica.sql` | `vehiculo`, `chofer`, `ruta_entrega`, `detalle_ruta` |
| V18 | `V18__erp_nomina.sql` | `empleado_nomina`, `nomina_periodo`, `recibo_nomina` |
| V19 | `V19__erp_devolucion_proveedor.sql` | `devolucion_proveedor`, `devolucion_proveedor_detalle` |
| V20 | `V20__inventario_codigos_barras.sql` | `codigo_barras_producto` |
| V21 | `V21__crm_garantias.sql` | `ticket_garantia`, `historial_garantia` |
| V22 | `V22__crm_pipeline_b2b.sql` | `prospecto`, `oportunidad_venta`, `interaccion_crm` |
| V23 | `V23__crm_marketing_campanas.sql` | `campana_marketing`, `log_envio_mensaje` |
| V24 | `V24__crm_nps_encuestas.sql` | `encuesta_nps`, `respuesta_nps` |
| V25 | `V25__ana_rfm.sql` | `analisis_rfm` |
| V26 | `V26__pro_alertas_notificaciones.sql` | `alerta_sistema`, `configuracion_alerta`, `config_notificacion` |
| V27 | `V27__sup_comparador_proveedores.sql` | `catalogo_proveedor`, `historial_precio_proveedor` |
| V28 | `V28__sup_actualizacion_precios.sql` | Actualización masiva de `historial_precio_proveedor` |
| V29 | `V29__sup_ordenes_compra.sql` | `orden_compra`, `detalle_orden_compra`, `carrito_compra` |
| V30 | `V30__pro_metas_comisiones.sql` | `meta_ventas_empleado`, `regla_comision` |
| V31 | `V31__pro_rbac_accesos.sql` | `rol`, `permiso`, `rol_permiso`, `usuario_rol`, `usuario_sucursal` |
| V32 | `V32__modulos_ventas_analitica.sql` | `cotizacion`, `detalle_cotizacion`, `reserva`, `devolucion`, `detalle_devolucion`, `alerta_lento_movimiento`, `analisis_churn`, `analisis_canasta`, `analisis_rendimiento_personal` |
| V33 | `V33__performance_indexes.sql` | 14 índices de performance (ver sección 8) |

### Tablas Principales — Columnas Relevantes (V1)

#### `producto_maestro` (PK: `sku_interno VARCHAR(50)`)

| Columna | Tipo | Nota |
|---|---|---|
| `sku_interno` | `VARCHAR(50)` | PK — identificador único del producto |
| `sku_proveedor` | `VARCHAR(50)` | SKU del catálogo del proveedor |
| `nombre_comercial` | `VARCHAR(200) NOT NULL` | Nombre de venta al público |
| `descripcion` | `TEXT` | Descripción larga |
| `marca` | `VARCHAR(100)` | Honda, Yamaha, Castrol, etc. |
| `categoria_id` | `INTEGER` | FK → `categoria(id)` |
| `proveedor_id` | `INTEGER` | FK → `proveedor(id)` |
| `clave_sat` | `VARCHAR(8)` | Clave SAT para facturación |
| `stock_minimo_global` | `INTEGER DEFAULT 2` | Stock mínimo de reorden global |
| `sensibilidad_precio` | `VARCHAR(20) DEFAULT 'MEDIA'` | ALTA / MEDIA / BAJA |
| `activo` | `BOOLEAN DEFAULT TRUE` | Soft delete |

#### `inventario_sucursal` (PK compuesta: `sucursal_id + sku_interno`)

| Columna | Tipo | Nota |
|---|---|---|
| `sucursal_id` | `INTEGER` | FK → `sucursal(id)` |
| `sku_interno` | `VARCHAR(50)` | FK → `producto_maestro(sku_interno)` |
| `stock_actual` | `INTEGER NOT NULL DEFAULT 0` | Stock en tiempo real |
| `stock_minimo_sucursal` | `INTEGER DEFAULT 0` | Mínimo configurable por sucursal |
| `costo_promedio_ponderado` | `NUMERIC(10,2)` | CPP actualizado en cada compra |
| `ubicacion_pasillo` | `VARCHAR(100)` | Ubicación física en almacén |
| `fecha_caducidad` | `DATE` | Fecha de caducidad del lote |
| `lote` | `VARCHAR(100)` | Número de lote |

#### `venta` (PK: `id SERIAL`)

| Columna | Tipo | Nota |
|---|---|---|
| `id` | `SERIAL` | PK autoincremental |
| `cliente_id` | `INTEGER` | FK → `cliente(id)` |
| `sucursal_id` | `INTEGER` | FK → `sucursal(id)` |
| `vendedor_id` | `INTEGER` | FK → `usuarios(id)` |
| `metodo_pago` | `VARCHAR(50)` | EFECTIVO / TARJETA / TRANSFERENCIA / CREDITO |
| `total` | `NUMERIC(10,2) DEFAULT 0.00` | Total calculado por `VentaService` |
| `fecha_venta` | `TIMESTAMP DEFAULT CURRENT_TIMESTAMP` | — |

#### `cliente` (PK: `id SERIAL`)

| Columna | Tipo | Nota |
|---|---|---|
| `id` | `SERIAL` | PK |
| `tipo_cliente_id` | `INTEGER` | FK → `tipo_cliente(id)` |
| `nombre` | `VARCHAR(255) NOT NULL` | — |
| `rfc` | `VARCHAR(13)` | RFC para facturación |
| `telefono` | `VARCHAR(20)` | — |
| `email` | `VARCHAR(100)` | — |
| `bloqueado` | `BOOLEAN DEFAULT FALSE` | Bloqueo por morosidad |
| `saldo_pendiente` | `NUMERIC(12,2) DEFAULT 0.00` | Deuda acumulada |
| `motivo_bloqueo` | `VARCHAR(500)` | Razón del bloqueo |

#### `usuarios` (PK: `id BIGSERIAL`)

| Columna | Tipo | Nota |
|---|---|---|
| `id` | `BIGSERIAL` | PK — BigInt por relación con Spring Security |
| `username` | `VARCHAR(50) UNIQUE NOT NULL` | Login |
| `password` | `VARCHAR(255) NOT NULL` | BCrypt hash (`$2a$10$...`) |
| `role` | `VARCHAR(20) DEFAULT 'ROLE_USER'` | Rol simple (legacy). RBAC usa tabla `usuario_rol` |
| `empleado_id` | `INTEGER` | FK → `empleado(id)` |
| `activo` | `BOOLEAN DEFAULT TRUE` | — |

**Seed:** `admin` / `admin123` (BCrypt `$2a$10$k/mIluuONgJLE8efmX6Cse4/k8aUv5dvUqsCjJmxXKELm6ZMPZqsm`)

#### Tablas RBAC — V31

| Tabla | Descripción |
|---|---|
| `rol` | `id, nombre VARCHAR(50) UNIQUE, descripcion, activo` |
| `permiso` | `id, nombre VARCHAR(100) UNIQUE, descripcion, modulo` |
| `rol_permiso` | PK compuesta `(rol_id, permiso_id)` |
| `usuario_rol` | PK compuesta `(usuario_id, rol_id)` |
| `usuario_sucursal` | PK compuesta `(usuario_id, sucursal_id)` — restringe sucursales visibles |

**Roles semilla:** `ROLE_ADMIN`, `ROLE_GERENTE_SUCURSAL`, `ROLE_VENDEDOR`, `ROLE_CAJERO`, `ROLE_ALMACENISTA`

**Permisos semilla:** `ACCESO_GLOBAL`, `LEER_VENTA`, `CREAR_VENTA`, `LEER_COMPRA`, `CREAR_COMPRA`, `GESTIONAR_PRECIOS`, `GESTIONAR_METAS`, `LEER_REPORTES`

---

## 6. Módulos Funcionales y Endpoints Detallados

> **Base URL:** `http://localhost:8080`  
> **Autenticación:** `Authorization: Bearer {TOKEN}` en todos los endpoints **excepto los listados como públicos**.
>
> **Roles disponibles** (en `@PreAuthorize`):  
> `ADMIN`, `SUPERVISOR`, `GERENTE`, `ALMACENISTA`, `VENDEDOR`, `CAJERO`

---

### 6.1 Autenticación y Usuarios

**Clase:** `AuthController` — `@RequestMapping("/api/v1/auth")`

| Método | Path | Roles | Request | Descripción |
|---|---|---|---|---|
| POST | `/api/v1/auth/login` | **PÚBLICO** | `LoginRequest{username, password}` | Devuelve JWT en `AuthResponse{token}` |

**Clase:** `UsuarioController` — `@RequestMapping("/api/v1/usuarios")`

| Método | Path | Roles | Request Body | Descripción |
|---|---|---|---|---|
| GET | `/api/v1/usuarios` | Autenticado | — | Listar usuarios |
| POST | `/api/v1/usuarios` | Autenticado | `Usuario{username, password, role, empleadoId}` | Crear usuario |
| GET | `/api/v1/usuarios/{id}` | Autenticado | — | Obtener por ID |
| PUT | `/api/v1/usuarios/{id}` | Autenticado | `Usuario` | Actualizar |
| PUT | `/api/v1/usuarios/{id}/password` | Autenticado | `{passwordActual, passwordNuevo}` | Cambiar contraseña (BCrypt) |
| DELETE | `/api/v1/usuarios/{id}` | Autenticado | — | Eliminar usuario |

**Clase:** `RolesController` — `@RequestMapping("/api/v1/admin")`

| Método | Path | Roles | Descripción |
|---|---|---|---|
| POST | `/api/v1/admin/roles` | ADMIN | Crear rol con permisos |
| POST | `/api/v1/admin/usuarios/{id}/roles` | ADMIN | Asignar rol a usuario |
| GET | `/api/v1/admin/usuarios/{id}/permisos` | ADMIN | Ver permisos del usuario |

---

### 6.2 Catálogo

**`CategoriaController`** — `/api/v1/categorias`

| Método | Path | Roles | Descripción |
|---|---|---|---|
| GET | `/api/v1/categorias` | Autenticado | Listar todas |
| POST | `/api/v1/categorias` | Autenticado | Crear (`nombre NOT NULL`, `descripcion`) |
| PUT | `/api/v1/categorias/{id}` | Autenticado | Actualizar |

**`ClienteController`** — `/api/v1/clientes`

| Método | Path | Roles | Request / Params | Descripción |
|---|---|---|---|---|
| GET | `/api/v1/clientes` | Autenticado | `?page=0&size=20` | Listar (paginado) |
| POST | `/api/v1/clientes` | Autenticado | `{nombre, rfc, telefono, email, tipoClienteId, bloqueado}` | Crear |
| PUT | `/api/v1/clientes/{id}` | Autenticado | `{nombre, rfc, telefono, email, tipoClienteId}` | Actualizar |
| GET | `/api/v1/clientes/bloqueados` | Autenticado | — | Clientes con `bloqueado=true` |
| GET | `/api/v1/clientes/morosos` | Autenticado | — | Clientes con `saldo_pendiente > 0` |

**`MorosidadController`** — `/api/v1/clientes`

| Método | Path | Roles | Descripción |
|---|---|---|---|
| POST | `/api/v1/clientes/{id}/bloquear` | Autenticado | `?motivo=` — setea `bloqueado=true` |
| POST | `/api/v1/clientes/{id}/desbloquear` | Autenticado | Setea `bloqueado=false` |
| POST | `/api/v1/clientes/{id}/registrar-pago` | Autenticado | `?monto=` — reduce `saldo_pendiente`, desbloquea si saldo=0 |

**`MotoController`** — `/api/v1/motos`

| Método | Path | Descripción |
|---|---|---|
| GET | `/api/v1/motos` | Listar (marca, modelo, cilindrada) |
| POST | `/api/v1/motos` | Crear `{marca, modelo, cilindrada, anioInicio, anioFin}` |
| PUT | `/api/v1/motos/{id}` | Actualizar |

**`CompatibilidadController`** — `/api/v1/compatibilidad`

| Método | Path | Descripción |
|---|---|---|
| GET | `/api/v1/compatibilidad/producto/{skuInterno}` | Motos compatibles con un SKU |
| GET | `/api/v1/compatibilidad/moto/{motoId}` | Productos compatibles con una moto |
| POST | `/api/v1/compatibilidad` | `{skuInterno, motoId, anioInicio, anioFin}` |

**`PrecioEspecialController`** — `/api/v1/precios-especiales`

> ⚠️ Solo `POST` y `DELETE`. No hay `GET`. Los precios especiales se consultan implícitamente en cada venta.

| Método | Path | Descripción |
|---|---|---|
| POST | `/api/v1/precios-especiales` | `{skuInterno, tipoClienteId, precioFijo, fechaInicio, fechaFin}` |
| DELETE | `/api/v1/precios-especiales/{id}` | Eliminar precio especial |

---

### 6.3 Inventario — Productos

**`ProductoController`** — `@RequestMapping("/api/v1/productos")`

#### `GET /api/v1/productos/search` — Motor de Búsqueda Omnicanal

Implementado con `ProductoMaestroSpecification.busquedaDinamica(...)`.  
Devuelve `Page<ProductoMaestroResponseDTO>` (default: `size=20, page=0`).  
Requiere roles: `ADMIN, SUPERVISOR, ALMACENISTA, VENDEDOR, CAJERO`.

Parámetros opcionales (todos `required = false`):

| Param | Tipo | Descripción |
|---|---|---|
| `q` | `String` | Texto libre — busca en SKU, nombre, descripción, marca |
| `categoriaId` | `Integer` | ID exacto de categoría |
| `nombreCategoria` | `String` | Búsqueda parcial por nombre de categoría |
| `proveedorId` | `Integer` | ID exacto de proveedor |
| `nombreProveedor` | `String` | Búsqueda parcial por nombre de proveedor |
| `motoId` | `Integer` | ID exacto de moto compatible |
| `marcaMoto` | `String` | Marca de moto (Honda, Yamaha…) |
| `modeloMoto` | `String` | Modelo de moto (CBR, YZF…) |
| `cilindrada` | `Integer` | Cilindraje (150, 200, 250…) |
| `anio` | `Integer` | Año de compatibilidad |
| `soloActivos` | `Boolean` | Si `true` → solo `activo=true` |
| `conStock` | `Boolean` | Si `true` → solo con `stock_actual > 0` en la sucursal indicada |
| `sucursalIdStock` | `Integer` | Sucursal para evaluar `conStock` |
| `precioMin` | `BigDecimal` | Precio mínimo de venta al público |
| `precioMax` | `BigDecimal` | Precio máximo de venta al público |
| `clasificacionAbc` | `String` | Clasificación ABC: `A`, `B` o `C` |

#### Otros Endpoints de Productos

| Método | Path | Roles | DTO Entrada | Descripción |
|---|---|---|---|---|
| POST | `/api/v1/productos` | ADMIN, SUPERVISOR | `ProductoMaestro{skuInterno, nombreComercial, categoriaId, proveedorId, claveSat, stockMinimoGlobal}` | Crea producto maestro |
| GET | `/api/v1/productos/{sku}` | ADMIN, SUPERVISOR, ALMACENISTA, VENDEDOR, CAJERO | — | Devuelve `ProductoMaestroResponseDTO` |
| PUT | `/api/v1/productos/{sku}` | ADMIN, SUPERVISOR | `ProductoMaestro` | Actualiza `nombreComercial`, `claveSat`, `stockMinimoGlobal` (SKU no se actualiza — es PK) |
| DELETE | `/api/v1/productos/{sku}` | ADMIN | — | Elimina producto (permanente) |
| GET | `/api/v1/productos/mostrador` | ADMIN, SUPERVISOR, ALMACENISTA, VENDEDOR, CAJERO | — | Lista simplificada `ProductoResumenDTO` para POS |

---

### 6.4 Inventario — Stock y Movimientos

**`InventarioController`** — `@RequestMapping("/api/v1/inventario")`

| Método | Path | Roles | Request | Descripción |
|---|---|---|---|---|
| POST | `/api/v1/inventario` | ADMIN, SUPERVISOR, ALMACENISTA | `InventarioSucursal{sucursalId, skuInterno, stockActual, costoPromedioPonderado}` | Inicializar/ajustar stock. Devuelve `201 Created` |
| GET | `/api/v1/inventario/sucursales/{sucursalId}` | Todos | `?page=0&size=50&sort=nombreComercial` | Paginado nativo SQL via `InventarioSucursalProjection` |
| GET | `/api/v1/inventario/alertas/stock-bajo/sucursales/{sucursalId}` | ADMIN, SUPERVISOR, ALMACENISTA | — | JPQL: `stockActual < stockMinimoSucursal AND activo=true`, ordenado por déficit DESC |
| GET | `/api/v1/inventario/alertas/stock-bajo` | ADMIN, SUPERVISOR, ALMACENISTA | — | Igual que anterior pero todas las sucursales |

**Queries Clave en `InventarioSucursalRepository`:**

```sql
-- Fotografía paginada (native SQL con proyección de interfaz)
SELECT i.sku_interno, p.nombre_comercial, i.stock_actual, i.costo_promedio_ponderado
FROM inventario_sucursal i
JOIN producto_maestro p ON i.sku_interno = p.sku_interno
WHERE i.sucursal_id = :sucursalId

-- Productos con stock bajo en una sucursal (JPQL)
WHERE i.id.sucursalId = :sucursalId
  AND i.stockActual < i.stockMinimoSucursal
  AND p.activo = true
ORDER BY (i.stockMinimoSucursal - i.stockActual) DESC

-- Productos próximos a caducar (JPQL)
WHERE i.fechaCaducidad IS NOT NULL
  AND i.fechaCaducidad <= :fechaLimite    -- now() + 30 días
  AND i.fechaCaducidad >= CURRENT_DATE
  AND p.activo = true
ORDER BY i.fechaCaducidad ASC
```

---

### 6.5 Inventario — Extensiones

**`CodigoBarrasController`** — `/api/v1/inventario`

| Método | Path | Descripción |
|---|---|---|
| GET | `/api/v1/inventario/productos/buscar-por-codigo?codigo=` | Busca producto por EAN/código de barras |
| POST | `/api/v1/inventario/escaneo` | `{codigoBarras, sucursalId}` — escaneo en POS |
| POST | `/api/v1/inventario/productos/{skuInterno}/codigos-barras` | `{codigo, descripcion}` — agrega código |
| GET | `/api/v1/inventario/productos/{skuInterno}/codigos-barras` | Lista códigos de un SKU |
| DELETE | `/api/v1/inventario/codigos-barras/{id}` | Elimina código de barras |
| POST | `/api/v1/inventario/productos/importar-masivo` | Importación masiva de productos |

**`TraspasoController`** — `/api/v1/inventario/traspasos`

| Método | Path | Request Body | Descripción |
|---|---|---|---|
| POST | `/api/v1/inventario/traspasos` | `{sucursalOrigenId, sucursalDestinoId, skuInterno, cantidad, motivo}` | Genera movimiento de inventario SALIDA en origen y ENTRADA en destino |

**`CaducidadController`** — `/api/v1/inventario/caducidad`

| Método | Path | Descripción |
|---|---|---|
| GET | `/api/v1/inventario/caducidad/proximos` | Vence en los próximos 30 días (hardcoded en repo query) |
| GET | `/api/v1/inventario/caducidad/vencidos` | `fechaCaducidad < CURRENT_DATE` |

**`AnalisisAbcController`** — `/api/v1/inventario/analisis-abc`

| Método | Path | Descripción |
|---|---|---|
| POST | `/api/v1/inventario/analisis-abc/generar` | `{sucursalId, fechaInicio, fechaFin}` — clasifica productos por rotación |
| GET | `/api/v1/inventario/analisis-abc/sucursal/{sucursalId}/ultimo` | Último análisis generado |
| GET | `/api/v1/inventario/analisis-abc/sucursal/{sucursalId}/clasificacion/{clasificacion}` | Productos clase `A`, `B` o `C` |
| GET | `/api/v1/inventario/analisis-abc/sucursal/{sucursalId}/resumen` | Porcentajes y totales por clase |

**`AlertaLentoMovimientoController`** — `/api/alertas/lento-movimiento` _(sin `/v1/`)_

| Método | Path | Descripción |
|---|---|---|
| GET | `/api/alertas/lento-movimiento` | Listar activas |
| POST | `/api/alertas/lento-movimiento/generar` | `{sucursalId, diasSinMovimiento}` |
| GET | `/api/alertas/lento-movimiento/{id}` | Obtener por ID |
| GET | `/api/alertas/lento-movimiento/sucursal/{sucursalId}` | Por sucursal |
| GET | `/api/alertas/lento-movimiento/criticas` | Solo criticidad ALTA |
| GET | `/api/alertas/lento-movimiento/producto/{skuInterno}` | Por SKU |
| GET | `/api/alertas/lento-movimiento/costo-inmovilizado` | Suma total del capital inmovilizado |
| PUT | `/api/alertas/lento-movimiento/{id}/resolver` | `{accionTomada, observaciones}` |
| DELETE | `/api/alertas/lento-movimiento/{id}` | Eliminar alerta |

---

### 6.6 Ventas

**`VentaController`** — `@RequestMapping("/api/v1/ventas")`

| Método | Path | Roles | Descripción |
|---|---|---|---|
| POST | `/api/v1/ventas` | ADMIN, VENDEDOR, CAJERO | Registrar venta. Devuelve `201 Created` con `VentaResponseDTO` |

**Request Body — `VentaRequestDTO`:**

```json
{
  "clienteId":  1,          // @NotNull — Integer
  "sucursalId": 1,          // @NotNull — Integer
  "metodoPago": "EFECTIVO", // @NotNull — String: EFECTIVO | TARJETA | TRANSFERENCIA | CREDITO
  "items": [                // @NotEmpty — List<ItemVentaDTO>
    {
      "skuInterno": "ACEITE-CASTROL-10W40-1L",  // @NotNull
      "cantidad": 2,                             // @NotNull
      "precioOfertaEspecial": 170.00             // Opcional — descuento adicional del vendedor
    }
  ]
}
```

**Flujo interno de `VentaService.procesarVenta()`:**

1. Busca `Usuario` por `vendedorUsername` del `SecurityContext` → 404 si no existe
2. Busca `Cliente` por `clienteId` → obtiene su `tipoClienteId`
3. Si `metodoPago == "CREDITO"`: calcula total estimado → `CreditoService.validarCreditoDisponible()` → lanza `CreditoInsuficienteException` si no alcanza
4. Para cada ítem: verifica `InventarioSucursal` → lanza `StockInsuficienteException` si `stockActual < cantidad`
5. Busca `HistorialPrecio` más reciente del SKU → obtiene `precioFinalPublico`
6. Busca `PrecioEspecial` por `(skuInterno, tipoClienteId)` → si existe, sobreescribe precio (precio para taller, mayorista, etc.)
7. Si `precioOfertaEspecial < precioCalculado` → aplica descuento especial del vendedor
8. Resta stock: `inventario.stockActual -= cantidad` → `inventarioRepository.save()`
9. Guarda `DetalleVenta` con precio final, descuento y porcentaje de descuento
10. Calcula y actualiza `venta.total`
11. Si `metodoPago == "CREDITO"`: `CreditoService.registrarCargo()` → actualiza `saldo_pendiente` del cliente

**`ReservaController`** — `/api/v1/reservas`

| Método | Path | Roles | Request / Params | Descripción |
|---|---|---|---|---|
| GET | `/api/v1/reservas` | Autenticado | — | Listar todas |
| POST | `/api/v1/reservas` | Autenticado | `{sucursalId, clienteId, skuInterno, cantidad, anticipo, diasVigencia}` | Crear apartado |
| GET | `/api/v1/reservas/{id}` | Autenticado | — | Consultar |
| GET | `/api/v1/reservas/cliente/{clienteId}` | Autenticado | — | Por cliente |
| GET | `/api/v1/reservas/estado/{estado}` | Autenticado | `estado`: PENDIENTE, COMPLETADA, CANCELADA | Por estado |
| GET | `/api/v1/reservas/proximas-vencer` | Autenticado | — | Vencen en las próximas 24h |
| PUT | `/api/v1/reservas/{id}/cancelar` | Autenticado | `{motivo}` | Cancela y libera stock |
| PUT | `/api/v1/reservas/{id}/completar` | Autenticado | `{observaciones}` | Entrega y genera venta |
| POST | `/api/v1/reservas/procesar-vencidas` | ADMIN | — | Batch — cancela vencidas automáticamente |

**`DevolucionController`** — `/api/v1/devoluciones`

| Método | Path | Request Body | Descripción |
|---|---|---|---|
| POST | `/api/v1/devoluciones` | `{ventaId, motivo, detalles:[{detalleVentaId, cantidadDevuelta}]}` | Registra devolución y reinstala stock |
| GET | `/api/v1/devoluciones/{id}` | — | Consultar devolución |
| GET | `/api/v1/devoluciones/venta/{ventaId}` | — | Devoluciones de una venta específica |

---

### 6.7 Cotizaciones

**`CotizacionController`** — `@RequestMapping("/api/cotizaciones")` _(sin `/v1/`)_

| Método | Path | Descripción |
|---|---|---|
| GET | `/api/cotizaciones` | Listar todas |
| POST | `/api/cotizaciones` | `{clienteId, empleadoId, detalles:[{skuInterno, cantidad, precioUnitario, descuento}], vigenciaDias}` |
| GET | `/api/cotizaciones/{id}` | Por ID |
| PUT | `/api/cotizaciones/{id}` | Actualizar cotización en estado BORRADOR |
| DELETE | `/api/cotizaciones/{id}` | Eliminar |
| GET | `/api/cotizaciones/folio/{folio}` | Buscar por folio `COT-{año}-{seq}` |
| PUT | `/api/cotizaciones/{id}/estado` | Cambiar estado |
| POST | `/api/cotizaciones/{id}/convertir-venta` | `{sucursalId, metodoPago}` — crea venta desde cotización |
| GET | `/api/cotizaciones/vencimiento/proximas` | Vencen en los próximos 3 días |
| GET | `/api/cotizaciones/pendientes-conversion` | En estado APROBADA pero no convertidas |
| GET | `/api/cotizaciones/estadisticas` | Tasa de conversión, monto promedio |
| POST | `/api/cotizaciones/marcar-vencidas` | Batch — marca como VENCIDAS las expiradas |

---

### 6.8 Compras e Ingresos

| Método | Path | Request Body | Descripción |
|---|---|---|---|
| POST | `/api/v1/compras/ingreso` | `{proveedorId, sucursalId, esCredito, diasCredito, detalles:[{skuInterno, cantidad, precioUnitario}]}` | Ingresa mercancía, actualiza CPP, genera CxP si `esCredito=true` |

---

### 6.9 Adquisiciones

**Comparador:** `GET /api/sup/comparador/producto/{sku}` — lista precios del SKU en todos los proveedores del catálogo de comparación.

**Actualización masiva de precios** — `/api/v1/comparador/catalogo`

| Método | Path | Descripción |
|---|---|---|
| PUT | `/api/v1/comparador/catalogo/{id}/precio` | `{nuevoPrecio, motivo}` — actualiza un ítem |
| POST | `/api/v1/comparador/catalogo/actualizar-masivo` | `[{skuInterno, nuevoPrecio}]` — batch |
| GET | `/api/v1/comparador/catalogo/{id}/historial` | Historial de cambios de precio de un ítem |

**Órdenes de Compra** — `/api/v1/oc`

| Método | Path | Descripción |
|---|---|---|
| GET | `/api/v1/oc` | Listar OC |
| POST | `/api/v1/oc/carrito/agregar` | `{catalogoId, cantidad}` |
| DELETE | `/api/v1/oc/carrito/{catalogoId}` | Quitar del carrito |
| GET | `/api/v1/oc/carrito` | Ver carrito actual |
| POST | `/api/v1/oc/generar` | `{sucursalId, proveedorId}` — genera OC desde carrito |
| PATCH | `/api/v1/oc/{id}/estado` | `{estado}`: BORRADOR, ENVIADA, CONFIRMADA, RECIBIDA |
| POST | `/api/v1/oc/{id}/recibir` | Confirma recepción, actualiza inventario |
| GET | `/api/v1/oc/{id}/exportar-excel` | Descarga `.xlsx` (Apache POI) |

---

### 6.10 Caja

**`CajaController`** — `/api/v1/cajas`

| Método | Path | Request Body | Descripción |
|---|---|---|---|
| POST | `/api/v1/cajas/abrir` | `{sucursalId, empleadoId, fondoInicial}` | Abre turno — `turno_caja.estado = ABIERTO` |
| POST | `/api/v1/cajas/movimientos` | `{turnoCajaId, tipo: ENTRADA|SALIDA, monto, concepto}` | Registra movimiento |
| POST | `/api/v1/cajas/{id}/cerrar` | `{efectivoContado, observaciones}` | Arqueo Z — calcula diferencia, cierra turno |
| GET | `/api/v1/cajas/{id}/resumen` | — | Resumen del turno: ventas, efectivo, diferencia |

---

### 6.11 POS

**Facturación CFDI** — `/api/v1/facturacion`

| Método | Path | Request Body | Descripción |
|---|---|---|---|
| POST | `/api/v1/facturacion/timbrar` | `{ventaId, rfcReceptor, usoCfdi, metodoPago, formaPago}` | Genera CFDI 4.0 |
| POST | `/api/v1/facturacion/{facturaId}/cancelar` | `{motivo, folioSustitucion}` | Cancela ante SAT |
| GET | `/api/v1/facturacion/{facturaId}/descargar` | — | Descarga XML/PDF |
| GET | `/api/v1/facturacion/cliente/{clienteId}` | — | Historial de facturas |

**Terminal Bancaria** — `/api/v1/pos/pagos`

| Método | Path | Descripción |
|---|---|---|
| POST | `/api/v1/pos/pagos/tarjeta` | `{ventaId, monto, tipoTarjeta: DEBITO|CREDITO}` |
| POST | `/api/v1/pos/pagos/cancelar` | Cancela cargo en terminal |
| GET | `/api/v1/pos/pagos/{referencia}/estatus` | Estatus de transacción |

**Sincronización Offline** — `/api/v1/sincronizacion`

| Método | Path | Descripción |
|---|---|---|
| POST | `/api/v1/sincronizacion/lote` | Sube lote de ventas capturadas sin conexión |
| POST | `/api/v1/sincronizacion/reintentar` | Reintenta lotes fallidos |
| GET | `/api/v1/sincronizacion/pendientes` | Lotes en estado PENDIENTE |

---

### 6.12 Sucursales y Empleados

**`SucursalController`** — `/api/v1/sucursales`

| Método | Path | Roles | Descripción |
|---|---|---|---|
| GET | `/api/v1/sucursales` | Autenticado | Listar |
| POST | `/api/v1/sucursales` | ADMIN | `{nombre, direccion, activo}` |
| PUT | `/api/v1/sucursales/{id}` | ADMIN | Actualizar |
| DELETE | `/api/v1/sucursales/{id}` | ADMIN | Eliminar |

**`EmpleadoController`** — `/api/v1/empleados`

| Método | Path | Descripción |
|---|---|---|
| POST | `/api/v1/empleados` | `{nombre, apellidos, puesto, departamento, sucursalId, salarioDiario, fechaIngreso}` |
| GET | `/api/v1/empleados/sucursal/{sucursalId}` | Empleados de una sucursal |
| DELETE | `/api/v1/empleados/{id}` | Eliminar |

---

### 6.13 Comisiones y Metas

**`ComisionController`** — `@RequestMapping("/api/comisiones")` _(sin `/v1/`)_

| Método | Path | Descripción |
|---|---|---|
| GET | `/api/comisiones/reglas` | Listar reglas de comisión |
| POST | `/api/comisiones/reglas` | `{nombre, tipo: PORCENTAJE|MONTO_FIJO, porcentaje}` |
| GET | `/api/comisiones/reglas/{id}` | Obtener regla |
| PUT | `/api/comisiones/reglas/{id}` | Actualizar regla |
| DELETE | `/api/comisiones/reglas/{id}` | Eliminar regla |
| POST | `/api/comisiones/calcular?anio=&mes=` | Calcula comisiones de todos los vendedores del mes |
| POST | `/api/comisiones/calcular/vendedor/{vendedorId}` | Calcula solo para un vendedor |
| GET | `/api/comisiones/{id}` | Obtener comisión específica |
| GET | `/api/comisiones/vendedor/{vendedorId}` | Historial de un vendedor |
| GET | `/api/comisiones/periodo` | Por periodo |
| GET | `/api/comisiones/estado/{estado}` | PENDIENTE, APROBADA, PAGADA |
| GET | `/api/comisiones/resumen?anio=&mes=` | Total y promedio del mes |
| PUT | `/api/comisiones/{id}/aprobar` | `{observaciones}` — aprueba para nómina |
| PUT | `/api/comisiones/{id}/pagar` | Marca como pagada |
| PUT | `/api/comisiones/{id}/ajustar` | `{nuevoMonto, justificacion}` |

**Metas (RH)** — `/api/v1/rh/metas`

| Método | Path | Descripción |
|---|---|---|
| POST | `/api/v1/rh/metas` | `{empleadoId, mes, anio, metaMonto}` |
| GET | `/api/v1/rh/metas/{empleadoId}/progreso` | Avance actual vs meta asignada |

---

### 6.14 Finanzas

**Dashboard:** `GET /api/v1/dashboard` — KPIs consolidados: ventas del día, inventario total, CxP pendientes, etc.

**Configuración Financiera** — `/api/v1/finanzas/parametros`

| Método | Path | Descripción |
|---|---|---|
| GET | `/api/v1/finanzas/parametros` | Config vigente: `margenGananciaBase`, `comisionTarjeta`, `iva`, `metaVentasMensual`, etc. |
| PUT | `/api/v1/finanzas/parametros` | Actualizar parámetros globales |

**Auditoría de Precios** — `/api/v1/auditoria/precios`

| Método | Path | Params | Descripción |
|---|---|---|---|
| GET | `/api/v1/auditoria/precios/producto/{skuInterno}` | — | Todos los registros de `historial_precio` para ese SKU |
| GET | `/api/v1/auditoria/precios/periodo` | `?fechaInicio=ISO&fechaFin=ISO` | Cambios en un rango de fechas |
| GET | `/api/v1/auditoria/precios/significativos` | — | Cambios > umbral configurado (default 10%) |

**Crédito y Cobranza** — `@RequestMapping("/api/credito")` _(sin `/v1/`)_

| Método | Path | Descripción |
|---|---|---|
| POST | `/api/credito/limites` | `{clienteId, limiteCredito, diasCredito}` — crea/actualiza límite |
| GET | `/api/credito/limites` | Listar todos |
| GET | `/api/credito/limites/cliente/{clienteId}` | Límite y disponible de un cliente |
| PUT | `/api/credito/limites/cliente/{clienteId}` | Actualizar límite |
| GET | `/api/credito/limites/estado/{estado}` | Por estado: ACTIVO, BLOQUEADO, SUSPENDIDO |
| GET | `/api/credito/limites/activos` | Solo activos |
| GET | `/api/credito/limites/bloqueados` | Solo bloqueados |
| GET | `/api/credito/limites/riesgo` | `saldo_pendiente / limite_credito > 80%` |
| GET | `/api/credito/limites/sobregiro` | `saldo_pendiente > limite_credito` |
| GET | `/api/credito/validar?clienteId=&monto=` | Devuelve `{creditoDisponible, montoDisponible, codigo, mensaje}` |
| POST | `/api/credito/abonos` | `{clienteId, monto, metodoPago, referencia}` — reduce `saldo_pendiente` |
| PUT | `/api/credito/limites/cliente/{clienteId}/bloquear` | Bloquea crédito (`estado=BLOQUEADO`) |
| PUT | `/api/credito/limites/cliente/{clienteId}/desbloquear` | Reactiva crédito |
| PUT | `/api/credito/limites/cliente/{clienteId}/suspender` | Suspende temporalmente |
| GET | `/api/credito/historial/{clienteId}` | Todos los movimientos del cliente |
| GET | `/api/credito/historial/{clienteId}/cargos` | Solo cargos (ventas a crédito) |
| GET | `/api/credito/historial/{clienteId}/abonos` | Solo abonos (pagos) |
| GET | `/api/credito/historial/{clienteId}/rango` | `?fechaInicio=&fechaFin=` |

---

### 6.15 ERP

**CxP y Gastos** — `/api/v1`

| Método | Path | Descripción |
|---|---|---|
| GET | `/api/v1/cxp` | Listar cuentas por pagar pendientes |
| POST | `/api/v1/cxp` | Crear CxP manual |
| POST | `/api/v1/cxp/{id}/pagos` | `{monto, metodoPago, referencia}` — registra pago |
| POST | `/api/v1/finanzas/gastos` | `{concepto, monto, categoria, sucursalId, fecha}` |
| GET | `/api/v1/finanzas/gastos` | Listar gastos operativos |

**Contabilidad** — `/api/v1/contabilidad`

| Método | Path | Descripción |
|---|---|---|
| GET | `/api/v1/contabilidad/cuentas` | Catálogo de cuentas contables |
| POST | `/api/v1/contabilidad/polizas` | `{tipo: INGRESO|EGRESO|DIARIO, descripcion, movimientos:[{cuentaId, debe, haber}]}` |
| GET | `/api/v1/contabilidad/polizas` | Listar pólizas |
| GET | `/api/v1/contabilidad/reportes/balanza` | Balanza de comprobación al corte |
| GET | `/api/v1/contabilidad/reportes/estado-resultados` | Estado de resultados del periodo |

**Logística** — `/api/v1/logistica`

| Método | Path | Descripción |
|---|---|---|
| GET | `/api/v1/logistica/vehiculos` | Flota vehicular con capacidad y estado |
| GET | `/api/v1/logistica/choferes` | Choferes registrados |
| GET | `/api/v1/logistica/rutas` | Listar rutas de entrega |
| POST | `/api/v1/logistica/rutas` | `{vehiculoId, choferId, fecha}` |
| POST | `/api/v1/logistica/rutas/{id}/facturas` | `{facturaIds:[1,2,3]}` — asigna facturas a ruta |
| PATCH | `/api/v1/logistica/rutas/{id}/estatus` | `{estatus}`: PENDIENTE, EN_CAMINO, ENTREGADO |

**Nómina** — `/api/v1/nomina`

| Método | Path | Descripción |
|---|---|---|
| GET | `/api/v1/nomina/empleados` | Empleados en nómina |
| POST | `/api/v1/nomina/empleados` | `{empleadoId, salarioBruto, periodicidad: SEMANAL|QUINCENAL|MENSUAL, tipoPago}` |
| GET | `/api/v1/nomina/periodos` | Periodos de nómina |
| POST | `/api/v1/nomina/periodos` | `{fechaInicio, fechaFin, descripcion}` |
| POST | `/api/v1/nomina/periodos/{id}/generar` | Genera `recibo_nomina` para cada empleado con cálculo de ISR/IMSS |
| GET | `/api/v1/nomina/recibos/{id}` | Recibo individual con desglose |

**Devoluciones a Proveedor** — `/api/v1/devoluciones/proveedores`

| Método | Path | Descripción |
|---|---|---|
| GET | `/api/v1/devoluciones/proveedores` | Listar |
| POST | `/api/v1/devoluciones/proveedores` | `{proveedorId, detalles:[{skuInterno, cantidad, motivo}]}` |
| GET | `/api/v1/devoluciones/proveedores/{id}` | Obtener |
| POST | `/api/v1/devoluciones/proveedores/{id}/aplicar` | Genera movimiento SALIDA en inventario y nota de crédito |

---

### 6.16 CRM

**Pipeline B2B** — `/api/v1/crm`

| Método | Path | Descripción |
|---|---|---|
| GET | `/api/v1/crm/prospectos` | Listar prospectos B2B |
| POST | `/api/v1/crm/prospectos` | `{empresa, contactoPrincipal, telefono, email, rfc}` |
| GET | `/api/v1/crm/prospectos/{id}` | Obtener prospecto |
| POST | `/api/v1/crm/oportunidades` | `{prospectoId, titulo, valorProyectado, etapa, probabilidad}` |
| PATCH | `/api/v1/crm/oportunidades/{id}/etapa` | `{nuevaEtapa, observaciones}` — avanza en el embudo |
| GET | `/api/v1/crm/prospectos/{prospectoId}/oportunidades` | Oportunidades de un prospecto |
| POST | `/api/v1/crm/interacciones` | `{prospectoId, tipoInteraccion: LLAMADA|EMAIL|VISITA, resumen}` |
| GET | `/api/v1/crm/prospectos/{prospectoId}/interacciones` | Historial |

**Garantías** — `@RequestMapping("/api/crm/garantias")` _(sin `/v1/`)_

| Método | Path | Roles | Descripción |
|---|---|---|---|
| GET | `/api/crm/garantias` | Autenticado | Listar tickets |
| POST | `/api/crm/garantias` | Autenticado | `{clienteId, ventaId, skuInterno, descripcionProblema}` |
| GET | `/api/crm/garantias/cliente/{clienteId}` | Autenticado | Por cliente |
| GET | `/api/crm/garantias/{ticketId}` | Autenticado | Obtener |
| PUT | `/api/crm/garantias/{ticketId}/estado` | Autenticado | Cambiar estado |
| PUT | `/api/crm/garantias/{ticketId}/resolver` | Autenticado | `{resolucion, observaciones}` |

**NPS** — `/api/v1/crm/nps`

| Método | Path | Roles | Descripción |
|---|---|---|---|
| POST | `/api/v1/crm/nps/encuestas` | Autenticado | `{clienteId, ventaId}` — genera encuesta |
| POST | `/api/v1/crm/nps/respuestas` | **PÚBLICO** | `{encuestaId, puntuacion: 0-10, comentario}` — clientes responden sin login |
| GET | `/api/v1/crm/nps/dashboard` | Autenticado | Score NPS, distribución Promotores/Pasivos/Detractores |

**Marketing** — `/api/v1/marketing/campanas`

| Método | Path | Descripción |
|---|---|---|
| POST | `/api/v1/marketing/campanas` | `{nombre, tipo: EMAIL|SMS, segmento, mensaje}` |
| POST | `/api/v1/marketing/campanas/{id}/ejecutar` | Dispara envío masivo (email o Telegram) |
| GET | `/api/v1/marketing/campanas/{id}/metricas` | Enviados, abiertos, errores |

---

### 6.17 Métricas (4 módulos)

**Métricas Financieras** — `/api/v1/metricas-financieras`

| Método | Path | Descripción |
|---|---|---|
| GET | `/api/v1/metricas-financieras` | Listar registros históricos |
| POST | `/api/v1/metricas-financieras/analisis` | `{sucursalId, fechaInicio, fechaFin}` |
| POST | `/api/v1/metricas-financieras/comparacion` | Comparar dos periodos |
| GET | `/api/v1/metricas-financieras/top-productos` | Top 10 por ingreso bruto |
| GET | `/api/v1/metricas-financieras/historico` | Serie temporal de snapshots |
| GET | `/api/v1/metricas-financieras/dashboard-ejecutivo` | KPIs ejecutivos consolidados |
| GET | `/api/v1/metricas-financieras/sucursal/{sucursalId}` | Por sucursal |
| GET | `/api/v1/metricas-financieras/health` | Health check del módulo |

**Métricas de Inventario** — `/api/v1/metricas/inventario`

| Método | Path | Descripción |
|---|---|---|
| GET | `/api/v1/metricas/inventario` | Resumen (valor total, productos sin stock, etc.) |
| POST | `/api/v1/metricas/inventario/generar` | Genera snapshot del inventario |
| GET | `/api/v1/metricas/inventario/valor-actual` | Valor total `sum(stock_actual * costo_promedio_ponderado)` |
| GET | `/api/v1/metricas/inventario/productos/bajo-stock` | Productos con stock bajo |
| GET | `/api/v1/metricas/inventario/productos/sin-stock` | `stock_actual = 0` |
| GET | `/api/v1/metricas/inventario/productos/proximos-caducar` | Vencen en 30 días |
| GET | `/api/v1/metricas/inventario/historico` | Histórico de snapshots |

**Métricas Operativas** — `/api/v1/metricas/operativas`

| Método | Path | Descripción |
|---|---|---|
| POST | `/api/v1/metricas/operativas/analisis` | `{fechaInicio, fechaFin, sucursalId}` |
| GET | `/api/v1/metricas/operativas/mes-actual` | Mes en curso |
| GET | `/api/v1/metricas/operativas/mes-anterior` | Mes anterior |
| GET | `/api/v1/metricas/operativas/ultimos-7-dias` | Últimos 7 días |
| POST | `/api/v1/metricas/operativas/por-sucursal/{sucursalId}` | Por sucursal |
| POST | `/api/v1/metricas/operativas/generar-guardar` | Genera y persiste snapshot |
| GET | `/api/v1/metricas/operativas/consolidado` | Consolidado multi-sucursal |
| GET | `/api/v1/metricas/operativas/historial` | Histórico de snapshots |
| POST | `/api/v1/metricas/operativas/analisis-consolidado-con-detalle` | Con desglose por sucursal |

**Métricas Ventas/Clientes** — `/api/metricas/ventas-clientes` _(sin `/v1/`)_

| Método | Path | Descripción |
|---|---|---|
| POST | `/api/metricas/ventas-clientes/analisis` | `{sucursalId, fechaInicio, fechaFin}` |
| GET | `/api/metricas/ventas-clientes/consolidado` | Consolidado |
| GET | `/api/metricas/ventas-clientes/historial` | Histórico |
| POST | `/api/metricas/ventas-clientes/generar-guardar` | Genera y persiste |
| GET | `/api/metricas/ventas-clientes/mes-actual` | Mes actual |
| GET | `/api/metricas/ventas-clientes/mes-anterior` | Mes anterior |
| GET | `/api/metricas/ventas-clientes/ultimos-7-dias` | Últimos 7 días |
| POST | `/api/metricas/ventas-clientes/por-sucursal/{sucursalId}` | Por sucursal |

---

### 6.18 Rentabilidad

**Base:** `/api/v1/rentabilidad`

| Método | Path | Descripción |
|---|---|---|
| POST | `/api/v1/rentabilidad/venta/{ventaId}` | Calcula rentabilidad de una venta (margen = ingresos - CPP) |
| GET | `/api/v1/rentabilidad/venta/{ventaId}` | Obtener rentabilidad calculada |
| POST | `/api/v1/rentabilidad/productos` | `{sucursalId, fechaInicio, fechaFin}` |
| GET | `/api/v1/rentabilidad/productos/mas-rentables` | `?top=10&sucursalId=1` |
| GET | `/api/v1/rentabilidad/productos/menos-rentables` | `?top=10&sucursalId=1` |
| GET | `/api/v1/rentabilidad/ventas/bajo-costo` | Ventas con margen bruto < 10% |
| GET | `/api/v1/rentabilidad/estadisticas` | `?sucursalId=1` — promedio de margen, top SKU |

---

### 6.19 Analítica

**RFM** — `/api/v1/analitica/rfm`

| Método | Path | Descripción |
|---|---|---|
| POST | `/api/v1/analitica/rfm/calcular` | Calcula recencia, frecuencia, monto para todos los clientes |
| GET | `/api/v1/analitica/rfm/segmentos` | Segmentos: Campeones, Leales, En Riesgo, Perdidos |
| GET | `/api/v1/analitica/rfm/cliente/{id}` | Score RFM individual |

**Churn** — `/api/v1/analitica/churn`

| Método | Path | Descripción |
|---|---|---|
| POST | `/api/v1/analitica/churn/calcular` | Calcula probabilidad de abandono por cliente |
| GET | `/api/v1/analitica/churn/en-riesgo` | Clientes con score > umbral |

**Market Basket** — `/api/v1/analitica/canasta`

| Método | Path | Descripción |
|---|---|---|
| POST | `/api/v1/analitica/canasta/calcular` | Calcula reglas de asociación (productos que se compran juntos) |
| GET | `/api/v1/analitica/canasta/{sku}` | Sugerencias de cross-sell para un SKU |

**Rendimiento Personal** — `/api/v1/analitica/personal`

| Método | Path | Descripción |
|---|---|---|
| POST | `/api/v1/analitica/personal/calcular` | Calcula score de rendimiento por vendedor |
| GET | `/api/v1/analitica/personal/rendimiento` | Ranking general |
| GET | `/api/v1/analitica/personal/{empleadoId}/tendencia` | Tendencia histórica de un empleado |

---

### 6.20 Fidelidad

**Base:** `/api/v1/fidelidad`

| Método | Path | Descripción |
|---|---|---|
| POST | `/api/v1/fidelidad/programa` | `{clienteId, nivel: BRONCE|PLATA|ORO}` |
| GET | `/api/v1/fidelidad/programa/cliente/{clienteId}` | Programa activo, puntos acumulados |
| POST | `/api/v1/fidelidad/acumular` | `{clienteId, montoCompra, ventaId}` — suma puntos |
| POST | `/api/v1/fidelidad/canjear` | `{clienteId, puntosACanjear, ventaId}` — descuenta puntos |
| GET | `/api/v1/fidelidad/historial/cliente/{clienteId}` | Movimientos de puntos |
| GET | `/api/v1/fidelidad/calcular-descuento` | Calcula descuento equivalente a un monto de puntos |
| GET | `/api/v1/fidelidad/estadisticas` | Total puntos activos, canjes, etc. |
| PATCH | `/api/v1/fidelidad/programa/cliente/{clienteId}/desactivar` | Desactivar |
| PATCH | `/api/v1/fidelidad/programa/cliente/{clienteId}/reactivar` | Reactivar |

---

### 6.21 Predicción de Demanda

**Base:** `/api/predicciones` _(sin `/v1/`)_

| Método | Path | Request | Descripción |
|---|---|---|---|
| POST | `/api/predicciones/generar` | `{skuProducto, sucursalId, periodos, algoritmo: PROMEDIO_MOVIL}` | Genera predicción estadística |
| GET | `/api/predicciones/{id}` | — | Obtener predicción por ID |
| GET | `/api/predicciones/producto/{skuProducto}` | — | Predicción activa de un SKU |
| GET | `/api/predicciones/recomendaciones` | — | SKUs con stock proyectado < stock mínimo en N días |

---

### 6.22 Alertas del Sistema

**`AlertaController`** — `@RequestMapping("/api/v1/alertas")`

| Método | Path | `@PreAuthorize` | Descripción |
|---|---|---|---|
| GET | `/api/v1/alertas/mis-alertas/{usuarioId}` | `isAuthenticated()` | Alertas no leídas del usuario |
| GET | `/api/v1/alertas/sucursal/{sucursalId}` | `ADMIN, GERENTE` | Alertas no resueltas de la sucursal |
| GET | `/api/v1/alertas/badge/{usuarioId}` | `isAuthenticated()` | Contador no leídas para badge en UI |
| PUT | `/api/v1/alertas/{alertaId}/resolver` | `ADMIN, GERENTE` | Cierre definitivo de alerta (devuelve `204 No Content`) |
| PUT | `/api/v1/alertas/{alertaId}/leer` | `isAuthenticated()` | Marca como leída (devuelve `204 No Content`) |
| POST | `/api/v1/alertas/configurar-sucursal` | `ADMIN` | `@Valid ConfigurarAlertaRequest` — umbrales por sucursal |
| POST | `/api/v1/alertas/configurar-canal` | `isAuthenticated()` | `@Valid ConfigNotificacionRequest{canal: EMAIL|TELEGRAM}` |

---

## 7. Seguridad y Autenticación

### Configuración de Seguridad (`SecurityConfig`)

```
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)   // Activa @PreAuthorize / @PostAuthorize
```

**Endpoints Públicos (sin JWT):**

```
/api/v1/auth/**                   — Login
/api/v1/crm/nps/respuestas        — Clientes responden encuestas NPS sin cuenta
/actuator/**                      — Salud y métricas de Actuator
/h2-console/**                    — H2 (solo dev/tests)
/swagger-ui/**                    — Swagger UI
/v3/api-docs/**                   — OpenAPI JSON
/swagger-ui.html                  — Swagger redirect
```

**Todo lo demás:** `.anyRequest().authenticated()` — requiere JWT válido.

### JWT (`JwtUtil`)

| Aspecto | Detalle |
|---|---|
| Algoritmo | HMAC-SHA256 (HS256) mediante `io.jsonwebtoken.security.Keys.hmacShaKeyFor()` |
| Expiración | **1 hora** (3,600,000 ms, configurado en `jwt.expiration: 3600000`) |
| Clave mínima | 32 caracteres (256 bits) — validado en `@PostConstruct` |
| Subject | `username` del usuario autenticado |
| Campos del payload | `sub` (username), `iat` (issued at), `exp` (expiration) |

**Validación del Secret en `@PostConstruct`:**

1. Falla si `secret.length() < 32` → `IllegalStateException`
2. En perfil de producción (sin `dev` o `test`), falla si el secret contiene palabras como `test`, `dev`, `demo`, `example`, `sample`
3. En producción, falla si el secret es solo letras (sin números/símbolos)
4. Logea longitud del secret (sin exponerlo)

**Cabecera requerida:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
Content-Type: application/json
```

### CORS (`CorsConfigurationSource`)

Configurable vía variables de entorno (sin reinicio si se usan env vars del SO):

| Variable de Entorno | Default | Descripción |
|---|---|---|
| `CORS_ALLOWED_ORIGINS` | `http://localhost:3000,http://localhost:4200` | Orígenes permitidos (separados por coma) |
| `CORS_ALLOWED_METHODS` | `GET,POST,PUT,DELETE,OPTIONS,PATCH` | Métodos HTTP |
| `CORS_ALLOWED_HEADERS` | `*` | Headers permitidos |
| `CORS_ALLOW_CREDENTIALS` | `true` | Permitir cookies y Authorization header |
| `CORS_MAX_AGE` | `3600` | Cache del preflight (segundos) |

Headers expuestos al cliente: `Authorization`, `X-Total-Count`, `X-Page-Number`, `X-Page-Size`.

### RBAC — Modelo de Entidades

```
Usuario (tabla: usuarios)
  @ManyToMany → Rol (tabla: rol) — via tabla puente usuario_rol
  @ManyToMany → Sucursal — via tabla puente usuario_sucursal (restringe visibilidad)

Rol (tabla: rol)
  @ManyToMany → Permiso (tabla: permiso) — via tabla puente rol_permiso
```

`@PreAuthorize` evalúa el rol del usuario del JWT. Spring Security extrae los roles del `UserDetails` cargado por `UserDetailsServiceImpl`.

### Passwords

`BCryptPasswordEncoder` strength 10.  
Seed admin: `admin123` → hash `$2a$10$k/mIluuONgJLE8efmX6Cse4/k8aUv5dvUqsCjJmxXKELm6ZMPZqsm`

### Manejo de Errores de Seguridad

| Exception | HTTP | Código | Descripción |
|---|---|---|---|
| `BadCredentialsException` | 401 | UNAUTHORIZED | Login con credenciales incorrectas |
| `UsernameNotFoundException` | 401 | UNAUTHORIZED | Usuario no existe |
| Sin token / token expirado | 401 | — | Respuesta automática de Spring Security |
| Sin permisos (`@PreAuthorize` falla) | 403 | FORBIDDEN | Token válido pero rol insuficiente |
| `ResourceNotFoundException` | 404 | No encontrado | Recurso no existe en BD |
| `DataIntegrityViolationException` (duplicate key) | 409 | — | SKU, RFC o email duplicado |
| `StockInsuficienteException` | 409 | — | Stock insuficiente al vender |
| `CreditoInsuficienteException` | 409 | — | Límite de crédito superado |
| `MethodArgumentNotValidException` | 400 | Datos invalidos | Validación de `@Valid` con lista de campos |
| Cualquier otra `Exception` | 500 | Error Interno | Log con stack trace, mensaje genérico al cliente |

---

## 8. Caché y Performance

### CacheConfig — Graceful Degradation

La clase `CacheConfig` implementa `CachingConfigurer` únicamente para proveer un `CacheErrorHandler` personalizado. Si el caché (Redis cuando esté habilitado) falla, la aplicación **no lanza excepción** — simplemente omite el caché y consulta directamente en PostgreSQL, logueando un `WARN`.

Esto garantiza:
- Desarrollo sin Redis instalado — funciona normalmente
- Producción resiliente — si Redis cae, la app no crashea
- Logs claros para diagnóstico

### Índices PostgreSQL (V1 — 25 índices)

Creados en `V1__initial_schema.sql`:

| Índice | Tabla / Columna(s) | Propósito |
|---|---|---|
| `idx_venta_sucursal_fecha` | `venta(sucursal_id, fecha_venta DESC)` | Reportes de ventas diarios |
| `idx_venta_cliente` | `venta(cliente_id)` | Historial por cliente |
| `idx_venta_vendedor` | `venta(vendedor_id)` | Comisiones por vendedor |
| `idx_compra_proveedor_fecha` | `compra(proveedor_id, fecha_compra DESC)` | Historial compras por proveedor |
| `idx_compra_sucursal` | `compra(sucursal_id)` | Compras por sucursal |
| `idx_inventario_sku` | `inventario_sucursal(sku_interno)` | Búsquedas de stock |
| `idx_inventario_sucursal` | `inventario_sucursal(sucursal_id)` | Inventario por sucursal |
| `idx_historial_precio_sku_fecha` | `historial_precio(sku_interno, fecha_calculo DESC)` | Último precio del SKU |
| `idx_precio_especial_sku_tipo` | `precio_especial(sku_interno, tipo_cliente_id)` | Lookup dinámico en venta |
| `idx_producto_categoria` | `producto_maestro(categoria_id)` | Filtro por categoría |
| `idx_producto_proveedor` | `producto_maestro(proveedor_id)` | Filtro por proveedor |
| `idx_producto_nombre` | `producto_maestro(nombre_comercial)` | Búsqueda por nombre |
| `idx_producto_marca` | `producto_maestro(marca)` | Filtro por marca |
| `idx_producto_activo` | `producto_maestro(activo)` | Soft delete filter |
| `idx_proveedor_nombre` | `proveedor(nombre_empresa)` | Búsqueda de proveedores |
| `idx_categoria_nombre` | `categoria(nombre)` | Búsqueda de categorías |
| `idx_moto_marca` | `moto(marca)` | Filtro compatibilidad |
| `idx_moto_modelo` | `moto(modelo)` | Filtro compatibilidad |
| `idx_moto_cilindrada` | `moto(cilindrada)` | Filtro compatibilidad |
| `idx_moto_marca_modelo_cilindrada` | `moto(marca, modelo, cilindrada)` | Búsqueda compuesta |
| `idx_compatibilidad_sku` | `compatibilidad_producto(sku_interno)` | Motos de un producto |
| `idx_compatibilidad_moto` | `compatibilidad_producto(moto_id)` | Productos de una moto |
| `idx_compatibilidad_anios` | `compatibilidad_producto(anio_inicio, anio_fin)` | Filtro por año |
| `idx_movimiento_sucursal_fecha` | `movimiento_inventario(sucursal_id, fecha_movimiento DESC)` | Trazabilidad |
| `idx_movimiento_rastreo` | `movimiento_inventario(rastreo_id)` | Lookup por UUID |

### Índices PostgreSQL (V33 — 14 índices adicionales de performance)

Creados en `V33__performance_indexes.sql`:

| Índice | Tabla / Columna(s) | Propósito |
|---|---|---|
| `idx_producto_busqueda` | `producto_maestro(nombre_comercial, marca)` | Búsqueda omnicanal en POS |
| `idx_producto_sensibilidad` | `producto_maestro(sensibilidad_precio)` | Filtro pricing |
| `idx_inventario_sucursal_sku` | `inventario_sucursal(sku_interno)` | JOINs de CPP |
| `idx_inventario_sucursal_id` | `inventario_sucursal(sucursal_id)` | JOINs de sucursal |
| `idx_cliente_rfc` | `cliente(rfc)` | Validación fiscal |
| `idx_cliente_nombre` | `cliente(nombre)` | Búsqueda en POS |
| `idx_cliente_bloqueado` | `cliente(bloqueado)` | Filtro morosidad |
| `idx_crm_oportunidad_prospecto` | `oportunidad_venta(prospecto_id)` | Pipeline B2B |
| `idx_crm_oportunidad_etapa` | `oportunidad_venta(etapa)` | Kanban de oportunidades |
| `idx_historial_precio_sku` | `historial_precio(sku_interno)` | Dashboards ejecutivos |
| `idx_detalle_compra_sku` | `detalle_compra(sku_interno)` | Métricas de costo |
| `idx_detalle_orden_compra_sku` | `detalle_orden_compra(sku_interno)` | Seguimiento de OC |
| `idx_venta_fecha` | `venta(fecha_venta)` | Reportes de ventas por periodo |
| `idx_turno_caja_estado` | `turno_caja(estado)` | Turnos abiertos en POS |

**Total:** 39 índices declarados en código.

### Configuración de Batch JPA (`application.yml`)

```yaml
jpa:
  show-sql: false               # En producción
  properties:
    hibernate:
      format_sql: true
      generate_statistics: true  # Detecta N+1 queries en logs
      jdbc:
        batch_size: 20           # Agrupa hasta 20 inserts/updates por round-trip
      order_inserts: true        # Ordena inserts por tipo de entidad para aprovechar batch
      order_updates: true
```

---

## 9. Notificaciones y Alertas Automáticas

### Canales de Notificación

| Canal | Clase | Estado | Variable |
|---|---|---|---|
| In-app (BD) | `AlertaSistema` entity | **Siempre activo** | — |
| Email (Gmail) | `GmailNotificacionService` | **Activo** si `MAIL_PASSWORD` configurado | `MAIL_USERNAME`, `MAIL_PASSWORD` |
| Telegram | `TelegramNotificacionService` | **Deshabilitado** por default | `TELEGRAM_ENABLED=true`, `TELEGRAM_BOT_TOKEN` |
| SSE Stream | `AlertaController` | **Activo** | Endpoint: `GET /api/v1/alertas/stream/SSE` |

### Scheduler Automático (`AlertaScheduler`)

4 Jobs con expresiones cron Spring:

| Job | Cron | Descripción |
|---|---|---|
| `verificarStockBajo()` | `"0 0 * * * *"` | **Cada hora** — compara `stockActual` vs `ConfiguracionAlerta.stockMinimo` por sucursal |
| `verificarProximosCaducar()` | `"0 0 7 * * *"` | **Diario 07:00** — productos con `fechaCaducidad <= hoy + 30 días` |
| `verificarCxCVencidas()` | `"0 0 8 * * *"` | **Diario 08:00** — CxC vencidas (stub, pendiente integración CxC) |
| `verificarMetasVentas()` | `"0 0 9 1 * *"` | **1ro de cada mes 09:00** — avance de metas de ventas vs umbral |

### Tipos de Alerta (`TipoAlerta` enum)

- `STOCK_BAJO` — Stock por debajo del mínimo
- `PRODUCTO_POR_CADUCAR` — Fecha de caducidad próxima (< 30 días)
- `CXC_VENCIDA` — Cuenta por cobrar vencida
- `META_VENTAS_BAJA` — Avance de ventas < umbral de meta

### Configuración por Sucursal (`ConfiguracionAlerta`)

Campos configurables vía `POST /api/v1/alertas/configurar-sucursal`:

- `sucursalId`
- `stockMinimo` — umbral global de stock bajo para la sucursal
- `diasVencimientoCxC` — días de gracia para CxC
- `porcentajeMetaAlerta` — % de avance mínimo antes de alertar
- `activo` — habilita/deshabilita alertas para la sucursal

### Gmail SMTP (`application.yml`)

```yaml
mail:
  host: smtp.gmail.com
  port: 587
  properties:
    mail.smtp.auth: true
    mail.smtp.starttls.enable: true
    mail.smtp.starttls.required: true
    mail.transport.protocol: smtp
```

Se requiere **App Password de Google** (16 caracteres) en la variable `MAIL_PASSWORD`. No sirve la contraseña normal de Gmail.

---

## 10. Testing y Calidad

### Resultados

| Métrica | Valor |
|---|---|
| Total tests | **950+** |
| Tests pasando | **100%** |
| Cobertura instrucciones | **68%** |
| Cobertura ramas | **50%** |
| Tests con H2 | Todos los de integración (sin PostgreSQL) |

### Framework

- **JUnit 5.10.1 (Jupiter)** — anotaciones `@Test`, `@BeforeEach`, `@AfterEach`
- **Mockito** — `@MockBean` para aislar dependencias
- **Spring Boot Test** — `@SpringBootTest`, `@WebMvcTest`, `@DataJpaTest`
- **Spring Security Test** — `@WithMockUser` para tests con roles

### Exclusiones de JaCoCo

El plugin JaCoCo en `build.gradle.kts` excluye:

```kotlin
exclude("**/dto/**")
exclude("**/entity/**")
exclude("**/mapper/**")
exclude("**/config/**")
exclude("**/security/**")
exclude("**/exception/**")
exclude("**/*Application*")
exclude("**/*Id*")          // Claves compuestas (InventarioSucursalId, etc.)
exclude("**/specification/**")
```

El **68%** refleja exclusivamente la cobertura de **Servicios y Controladores** (la lógica real de negocio).

### Comandos

```bash
# Solo tests
.\gradlew test

# Tests + reporte HTML de JaCoCo (tasks.test finalizedBy tasks.jacocoTestReport)
.\gradlew test jacocoTestReport

# Reporte en:
# app/build/reports/jacoco/test/html/index.html

# Limpiar caché de build antes de tests (si hay archivos bloqueados)
.\gradlew clean test
```

### QA E2E Scripts

```bash
# Flujo principal (40 pasos): login, catálogos, inventario, venta, reserva, devolución, crédito, CRM, predicción, RFM
powershell -ExecutionPolicy Bypass -File .\run-qa-flows.ps1
# Genera: reporte-qa-completo.md

# Flujo extendido (113 pasos): ERP, analítica avanzada, nómina, CFDI, offline sync, métricas
powershell -ExecutionPolicy Bypass -File .\run-qa-extended.ps1
# Genera: reporte-qa-extendido.md
```

**Criterio de falla:** Únicamente `HTTP 500`. Los códigos 400, 404, 405, 409 se consideran respuestas válidas de negocio.

---

## 11. Despliegue y Configuración Completa

### Docker Compose (`docker-compose.yml`)

```yaml
services:
  postgres-db:
    image: postgres:15-alpine
    environment:
      POSTGRES_DB: nexoohub_almacen
      POSTGRES_USER: nexoohub_user
      POSTGRES_PASSWORD: ${DB_PASSWORD}
    ports:
      - "5432:5432"

  java-backend:
    build: .
    ports:
      - "8080:8080"
    depends_on:
      - postgres-db
    environment:
      DB_URL: jdbc:postgresql://postgres-db:5432/nexoohub_almacen
      DB_USER: nexoohub_user
      DB_PASSWORD: ${DB_PASSWORD}
      JWT_SECRET: ${JWT_SECRET}
      MAIL_USERNAME: ${MAIL_USERNAME}
      MAIL_PASSWORD: ${MAIL_PASSWORD}
      TELEGRAM_ENABLED: ${TELEGRAM_ENABLED:-false}
      TELEGRAM_BOT_TOKEN: ${TELEGRAM_BOT_TOKEN:-}
```

### Todas las Variables de Entorno

| Variable | Default en `application.yml` | Obligatoria en Producción |
|---|---|---|
| `DB_URL` | `jdbc:postgresql://localhost:5432/postgres` | No (Docker usa `postgres-db`) |
| `DB_USER` | `user` | No |
| `DB_PASSWORD` | *(vacío)* | **Sí** |
| `JWT_SECRET` | `NexooHubSuperSecretKeyParaFirmaDeTokens2026!` | **Sí** (cambiar por uno seguro) |
| `MAIL_USERNAME` | `nexoohub.erp@gmail.com` | No |
| `MAIL_PASSWORD` | *(vacío)* | Para emails (App Password 16 chars) |
| `MAIL_FROM` | `nexoohub.erp@gmail.com` | No |
| `TELEGRAM_ENABLED` | `false` | No |
| `TELEGRAM_BOT_TOKEN` | *(vacío)* | Para Telegram |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:3000,http://localhost:4200` | Sí (cambiar a dominio de producción) |
| `CORS_ALLOWED_METHODS` | `GET,POST,PUT,DELETE,OPTIONS,PATCH` | No |
| `CORS_ALLOWED_HEADERS` | `*` | No |
| `CORS_ALLOW_CREDENTIALS` | `true` | No |
| `CORS_MAX_AGE` | `3600` | No |

### Ciclo de Despliegue Completo

```bash
# 1. Compilar el .jar
.\gradlew bootJar
# Salida: app/build/libs/app-0.0.1-SNAPSHOT.jar

# 2. Levantar contenedores (build de imagen + inicio)
docker-compose up -d --build

# 3. Verificar estado
docker-compose ps
docker-compose logs -f java-backend

# 4. Verificar salud
curl http://localhost:8080/actuator/health

# 5. Primer login
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

### URLs de Acceso

| Recurso | URL |
|---|---|
| API REST | `http://localhost:8080/api/v1/` |
| Swagger UI | `http://localhost:8080/swagger-ui.html` |
| OpenAPI JSON | `http://localhost:8080/v3/api-docs` |
| Actuator Health | `http://localhost:8080/actuator/health` |
| Actuator Metrics | `http://localhost:8080/actuator/metrics` |
| Actuator Info | `http://localhost:8080/actuator/info` |

### Logging (`application.yml`)

```yaml
logging:
  level:
    root: INFO
    com.nexoohub.almacen: INFO
    org.hibernate.SQL: DEBUG            # Muestra todas las queries SQL
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE  # Muestra valores bindados
    org.hibernate.stat: DEBUG           # Estadísticas Hibernate (detecta N+1)
    org.springframework.orm.jpa: DEBUG  # Transacciones JPA
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level [%X{traceId}] %logger{36} - %msg%n"
    # %X{traceId} = UUID inyectado por LogFilter en cada request
```

---

## 12. Consideraciones y Deuda Técnica

### Inconsistencia de Prefijos de URL

No todos los módulos usan `/api/v1/`. Los siguientes usan `/api/` sin versión:

| Módulo | Prefijo Real | Causa |
|---|---|---|
| Cotizaciones | `/api/cotizaciones` | Migrado antes de estandarizar versioning |
| Comisiones | `/api/comisiones` | Ídem |
| Crédito | `/api/credito` | Ídem |
| Garantías CRM | `/api/crm/garantias` | Módulo CRM creado en dos partes |
| Métricas ventas-clientes | `/api/metricas/ventas-clientes` | Ídem |
| Predicciones | `/api/predicciones` | Ídem |
| Alertas lento movimiento | `/api/alertas/lento-movimiento` | Ídem |
| Comparador precios SUP | `/api/sup/comparador` | Namespace de proveedor |

**Recomendación:** Estandarizar todo bajo `/api/v1/` en una futura refactorización.

### Controladores V1 Heredados (Duplicados)

| Heredado | Activo | Estado |
|---|---|---|
| `MorosidadV1Controller` | `MorosidadController` | Ambos activos — posible conflicto de rutas |
| `MarketBasketV1Controller` | `MarketBasketController` | Ídem |
| `RendimientoPersonalV1Controller` | `RendimientoPersonalController` | Ídem |
| `NpsV1Controller` | `NpsController` | Ídem |
| `GarantiasV1Controller` | `GarantiaController` | Ídem |
| `OrdenCompraV1Controller` | `OrdenCompraController` | Ídem |

**Recomendación:** Verificar si los V1 están en uso activo en frontends; si no, eliminar.

### Entidad `Empleado` Duplicada

- `empleados/entity/Empleado.java` — Empleado operativo (sucursal, comisiones)
- `erp/entity/Empleado.java` — Empleado en nómina ERP (datos salariales)

Ambas mapean a la tabla `empleado` con propósitos distintos. Funciona, pero genera confusión durante el mantenimiento.

### Redis No Habilitado

`RedisConfig.java` existe pero está **completamente comentado** y protegido con `@ConditionalOnProperty`. `CacheConfig` implementa `CachingConfigurer` solo para el error handler de degradación.

Para habilitar Redis en el futuro:
1. Descomentar `RedisConfig.java`
2. Agregar a `build.gradle.kts`: `implementation("org.springframework.boot:spring-boot-starter-data-redis")`
3. Agregar servicio `redis` en `docker-compose.yml`
4. En `application.yml`: `spring.cache.type: redis`

### Rate Limiting Deshabilitado

`RateLimitingFilter.java.disabled` está renombrado para que no compile. Para activar:
1. Renombrar a `RateLimitingFilter.java`
2. Verificar e importar dependencia de Bucket4j o similar

### JWT Expira en 1 Hora

`jwt.expiration: 3600000` ms = 1 hora. Toda sesión de frontend debe manejar el refresh del token antes de expirar para evitar interrupciones en flujos largos.

### `generate_statistics: true` en Producción

`hibernate.generate_statistics: true` en `application.yml` genera overhead de logging en producción. Considerar cambiar a `false` en deploy de alta carga.

---

*© 2026 NexooHub Development Team*
