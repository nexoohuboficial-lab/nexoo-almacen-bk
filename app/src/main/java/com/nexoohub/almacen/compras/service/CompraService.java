package com.nexoohub.almacen.compras.service;

import com.nexoohub.almacen.common.exception.ResourceNotFoundException;
import com.nexoohub.almacen.compras.dto.CompraRequestDTO;
import com.nexoohub.almacen.compras.entity.Compra;
import com.nexoohub.almacen.compras.entity.DetalleCompra;
import com.nexoohub.almacen.compras.repository.CompraRepository;
import com.nexoohub.almacen.compras.repository.DetalleCompraRepository;
import com.nexoohub.almacen.finanzas.entity.ConfiguracionFinanciera;
import com.nexoohub.almacen.finanzas.entity.HistorialPrecio;
import com.nexoohub.almacen.finanzas.repository.ConfiguracionFinancieraRepository;
import com.nexoohub.almacen.finanzas.repository.HistorialPrecioRepository;
import com.nexoohub.almacen.inventario.entity.InventarioSucursal;
import com.nexoohub.almacen.inventario.entity.InventarioSucursalId;
import com.nexoohub.almacen.inventario.entity.ProductoMaestro;
import com.nexoohub.almacen.inventario.repository.InventarioSucursalRepository;
import com.nexoohub.almacen.inventario.repository.ProductoMaestroRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Servicio para procesamiento de compras a proveedores.
 * 
 * <p>Gestiona la lógica de negocio completa para ingreso de mercancía:</p>
 * <ul>
 *   <li>Recálculo automático de Costo Promedio Ponderado (CPP)</li>
 *   <li>Limpieza de IVA en costos (si vienen incluidos en facturas)</li>
 *   <li>Cálculo inteligente de precios de venta con estrategias de sensibilidad</li>
 *   <li>Actualización automática de inventario y historial de precios</li>
 * </ul>
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
@Service
public class CompraService {

    private final CompraRepository compraRepository;
    private final DetalleCompraRepository detalleCompraRepository;
    private final InventarioSucursalRepository inventarioRepository;
    private final ProductoMaestroRepository productoRepository;
    private final ConfiguracionFinancieraRepository finanzasRepository;
    private final HistorialPrecioRepository historialPrecioRepository;

    /**
     * Constructor con inyección de dependencias.
     * 
     * @param compraRepository Repositorio de compras
     * @param detalleCompraRepository Repositorio de detalles de compra
     * @param inventarioRepository Repositorio de inventario por sucursal
     * @param productoRepository Repositorio de productos maestros
     * @param finanzasRepository Repositorio de configuración financiera
     * @param historialPrecioRepository Repositorio de historial de precios
     */
    public CompraService(
            CompraRepository compraRepository,
            DetalleCompraRepository detalleCompraRepository,
            InventarioSucursalRepository inventarioRepository,
            ProductoMaestroRepository productoRepository,
            ConfiguracionFinancieraRepository finanzasRepository,
            HistorialPrecioRepository historialPrecioRepository) {
        this.compraRepository = compraRepository;
        this.detalleCompraRepository = detalleCompraRepository;
        this.inventarioRepository = inventarioRepository;
        this.productoRepository = productoRepository;
        this.finanzasRepository = finanzasRepository;
        this.historialPrecioRepository = historialPrecioRepository;
    }

