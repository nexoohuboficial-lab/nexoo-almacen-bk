package com.nexoohub.almacen.compras.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Compra - Tests de Entidad")
class CompraTest {

    @Test
    @DisplayName("Debe crear compra con ID")
    void testCrearCompraConId() {
        // Given & When
        Compra compra = new Compra();
        compra.setId(1);

        // Then
        assertEquals(1, compra.getId());
    }

    @Test
    @DisplayName("Debe establecer y obtener proveedorId")
    void testProveedorId() {
        // Given
        Compra compra = new Compra();

        // When
        compra.setProveedorId(50);

        // Then
        assertEquals(50, compra.getProveedorId());
    }

    @Test
    @DisplayName("Debe establecer y obtener folioFacturaProveedor")
    void testFolioFactura() {
        // Given
        Compra compra = new Compra();

        // When
        compra.setFolioFacturaProveedor("FACT-2024-001");

        // Then
        assertEquals("FACT-2024-001", compra.getFolioFacturaProveedor());
    }

    @Test
    @DisplayName("Debe establecer y obtener totalCompra")
    void testTotalCompra() {
        // Given
        Compra compra = new Compra();

        // When
        compra.setTotalCompra(new BigDecimal("25000.75"));

        // Then
        assertEquals(new BigDecimal("25000.75"), compra.getTotalCompra());
    }

    @Test
    @DisplayName("Debe establecer y obtener usuarioCreacion")
    void testUsuarioCreacion() {
        // Given
        Compra compra = new Compra();

        // When
        compra.setUsuarioCreacion("admin");

        // Then
        assertEquals("admin", compra.getUsuarioCreacion());
    }

    @Test
    @DisplayName("Debe crear compra completa")
    void testCompraCompleta() {
        // Given & When
        Compra compra = new Compra();
        compra.setId(100);
        compra.setProveedorId(25);
        compra.setFolioFacturaProveedor("INV-2024-500");
        compra.setTotalCompra(new BigDecimal("15000.00"));
        compra.setUsuarioCreacion("comprador1");

        // Then
        assertNotNull(compra);
        assertEquals(100, compra.getId());
        assertEquals(25, compra.getProveedorId());
        assertEquals("INV-2024-500", compra.getFolioFacturaProveedor());
        assertEquals(new BigDecimal("15000.00"), compra.getTotalCompra());
        assertEquals("comprador1", compra.getUsuarioCreacion());
    }

    @Test
    @DisplayName("Debe establecer fechaCompra con prePersist")
    void testPrePersist() {
        // Given
        Compra compra = new Compra();

        // When
        compra.prePersist();

        // Then
        assertNotNull(compra.getFechaCompra());
        assertTrue(compra.getFechaCompra().isBefore(LocalDateTime.now().plusSeconds(1)));
        assertTrue(compra.getFechaCompra().isAfter(LocalDateTime.now().minusSeconds(1)));
    }

    @Test
    @DisplayName("Debe manejar montos grandes")
    void testMontosGrandes() {
        // Given
        Compra compra = new Compra();

        // When
        compra.setTotalCompra(new BigDecimal("999999.99"));

        // Then
        assertEquals(new BigDecimal("999999.99"), compra.getTotalCompra());
        assertTrue(compra.getTotalCompra().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    @DisplayName("Debe manejar folios con diferentes formatos")
    void testDiferentesFolios() {
        // Given
        Compra compra1 = new Compra();
        Compra compra2 = new Compra();
        Compra compra3 = new Compra();

        // When
        compra1.setFolioFacturaProveedor("FACT-001");
        compra2.setFolioFacturaProveedor("2024-INV-500");
        compra3.setFolioFacturaProveedor("ABC123XYZ");

        // Then
        assertEquals("FACT-001", compra1.getFolioFacturaProveedor());
        assertEquals("2024-INV-500", compra2.getFolioFacturaProveedor());
        assertEquals("ABC123XYZ", compra3.getFolioFacturaProveedor());
    }

    @Test
    @DisplayName("Debe permitir folio null")
    void testFolioNull() {
        // Given & When
        Compra compra = new Compra();
        compra.setFolioFacturaProveedor(null);

        // Then
        assertNull(compra.getFolioFacturaProveedor());
    }

    @Test
    @DisplayName("Debe permitir setFechaCompra manualmente")
    void testSetFechaCompraManual() {
        // Given
        Compra compra = new Compra();
        LocalDateTime fechaEspecifica = LocalDateTime.of(2024, 1, 15, 10, 30);

        // When
        compra.setFechaCompra(fechaEspecifica);

        // Then
        assertEquals(fechaEspecifica, compra.getFechaCompra());
    }

    @Test
    @DisplayName("Debe crear instancia sin valores por defecto")
    void testInstanciaPorDefecto() {
        // Given & When
        Compra compra = new Compra();

        // Then
        assertNotNull(compra);
        assertNull(compra.getId());
        assertNull(compra.getProveedorId());
        assertNull(compra.getFolioFacturaProveedor());
        assertNull(compra.getTotalCompra());
        assertNull(compra.getFechaCompra());
        assertNull(compra.getUsuarioCreacion());
    }
}
