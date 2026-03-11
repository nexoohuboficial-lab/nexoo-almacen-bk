package com.nexoohub.almacen.comisiones.mapper;

import com.nexoohub.almacen.comisiones.dto.ReglaComisionRequestDTO;
import com.nexoohub.almacen.comisiones.dto.ReglaComisionResponseDTO;
import com.nexoohub.almacen.comisiones.entity.ReglaComision;
import org.springframework.stereotype.Component;

/**
 * Mapper para convertir entre entidades y DTOs de ReglaComision
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
@Component
public class ReglaComisionMapper {

    public ReglaComision toEntity(ReglaComisionRequestDTO dto) {
        ReglaComision entity = new ReglaComision();
        entity.setNombre(dto.getNombre());
        entity.setDescripcion(dto.getDescripcion());
        entity.setTipo(dto.getTipo());
        entity.setPuesto(dto.getPuesto());
        entity.setPorcentajeComision(dto.getPorcentajeComision());
        entity.setMontoFijo(dto.getMontoFijo());
        entity.setMetaMensual(dto.getMetaMensual());
        entity.setBonoMeta(dto.getBonoMeta());
        entity.setSkuProducto(dto.getSkuProducto());
        entity.setActiva(dto.getActiva());
        entity.setPrioridad(dto.getPrioridad());
        return entity;
    }

    public ReglaComisionResponseDTO toDTO(ReglaComision entity) {
        ReglaComisionResponseDTO dto = new ReglaComisionResponseDTO();
        dto.setId(entity.getId());
        dto.setNombre(entity.getNombre());
        dto.setDescripcion(entity.getDescripcion());
        dto.setTipo(entity.getTipo());
        dto.setPuesto(entity.getPuesto());
        dto.setPorcentajeComision(entity.getPorcentajeComision());
        dto.setMontoFijo(entity.getMontoFijo());
        dto.setMetaMensual(entity.getMetaMensual());
        dto.setBonoMeta(entity.getBonoMeta());
        dto.setSkuProducto(entity.getSkuProducto());
        dto.setActiva(entity.getActiva());
        dto.setPrioridad(entity.getPrioridad());
        dto.setFechaCreacion(entity.getFechaCreacion());
        dto.setUsuarioCreacion(entity.getUsuarioCreacion());
        return dto;
    }

    public void updateEntity(ReglaComision entity, ReglaComisionRequestDTO dto) {
        entity.setNombre(dto.getNombre());
        entity.setDescripcion(dto.getDescripcion());
        entity.setTipo(dto.getTipo());
        entity.setPuesto(dto.getPuesto());
        entity.setPorcentajeComision(dto.getPorcentajeComision());
        entity.setMontoFijo(dto.getMontoFijo());
        entity.setMetaMensual(dto.getMetaMensual());
        entity.setBonoMeta(dto.getBonoMeta());
        entity.setSkuProducto(dto.getSkuProducto());
        entity.setActiva(dto.getActiva());
        entity.setPrioridad(dto.getPrioridad());
    }
}
