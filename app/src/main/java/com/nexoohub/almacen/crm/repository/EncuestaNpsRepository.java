package com.nexoohub.almacen.crm.repository;

import com.nexoohub.almacen.crm.entity.EncuestaNps;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EncuestaNpsRepository extends JpaRepository<EncuestaNps, Integer> {
    Optional<EncuestaNps> findByEnlaceUnico(String enlaceUnico);
    boolean existsByVentaId(Integer ventaId);
}
