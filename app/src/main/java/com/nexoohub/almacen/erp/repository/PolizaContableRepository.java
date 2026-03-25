package com.nexoohub.almacen.erp.repository;

import com.nexoohub.almacen.erp.entity.PolizaContable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PolizaContableRepository extends JpaRepository<PolizaContable, Integer> {

    List<PolizaContable> findByFechaBetweenOrderByFechaDesc(LocalDate desde, LocalDate hasta);

    Optional<PolizaContable> findByNumeroPoliza(String numeroPoliza);
}
