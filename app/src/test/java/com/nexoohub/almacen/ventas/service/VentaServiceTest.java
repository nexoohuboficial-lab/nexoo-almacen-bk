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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import com.nexoohub.almacen.common.exception.ResourceNotFoundException;
import com.nexoohub.almacen.common.exception.StockInsuficienteException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("VentaService - Tests de Procesamiento de Ventas")
class VentaServiceTest {

    @Mock
    private VentaRepository ventaRepository;

    @Mock
    private DetalleVentaRepository detalleVentaRepository;

    @Mock
    private InventarioSucursalRepository inventarioRepository;

    @Mock
    private HistorialPrecioRepository historialPrecioRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private PrecioEspecialRepository precioEspecialRepository;

    @InjectMocks
    private VentaService ventaService;

    private Usuario vendedor;
    private Cliente clientePublico;
    private Cliente clienteTaller;
    private InventarioSucursal inventario;
    private HistorialPrecio historialPrecio;

    @BeforeEach
    void setUp() {
        vendedor = new Usuario();
        vendedor.setId(1L);
        vendedor.setUsername("vendedor1");

        clientePublico = new Cliente();
        clientePublico.setId(1);
        clientePublico.setTipoClienteId(1); // Cliente público

        clienteTaller = new Cliente();
        clienteTaller.setId(2);
        clienteTaller.setTipoClienteId(2); // Cliente taller

        inventario = new InventarioSucursal();
        inventario.setId(new InventarioSucursalId(1, "SKU001"));
        inventario.setStockActual(20);
        inventario.setCostoPromedioPonderado(new BigDecimal("100.00"));

        historialPrecio = new HistorialPrecio();
        historialPrecio.setSkuInterno("SKU001");
        historialPrecio.setPrecioFinalPublico(new BigDecimal("200.00"));
    }

    @Test
    @DisplayName("Debe procesar venta exitosamente con precio público")
    void testProcesarVentaExitosa() {
        // Given
        VentaRequestDTO request = crearRequestVenta(1, 5);
        Venta ventaMock = new Venta();
        ventaMock.setId(1);

        when(usuarioRepository.findByUsername("vendedor1")).thenReturn(Optional.of(vendedor));
        when(clienteRepository.findById(1)).thenReturn(Optional.of(clientePublico));
        when(ventaRepository.save(any(Venta.class))).thenReturn(ventaMock);
        when(inventarioRepository.findById(any(InventarioSucursalId.class)))
                .thenReturn(Optional.of(inventario));
        when(historialPrecioRepository.findTopBySkuInternoOrderByFechaCalculoDesc("SKU001"))
                .thenReturn(Optional.of(historialPrecio));
        when(precioEspecialRepository.findBySkuInternoAndTipoClienteId("SKU001", 1))
                .thenReturn(Optional.empty());
        when(detalleVentaRepository.save(any(DetalleVenta.class))).thenReturn(new DetalleVenta());

        // When
        Venta resultado = ventaService.procesarVenta(request, "vendedor1");

        // Then
        assertNotNull(resultado, "La venta no debe ser null");
        verify(inventarioRepository, times(1)).save(any(InventarioSucursal.class));
        verify(detalleVentaRepository, times(1)).save(any(DetalleVenta.class));
        verify(ventaRepository, times(2)).save(any(Venta.class));
    }

