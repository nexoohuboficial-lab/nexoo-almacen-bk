package com.nexoohub.almacen.adquisiciones.repository;

import com.nexoohub.almacen.adquisiciones.entity.CatalogoProveedorProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CatalogoProveedorProductoRepository extends JpaRepository<CatalogoProveedorProducto, Long> {
    
    // Buscar todos los proveedores que ofrecen un producto (para el comparador)
    @Query("SELECT c FROM CatalogoProveedorProducto c JOIN FETCH c.proveedor WHERE c.producto.skuInterno = :sku AND c.disponibilidad = true ORDER BY c.precioCompraActual ASC")
    List<CatalogoProveedorProducto> findProveedoresDisponiblesByProductoOrderByPrecioAsc(@Param("sku") String sku);
    
    // Buscar un registro específico
    CatalogoProveedorProducto findByProveedorIdAndProductoSkuInterno(Long proveedorId, String sku);
}
