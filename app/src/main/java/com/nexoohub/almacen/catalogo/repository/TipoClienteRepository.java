package com.nexoohub.almacen.catalogo.repository;

import com.nexoohub.almacen.catalogo.entity.TipoCliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoClienteRepository extends JpaRepository<TipoCliente, Integer> {
}