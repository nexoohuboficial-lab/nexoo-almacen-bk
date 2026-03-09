package com.nexoohub.almacen.inventario.repository;

import com.nexoohub.almacen.inventario.entity.AlertaLentoMovimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la gestión de alertas de productos de lento movimiento.
 * 
 * <p>Incluye queries especializadas para:
 * <ul>
 *   <li>Detectar productos sin ventas en X días</li>
 *   <li>Calcular costos de inventario inmovilizado</li>
 *   <li>Filtrar alertas por criticidad</li>
 *   <li>Generar reportes por sucursales</li>
 * </ul>
 * 
 * @author NexooHub Development Team
 * @since 1.2.0
 */
@Repository
public interface AlertaLentoMovimientoRepository extends JpaRepository<AlertaLentoMovimiento, Integer> {
    
    /**
     * Busca alertas activas (no resueltas) para una sucursal específica.
     * Carga las relaciones producto y sucursal en una sola query.
     * 
     * @param sucursalId ID de la sucursal
     * @return Lista de alertas activas
     */
    @Query("SELECT DISTINCT a FROM AlertaLentoMovimiento a " +
           "LEFT JOIN FETCH a.producto " +
           "LEFT JOIN FETCH a.sucursal " +
           "WHERE a.sucursalId = :sucursalId AND a.resuelto = false " +
           "ORDER BY a.diasSinVenta DESC, a.costoInmovilizado DESC")
    List<AlertaLentoMovimiento> findBySucursalIdAndResueltoFalse(@Param("sucursalId") Integer sucursalId);
    
    /**
     * Busca todas las alertas activas (no resueltas) del sistema.
     * Útil para dashboard ejecutivo.
     * 
     * @return Lista de todas las alertas activas
     */
    @Query("SELECT DISTINCT a FROM AlertaLentoMovimiento a " +
           "LEFT JOIN FETCH a.producto " +
           "LEFT JOIN FETCH a.sucursal " +
           "WHERE a.resuelto = false " +
           "ORDER BY a.estadoAlerta DESC, a.costoInmovilizado DESC")
    List<AlertaLentoMovimiento> findAllActiveWithDetails();
    
    /**
     * Busca alertas críticas (más de 60 días sin venta) activas.
     * 
     * @return Lista de alertas en estado CRITICO
     */
    @Query("SELECT DISTINCT a FROM AlertaLentoMovimiento a " +
           "LEFT JOIN FETCH a.producto " +
           "LEFT JOIN FETCH a.sucursal " +
           "WHERE a.estadoAlerta = 'CRITICO' AND a.resuelto = false " +
           "ORDER BY a.diasSinVenta DESC")
    List<AlertaLentoMovimiento> findCriticasActivas();
    
    /**
     * Busca alertas activas para un producto específico en todas las sucursales.
     * 
     * @param skuInterno SKU del producto
     * @return Lista de alertas del producto
     */
    @Query("SELECT DISTINCT a FROM AlertaLentoMovimiento a " +
           "LEFT JOIN FETCH a.sucursal " +
           "WHERE a.skuInterno = :skuInterno AND a.resuelto = false " +
           "ORDER BY a.diasSinVenta DESC")
    List<AlertaLentoMovimiento> findBySkuInternoAndResueltoFalse(@Param("skuInterno") String skuInterno);
    
    /**
     * Busca si ya existe una alerta activa para un producto en una sucursal.
     * Evita duplicados.
     * 
     * @param skuInterno SKU del producto
     * @param sucursalId ID de la sucursal
     * @return Optional con la alerta si existe
     */
    Optional<AlertaLentoMovimiento> findBySkuInternoAndSucursalIdAndResueltoFalse(
        String skuInterno, Integer sucursalId
    );
    
    /**
     * Calcula el costo total de inventario inmovilizado en alertas activas.
     * 
     * @return Suma del costo inmovilizado en todas las alertas activas
     */
    @Query("SELECT COALESCE(SUM(a.costoInmovilizado), 0.0) FROM AlertaLentoMovimiento a " +
           "WHERE a.resuelto = false")
    BigDecimal calcularCostoTotalInmovilizado();
    
