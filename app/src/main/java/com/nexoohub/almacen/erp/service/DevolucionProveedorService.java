package com.nexoohub.almacen.erp.service;

import com.nexoohub.almacen.common.exception.ResourceNotFoundException;
import com.nexoohub.almacen.erp.dto.*;
import com.nexoohub.almacen.erp.entity.*;
import com.nexoohub.almacen.erp.repository.*;
import com.nexoohub.almacen.catalogo.entity.Proveedor;
import com.nexoohub.almacen.catalogo.repository.ProveedorRepository;
import com.nexoohub.almacen.inventario.entity.InventarioSucursal;
import com.nexoohub.almacen.inventario.entity.InventarioSucursalId;
import com.nexoohub.almacen.inventario.entity.MovimientoInventario;
import com.nexoohub.almacen.inventario.repository.InventarioSucursalRepository;
import com.nexoohub.almacen.inventario.repository.MovimientoInventarioRepository;
import com.nexoohub.almacen.inventario.entity.ProductoMaestro;
import com.nexoohub.almacen.inventario.repository.ProductoMaestroRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DevolucionProveedorService {

    private static final Logger log = LoggerFactory.getLogger(DevolucionProveedorService.class);

    private final DevolucionProveedorRepository devolucionRepo;
    private final ProveedorRepository proveedorRepo;
    private final InventarioSucursalRepository inventarioRepo;
    private final MovimientoInventarioRepository movimientoRepo;
    private final ProductoMaestroRepository productoRepo;

    public DevolucionProveedorService(
            DevolucionProveedorRepository devolucionRepo,
            ProveedorRepository proveedorRepo,
            InventarioSucursalRepository inventarioRepo,
            MovimientoInventarioRepository movimientoRepo,
            ProductoMaestroRepository productoRepo) {
        this.devolucionRepo = devolucionRepo;
        this.proveedorRepo = proveedorRepo;
        this.inventarioRepo = inventarioRepo;
        this.movimientoRepo = movimientoRepo;
        this.productoRepo = productoRepo;
    }

    @Transactional
    public DevolucionProveedorResponse registrarDevolucion(DevolucionProveedorRequest req) {
        log.info("Registrando devolución a proveedor: {}", req.getProveedorId());
        
        Proveedor prov = proveedorRepo.findById(req.getProveedorId())
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado"));

        DevolucionProveedor dev = new DevolucionProveedor();
        dev.setProveedor(prov);
        dev.setSucursalId(req.getSucursalId());
        dev.setUsuarioId(req.getUsuarioId());
        dev.setMotivo(req.getMotivo());
        dev.setEstatus("CREADA");
        dev.setFecha(LocalDate.now());

        for (DevolucionProveedorDetalleRequest detReq : req.getDetalles()) {
            DevolucionProveedorDetalle det = new DevolucionProveedorDetalle();
            det.setSkuInterno(detReq.getSkuInterno());
            det.setCantidad(detReq.getCantidad());
            det.setCostoUnitario(detReq.getCostoUnitario());
            det.setSubtotal(detReq.getCostoUnitario().multiply(new java.math.BigDecimal(detReq.getCantidad())));
            dev.addDetalle(det);
        }

        return mapToResponse(devolucionRepo.save(dev));
    }

    @Transactional
    public DevolucionProveedorResponse aplicarDevolucion(Integer id) {
        log.info("Aplicando devolución a proveedor ID: {}", id);
        
        DevolucionProveedor dev = devolucionRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Devolución no encontrada"));

        if (!"CREADA".equals(dev.getEstatus())) {
            throw new IllegalStateException("La devolución ya fue aplicada o cancelada");
        }

        // 1. Modificar inventario y generar movimientos
        for (DevolucionProveedorDetalle det : dev.getDetalles()) {
            InventarioSucursalId invId = new InventarioSucursalId(dev.getSucursalId(), det.getSkuInterno());
            InventarioSucursal inv = inventarioRepo.findById(invId)
                    .orElseThrow(() -> new IllegalStateException("Producto no existe en el inventario de la sucursal: " + det.getSkuInterno()));

            if (inv.getStockActual() < det.getCantidad()) {
                throw new IllegalStateException("Stock insuficiente para el producto: " + det.getSkuInterno());
            }

            // Descontar
            inv.setStockActual(inv.getStockActual() - det.getCantidad());
            inventarioRepo.save(inv);

            // Registrar Movimiento Histórico
            MovimientoInventario mov = new MovimientoInventario();
            mov.setSucursalId(dev.getSucursalId());
            mov.setSkuInterno(det.getSkuInterno());
            mov.setTipoMovimiento("SALIDA_DEVOLUCION_PROVEEDOR");
            mov.setCantidad(det.getCantidad());
            mov.setUsuarioId(dev.getUsuarioId());
            mov.setRastreoId("DEV-PROV-" + dev.getId());
            mov.setComentarios("Devolución por merma: " + dev.getMotivo());
            movimientoRepo.save(mov);
        }

        // 2. Cambiar Estatus
        dev.setEstatus("APLICADA");
        
        // El impacto en CxP (Saldo a favor) puede ser delegado a otro flujo u orquestador. 
        // Históricamente, en ERP, se genera una nota de crédito a favor. 
        // Asumiendo que el encargado de CxP conciliará este abono contra futuras deudas.
        
        return mapToResponse(devolucionRepo.save(dev));
    }

    @Transactional(readOnly = true)
    public List<DevolucionProveedorResponse> listar(Integer sucursalId, LocalDate inicio, LocalDate fin) {
        return devolucionRepo.findBySucursalIdAndFechaBetweenOrderByFechaDesc(sucursalId, inicio, fin)
                .stream().map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DevolucionProveedorResponse obtenerPorId(Integer id) {
        return devolucionRepo.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Devolución no encontrada"));
    }

    // =====================================
    // MAPPERS
    // =====================================

    private DevolucionProveedorResponse mapToResponse(DevolucionProveedor d) {
        DevolucionProveedorResponse r = new DevolucionProveedorResponse();
        r.setId(d.getId());
        r.setSucursalId(d.getSucursalId());
        r.setUsuarioId(d.getUsuarioId());
        r.setFecha(d.getFecha());
        r.setMotivo(d.getMotivo());
        r.setEstatus(d.getEstatus());
        r.setTotal(d.getTotal());
        r.setCreatedAt(d.getCreatedAt());

        r.setProveedorId(d.getProveedor().getId());
        r.setProveedorNombre(d.getProveedor().getNombreEmpresa());

        r.setDetalles(d.getDetalles().stream().map(det -> {
            DevolucionProveedorDetalleDTO dto = new DevolucionProveedorDetalleDTO();
            dto.setId(det.getId());
            dto.setSkuInterno(det.getSkuInterno());
            dto.setCantidad(det.getCantidad());
            dto.setCostoUnitario(det.getCostoUnitario());
            dto.setSubtotal(det.getSubtotal());
            dto.setCreatedAt(det.getCreatedAt());
            return dto;
        }).collect(Collectors.toList()));

        return r;
    }
}
