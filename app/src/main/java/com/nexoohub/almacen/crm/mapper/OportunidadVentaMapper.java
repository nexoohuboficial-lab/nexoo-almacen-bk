package com.nexoohub.almacen.crm.mapper;

import com.nexoohub.almacen.crm.dto.OportunidadVentaRequest;
import com.nexoohub.almacen.crm.dto.OportunidadVentaResponse;
import com.nexoohub.almacen.crm.entity.OportunidadVenta;
import org.springframework.stereotype.Component;

@Component
public class OportunidadVentaMapper {

    public OportunidadVenta toEntity(OportunidadVentaRequest request) {
        if (request == null) {
            return null;
        }

        OportunidadVenta entity = new OportunidadVenta();
        entity.setTitulo(request.getTitulo());
        entity.setValorProyectado(request.getValorProyectado());
        entity.setEtapa(request.getEtapa() != null ? request.getEtapa() : "DESCUBRIMIENTO");
        entity.setFechaCierreEstimada(request.getFechaCierreEstimada());
        entity.setProbabilidadPorcentaje(request.getProbabilidadPorcentaje() != null ? request.getProbabilidadPorcentaje() : 10);
        return entity;
    }

    public OportunidadVentaResponse toResponse(OportunidadVenta entity) {
        if (entity == null) {
            return null;
        }

        OportunidadVentaResponse response = new OportunidadVentaResponse();
        response.setId(entity.getId());
        
        if (entity.getProspecto() != null) {
            response.setProspectoId(entity.getProspecto().getId());
        }
        
        response.setTitulo(entity.getTitulo());
        response.setValorProyectado(entity.getValorProyectado());
        response.setEtapa(entity.getEtapa());
        response.setFechaCierreEstimada(entity.getFechaCierreEstimada());
        response.setProbabilidadPorcentaje(entity.getProbabilidadPorcentaje());
        response.setFechaCreacion(entity.getFechaCreacion());
        return response;
    }
}
