package com.nexoohub.almacen.comisiones.repository;

import com.nexoohub.almacen.comisiones.entity.ReglaComisionEscalonada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReglaComisionEscalonadaRepository extends JpaRepository<ReglaComisionEscalonada, Integer> {
    
    // Busca la regla de comisión exacta en donde cae el porcentaje de logro
    @Query("SELECT r FROM ReglaComisionEscalonada r WHERE r.activo = true " +
           "AND r.porcentajeMinimoLogro <= :porcentaje " +
           "AND r.porcentajeMaximoLogro >= :porcentaje " +
           "ORDER BY r.porcentajeComision DESC")
    List<ReglaComisionEscalonada> findReglasAplicables(@Param("porcentaje") BigDecimal porcentaje);
}
