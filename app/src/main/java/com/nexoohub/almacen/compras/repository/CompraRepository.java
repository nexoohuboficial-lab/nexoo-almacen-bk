package com.nexoohub.almacen.compras.repository;

import com.nexoohub.almacen.compras.entity.Compra;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CompraRepository extends JpaRepository<Compra, Integer> {
    
    /**
     * Carga una compra con todas sus relaciones en 1 sola query
     * Evita N+1: Sin esto se harían 1 query para compra + N para detalles + N para productos + 1 para proveedor
     * Con esto: 1 sola query con LEFT JOINs
     */
    @EntityGraph(attributePaths = {"detalles", "detalles.producto", "proveedor"})
    Optional<Compra> findWithDetallesById(Integer id);
    
    /**
     * Buscar compras por proveedor (sin cargar relaciones automáticamente para evitar ambigüedad)
     * Usa el campo primitivo proveedorId para filtrado eficiente
     */
    List<Compra> findByProveedorId(Integer proveedorId);
    
    /**
     * Buscar compras por rango de fechas con JOIN FETCH explícito
     * Útil para reportes de inventario y análisis de costos
     */
    @Query("SELECT DISTINCT c FROM Compra c " +
           "LEFT JOIN FETCH c.detalles d " +
           "LEFT JOIN FETCH d.producto " +
           "LEFT JOIN FETCH c.proveedor " +
           "WHERE c.fechaCompra BETWEEN :inicio AND :fin " +
           "ORDER BY c.fechaCompra DESC")
    List<Compra> findByFechaRangoConDetalles(
        @Param("inicio") LocalDateTime inicio,
        @Param("fin") LocalDateTime fin
    );
    
    /**
     * Buscar compras recientes sin cargar detalles (para listados rápidos)
     */
    @EntityGraph(attributePaths = {"proveedor"})
    List<Compra> findTop20ByOrderByFechaCompraDesc();
}
