package com.nexoohub.almacen.alertas.service;

import com.nexoohub.almacen.alertas.entity.ConfiguracionAlerta;
import com.nexoohub.almacen.alertas.entity.TipoAlerta;
import com.nexoohub.almacen.alertas.repository.ConfiguracionAlertaRepository;
import com.nexoohub.almacen.inventario.dto.ProductoStockBajoDTO;
import com.nexoohub.almacen.inventario.repository.InventarioSucursalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Scheduler del Motor de Alertas y Notificaciones (PRO-01).
 *
 * <p>Ejecuta automáticamente 4 revisiones periódicas:
 * <ol>
 *   <li>Stock bajo por sucursal — cada hora.</li>
 *   <li>Productos próximos a caducar — diariamente a las 07:00.</li>
 *   <li>Cuentas por cobrar vencidas — diariamente a las 08:00.</li>
 *   <li>Metas de ventas por debajo del umbral — primer día del mes a las 09:00.</li>
 * </ol>
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AlertaScheduler {

    private final AlertaService alertaService;
    private final ConfiguracionAlertaRepository configAlertaRepo;
    private final InventarioSucursalRepository inventarioRepo;

    // ─────────────────────────────── JOB 1 ───────────────────────────────────
    /**
     * Revisa el stock de cada sucursal y genera alertas cuando algún producto
     * está por debajo del mínimo configurado.
     * Se ejecuta cada hora en punto.
     */
    @Scheduled(cron = "0 0 * * * *")
    public void verificarStockBajo() {
        log.info("SCHEDULER: Iniciando verificación de stock bajo...");

        List<ProductoStockBajoDTO> productos = inventarioRepo.obtenerTodosProductosStockBajo();

        for (ProductoStockBajoDTO p : productos) {
            // Comprobar si se supera el mínimo configurado por sucursal
            configAlertaRepo.findBySucursalIdAndActivoTrue(p.getSucursalId())
                    .ifPresentOrElse(config -> {
                        if (p.getStockActual() < config.getStockMinimo()) {
                            String mensaje = String.format(
                                    "⚠️ Stock bajo: '%s' (SKU: %s) en sucursal '%s' — stock actual: %d, mínimo: %d.",
                                    p.getNombreComercial(), p.getSkuInterno(),
                                    p.getNombreSucursal(), p.getStockActual(), config.getStockMinimo());
                            alertaService.crearAlerta(
                                    TipoAlerta.STOCK_BAJO, mensaje, p.getSucursalId(), null);
                        }
                    }, () -> log.debug("Sin configuración de alertas para sucursal {}", p.getSucursalId()));
        }

        log.info("SCHEDULER: Verificación de stock bajo finalizada. {} producto(s) con stock bajo detectados.",
                productos.size());
    }

    // ─────────────────────────────── JOB 2 ───────────────────────────────────
    /**
     * Verifica los productos próximos a caducar en las próximas 30 días.
     * Se ejecuta todos los días a las 07:00 AM.
     */
    @Scheduled(cron = "0 0 7 * * *")
    public void verificarProximosCaducar() {
        log.info("SCHEDULER: Iniciando verificación de productos próximos a caducar...");

        java.time.LocalDate fechaLimite = java.time.LocalDate.now().plusDays(30);
        var productos = inventarioRepo.obtenerProductosProximosCaducar(fechaLimite);

        for (var p : productos) {
            String mensaje = String.format(
                    "⏳ Producto próximo a caducar: '%s' (SKU: %s) en sucursal '%s' — caduca el %s, stock: %d unidades.",
                    p.getNombreComercial(), p.getSkuInterno(),
                    p.getNombreSucursal(), p.getFechaCaducidad(), p.getStockActual());
            alertaService.crearAlerta(
                    TipoAlerta.PRODUCTO_POR_CADUCAR, mensaje, p.getSucursalId(), null);
        }

        log.info("SCHEDULER: {} producto(s) próximos a caducar detectados.", productos.size());
    }

    // ─────────────────────────────── JOB 3 ───────────────────────────────────
    /**
     * Verifica las cuentas por cobrar vencidas.
     * Se ejecuta todos los días a las 08:00 AM.
     *
     * <p>Requiere el módulo de finanzas/CxC. Si no está disponible se omite silenciosamente.
     */
    @Scheduled(cron = "0 0 8 * * *")
    public void verificarCxCVencidas() {
        log.info("SCHEDULER: Iniciando verificación de CxC vencidas...");

        List<ConfiguracionAlerta> configs = configAlertaRepo.findAll();

        for (ConfiguracionAlerta config : configs) {
            if (!config.isActivo()) continue;

            // Construimos un mensaje informativo por sucursal
            String mensaje = String.format(
                    "💸 Revisión de CxC: la sucursal %d tiene configurados %d días como umbral de vencimiento.",
                    config.getSucursalId(), config.getDiasVencimientoCxC());

            log.debug(mensaje);
            /* Integración real con CxC:
             * Se inyectaría CuentaPorCobrarRepository cuando ese módulo exista,
             * y se filtraría: fechaVencimiento <= LocalDate.now().minusDays(diasVencimientoCxC).
             * alertaService.crearAlerta(TipoAlerta.CXC_VENCIDA, mensaje, config.getSucursalId(), null);
             */
        }

        log.info("SCHEDULER: Verificación de CxC vencidas finalizada.");
    }

    // ─────────────────────────────── JOB 4 ───────────────────────────────────
    /**
     * Verifica el avance de metas de ventas mensual.
     * Se ejecuta el primer día de cada mes a las 09:00 AM.
     *
     * <p>Usa el umbral {@code porcentajeMetaAlerta} de cada sucursal.
     */
    @Scheduled(cron = "0 0 9 1 * *")
    public void verificarMetasVentas() {
        log.info("SCHEDULER: Iniciando verificación de metas de ventas mensual...");

        List<ConfiguracionAlerta> configs = configAlertaRepo.findAll();

        for (ConfiguracionAlerta config : configs) {
            if (!config.isActivo()) continue;

            /* Integración real con módulo de ventas/metas:
             * Al tener el módulo de metas, se consultaría el porcentaje de avance real
             * y se compararía con config.getPorcentajeMetaAlerta().
             *
             * if (porcentajeReal < config.getPorcentajeMetaAlerta()) {
             *     alertaService.crearAlerta(TipoAlerta.META_VENTAS_BAJA, mensaje, config.getSucursalId(), null);
             * }
             */
            log.debug("Verificando meta de ventas sucursal {} — umbral: {}%",
                    config.getSucursalId(), config.getPorcentajeMetaAlerta());
        }

        log.info("SCHEDULER: Verificación de metas de ventas finalizada.");
    }
}
