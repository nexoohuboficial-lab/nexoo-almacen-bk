package com.nexoohub.almacen.crm.repository;

import com.nexoohub.almacen.crm.entity.TicketGarantia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketGarantiaRepository extends JpaRepository<TicketGarantia, Integer> {
    List<TicketGarantia> findByClienteIdOrderByFechaCreacionDesc(Integer clienteId);
    List<TicketGarantia> findByVentaIdOrderByFechaCreacionDesc(Integer ventaId);
    List<TicketGarantia> findByEstadoOrderByFechaCreacionDesc(String estado);
}
