package com.nexoohub.almacen.metricas.service;

import com.nexoohub.almacen.catalogo.entity.Cliente;
import com.nexoohub.almacen.catalogo.repository.ClienteRepository;
import com.nexoohub.almacen.empleados.entity.Empleado;
import com.nexoohub.almacen.empleados.repository.EmpleadoRepository;
import com.nexoohub.almacen.metricas.dto.AnalisisVentaClienteRequestDTO;
import com.nexoohub.almacen.metricas.dto.DetalleClienteDTO;
import com.nexoohub.almacen.metricas.dto.DetalleVendedorDTO;
import com.nexoohub.almacen.metricas.dto.MetricaVentaClienteResponseDTO;
import com.nexoohub.almacen.metricas.entity.MetricaVentaCliente;
import com.nexoohub.almacen.metricas.repository.MetricaVentaClienteRepository;
import com.nexoohub.almacen.sucursal.entity.Sucursal;
import com.nexoohub.almacen.sucursal.repository.SucursalRepository;
import com.nexoohub.almacen.ventas.entity.Venta;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para MetricaVentaClienteService.
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("MetricaVentaClienteService - Tests Unitarios")
class MetricaVentaClienteServiceTest {

    @Mock
    private MetricaVentaClienteRepository metricaRepository;

    @Mock
    private VentaRepository ventaRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private EmpleadoRepository empleadoRepository;

    @Mock
    private SucursalRepository sucursalRepository;

    @InjectMocks
    private MetricaVentaClienteService service;

    private AnalisisVentaClienteRequestDTO request;
    private List<Venta> ventasMock;
    private Cliente clienteMock;
    private Empleado empleadoMock;
    private Sucursal sucursalMock;

    @BeforeEach
    void setUp() {
        // Request básico
        request = new AnalisisVentaClienteRequestDTO();
        request.setFechaInicio(LocalDate.of(2024, 1, 1));
        request.setFechaFin(LocalDate.of(2024, 1, 31));
        request.setTipoPeriodo("MENSUAL");
        request.setCompararPeriodoAnterior(false);
        request.setIncluirDetalleVendedores(true);
        request.setIncluirDetalleClientes(true);
        request.setLimitTopVendedores(5);
        request.setLimitTopClientes(10);

        // Mock de ventas
        Venta venta1 = new Venta();
        venta1.setId(1);
        venta1.setClienteId(100);
        venta1.setVendedorId(200);
        venta1.setSucursalId(1);
        venta1.setTotal(new BigDecimal("1500.00"));
        venta1.setMetodoPago("EFECTIVO");
        venta1.setFechaVenta(LocalDateTime.of(2024, 1, 15, 10, 0));

        Venta venta2 = new Venta();
        venta2.setId(2);
        venta2.setClienteId(101);
        venta2.setVendedorId(200);
        venta2.setSucursalId(1);
        venta2.setTotal(new BigDecimal("2500.00"));
        venta2.setMetodoPago("TARJETA");
        venta2.setFechaVenta(LocalDateTime.of(2024, 1, 20, 14, 30));

        Venta venta3 = new Venta();
        venta3.setId(3);
        venta3.setClienteId(102);
        venta3.setVendedorId(201);
        venta3.setSucursalId(1);
        venta3.setTotal(new BigDecimal("3000.00"));
        venta3.setMetodoPago("CREDITO");
        venta3.setFechaVenta(LocalDateTime.of(2024, 1, 25, 16, 45));

        ventasMock = Arrays.asList(venta1, venta2, venta3);

        // Mock de cliente
        clienteMock = new Cliente();
        clienteMock.setId(100);
        clienteMock.setNombre("Cliente Test");
        clienteMock.setRfc("TEST123456");
        clienteMock.setBloqueado(false);
        clienteMock.setSaldoPendiente(BigDecimal.ZERO);
        clienteMock.setFechaCreacion(LocalDateTime.of(2023, 6, 1, 0, 0));

        // Mock de empleado
        empleadoMock = new Empleado();
        empleadoMock.setId(200);
        empleadoMock.setNombre("Juan");
        empleadoMock.setApellidos("Pérez");
        empleadoMock.setPuesto("Vendedor");
        empleadoMock.setSucursalId(1);
        empleadoMock.setActivo(true);

        // Mock de sucursal
        sucursalMock = new Sucursal();
        sucursalMock.setId(1);
        sucursalMock.setNombre("Sucursal Centro");
    }

