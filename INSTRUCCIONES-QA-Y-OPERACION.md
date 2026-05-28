# Guía Operacional de Despliegue, QA y Optimización — NexooHub Almacén

> Centraliza todas las instrucciones de operación, compilación, pruebas unitarias/integración (JUnit), validaciones de flujos de negocio E2E y optimizaciones de base de datos.  
> Actualización: Mayo 2026

---

## Estado Actual del Proyecto

| Indicador | Resultado |
|---|---|
| **Tests JUnit** | 950+ tests, **100% pasando** |
| **Cobertura JaCoCo** | **68% instrucciones / 50% ramas** (core lógico: servicios y controladores) |
| **Estabilidad API E2E** | **0 errores 500 / 0 caídas críticas** en 150+ llamadas validadas |
| **Índices BD** | **13 índices B-Tree** aplicados via Flyway V33 |
| **Migraciones Flyway** | **V1–V33** aplicadas y validadas |

---

## Requisitos Previos

- **Java Development Kit (JDK) 21** — para compilar fuera de Docker
- **Docker Desktop** — para el ciclo de base de datos y contenedores
- **PowerShell** — para ejecutar los scripts de QA E2E

---

## 1. Compilación y Despliegue

### A. Compilar el .jar de producción

```bash
# Windows
.\gradlew bootJar

# macOS / Linux
./gradlew bootJar
```

### B. Levantar los contenedores (PostgreSQL + Backend)

```bash
docker-compose up -d --build
```

> - **PostgreSQL** disponible en `localhost:5432`, base de datos `nexoohub_almacen`
> - **Backend Spring Boot** disponible en `http://localhost:8080`
> - Las 33 migraciones Flyway se ejecutan automáticamente al iniciar

### C. Verificar el estado

```bash
docker-compose ps

# Logs del backend
docker-compose logs -f java-backend

# Logs de la base de datos
docker-compose logs -f postgres-db
```

### D. Detener el sistema

```bash
docker-compose down

# Detener Y eliminar todos los datos (borra el volumen)
docker-compose down -v
```

### Puertos

| Servicio | Puerto |
|---|---|
| API REST | 8080 |
| Swagger UI | 8080/swagger-ui.html |
| OpenAPI JSON | 8080/v3/api-docs |
| Actuator Health | 8080/actuator/health |
| PostgreSQL | 5432 |

---

## 2. Pruebas Unitarias e Integración (JUnit + JaCoCo)

### A. Ejecutar toda la suite de tests

```bash
.\gradlew test
```

### B. Generar reporte de cobertura JaCoCo (HTML)

```bash
.\gradlew test jacocoTestReport
```

Ver reporte: `app/build/reports/jacoco/test/html/index.html`

### C. Solo compilar sin ejecutar tests

```bash
.\gradlew assemble
```

### Cobertura JaCoCo

La configuración en `build.gradle.kts` excluye del reporte:

- DTOs (`**/dto/**`)
- Entidades JPA (`**/entity/**`)
- Mappers (`**/mapper/**`)
- Configuración (`**/config/**`, `**/security/**`)
- Excepciones (`**/exception/**`)
- Clases de inicio y utilitarios simples

Esto hace que el **68%** reportado represente la cobertura real sobre la **lógica de negocio** (Servicios y Controladores), que es lo que realmente importa medir.

---

## 3. Validación E2E de Endpoints (QA Scripts)

Dos scripts PowerShell en la raíz del proyecto ejecutan flujos de negocio completos contra la API.

### A. Flujo Principal de Negocio (40 pasos)

Simula un día de operación completo:
- Crear catálogos (categorías, clientes, proveedores, motos)
- Crear producto e inicializar inventario
- Registrar compra a crédito, verificar CxP
- Abrir turno de caja
- Crear cotización e intentar convertirla (válida el flujo de estados)
- Registrar venta directa
- Crear y cancelar reserva
- Registrar devolución
- Cerrar turno con arqueo Z
- Calcular comisiones del mes
- Establecer límite de crédito, bloquear cliente por mora, registrar abono (desbloqueo auto)
- Registrar prospecto B2B, oportunidad, interacción
- Generar predicción de demanda
- Calcular RFM

```powershell
powershell -ExecutionPolicy Bypass -File .\run-qa-flows.ps1
```

Genera: `reporte-qa-completo.md`

### B. Flujo Extendido (113 pasos)

Valida los controladores especializados de ERP, CRM avanzado y analítica:
- Dashboard ejecutivo
- Sucursales y usuarios
- Búsqueda y escaneo de productos
- Análisis ABC, alertas de lento movimiento, caducidad
- Actualización de precios y comparador
- Rentabilidad de productos
- Métricas financieras, inventario y operativas
- Nómina quincenal
- Facturación CFDI y terminal bancaria
- Encuestas NPS
- Sincronización offline
- Market Basket, análisis de rendimiento personal

