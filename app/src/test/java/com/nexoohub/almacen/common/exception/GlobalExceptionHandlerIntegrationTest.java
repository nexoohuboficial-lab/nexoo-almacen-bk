package com.nexoohub.almacen.common.exception;

import com.nexoohub.almacen.common.config.JwtUtil;
import com.nexoohub.almacen.common.entity.Usuario;
import com.nexoohub.almacen.common.repository.UsuarioRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class GlobalExceptionHandlerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;

    @BeforeEach
    void setUp() {
        usuarioRepository.deleteAll();

        Usuario usuario = new Usuario();
        usuario.setUsername("admin@nexoo.com");
        usuario.setPassword("$2a$10$test");
        usuario.setRole("ROLE_ADMIN");
        usuarioRepository.save(usuario);

        token = jwtUtil.generateToken("admin@nexoo.com");
    }

    @Test
    void handleResourceNotFoundException_Retorna404() throws Exception {
        // Este endpoint lanza ResourceNotFoundException
        mockMvc.perform(get("/api/test-exceptions/not-found")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.estatus").value(404))
                .andExpect(jsonPath("$.codigoError").value("No encontrado"))
                .andExpect(jsonPath("$.mensaje").value("Recurso de prueba no encontrado"))
                .andExpect(jsonPath("$.fechaHora").exists());
    }

    @Test
    void handleMethodArgumentNotValidException_Retorna400() throws Exception {
        // Enviamos datos inválidos para disparar validación
        TestRequest invalidRequest = new TestRequest("");

        mockMvc.perform(post("/api/test-exceptions/validation")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.estatus").value(400))
                .andExpect(jsonPath("$.codigoError").value("Datos invalidos"))
                .andExpect(jsonPath("$.mensaje").value("Datos de entrada inválidos"))
                .andExpect(jsonPath("$.detalles").isArray())
                .andExpect(jsonPath("$.fechaHora").exists());
    }

    @Test
    void handleDataIntegrityViolationException_Retorna409() throws Exception {
        // Este endpoint simula violación de integridad de datos
        mockMvc.perform(post("/api/test-exceptions/data-integrity")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.exitoso").value(false))
                .andExpect(jsonPath("$.mensaje").exists())
                .andExpect(jsonPath("$.fechaHora").exists());
    }

    @Test
    void handleBadCredentialsException_Retorna401() throws Exception {
        // Este endpoint lanza BadCredentialsException
        mockMvc.perform(post("/api/test-exceptions/bad-credentials")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.estatus").value(401))
                .andExpect(jsonPath("$.codigoError").value("Credenciales Invalidas"))
                .andExpect(jsonPath("$.mensaje").value("El usuario o la contraseña son incorrectos"))
                .andExpect(jsonPath("$.fechaHora").exists());
    }

    @Test
    void handleGenericException_Retorna500() throws Exception {
        // Este endpoint lanza Exception genérica
        mockMvc.perform(get("/api/test-exceptions/generic")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.estatus").value(500))
                .andExpect(jsonPath("$.codigoError").value("Error Interno"))
                .andExpect(jsonPath("$.mensaje").value("Ocurrió un error inesperado"))
                .andExpect(jsonPath("$.fechaHora").exists());
    }

    // DTO para test de validación
    record TestRequest(@NotBlank(message = "Campo requerido") String campo) {}
}

// Controlador de prueba para disparar excepciones
@RestController
@RequestMapping("/api/test-exceptions")
class TestExceptionController {

    @GetMapping("/not-found")
    public ResponseEntity<String> throwNotFound() {
        throw new ResourceNotFoundException("Recurso de prueba no encontrado");
    }

    @PostMapping("/validation")
    public ResponseEntity<String> throwValidation(@Valid @RequestBody GlobalExceptionHandlerIntegrationTest.TestRequest request) {
        return ResponseEntity.ok("Valid");
    }

    @PostMapping("/data-integrity")
    public ResponseEntity<String> throwDataIntegrity() {
        throw new DataIntegrityViolationException("Violación de llave única");
    }

    @PostMapping("/bad-credentials")
    public ResponseEntity<String> throwBadCredentials() {
        throw new BadCredentialsException("Credenciales incorrectas");
    }

    @GetMapping("/generic")
    public ResponseEntity<String> throwGeneric() {
        throw new RuntimeException("Error inesperado de prueba");
    }
}
