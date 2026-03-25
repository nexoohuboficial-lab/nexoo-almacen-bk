package com.nexoohub.almacen.crm.repository;

import com.nexoohub.almacen.crm.entity.Prospecto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProspectoRepository extends JpaRepository<Prospecto, Integer> {
    boolean existsByRfc(String rfc);
}
