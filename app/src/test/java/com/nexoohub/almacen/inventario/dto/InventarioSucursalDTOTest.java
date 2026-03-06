package com.nexoohub.almacen.inventario.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("InventarioSucursalDTO - Tests de Estructura")
class InventarioSucursalDTOTest {

    @Test
    @DisplayName("Debe crear DTO con constructor completo")
    void testConstructorCompleto() {
        // Given & When
        InventarioSucursalDTO dto = new InventarioSucursalDTO(
                "SKU001",
                "Producto Test",
                100,
                new BigDecimal("150.50")
        );

        // Then
        assertNotNull(dto);
        assertEquals("SKU001", dto.getSkuInterno());
        assertEquals("Producto Test", dto.getNombreComercial());
        assertEquals(100, dto.getStockActual());
        assertEquals(new BigDecimal("150.50"), dto.getCostoPromedioPonderado());
    }

    @Test
    @DisplayName("Debe manejar valores mínimos correctamente")
    void testValoresMinimos() {
        // Given & When
        InventarioSucursalDTO dto = new InventarioSucursalDTO(
                "SKU",
                "P",
                0,
                BigDecimal.ZERO
        );

        // Then
        assertEquals("SKU", dto.getSkuInterno());
        assertEquals("P", dto.getNombreComercial());
        assertEquals(0, dto.getStockActual());
        assertEquals(BigDecimal.ZERO, dto.getCostoPromedioPonderado());
    }

    @Test
    @DisplayName("Debe manejar valores null en campos permitidos")
    void testValoresNull() {
        // Given & When
        InventarioSucursalDTO dto = new InventarioSucursalDTO(
                null,
                null,
                null,
                null
        );

        // Then
        assertNotNull(dto);
        assertNull(dto.getSkuInterno());
        assertNull(dto.getNombreComercial());
        assertNull(dto.getStockActual());
        assertNull(dto.getCostoPromedioPonderado());
    }

    @Test
    @DisplayName("Debe manejar stock alto correctamente")
    void testStockAlto() {
        // Given & When
        InventarioSucursalDTO dto = new InventarioSucursalDTO(
                "SKU999",
                "Producto Stock Alto",
                999999,
                new BigDecimal("99999.99")
        );

        // Then
        assertEquals(999999, dto.getStockActual());
        assertEquals(new BigDecimal("99999.99"), dto.getCostoPromedioPonderado());
    }

    @Test
    @DisplayName("Debe manejar costos con muchos decimales")
    void testCostosDecimales() {
        // Given & When
        InventarioSucursalDTO dto = new InventarioSucursalDTO(
                "SKU123",
                "Producto Decimal",
                50,
                new BigDecimal("123.456789")
        );

        // Then
        assertEquals(new BigDecimal("123.456789"), dto.getCostoPromedioPonderado());
    }

    @Test
    @DisplayName("Debe crear DTO para proyección JPA (constructor inyectable)")
    void testConstructorParaJPA() {
        // Este test simula cómo JPA crearía el DTO desde una query con proyección
        // Given
        String sku = "SKU-JPA-001";
        String nombre = "Producto desde JPA";
        Integer stock = 25;
        BigDecimal costo = new BigDecimal("75.00");

        // When
        InventarioSucursalDTO dto = new InventarioSucursalDTO(sku, nombre, stock, costo);

        // Then
        assertNotNull(dto);
        assertEquals(sku, dto.getSkuInterno());
        assertEquals(nombre, dto.getNombreComercial());
        assertEquals(stock, dto.getStockActual());
        assertEquals(costo, dto.getCostoPromedioPonderado());
    }

    @Test
    @DisplayName("Debe tener getters funcionando correctamente")
    void testGetters() {
        // Given
        InventarioSucursalDTO dto = new InventarioSucursalDTO(
                "SKU-GET",
                "Test Getters",
                42,
                new BigDecimal("42.42")
        );

        // When & Then
        assertDoesNotThrow(() -> {
            dto.getSkuInterno();
            dto.getNombreComercial();
            dto.getStockActual();
            dto.getCostoPromedioPonderado();
        });
    }
}
