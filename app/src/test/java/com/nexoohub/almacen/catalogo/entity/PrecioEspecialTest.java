package com.nexoohub.almacen.catalogo.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PrecioEspecial - Tests de Entidad")
class PrecioEspecialTest {

    @Test
    @DisplayName("Debe crear precio especial con ID")
    void testCrearPrecioEspecialConId() {
        // Given & When
        PrecioEspecial precio = new PrecioEspecial();
        precio.setId(1);

        // Then
        assertEquals(1, precio.getId());
    }

    @Test
    @DisplayName("Debe establecer y obtener skuInterno")
    void testSkuInterno() {
        // Given
        PrecioEspecial precio = new PrecioEspecial();

        // When
        precio.setSkuInterno("SKU-ESPECIAL-001");

        // Then
        assertEquals("SKU-ESPECIAL-001", precio.getSkuInterno());
    }

    @Test
    @DisplayName("Debe establecer y obtener tipoClienteId")
    void testTipoClienteId() {
        // Given
        PrecioEspecial precio = new PrecioEspecial();

        // When
        precio.setTipoClienteId(2);

        // Then
        assertEquals(2, precio.getTipoClienteId());
    }

    @Test
    @DisplayName("Debe establecer y obtener precioFijo")
    void testPrecioFijo() {
        // Given
        PrecioEspecial precio = new PrecioEspecial();
        BigDecimal precioFijo = new BigDecimal("50.00");

        // When
        precio.setPrecioFijo(precioFijo);

        // Then
        assertEquals(precioFijo, precio.getPrecioFijo());
    }

    @Test
    @DisplayName("Debe crear precio especial completo")
    void testPrecioEspecialCompleto() {
        // Given & When
        PrecioEspecial precio = new PrecioEspecial();
        precio.setId(10);
        precio.setSkuInterno("SKU-TALLER-500");
        precio.setTipoClienteId(2); // Taller Mecánico
        precio.setPrecioFijo(new BigDecimal("45.00"));

        // Then
        assertNotNull(precio);
        assertEquals(10, precio.getId());
        assertEquals("SKU-TALLER-500", precio.getSkuInterno());
        assertEquals(2, precio.getTipoClienteId());
        assertEquals(new BigDecimal("45.00"), precio.getPrecioFijo());
    }

    @Test
    @DisplayName("Debe manejar diferentes tipos de cliente")
    void testDiferentesTiposCliente() {
        // Given
        PrecioEspecial p1 = new PrecioEspecial();
        PrecioEspecial p2 = new PrecioEspecial();
        PrecioEspecial p3 = new PrecioEspecial();

        // When
        p1.setTipoClienteId(1); // Cliente Público
        p2.setTipoClienteId(2); // Taller Mecánico
        p3.setTipoClienteId(3); // Distribuidor

        // Then
        assertEquals(1, p1.getTipoClienteId());
        assertEquals(2, p2.getTipoClienteId());
        assertEquals(3, p3.getTipoClienteId());
    }

    @Test
    @DisplayName("Debe manejar precios especiales con decimales")
    void testPreciosConDecimales() {
        // Given
        PrecioEspecial precio = new PrecioEspecial();
        BigDecimal precioDecimal = new BigDecimal("123.45");

        // When
        precio.setPrecioFijo(precioDecimal);

        // Then
        assertEquals(precioDecimal, precio.getPrecioFijo());
    }

    @Test
    @DisplayName("Debe manejar diferentes SKUs con precios especiales")
    void testDiferentesSKUs() {
        // Given
        PrecioEspecial p1 = new PrecioEspecial();
        PrecioEspecial p2 = new PrecioEspecial();

        // When
        p1.setSkuInterno("SKU-A-001");
        p1.setTipoClienteId(2);
        p1.setPrecioFijo(new BigDecimal("40.00"));

        p2.setSkuInterno("SKU-B-002");
        p2.setTipoClienteId(2);
        p2.setPrecioFijo(new BigDecimal("60.00"));

        // Then
        assertEquals("SKU-A-001", p1.getSkuInterno());
        assertEquals("SKU-B-002", p2.getSkuInterno());
        assertTrue(p2.getPrecioFijo().compareTo(p1.getPrecioFijo()) > 0);
    }

    @Test
    @DisplayName("Debe validar precio especial menor que precio público")
    void testPrecioEspecialEsDescuento() {
        // Given
        PrecioEspecial precioEspecial = new PrecioEspecial();
        precioEspecial.setPrecioFijo(new BigDecimal("80.00"));
        BigDecimal precioPublico = new BigDecimal("100.00");

        // Then
        assertTrue(precioEspecial.getPrecioFijo().compareTo(precioPublico) < 0);
    }

    @Test
    @DisplayName("Debe manejar múltiples precios para mismo producto")
    void testMultiplesPreciosMismoProducto() {
        // Given
        String sku = "SKU-PROD-001";
        PrecioEspecial precioTaller = new PrecioEspecial();
        PrecioEspecial precioDistribuidor = new PrecioEspecial();

        // When
        precioTaller.setSkuInterno(sku);
        precioTaller.setTipoClienteId(2);
        precioTaller.setPrecioFijo(new BigDecimal("50.00"));

        precioDistribuidor.setSkuInterno(sku);
        precioDistribuidor.setTipoClienteId(3);
        precioDistribuidor.setPrecioFijo(new BigDecimal("40.00"));

        // Then
        assertEquals(sku, precioTaller.getSkuInterno());
        assertEquals(sku, precioDistribuidor.getSkuInterno());
        assertNotEquals(precioTaller.getTipoClienteId(), precioDistribuidor.getTipoClienteId());
        assertTrue(precioDistribuidor.getPrecioFijo().compareTo(precioTaller.getPrecioFijo()) < 0);
    }

    @Test
    @DisplayName("Debe permitir valores null")
    void testValoresNull() {
        // Given & When
        PrecioEspecial precio = new PrecioEspecial();
        precio.setSkuInterno(null);
        precio.setTipoClienteId(null);
        precio.setPrecioFijo(null);

        // Then
        assertNull(precio.getSkuInterno());
        assertNull(precio.getTipoClienteId());
        assertNull(precio.getPrecioFijo());
    }

    @Test
    @DisplayName("Debe crear instancia sin valores por defecto")
    void testInstanciaPorDefecto() {
        // Given & When
        PrecioEspecial precio = new PrecioEspecial();

        // Then
        assertNotNull(precio);
        assertNull(precio.getId());
        assertNull(precio.getSkuInterno());
        assertNull(precio.getTipoClienteId());
        assertNull(precio.getPrecioFijo());
    }
}
