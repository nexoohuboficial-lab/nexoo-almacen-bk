package com.nexoohub.almacen.erp.repository;

import com.nexoohub.almacen.erp.entity.NominaPeriodo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface NominaPeriodoRepository extends JpaRepository<NominaPeriodo, Integer> {
    List<NominaPeriodo> findByFechaInicioGreaterThanEqualAndFechaFinLessThanEqualOrderByFechaInicioDesc(LocalDate desde, LocalDate hasta);
}
