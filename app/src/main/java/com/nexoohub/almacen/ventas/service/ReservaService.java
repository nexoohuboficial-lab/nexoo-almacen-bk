package com.nexoohub.almacen.ventas.service;

import com.nexoohub.almacen.catalogo.entity.Cliente;
import com.nexoohub.almacen.catalogo.repository.ClienteRepository;
import com.nexoohub.almacen.common.exception.InvalidOperationException;
import com.nexoohub.almacen.common.exception.ResourceNotFoundException;
import com.nexoohub.almacen.inventario.entity.InventarioSucursal;
import com.nexoohub.almacen.inventario.entity.ProductoMaestro;
import com.nexoohub.almacen.inventario.repository.InventarioSucursalRepository;
import com.nexoohub.almacen.inventario.repository.ProductoMaestroRepository;
import com.nexoohub.almacen.sucursal.entity.Sucursal;
import com.nexoohub.almacen.sucursal.repository.SucursalRepository;
import com.nexoohub.almacen.ventas.dto.ReservaRequestDTO;
import com.nexoohub.almacen.ventas.dto.ReservaResponseDTO;
import com.nexoohub.almacen.ventas.entity.Reserva;
import com.nexoohub.almacen.ventas.repository.ReservaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para gestión de reservas/apartados de productos.
 * 
 * <p>Implementa la lógica de negocio para:</p>
 * <ul>
 *   <li>Crear reservas cuando no hay stock disponible</li>
 *   <li>Notificar clientes cuando llega mercancía reservada</li>
 *   <li>Gestionar vencimiento automático de reservas</li>
 *   <li>Convertir reservas en ventas</li>
 * </ul>
 * 
 * @author NexooHub Development Team
 * @since 1.1.0
 */
@Service
@Transactional
@Slf4j
public class ReservaService {

    private static final int DIAS_VIGENCIA_DEFAULT = 7;
    private static final int MAX_RESERVAS_POR_CLIENTE = 10;

    private final ReservaRepository reservaRepository;
    private final ClienteRepository clienteRepository;
    private final ProductoMaestroRepository productoRepository;
    private final SucursalRepository sucursalRepository;
    private final InventarioSucursalRepository inventarioRepository;

    public ReservaService(
            ReservaRepository reservaRepository,
            ClienteRepository clienteRepository,
            ProductoMaestroRepository productoRepository,
            SucursalRepository sucursalRepository,
            InventarioSucursalRepository inventarioRepository) {
        this.reservaRepository = reservaRepository;
        this.clienteRepository = clienteRepository;
        this.productoRepository = productoRepository;
        this.sucursalRepository = sucursalRepository;
        this.inventarioRepository = inventarioRepository;
    }

    /**
     * Crea una nueva reserva de producto.
     * 
     * <p><b>Reglas de negocio:</b></p>
     * <ul>
     *   <li>Solo se puede reservar si no hay stock suficiente</li>
     *   <li>Cliente no puede tener más de 10 reservas activas</li>
     *   <li>Vigencia por defecto: 7 días desde creación</li>
     * </ul>
     */
    public ReservaResponseDTO crearReserva(ReservaRequestDTO request, String username) {
        log.info("Creando reserva para cliente {} - producto {}", request.getClienteId(), request.getSkuInterno());

        // 1. Validar entidades
        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con ID: " + request.getClienteId()));

        ProductoMaestro producto = productoRepository.findById(request.getSkuInterno())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con SKU: " + request.getSkuInterno()));

        Sucursal sucursal = sucursalRepository.findById(request.getSucursalId())
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada con ID: " + request.getSucursalId()));

        // 2. Verificar límite de reservas por cliente
        Long reservasActivas = reservaRepository.contarReservasActivasPorCliente(cliente.getId());
        if (reservasActivas >= MAX_RESERVAS_POR_CLIENTE) {
            throw new InvalidOperationException(
                    "El cliente ya tiene " + MAX_RESERVAS_POR_CLIENTE + " reservas activas. " +
                    "Debe completar o cancelar alguna antes de crear una nueva."
            );
        }

        // 3. Verificar stock disponible
        InventarioSucursal inventario = inventarioRepository
                .findByIdSkuInternoAndIdSucursalId(request.getSkuInterno(), request.getSucursalId())
                .orElse(null);

        if (inventario != null && inventario.getStockActual() >= request.getCantidad()) {
            throw new InvalidOperationException(
                    "No se puede crear reserva. El producto tiene stock disponible (" +
                    inventario.getStockActual() + " unidades). Proceda con venta directa."
            );
        }

        // 4. Crear la reserva
        Reserva reserva = new Reserva();
        reserva.setCliente(cliente);
        reserva.setProducto(producto);
        reserva.setSucursal(sucursal);
        reserva.setCantidad(request.getCantidad());
        reserva.setEstado(Reserva.EstadoReserva.PENDIENTE);
        reserva.setComentarios(request.getComentarios());
        reserva.setUsuarioRegistro(username);

        // Calcular fecha de vencimiento
        int diasVigencia = request.getDiasVigencia() != null && request.getDiasVigencia() > 0
                ? request.getDiasVigencia()
                : DIAS_VIGENCIA_DEFAULT;
        reserva.setFechaVencimiento(LocalDateTime.now().plusDays(diasVigencia));

        Reserva guardada = reservaRepository.save(reserva);
        log.info("Reserva creada exitosamente con ID: {}", guardada.getId());

        return ReservaResponseDTO.fromEntity(guardada);
    }

