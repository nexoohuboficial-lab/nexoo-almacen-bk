# Guía Funcional del Sistema — NexooHub Almacén

> **Manual para Usuarios de Negocio**  
> En lenguaje simple, sin términos técnicos  
> Actualización: Mayo 2026

---

## Índice

1. [¿Qué es NexooHub Almacén?](#1-qué-es-nexoohub-almacén)
2. [¿Para Quién es Este Sistema?](#2-para-quién-es-este-sistema)
3. [¿Qué Problemas Resuelve?](#3-qué-problemas-resuelve)
4. [Módulos del Sistema](#4-módulos-del-sistema)
5. [Flujos de Trabajo Principales](#5-flujos-de-trabajo-principales)
6. [Roles y Permisos](#6-roles-y-permisos)
7. [Beneficios del Sistema](#7-beneficios-del-sistema)
8. [Preguntas Frecuentes](#8-preguntas-frecuentes)

---

## 1. ¿Qué es NexooHub Almacén?

**NexooHub Almacén** es un sistema completo para administrar tu negocio de refacciones de motocicletas.

Es como un **gerente digital** que:
- Sabe en todo momento qué tienes en cada tienda
- Registra cada venta y compra
- Te avisa antes de que se te acabe algún producto
- Calcula automáticamente las comisiones de tus vendedores
- Te dice cuánto ganas con cada producto
- Predice qué vas a necesitar comprar el siguiente mes
- Maneja créditos, cobranza y fidelidad de clientes
- Lleva la contabilidad, nómina y logística de entregas

---

## 2. ¿Para Quién es Este Sistema?

Diseñado para negocios de refacciones de motocicletas que:

- Tienen **una o varias tiendas** (el sistema maneja múltiples sucursales)
- Quieren **controlar su inventario** sin errores manuales
- Necesitan **saber cuánto ganan** con cada venta
- Quieren **automatizar** el cálculo de comisiones
- Desean **tomar decisiones con datos reales**
- Tienen clientes a los que les **venden a crédito**
- Quieren **premiar a sus clientes frecuentes**

---

## 3. ¿Qué Problemas Resuelve?

| Problema Común | Cómo lo Resuelve NexooHub |
|---|---|
| "No sé cuántas piezas tengo en cada tienda" | Consultas de stock en tiempo real por sucursal |
| "Se me acaba la mercancía sin darme cuenta" | Alertas automáticas cuando el stock baja del mínimo |
| "Pierdo ventas porque no sé si tengo la pieza" | Búsqueda por nombre, código, o hasta por modelo de moto |
| "No sé qué productos me dejan más ganancia" | Cálculo automático de rentabilidad por producto y venta |
| "Calculo las comisiones a mano y me tardo" | Cálculo automático de comisiones al cerrar el mes |
| "Los clientes no regresan" | Programa de puntos de fidelidad que los premia |
| "Tengo productos viejos que no se venden" | Alertas de lento movimiento y análisis ABC |
| "Me fían y no me pagan" | Control de límites de crédito y bloqueo automático de morosos |
| "No puedo mover mercancía entre tiendas fácil" | Módulo de traspasos entre sucursales |
| "No sé cuáles son mis mejores clientes" | Análisis RFM: quién compra más, con qué frecuencia y cuánto |
| "No sé qué va a necesitar cada tienda el siguiente mes" | Predicción de demanda con algoritmos de inteligencia artificial |
| "Mis cotizaciones se pierden" | Gestión de cotizaciones con seguimiento y conversión a venta |
| "No controlo mis órdenes de compra con proveedores" | Carrito de compras, comparador de precios y generación de OC en Excel |
| "Mi equipo de ventas necesita objetivos claros" | Metas mensuales por vendedor con seguimiento de progreso |

---

## 4. Módulos del Sistema

El sistema tiene **21 módulos** organizados por área de negocio:

---

### Módulo 1: Catálogos

**¿Qué hace?**  
Es tu libreta de contactos y directorio del negocio. Aquí guardas toda la información base.

**Incluye:**
- **Categorías** de productos (aceites, frenos, llantas, etc.)
- **Clientes** con RFC, contacto y tipo de cliente
- **Proveedores** con contacto y datos de facturación
- **Motos** por marca, modelo y año (para buscar compatibilidad)
- **Tipos de cliente** (mayorista, menudeo, VIP, etc.)
- **Precios especiales** por tipo de cliente
- **Compatibilidad** entre productos y modelos de moto

---

### Módulo 2: Inventario

**¿Qué hace?**  
Controla exactamente cuántos productos tienes en cada tienda.

**Incluye:**
- Ficha maestra de cada producto (SKU, precio de compra, precio de venta, stock mínimo)
- Stock en tiempo real por sucursal
- Registro de cada entrada y salida de mercancía
- Búsqueda por código de barras o escáner
- Alertas cuando el stock está bajo
- Productos próximos a caducar / ya vencidos
- **Análisis ABC:** clasifica tus productos en A (mayor ingreso), B (medio) y C (menor impacto)

---

### Módulo 3: Traspasos

**¿Qué hace?**  
Te permite mover mercancía de una tienda a otra.

**¿Cuándo usarlo?**  
Cuando una sucursal se está quedando sin un producto pero otra tiene exceso.

---

### Módulo 4: Ventas

**¿Qué hace?**  
Registra todas las ventas de tus tiendas.

**Incluye:**
- Venta directa con cualquier método de pago
- **Reservas / Apartados:** el cliente separa un producto aunque no haya en ese momento
- **Devoluciones:** manejo de productos devueltos por el cliente

---

### Módulo 5: Cotizaciones

**¿Qué hace?**  
Genera presupuestos para tus clientes y los convierte en venta cuando aceptan.

**Flujo:**  
Crear cotización → Cliente la revisa → Aceptar → Se convierte en venta automáticamente

Tiene seguimiento de cuáles están vigentes, cuáles van a vencer y estadísticas de conversión.

---

### Módulo 6: Compras

**¿Qué hace?**  
Registra el ingreso de mercancía de tus proveedores.

Cuando recibes un pedido del proveedor, el sistema registra la entrada de inventario y, si fue a crédito, genera la cuenta por pagar correspondiente.

---

### Módulo 7: Adquisiciones (Compras Avanzadas)

**¿Qué hace?**  
Te ayuda a comprar de forma más inteligente con tus proveedores.

**Incluye:**
- **Comparador de precios:** ve qué proveedor tiene el mismo producto más barato
- **Actualización masiva de precios:** cuando un proveedor manda su lista, el sistema actualiza automáticamente tus costos
- **Carrito de compras:** arma tu pedido producto por producto antes de enviarlo
- **Órdenes de compra en Excel:** genera el documento formal para tu proveedor

---

### Módulo 8: Caja

**¿Qué hace?**  
Controla los turnos de caja de cada vendedor/cajero.

**Flujo del día:**  
Abrir turno → Registrar ventas y movimientos → Cerrar turno con arqueo Z (conteo de efectivo vs. lo que debería haber)

---

### Módulo 9: POS (Punto de Venta Avanzado)

**¿Qué hace?**  
Funcionalidades avanzadas de punto de venta para negocios más grandes.

**Incluye:**
- **Facturación electrónica (CFDI):** timbra facturas fiscales directamente desde el sistema
- **Terminal bancaria:** procesa pagos con tarjeta de crédito/débito
- **Modo offline:** si no hay internet, el sistema guarda las ventas y las sincroniza cuando vuelve la conexión

---

### Módulo 10: Crédito y Cobranza

**¿Qué hace?**  
Controla a qué clientes les fías, cuánto les debes y cuándo te pagan.

**Incluye:**
- Límites de crédito por cliente
- Bloqueo automático cuando supera el límite o se atrasa
- Historial de cargos y abonos
- Clientes morosos y bloqueados
- Validación automática en ventas: el sistema avisa si el cliente no puede comprar a crédito

---

### Módulo 11: Finanzas

**¿Qué hace?**  
Te da una visión clara del estado financiero de tu negocio.

**Incluye:**
- **Dashboard ejecutivo:** resumen de ventas, compras, utilidades y flujo del día
- **Auditoría de precios:** registro de cada cambio de precio, quién lo hizo y por qué
- **Configuración financiera:** define el margen de utilidad objetivo, máximo descuento permitido, días de crédito por defecto, etc.

---

### Módulo 12: ERP (Gestión Empresarial Avanzada)

**¿Qué hace?**  
Módulos empresariales para negocios que necesitan más control.

**Incluye:**

- **Contabilidad:** registro de pólizas contables, catálogo de cuentas, balanza de comprobación y estado de resultados
- **Cuentas por Pagar:** lleva el control de lo que debes a proveedores y registra los pagos
- **Gastos Operativos:** registra los gastos del negocio (renta, servicios, etc.)
- **Logística:** gestiona rutas de entrega, flota vehicular y choferes
- **Nómina:** calcula y genera recibos de nómina para tus empleados
- **Devoluciones a Proveedor:** maneja el proceso de devolver mercancía defectuosa o incorrecta al proveedor

---

### Módulo 13: CRM (Gestión de Relaciones con Clientes)

**¿Qué hace?**  
Herramientas para mantener y mejorar la relación con tus clientes y prospectos.

**Incluye:**

- **Pipeline B2B:** si vendes a talleres o distribuidores, registra prospectos, oportunidades de negocio y el historial de contacto
- **Garantías:** gestiona los tickets de garantía de productos, desde la apertura hasta la resolución
- **NPS (Satisfacción del Cliente):** envía encuestas de satisfacción y mide el índice de recomendación de tu negocio
- **Campañas de Marketing:** crea y ejecuta campañas de comunicación a tus clientes

---

### Módulo 14: Comisiones y Metas

**¿Qué hace?**  
Calcula automáticamente lo que le corresponde a cada vendedor.

**Incluye:**
- Reglas de comisión flexibles (porcentaje plano o por escalones según cuánto venda)
- Metas mensuales por vendedor
- Seguimiento del progreso de cada vendedor hacia su meta
- Cálculo automático al cierre del mes
- Aprobación y pago de comisiones

---

### Módulo 15: Programa de Fidelidad

**¿Qué hace?**  
Premia a tus clientes frecuentes con puntos canjeables.

**Flujo:**
1. Cliente compra → acumula puntos según el monto
2. Cliente acumula suficientes puntos → los canjea por descuento en su próxima compra
3. Hay niveles (Bronce, Plata, Oro) con beneficios distintos

---

### Módulo 16: Métricas

**¿Qué hace?**  
Genera reportes e indicadores detallados de las 4 áreas principales.

**Tipos de métricas:**
- **Financieras:** ingresos, utilidades, comparativo entre periodos
- **Inventario:** rotación, valor total, productos críticos
- **Operativas:** eficiencia por sucursal, balance de transacciones, últimos 7 días
- **Ventas y Clientes:** quiénes compran más, frecuencia, métodos de pago preferidos

---

### Módulo 17: Rentabilidad

**¿Qué hace?**  
Te dice cuánto ganas realmente con cada producto y cada venta.

**Incluye:**
- Rentabilidad por producto (margen real después de costos)
- Top de los productos más rentables
- Identificación de productos con margen peligrosamente bajo
- Ventas donde se vendió por debajo del costo

---

### Módulo 18: Analítica Avanzada

**¿Qué hace?**  
Aplica técnicas de análisis de datos para darte ventajas competitivas.

**Incluye:**
- **RFM (Recencia, Frecuencia, Monto):** clasifica a tus clientes en segmentos: Champions, Clientes Leales, En Riesgo, Perdidos, etc.
- **Churn Prediction:** predice qué clientes están a punto de irse con la competencia antes de que se vayan
- **Market Basket (Canasta de Productos):** descubre qué productos siempre se compran juntos para hacer bundles o sugerencias de venta cruzada
- **Rendimiento de Personal:** compara el desempeño de tus vendedores y detecta tendencias

---

### Módulo 19: Alertas del Sistema

**¿Qué hace?**  
Te notifica automáticamente cuando algo necesita tu atención.

**Canales disponibles:**
- Notificaciones dentro del sistema
- Correo electrónico (Gmail)
- Telegram (mensaje directo al celular)

**Tipos de alertas automáticas:**
- Producto por debajo del stock mínimo
- Producto próximo a caducar
- Producto sin movimiento por mucho tiempo
- Cliente que excedió su límite de crédito
- Reserva próxima a vencer sin recoger

---

### Módulo 20: Predicción de Demanda

**¿Qué hace?**  
Usa inteligencia artificial para predecir cuánto vas a vender de cada producto.

**¿Para qué sirve?**  
- Saber con anticipación cuándo y cuánto comprar
- Evitar quedarte sin stock en temporada alta
- Evitar comprar de más en temporada baja
- El sistema genera recomendaciones de cuándo reordenar

---

### Módulo 21: Sucursales y Empleados

**¿Qué hace?**  
Gestiona la información base de tus tiendas y tu equipo.

**Incluye:**
- Datos de cada sucursal (dirección, teléfono, responsable)
- Registro de empleados por sucursal
- Sistema de usuarios con roles y permisos diferenciados

---

## 5. Flujos de Trabajo Principales

### Flujo de una Venta Normal

```
1. Cajero abre su turno de caja
2. Busca el producto por nombre, SKU o código de barras
3. Verifica si el cliente tiene crédito disponible (si aplica)
4. Registra la venta con los productos y método de pago
5. Si el cliente tiene programa de fidelidad → se acumulan puntos automáticamente
6. Al final del día, cajero cierra su turno con el arqueo Z
```

### Flujo de una Compra a Proveedor

```
1. Sistema detecta stock bajo y envía alerta
2. Usuario compara precios entre proveedores en el comparador
3. Agrega productos al carrito de compra
4. Genera la Orden de Compra (exporta a Excel si es necesario)
5. Cuando llega la mercancía → se registra el ingreso de inventario
6. Si fue a crédito → se crea la Cuenta por Pagar automáticamente
```

### Flujo de Cierre de Mes (Comisiones)

```
1. Al último día del mes → se ejecuta el cálculo de comisiones
2. El sistema calcula lo que corresponde a cada vendedor según la regla asignada
3. El gerente revisa y aprueba las comisiones
4. Se registra el pago en el sistema
```

### Flujo de Atención a Cliente Moroso

```
1. Cliente compra a crédito y se atrasa
2. Sistema alerta cuando el crédito está vencido
3. Gerente bloquea el crédito del cliente
4. Cliente registra un pago → sistema desbloquea automáticamente si la deuda es suficiente
```

### Flujo de Garantía Post-Venta

```
1. Cliente regresa con producto defectuoso
2. Se abre un ticket de garantía con descripción del problema
3. Se registra el seguimiento (en revisión, en reparación, resuelto)
4. Se cierra el ticket con la resolución final
```

---

## 6. Roles y Permisos

| Rol | Qué puede hacer |
|---|---|
| **Administrador** | Acceso completo a todo el sistema |
| **Gerente** | Ver reportes, aprobar comisiones y devoluciones, configurar el sistema |
| **Vendedor** | Registrar ventas, consultar precios, crear cotizaciones, atender clientes |
| **Almacenista** | Gestionar inventario, recibir compras, hacer traspasos |
| **Cajero** | Abrir/cerrar turno de caja, registrar ventas, facturar |

Los permisos son granulares: el sistema permite crear roles personalizados con acceso específico por módulo.

---

## 7. Beneficios del Sistema

### Para el Dueño / Director

- Visión en tiempo real de todas las tiendas desde un solo lugar
- Dashboard con los indicadores más importantes del negocio
- Reportes de rentabilidad para tomar decisiones de qué productos impulsar
- Predicciones que evitan tanto quedarse sin mercancía como sobreabastecerse

### Para el Gerente de Tienda

- Control total de inventario sin conteos manuales
- Alertas proactivas antes de que haya un problema
- Seguimiento de metas del equipo de vendedores
- Historial completo de cada cliente (compras, crédito, puntos)

### Para el Vendedor

- Búsqueda rápida de productos con compatibilidad por moto del cliente
- Vista del crédito disponible del cliente antes de vender
- Cotizaciones profesionales en segundos
- Seguimiento de sus comisiones en tiempo real

### Para el Almacenista

- Lista automática de productos por reabastecer
- Registro simple de entradas y salidas
- Traspasos entre tiendas con trazabilidad completa
- Alertas de caducidad para evitar pérdidas

---

## 8. Preguntas Frecuentes

**¿Qué pasa si se cae el internet en una tienda?**  
El sistema tiene modo offline. Las ventas se guardan localmente y se sincronizan automáticamente cuando vuelve la conexión.

**¿Puedo configurar diferentes precios para diferentes tipos de cliente?**  
Sí. El módulo de Precios Especiales permite asignar precios distintos por tipo de cliente.

**¿Se pueden manejar productos con fecha de caducidad?**  
Sí. Cada producto puede tener fecha de caducidad. El sistema alerta con anticipación configurable.

**¿Cómo sé si un cliente puede comprar a crédito?**  
Al registrar la venta, el sistema valida automáticamente el límite disponible. Si el cliente está bloqueado o excedió el límite, se muestra una alerta.

**¿Las comisiones se calculan solas?**  
Sí. Al fin de cada mes se ejecuta el cálculo y el gerente solo tiene que revisar y aprobar.

**¿Puedo ver el rendimiento de mis vendedores?**  
Sí. El módulo de Analítica de Rendimiento Personal muestra comparativas, rankings y tendencias por empleado.

**¿Qué pasa si un producto lo venden en varias tiendas?**  
Cada tienda (sucursal) tiene su propio stock. Los traspasos entre tiendas están completamente registrados.

**¿Puedo saber qué productos compran juntos mis clientes?**  
Sí. El análisis de Market Basket identifica automáticamente qué productos siempre se compran en la misma transacción, útil para hacer ofertas y bundles.

**¿El sistema emite facturas fiscales?**  
Sí. El módulo de Facturación CFDI permite timbrar facturas electrónicas directamente desde el sistema (requiere configuración con un PAC).

**¿Cómo recibo alertas?**  
Por notificación dentro del sistema, por correo electrónico (Gmail), o por mensaje de Telegram. Se configura según las preferencias de cada usuario y sucursal.

---

*© 2026 NexooHub Development Team*
