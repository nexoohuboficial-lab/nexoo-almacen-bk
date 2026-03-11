package com.nexoohub.almacen.prediccion.service;

import com.nexoohub.almacen.inventario.entity.InventarioSucursal;
import com.nexoohub.almacen.inventario.entity.InventarioSucursalId;
import com.nexoohub.almacen.inventario.entity.ProductoMaestro;
import com.nexoohub.almacen.inventario.repository.InventarioSucursalRepository;
import com.nexoohub.almacen.inventario.repository.ProductoMaestroRepository;
import com.nexoohub.almacen.prediccion.dto.GenerarPrediccionRequestDTO;
import com.nexoohub.almacen.prediccion.dto.PrediccionDemandaResponseDTO;
import com.nexoohub.almacen.prediccion.dto.RecomendacionCompraDTO;
import com.nexoohub.almacen.prediccion.entity.PrediccionDemanda;
import com.nexoohub.almacen.prediccion.mapper.PrediccionDemandaMapper;
import com.nexoohub.almacen.prediccion.repository.PrediccionDemandaRepository;
import com.nexoohub.almacen.sucursal.entity.Sucursal;
import com.nexoohub.almacen.sucursal.repository.SucursalRepository;
import com.nexoohub.almacen.ventas.entity.DetalleVenta;
import com.nexoohub.almacen.ventas.entity.Venta;
import com.nexoohub.almacen.ventas.repository.VentaRepository;
import jakarta.persistence.EntityNotFoundException;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para PrediccionDemandaService
 * 
 * @author NexooHub Development Team
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PrediccionDemandaService - Tests Unitarios")
class PrediccionDemandaServiceTest {

    @Mock
    private PrediccionDemandaRepository prediccionRepository;

    @Mock
    private VentaRepository ventaRepository;

    @Mock
    private InventarioSucursalRepository inventarioRepository;

    @Mock
    private ProductoMaestroRepository productoRepository;

    @Mock
    private SucursalRepository sucursalRepository;

    @Mock
    private PrediccionDemandaMapper mapper;

    @InjectMocks
    private PrediccionDemandaService service;

    private Sucursal sucursal;
    private ProductoMaestro producto;
    private InventarioSucursal inventario;

    @BeforeEach
    void setUp() {
        sucursal = new Sucursal();
        sucursal.setId(1);
        sucursal.setNombre("Sucursal Centro");

        producto = new ProductoMaestro();
        producto.setSkuInterno("PROD-001");
        producto.setNombreComercial("Producto Test");

        inventario = new InventarioSucursal();
        inventario.setStockActual(50);
    }

    // ==========================================
    // TESTS: Generación de Predicciones
    // ==========================================

