package com.nexoohub.almacen.analitica.repository;

import com.nexoohub.almacen.analitica.entity.ReporteRendimientoEmpleado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReporteRendimientoEmpleadoRepository extends JpaRepository<ReporteRendimientoEmpleado, Integer> {

    /**
     * Busca el snapshot de un empleado en un mes/año específico.
     * Útil para decidir si se hace INSERT o UPDATE.
     */
    Optional<ReporteRendimientoEmpleado> findByEmpleadoIdAndMesAndAnio(Integer empleadoId, Integer mes, Integer anio);

    /**
     * Dashboard general de un periodo: todos los empleados ordenados por monto total descendente.
     */
    List<ReporteRendimientoEmpleado> findByAnioAndMesOrderByMontoTotalVentasDesc(Integer anio, Integer mes);

    /**
     * Tendencia histórica de un empleado: últimos N snapshots por mes.
     */
    List<ReporteRendimientoEmpleado> findByEmpleadoIdOrderByAnioDescMesDesc(Integer empleadoId);
}
