package com.nexoohub.almacen.ventas.scheduled;

import com.nexoohub.almacen.ventas.dto.ReservaResponseDTO;
import com.nexoohub.almacen.ventas.entity.Reserva;
import com.nexoohub.almacen.ventas.service.ReservaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Tareas programadas para el Sistema de Reservas.
 * 
 * <p>Incluye jobs automáticos para:</p>
 * <ul>
 *   <li>Procesar reservas vencidas diariamente</li>
 *   <li>Enviar alertas de reservas próximas a vencer</li>
 *   <li>Generar métricas y reportes periódicos</li>
 * </ul>
 * 
 * <p><strong>Requisito:</strong> Habilitar @EnableScheduling en la clase principal de Spring Boot:</p>
 * <pre>{@code
 * @SpringBootApplication
 * @EnableScheduling
 * public class AlmacenApplication {
 *     // ...
 * }
 * }</pre>
 * 
 * @author NexooHub Development Team
 * @since 1.1.0
 */
@Component
public class ReservaScheduledTasks {

    private static final Logger log = LoggerFactory.getLogger(ReservaScheduledTasks.class);

    private final ReservaService reservaService;

    public ReservaScheduledTasks(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    /**
     * Procesa automáticamente las reservas vencidas.
     * 
     * <p><strong>Horario:</strong> Todos los días a las 2:00 AM</p>
     * <p><strong>Acción:</strong> Cambia el estado de reservas PENDIENTES/NOTIFICADAS 
     * con fecha_vencimiento vencida a estado VENCIDA</p>
     * 
     * <p>Este job es crítico para mantener la integridad del sistema y liberar 
     * reservas que no fueron completadas a tiempo.</p>
     */
    @Scheduled(cron = "0 0 2 * * *") // A las 02:00 AM todos los días
    public void procesarReservasVencidas() {
        log.info("⏰ Iniciando procesamiento automático de reservas vencidas");
        
        try {
            int procesadas = reservaService.procesarReservasVencidas();
            
            if (procesadas > 0) {
                log.warn("⚠️ Se procesaron {} reservas vencidas", procesadas);
            } else {
                log.info("✅ No hay reservas vencidas para procesar");
            }
            
        } catch (Exception e) {
            log.error("❌ Error al procesar reservas vencidas: {}", e.getMessage(), e);
        }
    }

    /**
     * Envía alertas de reservas próximas a vencer.
     * 
     * <p><strong>Horario:</strong> Cada 6 horas (00:00, 06:00, 12:00, 18:00)</p>
     * <p><strong>Acción:</strong> Busca reservas que vencen en las próximas 24 horas 
     * y genera alertas en el log (puede integrarse con email/SMS)</p>
     * 
     * <p>Este job permite tomar acción preventiva antes de que las reservas venzan.</p>
     */
    @Scheduled(cron = "0 0 */6 * * *") // Cada 6 horas
    public void alertarReservasProximasAVencer() {
        log.info("🔔 Verificando reservas próximas a vencer");
        
        try {
            List<ReservaResponseDTO> proximasVencer = reservaService.listarReservasProximasAVencer();
            
            if (!proximasVencer.isEmpty()) {
                log.warn("⏳ Se encontraron {} reservas próximas a vencer en las próximas 48 horas", 
                         proximasVencer.size());
                
                proximasVencer.forEach(reserva -> {
                    log.warn("  📌 Reserva ID: {} | Cliente: {} | Producto: {} | Vence: {}", 
                             reserva.getId(),
                             reserva.getClienteNombre(),
                             reserva.getProductoNombre(),
                             reserva.getFechaVencimiento());
                    
                    // TODO: Integración futura con servicio de notificaciones
                    // notificacionService.enviarAlertaProximaVencer(reserva);
                });
            } else {
                log.info("✅ No hay reservas próximas a vencer en las próximas 48 horas");
            }
            
        } catch (Exception e) {
            log.error("❌ Error al verificar reservas próximas a vencer: {}", e.getMessage(), e);
        }
    }

    /**
     * Genera métricas y estadísticas del sistema de reservas.
     * 
     * <p><strong>Horario:</strong> Todos los días a las 23:00</p>
     * <p><strong>Acción:</strong> Genera métricas de rendimiento y KPIs del sistema</p>
     * 
     * <p>Útil para monitoreo, dashboards y análisis de negocio.</p>
     */
    @Scheduled(cron = "0 0 23 * * *") // A las 11:00 PM todos los días
    public void generarMetricas() {
        log.info("📊 Generando métricas diarias del sistema de reservas");
        
        try {
            // Obtener métricas por estado
            long pendientes = reservaService.listarReservasPorEstado("PENDIENTE", 
                                                                      PageRequest.of(0, 1))
                                           .getTotalElements();
            
            long notificadas = reservaService.listarReservasPorEstado("NOTIFICADA", 
                                                                       PageRequest.of(0, 1))
                                            .getTotalElements();
            
            long completadas = reservaService.listarReservasPorEstado("COMPLETADA", 
                                                                       PageRequest.of(0, 1))
                                             .getTotalElements();
            
            long vencidas = reservaService.listarReservasPorEstado("VENCIDA", 
                                                                    PageRequest.of(0, 1))
                                          .getTotalElements();
            
            long canceladas = reservaService.listarReservasPorEstado("CANCELADA", 
                                                                      PageRequest.of(0, 1))
                                            .getTotalElements();
            
            long totalActivas = pendientes + notificadas;
            long total = totalActivas + completadas + vencidas + canceladas;
            
            // Calcular tasa de conversión (completadas / total)
            double tasaConversion = total > 0 ? (completadas * 100.0 / total) : 0.0;
            
            // Calcular tasa de vencimiento (vencidas / total)
            double tasaVencimiento = total > 0 ? (vencidas * 100.0 / total) : 0.0;
            
            log.info("📈 Métricas del Sistema de Reservas:");
            log.info("   ├─ Total de reservas históricas: {}", total);
            log.info("   ├─ Reservas activas: {} (PENDIENTE: {}, NOTIFICADA: {})", 
                     totalActivas, pendientes, notificadas);
            log.info("   ├─ Completadas: {} (Tasa de conversión: {:.2f}%)", 
                     completadas, tasaConversion);
            log.info("   ├─ Vencidas: {} (Tasa de vencimiento: {:.2f}%)", 
                     vencidas, tasaVencimiento);
            log.info("   └─ Canceladas: {}", canceladas);
            
            // TODO: Integración futura con sistema de métricas/dashboard
            // metricsService.registrarMetrica("reservas.activas", totalActivas);
            // metricsService.registrarMetrica("reservas.tasa_conversion", tasaConversion);
            
        } catch (Exception e) {
            log.error("❌ Error al generar métricas: {}", e.getMessage(), e);
        }
    }

    /**
     * Limpieza de datos antiguos (housekeeping).
     * 
     * <p><strong>Horario:</strong> Primer día del mes a las 3:00 AM</p>
     * <p><strong>Acción:</strong> Opcionalmente, archiva o limpia reservas muy antiguas</p>
     * 
     * <p><em>Nota:</em> Por defecto está deshabilitado. Habilitar según políticas de retención.</p>
     */
    // @Scheduled(cron = "0 0 3 1 * *") // Primer día del mes a las 03:00 AM
    public void limpiezaDatosAntiguos() {
        log.info("🧹 Iniciando limpieza de datos antiguos");
        
        // TODO: Implementar políticas de retención según requisitos de negocio
        // Por ejemplo: archivar reservas COMPLETADAS/VENCIDAS de hace más de 2 años
        
        log.info("✅ Limpieza completada (no implementada por defecto)");
    }
}
