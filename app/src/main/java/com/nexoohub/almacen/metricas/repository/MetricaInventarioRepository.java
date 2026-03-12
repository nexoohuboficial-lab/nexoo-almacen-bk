package com.nexoohub.almacen.metricas.repository;

import com.nexoohub.almacen.metricas.entity.MetricaInventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para métricas de inventario.
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
@Repository
public interface MetricaInventarioRepository extends JpaRepository<MetricaInventario, Long> {

    /**
     * Busca snapshot consolidado por fecha.
     * 
     * @param fechaCorte Fecha del snapshot
     * @return Métrica consolidada
     */
    Optional<MetricaInventario> findBySucursalIdIsNullAndFechaCorte(LocalDate fechaCorte);

    /**
     * Busca snapshot de una sucursal específica por fecha.
     * 
     * @param sucursalId ID de la sucursal
     * @param fechaCorte Fecha del snapshot
     * @return Métrica de la sucursal
     */
    Optional<MetricaInventario> findBySucursalIdAndFechaCorte(Integer sucursalId, LocalDate fechaCorte);

    /**
     * Obtiene histórico de métricas consolidadas.
     * 
     * @return Lista ordenada por fecha descendente
     */
    List<MetricaInventario> findBySucursalIdIsNullOrderByFechaCorteDesc();

    /**
     * Obtiene histórico de métricas de una sucursal.
     * 
     * @param sucursalId ID de la sucursal
     * @return Lista ordenada por fecha descendente
     */
    List<MetricaInventario> findBySucursalIdOrderByFechaCorteDesc(Integer sucursalId);

    /**
     * Obtiene métricas de todas las sucursales en una fecha.
     * 
     * @param fechaCorte Fecha del snapshot
     * @return Lista de métricas por sucursal
     */
    @Query("SELECT m FROM MetricaInventario m WHERE m.fechaCorte = :fechaCorte AND m.sucursalId IS NOT NULL ORDER BY m.nombreSucursal")
    List<MetricaInventario> findByFechaCorteAndSucursalIdIsNotNull(@Param("fechaCorte") LocalDate fechaCorte);

    /**
     * Elimina snapshot por fecha y sucursal (para recalcular).
     * 
     * @param sucursalId ID sucursal (puede ser null)
     * @param fechaCorte Fecha snapshot
     */
    void deleteBySucursalIdAndFechaCorte(Integer sucursalId, LocalDate fechaCorte);

    /**
     * Obtiene último snapshot consolidado.
     * 
     * @return Última métrica registrada
     */
    @Query("SELECT m FROM MetricaInventario m WHERE m.sucursalId IS NULL ORDER BY m.fechaCorte DESC LIMIT 1")
    Optional<MetricaInventario> findUltimoSnapshotConsolidado();

    /**
     * Obtiene último snapshot de una sucursal.
     * 
     * @param sucursalId ID de la sucursal
     * @return Última métrica registrada
     */
    @Query("SELECT m FROM MetricaInventario m WHERE m.sucursalId = :sucursalId ORDER BY m.fechaCorte DESC LIMIT 1")
    Optional<MetricaInventario> findUltimoSnapshotSucursal(@Param("sucursalId") Integer sucursalId);
}
