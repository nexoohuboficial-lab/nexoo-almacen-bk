package com.nexoohub.almacen.inventario.service;

import com.nexoohub.almacen.common.exception.ResourceNotFoundException;
import com.nexoohub.almacen.inventario.dto.*;
import com.nexoohub.almacen.inventario.entity.AlertaLentoMovimiento;
import com.nexoohub.almacen.inventario.entity.ProductoMaestro;
import com.nexoohub.almacen.inventario.repository.AlertaLentoMovimientoRepository;
import com.nexoohub.almacen.inventario.repository.InventarioSucursalRepository;
import com.nexoohub.almacen.inventario.repository.ProductoMaestroRepository;
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
import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para AlertaLentoMovimientoService.
 * 
 * @author NexooHub Development Team
 * @since 1.2.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AlertaLentoMovimientoService - Tests de Generación y Gestión de Alertas")
class AlertaLentoMovimientoServiceTest {

    @Mock
    private AlertaLentoMovimientoRepository alertaRepository;

    @Mock
    private InventarioSucursalRepository inventarioRepository;

    @Mock
    private ProductoMaestroRepository productoRepository;

    @Mock
    private SucursalRepository sucursalRepository;

    @InjectMocks
    private AlertaLentoMovimientoService alertaService;

    private AlertaLentoMovimiento alertaAdvertencia;
    private AlertaLentoMovimiento alertaCritica;
    private ProductoMaestro producto;
    private Sucursal sucursal;

    @BeforeEach
    void setUp() {
        producto = new ProductoMaestro();
        producto.setSkuInterno("SKU001");
        producto.setNombreComercial("Producto Test");
        producto.setMarca("HONDA");

        sucursal = new Sucursal();
        sucursal.setId(1);
        sucursal.setNombre("Sucursal Centro");

        alertaAdvertencia = new AlertaLentoMovimiento();
        alertaAdvertencia.setId(1);
        alertaAdvertencia.setSkuInterno("SKU001");
        alertaAdvertencia.setSucursalId(1);
        alertaAdvertencia.setDiasSinVenta(45);
        alertaAdvertencia.setStockActual(10);
        alertaAdvertencia.setCostoInmovilizado(new BigDecimal("1000.00"));
        alertaAdvertencia.setEstadoAlerta("ADVERTENCIA");
        alertaAdvertencia.setFechaDeteccion(LocalDate.now());
        alertaAdvertencia.setResuelto(false);
        alertaAdvertencia.setProducto(producto);
        alertaAdvertencia.setSucursal(sucursal);

        alertaCritica = new AlertaLentoMovimiento();
        alertaCritica.setId(2);
        alertaCritica.setSkuInterno("SKU002");
        alertaCritica.setSucursalId(1);
        alertaCritica.setDiasSinVenta(95); // >= 90 días para sugerir LIQUIDACION
        alertaCritica.setStockActual(5);
        alertaCritica.setCostoInmovilizado(new BigDecimal("2500.00"));
        alertaCritica.setEstadoAlerta("CRITICO");
        alertaCritica.setFechaDeteccion(LocalDate.now());
        alertaCritica.setResuelto(false);
        alertaCritica.setProducto(producto);
        alertaCritica.setSucursal(sucursal);
    }

