package com.nexoohub.almacen.cotizaciones.service;

import com.nexoohub.almacen.catalogo.entity.Cliente;
import com.nexoohub.almacen.catalogo.repository.ClienteRepository;
import com.nexoohub.almacen.common.exception.InvalidOperationException;
import com.nexoohub.almacen.common.exception.ResourceNotFoundException;
import com.nexoohub.almacen.common.exception.StockInsuficienteException;
import com.nexoohub.almacen.cotizaciones.dto.*;
import com.nexoohub.almacen.cotizaciones.entity.Cotizacion;
import com.nexoohub.almacen.cotizaciones.entity.DetalleCotizacion;
import com.nexoohub.almacen.cotizaciones.repository.CotizacionRepository;
import com.nexoohub.almacen.cotizaciones.repository.DetalleCotizacionRepository;
import com.nexoohub.almacen.empleados.entity.Empleado;
import com.nexoohub.almacen.empleados.repository.EmpleadoRepository;
import com.nexoohub.almacen.inventario.entity.InventarioSucursal;
import com.nexoohub.almacen.inventario.entity.InventarioSucursalId;
import com.nexoohub.almacen.inventario.entity.ProductoMaestro;
import com.nexoohub.almacen.inventario.repository.InventarioSucursalRepository;
import com.nexoohub.almacen.inventario.repository.ProductoMaestroRepository;
import com.nexoohub.almacen.sucursal.entity.Sucursal;
import com.nexoohub.almacen.sucursal.repository.SucursalRepository;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para CotizacionService
 * Valida la lógica de negocio relacionada con cotizaciones
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CotizacionService - Tests Unitarios")
class CotizacionServiceTest {

    @Mock
    private CotizacionRepository cotizacionRepository;

    @Mock
    private DetalleCotizacionRepository detalleCotizacionRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private SucursalRepository sucursalRepository;

    @Mock
    private EmpleadoRepository empleadoRepository;

    @Mock
    private ProductoMaestroRepository productoRepository;

    @Mock
    private VentaRepository ventaRepository;

    @Mock
    private DetalleVentaRepository detalleVentaRepository;

    @Mock
    private InventarioSucursalRepository inventarioRepository;

    @InjectMocks
    private CotizacionService cotizacionService;

    private Cliente cliente;
    private Sucursal sucursal;
    private Empleado vendedor;
    private ProductoMaestro producto;
    private CotizacionRequestDTO requestDTO;
    private Cotizacion cotizacion;

    @BeforeEach
    void setUp() {
        // Configurar cliente
        cliente = new Cliente();
        cliente.setId(1);
        cliente.setNombre("Cliente Test");
        cliente.setTelefono("8123456789");

        // Configurar sucursal
        sucursal = new Sucursal();
        sucursal.setId(1);
        sucursal.setNombre("Sucursal Centro");
        sucursal.setActivo(true);

        // Configurar vendedor
        vendedor = new Empleado();
        vendedor.setId(1);
        vendedor.setNombre("Vendedor Test");

        // Configurar producto
        producto = new ProductoMaestro();
        producto.setSkuInterno("SKU001");
        producto.setNombreComercial("Producto Test");
        producto.setActivo(true);

        // Configurar request DTO
        DetalleCotizacionDTO detalleDTO = new DetalleCotizacionDTO();
        detalleDTO.setSkuInterno("SKU001");
        detalleDTO.setCantidad(5);
        detalleDTO.setPrecioUnitario(new BigDecimal("100.00"));
        detalleDTO.setDescuentoEspecial(BigDecimal.ZERO);
        detalleDTO.setPorcentajeDescuento(BigDecimal.ZERO);

        requestDTO = new CotizacionRequestDTO();
        requestDTO.setClienteId(1);
        requestDTO.setSucursalId(1);
        requestDTO.setVendedorId(1);
        requestDTO.setFechaValidez(LocalDate.now().plusDays(15));
        requestDTO.setNotas("Cotización de prueba");
        requestDTO.setDetalles(Arrays.asList(detalleDTO));

        // Configurar cotización mock
        cotizacion = new Cotizacion();
        cotizacion.setId(1L);
        cotizacion.setFolio("COT-2026-0001");
        cotizacion.setClienteId(1);
        cotizacion.setSucursalId(1);
        cotizacion.setVendedorId(1);
        cotizacion.setFechaValidez(LocalDate.now().plusDays(15));
        cotizacion.setEstado("BORRADOR");
        cotizacion.setSubtotal(new BigDecimal("500.00"));
        cotizacion.setTotal(new BigDecimal("500.00"));
        
        DetalleCotizacion detalle = new DetalleCotizacion();
        detalle.setId(1L);
        detalle.setCotizacion(cotizacion);
        detalle.setSkuInterno("SKU001");
        detalle.setCantidad(5);
        detalle.setPrecioUnitario(new BigDecimal("100.00"));
        detalle.setProducto(producto);
        cotizacion.setDetalles(Arrays.asList(detalle));
    }

