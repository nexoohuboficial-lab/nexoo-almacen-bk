package com.nexoohub.almacen.adquisiciones.service;

import com.nexoohub.almacen.adquisiciones.dto.CarritoResumenResponse;
import com.nexoohub.almacen.adquisiciones.dto.CarritoResumenResponse.GrupoProveedorCarritoDTO;
import com.nexoohub.almacen.adquisiciones.dto.CarritoResumenResponse.ItemCarritoDTO;
import com.nexoohub.almacen.adquisiciones.dto.OrdenCompraResponse;
import com.nexoohub.almacen.adquisiciones.entity.DetalleOrdenCompra;
import com.nexoohub.almacen.adquisiciones.entity.OrdenCompraProveedor;
import com.nexoohub.almacen.adquisiciones.repository.OrdenCompraProveedorRepository;
import com.nexoohub.almacen.catalogo.entity.Proveedor;
import com.nexoohub.almacen.catalogo.repository.ProveedorRepository;
import com.nexoohub.almacen.common.entity.Usuario;
import com.nexoohub.almacen.common.exception.BusinessException;
import com.nexoohub.almacen.common.exception.ResourceNotFoundException;
import com.nexoohub.almacen.common.repository.UsuarioRepository;
import com.nexoohub.almacen.empleados.entity.Empleado;
import com.nexoohub.almacen.empleados.repository.EmpleadoRepository;
import com.nexoohub.almacen.compras.service.CompraService;
import com.nexoohub.almacen.sucursal.entity.Sucursal;
import com.nexoohub.almacen.sucursal.repository.SucursalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrdenCompraService {

    private final OrdenCompraProveedorRepository ordenRepository;
    private final CarritoCompraService carritoService;
    private final UsuarioRepository usuarioRepository;
    private final EmpleadoRepository empleadoRepository;
    private final SucursalRepository sucursalRepository;
    private final ProveedorRepository proveedorRepository;
    private final CompraService compraService; // Para integrarlo luego al recibir mercancia

    @Transactional
    public List<OrdenCompraResponse> generarOrdenesDeCompra(String username) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("Usuario no autenticado"));
                
        Integer usuarioId = usuario.getId().intValue();
        
        Empleado empleado = empleadoRepository.findById(usuario.getEmpleadoId())
                .orElseThrow(() -> new BusinessException("El usuario no tiene un empleado vinculado"));
                
        Sucursal sucursal = sucursalRepository.findById(empleado.getSucursalId())
                .orElseThrow(() -> new BusinessException("El empleado no tiene sucursal vinculada"));

        CarritoResumenResponse carrito = carritoService.verCarrito(usuarioId);

        if (carrito.getTotalArticulos() == 0 || carrito.getGruposPorProveedor().isEmpty()) {
            throw new BusinessException("El carrito de compras está vacío");
        }

        List<OrdenCompraResponse> generadas = new ArrayList<>();
        String currentYear = String.valueOf(java.time.Year.now().getValue());

        for (GrupoProveedorCarritoDTO grupo : carrito.getGruposPorProveedor()) {
            Proveedor proveedor = proveedorRepository.findById(grupo.getProveedorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Proveedor", grupo.getProveedorId()));

            OrdenCompraProveedor oc = new OrdenCompraProveedor();
            oc.setProveedor(proveedor);
            oc.setSucursal(sucursal);
            oc.setEstado("BORRADOR");
            oc.setTotalEstimado(grupo.getSubtotalProveedor());
            oc.setUsuarioCreacion(username);
            
            // Generar Folio OC-YYYY-NNNN
            long count = ordenRepository.countByFolioPrefix("OC-" + currentYear + "-");
            String folio = String.format("OC-%s-%04d", currentYear, count + 1);
            oc.setFolio(folio);
            
            for (ItemCarritoDTO item : grupo.getItems()) {
                DetalleOrdenCompra detalle = new DetalleOrdenCompra();
                detalle.setSkuInterno(item.getSkuInterno());
                detalle.setSkuProveedor(item.getSkuProveedor());
                detalle.setNombreProducto(item.getNombreProducto());
                detalle.setCantidad(item.getCantidad());
                detalle.setPrecioCostoUnitario(item.getPrecioCostoUnitario());
                detalle.setPrecioVentaSugerido(item.getPrecioVentaSugerido());
                detalle.setSubtotal(item.getSubtotal());
                detalle.setUsuarioCreacion(username);
                oc.addDetalle(detalle);
            }

            ordenRepository.save(oc);
            generadas.add(mapearResponse(oc));
        }

        // Vaciar el carrito
        carritoService.vaciarCarrito(usuarioId);

        return generadas;
    }

    @Transactional
    public OrdenCompraResponse actualizarEstado(Integer ordenId, String nuevoEstado, String username) {
        OrdenCompraProveedor oc = ordenRepository.findById(ordenId)
                .orElseThrow(() -> new ResourceNotFoundException("OrdenCompra", ordenId));
        
        oc.setEstado(nuevoEstado);
        oc.setUsuarioActualizacion(username);
        oc.setFechaActualizacion(LocalDateTime.now());
        
        if ("ENVIADA".equalsIgnoreCase(nuevoEstado) && oc.getFechaEnvio() == null) {
            oc.setFechaEnvio(LocalDateTime.now());
            // Estimamos entrega en base al primer producto o se puede requerir input de usuario
            // Por defecto damos 3 días
            oc.setFechaEsperadaEntrega(LocalDateTime.now().plusDays(3));
        }

        ordenRepository.save(oc);
        return mapearResponse(oc);
    }

    @Transactional
    public OrdenCompraResponse recibirOrdenCompra(Integer ordenId, String username) {
        OrdenCompraProveedor oc = ordenRepository.findById(ordenId)
                .orElseThrow(() -> new ResourceNotFoundException("OrdenCompra", ordenId));

        if ("RECIBIDA".equalsIgnoreCase(oc.getEstado())) {
            throw new BusinessException("La orden de compra ya fue recibida anteriormente");
        }

        oc.setEstado("RECIBIDA");
        oc.setUsuarioActualizacion(username);
        oc.setFechaActualizacion(LocalDateTime.now());
        
        // Integración con el Módulo de Inventario y Finanzas
        com.nexoohub.almacen.compras.dto.CompraRequestDTO compraReq = new com.nexoohub.almacen.compras.dto.CompraRequestDTO();
        compraReq.setProveedorId(oc.getProveedor().getId());
        compraReq.setFolioFactura(oc.getFolio());
        compraReq.setSucursalDestinoId(oc.getSucursal().getId());
        // Se asume que las OCs para almacén interno ya vienen con IVA desglosado si aplica o limpio
        // Dejaremos false para que tome el precio tal como se generó en el comparador (que ya es costo limpio)
        compraReq.setPreciosIncluyenIva(false);
        
        List<com.nexoohub.almacen.compras.dto.CompraRequestDTO.DetalleItemDTO> detallesReq = new ArrayList<>();
        for (DetalleOrdenCompra det : oc.getDetalles()) {
            com.nexoohub.almacen.compras.dto.CompraRequestDTO.DetalleItemDTO itemReq = new com.nexoohub.almacen.compras.dto.CompraRequestDTO.DetalleItemDTO();
            itemReq.setSkuInterno(det.getSkuInterno());
            itemReq.setCantidad(det.getCantidad());
            itemReq.setCostoUnitario(det.getPrecioCostoUnitario());
            itemReq.setPrecioPublicoProveedor(det.getPrecioVentaSugerido());
            detallesReq.add(itemReq);
        }
        compraReq.setDetalles(detallesReq);
        
        // Ejecutar compra física
        compraService.procesarIngresoMercancia(compraReq, username);

        ordenRepository.save(oc);
        return mapearResponse(oc);
    }
    
    @Transactional(readOnly = true)
    public List<OrdenCompraResponse> listarOrdenes(Integer proveedorId, String estado, LocalDateTime fechaInicio) {
        return ordenRepository.findByFiltros(proveedorId, estado, fechaInicio)
                .stream().map(this::mapearResponse).toList();
    }
    
    @Transactional(readOnly = true)
    public OrdenCompraProveedor obtenerPorId(Integer ordenId) {
        return ordenRepository.findById(ordenId)
                .orElseThrow(() -> new ResourceNotFoundException("OrdenCompra", ordenId));
    }

    @Transactional(readOnly = true)
    public OrdenCompraResponse obtenerOrdenCompraResponse(Integer ordenId) {
        OrdenCompraProveedor oc = obtenerPorId(ordenId);
        return mapearResponse(oc);
    }

    private OrdenCompraResponse mapearResponse(OrdenCompraProveedor oc) {
        List<OrdenCompraResponse.DetalleResponse> detalles = oc.getDetalles().stream().map(d -> 
            OrdenCompraResponse.DetalleResponse.builder()
                .id(d.getId())
                .skuInterno(d.getSkuInterno())
                .skuProveedor(d.getSkuProveedor())
                .nombreProducto(d.getNombreProducto())
                .cantidad(d.getCantidad())
                .precioCostoUnitario(d.getPrecioCostoUnitario())
                .subtotal(d.getSubtotal())
                .build()
        ).toList();

        return OrdenCompraResponse.builder()
                .id(oc.getId())
                .folio(oc.getFolio())
                .proveedorId(oc.getProveedor().getId())
                .nombreProveedor(oc.getProveedor().getNombreEmpresa())
                .rfcProveedor(oc.getProveedor().getRfc())
                .sucursalId(oc.getSucursal().getId())
                .nombreSucursal(oc.getSucursal().getNombre())
                .estado(oc.getEstado())
                .totalEstimado(oc.getTotalEstimado())
                .notas(oc.getNotas())
                .fechaEnvio(oc.getFechaEnvio())
                .fechaEsperadaEntrega(oc.getFechaEsperadaEntrega())
                .creadoPor(oc.getUsuarioCreacion())
                .fechaCreacion(oc.getFechaCreacion())
                .detalles(detalles)
                .build();
    }
}
