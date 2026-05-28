# NexooHub Almacén — Sistema Integral de Gestión Multi-Sucursal

> Sistema backend para cadenas de tiendas de refacciones de motocicletas. Gestión de inventario, ventas, CRM, ERP, analítica y más, en una sola API REST.

---

## Stack Tecnológico

| Componente | Tecnología |
|---|---|
| **Lenguaje** | Java 21 |
| **Framework** | Spring Boot 3.2.3 |
| **Base de Datos** | PostgreSQL 15 |
| **Migraciones** | Flyway (V1–V33) |
| **Seguridad** | Spring Security + JWT (JJWT 0.12.5) |
| **Build** | Gradle 8.7 (Kotlin DSL) |
| **Contenedores** | Docker + Docker Compose |
| **Documentación API** | SpringDoc OpenAPI / Swagger UI |
| **Testing** | JUnit 5 + Mockito + JaCoCo |
| **Cache** | Caffeine (in-process) |
| **Email** | Spring Mail (Gmail SMTP) |
| **Excel** | Apache POI 5.2.5 |

---

## Módulos del Sistema

| Módulo | Descripción |
|---|---|
| **Auth / RBAC** | JWT, usuarios, roles con permisos granulares |
| **Catálogo** | Categorías, clientes, proveedores, motos, compatibilidad, precios especiales |
| **Inventario** | Productos, stock multi-sucursal, traspasos, caducidad, códigos de barras, análisis ABC |
| **Ventas** | Ventas, reservas/apartados, devoluciones, cotizaciones |
| **Compras** | Ingreso de mercancía a crédito/contado |
| **Adquisiciones** | Comparador de precios, actualización masiva, carrito + OC en Excel |
| **Caja / POS** | Turnos de caja, arqueos, terminal bancaria, facturación CFDI, sync offline |
| **Crédito y Cobranza** | Límites de crédito, morosidad, abonos, bloqueo automático |
| **Finanzas** | Dashboard ejecutivo, configuración de parámetros, auditoría de precios |
| **ERP** | Contabilidad (pólizas), CxP, gastos operativos, logística, nómina, devoluciones a proveedor |
| **CRM** | Pipeline B2B (prospectos/oportunidades), garantías, encuestas NPS, campañas de marketing |
| **Comisiones / RH** | Reglas de comisión escalonadas, metas de ventas, cálculo automático |
| **Fidelidad** | Programa de puntos (acumulación, canje, niveles) |
| **Métricas** | Financieras, inventario, operativas, ventas/cliente |
| **Rentabilidad** | Por venta, por producto, margen, estadísticas |
| **Analítica** | RFM, Churn prediction, Market Basket, Rendimiento de personal |
| **Alertas** | Notificaciones en-app, email (Gmail), Telegram, SSE stream |
| **Predicción** | Demanda por producto/sucursal (promedio móvil y más) |

---

## Inicio Rápido

### Requisitos

- Docker Desktop
- JDK 21 (para compilar fuera de Docker)

### Despliegue con Docker

```bash
# 1. Compilar el .jar
./gradlew bootJar          # macOS/Linux
.\gradlew bootJar          # Windows

# 2. Levantar PostgreSQL + Backend
docker-compose up -d --build
```

La API queda disponible en `http://localhost:8080`.  
Swagger UI: `http://localhost:8080/swagger-ui.html`

### Variables de Entorno

| Variable | Default | Descripción |
|---|---|---|
| `DB_PASSWORD` | `MiClaveSecreta999` | Contraseña de PostgreSQL |
| `JWT_SECRET` | *(valor interno)* | Clave para firmar tokens JWT |
| `MAIL_USERNAME` | `nexoohub.erp@gmail.com` | Cuenta Gmail para alertas |
| `MAIL_PASSWORD` | *(vacío)* | App Password de Gmail |
| `TELEGRAM_ENABLED` | `false` | Habilitar notificaciones Telegram |
| `TELEGRAM_BOT_TOKEN` | *(vacío)* | Token del bot de Telegram |

### Primer Login

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

---

## Pruebas