    @Test
    @DisplayName("Debe generar análisis completo de ventas y clientes")
    void debeGenerarAnalisisCompleto() {
        // Arrange
        when(ventaRepository.findByFechaVentaBetween(any(), any())).thenReturn(ventasMock);
        when(sucursalRepository.findById(anyInt())).thenReturn(Optional.of(sucursalMock));
        when(clienteRepository.findById(anyInt())).thenReturn(Optional.of(clienteMock));
        when(empleadoRepository.findById(anyInt())).thenReturn(Optional.of(empleadoMock));

        // Act
        MetricaVentaClienteResponseDTO response = service.generarAnalisis(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getPeriodoInicio()).isEqualTo(request.getFechaInicio());
        assertThat(response.getPeriodoFin()).isEqualTo(request.getFechaFin());
        assertThat(response.getTipoPeriodo()).isEqualTo("MENSUAL");
        
        verify(ventaRepository, times(1)).findByFechaVentaBetween(any(), any());
    }

    @Test
    @DisplayName("Debe calcular resumen de ventas correctamente")
    void debeCalcularResumenVentas() {
        // Arrange
        when(ventaRepository.findByFechaVentaBetween(any(), any())).thenReturn(ventasMock);
        when(clienteRepository.findById(anyInt())).thenReturn(Optional.of(clienteMock));
        when(empleadoRepository.findById(anyInt())).thenReturn(Optional.of(empleadoMock));

        // Act
        MetricaVentaClienteResponseDTO response = service.generarAnalisis(request);

        // Assert
        assertThat(response.getResumenVentas()).isNotNull();
        assertThat(response.getResumenVentas().getTotalVentas())
                .isEqualByComparingTo(new BigDecimal("7000.00")); // 1500 + 2500 + 3000
        assertThat(response.getResumenVentas().getNumeroTransacciones()).isEqualTo(3);
        assertThat(response.getResumenVentas().getTicketPromedio())
                .isGreaterThan(BigDecimal.ZERO);
        assertThat(response.getResumenVentas().getDiasPeriodo()).isEqualTo(31);
    }

    @Test
    @DisplayName("Debe calcular resumen de clientes correctamente")
    void debeCalcularResumenClientes() {
        // Arrange
        when(ventaRepository.findByFechaVentaBetween(any(), any())).thenReturn(ventasMock);
        when(clienteRepository.findById(100)).thenReturn(Optional.of(clienteMock));
        when(clienteRepository.findById(101)).thenReturn(Optional.of(clienteMock));
        when(clienteRepository.findById(102)).thenReturn(Optional.of(clienteMock));
        when(empleadoRepository.findById(anyInt())).thenReturn(Optional.of(empleadoMock));

        // Act
        MetricaVentaClienteResponseDTO response = service.generarAnalisis(request);

        // Assert
        assertThat(response.getResumenClientes()).isNotNull();
        assertThat(response.getResumenClientes().getTotalClientesActivos()).isEqualTo(3);
        assertThat(response.getResumenClientes().getValorVidaCliente()).isNotNull();
        assertThat(response.getResumenClientes().getFrecuenciaCompra()).isNotNull();
    }

    @Test
    @DisplayName("Debe calcular resumen de vendedores correctamente")
    void debeCalcularResumenVendedores() {
        // Arrange
        when(ventaRepository.findByFechaVentaBetween(any(), any())).thenReturn(ventasMock);
        when(clienteRepository.findById(anyInt())).thenReturn(Optional.of(clienteMock));
        when(empleadoRepository.findById(200)).thenReturn(Optional.of(empleadoMock));
        
        Empleado empleado2 = new Empleado();
        empleado2.setId(201);
        empleado2.setNombre("María");
        empleado2.setApellidos("García");
        when(empleadoRepository.findById(201)).thenReturn(Optional.of(empleado2));

        // Act
        MetricaVentaClienteResponseDTO response = service.generarAnalisis(request);

        // Assert
        assertThat(response.getResumenVendedores()).isNotNull();
        assertThat(response.getResumenVendedores().getTotalVendedores()).isEqualTo(2);
        assertThat(response.getResumenVendedores().getTopVendedorId()).isNotNull();
        assertThat(response.getResumenVendedores().getTopVendedorVentas()).isNotNull();
    }

