package com.nexoohub.almacen.inventario.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ProductoResumenDTO - Tests de DTO")
class ProductoResumenDTOTest {

    @Test
    @DisplayName("Debe crear ProductoResumenDTO con todos los campos")
    void testCrearConTodosLosCampos() {
        // Given & When
        ProductoResumenDTO dto = new ProductoResumenDTO(
            "SKU-001",
            "Filtro de Aceite",
            "Aceites y Lubricantes",
            new BigDecimal("150.00"),
            50,
            "NORMAL"
        );

        // Then
        assertNotNull(dto);
        assertEquals("SKU-001", dto.getSkuInterno());
        assertEquals("Filtro de Aceite", dto.getNombreComercial());
        assertEquals("Aceites y Lubricantes", dto.getCategoriaNombre());
        assertEquals(new BigDecimal("150.00"), dto.getPrecioVenta());
        assertEquals(50, dto.getStockActual());
        assertEquals("NORMAL", dto.getSensibilidad());
    }

    @Test
    @DisplayName("Debe acceder a skuInterno")
    void testAccederSkuInterno() {
        // Given
        ProductoResumenDTO dto = new ProductoResumenDTO(
            "SKU-ABC-123", "Producto Test", "Categoria", 
            BigDecimal.ZERO, 0, "NORMAL"
        );

        // When
        String sku = dto.getSkuInterno();

        // Then
        assertEquals("SKU-ABC-123", sku);
    }

    @Test
    @DisplayName("Debe acceder a nombreComercial")
    void testAccederNombreComercial() {
        // Given
        ProductoResumenDTO dto = new ProductoResumenDTO(
            "SKU-001", "Bujía NGK", "Refacciones", 
            BigDecimal.ZERO, 0, "NORMAL"
        );

        // When
        String nombre = dto.getNombreComercial();

        // Then
        assertEquals("Bujía NGK", nombre);
    }

    @Test
    @DisplayName("Debe acceder a categoriaNombre")
    void testAccederCategoriaNombre() {
        // Given
        ProductoResumenDTO dto = new ProductoResumenDTO(
            "SKU-001", "Producto", "Frenos y Suspensión", 
            BigDecimal.ZERO, 0, "NORMAL"
        );

        // When
        String categoria = dto.getCategoriaNombre();

        // Then
        assertEquals("Frenos y Suspensión", categoria);
    }

    @Test
    @DisplayName("Debe acceder a precioVenta")
    void testAccederPrecioVenta() {
        // Given
        BigDecimal precio = new BigDecimal("299.99");
        ProductoResumenDTO dto = new ProductoResumenDTO(
            "SKU-001", "Producto", "Categoria", 
            precio, 10, "NORMAL"
        );

        // When
        BigDecimal precioVenta = dto.getPrecioVenta();

        // Then
        assertEquals(precio, precioVenta);
    }

    @Test
    @DisplayName("Debe acceder a stockActual")
    void testAccederStockActual() {
        // Given
        ProductoResumenDTO dto = new ProductoResumenDTO(
            "SKU-001", "Producto", "Categoria", 
            BigDecimal.ZERO, 100, "NORMAL"
        );

        // When
        Integer stock = dto.getStockActual();

        // Then
        assertEquals(100, stock);
    }

    @Test
    @DisplayName("Debe acceder a sensibilidad")
    void testAccederSensibilidad() {
        // Given
        ProductoResumenDTO dto = new ProductoResumenDTO(
            "SKU-001", "Producto", "Categoria", 
            BigDecimal.ZERO, 0, "ALTA"
        );

        // When
        String sensibilidad = dto.getSensibilidad();

        // Then
        assertEquals("ALTA", sensibilidad);
    }

    @Test
    @DisplayName("Debe crear DTO con diferentes sensibilidades")
    void testDiferentesSensibilidades() {
        // Given
        ProductoResumenDTO dto1 = new ProductoResumenDTO(
            "SKU-1", "Prod1", "Cat", BigDecimal.ZERO, 0, "BAJA"
        );
        ProductoResumenDTO dto2 = new ProductoResumenDTO(
            "SKU-2", "Prod2", "Cat", BigDecimal.ZERO, 0, "NORMAL"
        );
        ProductoResumenDTO dto3 = new ProductoResumenDTO(
            "SKU-3", "Prod3", "Cat", BigDecimal.ZERO, 0, "ALTA"
        );

        // Then
        assertEquals("BAJA", dto1.getSensibilidad());
        assertEquals("NORMAL", dto2.getSensibilidad());
        assertEquals("ALTA", dto3.getSensibilidad());
    }

    @Test
    @DisplayName("Debe manejar stock cero")
    void testStockCero() {
        // Given
        ProductoResumenDTO dto = new ProductoResumenDTO(
            "SKU-001", "Producto Agotado", "Categoria", 
            new BigDecimal("100.00"), 0, "NORMAL"
        );

        // Then
        assertEquals(0, dto.getStockActual());
    }

    @Test
    @DisplayName("Debe manejar precios con decimales")
    void testPreciosConDecimales() {
        // Given
        ProductoResumenDTO dto = new ProductoResumenDTO(
            "SKU-001", "Producto", "Categoria", 
            new BigDecimal("99.99"), 10, "NORMAL"
        );

        // Then
        assertEquals(new BigDecimal("99.99"), dto.getPrecioVenta());
    }

    @Test
    @DisplayName("Debe crear múltiples productos resumen")
    void testMultiplesProductos() {
        // Given
        ProductoResumenDTO p1 = new ProductoResumenDTO(
            "SKU-A", "Producto A", "Cat A", 
            new BigDecimal("100.00"), 50, "NORMAL"
        );
        ProductoResumenDTO p2 = new ProductoResumenDTO(
            "SKU-B", "Producto B", "Cat B", 
            new BigDecimal("200.00"), 30, "ALTA"
        );

        // Then
        assertNotEquals(p1.getSkuInterno(), p2.getSkuInterno());
        assertNotEquals(p1.getNombreComercial(), p2.getNombreComercial());
        assertNotEquals(p1.getPrecioVenta(), p2.getPrecioVenta());
    }

    @Test
    @DisplayName("Debe manejar valores null en constructor")
    void testValoresNull() {
        // Given & When
        ProductoResumenDTO dto = new ProductoResumenDTO(
            null, null, null, null, null, null
        );

        // Then
        assertNull(dto.getSkuInterno());
        assertNull(dto.getNombreComercial());
        assertNull(dto.getCategoriaNombre());
        assertNull(dto.getPrecioVenta());
        assertNull(dto.getStockActual());
        assertNull(dto.getSensibilidad());
    }

    @Test
    @DisplayName("Debe representar producto con stock alto")
    void testProductoStockAlto() {
        // Given
        ProductoResumenDTO dto = new ProductoResumenDTO(
            "SKU-123", "Producto Popular", "Categoria", 
            new BigDecimal("50.00"), 1000, "NORMAL"
        );

        // Then
        assertTrue(dto.getStockActual() > 100);
    }
}
