package com.nexoohub.almacen.inventario.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MovimientoInventario - Tests de Entidad")
class MovimientoInventarioTest {

    @Test
    @DisplayName("Debe crear movimiento con ID")
    void testCrearMovimientoConId() {
        // Given & When
        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setId(1);

        // Then
        assertEquals(1, movimiento.getId());
    }

    @Test
    @DisplayName("Debe establecer y obtener skuInterno")
    void testSkuInterno() {
        // Given
        MovimientoInventario movimiento = new MovimientoInventario();

        // When
        movimiento.setSkuInterno("SKU001");

        // Then
        assertEquals("SKU001", movimiento.getSkuInterno());
    }

    @Test
    @DisplayName("Debe establecer y obtener sucursalId")
    void testSucursalId() {
        // Given
        MovimientoInventario movimiento = new MovimientoInventario();

        // When
        movimiento.setSucursalId(5);

        // Then
        assertEquals(5, movimiento.getSucursalId());
    }

    @Test
    @DisplayName("Debe establecer y obtener tipoMovimiento")
    void testTipoMovimiento() {
        // Given
        MovimientoInventario movimiento = new MovimientoInventario();

        // When
        movimiento.setTipoMovimiento("ENTRADA_COMPRA");

        // Then
        assertEquals("ENTRADA_COMPRA", movimiento.getTipoMovimiento());
    }

    @Test
    @DisplayName("Debe establecer y obtener cantidad")
    void testCantidad() {
        // Given
        MovimientoInventario movimiento = new MovimientoInventario();

        // When
        movimiento.setCantidad(50);

        // Then
        assertEquals(50, movimiento.getCantidad());
    }

    @Test
    @DisplayName("Debe establecer y obtener comentarios")
    void testComentarios() {
        // Given
        MovimientoInventario movimiento = new MovimientoInventario();

        // When
        movimiento.setComentarios("Traspaso urgente");

        // Then
        assertEquals("Traspaso urgente", movimiento.getComentarios());
    }

    @Test
    @DisplayName("Debe establecer y obtener rastreoId")
    void testRastreoId() {
        // Given
        MovimientoInventario movimiento = new MovimientoInventario();

        // When
        movimiento.setRastreoId("TR-ABC123");

        // Then
        assertEquals("TR-ABC123", movimiento.getRastreoId());
    }

    @Test
    @DisplayName("Debe establecer y obtener usuarioId")
    void testUsuarioId() {
        // Given
        MovimientoInventario movimiento = new MovimientoInventario();

        // When
        movimiento.setUsuarioId(10);

        // Then
        assertEquals(10, movimiento.getUsuarioId());
    }

    @Test
    @DisplayName("Debe crear movimiento completo")
    void testMovimientoCompleto() {
        // Given & When
        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setId(100);
        movimiento.setSkuInterno("SKU-TEST-001");
        movimiento.setSucursalId(3);
        movimiento.setTipoMovimiento("SALIDA_VENTA");
        movimiento.setCantidad(25);
        movimiento.setComentarios("Venta al cliente 123");
        movimiento.setRastreoId("V-2024-500");
        movimiento.setUsuarioId(7);

        // Then
        assertNotNull(movimiento);
        assertEquals(100, movimiento.getId());
        assertEquals("SKU-TEST-001", movimiento.getSkuInterno());
        assertEquals(3, movimiento.getSucursalId());
        assertEquals("SALIDA_VENTA", movimiento.getTipoMovimiento());
        assertEquals(25, movimiento.getCantidad());
        assertEquals("Venta al cliente 123", movimiento.getComentarios());
        assertEquals("V-2024-500", movimiento.getRastreoId());
        assertEquals(7, movimiento.getUsuarioId());
    }

    @Test
    @DisplayName("Debe manejar diferentes tipos de movimiento")
    void testDiferentesTiposMovimiento() {
        // Given
        MovimientoInventario m1 = new MovimientoInventario();
        MovimientoInventario m2 = new MovimientoInventario();
        MovimientoInventario m3 = new MovimientoInventario();
        MovimientoInventario m4 = new MovimientoInventario();

        // When
        m1.setTipoMovimiento("ENTRADA_COMPRA");
        m2.setTipoMovimiento("SALIDA_VENTA");
        m3.setTipoMovimiento("SALIDA_TRASPASO");
        m4.setTipoMovimiento("ENTRADA_TRASPASO");

        // Then
        assertEquals("ENTRADA_COMPRA", m1.getTipoMovimiento());
        assertEquals("SALIDA_VENTA", m2.getTipoMovimiento());
        assertEquals("SALIDA_TRASPASO", m3.getTipoMovimiento());
        assertEquals("ENTRADA_TRASPASO", m4.getTipoMovimiento());
    }

    @Test
    @DisplayName("Debe establecer fechaMovimiento con prePersist")
    void testPrePersist() {
        // Given
        MovimientoInventario movimiento = new MovimientoInventario();

        // When
        movimiento.prePersist();

        // Then
        assertNotNull(movimiento.getFechaMovimiento());
        assertTrue(movimiento.getFechaMovimiento().isBefore(LocalDateTime.now().plusSeconds(1)));
        assertTrue(movimiento.getFechaMovimiento().isAfter(LocalDateTime.now().minusSeconds(1)));
    }

    @Test
    @DisplayName("Debe obtener fechaMovimiento")
    void testGetFechaMovimiento() {
        // Given
        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.prePersist();

        // When
        LocalDateTime fecha = movimiento.getFechaMovimiento();

        // Then
        assertNotNull(fecha);
        assertInstanceOf(LocalDateTime.class, fecha);
    }

    @Test
    @DisplayName("Debe manejar cantidades negativas para salidas")
    void testCantidadesNegativas() {
        // Given
        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setTipoMovimiento("SALIDA_VENTA");

        // When
        movimiento.setCantidad(-10);

        // Then
        assertEquals(-10, movimiento.getCantidad());
        assertTrue(movimiento.getCantidad() < 0);
    }

    @Test
    @DisplayName("Debe permitir comentarios null")
    void testComentariosNull() {
        // Given & When
        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setComentarios(null);

        // Then
        assertNull(movimiento.getComentarios());
    }

    @Test
    @DisplayName("Debe permitir rastreoId null")
    void testRastreoIdNull() {
        // Given & When
        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setRastreoId(null);

        // Then
        assertNull(movimiento.getRastreoId());
    }

    @Test
    @DisplayName("Debe crear instancia sin valores por defecto")
    void testInstanciaPorDefecto() {
        // Given & When
        MovimientoInventario movimiento = new MovimientoInventario();

        // Then
        assertNotNull(movimiento);
        assertNull(movimiento.getId());
        assertNull(movimiento.getSkuInterno());
        assertNull(movimiento.getSucursalId());
        assertNull(movimiento.getTipoMovimiento());
        assertNull(movimiento.getCantidad());
        assertNull(movimiento.getComentarios());
        assertNull(movimiento.getRastreoId());
        assertNull(movimiento.getFechaMovimiento());
        assertNull(movimiento.getUsuarioId());
    }
}
