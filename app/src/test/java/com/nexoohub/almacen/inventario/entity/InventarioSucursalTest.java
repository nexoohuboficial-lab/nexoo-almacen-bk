package com.nexoohub.almacen.inventario.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("InventarioSucursal - Tests de Entidad")
class InventarioSucursalTest {

    @Test
    @DisplayName("Debe crear inventario con ID compuesto")
    void testCrearConIdCompuesto() {
        // Given
        InventarioSucursalId id = new InventarioSucursalId(1, "SKU-001");
        InventarioSucursal inventario = new InventarioSucursal();

        // When
        inventario.setId(id);

        // Then
        assertNotNull(inventario.getId());
        assertEquals(1, inventario.getId().getSucursalId());
        assertEquals("SKU-001", inventario.getId().getSkuInterno());
    }

    @Test
    @DisplayName("Debe establecer y obtener stockActual")
    void testStockActual() {
        // Given
        InventarioSucursal inventario = new InventarioSucursal();

        // When
        inventario.setStockActual(100);

        // Then
        assertEquals(100, inventario.getStockActual());
    }

    @Test
    @DisplayName("Debe tener stockActual con valor por defecto 0")
    void testStockActualDefault() {
        // Given & When
        InventarioSucursal inventario = new InventarioSucursal();

        // Then
        assertEquals(0, inventario.getStockActual());
    }

    @Test
    @DisplayName("Debe establecer y obtener stockMinimoSucursal")
    void testStockMinimo() {
        // Given
        InventarioSucursal inventario = new InventarioSucursal();

        // When
        inventario.setStockMinimoSucursal(10);

        // Then
        assertEquals(10, inventario.getStockMinimoSucursal());
    }

    @Test
    @DisplayName("Debe tener stockMinimoSucursal con valor por defecto 1")
    void testStockMinimoDefault() {
        // Given & When
        InventarioSucursal inventario = new InventarioSucursal();

        // Then
        assertEquals(1, inventario.getStockMinimoSucursal());
    }

    @Test
    @DisplayName("Debe establecer y obtener ubicacionPasillo")
    void testUbicacionPasillo() {
        // Given
        InventarioSucursal inventario = new InventarioSucursal();

        // When
        inventario.setUbicacionPasillo("Anaquel 4, Repisa B");

        // Then
        assertEquals("Anaquel 4, Repisa B", inventario.getUbicacionPasillo());
    }

    @Test
    @DisplayName("Debe establecer y obtener costoPromedioPonderado")
    void testCostoPromedioPonderado() {
        // Given
        InventarioSucursal inventario = new InventarioSucursal();
        BigDecimal costo = new BigDecimal("150.50");

        // When
        inventario.setCostoPromedioPonderado(costo);

        // Then
        assertEquals(costo, inventario.getCostoPromedioPonderado());
    }

    @Test
    @DisplayName("Debe tener costoPromedioPonderado con valor por defecto ZERO")
    void testCostoPromedioPonderadoDefault() {
        // Given & When
        InventarioSucursal inventario = new InventarioSucursal();

        // Then
        assertEquals(BigDecimal.ZERO, inventario.getCostoPromedioPonderado());
    }

    @Test
    @DisplayName("Debe crear inventario completo")
    void testInventarioCompleto() {
        // Given
        InventarioSucursalId id = new InventarioSucursalId(5, "SKU-PROD-123");
        InventarioSucursal inventario = new InventarioSucursal();

        // When
        inventario.setId(id);
        inventario.setStockActual(250);
        inventario.setStockMinimoSucursal(20);
        inventario.setUbicacionPasillo("Anaquel 7, Repisa C");
        inventario.setCostoPromedioPonderado(new BigDecimal("85.75"));

        // Then
        assertNotNull(inventario);
        assertEquals(5, inventario.getId().getSucursalId());
        assertEquals("SKU-PROD-123", inventario.getId().getSkuInterno());
        assertEquals(250, inventario.getStockActual());
        assertEquals(20, inventario.getStockMinimoSucursal());
        assertEquals("Anaquel 7, Repisa C", inventario.getUbicacionPasillo());
        assertEquals(new BigDecimal("85.75"), inventario.getCostoPromedioPonderado());
    }