    @Test
    @DisplayName("Test 1: Debe crear cotización exitosamente con generación de folio")
    void testCrearCotizacion_Exitoso() {
        // Given
        when(clienteRepository.findById(1)).thenReturn(Optional.of(cliente));
        when(sucursalRepository.findById(1)).thenReturn(Optional.of(sucursal));
        when(empleadoRepository.findById(1)).thenReturn(Optional.of(vendedor));
        when(productoRepository.findById("SKU001")).thenReturn(Optional.of(producto));
        when(cotizacionRepository.save(any(Cotizacion.class))).thenReturn(cotizacion);

        // When
        CotizacionResponseDTO resultado = cotizacionService.crearCotizacion(requestDTO);

        // Then
        assertNotNull(resultado, "La cotización no debe ser null");
        assertEquals("COT-2026-0001", resultado.getFolio(), "El folio debe ser generado");
        assertEquals("BORRADOR", resultado.getEstado(), "El estado inicial debe ser BORRADOR");
        verify(clienteRepository, times(1)).findById(1);
        verify(sucursalRepository, times(1)).findById(1);
        verify(empleadoRepository, times(1)).findById(1);
        verify(productoRepository, atLeastOnce()).findById("SKU001");
        verify(cotizacionRepository, times(1)).save(any(Cotizacion.class));
    }

    @Test
    @DisplayName("Test 2: Debe lanzar ResourceNotFoundException cuando el cliente no existe")
    void testCrearCotizacion_ClienteNoExiste() {
        // Given
        when(clienteRepository.findById(1)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> cotizacionService.crearCotizacion(requestDTO),
            "Debe lanzar ResourceNotFoundException cuando el cliente no existe"
        );

        assertTrue(exception.getMessage().contains("Cliente no encontrado"));
        verify(cotizacionRepository, never()).save(any(Cotizacion.class));
    }

    @Test
    @DisplayName("Test 3: Debe actualizar cotización exitosamente cuando está en estado BORRADOR")
    void testActualizarCotizacion_Exitoso() {
        // Given
        when(cotizacionRepository.findWithDetallesById(1L)).thenReturn(Optional.of(cotizacion));
        when(clienteRepository.findById(1)).thenReturn(Optional.of(cliente));
        when(sucursalRepository.findById(1)).thenReturn(Optional.of(sucursal));
        when(empleadoRepository.findById(1)).thenReturn(Optional.of(vendedor));
        when(productoRepository.findById("SKU001")).thenReturn(Optional.of(producto));
        when(cotizacionRepository.save(any(Cotizacion.class))).thenReturn(cotizacion);

        // When
        CotizacionResponseDTO resultado = cotizacionService.actualizarCotizacion(1L, requestDTO);

        // Then
        assertNotNull(resultado, "La cotización actualizada no debe ser null");
        verify(cotizacionRepository, times(1)).findWithDetallesById(1L);
        verify(detalleCotizacionRepository, times(1)).deleteByCotizacionId(1L);
        verify(cotizacionRepository, times(1)).save(any(Cotizacion.class));
    }

    @Test
    @DisplayName("Test 4: Debe lanzar InvalidOperationException al actualizar cotización no editable")
    void testActualizarCotizacion_NoEditable() {
        // Given
        cotizacion.setEstado("ENVIADA"); // Estado no editable
        when(cotizacionRepository.findWithDetallesById(1L)).thenReturn(Optional.of(cotizacion));

        // When & Then
        InvalidOperationException exception = assertThrows(
            InvalidOperationException.class,
            () -> cotizacionService.actualizarCotizacion(1L, requestDTO),
            "Debe lanzar InvalidOperationException para cotizaciones no editables"
        );

        assertTrue(exception.getMessage().contains("BORRADOR"));
        verify(cotizacionRepository, never()).save(any(Cotizacion.class));
    }

    @Test
    @DisplayName("Test 5: Debe cambiar estado de BORRADOR a ENVIADA exitosamente")
    void testCambiarEstado_BorradorAEnviada() {
        // Given
        CambiarEstadoRequestDTO request = new CambiarEstadoRequestDTO("ENVIADA");
        when(cotizacionRepository.findById(1L)).thenReturn(Optional.of(cotizacion));
        when(cotizacionRepository.save(any(Cotizacion.class))).thenReturn(cotizacion);

        // When
        CotizacionResponseDTO resultado = cotizacionService.cambiarEstado(1L, request);

        // Then
        assertNotNull(resultado);
        verify(cotizacionRepository, times(1)).findById(1L);
        verify(cotizacionRepository, times(1)).save(any(Cotizacion.class));
    }

