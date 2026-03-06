package com.nexoohub.almacen.empleados.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexoohub.almacen.common.config.JwtUtil;
import com.nexoohub.almacen.common.entity.Usuario;
import com.nexoohub.almacen.common.repository.UsuarioRepository;
import com.nexoohub.almacen.empleados.entity.Empleado;
import com.nexoohub.almacen.empleados.repository.EmpleadoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class EmpleadoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;

    @BeforeEach
    void setUp() {
        empleadoRepository.deleteAll();
        usuarioRepository.deleteAll();

        Usuario usuario = new Usuario();
        usuario.setUsername("admin@nexoo.com");
        usuario.setPassword("$2a$10$test");
        usuario.setRole("ROLE_ADMIN");
        usuarioRepository.save(usuario);

        token = jwtUtil.generateToken("admin@nexoo.com");
    }

    @Test
    void registrarEmpleado_Exitoso() throws Exception {
        Empleado empleado = new Empleado();
        empleado.setNombre("Juan");
        empleado.setApellidos("Pérez García");
        empleado.setPuesto("Cajero");
        empleado.setSucursalId(1);
        empleado.setFechaContratacion(LocalDate.now());

        mockMvc.perform(post("/api/v1/empleados")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(empleado)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exitoso").value(true))
                .andExpect(jsonPath("$.mensaje").value("Empleado registrado correctamente"))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void registrarEmpleado_NombreVacio() throws Exception {
        Empleado empleado = new Empleado();
        empleado.setNombre("");
        empleado.setApellidos("Test");
        empleado.setPuesto("Test");
        empleado.setSucursalId(1);

        mockMvc.perform(post("/api/v1/empleados")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(empleado)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void obtenerEmpleadosPorSucursal_ConDatos() throws Exception {
        Empleado e1 = new Empleado();
        e1.setNombre("Juan");
        e1.setApellidos("Pérez");
        e1.setPuesto("Cajero");
        e1.setSucursalId(1);
        e1.setActivo(true);
        empleadoRepository.save(e1);

        Empleado e2 = new Empleado();
        e2.setNombre("María");
        e2.setApellidos("González");
        e2.setPuesto("Gerente");
        e2.setSucursalId(1);
        e2.setActivo(true);
        empleadoRepository.save(e2);

        Empleado e3 = new Empleado();
        e3.setNombre("Pedro");
        e3.setApellidos("López");
        e3.setPuesto("Mecánico");
        e3.setSucursalId(2);
        e3.setActivo(true);
        empleadoRepository.save(e3);

        mockMvc.perform(get("/api/v1/empleados/sucursal/1")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void obtenerEmpleadosPorSucursal_SinDatos() throws Exception {
        mockMvc.perform(get("/api/v1/empleados/sucursal/999")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void darDeBajaEmpleado_Exitoso() throws Exception {
        Empleado empleado = new Empleado();
        empleado.setNombre("Test");
        empleado.setApellidos("User");
        empleado.setPuesto("Test");
        empleado.setSucursalId(1);
        empleado.setActivo(true);
        empleado = empleadoRepository.save(empleado);

        mockMvc.perform(delete("/api/v1/empleados/" + empleado.getId())
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        Empleado actualizado = empleadoRepository.findById(empleado.getId()).orElseThrow();
        assertThat(actualizado.getActivo()).isFalse();
    }

    @Test
    void darDeBajaEmpleado_NoExiste() throws Exception {
        mockMvc.perform(delete("/api/v1/empleados/99999")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }
}
