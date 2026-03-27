package com.nexoohub.almacen.crm.mapper;

import com.nexoohub.almacen.crm.dto.EncuestaNpsResponse;
import com.nexoohub.almacen.crm.entity.EncuestaNps;
import org.springframework.stereotype.Component;

@Component
public class EncuestaNpsMapper {

    public EncuestaNpsResponse toResponse(EncuestaNps entity) {
        if (entity == null) {
            return null;
        }

        EncuestaNpsResponse response = new EncuestaNpsResponse();
        response.setId(entity.getId());
        response.setVentaId(entity.getVentaId());
        response.setClienteId(entity.getClienteId());
        response.setEnlaceUnico(entity.getEnlaceUnico());
        response.setEstado(entity.getEstado());
        response.setFechaEnvio(entity.getFechaEnvio());
        response.setFechaExpiracion(entity.getFechaExpiracion());

        return response;
    }
}
