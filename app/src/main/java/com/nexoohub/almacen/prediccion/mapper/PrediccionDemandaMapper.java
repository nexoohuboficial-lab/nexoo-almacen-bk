package com.nexoohub.almacen.prediccion.mapper;

import com.nexoohub.almacen.inventario.entity.ProductoMaestro;
import com.nexoohub.almacen.prediccion.dto.PrediccionDemandaResponseDTO;
import com.nexoohub.almacen.prediccion.entity.PrediccionDemanda;
import com.nexoohub.almacen.sucursal.entity.Sucursal;
import org.springframework.stereotype.Component;

import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;

/**
 * Mapper para convertir entidades PrediccionDemanda a DTOs.
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
@Component
public class PrediccionDemandaMapper {

    /**
     * Convierte una predicción a DTO de respuesta
     */
    public PrediccionDemandaResponseDTO toDTO(PrediccionDemanda prediccion, 
                                              ProductoMaestro producto, 
                                              Sucursal sucursal) {
        if (prediccion == null) {
            return null;
        }

        Month month = Month.of(prediccion.getPeriodoMes());
        String nombreMes = month.getDisplayName(TextStyle.FULL, Locale.of("es", "MX"));
        String periodoTexto = nombreMes.substring(0, 1).toUpperCase() + 
                             nombreMes.substring(1) + " " + prediccion.getPeriodoAnio();

        return new PrediccionDemandaResponseDTO(
            prediccion.getId(),
            prediccion.getSkuProducto(),
            producto != null ? producto.getNombreComercial() : prediccion.getSkuProducto(),
            prediccion.getSucursalId(),
            sucursal != null ? sucursal.getNombre() : "Sucursal " + prediccion.getSucursalId(),
            prediccion.getPeriodoAnio(),
            prediccion.getPeriodoMes(),
            periodoTexto,
            prediccion.getDemandaHistorica(),
            prediccion.getTendencia(),
            prediccion.getDemandaPredicha(),
            prediccion.getStockActual(),
            prediccion.getStockSeguridad(),
            prediccion.getStockSugerido(),
            prediccion.getCantidadComprar(),
            prediccion.getNivelConfianza(),
            prediccion.getMetodoCalculo(),
            prediccion.getPeriodosAnalizados(),
            prediccion.getFechaCalculo(),
            prediccion.getObservaciones()
        );
    }

    /**
     * Convierte sin cargar relaciones (para performance)
     */
    public PrediccionDemandaResponseDTO toDTOSimple(PrediccionDemanda prediccion) {
        return toDTO(prediccion, null, null);
    }
}
