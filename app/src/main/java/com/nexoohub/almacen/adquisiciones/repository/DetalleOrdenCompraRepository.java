package com.nexoohub.almacen.adquisiciones.repository;

import com.nexoohub.almacen.adquisiciones.entity.DetalleOrdenCompra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetalleOrdenCompraRepository extends JpaRepository<DetalleOrdenCompra, Integer> {
}
