package com.nexoohub.almacen.catalogo.mapper;

import com.nexoohub.almacen.catalogo.dto.CompatibilidadResponseDTO;
import com.nexoohub.almacen.catalogo.entity.CompatibilidadProducto;
import com.nexoohub.almacen.catalogo.repository.MotoRepository;
import com.nexoohub.almacen.inventario.repository.ProductoMaestroRepository;
import org.springframework.stereotype.Component;

/**
 * Mapper para convertir CompatibilidadProducto entity a DTOs.
 * 
 * <p>Denormaliza información de producto y moto.</p>
 */
@Component
public class CompatibilidadMapper {

    private final ProductoMaestroRepository productoRepository;
    private final MotoRepository motoRepository;

    public CompatibilidadMapper(ProductoMaestroRepository productoRepository, 
                                MotoRepository motoRepository) {
        this.productoRepository = productoRepository;
        this.motoRepository = motoRepository;
    }

    public CompatibilidadResponseDTO toResponseDTO(CompatibilidadProducto entity) {
        if (entity == null) {
            return null;
        }
        
        String nombreProducto = null;
        if (entity.getSkuInterno() != null) {
            nombreProducto = productoRepository.findById(entity.getSkuInterno())
                .map(p -> p.getNombreComercial())
                .orElse(null);
        }
        
        String marcaMoto = null;
        String modeloMoto = null;
        if (entity.getMotoId() != null) {
            var moto = motoRepository.findById(entity.getMotoId()).orElse(null);
            if (moto != null) {
                marcaMoto = moto.getMarca();
                modeloMoto = moto.getModelo();
            }
        }
        
        return new CompatibilidadResponseDTO(
            entity.getId(),
            entity.getSkuInterno(),
            nombreProducto,
            entity.getMotoId(),
            marcaMoto,
            modeloMoto,
            entity.getAnioInicio(),
            entity.getAnioFin(),
            entity.getFechaCreacion()
        );
    }
}
