package com.nexoohub.almacen.adquisiciones.service;

import com.nexoohub.almacen.adquisiciones.dto.OpcionCompraProveedorDTO;
import com.nexoohub.almacen.adquisiciones.entity.CatalogoProveedorProducto;
import com.nexoohub.almacen.adquisiciones.repository.CatalogoProveedorProductoRepository;
import com.nexoohub.almacen.common.exception.ResourceNotFoundException;
import com.nexoohub.almacen.finanzas.entity.ConfiguracionFinanciera;
import com.nexoohub.almacen.finanzas.repository.ConfiguracionFinancieraRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class ComparadorPreciosService {

    private final CatalogoProveedorProductoRepository catalogoRepository;
    private final ConfiguracionFinancieraRepository finanzasRepository;

    public ComparadorPreciosService(CatalogoProveedorProductoRepository catalogoRepository,
                                    ConfiguracionFinancieraRepository finanzasRepository) {
        this.catalogoRepository = catalogoRepository;
        this.finanzasRepository = finanzasRepository;
    }

    @Transactional(readOnly = true)
    public List<OpcionCompraProveedorDTO> compararPreciosParaProducto(String sku) {
        List<CatalogoProveedorProducto> opciones = catalogoRepository.findProveedoresDisponiblesByProductoOrderByPrecioAsc(sku);
        
        if (opciones.isEmpty()) {
            return new ArrayList<>();
        }

        // Obtener configuración financiera para simular el precio de venta sugerido
        ConfiguracionFinanciera config = finanzasRepository.findById(1)
                .orElseThrow(() -> new ResourceNotFoundException("ConfiguracionFinanciera", 1));
        
        BigDecimal tasaIva = config.getIva();
        BigDecimal margenBase = config.getMargenGananciaBase();
        
        List<OpcionCompraProveedorDTO> resultados = new ArrayList<>();
        boolean primeraOpcion = true;

        for (CatalogoProveedorProducto opcion : opciones) {
            OpcionCompraProveedorDTO dto = mapearAOpcion(opcion, config);
            
            // Si la lista ya viene ordenada ASC por la base de datos, el primero es la mejor opción en precio
            dto.setMejorOpcion(primeraOpcion);
            if (primeraOpcion) {
                primeraOpcion = false;
            }

            resultados.add(dto);
        }

        return resultados;
    }

    public OpcionCompraProveedorDTO mapearAOpcion(CatalogoProveedorProducto opcion) {
        ConfiguracionFinanciera config = finanzasRepository.findById(1)
                .orElseThrow(() -> new ResourceNotFoundException("ConfiguracionFinanciera", 1));
        return mapearAOpcion(opcion, config);
    }

    private OpcionCompraProveedorDTO mapearAOpcion(CatalogoProveedorProducto opcion, ConfiguracionFinanciera config) {
        BigDecimal tasaIva = config.getIva();
        BigDecimal margenBase = config.getMargenGananciaBase();

        OpcionCompraProveedorDTO dto = new OpcionCompraProveedorDTO();
        dto.setProveedorId(opcion.getProveedor().getId());
        dto.setNombreProveedor(opcion.getProveedor().getNombreEmpresa());
        dto.setCodigoArticuloProveedor(opcion.getProveedorCodigoProducto());
        dto.setPrecioCompraCotizado(opcion.getPrecioCompraActual());
        dto.setMoneda(opcion.getMoneda());
        dto.setTiempoEstimadoEntregaDias(opcion.getTiempoEntregaDias());
        dto.setUltimaActualizacion(opcion.getUltimaActualizacion());
        dto.setMejorOpcion(false); // Por defecto

        // Calcular precio sugerido de venta (Lógica reutilizada de CompraService)
        BigDecimal costoLimpio = opcion.getPrecioCompraActual();
        BigDecimal divisorMargen = BigDecimal.ONE.subtract(margenBase);
        BigDecimal precioBaseVenta = costoLimpio.divide(divisorMargen, 2, RoundingMode.HALF_UP);
        BigDecimal precioTecnicoPublico = precioBaseVenta.multiply(BigDecimal.ONE.add(tasaIva)).setScale(2, RoundingMode.HALF_UP);
        
        // Redondeo Psicológico (Al múltiplo de 5 superior)
        double precioDouble = precioTecnicoPublico.doubleValue();
        double redondeado = Math.ceil(precioDouble / 5.0) * 5.0;
        BigDecimal precioSugeridoVenta = new BigDecimal(String.valueOf(redondeado)).setScale(2, RoundingMode.HALF_UP);
        
        dto.setPrecioSugeridoVenta(precioSugeridoVenta);
        return dto;
    }
}
