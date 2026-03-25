package com.nexoohub.almacen.pos.repository;

import com.nexoohub.almacen.pos.entity.LogTransaccionBancaria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LogTransaccionBancariaRepository extends JpaRepository<LogTransaccionBancaria, Integer> {

    Optional<LogTransaccionBancaria> findByReferenciaVenta(String referenciaVenta);
}
