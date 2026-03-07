package com.nexoohub.almacen.catalogo.mapper;

import com.nexoohub.almacen.catalogo.dto.CategoriaResponseDTO;
import com.nexoohub.almacen.catalogo.entity.Categoria;
import org.springframework.stereotype.Component;

/**
 * Mapper para convertir Categoria entity a DTOs.
 */
@Component
public class CategoriaMapper {

    public CategoriaResponseDTO toResponseDTO(Categoria entity) {
        if (entity == null) {
            return null;
        }
        
        return new CategoriaResponseDTO(
            entity.getId(),
            entity.getNombre(),
            entity.getDescripcion(),
            entity.getFechaCreacion(),
            entity.getFechaActualizacion()
        );
    }
}
