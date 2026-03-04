package com.nexoohub.almacen.sucursal.repository;

import com.nexoohub.almacen.sucursal.entity.Sucursal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SucursalRepository extends JpaRepository<Sucursal, Integer> {
    // Spring Boot crea la consulta SQL automáticamente para buscar solo las activas
    List<Sucursal> findByActivoTrue();
}
