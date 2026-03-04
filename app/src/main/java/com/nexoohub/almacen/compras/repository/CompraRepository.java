package com.nexoohub.almacen.compras.repository;

import com.nexoohub.almacen.compras.entity.Compra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompraRepository extends JpaRepository<Compra, Integer> {}
