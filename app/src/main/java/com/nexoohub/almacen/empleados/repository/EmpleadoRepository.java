package com.nexoohub.almacen.empleados.repository;

import com.nexoohub.almacen.empleados.entity.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, Integer> {
    
    // Útil para ver la plantilla laboral de una tienda en específico
    List<Empleado> findBySucursalIdAndActivoTrue(Integer sucursalId);
}