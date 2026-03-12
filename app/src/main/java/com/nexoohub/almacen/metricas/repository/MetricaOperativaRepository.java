package com.nexoohub.almacen.metricas.repository;

import com.nexoohub.almacen.metricas.entity.MetricaOperativa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para gestionar métricas operacionales.
 * 
 * <p>Proporciona métodos para:</p>
 * <ul>
 *   <li>Buscar métricas consolidadas y por sucursal</li>
 *   <li>Filtrar por tipo de período (DIARIO, MENSUAL, etc.)</li>
 *   <li>Consultar métricas históricas</li>
 *   <li>Identificar snapshots existentes para evitar duplicados</li>
 * </ul>
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
@Repository
public interface MetricaOperativaRepository extends JpaRepository<MetricaOperativa, Long> {

    /**
     * Busca la métrica consolidada (todas las sucursales) para un período específico.
     * 
     * @param inicio Fecha de inicio del período
     * @param fin Fecha de fin del período
     * @return Optional con la métrica consolidada si existe
     */
    @Query("SELECT m FROM MetricaOperativa m " +
           "WHERE m.sucursalId IS NULL " +
           "AND m.periodoInicio = :inicio " +
           "AND m.periodoFin = :fin " +
           "ORDER BY m.fechaCreacion DESC")
    Optional<MetricaOperativa> findMetricaConsolidada(
        @Param("inicio") LocalDate inicio,
        @Param("fin") LocalDate fin
    );

    /**
     * Busca la métrica de una sucursal específica para un período.
     * 
     * @param sucursalId ID de la sucursal
     * @param inicio Fecha de inicio del período
     * @param fin Fecha de fin del período
     * @return Optional con la métrica de la sucursal si existe
     */
    @Query("SELECT m FROM MetricaOperativa m " +
           "WHERE m.sucursalId = :sucursalId " +
           "AND m.periodoInicio = :inicio " +
           "AND m.periodoFin = :fin " +
           "ORDER BY m.fechaCreacion DESC")
    Optional<MetricaOperativa> findMetricaPorSucursal(
        @Param("sucursalId") Integer sucursalId,
        @Param("inicio") LocalDate inicio,
        @Param("fin") LocalDate fin
    );

    /**
     * Busca métricas por tipo de período (para comparaciones históricas).
     * 
     * @param tipoPeriodo Tipo de período (DIARIO, SEMANAL, MENSUAL, etc.)
     * @param sucursalId ID de la sucursal (null = consolidado)
     * @return Lista de métricas ordenadas por período más reciente primero
     */
    @Query("SELECT m FROM MetricaOperativa m " +
           "WHERE m.tipoPeriodo = :tipoPeriodo " +
           "AND (:sucursalId IS NULL AND m.sucursalId IS NULL OR m.sucursalId = :sucursalId) " +
           "ORDER BY m.periodoFin DESC, m.periodoInicio DESC")
    List<MetricaOperativa> findByTipoPeriodoAndSucursal(
        @Param("tipoPeriodo") String tipoPeriodo,
        @Param("sucursalId") Integer sucursalId
    );

    /**
     * Obtiene historial consolidado de métricas hasta una fecha límite.
     * 
     * @param hasta Fecha límite (inclusiva)
     * @return Lista de métricas consolidadas recientes
     */
    @Query("SELECT m FROM MetricaOperativa m " +
           "WHERE m.sucursalId IS NULL " +
           "AND m.periodoFin <= :hasta " +
           "ORDER BY m.periodoFin DESC, m.periodoInicio DESC")
    List<MetricaOperativa> findHistorialConsolidado(
        @Param("hasta") LocalDate hasta
    );

    /**
     * Obtiene historial de métricas por sucursal hasta una fecha límite.
     * 
     * @param sucursalId ID de la sucursal
     * @param hasta Fecha límite (inclusiva)
     * @return Lista de métricas de la sucursal
     */
    @Query("SELECT m FROM MetricaOperativa m " +
           "WHERE m.sucursalId = :sucursalId " +
           "AND m.periodoFin <= :hasta " +
           "ORDER BY m.periodoFin DESC, m.periodoInicio DESC")
    List<MetricaOperativa> findHistorialPorSucursal(
        @Param("sucursalId") Integer sucursalId,
        @Param("hasta") LocalDate hasta
    );

    /**
     * Encuentra las top sucursales por volumen de operaciones.
     * 
     * @param inicio Fecha de inicio del período
     * @param fin Fecha de fin del período
     * @return Lista de métricas de las sucursales más activas
     */
    @Query("SELECT m FROM MetricaOperativa m " +
           "WHERE m.sucursalId IS NOT NULL " +
           "AND m.periodoInicio = :inicio " +
           "AND m.periodoFin = :fin " +
           "ORDER BY m.totalOperaciones DESC")
    List<MetricaOperativa> findTopSucursalesPorActividad(
        @Param("inicio") LocalDate inicio,
        @Param("fin") LocalDate fin
    );

    /**
     * Verifica si ya existe una métrica consolidada para un período.
     * 
     * @param inicio Fecha de inicio
     * @param fin Fecha de fin
     * @return true si existe, false si no
     */
    @Query("SELECT CASE WHEN COUNT(m) > 0 THEN true ELSE false END " +
           "FROM MetricaOperativa m " +
           "WHERE m.sucursalId IS NULL " +
           "AND m.periodoInicio = :inicio " +
           "AND m.periodoFin = :fin")
    boolean existsMetricaConsolidada(
        @Param("inicio") LocalDate inicio,
        @Param("fin") LocalDate fin
    );

    /**
     * Verifica si ya existe una métrica para una sucursal en un período.
     * 
     * @param sucursalId ID de la sucursal
     * @param inicio Fecha de inicio
     * @param fin Fecha de fin
     * @return true si existe, false si no
     */
    @Query("SELECT CASE WHEN COUNT(m) > 0 THEN true ELSE false END " +
           "FROM MetricaOperativa m " +
           "WHERE m.sucursalId = :sucursalId " +
           "AND m.periodoInicio = :inicio " +
           "AND m.periodoFin = :fin")
    boolean existsMetricaPorSucursal(
        @Param("sucursalId") Integer sucursalId,
        @Param("inicio") LocalDate inicio,
        @Param("fin") LocalDate fin
    );

    /**
     * Elimina métricas antiguas anteriores a una fecha (limpieza de mantenimiento).
     * 
     * @param fechaLimite Fecha anterior a la cual se eliminarán las métricas
     */
    @Query("DELETE FROM MetricaOperativa m WHERE m.periodoFin < :fechaLimite")
    void deleteMetricasAntiguas(@Param("fechaLimite") LocalDate fechaLimite);

    /**
     * Busca métricas por clasificación de actividad.
     * 
     * @param clasificacion Clasificación de actividad (ALTO, MEDIO, BAJO)
     * @param desde Fecha desde
     * @param hasta Fecha hasta
     * @return Lista de métricas con esa clasificación
     */
    @Query("SELECT m FROM MetricaOperativa m " +
           "WHERE m.clasificacionActividad = :clasificacion " +
           "AND m.periodoInicio >= :desde " +
           "AND m.periodoFin <= :hasta " +
           "ORDER BY m.totalOperaciones DESC")
    List<MetricaOperativa> findByClasificacionActividad(
        @Param("clasificacion") String clasificacion,
        @Param("desde") LocalDate desde,
        @Param("hasta") LocalDate hasta
    );
}