    @Test
    @DisplayName("Debe aplicar precio especial para cliente taller")
    void testAplicarPrecioEspecialTaller() {
        // Given
        VentaRequestDTO request = crearRequestVenta(2, 3);
        Venta ventaMock = new Venta();
        ventaMock.setId(1);

        PrecioEspecial precioEspecial = new PrecioEspecial();
        precioEspecial.setSkuInterno("SKU001");
        precioEspecial.setTipoClienteId(2);
        precioEspecial.setPrecioFijo(new BigDecimal("150.00"));

        when(usuarioRepository.findByUsername("vendedor1")).thenReturn(Optional.of(vendedor));
        when(clienteRepository.findById(2)).thenReturn(Optional.of(clienteTaller));
        when(ventaRepository.save(any(Venta.class))).thenReturn(ventaMock);
        when(inventarioRepository.findById(any(InventarioSucursalId.class)))
                .thenReturn(Optional.of(inventario));
        when(historialPrecioRepository.findTopBySkuInternoOrderByFechaCalculoDesc("SKU001"))
                .thenReturn(Optional.of(historialPrecio));
        when(precioEspecialRepository.findBySkuInternoAndTipoClienteId("SKU001", 2))
                .thenReturn(Optional.of(precioEspecial));
        when(detalleVentaRepository.save(any(DetalleVenta.class))).thenReturn(new DetalleVenta());

        // When
        ventaService.procesarVenta(request, "vendedor1");

        // Then
        ArgumentCaptor<DetalleVenta> detalleCaptor = ArgumentCaptor.forClass(DetalleVenta.class);
        verify(detalleVentaRepository).save(detalleCaptor.capture());
        
        DetalleVenta detalle = detalleCaptor.getValue();
        assertEquals(new BigDecimal("150.00"), detalle.getPrecioUnitarioVenta(),
                "Debe aplicar el precio especial de taller");
    }

    @Test
    @DisplayName("Debe decrementar stock correctamente al procesar venta")
    void testDecrementarStock() {
        // Given
        VentaRequestDTO request = crearRequestVenta(1, 5);
        Venta ventaMock = new Venta();
        ventaMock.setId(1);

        when(usuarioRepository.findByUsername("vendedor1")).thenReturn(Optional.of(vendedor));
        when(clienteRepository.findById(1)).thenReturn(Optional.of(clientePublico));
        when(ventaRepository.save(any(Venta.class))).thenReturn(ventaMock);
        when(inventarioRepository.findById(any(InventarioSucursalId.class)))
                .thenReturn(Optional.of(inventario));
        when(historialPrecioRepository.findTopBySkuInternoOrderByFechaCalculoDesc("SKU001"))
                .thenReturn(Optional.of(historialPrecio));
        when(precioEspecialRepository.findBySkuInternoAndTipoClienteId("SKU001", 1))
                .thenReturn(Optional.empty());
        when(detalleVentaRepository.save(any(DetalleVenta.class))).thenReturn(new DetalleVenta());

        // When
        ventaService.procesarVenta(request, "vendedor1");

        // Then
        ArgumentCaptor<InventarioSucursal> inventarioCaptor = ArgumentCaptor.forClass(InventarioSucursal.class);
        verify(inventarioRepository).save(inventarioCaptor.capture());
        
        InventarioSucursal inventarioActualizado = inventarioCaptor.getValue();
        assertEquals(15, inventarioActualizado.getStockActual(), "Stock debe ser 20 - 5 = 15");
    }

