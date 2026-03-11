package com.nexoohub.almacen.fidelidad.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexoohub.almacen.catalogo.entity.Cliente;
import com.nexoohub.almacen.catalogo.entity.TipoCliente;
import com.nexoohub.almacen.catalogo.repository.ClienteRepository;
import com.nexoohub.almacen.catalogo.repository.TipoClienteRepository;
import com.nexoohub.almacen.fidelidad.dto.AcumularPuntosRequestDTO;
import com.nexoohub.almacen.fidelidad.dto.CanjearPuntosRequestDTO;
import com.nexoohub.almacen.fidelidad.entity.ProgramaFidelidad;
import com.nexoohub.almacen.fidelidad.repository.MovimientoPuntoRepository;
import com.nexoohub.almacen.fidelidad.repository.ProgramaFidelidadRepository;
import com.nexoohub.almacen.common.config.JwtUtil;
import com.nexoohub.almacen.common.entity.Usuario;
import com.nexoohub.almacen.common.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests de integración para ProgramaFidelidadController.
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("ProgramaFidelidadController - Tests de Integración")
class ProgramaFidelidadControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProgramaFidelidadRepository programaRepository;

    @Autowired
    private MovimientoPuntoRepository movimientoRepository;

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
    private Integer clienteId;

    @BeforeEach
    void setUp() {
        programaRepository.deleteAll();
        movimientoRepository.deleteAll();
        clienteRepository.deleteAll();
        tipoClienteRepository.deleteAll();
        usuarioRepository.deleteAll();

        // Crear usuario
        if (usuarioRepository.findByUsername("admin@nexoo.com").isEmpty()) {
            Usuario usuario = new Usuario();
            usuario.setUsername("admin@nexoo.com");
            usuario.setPassword("$2a$10$test");
            usuario.setRole("ROLE_ADMIN");
            usuarioRepository.save(usuario);
        }

        // Crear tipo de cliente
        TipoCliente tipoCliente = new TipoCliente();
        tipoCliente.setNombre("General");
        tipoClienteRepository.save(tipoCliente);

        // Crear cliente
        Cliente cliente = new Cliente();
        cliente.setNombre("Juan Pérez");
        cliente.setTipoClienteId(tipoCliente.getId());
        cliente.setTelefono("5551234567");
        Cliente guardado = clienteRepository.save(cliente);
        clienteId = guardado.getId();

        token = jwtUtil.generateToken("admin@nexoo.com");
    }

    @Test
    @DisplayName("Crear programa de fidelidad exitosamente")
    void crearPrograma_Exitoso() throws Exception {
        mockMvc.perform(post("/api/v1/fidelidad/programa")
                        .param("clienteId", clienteId.toString())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exitoso").value(true))
                .andExpect(jsonPath("$.mensaje").value("Programa de fidelidad creado exitosamente"))
                .andExpect(jsonPath("$.data.clienteId").value(clienteId))
                .andExpect(jsonPath("$.data.puntosAcumulados").value(0))
                .andExpect(jsonPath("$.data.activo").value(true));
    }

    @Test
    @DisplayName("No permitir crear programa duplicado")
    void crearPrograma_YaExiste() throws Exception {
        // Crear programa primero
        ProgramaFidelidad programa = new ProgramaFidelidad();
        programa.setClienteId(clienteId);
        programa.setPuntosAcumulados(0);
        programa.setTotalCompras(BigDecimal.ZERO);
        programa.setTotalCanjeado(BigDecimal.ZERO);
        programa.setActivo(true);
        programaRepository.save(programa);

        // Intentar crear duplicado
        mockMvc.perform(post("/api/v1/fidelidad/programa")
                        .param("clienteId", clienteId.toString())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Consultar programa por cliente")
    void consultarPorCliente_Exitoso() throws Exception {
        // Crear programa
        ProgramaFidelidad programa = new ProgramaFidelidad();
        programa.setClienteId(clienteId);
        programa.setPuntosAcumulados(150);
        programa.setTotalCompras(new BigDecimal("1500.00"));
        programa.setTotalCanjeado(BigDecimal.ZERO);
        programa.setActivo(true);
        programaRepository.save(programa);

        mockMvc.perform(get("/api/v1/fidelidad/programa/cliente/{clienteId}", clienteId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clienteId").value(clienteId))
                .andExpect(jsonPath("$.puntosAcumulados").value(150))
                .andExpect(jsonPath("$.totalCompras").value(1500.00))
                .andExpect(jsonPath("$.clienteNombre").value("Juan Pérez"));
    }

    @Test
    @DisplayName("Acumular puntos exitosamente")
    void acumularPuntos_Exitoso() throws Exception {
        // Crear programa
        ProgramaFidelidad programa = new ProgramaFidelidad();
        programa.setClienteId(clienteId);
        programa.setPuntosAcumulados(0);
        programa.setTotalCompras(BigDecimal.ZERO);
        programa.setTotalCanjeado(BigDecimal.ZERO);
        programa.setActivo(true);
        programaRepository.save(programa);

        AcumularPuntosRequestDTO request = new AcumularPuntosRequestDTO(
                clienteId,
                new BigDecimal("250.00"),
                null,
                "Compra de productos"
        );

        mockMvc.perform(post("/api/v1/fidelidad/acumular")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exitoso").value(true))
                .andExpect(jsonPath("$.data.puntosAcumulados").value(25)) // 250/10 = 25 puntos
                .andExpect(jsonPath("$.data.totalCompras").value(250.00));
    }

    @Test
    @DisplayName("Acumular puntos con monto insuficiente")
    void acumularPuntos_MontoInsuficiente() throws Exception {
        // Crear programa
        ProgramaFidelidad programa = new ProgramaFidelidad();
        programa.setClienteId(clienteId);
        programa.setPuntosAcumulados(0);
        programa.setTotalCompras(BigDecimal.ZERO);
        programa.setTotalCanjeado(BigDecimal.ZERO);
        programa.setActivo(true);
        programaRepository.save(programa);

        AcumularPuntosRequestDTO request = new AcumularPuntosRequestDTO(
                clienteId,
                new BigDecimal("5.00"), // Menos de $10
                null,
                "Compra pequeña"
        );

        mockMvc.perform(post("/api/v1/fidelidad/acumular")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Canjear puntos exitosamente")
    void canjearPuntos_Exitoso() throws Exception {
        // Crear programa con puntos
        ProgramaFidelidad programa = new ProgramaFidelidad();
        programa.setClienteId(clienteId);
        programa.setPuntosAcumulados(200);
        programa.setTotalCompras(new BigDecimal("2000.00"));
        programa.setTotalCanjeado(BigDecimal.ZERO);
        programa.setActivo(true);
        programaRepository.save(programa);

        CanjearPuntosRequestDTO request = new CanjearPuntosRequestDTO(
                clienteId,
                100, // 100 puntos = $10 MXN
                null,
                "Canje por descuento"
        );

        mockMvc.perform(post("/api/v1/fidelidad/canjear")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exitoso").value(true))
                .andExpect(jsonPath("$.data.puntosAcumulados").value(100)) // 200 - 100
                .andExpect(jsonPath("$.data.totalCanjeado").value(10.00));
    }

    @Test
    @DisplayName("No permitir canjear sin puntos suficientes")
    void canjearPuntos_InsuficientePuntos() throws Exception {
        // Crear programa con pocos puntos
        ProgramaFidelidad programa = new ProgramaFidelidad();
        programa.setClienteId(clienteId);
        programa.setPuntosAcumulados(50);
        programa.setTotalCompras(new BigDecimal("500.00"));
        programa.setTotalCanjeado(BigDecimal.ZERO);
        programa.setActivo(true);
        programaRepository.save(programa);

        CanjearPuntosRequestDTO request = new CanjearPuntosRequestDTO(
                clienteId,
                100,
                null,
                "Intento de canje"
        );

        mockMvc.perform(post("/api/v1/fidelidad/canjear")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Consultar historial de movimientos")
    void obtenerHistorial_Exitoso() throws Exception {
        // Crear programa
        ProgramaFidelidad programa = new ProgramaFidelidad();
        programa.setClienteId(clienteId);
        programa.setPuntosAcumulados(150);
        programa.setTotalCompras(new BigDecimal("1500.00"));
        programa.setTotalCanjeado(BigDecimal.ZERO);
        programa.setActivo(true);
        programaRepository.save(programa);

        mockMvc.perform(get("/api/v1/fidelidad/historial/cliente/{clienteId}", clienteId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", isA(java.util.List.class)));
    }

    @Test
    @DisplayName("Calcular descuento por puntos")
    void calcularDescuento_Exitoso() throws Exception {
        mockMvc.perform(get("/api/v1/fidelidad/calcular-descuento")
                        .param("puntos", "200")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.puntos").value(200))
                .andExpect(jsonPath("$.descuentoMXN").value(20.00)); // 200 puntos = $20 MXN
    }

    @Test
    @DisplayName("Obtener estadísticas del sistema")
    void obtenerEstadisticas_Exitoso() throws Exception {
        mockMvc.perform(get("/api/v1/fidelidad/estadisticas")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalProgramasActivos").exists())
                .andExpect(jsonPath("$.totalPuntosEnSistema").exists())
                .andExpect(jsonPath("$.tasaConversionPuntos").value(10)) // $10 = 1 punto
                .andExpect(jsonPath("$.tasaConversionDescuento").value(100)); // 100 puntos mínimo
    }

    @Test
    @DisplayName("Desactivar programa de fidelidad")
    void desactivarPrograma_Exitoso() throws Exception {
        // Crear programa
        ProgramaFidelidad programa = new ProgramaFidelidad();
        programa.setClienteId(clienteId);
        programa.setPuntosAcumulados(150);
        programa.setTotalCompras(new BigDecimal("1500.00"));
        programa.setTotalCanjeado(BigDecimal.ZERO);
        programa.setActivo(true);
        programaRepository.save(programa);

        mockMvc.perform(patch("/api/v1/fidelidad/programa/cliente/{clienteId}/desactivar", clienteId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exitoso").value(true))
                .andExpect(jsonPath("$.mensaje").value("Programa de fidelidad desactivado"));
    }

    @Test
    @DisplayName("Reactivar programa de fidelidad")
    void reactivarPrograma_Exitoso() throws Exception {
        // Crear programa inactivo
        ProgramaFidelidad programa = new ProgramaFidelidad();
        programa.setClienteId(clienteId);
        programa.setPuntosAcumulados(150);
        programa.setTotalCompras(new BigDecimal("1500.00"));
        programa.setTotalCanjeado(BigDecimal.ZERO);
        programa.setActivo(false);
        programaRepository.save(programa);

        mockMvc.perform(patch("/api/v1/fidelidad/programa/cliente/{clienteId}/reactivar", clienteId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exitoso").value(true))
                .andExpect(jsonPath("$.mensaje").value("Programa de fidelidad reactivado"));
    }
}
