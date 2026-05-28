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
import com.nexoohub.almacen.compras.entity.Compra;
import com.nexoohub.almacen.compras.service.CompraService;
import com.nexoohub.almacen.empleados.entity.Empleado;
import com.nexoohub.almacen.empleados.repository.EmpleadoRepository;
import com.nexoohub.almacen.sucursal.entity.Sucursal;
import com.nexoohub.almacen.sucursal.repository.SucursalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrdenCompraService - Tests Unitarios")
class OrdenCompraServiceTest {

    @Mock
    private OrdenCompraProveedorRepository ordenRepository;

    @Mock
    private CarritoCompraService carritoService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private EmpleadoRepository empleadoRepository;

    @Mock
    private SucursalRepository sucursalRepository;

    @Mock
    private ProveedorRepository proveedorRepository;

    @Mock
    private CompraService compraService;

    @InjectMocks
    private OrdenCompraService ordenCompraService;

    private Usuario usuario;
    private Empleado empleado;
    private Sucursal sucursal;
    private Proveedor proveedor;
    private CarritoResumenResponse carritoResumen;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(10L);
        usuario.setUsername("admin");
        usuario.setEmpleadoId(100);

        empleado = new Empleado();
        empleado.setId(100);
        empleado.setSucursalId(1);

        sucursal = new Sucursal();
        sucursal.setId(1);
        sucursal.setNombre("Sucursal Principal");

        proveedor = new Proveedor();
        proveedor.setId(2);
        proveedor.setNombreEmpresa("Proveedor Mayorista");
        proveedor.setRfc("PROV123456");

        ItemCarritoDTO item = ItemCarritoDTO.builder()
                .catalogoId(5)
                .skuInterno("SKU-TEST")
                .skuProveedor("PROV-SKU")
                .nombreProducto("Llanta Test")
                .cantidad(2)
                .precioCostoUnitario(new BigDecimal("100.00"))
                .subtotal(new BigDecimal("200.00"))
                .precioVentaSugerido(new BigDecimal("150.00"))
                .build();

        GrupoProveedorCarritoDTO grupo = GrupoProveedorCarritoDTO.builder()
                .proveedorId(2)
                .nombreProveedor("Proveedor Mayorista")
                .subtotalProveedor(new BigDecimal("200.00"))
                .items(List.of(item))
                .build();

        carritoResumen = CarritoResumenResponse.builder()
                .totalArticulos(2)
                .totalEstimadoGlobal(new BigDecimal("200.00"))
                .gruposPorProveedor(List.of(grupo))
                .build();
    }

    @Test
    @DisplayName("Debe generar órdenes de compra exitosamente desde el carrito")
    void testGenerarOrdenesDeCompraExitoso() {
        // Given
        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(usuario));
        when(empleadoRepository.findById(100)).thenReturn(Optional.of(empleado));
        when(sucursalRepository.findById(1)).thenReturn(Optional.of(sucursal));
        when(carritoService.verCarrito(10)).thenReturn(carritoResumen);
        when(proveedorRepository.findById(2)).thenReturn(Optional.of(proveedor));
        when(ordenRepository.countByFolioPrefix(anyString())).thenReturn(5L);

        // When
        List<OrdenCompraResponse> responses = ordenCompraService.generarOrdenesDeCompra("admin");

        // Then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("Proveedor Mayorista", responses.get(0).getNombreProveedor());
        assertEquals(new BigDecimal("200.00"), responses.get(0).getTotalEstimado());
        verify(ordenRepository, times(1)).save(any(OrdenCompraProveedor.class));
        verify(carritoService, times(1)).vaciarCarrito(10);
    }

    @Test
    @DisplayName("Debe lanzar excepción al generar órdenes si el carrito está vacío")
    void testGenerarOrdenesDeCompraCarritoVacio() {
        // Given
        carritoResumen = CarritoResumenResponse.builder()
                .totalArticulos(0)
                .totalEstimadoGlobal(BigDecimal.ZERO)
                .gruposPorProveedor(Collections.emptyList())
                .build();

        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(usuario));
        when(empleadoRepository.findById(100)).thenReturn(Optional.of(empleado));
        when(sucursalRepository.findById(1)).thenReturn(Optional.of(sucursal));
        when(carritoService.verCarrito(10)).thenReturn(carritoResumen);

        // When & Then
        assertThrows(BusinessException.class, () -> ordenCompraService.generarOrdenesDeCompra("admin"));
        verify(ordenRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe actualizar el estado de una orden de compra")
    void testActualizarEstado() {
        // Given
        OrdenCompraProveedor oc = new OrdenCompraProveedor();
        oc.setId(1);
        oc.setFolio("OC-2026-0006");
        oc.setEstado("BORRADOR");
        oc.setProveedor(proveedor);
        oc.setSucursal(sucursal);

        when(ordenRepository.findById(1)).thenReturn(Optional.of(oc));

        // When
        OrdenCompraResponse response = ordenCompraService.actualizarEstado(1, "ENVIADA", "admin");

        // Then
        assertNotNull(response);
        assertEquals("ENVIADA", response.getEstado());
        verify(ordenRepository, times(1)).save(oc);
    }

    @Test
    @DisplayName("Debe recibir una orden de compra e ingresar mercancía")
    void testRecibirOrdenCompra() {
        // Given
        OrdenCompraProveedor oc = new OrdenCompraProveedor();
        oc.setId(1);
        oc.setFolio("OC-2026-0006");
        oc.setEstado("ENVIADA");
        oc.setProveedor(proveedor);
        oc.setSucursal(sucursal);
        oc.setDetalles(new ArrayList<>());

        DetalleOrdenCompra det = new DetalleOrdenCompra();
        det.setSkuInterno("SKU-TEST");
        det.setCantidad(5);
        det.setPrecioCostoUnitario(new BigDecimal("100.00"));
        det.setPrecioVentaSugerido(new BigDecimal("150.00"));
        oc.getDetalles().add(det);

        when(ordenRepository.findById(1)).thenReturn(Optional.of(oc));
        when(compraService.procesarIngresoMercancia(any(), anyString())).thenReturn(new Compra());

        // When
        OrdenCompraResponse response = ordenCompraService.recibirOrdenCompra(1, "admin");

        // Then
        assertNotNull(response);
        assertEquals("RECIBIDA", response.getEstado());
        verify(compraService, times(1)).procesarIngresoMercancia(any(), eq("admin"));
        verify(ordenRepository, times(1)).save(oc);
    }

    @Test
    @DisplayName("Debe lanzar excepción al recibir orden si ya está recibida")
    void testRecibirOrdenCompraYaRecibida() {
        // Given
        OrdenCompraProveedor oc = new OrdenCompraProveedor();
        oc.setId(1);
        oc.setEstado("RECIBIDA");

        when(ordenRepository.findById(1)).thenReturn(Optional.of(oc));

        // When & Then
        assertThrows(BusinessException.class, () -> ordenCompraService.recibirOrdenCompra(1, "admin"));
        verify(compraService, never()).procesarIngresoMercancia(any(), anyString());
    }
}