    /**
     * Notifica a clientes con reservas pendientes cuando llega mercancía.
     * 
     * <p>Este método debe ser llamado desde CompraService después de registrar un ingreso.</p>
     * 
     * @param sku SKU del producto que acaba de llegar
     * @param sucursalId ID de la sucursal
     * @return Número de reservas notificadas
     */
    public int notificarReservasDisponibles(String sku, Integer sucursalId) {
        log.info("Buscando reservas pendientes para producto {} en sucursal {}", sku, sucursalId);

        List<Reserva> reservasPendientes = reservaRepository
                .findReservasPendientesByProductoYSucursal(sku, sucursalId);

        if (reservasPendientes.isEmpty()) {
            log.info("No hay reservas pendientes para el producto {}", sku);
            return 0;
        }

        // Verificar stock disponible
        InventarioSucursal inventario = inventarioRepository
                .findByIdSkuInternoAndIdSucursalId(sku, sucursalId)
                .orElse(null);

        if (inventario == null || inventario.getStockActual() <= 0) {
            log.warn("No hay stock disponible para notificar reservas");
            return 0;
        }

        int stockDisponible = inventario.getStockActual();
        log.info("Stock disponible: {}. Procesando {} reservas", stockDisponible, reservasPendientes.size());

        int reservasNotificadas = 0;
        for (Reserva reserva : reservasPendientes) {
            if (stockDisponible >= reserva.getCantidad()) {
                reserva.setEstado(Reserva.EstadoReserva.NOTIFICADA);
                reserva.setFechaNotificacion(LocalDateTime.now());
                reservaRepository.save(reserva);

                log.info("Cliente {} notificado para reserva ID: {}", 
                        reserva.getCliente().getNombre(), reserva.getId());

                // TODO: Integrar con sistema de notificaciones (SMS/Email/WhatsApp)
                stockDisponible -= reserva.getCantidad();
                reservasNotificadas++;
            } else {
                log.info("Stock insuficiente para reserva ID: {} (requiere {}, disponible {})",
                        reserva.getId(), reserva.getCantidad(), stockDisponible);
                break;
            }
        }
        
        return reservasNotificadas;
    }

    /**
     * Procesa automáticamente reservas vencidas.
     * 
     * <p>Este método debe ejecutarse periódicamente (ej: tarea programada cada hora).</p>
     */
    public int procesarReservasVencidas() {
        log.info("Procesando reservas vencidas...");

        List<Reserva> vencidas = reservaRepository.findReservasVencidas(LocalDateTime.now());
        
        for (Reserva reserva : vencidas) {
            reserva.setEstado(Reserva.EstadoReserva.VENCIDA);
            reserva.setFechaFinalizacion(LocalDateTime.now());
            reservaRepository.save(reserva);
            log.info("Reserva ID {} marcada como VENCIDA", reserva.getId());
        }

        log.info("Total de reservas vencidas procesadas: {}", vencidas.size());
        return vencidas.size();
    }

