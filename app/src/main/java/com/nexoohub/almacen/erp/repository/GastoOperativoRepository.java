package com.nexoohub.almacen.erp.repository;

import com.nexoohub.almacen.erp.entity.GastoOperativo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface GastoOperativoRepository extends JpaRepository<GastoOperativo, Integer> {

    List<GastoOperativo> findByFechaGastoBetweenOrderByFechaGastoDesc(LocalDate desde, LocalDate hasta);

    List<GastoOperativo> findBySucursalIdAndFechaGastoBetweenOrderByFechaGastoDesc(
            Integer sucursalId, LocalDate desde, LocalDate hasta);
}
