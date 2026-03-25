package com.nexoohub.almacen.crm.mapper;

import com.nexoohub.almacen.crm.dto.CampanaMarketingRequest;
import com.nexoohub.almacen.crm.dto.CampanaMarketingResponse;
import com.nexoohub.almacen.crm.entity.CampanaMarketing;
import org.springframework.stereotype.Component;

@Component
public class CampanaMarketingMapper {

    public CampanaMarketing toEntity(CampanaMarketingRequest request) {
        if (request == null) {
            return null;
        }

        CampanaMarketing entidad = new CampanaMarketing();
        entidad.setNombre(request.getNombre());
        entidad.setSegmentoObjetivo(request.getSegmentoObjetivo());
        entidad.setCanal(request.getCanal());
        entidad.setContenidoPlantilla(request.getContenidoPlantilla());
        entidad.setFechaProgramada(request.getFechaProgramada());
        entidad.setCreadoPorUsuarioId(request.getCreadoPorUsuarioId());

        return entidad;
    }

    public CampanaMarketingResponse toResponse(CampanaMarketing entity) {
        if (entity == null) {
            return null;
        }

        CampanaMarketingResponse response = new CampanaMarketingResponse();
        response.setId(entity.getId());
        response.setNombre(entity.getNombre());
        response.setSegmentoObjetivo(entity.getSegmentoObjetivo());
        response.setCanal(entity.getCanal());
        response.setEstado(entity.getEstado());
        response.setContenidoPlantilla(entity.getContenidoPlantilla());
        response.setFechaProgramada(entity.getFechaProgramada());
        response.setFechaEjecucion(entity.getFechaEjecucion());
        response.setTotalDestinatarios(entity.getTotalDestinatarios());
        response.setCreadoPorUsuarioId(entity.getCreadoPorUsuarioId());
        // Map created date if any, but auditing fields usually come from superclass
        // response.setFechaCreacion(entity.getFechaCreacion());
        
        return response;
    }
}
