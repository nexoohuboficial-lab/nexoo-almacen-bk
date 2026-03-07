package com.nexoohub.almacen.catalogo.repository;

import com.nexoohub.almacen.catalogo.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {
}
