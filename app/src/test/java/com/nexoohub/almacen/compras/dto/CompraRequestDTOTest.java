package com.nexoohub.almacen.compras.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CompraRequestDTO - Tests de DTO")
class CompraRequestDTOTest {

    @Test
    @DisplayName("Debe crear CompraRequestDTO vacío")
    void testCrearVacio() {
        // Given & When
        CompraRequestDTO dto = new CompraRequestDTO();

        // Then
        assertNotNull(dto);
        assertNull(dto.getProveedorId());
        assertNull(dto.getFolioFactura());
        assertNull(dto.getPreciosIncluyenIva());
        assertNull(dto.getSucursalDestinoId());
        assertNull(dto.getDetalles());
    }

    @Test
    @DisplayName("Debe establecer y obtener proveedorId")
    void testProveedorId() {
        // Given
        CompraRequestDTO dto = new CompraRequestDTO();

        // When
        dto.setProveedorId(15);

        // Then
        assertEquals(15, dto.getProveedorId());
    }

    @Test
    @DisplayName("Debe establecer y obtener folioFactura")
    void testFolioFactura() {
        // Given
        CompraRequestDTO dto = new CompraRequestDTO();

        // When
        dto.setFolioFactura("FACT-2024-001");

        // Then
        assertEquals("FACT-2024-001", dto.getFolioFactura());
    }

    @Test
    @DisplayName("Debe establecer y obtener preciosIncluyenIva")
    void testPreciosIncluyenIva() {
        // Given
        CompraRequestDTO dto = new CompraRequestDTO();

        // When
        dto.setPreciosIncluyenIva(true);

        // Then
        assertTrue(dto.getPreciosIncluyenIva());
    }

    @Test
    @DisplayName("Debe manejar preciosIncluyenIva false")
    void testPreciosSinIva() {
        // Given
        CompraRequestDTO dto = new CompraRequestDTO();

        // When
        dto.setPreciosIncluyenIva(false);

        // Then
        assertFalse(dto.getPreciosIncluyenIva());
    }

    @Test
    @DisplayName("Debe establecer y obtener sucursalDestinoId")
    void testSucursalDestinoId() {
        // Given
        CompraRequestDTO dto = new CompraRequestDTO();

        // When
        dto.setSucursalDestinoId(3);

        // Then
        assertEquals(3, dto.getSucursalDestinoId());
    }

    @Test
    @DisplayName("Debe establecer y obtener detalles")
    void testDetalles() {
        // Given
        CompraRequestDTO dto = new CompraRequestDTO();
        List<CompraRequestDTO.DetalleItemDTO> detalles = new ArrayList<>();

        // When
        dto.setDetalles(detalles);

        // Then
        assertNotNull(dto.getDetalles());
        assertEquals(detalles, dto.getDetalles());
    }

    @Test
    @DisplayName("Debe crear compra request completa")
    void testCompraCompleta() {
        // Given
        CompraRequestDTO dto = new CompraRequestDTO();
        List<CompraRequestDTO.DetalleItemDTO> detalles = new ArrayList<>();

        // When
        dto.setProveedorId(5);
        dto.setFolioFactura("F-2024-500");
        dto.setPreciosIncluyenIva(true);
        dto.setSucursalDestinoId(2);
        dto.setDetalles(detalles);

        // Then
        assertEquals(5, dto.getProveedorId());
        assertEquals("F-2024-500", dto.getFolioFactura());
        assertTrue(dto.getPreciosIncluyenIva());
        assertEquals(2, dto.getSucursalDestinoId());
        assertNotNull(dto.getDetalles());
    }

    // Tests para DetalleItemDTO (clase interna)

    @Test
    @DisplayName("Debe crear DetalleItemDTO vacío")
    void testCrearDetalleVacio() {
        // Given & When
        CompraRequestDTO.DetalleItemDTO detalle = new CompraRequestDTO.DetalleItemDTO();

        // Then
        assertNotNull(detalle);
        assertNull(detalle.getSkuInterno());
        assertNull(detalle.getCantidad());
        assertNull(detalle.getCostoUnitario());
        assertNull(detalle.getPrecioPublicoProveedor());
    }

    @Test
    @DisplayName("Debe establecer y obtener skuInterno en detalle")
    void testDetalleSkuInterno() {
        // Given
        CompraRequestDTO.DetalleItemDTO detalle = new CompraRequestDTO.DetalleItemDTO();

        // When
        detalle.setSkuInterno("SKU-PROD-001");

        // Then
        assertEquals("SKU-PROD-001", detalle.getSkuInterno());
    }

