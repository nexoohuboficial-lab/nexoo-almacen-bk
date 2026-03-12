package com.nexoohub.almacen.metricas.service;

import com.nexoohub.almacen.inventario.entity.InventarioSucursal;
import com.nexoohub.almacen.inventario.entity.InventarioSucursalId;
import com.nexoohub.almacen.inventario.repository.InventarioSucursalRepository;
import com.nexoohub.almacen.inventario.repository.ProductoMaestroRepository;
import com.nexoohub.almacen.metricas.dto.AnalisisInventarioRequestDTO;
import com.nexoohub.almacen.metricas.dto.MetricaInventarioResponseDTO;
import com.nexoohub.almacen.metricas.dto.ProductoInventarioDTO;
import com.nexoohub.almacen.metricas.entity.MetricaInventario;
import com.nexoohub.almacen.metricas.repository.MetricaInventarioRepository;
import com.nexoohub.almacen.rentabilidad.repository.RentabilidadVentaRepository;
import com.nexoohub.almacen.sucursal.repository.SucursalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para MetricaInventarioService.
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("MetricaInventarioService - Tests de Métricas de Inventario")
class MetricaInventarioServiceTest {

    @Mock
    private MetricaInventarioRepository metricaInventarioRepository;

    @Mock
    private InventarioSucursalRepository inventarioSucursalRepository;

    @Mock
    private ProductoMaestroRepository productoMaestroRepository;

    @Mock
    private RentabilidadVentaRepository rentabilidadVentaRepository;

    @Mock
    private SucursalRepository sucursalRepository;

    @InjectMocks
    private MetricaInventarioService metricaInventarioService;

    private List<InventarioSucursal> inventariosTest;
    private LocalDate fechaCorte;

    @BeforeEach
    void setUp() {
        fechaCorte = LocalDate.of(2024, 12, 31);

        // Inventario 1: Stock saludable
        InventarioSucursal inv1 = new InventarioSucursal();
        InventarioSucursalId id1 = new InventarioSucursalId();
        id1.setSkuInterno("SKU001");
        id1.setSucursalId(1);
        inv1.setId(id1);
        inv1.setStockActual(100);
        inv1.setStockMinimoSucursal(20);
        inv1.setCostoPromedioPonderado(new BigDecimal("50.00"));
        inv1.setFechaCaducidad(null);

        // Inventario 2: Stock bajo
        InventarioSucursal inv2 = new InventarioSucursal();
        InventarioSucursalId id2 = new InventarioSucursalId();
        id2.setSkuInterno("SKU002");
        id2.setSucursalId(1);
        inv2.setId(id2);
        inv2.setStockActual(10);
        inv2.setStockMinimoSucursal(50);
        inv2.setCostoPromedioPonderado(new BigDecimal("30.00"));
        inv2.setFechaCaducidad(null);

        // Inventario 3: Sin stock
        InventarioSucursal inv3 = new InventarioSucursal();
        InventarioSucursalId id3 = new InventarioSucursalId();
        id3.setSkuInterno("SKU003");
        id3.setSucursalId(1);
        inv3.setId(id3);
        inv3.setStockActual(0);
        inv3.setStockMinimoSucursal(30);
        inv3.setCostoPromedioPonderado(new BigDecimal("20.00"));
        inv3.setFechaCaducidad(null);

        // Inventario 4: Próximo a caducar
        InventarioSucursal inv4 = new InventarioSucursal();
        InventarioSucursalId id4 = new InventarioSucursalId();
        id4.setSkuInterno("SKU004");
        id4.setSucursalId(1);
        inv4.setId(id4);
        inv4.setStockActual(50);
        inv4.setStockMinimoSucursal(10);
        inv4.setCostoPromedioPonderado(new BigDecimal("15.00"));
        inv4.setFechaCaducidad(LocalDate.now().plusDays(15)); // Caduca en 15 días

        inventariosTest = Arrays.asList(inv1, inv2, inv3, inv4);
    }

    // ==================== TESTS DE GENERACIÓN DE ANÁLISIS ====================