    @Test
    @DisplayName("Debe detectar stock bajo mínimo")
    void testStockBajoMinimo() {
        // Given
        InventarioSucursal inventario = new InventarioSucursal();
        inventario.setStockActual(5);
        inventario.setStockMinimoSucursal(10);

        // Then
        assertTrue(inventario.getStockActual() < inventario.getStockMinimoSucursal());
    }

    @Test
    @DisplayName("Debe manejar diferentes ubicaciones")
    void testDiferentesUbicaciones() {
        // Given
        InventarioSucursal i1 = new InventarioSucursal();
        InventarioSucursal i2 = new InventarioSucursal();
        InventarioSucursal i3 = new InventarioSucursal();

        // When
        i1.setUbicacionPasillo("Anaquel 1, Repisa A");
        i2.setUbicacionPasillo("Anaquel 5, Repisa B");
        i3.setUbicacionPasillo("Bodega Principal");

        // Then
        assertEquals("Anaquel 1, Repisa A", i1.getUbicacionPasillo());
        assertEquals("Anaquel 5, Repisa B", i2.getUbicacionPasillo());
        assertEquals("Bodega Principal", i3.getUbicacionPasillo());
    }

    @Test
    @DisplayName("Debe calcular valor del inventario")
    void testCalculoValorInventario() {
        // Given
        InventarioSucursal inventario = new InventarioSucursal();
        inventario.setStockActual(100);
        inventario.setCostoPromedioPonderado(new BigDecimal("50.00"));

        // When
        BigDecimal valorInventario = inventario.getCostoPromedioPonderado()
                .multiply(new BigDecimal(inventario.getStockActual()));

        // Then
        assertEquals(new BigDecimal("5000.00"), valorInventario);
    }

    @Test
    @DisplayName("Debe permitir stock cero")
    void testStockCero() {
        // Given & When
        InventarioSucursal inventario = new InventarioSucursal();
        inventario.setStockActual(0);

        // Then
        assertEquals(0, inventario.getStockActual());
    }

    @Test
    @DisplayName("Debe permitir ubicación null")
    void testUbicacionNull() {
        // Given & When
        InventarioSucursal inventario = new InventarioSucursal();
        inventario.setUbicacionPasillo(null);

        // Then
        assertNull(inventario.getUbicacionPasillo());
    }

    @Test
    @DisplayName("Debe lanzar excepción al intentar establecer stock negativo")
    void testStockNegativoLanzaExcepcion() {
        // Given
        InventarioSucursal inventario = new InventarioSucursal();

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class, 
            () -> inventario.setStockActual(-10)
        );
        
        assertTrue(exception.getMessage().contains("stock actual no puede ser negativo"));
    }

    @Test
    @DisplayName("Debe lanzar excepción con stock negativo de -1")
    void testStockMenosUno() {
        // Given
        InventarioSucursal inventario = new InventarioSucursal();

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> inventario.setStockActual(-1));
    }

    @Test
    @DisplayName("Debe lanzar excepción con stock muy negativo")
    void testStockMuyNegativo() {
        // Given
        InventarioSucursal inventario = new InventarioSucursal();

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> inventario.setStockActual(-999));
    }

    @Test
    @DisplayName("Debe permitir establecer stock positivo después de intento negativo")
    void testRecuperacionDespuesDeErrorStock() {
        // Given
        InventarioSucursal inventario = new InventarioSucursal();

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> inventario.setStockActual(-5));
        
        // Ahora establecemos un valor válido
        inventario.setStockActual(50);
        assertEquals(50, inventario.getStockActual());
    }

    @Test
    @DisplayName("Debe prevenir operaciones que resulten en stock negativo")
    void testOperacionesQueResultanEnStockNegativo() {
        // Given
        InventarioSucursal inventario = new InventarioSucursal();
        inventario.setStockActual(10);

        // Simular una venta de 15 unidades cuando solo hay 10
        int ventaCantidad = 15;
        int stockActual = inventario.getStockActual();
        int nuevoStock = stockActual - ventaCantidad;

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> inventario.setStockActual(nuevoStock));
    }
}
