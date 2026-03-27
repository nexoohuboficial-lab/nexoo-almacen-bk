package com.nexoohub.almacen.analitica.repository;

import com.nexoohub.almacen.analitica.entity.ReglaAsociacionProductos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReglaAsociacionProductosRepository extends JpaRepository<ReglaAsociacionProductos, Long> {
    
    // Retorna los productos más propensos a ser comprados junto con el origen (ordenado por Confianza y Lift)
    List<ReglaAsociacionProductos> findTop5BySkuOrigenOrderByConfianzaDescLiftDesc(String skuOrigen);
}
