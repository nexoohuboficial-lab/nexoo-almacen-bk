package com.nexoohub.almacen.adquisiciones.repository;

import com.nexoohub.almacen.adquisiciones.entity.HistorialPrecioProveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistorialPrecioProveedorRepository extends JpaRepository<HistorialPrecioProveedor, Long> {

    List<HistorialPrecioProveedor> findByCatalogoIdOrderByFechaActualizacionDesc(Long catalogoId);

}