    @Test
    @DisplayName("Test 6: Debe lanzar InvalidOperationException al rechazar sin motivo")
    void testCambiarEstado_RechazarSinMotivo() {
        // Given
        cotizacion.setEstado("ENVIADA");
        CambiarEstadoRequestDTO request = new CambiarEstadoRequestDTO("RECHAZADA", null);
        when(cotizacionRepository.findById(1L)).thenReturn(Optional.of(cotizacion));

        // When & Then
        InvalidOperationException exception = assertThrows(
            InvalidOperationException.class,
            () -> cotizacionService.cambiarEstado(1L, request),
            "Debe lanzar InvalidOperationException al rechazar sin motivo"
        );

        assertTrue(exception.getMessage().contains("motivo es obligatorio"));
        verify(cotizacionRepository, never()).save(any(Cotizacion.class));
    }

    @Test
    @DisplayName("Test 7: Debe convertir cotización a venta exitosamente")
    void testConvertirAVenta_Exitoso() {
        // Given
        cotizacion.setEstado("ACEPTADA");
        ConvertirVentaRequestDTO request = new ConvertirVentaRequestDTO();
        request.setMetodoPago("EFECTIVO");

        InventarioSucursal inventario = new InventarioSucursal();
        inventario.setId(new InventarioSucursalId(1, "SKU001"));
        inventario.setStockActual(10); // Stock suficiente

        Venta venta = new Venta();
        venta.setId(1);

        when(cotizacionRepository.findWithDetallesById(1L)).thenReturn(Optional.of(cotizacion));
        when(inventarioRepository.findById(any(InventarioSucursalId.class))).thenReturn(Optional.of(inventario));
        when(ventaRepository.save(any(Venta.class))).thenReturn(venta);
        when(detalleVentaRepository.save(any(DetalleVenta.class))).thenReturn(new DetalleVenta());
        when(inventarioRepository.save(any(InventarioSucursal.class))).thenReturn(inventario);
        when(cotizacionRepository.save(any(Cotizacion.class))).thenReturn(cotizacion);

        // When
        Integer ventaId = cotizacionService.convertirAVenta(1L, request);

        // Then
        assertNotNull(ventaId);
        assertEquals(1, ventaId);
        verify(ventaRepository, times(1)).save(any(Venta.class));
        verify(detalleVentaRepository, times(1)).save(any(DetalleVenta.class));
        verify(inventarioRepository, times(1)).save(any(InventarioSucursal.class));
        verify(cotizacionRepository, times(1)).save(any(Cotizacion.class));
    }

    @Test
    @DisplayName("Test 8: Debe lanzar InvalidOperationException al convertir cotización con estado inválido")
    void testConvertirAVenta_EstadoInvalido() {
        // Given
        cotizacion.setEstado("BORRADOR"); // Estado inválido para conversión
        ConvertirVentaRequestDTO request = new ConvertirVentaRequestDTO();
        request.setMetodoPago("EFECTIVO");

        when(cotizacionRepository.findWithDetallesById(1L)).thenReturn(Optional.of(cotizacion));

        // When & Then
        InvalidOperationException exception = assertThrows(
            InvalidOperationException.class,
            () -> cotizacionService.convertirAVenta(1L, request),
            "Debe lanzar InvalidOperationException para estado inválido"
        );

        assertTrue(exception.getMessage().contains("no puede ser convertida en venta"));
        verify(ventaRepository, never()).save(any(Venta.class));
    }

    @Test
    @DisplayName("Test 9: Debe lanzar StockInsuficienteException cuando no hay stock suficiente")
    void testConvertirAVenta_StockInsuficiente() {
        // Given
        cotizacion.setEstado("ACEPTADA");
        ConvertirVentaRequestDTO request = new ConvertirVentaRequestDTO();
        request.setMetodoPago("EFECTIVO");

        InventarioSucursal inventario = new InventarioSucursal();
        inventario.setId(new InventarioSucursalId(1, "SKU001"));
        inventario.setStockActual(2); // Stock insuficiente (necesita 5)

        when(cotizacionRepository.findWithDetallesById(1L)).thenReturn(Optional.of(cotizacion));
        when(inventarioRepository.findById(any(InventarioSucursalId.class))).thenReturn(Optional.of(inventario));

        // When & Then
        StockInsuficienteException exception = assertThrows(
            StockInsuficienteException.class,
            () -> cotizacionService.convertirAVenta(1L, request),
            "Debe lanzar StockInsuficienteException cuando no hay stock suficiente"
        );

        assertTrue(exception.getMessage().contains("Stock insuficiente"));
        verify(ventaRepository, never()).save(any(Venta.class));
    }