    @Test
    @DisplayName("Debe calcular métodos de pago correctamente")
    void debeCalcularMetodosPago() {
        // Arrange
        when(ventaRepository.findByFechaVentaBetween(any(), any())).thenReturn(ventasMock);
        when(clienteRepository.findById(anyInt())).thenReturn(Optional.of(clienteMock));
        when(empleadoRepository.findById(anyInt())).thenReturn(Optional.of(empleadoMock));

        // Act
        MetricaVentaClienteResponseDTO response = service.generarAnalisis(request);

        // Assert
        assertThat(response.getMetodosPago()).isNotNull();
        assertThat(response.getMetodosPago().getVentasEfectivo())
                .isEqualByComparingTo(new BigDecimal("1500.00"));
        assertThat(response.getMetodosPago().getVentasTarjeta())
                .isEqualByComparingTo(new BigDecimal("2500.00"));
        assertThat(response.getMetodosPago().getVentasCredito())
                .isEqualByComparingTo(new BigDecimal("3000.00"));
        assertThat(response.getMetodosPago().getPorcentajeEfectivo()).isNotNull();
    }

    @Test
    @DisplayName("Debe incluir detalle de vendedores cuando se solicita")
    void debeIncluirDetalleVendedores() {
        // Arrange
        when(ventaRepository.findByFechaVentaBetween(any(), any())).thenReturn(ventasMock);
        when(clienteRepository.findById(anyInt())).thenReturn(Optional.of(clienteMock));
        when(empleadoRepository.findById(200)).thenReturn(Optional.of(empleadoMock));
        
        Empleado empleado2 = new Empleado();
        empleado2.setId(201);
        empleado2.setNombre("María");
        empleado2.setApellidos("García");
        empleado2.setPuesto("Vendedor");
        when(empleadoRepository.findById(201)).thenReturn(Optional.of(empleado2));

        // Act
        MetricaVentaClienteResponseDTO response = service.generarAnalisis(request);

        // Assert
        assertThat(response.getTopVendedores()).isNotNull();
        assertThat(response.getTopVendedores()).hasSize(2);
        
        DetalleVendedorDTO topVendedor = response.getTopVendedores().get(0);
        assertThat(topVendedor.getVendedorId()).isNotNull();
        assertThat(topVendedor.getNombre()).isNotNull();
        assertThat(topVendedor.getTotalVentas()).isGreaterThan(BigDecimal.ZERO);
        assertThat(topVendedor.getRanking()).isEqualTo(1);
        assertThat(topVendedor.getClasificacion()).isNotNull();
    }

    @Test
    @DisplayName("Debe incluir detalle de clientes cuando se solicita")
    void debeIncluirDetalleClientes() {
        // Arrange
        when(ventaRepository.findByFechaVentaBetween(any(), any())).thenReturn(ventasMock);
        when(empleadoRepository.findById(anyInt())).thenReturn(Optional.of(empleadoMock));
        
        Cliente cliente1 = new Cliente();
        cliente1.setId(100);
        cliente1.setNombre("Cliente 1");
        cliente1.setRfc("CLI1");
        cliente1.setBloqueado(false);
        cliente1.setFechaCreacion(LocalDateTime.of(2023, 1, 1, 0, 0));
        when(clienteRepository.findById(100)).thenReturn(Optional.of(cliente1));
        
        Cliente cliente2 = new Cliente();
        cliente2.setId(101);
        cliente2.setNombre("Cliente 2");
        cliente2.setRfc("CLI2");
        cliente2.setBloqueado(false);
        cliente2.setFechaCreacion(LocalDateTime.of(2023, 2, 1, 0, 0));
        when(clienteRepository.findById(101)).thenReturn(Optional.of(cliente2));
        
        Cliente cliente3 = new Cliente();
        cliente3.setId(102);
        cliente3.setNombre("Cliente 3");
        cliente3.setRfc("CLI3");
        cliente3.setBloqueado(false);
        cliente3.setFechaCreacion(LocalDateTime.of(2023, 3, 1, 0, 0));
        when(clienteRepository.findById(102)).thenReturn(Optional.of(cliente3));

        // Act
        MetricaVentaClienteResponseDTO response = service.generarAnalisis(request);

        // Assert
        assertThat(response.getTopClientes()).isNotNull();
        assertThat(response.getTopClientes()).hasSize(3);
        
        DetalleClienteDTO topCliente = response.getTopClientes().get(0);
        assertThat(topCliente.getClienteId()).isNotNull();
        assertThat(topCliente.getNombre()).isNotNull();
        assertThat(topCliente.getTotalCompras()).isGreaterThan(BigDecimal.ZERO);
        assertThat(topCliente.getRanking()).isEqualTo(1);
        assertThat(topCliente.getSegmento()).isNotNull();
    }