    /**
     * Procesa el ingreso de mercancía desde una compra a proveedor.
     * 
     * <p>Ejecuta en una transacción atómica:</p>
     * <ol>
     *   <li>Limpia IVA de costos (si aplica)</li>
     *   <li>Actualiza inventario con nuevo stock</li>
     *   <li>Recalcula Costo Promedio Ponderado (CPP)</li>
     *   <li>Calcula precios de venta con estrategias de sensibilidad</li>
     *   <li>Guarda historial de precios</li>
     *   <li>Registra detalles de la compra</li>
     * </ol>
     * 
     * @param request DTO con datos de la compra (proveedor, folio, detalles)
     * @param usuarioActual Username del usuario que registra la compra
     * @return Compra procesada con folio y total calculado
     * @throws ResourceNotFoundException si no existe configuración financiera o SKU
     */
    // TODO ESTO OCURRE EN UNA SOLA TRANSACCIÓN (O se guarda todo, o no se guarda nada)
    @Transactional
    public Compra procesarIngresoMercancia(CompraRequestDTO request, String usuarioActual) {
        
        // 1. Obtenemos las reglas de negocio financieras (IVA y Margen)
        // Asumimos que el ID 1 es la configuración vigente (la metimos en el script)
        ConfiguracionFinanciera config = finanzasRepository.findById(1)
                .orElseThrow(() -> new ResourceNotFoundException("ConfiguracionFinanciera", 1));

        BigDecimal tasaIva = config.getIva(); // 0.16
        BigDecimal margenBase = config.getMargenGananciaBase(); // 0.30
        
        // 2. Creamos y guardamos el encabezado de la compra
        Compra compra = new Compra();
        compra.setProveedorId(request.getProveedorId());
        compra.setFolioFacturaProveedor(request.getFolioFactura());
        compra.setUsuarioCreacion(usuarioActual);
        compra.setTotalCompra(BigDecimal.ZERO); // Lo sumaremos enseguida
        
        Compra compraGuardada = compraRepository.save(compra);
        BigDecimal totalCompraAcumulado = BigDecimal.ZERO;

        // 3. Procesamos cada renglón de la factura
        for (CompraRequestDTO.DetalleItemDTO item : request.getDetalles()) {
            
            // --- A) LIMPIEZA DE IVA ---
            BigDecimal costoLimpio = item.getCostoUnitario();
            if (request.getPreciosIncluyenIva()) {
                // Le quitamos el IVA: Costo / 1.16
                BigDecimal divisor = BigDecimal.ONE.add(tasaIva);
                costoLimpio = costoLimpio.divide(divisor, 2, RoundingMode.HALF_UP);
            }

            // --- B) ACTUALIZACIÓN DE INVENTARIO Y CPP ---
            InventarioSucursalId idInv = new InventarioSucursalId(request.getSucursalDestinoId(), item.getSkuInterno());
            InventarioSucursal inventario = inventarioRepository.findById(idInv)
                    .orElseGet(() -> {
                        // Si nunca ha habido esta pieza en esta sucursal, creamos el registro
                        InventarioSucursal nuevoInv = new InventarioSucursal();
                        nuevoInv.setId(idInv);
                        nuevoInv.setStockActual(0);
                        nuevoInv.setCostoPromedioPonderado(BigDecimal.ZERO);
                        return nuevoInv;
                    });

            Integer stockViejo = inventario.getStockActual();
            BigDecimal cppViejo = inventario.getCostoPromedioPonderado() != null ? inventario.getCostoPromedioPonderado() : BigDecimal.ZERO;
            
            Integer stockNuevo = item.getCantidad();
            BigDecimal costoNuevo = costoLimpio;

            // Fórmula de Costo Promedio Ponderado
            BigDecimal valorInventarioViejo = cppViejo.multiply(new BigDecimal(stockViejo));
            BigDecimal valorInventarioNuevo = costoNuevo.multiply(new BigDecimal(stockNuevo));
            BigDecimal valorTotal = valorInventarioViejo.add(valorInventarioNuevo);
            Integer stockTotal = stockViejo + stockNuevo;
            
            BigDecimal nuevoCpp = valorTotal.divide(new BigDecimal(stockTotal), 2, RoundingMode.HALF_UP);

            // Guardamos los nuevos valores físicos
            inventario.setStockActual(stockTotal);
            inventario.setCostoPromedioPonderado(nuevoCpp);
            inventarioRepository.save(inventario);

            // --- C) CÁLCULO DE PRECIOS E INTELIGENCIA DE NEGOCIO ---
            ProductoMaestro producto = productoRepository.findById(item.getSkuInterno())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto", item.getSkuInterno()));

            // Fórmula del Precio Técnico: CPP / (1 - Margen) * (1 + IVA)
            BigDecimal divisorMargen = BigDecimal.ONE.subtract(margenBase);
            BigDecimal precioBaseVenta = nuevoCpp.divide(divisorMargen, 2, RoundingMode.HALF_UP);
            BigDecimal precioTecnicoPublico = precioBaseVenta.multiply(BigDecimal.ONE.add(tasaIva)).setScale(2, RoundingMode.HALF_UP);

            BigDecimal precioFinal = precioTecnicoPublico;

            // Estrategia de Sensibilidad (Value-Based Pricing)
            if (item.getPrecioPublicoProveedor() != null && item.getPrecioPublicoProveedor().compareTo(BigDecimal.ZERO) > 0) {
                String sensibilidad = producto.getSensibilidadPrecio();
                
                // Si mi precio técnico es menor al del proveedor, hay oportunidad de ganar más
                if (precioTecnicoPublico.compareTo(item.getPrecioPublicoProveedor()) <= 0) {
                    if ("BAJA".equalsIgnoreCase(sensibilidad)) {
                        // Le subimos un 15% extra sobre el precio del proveedor (El cliente pagará por la urgencia)
                        precioFinal = item.getPrecioPublicoProveedor().multiply(new BigDecimal("1.15"));
                    } else if ("MEDIA".equalsIgnoreCase(sensibilidad)) {
                        // Le subimos un 10%
                        precioFinal = item.getPrecioPublicoProveedor().multiply(new BigDecimal("1.10"));
                    } else {
                        // ALTA sensibilidad (Ej. Aceites). Lo dejamos al precio del mercado, sin inflarlo.
                        precioFinal = item.getPrecioPublicoProveedor();
                    }
                }
            }

            // Redondeo Psicológico (Al múltiplo de 5 superior)
            // Ej. 57.50 -> 60.00
            double precioDouble = precioFinal.doubleValue();
            double redondeado = Math.ceil(precioDouble / 5.0) * 5.0;
            BigDecimal precioPublicoRedondeado = new BigDecimal(String.valueOf(redondeado)).setScale(2, RoundingMode.HALF_UP);

            // --- D) GUARDAR HISTORIAL DE PRECIO ---
            HistorialPrecio historial = new HistorialPrecio();
            historial.setSkuInterno(item.getSkuInterno());
            historial.setCostoBase(nuevoCpp);
            historial.setPrecioPonderado(precioTecnicoPublico);
            historial.setPrecioFinalPublico(precioPublicoRedondeado);
            historial.setPrecioPublicoProveedor(item.getPrecioPublicoProveedor() != null ? item.getPrecioPublicoProveedor() : BigDecimal.ZERO);
            historial.setUsuarioCreacion(usuarioActual);
            historialPrecioRepository.save(historial);

            // --- E) GUARDAR DETALLE DE LA COMPRA ---
            DetalleCompra detalle = new DetalleCompra();
            detalle.setCompraId(compraGuardada.getId());
            detalle.setSkuInterno(item.getSkuInterno());
            detalle.setCantidad(item.getCantidad());
            detalle.setCostoUnitarioCompra(costoLimpio);
            detalleCompraRepository.save(detalle);

            // Acumulamos el total de la factura
            BigDecimal subtotalRenglon = costoLimpio.multiply(new BigDecimal(item.getCantidad()));
            totalCompraAcumulado = totalCompraAcumulado.add(subtotalRenglon);
        }

        // 4. Actualizamos el total de la compra (Agregando el IVA total de la factura)
        BigDecimal totalConIva = totalCompraAcumulado.multiply(BigDecimal.ONE.add(tasaIva)).setScale(2, RoundingMode.HALF_UP);
        compraGuardada.setTotalCompra(totalConIva);
        return compraRepository.save(compraGuardada);
    }
}