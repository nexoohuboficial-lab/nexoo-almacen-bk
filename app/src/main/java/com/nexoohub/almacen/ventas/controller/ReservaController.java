package com.nexoohub.almacen.ventas.controller;

import com.nexoohub.almacen.ventas.dto.ReservaRequestDTO;
import com.nexoohub.almacen.ventas.dto.ReservaResponseDTO;
import com.nexoohub.almacen.ventas.service.ReservaService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para gestión de reservas/apartados de productos.
 * 
 * <p>Endpoints disponibles:</p>
 * <ul>
 *   <li>POST /api/v1/reservas - Crear nueva reserva</li>
 *   <li>GET /api/v1/reservas - Listar todas las reservas (paginado)</li>
 *   <li>GET /api/v1/reservas/{id} - Obtener reserva por ID</li>
 *   <li>GET /api/v1/reservas/cliente/{clienteId} - Listar reservas de un cliente</li>
 *   <li>GET /api/v1/reservas/estado/{estado} - Listar por estado</li>
 *   <li>GET /api/v1/reservas/proximas-vencer - Alertas de vencimiento</li>
 *   <li>PUT /api/v1/reservas/{id}/cancelar - Cancelar reserva</li>
 *   <li>PUT /api/v1/reservas/{id}/completar - Completar reserva (asociar venta)</li>
 *   <li>POST /api/v1/reservas/procesar-vencidas - Procesar reservas vencidas</li>
 * </ul>
 * 
 * @author NexooHub Development Team
 * @since 1.1.0
 */
@RestController
@RequestMapping("/api/v1/reservas")
@Slf4j
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    /**
     * Crea una nueva reserva de producto.
     * 
     * <p><b>Regla de negocio:</b> Solo permite reservar si NO hay stock disponible.</p>
     * 
     * @param request Datos de la reserva
     * @return Reserva creada con ID y fecha de vencimiento
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> crearReserva(@Valid @RequestBody ReservaRequestDTO request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("Usuario {} creando reserva para cliente {}", username, request.getClienteId());

        ReservaResponseDTO reserva = reservaService.crearReserva(request, username);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("exitoso", true);
        respuesta.put("mensaje", "Reserva creada exitosamente");
        respuesta.put("reservaId", reserva.getId());
        respuesta.put("fechaVencimiento", reserva.getFechaVencimiento());
        respuesta.put("estado", reserva.getEstado());

        return new ResponseEntity<>(respuesta, HttpStatus.CREATED);
    }

    /**
     * Obtiene una reserva por ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ReservaResponseDTO> obtenerReservaPorId(@PathVariable("id") Integer id) {
        ReservaResponseDTO reserva = reservaService.obtenerReservaPorId(id);
        return ResponseEntity.ok(reserva);
    }

    /**
     * Lista todas las reservas (paginado).
     */
    @GetMapping
    public ResponseEntity<Page<ReservaResponseDTO>> listarReservas(
            @PageableDefault(size = 20, sort = "fechaCreacion") Pageable pageable) {
        Page<ReservaResponseDTO> reservas = reservaService.listarReservas(pageable);
        return ResponseEntity.ok(reservas);
    }

    /**
     * Lista reservas de un cliente específico.
     */
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<Page<ReservaResponseDTO>> listarReservasPorCliente(
            @PathVariable("clienteId") Integer clienteId,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<ReservaResponseDTO> reservas = reservaService.listarReservasPorCliente(clienteId, pageable);
        return ResponseEntity.ok(reservas);
    }

    /**
     * Lista reservas por estado (PENDIENTE, NOTIFICADA, COMPLETADA, VENCIDA, CANCELADA).
     */
    @GetMapping("/estado/{estado}")
    public ResponseEntity<Page<ReservaResponseDTO>> listarReservasPorEstado(
            @PathVariable("estado") String estado,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<ReservaResponseDTO> reservas = reservaService.listarReservasPorEstado(estado, pageable);
        return ResponseEntity.ok(reservas);
    }

    /**
     * Lista reservas próximas a vencer (últimos 2 días).
     * Útil para recordatorios.
     */
    @GetMapping("/proximas-vencer")
    public ResponseEntity<List<ReservaResponseDTO>> listarReservasProximasAVencer() {
        List<ReservaResponseDTO> reservas = reservaService.listarReservasProximasAVencer();
        return ResponseEntity.ok(reservas);
    }

    /**
     * Cancela una reserva.
     * 
     * @param id ID de la reserva
     * @param request Mapa con el motivo de cancelación
     */
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<Map<String, Object>> cancelarReserva(
            @PathVariable("id") Integer id,
            @RequestBody Map<String, String> request) {
        String motivo = request.getOrDefault("motivo", "Sin motivo especificado");
        ReservaResponseDTO reserva = reservaService.cancelarReserva(id, motivo);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("exitoso", true);
        respuesta.put("mensaje", "Reserva cancelada exitosamente");
        respuesta.put("reservaId", reserva.getId());
        respuesta.put("estado", reserva.getEstado());

        return ResponseEntity.ok(respuesta);
    }

    /**
     * Completa una reserva asociándola a una venta.
     * 
     * @param id ID de la reserva
     * @param request Mapa con el ID de la venta
     */
    @PutMapping("/{id}/completar")
    public ResponseEntity<Map<String, Object>> completarReserva(
            @PathVariable("id") Integer id,
            @RequestBody Map<String, Integer> request) {
        Integer ventaId = request.get("ventaId");
        if (ventaId == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("exitoso", false);
            error.put("mensaje", "El campo 'ventaId' es requerido");
            return ResponseEntity.badRequest().body(error);
        }

        ReservaResponseDTO reserva = reservaService.completarReserva(id, ventaId);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("exitoso", true);
        respuesta.put("mensaje", "Reserva completada exitosamente");
        respuesta.put("reservaId", reserva.getId());
        respuesta.put("ventaId", reserva.getVentaId());
        respuesta.put("estado", reserva.getEstado());

        return ResponseEntity.ok(respuesta);
    }

    /**
     * Procesa manualmente todas las reservas vencidas.
     * 
     * <p>En producción, este endpoint debería protegerse o ejecutarse vía tarea programada.</p>
     */
    @PostMapping("/procesar-vencidas")
    public ResponseEntity<Map<String, Object>> procesarReservasVencidas() {
        log.info("Procesando reservas vencidas manualmente");
        int procesadas = reservaService.procesarReservasVencidas();

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("exitoso", true);
        respuesta.put("mensaje", "Reservas vencidas procesadas");
        respuesta.put("totalProcesadas", procesadas);

        return ResponseEntity.ok(respuesta);
    }
}
