package com.nexoohub.almacen.catalogo.controller;

import com.nexoohub.almacen.catalogo.entity.TipoCliente;
import com.nexoohub.almacen.catalogo.repository.TipoClienteRepository;
import com.nexoohub.almacen.common.config.JwtUtil;
import com.nexoohub.almacen.common.entity.Usuario;
import com.nexoohub.almacen.common.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class TipoClienteControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TipoClienteRepository tipoClienteRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private String token;

    @BeforeEach
    void setUp() {
        tipoClienteRepository.deleteAll();
        usuarioRepository.deleteAll();
        
        Usuario usuario = new Usuario();
        usuario.setUsername("admin@nexoo.com");
        usuario.setPassword("$2a$10$test");
        usuario.setRole("ROLE_ADMIN");
        usuarioRepository.save(usuario);
        
        token = jwtUtil.generateToken("admin@nexoo.com");
    }

    @Test
    void listarTiposCliente_SinDatos() throws Exception {
        mockMvc.perform(get("/api/v1/tipos-cliente")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void listarTiposCliente_ConDatos() throws Exception {
        TipoCliente t1 = new TipoCliente();
        t1.setNombre("Público General");
        t1.setDescripcion("Cliente ocasional");
        tipoClienteRepository.save(t1);

        TipoCliente t2 = new TipoCliente();
        t2.setNombre("Taller Mecánico");
        t2.setDescripcion("Cliente mayorista");
        tipoClienteRepository.save(t2);

        mockMvc.perform(get("/api/v1/tipos-cliente")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].nombre").value("Público General"))
                .andExpect(jsonPath("$.content[1].nombre").value("Taller Mecánico"))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void sinAutenticacion_RetornaUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/tipos-cliente"))
                .andExpect(status().isForbidden());
    }
}
