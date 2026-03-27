package com.nexoohub.almacen.erp.repository;

import com.nexoohub.almacen.empleados.entity.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Empleado del módulo ERP (tabla: empleado).
 * Renombrado desde EmpleadoRepository para evitar colisión con
 * com.nexoohub.almacen.empleados.repository.EmpleadoRepository.
 */
@Repository
public interface ErpEmpleadoRepository extends JpaRepository<Empleado, Integer> {
    List<Empleado> findBySucursalIdAndEstatus(Integer sucursalId, String estatus);
    Optional<Empleado> findByRfc(String rfc);
}
