package com.nexoohub.almacen.compras.service;

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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CompraService - Tests de Procesamiento de Compras")
class CompraServiceTest {

    @Mock
    private CompraRepository compraRepository;

    @Mock
    private DetalleCompraRepository detalleCompraRepository;

    @Mock
    private InventarioSucursalRepository inventarioRepository;

    @Mock
    private ProductoMaestroRepository productoRepository;

    @Mock
    private ConfiguracionFinancieraRepository finanzasRepository;

    @Mock
    private HistorialPrecioRepository historialPrecioRepository;

    @InjectMocks
    private CompraService compraService;

    private ConfiguracionFinanciera configuracion;
    private ProductoMaestro productoMaestro;
    private InventarioSucursal inventarioExistente;

    @BeforeEach
    void setUp() {
        // Configuración financiera base
        configuracion = new ConfiguracionFinanciera();
        configuracion.setId(1);
        configuracion.setIva(new BigDecimal("0.16"));
        configuracion.setMargenGananciaBase(new BigDecimal("0.30"));

        // Producto maestro con sensibilidad media
        productoMaestro = new ProductoMaestro();
        productoMaestro.setSkuInterno("SKU001");
        productoMaestro.setSensibilidadPrecio("MEDIA");

        // Inventario existente
        inventarioExistente = new InventarioSucursal();
        inventarioExistente.setId(new InventarioSucursalId(1, "SKU001"));
        inventarioExistente.setStockActual(10);
        inventarioExistente.setCostoPromedioPonderado(new BigDecimal("100.00"));
    }

    @Test
    @DisplayName("Debe procesar compra exitosamente con precios sin IVA")
    void testProcesarCompraExitosaSinIva() {
        // Given
        CompraRequestDTO request = crearRequestCompra(false);
        Compra compraMock = new Compra();
        compraMock.setId(1);

        when(finanzasRepository.findById(1)).thenReturn(Optional.of(configuracion));
        when(compraRepository.save(any(Compra.class))).thenReturn(compraMock);
        when(inventarioRepository.findById(any(InventarioSucursalId.class)))
                .thenReturn(Optional.of(inventarioExistente));
        when(productoRepository.findById("SKU001")).thenReturn(Optional.of(productoMaestro));
        when(detalleCompraRepository.save(any(DetalleCompra.class))).thenReturn(new DetalleCompra());
        when(historialPrecioRepository.save(any(HistorialPrecio.class))).thenReturn(new HistorialPrecio());

        // When
        Compra resultado = compraService.procesarIngresoMercancia(request, "admin");

        // Then
        assertNotNull(resultado, "La compra procesada no debe ser null");
        verify(finanzasRepository, times(1)).findById(1);
        verify(compraRepository, times(2)).save(any(Compra.class));
        verify(inventarioRepository, times(1)).save(any(InventarioSucursal.class));
        verify(detalleCompraRepository, times(1)).save(any(DetalleCompra.class));
        verify(historialPrecioRepository, times(1)).save(any(HistorialPrecio.class));
    }

    @Test
    @DisplayName("Debe procesar compra con precios que incluyen IVA y limpiarlos correctamente")
    void testProcesarCompraConLimpiezaIva() {
        // Given
        CompraRequestDTO request = crearRequestCompra(true);
        Compra compraMock = new Compra();
        compraMock.setId(1);

        when(finanzasRepository.findById(1)).thenReturn(Optional.of(configuracion));
        when(compraRepository.save(any(Compra.class))).thenReturn(compraMock);
        when(inventarioRepository.findById(any(InventarioSucursalId.class)))
                .thenReturn(Optional.of(inventarioExistente));
        when(productoRepository.findById("SKU001")).thenReturn(Optional.of(productoMaestro));
        when(detalleCompraRepository.save(any(DetalleCompra.class))).thenReturn(new DetalleCompra());
        when(historialPrecioRepository.save(any(HistorialPrecio.class))).thenReturn(new HistorialPrecio());

        // When
        Compra resultado = compraService.procesarIngresoMercancia(request, "admin");

        // Then
        assertNotNull(resultado);
        ArgumentCaptor<DetalleCompra> detalleCaptor = ArgumentCaptor.forClass(DetalleCompra.class);
        verify(detalleCompraRepository).save(detalleCaptor.capture());
        
        DetalleCompra detalleGuardado = detalleCaptor.getValue();
        assertTrue(detalleGuardado.getCostoUnitarioCompra().compareTo(new BigDecimal("116.00")) < 0,
                "El costo debe estar limpio de IVA (menor a 116)");
    }

