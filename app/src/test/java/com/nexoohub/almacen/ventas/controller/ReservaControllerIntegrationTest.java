package com.nexoohub.almacen.ventas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexoohub.almacen.catalogo.entity.Cliente;
import com.nexoohub.almacen.catalogo.repository.ClienteRepository;
import com.nexoohub.almacen.inventario.entity.ProductoMaestro;
import com.nexoohub.almacen.inventario.repository.InventarioSucursalRepository;
import com.nexoohub.almacen.inventario.repository.ProductoMaestroRepository;
import com.nexoohub.almacen.sucursal.entity.Sucursal;
import com.nexoohub.almacen.sucursal.repository.SucursalRepository;
import com.nexoohub.almacen.ventas.dto.ReservaRequestDTO;
import com.nexoohub.almacen.ventas.entity.Reserva;
import com.nexoohub.almacen.ventas.repository.ReservaRepository;
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

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests de integración para ReservaController.
 * 
 * @author NexooHub Development Team
 * @since 1.1.0
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("ReservaController - Tests de Integración")
class ReservaControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ProductoMaestroRepository productoRepository;

    @Autowired
    private SucursalRepository sucursalRepository;

    @Autowired
    private InventarioSucursalRepository inventarioRepository;

    private Cliente cliente;
    private ProductoMaestro producto;
    private Sucursal sucursal;

    @BeforeEach
    void setUp() {
        // Crear cliente
        cliente = new Cliente();
        cliente.setNombre("Cliente Test Reservas");
        cliente.setRfc("XAXX010101000");
        cliente.setTipoClienteId(1); // Público general
        cliente = clienteRepository.save(cliente);

        // Crear producto
        producto = new ProductoMaestro();
        producto.setSkuInterno("TEST-RESERVA-001");
        producto.setNombreComercial("Producto Test Reserva");
        producto.setClaveSat("01010101");
        producto.setStockMinimoGlobal(5);
        producto = productoRepository.save(producto);

        // Crear sucursal si no existe
        if (sucursalRepository.findById(1).isEmpty()) {
            sucursal = new Sucursal();
            sucursal.setNombre("Sucursal Test");
            sucursal.setDireccion("Test 123");
            sucursal.setActivo(true);
            sucursal = sucursalRepository.save(sucursal);
        } else {
            sucursal = sucursalRepository.findById(1).get();
        }
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN", "USER"})
    @DisplayName("Debe crear reserva exitosamente cuando no hay stock")
    void debeCrearReservaExitosamente() throws Exception {
        // Given
        ReservaRequestDTO request = new ReservaRequestDTO();
        request.setClienteId(cliente.getId());
        request.setSkuInterno(producto.getSkuInterno());
        request.setSucursalId(sucursal.getId());
        request.setCantidad(2);
        request.setComentarios("Test de reserva");

        // When / Then
        mockMvc.perform(post("/api/v1/reservas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exitoso").value(true))
                .andExpect(jsonPath("$.reservaId").isNumber())
                .andExpect(jsonPath("$.estado").value("PENDIENTE"))
                .andExpect(jsonPath("$.fechaVencimiento").exists());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN", "USER"})
    @DisplayName("Debe retornar 404 cuando cliente no existe")
    void debeRetornar404CuandoClienteNoExiste() throws Exception {
        // Given
        ReservaRequestDTO request = new ReservaRequestDTO();
        request.setClienteId(99999); // Cliente inexistente
        request.setSkuInterno(producto.getSkuInterno());
        request.setSucursalId(sucursal.getId());
        request.setCantidad(2);

        // When / Then
        mockMvc.perform(post("/api/v1/reservas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value(containsString("Cliente no encontrado")));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN", "USER"})
    @DisplayName("Debe obtener reserva por ID")
    void debeObtenerReservaPorId() throws Exception {
        // Given - Crear reserva
        Reserva reserva = new Reserva();
        reserva.setCliente(cliente);
        reserva.setProducto(producto);
        reserva.setSucursal(sucursal);
        reserva.setCantidad(3);
        reserva.setEstado(Reserva.EstadoReserva.PENDIENTE);
        reserva.setFechaVencimiento(LocalDateTime.now().plusDays(7));
        reserva.setUsuarioRegistro("admin");
        reserva = reservaRepository.save(reserva);

        // When / Then
        mockMvc.perform(get("/api/v1/reservas/" + reserva.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(reserva.getId()))
                .andExpect(jsonPath("$.cantidad").value(3))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"))
                .andExpect(jsonPath("$.clienteNombre").value(cliente.getNombre()))
                .andExpect(jsonPath("$.productoNombre").value(producto.getNombreComercial()));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN", "USER"})
    @DisplayName("Debe listar reservas por cliente")
    void debeListarReservasPorCliente() throws Exception {
        // Given - Crear 2 reservas para el cliente
        for (int i = 0; i < 2; i++) {
            Reserva reserva = new Reserva();
            reserva.setCliente(cliente);
            reserva.setProducto(producto);
            reserva.setSucursal(sucursal);
            reserva.setCantidad(i + 1);
            reserva.setEstado(Reserva.EstadoReserva.PENDIENTE);
            reserva.setFechaVencimiento(LocalDateTime.now().plusDays(7));
            reserva.setUsuarioRegistro("admin");
            reservaRepository.save(reserva);
        }

        // When / Then
        mockMvc.perform(get("/api/v1/reservas/cliente/" + cliente.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(2))));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN", "USER"})
    @DisplayName("Debe listar reservas por estado PENDIENTE")
    void debeListarReservasPorEstado() throws Exception {
        // Given
        Reserva reserva = new Reserva();
        reserva.setCliente(cliente);
        reserva.setProducto(producto);
        reserva.setSucursal(sucursal);
        reserva.setCantidad(2);
        reserva.setEstado(Reserva.EstadoReserva.PENDIENTE);
        reserva.setFechaVencimiento(LocalDateTime.now().plusDays(7));
        reserva.setUsuarioRegistro("admin");
        reservaRepository.save(reserva);

        // When / Then
        mockMvc.perform(get("/api/v1/reservas/estado/PENDIENTE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[*].estado").value(everyItem(is("PENDIENTE"))));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN", "USER"})
    @DisplayName("Debe cancelar reserva exitosamente")
    void debeCancelarReservaExitosamente() throws Exception {
        // Given
        Reserva reserva = new Reserva();
        reserva.setCliente(cliente);
        reserva.setProducto(producto);
        reserva.setSucursal(sucursal);
        reserva.setCantidad(2);
        reserva.setEstado(Reserva.EstadoReserva.PENDIENTE);
        reserva.setFechaVencimiento(LocalDateTime.now().plusDays(7));
        reserva.setUsuarioRegistro("admin");
        reserva = reservaRepository.save(reserva);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("motivo", "Cliente ya no necesita el producto");

        // When / Then
        mockMvc.perform(put("/api/v1/reservas/" + reserva.getId() + "/cancelar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exitoso").value(true))
                .andExpect(jsonPath("$.estado").value("CANCELADA"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN", "USER"})
    @DisplayName("Debe completar reserva asociándola a una venta")
    void debeCompletarReserva() throws Exception {
        // Given
        Reserva reserva = new Reserva();
        reserva.setCliente(cliente);
        reserva.setProducto(producto);
        reserva.setSucursal(sucursal);
        reserva.setCantidad(2);
        reserva.setEstado(Reserva.EstadoReserva.NOTIFICADA);
        reserva.setFechaNotificacion(LocalDateTime.now());
        reserva.setFechaVencimiento(LocalDateTime.now().plusDays(7));
        reserva.setUsuarioRegistro("admin");
        reserva = reservaRepository.save(reserva);

        Map<String, Integer> requestBody = new HashMap<>();
        requestBody.put("ventaId", 42);

        // When / Then
        mockMvc.perform(put("/api/v1/reservas/" + reserva.getId() + "/completar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exitoso").value(true))
                .andExpect(jsonPath("$.estado").value("COMPLETADA"))
                .andExpect(jsonPath("$.ventaId").value(42));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN", "USER"})
    @DisplayName("Debe procesar reservas vencidas")
    void debeProcesarReservasVencidas() throws Exception {
        // Given - Crear reserva vencida
        Reserva reserva = new Reserva();
        reserva.setCliente(cliente);
        reserva.setProducto(producto);
        reserva.setSucursal(sucursal);
        reserva.setCantidad(2);
        reserva.setEstado(Reserva.EstadoReserva.PENDIENTE);
        reserva.setFechaCreacion(LocalDateTime.now().minusDays(10));
        reserva.setFechaVencimiento(LocalDateTime.now().minusDays(3)); // Vencida hace 3 días
        reserva.setUsuarioRegistro("admin");
        reservaRepository.save(reserva);

        // When / Then
        mockMvc.perform(post("/api/v1/reservas/procesar-vencidas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exitoso").value(true))
                .andExpect(jsonPath("$.totalProcesadas").isNumber());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN", "USER"})
    @DisplayName("Debe validar campos requeridos al crear reserva")
    void debeValidarCamposRequeridosAlCrear() throws Exception {
        // Given - Request sin campos requeridos
        ReservaRequestDTO request = new ReservaRequestDTO();

        // When / Then
        mockMvc.perform(post("/api/v1/reservas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Debe retornar 403 sin autenticación")
    void debeRetornar403SinAutenticacion() throws Exception {
        mockMvc.perform(get("/api/v1/reservas"))
                .andExpect(status().isForbidden());
    }
}
