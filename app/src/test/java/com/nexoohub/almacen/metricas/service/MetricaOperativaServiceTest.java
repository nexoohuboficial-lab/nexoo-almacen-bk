package com.nexoohub.almacen.metricas.service;

import com.nexoohub.almacen.compras.entity.Compra;
import com.nexoohub.almacen.compras.entity.DetalleCompra;
import com.nexoohub.almacen.compras.repository.CompraRepository;
import com.nexoohub.almacen.compras.repository.DetalleCompraRepository;
import com.nexoohub.almacen.inventario.entity.MovimientoInventario;
import com.nexoohub.almacen.inventario.repository.MovimientoInventarioRepository;
import com.nexoohub.almacen.metricas.dto.AnalisisOperativoRequestDTO;
import com.nexoohub.almacen.metricas.dto.MetricaOperativaResponseDTO;
import com.nexoohub.almacen.metricas.entity.MetricaOperativa;
import com.nexoohub.almacen.metricas.repository.MetricaOperativaRepository;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para MetricaOperativaService.
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("MetricaOperativaService - Tests de Métricas Operacionales")
class MetricaOperativaServiceTest {

    @Mock
    private MetricaOperativaRepository metricaRepository;

    @Mock
    private MovimientoInventarioRepository movimientoRepository;

    @Mock
    private CompraRepository compraRepository;

    @Mock
    private DetalleCompraRepository detalleCompraRepository;

    @Mock
    private VentaRepository ventaRepository;

    @Mock
    private DetalleVentaRepository detalleVentaRepository;

    @Mock
    private SucursalRepository sucursalRepository;

    @InjectMocks
    private MetricaOperativaService metricaService;

    private List<MovimientoInventario> movimientos;
    private List<Compra> compras;
    private List<DetalleCompra> detallesCompra;
    private List<Venta> ventas;
    private List<DetalleVenta> detallesVenta;

    @BeforeEach
    void setUp() {
        // Preparar datos de prueba - usar fecha actual para que coincida con los tests
        LocalDateTime fecha = LocalDateTime.now().minusDays(15); // 15 días atrás

        // Movimientos de traspasos
        movimientos = new ArrayList<>();
        MovimientoInventario mov1 = new MovimientoInventario();
        mov1.setId(1);
        mov1.setTipoMovimiento("ENTRADA_TRASPASO");
        mov1.setCantidad(100);
        mov1.setRastreoId("TR-001");
        mov1.setSucursalId(1);
        mov1.prePersist(); // Esto establece fechaMovimiento automáticamente
        movimientos.add(mov1);

        MovimientoInventario mov2 = new MovimientoInventario();
        mov2.setId(2);
        mov2.setTipoMovimiento("SALIDA_TRASPASO");
        mov2.setCantidad(50);
        mov2.setRastreoId("TR-001");
        mov2.setSucursalId(2);
        mov2.prePersist(); // Esto establece fechaMovimiento automáticamente
        movimientos.add(mov2);

        // Compras
        compras = new ArrayList<>();
        Compra compra1 = new Compra();
        compra1.setId(1);
        compra1.setTotalCompra(new BigDecimal("10000.00"));
        compra1.setFechaCompra(fecha);
        compras.add(compra1);

        // Detalles de compra
        detallesCompra = new ArrayList<>();
        DetalleCompra detComp1 = new DetalleCompra();
        detComp1.setCompraId(1);
        detComp1.setCantidad(200);
        detallesCompra.add(detComp1);

        // Ventas
        ventas = new ArrayList<>();
        Venta venta1 = new Venta();
        venta1.setId(1);
        venta1.setTotal(new BigDecimal("5000.00"));
        venta1.setFechaVenta(fecha);
        venta1.setSucursalId(1);
        ventas.add(venta1);

        Venta venta2 = new Venta();
        venta2.setId(2);
        venta2.setTotal(new BigDecimal("3000.00"));
        venta2.setFechaVenta(fecha);
        venta2.setSucursalId(1);
        ventas.add(venta2);

        // Detalles de venta
        detallesVenta = new ArrayList<>();
        DetalleVenta detVenta1 = new DetalleVenta();
        detVenta1.setVentaId(1);
        detVenta1.setCantidad(10);
        detallesVenta.add(detVenta1);

        DetalleVenta detVenta2 = new DetalleVenta();
        detVenta2.setVentaId(2);
        detVenta2.setCantidad(5);
        detallesVenta.add(detVenta2);
    }

