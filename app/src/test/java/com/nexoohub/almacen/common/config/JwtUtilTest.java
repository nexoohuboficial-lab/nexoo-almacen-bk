package com.nexoohub.almacen.common.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtUtil - Tests de Generación y Validación de JWT")
class JwtUtilTest {

    @Mock
    private Environment environment;

    private JwtUtil jwtUtil;
    private final String SECRET_KEY = "MySecretKeyForTestingMustBeLongEnoughForHS256Algorithm";
    private final long EXPIRATION_TIME = 86400000; // 24 horas en milisegundos

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil(environment);
        ReflectionTestUtils.setField(jwtUtil, "secret", SECRET_KEY);
        ReflectionTestUtils.setField(jwtUtil, "expirationTime", EXPIRATION_TIME);
    }

    @Test
    @DisplayName("Debe generar un token JWT válido")
    void testGenerarTokenValido() {
        // Given
        String username = "testuser";

        // When
        String token = jwtUtil.generateToken(username);

        // Then
        assertNotNull(token, "El token no debe ser null");
        assertFalse(token.isEmpty(), "El token no debe estar vacío");
        assertTrue(token.split("\\.").length == 3, "El token debe tener 3 partes separadas por punto");
    }

    @Test
    @DisplayName("Debe extraer el username correctamente del token")
    void testExtraerUsername() {
        // Given
        String username = "admin";
        String token = jwtUtil.generateToken(username);

        // When
        String extractedUsername = jwtUtil.extractUsername(token);

        // Then
        assertEquals(username, extractedUsername, "El username extraído debe coincidir");
    }

    @Test
    @DisplayName("Debe validar token correctamente para usuario válido")
    void testValidarTokenValido() {
        // Given
        String username = "vendedor1";
        String token = jwtUtil.generateToken(username);

        // When
        boolean isValid = jwtUtil.isTokenValid(token, username);

        // Then
        assertTrue(isValid, "El token debe ser válido para el usuario correcto");
    }

    @Test
    @DisplayName("Debe rechazar token para usuario diferente")
    void testValidarTokenUsuarioDiferente() {
        // Given
        String username = "vendedor1";
        String token = jwtUtil.generateToken(username);

        // When
        boolean isValid = jwtUtil.isTokenValid(token, "vendedor2");

        // Then
        assertFalse(isValid, "El token no debe ser válido para un usuario diferente");
    }

    @Test
    @DisplayName("Debe extraer fecha de expiración del token")
    void testExtraerFechaExpiracion() {
        // Given
        String username = "testuser";
        String token = jwtUtil.generateToken(username);

        // When
        Date expiration = jwtUtil.extractClaim(token, Claims::getExpiration);

        // Then
        assertNotNull(expiration, "La fecha de expiración no debe ser null");
        assertTrue(expiration.after(new Date()), "La fecha de expiración debe ser futura");
    }

    @Test
    @DisplayName("Debe extraer fecha de emisión del token")
    void testExtraerFechaEmision() {
        // Given
        String username = "testuser";
        String token = jwtUtil.generateToken(username);

        // When
        Date issuedAt = jwtUtil.extractClaim(token, Claims::getIssuedAt);

        // Then
        assertNotNull(issuedAt, "La fecha de emisión no debe ser null");
        assertTrue(issuedAt.before(new Date(System.currentTimeMillis() + 1000)),
                "La fecha de emisión debe ser reciente");
    }

    @Test
    @DisplayName("Debe generar tokens diferentes para el mismo usuario")
    void testGenerarTokensDiferentes() throws InterruptedException {
        // Given
        String username = "testuser";

        // When
        String token1 = jwtUtil.generateToken(username);
        Thread.sleep(1000); // Pausa de 1 segundo porque JWT usa epoch seconds, no milisegundos
        String token2 = jwtUtil.generateToken(username);

        // Then
        assertNotEquals(token1, token2, "Los tokens deben ser diferentes aunque sean del mismo usuario");
    }

    @Test
    @DisplayName("Debe validar correctamente múltiples usuarios")
    void testValidarMultiplesUsuarios() {
        // Given
        String user1 = "admin";
        String user2 = "vendedor";
        String token1 = jwtUtil.generateToken(user1);
        String token2 = jwtUtil.generateToken(user2);

        // When & Then
        assertTrue(jwtUtil.isTokenValid(token1, user1), "Token1 debe ser válido para user1");
        assertTrue(jwtUtil.isTokenValid(token2, user2), "Token2 debe ser válido para user2");
        assertFalse(jwtUtil.isTokenValid(token1, user2), "Token1 no debe ser válido para user2");
        assertFalse(jwtUtil.isTokenValid(token2, user1), "Token2 no debe ser válido para user1");
    }

    @Test
    @DisplayName("Debe manejar username vacío correctamente")
    void testGenerarTokenUsernameVacio() {
        // Given
        String username = "";

        // When
        String token = jwtUtil.generateToken(username);

        // Then
        assertNotNull(token, "El token debe generarse incluso con username vacío");
        // JWT puede retornar null para subject vacío, lo cual es comportamiento esperado
        String extractedUsername = jwtUtil.extractUsername(token);
        assertTrue(extractedUsername == null || extractedUsername.isEmpty(), 
                   "Username extraído debe ser null o vacío");
    }

    @Test
    @DisplayName("Debe manejar username con caracteres especiales")
    void testGenerarTokenUsernameEspecial() {
        // Given
        String username = "user@domain.com";

        // When
        String token = jwtUtil.generateToken(username);
        String extracted = jwtUtil.extractUsername(token);

        // Then
        assertEquals(username, extracted, "Debe manejar caracteres especiales correctamente");
    }

    @Test
    @DisplayName("Debe lanzar excepción con token inválido")
    void testTokenInvalido() {
        // Given
        String tokenInvalido = "token.invalido.xyz";

        // When & Then
        assertThrows(Exception.class, () -> jwtUtil.extractUsername(tokenInvalido),
                "Debe lanzar excepción con token inválido");
    }

    @Test
    @DisplayName("Debe lanzar excepción con token null")
    void testTokenNull() {
        // When & Then
        assertThrows(Exception.class, () -> jwtUtil.extractUsername(null),
                "Debe lanzar excepción con token null");
    }

    @Test
    @DisplayName("Debe detectar token expirado correctamente")
    void testDetectarTokenExpirado() {
        // Given
        JwtUtil jwtUtilExpired = new JwtUtil(environment);
        ReflectionTestUtils.setField(jwtUtilExpired, "secret", SECRET_KEY);
        ReflectionTestUtils.setField(jwtUtilExpired, "expirationTime", -1000L); // Token expirado inmediatamente

        String username = "testuser";
        String tokenExpirado = jwtUtilExpired.generateToken(username);

        // When & Then
        assertThrows(ExpiredJwtException.class, 
                () -> jwtUtilExpired.isTokenValid(tokenExpirado, username),
                "Debe detectar token expirado");
    }

    @Test
    @DisplayName("Debe calcular correctamente el tiempo de expiración")
    void testTiempoExpiracion() {
        // Given
        String username = "testuser";
        long before = System.currentTimeMillis();

        // When
        String token = jwtUtil.generateToken(username);
        Date expiration = jwtUtil.extractClaim(token, Claims::getExpiration);

        // Then
        long expectedExpiration = before + EXPIRATION_TIME;
        long actualExpiration = expiration.getTime();
        long difference = Math.abs(actualExpiration - expectedExpiration);
        
        assertTrue(difference < 1000, "La diferencia debe ser menor a 1 segundo");
    }

    @Test
    @DisplayName("Debe generar token con firma HMAC-SHA256 válida")
    void testFirmaTokenValida() {
        // Given
        String username = "testuser";
        String token = jwtUtil.generateToken(username);

        // When - Intentar validar firma manualmente
        SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
        
        // Then - No debe lanzar excepción
        assertDoesNotThrow(() -> 
                Jwts.parser()
                        .verifyWith(key)
                        .build()
                        .parseSignedClaims(token),
                "La firma del token debe ser válida"
        );
    }

    @Test
    @DisplayName("Debe extraer claims personalizados correctamente")
    void testExtraerClaimsPersonalizados() {
        // Given
        String username = "testuser";
        String token = jwtUtil.generateToken(username);

        // When
        String subject = jwtUtil.extractClaim(token, Claims::getSubject);

        // Then
        assertEquals(username, subject, "El subject debe coincidir con el username");
    }

    @Test
    @DisplayName("Debe manejar username con espacios")
    void testUsernameConEspacios() {
        // Given
        String username = "test user";

        // When
        String token = jwtUtil.generateToken(username);
        String extracted = jwtUtil.extractUsername(token);

        // Then
        assertEquals(username, extracted, "Debe mantener espacios en el username");
    }

    @Test
    @DisplayName("Debe validar que token no esté expirado")
    void testTokenNoExpirado() {
        // Given
        String username = "testuser";
        String token = jwtUtil.generateToken(username);

        // When
        Date expiration = jwtUtil.extractClaim(token, Claims::getExpiration);

        // Then
        assertTrue(expiration.after(new Date()), "El token no debe estar expirado");
        assertTrue(jwtUtil.isTokenValid(token, username), "El token debe ser válido");
    }

    @Test
    @DisplayName("Debe generar token con longitud adecuada")
    void testLongitudToken() {
        // Given
        String username = "testuser";

        // When
        String token = jwtUtil.generateToken(username);

        // Then
        assertTrue(token.length() > 100, "El token debe tener longitud adecuada");
        assertTrue(token.length() < 1000, "El token no debe ser excesivamente largo");
    }
}