    @Test
    @DisplayName("Debe generar nuevas alertas correctamente")
    void testGenerarAlertasNuevas() {
        // Given
        GenerarAlertasRequestDTO request = new GenerarAlertasRequestDTO();
        request.setDiasSinVentaMinimo(30);

        LocalDate fechaCorte = LocalDate.now().minusDays(30);
        Object[] row1 = {
            "SKU001", 1, 10, new BigDecimal("100.00"),
            Date.valueOf(LocalDate.now().minusDays(45)), 45
        };

        when(alertaRepository.detectarProductosSinVentas(fechaCorte))
                .thenReturn(java.util.Collections.singletonList(row1));
        when(alertaRepository.findBySkuInternoAndSucursalIdAndResueltoFalse("SKU001", 1))
                .thenReturn(Optional.empty());
        when(alertaRepository.save(any(AlertaLentoMovimiento.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        List<AlertaLentoMovimientoResponseDTO> resultado = alertaService.generarAlertas(request);

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(alertaRepository, times(1)).save(any(AlertaLentoMovimiento.class));
    }

    @Test
    @DisplayName("Debe actualizar alertas existentes sin crear duplicados")
    void testActualizarAlertasExistentes() {
        // Given
        GenerarAlertasRequestDTO request = new GenerarAlertasRequestDTO();
        request.setDiasSinVentaMinimo(30);

        LocalDate fechaCorte = LocalDate.now().minusDays(30);
        Object[] row1 = {
            "SKU001", 1, 10, new BigDecimal("100.00"),
            Date.valueOf(LocalDate.now().minusDays(50)), 50
        };

        when(alertaRepository.detectarProductosSinVentas(fechaCorte))
                .thenReturn(java.util.Collections.singletonList(row1));
        when(alertaRepository.findBySkuInternoAndSucursalIdAndResueltoFalse("SKU001", 1))
                .thenReturn(Optional.of(alertaAdvertencia));
        when(alertaRepository.save(any(AlertaLentoMovimiento.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        List<AlertaLentoMovimientoResponseDTO> resultado = alertaService.generarAlertas(request);

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(alertaRepository, times(1)).save(any(AlertaLentoMovimiento.class));
        assertEquals(50, alertaAdvertencia.getDiasSinVenta());
    }

    @Test
    @DisplayName("Debe filtrar por sucursal cuando se especifica")
    void testGenerarAlertasPorSucursal() {
        // Given
        GenerarAlertasRequestDTO request = new GenerarAlertasRequestDTO();
        request.setDiasSinVentaMinimo(30);
        request.setSucursalId(1);

        LocalDate fechaCorte = LocalDate.now().minusDays(30);
        Object[] row1 = {
            "SKU001", 1, 10, new BigDecimal("100.00"),
            Date.valueOf(LocalDate.now().minusDays(45)), 45
        };
        Object[] row2 = {
            "SKU002", 2, 5, new BigDecimal("50.00"),
            Date.valueOf(LocalDate.now().minusDays(50)), 50
        };

        when(alertaRepository.detectarProductosSinVentas(fechaCorte))
                .thenReturn(java.util.List.of(row1, row2));
        when(alertaRepository.findBySkuInternoAndSucursalIdAndResueltoFalse("SKU001", 1))
                .thenReturn(Optional.empty());
        when(alertaRepository.save(any(AlertaLentoMovimiento.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        List<AlertaLentoMovimientoResponseDTO> resultado = alertaService.generarAlertas(request);

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size()); // Solo debe procesar sucursal 1
        verify(alertaRepository, times(1)).save(any(AlertaLentoMovimiento.class));
    }

    @Test
    @DisplayName("Debe obtener todas las alertas activas")
    void testObtenerAlertasActivas() {
        // Given
        when(alertaRepository.findAllActiveWithDetails())
                .thenReturn(Arrays.asList(alertaAdvertencia, alertaCritica));

        // When
        List<AlertaLentoMovimientoResponseDTO> resultado = alertaService.obtenerAlertasActivas();

        // Then
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(alertaRepository, times(1)).findAllActiveWithDetails();
    }

    @Test
    @DisplayName("Debe obtener alertas por sucursal")
    void testObtenerAlertasPorSucursal() {
        // Given
        when(alertaRepository.findBySucursalIdAndResueltoFalse(1))
                .thenReturn(Arrays.asList(alertaAdvertencia));

        // When
        List<AlertaLentoMovimientoResponseDTO> resultado = alertaService.obtenerAlertasPorSucursal(1);

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(1, resultado.get(0).getSucursalId());
        verify(alertaRepository, times(1)).findBySucursalIdAndResueltoFalse(1);
    }

    @Test
    @DisplayName("Debe obtener solo alertas críticas")
    void testObtenerAlertasCriticas() {
        // Given
        when(alertaRepository.findCriticasActivas())
                .thenReturn(Arrays.asList(alertaCritica));

        // When
        List<AlertaLentoMovimientoResponseDTO> resultado = alertaService.obtenerAlertasCriticas();

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("CRITICO", resultado.get(0).getEstadoAlerta());
        verify(alertaRepository, times(1)).findCriticasActivas();
    }

    @Test
    @DisplayName("Debe obtener alertas por producto")
    void testObtenerAlertasPorProducto() {
        // Given
        when(alertaRepository.findBySkuInternoAndResueltoFalse("SKU001"))
                .thenReturn(Arrays.asList(alertaAdvertencia));

        // When
        List<AlertaLentoMovimientoResponseDTO> resultado = alertaService.obtenerAlertasPorProducto("SKU001");

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("SKU001", resultado.get(0).getSkuInterno());
        verify(alertaRepository, times(1)).findBySkuInternoAndResueltoFalse("SKU001");
    }

    @Test
    @DisplayName("Debe obtener alerta por ID exitosamente")
    void testObtenerAlertaPorId() {
        // Given
        when(alertaRepository.findById(1)).thenReturn(Optional.of(alertaAdvertencia));

        // When
        AlertaLentoMovimientoResponseDTO resultado = alertaService.obtenerAlertaPorId(1);

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.getId());
        assertEquals("SKU001", resultado.getSkuInterno());
        verify(alertaRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando alerta no existe")
    void testObtenerAlertaPorIdNoExiste() {
        // Given
        when(alertaRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            alertaService.obtenerAlertaPorId(999);
        });
        verify(alertaRepository, times(1)).findById(999);
    }

    @Test
    @DisplayName("Debe calcular costo inmovilizado global")
    void testCalcularCostoInmovilizadoGlobal() {
        // Given
        when(alertaRepository.calcularCostoTotalInmovilizado())
                .thenReturn(new BigDecimal("15000.00"));
        when(alertaRepository.countByEstadoAlertaAndResueltoFalse("ADVERTENCIA"))
                .thenReturn(3L);
        when(alertaRepository.countByEstadoAlertaAndResueltoFalse("CRITICO"))
                .thenReturn(2L);

        // When
        CostoInmovilizadoResumenDTO resultado = alertaService.calcularCostoInmovilizado(null);

        // Then
        assertNotNull(resultado);
        assertEquals(new BigDecimal("15000.00"), resultado.getCostoTotalInmovilizado());
        assertEquals(3L, resultado.getAlertasAdvertencia());
        assertEquals(2L, resultado.getAlertasCriticas());
        assertEquals(5L, resultado.getTotalProductosAfectados());
        assertNull(resultado.getSucursalId());
    }

    @Test
    @DisplayName("Debe calcular costo inmovilizado por sucursal")
    void testCalcularCostoInmovilizadoPorSucursal() {
        // Given
        when(sucursalRepository.findById(1)).thenReturn(Optional.of(sucursal));
        when(alertaRepository.calcularCostoInmovilizadoPorSucursal(1))
                .thenReturn(new BigDecimal("5000.00"));
        when(alertaRepository.countByEstadoAlertaAndResueltoFalse("ADVERTENCIA"))
                .thenReturn(2L);
        when(alertaRepository.countByEstadoAlertaAndResueltoFalse("CRITICO"))
                .thenReturn(1L);

        // When
        CostoInmovilizadoResumenDTO resultado = alertaService.calcularCostoInmovilizado(1);

        // Then
        assertNotNull(resultado);
        assertEquals(new BigDecimal("5000.00"), resultado.getCostoTotalInmovilizado());
        assertEquals(1, resultado.getSucursalId());
        assertEquals("Sucursal Centro", resultado.getNombreSucursal());
        verify(sucursalRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Debe resolver alerta correctamente")
    void testResolverAlerta() {
        // Given
        ResolverAlertaRequestDTO request = new ResolverAlertaRequestDTO();
        request.setAccionTomada("LIQUIDACION");
        request.setObservaciones("Producto liquidado al 50% de descuento");

        when(alertaRepository.findById(1)).thenReturn(Optional.of(alertaAdvertencia));
        when(alertaRepository.save(any(AlertaLentoMovimiento.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        AlertaLentoMovimientoResponseDTO resultado = alertaService.resolverAlerta(1, request);

        // Then
        assertNotNull(resultado);
        assertTrue(alertaAdvertencia.getResuelto());
        assertEquals("RESUELTA", alertaAdvertencia.getEstadoAlerta());
        assertEquals("LIQUIDACION", alertaAdvertencia.getAccionTomada());
        assertEquals(LocalDate.now(), alertaAdvertencia.getFechaResolucion());
        verify(alertaRepository, times(1)).save(alertaAdvertencia);
    }

    @Test
    @DisplayName("Debe lanzar excepción al resolver alerta inexistente")
    void testResolverAlertaNoExiste() {
        // Given
        ResolverAlertaRequestDTO request = new ResolverAlertaRequestDTO();
        request.setAccionTomada("LIQUIDACION");

        when(alertaRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            alertaService.resolverAlerta(999, request);
        });
        verify(alertaRepository, times(1)).findById(999);
        verify(alertaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe eliminar alerta exitosamente")
    void testEliminarAlerta() {
        // Given
        when(alertaRepository.existsById(1)).thenReturn(true);
        doNothing().when(alertaRepository).deleteById(1);

        // When
        alertaService.eliminarAlerta(1);

        // Then
        verify(alertaRepository, times(1)).existsById(1);
        verify(alertaRepository, times(1)).deleteById(1);
    }

    @Test
    @DisplayName("Debe lanzar excepción al eliminar alerta inexistente")
    void testEliminarAlertaNoExiste() {
        // Given
        when(alertaRepository.existsById(999)).thenReturn(false);

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            alertaService.eliminarAlerta(999);
        });
        verify(alertaRepository, times(1)).existsById(999);
        verify(alertaRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Debe calcular sugerencias de acción correctamente")
    void testSugerenciasAccion() {
        // Given
        when(alertaRepository.findAllActiveWithDetails())
                .thenReturn(Arrays.asList(alertaAdvertencia, alertaCritica));

        // When
        List<AlertaLentoMovimientoResponseDTO> resultado = alertaService.obtenerAlertasActivas();

        // Then
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        
        // Alerta advertencia (45 días) debe sugerir promoción
        assertTrue(resultado.get(0).getSugerenciaAccion().contains("PROMOCION") ||
                   resultado.get(0).getSugerenciaAccion().contains("Monitorear"));
        
        // Alerta crítica (75 días) debe sugerir liquidación
        assertTrue(resultado.get(1).getSugerenciaAccion().contains("LIQUIDACION") ||
                   resultado.get(1).getSugerenciaAccion().contains("DESCONTINUAR"));
    }

    @Test
    @DisplayName("Debe manejar productos sin ultima venta (nunca vendidos)")
    void testProductosSinUltimaVenta() {
        // Given
        GenerarAlertasRequestDTO request = new GenerarAlertasRequestDTO();
        request.setDiasSinVentaMinimo(30);

        LocalDate fechaCorte = LocalDate.now().minusDays(30);
        Object[] row1 = {
            "SKU999", 1, 15, new BigDecimal("200.00"),
            null, 365 // Nunca vendido
        };

        when(alertaRepository.detectarProductosSinVentas(fechaCorte))
                .thenReturn(java.util.Collections.singletonList(row1));
        when(alertaRepository.findBySkuInternoAndSucursalIdAndResueltoFalse("SKU999", 1))
                .thenReturn(Optional.empty());
        when(alertaRepository.save(any(AlertaLentoMovimiento.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        List<AlertaLentoMovimientoResponseDTO> resultado = alertaService.generarAlertas(request);

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertNull(resultado.get(0).getUltimaVenta()); // Debe aceptar null para nunca vendido
    }
}