    @Test
    @DisplayName("Debe generar predicción con método PROMEDIO_MOVIL correctamente")
    void testGenerarPrediccionPromedioMovil() {
        // Given
        GenerarPrediccionRequestDTO request = new GenerarPrediccionRequestDTO();
        request.setSucursalId(1);
        request.setPeriodoAnio(2026);
        request.setPeriodoMes(4);
        request.setMesesHistoricos(6);
        request.setDiasStockSeguridad(7);
        request.setMetodoCalculo("PROMEDIO_MOVIL");
        request.setSkuProducto("PROD-001");

        when(sucursalRepository.findById(1)).thenReturn(Optional.of(sucursal));
        when(prediccionRepository.findBySkuProductoAndSucursalIdAndPeriodoAnioAndPeriodoMes(
                anyString(), anyInt(), anyInt(), anyInt())).thenReturn(Optional.empty());
        
        // Mock ventas históricas: 30, 32, 28, 35, 30, 33 unidades
        mockVentasHistoricas(Arrays.asList(30, 32, 28, 35, 30, 33));
        
        when(inventarioRepository.findByIdSkuInternoAndIdSucursalId(anyString(), anyInt()))
                .thenReturn(Optional.of(inventario));

        when(prediccionRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
        when(productoRepository.findById(anyString())).thenReturn(Optional.of(producto));
        
        PrediccionDemandaResponseDTO responseDTO = new PrediccionDemandaResponseDTO();
        responseDTO.setDemandaPredicha(BigDecimal.valueOf(32.67));
        when(mapper.toDTO(any(), any(), any())).thenReturn(responseDTO);

        // When
        List<PrediccionDemandaResponseDTO> result = service.generarPredicciones(request);

        // Then
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        
        // Capturar la predicción guardada
        ArgumentCaptor<List<PrediccionDemanda>> captor = ArgumentCaptor.forClass(List.class);
        verify(prediccionRepository).saveAll(captor.capture());
        
        PrediccionDemanda prediccionGuardada = captor.getValue().get(0);
        assertThat(prediccionGuardada.getSkuProducto()).isEqualTo("PROD-001");
        assertThat(prediccionGuardada.getSucursalId()).isEqualTo(1);
        assertThat(prediccionGuardada.getPeriodoAnio()).isEqualTo(2026);
        assertThat(prediccionGuardada.getPeriodoMes()).isEqualTo(4);
        assertThat(prediccionGuardada.getMetodoCalculo()).isEqualTo("PROMEDIO_MOVIL");
        assertThat(prediccionGuardada.getDemandaPredicha()).isNotNull();
        assertThat(prediccionGuardada.getStockActual()).isEqualTo(50);
    }

    @Test
    @DisplayName("Debe generar predicción con método TENDENCIA_LINEAL y tendencia positiva")
    void testGenerarPrediccionTendenciaLinealPositiva() {
        // Given
        GenerarPrediccionRequestDTO request = new GenerarPrediccionRequestDTO();
        request.setSucursalId(1);
        request.setPeriodoAnio(2026);
        request.setPeriodoMes(4);
        request.setMesesHistoricos(4);
        request.setDiasStockSeguridad(7);
        request.setMetodoCalculo("TENDENCIA_LINEAL");
        request.setSkuProducto("PROD-001");

        when(sucursalRepository.findById(1)).thenReturn(Optional.of(sucursal));
        when(prediccionRepository.findBySkuProductoAndSucursalIdAndPeriodoAnioAndPeriodoMes(
                anyString(), anyInt(), anyInt(), anyInt())).thenReturn(Optional.empty());
        
        // Mock ventas con tendencia creciente: 25, 20, 15, 10 (orden inverso porque Collections.reverse)
        mockVentasHistoricas(Arrays.asList(25, 20, 15, 10));
        
        when(inventarioRepository.findByIdSkuInternoAndIdSucursalId(anyString(), anyInt()))
                .thenReturn(Optional.of(inventario));
        when(prediccionRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
        when(productoRepository.findById(anyString())).thenReturn(Optional.of(producto));
        when(mapper.toDTO(any(), any(), any())).thenReturn(new PrediccionDemandaResponseDTO());

        // When
        List<PrediccionDemandaResponseDTO> result = service.generarPredicciones(request);

        // Then
        assertThat(result).isNotEmpty();
        
        ArgumentCaptor<List<PrediccionDemanda>> captor = ArgumentCaptor.forClass(List.class);
        verify(prediccionRepository).saveAll(captor.capture());
        
        PrediccionDemanda prediccion = captor.getValue().get(0);
        assertThat(prediccion.getTendencia()).isNotNull();
        assertThat(prediccion.getTendencia().doubleValue()).isGreaterThan(0); // Tendencia positiva
    }

    @Test
    @DisplayName("Debe generar predicción con método ESTACIONAL correctamente")
    void testGenerarPrediccionEstacional() {
        // Given
        GenerarPrediccionRequestDTO request = new GenerarPrediccionRequestDTO();
        request.setSucursalId(1);
        request.setPeriodoAnio(2026);
        request.setPeriodoMes(12); // Diciembre
        request.setMesesHistoricos(12);
        request.setDiasStockSeguridad(10);
        request.setMetodoCalculo("ESTACIONAL");
        request.setSkuProducto("PROD-001");

        when(sucursalRepository.findById(1)).thenReturn(Optional.of(sucursal));
        when(prediccionRepository.findBySkuProductoAndSucursalIdAndPeriodoAnioAndPeriodoMes(
                anyString(), anyInt(), anyInt(), anyInt())).thenReturn(Optional.empty());
        
        // Mock ventas con patrón estacional (Diciembre alto)
        mockVentasHistoricas(Arrays.asList(20, 22, 25, 30, 28, 26, 24, 22, 25, 30, 35, 100));
        
        when(inventarioRepository.findByIdSkuInternoAndIdSucursalId(anyString(), anyInt()))
                .thenReturn(Optional.of(inventario));
        when(prediccionRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
        when(productoRepository.findById(anyString())).thenReturn(Optional.of(producto));
        when(mapper.toDTO(any(), any(), any())).thenReturn(new PrediccionDemandaResponseDTO());

        // When
        List<PrediccionDemandaResponseDTO> result = service.generarPredicciones(request);

        // Then
        assertThat(result).isNotEmpty();
        
        ArgumentCaptor<List<PrediccionDemanda>> captor = ArgumentCaptor.forClass(List.class);
        verify(prediccionRepository).saveAll(captor.capture());
        
        PrediccionDemanda prediccion = captor.getValue().get(0);
        assertThat(prediccion.getMetodoCalculo()).isEqualTo("ESTACIONAL");
        assertThat(prediccion.getDemandaPredicha()).isNotNull();
    }

    @Test
    @DisplayName("No debe generar predicción cuando no hay datos históricos")
    void testGenerarPrediccionSinDatosHistoricos() {
        // Given
        GenerarPrediccionRequestDTO request = new GenerarPrediccionRequestDTO();
        request.setSucursalId(1);
        request.setPeriodoAnio(2026);
        request.setPeriodoMes(4);
        request.setMesesHistoricos(6);
        request.setDiasStockSeguridad(7);
        request.setMetodoCalculo("PROMEDIO_MOVIL");
        request.setSkuProducto("PROD-001");

        when(sucursalRepository.findById(1)).thenReturn(Optional.of(sucursal));
        when(prediccionRepository.findBySkuProductoAndSucursalIdAndPeriodoAnioAndPeriodoMes(
                anyString(), anyInt(), anyInt(), anyInt())).thenReturn(Optional.empty());
        
        // Mock sin ventas históricas
        mockVentasHistoricas(Arrays.asList());
        
        // Mock saveAll
        when(prediccionRepository.saveAll(anyList())).thenReturn(new ArrayList<>());

        // When
        List<PrediccionDemandaResponseDTO> result = service.generarPredicciones(request);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Debe generar predicción con un solo periodo histórico")
    void testGenerarPrediccionUnSoloPeriodo() {
        // Given
        GenerarPrediccionRequestDTO request = new GenerarPrediccionRequestDTO();
        request.setSucursalId(1);
        request.setPeriodoAnio(2026);
        request.setPeriodoMes(4);
        request.setMesesHistoricos(1); // Solo 1 mes histórico
        request.setDiasStockSeguridad(7);
        request.setMetodoCalculo("PROMEDIO_MOVIL");
        request.setSkuProducto("PROD-001");

        when(sucursalRepository.findById(1)).thenReturn(Optional.of(sucursal));
        when(prediccionRepository.findBySkuProductoAndSucursalIdAndPeriodoAnioAndPeriodoMes(
                anyString(), anyInt(), anyInt(), anyInt())).thenReturn(Optional.empty());
        
        // Mock con un solo periodo
        mockVentasHistoricas(Arrays.asList(50));
        
        when(inventarioRepository.findByIdSkuInternoAndIdSucursalId(anyString(), anyInt()))
                .thenReturn(Optional.of(inventario));
        when(prediccionRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
        when(productoRepository.findById(anyString())).thenReturn(Optional.of(producto));
        when(mapper.toDTO(any(), any(), any())).thenReturn(new PrediccionDemandaResponseDTO());

        // When
        List<PrediccionDemandaResponseDTO> result = service.generarPredicciones(request);

        // Then
        assertThat(result).isNotEmpty();
        
        ArgumentCaptor<List<PrediccionDemanda>> captor = ArgumentCaptor.forClass(List.class);
        verify(prediccionRepository).saveAll(captor.capture());
        
        PrediccionDemanda prediccion = captor.getValue().get(0);
        // Con 1 solo periodo no se puede calcular tendencia (requiere 2+)
        assertThat(prediccion.getTendencia()).isEqualTo(BigDecimal.ZERO);
        assertThat(prediccion.getDemandaPredicha()).isNotNull();
    }

    @Test
    @DisplayName("Debe detectar tendencia negativa correctamente")
    void testGenerarPrediccionTendenciaNegativa() {
        // Given
        GenerarPrediccionRequestDTO request = new GenerarPrediccionRequestDTO();
        request.setSucursalId(1);
        request.setPeriodoAnio(2026);
        request.setPeriodoMes(4);
        request.setMesesHistoricos(4);
        request.setDiasStockSeguridad(7);
        request.setMetodoCalculo("TENDENCIA_LINEAL");
        request.setSkuProducto("PROD-001");

        when(sucursalRepository.findById(1)).thenReturn(Optional.of(sucursal));
        when(prediccionRepository.findBySkuProductoAndSucursalIdAndPeriodoAnioAndPeriodoMes(
                anyString(), anyInt(), anyInt(), anyInt())).thenReturn(Optional.empty());
        
        // Mock ventas con tendencia decreciente: 40, 60, 80, 100 (orden inverso porque Collections.reverse)
        mockVentasHistoricas(Arrays.asList(40, 60, 80, 100));
        
        when(inventarioRepository.findByIdSkuInternoAndIdSucursalId(anyString(), anyInt()))
                .thenReturn(Optional.of(inventario));
        when(prediccionRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
        when(productoRepository.findById(anyString())).thenReturn(Optional.of(producto));
        when(mapper.toDTO(any(), any(), any())).thenReturn(new PrediccionDemandaResponseDTO());

        // When
        List<PrediccionDemandaResponseDTO> result = service.generarPredicciones(request);

        // Then
        assertThat(result).isNotEmpty();
        
        ArgumentCaptor<List<PrediccionDemanda>> captor = ArgumentCaptor.forClass(List.class);
        verify(prediccionRepository).saveAll(captor.capture());
        
        PrediccionDemanda prediccion = captor.getValue().get(0);
        assertThat(prediccion.getTendencia()).isNotNull();
        assertThat(prediccion.getTendencia().doubleValue()).isLessThan(0); // Tendencia negativa
    }

    @Test
    @DisplayName("Debe calcular stock de seguridad correctamente")
    void testCalcularStockSeguridad() {
        // Given
        GenerarPrediccionRequestDTO request = new GenerarPrediccionRequestDTO();
        request.setSucursalId(1);
        request.setPeriodoAnio(2026);
        request.setPeriodoMes(4); // Abril tiene 30 días
        request.setMesesHistoricos(3);
        request.setDiasStockSeguridad(10); // 10 días de seguridad
        request.setMetodoCalculo("PROMEDIO_MOVIL");
        request.setSkuProducto("PROD-001");

        when(sucursalRepository.findById(1)).thenReturn(Optional.of(sucursal));
        when(prediccionRepository.findBySkuProductoAndSucursalIdAndPeriodoAnioAndPeriodoMes(
                anyString(), anyInt(), anyInt(), anyInt())).thenReturn(Optional.empty());
        
        // Mock ventas: 60 unidades promedio
        mockVentasHistoricas(Arrays.asList(60, 60, 60));
        
        when(inventarioRepository.findByIdSkuInternoAndIdSucursalId(anyString(), anyInt()))
                .thenReturn(Optional.of(inventario));
        when(prediccionRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
        when(productoRepository.findById(anyString())).thenReturn(Optional.of(producto));
        when(mapper.toDTO(any(), any(), any())).thenReturn(new PrediccionDemandaResponseDTO());

        // When
        List<PrediccionDemandaResponseDTO> result = service.generarPredicciones(request);

        // Then
        ArgumentCaptor<List<PrediccionDemanda>> captor = ArgumentCaptor.forClass(List.class);
        verify(prediccionRepository).saveAll(captor.capture());
        
        PrediccionDemanda prediccion = captor.getValue().get(0);
        
        // Demanda diaria = 60 / 30 = 2 unidades/día
        // Stock seguridad = 2 * 10 = 20 unidades
        assertThat(prediccion.getStockSeguridad()).isEqualTo(20);
        
        // Stock sugerido = 60 (demanda) + 20 (seguridad) = 80
        assertThat(prediccion.getStockSugerido()).isEqualTo(80);
        
        // Cantidad comprar = 80 - 50 (stock actual) = 30
        assertThat(prediccion.getCantidadComprar()).isEqualTo(30);
    }

    @Test
    @DisplayName("Debe calcular nivel de confianza alto con ventas estables")
    void testCalcularNivelConfianzaAlto() {
        // Given
        GenerarPrediccionRequestDTO request = new GenerarPrediccionRequestDTO();
        request.setSucursalId(1);
        request.setPeriodoAnio(2026);
        request.setPeriodoMes(4);
        request.setMesesHistoricos(6);
        request.setDiasStockSeguridad(7);
        request.setMetodoCalculo("PROMEDIO_MOVIL");
        request.setSkuProducto("PROD-001");

        when(sucursalRepository.findById(1)).thenReturn(Optional.of(sucursal));
        when(prediccionRepository.findBySkuProductoAndSucursalIdAndPeriodoAnioAndPeriodoMes(
                anyString(), anyInt(), anyInt(), anyInt())).thenReturn(Optional.empty());
        
        // Mock ventas MUY estables: 50, 50, 50, 50, 50, 50
        mockVentasHistoricas(Arrays.asList(50, 50, 50, 50, 50, 50));
        
        when(inventarioRepository.findByIdSkuInternoAndIdSucursalId(anyString(), anyInt()))
                .thenReturn(Optional.of(inventario));
        when(prediccionRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
        when(productoRepository.findById(anyString())).thenReturn(Optional.of(producto));
        when(mapper.toDTO(any(), any(), any())).thenReturn(new PrediccionDemandaResponseDTO());

        // When
        List<PrediccionDemandaResponseDTO> result = service.generarPredicciones(request);

        // Then
        ArgumentCaptor<List<PrediccionDemanda>> captor = ArgumentCaptor.forClass(List.class);
        verify(prediccionRepository).saveAll(captor.capture());
        
        PrediccionDemanda prediccion = captor.getValue().get(0);
        
        // Con varianza 0, confianza debe ser 100%
        assertThat(prediccion.getNivelConfianza()).isEqualByComparingTo(BigDecimal.valueOf(100.00));
    }

    @Test
    @DisplayName("Debe calcular nivel de confianza bajo con ventas erráticas")
    void testCalcularNivelConfianzaBajo() {
        // Given
        GenerarPrediccionRequestDTO request = new GenerarPrediccionRequestDTO();
        request.setSucursalId(1);
        request.setPeriodoAnio(2026);
        request.setPeriodoMes(4);
        request.setMesesHistoricos(6);
        request.setDiasStockSeguridad(7);
        request.setMetodoCalculo("PROMEDIO_MOVIL");
        request.setSkuProducto("PROD-001");

        when(sucursalRepository.findById(1)).thenReturn(Optional.of(sucursal));
        when(prediccionRepository.findBySkuProductoAndSucursalIdAndPeriodoAnioAndPeriodoMes(
                anyString(), anyInt(), anyInt(), anyInt())).thenReturn(Optional.empty());
        
        // Mock ventas ERRÁTICAS: 10, 50, 5, 80, 20, 100
        mockVentasHistoricas(Arrays.asList(10, 50, 5, 80, 20, 100));
        
        when(inventarioRepository.findByIdSkuInternoAndIdSucursalId(anyString(), anyInt()))
                .thenReturn(Optional.of(inventario));
        when(prediccionRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
        when(productoRepository.findById(anyString())).thenReturn(Optional.of(producto));
        when(mapper.toDTO(any(), any(), any())).thenReturn(new PrediccionDemandaResponseDTO());

        // When
        List<PrediccionDemandaResponseDTO> result = service.generarPredicciones(request);

        // Then
        ArgumentCaptor<List<PrediccionDemanda>> captor = ArgumentCaptor.forClass(List.class);
        verify(prediccionRepository).saveAll(captor.capture());
        
        PrediccionDemanda prediccion = captor.getValue().get(0);
        
        // Con alta variabilidad, confianza debe ser baja (< 70%)
        assertThat(prediccion.getNivelConfianza().doubleValue()).isLessThan(70.0);
    }

    // ==========================================
    // TESTS: Consultas
    // ==========================================

    @Test
    @DisplayName("Debe obtener predicción por ID correctamente")
    void testObtenerPrediccionPorId() {
        // Given
        PrediccionDemanda prediccion = new PrediccionDemanda();
        prediccion.setId(1);
        prediccion.setSkuProducto("PROD-001");
        prediccion.setSucursalId(1);

        when(prediccionRepository.findById(1)).thenReturn(Optional.of(prediccion));
        when(productoRepository.findById("PROD-001")).thenReturn(Optional.of(producto));
        when(sucursalRepository.findById(1)).thenReturn(Optional.of(sucursal));
        
        PrediccionDemandaResponseDTO responseDTO = new PrediccionDemandaResponseDTO();
        responseDTO.setId(1);
        when(mapper.toDTO(any(), any(), any())).thenReturn(responseDTO);

        // When
        PrediccionDemandaResponseDTO result = service.obtenerPrediccion(1);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        verify(prediccionRepository).findById(1);
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando predicción no existe")
    void testObtenerPrediccionNoExiste() {
        // Given
        when(prediccionRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> service.obtenerPrediccion(999))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Predicción no encontrada");
    }

    @Test
    @DisplayName("Debe obtener recomendaciones de compra correctamente")
    void testObtenerRecomendacionesCompra() {
        // Given
        when(sucursalRepository.findById(1)).thenReturn(Optional.of(sucursal));
        
        PrediccionDemanda pred1 = new PrediccionDemanda();
        pred1.setId(1);
        pred1.setSkuProducto("PROD-001");
        pred1.setCantidadComprar(50);
        
        PrediccionDemanda pred2 = new PrediccionDemanda();
        pred2.setId(2);
        pred2.setSkuProducto("PROD-002");
        pred2.setCantidadComprar(30);
        
        when(prediccionRepository.findProductosParaComprar(1, 2026, 4))
                .thenReturn(Arrays.asList(pred1, pred2));
        when(prediccionRepository.countBySucursalIdAndPeriodoAnioAndPeriodoMes(1, 2026, 4))
                .thenReturn(10L);
        when(productoRepository.findById(anyString())).thenReturn(Optional.of(producto));
        when(mapper.toDTO(any(), any(), any())).thenReturn(new PrediccionDemandaResponseDTO());

        // When
        RecomendacionCompraDTO result = service.obtenerRecomendacionesCompra(1, 2026, 4);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getSucursalId()).isEqualTo(1);
        assertThat(result.getTotalProductos()).isEqualTo(10);
        assertThat(result.getProductosAComprar()).isEqualTo(2);
        assertThat(result.getUnidadesTotalesComprar()).isEqualTo(80); // 50 + 30
        assertThat(result.getPeriodoTexto()).contains("Abril");
        assertThat(result.getPeriodoTexto()).contains("2026");
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando sucursal no existe en recomendaciones")
    void testObtenerRecomendacionesSucursalNoExiste() {
        // Given
        when(sucursalRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> service.obtenerRecomendacionesCompra(999, 2026, 4))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Sucursal no encontrada");
    }

    @Test
    @DisplayName("Debe generar predicciones para todos los productos cuando skuProducto es null")
    void testGenerarPrediccionesTodosProductos() {
        // Given
        GenerarPrediccionRequestDTO request = new GenerarPrediccionRequestDTO();
        request.setSucursalId(1);
        request.setPeriodoAnio(2026);
        request.setPeriodoMes(4);
        request.setMesesHistoricos(3);
        request.setDiasStockSeguridad(7);
        request.setMetodoCalculo("PROMEDIO_MOVIL");
        request.setSkuProducto(null); // Todos los productos

        when(sucursalRepository.findById(1)).thenReturn(Optional.of(sucursal));
        
        // Mock 2 productos en inventario
        InventarioSucursal inv1 = new InventarioSucursal();
        InventarioSucursalId id1 = new InventarioSucursalId(1, "PROD-001");
        inv1.setId(id1);
        inv1.setStockActual(50);
        
        InventarioSucursal inv2 = new InventarioSucursal();
        InventarioSucursalId id2 = new InventarioSucursalId(1, "PROD-002");
        inv2.setId(id2);
        inv2.setStockActual(30);
        
        when(inventarioRepository.findByIdSucursalId(1))
                .thenReturn(Arrays.asList(inv1, inv2));
        
        when(prediccionRepository.findBySkuProductoAndSucursalIdAndPeriodoAnioAndPeriodoMes(
                anyString(), anyInt(), anyInt(), anyInt())).thenReturn(Optional.empty());
        
        // Mock necesita suficientes datos para 2 productos x 3 meses = 6 llamadas
        mockVentasHistoricas(Arrays.asList(30, 32, 28, 25, 27, 26));
        
        when(inventarioRepository.findByIdSkuInternoAndIdSucursalId(anyString(), anyInt()))
                .thenReturn(Optional.of(inventario));
        when(prediccionRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
        when(productoRepository.findById(anyString())).thenReturn(Optional.of(producto));
        when(mapper.toDTO(any(), any(), any())).thenReturn(new PrediccionDemandaResponseDTO());

        // When
        List<PrediccionDemandaResponseDTO> result = service.generarPredicciones(request);

        // Then
        assertThat(result).hasSize(2); // Debe generar para 2 productos
        verify(inventarioRepository).findByIdSucursalId(1);
    }

    @Test
    @DisplayName("No debe fallar si un producto no tiene ventas al generar múltiples predicciones")
    void testGenerarPrediccionesMultiplesConProductoSinVentas() {
        // Given
        GenerarPrediccionRequestDTO request = new GenerarPrediccionRequestDTO();
        request.setSucursalId(1);
        request.setPeriodoAnio(2026);
        request.setPeriodoMes(4);
        request.setMesesHistoricos(3);
        request.setDiasStockSeguridad(7);
        request.setMetodoCalculo("PROMEDIO_MOVIL");
        request.setSkuProducto(null);

        when(sucursalRepository.findById(1)).thenReturn(Optional.of(sucursal));
        
        InventarioSucursal inv1 = new InventarioSucursal();
        InventarioSucursalId id1 = new InventarioSucursalId(1, "PROD-001");
        inv1.setId(id1);
        
        InventarioSucursal inv2 = new InventarioSucursal();
        InventarioSucursalId id2 = new InventarioSucursalId(1, "PROD-002");
        inv2.setId(id2);
        
        when(inventarioRepository.findByIdSucursalId(1))
                .thenReturn(Arrays.asList(inv1, inv2));
        
        when(prediccionRepository.findBySkuProductoAndSucursalIdAndPeriodoAnioAndPeriodoMes(
                anyString(), anyInt(), anyInt(), anyInt())).thenReturn(Optional.empty());
        
        // PROD-001 tiene ventas (3 llamadas), PROD-002 no tiene ventas (3 llamadas con lista vacía)
        // Total 6 llamadas: 3 para PROD-001, 3 para PROD-002
        java.util.Queue<Integer> queue = new java.util.LinkedList<>(Arrays.asList(30, 32, 28, 0, 0, 0));
        when(ventaRepository.findByFechaRangoConDetalles(any(), any()))
                .thenAnswer(invocation -> {
                    Integer cantidad = queue.poll();
                    if (cantidad == null || cantidad == 0) {
                        return new ArrayList<>();
                    }
                    Venta venta = new Venta();
                    venta.setSucursalId(1);
                    DetalleVenta detalle = new DetalleVenta();
                    detalle.setSkuInterno("PROD-001");
                    detalle.setCantidad(cantidad);
                    venta.setDetalles(Arrays.asList(detalle));
                    return Arrays.asList(venta);
                });
        
        when(inventarioRepository.findByIdSkuInternoAndIdSucursalId(anyString(), anyInt()))
                .thenReturn(Optional.of(inventario));
        when(prediccionRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
        when(productoRepository.findById(anyString())).thenReturn(Optional.of(producto));
        when(mapper.toDTO(any(), any(), any())).thenReturn(new PrediccionDemandaResponseDTO());

        // When
        List<PrediccionDemandaResponseDTO> result = service.generarPredicciones(request);

        // Then
        // Debe generar 2 predicciones: PROD-001 con demanda real, PROD-002 con demanda = 0
        // El servicio no salta productos sin ventas, genera predicción con demanda 0
        assertThat(result).hasSize(2);
    }

    // ==========================================
    // HELPERS
    // ==========================================

    private void mockVentasHistoricas(List<Integer> cantidades) {
        // Reset mock
        reset(ventaRepository);
        
        if (cantidades.isEmpty()) {
            when(ventaRepository.findByFechaRangoConDetalles(any(LocalDateTime.class), any(LocalDateTime.class)))
                    .thenReturn(new ArrayList<>());
            return;
        }
        
        // Crear Queue para consumir cantidades en orden
        java.util.Queue<Integer> queue = new java.util.LinkedList<>(cantidades);
        
        when(ventaRepository.findByFechaRangoConDetalles(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenAnswer(invocation -> {
                    if (queue.isEmpty()) {
                        return new ArrayList<>();
                    }
                    
                    Integer cantidad = queue.poll();
                    
                    // Crear venta con la cantidad correspondiente
                    Venta venta = new Venta();
                    venta.setSucursalId(1);
                    
                    DetalleVenta detalle = new DetalleVenta();
                    detalle.setSkuInterno("PROD-001");
                    detalle.setCantidad(cantidad);
                    
                    venta.setDetalles(Arrays.asList(detalle));
                    return Arrays.asList(venta);
                });
    }
}
