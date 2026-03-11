package com.nexoohub.almacen.fidelidad.service;

import com.nexoohub.almacen.catalogo.entity.Cliente;
import com.nexoohub.almacen.catalogo.repository.ClienteRepository;
import com.nexoohub.almacen.common.exception.BusinessException;
import com.nexoohub.almacen.common.exception.ResourceNotFoundException;
import com.nexoohub.almacen.fidelidad.dto.*;
import com.nexoohub.almacen.fidelidad.entity.MovimientoPunto;
import com.nexoohub.almacen.fidelidad.entity.ProgramaFidelidad;
import com.nexoohub.almacen.fidelidad.repository.MovimientoPuntoRepository;
import com.nexoohub.almacen.fidelidad.repository.ProgramaFidelidadRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para ProgramaFidelidadService.
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ProgramaFidelidadService - Tests Unitarios")
class ProgramaFidelidadServiceTest {

    @Mock
    private ProgramaFidelidadRepository programaRepository;

    @Mock
    private MovimientoPuntoRepository movimientoRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ProgramaFidelidadService fidelidadService;

    private Cliente cliente;
    private ProgramaFidelidad programa;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setId(1);
        cliente.setNombre("Juan Pérez");
        cliente.setTelefono("5551234567");

        programa = new ProgramaFidelidad();
        programa.setId(1);
        programa.setClienteId(cliente.getId());
        programa.setPuntosAcumulados(0);
        programa.setTotalCompras(BigDecimal.ZERO);
        programa.setTotalCanjeado(BigDecimal.ZERO);
        programa.setActivo(true);
    }

    @Test
    @DisplayName("Crear programa exitosamente")
    void crearPrograma_Exitoso() {
        when(clienteRepository.findById(1)).thenReturn(Optional.of(cliente));
        when(programaRepository.existsByClienteId(1)).thenReturn(false);
        when(programaRepository.save(any(ProgramaFidelidad.class))).thenReturn(programa);

        ProgramaFidelidadResponseDTO resultado = fidelidadService.crearPrograma(1);

        assertThat(resultado).isNotNull();
        assertThat(resultado.clienteId()).isEqualTo(1);
        assertThat(resultado.puntosAcumulados()).isEqualTo(0);
        assertThat(resultado.activo()).isTrue();

        verify(programaRepository, times(1)).save(any(ProgramaFidelidad.class));
    }

    @Test
    @DisplayName("No permitir crear programa duplicado")
    void crearPrograma_YaExiste() {
        when(clienteRepository.findById(1)).thenReturn(Optional.of(cliente));
        when(programaRepository.existsByClienteId(1)).thenReturn(true);

        assertThatThrownBy(() -> fidelidadService.crearPrograma(1))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("ya tiene un programa de fidelidad");

        verify(programaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Fallar si cliente no existe")
    void crearPrograma_ClienteNoExiste() {
        when(clienteRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fidelidadService.crearPrograma(999))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Cliente no encontrado");
    }

    @Test
    @DisplayName("Consultar programa por cliente")
    void consultarPorCliente_Exitoso() {
        when(clienteRepository.findById(1)).thenReturn(Optional.of(cliente));
        when(programaRepository.findByClienteId(1)).thenReturn(Optional.of(programa));

        ProgramaFidelidadResponseDTO resultado = fidelidadService.consultarPorCliente(1);

        assertThat(resultado).isNotNull();
        assertThat(resultado.clienteId()).isEqualTo(1);
        assertThat(resultado.clienteNombre()).isEqualTo("Juan Pérez");
    }

    @Test
    @DisplayName("Acumular puntos correctamente")
    void acumularPuntos_Exitoso() {
        when(clienteRepository.findById(1)).thenReturn(Optional.of(cliente));
        when(programaRepository.findByClienteId(1)).thenReturn(Optional.of(programa));
        when(programaRepository.save(any(ProgramaFidelidad.class))).thenReturn(programa);
        when(movimientoRepository.save(any(MovimientoPunto.class))).thenReturn(new MovimientoPunto());

        AcumularPuntosRequestDTO request = new AcumularPuntosRequestDTO(
                1,
                new BigDecimal("250.00"),
                null,
                "Compra de prueba"
        );

        ProgramaFidelidadResponseDTO resultado = fidelidadService.acumularPuntos(request);

        assertThat(resultado).isNotNull();
        verify(programaRepository, times(1)).save(any(ProgramaFidelidad.class));
        verify(movimientoRepository, times(1)).save(any(MovimientoPunto.class));
    }

    @Test
    @DisplayName("No acumular puntos si monto es insuficiente")
    void acumularPuntos_MontoInsuficiente() {
        when(clienteRepository.findById(1)).thenReturn(Optional.of(cliente));
        when(programaRepository.findByClienteId(1)).thenReturn(Optional.of(programa));

        AcumularPuntosRequestDTO request = new AcumularPuntosRequestDTO(
                1,
                new BigDecimal("5.00"), // Menos de $10
                null,
                "Compra pequeña"
        );

        assertThatThrownBy(() -> fidelidadService.acumularPuntos(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("insuficiente para generar puntos");
    }

    @Test
    @DisplayName("Canjear puntos correctamente")
    void canjearPuntos_Exitoso() {
        programa.setPuntosAcumulados(200);

        when(clienteRepository.findById(1)).thenReturn(Optional.of(cliente));
        when(programaRepository.findByClienteId(1)).thenReturn(Optional.of(programa));
        when(programaRepository.save(any(ProgramaFidelidad.class))).thenReturn(programa);
        when(movimientoRepository.save(any(MovimientoPunto.class))).thenReturn(new MovimientoPunto());

        CanjearPuntosRequestDTO request = new CanjearPuntosRequestDTO(
                1,
                100,
                null,
                "Canje de prueba"
        );

        ProgramaFidelidadResponseDTO resultado = fidelidadService.canjearPuntos(request);

        assertThat(resultado).isNotNull();
        verify(programaRepository, times(1)).save(any(ProgramaFidelidad.class));
        verify(movimientoRepository, times(1)).save(any(MovimientoPunto.class));
    }

    @Test
    @DisplayName("No canjear si puntos son insuficientes")
    void canjearPuntos_PuntosInsuficientes() {
        programa.setPuntosAcumulados(50);

        when(clienteRepository.findById(1)).thenReturn(Optional.of(cliente));
        when(programaRepository.findByClienteId(1)).thenReturn(Optional.of(programa));

        CanjearPuntosRequestDTO request = new CanjearPuntosRequestDTO(
                1,
                100,
                null,
                "Intento de canje"
        );

        assertThatThrownBy(() -> fidelidadService.canjearPuntos(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Puntos insuficientes");
    }

    @Test
    @DisplayName("No canjear menos del mínimo requerido")
    void canjearPuntos_MenosDelMinimo() {
        programa.setPuntosAcumulados(200);

        when(clienteRepository.findById(1)).thenReturn(Optional.of(cliente));
        when(programaRepository.findByClienteId(1)).thenReturn(Optional.of(programa));

        CanjearPuntosRequestDTO request = new CanjearPuntosRequestDTO(
                1,
                50, // Menos de 100
                null,
                "Intento de canje"
        );

        assertThatThrownBy(() -> fidelidadService.canjearPuntos(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("mínimo 100");
    }

    @Test
    @DisplayName("Calcular descuento por puntos correctamente")
    void calcularDescuentoPorPuntos_Exitoso() {
        BigDecimal descuento = fidelidadService.calcularDescuentoPorPuntos(200);

        assertThat(descuento).isEqualByComparingTo(new BigDecimal("20.00")); // 200 puntos = $20
    }

    @Test
    @DisplayName("Retornar cero si puntos son menores al mínimo")
    void calcularDescuentoPorPuntos_MenorMinimo() {
        BigDecimal descuento = fidelidadService.calcularDescuentoPorPuntos(50);

        assertThat(descuento).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Obtener estadísticas del sistema")
    void obtenerEstadisticas_Exitoso() {
        when(programaRepository.contarProgramasActivos()).thenReturn(10L);
        when(programaRepository.obtenerTotalPuntosEnSistema()).thenReturn(5000L);

        EstadisticasFidelidadDTO estadisticas = fidelidadService.obtenerEstadisticas();

        assertThat(estadisticas).isNotNull();
        assertThat(estadisticas.totalProgramasActivos()).isEqualTo(10L);
        assertThat(estadisticas.totalPuntosEnSistema()).isEqualTo(5000L);
        assertThat(estadisticas.tasaConversionPuntos()).isEqualTo(10);
        assertThat(estadisticas.tasaConversionDescuento()).isEqualTo(100);
    }

    @Test
    @DisplayName("Desactivar programa")
    void desactivarPrograma_Exitoso() {
        when(programaRepository.findByClienteId(1)).thenReturn(Optional.of(programa));
        when(programaRepository.save(any(ProgramaFidelidad.class))).thenReturn(programa);

        fidelidadService.desactivarPrograma(1);

        verify(programaRepository, times(1)).save(any(ProgramaFidelidad.class));
    }

    @Test
    @DisplayName("Reactivar programa")
    void reactivarPrograma_Exitoso() {
        programa.setActivo(false);

        when(programaRepository.findByClienteId(1)).thenReturn(Optional.of(programa));
        when(programaRepository.save(any(ProgramaFidelidad.class))).thenReturn(programa);

        fidelidadService.reactivarPrograma(1);

        verify(programaRepository, times(1)).save(any(ProgramaFidelidad.class));
    }

    @Test
    @DisplayName("Obtener historial de movimientos")
    void obtenerHistorial_Exitoso() {
        when(programaRepository.findByClienteId(1)).thenReturn(Optional.of(programa));
        when(movimientoRepository.obtenerHistorialPorPrograma(1)).thenReturn(List.of());

        List<MovimientoPuntoResponseDTO> historial = fidelidadService.obtenerHistorial(1);

        assertThat(historial).isNotNull();
        assertThat(historial).isEmpty();
        verify(movimientoRepository, times(1)).obtenerHistorialPorPrograma(1);
    }
}
