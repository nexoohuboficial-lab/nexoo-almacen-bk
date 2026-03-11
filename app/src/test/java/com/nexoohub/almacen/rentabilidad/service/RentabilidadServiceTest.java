package com.nexoohub.almacen.rentabilidad.service;

import com.nexoohub.almacen.common.exception.BusinessException;
import com.nexoohub.almacen.common.exception.ResourceNotFoundException;
import com.nexoohub.almacen.inventario.entity.InventarioSucursal;
import com.nexoohub.almacen.inventario.entity.InventarioSucursalId;
import com.nexoohub.almacen.inventario.repository.InventarioSucursalRepository;
import com.nexoohub.almacen.rentabilidad.dto.*;
import com.nexoohub.almacen.rentabilidad.entity.RentabilidadProducto;
import com.nexoohub.almacen.rentabilidad.entity.RentabilidadVenta;
import com.nexoohub.almacen.rentabilidad.repository.RentabilidadProductoRepository;
import com.nexoohub.almacen.rentabilidad.repository.RentabilidadVentaRepository;
import com.nexoohub.almacen.ventas.entity.DetalleVenta;
import com.nexoohub.almacen.ventas.entity.Venta;
import com.nexoohub.almacen.ventas.repository.DetalleVentaRepository;
import com.nexoohub.almacen.ventas.repository.VentaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RentabilidadService - Tests de Análisis de Rentabilidad")
class RentabilidadServiceTest {

    @Mock
    private RentabilidadVentaRepository rentabilidadVentaRepository;

    @Mock
    private RentabilidadProductoRepository rentabilidadProductoRepository;

    @Mock
    private VentaRepository ventaRepository;

    @Mock
    private DetalleVentaRepository detalleVentaRepository;

    @Mock
    private InventarioSucursalRepository inventarioRepository;

    @InjectMocks
    private RentabilidadService rentabilidadService;

    private Venta ventaTest;
    private List<DetalleVenta> detallesTest;
    private InventarioSucursal inventarioTest;

    @BeforeEach
    void setUp() {
        // Venta de prueba
        ventaTest = new Venta();
        ventaTest.setId(100);
        ventaTest.setSucursalId(1);
        ventaTest.setTotal(new BigDecimal("1000.00")); // Precio de venta
        ventaTest.setFechaVenta(LocalDateTime.now());

        // Detalle de venta de prueba (2 productos)
        DetalleVenta detalle1 = new DetalleVenta();
        detalle1.setId(1);
        detalle1.setVentaId(100);
        detalle1.setSkuInterno("SKU001");
        detalle1.setCantidad(10);
        detalle1.setPrecioUnitarioVenta(new BigDecimal("50.00"));

        DetalleVenta detalle2 = new DetalleVenta();
        detalle2.setId(2);
        detalle2.setVentaId(100);
        detalle2.setSkuInterno("SKU002");
        detalle2.setCantidad(10);
        detalle2.setPrecioUnitarioVenta(new BigDecimal("50.00"));

        detallesTest = Arrays.asList(detalle1, detalle2);

        // Inventario de prueba (costo promedio ponderado)
        inventarioTest = new InventarioSucursal();
        InventarioSucursalId invId = new InventarioSucursalId();
        invId.setSkuInterno("SKU001");
        invId.setSucursalId(1);
        inventarioTest.setId(invId);
        inventarioTest.setCostoPromedioPonderado(new BigDecimal("30.00")); // Costo: $30
    }

