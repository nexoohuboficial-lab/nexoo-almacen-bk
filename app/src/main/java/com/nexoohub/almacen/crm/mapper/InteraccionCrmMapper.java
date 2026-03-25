package com.nexoohub.almacen.crm.mapper;

import com.nexoohub.almacen.crm.dto.InteraccionCrmRequest;
import com.nexoohub.almacen.crm.dto.InteraccionCrmResponse;
import com.nexoohub.almacen.crm.entity.InteraccionCrm;
import org.springframework.stereotype.Component;

@Component
public class InteraccionCrmMapper {

    public InteraccionCrm toEntity(InteraccionCrmRequest request) {
        if (request == null) {
            return null;
        }

        InteraccionCrm entity = new InteraccionCrm();
        entity.setTipoInteraccion(request.getTipoInteraccion());
        entity.setResumen(request.getResumen());
        entity.setDetalles(request.getDetalles());
        return entity;
    }

    public InteraccionCrmResponse toResponse(InteraccionCrm entity) {
        if (entity == null) {
            return null;
        }

        InteraccionCrmResponse response = new InteraccionCrmResponse();
        response.setId(entity.getId());
        
        if (entity.getProspecto() != null) {
            response.setProspectoId(entity.getProspecto().getId());
        }
        
        if (entity.getOportunidadVenta() != null) {
            response.setOportunidadId(entity.getOportunidadVenta().getId());
        }
        
        response.setTipoInteraccion(entity.getTipoInteraccion());
        response.setResumen(entity.getResumen());
        response.setDetalles(entity.getDetalles());
        response.setFechaInteraccion(entity.getFechaInteraccion());
        response.setFechaCreacion(entity.getFechaCreacion());
        return response;
    }
}
