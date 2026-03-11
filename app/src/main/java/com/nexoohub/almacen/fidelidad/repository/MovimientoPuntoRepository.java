package com.nexoohub.almacen.fidelidad.repository;

import com.nexoohub.almacen.fidelidad.entity.MovimientoPunto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para gestión de movimientos de puntos.
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
@Repository
public interface MovimientoPuntoRepository extends JpaRepository<MovimientoPunto, Integer> {

    /**
     * Obtiene el historial de movimientos de un programa específico.
     * 
     * @param programaId ID del programa de fidelidad
     * @return lista de movimientos ordenados por fecha descendente
     */
    @Query("SELECT m FROM MovimientoPunto m WHERE m.programaId = :programaId ORDER BY m.fechaCreacion DESC")
    List<MovimientoPunto> obtenerHistorialPorPrograma(@Param("programaId") Integer programaId);

    /**
     * Obtiene movimientos por tipo (ACUMULACION o CANJE).
     * 
     * @param programaId ID del programa
     * @param tipoMovimiento tipo de movimiento
     * @return lista de movimientos filtrados
     */
    @Query("SELECT m FROM MovimientoPunto m WHERE m.programaId = :programaId AND m.tipoMovimiento = :tipo ORDER BY m.fechaCreacion DESC")
    List<MovimientoPunto> obtenerMovimientosPorTipo(@Param("programaId") Integer programaId, @Param("tipo") String tipoMovimiento);

    /**
     * Obtiene movimientos en un rango de fechas.
     * 
     * @param programaId ID del programa
     * @param fechaInicio fecha inicial
     * @param fechaFin fecha final
     * @return lista de movimientos en el rango
     */
    @Query("SELECT m FROM MovimientoPunto m WHERE m.programaId = :programaId AND m.fechaCreacion BETWEEN :fechaInicio AND :fechaFin ORDER BY m.fechaCreacion DESC")
    List<MovimientoPunto> obtenerMovimientosPorFechas(
            @Param("programaId") Integer programaId,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin
    );

    /**
     * Obtiene movimientos asociados a una venta específica.
     * 
     * @param ventaId ID de la venta
     * @return lista de movimientos de esa venta
     */
    List<MovimientoPunto> findByVentaId(Integer ventaId);
}
