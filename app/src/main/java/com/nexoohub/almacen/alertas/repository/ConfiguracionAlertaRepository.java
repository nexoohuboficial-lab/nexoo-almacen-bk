package com.nexoohub.almacen.alertas.repository;

import com.nexoohub.almacen.alertas.entity.ConfiguracionAlerta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfiguracionAlertaRepository extends JpaRepository<ConfiguracionAlerta, Integer> {

    Optional<ConfiguracionAlerta> findBySucursalIdAndActivoTrue(Integer sucursalId);
}