    /**
     * Calcula el costo total inmovilizado por sucursal.
     * 
     * @param sucursalId ID de la sucursal
     * @return Suma del costo inmovilizado en la sucursal
     */
    @Query("SELECT COALESCE(SUM(a.costoInmovilizado), 0.0) FROM AlertaLentoMovimiento a " +
           "WHERE a.sucursalId = :sucursalId AND a.resuelto = false")
    BigDecimal calcularCostoInmovilizadoPorSucursal(@Param("sucursalId") Integer sucursalId);
    
    /**
     * Cuenta las alertas activas por nivel de criticidad.
     * 
     * @param estadoAlerta Estado: ADVERTENCIA, CRITICO
     * @return Número de alertas en ese estado
     */
    @Query("SELECT COUNT(a) FROM AlertaLentoMovimiento a " +
           "WHERE a.estadoAlerta = :estadoAlerta AND a.resuelto = false")
    Long countByEstadoAlertaAndResueltoFalse(@Param("estadoAlerta") String estadoAlerta);
    
    /**
     * Busca alertas resueltas en un rango de fechas.
     * Útil para análisis histórico de acciones tomadas.
     * 
     * @param inicio Fecha inicio del periodo
     * @param fin Fecha fin del periodo
     * @return Lista de alertas resueltas en el periodo
     */
    @Query("SELECT a FROM AlertaLentoMovimiento a " +
           "WHERE a.resuelto = true " +
           "AND a.fechaResolucion BETWEEN :inicio AND :fin " +
           "ORDER BY a.fechaResolucion DESC")
    List<AlertaLentoMovimiento> findResueltasEnPeriodo(
        @Param("inicio") LocalDate inicio,
        @Param("fin") LocalDate fin
    );
    
    /**
     * Identifica productos que no han tenido ventas en los últimos N días.
     * Query crítico para generar nuevas alertas.
     * 
     * @param diasSinVenta Umbral de días sin venta
     * @param fechaCorte Fecha desde la cual calcular (LocalDate.now() - diasSinVenta)
     * @return Lista de productos candidatos a alerta
     */
    @Query(value = 
        "SELECT " +
        "    inv.sku_interno, " +
        "    inv.sucursal_id, " +
        "    inv.stock_actual, " +
        "    inv.costo_promedio_ponderado, " +
        "    MAX(v.fecha_venta) as ultima_venta, " +
        " CASE " +
        "        WHEN MAX(v.fecha_venta) IS NULL THEN 999 " +
        "        ELSE DATEDIFF('DAY', MAX(v.fecha_venta), CURRENT_DATE) " +
        "    END as dias_sin_venta " +
        "FROM inventario_sucursal inv " +
        "LEFT JOIN detalle_venta dv ON inv.sku_interno = dv.sku_interno " +
        "LEFT JOIN venta v ON dv.venta_id = v.id " +
        "WHERE inv.stock_actual > 0 " +
        "GROUP BY inv.sku_interno, inv.sucursal_id, inv.stock_actual, inv.costo_promedio_ponderado " +
        "HAVING (MAX(v.fecha_venta) IS NULL OR MAX(v.fecha_venta) < :fechaCorte) " +
        "ORDER BY dias_sin_venta DESC, inv.costo_promedio_ponderado * inv.stock_actual DESC",
        nativeQuery = true
    )
    List<Object[]> detectarProductosSinVentas(
        @Param("fechaCorte") LocalDate fechaCorte
    );
    
    /**
     * Elimina alertas resueltas más antiguas que X días.
     * Útil para limpieza periódica del sistema (ejecutar con @Scheduled).
     * 
     * @param fechaLimite Fecha límite (ej. LocalDate.now().minusDays(365))
     */
    @Query("DELETE FROM AlertaLentoMovimiento a " +
           "WHERE a.resuelto = true AND a.fechaResolucion < :fechaLimite")
    void deleteResueltasAntesde(@Param("fechaLimite") LocalDate fechaLimite);
}
