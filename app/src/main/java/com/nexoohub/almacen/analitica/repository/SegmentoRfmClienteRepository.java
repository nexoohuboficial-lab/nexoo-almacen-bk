package com.nexoohub.almacen.analitica.repository;

import com.nexoohub.almacen.analitica.entity.SegmentoRfmCliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SegmentoRfmClienteRepository extends JpaRepository<SegmentoRfmCliente, Integer> {

    Optional<SegmentoRfmCliente> findByClienteId(Integer clienteId);

    @Query("SELECT r.segmento as segmento, COUNT(r) as cantidad FROM SegmentoRfmCliente r GROUP BY r.segmento")
    List<SegmentoPoblacion> countBySegmento();

    interface SegmentoPoblacion {
        String getSegmento();
        Long getCantidad();
    }
}
