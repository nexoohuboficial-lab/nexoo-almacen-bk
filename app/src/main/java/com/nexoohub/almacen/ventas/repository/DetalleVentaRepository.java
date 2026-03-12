package com.nexoohub.almacen.ventas.repository;

import com.nexoohub.almacen.ventas.entity.DetalleVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Integer> {
    
    /**
     * Busca todos los detalles de una venta específica.
     * 
     * @param ventaId ID de la venta
     * @return Lista de detalles de la venta
     */
    List<DetalleVenta> findByVentaId(Integer ventaId);
    
    /**
     * Busca detalles de múltiples ventas.
     * Útil para análisis agregados por período.
     * 
     * @param ventaIds Lista de IDs de ventas
     * @return Lista de detalles
     */
    List<DetalleVenta> findByVentaIdIn(List<Integer> ventaIds);

    // ==================== MÉTODOS PARA MÉTRICAS FINANCIERAS ====================

    /**
     * Obtiene los detalles de ventas con información del producto.
     * Utilizado para análisis de top productos por ingresos en métricas financieras.
     * 
     * Retorna un Object[] con:
     * [0] skuInterno (String)
     * [1] nombreComercial (String)
     * [2] marca (String)
     * [3] categoria (String)
     * [4] cantidad (BigDecimal)
     * [5] precioUnitario (BigDecimal)
     * [6] subtotal (BigDecimal)
     * [7] descuento (BigDecimal)
     * 
     * @param ventaIds Lista de IDs de ventas a analizar
     * @return Lista de Object[] con información agregada
     */
    @Query("SELECT p.skuInterno, p.nombreComercial, " +
           "p.marca, p.categoria, " +
           "d.cantidad, d.precioUnitarioVenta, " +
           "(d.cantidad * d.precioUnitarioVenta), d.descuentoEspecial " +
           "FROM DetalleVenta d " +
           "JOIN ProductoMaestro p ON p.skuInterno = d.skuInterno " +
           "WHERE d.ventaId IN :ventaIds")
    List<Object[]> findDetallesPorVentasConProducto(@Param("ventaIds") List<Integer> ventaIds);
}