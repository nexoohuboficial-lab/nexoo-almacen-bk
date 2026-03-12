package com.nexoohub.almacen.comisiones.repository;

import com.nexoohub.almacen.comisiones.entity.Comision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la gestión de comisiones
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
@Repository
public interface ComisionRepository extends JpaRepository<Comision, Integer> {

    /**
     * Busca una comisión específica de un vendedor en un periodo
     */
    Optional<Comision> findByVendedorIdAndPeriodoAnioAndPeriodoMes(
            Integer vendedorId, Integer anio, Integer mes);

    /**
     * Busca todas las comisiones de un vendedor
     */
    List<Comision> findByVendedorIdOrderByPeriodoAnioDescPeriodoMesDesc(Integer vendedorId);

    /**
     * Busca comisiones por periodo
     */
    List<Comision> findByPeriodoAnioAndPeriodoMesOrderByVendedorId(Integer anio, Integer mes);

    /**
     * Busca comisiones por estado
     */
    List<Comision> findByEstadoOrderByPeriodoAnioDescPeriodoMesDesc(String estado);

    /**
     * Busca comisiones pendientes de un vendedor
     */
    @Query("SELECT c FROM Comision c WHERE c.vendedorId = :vendedorId AND c.estado = 'PENDIENTE' " +
           "ORDER BY c.periodoAnio DESC, c.periodoMes DESC")
    List<Comision> findPendientesByVendedor(@Param("vendedorId") Integer vendedorId);

    /**
     * Calcula el total de comisiones por estado en un periodo
     */
    @Query("SELECT COALESCE(SUM(c.totalComision), 0) FROM Comision c " +
           "WHERE c.periodoAnio = :anio AND c.periodoMes = :mes AND c.estado = :estado")
    BigDecimal calcularTotalPorEstadoYPeriodo(
            @Param("anio") Integer anio, 
            @Param("mes") Integer mes, 
            @Param("estado") String estado);

    /**
     * Cuenta comisiones por estado en un periodo
     */
    @Query("SELECT COUNT(c) FROM Comision c " +
           "WHERE c.periodoAnio = :anio AND c.periodoMes = :mes AND c.estado = :estado")
    Long contarPorEstadoYPeriodo(
            @Param("anio") Integer anio, 
            @Param("mes") Integer mes, 
            @Param("estado") String estado);

    /**
     * Obtiene el total de ventas por vendedor en un periodo
     */
    @Query("SELECT COALESCE(SUM(c.totalVentas), 0) FROM Comision c " +
           "WHERE c.periodoAnio = :anio AND c.periodoMes = :mes")
    BigDecimal calcularTotalVentasPorPeriodo(
            @Param("anio") Integer anio, 
            @Param("mes") Integer mes);

    /**
     * Verifica si ya existe una comisión para un vendedor en un periodo
     */
    boolean existsByVendedorIdAndPeriodoAnioAndPeriodoMes(
            Integer vendedorId, Integer anio, Integer mes);

    // ==================== MÉTODOS PARA MÉTRICAS FINANCIERAS ====================

    /**
     * Calcula el total de comisiones (gastos operativos) en un período de fechas.
     * Utilizado para métricas financieras.
     * 
     * NOTA: Las comisiones se almacenan por mes completo (año/mes).
     * Este método suma todas las comisiones cuyo período (año/mes) esté dentro 
     * del rango de fechas especificado.
     * 
     * @param anioInicio Año de inicio del período
     * @param mesInicio Mes de inicio del período (1-12)
     * @param anioFin Año de fin del período
     * @param mesFin Mes de fin del período (1-12)
     * @return Suma total de comisiones en el período
     */
    @Query("SELECT COALESCE(SUM(c.totalComision), 0) FROM Comision c " +
           "WHERE (c.periodoAnio * 12 + c.periodoMes) BETWEEN " +
           "(:anioInicio * 12 + :mesInicio) AND (:anioFin * 12 + :mesFin)")
    BigDecimal calcularComisionesTotalesPeriodo(
        @Param("anioInicio") Integer anioInicio,
        @Param("mesInicio") Integer mesInicio,
        @Param("anioFin") Integer anioFin,
        @Param("mesFin") Integer mesFin
    );
}
