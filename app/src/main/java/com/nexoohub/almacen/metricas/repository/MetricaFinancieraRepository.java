package com.nexoohub.almacen.metricas.repository;

import com.nexoohub.almacen.metricas.entity.MetricaFinanciera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para gestionar métricas financieras consolidadas.
 * 
 * <p>Proporciona queries para calcular métricas en tiempo real
 * y recuperar snapshots históricos almacenados.</p>
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
@Repository
public interface MetricaFinancieraRepository extends JpaRepository<MetricaFinanciera, Long> {

    /**
     * Busca métrica financiera por período específico (consolidada)
     */
    Optional<MetricaFinanciera> findBySucursalIdIsNullAndPeriodoInicioAndPeriodoFin(
            LocalDate periodoInicio, 
            LocalDate periodoFin
    );

    /**
     * Busca métrica financiera por sucursal y período
     */
    Optional<MetricaFinanciera> findBySucursalIdAndPeriodoInicioAndPeriodoFin(
            Integer sucursalId,
            LocalDate periodoInicio, 
            LocalDate periodoFin
    );

    /**
     * Obtiene métricas históricas ordenadas por período (más reciente primero)
     */
    List<MetricaFinanciera> findBySucursalIdIsNullOrderByPeriodoFinDesc();

    /**
     * Obtiene métricas históricas de una sucursal específica
     */
    List<MetricaFinanciera> findBySucursalIdOrderByPeriodoFinDesc(Integer sucursalId);

    /**
     * Obtiene métricas de un rango de fechas para comparaciones
     */
    @Query("SELECT m FROM MetricaFinanciera m " +
           "WHERE m.sucursalId IS NULL " +
           "AND m.periodoFin BETWEEN :fechaDesde AND :fechaHasta " +
           "ORDER BY m.periodoFin DESC")
    List<MetricaFinanciera> findMetricasEnRango(
            @Param("fechaDesde") LocalDate fechaDesde,
            @Param("fechaHasta") LocalDate fechaHasta
    );

    /**
     * Elimina métricas antiguas (para recalcular)
     */
    void deleteByPeriodoInicioAndPeriodoFin(LocalDate periodoInicio, LocalDate periodoFin);

    /**
     * Elimina métricas de una sucursal específica
     */
    void deleteBySucursalIdAndPeriodoInicioAndPeriodoFin(
            Integer sucursalId,
            LocalDate periodoInicio, 
            LocalDate periodoFin
    );
}
