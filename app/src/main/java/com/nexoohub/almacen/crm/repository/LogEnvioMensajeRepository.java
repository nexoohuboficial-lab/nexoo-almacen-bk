package com.nexoohub.almacen.crm.repository;

import com.nexoohub.almacen.crm.entity.LogEnvioMensaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LogEnvioMensajeRepository extends JpaRepository<LogEnvioMensaje, Integer> {
    
    @Query("SELECT COUNT(l) FROM LogEnvioMensaje l WHERE l.campana.id = :campanaId AND l.estadoEnvio = :estadoEnvio")
    long countByCampanaIdAndEstadoEnvio(@Param("campanaId") Integer campanaId, @Param("estadoEnvio") String estadoEnvio);

}
