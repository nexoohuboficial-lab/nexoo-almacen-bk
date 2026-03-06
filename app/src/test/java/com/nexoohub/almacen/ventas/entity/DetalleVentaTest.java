package com.nexoohub.almacen.ventas.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DetalleVenta - Tests de Entidad")
class DetalleVentaTest {

    @Test
    @DisplayName("Debe crear detalle venta con ID")
    void testCrearDetalleVentaConId() {
        // Given & When
        DetalleVenta detalle = new DetalleVenta();
        detalle.setId(1);

        // Then
        assertEquals(1, detalle.getId());
    }

    @Test
    @DisplayName("Debe establecer y obtener ventaId")
    void testVentaId() {
        // Given
        DetalleVenta detalle = new DetalleVenta();

        // When
        detalle.setVentaId(100);

        // Then
        assertEquals(100, detalle.getVentaId());
    }

    @Test
    @DisplayName("Debe establecer y obtener skuInterno")
    void testSkuInterno() {
        // Given
        DetalleVenta detalle = new DetalleVenta();

        // When
        detalle.setSkuInterno("SKU-PROD-001");

        // Then
        assertEquals("SKU-PROD-001", detalle.getSkuInterno());
    }

    @Test
    @DisplayName("Debe establecer y obtener cantidad")
    void testCantidad() {
        // Given
        DetalleVenta detalle = new DetalleVenta();

        // When
        detalle.setCantidad(5);

        // Then
        assertEquals(5, detalle.getCantidad());
    }

    @Test
    @DisplayName("Debe establecer y obtener precioUnitarioVenta")
    void testPrecioUnitarioVenta() {
        // Given
        DetalleVenta detalle = new DetalleVenta();
        BigDecimal precio = new BigDecimal("150.00");

        // When
        detalle.setPrecioUnitarioVenta(precio);

        // Then
        assertEquals(precio, detalle.getPrecioUnitarioVenta());
    }

    @Test
    @DisplayName("Debe crear detalle venta completo")
    void testDetalleVentaCompleto() {
        // Given & When
        DetalleVenta detalle = new DetalleVenta();
        detalle.setId(50);
        detalle.setVentaId(200);
        detalle.setSkuInterno("SKU-ABC-123");
        detalle.setCantidad(3);
        detalle.setPrecioUnitarioVenta(new BigDecimal("250.50"));

        // Then
        assertNotNull(detalle);
        assertEquals(50, detalle.getId());
        assertEquals(200, detalle.getVentaId());
        assertEquals("SKU-ABC-123", detalle.getSkuInterno());
        assertEquals(3, detalle.getCantidad());
        assertEquals(new BigDecimal("250.50"), detalle.getPrecioUnitarioVenta());
    }

    @Test
    @DisplayName("Debe manejar cantidades grandes")
    void testCantidadesGrandes() {
        // Given
        DetalleVenta detalle = new DetalleVenta();

        // When
        detalle.setCantidad(1000);

        // Then
        assertEquals(1000, detalle.getCantidad());
        assertTrue(detalle.getCantidad() > 100);
    }

    @Test
    @DisplayName("Debe manejar precios con decimales")
    void testPreciosConDecimales() {
        // Given
        DetalleVenta detalle = new DetalleVenta();
        BigDecimal precioConDecimales = new BigDecimal("99.99");

        // When
        detalle.setPrecioUnitarioVenta(precioConDecimales);

        // Then
        assertEquals(precioConDecimales, detalle.getPrecioUnitarioVenta());
    }

    @Test
    @DisplayName("Debe calcular subtotal correctamente")
    void testCalculoSubtotal() {
        // Given
        DetalleVenta detalle = new DetalleVenta();
        detalle.setCantidad(4);
        detalle.setPrecioUnitarioVenta(new BigDecimal("50.00"));

        // When
        BigDecimal subtotal = detalle.getPrecioUnitarioVenta()
                .multiply(new BigDecimal(detalle.getCantidad()));

        // Then
        assertEquals(new BigDecimal("200.00"), subtotal);
    }

    @Test
    @DisplayName("Debe manejar diferentes SKUs")
    void testDiferentesSKUs() {
        // Given
        DetalleVenta d1 = new DetalleVenta();
        DetalleVenta d2 = new DetalleVenta();
        DetalleVenta d3 = new DetalleVenta();

        // When
        d1.setSkuInterno("SKU-A-001");
        d2.setSkuInterno("PROD-B-999");
        d3.setSkuInterno("ITEM-C-500");

        // Then
        assertEquals("SKU-A-001", d1.getSkuInterno());
        assertEquals("PROD-B-999", d2.getSkuInterno());
        assertEquals("ITEM-C-500", d3.getSkuInterno());
    }

    @Test
    @DisplayName("Debe permitir valores null")
    void testValoresNull() {
        // Given & When
        DetalleVenta detalle = new DetalleVenta();
        detalle.setVentaId(null);
        detalle.setSkuInterno(null);
        detalle.setCantidad(null);
        detalle.setPrecioUnitarioVenta(null);

        // Then
        assertNull(detalle.getVentaId());
        assertNull(detalle.getSkuInterno());
        assertNull(detalle.getCantidad());
        assertNull(detalle.getPrecioUnitarioVenta());
    }

    @Test
    @DisplayName("Debe crear instancia sin valores por defecto")
    void testInstanciaPorDefecto() {
        // Given & When
        DetalleVenta detalle = new DetalleVenta();

        // Then
        assertNotNull(detalle);
        assertNull(detalle.getId());
        assertNull(detalle.getVentaId());
        assertNull(detalle.getSkuInterno());
        assertNull(detalle.getCantidad());
        assertNull(detalle.getPrecioUnitarioVenta());
    }
}
