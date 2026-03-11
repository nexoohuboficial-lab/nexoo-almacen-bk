package com.nexoohub.almacen.rentabilidad.repository;

import com.nexoohub.almacen.rentabilidad.entity.RentabilidadProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para análisis de rentabilidad agregada por producto.
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
@Repository
public interface RentabilidadProductoRepository extends JpaRepository<RentabilidadProducto, Long> {

    /**
     * Busca análisis de un producto en un período específico.
     * 
     * @param skuInterno SKU del producto
     * @param periodoInicio Fecha de inicio del período
     * @param periodoFin Fecha de fin del período
     * @return Análisis si existe
     */
    Optional<RentabilidadProducto> findBySkuInternoAndPeriodoInicioAndPeriodoFin(
        String skuInterno, 
        LocalDate periodoInicio, 
        LocalDate periodoFin
    );

    /**
     * Obtiene todos los análisis de un producto.
     * 
     * @param skuInterno SKU del producto
     * @return Lista de análisis históricos
     */
    List<RentabilidadProducto> findBySkuInternoOrderByPeriodoFinDesc(String skuInterno);

    /**
     * Obtiene los N productos MÁS rentables en un período.
     * 
     * @param periodoInicio Fecha de inicio
     * @param periodoFin Fecha de fin
     * @param limite Número de resultados
     * @return Lista de productos más rentables
     */
    @Query("SELECT rp FROM RentabilidadProducto rp " +
           "WHERE rp.periodoInicio = :periodoInicio AND rp.periodoFin = :periodoFin " +
           "ORDER BY rp.utilidadTotalGenerada DESC")
    List<RentabilidadProducto> obtenerProductosMasRentables(
        @Param("periodoInicio") LocalDate periodoInicio,
        @Param("periodoFin") LocalDate periodoFin,
        @Param("limite") int limite
    );

    /**
     * Obtiene los N productos MENOS rentables en un período.
     * 
     * @param periodoInicio Fecha de inicio
     * @param periodoFin Fecha de fin
     * @param limite Número de resultados
     * @return Lista de productos menos rentables
     */
    @Query("SELECT rp FROM RentabilidadProducto rp " +
           "WHERE rp.periodoInicio = :periodoInicio AND rp.periodoFin = :periodoFin " +
           "ORDER BY rp.utilidadTotalGenerada ASC")
    List<RentabilidadProducto> obtenerProductosMenosRentables(
        @Param("periodoInicio") LocalDate periodoInicio,
        @Param("periodoFin") LocalDate periodoFin,
        @Param("limite") int limite
    );

    /**
     * Obtiene productos con margen promedio menor a un umbral.
     * 
     * @param periodoInicio Fecha de inicio
     * @param periodoFin Fecha de fin
     * @param margenMinimo Margen mínimo aceptable
     * @return Productos con margen bajo
     */
    @Query("SELECT rp FROM RentabilidadProducto rp " +
           "WHERE rp.periodoInicio = :periodoInicio AND rp.periodoFin = :periodoFin " +
           "AND rp.margenPromedioPorcentaje < :margenMinimo " +
           "ORDER BY rp.margenPromedioPorcentaje ASC")
    List<RentabilidadProducto> obtenerProductosConMargenBajo(
        @Param("periodoInicio") LocalDate periodoInicio,
        @Param("periodoFin") LocalDate periodoFin,
        @Param("margenMinimo") java.math.BigDecimal margenMinimo
    );

    /**
     * Obtiene todos los análisis de un período ordenados por utilidad.
     * 
     * @param periodoInicio Fecha de inicio
     * @param periodoFin Fecha de fin
     * @return Lista de análisis del período
     */
    List<RentabilidadProducto> findByPeriodoInicioAndPeriodoFinOrderByUtilidadTotalGeneradaDesc(
        LocalDate periodoInicio, 
        LocalDate periodoFin
    );

    /**
     * Elimina análisis antiguos de un período específico.
     * Útil para regenerar el análisis.
     * 
     * @param periodoInicio Fecha de inicio
     * @param periodoFin Fecha de fin
     */
    void deleteByPeriodoInicioAndPeriodoFin(LocalDate periodoInicio, LocalDate periodoFin);
}
