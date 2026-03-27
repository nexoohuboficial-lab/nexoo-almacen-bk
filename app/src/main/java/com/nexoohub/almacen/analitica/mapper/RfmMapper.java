package com.nexoohub.almacen.analitica.mapper;

import com.nexoohub.almacen.analitica.dto.RfmClienteResponse;
import com.nexoohub.almacen.analitica.entity.SegmentoRfmCliente;
import org.springframework.stereotype.Component;

@Component
public class RfmMapper {

    public RfmClienteResponse toResponse(SegmentoRfmCliente entity) {
        if (entity == null) {
            return null;
        }

        RfmClienteResponse response = new RfmClienteResponse();
        response.setClienteId(entity.getClienteId());
        if (entity.getCliente() != null) {
            response.setNombreCliente(entity.getCliente().getNombre());
        }
        
        response.setRecenciaDias(entity.getRecenciaDias());
        response.setFrecuenciaCompras(entity.getFrecuenciaCompras());
        response.setMontoGastado(entity.getMontoGastado());
        response.setScoreR(entity.getScoreR());
        response.setScoreF(entity.getScoreF());
        response.setScoreM(entity.getScoreM());
        response.setSegmento(entity.getSegmento());
        response.setFechaCalculo(entity.getFechaCalculo());

        return response;
    }
}
