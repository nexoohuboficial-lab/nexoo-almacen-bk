package com.nexoohub.almacen.metricas.repository;

import com.nexoohub.almacen.metricas.entity.MetricaVentaCliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository para el acceso a métricas de ventas y clientes.
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
@Repository
public interface MetricaVentaClienteRepository extends JpaRepository<MetricaVentaCliente, Long> {

    /**
     * Obtiene la métrica consolidada (todas las sucursales) para un período específico.
     * 
     * @param fechaInicio Fecha de inicio del período
     * @param fechaFin Fecha de fin del período
     * @return Métrica consolidada del período
     */
    @Query("SELECT m FROM MetricaVentaCliente m " +
           "WHERE m.sucursalId IS NULL " +
           "AND m.periodoInicio = :fechaInicio " +
           "AND m.periodoFin = :fechaFin")
    Optional<MetricaVentaCliente> findMetricaConsolidada(
        @Param("fechaInicio") LocalDate fechaInicio,
        @Param("fechaFin") LocalDate fechaFin
    );

    /**
     * Obtiene la métrica de una sucursal específica para un período.
     * 
     * @param sucursalId ID de la sucursal
     * @param fechaInicio Fecha de inicio del período
     * @param fechaFin Fecha de fin del período
     * @return Métrica de la sucursal en el período
     */
    @Query("SELECT m FROM MetricaVentaCliente m " +
           "WHERE m.sucursalId = :sucursalId " +
           "AND m.periodoInicio = :fechaInicio " +
           "AND m.periodoFin = :fechaFin")
    Optional<MetricaVentaCliente> findMetricaPorSucursal(
        @Param("sucursalId") Integer sucursalId,
        @Param("fechaInicio") LocalDate fechaInicio,
        @Param("fechaFin") LocalDate fechaFin
    );

    /**
     * Obtiene todas las métricas (consolidada + por sucursal) de un tipo de período específico.
     * 
     * @param tipoPeriodo Tipo de período (DIARIO, SEMANAL, MENSUAL, etc.)
     * @return Lista de métricas ordenadas por fecha descendente
     */
    @Query("SELECT m FROM MetricaVentaCliente m " +
           "WHERE m.tipoPeriodo = :tipoPeriodo " +
           "ORDER BY m.periodoFin DESC, m.periodoInicio DESC")
    List<MetricaVentaCliente> findByTipoPeriodo(@Param("tipoPeriodo") String tipoPeriodo);

    /**
     * Obtiene el historial de métricas consolidadas (últimos N períodos).
     * 
     * @param tipoPeriodo Tipo de período
     * @param fechaHasta Fecha límite
     * @return Lista de métricas consolidadas ordenadas por fecha descendente
     */
    @Query("SELECT m FROM MetricaVentaCliente m " +
           "WHERE m.sucursalId IS NULL " +
           "AND m.tipoPeriodo = :tipoPeriodo " +
           "AND m.periodoFin <= :fechaHasta " +
           "ORDER BY m.periodoFin DESC, m.periodoInicio DESC")
    List<MetricaVentaCliente> findHistorialConsolidado(
        @Param("tipoPeriodo") String tipoPeriodo,
        @Param("fechaHasta") LocalDate fechaHasta
    );

    /**
     * Obtiene el historial de métricas de una sucursal específica.
     * 
     * @param sucursalId ID de la sucursal
     * @param tipoPeriodo Tipo de período
     * @param fechaHasta Fecha límite
     * @return Lista de métricas de la sucursal ordenadas por fecha descendente
     */
    @Query("SELECT m FROM MetricaVentaCliente m " +
           "WHERE m.sucursalId = :sucursalId " +
           "AND m.tipoPeriodo = :tipoPeriodo " +
           "AND m.periodoFin <= :fechaHasta " +
           "ORDER BY m.periodoFin DESC, m.periodoInicio DESC")
    List<MetricaVentaCliente> findHistorialPorSucursal(
        @Param("sucursalId") Integer sucursalId,
        @Param("tipoPeriodo") String tipoPeriodo,
        @Param("fechaHasta") LocalDate fechaHasta
    );

    /**
     * Obtiene las sucursales con mejor rendimiento en un período específico.
     * 
     * @param fechaInicio Fecha de inicio del período
     * @param fechaFin Fecha de fin del período
     * @return Lista de métricas ordenadas por total de ventas descendente
     */
    @Query("SELECT m FROM MetricaVentaCliente m " +
           "WHERE m.sucursalId IS NOT NULL " +
           "AND m.periodoInicio = :fechaInicio " +
           "AND m.periodoFin = :fechaFin " +
           "ORDER BY m.totalVentas DESC")
    List<MetricaVentaCliente> findTopSucursales(
        @Param("fechaInicio") LocalDate fechaInicio,
        @Param("fechaFin") LocalDate fechaFin
    );

    /**
     * Cuenta el número de métricas almacenadas para un tipo de período.
     * 
     * @param tipoPeriodo Tipo de período
     * @return Cantidad de métricas
     */
    long countByTipoPeriodo(String tipoPeriodo);

    /**
     * Verifica si ya existe una métrica consolidada para un período específico.
     * 
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @return true si existe, false si no
     */
    @Query("SELECT COUNT(m) > 0 FROM MetricaVentaCliente m " +
           "WHERE m.sucursalId IS NULL " +
           "AND m.periodoInicio = :fechaInicio " +
           "AND m.periodoFin = :fechaFin")
    boolean existsMetricaConsolidada(
        @Param("fechaInicio") LocalDate fechaInicio,
        @Param("fechaFin") LocalDate fechaFin
    );

    /**
     * Obtiene todas las métricas dentro de un rango de fechas.
     * 
     * @param fechaDesde Fecha desde
     * @param fechaHasta Fecha hasta
     * @return Lista de métricas en el rango
     */
    @Query("SELECT m FROM MetricaVentaCliente m " +
           "WHERE m.periodoInicio >= :fechaDesde " +
           "AND m.periodoFin <= :fechaHasta " +
           "ORDER BY m.periodoFin DESC, m.nombreSucursal ASC")
    List<MetricaVentaCliente> findByRangoFechas(
        @Param("fechaDesde") LocalDate fechaDesde,
        @Param("fechaHasta") LocalDate fechaHasta
    );

    /**
     * Elimina métricas antiguas anteriores a una fecha específica.
     * 
     * @param fechaLimite Fecha límite (se eliminan las anteriores)
     */
    @Query("DELETE FROM MetricaVentaCliente m WHERE m.periodoFin < :fechaLimite")
    void deleteMetricasAntiguas(@Param("fechaLimite") LocalDate fechaLimite);
}
