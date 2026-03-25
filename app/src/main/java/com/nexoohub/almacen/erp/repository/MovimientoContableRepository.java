package com.nexoohub.almacen.erp.repository;

import com.nexoohub.almacen.erp.entity.MovimientoContable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MovimientoContableRepository extends JpaRepository<MovimientoContable, Integer> {

    @Query("SELECT m FROM MovimientoContable m JOIN m.poliza p WHERE p.fecha BETWEEN :desde AND :hasta AND p.estatus = 'APLICADA'")
    List<MovimientoContable> findMovimientosPorFecha(@Param("desde") LocalDate desde, @Param("hasta") LocalDate hasta);
}
