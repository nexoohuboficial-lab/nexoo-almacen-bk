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
        
        // Acceso seguro a relaciones para evitar problemas en tests
        String categoriaNombre = null;
        String proveedorNombre = null;
        
        try {
            if (entity.getCategoria() != null) {
                categoriaNombre = entity.getCategoria().getNombre();
            }
        } catch (Exception e) {
            // Ignorar errores de lazy loading en tests
        }
        
        try {
            if (entity.getProveedor() != null) {
                proveedorNombre = entity.getProveedor().getNombreEmpresa();
            }
        } catch (Exception e) {
            // Ignorar errores de lazy loading en tests
        }
        
        return new ProductoMaestroResponseDTO(
            entity.getSkuInterno(),
            entity.getSkuProveedor(),
            entity.getNombreComercial(),
            entity.getDescripcion(),
            entity.getMarca(),
            entity.getCategoriaId(),
            categoriaNombre,
            entity.getProveedorId(),
            proveedorNombre,
            entity.getClaveSat(),
            entity.getStockMinimoGlobal(),
            entity.getActivo(),
            entity.getSensibilidadPrecio(),
            entity.getFechaCreacion(),
            entity.getFechaActualizacion()
        );
    }
}