```powershell
powershell -ExecutionPolicy Bypass -File .\run-qa-extended.ps1
```

Genera: `reporte-qa-extendido.md`

### C. Resultados Esperados

Los scripts usan un criterio amplio de éxito:

| Código HTTP | Categoría | ¿Es falla? |
|---|---|---|
| 200, 201, 202 | Éxito | No — operación correcta |
| 400 | Business rule | No — regla de negocio activa (ej. stock insuficiente) |
| 404 | Not found | No — recurso no existe aún (estado esperado en tests de consulta vacíos) |
| 405 | Method not allowed | No — endpoint correcto pero método HTTP no soportado |
| 409 | Conflict | No — duplicado controlado o estado inválido |
| 500 | Server error | **SÍ — falla crítica** |

El único resultado que cuenta como falla es un **500 Internal Server Error**.

---

## 4. Optimizaciones de Base de Datos

### Estrategia de Índices (Flyway V33)

La migración `V33__performance_indexes.sql` aplica 13 índices B-Tree automáticamente en todo despliegue o ambiente nuevo:

| Tabla | Columna | Beneficio |
|---|---|---|
| `producto_maestro` | `nombre_comercial` | Búsqueda por nombre en POS |
| `producto_maestro` | `marca` | Filtros por marca |
| `inventario_sucursal` | `sku_interno` | Cálculo de stock bajo y costos promedio |
| `oportunidad_venta` | `prospecto_id` | Carga del embudo de conversión B2B |
| `cliente` | `rfc` | Validación de límites de crédito |
| `cliente` | `nombre` | Búsqueda en punto de venta |
| `historial_precio` | `sku_interno` | Dashboards ejecutivos |
| `detalle_compra` | `sku_interno` | Métricas de costo promedio |
| `venta` | `fecha_venta` | Reportes de ventas por periodo |

### Configuración Batch JPA

En `application.yml` se activan inserts/updates en lote:

```yaml
hibernate:
  jdbc.batch_size: 20
  order_inserts: true
  order_updates: true
```

Esto reduce el número de round-trips a la base de datos al procesar operaciones masivas (importaciones, cálculos de comisiones, generación de métricas).

---

## 5. Configuración de Variables de Entorno

### Para Desarrollo Local (`.env` file o export)

```bash
export DB_PASSWORD=MiClaveSecreta999
export JWT_SECRET=NexooHubSuperSecretKey2026MinLength32Chars
export MAIL_PASSWORD=xxxx-xxxx-xxxx-xxxx   # App Password Gmail
```

### Para Producción

Usa variables de entorno del sistema operativo o un gestor de secretos. **Nunca** commits en el repositorio.

```bash
# Verificar que el backend recibió las variables
docker-compose exec java-backend env | grep -E "(DB_|JWT_|MAIL_)"
```

---

## 6. Resumen de Comandos Rápidos

| Objetivo | Comando |
|---|---|
| **Empaquetar** | `.\gradlew bootJar` |
| **Desplegar Docker** | `docker-compose up -d --build` |
| **Ver logs backend** | `docker-compose logs -f java-backend` |
| **Correr Tests JUnit** | `.\gradlew test` |
| **Cobertura HTML** | `.\gradlew test jacocoTestReport` |
| **QA Flujo Principal** | `powershell -ExecutionPolicy Bypass -File .\run-qa-flows.ps1` |
| **QA Flujo Extendido** | `powershell -ExecutionPolicy Bypass -File .\run-qa-extended.ps1` |
| **Detener sistema** | `docker-compose down` |
| **Limpiar volúmenes** | `docker-compose down -v` |

---

## 7. Troubleshooting

### El contenedor no arranca

```bash
# Ver logs detallados
docker-compose logs java-backend

# Causas comunes:
# 1. Puerto 8080 o 5432 ocupado → cambiar en docker-compose.yml
# 2. Falla en migración Flyway → revisar logs de "Flyway" en los logs
# 3. Variable de entorno no configurada → verificar docker-compose.yml
```

### Flyway falla al migrar

```bash
# Resetear el historial de Flyway (solo en desarrollo, nunca en producción)
docker-compose down -v
docker-compose up -d --build
```

### Tests fallan por conexión a BD

Los tests usan H2 en-memoria, no requieren PostgreSQL activo. Si fallan, verificar:

```bash
# Que el compilador incluya los parámetros de métodos (crítico para Spring)
# Esto está configurado en build.gradle.kts:
# options.compilerArgs.add("-parameters")
.\gradlew clean test
```

### La API responde 401 en todos los endpoints

```bash
# El token JWT expiró (duración: 24 horas). Hacer login nuevamente:
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

---

*© 2026 NexooHub Development Team*
