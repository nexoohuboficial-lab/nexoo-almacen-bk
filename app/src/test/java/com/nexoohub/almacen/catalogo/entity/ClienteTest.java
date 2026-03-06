package com.nexoohub.almacen.catalogo.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Cliente - Tests de Entidad")
class ClienteTest {

    @Test
    @DisplayName("Debe establecer y obtener ID correctamente")
    void testGetSetId() {
        Cliente cliente = new Cliente();
        cliente.setId(1);
        
        assertEquals(1, cliente.getId());
    }

    @Test
    @DisplayName("Debe establecer y obtener tipo de cliente ID correctamente")
    void testGetSetTipoClienteId() {
        Cliente cliente = new Cliente();
        cliente.setTipoClienteId(2);
        
        assertEquals(2, cliente.getTipoClienteId());
    }

    @Test
    @DisplayName("Debe establecer y obtener nombre correctamente")
    void testGetSetNombre() {
        Cliente cliente = new Cliente();
        cliente.setNombre("Juan Pérez");
        
        assertEquals("Juan Pérez", cliente.getNombre());
    }

    @Test
    @DisplayName("Debe establecer y obtener RFC correctamente")
    void testGetSetRfc() {
        Cliente cliente = new Cliente();
        cliente.setRfc("XAXX010101000");
        
        assertEquals("XAXX010101000", cliente.getRfc());
    }

    @Test
    @DisplayName("Debe establecer y obtener teléfono correctamente")
    void testGetSetTelefono() {
        Cliente cliente = new Cliente();
        cliente.setTelefono("5512345678");
        
        assertEquals("5512345678", cliente.getTelefono());
    }

    @Test
    @DisplayName("Debe establecer y obtener email correctamente")
    void testGetSetEmail() {
        Cliente cliente = new Cliente();
        cliente.setEmail("juan@example.com");
        
        assertEquals("juan@example.com", cliente.getEmail());
    }

    @Test
    @DisplayName("Debe establecer y obtener dirección fiscal correctamente")
    void testGetSetDireccionFiscal() {
        Cliente cliente = new Cliente();
        cliente.setDireccionFiscal("Calle Principal 123");
        
        assertEquals("Calle Principal 123", cliente.getDireccionFiscal());
    }

    @Test
    @DisplayName("Debe crear cliente completo con todos los campos")
    void testCrearClienteCompleto() {
        Cliente cliente = new Cliente();
        cliente.setId(1);
        cliente.setTipoClienteId(2);
        cliente.setNombre("María González");
        cliente.setRfc("XAXX010101000");
        cliente.setTelefono("5587654321");
        cliente.setEmail("maria@example.com");
        cliente.setDireccionFiscal("Av. Reforma 456");
        
        assertAll("cliente",
            () -> assertEquals(1, cliente.getId()),
            () -> assertEquals(2, cliente.getTipoClienteId()),
            () -> assertEquals("María González", cliente.getNombre()),
            () -> assertEquals("XAXX010101000", cliente.getRfc()),
            () -> assertEquals("5587654321", cliente.getTelefono()),
            () -> assertEquals("maria@example.com", cliente.getEmail()),
            () -> assertEquals("Av. Reforma 456", cliente.getDireccionFiscal())
        );
    }
}
