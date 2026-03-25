package com.nexoohub.almacen.erp.repository;

import com.nexoohub.almacen.erp.entity.DevolucionProveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DevolucionProveedorRepository extends JpaRepository<DevolucionProveedor, Integer> {
    List<DevolucionProveedor> findBySucursalIdAndFechaBetweenOrderByFechaDesc(Integer sucursalId, LocalDate inicio, LocalDate fin);
}
