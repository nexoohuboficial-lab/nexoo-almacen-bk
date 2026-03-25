package com.nexoohub.almacen.crm.mapper;

import com.nexoohub.almacen.crm.dto.HistorialGarantiaDTO;
import com.nexoohub.almacen.crm.dto.TicketGarantiaResponse;
import com.nexoohub.almacen.crm.entity.HistorialGarantia;
import com.nexoohub.almacen.crm.entity.TicketGarantia;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TicketGarantiaMapper {

    public TicketGarantiaResponse toDto(TicketGarantia entity) {
        if (entity == null) {
            return null;
        }

        TicketGarantiaResponse dto = new TicketGarantiaResponse();
        dto.setId(entity.getId());
        dto.setVentaId(entity.getVentaId());
        dto.setClienteId(entity.getClienteId());
        dto.setSkuProducto(entity.getSkuProducto());
        dto.setNumeroSerie(entity.getNumeroSerie());
        dto.setMotivoReclamo(entity.getMotivoReclamo());
        dto.setEstado(entity.getEstado());
        dto.setResolucion(entity.getResolucion());
        dto.setNotasInternas(entity.getNotasInternas());
        dto.setFechaCreacion(entity.getFechaCreacion());
        dto.setFechaActualizacion(entity.getFechaActualizacion());

        // Mapear historial
        if (entity.getHistorial() != null) {
            dto.setHistorial(entity.getHistorial().stream()
                    .map(this::toHistorialDto)
                    .collect(Collectors.toList()));
        } else {
            dto.setHistorial(Collections.emptyList());
        }

        return dto;
    }

    public HistorialGarantiaDTO toHistorialDto(HistorialGarantia entity) {
        if (entity == null) {
            return null;
        }

        HistorialGarantiaDTO dto = new HistorialGarantiaDTO();
        dto.setId(entity.getId());
        dto.setEstadoAnterior(entity.getEstadoAnterior());
        dto.setEstadoNuevo(entity.getEstadoNuevo());
        dto.setComentario(entity.getComentario());
        dto.setUsuarioId(entity.getUsuarioId());
        dto.setFechaCreacion(entity.getFechaCreacion());
        
        return dto;
    }

    public List<TicketGarantiaResponse> toDtoList(List<TicketGarantia> entities) {
        if (entities == null) {
            return Collections.emptyList();
        }
        return entities.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
