package com.nexoohub.almacen.alertas.repository;

import com.nexoohub.almacen.alertas.entity.ConfigNotificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfigNotificacionRepository extends JpaRepository<ConfigNotificacion, Integer> {

    Optional<ConfigNotificacion> findByUsuarioIdAndActivoTrue(Integer usuarioId);
}
