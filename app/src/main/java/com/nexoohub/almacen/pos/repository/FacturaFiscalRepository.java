package com.nexoohub.almacen.pos.repository;

import com.nexoohub.almacen.pos.entity.FacturaFiscal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FacturaFiscalRepository extends JpaRepository<FacturaFiscal, Integer> {

    Optional<FacturaFiscal> findByVentaId(Integer ventaId);
    
    List<FacturaFiscal> findByClienteIdOrderByFechaEmisionDesc(Integer clienteId);
}
