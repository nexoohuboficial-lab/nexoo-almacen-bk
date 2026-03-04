package com.nexoohub.almacen.finanzas.repository;

import com.nexoohub.almacen.finanzas.entity.HistorialPrecio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistorialPrecioRepository extends JpaRepository<HistorialPrecio, Integer> {

    java.util.Optional<HistorialPrecio> findTopBySkuInternoOrderByFechaCalculoDesc(String skuInterno);
}
