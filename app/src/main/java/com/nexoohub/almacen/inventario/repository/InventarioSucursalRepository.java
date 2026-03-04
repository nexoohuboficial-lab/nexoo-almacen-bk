package com.nexoohub.almacen.inventario.repository;

import com.nexoohub.almacen.inventario.entity.InventarioSucursal;
import com.nexoohub.almacen.inventario.entity.InventarioSucursalId;
import com.nexoohub.almacen.inventario.dto.InventarioSucursalDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventarioSucursalRepository extends JpaRepository<InventarioSucursal, InventarioSucursalId> {
    
    // Magia de Spring Data: Busca dentro de la llave compuesta (id) el campo 'sucursalId'
    List<InventarioSucursal> findByIdSucursalId(Integer sucursalId);
    @Query("SELECT new com.nexoohub.almacen.inventario.dto.InventarioSucursalDTO(" +
           "i.id.skuInterno, p.nombreComercial, i.stockActual, i.costoPromedioPonderado) " +
           "FROM InventarioSucursal i " +
           "JOIN ProductoMaestro p ON i.id.skuInterno = p.skuInterno " +
           "WHERE i.id.sucursalId = :sucursalId " +
           "ORDER BY p.nombreComercial ASC")
    List<InventarioSucursalDTO> obtenerFotografiaInventario(@Param("sucursalId") Integer sucursalId);
}
