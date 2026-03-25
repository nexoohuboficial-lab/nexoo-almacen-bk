package com.nexoohub.almacen.caja.repository;

import com.nexoohub.almacen.caja.entity.TurnoCaja;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TurnoCajaRepository extends JpaRepository<TurnoCaja, Integer> {

    /** Busca el turno actualmente ABIERTO para un empleado en una sucursal. */
    Optional<TurnoCaja> findByEmpleadoIdAndSucursalIdAndEstado(
        Integer empleadoId, Integer sucursalId, String estado);

    /** Verifica si existe algún turno ABIERTO en una sucursal. */
    boolean existsBySucursalIdAndEstado(Integer sucursalId, String estado);
}
