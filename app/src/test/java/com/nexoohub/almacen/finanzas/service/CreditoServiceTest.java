package com.nexoohub.almacen.finanzas.service;

import com.nexoohub.almacen.catalogo.entity.Cliente;
import com.nexoohub.almacen.catalogo.repository.ClienteRepository;
import com.nexoohub.almacen.finanzas.dto.*;
import com.nexoohub.almacen.finanzas.entity.HistorialCredito;
import com.nexoohub.almacen.finanzas.entity.LimiteCredito;
import com.nexoohub.almacen.finanzas.repository.HistorialCreditoRepository;
import com.nexoohub.almacen.finanzas.repository.LimiteCreditoRepository;
import com.nexoohub.almacen.ventas.entity.Venta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para CreditoService.
 * 
 * @author NexooHub Development Team
 * @since 1.2.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CreditoService - Tests de Control de Crédito")
class CreditoServiceTest {

    @Mock
    private LimiteCreditoRepository limiteCreditoRepository;

    @Mock
    private HistorialCreditoRepository historialCreditoRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private CreditoService creditoService;

    private Cliente clienteMock;
    private LimiteCredito limiteCreditoMock;
    private LimiteCreditoRequestDTO requestMock;

    @BeforeEach
    void setUp() {
        clienteMock = new Cliente();
        clienteMock.setId(1);
        clienteMock.setNombre("Juan Pérez");
        clienteMock.setRfc("PEXJ800101XXX");
        clienteMock.setTelefono("555-1234");
        clienteMock.setEmail("juan@test.com");

        limiteCreditoMock = new LimiteCredito();
        limiteCreditoMock.setId(1);
        limiteCreditoMock.setCliente(clienteMock);
        limiteCreditoMock.setLimiteAutorizado(BigDecimal.valueOf(10000));
        limiteCreditoMock.setSaldoUtilizado(BigDecimal.valueOf(3000));
        limiteCreditoMock.setEstado("ACTIVO");
        limiteCreditoMock.setPlazoPagoDias(30);
        limiteCreditoMock.setMaxFacturasVencidas(3);
        limiteCreditoMock.setPermiteSobregiro(false);
        limiteCreditoMock.setMontoSobregiro(BigDecimal.ZERO);
        limiteCreditoMock.setFechaRevision(LocalDate.now());

        requestMock = new LimiteCreditoRequestDTO();
        requestMock.setClienteId(1);
        requestMock.setLimiteAutorizado(BigDecimal.valueOf(10000));
        requestMock.setPlazoPagoDias(30);
        requestMock.setMaxFacturasVencidas(3);
        requestMock.setPermiteSobregiro(false);
        requestMock.setMontoSobregiro(BigDecimal.ZERO);
    }

    // ==================== TESTS DE CREACIÓN ====================

    @Test
    @DisplayName("Debe crear límite de crédito exitosamente")
    void debeCrearLimiteCreditoExitosamente() {
        // Given
        when(clienteRepository.findById(1)).thenReturn(Optional.of(clienteMock));
        when(limiteCreditoRepository.findByClienteId(1)).thenReturn(Optional.empty());
        when(limiteCreditoRepository.save(any(LimiteCredito.class))).thenReturn(limiteCreditoMock);

        // When
        LimiteCreditoResponseDTO response = creditoService.crearLimiteCredito(requestMock);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1);
        assertThat(response.getLimiteAutorizado()).isEqualByComparingTo(BigDecimal.valueOf(10000));
        assertThat(response.getSaldoUtilizado()).isEqualByComparingTo(BigDecimal.valueOf(3000));
        assertThat(response.getEstado()).isEqualTo("ACTIVO");

