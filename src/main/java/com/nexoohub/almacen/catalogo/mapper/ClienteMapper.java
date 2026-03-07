package com.nexoohub.almacen.catalogo.mapper;

import com.nexoohub.almacen.catalogo.dto.ClienteResponseDTO;
import com.nexoohub.almacen.catalogo.entity.Cliente;
import com.nexoohub.almacen.catalogo.repository.TipoClienteRepository;
import org.springframework.stereotype.Component;

/**
 * Mapper para convertir Cliente entity a DTOs.
 * 
 * <p>Denormaliza el tipo de cliente para mejorar experiencia del frontend.</p>
 */
@Component
public class ClienteMapper {

    private final TipoClienteRepository tipoClienteRepository;

    public ClienteMapper(TipoClienteRepository tipoClienteRepository) {
        this.tipoClienteRepository = tipoClienteRepository;
    }

    /**
     * Convierte una entidad Cliente a DTO de respuesta.
     * 
     * @param entity Cliente a convertir
     * @return DTO con tipo de cliente denormalizado
     */
    public ClienteResponseDTO toResponseDTO(Cliente entity) {
        if (entity == null) {
            return null;
        }
        
        String tipoClienteNombre = null;
        if (entity.getTipoClienteId() != null) {
            tipoClienteNombre = tipoClienteRepository.findById(entity.getTipoClienteId())
                .map(tc -> tc.getNombre())
                .orElse(null);
        }
        
        return new ClienteResponseDTO(
            entity.getId(),
            entity.getTipoClienteId(),
            tipoClienteNombre,
            entity.getNombre(),
            entity.getRfc(),
            entity.getTelefono(),
            entity.getEmail(),
            entity.getDireccionFiscal(),
            entity.getFechaCreacion(),
            entity.getFechaActualizacion()
        );
    }
}
