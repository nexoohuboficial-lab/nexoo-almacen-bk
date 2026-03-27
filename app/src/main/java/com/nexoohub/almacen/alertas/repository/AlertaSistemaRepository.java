package com.nexoohub.almacen.alertas.repository;

import com.nexoohub.almacen.alertas.entity.AlertaSistema;
import com.nexoohub.almacen.alertas.entity.TipoAlerta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertaSistemaRepository extends JpaRepository<AlertaSistema, Integer> {

    /** Alertas no leídas de un usuario específico */
    List<AlertaSistema> findByUsuarioDestinoIdAndLeidaFalse(Integer usuarioDestinoId);

    /** Alertas no resueltas de una sucursal */
    List<AlertaSistema> findBySucursalIdAndResueltaFalse(Integer sucursalId);

    /** Cantidad de alertas no leídas de un usuario */
    long countByUsuarioDestinoIdAndLeidaFalse(Integer usuarioDestinoId);

    /** ¿Ya existe una alerta activa del mismo tipo para la misma sucursal hoy? */
    boolean existsBySucursalIdAndTipoAndResueltaFalse(Integer sucursalId, TipoAlerta tipo);
}