    /**
     * Cancela una reserva manualmente.
     */
    public ReservaResponseDTO cancelarReserva(Integer reservaId, String motivo) {
        log.info("Cancelando reserva ID: {}", reservaId);

        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada con ID: " + reservaId));

        if (reserva.getEstado() == Reserva.EstadoReserva.COMPLETADA) {
            throw new InvalidOperationException("No se puede cancelar una reserva ya completada");
        }

        if (reserva.getEstado() == Reserva.EstadoReserva.CANCELADA) {
            throw new InvalidOperationException("La reserva ya está cancelada");
        }

        reserva.setEstado(Reserva.EstadoReserva.CANCELADA);
        reserva.setFechaFinalizacion(LocalDateTime.now());
        reserva.setComentarios(reserva.getComentarios() + " | CANCELACIÓN: " + motivo);

        Reserva actualizada = reservaRepository.save(reserva);
        log.info("Reserva ID {} cancelada exitosamente", reservaId);

        return ReservaResponseDTO.fromEntity(actualizada);
    }

    /**
     * Marca una reserva como completada (asociada a una venta).
     */
    public ReservaResponseDTO completarReserva(Integer reservaId, Integer ventaId) {
        log.info("Completando reserva ID: {} con venta ID: {}", reservaId, ventaId);

        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada con ID: " + reservaId));

        if (reserva.getEstado() != Reserva.EstadoReserva.NOTIFICADA) {
            throw new InvalidOperationException(
                    "Solo se pueden completar reservas en estado NOTIFICADA. Estado actual: " + reserva.getEstado()
            );
        }

        reserva.setEstado(Reserva.EstadoReserva.COMPLETADA);
        reserva.setFechaFinalizacion(LocalDateTime.now());
        reserva.setVentaId(ventaId);

        Reserva actualizada = reservaRepository.save(reserva);
        log.info("Reserva completada exitosamente. Venta ID: {}", ventaId);

        return ReservaResponseDTO.fromEntity(actualizada);
    }

    /**
     * Obtiene una reserva por ID.
     */
    @Transactional(readOnly = true)
    public ReservaResponseDTO obtenerReservaPorId(Integer id) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada con ID: " + id));
        return ReservaResponseDTO.fromEntity(reserva);
    }

    /**
     * Lista todas las reservas con paginación.
     */
    @Transactional(readOnly = true)
    public Page<ReservaResponseDTO> listarReservas(Pageable pageable) {
        return reservaRepository.findAll(pageable)
                .map(ReservaResponseDTO::fromEntity);
    }

    /**
     * Lista reservas de un cliente específico.
     */
    @Transactional(readOnly = true)
    public Page<ReservaResponseDTO> listarReservasPorCliente(Integer clienteId, Pageable pageable) {
        return reservaRepository.findByClienteIdOrderByFechaCreacionDesc(clienteId, pageable)
                .map(ReservaResponseDTO::fromEntity);
    }

    /**
     * Lista reservas por estado.
     */
    @Transactional(readOnly = true)
    public Page<ReservaResponseDTO> listarReservasPorEstado(String estado, Pageable pageable) {
        Reserva.EstadoReserva estadoEnum;
        try {
            estadoEnum = Reserva.EstadoReserva.valueOf(estado.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidOperationException("Estado inválido: " + estado);
        }

        return reservaRepository.findByEstadoOrderByFechaCreacionDesc(estadoEnum, pageable)
                .map(ReservaResponseDTO::fromEntity);
    }

    /**
     * Lista reservas próximas a vencer (últimos 2 días).
     */
    @Transactional(readOnly = true)
    public List<ReservaResponseDTO> listarReservasProximasAVencer() {
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime dentroDosDias = ahora.plusDays(2);

        return reservaRepository.findReservasNotificadasProximasAVencer(ahora, dentroDosDias)
                .stream()
                .map(ReservaResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
}
