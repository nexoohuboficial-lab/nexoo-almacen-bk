package com.nexoohub.almacen.ventas.service;

import com.nexoohub.almacen.catalogo.entity.Cliente;
import com.nexoohub.almacen.catalogo.repository.ClienteRepository;
import com.nexoohub.almacen.common.exception.InvalidOperationException;
import com.nexoohub.almacen.common.exception.ResourceNotFoundException;
import com.nexoohub.almacen.inventario.entity.InventarioSucursal;
import com.nexoohub.almacen.inventario.entity.ProductoMaestro;
import com.nexoohub.almacen.inventario.repository.InventarioSucursalRepository;
import com.nexoohub.almacen.inventario.repository.ProductoMaestroRepository;
import com.nexoohub.almacen.sucursal.entity.Sucursal;
import com.nexoohub.almacen.sucursal.repository.SucursalRepository;
import com.nexoohub.almacen.ventas.dto.ReservaRequestDTO;
import com.nexoohub.almacen.ventas.dto.ReservaResponseDTO;
import com.nexoohub.almacen.ventas.entity.Reserva;
import com.nexoohub.almacen.ventas.repository.ReservaRepository;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para ReservaService.
 * 
 * @author NexooHub Development Team
 * @since 1.1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ReservaService - Tests de Gestión de Reservas")
class ReservaServiceTest {

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ProductoMaestroRepository productoRepository;

    @Mock
    private SucursalRepository sucursalRepository;

    @Mock
    private InventarioSucursalRepository inventarioRepository;

    @InjectMocks
    private ReservaService reservaService;

    private Cliente clienteMock;
    private ProductoMaestro productoMock;
    private Sucursal sucursalMock;
    private ReservaRequestDTO requestMock;

    @BeforeEach
    void setUp() {
        clienteMock = new Cliente();
        clienteMock.setId(1);
        clienteMock.setNombre("Juan Pérez");

        productoMock = new ProductoMaestro();
        productoMock.setSkuInterno("TEST-001");
        productoMock.setNombreComercial("Producto Test");

        sucursalMock = new Sucursal();
        sucursalMock.setId(1);
        sucursalMock.setNombre("Sucursal Centro");

        requestMock = new ReservaRequestDTO();
        requestMock.setClienteId(1);
        requestMock.setSkuInterno("TEST-001");
        requestMock.setSucursalId(1);
        requestMock.setCantidad(2);
        requestMock.setComentarios("Test");
    }

