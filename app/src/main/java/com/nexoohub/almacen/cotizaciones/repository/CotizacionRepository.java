package com.nexoohub.almacen.cotizaciones.repository;

import com.nexoohub.almacen.cotizaciones.entity.Cotizacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CotizacionRepository extends JpaRepository<Cotizacion, Long> {
    
    /**
     * Carga una cotización con todas sus relaciones en 1 sola query
     * Evita N+1: Sin esto se harían 1 query para cotización + N para detalles + N para productos
     */
    @EntityGraph(attributePaths = {"cliente", "sucursal", "vendedor"})
    Optional<Cotizacion> findWithDetallesById(Long id);
    
    /**
     * Buscar cotización por folio
     */
    Optional<Cotizacion> findByFolio(String folio);
    
    /**
     * Buscar cotización por folio con detalles
     */
    @EntityGraph(attributePaths = {"cliente", "sucursal", "vendedor"})
    Optional<Cotizacion> findWithDetallesByFolio(String folio);
    
    /**
     * Buscar cotizaciones por estado
     */
    List<Cotizacion> findByEstado(String estado);
    
    /**
     * Buscar cotizaciones por estado con paginación
     */
    Page<Cotizacion> findByEstado(String estado, Pageable pageable);
    
    /**
     * Buscar cotizaciones por cliente
     */
    List<Cotizacion> findByClienteId(Integer clienteId);
    
    /**
     * Buscar cotizaciones por cliente con paginación
     */
    Page<Cotizacion> findByClienteId(Integer clienteId, Pageable pageable);
    
    /**
     * Buscar cotizaciones por sucursal
     */
    List<Cotizacion> findBySucursalId(Integer sucursalId);
    
    /**
     * Buscar cotizaciones por sucursal con paginación
     */
    Page<Cotizacion> findBySucursalId(Integer sucursalId, Pageable pageable);
    
    /**
     * Buscar cotizaciones por vendedor
     */
    List<Cotizacion> findByVendedorId(Integer vendedorId);
    
    /**
     * Buscar cotizaciones por vendedor con paginación
     */
    Page<Cotizacion> findByVendedorId(Integer vendedorId, Pageable pageable);
    
    /**
     * Buscar cotizaciones por rango de fechas
     */
    List<Cotizacion> findByFechaCotizacionBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    
    /**
     * Buscar cotizaciones por rango de fechas con paginación
     */
    Page<Cotizacion> findByFechaCotizacionBetween(
        LocalDateTime fechaInicio, 
        LocalDateTime fechaFin, 
        Pageable pageable
    );
    
    /**
     * Buscar cotizaciones vencidas (fecha de validez pasada y no convertidas)
     */
    @Query("SELECT c FROM Cotizacion c " +
           "WHERE c.fechaValidez < :fechaActual " +
           "AND c.estado NOT IN ('CONVERTIDA', 'RECHAZADA', 'VENCIDA') " +
           "ORDER BY c.fechaValidez ASC")
    List<Cotizacion> findVencidas(@Param("fechaActual") LocalDate fechaActual);
    
    /**
     * Buscar cotizaciones próximas a vencer (dentro de los próximos N días y no convertidas)
     */
    @Query("SELECT c FROM Cotizacion c " +
           "WHERE c.fechaValidez BETWEEN :fechaActual AND :fechaLimite " +
           "AND c.estado NOT IN ('CONVERTIDA', 'RECHAZADA', 'VENCIDA') " +
           "ORDER BY c.fechaValidez ASC")
    List<Cotizacion> findProximasAVencer(
        @Param("fechaActual") LocalDate fechaActual,
        @Param("fechaLimite") LocalDate fechaLimite
    );
    
    /**
     * Buscar cotizaciones pendientes de conversión (ENVIADAS o ACEPTADAS, no vencidas, sin venta asociada)
     */
    @Query("SELECT c FROM Cotizacion c " +
           "WHERE c.estado IN ('ENVIADA', 'ACEPTADA') " +
           "AND c.fechaValidez >= :fechaActual " +
           "AND c.ventaId IS NULL " +
           "ORDER BY c.fechaCotizacion DESC")
    List<Cotizacion> findPendientesDeConversion(@Param("fechaActual") LocalDate fechaActual);
    
    /**
     * Buscar cotizaciones convertidas en venta
     */
    @Query("SELECT c FROM Cotizacion c " +
           "WHERE c.estado = 'CONVERTIDA' " +
           "AND c.ventaId IS NOT NULL " +
           "ORDER BY c.fechaConversion DESC")
    List<Cotizacion> findConvertidas();
    
    /**
     * Buscar cotizaciones por cliente y estado
     */
    List<Cotizacion> findByClienteIdAndEstado(Integer clienteId, String estado);
    
    /**
     * Buscar cotizaciones por sucursal y estado
     */
    List<Cotizacion> findBySucursalIdAndEstado(Integer sucursalId, String estado);
    
    /**
     * Buscar cotizaciones por sucursal y rango de fechas
     */
    @Query("SELECT c FROM Cotizacion c " +
           "WHERE c.sucursalId = :sucursalId " +
           "AND c.fechaCotizacion BETWEEN :fechaInicio AND :fechaFin " +
           "ORDER BY c.fechaCotizacion DESC")
    List<Cotizacion> findBySucursalIdAndFechaRango(
        @Param("sucursalId") Integer sucursalId,
        @Param("fechaInicio") LocalDateTime fechaInicio,
        @Param("fechaFin") LocalDateTime fechaFin
    );
    
    /**
     * Contar cotizaciones por estado
     */
    Long countByEstado(String estado);
    
    /**
     * Contar cotizaciones vencidas
     */
    @Query("SELECT COUNT(c) FROM Cotizacion c " +
           "WHERE c.fechaValidez < :fechaActual " +
           "AND c.estado NOT IN ('CONVERTIDA', 'RECHAZADA', 'VENCIDA')")
    Long countVencidas(@Param("fechaActual") LocalDate fechaActual);
    
    /**
     * Contar cotizaciones por sucursal y estado
     */
    Long countBySucursalIdAndEstado(Integer sucursalId, String estado);
    
    /**
     * Calcular valor total de cotizaciones por estado
     */
    @Query("SELECT COALESCE(SUM(c.total), 0) FROM Cotizacion c WHERE c.estado = :estado")
    java.math.BigDecimal calcularTotalPorEstado(@Param("estado") String estado);
    
    /**
     * Calcular valor total de cotizaciones por sucursal y estado
     */
    @Query("SELECT COALESCE(SUM(c.total), 0) FROM Cotizacion c " +
           "WHERE c.sucursalId = :sucursalId AND c.estado = :estado")
    java.math.BigDecimal calcularTotalPorSucursalYEstado(
        @Param("sucursalId") Integer sucursalId,
        @Param("estado") String estado
    );
    
    /**
     * Obtener el último número de folio generado para un año específico
     * Formato esperado: COT-YYYY-NNNN
     */
    @Query("SELECT MAX(c.folio) FROM Cotizacion c WHERE c.folio LIKE :prefijo")
    Optional<String> findUltimoFolioPorAnio(@Param("prefijo") String prefijo);
    
    /**
     * Buscar cotizaciones con filtros múltiples
     */
    @Query("SELECT c FROM Cotizacion c " +
           "WHERE (:clienteId IS NULL OR c.clienteId = :clienteId) " +
           "AND (:sucursalId IS NULL OR c.sucursalId = :sucursalId) " +
           "AND (:vendedorId IS NULL OR c.vendedorId = :vendedorId) " +
           "AND (:estado IS NULL OR c.estado = :estado) " +
           "AND (:fechaInicio IS NULL OR c.fechaCotizacion >= :fechaInicio) " +
           "AND (:fechaFin IS NULL OR c.fechaCotizacion <= :fechaFin) " +
           "ORDER BY c.fechaCotizacion DESC")
    Page<Cotizacion> buscarConFiltros(
        @Param("clienteId") Integer clienteId,
        @Param("sucursalId") Integer sucursalId,
        @Param("vendedorId") Integer vendedorId,
        @Param("estado") String estado,
        @Param("fechaInicio") LocalDateTime fechaInicio,
        @Param("fechaFin") LocalDateTime fechaFin,
        Pageable pageable
    );
    
    /**
     * Obtener estadísticas de cotizaciones por estado
     */
    @Query("SELECT c.estado, COUNT(c), COALESCE(SUM(c.total), 0) " +
           "FROM Cotizacion c " +
           "GROUP BY c.estado")
    List<Object[]> obtenerEstadisticasPorEstado();
    
    /**
     * Obtener estadísticas de cotizaciones por sucursal
     */
    @Query("SELECT c.sucursalId, COUNT(c), COALESCE(SUM(c.total), 0) " +
           "FROM Cotizacion c " +
           "WHERE c.fechaCotizacion BETWEEN :fechaInicio AND :fechaFin " +
           "GROUP BY c.sucursalId")
    List<Object[]> obtenerEstadisticasPorSucursal(
        @Param("fechaInicio") LocalDateTime fechaInicio,
        @Param("fechaFin") LocalDateTime fechaFin
    );
    
    /**
     * Obtener tasa de conversión por sucursal (cotizaciones convertidas vs total)
     */
    @Query("SELECT c.sucursalId, " +
           "COUNT(CASE WHEN c.estado = 'CONVERTIDA' THEN 1 END), " +
           "COUNT(*), " +
           "CAST(COUNT(CASE WHEN c.estado = 'CONVERTIDA' THEN 1 END) * 100.0 / COUNT(*) AS double) " +
           "FROM Cotizacion c " +
           "WHERE c.fechaCotizacion BETWEEN :fechaInicio AND :fechaFin " +
           "GROUP BY c.sucursalId")
    List<Object[]> obtenerTasaConversionPorSucursal(
        @Param("fechaInicio") LocalDateTime fechaInicio,
        @Param("fechaFin") LocalDateTime fechaFin
    );
}
