package com.nexoohub.almacen.common.repository;

import com.nexoohub.almacen.common.entity.Permiso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface PermisoRepository extends JpaRepository<Permiso, Integer> {
    Optional<Permiso> findByNombre(String nombre);
    
    // Para buscar un batch de permisos y sumarlos al rol
    Set<Permiso> findByNombreIn(List<String> nombres);
}
