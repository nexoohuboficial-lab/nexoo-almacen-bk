package com.nexoohub.almacen.ventas.service;
import com.nexoohub.almacen.common.entity.Usuario;
import com.nexoohub.almacen.common.repository.UsuarioRepository;
import com.nexoohub.almacen.finanzas.entity.HistorialPrecio;
import com.nexoohub.almacen.finanzas.repository.HistorialPrecioRepository;
import com.nexoohub.almacen.inventario.entity.InventarioSucursal;
import com.nexoohub.almacen.inventario.entity.InventarioSucursalId;
import com.nexoohub.almacen.inventario.repository.InventarioSucursalRepository;
import com.nexoohub.almacen.ventas.dto.VentaRequestDTO;
import com.nexoohub.almacen.ventas.entity.DetalleVenta;
import com.nexoohub.almacen.ventas.entity.Venta;
import com.nexoohub.almacen.ventas.repository.DetalleVentaRepository;
import com.nexoohub.almacen.ventas.repository.VentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class VentaService {

    @Autowired private VentaRepository ventaRepository;
    @Autowired private DetalleVentaRepository detalleVentaRepository;
    @Autowired private InventarioSucursalRepository inventarioRepository;
    @Autowired private HistorialPrecioRepository historialPrecioRepository;
    @Autowired private UsuarioRepository usuarioRepository;

    @Transactional
    public Venta procesarVenta(VentaRequestDTO request, String vendedorUsername) {
        
        Usuario usuario = usuarioRepository.findByUsername(vendedorUsername)
            .orElseThrow(() -> new RuntimeException("Vendedor no encontrado"));
        // 1. Crear encabezado de venta
        Venta venta = new Venta();
        venta.setClienteId(request.getClienteId());
        venta.setSucursalId(request.getSucursalId());
        venta.setVendedorId(usuario.getId());
        venta.setMetodoPago(request.getMetodoPago());
        venta.setTotal(BigDecimal.ZERO);
        
        Venta ventaGuardada = ventaRepository.save(venta);
        BigDecimal totalVenta = BigDecimal.ZERO;

        // 2. Procesar cada artículo
        for (VentaRequestDTO.ItemVentaDTO item : request.getItems()) {
            
            // A) Verificar Existencias
            InventarioSucursalId idInv = new InventarioSucursalId(request.getSucursalId(), item.getSkuInterno());
            InventarioSucursal inventario = inventarioRepository.findById(idInv)
                    .orElseThrow(() -> new RuntimeException("El producto " + item.getSkuInterno() + " no existe en esta sucursal."));

            if (inventario.getStockActual() < item.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para " + item.getSkuInterno() + ". Disponible: " + inventario.getStockActual());
            }

            // B) Obtener el precio de venta más reciente
            HistorialPrecio ultimoPrecio = historialPrecioRepository.findTopBySkuInternoOrderByFechaCalculoDesc(item.getSkuInterno())
                    .orElseThrow(() -> new RuntimeException("El producto " + item.getSkuInterno() + " no tiene un precio configurado."));

            // C) Restar del inventario
            inventario.setStockActual(inventario.getStockActual() - item.getCantidad());
            inventarioRepository.save(inventario);

            // D) Registrar detalle de venta
            DetalleVenta detalle = new DetalleVenta();
            detalle.setVentaId(ventaGuardada.getId());
            detalle.setSkuInterno(item.getSkuInterno());
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitarioVenta(ultimoPrecio.getPrecioFinalPublico());
            detalleVentaRepository.save(detalle);

            // E) Acumular total
            BigDecimal subtotal = ultimoPrecio.getPrecioFinalPublico().multiply(new BigDecimal(item.getCantidad()));
            totalVenta = totalVenta.add(subtotal);
        }

        // 3. Actualizar total final de la venta
        ventaGuardada.setTotal(totalVenta);
        return ventaRepository.save(ventaGuardada);
    }
}