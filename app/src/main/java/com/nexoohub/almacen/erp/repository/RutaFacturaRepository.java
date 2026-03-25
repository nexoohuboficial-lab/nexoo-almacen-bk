package com.nexoohub.almacen.erp.repository;

import com.nexoohub.almacen.erp.entity.RutaFactura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RutaFacturaRepository extends JpaRepository<RutaFactura, Integer> {
    Optional<RutaFactura> findByNumeroGuia(String numeroGuia);
}
