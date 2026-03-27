package com.nexoohub.almacen.analitica.repository;

import com.nexoohub.almacen.analitica.entity.PrediccionChurnCliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PrediccionChurnClienteRepository extends JpaRepository<PrediccionChurnCliente, Integer> {

    Optional<PrediccionChurnCliente> findByClienteId(Integer clienteId);

    List<PrediccionChurnCliente> findByScoreRiesgoGreaterThanEqualOrderByScoreRiesgoDesc(Integer score);
}