    @Test
    @DisplayName("Debe calcular total de venta correctamente")
    void testCalculoTotalVenta() {
        // Given
        VentaRequestDTO request = crearRequestVenta(1, 5);
        Venta ventaMock = new Venta();
        ventaMock.setId(1);

        when(usuarioRepository.findByUsername("vendedor1")).thenReturn(Optional.of(vendedor));
        when(clienteRepository.findById(1)).thenReturn(Optional.of(clientePublico));
        when(ventaRepository.save(any(Venta.class))).thenReturn(ventaMock);
        when(inventarioRepository.findById(any(InventarioSucursalId.class)))
                .thenReturn(Optional.of(inventario));
        when(historialPrecioRepository.findTopBySkuInternoOrderByFechaCalculoDesc("SKU001"))
                .thenReturn(Optional.of(historialPrecio));
        when(precioEspecialRepository.findBySkuInternoAndTipoClienteId("SKU001", 1))
                .thenReturn(Optional.empty());
        when(detalleVentaRepository.save(any(DetalleVenta.class))).thenReturn(new DetalleVenta());

        // When
        ventaService.procesarVenta(request, "vendedor1");

        // Then
        ArgumentCaptor<Venta> ventaCaptor = ArgumentCaptor.forClass(Venta.class);
        verify(ventaRepository, times(2)).save(ventaCaptor.capture());
        
        Venta ventaFinal = ventaCaptor.getAllValues().get(1);
        assertEquals(new BigDecimal("1000.00"), ventaFinal.getTotal(),
                "Total debe ser 200.00 * 5 = 1000.00");
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el vendedor no existe")
    void testErrorVendedorNoEncontrado() {
        // Given
        VentaRequestDTO request = crearRequestVenta(1, 5);
        when(usuarioRepository.findByUsername("vendedor1")).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> ventaService.procesarVenta(request, "vendedor1"));
        
        assertTrue(exception.getMessage().contains("Usuario"));
        verify(ventaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el cliente no existe")
    void testErrorClienteNoEncontrado() {
        // Given
        VentaRequestDTO request = crearRequestVenta(99, 5);
        when(usuarioRepository.findByUsername("vendedor1")).thenReturn(Optional.of(vendedor));
        when(clienteRepository.findById(99)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> ventaService.procesarVenta(request, "vendedor1"));
        
        assertTrue(exception.getMessage().contains("Cliente"));
        verify(ventaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el producto no existe en sucursal")
    void testErrorProductoNoExisteEnSucursal() {
        // Given
        VentaRequestDTO request = crearRequestVenta(1, 5);
        Venta ventaMock = new Venta();
        ventaMock.setId(1);

        when(usuarioRepository.findByUsername("vendedor1")).thenReturn(Optional.of(vendedor));
        when(clienteRepository.findById(1)).thenReturn(Optional.of(clientePublico));
        when(ventaRepository.save(any(Venta.class))).thenReturn(ventaMock);
        when(inventarioRepository.findById(any(InventarioSucursalId.class)))
                .thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> ventaService.procesarVenta(request, "vendedor1"));
        
        assertTrue(exception.getMessage().contains("Producto"));
        verify(detalleVentaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando hay stock insuficiente")
    void testErrorStockInsuficiente() {
        // Given
        VentaRequestDTO request = crearRequestVenta(1, 50); // Pedir más de lo disponible
        Venta ventaMock = new Venta();
        ventaMock.setId(1);

        when(usuarioRepository.findByUsername("vendedor1")).thenReturn(Optional.of(vendedor));
        when(clienteRepository.findById(1)).thenReturn(Optional.of(clientePublico));
        when(ventaRepository.save(any(Venta.class))).thenReturn(ventaMock);
        when(inventarioRepository.findById(any(InventarioSucursalId.class)))
                .thenReturn(Optional.of(inventario));

        // When & Then
        StockInsuficienteException exception = assertThrows(StockInsuficienteException.class,
                () -> ventaService.procesarVenta(request, "vendedor1"));
        
        assertTrue(exception.getMessage().contains("Stock insuficiente"));
        verify(detalleVentaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el producto no tiene precio configurado")
    void testErrorSinPrecioConfigurado() {
        // Given
        VentaRequestDTO request = crearRequestVenta(1, 5);
        Venta ventaMock = new Venta();
        ventaMock.setId(1);

        when(usuarioRepository.findByUsername("vendedor1")).thenReturn(Optional.of(vendedor));
        when(clienteRepository.findById(1)).thenReturn(Optional.of(clientePublico));
        when(ventaRepository.save(any(Venta.class))).thenReturn(ventaMock);
        when(inventarioRepository.findById(any(InventarioSucursalId.class)))
                .thenReturn(Optional.of(inventario));
        when(historialPrecioRepository.findTopBySkuInternoOrderByFechaCalculoDesc("SKU001"))
                .thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> ventaService.procesarVenta(request, "vendedor1"));
        
        assertTrue(exception.getMessage().contains("precio"));
        verify(detalleVentaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe manejar venta con múltiples items correctamente")
    void testVentaConMultiplesItems() {
        // Given
        VentaRequestDTO request = crearRequestVentaMultiple();
        Venta ventaMock = new Venta();
        ventaMock.setId(1);

        InventarioSucursal inventario2 = new InventarioSucursal();
        inventario2.setId(new InventarioSucursalId(1, "SKU002"));
        inventario2.setStockActual(15);
        inventario2.setCostoPromedioPonderado(new BigDecimal("80.00"));

        HistorialPrecio historialPrecio2 = new HistorialPrecio();
        historialPrecio2.setSkuInterno("SKU002");
        historialPrecio2.setPrecioFinalPublico(new BigDecimal("150.00"));

        when(usuarioRepository.findByUsername("vendedor1")).thenReturn(Optional.of(vendedor));
        when(clienteRepository.findById(1)).thenReturn(Optional.of(clientePublico));
        when(ventaRepository.save(any(Venta.class))).thenReturn(ventaMock);
        
        when(inventarioRepository.findById(new InventarioSucursalId(1, "SKU001")))
                .thenReturn(Optional.of(inventario));
        when(inventarioRepository.findById(new InventarioSucursalId(1, "SKU002")))
                .thenReturn(Optional.of(inventario2));
        
        when(historialPrecioRepository.findTopBySkuInternoOrderByFechaCalculoDesc("SKU001"))
                .thenReturn(Optional.of(historialPrecio));
        when(historialPrecioRepository.findTopBySkuInternoOrderByFechaCalculoDesc("SKU002"))
                .thenReturn(Optional.of(historialPrecio2));
        
        when(precioEspecialRepository.findBySkuInternoAndTipoClienteId(anyString(), anyInt()))
                .thenReturn(Optional.empty());
        when(detalleVentaRepository.save(any(DetalleVenta.class))).thenReturn(new DetalleVenta());

        // When
        ventaService.procesarVenta(request, "vendedor1");

        // Then
        verify(inventarioRepository, times(2)).save(any(InventarioSucursal.class));
        verify(detalleVentaRepository, times(2)).save(any(DetalleVenta.class));
    }

    @Test
    @DisplayName("Debe manejar lista vacía de items sin errores")
    void testListaItemsVacia() {
        // Given
        VentaRequestDTO request = new VentaRequestDTO();
        request.setClienteId(1);
        request.setSucursalId(1);
        request.setMetodoPago("EFECTIVO");
        request.setItems(Collections.emptyList());
        
        Venta ventaMock = new Venta();
        ventaMock.setId(1);

        when(usuarioRepository.findByUsername("vendedor1")).thenReturn(Optional.of(vendedor));
        when(clienteRepository.findById(1)).thenReturn(Optional.of(clientePublico));
        when(ventaRepository.save(any(Venta.class))).thenReturn(ventaMock);

        // When
        Venta resultado = ventaService.procesarVenta(request, "vendedor1");

        // Then
        assertNotNull(resultado);
        verify(detalleVentaRepository, never()).save(any());
        verify(inventarioRepository, never()).save(any());
    }

    // ========== MÉTODOS AUXILIARES ==========

    private VentaRequestDTO crearRequestVenta(Integer clienteId, Integer cantidad) {
        VentaRequestDTO request = new VentaRequestDTO();
        request.setClienteId(clienteId);
        request.setSucursalId(1);
        request.setMetodoPago("EFECTIVO");

        VentaRequestDTO.ItemVentaDTO item = new VentaRequestDTO.ItemVentaDTO();
        item.setSkuInterno("SKU001");
        item.setCantidad(cantidad);

        request.setItems(List.of(item));
        return request;
    }

    private VentaRequestDTO crearRequestVentaMultiple() {
        VentaRequestDTO request = new VentaRequestDTO();
        request.setClienteId(1);
        request.setSucursalId(1);
        request.setMetodoPago("EFECTIVO");

        VentaRequestDTO.ItemVentaDTO item1 = new VentaRequestDTO.ItemVentaDTO();
        item1.setSkuInterno("SKU001");
        item1.setCantidad(3);

        VentaRequestDTO.ItemVentaDTO item2 = new VentaRequestDTO.ItemVentaDTO();
        item2.setSkuInterno("SKU002");
        item2.setCantidad(2);

        request.setItems(List.of(item1, item2));
        return request;
    }
}
