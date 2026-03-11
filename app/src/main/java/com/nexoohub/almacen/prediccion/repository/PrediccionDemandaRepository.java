package com.nexoohub.almacen.prediccion.repository;

import com.nexoohub.almacen.prediccion.entity.PrediccionDemanda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository para gestionar predicciones de demanda.
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
@Repository
public interface PrediccionDemandaRepository extends JpaRepository<PrediccionDemanda, Integer> {

    /**
     * Busca predicción para un producto y sucursal en un periodo específico
     */
    Optional<PrediccionDemanda> findBySkuProductoAndSucursalIdAndPeriodoAnioAndPeriodoMes(
        String skuProducto, 
        Integer sucursalId, 
        Integer periodoAnio, 
        Integer periodoMes
    );

    /**
     * Busca todas las predicciones de un producto en una sucursal
     */
    List<PrediccionDemanda> findBySkuProductoAndSucursalIdOrderByPeriodoAnioDescPeriodoMesDesc(
        String skuProducto, 
        Integer sucursalId
    );

    /**
     * Busca todas las predicciones de una sucursal para un periodo
     */
    List<PrediccionDemanda> findBySucursalIdAndPeriodoAnioAndPeriodoMes(
        Integer sucursalId, 
        Integer periodoAnio, 
        Integer periodoMes
    );

    /**
     * Busca predicciones con cantidad a comprar mayor a cero
     */
    @Query("SELECT p FROM PrediccionDemanda p " +
           "WHERE p.sucursalId = :sucursalId " +
           "AND p.periodoAnio = :anio " +
           "AND p.periodoMes = :mes " +
           "AND p.cantidadComprar > 0 " +
           "ORDER BY p.cantidadComprar DESC")
    List<PrediccionDemanda> findProductosParaComprar(
        @Param("sucursalId") Integer sucursalId,
        @Param("anio") Integer anio,
        @Param("mes") Integer mes
    );

    /**
     * Busca predicciones recientes (últimos N días)
     */
    @Query("SELECT p FROM PrediccionDemanda p " +
           "WHERE p.sucursalId = :sucursalId " +
           "AND p.fechaCalculo >= :fechaDesde " +
           "ORDER BY p.fechaCalculo DESC, p.demandaPredicha DESC")
    List<PrediccionDemanda> findPrediccionesRecientes(
        @Param("sucursalId") Integer sucursalId,
        @Param("fechaDesde") LocalDate fechaDesde
    );

    /**
     * Cuenta predicciones para un periodo
     */
    long countBySucursalIdAndPeriodoAnioAndPeriodoMes(
        Integer sucursalId, 
        Integer periodoAnio, 
        Integer periodoMes
    );
}
