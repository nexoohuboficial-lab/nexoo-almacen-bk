package com.nexoohub.almacen.sucursal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexoohub.almacen.common.config.JwtUtil;
import com.nexoohub.almacen.common.entity.Usuario;
import com.nexoohub.almacen.common.repository.UsuarioRepository;
import com.nexoohub.almacen.sucursal.entity.Sucursal;
import com.nexoohub.almacen.sucursal.repository.SucursalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class SucursalControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SucursalRepository sucursalRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;

    @BeforeEach
    void setUp() {
        sucursalRepository.deleteAll();
        usuarioRepository.deleteAll();

        Usuario usuario = new Usuario();
        usuario.setUsername("admin@nexoo.com");
        usuario.setPassword("$2a$10$test");
        usuario.setRole("ROLE_ADMIN");
        usuarioRepository.save(usuario);

        token = jwtUtil.generateToken("admin@nexoo.com");
    }

    @Test
    void listarSucursales_SoloActivas() throws Exception {
        Sucursal s1 = new Sucursal();
        s1.setNombre("Sucursal Centro");
        s1.setDireccion("Av. Principal 123");
        s1.setActivo(true);
        sucursalRepository.save(s1);

        Sucursal s2 = new Sucursal();
        s2.setNombre("Sucursal Norte");
        s2.setDireccion("Calle Norte 456");
        s2.setActivo(false);
        sucursalRepository.save(s2);

        mockMvc.perform(get("/api/v1/sucursales")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].nombre").value("Sucursal Centro"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void crearSucursal_Exitoso() throws Exception {
        Sucursal sucursal = new Sucursal();
        sucursal.setNombre("Sucursal Sur");
        sucursal.setDireccion("Av. Sur 789");

        mockMvc.perform(post("/api/v1/sucursales")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sucursal)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exitoso").value(true))
                .andExpect(jsonPath("$.mensaje").value("Sucursal creada correctamente"))
                .andExpect(jsonPath("$.fechaCreacion").exists());
    }

    @Test
    void crearSucursal_NombreVacio() throws Exception {
        Sucursal sucursal = new Sucursal();
        sucursal.setNombre("");
        sucursal.setDireccion("Test");

        mockMvc.perform(post("/api/v1/sucursales")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sucursal)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void actualizarSucursal_Exitoso() throws Exception {
        Sucursal existente = new Sucursal();
        existente.setNombre("Original");
        existente.setDireccion("Dirección Original");
        existente = sucursalRepository.save(existente);

        Sucursal actualizado = new Sucursal();
        actualizado.setNombre("Actualizado");
        actualizado.setDireccion("Dirección Actualizada");

        mockMvc.perform(put("/api/v1/sucursales/" + existente.getId())
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(actualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exitoso").value(true))
                .andExpect(jsonPath("$.mensaje").value("Sucursal actualizada correctamente"))
                .andExpect(jsonPath("$.fechaActualizacion").exists());
    }

    @Test
    void actualizarSucursal_NoExiste() throws Exception {
        Sucursal sucursal = new Sucursal();
        sucursal.setNombre("Test");
        sucursal.setDireccion("Test");

        mockMvc.perform(put("/api/v1/sucursales/99999")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sucursal)))
                .andExpect(status().isNotFound());
    }

    @Test
    void desactivarSucursal_Exitoso() throws Exception {
        Sucursal sucursal = new Sucursal();
        sucursal.setNombre("Test");
        sucursal.setDireccion("Test");
        sucursal.setActivo(true);
        sucursal = sucursalRepository.save(sucursal);

        mockMvc.perform(delete("/api/v1/sucursales/" + sucursal.getId())
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        Sucursal actualizada = sucursalRepository.findById(sucursal.getId()).orElseThrow();
        assertThat(actualizada.getActivo()).isFalse();
    }

    @Test
    void desactivarSucursal_NoExiste() throws Exception {
        mockMvc.perform(delete("/api/v1/sucursales/99999")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }
}
