package com.nexoohub.almacen.catalogo.repository;

import com.nexoohub.almacen.catalogo.entity.PrecioEspecial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PrecioEspecialRepository extends JpaRepository<PrecioEspecial, Integer> {
    
    // Este método es el que usará el VentaService para saber a cómo cobrarle
    Optional<PrecioEspecial> findBySkuInternoAndTipoClienteId(String skuInterno, Integer tipoClienteId);
}