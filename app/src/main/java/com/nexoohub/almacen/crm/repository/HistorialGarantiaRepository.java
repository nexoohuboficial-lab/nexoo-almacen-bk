package com.nexoohub.almacen.crm.repository;

import com.nexoohub.almacen.crm.entity.HistorialGarantia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistorialGarantiaRepository extends JpaRepository<HistorialGarantia, Integer> {
    List<HistorialGarantia> findByTicketIdOrderByFechaCreacionAsc(Integer ticketId);
}