    @Test
    @DisplayName("Debe calcular CPP correctamente con inventario existente")
    void testCalculoCostoPromedioPonderado() {
        // Given
        CompraRequestDTO request = crearRequestCompra(false);
        Compra compraMock = new Compra();
        compraMock.setId(1);

        when(finanzasRepository.findById(1)).thenReturn(Optional.of(configuracion));
        when(compraRepository.save(any(Compra.class))).thenReturn(compraMock);
        when(inventarioRepository.findById(any(InventarioSucursalId.class)))
                .thenReturn(Optional.of(inventarioExistente));
        when(productoRepository.findById("SKU001")).thenReturn(Optional.of(productoMaestro));
        when(detalleCompraRepository.save(any(DetalleCompra.class))).thenReturn(new DetalleCompra());
        when(historialPrecioRepository.save(any(HistorialPrecio.class))).thenReturn(new HistorialPrecio());

        // When
        compraService.procesarIngresoMercancia(request, "admin");

        // Then
        ArgumentCaptor<InventarioSucursal> inventarioCaptor = ArgumentCaptor.forClass(InventarioSucursal.class);
        verify(inventarioRepository).save(inventarioCaptor.capture());
        
        InventarioSucursal inventarioActualizado = inventarioCaptor.getValue();
        assertEquals(15, inventarioActualizado.getStockActual(), "Stock debe ser 10 + 5 = 15");
        assertNotNull(inventarioActualizado.getCostoPromedioPonderado());
        assertTrue(inventarioActualizado.getCostoPromedioPonderado().compareTo(BigDecimal.ZERO) > 0,
                "El CPP debe ser mayor a cero");
    }

    @Test
    @DisplayName("Debe crear inventario nuevo si no existe en la sucursal")
    void testCrearInventarioNuevo() {
        // Given
        CompraRequestDTO request = crearRequestCompra(false);
        Compra compraMock = new Compra();
        compraMock.setId(1);

        when(finanzasRepository.findById(1)).thenReturn(Optional.of(configuracion));
        when(compraRepository.save(any(Compra.class))).thenReturn(compraMock);
        when(inventarioRepository.findById(any(InventarioSucursalId.class)))
                .thenReturn(Optional.empty()); // No existe inventario
        when(productoRepository.findById("SKU001")).thenReturn(Optional.of(productoMaestro));
        when(detalleCompraRepository.save(any(DetalleCompra.class))).thenReturn(new DetalleCompra());
        when(historialPrecioRepository.save(any(HistorialPrecio.class))).thenReturn(new HistorialPrecio());

        // When
        compraService.procesarIngresoMercancia(request, "admin");

        // Then
        ArgumentCaptor<InventarioSucursal> inventarioCaptor = ArgumentCaptor.forClass(InventarioSucursal.class);
        verify(inventarioRepository).save(inventarioCaptor.capture());
        
        InventarioSucursal nuevoInventario = inventarioCaptor.getValue();
        assertEquals(5, nuevoInventario.getStockActual(), "El stock inicial debe ser 5");
    }

    @Test
    @DisplayName("Debe aplicar estrategia de sensibilidad BAJA correctamente")
    void testEstrategiaSensibilidadBaja() {
        // Given
        productoMaestro.setSensibilidadPrecio("BAJA");
        CompraRequestDTO request = crearRequestConPrecioProveedor();
        Compra compraMock = new Compra();
        compraMock.setId(1);

        when(finanzasRepository.findById(1)).thenReturn(Optional.of(configuracion));
        when(compraRepository.save(any(Compra.class))).thenReturn(compraMock);
        when(inventarioRepository.findById(any(InventarioSucursalId.class)))
                .thenReturn(Optional.of(inventarioExistente));
        when(productoRepository.findById("SKU001")).thenReturn(Optional.of(productoMaestro));
        when(detalleCompraRepository.save(any(DetalleCompra.class))).thenReturn(new DetalleCompra());
        when(historialPrecioRepository.save(any(HistorialPrecio.class))).thenReturn(new HistorialPrecio());

        // When
        compraService.procesarIngresoMercancia(request, "admin");

        // Then
        ArgumentCaptor<HistorialPrecio> historialCaptor = ArgumentCaptor.forClass(HistorialPrecio.class);
        verify(historialPrecioRepository).save(historialCaptor.capture());
        
        HistorialPrecio historial = historialCaptor.getValue();
        assertNotNull(historial.getPrecioFinalPublico());
        assertTrue(historial.getPrecioFinalPublico().compareTo(BigDecimal.ZERO) > 0,
                "El precio final debe ser positivo");
    }