        verify(clienteRepository).findById(1);
        verify(limiteCreditoRepository).findByClienteId(1);
        verify(limiteCreditoRepository).save(any(LimiteCredito.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si cliente no existe al crear límite")
    void debeLanzarExcepcionSiClienteNoExisteAlCrear() {
        // Given
        when(clienteRepository.findById(1)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> creditoService.crearLimiteCredito(requestMock))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cliente no encontrado");

        verify(clienteRepository).findById(1);
        verify(limiteCreditoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar excepción si cliente ya tiene límite configurado")
    void debeLanzarExcepcionSiClienteYaTieneLimite() {
        // Given
        when(clienteRepository.findById(1)).thenReturn(Optional.of(clienteMock));
        when(limiteCreditoRepository.findByClienteId(1)).thenReturn(Optional.of(limiteCreditoMock));

        // When & Then
        assertThatThrownBy(() -> creditoService.crearLimiteCredito(requestMock))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ya tiene un límite de crédito configurado");

        verify(clienteRepository).findById(1);
        verify(limiteCreditoRepository).findByClienteId(1);
        verify(limiteCreditoRepository, never()).save(any());
    }

    // ==================== TESTS DE ACTUALIZACIÓN ====================

    @Test
    @DisplayName("Debe actualizar límite de crédito exitosamente")
    void debeActualizarLimiteCreditoExitosamente() {
        // Given
        requestMock.setLimiteAutorizado(BigDecimal.valueOf(15000));
        when(limiteCreditoRepository.findByClienteId(1)).thenReturn(Optional.of(limiteCreditoMock));
        when(limiteCreditoRepository.save(any(LimiteCredito.class))).thenReturn(limiteCreditoMock);

        // When
        LimiteCreditoResponseDTO response = creditoService.actualizarLimiteCredito(1, requestMock);

        // Then
        assertThat(response).isNotNull();
        verify(limiteCreditoRepository).findByClienteId(1);
        verify(limiteCreditoRepository).save(any(LimiteCredito.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si no existe límite al actualizar")
    void debeLanzarExcepcionSiNoExisteLimiteAlActualizar() {
        // Given
        when(limiteCreditoRepository.findByClienteId(1)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> creditoService.actualizarLimiteCredito(1, requestMock))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No existe límite de crédito");

        verify(limiteCreditoRepository).findByClienteId(1);
        verify(limiteCreditoRepository, never()).save(any());
    }

    // ==================== TESTS DE CONSULTA ====================

    @Test
    @DisplayName("Debe obtener límite por cliente exitosamente")
    void debeObtenerLimitePorClienteExitosamente() {
        // Given
        when(limiteCreditoRepository.findByClienteId(1)).thenReturn(Optional.of(limiteCreditoMock));

        // When
        LimiteCreditoResponseDTO response = creditoService.obtenerLimitePorCliente(1);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getCliente().getNombre()).isEqualTo("Juan Pérez");
        assertThat(response.getCreditoDisponible()).isEqualByComparingTo(BigDecimal.valueOf(7000));

        verify(limiteCreditoRepository).findByClienteId(1);
    }

    @Test
    @DisplayName("Debe calcular porcentaje de utilización correctamente")
    void debeCalcularPorcentajeUtilizacionCorrectamente() {
        // Given
        when(limiteCreditoRepository.findByClienteId(1)).thenReturn(Optional.of(limiteCreditoMock));

        // When
        LimiteCreditoResponseDTO response = creditoService.obtenerLimitePorCliente(1);

        // Then
        assertThat(response.getPorcentajeUtilizacion()).isEqualByComparingTo(BigDecimal.valueOf(30.00));
    }

    @Test
    @DisplayName("Debe listar todos los límites exitosamente")
    void debeListarTodosLosLimitesExitosamente() {
        // Given
        List<LimiteCredito> limites = Arrays.asList(limiteCreditoMock);
        when(limiteCreditoRepository.findAll()).thenReturn(limites);

        // When
        List<LimiteCreditoResponseDTO> response = creditoService.listarTodosLosLimites();

        // Then
        assertThat(response).hasSize(1);
        verify(limiteCreditoRepository).findAll();
    }

    @Test
    @DisplayName("Debe listar límites por estado")
    void debeListarLimitesPorEstado() {
        // Given
        List<LimiteCredito> limites = Arrays.asList(limiteCreditoMock);
        when(limiteCreditoRepository.findByEstado("ACTIVO")).thenReturn(limites);

        // When
        List<LimiteCreditoResponseDTO> response = creditoService.listarPorEstado("ACTIVO");

        // Then
        assertThat(response).hasSize(1);
        assertThat(response.get(0).getEstado()).isEqualTo("ACTIVO");
        verify(limiteCreditoRepository).findByEstado("ACTIVO");
    }

    @Test
    @DisplayName("Debe listar clientes próximos a exceder límite")
    void debeListarClientesProximosAExceder() {
        // Given
        List<LimiteCredito> limites = Arrays.asList(limiteCreditoMock);
        when(limiteCreditoRepository.findProximosAExceder()).thenReturn(limites);

        // When
        List<LimiteCreditoResponseDTO> response = creditoService.listarProximosAExceder();

        // Then
        assertThat(response).isNotEmpty();
        verify(limiteCreditoRepository).findProximosAExceder();
    }

    @Test
    @DisplayName("Debe listar clientes en sobregiro")
    void debeListarClientesEnSobregiro() {
        // Given
        List<LimiteCredito> limites = Arrays.asList(limiteCreditoMock);
        when(limiteCreditoRepository.findEnSobregiro()).thenReturn(limites);

        // When
        List<LimiteCreditoResponseDTO> response = creditoService.listarEnSobregiro();

        // Then
        assertThat(response).isNotEmpty();
        verify(limiteCreditoRepository).findEnSobregiro();
    }

    // ==================== TESTS DE VALIDACIÓN ====================

    @Test
    @DisplayName("Debe validar crédito disponible correctamente cuando hay suficiente")
    void debeValidarCreditoDisponibleCuandoHaySuficiente() {
        // Given
        when(limiteCreditoRepository.findByClienteId(1)).thenReturn(Optional.of(limiteCreditoMock));

        // When
        ValidacionCreditoDTO response = creditoService.validarCreditoDisponible(1, BigDecimal.valueOf(5000));

        // Then
        assertThat(response.getCreditoDisponible()).isTrue();
        assertThat(response.getCodigo()).isEqualTo("OK");
        assertThat(response.getMontoDisponible()).isEqualByComparingTo(BigDecimal.valueOf(7000));
        assertThat(response.getEstado()).isEqualTo("ACTIVO");

        verify(limiteCreditoRepository).findByClienteId(1);
    }

    @Test
    @DisplayName("Debe validar y rechazar cuando no hay crédito suficiente")
    void debeRechazarCuandoNoHayCreditoSuficiente() {
        // Given
        when(limiteCreditoRepository.findByClienteId(1)).thenReturn(Optional.of(limiteCreditoMock));

        // When
        ValidacionCreditoDTO response = creditoService.validarCreditoDisponible(1, BigDecimal.valueOf(8000));

        // Then
        assertThat(response.getCreditoDisponible()).isFalse();
        assertThat(response.getCodigo()).isEqualTo("LIMITE_EXCEDIDO");
        assertThat(response.getMensaje()).contains("Crédito insuficiente");

        verify(limiteCreditoRepository).findByClienteId(1);
    }

    @Test
    @DisplayName("Debe rechazar validación si cliente no tiene límite configurado")
    void debeRechazarSiClienteNoTieneLimiteConfigurado() {
        // Given
        when(limiteCreditoRepository.findByClienteId(1)).thenReturn(Optional.empty());

        // When
        ValidacionCreditoDTO response = creditoService.validarCreditoDisponible(1, BigDecimal.valueOf(5000));

        // Then
        assertThat(response.getCreditoDisponible()).isFalse();
        assertThat(response.getCodigo()).isEqualTo("SIN_CREDITO");
        assertThat(response.getMensaje()).contains("no tiene límite de crédito configurado");

        verify(limiteCreditoRepository).findByClienteId(1);
    }

    @Test
    @DisplayName("Debe rechazar validación si crédito está bloqueado")
    void debeRechazarSiCreditoBloqueado() {
        // Given
        limiteCreditoMock.setEstado("BLOQUEADO");
        when(limiteCreditoRepository.findByClienteId(1)).thenReturn(Optional.of(limiteCreditoMock));

        // When
        ValidacionCreditoDTO response = creditoService.validarCreditoDisponible(1, BigDecimal.valueOf(1000));

        // Then
        assertThat(response.getCreditoDisponible()).isFalse();
        assertThat(response.getCodigo()).isEqualTo("BLOQUEADO");
        assertThat(response.getEstado()).isEqualTo("BLOQUEADO");

        verify(limiteCreditoRepository).findByClienteId(1);
    }

    @Test
    @DisplayName("Debe permitir sobregiro cuando está autorizado")
    void debePermitirSobregiroCuandoEstaAutorizado() {
        // Given
        limiteCreditoMock.setPermiteSobregiro(true);
        limiteCreditoMock.setMontoSobregiro(BigDecimal.valueOf(2000));
        when(limiteCreditoRepository.findByClienteId(1)).thenReturn(Optional.of(limiteCreditoMock));

        // When - Monto que excede límite pero no excede límite + sobregiro
        ValidacionCreditoDTO response = creditoService.validarCreditoDisponible(1, BigDecimal.valueOf(8000));

        // Then
        assertThat(response.getCreditoDisponible()).isTrue();
        assertThat(response.getCodigo()).isEqualTo("OK");

        verify(limiteCreditoRepository).findByClienteId(1);
    }

    // ==================== TESTS DE CARGOS ====================

    @Test
    @DisplayName("Debe registrar cargo exitosamente")
    void debeRegistrarCargoExitosamente() {
        // Given
        Venta venta = new Venta();
        venta.setId(100);
        venta.setTotal(BigDecimal.valueOf(2000));

        HistorialCredito historialMock = new HistorialCredito();
        historialMock.setId(1);
        historialMock.setCliente(clienteMock);
        historialMock.setVenta(venta);
        historialMock.setTipoMovimiento("CARGO");
        historialMock.setMonto(BigDecimal.valueOf(2000));
        historialMock.setSaldoResultante(BigDecimal.valueOf(5000));

        when(clienteRepository.findById(1)).thenReturn(Optional.of(clienteMock));
        when(limiteCreditoRepository.findByClienteId(1)).thenReturn(Optional.of(limiteCreditoMock));
        when(limiteCreditoRepository.save(any(LimiteCredito.class))).thenReturn(limiteCreditoMock);
        when(historialCreditoRepository.save(any(HistorialCredito.class))).thenReturn(historialMock);

        // When
        HistorialCreditoResponseDTO response = creditoService.registrarCargo(1, venta, BigDecimal.valueOf(2000), "testuser");

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getTipoMovimiento()).isEqualTo("CARGO");
        assertThat(response.getMonto()).isEqualByComparingTo(BigDecimal.valueOf(2000));

        verify(limiteCreditoRepository).save(any(LimiteCredito.class));
        verify(historialCreditoRepository).save(any(HistorialCredito.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si cargo excede límite")
    void debeLanzarExcepcionSiCargoExcedeLimite() {
        // Given
        Venta venta = new Venta();
        venta.setId(100);

        when(clienteRepository.findById(1)).thenReturn(Optional.of(clienteMock));
        when(limiteCreditoRepository.findByClienteId(1)).thenReturn(Optional.of(limiteCreditoMock));

        // When & Then - Cargo de 8000 excede disponible de 7000
        assertThatThrownBy(() -> creditoService.registrarCargo(1, venta, BigDecimal.valueOf(8000), "testuser"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("excede el límite de crédito");

        verify(limiteCreditoRepository, never()).save(any());
        verify(historialCreditoRepository, never()).save(any());
    }

    // ==================== TESTS DE ABONOS ====================

    @Test
    @DisplayName("Debe registrar abono exitosamente")
    void debeRegistrarAbonoExitosamente() {
        // Given
        AbonoRequestDTO abonoRequest = new AbonoRequestDTO();
        abonoRequest.setClienteId(1);
        abonoRequest.setMonto(BigDecimal.valueOf(1000));
        abonoRequest.setMetodoPago("EFECTIVO");
        abonoRequest.setConcepto("Pago parcial");

        HistorialCredito historialMock = new HistorialCredito();
        historialMock.setId(1);
        historialMock.setCliente(clienteMock);
        historialMock.setTipoMovimiento("ABONO");
        historialMock.setMonto(BigDecimal.valueOf(1000));
        historialMock.setSaldoResultante(BigDecimal.valueOf(2000));

        when(clienteRepository.findById(1)).thenReturn(Optional.of(clienteMock));
        when(limiteCreditoRepository.findByClienteId(1)).thenReturn(Optional.of(limiteCreditoMock));
        when(limiteCreditoRepository.save(any(LimiteCredito.class))).thenReturn(limiteCreditoMock);
        when(historialCreditoRepository.save(any(HistorialCredito.class))).thenReturn(historialMock);

        // When
        HistorialCreditoResponseDTO response = creditoService.registrarAbono(abonoRequest, "testuser");

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getTipoMovimiento()).isEqualTo("ABONO");
        assertThat(response.getMonto()).isEqualByComparingTo(BigDecimal.valueOf(1000));

        verify(limiteCreditoRepository).save(any(LimiteCredito.class));
        verify(historialCreditoRepository).save(any(HistorialCredito.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si abono excede saldo utilizado")
    void debeLanzarExcepcionSiAbonoExcedeSaldoUtilizado() {
        // Given
        AbonoRequestDTO abonoRequest = new AbonoRequestDTO();
        abonoRequest.setClienteId(1);
        abonoRequest.setMonto(BigDecimal.valueOf(5000)); // Mayor al saldo utilizado (3000)
        abonoRequest.setMetodoPago("EFECTIVO");

        when(clienteRepository.findById(1)).thenReturn(Optional.of(clienteMock));
        when(limiteCreditoRepository.findByClienteId(1)).thenReturn(Optional.of(limiteCreditoMock));

        // When & Then
        assertThatThrownBy(() -> creditoService.registrarAbono(abonoRequest, "testuser"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("no puede ser mayor al saldo utilizado");

        verify(limiteCreditoRepository, never()).save(any());
        verify(historialCreditoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe desbloquear automáticamente al registrar abono si saldo queda bajo límite")
    void debeDesbloquearAutomaticamenteAlRegistrarAbono() {
        // Given
        limiteCreditoMock.setEstado("BLOQUEADO");
        limiteCreditoMock.setSaldoUtilizado(BigDecimal.valueOf(11000)); // Sobre límite

        AbonoRequestDTO abonoRequest = new AbonoRequestDTO();
        abonoRequest.setClienteId(1);
        abonoRequest.setMonto(BigDecimal.valueOf(2000)); // Reduce a 9000 (bajo límite)
        abonoRequest.setMetodoPago("EFECTIVO");

        HistorialCredito historialMock = new HistorialCredito();
        historialMock.setId(1);
        historialMock.setCliente(clienteMock);

        when(clienteRepository.findById(1)).thenReturn(Optional.of(clienteMock));
        when(limiteCreditoRepository.findByClienteId(1)).thenReturn(Optional.of(limiteCreditoMock));
        when(limiteCreditoRepository.save(any(LimiteCredito.class))).thenReturn(limiteCreditoMock);
        when(historialCreditoRepository.save(any(HistorialCredito.class))).thenReturn(historialMock);

        // When
        creditoService.registrarAbono(abonoRequest, "testuser");

        // Then
        verify(limiteCreditoRepository).save(any(LimiteCredito.class));
    }

    // ==================== TESTS DE BLOQUEO/DESBLOQUEO ====================

    @Test
    @DisplayName("Debe bloquear crédito exitosamente")
    void debeBloquearCreditoExitosamente() {
        // Given
        when(limiteCreditoRepository.findByClienteId(1)).thenReturn(Optional.of(limiteCreditoMock));
        when(limiteCreditoRepository.save(any(LimiteCredito.class))).thenReturn(limiteCreditoMock);

        // When
        creditoService.bloquearCredito(1, "Morosidad");

        // Then
        verify(limiteCreditoRepository).findByClienteId(1);
        verify(limiteCreditoRepository).save(any(LimiteCredito.class));
    }

    @Test
    @DisplayName("Debe desbloquear crédito exitosamente")
    void debeDesbloquearCreditoExitosamente() {
        // Given
        limiteCreditoMock.setEstado("BLOQUEADO");
        when(limiteCreditoRepository.findByClienteId(1)).thenReturn(Optional.of(limiteCreditoMock));
        when(limiteCreditoRepository.save(any(LimiteCredito.class))).thenReturn(limiteCreditoMock);

        // When
        creditoService.desbloquearCredito(1);

        // Then
        verify(limiteCreditoRepository).findByClienteId(1);
        verify(limiteCreditoRepository).save(any(LimiteCredito.class));
    }

    @Test
    @DisplayName("Debe suspender crédito exitosamente")
    void debeSuspenderCreditoExitosamente() {
        // Given
        when(limiteCreditoRepository.findByClienteId(1)).thenReturn(Optional.of(limiteCreditoMock));
        when(limiteCreditoRepository.save(any(LimiteCredito.class))).thenReturn(limiteCreditoMock);

        // When
        creditoService.suspenderCredito(1, "Revisión pendiente");

        // Then
        verify(limiteCreditoRepository).findByClienteId(1);
        verify(limiteCreditoRepository).save(any(LimiteCredito.class));
    }

    // ==================== TESTS DE HISTORIAL ====================

    @Test
    @DisplayName("Debe obtener historial de cliente paginado")
    void debeObtenerHistorialClientePaginado() {
        // Given
        HistorialCredito historial = new HistorialCredito();
        historial.setId(1);
        historial.setCliente(clienteMock);
        historial.setTipoMovimiento("CARGO");
        historial.setMonto(BigDecimal.valueOf(1000));
        historial.setSaldoResultante(BigDecimal.valueOf(4000));

        Page<HistorialCredito> page = new PageImpl<>(Arrays.asList(historial));
        Pageable pageable = PageRequest.of(0, 20);

        when(historialCreditoRepository.findByClienteId(1, pageable)).thenReturn(page);

        // When
        Page<HistorialCreditoResponseDTO> response = creditoService.obtenerHistorialCliente(1, pageable);

        // Then
        assertThat(response).isNotEmpty();
        assertThat(response.getTotalElements()).isEqualTo(1);
        verify(historialCreditoRepository).findByClienteId(1, pageable);
    }

    @Test
    @DisplayName("Debe obtener solo cargos de cliente")
    void debeObtenerSoloCargosDeCliente() {
        // Given
        HistorialCredito cargo = new HistorialCredito();
        cargo.setId(1);
        cargo.setCliente(clienteMock);
        cargo.setTipoMovimiento("CARGO");

        Page<HistorialCredito> page = new PageImpl<>(Arrays.asList(cargo));
        Pageable pageable = PageRequest.of(0, 20);

        when(historialCreditoRepository.findCargosByClienteId(1, pageable)).thenReturn(page);

        // When
        Page<HistorialCreditoResponseDTO> response = creditoService.obtenerCargosCliente(1, pageable);

        // Then
        assertThat(response).isNotEmpty();
        assertThat(response.getContent().get(0).getTipoMovimiento()).isEqualTo("CARGO");
        verify(historialCreditoRepository).findCargosByClienteId(1, pageable);
    }

    @Test
    @DisplayName("Debe obtener solo abonos de cliente")
    void debeObtenerSoloAbonosDeCliente() {
        // Given
        HistorialCredito abono = new HistorialCredito();
        abono.setId(1);
        abono.setCliente(clienteMock);
        abono.setTipoMovimiento("ABONO");

        Page<HistorialCredito> page = new PageImpl<>(Arrays.asList(abono));
        Pageable pageable = PageRequest.of(0, 20);

        when(historialCreditoRepository.findAbonosByClienteId(1, pageable)).thenReturn(page);

        // When
        Page<HistorialCreditoResponseDTO> response = creditoService.obtenerAbonosCliente(1, pageable);

        // Then
        assertThat(response).isNotEmpty();
        assertThat(response.getContent().get(0).getTipoMovimiento()).isEqualTo("ABONO");
        verify(historialCreditoRepository).findAbonosByClienteId(1, pageable);
    }

    @Test
    @DisplayName("Debe obtener historial por rango de fechas")
    void debeObtenerHistorialPorRangoFechas() {
        // Given
        HistorialCredito historial = new HistorialCredito();
        historial.setId(1);
        historial.setCliente(clienteMock);
        historial.setFechaMovimiento(LocalDateTime.now());

        LocalDateTime inicio = LocalDateTime.now().minusDays(30);
        LocalDateTime fin = LocalDateTime.now();

        when(historialCreditoRepository.findByClienteIdAndFechasBetween(1, inicio, fin))
                .thenReturn(Arrays.asList(historial));

        // When
        List<HistorialCreditoResponseDTO> response = creditoService.obtenerHistorialPorFechas(1, inicio, fin);

        // Then
        assertThat(response).hasSize(1);
        verify(historialCreditoRepository).findByClienteIdAndFechasBetween(1, inicio, fin);
    }
}
