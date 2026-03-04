package com.nexoohub.almacen.inventario.repository;

import com.nexoohub.almacen.inventario.dto.ProductoResumenDTO;
import com.nexoohub.almacen.inventario.entity.ProductoMaestro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductoMaestroRepository extends JpaRepository<ProductoMaestro, String>, JpaSpecificationExecutor<ProductoMaestro> {
    // Al agregar JpaSpecificationExecutor, Spring Boot le da superpoderes a este repositorio
    // para recibir consultas dinámicas construidas al vuelo.
    @Query("SELECT new com.nexoohub.almacen.inventario.dto.ProductoResumenDTO(" +
           "p.skuInterno, p.nombreComercial, c.nombre, " +
           "(SELECT h.precioFinalPublico FROM HistorialPrecio h WHERE h.skuInterno = p.skuInterno ORDER BY h.fechaCalculo DESC LIMIT 1), " +
           "COALESCE(i.stockActual, 0), p.sensibilidadPrecio) " +
           "FROM ProductoMaestro p " +
           "JOIN Categoria c ON p.categoriaId = c.id " +
           "LEFT JOIN InventarioSucursal i ON i.id.skuInterno = p.skuInterno AND i.id.sucursalId = :sucursalId " +
           "WHERE LOWER(p.nombreComercial) LIKE LOWER(CONCAT('%', :termino, '%')) " +
           "OR LOWER(p.skuInterno) LIKE LOWER(CONCAT('%', :termino, '%'))")
    List<ProductoResumenDTO> buscarParaMostrador(@Param("termino") String termino, @Param("sucursalId") Integer sucursalId);
}