    @Test
    @DisplayName("Debe calcular comparación con período anterior cuando se solicita")
    void debeCalcularComparacionPeriodoAnterior() {
        // Arrange
        request.setCompararPeriodoAnterior(true);
        
        when(ventaRepository.findByFechaVentaBetween(any(), any())).thenReturn(ventasMock);
        when(clienteRepository.findById(anyInt())).thenReturn(Optional.of(clienteMock));
        when(empleadoRepository.findById(anyInt())).thenReturn(Optional.of(empleadoMock));

        // Act
        MetricaVentaClienteResponseDTO response = service.generarAnalisis(request);

        // Assert
        assertThat(response.getComparacion()).isNotNull();
        assertThat(response.getComparacion().getVentasActual()).isNotNull();
        assertThat(response.getComparacion().getTendencia()).isNotNull();
    }

    @Test
    @DisplayName("Debe guardar métrica correctamente")
    void debeGuardarMetrica() {
        // Arrange
        MetricaVentaCliente metrica = new MetricaVentaCliente();
        metrica.setPeriodoInicio(LocalDate.of(2024, 1, 1));
        metrica.setPeriodoFin(LocalDate.of(2024, 1, 31));
        metrica.setTotalVentas(new BigDecimal("10000.00"));

        when(metricaRepository.save(any(MetricaVentaCliente.class))).thenReturn(metrica);

        // Act
        MetricaVentaCliente resultado = service.guardarMetrica(metrica);

        // Assert
        assertThat(resultado).isNotNull();
        verify(metricaRepository, times(1)).save(any(MetricaVentaCliente.class));
    }

    @Test
    @DisplayName("Debe obtener métrica consolidada existente")
    void debeObtenerMetricaConsolidada() {
        // Arrange
        LocalDate inicio = LocalDate.of(2024, 1, 1);
        LocalDate fin = LocalDate.of(2024, 1, 31);
        
        MetricaVentaCliente metrica = new MetricaVentaCliente();
        metrica.setPeriodoInicio(inicio);
        metrica.setPeriodoFin(fin);
        
        when(metricaRepository.findMetricaConsolidada(inicio, fin))
                .thenReturn(Optional.of(metrica));

        // Act
        Optional<MetricaVentaCliente> resultado = service.obtenerMetricaConsolidada(inicio, fin);

        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().getPeriodoInicio()).isEqualTo(inicio);
        verify(metricaRepository, times(1)).findMetricaConsolidada(inicio, fin);
    }

    @Test
    @DisplayName("Debe retornar vacío cuando no existe métrica consolidada")
    void debeRetornarVacioCuandoNoExisteMetrica() {
        // Arrange
        LocalDate inicio = LocalDate.of(2024, 1, 1);
        LocalDate fin = LocalDate.of(2024, 1, 31);
        
        when(metricaRepository.findMetricaConsolidada(inicio, fin))
                .thenReturn(Optional.empty());

        // Act
        Optional<MetricaVentaCliente> resultado = service.obtenerMetricaConsolidada(inicio, fin);

        // Assert
        assertThat(resultado).isEmpty();
        verify(metricaRepository, times(1)).findMetricaConsolidada(inicio, fin);
    }

    @Test
    @DisplayName("Debe obtener historial consolidado")
    void debeObtenerHistorialConsolidado() {
        // Arrange
        LocalDate fechaHasta = LocalDate.of(2024, 12, 31);
        String tipoPeriodo = "MENSUAL";
        
        MetricaVentaCliente metrica1 = new MetricaVentaCliente();
        metrica1.setId(1L);
        MetricaVentaCliente metrica2 = new MetricaVentaCliente();
        metrica2.setId(2L);
        
        when(metricaRepository.findHistorialConsolidado(tipoPeriodo, fechaHasta))
                .thenReturn(Arrays.asList(metrica1, metrica2));

        // Act
        List<MetricaVentaCliente> historial = service.obtenerHistorialConsolidado(tipoPeriodo, fechaHasta);

        // Assert
        assertThat(historial).isNotNull();
        assertThat(historial).hasSize(2);
        verify(metricaRepository, times(1)).findHistorialConsolidado(tipoPeriodo, fechaHasta);
    }
}
