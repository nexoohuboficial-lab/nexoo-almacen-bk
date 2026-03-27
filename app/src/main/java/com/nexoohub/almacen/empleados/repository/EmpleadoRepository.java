package com.nexoohub.almacen.empleados.repository;

import com.nexoohub.almacen.empleados.entity.Empleado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, Integer> {
    
    // Útil para ver la plantilla laboral de una tienda en específico
    List<Empleado> findBySucursalIdAndActivoTrue(Integer sucursalId);
    
    // Versión con paginación
    Page<Empleado> findBySucursalIdAndActivoTrue(Integer sucursalId, Pageable pageable);

    // ANA-04: obtener todos los empleados activos (sin filtro de sucursal)
    List<Empleado> findByActivoTrue();
}