package com.nexoohub.almacen.fidelidad.repository;

import com.nexoohub.almacen.fidelidad.entity.ProgramaFidelidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para gestión de programas de fidelidad.
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
@Repository
public interface ProgramaFidelidadRepository extends JpaRepository<ProgramaFidelidad, Integer> {

    /**
     * Busca el programa de fidelidad de un cliente específico.
     * 
     * @param clienteId ID del cliente
     * @return programa de fidelidad si existe
     */
    Optional<ProgramaFidelidad> findByClienteId(Integer clienteId);

    /**
     * Obtiene el total de puntos acumulados en el sistema.
     * 
     * @return total de puntos en todos los programas
     */
    @Query("SELECT COALESCE(SUM(p.puntosAcumulados), 0) FROM ProgramaFidelidad p WHERE p.activo = true")
    Long obtenerTotalPuntosEnSistema();

    /**
     * Cuenta cuántos clientes tienen programas activos.
     * 
     * @return cantidad de programas activos
     */
    @Query("SELECT COUNT(p) FROM ProgramaFidelidad p WHERE p.activo = true")
    Long contarProgramasActivos();

    /**
     * Verifica si un cliente ya tiene programa de fidelidad.
     * 
     * @param clienteId ID del cliente
     * @return true si existe programa
     */
    boolean existsByClienteId(Integer clienteId);
}
