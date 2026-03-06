package com.nexoohub.almacen.catalogo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexoohub.almacen.catalogo.entity.Cliente;
import com.nexoohub.almacen.catalogo.entity.TipoCliente;
import com.nexoohub.almacen.catalogo.repository.ClienteRepository;
import com.nexoohub.almacen.catalogo.repository.TipoClienteRepository;
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
class ClienteControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private TipoClienteRepository tipoClienteRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;
    private Integer tipoClienteId;

    @BeforeEach
    void setUp() {
        clienteRepository.deleteAll();
        tipoClienteRepository.deleteAll();
        usuarioRepository.deleteAll();

        Usuario usuario = new Usuario();
        usuario.setUsername("admin@nexoo.com");
        usuario.setPassword("$2a$10$test");
        usuario.setRole("ROLE_ADMIN");
        usuarioRepository.save(usuario);

        TipoCliente tipoCliente = new TipoCliente();
        tipoCliente.setNombre("General");
        TipoCliente guardado = tipoClienteRepository.save(tipoCliente);
        tipoClienteId = guardado.getId();

        token = jwtUtil.generateToken("admin@nexoo.com");
    }

    @Test
    void listarClientes_SinDatos() throws Exception {
        mockMvc.perform(get("/api/v1/clientes")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(0))
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void listarClientes_ConDatos() throws Exception {
        Cliente c1 = new Cliente();
        c1.setTipoClienteId(tipoClienteId);
        c1.setNombre("Juan Pérez García");
        c1.setRfc("PEGJ850101ABC");
        c1.setTelefono("8123456789");
        c1.setEmail("juan.perez@email.com");
        c1.setDireccionFiscal("Av. Juárez 123");
        clienteRepository.save(c1);

        Cliente c2 = new Cliente();
        c2.setTipoClienteId(tipoClienteId);
        c2.setNombre("María López Sánchez");
        c2.setRfc("LOSM900101XYZ");
        clienteRepository.save(c2);

        mockMvc.perform(get("/api/v1/clientes")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].nombre").value("Juan Pérez García"))
                .andExpect(jsonPath("$.content[1].nombre").value("María López Sánchez"))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void crearCliente_Exitoso() throws Exception {
        Cliente cliente = new Cliente();
        cliente.setTipoClienteId(tipoClienteId);
        cliente.setNombre("Carlos Rodríguez Martínez");
        cliente.setRfc("ROMC950601DEF");
        cliente.setTelefono("8187654321");
        cliente.setEmail("carlos.rodriguez@email.com");
        cliente.setDireccionFiscal("Calle Morelos 456");

        mockMvc.perform(post("/api/v1/clientes")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cliente)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exitoso").value(true))
                .andExpect(jsonPath("$.mensaje").exists())
                .andExpect(jsonPath("$.id").isNumber());

        assertThat(clienteRepository.count()).isEqualTo(1);
    }

    @Test
    void crearCliente_NombreVacio() throws Exception {
        Cliente cliente = new Cliente();
        cliente.setTipoClienteId(tipoClienteId);
        cliente.setNombre("");

        mockMvc.perform(post("/api/v1/clientes")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cliente)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void crearCliente_TipoClienteNulo() throws Exception {
        Cliente cliente = new Cliente();
        cliente.setNombre("Cliente Sin Tipo");
        cliente.setTipoClienteId(null);

        mockMvc.perform(post("/api/v1/clientes")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cliente)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void actualizarCliente_Exitoso() throws Exception {
        Cliente cliente = new Cliente();
        cliente.setTipoClienteId(tipoClienteId);
        cliente.setNombre("Cliente Original");
        cliente.setRfc("CLI123");
        Cliente guardado = clienteRepository.save(cliente);

        Cliente actualizado = new Cliente();
        actualizado.setTipoClienteId(tipoClienteId);
        actualizado.setNombre("Cliente Actualizado");
        actualizado.setRfc("CLI456");
        actualizado.setTelefono("8112223344");
        actualizado.setEmail("actualizado@email.com");
        actualizado.setDireccionFiscal("Nueva Dirección 789");

        mockMvc.perform(put("/api/v1/clientes/" + guardado.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(actualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exitoso").value(true))
                .andExpect(jsonPath("$.mensaje").exists());

        Cliente verificar = clienteRepository.findById(guardado.getId()).orElseThrow();
        assertThat(verificar.getNombre()).isEqualTo("Cliente Actualizado");
        assertThat(verificar.getRfc()).isEqualTo("CLI456");
        assertThat(verificar.getTelefono()).isEqualTo("8112223344");
    }

    @Test
    void actualizarCliente_NoExiste() throws Exception {
        Cliente cliente = new Cliente();
        cliente.setTipoClienteId(tipoClienteId);
        cliente.setNombre("Cliente Test");

        mockMvc.perform(put("/api/v1/clientes/99999")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cliente)))
                .andExpect(status().isNotFound());
    }

    @Test
    void sinAutenticacion_RetornaForbidden() throws Exception {
        mockMvc.perform(get("/api/v1/clientes"))
                .andExpect(status().isForbidden());
    }
}
