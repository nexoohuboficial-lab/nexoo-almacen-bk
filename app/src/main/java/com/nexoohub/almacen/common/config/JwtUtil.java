package com.nexoohub.almacen.common.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {
    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);
    
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expirationTime;

    private final Environment environment;

    public JwtUtil(Environment environment) {
        this.environment = environment;
    }

    /**
     * Validación de seguridad crítica al inicializar el componente.
     * Verifica que el JWT Secret sea apropiado para el entorno de ejecución.
     * 
     * @throws IllegalStateException si el secret no cumple requisitos de seguridad
     */
    @PostConstruct
    public void validateJwtSecret() {
        // Validar longitud mínima (256 bits = 32 caracteres)
        if (secret == null || secret.length() < 32) {
            throw new IllegalStateException(
                "⚠️ CONFIGURACIÓN INSEGURA: JWT Secret debe tener al menos 32 caracteres (256 bits). " +
                "Longitud actual: " + (secret != null ? secret.length() : 0)
            );
        }

        // Obtener perfiles activos
        String[] activeProfiles = environment.getActiveProfiles();
        boolean isDevOrTest = Arrays.stream(activeProfiles)
                .anyMatch(profile -> profile.equalsIgnoreCase("dev") || 
                                   profile.equalsIgnoreCase("test"));

        // En producción (sin perfiles dev/test), validar que no sea un secret de desarrollo
        if (!isDevOrTest) {
            String secretLower = secret.toLowerCase();
            
            if (secretLower.contains("test") || 
                secretLower.contains("dev") || 
                secretLower.contains("desarrollo") ||
                secretLower.contains("demo") ||
                secretLower.contains("example") ||
                secretLower.contains("sample")) {
                
                throw new IllegalStateException(
                    "🔴 SEGURIDAD CRÍTICA: JWT Secret de desarrollo/test detectado en PRODUCCIÓN. " +
                    "El secret contiene palabras prohibidas (test/dev/demo/example). " +
                    "Configure un secret seguro usando la variable de entorno JWT_SECRET."
                );
            }

            // En producción, validar que el secret sea suficientemente complejo
            if (secret.matches("^[a-zA-Z]+$")) {
                throw new IllegalStateException(
                    "⚠️ SEGURIDAD: JWT Secret en producción es demasiado simple (solo letras). " +
                    "Use una combinación de letras, números y símbolos."
                );
            }
        }

        // Log de validación exitosa (sin exponer el secret)
        log.info("✅ JWT Secret validado correctamente. " +
                "Longitud: {} caracteres. Perfil: {}", 
                secret.length(), 
                (isDevOrTest ? "DESARROLLO" : "PRODUCCIÓN"));
    }

    // 1. Generar la llave encriptada a partir de tu secreto
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // 2. Extraer el nombre de usuario (subject) del Token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // 3. Método genérico para extraer cualquier dato del Token
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claimsResolver.apply(claims);
    }

    // 4. Fabricar un nuevo Token para un usuario
    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSigningKey())
                .compact();
    }

    // 5. Validar si el Token sigue siendo útil y pertenece al usuario
    public boolean isTokenValid(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }
}