    @Test
    @DisplayName("Debe aplicar redondeo psicológico a múltiplos de 5")
    void testRedondeoSicologico() {
        // Given
        CompraRequestDTO request = crearRequestCompra(false);
        Compra compraMock = new Compra();
        compraMock.setId(1);

        when(finanzasRepository.findById(1)).thenReturn(Optional.of(configuracion));
        when(compraRepository.save(any(Compra.class))).thenReturn(compraMock);
        when(inventarioRepository.findById(any(InventarioSucursalId.class)))
                .thenReturn(Optional.of(inventarioExistente));
        when(productoRepository.findById("SKU001")).thenReturn(Optional.of(productoMaestro));
        when(detalleCompraRepository.save(any(DetalleCompra.class))).thenReturn(new DetalleCompra());
        when(historialPrecioRepository.save(any(HistorialPrecio.class))).thenReturn(new HistorialPrecio());

        // When
        compraService.procesarIngresoMercancia(request, "admin");

        // Then
        ArgumentCaptor<HistorialPrecio> historialCaptor = ArgumentCaptor.forClass(HistorialPrecio.class);
        verify(historialPrecioRepository).save(historialCaptor.capture());
        
        HistorialPrecio historial = historialCaptor.getValue();
        BigDecimal precioFinal = historial.getPrecioFinalPublico();
        double resto = precioFinal.doubleValue() % 5;
        assertEquals(0.0, resto, 0.01, "El precio debe ser múltiplo de 5");
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando no existe configuración financiera")
    void testErrorSinConfiguracionFinanciera() {
        // Given
        CompraRequestDTO request = crearRequestCompra(false);
        when(finanzasRepository.findById(anyInt())).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, 
                () -> compraService.procesarIngresoMercancia(request, "admin"));
        
        assertTrue(exception.getMessage().contains("ConfiguracionFinanciera"));
        verify(compraRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el SKU no existe")
    void testErrorSkuNoExistente() {
        // Given
        CompraRequestDTO request = crearRequestCompra(false);
        Compra compraMock = new Compra();
        compraMock.setId(1);

        when(finanzasRepository.findById(1)).thenReturn(Optional.of(configuracion));
        when(compraRepository.save(any(Compra.class))).thenReturn(compraMock);
        when(inventarioRepository.findById(any(InventarioSucursalId.class)))
                .thenReturn(Optional.of(inventarioExistente));
        when(productoRepository.findById("SKU001")).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> compraService.procesarIngresoMercancia(request, "admin"));
        
        assertTrue(exception.getMessage().contains("Producto"));
    }

    @Test
    @DisplayName("Debe manejar correctamente lista de detalles vacía")
    void testListaDetallesVacia() {
        // Given
        CompraRequestDTO request = crearRequestCompra(false);
        request.setDetalles(Collections.emptyList());
        Compra compraMock = new Compra();
        compraMock.setId(1);

        when(finanzasRepository.findById(1)).thenReturn(Optional.of(configuracion));
        when(compraRepository.save(any(Compra.class))).thenReturn(compraMock);

        // When
        Compra resultado = compraService.procesarIngresoMercancia(request, "admin");

        // Then
        assertNotNull(resultado);
        verify(inventarioRepository, never()).save(any());
        verify(detalleCompraRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe calcular total de compra con IVA correctamente")
    void testCalculoTotalCompraConIva() {
        // Given
        CompraRequestDTO request = crearRequestCompra(false);
        Compra compraMock = new Compra();
        compraMock.setId(1);

        when(finanzasRepository.findById(1)).thenReturn(Optional.of(configuracion));
        when(compraRepository.save(any(Compra.class))).thenReturn(compraMock);
        when(inventarioRepository.findById(any(InventarioSucursalId.class)))
                .thenReturn(Optional.of(inventarioExistente));
        when(productoRepository.findById("SKU001")).thenReturn(Optional.of(productoMaestro));
        when(detalleCompraRepository.save(any(DetalleCompra.class))).thenReturn(new DetalleCompra());
        when(historialPrecioRepository.save(any(HistorialPrecio.class))).thenReturn(new HistorialPrecio());

        // When
        compraService.procesarIngresoMercancia(request, "admin");

        // Then
        ArgumentCaptor<Compra> compraCaptor = ArgumentCaptor.forClass(Compra.class);
        verify(compraRepository, times(2)).save(compraCaptor.capture());
        
        Compra compraFinal = compraCaptor.getAllValues().get(1);
        assertNotNull(compraFinal.getTotalCompra());
        assertTrue(compraFinal.getTotalCompra().compareTo(BigDecimal.ZERO) > 0,
                "El total debe ser mayor a cero");
    }

    // ========== MÉTODOS AUXILIARES ==========

    private CompraRequestDTO crearRequestCompra(boolean incluyeIva) {
        CompraRequestDTO request = new CompraRequestDTO();
        request.setProveedorId(1);
        request.setFolioFactura("FAC-001");
        request.setPreciosIncluyenIva(incluyeIva);
        request.setSucursalDestinoId(1);

        CompraRequestDTO.DetalleItemDTO detalle = new CompraRequestDTO.DetalleItemDTO();
        detalle.setSkuInterno("SKU001");
        detalle.setCantidad(5);
        detalle.setCostoUnitario(new BigDecimal("100.00"));
        detalle.setPrecioPublicoProveedor(null);

        request.setDetalles(List.of(detalle));
        return request;
    }

    private CompraRequestDTO crearRequestConPrecioProveedor() {
        CompraRequestDTO request = crearRequestCompra(false);
        request.getDetalles().get(0).setPrecioPublicoProveedor(new BigDecimal("200.00"));
        return request;
    }
}
