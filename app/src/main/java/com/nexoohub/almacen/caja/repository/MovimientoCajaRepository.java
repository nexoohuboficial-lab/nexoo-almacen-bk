package com.nexoohub.almacen.caja.repository;

import com.nexoohub.almacen.caja.entity.MovimientoCaja;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovimientoCajaRepository extends JpaRepository<MovimientoCaja, Integer> {

    /** Lista todos los movimientos de un turno ordenados por fecha. */
    List<MovimientoCaja> findByTurnoIdOrderByFechaMovimientoAsc(Integer turnoId);
}