    @Test
    @DisplayName("Debe calcular rentabilidad de venta exitosamente")
    void testCalcularRentabilidadVentaExitosa() {
        // Given
        when(ventaRepository.findById(100)).thenReturn(Optional.of(ventaTest));
        when(rentabilidadVentaRepository.findByVentaId(100)).thenReturn(Optional.empty());
        when(detalleVentaRepository.findByVentaId(100)).thenReturn(detallesTest);
        when(inventarioRepository.findByIdSkuInternoAndIdSucursalId(anyString(), anyInt()))
                .thenReturn(Optional.of(inventarioTest));

        RentabilidadVenta guardada = new RentabilidadVenta();
        guardada.setId(1L);
        guardada.setVentaId(100);
        guardada.setCostoTotal(new BigDecimal("600.00")); // 20 unidades * $30
        guardada.setPrecioVentaTotal(new BigDecimal("1000.00"));
        guardada.setUtilidadBruta(new BigDecimal("400.00")); // $1000 - $600
        guardada.setMargenPorcentaje(new BigDecimal("40.00")); // 40%
        guardada.setVentaBajoCosto(false);
        guardada.setCantidadItems(2);

        when(rentabilidadVentaRepository.save(any(RentabilidadVenta.class))).thenReturn(guardada);

        // When
        RentabilidadVentaResponseDTO resultado = rentabilidadService.calcularRentabilidadVenta(100);

        // Then
        assertNotNull(resultado);
        assertEquals(100, resultado.getVentaId());
        assertEquals(new BigDecimal("600.00"), resultado.getCostoTotal());
        assertEquals(new BigDecimal("1000.00"), resultado.getPrecioVentaTotal());
        assertEquals(new BigDecimal("400.00"), resultado.getUtilidadBruta());
        assertEquals(new BigDecimal("40.00"), resultado.getMargenPorcentaje());
        assertFalse(resultado.getVentaBajoCosto());

        verify(rentabilidadVentaRepository, times(1)).save(any(RentabilidadVenta.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si venta no existe")
    void testCalcularRentabilidadVentaNoExiste() {
        // Given
        when(ventaRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            rentabilidadService.calcularRentabilidadVenta(999);
        });

        verify(rentabilidadVentaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar excepción si ya existe análisis previo")
    void testCalcularRentabilidadVentaYaExiste() {
        // Given
        when(ventaRepository.findById(100)).thenReturn(Optional.of(ventaTest));
        when(rentabilidadVentaRepository.findByVentaId(100))
                .thenReturn(Optional.of(new RentabilidadVenta()));

        // When & Then
        assertThrows(BusinessException.class, () -> {
            rentabilidadService.calcularRentabilidadVenta(100);
        });

        verify(rentabilidadVentaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe detectar venta bajo costo (con pérdida)")
    void testDetectarVentaBajoCosto() {
        // Given - Costo mayor al precio de venta
        inventarioTest.setCostoPromedioPonderado(new BigDecimal("60.00")); // Costo alto
        ventaTest.setTotal(new BigDecimal("1000.00"));

        when(ventaRepository.findById(100)).thenReturn(Optional.of(ventaTest));
        when(rentabilidadVentaRepository.findByVentaId(100)).thenReturn(Optional.empty());
        when(detalleVentaRepository.findByVentaId(100)).thenReturn(detallesTest);
        when(inventarioRepository.findByIdSkuInternoAndIdSucursalId(anyString(), anyInt()))
                .thenReturn(Optional.of(inventarioTest));

        RentabilidadVenta guardada = new RentabilidadVenta();
        guardada.setId(1L);
        guardada.setVentaId(100);
        guardada.setCostoTotal(new BigDecimal("1200.00")); // 20 * $60 = $1200
        guardada.setPrecioVentaTotal(new BigDecimal("1000.00"));
        guardada.setUtilidadBruta(new BigDecimal("-200.00")); // PÉRDIDA
        guardada.setMargenPorcentaje(new BigDecimal("-20.00"));
        guardada.setVentaBajoCosto(true); // ALERTA: Venta con pérdida
        guardada.setCantidadItems(2);

        when(rentabilidadVentaRepository.save(any(RentabilidadVenta.class))).thenReturn(guardada);

        // When
        RentabilidadVentaResponseDTO resultado = rentabilidadService.calcularRentabilidadVenta(100);

        // Then
        assertNotNull(resultado);
        assertTrue(resultado.getVentaBajoCosto());
        assertTrue(resultado.getUtilidadBruta().compareTo(BigDecimal.ZERO) < 0);
        assertEquals("PERDIDA", resultado.getAlertaCalidad());
    }

    @Test
    @DisplayName("Debe consultar rentabilidad por venta existente")
    void testConsultarPorVentaExitosa() {
        // Given
        RentabilidadVenta rentabilidad = new RentabilidadVenta();
        rentabilidad.setId(1L);
        rentabilidad.setVentaId(100);
        rentabilidad.setCostoTotal(new BigDecimal("600.00"));
        rentabilidad.setPrecioVentaTotal(new BigDecimal("1000.00"));
        rentabilidad.setUtilidadBruta(new BigDecimal("400.00"));
        rentabilidad.setMargenPorcentaje(new BigDecimal("40.00"));
        rentabilidad.setVentaBajoCosto(false);
        rentabilidad.setCantidadItems(2);

        when(rentabilidadVentaRepository.findByVentaId(100)).thenReturn(Optional.of(rentabilidad));
        when(ventaRepository.findById(100)).thenReturn(Optional.of(ventaTest));

        // When
        RentabilidadVentaResponseDTO resultado = rentabilidadService.consultarPorVenta(100);

        // Then
        assertNotNull(resultado);
        assertEquals(100, resultado.getVentaId());
        assertEquals(new BigDecimal("400.00"), resultado.getUtilidadBruta());
    }

    @Test
    @DisplayName("Debe lanzar excepción si no existe análisis de la venta")
    void testConsultarPorVentaNoExisteAnalisis() {
        // Given
        when(rentabilidadVentaRepository.findByVentaId(999)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            rentabilidadService.consultarPorVenta(999);
        });
    }

    @Test
    @DisplayName("Debe generar análisis por producto en un período")
    void testGenerarAnalisisPorProducto() {
        // Given
        LocalDate fechaInicio = LocalDate.of(2024, 1, 1);
        LocalDate fechaFin = LocalDate.of(2024, 1, 31);
        AnalisisRentabilidadRequestDTO request = new AnalisisRentabilidadRequestDTO(fechaInicio, fechaFin);

        List<Venta> ventas = Arrays.asList(ventaTest);
        when(ventaRepository.findByFechaVentaBetween(any(), any())).thenReturn(ventas);
        when(detalleVentaRepository.findByVentaIdIn(anyList())).thenReturn(detallesTest);
        when(inventarioRepository.findByIdSkuInterno(anyString()))
                .thenReturn(Arrays.asList(inventarioTest));

        RentabilidadProducto analisisGuardado = new RentabilidadProducto();
        analisisGuardado.setId(1L);
        analisisGuardado.setSkuInterno("SKU001");
        analisisGuardado.setPeriodoInicio(fechaInicio);
        analisisGuardado.setPeriodoFin(fechaFin);
        analisisGuardado.setCantidadVendida(10);
        analisisGuardado.setCostoPromedioUnitario(new BigDecimal("30.00"));
        analisisGuardado.setPrecioPromedioVenta(new BigDecimal("50.00"));
        analisisGuardado.setUtilidadTotalGenerada(new BigDecimal("200.00"));
        analisisGuardado.setMargenPromedioPorcentaje(new BigDecimal("40.00"));
        analisisGuardado.setNumeroVentas(1);

        when(rentabilidadProductoRepository.save(any(RentabilidadProducto.class)))
                .thenReturn(analisisGuardado);

        // When
        List<RentabilidadProductoResponseDTO> resultado = 
                rentabilidadService.generarAnalisisPorProducto(request);

        // Then
        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        verify(rentabilidadProductoRepository).deleteByPeriodoInicioAndPeriodoFin(fechaInicio, fechaFin);
        verify(rentabilidadProductoRepository, atLeastOnce()).save(any(RentabilidadProducto.class));
    }

    @Test
    @DisplayName("Debe validar que fechaFin no sea anterior a fechaInicio")
    void testGenerarAnalisisConFechasInvalidas() {
        // Given
        LocalDate fechaInicio = LocalDate.of(2024, 1, 31);
        LocalDate fechaFin = LocalDate.of(2024, 1, 1); // Fecha fin ANTES de inicio
        AnalisisRentabilidadRequestDTO request = new AnalisisRentabilidadRequestDTO(fechaInicio, fechaFin);

        // When & Then
        assertThrows(BusinessException.class, () -> {
            rentabilidadService.generarAnalisisPorProducto(request);
        });
    }

    @Test
    @DisplayName("Debe obtener productos más rentables")
    void testObtenerProductosMasRentables() {
        // Given
        LocalDate fechaInicio = LocalDate.of(2024, 1, 1);
        LocalDate fechaFin = LocalDate.of(2024, 1, 31);
        int limite = 5;

        RentabilidadProducto prod1 = new RentabilidadProducto();
        prod1.setId(1L);
        prod1.setSkuInterno("SKU001");
        prod1.setPeriodoInicio(fechaInicio);
        prod1.setPeriodoFin(fechaFin);
        prod1.setCantidadVendida(100);
        prod1.setCostoPromedioUnitario(new BigDecimal("30.00"));
        prod1.setPrecioPromedioVenta(new BigDecimal("60.00"));
        prod1.setUtilidadTotalGenerada(new BigDecimal("5000.00"));
        prod1.setMargenPromedioPorcentaje(new BigDecimal("50.00"));
        prod1.setNumeroVentas(10);

        when(rentabilidadProductoRepository.obtenerProductosMasRentables(fechaInicio, fechaFin, limite))
                .thenReturn(Arrays.asList(prod1));

        // When
        List<RentabilidadProductoResponseDTO> resultado = 
                rentabilidadService.obtenerProductosMasRentables(fechaInicio, fechaFin, limite);

        // Then
        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        assertEquals("SKU001", resultado.get(0).getSkuInterno());
    }

    @Test
    @DisplayName("Debe obtener ventas bajo costo")
    void testObtenerVentasBajoCosto() {
        // Given
        RentabilidadVenta ventaPerdida = new RentabilidadVenta();
        ventaPerdida.setId(1L);
        ventaPerdida.setVentaId(100);
        ventaPerdida.setVentaBajoCosto(true);
        ventaPerdida.setUtilidadBruta(new BigDecimal("-100.00"));
        ventaPerdida.setCostoTotal(new BigDecimal("1100.00"));
        ventaPerdida.setPrecioVentaTotal(new BigDecimal("1000.00"));
        ventaPerdida.setMargenPorcentaje(new BigDecimal("-10.00"));
        ventaPerdida.setCantidadItems(2);

        when(rentabilidadVentaRepository.findByVentaBajoCostoTrue())
                .thenReturn(Arrays.asList(ventaPerdida));
        when(ventaRepository.findById(100)).thenReturn(Optional.of(ventaTest));

        // When
        List<RentabilidadVentaResponseDTO> resultado = rentabilidadService.obtenerVentasBajoCosto();

        // Then
        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        assertTrue(resultado.get(0).getVentaBajoCosto());
        assertTrue(resultado.get(0).getUtilidadBruta().compareTo(BigDecimal.ZERO) < 0);
    }

    @Test
    @DisplayName("Debe calcular estadísticas de rentabilidad")
    void testObtenerEstadisticas() {
        // Given
        LocalDate fechaInicio = LocalDate.of(2024, 1, 1);
        LocalDate fechaFin = LocalDate.of(2024, 1, 31);

        when(rentabilidadVentaRepository.calcularUtilidadTotalPeriodo(any(), any()))
                .thenReturn(new BigDecimal("50000.00"));
        when(rentabilidadVentaRepository.calcularMargenPromedioPeriodo(any(), any()))
                .thenReturn(new BigDecimal("35.50"));
        when(ventaRepository.countByFechaVentaBetween(any(), any())).thenReturn(100L);
        when(rentabilidadVentaRepository.contarVentasBajoCostoPeriodo(any(), any())).thenReturn(5L);

        RentabilidadVenta masRentable = new RentabilidadVenta();
        masRentable.setUtilidadBruta(new BigDecimal("2000.00"));
        when(rentabilidadVentaRepository.obtenerVentasMasRentables(1))
                .thenReturn(Arrays.asList(masRentable));

        RentabilidadVenta menosRentable = new RentabilidadVenta();
        menosRentable.setUtilidadBruta(new BigDecimal("-100.00"));
        when(rentabilidadVentaRepository.obtenerVentasMenosRentables(1))
                .thenReturn(Arrays.asList(menosRentable));

        when(rentabilidadProductoRepository.obtenerProductosMasRentables(any(), any(), eq(1)))
                .thenReturn(Arrays.asList());
        when(rentabilidadProductoRepository.obtenerProductosMenosRentables(any(), any(), eq(1)))
                .thenReturn(Arrays.asList());

        // When
        EstadisticasRentabilidadDTO resultado = 
                rentabilidadService.obtenerEstadisticas(fechaInicio, fechaFin);

        // Then
        assertNotNull(resultado);
        assertEquals(new BigDecimal("50000.00"), resultado.getUtilidadTotalPeriodo());
        assertEquals(new BigDecimal("35.50"), resultado.getMargenPromedioPorcentaje());
        assertEquals(100L, resultado.getTotalVentasAnalizadas());
        assertEquals(5L, resultado.getVentasBajoCosto());
        assertEquals(new BigDecimal("5.00"), resultado.getPorcentajeVentasBajoCosto()); // 5/100 * 100
    }

    @Test
    @DisplayName("Debe clasificar calidad de venta según margen")
    void testClasificacionCalidadVenta() {
        // Given - Venta con margen excelente (>= 30%)
        inventarioTest.setCostoPromedioPonderado(new BigDecimal("20.00"));
        ventaTest.setTotal(new BigDecimal("1000.00"));

        when(ventaRepository.findById(100)).thenReturn(Optional.of(ventaTest));
        when(rentabilidadVentaRepository.findByVentaId(100)).thenReturn(Optional.empty());
        when(detalleVentaRepository.findByVentaId(100)).thenReturn(detallesTest);
        when(inventarioRepository.findByIdSkuInternoAndIdSucursalId(anyString(), anyInt()))
                .thenReturn(Optional.of(inventarioTest));

        RentabilidadVenta guardada = new RentabilidadVenta();
        guardada.setId(1L);
        guardada.setVentaId(100);
        guardada.setCostoTotal(new BigDecimal("400.00")); // 20 * $20
        guardada.setPrecioVentaTotal(new BigDecimal("1000.00"));
        guardada.setUtilidadBruta(new BigDecimal("600.00"));
        guardada.setMargenPorcentaje(new BigDecimal("60.00")); // 60% - EXCELENTE
        guardada.setVentaBajoCosto(false);
        guardada.setCantidadItems(2);

        when(rentabilidadVentaRepository.save(any(RentabilidadVenta.class))).thenReturn(guardada);

        // When
        RentabilidadVentaResponseDTO resultado = rentabilidadService.calcularRentabilidadVenta(100);

        // Then
        assertEquals("EXCELENTE", resultado.getAlertaCalidad());
    }
}
