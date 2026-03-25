package com.nexoohub.almacen.erp.repository;

import com.nexoohub.almacen.erp.entity.Vehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehiculoRepository extends JpaRepository<Vehiculo, Integer> {
    List<Vehiculo> findBySucursalIdAndEstatus(Integer sucursalId, String estatus);
}
