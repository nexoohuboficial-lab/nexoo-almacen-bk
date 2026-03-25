package com.nexoohub.almacen.erp.repository;

import com.nexoohub.almacen.erp.entity.ReciboNomina;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReciboNominaRepository extends JpaRepository<ReciboNomina, Integer> {
    Optional<ReciboNomina> findByPeriodoIdAndEmpleadoId(Integer periodoId, Integer empleadoId);
}
