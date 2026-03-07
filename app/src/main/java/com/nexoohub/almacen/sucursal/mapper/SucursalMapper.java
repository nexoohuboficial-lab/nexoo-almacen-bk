package com.nexoohub.almacen.sucursal.mapper;

import com.nexoohub.almacen.sucursal.dto.SucursalResponseDTO;
import com.nexoohub.almacen.sucursal.entity.Sucursal;
import org.springframework.stereotype.Component;

/**
 * Mapper para convertir Sucursal entity a DTOs.
 */
@Component
public class SucursalMapper {

    /**
     * Convierte una entidad Sucursal a DTO de respuesta.
     * 
     * @param entity Sucursal a convertir
     * @return DTO sin campos de auditoría sensibles
     */
    public SucursalResponseDTO toResponseDTO(Sucursal entity) {
        if (entity == null) {
            return null;
        }
        
        return new SucursalResponseDTO(
            entity.getId(),
            entity.getNombre(),
            entity.getDireccion(),
            entity.getActivo(),
            entity.getFechaCreacion(),
            entity.getFechaActualizacion()
        );
    }
}
