package com.nexoohub.almacen.comisiones.repository;

import com.nexoohub.almacen.comisiones.entity.ReglaComision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la gestión de reglas de comisión
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
@Repository
public interface ReglaComisionRepository extends JpaRepository<ReglaComision, Integer> {

    /**
     * Busca reglas activas ordenadas por prioridad
     */
    @Query("SELECT r FROM ReglaComision r WHERE r.activa = true ORDER BY r.prioridad ASC")
    List<ReglaComision> findAllActivas();

    /**
     * Busca reglas por puesto (incluyendo las generales que aplican a todos)
     */
    @Query("SELECT r FROM ReglaComision r WHERE r.activa = true AND " +
           "(r.puesto = :puesto OR r.puesto IS NULL) " +
           "ORDER BY r.prioridad ASC")
    List<ReglaComision> findByPuesto(@Param("puesto") String puesto);

    /**
     * Busca reglas por tipo
     */
    List<ReglaComision> findByTipoAndActivaTrue(String tipo);

    /**
     * Busca reglas por producto
     */
    Optional<ReglaComision> findBySkuProductoAndActivaTrue(String skuProducto);

    /**
     * Verifica si existe una regla con el mismo nombre
     */
    boolean existsByNombreAndIdNot(String nombre, Integer id);

    /**
     * Busca todos los puestos distintos con reglas configuradas
     */
    @Query("SELECT DISTINCT r.puesto FROM ReglaComision r WHERE r.puesto IS NOT NULL")
    List<String> findDistinctPuestos();
}
