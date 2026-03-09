package com.nexoohub.almacen.finanzas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexoohub.almacen.catalogo.entity.Cliente;
import com.nexoohub.almacen.catalogo.repository.ClienteRepository;
import com.nexoohub.almacen.finanzas.dto.AbonoRequestDTO;
import com.nexoohub.almacen.finanzas.dto.LimiteCreditoRequestDTO;
import com.nexoohub.almacen.finanzas.entity.LimiteCredito;
import com.nexoohub.almacen.finanzas.repository.HistorialCreditoRepository;
import com.nexoohub.almacen.finanzas.repository.LimiteCreditoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests de integración para CreditoController.
 * 
 * @author NexooHub Development Team
 * @since 1.2.0
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("CreditoController - Tests de Integración")
class CreditoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LimiteCreditoRepository limiteCreditoRepository;

    @Autowired
    private HistorialCreditoRepository historialCreditoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    private Cliente cliente;

    @BeforeEach
    void setUp() {
        // Limpiar datos previos
        historialCreditoRepository.deleteAll();
        limiteCreditoRepository.deleteAll();

        // Crear cliente test
        cliente = new Cliente();
        cliente.setNombre("Cliente Test Crédito");
        cliente.setRfc("XAXX010101000");
        cliente.setTelefono("555-1234");
        cliente.setEmail("test@credito.com");
        cliente.setTipoClienteId(1); // Público general
        cliente = clienteRepository.save(cliente);
    }

    // ==================== TESTS DE CREACIÓN ====================

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN", "USER"})
    @DisplayName("POST /api/credito/limites - Debe crear límite de crédito exitosamente")
    void debeCrearLimiteCreditoExitosamente() throws Exception {
        // Given
        LimiteCreditoRequestDTO request = new LimiteCreditoRequestDTO();
        request.setClienteId(cliente.getId());
        request.setLimiteAutorizado(BigDecimal.valueOf(10000));
        request.setPlazoPagoDias(30);
        request.setMaxFacturasVencidas(3);
        request.setPermiteSobregiro(false);
        request.setMontoSobregiro(BigDecimal.ZERO);
        request.setObservaciones("Cliente nuevo con límite estándar");

        // When / Then
        mockMvc.perform(post("/api/credito/limites")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.limiteAutorizado").value(10000))
                .andExpect(jsonPath("$.saldoUtilizado").value(0))
                .andExpect(jsonPath("$.creditoDisponible").value(10000))
                .andExpect(jsonPath("$.estado").value("ACTIVO"))
                .andExpect(jsonPath("$.porcentajeUtilizacion").value(0.0))
                .andExpect(jsonPath("$.cliente.nombre").value("Cliente Test Crédito"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN", "USER"})
    @DisplayName("POST /api/credito/limites - Debe rechazar si cliente ya tiene límite")
    void debeRechazarSiClienteYaTieneLimite() throws Exception {
        // Given - Cliente ya tiene límite
        LimiteCredito limiteExistente = new LimiteCredito();
        limiteExistente.setCliente(cliente);
        limiteExistente.setLimiteAutorizado(BigDecimal.valueOf(5000));
        limiteExistente.setSaldoUtilizado(BigDecimal.ZERO);
        limiteExistente.setEstado("ACTIVO");
        limiteExistente.setPlazoPagoDias(30);
        limiteCreditoRepository.save(limiteExistente);

        LimiteCreditoRequestDTO request = new LimiteCreditoRequestDTO();
        request.setClienteId(cliente.getId());
        request.setLimiteAutorizado(BigDecimal.valueOf(10000));

        // When / Then
        mockMvc.perform(post("/api/credito/limites")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value(containsString("ya tiene un límite")));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN", "USER"})
    @DisplayName("POST /api/credito/limites - Debe rechazar cliente inexistente")
    void debeRechazarClienteInexistente() throws Exception {
        // Given
        LimiteCreditoRequestDTO request = new LimiteCreditoRequestDTO();
        request.setClienteId(999999); // Cliente que no existe
        request.setLimiteAutorizado(BigDecimal.valueOf(10000));

        // When / Then
        mockMvc.perform(post("/api/credito/limites")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value(containsString("Cliente no encontrado")));
    }

    // ==================== TESTS DE ACTUALIZACIÓN ====================

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN", "USER"})
    @DisplayName("PUT /api/credito/limites/cliente/{id} - Debe actualizar límite exitosamente")
    void debeActualizarLimiteExitosamente() throws Exception {
        // Given - Cliente con límite existente
        LimiteCredito limiteExistente = new LimiteCredito();
        limiteExistente.setCliente(cliente);
        limiteExistente.setLimiteAutorizado(BigDecimal.valueOf(5000));
        limiteExistente.setSaldoUtilizado(BigDecimal.valueOf(2000));
        limiteExistente.setEstado("ACTIVO");
        limiteExistente.setPlazoPagoDias(30);
        limiteCreditoRepository.save(limiteExistente);

        LimiteCreditoRequestDTO request = new LimiteCreditoRequestDTO();
        request.setClienteId(cliente.getId());
        request.setLimiteAutorizado(BigDecimal.valueOf(15000)); // Incrementar límite
        request.setPlazoPagoDias(45); // Cambiar plazo

        // When / Then
        mockMvc.perform(put("/api/credito/limites/cliente/" + cliente.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.limiteAutorizado").value(15000))
                .andExpect(jsonPath("$.plazoPagoDias").value(45))
                .andExpect(jsonPath("$.creditoDisponible").value(13000)); // 15000 - 2000
    }

    // ==================== TESTS DE CONSULTA ====================

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN", "USER"})
    @DisplayName("GET /api/credito/limites/cliente/{id} - Debe obtener límite del cliente")
    void debeObtenerlimiteDelCliente() throws Exception {
        // Given
        LimiteCredito limite = new LimiteCredito();
        limite.setCliente(cliente);
        limite.setLimiteAutorizado(BigDecimal.valueOf(10000));
        limite.setSaldoUtilizado(BigDecimal.valueOf(3000));
        limite.setEstado("ACTIVO");
        limite.setPlazoPagoDias(30);
        limiteCreditoRepository.save(limite);

        // When / Then
        mockMvc.perform(get("/api/credito/limites/cliente/" + cliente.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.limiteAutorizado").value(10000))
                .andExpect(jsonPath("$.saldoUtilizado").value(3000))
                .andExpect(jsonPath("$.creditoDisponible").value(7000))
                .andExpect(jsonPath("$.porcentajeUtilizacion").value(30.0))
                .andExpect(jsonPath("$.estado").value("ACTIVO"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN", "USER"})
    @DisplayName("GET /api/credito/limites/cliente/{id} - Debe retornar 400 si no existe limite")
    void debeRetornar400SiNoExisteLimite() throws Exception {
        // When / Then
        mockMvc.perform(get("/api/credito/limites/cliente/" + cliente.getId()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value(containsString("No existe límite")));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN", "USER"})
    @DisplayName("GET /api/credito/limites - Debe listar todos los límites")
    void debeListarTodosLosLimites() throws Exception {
        // Given - Crear varios límites
        for (int i = 0; i < 3; i++) {
            Cliente c = new Cliente();
            c.setNombre("Cliente " + i);
            c.setRfc("RFC" + i);
            c.setTipoClienteId(1);
            c = clienteRepository.save(c);

            LimiteCredito limite = new LimiteCredito();
            limite.setCliente(c);
            limite.setLimiteAutorizado(BigDecimal.valueOf(5000 * (i + 1)));
            limite.setSaldoUtilizado(BigDecimal.ZERO);
            limite.setEstado("ACTIVO");
            limite.setPlazoPagoDias(30);
            limiteCreditoRepository.save(limite);
        }

        // When / Then
        mockMvc.perform(get("/api/credito/limites"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(greaterThanOrEqualTo(3)));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN", "USER"})
    @DisplayName("GET /api/credito/limites/estado/ACTIVO - Debe filtrar por estado")
    void debeFiltrarPorEstado() throws Exception {
        // Given
        LimiteCredito limiteActivo = new LimiteCredito();
        limiteActivo.setCliente(cliente);
        limiteActivo.setLimiteAutorizado(BigDecimal.valueOf(10000));
        limiteActivo.setSaldoUtilizado(BigDecimal.ZERO);
        limiteActivo.setEstado("ACTIVO");
        limiteActivo.setPlazoPagoDias(30);
        limiteCreditoRepository.save(limiteActivo);

        // When / Then
        mockMvc.perform(get("/api/credito/limites/estado/ACTIVO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].estado").value("ACTIVO"));
    }

    // ==================== TESTS DE VALIDACIÓN ====================

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN", "USER"})
    @DisplayName("GET /api/credito/validar - Debe validar crédito disponible OK")
    void debeValidarCreditoDisponibleOK() throws Exception {
        // Given
        LimiteCredito limite = new LimiteCredito();
        limite.setCliente(cliente);
        limite.setLimiteAutorizado(BigDecimal.valueOf(10000));
        limite.setSaldoUtilizado(BigDecimal.valueOf(3000));
        limite.setEstado("ACTIVO");
        limite.setPlazoPagoDias(30);
        limiteCreditoRepository.save(limite);

        // When / Then - Validar monto dentro del límite
        mockMvc.perform(get("/api/credito/validar")
                .param("clienteId", cliente.getId().toString())
                .param("monto", "5000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.creditoDisponible").value(true))
                .andExpect(jsonPath("$.codigo").value("OK"))
                .andExpect(jsonPath("$.montoDisponible").value(7000))
                .andExpect(jsonPath("$.montoSolicitado").value(5000));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN", "USER"})
    @DisplayName("GET /api/credito/validar - Debe rechazar si excede límite")
    void debeRechazarSiExcedeLimite() throws Exception {
        // Given
        LimiteCredito limite = new LimiteCredito();
        limite.setCliente(cliente);
        limite.setLimiteAutorizado(BigDecimal.valueOf(10000));
        limite.setSaldoUtilizado(BigDecimal.valueOf(8000));
        limite.setEstado("ACTIVO");
        limite.setPlazoPagoDias(30);
        limiteCreditoRepository.save(limite);

        // When / Then - Validar monto que excede el disponible
        mockMvc.perform(get("/api/credito/validar")
                .param("clienteId", cliente.getId().toString())
                .param("monto", "5000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.creditoDisponible").value(false))
                .andExpect(jsonPath("$.codigo").value("LIMITE_EXCEDIDO"))
                .andExpect(jsonPath("$.mensaje").value(containsString("insuficiente")));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN", "USER"})
    @DisplayName("GET /api/credito/validar - Debe rechazar si cliente bloqueado")
    void debeRechazarSiClienteBloqueado() throws Exception {
        // Given
        LimiteCredito limite = new LimiteCredito();
        limite.setCliente(cliente);
        limite.setLimiteAutorizado(BigDecimal.valueOf(10000));
        limite.setSaldoUtilizado(BigDecimal.valueOf(2000));
        limite.setEstado("BLOQUEADO");
        limite.setPlazoPagoDias(30);
        limiteCreditoRepository.save(limite);

        // When / Then
        mockMvc.perform(get("/api/credito/validar")
                .param("clienteId", cliente.getId().toString())
                .param("monto", "1000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.creditoDisponible").value(false))
                .andExpect(jsonPath("$.codigo").value("BLOQUEADO"))
                .andExpect(jsonPath("$.estado").value("BLOQUEADO"));
    }

    // ==================== TESTS DE ABONOS ====================

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN", "USER"})
    @DisplayName("POST /api/credito/abonos - Debe registrar abono exitosamente")
    void debeRegistrarAbonoExitosamente() throws Exception {
        // Given
        LimiteCredito limite = new LimiteCredito();
        limite.setCliente(cliente);
        limite.setLimiteAutorizado(BigDecimal.valueOf(10000));
        limite.setSaldoUtilizado(BigDecimal.valueOf(5000));
        limite.setEstado("ACTIVO");
        limite.setPlazoPagoDias(30);
        limiteCreditoRepository.save(limite);

        AbonoRequestDTO request = new AbonoRequestDTO();
        request.setClienteId(cliente.getId());
        request.setMonto(BigDecimal.valueOf(2000));
        request.setMetodoPago("EFECTIVO");
        request.setFolioComprobante("REC-001");
        request.setConcepto("Pago parcial");

        // When / Then
        mockMvc.perform(post("/api/credito/abonos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tipoMovimiento").value("ABONO"))
                .andExpect(jsonPath("$.monto").value(2000))
                .andExpect(jsonPath("$.saldoResultante").value(3000))
                .andExpect(jsonPath("$.metodoPago").value("EFECTIVO"))
                .andExpect(jsonPath("$.clienteNombre").value("Cliente Test Crédito"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN", "USER"})
    @DisplayName("POST /api/credito/abonos - Debe rechazar abono mayor al saldo")
    void debeRechazarAbonoMayorAlSaldo() throws Exception {
        // Given
        LimiteCredito limite = new LimiteCredito();
        limite.setCliente(cliente);
        limite.setLimiteAutorizado(BigDecimal.valueOf(10000));
        limite.setSaldoUtilizado(BigDecimal.valueOf(2000));
        limite.setEstado("ACTIVO");
        limite.setPlazoPagoDias(30);
        limiteCreditoRepository.save(limite);

        AbonoRequestDTO request = new AbonoRequestDTO();
        request.setClienteId(cliente.getId());
        request.setMonto(BigDecimal.valueOf(5000)); // Mayor al saldo utilizado
        request.setMetodoPago("EFECTIVO");

        // When / Then
        mockMvc.perform(post("/api/credito/abonos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value(containsString("no puede ser mayor al saldo")));
    }

    // ==================== TESTS DE BLOQUEO/DESBLOQUEO ====================

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN", "USER"})
    @DisplayName("PUT /api/credito/limites/cliente/{id}/bloquear - Debe bloquear crédito")
    void debeBloquearCredito() throws Exception {
        // Given
        LimiteCredito limite = new LimiteCredito();
        limite.setCliente(cliente);
        limite.setLimiteAutorizado(BigDecimal.valueOf(10000));
        limite.setSaldoUtilizado(BigDecimal.valueOf(3000));
        limite.setEstado("ACTIVO");
        limite.setPlazoPagoDias(30);
        limiteCreditoRepository.save(limite);

        // When / Then
        mockMvc.perform(put("/api/credito/limites/cliente/" + cliente.getId() + "/bloquear")
                .param("motivo", "Morosidad detectada"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Crédito bloqueado exitosamente"))
                .andExpect(jsonPath("$.motivo").value("Morosidad detectada"));

        // Verificar que quedó bloqueado
        LimiteCredito actualizado = limiteCreditoRepository.findByClienteId(cliente.getId()).orElseThrow();
        assert actualizado.getEstado().equals("BLOQUEADO");
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN", "USER"})
    @DisplayName("PUT /api/credito/limites/cliente/{id}/desbloquear - Debe desbloquear crédito")
    void debeDesbloquearCredito() throws Exception {
        // Given - Cliente bloqueado
        LimiteCredito limite = new LimiteCredito();
        limite.setCliente(cliente);
        limite.setLimiteAutorizado(BigDecimal.valueOf(10000));
        limite.setSaldoUtilizado(BigDecimal.valueOf(3000));
        limite.setEstado("BLOQUEADO");
        limite.setPlazoPagoDias(30);
        limiteCreditoRepository.save(limite);

        // When / Then
        mockMvc.perform(put("/api/credito/limites/cliente/" + cliente.getId() + "/desbloquear"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Crédito desbloqueado exitosamente"));

        // Verificar que quedó activo
        LimiteCredito actualizado = limiteCreditoRepository.findByClienteId(cliente.getId()).orElseThrow();
        assert actualizado.getEstado().equals("ACTIVO");
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN", "USER"})
    @DisplayName("PUT /api/credito/limites/cliente/{id}/suspender - Debe suspender crédito")
    void debeSuspenderCredito() throws Exception {
        // Given
        LimiteCredito limite = new LimiteCredito();
        limite.setCliente(cliente);
        limite.setLimiteAutorizado(BigDecimal.valueOf(10000));
        limite.setSaldoUtilizado(BigDecimal.valueOf(3000));
        limite.setEstado("ACTIVO");
        limite.setPlazoPagoDias(30);
        limiteCreditoRepository.save(limite);

        // When / Then
        mockMvc.perform(put("/api/credito/limites/cliente/" + cliente.getId() + "/suspender")
                .param("motivo", "Revisión pendiente"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Crédito suspendido exitosamente"));

        // Verificar que quedó suspendido
        LimiteCredito actualizado = limiteCreditoRepository.findByClienteId(cliente.getId()).orElseThrow();
        assert actualizado.getEstado().equals("SUSPENDIDO");
    }

    // ==================== TESTS DE HISTORIAL ====================

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN", "USER"})
    @DisplayName("GET /api/credito/historial/{id} - Debe obtener historial paginado")
    void debeObtenerHistorialPaginado() throws Exception {
        // Given - Cliente con límite y un abono registrado
        LimiteCredito limite = new LimiteCredito();
        limite.setCliente(cliente);
        limite.setLimiteAutorizado(BigDecimal.valueOf(10000));
        limite.setSaldoUtilizado(BigDecimal.valueOf(5000));
        limite.setEstado("ACTIVO");
        limite.setPlazoPagoDias(30);
        limiteCreditoRepository.save(limite);

        // Registrar un abono para crear historial
        AbonoRequestDTO request = new AbonoRequestDTO();
        request.setClienteId(cliente.getId());
        request.setMonto(BigDecimal.valueOf(1000));
        request.setMetodoPago("EFECTIVO");

        mockMvc.perform(post("/api/credito/abonos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // When / Then
        mockMvc.perform(get("/api/credito/historial/" + cliente.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].tipoMovimiento").value("ABONO"))
                .andExpect(jsonPath("$.content[0].monto").value(1000));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN", "USER"})
    @DisplayName("GET /api/credito/historial/{id}/abonos - Debe filtrar solo abonos")
    void debeObtenerSoloAbonos() throws Exception {
        // Given
        LimiteCredito limite = new LimiteCredito();
        limite.setCliente(cliente);
        limite.setLimiteAutorizado(BigDecimal.valueOf(10000));
        limite.setSaldoUtilizado(BigDecimal.valueOf(5000));
        limite.setEstado("ACTIVO");
        limite.setPlazoPagoDias(30);
        limiteCreditoRepository.save(limite);

        // Registrar abono
        AbonoRequestDTO request = new AbonoRequestDTO();
        request.setClienteId(cliente.getId());
        request.setMonto(BigDecimal.valueOf(1000));
        request.setMetodoPago("EFECTIVO");

        mockMvc.perform(post("/api/credito/abonos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        // When / Then
        mockMvc.perform(get("/api/credito/historial/" + cliente.getId() + "/abonos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].tipoMovimiento").value("ABONO"));
    }
}
