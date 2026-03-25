package com.nexoohub.almacen.erp.repository;

import com.nexoohub.almacen.erp.entity.RutaEntrega;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RutaEntregaRepository extends JpaRepository<RutaEntrega, Integer> {
    List<RutaEntrega> findByFechaProgramadaBetweenOrderByFechaProgramadaDesc(LocalDate desde, LocalDate hasta);
    Optional<RutaEntrega> findByCodigoRuta(String codigoRuta);
}
