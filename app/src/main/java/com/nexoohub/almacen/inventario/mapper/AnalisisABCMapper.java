package com.nexoohub.almacen.inventario.mapper;

import com.nexoohub.almacen.inventario.dto.AnalisisABCResponseDTO;
import com.nexoohub.almacen.inventario.entity.AnalisisABC;
import com.nexoohub.almacen.inventario.entity.ProductoMaestro;
import com.nexoohub.almacen.sucursal.entity.Sucursal;
import org.springframework.stereotype.Component;

/**
 * Mapper para convertir AnalisisABC entity a DTOs.
 * 
 * @author NexooHub Development Team
 * @version 1.0
 */
@Component
public class AnalisisABCMapper {

    /**
     * Convierte una entidad AnalisisABC a DTO de respuesta.
     * 
     * @param analisis La entidad de análisis
     * @param producto Producto maestro (puede ser null)
     * @param sucursal Sucursal (puede ser null)
     * @return DTO con datos denormalizados
     */
    public AnalisisABCResponseDTO toDTO(AnalisisABC analisis, ProductoMaestro producto, Sucursal sucursal) {
        if (analisis == null) {
            return null;
        }

        return new AnalisisABCResponseDTO(
            analisis.getId(),
            analisis.getSkuProducto(),
            producto != null ? producto.getNombreComercial() : null,
            analisis.getSucursalId(),
            sucursal != null ? sucursal.getNombre() : null,
            analisis.getClasificacion(),
            analisis.getPeriodoInicio(),
            analisis.getPeriodoFin(),
            analisis.getCantidadVendida(),
            analisis.getValorVentas(),
            analisis.getPorcentajeValor(),
            analisis.getPorcentajeAcumulado(),
            analisis.getStockActual(),
            analisis.getValorStock(),
            analisis.getRotacionInventario(),
            analisis.getFechaAnalisis(),
            analisis.getObservaciones()
        );
    }

    /**
     * Convierte sin datos de producto/sucursal (para casos donde no se requieren)
     */
    public AnalisisABCResponseDTO toDTO(AnalisisABC analisis) {
        return toDTO(analisis, null, null);
    }
}
