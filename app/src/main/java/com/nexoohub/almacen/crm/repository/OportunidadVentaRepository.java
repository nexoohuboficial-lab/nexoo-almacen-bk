package com.nexoohub.almacen.crm.repository;

import com.nexoohub.almacen.crm.entity.OportunidadVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OportunidadVentaRepository extends JpaRepository<OportunidadVenta, Integer> {
    List<OportunidadVenta> findByProspectoId(Integer prospectoId);
}
