package com.nexoohub.almacen.inventario.repository;

import com.nexoohub.almacen.inventario.entity.AnalisisABC;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para análisis ABC de inventario.
 * 
 * @author NexooHub Development Team
 * @version 1.0
 */
@Repository
public interface AnalisisABCRepository extends JpaRepository<AnalisisABC, Integer> {

    /**
     * Buscar análisis por sucursal y fecha
     */
    List<AnalisisABC> findBySucursalIdAndFechaAnalisis(Integer sucursalId, LocalDate fechaAnalisis);

    /**
     * Buscar análisis por sucursal ordenado por valor (descendente)
     */
    List<AnalisisABC> findBySucursalIdAndFechaAnalisisOrderByValorVentasDesc(
            Integer sucursalId, 
            LocalDate fechaAnalisis
    );

    /**
     * Buscar análisis por clasificación
     */
    List<AnalisisABC> findBySucursalIdAndClasificacionAndFechaAnalisis(
            Integer sucursalId,
            String clasificacion,
            LocalDate fechaAnalisis
    );

    /**
     * Buscar análisis por producto y sucursal (más reciente)
     */
    Optional<AnalisisABC> findTopBySkuProductoAndSucursalIdOrderByFechaAnalisisDesc(
            String skuProducto,
            Integer sucursalId
    );

    /**
     * Buscar análisis más recientes por sucursal
     */
    @Query("SELECT a FROM AnalisisABC a WHERE a.sucursalId = :sucursalId " +
           "AND a.fechaAnalisis = (SELECT MAX(a2.fechaAnalisis) FROM AnalisisABC a2 " +
           "WHERE a2.sucursalId = :sucursalId) " +
           "ORDER BY a.valorVentas DESC")
    List<AnalisisABC> findUltimoAnalisisBySucursal(@Param("sucursalId") Integer sucursalId);

    /**
     * Contar productos por clasificación
     */
    @Query("SELECT a.clasificacion, COUNT(a) FROM AnalisisABC a " +
           "WHERE a.sucursalId = :sucursalId AND a.fechaAnalisis = :fecha " +
           "GROUP BY a.clasificacion")
    List<Object[]> contarPorClasificacion(
            @Param("sucursalId") Integer sucursalId, 
            @Param("fecha") LocalDate fecha
    );

    /**
     * Obtener resumen de valor por clasificación
     */
    @Query("SELECT a.clasificacion, SUM(a.valorVentas), COUNT(a) FROM AnalisisABC a " +
           "WHERE a.sucursalId = :sucursalId AND a.fechaAnalisis = :fecha " +
           "GROUP BY a.clasificacion " +
           "ORDER BY a.clasificacion")
    List<Object[]> resumenPorClasificacion(
            @Param("sucursalId") Integer sucursalId,
            @Param("fecha") LocalDate fecha
    );

    /**
     * Eliminar análisis antiguos (para limpieza)
     */
    void deleteByFechaAnalisisBefore(LocalDate fecha);

    /**
     * Verificar si existe análisis para una fecha y sucursal
     */
    boolean existsBySucursalIdAndFechaAnalisis(Integer sucursalId, LocalDate fechaAnalisis);
}
