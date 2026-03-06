package com.nexoohub.almacen.inventario.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ProductoMaestro - Tests de Entidad")
class ProductoMaestroTest {

    @Test
    @DisplayName("Debe crear producto con valores por defecto")
    void testCrearProductoConDefaults() {
        ProductoMaestro producto = new ProductoMaestro();
        
        assertEquals(2, producto.getStockMinimoGlobal());
        assertTrue(producto.getActivo());
        assertEquals("MEDIA", producto.getSensibilidadPrecio());
    }

    @Test
    @DisplayName("Debe establecer y obtener SKU interno correctamente")
    void testGetSetSkuInterno() {
        ProductoMaestro producto = new ProductoMaestro();
        producto.setSkuInterno("FILTRO-001");
        
        assertEquals("FILTRO-001", producto.getSkuInterno());
    }

    @Test
    @DisplayName("Debe establecer y obtener nombre comercial correctamente")
    void testGetSetNombreComercial() {
        ProductoMaestro producto = new ProductoMaestro();
        producto.setNombreComercial("Filtro de Aceite Premium");
        
        assertEquals("Filtro de Aceite Premium", producto.getNombreComercial());
    }

    @Test
    @DisplayName("Debe establecer y obtener descripción correctamente")
    void testGetSetDescripcion() {
        ProductoMaestro producto = new ProductoMaestro();
        producto.setDescripcion("Filtro de aceite para motos");
        
        assertEquals("Filtro de aceite para motos", producto.getDescripcion());
    }

    @Test
    @DisplayName("Debe establecer y obtener SKU proveedor correctamente")
    void testGetSetSkuProveedor() {
        ProductoMaestro producto = new ProductoMaestro();
        producto.setSkuProveedor("PROV-12345");
        
        assertEquals("PROV-12345", producto.getSkuProveedor());
    }

    @Test
    @DisplayName("Debe establecer y obtener categoría ID correctamente")
    void testGetSetCategoriaId() {
        ProductoMaestro producto = new ProductoMaestro();
        producto.setCategoriaId(5);
        
        assertEquals(5, producto.getCategoriaId());
    }

    @Test
    @DisplayName("Debe establecer y obtener proveedor ID correctamente")
    void testGetSetProveedorId() {
        ProductoMaestro producto = new ProductoMaestro();
        producto.setProveedorId(10);
        
        assertEquals(10, producto.getProveedorId());
    }

    @Test
    @DisplayName("Debe establecer y obtener clave SAT correctamente")
    void testGetSetClaveSat() {
        ProductoMaestro producto = new ProductoMaestro();
        producto.setClaveSat("12345678");
        
        assertEquals("12345678", producto.getClaveSat());
    }

    @Test
    @DisplayName("Debe establecer y obtener stock mínimo global correctamente")
    void testGetSetStockMinimoGlobal() {
        ProductoMaestro producto = new ProductoMaestro();
        producto.setStockMinimoGlobal(5);
        
        assertEquals(5, producto.getStockMinimoGlobal());
    }

    @Test
    @DisplayName("Debe establecer y obtener estado activo correctamente")
    void testGetSetActivo() {
        ProductoMaestro producto = new ProductoMaestro();
        producto.setActivo(false);
        
        assertFalse(producto.getActivo());
    }

    @Test
    @DisplayName("Debe establecer y obtener sensibilidad de precio correctamente")
    void testGetSetSensibilidadPrecio() {
        ProductoMaestro producto = new ProductoMaestro();
        producto.setSensibilidadPrecio("ALTA");
        
        assertEquals("ALTA", producto.getSensibilidadPrecio());
    }

    @Test
    @DisplayName("Debe crear producto completo con todos los campos")
    void testCrearProductoCompleto() {
        ProductoMaestro producto = new ProductoMaestro();
        producto.setSkuInterno("FILTRO-001");
        producto.setSkuProveedor("PROV-ABC");
        producto.setNombreComercial("Filtro Premium");
        producto.setDescripcion("Filtro de alta calidad");
        producto.setCategoriaId(5);
        producto.setProveedorId(10);
        producto.setClaveSat("12345678");
        producto.setStockMinimoGlobal(3);
        producto.setActivo(true);
        producto.setSensibilidadPrecio("BAJA");
        
        assertAll("producto",
            () -> assertEquals("FILTRO-001", producto.getSkuInterno()),
            () -> assertEquals("PROV-ABC", producto.getSkuProveedor()),
            () -> assertEquals("Filtro Premium", producto.getNombreComercial()),
            () -> assertEquals("Filtro de alta calidad", producto.getDescripcion()),
            () -> assertEquals(5, producto.getCategoriaId()),
            () -> assertEquals(10, producto.getProveedorId()),
            () -> assertEquals("12345678", producto.getClaveSat()),
            () -> assertEquals(3, producto.getStockMinimoGlobal()),
            () -> assertTrue(producto.getActivo()),
            () -> assertEquals("BAJA", producto.getSensibilidadPrecio())
        );
    }
}
