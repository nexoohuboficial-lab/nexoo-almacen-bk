package com.nexoohub.almacen.erp.repository;

import com.nexoohub.almacen.erp.entity.CuentaPorPagar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CuentaPorPagarRepository extends JpaRepository<CuentaPorPagar, Integer> {

    List<CuentaPorPagar> findByEstatusOrderByFechaVencimientoAsc(String estatus);

    List<CuentaPorPagar> findByProveedorId(Integer proveedorId);
}
