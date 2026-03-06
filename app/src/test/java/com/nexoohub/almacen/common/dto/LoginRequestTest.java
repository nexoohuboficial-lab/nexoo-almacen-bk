package com.nexoohub.almacen.common.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("LoginRequest - Tests de DTO (Record)")
class LoginRequestTest {

    @Test
    @DisplayName("Debe crear LoginRequest con username y password")
    void testCrearLoginRequest() {
        // Given & When
        LoginRequest request = new LoginRequest("admin", "password123");

        // Then
        assertNotNull(request);
        assertEquals("admin", request.username());
        assertEquals("password123", request.password());
    }

    @Test
    @DisplayName("Debe acceder a username")
    void testAccederUsername() {
        // Given
        LoginRequest request = new LoginRequest("usuario1", "clave123");

        // When
        String username = request.username();

        // Then
        assertEquals("usuario1", username);
    }

    @Test
    @DisplayName("Debe acceder a password")
    void testAccederPassword() {
        // Given
        LoginRequest request = new LoginRequest("admin", "securePass");

        // When
        String password = request.password();

        // Then
        assertEquals("securePass", password);
    }

    @Test
    @DisplayName("Debe manejar diferentes usuarios")
    void testDiferentesUsuarios() {
        // Given
        LoginRequest r1 = new LoginRequest("admin", "pass1");
        LoginRequest r2 = new LoginRequest("vendedor", "pass2");
        LoginRequest r3 = new LoginRequest("gerente", "pass3");

        // Then
        assertEquals("admin", r1.username());
        assertEquals("vendedor", r2.username());
        assertEquals("gerente", r3.username());
    }

    @Test
    @DisplayName("Debe ser inmutable (characteristic de records)")
    void testInmutabilidad() {
        // Given
        LoginRequest request = new LoginRequest("usuario", "password");

        // When - intentar "cambiar" valores crea nueva instancia
        LoginRequest otroRequest = new LoginRequest("otroUsuario", "otraPassword");

        // Then
        assertEquals("usuario", request.username());
        assertEquals("otroUsuario", otroRequest.username());
        assertNotEquals(request.username(), otroRequest.username());
    }

    @Test
    @DisplayName("Dos LoginRequest con mismos valores deben ser iguales")
    void testEqualsConMismosValores() {
        // Given
        LoginRequest r1 = new LoginRequest("admin", "password");
        LoginRequest r2 = new LoginRequest("admin", "password");

        // Then
        assertEquals(r1, r2);
    }

    @Test
    @DisplayName("Dos LoginRequest con diferentes valores no deben ser iguales")
    void testEqualsConDiferentesValores() {
        // Given
        LoginRequest r1 = new LoginRequest("admin", "password");
        LoginRequest r2 = new LoginRequest("user", "password");

        // Then
        assertNotEquals(r1, r2);
    }

    @Test
    @DisplayName("Debe generar mismo hashCode para mismos valores")
    void testHashCode() {
        // Given
        LoginRequest r1 = new LoginRequest("admin", "password");
        LoginRequest r2 = new LoginRequest("admin", "password");

        // Then
        assertEquals(r1.hashCode(), r2.hashCode());
    }

    @Test
    @DisplayName("Debe generar toString legible")
    void testToString() {
        // Given
        LoginRequest request = new LoginRequest("admin", "password");

        // When
        String resultado = request.toString();

        // Then
        assertTrue(resultado.contains("admin"));
        assertTrue(resultado.contains("LoginRequest"));
    }

    @Test
    @DisplayName("Debe permitir valores null")
    void testValoresNull() {
        // Given & When
        LoginRequest request = new LoginRequest(null, null);

        // Then
        assertNull(request.username());
        assertNull(request.password());
    }

    @Test
    @DisplayName("Debe manejar strings vacíos")
    void testStringsVacios() {
        // Given & When
        LoginRequest request = new LoginRequest("", "");

        // Then
        assertEquals("", request.username());
        assertEquals("", request.password());
    }

    @Test
    @DisplayName("Debe crear request con credenciales válidas")
    void testCredencialesValidas() {
        // Given & When
        LoginRequest request = new LoginRequest("admin@empresa.com", "P@ssw0rd123!");

        // Then
        assertNotNull(request);
        assertTrue(request.username().contains("@"));
        assertTrue(request.password().length() > 8);
    }
}
