package com.nexoohub.almacen.catalogo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexoohub.almacen.catalogo.entity.Proveedor;
import com.nexoohub.almacen.catalogo.repository.ProveedorRepository;
import com.nexoohub.almacen.common.entity.Usuario;
import com.nexoohub.almacen.common.repository.UsuarioRepository;
import com.nexoohub.almacen.common.config.JwtUtil;
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
class ProveedorControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProveedorRepository proveedorRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;

    @BeforeEach
    void setUp() {
        proveedorRepository.deleteAll();
        usuarioRepository.deleteAll();

        Usuario usuario = new Usuario();
        usuario.setUsername("admin@nexoo.com");
        usuario.setPassword("$2a$10$test");
        usuario.setRole("ROLE_ADMIN");
        usuarioRepository.save(usuario);

        token = jwtUtil.generateToken("admin@nexoo.com");
    }

    @Test
    void listarProveedores_SinDatos() throws Exception {
        mockMvc.perform(get("/api/v1/proveedores")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(0))
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void listarProveedores_ConDatos() throws Exception {
        Proveedor p1 = new Proveedor();
        p1.setNombreEmpresa("Aceros del Norte");
        p1.setRfc("ADN850101ABC");
        p1.setNombreContacto("Juan Pérez");
        p1.setTelefono("8123456789");
        p1.setEmail("contacto@acerosdn.com");
        p1.setDireccion("Av. Industrial 123");
        proveedorRepository.save(p1);

        Proveedor p2 = new Proveedor();
        p2.setNombreEmpresa("Distribuidora La Confianza");
        p2.setRfc("DLC900501XYZ");
        p2.setNombreContacto("María López");
        proveedorRepository.save(p2);

        mockMvc.perform(get("/api/v1/proveedores")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].nombreEmpresa").value("Aceros del Norte"))
                .andExpect(jsonPath("$.content[1].nombreEmpresa").value("Distribuidora La Confianza"))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void crearProveedor_Exitoso() throws Exception {
        Proveedor proveedor = new Proveedor();
        proveedor.setNombreEmpresa("Repuestos Monterrey");
        proveedor.setRfc("REMX950101AAA");
        proveedor.setNombreContacto("Carlos Rodríguez");
        proveedor.setTelefono("8187654321");
        proveedor.setEmail("ventas@repuestosmty.com");
        proveedor.setDireccion("Calle Morelos 456");

        mockMvc.perform(post("/api/v1/proveedores")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(proveedor)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exitoso").value(true))
                .andExpect(jsonPath("$.mensaje").exists())
                .andExpect(jsonPath("$.id").isNumber());

        assertThat(proveedorRepository.count()).isEqualTo(1);
    }

    @Test
    void crearProveedor_NombreVacio() throws Exception {
        Proveedor proveedor = new Proveedor();
        proveedor.setNombreEmpresa("");
        proveedor.setRfc("TEST123");

        mockMvc.perform(post("/api/v1/proveedores")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(proveedor)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void actualizarProveedor_Exitoso() throws Exception {
        Proveedor proveedor = new Proveedor();
        proveedor.setNombreEmpresa("Proveedor Original");
        proveedor.setRfc("PO123");
        proveedor.setNombreContacto("Juan");
        Proveedor guardado = proveedorRepository.save(proveedor);

        Proveedor actualizado = new Proveedor();
        actualizado.setNombreEmpresa("Proveedor Actualizado");
        actualizado.setRfc("PA456");
        actualizado.setNombreContacto("Pedro");
        actualizado.setTelefono("8112345678");
        actualizado.setEmail("nuevo@proveedor.com");
        actualizado.setDireccion("Nueva Dirección 789");

        mockMvc.perform(put("/api/v1/proveedores/" + guardado.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(actualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exitoso").value(true))
                .andExpect(jsonPath("$.mensaje").exists());

        Proveedor verificar = proveedorRepository.findById(guardado.getId()).orElseThrow();
        assertThat(verificar.getNombreEmpresa()).isEqualTo("Proveedor Actualizado");
        assertThat(verificar.getRfc()).isEqualTo("PA456");
        assertThat(verificar.getTelefono()).isEqualTo("8112345678");
    }

    @Test
    void actualizarProveedor_NoExiste() throws Exception {
        Proveedor proveedor = new Proveedor();
        proveedor.setNombreEmpresa("Proveedor Test");
        proveedor.setRfc("TEST");

        mockMvc.perform(put("/api/v1/proveedores/99999")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(proveedor)))
                .andExpect(status().isNotFound());
    }

    @Test
    void sinAutenticacion_RetornaForbidden() throws Exception {
        mockMvc.perform(get("/api/v1/proveedores"))
                .andExpect(status().isForbidden());
    }
}
