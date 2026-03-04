package com.nexoohub.almacen.catalogo.repository;

import com.nexoohub.almacen.catalogo.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
    // Aquí podrías agregar búsquedas por RFC o Teléfono si lo necesitas después
}