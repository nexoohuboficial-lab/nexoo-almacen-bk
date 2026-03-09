package com.nexoohub.almacen.finanzas.repository;

import com.nexoohub.almacen.finanzas.entity.LimiteCredito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repository para gestión de límites de crédito.
 * 
 * @author NexooHub Development Team
 * @since 1.2.0
 */
@Repository
public interface LimiteCreditoRepository extends JpaRepository<LimiteCredito, Integer> {

    /**
     * Busca el límite de crédito de un cliente específico.
     * 
     * @param clienteId ID del cliente
     * @return Límite de crédito si existe
     */
    @Query("SELECT lc FROM LimiteCredito lc WHERE lc.cliente.id = :clienteId")
    Optional<LimiteCredito> findByClienteId(@Param("clienteId") Integer clienteId);

    /**
     * Lista clientes por estado de crédito.
     * 
     * @param estado Estado del crédito (ACTIVO, BLOQUEADO, SUSPENDIDO, INACTIVO)
     * @return Lista de límites de crédito
     */
    @Query("SELECT lc FROM LimiteCredito lc WHERE lc.estado = :estado ORDER BY lc.cliente.nombre")
    List<LimiteCredito> findByEstado(@Param("estado") String estado);

    /**
     * Lista clientes con crédito bloqueado.
     * 
     * @return Lista de límites de crédito bloqueados
     */
    default List<LimiteCredito> findBloqueados() {
        return findByEstado("BLOQUEADO");
    }

    /**
     * Lista clientes con crédito activo.
     * 
     * @return Lista de límites de crédito activos
     */
    default List<LimiteCredito> findActivos() {
        return findByEstado("ACTIVO");
    }

    /**
     * Lista clientes que excedieron un porcentaje de su límite.
     * Útil para alertas preventivas.
     * 
     * @param porcentaje Porcentaje mínimo de utilización (ej: 80 para 80%)
     * @return Lista de clientes con alta utilización
     */
    @Query("SELECT lc FROM LimiteCredito lc " +
           "WHERE lc.estado = 'ACTIVO' " +
           "AND (lc.saldoUtilizado * 100 / lc.limiteAutorizado) >= :porcentaje " +
           "ORDER BY (lc.saldoUtilizado * 100 / lc.limiteAutorizado) DESC")
    List<LimiteCredito> findByUtilizacionMayorA(@Param("porcentaje") BigDecimal porcentaje);

    /**
     * Lista clientes con crédito próximo a excederse (>= 80% de uso).
     * 
     * @return Lista de clientes en riesgo
     */
    default List<LimiteCredito> findProximosAExceder() {
        return findByUtilizacionMayorA(BigDecimal.valueOf(80));
    }

    /**
     * Lista clientes con saldo utilizado mayor al límite autorizado.
     * Indica clientes que están en sobregiro no autorizado.
     * 
     * @return Lista de clientes en sobregiro
     */
    @Query("SELECT lc FROM LimiteCredito lc " +
           "WHERE lc.saldoUtilizado > lc.limiteAutorizado " +
           "ORDER BY (lc.saldoUtilizado - lc.limiteAutorizado) DESC")
    List<LimiteCredito> findEnSobregiro();

    /**
     * Lista clientes con sobregiro autorizado y activo.
     * 
     * @return Lista de clientes con sobregiro permitido
     */
    @Query("SELECT lc FROM LimiteCredito lc " +
           "WHERE lc.permiteSobregiro = true " +
           "AND lc.estado = 'ACTIVO' " +
           "ORDER BY lc.cliente.nombre")
    List<LimiteCredito> findConSobregiroAutorizado();

    /**
     * Cuenta clientes por estado de crédito.
     * 
     * @param estado Estado a contar
     * @return Número de clientes en ese estado
     */
    @Query("SELECT COUNT(lc) FROM LimiteCredito lc WHERE lc.estado = :estado")
    Long countByEstado(@Param("estado") String estado);

    /**
     * Calcula el total de crédito autorizado en el sistema.
     * 
     * @return Suma de todos los límites autorizados
     */
    @Query("SELECT COALESCE(SUM(lc.limiteAutorizado), 0) FROM LimiteCredito lc WHERE lc.estado = 'ACTIVO'")
    BigDecimal sumLimiteAutorizadoActivos();

    /**
     * Calcula el total de saldo utilizado en el sistema.
     * 
     * @return Suma de todos los saldos utilizados
     */
    @Query("SELECT COALESCE(SUM(lc.saldoUtilizado), 0) FROM LimiteCredito lc WHERE lc.estado = 'ACTIVO'")
    BigDecimal sumSaldoUtilizadoActivos();

    /**
     * Busca clientes con límite menor o igual a un monto.
     * 
     * @param monto Monto máximo del límite
     * @return Lista de clientes
     */
    @Query("SELECT lc FROM LimiteCredito lc " +
           "WHERE lc.limiteAutorizado <= :monto " +
           "ORDER BY lc.limiteAutorizado DESC")
    List<LimiteCredito> findByLimiteAutorizadoMenorIgualA(@Param("monto") BigDecimal monto);

    /**
     * Busca clientes que requieren revisión (sin revisión desde una fecha específica).
     * 
     * @param fechaLimite Fecha límite para considerar que requiere revisión
     * @return Lista de clientes que requieren revisión
     */
    @Query("SELECT lc FROM LimiteCredito lc " +
           "WHERE lc.fechaRevision < :fechaLimite " +
           "OR lc.fechaRevision IS NULL " +
           "ORDER BY lc.fechaRevision ASC NULLS FIRST")
    List<LimiteCredito> findRequierenRevision(@Param("fechaLimite") java.time.LocalDate fechaLimite);
}
