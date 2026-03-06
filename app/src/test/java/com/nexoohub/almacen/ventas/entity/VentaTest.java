package com.nexoohub.almacen.ventas.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Venta - Tests de Entidad")
class VentaTest {

    @Test
    @DisplayName("Debe crear venta con ID")
    void testCrearVentaConId() {
        // Given & When
        Venta venta = new Venta();
        venta.setId(1);

        // Then
        assertEquals(1, venta.getId());
    }

    @Test
    @DisplayName("Debe establecer y obtener clienteId")
    void testClienteId() {
        // Given
        Venta venta = new Venta();

        // When
        venta.setClienteId(100);

        // Then
        assertEquals(100, venta.getClienteId());
    }

    @Test
    @DisplayName("Debe establecer y obtener sucursalId")
    void testSucursalId() {
        // Given
        Venta venta = new Venta();

        // When
        venta.setSucursalId(5);

        // Then
        assertEquals(5, venta.getSucursalId());
    }

    @Test
    @DisplayName("Debe establecer y obtener vendedorId")
    void testVendedorId() {
        // Given
        Venta venta = new Venta();

        // When
        venta.setVendedorId(25);

        // Then
        assertEquals(25, venta.getVendedorId());
    }

    @Test
    @DisplayName("Debe establecer y obtener metodoPago")
    void testMetodoPago() {
        // Given
        Venta venta = new Venta();

        // When
        venta.setMetodoPago("EFECTIVO");

        // Then
        assertEquals("EFECTIVO", venta.getMetodoPago());
    }

    @Test
    @DisplayName("Debe establecer y obtener total")
    void testTotal() {
        // Given
        Venta venta = new Venta();

        // When
        venta.setTotal(new BigDecimal("1500.50"));

        // Then
        assertEquals(new BigDecimal("1500.50"), venta.getTotal());
    }

    @Test
    @DisplayName("Debe crear venta completa")
    void testVentaCompleta() {
        // Given & When
        Venta venta = new Venta();
        venta.setId(10);
        venta.setClienteId(200);
        venta.setSucursalId(3);
        venta.setVendedorId(15);
        venta.setMetodoPago("TARJETA");
        venta.setTotal(new BigDecimal("5000.00"));

        // Then
        assertNotNull(venta);
        assertEquals(10, venta.getId());
        assertEquals(200, venta.getClienteId());
        assertEquals(3, venta.getSucursalId());
        assertEquals(15, venta.getVendedorId());
        assertEquals("TARJETA", venta.getMetodoPago());
        assertEquals(new BigDecimal("5000.00"), venta.getTotal());
    }

    @Test
    @DisplayName("Debe manejar diferentes métodos de pago")
    void testDiferentesMetodosPago() {
        // Given
        Venta venta1 = new Venta();
        Venta venta2 = new Venta();
        Venta venta3 = new Venta();

        // When
        venta1.setMetodoPago("EFECTIVO");
        venta2.setMetodoPago("TARJETA");
        venta3.setMetodoPago("TRANSFERENCIA");

        // Then
        assertEquals("EFECTIVO", venta1.getMetodoPago());
        assertEquals("TARJETA", venta2.getMetodoPago());
        assertEquals("TRANSFERENCIA", venta3.getMetodoPago());
    }

    @Test
    @DisplayName("Debe manejar totales con decimales")
    void testTotalesDecimales() {
        // Given
        Venta venta = new Venta();

        // When
        venta.setTotal(new BigDecimal("1234.56"));

        // Then
        assertEquals(new BigDecimal("1234.56"), venta.getTotal());
        assertTrue(venta.getTotal().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    @DisplayName("Debe establecer fechaVenta con prePersist")
    void testPrePersist() {
        // Given
        Venta venta = new Venta();

        // When
        venta.prePersist();

        // Then
        assertNotNull(venta.getFechaVenta());
        assertTrue(venta.getFechaVenta().isBefore(LocalDateTime.now().plusSeconds(1)));
        assertTrue(venta.getFechaVenta().isAfter(LocalDateTime.now().minusSeconds(1)));
    }

    @Test
    @DisplayName("Debe obtener fechaVenta")
    void testGetFechaVenta() {
        // Given
        Venta venta = new Venta();
        venta.prePersist();

        // When
        LocalDateTime fecha = venta.getFechaVenta();

        // Then
        assertNotNull(fecha);
        assertInstanceOf(LocalDateTime.class, fecha);
    }

    @Test
    @DisplayName("Debe permitir valores null en campos opcionales")
    void testValoresNull() {
        // Given & When
        Venta venta = new Venta();
        venta.setClienteId(null);
        venta.setSucursalId(null);
        venta.setVendedorId(null);
        venta.setMetodoPago(null);
        venta.setTotal(null);

        // Then
        assertNotNull(venta);
        assertNull(venta.getClienteId());
        assertNull(venta.getSucursalId());
        assertNull(venta.getVendedorId());
        assertNull(venta.getMetodoPago());
        assertNull(venta.getTotal());
    }

    @Test
    @DisplayName("Debe crear instancia sin valores por defecto")
    void testInstanciaPorDefecto() {
        // Given & When
        Venta venta = new Venta();

        // Then
        assertNotNull(venta);
        assertNull(venta.getId());
        assertNull(venta.getClienteId());
        assertNull(venta.getSucursalId());
        assertNull(venta.getVendedorId());
        assertNull(venta.getMetodoPago());
        assertNull(venta.getTotal());
        assertNull(venta.getFechaVenta());
    }
}
