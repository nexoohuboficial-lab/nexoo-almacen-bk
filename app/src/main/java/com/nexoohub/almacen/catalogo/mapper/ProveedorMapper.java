package com.nexoohub.almacen.catalogo.mapper;

import com.nexoohub.almacen.catalogo.dto.ProveedorResponseDTO;
import com.nexoohub.almacen.catalogo.entity.Proveedor;
import org.springframework.stereotype.Component;

/**
 * Mapper para convertir Proveedor entity a DTOs.
 */
@Component
public class ProveedorMapper {

    public ProveedorResponseDTO toResponseDTO(Proveedor entity) {
        if (entity == null) {
            return null;
        }
        
        return new ProveedorResponseDTO(
            entity.getId(),
            entity.getNombreEmpresa(),
            entity.getRfc(),
            entity.getNombreContacto(),
            entity.getTelefono(),
            entity.getEmail(),
            entity.getDireccion(),
            entity.getFechaCreacion(),
            entity.getFechaActualizacion()
        );
    }
}
