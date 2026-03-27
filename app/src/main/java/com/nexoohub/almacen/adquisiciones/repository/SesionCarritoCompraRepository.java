package com.nexoohub.almacen.adquisiciones.repository;

import com.nexoohub.almacen.adquisiciones.entity.SesionCarritoCompra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SesionCarritoCompraRepository extends JpaRepository<SesionCarritoCompra, Integer> {
    
    List<SesionCarritoCompra> findByUsuarioId(Integer usuarioId);
    
    Optional<SesionCarritoCompra> findByUsuarioIdAndCatalogoId(Integer usuarioId, Integer catalogoId);
    
    void deleteByUsuarioId(Integer usuarioId);
}
