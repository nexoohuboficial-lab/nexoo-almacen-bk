package com.nexoohub.almacen.crm.mapper;

import com.nexoohub.almacen.crm.dto.ProspectoRequest;
import com.nexoohub.almacen.crm.dto.ProspectoResponse;
import com.nexoohub.almacen.crm.entity.Prospecto;
import org.springframework.stereotype.Component;

@Component
public class ProspectoMapper {

    public Prospecto toEntity(ProspectoRequest request) {
        if (request == null) {
            return null;
        }

        Prospecto entity = new Prospecto();
        entity.setEmpresa(request.getEmpresa());
        entity.setRfc(request.getRfc());
        entity.setContactoPrincipal(request.getContactoPrincipal());
        entity.setCorreo(request.getCorreo());
        entity.setTelefono(request.getTelefono());
        entity.setNotas(request.getNotas());
        return entity;
    }

    public ProspectoResponse toResponse(Prospecto entity) {
        if (entity == null) {
            return null;
        }

        ProspectoResponse response = new ProspectoResponse();
        response.setId(entity.getId());
        response.setEmpresa(entity.getEmpresa());
        response.setRfc(entity.getRfc());
        response.setContactoPrincipal(entity.getContactoPrincipal());
        response.setCorreo(entity.getCorreo());
        response.setTelefono(entity.getTelefono());
        response.setEstatusViabilidad(entity.getEstatusViabilidad());
        response.setNotas(entity.getNotas());
        response.setFechaCreacion(entity.getFechaCreacion());
        return response;
    }
}
