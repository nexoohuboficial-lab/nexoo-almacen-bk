package com.nexoohub.almacen.finanzas.controller;

import com.nexoohub.almacen.finanzas.dto.DashboardDTO;
import com.nexoohub.almacen.finanzas.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador para dashboards y reportes ejecutivos.
 * 
 * <p>Endpoints disponibles:</p>
 * <ul>
 *   <li>GET /api/v1/dashboard - Dashboard ejecutivo con métricas clave</li>
 * </ul>
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {
    
    private final DashboardService dashboardService;
    
    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }
    
    /**
     * Obtiene datos del dashboard ejecutivo.
     * 
     * <p>Métricas incluidas:</p>
     * <ul>
     *   <li>Ventas del día y del mes</li>
     *   <li>Ticket promedio</li>
     *   <li>Productos con stock bajo</li>
     *   <li>Devoluciones del día</li>
     *   <li>Clientes bloqueados</li>
     *   <li>Productos próximos a caducar</li>
     * </ul>
     * 
     * @return DTO con métricas del dashboard
     */
    @GetMapping
    public ResponseEntity<DashboardDTO> obtenerDashboard() {
        DashboardDTO dashboard = dashboardService.generarDashboard();
        return ResponseEntity.ok(dashboard);
    }
}
