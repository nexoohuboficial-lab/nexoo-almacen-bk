package com.nexoohub.almacen.adquisiciones.service;

import com.nexoohub.almacen.adquisiciones.dto.ActualizacionMasivaRequest;
import com.nexoohub.almacen.adquisiciones.dto.ActualizarPrecioRequest;
import com.nexoohub.almacen.adquisiciones.dto.OpcionCompraProveedorDTO;
import com.nexoohub.almacen.adquisiciones.dto.ResultadoActualizacionResponse;
import com.nexoohub.almacen.adquisiciones.entity.CatalogoProveedorProducto;
import com.nexoohub.almacen.adquisiciones.entity.HistorialPrecioProveedor;
import com.nexoohub.almacen.adquisiciones.repository.CatalogoProveedorProductoRepository;
import com.nexoohub.almacen.adquisiciones.repository.HistorialPrecioProveedorRepository;
import com.nexoohub.almacen.alertas.entity.TipoAlerta;
import com.nexoohub.almacen.alertas.service.AlertaService;
import com.nexoohub.almacen.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActualizacionPreciosProveedorService {

    private final CatalogoProveedorProductoRepository catalogoRepository;
    private final HistorialPrecioProveedorRepository historialRepository;
    private final ComparadorPreciosService comparadorPreciosService;
    private final AlertaService alertaService;

    @Transactional
    public OpcionCompraProveedorDTO actualizarPrecioIndividual(Long id, ActualizarPrecioRequest request, String usuarioAutenticado) {
        CatalogoProveedorProducto catalogo = catalogoRepository.findById(id)
                .orElseThrow(() -> new BusinessException("No se encontró el artículo en el catálogo del proveedor"));

        BigDecimal precioCostoAnterior = catalogo.getPrecioCompraActual();
        BigDecimal precioCostoNuevo = request.precioCostoNuevo();

        if (precioCostoAnterior.compareTo(precioCostoNuevo) == 0) {
            throw new BusinessException("El nuevo precio de costo es igual al actual");
        }

        BigDecimal variacion = calcularVariacionPorcentual(precioCostoAnterior, precioCostoNuevo);

        // Guardar historial
        HistorialPrecioProveedor historial = new HistorialPrecioProveedor();
        historial.setCatalogo(catalogo);
        historial.setPrecioCostoAnterior(precioCostoAnterior);
        historial.setPrecioCostoNuevo(precioCostoNuevo);
        historial.setVariacionPorcentual(variacion);
        historial.setMotivo(request.motivo());
        historial.setUsuarioActualizacion(usuarioAutenticado);
        historialRepository.save(historial);

        // Actualizar el costo del catalogo
        catalogo.setPrecioCompraActual(precioCostoNuevo);
        catalogo.setUltimaActualizacion(LocalDateTime.now());
        catalogoRepository.save(catalogo); // Se dispara el PreUpdate, pero actualizamos manualmente si hay logica

        // Generar alerta si subió más del 10%
        verificarYGenerarAlerta(catalogo, precioCostoAnterior, precioCostoNuevo, variacion);

        // Retornar la proyeccion actualizada
        return comparadorPreciosService.mapearAOpcion(catalogo);
    }

    @Transactional
    public ResultadoActualizacionResponse actualizarPreciosMasivo(ActualizacionMasivaRequest request, String usuarioAutenticado) {
        List<ResultadoActualizacionResponse.ErrorDetalle> errores = new ArrayList<>();
        int exito = 0;

        for (ActualizacionMasivaRequest.ItemActualizacionMasiva item : request.items()) {
            try {
                // Instanciar el request individual
                ActualizarPrecioRequest reqIndiv = new ActualizarPrecioRequest(
                        item.precioCostoNuevo(), 
                        item.motivoEspecifico() != null ? item.motivoEspecifico() : request.motivoGeneral()
                );
                
                // Llamar al metodo individual para asegurar las mismas reglas
                actualizarPrecioIndividual(item.catalogoId(), reqIndiv, usuarioAutenticado);
                exito++;
            } catch (Exception e) {
                log.warn("Error al actualizar catálogo id {}: {}", item.catalogoId(), e.getMessage());
                errores.add(new ResultadoActualizacionResponse.ErrorDetalle(item.catalogoId(), e.getMessage()));
            }
        }

        return new ResultadoActualizacionResponse(request.items().size(), exito, errores.size(), errores);
    }

    @Transactional(readOnly = true)
    public List<HistorialPrecioProveedor> verHistorial(Long catalogoId) {
        if (!catalogoRepository.existsById(catalogoId)) {
            throw new BusinessException("Catálogo no encontrado");
        }
        return historialRepository.findByCatalogoIdOrderByFechaActualizacionDesc(catalogoId);
    }

    private BigDecimal calcularVariacionPorcentual(BigDecimal anterior, BigDecimal nuevo) {
        if (anterior == null || anterior.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return nuevo.subtract(anterior)
                .divide(anterior, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"))
                .setScale(2, RoundingMode.HALF_UP);
    }

    private void verificarYGenerarAlerta(CatalogoProveedorProducto catalogo, BigDecimal anterior, BigDecimal nuevo, BigDecimal variacion) {
        // Alza mayor o igual a 10%
        if (variacion.compareTo(new BigDecimal("10.00")) >= 0) {
            String mensaje = String.format("Aumento repentino de precio proveedor. Producto: %s, Proveedor: %s. Anterior: %s, Nuevo: %s (Subió +%s%%)",
                    catalogo.getProducto().getSkuInterno(),
                    catalogo.getProveedor().getNombreEmpresa(),
                    anterior,
                    nuevo,
                    variacion);
                    
            // Idealmente deberia enviarse al admin o a la sucursal matriz, ponemos sucursal null o 1 como base.
            // Aqui asumo que el ADMIN destino no dependera de sucursal pero enviamos 1 por definicion original.
            alertaService.crearAlerta(TipoAlerta.PRECIO_PROVEEDOR_CAMBIO, mensaje, 1, 1);
        }
    }
}
