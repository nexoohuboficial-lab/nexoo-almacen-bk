package com.nexoohub.almacen.ventas.repository;

import com.nexoohub.almacen.ventas.entity.Devolucion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para operaciones CRUD de Devoluciones.
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
@Repository
public interface DevolucionRepository extends JpaRepository<Devolucion, Integer> {
    
    /**
     * Obtiene todas las devoluciones de una venta específica.
     * 
     * @param ventaId ID de la venta
     * @return lista de devoluciones
     */
    List<Devolucion> findByVentaId(Integer ventaId);
    
    /**
     * Obtiene devoluciones por sucursal en un rango de fechas.
     * Si sucursalId es null, trae de todas las sucursales.
     * 
     * @param sucursalId ID de la sucursal (null para todas)
     * @param fechaInicio fecha inicial
     * @param fechaFin fecha final
     * @return lista de devoluciones
     */
    @Query("SELECT d FROM Devolucion d " +
           "WHERE (:sucursalId IS NULL OR d.sucursalId = :sucursalId) " +
           "AND d.fechaDevolucion BETWEEN :fechaInicio AND :fechaFin " +
           "ORDER BY d.fechaDevolucion DESC")
    List<Devolucion> findBySucursalAndFecha(
            @Param("sucursalId") Integer sucursalId,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin
    );
}