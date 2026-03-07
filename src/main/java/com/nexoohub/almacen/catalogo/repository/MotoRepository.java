package com.nexoohub.almacen.catalogo.repository;

import com.nexoohub.almacen.catalogo.entity.Moto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MotoRepository extends JpaRepository<Moto, Integer> {
}