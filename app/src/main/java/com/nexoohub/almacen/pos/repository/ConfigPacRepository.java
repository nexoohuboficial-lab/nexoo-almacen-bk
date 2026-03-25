package com.nexoohub.almacen.pos.repository;

import com.nexoohub.almacen.pos.entity.ConfigPac;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfigPacRepository extends JpaRepository<ConfigPac, Integer> {
    
    @Query("SELECT c FROM ConfigPac c WHERE c.isActivo = true")
    Optional<ConfigPac> findActiveConfiguration();
}
