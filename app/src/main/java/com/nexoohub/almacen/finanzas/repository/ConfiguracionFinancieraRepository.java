package com.nexoohub.almacen.finanzas.repository;

import com.nexoohub.almacen.finanzas.entity.ConfiguracionFinanciera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfiguracionFinancieraRepository extends JpaRepository<ConfiguracionFinanciera, Integer> {}