package com.nexoohub.almacen.finanzas.repository;

import com.nexoohub.almacen.finanzas.dto.AuditoriaPrecioDTO;
import com.nexoohub.almacen.finanzas.entity.HistorialPrecio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HistorialPrecioRepository extends JpaRepository<HistorialPrecio, Integer> {

    java.util.Optional<HistorialPrecio> findTopBySkuInternoOrderByFechaCalculoDesc(String skuInterno);
    
    /**
     * Obtiene auditoría de cambios de precio con información del producto.
     * 
     * @param fechaInicio fecha inicial
     * @param fechaFin fecha final
     * @return lista de cambios de precio
     */
    @Query("SELECT new com.nexoohub.almacen.finanzas.dto.AuditoriaPrecioDTO(" +
           "h.id, h.skuInterno, p.nombreComercial, h.precioAnterior, " +
           "h.precioFinalPublico, h.porcentajeCambio, h.razonCambio, " +
           "h.fechaCalculo, h.usuarioCreacion) " +
           "FROM HistorialPrecio h " +
           "JOIN ProductoMaestro p ON h.skuInterno = p.skuInterno " +
           "WHERE h.fechaCalculo BETWEEN :fechaInicio AND :fechaFin " +
           "AND h.precioAnterior IS NOT NULL " +
           "ORDER BY h.fechaCalculo DESC")
    List<AuditoriaPrecioDTO> obtenerAuditoriaCambiosPrecio(
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin
    );
    
    /**
     * Obtiene historial de cambios de un producto específico.
     * 
     * @param skuInterno SKU del producto
     * @return lista de cambios históricos
     */
    @Query("SELECT new com.nexoohub.almacen.finanzas.dto.AuditoriaPrecioDTO(" +
           "h.id, h.skuInterno, p.nombreComercial, h.precioAnterior, " +
           "h.precioFinalPublico, h.porcentajeCambio, h.razonCambio, " +
           "h.fechaCalculo, h.usuarioCreacion) " +
           "FROM HistorialPrecio h " +
           "JOIN ProductoMaestro p ON h.skuInterno = p.skuInterno " +
           "WHERE h.skuInterno = :skuInterno " +
           "ORDER BY h.fechaCalculo DESC")
    List<AuditoriaPrecioDTO> obtenerHistorialPorProducto(@Param("skuInterno") String skuInterno);
    
    /**
     * Obtiene cambios de precio en un periodo específico.
     * Alias para obtenerAuditoriaCambiosPrecio para compatibilidad con controller.
     */
    default List<AuditoriaPrecioDTO> obtenerCambiosPorFecha(
            LocalDateTime fechaInicio, 
            LocalDateTime fechaFin) {
        return obtenerAuditoriaCambiosPrecio(fechaInicio, fechaFin);
    }
    
    /**
     * Obtiene cambios de precio que superan un porcentaje específico.
     * 
     * @param porcentaje porcentaje mínimo de cambio
     * @return lista de cambios significativos
     */
    @Query("SELECT new com.nexoohub.almacen.finanzas.dto.AuditoriaPrecioDTO(" +
           "h.id, h.skuInterno, p.nombreComercial, h.precioAnterior, " +
           "h.precioFinalPublico, h.porcentajeCambio, h.razonCambio, " +
           "h.fechaCalculo, h.usuarioCreacion) " +
           "FROM HistorialPrecio h " +
           "JOIN ProductoMaestro p ON h.skuInterno = p.skuInterno " +
           "WHERE ABS(h.porcentajeCambio) >= :porcentaje " +
           "ORDER BY ABS(h.porcentajeCambio) DESC, h.fechaCalculo DESC")
    List<AuditoriaPrecioDTO> obtenerCambiosSignificativos(@Param("porcentaje") java.math.BigDecimal porcentaje);
}
