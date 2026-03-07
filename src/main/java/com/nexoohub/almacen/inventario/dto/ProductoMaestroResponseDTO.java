package com.nexoohub.almacen.inventario.dto;

import java.time.LocalDateTime;

/**
 * DTO para respuestas de Producto Maestro.
 * 
 * <p>Evita exponer la entidad completa y previene problemas de serialización 
 * con relaciones LAZY (Categoria, Proveedor).</p>
 * 
 * <p>Beneficios:</p>
 * <ul>
 *   <li>Oculta campos de auditoría (createdBy, updatedBy)</li>
 *   <li>Denormaliza datos de catálogo (categoriaNombre, proveedorNombre)</li>
 *   <li>Reduce payload JSON (no incluye relaciones completas)</li>
 *   <li>Previene lazy loading exceptions en API responses</li>
 * </ul>
 */
public record ProductoMaestroResponseDTO(
    String skuInterno,
    String skuProveedor,
    String nombreComercial,
    String descripcion,
    String marca,
    Integer categoriaId,
    String categoriaNombre,
    Integer proveedorId,
    String proveedorNombre,
    String claveSat,
    Integer stockMinimoGlobal,
    Boolean activo,
    String sensibilidadPrecio,
    LocalDateTime fechaCreacion,
    LocalDateTime fechaActualizacion
) {
    /**
     * Constructor compacto con valores por defecto.
     */
    public ProductoMaestroResponseDTO {
        if (stockMinimoGlobal == null) {
            stockMinimoGlobal = 2;
        }
        if (activo == null) {
            activo = true;
        }
        if (sensibilidadPrecio == null) {
            sensibilidadPrecio = "MEDIA";
        }
    }
}
