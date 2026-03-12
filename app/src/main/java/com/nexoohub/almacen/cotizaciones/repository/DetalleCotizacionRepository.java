package com.nexoohub.almacen.cotizaciones.repository;

import com.nexoohub.almacen.cotizaciones.entity.DetalleCotizacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface DetalleCotizacionRepository extends JpaRepository<DetalleCotizacion, Long> {
    
    /**
     * Obtener todos los detalles de una cotización específica
     */
    List<DetalleCotizacion> findByCotizacion_Id(Long cotizacionId);
    
    /**
     * Obtener detalles por FK cotizacion_id con fetch join de producto
     * (para evitar problemas con insertable=false y lazy loading)
     */
    @Query("SELECT d FROM DetalleCotizacion d LEFT JOIN FETCH d.producto WHERE d.cotizacionId = :cotizacionId")
    List<DetalleCotizacion> findByCotizacionId(@Param("cotizacionId") Long cotizacionId);
    
    /**
     * Obtener detalles de cotizaciones que incluyen un producto específico
     */
    List<DetalleCotizacion> findBySkuInterno(String skuInterno);
    
    /**
     * Buscardetalles de una cotización con un producto específico
     */
    List<DetalleCotizacion> findByCotizacion_IdAndSkuInterno(Long cotizacionId, String skuInterno);
    
    /**
     * Eliminar todos los detalles de una cotización
     */
    @Modifying
    @Transactional
    void deleteByCotizacion_Id(Long cotizacionId);
    
    /**
     * Contar detalles de una cotización
     */
    Long countByCotizacion_Id(Long cotizacionId);
    
    /**
     * Obteneproductos más cotizados en un periodo
     * Retorna: [sku_interno, nombre_producto, cantidad_total_cotizada, veces_cotizado]
     */
    @Query("SELECT d.skuInterno, p.nombreComercial, " +
           "SUM(d.cantidad), COUNT(DISTINCT d.cotizacionId) " +
           "FROM DetalleCotizacion d " +
           "JOIN ProductoMaestro p ON p.skuInterno = d.skuInterno " +
           "JOIN Cotizacion c ON c.id = d.cotizacionId " +
           "WHERE c.fechaCotizacion BETWEEN :fechaInicio AND :fechaFin " +
           "AND c.estado IN ('ENVIADA', 'ACEPTADA', 'CONVERTIDA') " +
           "GROUP BY d.skuInterno, p.nombreComercial " +
           "ORDER BY SUM(d.cantidad) DESC")
    List<Object[]> obtenerProductosMasCotizados(
        @Param("fechaInicio") java.time.LocalDateTime fechaInicio,
        @Param("fechaFin") java.time.LocalDateTime fechaFin
    );
    
    /**
     * Obtener valor total cotizado por producto
     * Retorna: [sku_interno, nombre_producto, valor_total]
     */
    @Query("SELECT d.skuInterno, p.nombreComercial, " +
           "SUM(d.precioUnitario * d.cantidad - d.descuentoEspecial) " +
           "FROM DetalleCotizacion d " +
           "JOIN ProductoMaestro p ON p.skuInterno = d.skuInterno " +
           "JOIN Cotizacion c ON c.id = d.cotizacionId " +
           "WHERE c.fechaCotizacion BETWEEN :fechaInicio AND :fechaFin " +
           "AND c.estado IN ('ENVIADA', 'ACEPTADA', 'CONVERTIDA') " +
           "GROUP BY d.skuInterno, p.nombreComercial " +
           "ORDER BY SUM(d.precioUnitario * d.cantidad - d.descuentoEspecial) DESC")
    List<Object[]> obtenerValorCotizadoPorProducto(
        @Param("fechaInicio") java.time.LocalDateTime fechaInicio,
        @Param("fechaFin") java.time.LocalDateTime fechaFin
    );
    
    /**
     * Calcular el descuento total aplicado en una cotización
     */
    @Query("SELECT COALESCE(SUM(d.descuentoEspecial), 0) FROM DetalleCotizacion d " +
           "WHERE d.cotizacionId = :cotizacionId")
    java.math.BigDecimal calcularDescuentoTotalCotizacion(@Param("cotizacionId") Long cotizacionId);
    
    /**
     * Obtener cantidad total de items en una cotización
     */
    @Query("SELECT COALESCE(SUM(d.cantidad), 0) FROM DetalleCotizacion d " +
           "WHERE d.cotizacionId = :cotizacionId")
    Integer calcularCantidadTotalItems(@Param("cotizacionId") Long cotizacionId);
}
