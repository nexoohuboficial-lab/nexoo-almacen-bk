package com.nexoohub.almacen.catalogo.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Proveedor - Tests de Entidad")
class ProveedorTest {

    @Test
    @DisplayName("Debe crear proveedor con ID")
    void testCrearProveedorConId() {
        // Given & When
        Proveedor proveedor = new Proveedor();
        proveedor.setId(1);

        // Then
        assertEquals(1, proveedor.getId());
    }

    @Test
    @DisplayName("Debe establecer y obtener nombreEmpresa")
    void testNombreEmpresa() {
        // Given
        Proveedor proveedor = new Proveedor();

        // When
        proveedor.setNombreEmpresa("Distribuidora ABC S.A.");

        // Then
        assertEquals("Distribuidora ABC S.A.", proveedor.getNombreEmpresa());
    }

    @Test
    @DisplayName("Debe establecer y obtener RFC")
    void testRfc() {
        // Given
        Proveedor proveedor = new Proveedor();

        // When
        proveedor.setRfc("ABC123456XYZ");

        // Then
        assertEquals("ABC123456XYZ", proveedor.getRfc());
    }

    @Test
    @DisplayName("Debe establecer y obtener nombreContacto")
    void testNombreContacto() {
        // Given
        Proveedor proveedor = new Proveedor();

        // When
        proveedor.setNombreContacto("Juan Pérez");

        // Then
        assertEquals("Juan Pérez", proveedor.getNombreContacto());
    }

    @Test
    @DisplayName("Debe establecer y obtener telefono")
    void testTelefono() {
        // Given
        Proveedor proveedor = new Proveedor();

        // When
        proveedor.setTelefono("555-1234");

        // Then
        assertEquals("555-1234", proveedor.getTelefono());
    }

    @Test
    @DisplayName("Debe establecer y obtener email")
    void testEmail() {
        // Given
        Proveedor proveedor = new Proveedor();

        // When
        proveedor.setEmail("contacto@distribuidora.com");

        // Then
        assertEquals("contacto@distribuidora.com", proveedor.getEmail());
    }

    @Test
    @DisplayName("Debe establecer y obtener direccion")
    void testDireccion() {
        // Given
        Proveedor proveedor = new Proveedor();

        // When
        proveedor.setDireccion("Av. Principal 123, Col. Centro");

        // Then
        assertEquals("Av. Principal 123, Col. Centro", proveedor.getDireccion());
    }

    @Test
    @DisplayName("Debe crear proveedor completo con todos los campos")
    void testProveedorCompleto() {
        // Given & When
        Proveedor proveedor = new Proveedor();
        proveedor.setId(100);
        proveedor.setNombreEmpresa("Autopartes del Norte S.A.");
        proveedor.setRfc("ADN890123ABC");
        proveedor.setNombreContacto("María González");
        proveedor.setTelefono("555-9876");
        proveedor.setEmail("ventas@autopartesdelnorte.com");
        proveedor.setDireccion("Calle Industrial 456, Zona Norte");

        // Then
        assertNotNull(proveedor);
        assertEquals(100, proveedor.getId());
        assertEquals("Autopartes del Norte S.A.", proveedor.getNombreEmpresa());
        assertEquals("ADN890123ABC", proveedor.getRfc());
        assertEquals("María González", proveedor.getNombreContacto());
        assertEquals("555-9876", proveedor.getTelefono());
        assertEquals("ventas@autopartesdelnorte.com", proveedor.getEmail());
        assertEquals("Calle Industrial 456, Zona Norte", proveedor.getDireccion());
    }

    @Test
    @DisplayName("Debe permitir valores null en campos opcionales")
    void testValoresNull() {
        // Given & When
        Proveedor proveedor = new Proveedor();
        proveedor.setNombreEmpresa("Empresa Test");
        proveedor.setRfc(null);
        proveedor.setNombreContacto(null);
        proveedor.setTelefono(null);
        proveedor.setEmail(null);
        proveedor.setDireccion(null);

        // Then
        assertEquals("Empresa Test", proveedor.getNombreEmpresa());
        assertNull(proveedor.getRfc());
        assertNull(proveedor.getNombreContacto());
        assertNull(proveedor.getTelefono());
        assertNull(proveedor.getEmail());
        assertNull(proveedor.getDireccion());
    }

    @Test
    @DisplayName("Debe crear instancia sin valores por defecto")
    void testInstanciaPorDefecto() {
        // Given & When
        Proveedor proveedor = new Proveedor();

        // Then
        assertNotNull(proveedor);
        assertNull(proveedor.getId());
        assertNull(proveedor.getNombreEmpresa());
        assertNull(proveedor.getRfc());
        assertNull(proveedor.getNombreContacto());
        assertNull(proveedor.getTelefono());
        assertNull(proveedor.getEmail());
        assertNull(proveedor.getDireccion());
    }
}