    @Test
    @DisplayName("Debe generar análisis operativo completo")
    void debeGenerarAnalisisCompleto() {
        // Given
        LocalDate hoy = LocalDate.now();
        AnalisisOperativoRequestDTO request = new AnalisisOperativoRequestDTO();
        request.setFechaInicio(hoy.minusDays(30));
        request.setFechaFin(hoy);
        request.setTipoPeriodo("MENSUAL");
        request.setCompararPeriodoAnterior(false);

        when(movimientoRepository.findAll()).thenReturn(movimientos);
        when(compraRepository.findAll()).thenReturn(compras);
        when(detalleCompraRepository.findAll()).thenReturn(detallesCompra);
        when(ventaRepository.findAll()).thenReturn(ventas);
        when(detalleVentaRepository.findAll()).thenReturn(detallesVenta);

        // When
        MetricaOperativaResponseDTO resultado = metricaService.generarAnalisis(request);

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getPeriodoInicio()).isEqualTo(request.getFechaInicio());
        assertThat(resultado.getPeriodoFin()).isEqualTo(request.getFechaFin());
        assertThat(resultado.getTipoPeriodo()).isEqualTo("MENSUAL");
        assertThat(resultado.getTraspasos()).isNotNull();
        assertThat(resultado.getCompras()).isNotNull();
        assertThat(resultado.getVentas()).isNotNull();
        assertThat(resultado.getEficiencia()).isNotNull();
    }

    @Test
    @DisplayName("Debe calcular métricas de traspasos correctamente")
    void debeCalcularMetricasTraspasos() {
        // Given
        LocalDate hoy = LocalDate.now();
        AnalisisOperativoRequestDTO request = new AnalisisOperativoRequestDTO();
        request.setFechaInicio(hoy.minusDays(30));
        request.setFechaFin(hoy);
        request.setCompararPeriodoAnterior(false);

        when(movimientoRepository.findAll()).thenReturn(movimientos);
        when(compraRepository.findAll()).thenReturn(new ArrayList<>());
        when(detalleCompraRepository.findAll()).thenReturn(new ArrayList<>());
        when(ventaRepository.findAll()).thenReturn(new ArrayList<>());
        when(detalleVentaRepository.findAll()).thenReturn(new ArrayList<>());

        // When
        MetricaOperativaResponseDTO resultado = metricaService.generarAnalisis(request);

        // Then
        assertThat(resultado.getTraspasos()).isNotNull();
        assertThat(resultado.getTraspasos().getTotalTraspasos()).isEqualTo(1); // 1 rastreoId único
        assertThat(resultado.getTraspasos().getUnidadesEntrada()).isEqualTo(100);
        assertThat(resultado.getTraspasos().getUnidadesSalida()).isEqualTo(50);
        assertThat(resultado.getTraspasos().getUnidadesNeto()).isEqualTo(50);
        assertThat(resultado.getTraspasos().getTendenciaTraspaso()).isEqualTo("EQUILIBRADO");
    }

    @Test
    @DisplayName("Debe calcular métricas de compras correctamente")
    void debeCalcularMetricasCompras() {
        LocalDate hoy = LocalDate.now();
        AnalisisOperativoRequestDTO request = new AnalisisOperativoRequestDTO();
        request.setFechaInicio(hoy.minusDays(30));
        request.setFechaFin(hoy);
        request.setCompararPeriodoAnterior(false);

        when(movimientoRepository.findAll()).thenReturn(new ArrayList<>());
        when(compraRepository.findAll()).thenReturn(compras);
        when(detalleCompraRepository.findAll()).thenReturn(detallesCompra);
        when(ventaRepository.findAll()).thenReturn(new ArrayList<>());
        when(detalleVentaRepository.findAll()).thenReturn(new ArrayList<>());

        // When
        MetricaOperativaResponseDTO resultado = metricaService.generarAnalisis(request);

        // Then
        assertThat(resultado.getCompras()).isNotNull();
        assertThat(resultado.getCompras().getTotalCompras()).isEqualTo(1);
        assertThat(resultado.getCompras().getUnidadesCompradas()).isEqualTo(200);
        assertThat(resultado.getCompras().getGastoTotal()).isEqualByComparingTo("10000.00");
        assertThat(resultado.getCompras().getCompraPromedio()).isEqualByComparingTo("10000.00");
    }

    @Test
    @DisplayName("Debe calcular métricas de ventas correctamente")
    void debeCalcularMetricasVentas() {
        // Given
        LocalDate hoy = LocalDate.now();
        AnalisisOperativoRequestDTO request = new AnalisisOperativoRequestDTO();
        request.setFechaInicio(hoy.minusDays(30));
        request.setFechaFin(hoy);
        request.setCompararPeriodoAnterior(false);

        when(movimientoRepository.findAll()).thenReturn(new ArrayList<>());
        when(compraRepository.findAll()).thenReturn(new ArrayList<>());
        when(detalleCompraRepository.findAll()).thenReturn(new ArrayList<>());
        when(ventaRepository.findAll()).thenReturn(ventas);
        when(detalleVentaRepository.findAll()).thenReturn(detallesVenta);

        // When
        MetricaOperativaResponseDTO resultado = metricaService.generarAnalisis(request);

        // Then
        assertThat(resultado.getVentas()).isNotNull();
        assertThat(resultado.getVentas().getTotalVentas()).isEqualTo(2);
        assertThat(resultado.getVentas().getUnidadesVendidas()).isEqualTo(15);
        assertThat(resultado.getVentas().getIngresoTotal()).isEqualByComparingTo("8000.00");
        assertThat(resultado.getVentas().getVentaPromedio()).isEqualByComparingTo("4000.00");
    }

    @Test
    @DisplayName("Debe calcular indicadores de eficiencia correctamente")
    void debeCalcularIndicadoresEficiencia() {
        LocalDate hoy = LocalDate.now();
        AnalisisOperativoRequestDTO request = new AnalisisOperativoRequestDTO();
        request.setFechaInicio(hoy.minusDays(30));
        request.setFechaFin(hoy);
        request.setCompararPeriodoAnterior(false);

        when(movimientoRepository.findAll()).thenReturn(movimientos);
        when(compraRepository.findAll()).thenReturn(compras);
        when(detalleCompraRepository.findAll()).thenReturn(detallesCompra);
        when(ventaRepository.findAll()).thenReturn(ventas);
        when(detalleVentaRepository.findAll()).thenReturn(detallesVenta);

        // When
        MetricaOperativaResponseDTO resultado = metricaService.generarAnalisis(request);

        // Then
        assertThat(resultado.getEficiencia()).isNotNull();
        assertThat(resultado.getEficiencia().getTotalOperaciones()).isGreaterThan(0);
        assertThat(resultado.getEficiencia().getTotalOperaciones()).isEqualTo(4); // 1 traspaso + 1 compra + 2 ventas
        assertThat(resultado.getEficiencia().getRatioEntradaSalida()).isNotNull();
        assertThat(resultado.getEficiencia().getProductividadDiariaVentas()).isNotNull();
        assertThat(resultado.getEficiencia().getClasificacionActividad()).isIn("ALTO", "MEDIO", "BAJO");
        assertThat(resultado.getEficiencia().getBalanceOperacional()).isIn("POSITIVO", "NEGATIVO", "EQUILIBRADO");
    }

    @Test
    @DisplayName("Debe filtrar por sucursal correctamente")
    void debeFiltrarPorSucursal() {
        // Given
        LocalDate hoy = LocalDate.now();
        AnalisisOperativoRequestDTO request = new AnalisisOperativoRequestDTO();
        request.setFechaInicio(hoy.minusDays(30));
        request.setFechaFin(hoy);
        request.setSucursalId(1);
        request.setCompararPeriodoAnterior(false);

        Sucursal sucursal = new Sucursal();
        sucursal.setId(1);
        sucursal.setNombre("Sucursal Centro");

        when(movimientoRepository.findAll()).thenReturn(movimientos);
        when(compraRepository.findAll()).thenReturn(compras);
        when(detalleCompraRepository.findAll()).thenReturn(detallesCompra);
        when(ventaRepository.findAll()).thenReturn(ventas);
        when(detalleVentaRepository.findAll()).thenReturn(detallesVenta);
        when(sucursalRepository.findById(1)).thenReturn(Optional.of(sucursal));

        // When
        MetricaOperativaResponseDTO resultado = metricaService.generarAnalisis(request);

        // Then
        assertThat(resultado.getSucursalId()).isEqualTo(1);
        assertThat(resultado.getNombreSucursal()).isEqualTo("Sucursal Centro");
        verifySearch(sucursalRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Debe guardar métrica operativa en la base de datos")
    void debeGuardarMetrica() {
        // Given
        LocalDate hoy = LocalDate.now();
        AnalisisOperativoRequestDTO request = new AnalisisOperativoRequestDTO();
        request.setFechaInicio(hoy.minusDays(30));
        request.setFechaFin(hoy);
        request.setTipoPeriodo("MENSUAL");
        request.setCompararPeriodoAnterior(false);

        when(movimientoRepository.findAll()).thenReturn(movimientos);
        when(compraRepository.findAll()).thenReturn(compras);
        when(detalleCompraRepository.findAll()).thenReturn(detallesCompra);
        when(ventaRepository.findAll()).thenReturn(ventas);
        when(detalleVentaRepository.findAll()).thenReturn(detallesVenta);
        when(metricaRepository.save(any(MetricaOperativa.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        MetricaOperativa metrica = metricaService.guardarMetrica(request);

        // Then
        assertThat(metrica).isNotNull();
        assertThat(metrica.getPeriodoInicio()).isEqualTo(request.getFechaInicio());
        assertThat(metrica.getPeriodoFin()).isEqualTo(request.getFechaFin());
        assertThat(metrica.getTipoPeriodo()).isEqualTo("MENSUAL");
        verify(metricaRepository, times(1)).save(any(MetricaOperativa.class));
    }

    @Test
    @DisplayName("Debe obtener métrica consolidada guardada")
    void debeObtenerMetricaConsolidada() {
        // Given
        LocalDate inicio = LocalDate.of(2024, 1, 1);
        LocalDate fin = LocalDate.of(2024, 1, 31);

        MetricaOperativa metricaMock = new MetricaOperativa();
        metricaMock.setId(1L);
        metricaMock.setPeriodoInicio(inicio);
        metricaMock.setPeriodoFin(fin);

        when(metricaRepository.findMetricaConsolidada(inicio, fin)).thenReturn(Optional.of(metricaMock));

        // When
        Optional<MetricaOperativa> resultado = metricaService.obtenerMetricaConsolidada(inicio, fin);

        // Then
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getPeriodoInicio()).isEqualTo(inicio);
        assertThat(resultado.get().getPeriodoFin()).isEqualTo(fin);
        verify(metricaRepository, times(1)).findMetricaConsolidada(inicio, fin);
    }

    @Test
    @DisplayName("Debe retornar vacío cuando no existe métrica consolidada")
    void debeRetornarVacioCuandoNoExisteMetrica() {
        // Given
        LocalDate inicio = LocalDate.of(2024, 1, 1);
        LocalDate fin = LocalDate.of(2024, 1, 31);

        when(metricaRepository.findMetricaConsolidada(inicio, fin)).thenReturn(Optional.empty());

        // When
        Optional<MetricaOperativa> resultado = metricaService.obtenerMetricaConsolidada(inicio, fin);

        // Then
        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("Debe obtener historial consolidado")
    void debeObtenerHistorialConsolidado() {
        // Given
        LocalDate hasta = LocalDate.of(2024, 12, 31);
        int limite = 10;

        List<MetricaOperativa> historialMock = Arrays.asList(
                crearMetricaMock(1L, LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 31)),
                crearMetricaMock(2L, LocalDate.of(2024, 2, 1), LocalDate.of(2024, 2, 29))
        );

        when(metricaRepository.findHistorialConsolidado(hasta)).thenReturn(historialMock);

        // When
        List<MetricaOperativa> resultado = metricaService.obtenerHistorialConsolidado(hasta, limite);

        // Then
        assertThat(resultado).hasSize(2);
        verify(metricaRepository, times(1)).findHistorialConsolidado(hasta);
    }

    @Test
    @DisplayName("Debe clasificar actividad como ALTO cuando hay muchas operaciones por día")
    void debeClasificarActividadAlto() {
        // Given
        LocalDate hoy = LocalDate.now();
        AnalisisOperativoRequestDTO request = new AnalisisOperativoRequestDTO();
        request.setFechaInicio(hoy.minusDays(1));
        request.setFechaFin(hoy); // 2 días

        // Crear muchas ventas para superar el umbral de 75 op/día
        List<Venta> muchasVentas = new ArrayList<>();
        for (int i = 0; i < 150; i++) {
            Venta venta = new Venta();
            venta.setId(i);
            venta.setTotal(new BigDecimal("100.00"));
            venta.setFechaVenta(LocalDateTime.now());
            muchasVentas.add(venta);
        }

        when(movimientoRepository.findAll()).thenReturn(new ArrayList<>());
        when(compraRepository.findAll()).thenReturn(new ArrayList<>());
        when(detalleCompraRepository.findAll()).thenReturn(new ArrayList<>());
        when(ventaRepository.findAll()).thenReturn(muchasVentas);

        // When
        MetricaOperativaResponseDTO resultado = metricaService.generarAnalisis(request);

        // Then
        assertThat(resultado.getEficiencia().getClasificacionActividad()).isEqualTo("ALTO");
    }

    @Test
    @DisplayName("Debe calcular balance operacional como POSITIVO cuando hay más entradas que salidas")
    void debeCalcularBalanceOperacionalPositivo() {
        // Given
        LocalDate hoy = LocalDate.now();
        AnalisisOperativoRequestDTO request = new AnalisisOperativoRequestDTO();
        request.setFechaInicio(hoy.minusDays(30));
        request.setFechaFin(hoy);
        request.setCompararPeriodoAnterior(false);

        // Muchas compras, pocas ventas = más entradas que salidas
        List<DetalleCompra> muchasCompras = new ArrayList<>();
        DetalleCompra dc = new DetalleCompra();
        dc.setCompraId(1);
        dc.setCantidad(1000);
        muchasCompras.add(dc);

        when(movimientoRepository.findAll()).thenReturn(new ArrayList<>());
        when(compraRepository.findAll()).thenReturn(compras);
        when(detalleCompraRepository.findAll()).thenReturn(muchasCompras);
        when(ventaRepository.findAll()).thenReturn(Arrays.asList(ventas.get(0))); // Solo 1 venta
        when(detalleVentaRepository.findAll()).thenReturn(Arrays.asList(detallesVenta.get(0))); // 10 unidades

        // When
        MetricaOperativaResponseDTO resultado = metricaService.generarAnalisis(request);

        // Then
        assertThat(resultado.getEficiencia().getBalanceOperacional()).isEqualTo("POSITIVO");
    }

    // ==========================================
    // MÉTODOS AUXILIARES
    // ==========================================

    private MetricaOperativa crearMetricaMock(Long id, LocalDate inicio, LocalDate fin) {
        MetricaOperativa metrica = new MetricaOperativa();
        metrica.setId(id);
        metrica.setPeriodoInicio(inicio);
        metrica.setPeriodoFin(fin);
        metrica.setTipoPeriodo("MENSUAL");
        return metrica;
    }

    // Helper para evitar errores de compilación con Mockito verify en findById
    private <T> T verifySearch(T mock, org.mockito.verification.VerificationMode mode) {
        return verify(mock, mode);
    }
}