    @Test
    @DisplayName("Debe generar análisis de inventario consolidado con snapshot")
    void testGenerarAnalisisConsolidadoConSnapshotGuardado() {
        // Given
        AnalisisInventarioRequestDTO request = new AnalisisInventarioRequestDTO();
        request.setFechaCorte(fechaCorte);
        request.setSucursalId(null); // Consolidado
        request.setGuardarSnapshot(true);
        request.setDiasPeriodoRotacion(30);

        when(inventarioSucursalRepository.findAll()).thenReturn(inventariosTest);

        // Costo de ventas del período: $10,000
        when(rentabilidadVentaRepository.calcularCostoTotalPeriodo(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(new BigDecimal("10000.00"));

        MetricaInventario metricaGuardada = new MetricaInventario();
        metricaGuardada.setId(1L);
        metricaGuardada.setFechaCorte(fechaCorte);
        metricaGuardada.setTotalSkus(4);
        metricaGuardada.setStockDisponibleTotal(160); // 100 + 10 + 0 + 50
        metricaGuardada.setSkusBajoStock(1); // SKU002
        metricaGuardada.setSkusSinStock(1); // SKU003
        metricaGuardada.setSkusProximosCaducar(1); // SKU004
        metricaGuardada.setValorTotalInventario(new BigDecimal("6050.00")); // (100*50)+(10*30)+(0*20)+(50*15)
        metricaGuardada.setIndiceRotacion(new BigDecimal("60.3960")); // (10000 / 6050) * (365 / 30)
        metricaGuardada.setDiasInventario(new BigDecimal("6.04"));
        metricaGuardada.setCostoVentasPeriodo(new BigDecimal("10000.00"));
        metricaGuardada.setDiasPeriodoRotacion(30);
        metricaGuardada.setTasaQuiebreStock(new BigDecimal("25.00")); // 1 sin stock / 4 total
        metricaGuardada.setSaludInventario("SALUDABLE");
        metricaGuardada.setClasificacionRotacion("ALTA");

        when(metricaInventarioRepository.save(any(MetricaInventario.class))).thenReturn(metricaGuardada);

        // When
        MetricaInventarioResponseDTO resultado = metricaInventarioService.generarAnalisisInventario(request);

        // Then
        assertNotNull(resultado);
        assertEquals(4, resultado.getTotalSkus());
        assertEquals(160, resultado.getStockDisponibleTotal());
        assertEquals(1, resultado.getSkusBajoStock());
        assertEquals(1, resultado.getSkusSinStock());
        assertEquals(1, resultado.getSkusProximosCaducar());
        assertEquals(new BigDecimal("6050.00"), resultado.getValorTotalInventario());
        assertEquals("SALUDABLE", resultado.getSaludInventario());
        assertEquals("ALTA", resultado.getClasificacionRotacion());

        verify(metricaInventarioRepository, times(1)).deleteBySucursalIdAndFechaCorte(null, fechaCorte);
        verify(metricaInventarioRepository, times(1)).save(any(MetricaInventario.class));
    }

    @Test
    @DisplayName("Debe generar análisis sin guardar snapshot")
    void testGenerarAnalisisSinGuardarSnapshot() {
        // Given
        AnalisisInventarioRequestDTO request = new AnalisisInventarioRequestDTO();
        request.setFechaCorte(fechaCorte);
        request.setSucursalId(1);
        request.setGuardarSnapshot(false); // No guardar
        request.setDiasPeriodoRotacion(30);

        when(inventarioSucursalRepository.findByIdSucursalId(1)).thenReturn(inventariosTest);
        when(rentabilidadVentaRepository.calcularCostoTotalPeriodo(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(new BigDecimal("10000.00"));

        // When
        MetricaInventarioResponseDTO resultado = metricaInventarioService.generarAnalisisInventario(request);

        // Then
        assertNotNull(resultado);
        assertEquals(4, resultado.getTotalSkus());
        verify(metricaInventarioRepository, never()).save(any(MetricaInventario.class));
        verify(metricaInventarioRepository, never()).deleteBySucursalIdAndFechaCorte(anyInt(), any(LocalDate.class));
    }

    // ==================== TESTS DE CONSULTA DE MÉTRICAS ====================

    @Test
    @DisplayName("Debe consultar snapshot existente en lugar de calcular")
    void testConsultarMetricasConSnapshotExistente() {
        // Given
        MetricaInventario metricaExistente = new MetricaInventario();
        metricaExistente.setId(1L);
        metricaExistente.setFechaCorte(fechaCorte);
        metricaExistente.setTotalSkus(4);
        metricaExistente.setValorTotalInventario(new BigDecimal("6050.00"));

        when(metricaInventarioRepository.findBySucursalIdIsNullAndFechaCorte(fechaCorte))
                .thenReturn(Optional.of(metricaExistente));

        // When
        MetricaInventarioResponseDTO resultado = metricaInventarioService.consultarMetricas(fechaCorte, null);

        // Then
        assertNotNull(resultado);
        assertEquals(4, resultado.getTotalSkus());
        assertEquals(new BigDecimal("6050.00"), resultado.getValorTotalInventario());
        verify(inventarioSucursalRepository, never()).findAll(); // No debe calcular
    }

    @Test
    @DisplayName("Debe calcular en tiempo real cuando no existe snapshot")
    void testConsultarMetricasSinSnapshotCalculaEnTiempoReal() {
        // Given
        when(metricaInventarioRepository.findBySucursalIdIsNullAndFechaCorte(fechaCorte))
                .thenReturn(Optional.empty());

        when(inventarioSucursalRepository.findAll()).thenReturn(inventariosTest);
        when(rentabilidadVentaRepository.calcularCostoTotalPeriodo(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(new BigDecimal("10000.00"));

        // When
        MetricaInventarioResponseDTO resultado = metricaInventarioService.consultarMetricas(fechaCorte, null);

        // Then
        assertNotNull(resultado);
        assertEquals(4, resultado.getTotalSkus());
        verify(inventarioSucursalRepository, times(1)).findAll(); // Debe calcular
    }

    // ==================== TESTS DE PRODUCTOS CON ALERTAS ====================

    @Test
    @DisplayName("Debe obtener productos con stock bajo mínimo")
    void testObtenerProductosBajoStock() {
        // Given
        when(inventarioSucursalRepository.findByIdSucursalId(1)).thenReturn(inventariosTest);

        // When
        List<ProductoInventarioDTO> productos = metricaInventarioService.obtenerProductosBajoStock(1, 10);

        // Then
        assertNotNull(productos);
        assertEquals(1, productos.size()); // Solo SKU002 tiene stock bajo (10 < 50)
        assertEquals("SKU002", productos.get(0).getSkuInterno());
        assertEquals("BAJO_STOCK", productos.get(0).getEstadoAlerta());
    }

    @Test
    @DisplayName("Debe obtener productos sin stock")
    void testObtenerProductosSinStock() {
        // Given
        when(inventarioSucursalRepository.findByIdSucursalId(1)).thenReturn(inventariosTest);

        // When
        List<ProductoInventarioDTO> productos = metricaInventarioService.obtenerProductosSinStock(1, 10);

        // Then
        assertNotNull(productos);
        assertEquals(1, productos.size()); // Solo SKU003 tiene stock = 0
        assertEquals("SKU003", productos.get(0).getSkuInterno());
        assertEquals("SIN_STOCK", productos.get(0).getEstadoAlerta());
    }

    @Test
    @DisplayName("Debe obtener productos próximos a caducar")
    void testObtenerProductosProximosCaducar() {
        // Given
        when(inventarioSucursalRepository.findAll()).thenReturn(inventariosTest);

        // When
        List<ProductoInventarioDTO> productos = metricaInventarioService.obtenerProductosProximosCaducar(null, 30, 10);

        // Then
        assertNotNull(productos);
        assertEquals(1, productos.size()); // Solo SKU004 caduca en 15 días
        assertEquals("SKU004", productos.get(0).getSkuInterno());
        assertEquals("PROXIMO_CADUCAR", productos.get(0).getEstadoAlerta());
    }

    // ==================== TESTS DE HISTÓRICOS ====================

    @Test
    @DisplayName("Debe obtener histórico de métricas consolidadas")
    void testObtenerHistoricoMetricasConsolidado() {
        // Given
        MetricaInventario metrica1 = new MetricaInventario();
        metrica1.setId(1L);
        metrica1.setFechaCorte(LocalDate.of(2024, 12, 31));
        metrica1.setTotalSkus(4);

        MetricaInventario metrica2 = new MetricaInventario();
        metrica2.setId(2L);
        metrica2.setFechaCorte(LocalDate.of(2024, 11, 30));
        metrica2.setTotalSkus(3);

        when(metricaInventarioRepository.findBySucursalIdIsNullOrderByFechaCorteDesc())
                .thenReturn(Arrays.asList(metrica1, metrica2));

        // When
        List<MetricaInventarioResponseDTO> historico = metricaInventarioService.obtenerHistoricoMetricas(null, 12);

        // Then
        assertNotNull(historico);
        assertEquals(2, historico.size());
        assertEquals(4, historico.get(0).getTotalSkus());
        assertEquals(3, historico.get(1).getTotalSkus());
    }

    // ==================== TESTS DE CÁLCULO DE VALOR ====================

    @Test
    @DisplayName("Debe calcular valor actual del inventario correctamente")
    void testCalcularValorInventarioActual() {
        // Given
        when(inventarioSucursalRepository.findAll()).thenReturn(inventariosTest);

        // When
        BigDecimal valorTotal = metricaInventarioService.calcularValorInventarioActual(null);

        // Then
        // Valor esperado: (100*50) + (10*30) + (0*20) + (50*15) = 5000 + 300 + 0 + 750 = 6050
        assertEquals(new BigDecimal("6050.00"), valorTotal);
    }

    @Test
    @DisplayName("Debe calcular valor cero cuando no hay inventarios")
    void testCalcularValorInventarioSinDatos() {
        // Given
        when(inventarioSucursalRepository.findAll()).thenReturn(Arrays.asList());

        // When
        BigDecimal valorTotal = metricaInventarioService.calcularValorInventarioActual(null);

        // Then
        assertEquals(BigDecimal.ZERO, valorTotal);
    }
}
