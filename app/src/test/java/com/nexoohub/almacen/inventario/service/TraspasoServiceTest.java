package com.nexoohub.almacen.inventario.service;

import com.nexoohub.almacen.common.entity.Usuario;
import com.nexoohub.almacen.common.repository.UsuarioRepository;
import com.nexoohub.almacen.inventario.dto.TraspasoRequestDTO;
import com.nexoohub.almacen.inventario.entity.InventarioSucursal;
import com.nexoohub.almacen.inventario.entity.InventarioSucursalId;
import com.nexoohub.almacen.inventario.entity.MovimientoInventario;
import com.nexoohub.almacen.inventario.repository.InventarioSucursalRepository;
import com.nexoohub.almacen.inventario.repository.MovimientoInventarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TraspasoService - Tests de Traspasos entre Sucursales")
class TraspasoServiceTest {

    @Mock
    private InventarioSucursalRepository inventarioRepository;

    @Mock
    private MovimientoInventarioRepository movimientoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private TraspasoService traspasoService;

    private Usuario usuario;
    private InventarioSucursal inventarioOrigen;
    private InventarioSucursal inventarioDestino;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("encargado1");

        inventarioOrigen = new InventarioSucursal();
        inventarioOrigen.setId(new InventarioSucursalId(1, "SKU001"));
        inventarioOrigen.setStockActual(20);
        inventarioOrigen.setCostoPromedioPonderado(new BigDecimal("100.00"));