    @Test
    @DisplayName("Test 10: Debe marcar cotizaciones vencidas correctamente")
    void testMarcarCotizacionesVencidas() {
        // Given
        Cotizacion cotizacion1 = new Cotizacion();
        cotizacion1.setId(1L);
        cotizacion1.setFolio("COT-2026-0001");
        cotizacion1.setEstado("ENVIADA");
        cotizacion1.setFechaValidez(LocalDate.now().minusDays(1));

        Cotizacion cotizacion2 = new Cotizacion();
        cotizacion2.setId(2L);
        cotizacion2.setFolio("COT-2026-0002");
        cotizacion2.setEstado("ENVIADA");
        cotizacion2.setFechaValidez(LocalDate.now().minusDays(5));

        List<Cotizacion> vencidas = Arrays.asList(cotizacion1, cotizacion2);

        when(cotizacionRepository.findVencidas(any(LocalDate.class))).thenReturn(vencidas);
        when(cotizacionRepository.saveAll(anyList())).thenReturn(vencidas);

        // When
        int resultado = cotizacionService.marcarCotizacionesVencidas();

        // Then
        assertEquals(2, resultado, "Debe marcar 2 cotizaciones como vencidas");
        verify(cotizacionRepository, times(1)).findVencidas(any(LocalDate.class));
        verify(cotizacionRepository, times(1)).saveAll(anyList());
    }

    @Test
    @DisplayName("Test 11: Debe eliminar cotización en estado BORRADOR exitosamente")
    void testEliminarCotizacion_Exitoso() {
        // Given
        when(cotizacionRepository.findById(1L)).thenReturn(Optional.of(cotizacion));
        doNothing().when(cotizacionRepository).delete(any(Cotizacion.class));

        // When
        cotizacionService.eliminarCotizacion(1L);

        // Then
        verify(cotizacionRepository, times(1)).findById(1L);
        verify(cotizacionRepository, times(1)).delete(cotizacion);
    }

    @Test
    @DisplayName("Test 12: Debe lanzar InvalidOperationException al eliminar cotización no editable")
    void testEliminarCotizacion_NoEditable() {
        // Given
        cotizacion.setEstado("ENVIADA"); // Estado no editable
        when(cotizacionRepository.findById(1L)).thenReturn(Optional.of(cotizacion));

        // When & Then
        InvalidOperationException exception = assertThrows(
            InvalidOperationException.class,
            () -> cotizacionService.eliminarCotizacion(1L),
            "Debe lanzar InvalidOperationException al eliminar cotización no editable"
        );

        assertTrue(exception.getMessage().contains("BORRADOR"));
        verify(cotizacionRepository, never()).delete(any(Cotizacion.class));
    }

    @Test
    @DisplayName("Test 13: Debe obtener estadísticas correctamente")
    void testObtenerEstadisticas() {
        // Given
        when(cotizacionRepository.countByEstado("BORRADOR")).thenReturn(5L);
        when(cotizacionRepository.countByEstado("ENVIADA")).thenReturn(10L);
        when(cotizacionRepository.countByEstado("ACEPTADA")).thenReturn(8L);
        when(cotizacionRepository.countByEstado("RECHAZADA")).thenReturn(2L);
        when(cotizacionRepository.countVencidas(any(LocalDate.class))).thenReturn(3L);
        when(cotizacionRepository.countByEstado("CONVERTIDA")).thenReturn(6L);
        
        when(cotizacionRepository.calcularTotalPorEstado("BORRADOR")).thenReturn(new BigDecimal("5000.00"));
        when(cotizacionRepository.calcularTotalPorEstado("ENVIADA")).thenReturn(new BigDecimal("10000.00"));
        when(cotizacionRepository.calcularTotalPorEstado("ACEPTADA")).thenReturn(new BigDecimal("8000.00"));
        when(cotizacionRepository.calcularTotalPorEstado("CONVERTIDA")).thenReturn(new BigDecimal("6000.00"));

        // When
        EstadisticasCotizacionDTO resultado = cotizacionService.obtenerEstadisticas();

        // Then
        assertNotNull(resultado);
        assertEquals(5L, resultado.getCotizacionesBorrador());
        assertEquals(10L, resultado.getCotizacionesEnviadas());
        assertEquals(8L, resultado.getCotizacionesAceptadas());
        assertEquals(2L, resultado.getCotizacionesRechazadas());
        assertEquals(3L, resultado.getCotizacionesVencidas());
        assertEquals(6L, resultado.getCotizacionesConvertidas());
        assertEquals(34L, resultado.getTotalCotizaciones());
        
        assertTrue(resultado.getTasaConversion() > 0);
        assertTrue(resultado.getTasaAceptacion() > 0);
        
        verify(cotizacionRepository, times(5)).countByEstado(anyString());
        verify(cotizacionRepository, times(1)).countVencidas(any(LocalDate.class));
        verify(cotizacionRepository, times(4)).calcularTotalPorEstado(anyString());
    }
}
