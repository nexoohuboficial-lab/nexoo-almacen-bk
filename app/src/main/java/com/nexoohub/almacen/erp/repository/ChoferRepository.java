package com.nexoohub.almacen.erp.repository;

import com.nexoohub.almacen.erp.entity.Chofer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChoferRepository extends JpaRepository<Chofer, Integer> {
    List<Chofer> findBySucursalIdAndEstatus(Integer sucursalId, String estatus);
}
