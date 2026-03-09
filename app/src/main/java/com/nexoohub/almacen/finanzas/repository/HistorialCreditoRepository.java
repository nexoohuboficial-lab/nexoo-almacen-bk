package com.nexoohub.almacen.finanzas.repository;

import com.nexoohub.almacen.finanzas.entity.HistorialCredito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository para gestión del historial de movimientos de crédito.
 * 
 * @author NexooHub Development Team
 * @since 1.2.0
 */
@Repository
public interface HistorialCreditoRepository extends JpaRepository<HistorialCredito, Integer> {

    /**
     * Lista el historial completo de un cliente, ordenado por fecha descendente.
     * 
     * @param clienteId ID del cliente
     * @param pageable Paginación
     * @return Página de movimientos
     */
    @Query("SELECT hc FROM HistorialCredito hc WHERE hc.cliente.id = :clienteId ORDER BY hc.fechaMovimiento DESC")
    Page<HistorialCredito> findByClienteId(@Param("clienteId") Integer clienteId, Pageable pageable);

    /**
     * Lista movimientos de un cliente por tipo.
     * 
     * @param clienteId ID del cliente
     * @param tipoMovimiento Tipo de movimiento (CARGO, ABONO, AJUSTE, etc.)
     * @param pageable Paginación
     * @return Página de movimientos
     */
    @Query("SELECT hc FROM HistorialCredito hc " +
           "WHERE hc.cliente.id = :clienteId " +
           "AND hc.tipoMovimiento = :tipoMovimiento " +
           "ORDER BY hc.fechaMovimiento DESC")
    Page<HistorialCredito> findByClienteIdAndTipoMovimiento(
            @Param("clienteId") Integer clienteId,
            @Param("tipoMovimiento") String tipoMovimiento,
            Pageable pageable);

    /**
     * Lista cargos de un cliente (ventas a crédito).
     * 
     * @param clienteId ID del cliente
     * @param pageable Paginación
     * @return Página de cargos
     */
    default Page<HistorialCredito> findCargosByClienteId(Integer clienteId, Pageable pageable) {
        return findByClienteIdAndTipoMovimiento(clienteId, "CARGO", pageable);
    }

    /**
     * Lista abonos de un cliente (pagos).
     * 
     * @param clienteId ID del cliente
     * @param pageable Paginación
     * @return Página de abonos
     */
    default Page<HistorialCredito> findAbonosByClienteId(Integer clienteId, Pageable pageable) {
        return findByClienteIdAndTipoMovimiento(clienteId, "ABONO", pageable);
    }

    /**
     * Lista movimientos en un rango de fechas.
     * 
     * @param clienteId ID del cliente
     * @param fechaInicio Fecha inicial
     * @param fechaFin Fecha final
     * @return Lista de movimientos
     */
    @Query("SELECT hc FROM HistorialCredito hc " +
           "WHERE hc.cliente.id = :clienteId " +
           "AND hc.fechaMovimiento BETWEEN :fechaInicio AND :fechaFin " +
           "ORDER BY hc.fechaMovimiento DESC")
    List<HistorialCredito> findByClienteIdAndFechasBetween(
            @Param("clienteId") Integer clienteId,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin);

    /**
     * Busca movimiento asociado a una venta específica.
     * 
     * @param ventaId ID de la venta
     * @return Movimiento asociado a la venta
     */
    @Query("SELECT hc FROM HistorialCredito hc WHERE hc.venta.id = :ventaId")
    List<HistorialCredito> findByVentaId(@Param("ventaId") Integer ventaId);

    /**
     * Calcula el total de cargos de un cliente.
     * 
     * @param clienteId ID del cliente
     * @return Suma de todos los cargos
     */
    @Query("SELECT COALESCE(SUM(hc.monto), 0) FROM HistorialCredito hc " +
           "WHERE hc.cliente.id = :clienteId AND hc.tipoMovimiento = 'CARGO'")
    BigDecimal sumCargosByClienteId(@Param("clienteId") Integer clienteId);

    /**
     * Calcula el total de abonos de un cliente.
     * 
     * @param clienteId ID del cliente
     * @return Suma de todos los abonos
     */
    @Query("SELECT COALESCE(SUM(hc.monto), 0) FROM HistorialCredito hc " +
           "WHERE hc.cliente.id = :clienteId AND hc.tipoMovimiento = 'ABONO'")
    BigDecimal sumAbonosByClienteId(@Param("clienteId") Integer clienteId);

    /**
     * Calcula el saldo del cliente basado en su historial.
     * Saldo = Total Cargos - Total Abonos
     * 
     * @param clienteId ID del cliente
     * @return Saldo calculado
     */
    @Query("SELECT COALESCE(SUM(CASE " +
           "WHEN hc.tipoMovimiento = 'CARGO' THEN hc.monto " +
           "WHEN hc.tipoMovimiento = 'ABONO' THEN -hc.monto " +
           "ELSE 0 END), 0) " +
           "FROM HistorialCredito hc WHERE hc.cliente.id = :clienteId")
    BigDecimal calcularSaldoByClienteId(@Param("clienteId") Integer clienteId);

    /**
     * Cuenta movimientos de un cliente por tipo.
     * 
     * @param clienteId ID del cliente
     * @param tipoMovimiento Tipo de movimiento
     * @return Cantidad de movimientos
     */
    @Query("SELECT COUNT(hc) FROM HistorialCredito hc " +
           "WHERE hc.cliente.id = :clienteId AND hc.tipoMovimiento = :tipoMovimiento")
    Long countByClienteIdAndTipoMovimiento(
            @Param("clienteId") Integer clienteId,
            @Param("tipoMovimiento") String tipoMovimiento);

    /**
     * Obtiene el último movimiento de un cliente.
     * 
     * @param clienteId ID del cliente
     * @return Último movimiento o null
     */
    @Query("SELECT hc FROM HistorialCredito hc " +
           "WHERE hc.cliente.id = :clienteId " +
           "ORDER BY hc.fechaMovimiento DESC LIMIT 1")
    HistorialCredito findUltimoMovimientoByClienteId(@Param("clienteId") Integer clienteId);

    /**
     * Lista los últimos N movimientos de todos los clientes (para auditoría).
     * 
     * @param pageable Paginación
     * @return Página de movimientos recientes
     */
    @Query("SELECT hc FROM HistorialCredito hc ORDER BY hc.fechaMovimiento DESC")
    Page<HistorialCredito> findMovimientosRecientes(Pageable pageable);

    /**
     * Busca movimientos por usuario que los registró.
     * Útil para auditoría.
     * 
     * @param usuarioRegistro Usuario que registró
     * @param pageable Paginación
     * @return Página de movimientos
     */
    @Query("SELECT hc FROM HistorialCredito hc " +
           "WHERE hc.usuarioRegistro = :usuario " +
           "ORDER BY hc.fechaMovimiento DESC")
    Page<HistorialCredito> findByUsuarioRegistro(
            @Param("usuario") String usuarioRegistro,
            Pageable pageable);
}
