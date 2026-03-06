package com.nexoohub.almacen.sucursal.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Sucursal - Tests de Entidad")
class SucursalTest {

    @Test
    @DisplayName("Debe crear sucursal con estado activo por defecto")
    void testCrearSucursalConDefaults() {
        Sucursal sucursal = new Sucursal();
        
        assertTrue(sucursal.getActivo());
    }

    @Test
    @DisplayName("Debe establecer y obtener ID correctamente")
    void testGetSetId() {
        Sucursal sucursal = new Sucursal();
        sucursal.setId(1);
        
        assertEquals(1, sucursal.getId());
    }

    @Test
    @DisplayName("Debe establecer y obtener nombre correctamente")
    void testGetSetNombre() {
        Sucursal sucursal = new Sucursal();
        sucursal.setNombre("Sucursal Centro");
        
        assertEquals("Sucursal Centro", sucursal.getNombre());
    }

    @Test
    @DisplayName("Debe establecer y obtener dirección correctamente")
    void testGetSetDireccion() {
        Sucursal sucursal = new Sucursal();
        sucursal.setDireccion("Av. Principal 123");
        
        assertEquals("Av. Principal 123", sucursal.getDireccion());
    }

    @Test
    @DisplayName("Debe establecer y obtener estado activo correctamente")
    void testGetSetActivo() {
        Sucursal sucursal = new Sucursal();
        sucursal.setActivo(false);
        
        assertFalse(sucursal.getActivo());
    }

    @Test
    @DisplayName("Debe crear sucursal completa con todos los campos")
    void testCrearSucursalCompleta() {
        Sucursal sucursal = new Sucursal();
        sucursal.setId(1);
        sucursal.setNombre("Sucursal Norte");
        sucursal.setDireccion("Calle Norte 456");
        sucursal.setActivo(true);
        
        assertAll("sucursal",
            () -> assertEquals(1, sucursal.getId()),
            () -> assertEquals("Sucursal Norte", sucursal.getNombre()),
            () -> assertEquals("Calle Norte 456", sucursal.getDireccion()),
            () -> assertTrue(sucursal.getActivo())
        );
    }
}
