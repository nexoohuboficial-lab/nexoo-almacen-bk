package com.nexoohub.almacen.finanzas.service;

import com.nexoohub.almacen.catalogo.repository.ClienteRepository;
import com.nexoohub.almacen.finanzas.dto.DashboardDTO;
import com.nexoohub.almacen.inventario.repository.InventarioSucursalRepository;
import com.nexoohub.almacen.ventas.repository.DevolucionRepository;
import com.nexoohub.almacen.ventas.repository.VentaRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Servicio para generar datos de dashboard ejecutivo.
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
@Service
public class DashboardService {
    
    private final VentaRepository ventaRepository;
    private final DevolucionRepository devolucionRepository;
    private final InventarioSucursalRepository inventarioRepository;
    private final ClienteRepository clienteRepository;
    
    public DashboardService(
            VentaRepository ventaRepository,
            DevolucionRepository devolucionRepository,
            InventarioSucursalRepository inventarioRepository,
            ClienteRepository clienteRepository) {
        this.ventaRepository = ventaRepository;
        this.devolucionRepository = devolucionRepository;
        this.inventarioRepository = inventarioRepository;
        this.clienteRepository = clienteRepository;
    }
    
    /**
     * Genera los datos del dashboard ejecutivo.
     * 
     * @return DTO con métricas del dashboard
     */
    public DashboardDTO generarDashboard() {
        LocalDateTime inicioDia = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime finDia = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        
        LocalDateTime inicioMes = LocalDateTime.of(LocalDate.now().withDayOfMonth(1), LocalTime.MIN);
        
        // Ventas de hoy
        BigDecimal ventasHoy = ventaRepository.findByFechaVentaBetween(inicioDia, finDia)
                .stream()
                .map(v -> v.getTotal())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Ventas del mes
        BigDecimal ventasMes = ventaRepository.findByFechaVentaBetween(inicioMes, finDia)
                .stream()
                .map(v -> v.getTotal())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Productos con stock bajo
        Integer productosStockBajo = inventarioRepository.obtenerTodosProductosStockBajo().size();
        
        // Devoluciones de hoy
        Integer devolucionesHoy = devolucionRepository.findBySucursalAndFecha(
                null, // Todas las sucursales
                inicioDia,
                finDia
        ).size();
        
        // Ticket promedio (ventas del mes / cantidad de ventas)
        Long cantidadVentas = ventaRepository.countByFechaVentaBetween(inicioMes, finDia);
        BigDecimal ticketPromedio = cantidadVentas > 0 
                ? ventasMes.divide(BigDecimal.valueOf(cantidadVentas), 2, java.math.RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        
        // Clientes bloqueados
        Integer clientesBloqueados = clienteRepository.obtenerClientesBloqueados().size();
        
        // Productos próximos a caducar (30 días)
        java.time.LocalDate fechaLimiteCaducidad = java.time.LocalDate.now().plusDays(30);
        Integer productosProximoCaducar = inventarioRepository.obtenerProductosProximosCaducar(fechaLimiteCaducidad).size();
        
        return new DashboardDTO(
                ventasHoy,
                ventasMes,
                productosStockBajo,
                devolucionesHoy,
                ticketPromedio,
                clientesBloqueados,
                productosProximoCaducar
        );
    }
}
