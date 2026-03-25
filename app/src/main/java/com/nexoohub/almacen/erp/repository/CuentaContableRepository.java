package com.nexoohub.almacen.erp.repository;

import com.nexoohub.almacen.erp.entity.CuentaContable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CuentaContableRepository extends JpaRepository<CuentaContable, Integer> {

    List<CuentaContable> findByActivaTrueOrderByCodigoAsc();

    boolean existsByCodigo(String codigo);
}
