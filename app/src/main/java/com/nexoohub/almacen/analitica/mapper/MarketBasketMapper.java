package com.nexoohub.almacen.analitica.mapper;

import com.nexoohub.almacen.analitica.dto.ReglaAsociacionDTO;
import com.nexoohub.almacen.analitica.entity.ReglaAsociacionProductos;
import org.springframework.stereotype.Component;

@Component
public class MarketBasketMapper {

    public ReglaAsociacionDTO toDto(ReglaAsociacionProductos entity) {
        if (entity == null) {
            return null;
        }
        return new ReglaAsociacionDTO(
            entity.getSkuDestino(),
            entity.getConfianza(),
            entity.getLift()
        );
    }
}
