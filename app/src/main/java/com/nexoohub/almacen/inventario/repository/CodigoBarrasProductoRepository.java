package com.nexoohub.almacen.inventario.repository;

import com.nexoohub.almacen.inventario.entity.CodigoBarrasProducto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CodigoBarrasProductoRepository extends JpaRepository<CodigoBarrasProducto, Integer> {

    Optional<CodigoBarrasProducto> findByCodigoAndActivoTrue(String codigo);

    List<CodigoBarrasProducto> findBySkuInternoAndActivoTrue(String skuInterno);

    boolean existsByCodigo(String codigo);
}
