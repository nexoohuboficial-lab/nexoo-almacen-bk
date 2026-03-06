package com.nexoohub.almacen.catalogo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexoohub.almacen.catalogo.entity.Moto;
import com.nexoohub.almacen.catalogo.repository.MotoRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class MotoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MotoRepository motoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;

    @BeforeEach
    void setUp() {
        motoRepository.deleteAll();
        usuarioRepository.deleteAll();

        Usuario usuario = new Usuario();
        usuario.setUsername("admin@nexoo.com");
        usuario.setPassword("$2a$10$test");
        usuario.setRole("ROLE_ADMIN");
        usuarioRepository.save(usuario);

        token = jwtUtil.generateToken("admin@nexoo.com");
    }

    @Test
    void listarMotos_SinDatos() throws Exception {
        mockMvc.perform(get("/api/v1/motos")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(0))
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void listarMotos_ConDatos() throws Exception {
        Moto moto1 = new Moto();
        moto1.setMarca("Honda");
        moto1.setModelo("CBR 600RR");
        moto1.setCilindrada(600);
        motoRepository.save(moto1);

        Moto moto2 = new Moto();
        moto2.setMarca("Yamaha");
        moto2.setModelo("R1");
        moto2.setCilindrada(1000);
        motoRepository.save(moto2);

        mockMvc.perform(get("/api/v1/motos")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].marca").value("Honda"))
                .andExpect(jsonPath("$.content[1].marca").value("Yamaha"))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void crearMoto_Exitoso() throws Exception {
        Moto moto = new Moto();
        moto.setMarca("Kawasaki");
        moto.setModelo("Ninja ZX-10R");
        moto.setCilindrada(998);

        mockMvc.perform(post("/api/v1/motos")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(moto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exitoso").value(true))
                .andExpect(jsonPath("$.mensaje").exists())
                .andExpect(jsonPath("$.id").isNumber());

        assertThat(motoRepository.count()).isEqualTo(1);
    }

    @Test
    void crearMoto_MarcaVacia() throws Exception {
        Moto moto = new Moto();
        moto.setMarca("");
        moto.setModelo("Test");

        mockMvc.perform(post("/api/v1/motos")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(moto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void crearMoto_ModeloVacio() throws Exception {
        Moto moto = new Moto();
        moto.setMarca("Suzuki");
        moto.setModelo("");

        mockMvc.perform(post("/api/v1/motos")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(moto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void actualizarMoto_Exitoso() throws Exception {
        Moto moto = new Moto();
        moto.setMarca("Ducati");
        moto.setModelo("Panigale V2");
        moto.setCilindrada(955);
        Moto guardada = motoRepository.save(moto);

        Moto actualizada = new Moto();
        actualizada.setMarca("Ducati");
        actualizada.setModelo("Panigale V4");
        actualizada.setCilindrada(1103);

        mockMvc.perform(put("/api/v1/motos/" + guardada.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(actualizada)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exitoso").value(true))
                .andExpect(jsonPath("$.mensaje").exists());

        Moto verificar = motoRepository.findById(guardada.getId()).orElseThrow();
        assertThat(verificar.getModelo()).isEqualTo("Panigale V4");
        assertThat(verificar.getCilindrada()).isEqualTo(1103);
    }

    @Test
    void actualizarMoto_NoExiste() throws Exception {
        Moto moto = new Moto();
        moto.setMarca("BMW");
        moto.setModelo("S1000RR");

        mockMvc.perform(put("/api/v1/motos/99999")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(moto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void sinAutenticacion_RetornaForbidden() throws Exception {
        mockMvc.perform(get("/api/v1/motos"))
                .andExpect(status().isForbidden());
    }
}
