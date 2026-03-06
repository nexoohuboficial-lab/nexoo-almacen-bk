package com.nexoohub.almacen.common.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AuthResponse - Tests de DTO (Record)")
class AuthResponseTest {

    @Test
    @DisplayName("Debe crear AuthResponse con token")
    void testCrearAuthResponse() {
        // Given & When
        AuthResponse response = new AuthResponse("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9");

        // Then
        assertNotNull(response);
        assertEquals("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9", response.token());
    }

    @Test
    @DisplayName("Debe acceder al token")
    void testAccederToken() {
        // Given
        String tokenJWT = "abc123.def456.ghi789";
        AuthResponse response = new AuthResponse(tokenJWT);

        // When
        String token = response.token();

        // Then
        assertEquals(tokenJWT, token);
    }

    @Test
    @DisplayName("Debe manejar diferentes tokens")
    void testDiferentesTokens() {
        // Given
        AuthResponse r1 = new AuthResponse("token1");
        AuthResponse r2 = new AuthResponse("token2");
        AuthResponse r3 = new AuthResponse("token3");

        // Then
        assertEquals("token1", r1.token());
        assertEquals("token2", r2.token());
        assertEquals("token3", r3.token());
        assertNotEquals(r1.token(), r2.token());
    }

    @Test
    @DisplayName("Debe ser inmutable (characteristic de records)")
    void testInmutabilidad() {
        // Given
        AuthResponse response = new AuthResponse("originalToken");

        // When - crear nueva instancia
        AuthResponse otraResponse = new AuthResponse("nuevoToken");

        // Then
        assertEquals("originalToken", response.token());
        assertEquals("nuevoToken", otraResponse.token());
        assertNotEquals(response.token(), otraResponse.token());
    }

    @Test
    @DisplayName("Dos AuthResponse con mismo token deben ser iguales")
    void testEqualsConMismoToken() {
        // Given
        AuthResponse r1 = new AuthResponse("mismo-token-123");
        AuthResponse r2 = new AuthResponse("mismo-token-123");

        // Then
        assertEquals(r1, r2);
    }

    @Test
    @DisplayName("Dos AuthResponse con diferentes tokens no deben ser iguales")
    void testEqualsConDiferentesTokens() {
        // Given
        AuthResponse r1 = new AuthResponse("token-A");
        AuthResponse r2 = new AuthResponse("token-B");

        // Then
        assertNotEquals(r1, r2);
    }

    @Test
    @DisplayName("Debe generar mismo hashCode para mismo token")
    void testHashCode() {
        // Given
        AuthResponse r1 = new AuthResponse("token123");
        AuthResponse r2 = new AuthResponse("token123");

        // Then
        assertEquals(r1.hashCode(), r2.hashCode());
    }

    @Test
    @DisplayName("Debe generar toString legible")
    void testToString() {
        // Given
        AuthResponse response = new AuthResponse("myToken");

        // When
        String resultado = response.toString();

        // Then
        assertTrue(resultado.contains("myToken"));
        assertTrue(resultado.contains("AuthResponse"));
    }

    @Test
    @DisplayName("Debe permitir token null")
    void testTokenNull() {
        // Given & When
        AuthResponse response = new AuthResponse(null);

        // Then
        assertNull(response.token());
    }

    @Test
    @DisplayName("Debe manejar token vacío")
    void testTokenVacio() {
        // Given & When
        AuthResponse response = new AuthResponse("");

        // Then
        assertEquals("", response.token());
    }

    @Test
    @DisplayName("Debe manejar tokens JWT largos")
    void testTokenJWTLargo() {
        // Given
        String jwtLargo = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";

        // When
        AuthResponse response = new AuthResponse(jwtLargo);

        // Then
        assertEquals(jwtLargo, response.token());
        assertTrue(response.token().length() > 100);
    }

    @Test
    @DisplayName("Debe crear response con token válido formato Bearer")
    void testTokenFormatoBearer() {
        // Given
        String token = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiJ9.signature";

        // When
        AuthResponse response = new AuthResponse(token);

        // Then
        assertTrue(response.token().startsWith("Bearer"));
    }
}
