package com.nexoohub.almacen.ventas.repository;

import com.nexoohub.almacen.ventas.entity.Reserva;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para gestión de reservas de productos.
 * 
 * @author NexooHub Development Team
 * @since 1.1.0
 */
@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Integer> {

    /**
     * Busca reservas por cliente.
     */
    Page<Reserva> findByClienteIdOrderByFechaCreacionDesc(Integer clienteId, Pageable pageable);

    /**
     * Busca reservas por estado.
     */
    Page<Reserva> findByEstadoOrderByFechaCreacionDesc(Reserva.EstadoReserva estado, Pageable pageable);

    /**
     * Busca reservas pendientes de un producto en una sucursal específica.
     */
    @Query("SELECT r FROM Reserva r WHERE r.producto.skuInterno = :sku " +
           "AND r.sucursal.id = :sucursalId " +
           "AND r.estado IN ('PENDIENTE', 'NOTIFICADA') " +
           "ORDER BY r.fechaCreacion ASC")
    List<Reserva> findReservasPendientesByProductoYSucursal(
        @Param("sku") String sku,
        @Param("sucursalId") Integer sucursalId
    );

    /**
     * Busca reservas vencidas que no han sido procesadas.
     */
    @Query("SELECT r FROM Reserva r WHERE r.estado = 'PENDIENTE' " +
           "AND r.fechaVencimiento < :fechaActual")
    List<Reserva> findReservasVencidas(@Param("fechaActual") LocalDateTime fechaActual);

    /**
     * Busca reservas notificadas próximas a vencer.
     */
    @Query("SELECT r FROM Reserva r WHERE r.estado = 'NOTIFICADA' " +
           "AND r.fechaVencimiento BETWEEN :inicio AND :fin")
    List<Reserva> findReservasNotificadasProximasAVencer(
        @Param("inicio") LocalDateTime inicio,
        @Param("fin") LocalDateTime fin
    );

    /**
     * Cuenta reservas activas de un cliente.
     */
    @Query("SELECT COUNT(r) FROM Reserva r WHERE r.cliente.id = :clienteId " +
           "AND r.estado IN ('PENDIENTE', 'NOTIFICADA')")
    Long contarReservasActivasPorCliente(@Param("clienteId") Integer clienteId);

    /**
     * Busca todas las reservas pendientes de un producto (todas las sucursales).
     */
    @Query("SELECT r FROM Reserva r WHERE r.producto.skuInterno = :sku " +
           "AND r.estado = 'PENDIENTE' " +
           "ORDER BY r.fechaCreacion ASC")
    List<Reserva> findReservasPendientesByProducto(@Param("sku") String sku);
}