    @Test
    @DisplayName("Debe establecer y obtener cantidad en detalle")
    void testDetalleCantidad() {
        // Given
        CompraRequestDTO.DetalleItemDTO detalle = new CompraRequestDTO.DetalleItemDTO();

        // When
        detalle.setCantidad(50);

        // Then
        assertEquals(50, detalle.getCantidad());
    }

    @Test
    @DisplayName("Debe establecer y obtener costoUnitario")
    void testDetalleCostoUnitario() {
        // Given
        CompraRequestDTO.DetalleItemDTO detalle = new CompraRequestDTO.DetalleItemDTO();
        BigDecimal costo = new BigDecimal("85.50");

        // When
        detalle.setCostoUnitario(costo);

        // Then
        assertEquals(costo, detalle.getCostoUnitario());
    }

    @Test
    @DisplayName("Debe establecer y obtener precioPublicoProveedor")
    void testDetallePrecioPublicoProveedor() {
        // Given
        CompraRequestDTO.DetalleItemDTO detalle = new CompraRequestDTO.DetalleItemDTO();
        BigDecimal precio = new BigDecimal("120.00");

        // When
        detalle.setPrecioPublicoProveedor(precio);

        // Then
        assertEquals(precio, detalle.getPrecioPublicoProveedor());
    }

    @Test
    @DisplayName("Debe crear detalle completo")
    void testDetalleCompleto() {
        // Given
        CompraRequestDTO.DetalleItemDTO detalle = new CompraRequestDTO.DetalleItemDTO();

        // When
        detalle.setSkuInterno("SKU-ABC-123");
        detalle.setCantidad(100);
        detalle.setCostoUnitario(new BigDecimal("50.00"));
        detalle.setPrecioPublicoProveedor(new BigDecimal("75.00"));

        // Then
        assertEquals("SKU-ABC-123", detalle.getSkuInterno());
        assertEquals(100, detalle.getCantidad());
        assertEquals(new BigDecimal("50.00"), detalle.getCostoUnitario());
        assertEquals(new BigDecimal("75.00"), detalle.getPrecioPublicoProveedor());
    }

    @Test
    @DisplayName("Debe permitir precioPublicoProveedor null")
    void testPrecioPublicoNull() {
        // Given
        CompraRequestDTO.DetalleItemDTO detalle = new CompraRequestDTO.DetalleItemDTO();
        detalle.setSkuInterno("SKU-001");
        detalle.setCantidad(10);
        detalle.setCostoUnitario(new BigDecimal("30.00"));

        // When
        detalle.setPrecioPublicoProveedor(null);

        // Then
        assertNull(detalle.getPrecioPublicoProveedor());
    }

    @Test
    @DisplayName("Debe agregar múltiples detalles a la compra")
    void testMultiplesDetalles() {
        // Given
        CompraRequestDTO dto = new CompraRequestDTO();
        List<CompraRequestDTO.DetalleItemDTO> detalles = new ArrayList<>();

        CompraRequestDTO.DetalleItemDTO d1 = new CompraRequestDTO.DetalleItemDTO();
        d1.setSkuInterno("SKU-A");
        d1.setCantidad(20);
        d1.setCostoUnitario(new BigDecimal("40.00"));

        CompraRequestDTO.DetalleItemDTO d2 = new CompraRequestDTO.DetalleItemDTO();
        d2.setSkuInterno("SKU-B");
        d2.setCantidad(30);
        d2.setCostoUnitario(new BigDecimal("60.00"));

        detalles.add(d1);
        detalles.add(d2);

        // When
        dto.setDetalles(detalles);

        // Then
        assertEquals(2, dto.getDetalles().size());
        assertEquals("SKU-A", dto.getDetalles().get(0).getSkuInterno());
        assertEquals("SKU-B", dto.getDetalles().get(1).getSkuInterno());
    }

    @Test
    @DisplayName("Debe manejar lista de detalles vacía")
    void testListaDetallesVacia() {
        // Given
        CompraRequestDTO dto = new CompraRequestDTO();
        List<CompraRequestDTO.DetalleItemDTO> detalles = new ArrayList<>();

        // When
        dto.setDetalles(detalles);

        // Then
        assertTrue(dto.getDetalles().isEmpty());
    }

    @Test
    @DisplayName("Debe calcular total de un detalle")
    void testCalculoTotalDetalle() {
        // Given
        CompraRequestDTO.DetalleItemDTO detalle = new CompraRequestDTO.DetalleItemDTO();
        detalle.setCantidad(10);
        detalle.setCostoUnitario(new BigDecimal("50.00"));

        // When
        BigDecimal total = detalle.getCostoUnitario()
                .multiply(new BigDecimal(detalle.getCantidad()));

        // Then
        assertEquals(new BigDecimal("500.00"), total);
    }
}