        inventarioDestino = new InventarioSucursal();
        inventarioDestino.setId(new InventarioSucursalId(2, "SKU001"));
        inventarioDestino.setStockActual(10);
        inventarioDestino.setCostoPromedioPonderado(new BigDecimal("90.00"));
    }

    @Test
    @DisplayName("Debe ejecutar traspaso exitosamente entre sucursales")
    void testEjecutarTraspasoExitoso() {
        // Given
        TraspasoRequestDTO request = crearRequestTraspaso(1, 2, 5);

        when(usuarioRepository.findByUsername("encargado1")).thenReturn(Optional.of(usuario));
        when(inventarioRepository.findById(new InventarioSucursalId(1, "SKU001")))
                .thenReturn(Optional.of(inventarioOrigen));
        when(inventarioRepository.findById(new InventarioSucursalId(2, "SKU001")))
                .thenReturn(Optional.of(inventarioDestino));
        when(movimientoRepository.save(any(MovimientoInventario.class)))
                .thenReturn(new MovimientoInventario());

        // When
        String rastreoId = traspasoService.ejecutarTraspaso(request, "encargado1");

        // Then
        assertNotNull(rastreoId, "El ID de rastreo no debe ser null");
        assertTrue(rastreoId.startsWith("TR-"), "El ID debe comenzar con TR-");
        verify(inventarioRepository, times(2)).save(any(InventarioSucursal.class));
        verify(movimientoRepository, times(2)).save(any(MovimientoInventario.class));
    }

    @Test
    @DisplayName("Debe decrementar stock en sucursal origen")
    void testDecrementarStockOrigen() {
        // Given
        TraspasoRequestDTO request = crearRequestTraspaso(1, 2, 5);

        when(usuarioRepository.findByUsername("encargado1")).thenReturn(Optional.of(usuario));
        when(inventarioRepository.findById(new InventarioSucursalId(1, "SKU001")))
                .thenReturn(Optional.of(inventarioOrigen));
        when(inventarioRepository.findById(new InventarioSucursalId(2, "SKU001")))
                .thenReturn(Optional.of(inventarioDestino));
        when(movimientoRepository.save(any(MovimientoInventario.class)))
                .thenReturn(new MovimientoInventario());

        // When
        traspasoService.ejecutarTraspaso(request, "encargado1");

        // Then
        ArgumentCaptor<InventarioSucursal> inventarioCaptor = ArgumentCaptor.forClass(InventarioSucursal.class);
        verify(inventarioRepository, times(2)).save(inventarioCaptor.capture());
        
        InventarioSucursal origenActualizado = inventarioCaptor.getAllValues().stream()
                .filter(inv -> inv.getId().getSucursalId().equals(1))
                .findFirst()
                .orElse(null);
        
        assertNotNull(origenActualizado);
        assertEquals(15, origenActualizado.getStockActual(), "Stock origen debe ser 20 - 5 = 15");
    }

    @Test
    @DisplayName("Debe incrementar stock en sucursal destino")
    void testIncrementarStockDestino() {
        // Given
        TraspasoRequestDTO request = crearRequestTraspaso(1, 2, 5);

        when(usuarioRepository.findByUsername("encargado1")).thenReturn(Optional.of(usuario));
        when(inventarioRepository.findById(new InventarioSucursalId(1, "SKU001")))
                .thenReturn(Optional.of(inventarioOrigen));
        when(inventarioRepository.findById(new InventarioSucursalId(2, "SKU001")))
                .thenReturn(Optional.of(inventarioDestino));
        when(movimientoRepository.save(any(MovimientoInventario.class)))
                .thenReturn(new MovimientoInventario());

        // When
        traspasoService.ejecutarTraspaso(request, "encargado1");

        // Then
        ArgumentCaptor<InventarioSucursal> inventarioCaptor = ArgumentCaptor.forClass(InventarioSucursal.class);
        verify(inventarioRepository, times(2)).save(inventarioCaptor.capture());
        
        InventarioSucursal destinoActualizado = inventarioCaptor.getAllValues().stream()
                .filter(inv -> inv.getId().getSucursalId().equals(2))
                .findFirst()
                .orElse(null);
        
        assertNotNull(destinoActualizado);
        assertEquals(15, destinoActualizado.getStockActual(), "Stock destino debe ser 10 + 5 = 15");
    }

    @Test
    @DisplayName("Debe calcular CPP correctamente en destino")
    void testCalcularCppDestino() {
        // Given
        TraspasoRequestDTO request = crearRequestTraspaso(1, 2, 5);

        when(usuarioRepository.findByUsername("encargado1")).thenReturn(Optional.of(usuario));
        when(inventarioRepository.findById(new InventarioSucursalId(1, "SKU001")))
                .thenReturn(Optional.of(inventarioOrigen));
        when(inventarioRepository.findById(new InventarioSucursalId(2, "SKU001")))
                .thenReturn(Optional.of(inventarioDestino));
        when(movimientoRepository.save(any(MovimientoInventario.class)))
                .thenReturn(new MovimientoInventario());

        // When
        traspasoService.ejecutarTraspaso(request, "encargado1");

        // Then
        ArgumentCaptor<InventarioSucursal> inventarioCaptor = ArgumentCaptor.forClass(InventarioSucursal.class);
        verify(inventarioRepository, times(2)).save(inventarioCaptor.capture());
        
        InventarioSucursal destinoActualizado = inventarioCaptor.getAllValues().stream()
                .filter(inv -> inv.getId().getSucursalId().equals(2))
                .findFirst()
                .orElse(null);
        
        assertNotNull(destinoActualizado);
        assertNotNull(destinoActualizado.getCostoPromedioPonderado());
        assertTrue(destinoActualizado.getCostoPromedioPonderado().compareTo(BigDecimal.ZERO) > 0,
                "El CPP destino debe ser mayor a cero");
    }

    @Test
    @DisplayName("Debe crear inventario en destino si no existe")
    void testCrearInventarioDestino() {
        // Given
        TraspasoRequestDTO request = crearRequestTraspaso(1, 2, 5);

        when(usuarioRepository.findByUsername("encargado1")).thenReturn(Optional.of(usuario));
        when(inventarioRepository.findById(new InventarioSucursalId(1, "SKU001")))
                .thenReturn(Optional.of(inventarioOrigen));
        when(inventarioRepository.findById(new InventarioSucursalId(2, "SKU001")))
                .thenReturn(Optional.empty()); // No existe en destino
        when(movimientoRepository.save(any(MovimientoInventario.class)))
                .thenReturn(new MovimientoInventario());

        // When
        traspasoService.ejecutarTraspaso(request, "encargado1");

        // Then
        ArgumentCaptor<InventarioSucursal> inventarioCaptor = ArgumentCaptor.forClass(InventarioSucursal.class);
        verify(inventarioRepository, times(2)).save(inventarioCaptor.capture());
        
        InventarioSucursal nuevoDestino = inventarioCaptor.getAllValues().stream()
                .filter(inv -> inv.getId().getSucursalId().equals(2))
                .findFirst()
                .orElse(null);
        
        assertNotNull(nuevoDestino);
        assertEquals(5, nuevoDestino.getStockActual(), "El nuevo stock debe ser 5");
    }

    @Test
    @DisplayName("Debe registrar movimientos de auditoría (SALIDA y ENTRADA)")
    void testRegistrarMovimientosAuditoria() {
        // Given
        TraspasoRequestDTO request = crearRequestTraspaso(1, 2, 5);

        when(usuarioRepository.findByUsername("encargado1")).thenReturn(Optional.of(usuario));
        when(inventarioRepository.findById(new InventarioSucursalId(1, "SKU001")))
                .thenReturn(Optional.of(inventarioOrigen));
        when(inventarioRepository.findById(new InventarioSucursalId(2, "SKU001")))
                .thenReturn(Optional.of(inventarioDestino));
        when(movimientoRepository.save(any(MovimientoInventario.class)))
                .thenReturn(new MovimientoInventario());

        // When
        traspasoService.ejecutarTraspaso(request, "encargado1");

        // Then
        ArgumentCaptor<MovimientoInventario> movimientoCaptor = ArgumentCaptor.forClass(MovimientoInventario.class);
        verify(movimientoRepository, times(2)).save(movimientoCaptor.capture());
        
        List<MovimientoInventario> movimientos = movimientoCaptor.getAllValues();
        
        MovimientoInventario salida = movimientos.stream()
                .filter(m -> "SALIDA_TRASPASO".equals(m.getTipoMovimiento()))
                .findFirst()
                .orElse(null);
        
        MovimientoInventario entrada = movimientos.stream()
                .filter(m -> "ENTRADA_TRASPASO".equals(m.getTipoMovimiento()))
                .findFirst()
                .orElse(null);
        
        assertNotNull(salida, "Debe existir movimiento de SALIDA");
        assertNotNull(entrada, "Debe existir movimiento de ENTRADA");
        assertEquals(salida.getRastreoId(), entrada.getRastreoId(), 
                "Ambos movimientos deben tener el mismo ID de rastreo");
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando origen y destino son iguales")
    void testErrorOrigenDestinoIguales() {
        // Given
        TraspasoRequestDTO request = crearRequestTraspaso(1, 1, 5);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> traspasoService.ejecutarTraspaso(request, "encargado1"));
        
        assertTrue(exception.getMessage().contains("no pueden ser la misma"));
        verify(inventarioRepository, never()).save(any());
        verify(movimientoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el usuario no existe")
    void testErrorUsuarioNoEncontrado() {
        // Given
        TraspasoRequestDTO request = crearRequestTraspaso(1, 2, 5);
        when(usuarioRepository.findByUsername("encargado1")).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> traspasoService.ejecutarTraspaso(request, "encargado1"));
        
        assertEquals("Usuario no encontrado", exception.getMessage());
        verify(inventarioRepository, never()).save(any());
        verify(movimientoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando producto no existe en origen")
    void testErrorProductoNoExisteOrigen() {
        // Given
        TraspasoRequestDTO request = crearRequestTraspaso(1, 2, 5);

        when(usuarioRepository.findByUsername("encargado1")).thenReturn(Optional.of(usuario));
        when(inventarioRepository.findById(new InventarioSucursalId(1, "SKU001")))
                .thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> traspasoService.ejecutarTraspaso(request, "encargado1"));
        
        assertTrue(exception.getMessage().contains("no existe en la sucursal de origen"));
        verify(movimientoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando hay stock insuficiente en origen")
    void testErrorStockInsuficienteOrigen() {
        // Given
        TraspasoRequestDTO request = crearRequestTraspaso(1, 2, 50); // Pedir más de lo disponible

        when(usuarioRepository.findByUsername("encargado1")).thenReturn(Optional.of(usuario));
        when(inventarioRepository.findById(new InventarioSucursalId(1, "SKU001")))
                .thenReturn(Optional.of(inventarioOrigen));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> traspasoService.ejecutarTraspaso(request, "encargado1"));
        
        assertTrue(exception.getMessage().contains("Stock insuficiente en origen"));
        verify(inventarioRepository, never()).save(any());
        verify(movimientoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe manejar traspaso con múltiples items")
    void testTraspasoMultiplesItems() {
        // Given
        TraspasoRequestDTO request = crearRequestTraspasoMultiple();

        InventarioSucursal inventarioOrigen2 = new InventarioSucursal();
        inventarioOrigen2.setId(new InventarioSucursalId(1, "SKU002"));
        inventarioOrigen2.setStockActual(15);
        inventarioOrigen2.setCostoPromedioPonderado(new BigDecimal("80.00"));

        InventarioSucursal inventarioDestino2 = new InventarioSucursal();
        inventarioDestino2.setId(new InventarioSucursalId(2, "SKU002"));
        inventarioDestino2.setStockActual(5);
        inventarioDestino2.setCostoPromedioPonderado(new BigDecimal("75.00"));

        when(usuarioRepository.findByUsername("encargado1")).thenReturn(Optional.of(usuario));
        when(inventarioRepository.findById(new InventarioSucursalId(1, "SKU001")))
                .thenReturn(Optional.of(inventarioOrigen));
        when(inventarioRepository.findById(new InventarioSucursalId(2, "SKU001")))
                .thenReturn(Optional.of(inventarioDestino));
        when(inventarioRepository.findById(new InventarioSucursalId(1, "SKU002")))
                .thenReturn(Optional.of(inventarioOrigen2));
        when(inventarioRepository.findById(new InventarioSucursalId(2, "SKU002")))
                .thenReturn(Optional.of(inventarioDestino2));
        when(movimientoRepository.save(any(MovimientoInventario.class)))
                .thenReturn(new MovimientoInventario());

        // When
        String rastreoId = traspasoService.ejecutarTraspaso(request, "encargado1");

        // Then
        assertNotNull(rastreoId);
        verify(inventarioRepository, times(4)).save(any(InventarioSucursal.class));
        verify(movimientoRepository, times(4)).save(any(MovimientoInventario.class));
    }

    @Test
    @DisplayName("Debe incluir comentarios en movimientos de auditoría")
    void testIncluirComentariosEnMovimientos() {
        // Given
        TraspasoRequestDTO request = crearRequestTraspaso(1, 2, 5);
        request.setComentarios("Reabastecimiento urgente");

        when(usuarioRepository.findByUsername("encargado1")).thenReturn(Optional.of(usuario));
        when(inventarioRepository.findById(new InventarioSucursalId(1, "SKU001")))
                .thenReturn(Optional.of(inventarioOrigen));
        when(inventarioRepository.findById(new InventarioSucursalId(2, "SKU001")))
                .thenReturn(Optional.of(inventarioDestino));
        when(movimientoRepository.save(any(MovimientoInventario.class)))
                .thenReturn(new MovimientoInventario());

        // When
        traspasoService.ejecutarTraspaso(request, "encargado1");

        // Then
        ArgumentCaptor<MovimientoInventario> movimientoCaptor = ArgumentCaptor.forClass(MovimientoInventario.class);
        verify(movimientoRepository, times(2)).save(movimientoCaptor.capture());
        
        List<MovimientoInventario> movimientos = movimientoCaptor.getAllValues();
        movimientos.forEach(mov -> 
                assertEquals("Reabastecimiento urgente", mov.getComentarios())
        );
    }

    // ========== MÉTODOS AUXILIARES ==========

    private TraspasoRequestDTO crearRequestTraspaso(Integer origen, Integer destino, Integer cantidad) {
        TraspasoRequestDTO request = new TraspasoRequestDTO();
        request.setSucursalOrigenId(origen);
        request.setSucursalDestinoId(destino);
        request.setComentarios("Traspaso de prueba");

        TraspasoRequestDTO.ItemTraspasoDTO item = new TraspasoRequestDTO.ItemTraspasoDTO();
        item.setSkuInterno("SKU001");
        item.setCantidad(cantidad);

        request.setItems(List.of(item));
        return request;
    }

    private TraspasoRequestDTO crearRequestTraspasoMultiple() {
        TraspasoRequestDTO request = new TraspasoRequestDTO();
        request.setSucursalOrigenId(1);
        request.setSucursalDestinoId(2);
        request.setComentarios("Traspaso múltiple");

        TraspasoRequestDTO.ItemTraspasoDTO item1 = new TraspasoRequestDTO.ItemTraspasoDTO();
        item1.setSkuInterno("SKU001");
        item1.setCantidad(5);

        TraspasoRequestDTO.ItemTraspasoDTO item2 = new TraspasoRequestDTO.ItemTraspasoDTO();
        item2.setSkuInterno("SKU002");
        item2.setCantidad(3);

        request.setItems(List.of(item1, item2));
        return request;
    }
}
