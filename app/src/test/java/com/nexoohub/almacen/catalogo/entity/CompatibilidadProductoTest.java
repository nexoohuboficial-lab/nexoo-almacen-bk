package com.nexoohub.almacen.catalogo.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CompatibilidadProducto - Tests de Entidad")
class CompatibilidadProductoTest {

    @Test
    @DisplayName("Debe crear compatibilidad con ID")
    void testCrearCompatibilidadConId() {
        // Given & When
        CompatibilidadProducto comp = new CompatibilidadProducto();
        comp.setId(1);

        // Then
        assertEquals(1, comp.getId());
    }

    @Test
    @DisplayName("Debe establecer y obtener skuInterno")
    void testSkuInterno() {
        // Given
        CompatibilidadProducto comp = new CompatibilidadProducto();

        // When
        comp.setSkuInterno("SKU-REFACCION-001");

        // Then
        assertEquals("SKU-REFACCION-001", comp.getSkuInterno());
    }

    @Test
    @DisplayName("Debe establecer y obtener motoId")
    void testMotoId() {
        // Given
        CompatibilidadProducto comp = new CompatibilidadProducto();

        // When
        comp.setMotoId(5);

        // Then
        assertEquals(5, comp.getMotoId());
    }

    @Test
    @DisplayName("Debe establecer y obtener anioInicio")
    void testAnioInicio() {
        // Given
        CompatibilidadProducto comp = new CompatibilidadProducto();

        // When
        comp.setAnioInicio(2015);

        // Then
        assertEquals(2015, comp.getAnioInicio());
    }

    @Test
    @DisplayName("Debe establecer y obtener anioFin")
    void testAnioFin() {
        // Given
        CompatibilidadProducto comp = new CompatibilidadProducto();

        // When
        comp.setAnioFin(2020);

        // Then
        assertEquals(2020, comp.getAnioFin());
    }

    @Test
    @DisplayName("Debe crear compatibilidad completa")
    void testCompatibilidadCompleta() {
        // Given & When
        CompatibilidadProducto comp = new CompatibilidadProducto();
        comp.setId(10);
        comp.setSkuInterno("SKU-FILTRO-500");
        comp.setMotoId(3);
        comp.setAnioInicio(2018);
        comp.setAnioFin(2023);

        // Then
        assertNotNull(comp);
        assertEquals(10, comp.getId());
        assertEquals("SKU-FILTRO-500", comp.getSkuInterno());
        assertEquals(3, comp.getMotoId());
        assertEquals(2018, comp.getAnioInicio());
        assertEquals(2023, comp.getAnioFin());
    }

    @Test
    @DisplayName("Debe validar rango de años correcto")
    void testRangoAniosValido() {
        // Given
        CompatibilidadProducto comp = new CompatibilidadProducto();
        comp.setAnioInicio(2015);
        comp.setAnioFin(2020);

        // Then
        assertTrue(comp.getAnioFin() >= comp.getAnioInicio());
    }

    @Test
    @DisplayName("Debe manejar compatibilidad sin fecha fin")
    void testCompatibilidadSinFechaFin() {
        // Given & When
        CompatibilidadProducto comp = new CompatibilidadProducto();
        comp.setSkuInterno("SKU-UNIVERSAL");
        comp.setMotoId(7);
        comp.setAnioInicio(2020);
        comp.setAnioFin(null); // Compatible hasta actualidad

        // Then
        assertEquals(2020, comp.getAnioInicio());
        assertNull(comp.getAnioFin());
    }

    @Test
    @DisplayName("Debe manejar múltiples motos para mismo SKU")
    void testMultiplesMotos() {
        // Given
        String sku = "SKU-BUJIA-001";
        CompatibilidadProducto c1 = new CompatibilidadProducto();
        CompatibilidadProducto c2 = new CompatibilidadProducto();
        CompatibilidadProducto c3 = new CompatibilidadProducto();

        // When
        c1.setSkuInterno(sku);
        c1.setMotoId(1); // Honda CBR

        c2.setSkuInterno(sku);
        c2.setMotoId(2); // Yamaha R1

        c3.setSkuInterno(sku);
        c3.setMotoId(3); // Kawasaki Ninja

        // Then
        assertEquals(sku, c1.getSkuInterno());
        assertEquals(sku, c2.getSkuInterno());
        assertEquals(sku, c3.getSkuInterno());
        assertNotEquals(c1.getMotoId(), c2.getMotoId());
        assertNotEquals(c2.getMotoId(), c3.getMotoId());
    }

    @Test
    @DisplayName("Debe calcular años de aplicación")
    void testCalculoAniosAplicacion() {
        // Given
        CompatibilidadProducto comp = new CompatibilidadProducto();
        comp.setAnioInicio(2015);
        comp.setAnioFin(2020);

        // When
        int aniosAplicacion = comp.getAnioFin() - comp.getAnioInicio() + 1;

        // Then
        assertEquals(6, aniosAplicacion);
    }

    @Test
    @DisplayName("Debe validar año dentro del rango")
    void testAnioEnRango() {
        // Given
        CompatibilidadProducto comp = new CompatibilidadProducto();
        comp.setAnioInicio(2015);
        comp.setAnioFin(2020);
        int anioConsulta = 2018;

        // Then
        assertTrue(anioConsulta >= comp.getAnioInicio() && anioConsulta <= comp.getAnioFin());
    }

    @Test
    @DisplayName("Debe permitir valores null")
    void testValoresNull() {
        // Given & When
        CompatibilidadProducto comp = new CompatibilidadProducto();
        comp.setSkuInterno(null);
        comp.setMotoId(null);
        comp.setAnioInicio(null);
        comp.setAnioFin(null);

        // Then
        assertNull(comp.getSkuInterno());
        assertNull(comp.getMotoId());
        assertNull(comp.getAnioInicio());
        assertNull(comp.getAnioFin());
    }

    @Test
    @DisplayName("Debe crear instancia sin valores por defecto")
    void testInstanciaPorDefecto() {
        // Given & When
        CompatibilidadProducto comp = new CompatibilidadProducto();

        // Then
        assertNotNull(comp);
        assertNull(comp.getId());
        assertNull(comp.getSkuInterno());
        assertNull(comp.getMotoId());
        assertNull(comp.getAnioInicio());
        assertNull(comp.getAnioFin());
    }
}
