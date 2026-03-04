package com.nexoohub.almacen.ventas.service;

import com.nexoohub.almacen.catalogo.entity.Cliente;
import com.nexoohub.almacen.catalogo.entity.PrecioEspecial;
import com.nexoohub.almacen.catalogo.repository.ClienteRepository;
import com.nexoohub.almacen.catalogo.repository.PrecioEspecialRepository;
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
import java.util.Optional;

@Service
public class VentaService {

    @Autowired private VentaRepository ventaRepository;
    @Autowired private DetalleVentaRepository detalleVentaRepository;
    @Autowired private InventarioSucursalRepository inventarioRepository;
    @Autowired private HistorialPrecioRepository historialPrecioRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    
    // Inyectamos los nuevos repositorios
    @Autowired private ClienteRepository clienteRepository;
    @Autowired private PrecioEspecialRepository precioEspecialRepository;

    @Transactional
    public Venta procesarVenta(VentaRequestDTO request, String vendedorUsername) {
        
        Usuario usuario = usuarioRepository.findByUsername(vendedorUsername)
                .orElseThrow(() -> new RuntimeException("Vendedor no encontrado"));

        // 1. OBTENEMOS AL CLIENTE PARA SABER SU TIPO (Público, Taller, Mayorista)
        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new RuntimeException("El cliente con ID " + request.getClienteId() + " no existe."));

        Venta venta = new Venta();
        venta.setClienteId(cliente.getId());
        venta.setSucursalId(request.getSucursalId());
        venta.setVendedorId(usuario.getId().intValue()); 
        venta.setMetodoPago(request.getMetodoPago());
        venta.setTotal(BigDecimal.ZERO);
        
        Venta ventaGuardada = ventaRepository.save(venta);
        BigDecimal totalVenta = BigDecimal.ZERO;

        for (VentaRequestDTO.ItemVentaDTO item : request.getItems()) {
            
            // A) Verificar Existencias
            InventarioSucursalId idInv = new InventarioSucursalId(request.getSucursalId(), item.getSkuInterno());
            InventarioSucursal inventario = inventarioRepository.findById(idInv)
                    .orElseThrow(() -> new RuntimeException("El producto " + item.getSkuInterno() + " no existe en esta sucursal."));

            if (inventario.getStockActual() < item.getCantidad()) {
                throw new RuntimeException("Stock insuficiente para " + item.getSkuInterno() + ". Disponible: " + inventario.getStockActual());
            }

            // B) Obtener el precio público base
            HistorialPrecio ultimoPrecio = historialPrecioRepository.findTopBySkuInternoOrderByFechaCalculoDesc(item.getSkuInterno())
                    .orElseThrow(() -> new RuntimeException("El producto " + item.getSkuInterno() + " no tiene un precio configurado."));

            // ==========================================
            // C) LÓGICA DE PRECIO DINÁMICO (LA MAGIA)
            // ==========================================
            BigDecimal precioFinalAplicado = ultimoPrecio.getPrecioFinalPublico(); // Asumimos precio público por defecto

            Optional<PrecioEspecial> precioEsp = precioEspecialRepository
                    .findBySkuInternoAndTipoClienteId(item.getSkuInterno(), cliente.getTipoClienteId());

            if (precioEsp.isPresent()) {
                precioFinalAplicado = precioEsp.get().getPrecioFijo(); // ¡Cambiamos al precio de Taller!
            }
            // ==========================================

            // D) Restar del inventario
            inventario.setStockActual(inventario.getStockActual() - item.getCantidad());
            inventarioRepository.save(inventario);

            // E) Registrar detalle de venta con el precio correcto
            DetalleVenta detalle = new DetalleVenta();
            detalle.setVentaId(ventaGuardada.getId());
            detalle.setSkuInterno(item.getSkuInterno());
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitarioVenta(precioFinalAplicado); // Usamos el precio ya evaluado
            detalleVentaRepository.save(detalle);

            // F) Acumular total
            BigDecimal subtotal = precioFinalAplicado.multiply(new BigDecimal(item.getCantidad()));
            totalVenta = totalVenta.add(subtotal);
        }

        ventaGuardada.setTotal(totalVenta);
        return ventaRepository.save(ventaGuardada);
    }
}