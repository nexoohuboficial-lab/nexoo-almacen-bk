package com.nexoohub.almacen.catalogo.repository;

import com.nexoohub.almacen.catalogo.entity.CompatibilidadProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompatibilidadRepository extends JpaRepository<CompatibilidadProducto, Integer> {
    
    // Para saber a qué motos le queda una pieza
    List<CompatibilidadProducto> findBySkuInterno(String skuInterno);
    
    // Para saber qué piezas le quedan a una moto
    List<CompatibilidadProducto> findByMotoId(Integer motoId);
}
