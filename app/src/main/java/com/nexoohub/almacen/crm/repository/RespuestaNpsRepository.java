package com.nexoohub.almacen.crm.repository;

import com.nexoohub.almacen.crm.entity.RespuestaNps;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface RespuestaNpsRepository extends JpaRepository<RespuestaNps, Integer> {

    @Query("SELECT COUNT(r) FROM RespuestaNps r WHERE r.clasificacion = :clasificacion")
    long countByClasificacion(@Param("clasificacion") String clasificacion);

    @Query("SELECT COUNT(r) FROM RespuestaNps r WHERE r.fechaRespuesta BETWEEN :inicio AND :fin AND r.clasificacion = :clasificacion")
    long countByClasificacionAndDateRange(@Param("clasificacion") String clasificacion, @Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);
    
    @Query("SELECT COUNT(r) FROM RespuestaNps r")
    long countTotalRespuestas();
}
