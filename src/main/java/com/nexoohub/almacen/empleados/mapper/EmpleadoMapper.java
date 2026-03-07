package com.nexoohub.almacen.empleados.mapper;

import com.nexoohub.almacen.empleados.dto.EmpleadoResponseDTO;
import com.nexoohub.almacen.empleados.entity.Empleado;
import com.nexoohub.almacen.sucursal.repository.SucursalRepository;
import org.springframework.stereotype.Component;

/**
 * Mapper para convertir Empleado entity a DTOs.
 * 
 * <p>Denormaliza información de sucursal para mejorar UX del frontend.</p>
 */
@Component
public class EmpleadoMapper {

    private final SucursalRepository sucursalRepository;

    public EmpleadoMapper(SucursalRepository sucursalRepository) {
        this.sucursalRepository = sucursalRepository;
    }

    /**
     * Convierte una entidad Empleado a DTO de respuesta.
     * 
     * @param entity Empleado a convertir
     * @return DTO con sucursal denormalizada
     */
    public EmpleadoResponseDTO toResponseDTO(Empleado entity) {
        if (entity == null) {
            return null;
        }
        
        String sucursalNombre = null;
        if (entity.getSucursalId() != null) {
            sucursalNombre = sucursalRepository.findById(entity.getSucursalId())
                .map(s -> s.getNombre())
                .orElse(null);
        }
        
        return new EmpleadoResponseDTO(
            entity.getId(),
            entity.getNombre(),
            entity.getApellidos(),
            entity.getPuesto(),
            entity.getSucursalId(),
            sucursalNombre,
            entity.getFechaContratacion(),
            entity.getActivo(),
            entity.getFechaCreacion()
        );
    }
}
