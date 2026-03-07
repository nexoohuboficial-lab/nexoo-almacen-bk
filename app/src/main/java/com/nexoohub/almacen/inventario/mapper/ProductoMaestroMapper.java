package com.nexoohub.almacen.inventario.mapper;

import com.nexoohub.almacen.inventario.dto.ProductoMaestroResponseDTO;
import com.nexoohub.almacen.inventario.entity.ProductoMaestro;
import org.springframework.stereotype.Component;

/**
 * Mapper para convertir ProductoMaestro entity a DTOs.
 * 
 * <p>Patrón: Mapper dedicado para mantener separación de responsabilidades.</p>
 */
@Component
public class ProductoMaestroMapper {

    /**
     * Convierte una entidad ProductoMaestro a DTO de respuesta.
     * 
     * @param entity La entidad a convertir
     * @return DTO con datos denormalizados (categoría, proveedor)
     */
    public ProductoMaestroResponseDTO toResponseDTO(ProductoMaestro entity) {
        if (entity == null) {
            return null;
        }
        
        return new ProductoMaestroResponseDTO(
            entity.getSkuInterno(),
            entity.getSkuProveedor(),
            entity.getNombreComercial(),
            entity.getDescripcion(),
            entity.getMarca(),
            entity.getCategoriaId(),
            // Denormalizamos el nombre de categoría
            entity.getCategoria() != null ? entity.getCategoria().getNombre() : null,
            entity.getProveedorId(),
            // Denormalizamos el nombre de proveedor
            entity.getProveedor() != null ? entity.getProveedor().getNombreEmpresa() : null,
            entity.getClaveSat(),
            entity.getStockMinimoGlobal(),
            entity.getActivo(),
            entity.getSensibilidadPrecio(),
            entity.getFechaCreacion(),
            entity.getFechaActualizacion()
        );
    }
}
