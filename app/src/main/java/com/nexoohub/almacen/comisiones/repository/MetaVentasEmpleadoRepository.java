package com.nexoohub.almacen.comisiones.repository;

import com.nexoohub.almacen.comisiones.entity.MetaVentasEmpleado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MetaVentasEmpleadoRepository extends JpaRepository<MetaVentasEmpleado, Integer> {
    Optional<MetaVentasEmpleado> findByEmpleadoIdAndMesAndAnioAndActivoTrue(Integer empleadoId, Integer mes, Integer anio);
}
