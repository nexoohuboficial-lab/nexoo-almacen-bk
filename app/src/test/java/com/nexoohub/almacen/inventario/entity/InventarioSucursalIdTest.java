package com.nexoohub.almacen.inventario.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("InventarioSucursalId - Tests de Clave Compuesta")
class InventarioSucursalIdTest {

    @Test
    @DisplayName("Debe crear ID con constructor vacío")
    void testConstructorVacio() {
        // Given & When
        InventarioSucursalId id = new InventarioSucursalId();

        // Then
        assertNotNull(id);
        assertNull(id.getSucursalId());
        assertNull(id.getSkuInterno());
    }

    @Test
    @DisplayName("Debe crear ID con constructor parametrizado")
    void testConstructorParametrizado() {
        // Given & When
        InventarioSucursalId id = new InventarioSucursalId(5, "SKU-001");

        // Then
        assertNotNull(id);
        assertEquals(5, id.getSucursalId());
        assertEquals("SKU-001", id.getSkuInterno());
    }

    @Test
    @DisplayName("Debe establecer y obtener sucursalId")
    void testSucursalId() {
        // Given
        InventarioSucursalId id = new InventarioSucursalId();

        // When
        id.setSucursalId(3);

        // Then
        assertEquals(3, id.getSucursalId());
    }

    @Test
    @DisplayName("Debe establecer y obtener skuInterno")
    void testSkuInterno() {
        // Given
        InventarioSucursalId id = new InventarioSucursalId();

        // When
        id.setSkuInterno("SKU-TEST-123");

        // Then
        assertEquals("SKU-TEST-123", id.getSkuInterno());
    }

    @Test
    @DisplayName("Debe ser igual cuando ambos campos coinciden")
    void testEqualsIguales() {
        // Given
        InventarioSucursalId id1 = new InventarioSucursalId(1, "SKU-A");
        InventarioSucursalId id2 = new InventarioSucursalId(1, "SKU-A");

        // Then
        assertEquals(id1, id2);
    }

    @Test
    @DisplayName("No debe ser igual cuando sucursalId difiere")
    void testEqualsDiferenteSucursal() {
        // Given
        InventarioSucursalId id1 = new InventarioSucursalId(1, "SKU-A");
        InventarioSucursalId id2 = new InventarioSucursalId(2, "SKU-A");

        // Then
        assertNotEquals(id1, id2);
    }

    @Test
    @DisplayName("No debe ser igual cuando skuInterno difiere")
    void testEqualsDiferenteSku() {
        // Given
        InventarioSucursalId id1 = new InventarioSucursalId(1, "SKU-A");
        InventarioSucursalId id2 = new InventarioSucursalId(1, "SKU-B");

        // Then
        assertNotEquals(id1, id2);
    }

    @Test
    @DisplayName("Debe ser igual a sí mismo")
    void testEqualsReflexivo() {
        // Given
        InventarioSucursalId id = new InventarioSucursalId(1, "SKU-A");

        // Then
        assertEquals(id, id);
    }

    @Test
    @DisplayName("No debe ser igual a null")
    void testEqualsNull() {
        // Given
        InventarioSucursalId id = new InventarioSucursalId(1, "SKU-A");

        // Then
        assertNotEquals(id, null);
    }

    @Test
    @DisplayName("No debe ser igual a objeto de otra clase")
    void testEqualsOtraClase() {
        // Given
        InventarioSucursalId id = new InventarioSucursalId(1, "SKU-A");
        String otro = "algo";

        // Then
        assertNotEquals(id, otro);
    }

    @Test
    @DisplayName("Debe generar mismo hashCode para objetos iguales")
    void testHashCodeIguales() {
        // Given
        InventarioSucursalId id1 = new InventarioSucursalId(1, "SKU-A");
        InventarioSucursalId id2 = new InventarioSucursalId(1, "SKU-A");

        // Then
        assertEquals(id1.hashCode(), id2.hashCode());
    }

    @Test
    @DisplayName("Debe generar diferente hashCode para objetos diferentes")
    void testHashCodeDiferentes() {
        // Given
        InventarioSucursalId id1 = new InventarioSucursalId(1, "SKU-A");
        InventarioSucursalId id2 = new InventarioSucursalId(2, "SKU-B");

        // Then
        assertNotEquals(id1.hashCode(), id2.hashCode());
    }

    @Test
    @DisplayName("Debe crear ID completo con setters")
    void testIdCompletoConSetters() {
        // Given & When
        InventarioSucursalId id = new InventarioSucursalId();
        id.setSucursalId(10);
        id.setSkuInterno("SKU-PRODUCTO-500");

        // Then
        assertEquals(10, id.getSucursalId());
        assertEquals("SKU-PRODUCTO-500", id.getSkuInterno());
    }
}
