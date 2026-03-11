package com.nexoohub.almacen.inventario.service;

import com.nexoohub.almacen.common.exception.BusinessException;
import com.nexoohub.almacen.inventario.dto.AnalisisABCRequestDTO;
import com.nexoohub.almacen.inventario.dto.AnalisisABCResumenDTO;
import com.nexoohub.almacen.inventario.dto.AnalisisABCResponseDTO;
import com.nexoohub.almacen.inventario.entity.AnalisisABC;
import com.nexoohub.almacen.inventario.entity.InventarioSucursal;
import com.nexoohub.almacen.inventario.entity.InventarioSucursalId;
import com.nexoohub.almacen.inventario.entity.ProductoMaestro;
import com.nexoohub.almacen.inventario.mapper.AnalisisABCMapper;
import com.nexoohub.almacen.inventario.repository.AnalisisABCRepository;
import com.nexoohub.almacen.inventario.repository.InventarioSucursalRepository;
import com.nexoohub.almacen.inventario.repository.ProductoMaestroRepository;
import com.nexoohub.almacen.sucursal.entity.Sucursal;
import com.nexoohub.almacen.sucursal.repository.SucursalRepository;
import com.nexoohub.almacen.ventas.entity.DetalleVenta;
import com.nexoohub.almacen.ventas.entity.Venta;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para AnalisisABCService - Módulo #10
 * Valida la lógica de clasificación ABC según el principio de Pareto 80/20
 * 
 * @author NexooHub Development Team
 * @since 1.4.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AnalisisABCService - Tests de Análisis ABC de Inventario")
class AnalisisABCServiceTest {

    @Mock
    private VentaRepository ventaRepository;

    @Mock
    private InventarioSucursalRepository inventarioRepository;

    @Mock
    private ProductoMaestroRepository productoRepository;

    @Mock
    private SucursalRepository sucursalRepository;

    @Mock
    private AnalisisABCRepository analisisRepository;

    @Mock
    private AnalisisABCMapper mapper;

    @InjectMocks
    private AnalisisABCService analisisService;

    private Sucursal sucursalMock;
    private ProductoMaestro producto1;
    private ProductoMaestro producto2;
    private ProductoMaestro producto3;
    private InventarioSucursal inventario1;
    private InventarioSucursal inventario2;
    private InventarioSucursal inventario3;
    private Venta venta1;
    private Venta venta2;
    private AnalisisABCRequestDTO requestMock;

    @BeforeEach
    void setUp() {
        // Setup Sucursal
        sucursalMock = new Sucursal();
        sucursalMock.setId(1);
        sucursalMock.setNombre("Sucursal Centro");

        // Setup Producto 1 - Alto valor (Clase A)
        producto1 = new ProductoMaestro();
        producto1.setSkuInterno("ACEITE-10W40");
        producto1.setNombreComercial("Aceite Motul 10W40 4T");

        // Setup Producto 2 - Valor medio (Clase B)
        producto2 = new ProductoMaestro();
        producto2.setSkuInterno("FILTRO-ACEITE");
        producto2.setNombreComercial("Filtro de Aceite Universal");

        // Setup Producto 3 - Bajo valor (Clase C)
        producto3 = new ProductoMaestro();
        producto3.setSkuInterno("TORNILLO-M8");
        producto3.setNombreComercial("Tornillo M8x20mm");

        // Setup Inventarios
        inventario1 = new InventarioSucursal();
        inventario1.setId(new InventarioSucursalId(1, "ACEITE-10W40"));
        inventario1.setStockActual(50);
        inventario1.setCostoPromedioPonderado(BigDecimal.valueOf(100.00));

        inventario2 = new InventarioSucursal();
        inventario2.setId(new InventarioSucursalId(1, "FILTRO-ACEITE"));
        inventario2.setStockActual(30);
        inventario2.setCostoPromedioPonderado(BigDecimal.valueOf(50.00));

        inventario3 = new InventarioSucursal();
        inventario3.setId(new InventarioSucursalId(1, "TORNILLO-M8"));
        inventario3.setStockActual(500);
        inventario3.setCostoPromedioPonderado(BigDecimal.valueOf(3.00));

        // Setup Ventas - Venta 1 con producto de alto valor
        venta1 = new Venta();
        venta1.setId(1);
        venta1.setSucursalId(1);
        venta1.setFechaVenta(LocalDateTime.of(2026, 2, 15, 10, 0));
        
        DetalleVenta detalle1 = new DetalleVenta();
        detalle1.setSkuInterno("ACEITE-10W40");
        detalle1.setCantidad(100); // Alto volumen
        detalle1.setPrecioUnitarioVenta(BigDecimal.valueOf(80.00)); // $8,000 = 80% del total
        venta1.setDetalles(Arrays.asList(detalle1));

        // Setup Ventas - Venta 2 con producto de valor medio y bajo
        venta2 = new Venta();
        venta2.setId(2);
        venta2.setSucursalId(1);
        venta2.setFechaVenta(LocalDateTime.of(2026, 2, 20, 14, 30));
        
        DetalleVenta detalle2 = new DetalleVenta();
        detalle2.setSkuInterno("FILTRO-ACEITE");
        detalle2.setCantidad(30); // Volumen medio
        detalle2.setPrecioUnitarioVenta(BigDecimal.valueOf(50.00)); // $1,500 = 15% del total
        
        DetalleVenta detalle3 = new DetalleVenta();
        detalle3.setSkuInterno("TORNILLO-M8");
        detalle3.setCantidad(50); // Volumen bajo en valor
        detalle3.setPrecioUnitarioVenta(BigDecimal.valueOf(10.00)); // $500 = 5% del total
        
        venta2.setDetalles(Arrays.asList(detalle2, detalle3));

        // Setup Request DTO
        requestMock = new AnalisisABCRequestDTO();
        requestMock.setSucursalId(1);
        requestMock.setPeriodoInicio(LocalDate.of(2026, 1, 1));
        requestMock.setPeriodoFin(LocalDate.of(2026, 3, 31));
        requestMock.setPorcentajeA(80.0);
        requestMock.setPorcentajeB(95.0);
        requestMock.setForzarRegeneracion(false);
    }

