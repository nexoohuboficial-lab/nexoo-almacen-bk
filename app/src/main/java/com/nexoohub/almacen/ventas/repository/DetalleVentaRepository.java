package com.nexoohub.almacen.ventas.repository;

import com.nexoohub.almacen.ventas.entity.DetalleVenta;
import org.springframework.data.jpa.repository.JpaRepository;
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
}