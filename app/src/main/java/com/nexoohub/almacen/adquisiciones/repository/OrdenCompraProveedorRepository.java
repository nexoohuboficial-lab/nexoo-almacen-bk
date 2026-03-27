package com.nexoohub.almacen.adquisiciones.repository;

import com.nexoohub.almacen.adquisiciones.entity.OrdenCompraProveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrdenCompraProveedorRepository extends JpaRepository<OrdenCompraProveedor, Integer> {

    Optional<OrdenCompraProveedor> findByFolio(String folio);

    @Query("SELECT o FROM OrdenCompraProveedor o WHERE " +
           "(:proveedorId IS NULL OR o.proveedor.id = :proveedorId) AND " +
           "(:estado IS NULL OR o.estado = :estado) AND " +
           "(:fechaInicio IS NULL OR o.fechaCreacion >= :fechaInicio) " +
           "ORDER BY o.fechaCreacion DESC")
    List<OrdenCompraProveedor> findByFiltros(
            @Param("proveedorId") Integer proveedorId,
            @Param("estado") String estado,
            @Param("fechaInicio") LocalDateTime fechaInicio
    );
    
    @Query("SELECT COUNT(o) FROM OrdenCompraProveedor o WHERE o.folio LIKE CONCAT(:prefijo, '%')")
    long countByFolioPrefix(@Param("prefijo") String prefijo);
}
