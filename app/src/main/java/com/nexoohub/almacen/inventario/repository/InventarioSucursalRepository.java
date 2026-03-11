package com.nexoohub.almacen.inventario.repository;

import com.nexoohub.almacen.inventario.entity.InventarioSucursal;
import com.nexoohub.almacen.inventario.entity.InventarioSucursalId;
import com.nexoohub.almacen.inventario.dto.InventarioSucursalDTO;
import com.nexoohub.almacen.inventario.dto.InventarioSucursalProjection;
import com.nexoohub.almacen.inventario.dto.ProductoStockBajoDTO;
import com.nexoohub.almacen.inventario.dto.ProductoCaducidadDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventarioSucursalRepository extends JpaRepository<InventarioSucursal, InventarioSucursalId> {
    
    // Magia de Spring Data: Busca dentro de la llave compuesta (id) el campo 'sucursalId'
    List<InventarioSucursal> findByIdSucursalId(Integer sucursalId);
    Page<InventarioSucursal> findByIdSucursalId(Integer sucursalId, Pageable pageable);
    
    // Buscar por SKU interno y sucursal (para sistema de reservas)
    Optional<InventarioSucursal> findByIdSkuInternoAndIdSucursalId(String skuInterno, Integer sucursalId);
    
    // Buscar por SKU interno en todas las sucursales (para cálculo de rentabilidad)
    List<InventarioSucursal> findByIdSkuInterno(String skuInterno);
    
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
    
    /**
     * Consulta productos con stock por debajo del mínimo en una sucursal.
     * Útil para generar alertas de reabastecimiento.
     */
    @Query("SELECT new com.nexoohub.almacen.inventario.dto.ProductoStockBajoDTO(" +
           "i.id.skuInterno, p.nombreComercial, p.marca, " +
           "i.id.sucursalId, s.nombre, i.stockActual, i.stockMinimoSucursal) " +
           "FROM InventarioSucursal i " +
           "JOIN ProductoMaestro p ON i.id.skuInterno = p.skuInterno " +
           "JOIN Sucursal s ON i.id.sucursalId = s.id " +
           "WHERE i.id.sucursalId = :sucursalId " +
           "AND i.stockActual < i.stockMinimoSucursal " +
           "AND p.activo = true " +
           "ORDER BY (i.stockMinimoSucursal - i.stockActual) DESC")
    List<com.nexoohub.almacen.inventario.dto.ProductoStockBajoDTO> obtenerProductosStockBajo(@Param("sucursalId") Integer sucursalId);
    
    /**
     * Consulta productos con stock bajo en TODAS las sucursales.
     */
    @Query("SELECT new com.nexoohub.almacen.inventario.dto.ProductoStockBajoDTO(" +
           "i.id.skuInterno, p.nombreComercial, p.marca, " +
           "i.id.sucursalId, s.nombre, i.stockActual, i.stockMinimoSucursal) " +
           "FROM InventarioSucursal i " +
           "JOIN ProductoMaestro p ON i.id.skuInterno = p.skuInterno " +
           "JOIN Sucursal s ON i.id.sucursalId = s.id " +
           "WHERE i.stockActual < i.stockMinimoSucursal " +
           "AND p.activo = true " +
           "ORDER BY (i.stockMinimoSucursal - i.stockActual) DESC")
    List<com.nexoohub.almacen.inventario.dto.ProductoStockBajoDTO> obtenerTodosProductosStockBajo();    
    /**
     * Obtiene productos próximos a caducar (en los próximos N días).
     * 
     * @param fechaLimite fecha límite hasta la cual considerar productos próximos a caducar
     * @return lista de productos próximos a caducar
     */
    @Query("SELECT new com.nexoohub.almacen.inventario.dto.ProductoCaducidadDTO(" +
           "i.id.skuInterno, p.nombreComercial, i.id.sucursalId, s.nombre, " +
           "i.stockActual, i.fechaCaducidad, i.lote) " +
           "FROM InventarioSucursal i " +
           "JOIN ProductoMaestro p ON i.id.skuInterno = p.skuInterno " +
           "JOIN Sucursal s ON i.id.sucursalId = s.id " +
           "WHERE i.fechaCaducidad IS NOT NULL " +
           "AND i.fechaCaducidad <= :fechaLimite " +
           "AND i.fechaCaducidad >= CURRENT_DATE " +
           "AND p.activo = true " +
           "ORDER BY i.fechaCaducidad ASC")
    List<ProductoCaducidadDTO> obtenerProductosProximosCaducar(@Param("fechaLimite") java.time.LocalDate fechaLimite);
    
    /**
     * Obtiene productos ya caducados.
     * 
     * @return lista de productos caducados
     */
    @Query("SELECT new com.nexoohub.almacen.inventario.dto.ProductoCaducidadDTO(" +
           "i.id.skuInterno, p.nombreComercial, i.id.sucursalId, s.nombre, " +
           "i.stockActual, i.fechaCaducidad, i.lote) " +
           "FROM InventarioSucursal i " +
           "JOIN ProductoMaestro p ON i.id.skuInterno = p.skuInterno " +
           "JOIN Sucursal s ON i.id.sucursalId = s.id " +
           "WHERE i.fechaCaducidad IS NOT NULL " +
           "AND i.fechaCaducidad < CURRENT_DATE " +
           "AND p.activo = true " +
           "ORDER BY i.fechaCaducidad DESC")
    List<ProductoCaducidadDTO> obtenerProductosCaducados();}
