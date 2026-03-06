package com.nexoohub.almacen.common.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexoohub.almacen.common.config.JwtUtil;
import com.nexoohub.almacen.common.entity.Usuario;
import com.nexoohub.almacen.common.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class UsuarioControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;

    @BeforeEach
    void setUp() {
        usuarioRepository.deleteAll();

        Usuario admin = new Usuario();
        admin.setUsername("admin@nexoo.com");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRole("ROLE_ADMIN");
        usuarioRepository.save(admin);

        token = jwtUtil.generateToken("admin@nexoo.com");
    }

    @Test
    void crearUsuario_Exitoso() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setUsername("nuevo@nexoo.com");
        usuario.setPassword("miPassword123");
        usuario.setRole("ROLE_USER");
        usuario.setEmpleadoId(42);

        mockMvc.perform(post("/api/v1/usuarios")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exitoso").value(true))
                .andExpect(jsonPath("$.mensaje").exists());

        Usuario guardado = usuarioRepository.findByUsername("nuevo@nexoo.com").orElseThrow();
        assertThat(guardado.getPassword()).isNotEqualTo("miPassword123"); // Debe estar encriptada
        assertThat(passwordEncoder.matches("miPassword123", guardado.getPassword())).isTrue();
    }

    @Test
    void crearUsuario_RoleAdmin() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setUsername("admin2@nexoo.com");
        usuario.setPassword("admin456");
        usuario.setRole("ROLE_ADMIN");
        usuario.setEmpleadoId(10);

        mockMvc.perform(post("/api/v1/usuarios")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exitoso").value(true));
    }

    @Test
    void crearUsuario_PasswordEncriptada() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setUsername("test@nexoo.com");
        usuario.setPassword("plaintext");
        usuario.setRole("ROLE_USER");

        mockMvc.perform(post("/api/v1/usuarios")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isCreated());

        Usuario guardado = usuarioRepository.findByUsername("test@nexoo.com").orElseThrow();
        assertThat(guardado.getPassword()).startsWith("$2a$"); // BCrypt hash
        assertThat(guardado.getPassword()).isNotEqualTo("plaintext");
    }
}