```bash
# Ejecutar suite JUnit (950+ tests)
.\gradlew test

# Generar reporte de cobertura JaCoCo
.\gradlew test jacocoTestReport
# Reporte en: app/build/reports/jacoco/test/html/index.html

# QA E2E — Flujos de negocio completos
powershell -ExecutionPolicy Bypass -File .\run-qa-flows.ps1
powershell -ExecutionPolicy Bypass -File .\run-qa-extended.ps1
```

**Estado actual:** 950+ tests pasando | 0 errores 500 | 68% cobertura (core lógico)

---

## Documentación

| Archivo | Contenido |
|---|---|
| [DOCUMENTACION-COMPLETA.md](DOCUMENTACION-COMPLETA.md) | Arquitectura, módulos, endpoints, BD, seguridad, despliegue |
| [GUIA-FUNCIONAL-NEGOCIO.md](GUIA-FUNCIONAL-NEGOCIO.md) | Manual de usuario en lenguaje de negocio (sin términos técnicos) |
| [CURL-COMPLETO.md](CURL-COMPLETO.md) | Colección completa de ejemplos cURL para todos los endpoints |
| [INSTRUCCIONES-QA-Y-OPERACION.md](INSTRUCCIONES-QA-Y-OPERACION.md) | Guía de operación, QA y optimizaciones de BD |

---

## Estructura del Proyecto

```
nexoo-almacen-bk/
├── app/
│   ├── src/main/java/com/nexoohub/almacen/
│   │   ├── adquisiciones/   # Comparador, OC, actualización masiva de precios
│   │   ├── alertas/         # Sistema de notificaciones y alertas
│   │   ├── analitica/       # RFM, Churn, Market Basket, Rendimiento
│   │   ├── caja/            # Turnos de caja y arqueos
│   │   ├── catalogo/        # Catálogos base (clientes, motos, precios...)
│   │   ├── comisiones/      # Reglas de comisión y metas
│   │   ├── common/          # Auth, JWT, seguridad, excepciones
│   │   ├── compras/         # Ingresos de mercancía
│   │   ├── cotizaciones/    # Cotizaciones y conversión a venta
│   │   ├── crm/             # CRM: pipeline B2B, garantías, NPS, marketing
│   │   ├── empleados/       # Gestión de empleados
│   │   ├── erp/             # Contabilidad, logística, nómina, CxP
│   │   ├── fidelidad/       # Programa de puntos
│   │   ├── finanzas/        # Dashboard, crédito, config financiera
│   │   ├── inventario/      # Inventario, productos, traspasos, ABC
│   │   ├── metricas/        # Métricas financieras/inventario/operativas
│   │   ├── pos/             # Facturación CFDI, terminales, sync offline
│   │   ├── prediccion/      # Predicción de demanda
│   │   ├── rentabilidad/    # Análisis de rentabilidad
│   │   ├── sucursal/        # Gestión de sucursales
│   │   └── ventas/          # Ventas, reservas, devoluciones
│   └── src/main/resources/
│       ├── db/migration/    # 33 migraciones Flyway
│       └── application.yml
├── docker-compose.yml
├── Dockerfile
└── recursos/                # YAMLs de referencia OpenAPI por módulo
```

---

## Consideraciones Técnicas Importantes

- **Redis:** `RedisConfig.java` existe pero está deshabilitado (`@ConditionalOnProperty`). El caché activo usa **Caffeine** (in-process). Para habilitar Redis agregar `spring-boot-starter-data-redis` a `build.gradle.kts`.
- **Rate Limiting:** `RateLimitingFilter.java.disabled` está deshabilitado. Renombrar sin `.disabled` y agregar la dependencia para activarlo.
- **Controladores V1:** Existen variantes `*V1Controller` (MarketBasketV1, NpsV1, GarantiasV1, etc.) junto a versiones sin sufijo. Las versiones activas son las **sin sufijo V1**; las V1 son versiones heredadas que pueden eliminarse en un futuro refactor.
- **Entidad Empleado duplicada:** `empleados/entity/Empleado.java` y `erp/entity/Empleado.java` coexisten con propósitos distintos (empleado operativo vs. nómina ERP).

---

*© 2026 NexooHub Development Team*