    @Test
    @DisplayName("Debe crear reserva exitosamente cuando no hay stock")
    void debeCrearReservaExitosamenteCuandoNoHayStock() {
        // Given
        when(clienteRepository.findById(1)).thenReturn(Optional.of(clienteMock));
        when(productoRepository.findById("TEST-001")).thenReturn(Optional.of(productoMock));
        when(sucursalRepository.findById(1)).thenReturn(Optional.of(sucursalMock));
        when(reservaRepository.contarReservasActivasPorCliente(1)).thenReturn(2L);
        when(inventarioRepository.findByIdSkuInternoAndIdSucursalId("TEST-001", 1))
                .thenReturn(Optional.empty());

        Reserva reservaGuardada = new Reserva();
        reservaGuardada.setId(1);
        reservaGuardada.setCliente(clienteMock);
        reservaGuardada.setProducto(productoMock);
        reservaGuardada.setSucursal(sucursalMock);
        reservaGuardada.setCantidad(2);
        reservaGuardada.setEstado(Reserva.EstadoReserva.PENDIENTE);
        reservaGuardada.setFechaCreacion(LocalDateTime.now());
        reservaGuardada.setFechaVencimiento(LocalDateTime.now().plusDays(7));
        reservaGuardada.setUsuarioRegistro("admin");

        when(reservaRepository.save(any(Reserva.class))).thenReturn(reservaGuardada);

        // When
        ReservaResponseDTO resultado = reservaService.crearReserva(requestMock, "admin");

        // Then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1);
        assertThat(resultado.getEstado()).isEqualTo("PENDIENTE");
        assertThat(resultado.getCantidad()).isEqualTo(2);
        verify(reservaRepository, times(1)).save(any(Reserva.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando cliente no existe")
    void debeLanzarExcepcionCuandoClienteNoExiste() {
        // Given
        when(clienteRepository.findById(1)).thenReturn(Optional.empty());

        // When / Then
        assertThatThrownBy(() -> reservaService.crearReserva(requestMock, "admin"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Cliente no encontrado");
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando producto tiene stock disponible")
    void debeLanzarExcepcionCuandoProductoTieneStock() {
        // Given
        when(clienteRepository.findById(1)).thenReturn(Optional.of(clienteMock));
        when(productoRepository.findById("TEST-001")).thenReturn(Optional.of(productoMock));
        when(sucursalRepository.findById(1)).thenReturn(Optional.of(sucursalMock));
        when(reservaRepository.contarReservasActivasPorCliente(1)).thenReturn(2L);

        InventarioSucursal inventario = new InventarioSucursal();
        inventario.setStockActual(5);
        when(inventarioRepository.findByIdSkuInternoAndIdSucursalId("TEST-001", 1))
                .thenReturn(Optional.of(inventario));

        // When / Then
        assertThatThrownBy(() -> reservaService.crearReserva(requestMock, "admin"))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessageContaining("tiene stock disponible");
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando cliente excede límite de reservas")
    void debeLanzarExcepcionCuandoClienteExcedeLimiteReservas() {
        // Given
        when(clienteRepository.findById(1)).thenReturn(Optional.of(clienteMock));
        when(productoRepository.findById("TEST-001")).thenReturn(Optional.of(productoMock));
        when(sucursalRepository.findById(1)).thenReturn(Optional.of(sucursalMock));
        when(reservaRepository.contarReservasActivasPorCliente(1)).thenReturn(10L);

        // When / Then
        assertThatThrownBy(() -> reservaService.crearReserva(requestMock, "admin"))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessageContaining("ya tiene 10 reservas activas");
    }

    @Test
    @DisplayName("Debe notificar reservas cuando llega mercancía")
    void debeNotificarReservasCuandoLlegaMercancia() {
        // Given
        Reserva reserva1 = crearReservaMock(1, 2, Reserva.EstadoReserva.PENDIENTE);
        Reserva reserva2 = crearReservaMock(2, 3, Reserva.EstadoReserva.PENDIENTE);
        List<Reserva> reservasPendientes = Arrays.asList(reserva1, reserva2);

        when(reservaRepository.findReservasPendientesByProductoYSucursal("TEST-001", 1))
                .thenReturn(reservasPendientes);

        InventarioSucursal inventario = new InventarioSucursal();
        inventario.setStockActual(10);
        when(inventarioRepository.findByIdSkuInternoAndIdSucursalId("TEST-001", 1))
                .thenReturn(Optional.of(inventario));

        // When
        reservaService.notificarReservasDisponibles("TEST-001", 1);

        // Then
        verify(reservaRepository, times(2)).save(any(Reserva.class));
        assertThat(reserva1.getEstado()).isEqualTo(Reserva.EstadoReserva.NOTIFICADA);
        assertThat(reserva2.getEstado()).isEqualTo(Reserva.EstadoReserva.NOTIFICADA);
    }

    @Test
    @DisplayName("No debe notificar reservas si no hay stock suficiente")
    void noDebeNotificarSiNoHayStockSuficiente() {
        // Given
        Reserva reserva = crearReservaMock(1, 10, Reserva.EstadoReserva.PENDIENTE);
        when(reservaRepository.findReservasPendientesByProductoYSucursal("TEST-001", 1))
                .thenReturn(Arrays.asList(reserva));

        InventarioSucursal inventario = new InventarioSucursal();
        inventario.setStockActual(5); // Stock insuficiente
        when(inventarioRepository.findByIdSkuInternoAndIdSucursalId("TEST-001", 1))
                .thenReturn(Optional.of(inventario));

        // When
        reservaService.notificarReservasDisponibles("TEST-001", 1);

        // Then
        verify(reservaRepository, never()).save(any(Reserva.class));
        assertThat(reserva.getEstado()).isEqualTo(Reserva.EstadoReserva.PENDIENTE);
    }

    @Test
    @DisplayName("Debe procesar reservas vencidas correctamente")
    void debeProcesarReservasVencidasCorrectamente() {
        // Given
        Reserva reserva1 = crearReservaMock(1, 2, Reserva.EstadoReserva.PENDIENTE);
        Reserva reserva2 = crearReservaMock(2, 3, Reserva.EstadoReserva.PENDIENTE);
        when(reservaRepository.findReservasVencidas(any(LocalDateTime.class)))
                .thenReturn(Arrays.asList(reserva1, reserva2));

        // When
        int procesadas = reservaService.procesarReservasVencidas();

        // Then
        assertThat(procesadas).isEqualTo(2);
        assertThat(reserva1.getEstado()).isEqualTo(Reserva.EstadoReserva.VENCIDA);
        assertThat(reserva2.getEstado()).isEqualTo(Reserva.EstadoReserva.VENCIDA);
        verify(reservaRepository, times(2)).save(any(Reserva.class));
    }

    @Test
    @DisplayName("Debe cancelar reserva exitosamente")
    void debeCancelarReservaExitosamente() {
        // Given
        Reserva reserva = crearReservaMock(1, 2, Reserva.EstadoReserva.PENDIENTE);
        when(reservaRepository.findById(1)).thenReturn(Optional.of(reserva));
        when(reservaRepository.save(any(Reserva.class))).thenReturn(reserva);

        // When
        ReservaResponseDTO resultado = reservaService.cancelarReserva(1, "Cliente ya no lo necesita");

        // Then
        assertThat(resultado.getEstado()).isEqualTo("CANCELADA");
        verify(reservaRepository, times(1)).save(reserva);
    }

    @Test
    @DisplayName("No debe cancelar reserva ya completada")
    void noDebeCancelarReservaYaCompletada() {
        // Given
        Reserva reserva = crearReservaMock(1, 2, Reserva.EstadoReserva.COMPLETADA);
        when(reservaRepository.findById(1)).thenReturn(Optional.of(reserva));

        // When / Then
        assertThatThrownBy(() -> reservaService.cancelarReserva(1, "Test"))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessageContaining("ya completada");
    }

    @Test
    @DisplayName("Debe completar reserva exitosamente")
    void debeCompletarReservaExitosamente() {
        // Given
        Reserva reserva = crearReservaMock(1, 2, Reserva.EstadoReserva.NOTIFICADA);
        when(reservaRepository.findById(1)).thenReturn(Optional.of(reserva));
        when(reservaRepository.save(any(Reserva.class))).thenReturn(reserva);

        // When
        ReservaResponseDTO resultado = reservaService.completarReserva(1, 42);

        // Then
        assertThat(resultado.getEstado()).isEqualTo("COMPLETADA");
        assertThat(resultado.getVentaId()).isEqualTo(42);
        verify(reservaRepository, times(1)).save(reserva);
    }

    @Test
    @DisplayName("Solo debe completar reservas en estado NOTIFICADA")
    void soloDebeCompletarReservasNotificadas() {
        // Given
        Reserva reserva = crearReservaMock(1, 2, Reserva.EstadoReserva.PENDIENTE);
        when(reservaRepository.findById(1)).thenReturn(Optional.of(reserva));

        // When / Then
        assertThatThrownBy(() -> reservaService.completarReserva(1, 42))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessageContaining("Solo se pueden completar reservas en estado NOTIFICADA");
    }

    @Test
    @DisplayName("Debe listar reservas por cliente")
    void debeListarReservasPorCliente() {
        // Given
        Reserva reserva = crearReservaMock(1, 2, Reserva.EstadoReserva.PENDIENTE);
        Page<Reserva> page = new PageImpl<>(Arrays.asList(reserva));
        Pageable pageable = PageRequest.of(0, 20);

        when(reservaRepository.findByClienteIdOrderByFechaCreacionDesc(1, pageable))
                .thenReturn(page);

        // When
        Page<ReservaResponseDTO> resultado = reservaService.listarReservasPorCliente(1, pageable);

        // Then
        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).getClienteId()).isEqualTo(1);
    }

    @Test
    @DisplayName("Debe listar reservas por estado")
    void debeListarReservasPorEstado() {
        // Given
        Reserva reserva = crearReservaMock(1, 2, Reserva.EstadoReserva.PENDIENTE);
        Page<Reserva> page = new PageImpl<>(Arrays.asList(reserva));
        Pageable pageable = PageRequest.of(0, 20);

        when(reservaRepository.findByEstadoOrderByFechaCreacionDesc(
                Reserva.EstadoReserva.PENDIENTE, pageable))
                .thenReturn(page);

        // When
        Page<ReservaResponseDTO> resultado = reservaService.listarReservasPorEstado("PENDIENTE", pageable);

        // Then
        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).getEstado()).isEqualTo("PENDIENTE");
    }

    @Test
    @DisplayName("Debe lanzar excepción con estado inválido")
    void debeLanzarExcepcionConEstadoInvalido() {
        // Given
        Pageable pageable = PageRequest.of(0, 20);

        // When / Then
        assertThatThrownBy(() -> reservaService.listarReservasPorEstado("INVALIDO", pageable))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessageContaining("Estado inválido");
    }

    // ========== Métodos auxiliares ==========

    private Reserva crearReservaMock(Integer id, Integer cantidad, Reserva.EstadoReserva estado) {
        Reserva reserva = new Reserva();
        reserva.setId(id);
        reserva.setCliente(clienteMock);
        reserva.setProducto(productoMock);
        reserva.setSucursal(sucursalMock);
        reserva.setCantidad(cantidad);
        reserva.setEstado(estado);
        reserva.setFechaCreacion(LocalDateTime.now());
        reserva.setFechaVencimiento(LocalDateTime.now().plusDays(7));
        reserva.setUsuarioRegistro("admin");
        return reserva;
    }
}
