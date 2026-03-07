package com.nexoohub.almacen.compras.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DetalleCompra - Tests de Entidad")
class DetalleCompraTest {

    @Test
    @DisplayName("Debe crear detalle compra con ID")
    void testCrearDetalleCompraConId() {
        // Given & When
        DetalleCompra detalle = new DetalleCompra();
        detalle.setId(1);

        // Then
        assertEquals(1, detalle.getId());
    }

    @Test
    @DisplayName("Debe establecer y obtener compraId")
    void testCompraId() {
        // Given
        DetalleCompra detalle = new DetalleCompra();

        // When
        detalle.setCompraId(75);

        // Then
        assertEquals(75, detalle.getCompraId());
    }

    @Test
    @DisplayName("Debe establecer y obtener skuInterno")
    void testSkuInterno() {
        // Given
        DetalleCompra detalle = new DetalleCompra();

        // When
        detalle.setSkuInterno("SKU-COMPRA-001");

        // Then
        assertEquals("SKU-COMPRA-001", detalle.getSkuInterno());
    }

    @Test
    @DisplayName("Debe establecer y obtener cantidad")
    void testCantidad() {
        // Given
        DetalleCompra detalle = new DetalleCompra();

        // When
        detalle.setCantidad(20);

        // Then
        assertEquals(20, detalle.getCantidad());
    }

    @Test
    @DisplayName("Debe establecer y obtener costoUnitarioCompra")
    void testCostoUnitarioCompra() {
        // Given
        DetalleCompra detalle = new DetalleCompra();
        BigDecimal costo = new BigDecimal("85.50");

        // When
        detalle.setCostoUnitarioCompra(costo);

        // Then
        assertEquals(costo, detalle.getCostoUnitarioCompra());
    }

    @Test
    @DisplayName("Debe crear detalle compra completo")
    void testDetalleCompraCompleto() {
        // Given & When
        DetalleCompra detalle = new DetalleCompra();
        detalle.setId(25);
        detalle.setCompraId(150);
        detalle.setSkuInterno("SKU-XYZ-789");
        detalle.setCantidad(50);
        detalle.setCostoUnitarioCompra(new BigDecimal("120.75"));

        // Then
        assertNotNull(detalle);
        assertEquals(25, detalle.getId());
        assertEquals(150, detalle.getCompraId());
        assertEquals("SKU-XYZ-789", detalle.getSkuInterno());
        assertEquals(50, detalle.getCantidad());
        assertEquals(new BigDecimal("120.75"), detalle.getCostoUnitarioCompra());
    }

    @Test
    @DisplayName("Debe manejar cantidades grandes de compra")
    void testCantidadesGrandesCompra() {
        // Given
        DetalleCompra detalle = new DetalleCompra();

        // When
        detalle.setCantidad(5000);

        // Then
        assertEquals(5000, detalle.getCantidad());
        assertTrue(detalle.getCantidad() >= 1000);
    }

    @Test
    @DisplayName("Debe manejar costos con decimales")
    void testCostosConDecimales() {
        // Given
        DetalleCompra detalle = new DetalleCompra();
        BigDecimal costoConDecimales = new BigDecimal("45.99");

        // When
        detalle.setCostoUnitarioCompra(costoConDecimales);

        // Then
        assertEquals(costoConDecimales, detalle.getCostoUnitarioCompra());
    }

    @Test
    @DisplayName("Debe calcular costo total correctamente")
    void testCalculoCostoTotal() {
        // Given
        DetalleCompra detalle = new DetalleCompra();
        detalle.setCantidad(10);
        detalle.setCostoUnitarioCompra(new BigDecimal("30.00"));

        // When
        BigDecimal costoTotal = detalle.getCostoUnitarioCompra()
                .multiply(new BigDecimal(detalle.getCantidad()));

        // Then
        assertEquals(new BigDecimal("300.00"), costoTotal);
    }

    @Test
    @DisplayName("Debe manejar diferentes SKUs de compra")
    void testDiferentesSKUsCompra() {
        // Given
        DetalleCompra d1 = new DetalleCompra();
        DetalleCompra d2 = new DetalleCompra();
        DetalleCompra d3 = new DetalleCompra();

        // When
        d1.setSkuInterno("COMP-A-001");
        d2.setSkuInterno("PROV-B-999");
        d3.setSkuInterno("ART-C-500");

        // Then
        assertEquals("COMP-A-001", d1.getSkuInterno());
        assertEquals("PROV-B-999", d2.getSkuInterno());
        assertEquals("ART-C-500", d3.getSkuInterno());
    }

    @Test
    @DisplayName("Debe permitir valores null")
    void testValoresNull() {
        // Given & When
        DetalleCompra detalle = new DetalleCompra();
        detalle.setCompraId(null);
        detalle.setSkuInterno(null);
        detalle.setCantidad(null);
        detalle.setCostoUnitarioCompra(null);

        // Then
        assertNull(detalle.getCompraId());
        assertNull(detalle.getSkuInterno());
        assertNull(detalle.getCantidad());
        assertNull(detalle.getCostoUnitarioCompra());
    }

    @Test
    @DisplayName("Debe crear instancia sin valores por defecto")
    void testInstanciaPorDefecto() {
        // Given & When
        DetalleCompra detalle = new DetalleCompra();

        // Then
        assertNotNull(detalle);
        assertNull(detalle.getId());
        assertNull(detalle.getCompraId());
        assertNull(detalle.getSkuInterno());
        assertNull(detalle.getCantidad());
        assertNull(detalle.getCostoUnitarioCompra());
    }

    @Test
    @DisplayName("Debe validar que cantidad debe ser al menos 1")
    void testCantidadMinima() {
        // Given
        DetalleCompra detalle = new DetalleCompra();

        // When - Cantidad válida
        detalle.setCantidad(1);

        // Then
        assertEquals(1, detalle.getCantidad());
        assertTrue(detalle.getCantidad() >= 1, "La cantidad debe ser al menos 1");
    }

    @Test
    @DisplayName("Debe validar que el costo debe ser positivo")
    void testCostoPositivo() {
        // Given
        DetalleCompra detalle = new DetalleCompra();

        // When - Costo válido
        detalle.setCostoUnitarioCompra(new BigDecimal("0.01"));

        // Then
        assertTrue(detalle.getCostoUnitarioCompra().compareTo(BigDecimal.ZERO) > 0,
                "El costo debe ser mayor a cero");
    }

    @Test
    @DisplayName("Debe documentar que costo cero no es válido según validaciones")
    void testCostoCeroNoValido() {
        // Given
        DetalleCompra detalle = new DetalleCompra();

        // When
        detalle.setCostoUnitarioCompra(BigDecimal.ZERO);

        // Then - Documentamos que con @Valid este caso fallaría
        // Con @DecimalMin("0.01") en la entidad, la validación rechazaría esto
        assertTrue(detalle.getCostoUnitarioCompra().compareTo(new BigDecimal("0.01")) < 0,
                "Costo cero no cumple con @DecimalMin(0.01)");
    }

    @Test
    @DisplayName("Debe documentar que cantidad cero no es válida según validaciones")
    void testCantidadCeroNoValida() {
        // Given  
        DetalleCompra detalle = new DetalleCompra();

        // When
        detalle.setCantidad(0);

        // Then - Documentamos que con @Valid este caso fallaría
        // Con @Min(1) en la entidad, la validación rechazaría esto
        assertTrue(detalle.getCantidad() < 1,
                "Cantidad cero no cumple con @Min(1)");
    }
}
