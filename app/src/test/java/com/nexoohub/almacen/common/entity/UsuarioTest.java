package com.nexoohub.almacen.common.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Usuario - Tests de Entidad")
class UsuarioTest {

    @Test
    @DisplayName("Debe crear usuario con role por defecto")
    void testCrearUsuarioConDefaults() {
        Usuario usuario = new Usuario();
        
        assertEquals("ROLE_USER", usuario.getRole());
    }

    @Test
    @DisplayName("Debe establecer y obtener ID correctamente")
    void testGetSetId() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        
        assertEquals(1L, usuario.getId());
    }

    @Test
    @DisplayName("Debe establecer y obtener username correctamente")
    void testGetSetUsername() {
        Usuario usuario = new Usuario();
        usuario.setUsername("admin");
        
        assertEquals("admin", usuario.getUsername());
    }

    @Test
    @DisplayName("Debe establecer y obtener password correctamente")
    void testGetSetPassword() {
        Usuario usuario = new Usuario();
        usuario.setPassword("$2a$10$encrypted");
        
        assertEquals("$2a$10$encrypted", usuario.getPassword());
    }

    @Test
    @DisplayName("Debe establecer y obtener role correctamente")
    void testGetSetRole() {
        Usuario usuario = new Usuario();
        usuario.setRole("ROLE_ADMIN");
        
        assertEquals("ROLE_ADMIN", usuario.getRole());
    }

    @Test
    @DisplayName("Debe establecer y obtener empleado ID correctamente")
    void testGetSetEmpleadoId() {
        Usuario usuario = new Usuario();
        usuario.setEmpleadoId(5);
        
        assertEquals(5, usuario.getEmpleadoId());
    }

    @Test
    @DisplayName("Debe crear usuario completo con todos los campos")
    void testCrearUsuarioCompleto() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("gerente01");
        usuario.setPassword("$2a$10$hashedPassword");
        usuario.setRole("ROLE_MANAGER");
        usuario.setEmpleadoId(10);
        
        assertAll("usuario",
            () -> assertEquals(1L, usuario.getId()),
            () -> assertEquals("gerente01", usuario.getUsername()),
            () -> assertEquals("$2a$10$hashedPassword", usuario.getPassword()),
            () -> assertEquals("ROLE_MANAGER", usuario.getRole()),
            () -> assertEquals(10, usuario.getEmpleadoId())
        );
    }
}
