package com.nexoohub.almacen.finanzas.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("HistorialPrecio - Tests de Entidad")
class HistorialPrecioTest {

    @Test
    @DisplayName("Debe crear historial con ID")
    void testCrearHistorialConId() {
        // Given & When
        HistorialPrecio historial = new HistorialPrecio();
        historial.setId(1);

        // Then
        assertEquals(1, historial.getId());
    }

    @Test
    @DisplayName("Debe establecer y obtener skuInterno")
    void testSkuInterno() {
        // Given
        HistorialPrecio historial = new HistorialPrecio();

        // When
        historial.setSkuInterno("SKU001");

        // Then
        assertEquals("SKU001", historial.getSkuInterno());
    }

    @Test
    @DisplayName("Debe establecer y obtener costoBase")
    void testCostoBase() {
        // Given
        HistorialPrecio historial = new HistorialPrecio();

        // When
        historial.setCostoBase(new BigDecimal("100.00"));

        // Then
        assertEquals(new BigDecimal("100.00"), historial.getCostoBase());
    }

    @Test
    @DisplayName("Debe establecer y obtener precioPonderado")
    void testPrecioPonderado() {
        // Given
        HistorialPrecio historial = new HistorialPrecio();

        // When
        historial.setPrecioPonderado(new BigDecimal("150.00"));

        // Then
        assertEquals(new BigDecimal("150.00"), historial.getPrecioPonderado());
    }

    @Test
    @DisplayName("Debe establecer y obtener precioFinalPublico")
    void testPrecioFinalPublico() {
        // Given
        HistorialPrecio historial = new HistorialPrecio();

        // When
        historial.setPrecioFinalPublico(new BigDecimal("200.00"));

        // Then
        assertEquals(new BigDecimal("200.00"), historial.getPrecioFinalPublico());
    }

    @Test
    @DisplayName("Debe establecer y obtener precioPublicoProveedor")
    void testPrecioPublicoProveedor() {
        // Given
        HistorialPrecio historial = new HistorialPrecio();

        // When
        historial.setPrecioPublicoProveedor(new BigDecimal("180.00"));

        // Then
        assertEquals(new BigDecimal("180.00"), historial.getPrecioPublicoProveedor());
    }

    @Test
    @DisplayName("Debe establecer y obtener usuarioCreacion")
    void testUsuarioCreacion() {
        // Given
        HistorialPrecio historial = new HistorialPrecio();

        // When
        historial.setUsuarioCreacion("admin");

        // Then
        assertEquals("admin", historial.getUsuarioCreacion());
    }

    @Test
    @DisplayName("Debe crear historial completo")
    void testHistorialCompleto() {
        // Given & When
        HistorialPrecio historial = new HistorialPrecio();
        historial.setId(10);
        historial.setSkuInterno("SKU-TEST-001");
        historial.setCostoBase(new BigDecimal("500.00"));
        historial.setPrecioPonderado(new BigDecimal("600.00"));
        historial.setPrecioFinalPublico(new BigDecimal("750.00"));
        historial.setPrecioPublicoProveedor(new BigDecimal("700.00"));
        historial.setUsuarioCreacion("sistema");

        // Then
        assertNotNull(historial);
        assertEquals(10, historial.getId());
        assertEquals("SKU-TEST-001", historial.getSkuInterno());
        assertEquals(new BigDecimal("500.00"), historial.getCostoBase());
        assertEquals(new BigDecimal("600.00"), historial.getPrecioPonderado());
        assertEquals(new BigDecimal("750.00"), historial.getPrecioFinalPublico());
        assertEquals(new BigDecimal("700.00"), historial.getPrecioPublicoProveedor());
        assertEquals("sistema", historial.getUsuarioCreacion());
    }

    @Test
    @DisplayName("Debe establecer fechaCalculo con prePersist")
    void testPrePersist() {
        // Given
        HistorialPrecio historial = new HistorialPrecio();

        // When
        historial.prePersist();

        // Then
        assertNotNull(historial.getFechaCalculo());
        assertTrue(historial.getFechaCalculo().isBefore(LocalDateTime.now().plusSeconds(1)));
        assertTrue(historial.getFechaCalculo().isAfter(LocalDateTime.now().minusSeconds(1)));
    }

    @Test
    @DisplayName("Debe manejar precios con múltiples decimales")
    void testPreciosDecimales() {
        // Given
        HistorialPrecio historial = new HistorialPrecio();

        // When
        historial.setCostoBase(new BigDecimal("123.456789"));
        historial.setPrecioPonderado(new BigDecimal("234.567890"));
        historial.setPrecioFinalPublico(new BigDecimal("345.678901"));

        // Then
        assertEquals(new BigDecimal("123.456789"), historial.getCostoBase());
        assertEquals(new BigDecimal("234.567890"), historial.getPrecioPonderado());
        assertEquals(new BigDecimal("345.678901"), historial.getPrecioFinalPublico());
    }

    @Test
    @DisplayName("Debe validar que precioFinal sea mayor que costo")
    void testLogicaPrecio() {
        // Given
        HistorialPrecio historial = new HistorialPrecio();
        historial.setCostoBase(new BigDecimal("100.00"));
        historial.setPrecioFinalPublico(new BigDecimal("150.00"));

        // When & Then
        assertTrue(historial.getPrecioFinalPublico().compareTo(historial.getCostoBase()) > 0);
    }

    @Test
    @DisplayName("Debe manejar diferentes SKUs")
    void testDiferentesSKUs() {
        // Given
        HistorialPrecio h1 = new HistorialPrecio();
        HistorialPrecio h2 = new HistorialPrecio();
        HistorialPrecio h3 = new HistorialPrecio();

        // When
        h1.setSkuInterno("SKU-A-001");
        h2.setSkuInterno("PROD-B-999");
        h3.setSkuInterno("ITEM-C-123");

        // Then
        assertEquals("SKU-A-001", h1.getSkuInterno());
        assertEquals("PROD-B-999", h2.getSkuInterno());
        assertEquals("ITEM-C-123", h3.getSkuInterno());
    }

    @Test
    @DisplayName("Debe obtener fechaCalculo")
    void testGetFechaCalculo() {
        // Given
        HistorialPrecio historial = new HistorialPrecio();
        historial.prePersist();

        // When
        LocalDateTime fecha = historial.getFechaCalculo();

        // Then
        assertNotNull(fecha);
        assertInstanceOf(LocalDateTime.class, fecha);
    }

    @Test
    @DisplayName("Debe crear instancia sin valores por defecto")
    void testInstanciaPorDefecto() {
        // Given & When
        HistorialPrecio historial = new HistorialPrecio();

        // Then
        assertNotNull(historial);
        assertNull(historial.getId());
        assertNull(historial.getSkuInterno());
        assertNull(historial.getCostoBase());
        assertNull(historial.getPrecioPonderado());
        assertNull(historial.getPrecioFinalPublico());
        assertNull(historial.getPrecioPublicoProveedor());
        assertNull(historial.getFechaCalculo());
        assertNull(historial.getUsuarioCreacion());
    }
}
