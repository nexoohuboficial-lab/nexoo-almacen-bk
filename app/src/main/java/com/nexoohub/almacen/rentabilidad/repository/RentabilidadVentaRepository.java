package com.nexoohub.almacen.rentabilidad.repository;

import com.nexoohub.almacen.rentabilidad.entity.RentabilidadVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para análisis de rentabilidad de ventas.
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
@Repository
public interface RentabilidadVentaRepository extends JpaRepository<RentabilidadVenta, Long> {

    /**
     * Busca análisis de rentabilidad por ID de venta.
     * 
     * @param ventaId ID de la venta
     * @return Análisis de rentabilidad si existe
     */
    Optional<RentabilidadVenta> findByVentaId(Integer ventaId);

    /**
     * Obtiene todas las ventas con pérdida (vendidas bajo costo).
     * 
     * @return Lista de ventas con pérdida
     */
    List<RentabilidadVenta> findByVentaBajoCostoTrue();

    /**
     * Obtiene ventas cuyo margen es menor a un porcentaje dado.
     * 
     * @param margenMinimo Margen mínimo aceptable
     * @return Ventas con margen bajo
     */
    @Query("SELECT rv FROM RentabilidadVenta rv WHERE rv.margenPorcentaje < :margenMinimo ORDER BY rv.margenPorcentaje ASC")
    List<RentabilidadVenta> obtenerVentasConMargenMenorA(@Param("margenMinimo") BigDecimal margenMinimo);

    /**
     * Obtiene el total de utilidad generada en un rango de fechas.
     * 
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @return Suma total de utilidades
     */
    @Query("SELECT COALESCE(SUM(rv.utilidadBruta), 0) FROM RentabilidadVenta rv " +
           "JOIN rv.venta v WHERE v.fechaVenta BETWEEN :fechaInicio AND :fechaFin")
    BigDecimal calcularUtilidadTotalPeriodo(
        @Param("fechaInicio") LocalDateTime fechaInicio, 
        @Param("fechaFin") LocalDateTime fechaFin
    );

    /**
     * Obtiene el margen promedio de todas las ventas en un período.
     * 
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @return Margen promedio porcentual
     */
    @Query("SELECT COALESCE(AVG(rv.margenPorcentaje), 0) FROM RentabilidadVenta rv " +
           "JOIN rv.venta v WHERE v.fechaVenta BETWEEN :fechaInicio AND :fechaFin")
    BigDecimal calcularMargenPromedioPeriodo(
        @Param("fechaInicio") LocalDateTime fechaInicio, 
        @Param("fechaFin") LocalDateTime fechaFin
    );

    /**
     * Cuenta cuántas ventas se realizaron bajo costo en un período.
     * 
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @return Número de ventas con pérdida
     */
    @Query("SELECT COUNT(rv) FROM RentabilidadVenta rv " +
           "JOIN rv.venta v WHERE rv.ventaBajoCosto = true " +
           "AND v.fechaVenta BETWEEN :fechaInicio AND :fechaFin")
    Long contarVentasBajoCostoPeriodo(
        @Param("fechaInicio") LocalDateTime fechaInicio, 
        @Param("fechaFin") LocalDateTime fechaFin
    );

    /**
     * Obtiene las N ventas más rentables.
     * 
     * @param limite Número máximo de resultados
     * @return Lista de ventas más rentables
     */
    @Query("SELECT rv FROM RentabilidadVenta rv ORDER BY rv.utilidadBruta DESC")
    List<RentabilidadVenta> obtenerVentasMasRentables(@Param("limite") int limite);

    /**
     * Obtiene las N ventas menos rentables (incluye pérdidas).
     * 
     * @param limite Número máximo de resultados
     * @return Lista de ventas menos rentables
     */
    @Query("SELECT rv FROM RentabilidadVenta rv ORDER BY rv.utilidadBruta ASC")
    List<RentabilidadVenta> obtenerVentasMenosRentables(@Param("limite") int limite);
}
