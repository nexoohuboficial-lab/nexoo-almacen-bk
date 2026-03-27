package com.nexoohub.almacen.analitica.service;

import com.nexoohub.almacen.analitica.entity.ReporteRendimientoEmpleado;
import com.nexoohub.almacen.analitica.repository.ReporteRendimientoEmpleadoRepository;
import com.nexoohub.almacen.cotizaciones.entity.Cotizacion;
import com.nexoohub.almacen.cotizaciones.repository.CotizacionRepository;
import com.nexoohub.almacen.empleados.entity.Empleado;
import com.nexoohub.almacen.empleados.repository.EmpleadoRepository;
import com.nexoohub.almacen.ventas.entity.Devolucion;
import com.nexoohub.almacen.ventas.entity.Venta;
import com.nexoohub.almacen.ventas.repository.DevolucionRepository;
import com.nexoohub.almacen.ventas.repository.VentaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RendimientoPersonalServiceTest {

    @Mock private EmpleadoRepository empleadoRepository;
    @Mock private VentaRepository ventaRepository;
    @Mock private CotizacionRepository cotizacionRepository;
    @Mock private DevolucionRepository devolucionRepository;
    @Mock private ReporteRendimientoEmpleadoRepository reporteRepository;

    @InjectMocks
    private RendimientoPersonalService service;

    private Empleado empleadoTest;

    @BeforeEach
    void setUp() {
        empleadoTest = new Empleado();
        empleadoTest.setId(1);
        empleadoTest.setNombre("Juan");
        empleadoTest.setApellidos("Pérez");
        empleadoTest.setPuesto("Vendedor");
        empleadoTest.setActivo(true);
    }

    // ─────────────────────────────────────────────────────────────────
    // CASO 1: Empleado con 5 ventas, 3 cotizaciones (2 convertidas)
    //         → Tasa de conversión = 66.67%
    // ─────────────────────────────────────────────────────────────────

    @Test
    void calcular_ConVersionEnCotizaciones_CalculaCorrectamente() {
        // Arrange
        when(empleadoRepository.findByActivoTrue()).thenReturn(List.of(empleadoTest));

        List<Venta> ventas = crearVentas(5, BigDecimal.valueOf(100));
        when(ventaRepository.findVentasByVendedorAndPeriodo(eq(1), any(), any()))
                .thenReturn(ventas);

        // 3 cotizaciones, 2 convertidas
        Cotizacion cot1 = crearCotizacion("CONVERTIDA");
        Cotizacion cot2 = crearCotizacion("CONVERTIDA");
        Cotizacion cot3 = crearCotizacion("PENDIENTE");
        when(cotizacionRepository.findByVendedorId(1)).thenReturn(List.of(cot1, cot2, cot3));

        when(devolucionRepository.findBySucursalAndFecha(isNull(), any(), any()))
                .thenReturn(Collections.emptyList());
        when(ventaRepository.findHoraPicoByVendedorAndPeriodo(eq(1), any(), any()))
                .thenReturn(List.<Object[]>of(new Object[]{14L, 3L}));
        when(reporteRepository.findByEmpleadoIdAndMesAndAnio(1, 3, 2026)).thenReturn(Optional.empty());
        when(reporteRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // Act
        service.calcularRendimientoMensual(3, 2026);

        // Assert
        ArgumentCaptor<ReporteRendimientoEmpleado> captor =
                ArgumentCaptor.forClass(ReporteRendimientoEmpleado.class);
        verify(reporteRepository).save(captor.capture());

        ReporteRendimientoEmpleado snap = captor.getValue();
        assertEquals(5, snap.getTotalVentas());
        assertEquals(0, new BigDecimal("500.00").compareTo(snap.getMontoTotalVentas()));
        assertEquals(0, new BigDecimal("100.00").compareTo(snap.getTicketPromedio()));
        assertEquals(3, snap.getTotalCotizaciones());
        assertEquals(2, snap.getCotizacionesConvertidas());
        assertEquals(0, new BigDecimal("66.67").compareTo(snap.getTasaConversion()));
        assertEquals(14, snap.getHoraPico());
    }

    // ─────────────────────────────────────────────────────────────────
    // CASO 2: Empleado con 1 devolución sobre 4 ventas
    //         → Tasa de devolución = 25%
    // ─────────────────────────────────────────────────────────────────

    @Test
    void calcular_ConDevolucion_CalculaTasaCorrecta() {
        when(empleadoRepository.findByActivoTrue()).thenReturn(List.of(empleadoTest));

        // 4 ventas con IDs 10,11,12,13
        List<Venta> ventas = crearVentasConId(new int[]{10, 11, 12, 13}, BigDecimal.valueOf(200));
        when(ventaRepository.findVentasByVendedorAndPeriodo(eq(1), any(), any())).thenReturn(ventas);
        when(cotizacionRepository.findByVendedorId(1)).thenReturn(Collections.emptyList());

        // 1 devolución de la venta con id=10
        Devolucion dev = new Devolucion();
        dev.setVentaId(10);
        dev.setTotalDevuelto(BigDecimal.valueOf(200));
        dev.setFechaDevolucion(LocalDateTime.now());
        when(devolucionRepository.findBySucursalAndFecha(isNull(), any(), any()))
                .thenReturn(List.of(dev));

        when(ventaRepository.findHoraPicoByVendedorAndPeriodo(eq(1), any(), any()))
                .thenReturn(List.<Object[]>of(new Object[]{10L, 2L}));
        when(reporteRepository.findByEmpleadoIdAndMesAndAnio(1, 3, 2026)).thenReturn(Optional.empty());
        when(reporteRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.calcularRendimientoMensual(3, 2026);

        ArgumentCaptor<ReporteRendimientoEmpleado> captor =
                ArgumentCaptor.forClass(ReporteRendimientoEmpleado.class);
        verify(reporteRepository).save(captor.capture());

        ReporteRendimientoEmpleado snap = captor.getValue();
        assertEquals(4, snap.getTotalVentas());
        assertEquals(1, snap.getTotalDevoluciones());
        assertEquals(0, BigDecimal.valueOf(200).compareTo(snap.getMontoDevoluciones()));
        assertEquals(0, new BigDecimal("25.00").compareTo(snap.getTasaDevolucion()));
    }

    // ─────────────────────────────────────────────────────────────────
    // CASO 3: Empleado sin ventas en el periodo → snapshot con todo en 0
    // ─────────────────────────────────────────────────────────────────

    @Test
    void calcular_SinVentas_DebePersistirSnapshotEnCero() {
        when(empleadoRepository.findByActivoTrue()).thenReturn(List.of(empleadoTest));
        when(ventaRepository.findVentasByVendedorAndPeriodo(eq(1), any(), any()))
                .thenReturn(Collections.emptyList());
        when(cotizacionRepository.findByVendedorId(1)).thenReturn(Collections.emptyList());
        when(devolucionRepository.findBySucursalAndFecha(isNull(), any(), any()))
                .thenReturn(Collections.emptyList());
        when(reporteRepository.findByEmpleadoIdAndMesAndAnio(1, 3, 2026)).thenReturn(Optional.empty());
        when(reporteRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.calcularRendimientoMensual(3, 2026);

        ArgumentCaptor<ReporteRendimientoEmpleado> captor =
                ArgumentCaptor.forClass(ReporteRendimientoEmpleado.class);
        verify(reporteRepository).save(captor.capture());

        ReporteRendimientoEmpleado snap = captor.getValue();
        assertEquals(0, snap.getTotalVentas());
        assertEquals(0, BigDecimal.ZERO.compareTo(snap.getMontoTotalVentas()));
        assertEquals(0, BigDecimal.ZERO.compareTo(snap.getTasaConversion()));
        assertEquals(0, BigDecimal.ZERO.compareTo(snap.getTasaDevolucion()));
        assertNull(snap.getHoraPico()); // Sin ventas, no hay hora pico
    }

    // ─────────────────────────────────────────────────────────────────
    // CASO 4: Snapshot existente → debe usar UPDATE (no INSERT)
    // ─────────────────────────────────────────────────────────────────

    @Test
    void calcular_SnapshotExistente_ActualizaEnLugarDeSave() {
        when(empleadoRepository.findByActivoTrue()).thenReturn(List.of(empleadoTest));
        when(ventaRepository.findVentasByVendedorAndPeriodo(eq(1), any(), any()))
                .thenReturn(Collections.emptyList());
        when(cotizacionRepository.findByVendedorId(1)).thenReturn(Collections.emptyList());
        when(devolucionRepository.findBySucursalAndFecha(isNull(), any(), any()))
                .thenReturn(Collections.emptyList());

        // Ya existía un snapshot
        ReporteRendimientoEmpleado snapshotExistente = new ReporteRendimientoEmpleado();
        snapshotExistente.setEmpleadoId(1);
        snapshotExistente.setMes(3);
        snapshotExistente.setAnio(2026);
        snapshotExistente.setTotalVentas(99); // valor viejo que debe sobreescribirse
        when(reporteRepository.findByEmpleadoIdAndMesAndAnio(1, 3, 2026))
                .thenReturn(Optional.of(snapshotExistente));
        when(reporteRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.calcularRendimientoMensual(3, 2026);

        // El save debe usar el objeto reutilizado (con el valor viejo sobreescrito)
        ArgumentCaptor<ReporteRendimientoEmpleado> captor =
                ArgumentCaptor.forClass(ReporteRendimientoEmpleado.class);
        verify(reporteRepository).save(captor.capture());
        assertEquals(0, captor.getValue().getTotalVentas()); // el viejo 99 fue actualizado a 0
    }

    // ─────────────────────────────────────────────────────────────────
    // HELPERS
    // ─────────────────────────────────────────────────────────────────

    private List<Venta> crearVentas(int cantidad, BigDecimal monto) {
        return java.util.stream.IntStream.rangeClosed(1, cantidad).mapToObj(i -> {
            Venta v = new Venta();
            v.setId(i);
            v.setTotal(monto);
            v.setFechaVenta(LocalDateTime.now().minusDays(i));
            return v;
        }).toList();
    }

    private List<Venta> crearVentasConId(int[] ids, BigDecimal monto) {
        return Arrays.stream(ids).mapToObj(i -> {
            Venta v = new Venta();
            v.setId(i);
            v.setTotal(monto);
            v.setFechaVenta(LocalDateTime.now().minusDays(i));
            return v;
        }).toList();
    }

    private Cotizacion crearCotizacion(String estado) {
        Cotizacion c = new Cotizacion();
        c.setEstado(estado);
        c.setFechaCotizacion(LocalDateTime.now().minusDays(5));
        return c;
    }
}
