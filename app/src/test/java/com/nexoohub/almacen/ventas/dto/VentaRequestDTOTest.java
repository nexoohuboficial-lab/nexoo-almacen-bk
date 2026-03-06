package com.nexoohub.almacen.ventas.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("VentaRequestDTO - Tests de DTO")
class VentaRequestDTOTest {

    @Test
    @DisplayName("Debe crear VentaRequestDTO vacío")
    void testCrearVacio() {
        // Given & When
        VentaRequestDTO dto = new VentaRequestDTO();

        // Then
        assertNotNull(dto);
        assertNull(dto.getClienteId());
        assertNull(dto.getSucursalId());
        assertNull(dto.getMetodoPago());
        assertNull(dto.getItems());
    }

    @Test
    @DisplayName("Debe establecer y obtener clienteId")
    void testClienteId() {
        // Given
        VentaRequestDTO dto = new VentaRequestDTO();

        // When
        dto.setClienteId(10);

        // Then
        assertEquals(10, dto.getClienteId());
    }

    @Test
    @DisplayName("Debe establecer y obtener sucursalId")
    void testSucursalId() {
        // Given
        VentaRequestDTO dto = new VentaRequestDTO();

        // When
        dto.setSucursalId(5);

        // Then
        assertEquals(5, dto.getSucursalId());
    }

    @Test
    @DisplayName("Debe establecer y obtener metodoPago")
    void testMetodoPago() {
        // Given
        VentaRequestDTO dto = new VentaRequestDTO();

        // When
        dto.setMetodoPago("EFECTIVO");

        // Then
        assertEquals("EFECTIVO", dto.getMetodoPago());
    }

    @Test
    @DisplayName("Debe manejar diferentes métodos de pago")
    void testDiferentesMetodosPago() {
        // Given
        VentaRequestDTO dto1 = new VentaRequestDTO();
        VentaRequestDTO dto2 = new VentaRequestDTO();
        VentaRequestDTO dto3 = new VentaRequestDTO();

        // When
        dto1.setMetodoPago("EFECTIVO");
        dto2.setMetodoPago("TARJETA");
        dto3.setMetodoPago("TRANSFERENCIA");

        // Then
        assertEquals("EFECTIVO", dto1.getMetodoPago());
        assertEquals("TARJETA", dto2.getMetodoPago());
        assertEquals("TRANSFERENCIA", dto3.getMetodoPago());
    }

    @Test
    @DisplayName("Debe establecer y obtener lista de items")
    void testItems() {
        // Given
        VentaRequestDTO dto = new VentaRequestDTO();
        List<VentaRequestDTO.ItemVentaDTO> items = new ArrayList<>();

        // When
        dto.setItems(items);

        // Then
        assertNotNull(dto.getItems());
        assertEquals(items, dto.getItems());
    }

    @Test
    @DisplayName("Debe crear venta request completa")
    void testVentaCompleta() {
        // Given
        VentaRequestDTO dto = new VentaRequestDTO();
        List<VentaRequestDTO.ItemVentaDTO> items = new ArrayList<>();

        // When
        dto.setClienteId(100);
        dto.setSucursalId(3);
        dto.setMetodoPago("TARJETA");
        dto.setItems(items);

        // Then
        assertEquals(100, dto.getClienteId());
        assertEquals(3, dto.getSucursalId());
        assertEquals("TARJETA", dto.getMetodoPago());
        assertNotNull(dto.getItems());
    }

    // Tests para ItemVentaDTO (clase interna)
    
    @Test
    @DisplayName("Debe crear ItemVentaDTO vacío")
    void testCrearItemVacio() {
        // Given & When
        VentaRequestDTO.ItemVentaDTO item = new VentaRequestDTO.ItemVentaDTO();

        // Then
        assertNotNull(item);
        assertNull(item.getSkuInterno());
        assertNull(item.getCantidad());
    }

    @Test
    @DisplayName("Debe establecer y obtener skuInterno en item")
    void testItemSkuInterno() {
        // Given
        VentaRequestDTO.ItemVentaDTO item = new VentaRequestDTO.ItemVentaDTO();

        // When
        item.setSkuInterno("SKU-001");

        // Then
        assertEquals("SKU-001", item.getSkuInterno());
    }

    @Test
    @DisplayName("Debe establecer y obtener cantidad en item")
    void testItemCantidad() {
        // Given
        VentaRequestDTO.ItemVentaDTO item = new VentaRequestDTO.ItemVentaDTO();

        // When
        item.setCantidad(5);

        // Then
        assertEquals(5, item.getCantidad());
    }

    @Test
    @DisplayName("Debe crear item completo")
    void testItemCompleto() {
        // Given
        VentaRequestDTO.ItemVentaDTO item = new VentaRequestDTO.ItemVentaDTO();

        // When
        item.setSkuInterno("SKU-PROD-123");
        item.setCantidad(10);

        // Then
        assertEquals("SKU-PROD-123", item.getSkuInterno());
        assertEquals(10, item.getCantidad());
    }

    @Test
    @DisplayName("Debe agregar múltiples items a la venta")
    void testMultiplesItems() {
        // Given
        VentaRequestDTO dto = new VentaRequestDTO();
        List<VentaRequestDTO.ItemVentaDTO> items = new ArrayList<>();

        VentaRequestDTO.ItemVentaDTO item1 = new VentaRequestDTO.ItemVentaDTO();
        item1.setSkuInterno("SKU-A");
        item1.setCantidad(2);

        VentaRequestDTO.ItemVentaDTO item2 = new VentaRequestDTO.ItemVentaDTO();
        item2.setSkuInterno("SKU-B");
        item2.setCantidad(3);

        items.add(item1);
        items.add(item2);

        // When
        dto.setItems(items);

        // Then
        assertEquals(2, dto.getItems().size());
        assertEquals("SKU-A", dto.getItems().get(0).getSkuInterno());
        assertEquals("SKU-B", dto.getItems().get(1).getSkuInterno());
    }

    @Test
    @DisplayName("Debe manejar lista de items vacía")
    void testListaItemsVacia() {
        // Given
        VentaRequestDTO dto = new VentaRequestDTO();
        List<VentaRequestDTO.ItemVentaDTO> items = new ArrayList<>();

        // When
        dto.setItems(items);

        // Then
        assertTrue(dto.getItems().isEmpty());
    }
}
