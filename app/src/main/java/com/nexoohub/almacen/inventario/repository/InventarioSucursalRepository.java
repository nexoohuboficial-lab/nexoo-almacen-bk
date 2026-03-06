package com.nexoohub.almacen.inventario.repository;

import com.nexoohub.almacen.inventario.entity.InventarioSucursal;
import com.nexoohub.almacen.inventario.entity.InventarioSucursalId;
import com.nexoohub.almacen.inventario.dto.InventarioSucursalDTO;
import com.nexoohub.almacen.inventario.dto.InventarioSucursalProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventarioSucursalRepository extends JpaRepository<InventarioSucursal, InventarioSucursalId> {
    
    // Magia de Spring Data: Busca dentro de la llave compuesta (id) el campo 'sucursalId'
    List<InventarioSucursal> findByIdSucursalId(Integer sucursalId);
    Page<InventarioSucursal> findByIdSucursalId(Integer sucursalId, Pageable pageable);
    
    @Query("SELECT new com.nexoohub.almacen.inventario.dto.InventarioSucursalDTO(" +
           "i.id.skuInterno, p.nombreComercial, i.stockActual, i.costoPromedioPonderado) " +
           "FROM InventarioSucursal i " +
           "JOIN ProductoMaestro p ON i.id.skuInterno = p.skuInterno " +
           "WHERE i.id.sucursalId = :sucursalId " +
           "ORDER BY p.nombreComercial ASC")
    List<InventarioSucursalDTO> obtenerFotografiaInventario(@Param("sucursalId") Integer sucursalId);
    
    // Versión con paginación usando SQL nativo con proyección de interfaz
    @Query(value = "SELECT i.sku_interno AS skuInterno, p.nombre_comercial AS nombreComercial, " +
           "i.stock_actual AS stockActual, i.costo_promedio_ponderado AS costoPromedioPonderado " +
           "FROM inventario_sucursal i " +
           "JOIN producto_maestro p ON i.sku_interno = p.sku_interno " +
           "WHERE i.sucursal_id = :sucursalId",
           countQuery = "SELECT COUNT(*) FROM inventario_sucursal i WHERE i.sucursal_id = :sucursalId",
           nativeQuery = true)
    Page<InventarioSucursalProjection> obtenerFotografiaInventarioPaginado(@Param("sucursalId") Integer sucursalId, Pageable pageable);
}
