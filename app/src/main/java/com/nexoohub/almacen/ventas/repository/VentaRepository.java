package com.nexoohub.almacen.ventas.repository;

import com.nexoohub.almacen.ventas.entity.Venta;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Integer> {
    
    /**
     * Carga una venta con todas sus relaciones en 1 sola query
     * Evita N+1: Sin esto se harían 1 query para venta + N para detalles + N para productos + 1 para cliente + 1 para sucursal
     * Con esto: 1 sola query con LEFT JOINs
     */
    @EntityGraph(attributePaths = {"detalles", "detalles.producto", "cliente", "sucursal", "vendedor"})
    Optional<Venta> findWithDetallesById(Integer id);
    
    /**
     * Buscar ventas por sucursal (sin cargar relaciones automáticamente para evitar ambigüedad)
     * Usa el campo primitivo sucursalId para filtrado eficiente
     */
    List<Venta> findBySucursalId(Integer sucursalId);
    
    /**
     * Buscar ventas por rango de fechas con JOIN FETCH explícito
     * Útil para reportes donde se necesitan los detalles
     */
    @Query("SELECT DISTINCT v FROM Venta v " +
           "LEFT JOIN FETCH v.detalles d " +
           "LEFT JOIN FETCH d.producto " +
           "LEFT JOIN FETCH v.cliente " +
           "LEFT JOIN FETCH v.sucursal " +
           "WHERE v.fechaVenta BETWEEN :inicio AND :fin " +
           "ORDER BY v.fechaVenta DESC")
    List<Venta> findByFechaRangoConDetalles(
        @Param("inicio") LocalDateTime inicio,
        @Param("fin") LocalDateTime fin
    );
    
    /**
     * Buscar ventas por cliente (sin cargar relaciones automáticamente para evitar ambigüedad)
     * Usa el campo primitivo clienteId para filtrado eficiente
     */
    List<Venta> findByClienteId(Integer clienteId);
    
    /**
     * Buscar ventas en un periodo (para cálculos de dashboard)
     */
    List<Venta> findByFechaVentaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    
    /**
     * Cuenta ventas en un periodo (para dashboard)
     */
    Long countByFechaVentaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    
    /**
     * Buscar ventas de un vendedor en un periodo (para cálculo de comisiones)
     * Carga detalles para calcular comisiones por producto
     */
    @Query("SELECT DISTINCT v FROM Venta v " +
           "LEFT JOIN FETCH v.detalles d " +
           "LEFT JOIN FETCH d.producto " +
           "WHERE v.vendedorId = :vendedorId " +
           "AND v.fechaVenta BETWEEN :inicio AND :fin " +
           "ORDER BY v.fechaVenta DESC")
    List<Venta> findByVendedorIdAndFechaVentaBetween(
        @Param("vendedorId") Integer vendedorId,
        @Param("inicio") LocalDateTime inicio,
        @Param("fin") LocalDateTime fin
    );

    // ==================== MÉTODOS PARA MÉTRICAS FINANCIERAS ====================

    /**
     * Obtiene ventas del período (para métricas financieras)
     * Retorna Object[] con: [ventaId, total, fechaVenta]
     */
    @Query("SELECT v.id, v.total, v.fechaVenta FROM Venta v " +
           "WHERE v.fechaVenta BETWEEN :inicio AND :fin")
    List<Object[]> findVentasEnPeriodo(
        @Param("inicio") LocalDateTime inicio,
        @Param("fin") LocalDateTime fin
    );

    /**
     * Obtiene ventas del período por sucursal
     */
    @Query("SELECT v.id, v.total, v.fechaVenta FROM Venta v " +
           "WHERE v.sucursalId = :sucursalId " +
           "AND v.fechaVenta BETWEEN :inicio AND :fin")
    List<Object[]> findVentasEnPeriodoPorSucursal(
        @Param("inicio") LocalDateTime inicio,
        @Param("fin") LocalDateTime fin,
        @Param("sucursalId") Integer sucursalId
    );

    /**
     * Calcula el total de ventas en un período
     */
    @Query("SELECT COALESCE(SUM(v.total), 0) FROM Venta v " +
           "WHERE v.fechaVenta BETWEEN :inicio AND :fin")
    BigDecimal calcularVentasTotalesPeriodo(
        @Param("inicio") LocalDateTime inicio,
        @Param("fin") LocalDateTime fin
    );

    /**
     * Cuenta ventas en un período
     */
    @Query("SELECT COUNT(v) FROM Venta v " +
           "WHERE v.fechaVenta BETWEEN :inicio AND :fin")
    Long contarVentasPeriodo(
        @Param("inicio") LocalDateTime inicio,
        @Param("fin") LocalDateTime fin
    );

    /**
     * Cuenta clientes únicos en un período
     */
    @Query("SELECT COUNT(DISTINCT v.clienteId) FROM Venta v " +
           "WHERE v.fechaVenta BETWEEN :inicio AND :fin")
    Long contarClientesUnicosPeriodo(
        @Param("inicio") LocalDateTime inicio,
        @Param("fin") LocalDateTime fin
    );

    /**
     * Calcula el total de ventas en un período por sucursal
     */
    @Query("SELECT COALESCE(SUM(v.total), 0) FROM Venta v " +
           "WHERE v.sucursalId = :sucursalId " +
           "AND v.fechaVenta BETWEEN :inicio AND :fin")
    BigDecimal calcularVentasTotalesPeriodoPorSucursal(
        @Param("inicio") LocalDateTime inicio,
        @Param("fin") LocalDateTime fin,
        @Param("sucursalId") Integer sucursalId
    );

    /**
     * Cuenta ventas en un período por sucursal
     */
    @Query("SELECT COUNT(v) FROM Venta v " +
           "WHERE v.sucursalId = :sucursalId " +
           "AND v.fechaVenta BETWEEN :inicio AND :fin")
    Long contarVentasPeriodoPorSucursal(
        @Param("inicio") LocalDateTime inicio,
        @Param("fin") LocalDateTime fin,
        @Param("sucursalId") Integer sucursalId
    );

    /**
     * Cuenta clientes únicos en un período por sucursal
     */
    @Query("SELECT COUNT(DISTINCT v.clienteId) FROM Venta v " +
           "WHERE v.sucursalId = :sucursalId " +
           "AND v.fechaVenta BETWEEN :inicio AND :fin")
    Long contarClientesUnicosPeriodoPorSucursal(
        @Param("inicio") LocalDateTime inicio,
        @Param("fin") LocalDateTime fin,
        @Param("sucursalId") Integer sucursalId
    );

    /**
     * Calcula ventas por método de pago en un período
     */
    @Query("SELECT COALESCE(SUM(v.total), 0) FROM Venta v " +
           "WHERE v.fechaVenta BETWEEN :inicio AND :fin " +
           "AND v.metodoPago = :metodoPago " +
           "AND (:sucursalId IS NULL OR v.sucursalId = :sucursalId)")
    BigDecimal calcularVentasPorMetodoPago(
        @Param("inicio") LocalDateTime inicio,
        @Param("fin") LocalDateTime fin,
        @Param("metodoPago") String metodoPago,
        @Param("sucursalId") Integer sucursalId
    );

    // ==================== ANALÍTICA ANA-04: RENDIMIENTO DE PERSONAL ====================

    /**
     * Obtiene las ventas de un vendedor en un periodo (sin detalles, para cálculo de KPIs).
     */
    @Query("SELECT v FROM Venta v " +
           "WHERE v.vendedorId = :vendedorId " +
           "AND v.fechaVenta BETWEEN :inicio AND :fin " +
           "ORDER BY v.fechaVenta ASC")
    List<Venta> findVentasByVendedorAndPeriodo(
        @Param("vendedorId") Integer vendedorId,
        @Param("inicio") LocalDateTime inicio,
        @Param("fin") LocalDateTime fin
    );

    /**
     * Calcula la hora del día con más ventas para un vendedor en un periodo.
     * Retorna Object[]: [hora (0-23), conteo de ventas en esa hora]
     * La primera fila tendrá la hora pico (mayor conteo).
     */
    @Query("SELECT FUNCTION('HOUR', v.fechaVenta), COUNT(v) " +
           "FROM Venta v " +
           "WHERE v.vendedorId = :vendedorId " +
           "AND v.fechaVenta BETWEEN :inicio AND :fin " +
           "GROUP BY FUNCTION('HOUR', v.fechaVenta) " +
           "ORDER BY COUNT(v) DESC")
    List<Object[]> findHoraPicoByVendedorAndPeriodo(
        @Param("vendedorId") Integer vendedorId,
        @Param("inicio") LocalDateTime inicio,
        @Param("fin") LocalDateTime fin
    );
}