package com.nexoohub.almacen.crm.repository;

import com.nexoohub.almacen.crm.entity.InteraccionCrm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface InteraccionCrmRepository extends JpaRepository<InteraccionCrm, Integer> {
    List<InteraccionCrm> findByProspectoId(Integer prospectoId);
    List<InteraccionCrm> findByOportunidadId(Integer oportunidadId);
    List<InteraccionCrm> findByProspectoIdOrderByFechaInteraccionDesc(Integer prospectoId);
}
