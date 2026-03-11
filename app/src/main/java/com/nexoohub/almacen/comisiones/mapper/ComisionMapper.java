package com.nexoohub.almacen.comisiones.mapper;

import com.nexoohub.almacen.comisiones.dto.ComisionResponseDTO;
import com.nexoohub.almacen.comisiones.entity.Comision;
import org.springframework.stereotype.Component;

import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;

/**
 * Mapper para convertir entre entidades y DTOs de Comision
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
@Component
public class ComisionMapper {

    public ComisionResponseDTO toDTO(Comision entity) {
        ComisionResponseDTO dto = new ComisionResponseDTO();
        dto.setId(entity.getId());
        dto.setVendedorId(entity.getVendedorId());
        
        // Información del vendedor
        if (entity.getVendedor() != null) {
            dto.setVendedorNombre(entity.getVendedor().getNombre() + " " + 
                                 entity.getVendedor().getApellidos());
            dto.setVendedorPuesto(entity.getVendedor().getPuesto());
        }
        
        dto.setPeriodoAnio(entity.getPeriodoAnio());
        dto.setPeriodoMes(entity.getPeriodoMes());
        dto.setPeriodoTexto(formatearPeriodo(entity.getPeriodoMes(), entity.getPeriodoAnio()));
        dto.setTotalVentas(entity.getTotalVentas());
        dto.setCantidadVentas(entity.getCantidadVentas());
        dto.setComisionBase(entity.getComisionBase());
        dto.setBonos(entity.getBonos());
        dto.setAjustes(entity.getAjustes());
        dto.setTotalComision(entity.getTotalComision());
        dto.setEstado(entity.getEstado());
        dto.setFechaAprobacion(entity.getFechaAprobacion());
        dto.setFechaPago(entity.getFechaPago());
        dto.setUsuarioAprobador(entity.getUsuarioAprobador());
        dto.setNotas(entity.getNotas());
        
        return dto;
    }

    private String formatearPeriodo(Integer mes, Integer anio) {
        Month month = Month.of(mes);
        String nombreMes = month.getDisplayName(TextStyle.FULL, Locale.of("es", "MX"));
        return nombreMes.substring(0, 1).toUpperCase() + nombreMes.substring(1) + " " + anio;
    }
}
