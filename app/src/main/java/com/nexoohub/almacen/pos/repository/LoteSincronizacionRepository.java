package com.nexoohub.almacen.pos.repository;

import com.nexoohub.almacen.pos.entity.LoteSincronizacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoteSincronizacionRepository extends JpaRepository<LoteSincronizacion, Integer> {

    Optional<LoteSincronizacion> findByCodigoLote(String codigoLote);

    List<LoteSincronizacion> findByEstatus(String estatus);
}
