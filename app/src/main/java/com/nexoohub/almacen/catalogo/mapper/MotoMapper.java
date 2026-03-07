package com.nexoohub.almacen.catalogo.mapper;

import com.nexoohub.almacen.catalogo.dto.MotoResponseDTO;
import com.nexoohub.almacen.catalogo.entity.Moto;
import org.springframework.stereotype.Component;

/**
 * Mapper para convertir Moto entity a DTOs.
 */
@Component
public class MotoMapper {

    public MotoResponseDTO toResponseDTO(Moto entity) {
        if (entity == null) {
            return null;
        }
        
        return new MotoResponseDTO(
            entity.getId(),
            entity.getMarca(),
            entity.getModelo(),
            entity.getCilindrada(),
            entity.getFechaCreacion(),
            entity.getFechaActualizacion()
        );
    }
}