    // ==================== TESTS DE GENERACIÓN DE ANÁLISIS ====================

    @Test
    @DisplayName("Test 1: Debe generar análisis ABC con clasificación correcta según Pareto")
    void testGenerarAnalisisABC_ClasificacionCorrecta() {
        // Given
        when(sucursalRepository.existsById(1)).thenReturn(true);
        when(sucursalRepository.findById(1)).thenReturn(Optional.of(sucursalMock));
        when(ventaRepository.findByFechaRangoConDetalles(
            any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenReturn(Arrays.asList(venta1, venta2));
        
        // Producto 1 - 100 unidades * $150 = $15,000 (Alto valor - Clase A)
        when(inventarioRepository.findByIdSkuInternoAndIdSucursalId("ACEITE-10W40", 1))
            .thenReturn(Optional.of(inventario1));
        when(productoRepository.findById("ACEITE-10W40"))
            .thenReturn(Optional.of(producto1));
        
        // Producto 2 - 30 unidades * $80 = $2,400 (Valor medio - Clase B)
        when(inventarioRepository.findByIdSkuInternoAndIdSucursalId("FILTRO-ACEITE", 1))
            .thenReturn(Optional.of(inventario2));
        when(productoRepository.findById("FILTRO-ACEITE"))
            .thenReturn(Optional.of(producto2));
        
        // Producto 3 - 50 unidades * $5 = $250 (Bajo valor - Clase C)
        when(inventarioRepository.findByIdSkuInternoAndIdSucursalId("TORNILLO-M8", 1))
            .thenReturn(Optional.of(inventario3));
        when(productoRepository.findById("TORNILLO-M8"))
            .thenReturn(Optional.of(producto3));

        when(analisisRepository.existsBySucursalIdAndFechaAnalisis(anyInt(), any(LocalDate.class)))
            .thenReturn(false);
        
        ArgumentCaptor<List<AnalisisABC>> captor = ArgumentCaptor.forClass(List.class);
        when(analisisRepository.saveAll(captor.capture())).thenAnswer(i -> i.getArgument(0));
        
        AnalisisABCResponseDTO responseA = new AnalisisABCResponseDTO();
        responseA.setClasificacion("A");
        AnalisisABCResponseDTO responseB = new AnalisisABCResponseDTO();
        responseB.setClasificacion("B");
        AnalisisABCResponseDTO responseC = new AnalisisABCResponseDTO();
        responseC.setClasificacion("C");
        
        when(mapper.toDTO(any(AnalisisABC.class), any(), any()))
            .thenReturn(responseA, responseB, responseC);

        // When
        List<AnalisisABCResponseDTO> resultado = analisisService.generarAnalisisABC(requestMock);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado).hasSize(3);
        
        // Verify saveAll fue llamado
        verify(analisisRepository, times(1)).saveAll(any());
        
        // Capturar y validar clasificaciones
        List<AnalisisABC> analisisCaptured = captor.getValue();
        assertThat(analisisCaptured).hasSize(3);
        
        // Total ventas: $8,000 + $1,500 + $500 = $10,000
        // Producto 1: $8,000 / $10,000 = 80.00% acumulado → Clase A (≤80%)
        // Producto 2: $1,500 / $10,000 = 15% → 95% acumulado → Clase B (>80% pero ≤95%)
        // Producto 3: $500 / $10,000 = 5% → 100% acumulado → Clase C (>95%)
        
        AnalisisABC analisisAltoValor = analisisCaptured.stream()
            .filter(a -> a.getSkuProducto().equals("ACEITE-10W40"))
            .findFirst().orElse(null);
        
        assertThat(analisisAltoValor).isNotNull();
        assertThat(analisisAltoValor.getClasificacion()).isEqualTo("A");
        assertThat(analisisAltoValor.getCantidadVendida()).isEqualTo(100);
        assertThat(analisisAltoValor.getValorVentas()).isEqualByComparingTo(BigDecimal.valueOf(8000.00));
    }

    @Test
    @DisplayName("Test 2: Debe calcular rotación de inventario correctamente")
    void testGenerarAnalisisABC_RotacionInventario() {
        // Given
        when(sucursalRepository.existsById(1)).thenReturn(true);
        when(sucursalRepository.findById(1)).thenReturn(Optional.of(sucursalMock));
        when(ventaRepository.findByFechaRangoConDetalles(
            any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenReturn(Arrays.asList(venta1));
        
        when(inventarioRepository.findByIdSkuInternoAndIdSucursalId("ACEITE-10W40", 1))
            .thenReturn(Optional.of(inventario1));
        when(productoRepository.findById("ACEITE-10W40"))
            .thenReturn(Optional.of(producto1));
        when(analisisRepository.existsBySucursalIdAndFechaAnalisis(anyInt(), any(LocalDate.class)))
            .thenReturn(false);
        
        ArgumentCaptor<List<AnalisisABC>> captor = ArgumentCaptor.forClass(List.class);
        when(analisisRepository.saveAll(captor.capture())).thenAnswer(i -> i.getArgument(0));
        when(mapper.toDTO(any(AnalisisABC.class), any(), any())).thenReturn(new AnalisisABCResponseDTO());

        // When
        analisisService.generarAnalisisABC(requestMock);

        // Then
        List<AnalisisABC> analisisCaptured = captor.getValue();
        AnalisisABC analisis = analisisCaptured.get(0);
        
        // Valor ventas: $8,000
        // Valor stock: 50 unidades * $100 costo = $5,000
        // Rotación esperada: $8,000 / $5,000 = 1.6000
        assertThat(analisis.getStockActual()).isEqualTo(50);
        assertThat(analisis.getValorStock()).isEqualByComparingTo(BigDecimal.valueOf(5000.00));
        assertThat(analisis.getRotacionInventario()).isNotNull();
        assertThat(analisis.getRotacionInventario().doubleValue()).isGreaterThan(1.5);
    }

    @Test
    @DisplayName("Test 3: Debe lanzar BusinessException cuando fechas son inválidas")
    void testGenerarAnalisisABC_FechasInvalidas() {
        // Given
        requestMock.setPeriodoInicio(LocalDate.of(2026, 3, 31));
        requestMock.setPeriodoFin(LocalDate.of(2026, 1, 1)); // Fin antes de inicio

        // When & Then
        assertThatThrownBy(() -> analisisService.generarAnalisisABC(requestMock))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("La fecha de inicio no puede ser posterior a la fecha fin");
        
        verify(ventaRepository, never()).findByFechaRangoConDetalles(any(), any());
    }

    @Test
    @DisplayName("Test 4: Debe lanzar BusinessException cuando porcentajes son inválidos")
    void testGenerarAnalisisABC_PorcentajesInvalidos() {
        // Given
        requestMock.setPorcentajeA(95.0);
        requestMock.setPorcentajeB(80.0); // B menor que A (inválido)

        // When & Then
        assertThatThrownBy(() -> analisisService.generarAnalisisABC(requestMock))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("El porcentaje A debe ser menor que el porcentaje B");
        
        verify(ventaRepository, never()).findByFechaRangoConDetalles(any(), any());
    }

    @Test
    @DisplayName("Test 5: Debe generar análisis vacío cuando no hay ventas en el período")
    void testGenerarAnalisisABC_SinVentas() {
        // Given
        when(sucursalRepository.existsById(1)).thenReturn(true);
        when(ventaRepository.findByFechaRangoConDetalles(
            any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenReturn(new ArrayList<>());

        // When
        List<AnalisisABCResponseDTO> resultado = analisisService.generarAnalisisABC(requestMock);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado).isEmpty();
        verify(analisisRepository, never()).saveAll(any());
    }

    @Test
    @DisplayName("Test 6: Debe regenerar análisis cuando forzarRegeneracion es true")
    void testGenerarAnalisisABC_ForzarRegeneracion() {
        // Given
        requestMock.setForzarRegeneracion(true);
        when(sucursalRepository.existsById(1)).thenReturn(true);
        when(sucursalRepository.findById(1)).thenReturn(Optional.of(sucursalMock));
        when(ventaRepository.findByFechaRangoConDetalles(
            any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenReturn(Arrays.asList(venta1));
        when(inventarioRepository.findByIdSkuInternoAndIdSucursalId(anyString(), anyInt()))
            .thenReturn(Optional.of(inventario1));
        when(productoRepository.findById(anyString())).thenReturn(Optional.of(producto1));
        // No verificamos existsBySucursalIdAndFechaAnalisis porque forzarRegeneracion=true omite esa verificación
        when(analisisRepository.saveAll(any())).thenAnswer(i -> i.getArgument(0));
        when(mapper.toDTO(any(AnalisisABC.class), any(), any())).thenReturn(new AnalisisABCResponseDTO());

        // When
        analisisService.generarAnalisisABC(requestMock);

        // Then
        // Nota: El servicio no borra registros existentes, simplemente guarda nuevos
        verify(analisisRepository, times(1)).saveAll(any());
    }

    // ==================== TESTS DE CONSULTA ====================

    @Test
    @DisplayName("Test 7: Debe obtener último análisis de una sucursal")
    void testObtenerUltimoAnalisis_Success() {
        // Given
        when(sucursalRepository.existsById(1)).thenReturn(true);
        
        AnalisisABC analisis1 = new AnalisisABC();
        analisis1.setId(1);
        analisis1.setSucursalId(1);
        analisis1.setSkuProducto("ACEITE-10W40");
        analisis1.setClasificacion("A");
        analisis1.setFechaAnalisis(LocalDate.of(2026, 4, 1));
        
        when(analisisRepository.findUltimoAnalisisBySucursal(1))
            .thenReturn(Arrays.asList(analisis1));
        when(productoRepository.findById("ACEITE-10W40"))
            .thenReturn(Optional.of(producto1));
        when(sucursalRepository.findById(1))
            .thenReturn(Optional.of(sucursalMock));
        
        AnalisisABCResponseDTO responseDTO = new AnalisisABCResponseDTO();
        responseDTO.setId(1);
        responseDTO.setClasificacion("A");
        when(mapper.toDTO(any(AnalisisABC.class), any(), any())).thenReturn(responseDTO);

        // When
        List<AnalisisABCResponseDTO> resultado = analisisService.obtenerUltimoAnalisis(1);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getClasificacion()).isEqualTo("A");
        verify(analisisRepository, times(1)).findUltimoAnalisisBySucursal(1);
    }

    @Test
    @DisplayName("Test 8: Debe obtener productos por clasificación específica")
    void testObtenerPorClasificacion_ClaseA() {
        // Given
        AnalisisABC analisisA = new AnalisisABC();
        analisisA.setId(1);
        analisisA.setSucursalId(1);
        analisisA.setSkuProducto("ACEITE-10W40");
        analisisA.setClasificacion("A");
        analisisA.setValorVentas(BigDecimal.valueOf(8000.00));
        analisisA.setFechaAnalisis(LocalDate.of(2026, 4, 1));
        
        // Mock para obtener último análisis (primer paso del método)
        when(analisisRepository.findUltimoAnalisisBySucursal(1))
            .thenReturn(Arrays.asList(analisisA));
        
        // Mock para filtrar por clasificación específica (segundo paso)
        when(analisisRepository.findBySucursalIdAndClasificacionAndFechaAnalisis(eq(1), eq("A"), any(LocalDate.class)))
            .thenReturn(Arrays.asList(analisisA));
        
        when(productoRepository.findById("ACEITE-10W40"))
            .thenReturn(Optional.of(producto1));
        when(sucursalRepository.findById(1))
            .thenReturn(Optional.of(sucursalMock));
        
        AnalisisABCResponseDTO responseDTO = new AnalisisABCResponseDTO();
        responseDTO.setClasificacion("A");
        responseDTO.setValorVentas(BigDecimal.valueOf(8000.00));
        when(mapper.toDTO(any(AnalisisABC.class), any(), any())).thenReturn(responseDTO);

        // When
        List<AnalisisABCResponseDTO> resultado = analisisService.obtenerPorClasificacion(1, "A");

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getClasificacion()).isEqualTo("A");
        verify(analisisRepository, times(1))
            .findBySucursalIdAndClasificacionAndFechaAnalisis(eq(1), eq("A"), any(LocalDate.class));
    }

    @Test
    @DisplayName("Test 9: Debe obtener resumen con estadísticas por clasificación")
    void testObtenerResumen_EstadisticasCorrectas() {
        // Given
        when(sucursalRepository.findById(1)).thenReturn(Optional.of(sucursalMock));
        
        LocalDate fechaAnalisis = LocalDate.of(2026, 4, 1);
        
        AnalisisABC analisisA = new AnalisisABC();
        analisisA.setSkuProducto("ACEITE-10W40"); // ← AGREGADO
        analisisA.setSucursalId(1);
        analisisA.setClasificacion("A");
        analisisA.setValorVentas(BigDecimal.valueOf(8000.00)); // Actualizado a nuevos valores
        analisisA.setValorStock(BigDecimal.valueOf(5000.00));
        analisisA.setRotacionInventario(BigDecimal.valueOf(1.6000)); // Actualizado
        analisisA.setFechaAnalisis(fechaAnalisis);
        analisisA.setPeriodoInicio(LocalDate.of(2026, 1, 1));
        analisisA.setPeriodoFin(LocalDate.of(2026, 3, 31));
        
        AnalisisABC analisisB = new AnalisisABC();
        analisisB.setSkuProducto("FILTRO-ACEITE"); // ← AGREGADO
        analisisB.setSucursalId(1);
        analisisB.setClasificacion("B");
        analisisB.setValorVentas(BigDecimal.valueOf(1500.00)); // Actualizado
        analisisB.setValorStock(BigDecimal.valueOf(1500.00));
        analisisB.setRotacionInventario(BigDecimal.valueOf(1.0000));
        analisisB.setFechaAnalisis(fechaAnalisis);
        
        when(analisisRepository.findUltimoAnalisisBySucursal(1))
            .thenReturn(Arrays.asList(analisisA, analisisB));
        
        // Mock para resumenPorClasificacion - retorna Object[] con {clasificacion, valorVentas, count}
        Object[] resumenA = {"A", BigDecimal.valueOf(8000.00), 1L};
        Object[] resumenB = {"B", BigDecimal.valueOf(1500.00), 1L};
        when(analisisRepository.resumenPorClasificacion(1, fechaAnalisis))
            .thenReturn(Arrays.asList(resumenA, resumenB));
        
        // Mock para obtenerPorClasificacion (llamado al final del método)
        when(analisisRepository.findBySucursalIdAndClasificacionAndFechaAnalisis(eq(1), eq("A"), any(LocalDate.class)))
            .thenReturn(Arrays.asList(analisisA));
        when(productoRepository.findById("ACEITE-10W40")).thenReturn(Optional.of(producto1)); // Específico
        
        AnalisisABCResponseDTO responseA = new AnalisisABCResponseDTO();
        responseA.setClasificacion("A");
        when(mapper.toDTO(any(AnalisisABC.class), any(), any())).thenReturn(responseA);

        // When
        AnalisisABCResumenDTO resultado = analisisService.obtenerResumen(1);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getTotalProductos()).isEqualTo(2);
        assertThat(resultado.getValorTotalVentas()).isEqualByComparingTo(BigDecimal.valueOf(9500.00)); // 8000 + 1500
        assertThat(resultado.getClasificacionA()).isNotNull();
        assertThat(resultado.getClasificacionB()).isNotNull();
        
        // Método es llamado 2 veces: 1 por obtenerResumen, 1 por obtenerPorClasificacion interno
        verify(analisisRepository, times(2)).findUltimoAnalisisBySucursal(1);
    }
}
