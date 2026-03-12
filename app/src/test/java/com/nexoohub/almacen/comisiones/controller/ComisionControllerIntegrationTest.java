package com.nexoohub.almacen.comisiones.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexoohub.almacen.comisiones.dto.*;
import com.nexoohub.almacen.common.config.JwtUtil;
import com.nexoohub.almacen.common.entity.Usuario;
import com.nexoohub.almacen.common.repository.UsuarioRepository;
import com.nexoohub.almacen.empleados.entity.Empleado;
import com.nexoohub.almacen.empleados.repository.EmpleadoRepository;
import com.nexoohub.almacen.finanzas.entity.ConfiguracionFinanciera;
import com.nexoohub.almacen.finanzas.repository.ConfiguracionFinancieraRepository;
import com.nexoohub.almacen.ventas.entity.Venta;
import com.nexoohub.almacen.ventas.repository.VentaRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.hamcrest.Matchers.*;

/**
 * Tests de integración para el módulo de comisiones
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("ComisionController - Tests de Integración")
public class ComisionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private ConfiguracionFinancieraRepository configuracionRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EntityManager entityManager;

    private String token;
    private Integer vendedorId;

    @BeforeEach
    void setUp() {
        // Crear usuario para autenticación
        Usuario usuario = new Usuario();
        usuario.setUsername("admin@nexoo.com");
        usuario.setPassword("$2a$10$test");
        usuario.setRole("ROLE_ADMIN");
        usuarioRepository.save(usuario);

        // Generar token
        token = jwtUtil.generateToken("admin@nexoo.com");

        // Crear configuración financiera si no existe
        if (configuracionRepository.count() == 0) {
            ConfiguracionFinanciera config = new ConfiguracionFinanciera();
            config.setIva(new BigDecimal("0.16"));
            config.setMargenGananciaBase(new BigDecimal("0.30"));
            config.setComisionTarjeta(new BigDecimal("0.03"));
            configuracionRepository.save(config);
        }

        // Crear un vendedor de prueba
        Empleado vendedor = new Empleado();
        vendedor.setNombre("Juan");
        vendedor.setApellidos("Pérez");
        vendedor.setPuesto("Vendedor");
        vendedor.setSucursalId(1);
        vendedor.setFechaContratacion(LocalDate.now().minusYears(1));
        vendedor.setActivo(true);
        vendedor = empleadoRepository.save(vendedor);
        vendedorId = vendedor.getId();
    }

    @Test
    @Order(1)
    @DisplayName("Test 1: POST /api/comisiones/reglas - Debe crear regla de comisión")
    void testCrearReglaComision() throws Exception {
        // Given
        ReglaComisionRequestDTO request = new ReglaComisionRequestDTO();
        request.setNombre("Comisión 5% Vendedores");
        request.setDescripcion("Comisión estándar para vendedores");
        request.setTipo("PORCENTAJE_VENTA");
        request.setPuesto("Vendedor");
        request.setPorcentajeComision(new BigDecimal("0.05")); // 5%
        request.setActiva(true);
        request.setPrioridad(1);

        // When & Then
        mockMvc.perform(post("/api/comisiones/reglas")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nombre").value("Comisión 5% Vendedores"))
                .andExpect(jsonPath("$.tipo").value("PORCENTAJE_VENTA"))
                .andExpect(jsonPath("$.porcentajeComision").value(0.05))
                .andExpect(jsonPath("$.activa").value(true));
    }

    @Test
    @Order(2)
    @DisplayName("Test 2: GET /api/comisiones/reglas - Debe listar reglas activas")
    void testListarReglasActivas() throws Exception {
        // Given - Crear una regla
        ReglaComisionRequestDTO request = new ReglaComisionRequestDTO();
        request.setNombre("Comisión Base");
        request.setDescripcion("Comisión base para todos");
        request.setTipo("PORCENTAJE_VENTA");
        request.setPorcentajeComision(new BigDecimal("0.03"));
        request.setActiva(true);
        request.setPrioridad(1);

        mockMvc.perform(post("/api/comisiones/reglas")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // When & Then
        mockMvc.perform(get("/api/comisiones/reglas")
                        .header("Authorization", "Bearer " + token)
                        .param("soloActivas", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @Order(3)
    @DisplayName("Test 3: POST /api/comisiones/calcular - Debe calcular comisiones del periodo")
    @Transactional
    void testCalcularComisionesPeriodo() throws Exception {
        // Given - Crear regla y venta
        ReglaComisionRequestDTO regla = new ReglaComisionRequestDTO();
        regla.setNombre("Comisión Test");
        regla.setDescripcion("Para prueba");
        regla.setTipo("PORCENTAJE_VENTA");
        regla.setPuesto("Vendedor");
        regla.setPorcentajeComision(new BigDecimal("0.05")); // 5%
        regla.setActiva(true);
        regla.setPrioridad(1);

        mockMvc.perform(post("/api/comisiones/reglas")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(regla)))
                .andExpect(status().isCreated());

        // Obtener periodo actual
        YearMonth ahora = YearMonth.now();
        Integer anio = ahora.getYear();
        Integer mes = ahora.getMonthValue();

        // Crear venta de prueba en el periodo correcto
        Empleado vendedor = empleadoRepository.findById(vendedorId)
            .orElseThrow(() -> new RuntimeException("Vendedor no encontrado"));
        
        Venta venta = new Venta();
        venta.setVendedor(vendedor); // Establecer relación en lugar de  ID directamente
        venta.setMetodoPago("EFECTIVO");
        venta.setTotal(new BigDecimal("10000.00")); // $10,000 de venta
        venta.setFechaVenta(ahora.atDay(15).atTime(10, 0)); // Fecha en mitad del mes
        ventaRepository.save(venta);
        entityManager.flush(); // Forzar persistencia inmediata

        // When & Then - Calcular comisiones
        mockMvc.perform(post("/api/comisiones/calcular")
                        .header("Authorization", "Bearer " + token)
                        .param("anio", anio.toString())
                        .param("mes", mes.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].vendedorId").value(vendedorId))
                .andExpect(jsonPath("$[0].totalVentas").value(10000.00))
                .andExpect(jsonPath("$[0].cantidadVentas").value(1))
                .andExpect(jsonPath("$[0].comisionBase").value(500.00)) // 5% de 10,000
                .andExpect(jsonPath("$[0].estado").value("PENDIENTE"));
    }

    @Test
    @Order(4)
    @DisplayName("Test 4: GET /api/comisiones/vendedor/{id} - Debe obtener comisiones del vendedor")
    @Transactional
    void testObtenerComisionesPorVendedor() throws Exception {
        // Given - Crear regla y calcular comisión
        ReglaComisionRequestDTO regla = new ReglaComisionRequestDTO();
        regla.setNombre("Comisión Vendedor");
        regla.setDescripcion("Test");
        regla.setTipo("PORCENTAJE_VENTA");
        regla.setPuesto("Vendedor");
        regla.setPorcentajeComision(new BigDecimal("0.03"));
        regla.setActiva(true);
        regla.setPrioridad(1);

        mockMvc.perform(post("/api/comisiones/reglas")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(regla)))
                .andExpect(status().isCreated());

        Venta venta = new Venta();
        venta.setVendedorId(vendedorId);
        venta.setClienteId(1);
        venta.setSucursalId(1);
        venta.setMetodoPago("EFECTIVO");
        venta.setTotal(new BigDecimal("5000.00"));
        ventaRepository.save(venta);

        YearMonth ahora = YearMonth.now();
        mockMvc.perform(post("/api/comisiones/calcular")
                        .header("Authorization", "Bearer " + token)
                        .param("anio", ahora.getYear() + "")
                        .param("mes", ahora.getMonthValue() + ""))
                .andExpect(status().isOk());

        // When & Then
        mockMvc.perform(get("/api/comisiones/vendedor/" + vendedorId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].vendedorId").value(vendedorId))
                .andExpect(jsonPath("$[0].totalVentas").exists());
    }

    @Test
    @Order(5)
    @DisplayName("Test 5: GET /api/comisiones/resumen - Debe obtener resumen del periodo")
    @Transactional
    void testObtenerResumenPeriodo() throws Exception {
        // Given - Setup inicial
        ReglaComisionRequestDTO regla = new ReglaComisionRequestDTO();
        regla.setNombre("Comisión Resumen");
        regla.setDescripcion("Test resumen");
        regla.setTipo("PORCENTAJE_VENTA");
        regla.setPorcentajeComision(new BigDecimal("0.04"));
        regla.setActiva(true);
        regla.setPrioridad(1);

        mockMvc.perform(post("/api/comisiones/reglas")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(regla)))
                .andExpect(status().isCreated());

        Venta venta = new Venta();
        venta.setVendedorId(vendedorId);
        venta.setClienteId(1);
        venta.setSucursalId(1);
        venta.setMetodoPago("EFECTIVO");
        venta.setTotal(new BigDecimal("8000.00"));
        ventaRepository.save(venta);

        YearMonth ahora = YearMonth.now();
        mockMvc.perform(post("/api/comisiones/calcular")
                        .header("Authorization", "Bearer " + token)
                        .param("anio", ahora.getYear() + "")
                        .param("mes", ahora.getMonthValue() + ""))
                .andExpect(status().isOk());

        // When & Then
        mockMvc.perform(get("/api/comisiones/resumen")
                        .header("Authorization", "Bearer " + token)
                        .param("anio", ahora.getYear() + "")
                        .param("mes", ahora.getMonthValue() + ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.periodo").exists())
                .andExpect(jsonPath("$.cantidadVendedores").value(greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.totalComisiones").exists())
                .andExpect(jsonPath("$.totalVentas").exists())
                .andExpect(jsonPath("$.cantidadComisionesPendientes").exists())
                .andExpect(jsonPath("$.detalles").isArray());
    }

    @Test
    @Order(6)
    @DisplayName("Test 6: PUT /api/comisiones/{id}/aprobar - Debe aprobar comisión")
    @Transactional
    void testAprobarComision() throws Exception {
        // Given - Calcular comisión primero
        ReglaComisionRequestDTO regla = new ReglaComisionRequestDTO();
        regla.setNombre("Comisión Aprobar");
        regla.setTipo("PORCENTAJE_VENTA");
        regla.setPorcentajeComision(new BigDecimal("0.03"));
        regla.setActiva(true);
        regla.setPrioridad(1);

        mockMvc.perform(post("/api/comisiones/reglas")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(regla)))
                .andExpect(status().isCreated());

        Venta venta = new Venta();
        venta.setVendedorId(vendedorId);
        venta.setClienteId(1);
        venta.setSucursalId(1);
        venta.setMetodoPago("TARJETA");
        venta.setTotal(new BigDecimal("3000.00"));
        ventaRepository.save(venta);

        YearMonth ahora = YearMonth.now();
        String responseCalculo = mockMvc.perform(post("/api/comisiones/calcular")
                        .header("Authorization", "Bearer " + token)
                        .param("anio", ahora.getYear() + "")
                        .param("mes", ahora.getMonthValue() + ""))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Integer comisionId = objectMapper.readTree(responseCalculo).get(0).get("id").asInt();

        // When & Then - Aprobar comisión
        AprobarComisionRequestDTO aprobar = new AprobarComisionRequestDTO();
        aprobar.setNuevoEstado("APROBADA");
        aprobar.setNotas("Aprobada por gerente");

        mockMvc.perform(put("/api/comisiones/" + comisionId + "/aprobar")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(aprobar)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(comisionId))
                .andExpect(jsonPath("$.estado").value("APROBADA"))
                .andExpect(jsonPath("$.fechaAprobacion").exists())
                .andExpect(jsonPath("$.notas").value("Aprobada por gerente"));
    }

    @Test
    @Order(7)
    @DisplayName("Test 7: POST /api/comisiones/reglas - Debe validar datos obligatorios")
    void testCrearReglaConDatosInvalidos() throws Exception {
        // Given - Request sin campos obligatorios
        ReglaComisionRequestDTO request = new ReglaComisionRequestDTO();
        request.setNombre(""); // Vacío - inválido
        request.setTipo("TIPO_INVALIDO"); // Tipo no permitido

        // When & Then
        mockMvc.perform(post("/api/comisiones/reglas")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
