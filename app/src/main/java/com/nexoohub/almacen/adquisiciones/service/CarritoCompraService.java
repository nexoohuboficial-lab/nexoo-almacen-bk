package com.nexoohub.almacen.adquisiciones.service;

import com.nexoohub.almacen.adquisiciones.dto.AgregarAlCarritoRequest;
import com.nexoohub.almacen.adquisiciones.dto.CarritoResumenResponse;
import com.nexoohub.almacen.adquisiciones.dto.CarritoResumenResponse.GrupoProveedorCarritoDTO;
import com.nexoohub.almacen.adquisiciones.dto.CarritoResumenResponse.ItemCarritoDTO;
import com.nexoohub.almacen.adquisiciones.entity.CatalogoProveedorProducto;
import com.nexoohub.almacen.adquisiciones.entity.SesionCarritoCompra;
import com.nexoohub.almacen.adquisiciones.repository.CatalogoProveedorProductoRepository;
import com.nexoohub.almacen.adquisiciones.repository.SesionCarritoCompraRepository;
import com.nexoohub.almacen.common.exception.BusinessException;
import com.nexoohub.almacen.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarritoCompraService {

    private final SesionCarritoCompraRepository carritoRepository;
    private final CatalogoProveedorProductoRepository catalogoRepository;

    @Transactional
    public void agregarAlCarrito(Integer usuarioId, AgregarAlCarritoRequest request) {
        CatalogoProveedorProducto catalogo = catalogoRepository.findById(Long.valueOf(request.getCatalogoId()))
                .orElseThrow(() -> new ResourceNotFoundException("CatalogoProveedorProducto", request.getCatalogoId()));

        if (!catalogo.getDisponibilidad()) {
            throw new BusinessException("El artículo no está disponible en el catálogo del proveedor");
        }

        // Buscar si ya existe en el carrito
        SesionCarritoCompra sesion = carritoRepository.findByUsuarioIdAndCatalogoId(usuarioId, request.getCatalogoId())
                .orElse(new SesionCarritoCompra());

        if (sesion.getId() == null) {
            sesion.setUsuarioId(usuarioId);
            sesion.setCatalogo(catalogo);
            sesion.setSkuInterno(catalogo.getProducto().getSkuInterno());
            sesion.setProveedor(catalogo.getProveedor());
            sesion.setCantidad(request.getCantidad());
        } else {
            sesion.setCantidad(sesion.getCantidad() + request.getCantidad());
        }

        carritoRepository.save(sesion);
    }

    @Transactional
    public void quitarDelCarrito(Integer usuarioId, Integer catalogoId) {
        SesionCarritoCompra sesion = carritoRepository.findByUsuarioIdAndCatalogoId(usuarioId, catalogoId)
                .orElseThrow(() -> new ResourceNotFoundException("SesionCarritoCompra (catalogoId)", catalogoId));
        carritoRepository.delete(sesion);
    }

    @Transactional(readOnly = true)
    public CarritoResumenResponse verCarrito(Integer usuarioId) {
        List<SesionCarritoCompra> items = carritoRepository.findByUsuarioId(usuarioId);

        if (items.isEmpty()) {
            return CarritoResumenResponse.builder()
                    .totalArticulos(0)
                    .totalEstimadoGlobal(BigDecimal.ZERO)
                    .gruposPorProveedor(List.of())
                    .build();
        }

        // Agrupar por proveedor
        Map<Integer, List<SesionCarritoCompra>> agrupado = items.stream()
                .collect(Collectors.groupingBy(item -> item.getProveedor().getId()));

        BigDecimal totalGlobal = BigDecimal.ZERO;
        Integer totalArticulos = 0;

        List<GrupoProveedorCarritoDTO> grupos = agrupado.entrySet().stream().map(entry -> {
            Integer proveedorId = entry.getKey();
            List<SesionCarritoCompra> compras = entry.getValue();
            String nombreProveedor = compras.get(0).getProveedor().getNombreEmpresa();

            BigDecimal subtotalProveedor = BigDecimal.ZERO;

            List<ItemCarritoDTO> itemDTOs = new ArrayList<>();
            for (SesionCarritoCompra c : compras) {
                CatalogoProveedorProducto catalogo = c.getCatalogo();
                BigDecimal cantidad = new BigDecimal(c.getCantidad());
                BigDecimal subtotalItem = catalogo.getPrecioCompraActual().multiply(cantidad);

                ItemCarritoDTO dto = ItemCarritoDTO.builder()
                        .sesionId(c.getId())
                        .catalogoId(catalogo.getId().intValue())
                        .skuInterno(c.getSkuInterno())
                        .skuProveedor(catalogo.getProveedorCodigoProducto())
                        .nombreProducto(catalogo.getProducto().getNombreComercial())
                        .cantidad(c.getCantidad())
                        .precioCostoUnitario(catalogo.getPrecioCompraActual())
                        .subtotal(subtotalItem)
                        .precioVentaSugerido(catalogo.getPrecioVentaSugeridoProveedor())
                        .diasEntregaEstimado(catalogo.getTiempoEntregaDias())
                        .build();
                itemDTOs.add(dto);
            }

            // Sumar al proveedor
            for (ItemCarritoDTO dto : itemDTOs) {
                subtotalProveedor = subtotalProveedor.add(dto.getSubtotal());
            }

            return GrupoProveedorCarritoDTO.builder()
                    .proveedorId(proveedorId)
                    .nombreProveedor(nombreProveedor)
                    .subtotalProveedor(subtotalProveedor)
                    .items(itemDTOs)
                    .build();
        }).collect(Collectors.toList());

        // Calcular globales
        for (GrupoProveedorCarritoDTO g : grupos) {
            totalGlobal = totalGlobal.add(g.getSubtotalProveedor());
            for (ItemCarritoDTO i : g.getItems()) {
                totalArticulos += i.getCantidad();
            }
        }

        return CarritoResumenResponse.builder()
                .totalArticulos(totalArticulos)
                .totalEstimadoGlobal(totalGlobal)
                .gruposPorProveedor(grupos)
                .build();
    }

    @Transactional
    public void vaciarCarrito(Integer usuarioId) {
        carritoRepository.deleteByUsuarioId(usuarioId);
    }
}
