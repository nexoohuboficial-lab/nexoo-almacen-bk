package com.nexoohub.almacen.catalogo.mapper;

import com.nexoohub.almacen.catalogo.dto.TipoClienteResponseDTO;
import com.nexoohub.almacen.catalogo.entity.TipoCliente;
import org.springframework.stereotype.Component;

/**
 * Mapper para convertir TipoCliente entity a DTOs.
 */
@Component
public class TipoClienteMapper {

    public TipoClienteResponseDTO toResponseDTO(TipoCliente entity) {
        if (entity == null) {
            return null;
        }
        
        return new TipoClienteResponseDTO(
            entity.getId(),
            entity.getNombre(),
            entity.getDescripcion(),
            entity.getFechaCreacion()
        );
    }
}
