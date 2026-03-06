package com.nexoohub.almacen.catalogo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexoohub.almacen.catalogo.entity.Categoria;
import com.nexoohub.almacen.catalogo.repository.CategoriaRepository;
import com.nexoohub.almacen.common.config.JwtUtil;
import com.nexoohub.almacen.common.entity.Usuario;
import com.nexoohub.almacen.common.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class CategoriaControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;

    @BeforeEach
    void setUp() {
        categoriaRepository.deleteAll();
        usuarioRepository.deleteAll();
        
        Usuario usuario = new Usuario();
        usuario.setUsername("admin@nexoo.com");
        usuario.setPassword("$2a$10$test");
        usuario.setRole("ROLE_ADMIN");
        usuarioRepository.save(usuario);
        
        token = jwtUtil.generateToken("admin@nexoo.com");
    }

    @Test
    void listarCategorias_SinDatos() throws Exception {
        mockMvc.perform(get("/api/v1/categorias")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void listarCategorias_ConDatos() throws Exception {
        Categoria c1 = new Categoria();
        c1.setNombre("Frenos");
        c1.setDescripcion("Componentes de freno");
        categoriaRepository.save(c1);

        Categoria c2 = new Categoria();
        c2.setNombre("Filtros");
        c2.setDescripcion("Filtros de aire y aceite");
        categoriaRepository.save(c2);

        mockMvc.perform(get("/api/v1/categorias")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].nombre").value("Filtros"))  // Alfabético: Filtros antes que Frenos
                .andExpect(jsonPath("$.content[1].nombre").value("Frenos"))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    void crearCategoria_Exitoso() throws Exception {
        Categoria categoria = new Categoria();
        categoria.setNombre("Suspensión");
        categoria.setDescripcion("Amortiguadores y resortes");

        mockMvc.perform(post("/api/v1/categorias")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoria)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exitoso").value(true))
                .andExpect(jsonPath("$.mensaje").value("Categoría creada correctamente"))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void crearCategoria_NombreVacio() throws Exception {
        Categoria categoria = new Categoria();
        categoria.setNombre("");
        categoria.setDescripcion("Test");

        mockMvc.perform(post("/api/v1/categorias")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoria)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void actualizarCategoria_Exitoso() throws Exception {
        Categoria existente = new Categoria();
        existente.setNombre("Frenos");
        existente.setDescripcion("Original");
        existente = categoriaRepository.save(existente);

        Categoria actualizado = new Categoria();
        actualizado.setNombre("Frenos Actualizados");
        actualizado.setDescripcion("Descripción actualizada");

        mockMvc.perform(put("/api/v1/categorias/" + existente.getId())
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(actualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exitoso").value(true))
                .andExpect(jsonPath("$.mensaje").value("Categoría actualizada correctamente"));
    }

    @Test
    void actualizarCategoria_NoExiste() throws Exception {
        Categoria categoria = new Categoria();
        categoria.setNombre("Test");
        categoria.setDescripcion("Test");

        mockMvc.perform(put("/api/v1/categorias/99999")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoria)))
                .andExpect(status().isNotFound());
    }

    @Test
    void sinAutenticacion_RetornaUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/categorias"))
                .andExpect(status().isForbidden());
    }
}
