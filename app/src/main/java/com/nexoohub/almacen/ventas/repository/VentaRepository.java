package com.nexoohub.almacen.ventas.repository;

import com.nexoohub.almacen.ventas.entity.Venta;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
}