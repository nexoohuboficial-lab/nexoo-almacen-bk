package com.nexoohub.almacen.common.service;

import com.nexoohub.almacen.common.entity.Usuario;
import com.nexoohub.almacen.common.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomUserDetailsService - Tests de Carga de Usuarios")
class CustomUserDetailsServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    private Usuario usuarioAdmin;
    private Usuario usuarioVendedor;

    @BeforeEach
    void setUp() {
        usuarioAdmin = new Usuario();
        usuarioAdmin.setId(1L);
        usuarioAdmin.setUsername("admin");
        usuarioAdmin.setPassword("$2a$10$hashedPassword123");
        usuarioAdmin.setRole("ROLE_ADMIN");

        usuarioVendedor = new Usuario();
        usuarioVendedor.setId(2L);
        usuarioVendedor.setUsername("vendedor1");
        usuarioVendedor.setPassword("$2a$10$anotherHashedPassword");
        usuarioVendedor.setRole("ROLE_VENDEDOR");
    }

    @Test
    @DisplayName("Debe cargar usuario correctamente por username")
    void testCargarUsuarioExitoso() {
        // Given
        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(usuarioAdmin));

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername("admin");

        // Then
        assertNotNull(userDetails, "UserDetails no debe ser null");
        assertEquals("admin", userDetails.getUsername(), "El username debe coincidir");
        assertEquals("$2a$10$hashedPassword123", userDetails.getPassword(), "La contraseña debe coincidir");
        verify(usuarioRepository, times(1)).findByUsername("admin");
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando usuario no existe")
    void testUsuarioNoEncontrado() {
        // Given
        when(usuarioRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        // When & Then
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("usuarioInexistente"));
        
        assertEquals("Usuario no encontrado", exception.getMessage());
        verify(usuarioRepository, times(1)).findByUsername("usuarioInexistente");
    }

    @Test
    @DisplayName("Debe cargar correctamente los roles del usuario")
    void testCargarRolesUsuario() {
        // Given
        when(usuarioRepository.findByUsername("vendedor1")).thenReturn(Optional.of(usuarioVendedor));

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername("vendedor1");

        // Then
        assertNotNull(userDetails.getAuthorities(), "Las autoridades no deben ser null");
        assertFalse(userDetails.getAuthorities().isEmpty(), "Debe tener al menos un rol");
        assertTrue(userDetails.getAuthorities().stream()
                        .anyMatch(auth -> auth.getAuthority().equals("ROLE_VENDEDOR")),
                "Debe contener el rol VENDEDOR");
    }

    @Test
    @DisplayName("Debe eliminar prefijo ROLE_ correctamente al construir UserDetails")
    void testEliminarPrefijoRole() {
        // Given
        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(usuarioAdmin));

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername("admin");

        // Then
        assertTrue(userDetails.getAuthorities().stream()
                        .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")),
                "Debe construir el rol correctamente");
    }

    @Test
    @DisplayName("Debe manejar usuario con username en minúsculas")
    void testUsernameMinusculas() {
        // Given
        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(usuarioAdmin));

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername("admin");

        // Then
        assertEquals("admin", userDetails.getUsername());
        verify(usuarioRepository, times(1)).findByUsername("admin");
    }

    @Test
    @DisplayName("Debe retornar UserDetails habilitado")
    void testUsuarioHabilitado() {
        // Given
        when(usuarioRepository.findByUsername("vendedor1")).thenReturn(Optional.of(usuarioVendedor));

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername("vendedor1");

        // Then
        assertTrue(userDetails.isEnabled(), "El usuario debe estar habilitado");
        assertTrue(userDetails.isAccountNonExpired(), "La cuenta no debe estar expirada");
        assertTrue(userDetails.isAccountNonLocked(), "La cuenta no debe estar bloqueada");
        assertTrue(userDetails.isCredentialsNonExpired(), "Las credenciales no deben estar expiradas");
    }

    @Test
    @DisplayName("Debe preservar la contraseña encriptada")
    void testPreservarPasswordEncriptada() {
        // Given
        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(usuarioAdmin));

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername("admin");

        // Then
        assertTrue(userDetails.getPassword().startsWith("$2a$"),
                "La contraseña debe mantener el formato BCrypt");
        assertEquals(usuarioAdmin.getPassword(), userDetails.getPassword(),
                "La contraseña no debe modificarse");
    }

    @Test
    @DisplayName("Debe manejar múltiples llamadas consecutivas")
    void testMultiplesLlamadas() {
        // Given
        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(usuarioAdmin));

        // When
        UserDetails userDetails1 = userDetailsService.loadUserByUsername("admin");
        UserDetails userDetails2 = userDetailsService.loadUserByUsername("admin");

        // Then
        assertNotNull(userDetails1);
        assertNotNull(userDetails2);
        assertEquals(userDetails1.getUsername(), userDetails2.getUsername());
        verify(usuarioRepository, times(2)).findByUsername("admin");
    }

    @Test
    @DisplayName("Debe lanzar excepción con username null")
    void testUsernameNull() {
        // Given
        when(usuarioRepository.findByUsername(null)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername(null));
    }

    @Test
    @DisplayName("Debe lanzar excepción con username vacío")
    void testUsernameVacio() {
        // Given
        when(usuarioRepository.findByUsername("")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername(""));
    }

    @Test
    @DisplayName("Debe cargar usuario con username con espacios")
    void testUsernameConEspacios() {
        // Given
        Usuario usuarioEspecial = new Usuario();
        usuarioEspecial.setId(3L);
        usuarioEspecial.setUsername("user name");
        usuarioEspecial.setPassword("$2a$10$hash");
        usuarioEspecial.setRole("ROLE_USER");

        when(usuarioRepository.findByUsername("user name")).thenReturn(Optional.of(usuarioEspecial));

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername("user name");

        // Then
        assertEquals("user name", userDetails.getUsername());
    }

    @Test
    @DisplayName("Debe cargar usuario con diferentes roles")
    void testDiferentesRoles() {
        // Given
        Usuario usuarioEncargado = new Usuario();
        usuarioEncargado.setId(4L);
        usuarioEncargado.setUsername("encargado1");
        usuarioEncargado.setPassword("$2a$10$hash");
        usuarioEncargado.setRole("ROLE_ENCARGADO");

        when(usuarioRepository.findByUsername("encargado1")).thenReturn(Optional.of(usuarioEncargado));

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername("encargado1");

        // Then
        assertTrue(userDetails.getAuthorities().stream()
                        .anyMatch(auth -> auth.getAuthority().equals("ROLE_ENCARGADO")),
                "Debe tener el rol ENCARGADO");
    }

    @Test
    @DisplayName("Debe mantener case-sensitive en username")
    void testUsernameCaseSensitive() {
        // Given
        when(usuarioRepository.findByUsername("Admin")).thenReturn(Optional.empty());
        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(usuarioAdmin));

        // When & Then
        assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("Admin"),
                "Debe ser case-sensitive");
        
        assertDoesNotThrow(() -> userDetailsService.loadUserByUsername("admin"),
                "Debe encontrar con case correcto");
    }

    @Test
    @DisplayName("Debe construir UserDetails con todos los campos necesarios")
    void testConstruccionCompleta() {
        // Given
        when(usuarioRepository.findByUsername("vendedor1")).thenReturn(Optional.of(usuarioVendedor));

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername("vendedor1");

        // Then
        assertAll("UserDetails debe tener todos los campos",
                () -> assertNotNull(userDetails.getUsername()),
                () -> assertNotNull(userDetails.getPassword()),
                () -> assertNotNull(userDetails.getAuthorities()),
                () -> assertFalse(userDetails.getAuthorities().isEmpty()),
                () -> assertTrue(userDetails.isEnabled()),
                () -> assertTrue(userDetails.isAccountNonExpired()),
                () -> assertTrue(userDetails.isAccountNonLocked()),
                () -> assertTrue(userDetails.isCredentialsNonExpired())
        );
    }
}
