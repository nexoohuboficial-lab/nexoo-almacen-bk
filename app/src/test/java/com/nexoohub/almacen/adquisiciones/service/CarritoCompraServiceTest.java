package com.nexoohub.almacen.adquisiciones.service;

import com.nexoohub.almacen.adquisiciones.dto.AgregarAlCarritoRequest;
import com.nexoohub.almacen.adquisiciones.dto.CarritoResumenResponse;
import com.nexoohub.almacen.adquisiciones.entity.CatalogoProveedorProducto;
import com.nexoohub.almacen.adquisiciones.entity.SesionCarritoCompra;
import com.nexoohub.almacen.adquisiciones.repository.CatalogoProveedorProductoRepository;
import com.nexoohub.almacen.adquisiciones.repository.SesionCarritoCompraRepository;
import com.nexoohub.almacen.catalogo.entity.Proveedor;
import com.nexoohub.almacen.common.exception.BusinessException;
import com.nexoohub.almacen.common.exception.ResourceNotFoundException;
import com.nexoohub.almacen.inventario.entity.ProductoMaestro;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CarritoCompraService - Tests Unitarios")
class CarritoCompraServiceTest {

    @Mock
    private SesionCarritoCompraRepository carritoRepository;

    @Mock
    private CatalogoProveedorProductoRepository catalogoRepository;

    @InjectMocks
    private CarritoCompraService carritoService;

    private CatalogoProveedorProducto catalogo;
    private Proveedor proveedor;
    private ProductoMaestro producto;

    @BeforeEach
    void setUp() {
        proveedor = new Proveedor();
        proveedor.setId(1);
        proveedor.setNombreEmpresa("Proveedor Test S.A.");

        producto = new ProductoMaestro();
        producto.setSkuInterno("SKU-TEST");
        producto.setNombreComercial("Producto Test");

        catalogo = new CatalogoProveedorProducto();
        catalogo.setId(10L);
        catalogo.setProveedor(proveedor);
        catalogo.setProducto(producto);
        catalogo.setProveedorCodigoProducto("PROV-SKU-TEST");
        catalogo.setPrecioCompraActual(new BigDecimal("100.00"));
        catalogo.setPrecioVentaSugeridoProveedor(new BigDecimal("150.00"));
        catalogo.setTiempoEntregaDias(3);
        catalogo.setDisponibilidad(true);
    }

    @Test
    @DisplayName("Debe agregar un producto nuevo al carrito")
    void testAgregarAlCarritoNuevo() {
        // Given
        AgregarAlCarritoRequest request = new AgregarAlCarritoRequest();
        request.setCatalogoId(10);
        request.setCantidad(5);

        when(catalogoRepository.findById(10L)).thenReturn(Optional.of(catalogo));
        when(carritoRepository.findByUsuarioIdAndCatalogoId(1, 10)).thenReturn(Optional.empty());

        // When
        carritoService.agregarAlCarrito(1, request);

        // Then
        verify(carritoRepository, times(1)).save(any(SesionCarritoCompra.class));
    }

    @Test
    @DisplayName("Debe incrementar cantidad si el producto ya existe en el carrito")
    void testAgregarAlCarritoIncrementarCantidad() {
        // Given
        AgregarAlCarritoRequest request = new AgregarAlCarritoRequest();
        request.setCatalogoId(10);
        request.setCantidad(3);

        SesionCarritoCompra sesionExistente = new SesionCarritoCompra();
        sesionExistente.setId(5);
        sesionExistente.setUsuarioId(1);
        sesionExistente.setCatalogo(catalogo);
        sesionExistente.setCantidad(2);

        when(catalogoRepository.findById(10L)).thenReturn(Optional.of(catalogo));
        when(carritoRepository.findByUsuarioIdAndCatalogoId(1, 10)).thenReturn(Optional.of(sesionExistente));

        // When
        carritoService.agregarAlCarrito(1, request);

        // Then
        assertEquals(5, sesionExistente.getCantidad(), "La cantidad debe incrementarse de 2 a 5");
        verify(carritoRepository, times(1)).save(sesionExistente);
    }

    @Test
    @DisplayName("Debe lanzar excepción si el producto no está disponible en catálogo")
    void testAgregarAlCarritoNoDisponible() {
        // Given
        AgregarAlCarritoRequest request = new AgregarAlCarritoRequest();
        request.setCatalogoId(10);
        request.setCantidad(1);
        catalogo.setDisponibilidad(false); // No disponible

        when(catalogoRepository.findById(10L)).thenReturn(Optional.of(catalogo));

        // When & Then
        assertThrows(BusinessException.class, () -> carritoService.agregarAlCarrito(1, request));
        verify(carritoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar excepción si el ID de catálogo no existe")
    void testAgregarAlCarritoIdInexistente() {
        // Given
        AgregarAlCarritoRequest request = new AgregarAlCarritoRequest();
        request.setCatalogoId(999);
        request.setCantidad(1);

        when(catalogoRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> carritoService.agregarAlCarrito(1, request));
    }

    @Test
    @DisplayName("Debe quitar un producto del carrito exitosamente")
    void testQuitarDelCarritoExitoso() {
        // Given
        SesionCarritoCompra sesion = new SesionCarritoCompra();
        when(carritoRepository.findByUsuarioIdAndCatalogoId(1, 10)).thenReturn(Optional.of(sesion));

        // When
        carritoService.quitarDelCarrito(1, 10);

        // Then
        verify(carritoRepository, times(1)).delete(sesion);
    }

    @Test
    @DisplayName("Debe lanzar excepción al intentar quitar un producto que no está en el carrito")
    void testQuitarDelCarritoInexistente() {
        // Given
        when(carritoRepository.findByUsuarioIdAndCatalogoId(1, 999)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> carritoService.quitarDelCarrito(1, 999));
        verify(carritoRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Debe retornar resumen vacío si el carrito está vacío")
    void testVerCarritoVacio() {
        // Given
        when(carritoRepository.findByUsuarioId(1)).thenReturn(Collections.emptyList());

        // When
        CarritoResumenResponse resumen = carritoService.verCarrito(1);

        // Then
        assertEquals(0, resumen.getTotalArticulos());
        assertEquals(BigDecimal.ZERO, resumen.getTotalEstimadoGlobal());
        assertTrue(resumen.getGruposPorProveedor().isEmpty());
    }

    @Test
    @DisplayName("Debe agrupar por proveedor y calcular totales al ver el carrito")
    void testVerCarritoConItems() {
        // Given
        SesionCarritoCompra item = new SesionCarritoCompra();
        item.setId(1);
        item.setUsuarioId(1);
        item.setCatalogo(catalogo);
        item.setSkuInterno("SKU-TEST");
        item.setProveedor(proveedor);
        item.setCantidad(3);

        when(carritoRepository.findByUsuarioId(1)).thenReturn(List.of(item));

        // When
        CarritoResumenResponse resumen = carritoService.verCarrito(1);

        // Then
        assertEquals(3, resumen.getTotalArticulos());
        assertEquals(new BigDecimal("300.00"), resumen.getTotalEstimadoGlobal());
        assertEquals(1, resumen.getGruposPorProveedor().size());
        assertEquals("Proveedor Test S.A.", resumen.getGruposPorProveedor().get(0).getNombreProveedor());
    }

    @Test
    @DisplayName("Debe vaciar el carrito completamente")
    void testVaciarCarrito() {
        // When
        carritoService.vaciarCarrito(1);

        // Then
        verify(carritoRepository, times(1)).deleteByUsuarioId(1);
    }
}
