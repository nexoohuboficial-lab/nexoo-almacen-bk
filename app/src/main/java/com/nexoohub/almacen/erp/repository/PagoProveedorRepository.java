package com.nexoohub.almacen.erp.repository;

import com.nexoohub.almacen.erp.entity.PagoProveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PagoProveedorRepository extends JpaRepository<PagoProveedor, Integer> {

    List<PagoProveedor> findByCuentaPorPagarId(Integer cuentaPorPagarId);
}
