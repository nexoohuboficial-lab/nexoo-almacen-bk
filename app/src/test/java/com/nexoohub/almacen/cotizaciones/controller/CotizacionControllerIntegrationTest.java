package com.nexoohub.almacen.cotizaciones.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexoohub.almacen.catalogo.entity.Cliente;
import com.nexoohub.almacen.catalogo.entity.TipoCliente;
import com.nexoohub.almacen.catalogo.repository.ClienteRepository;
import com.nexoohub.almacen.catalogo.repository.TipoClienteRepository;
import com.nexoohub.almacen.common.config.JwtUtil;
import com.nexoohub.almacen.common.entity.Usuario;
import com.nexoohub.almacen.common.repository.UsuarioRepository;
import com.nexoohub.almacen.cotizaciones.dto.*;
import com.nexoohub.almacen.cotizaciones.repository.CotizacionRepository;
import com.nexoohub.almacen.cotizaciones.repository.DetalleCotizacionRepository;
import com.nexoohub.almacen.empleados.entity.Empleado;
import com.nexoohub.almacen.empleados.repository.EmpleadoRepository;
import com.nexoohub.almacen.inventario.entity.InventarioSucursal;
import com.nexoohub.almacen.inventario.entity.InventarioSucursalId;
import com.nexoohub.almacen.inventario.entity.ProductoMaestro;
import com.nexoohub.almacen.inventario.repository.InventarioSucursalRepository;
import com.nexoohub.almacen.inventario.repository.ProductoMaestroRepository;
import com.nexoohub.almacen.sucursal.entity.Sucursal;
import com.nexoohub.almacen.sucursal.repository.SucursalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

/**
 * Tests de integración para CotizacionController
 * Valida el comportamiento end-to-end de la API REST de cotizaciones
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DisplayName("CotizacionController - Tests de Integración")
class CotizacionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CotizacionRepository cotizacionRepository;

    @Autowired
    private DetalleCotizacionRepository detalleCotizacionRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private TipoClienteRepository tipoClienteRepository;

    @Autowired
    private SucursalRepository sucursalRepository;

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private ProductoMaestroRepository productoMaestroRepository;

    @Autowired
    private InventarioSucursalRepository inventarioSucursalRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;
    private Integer clienteId;
    private Integer sucursalId;
    private Integer vendedorId;
    private String skuProducto1;
    private String skuProducto2;

    @BeforeEach
    void setUp() {
        // Limpiar bases de datos en el orden correcto (respetando foreign keys)
        detalleCotizacionRepository.deleteAll();
        cotizacionRepository.deleteAll();
        inventarioSucursalRepository.deleteAll();
        productoMaestroRepository.deleteAll();
        empleadoRepository.deleteAll();
        sucursalRepository.deleteAll();
        clienteRepository.deleteAll();
        tipoClienteRepository.deleteAll();
        usuarioRepository.deleteAll();

        // Crear usuario para autenticación (verificar que no exista)
        if (usuarioRepository.findByUsername("admin@nexoo.com").isEmpty()) {
            Usuario usuario = new Usuario();
            usuario.setUsername("admin@nexoo.com");
            usuario.setPassword("$2a$10$test");
            usuario.setRole("ROLE_ADMIN");
            usuarioRepository.save(usuario);
        }

        // Crear sucursal
        Sucursal sucursal = new Sucursal();
        sucursal.setNombre("Sucursal Centro");
        sucursal.setDireccion("Av. Principal 100");
        sucursal.setActivo(true);
        Sucursal sucursalGuardada = sucursalRepository.save(sucursal);
        sucursalId = sucursalGuardada.getId();

        // Crear tipo de cliente
        TipoCliente tipoCliente = new TipoCliente();
        tipoCliente.setNombre("General");
        tipoClienteRepository.save(tipoCliente);

        // Crear cliente
        Cliente cliente = new Cliente();
        cliente.setTipoClienteId(tipoCliente.getId());
        cliente.setNombre("Juan Pérez");
        cliente.setTelefono("8123456789");
        Cliente clienteGuardado = clienteRepository.save(cliente);
        clienteId = clienteGuardado.getId();

        // Crear vendedor
        Empleado vendedor = new Empleado();
        vendedor.setNombre("Vendedor");
        vendedor.setApellidos("Test");
        vendedor.setPuesto("Vendedor");
        vendedor.setSucursalId(sucursalId);
        vendedor.setActivo(true);
        Empleado vendedorGuardado = empleadoRepository.save(vendedor);
        vendedorId = vendedorGuardado.getId();

        // Crear productos
        skuProducto1 = "PROD001";
        ProductoMaestro producto1 = new ProductoMaestro();
        producto1.setSkuInterno(skuProducto1);
        producto1.setNombreComercial("Producto Test 1");
        producto1.setActivo(true);
        productoMaestroRepository.save(producto1);

        skuProducto2 = "PROD002";
        ProductoMaestro producto2 = new ProductoMaestro();
        producto2.setSkuInterno(skuProducto2);
        producto2.setNombreComercial("Producto Test 2");
        producto2.setActivo(true);
        productoMaestroRepository.save(producto2);

        // Crear inventario
        InventarioSucursal inventario1 = new InventarioSucursal();
        inventario1.setId(new InventarioSucursalId(sucursalId, skuProducto1));
        inventario1.setStockActual(100);
        inventario1.setCostoPromedioPonderado(new BigDecimal("100.00"));
        inventarioSucursalRepository.save(inventario1);

        InventarioSucursal inventario2 = new InventarioSucursal();
        inventario2.setId(new InventarioSucursalId(sucursalId, skuProducto2));
        inventario2.setStockActual(50);
        inventario2.setCostoPromedioPonderado(new BigDecimal("200.00"));
        inventarioSucursalRepository.save(inventario2);

        // Generar token JWT
        token = jwtUtil.generateToken("admin@nexoo.com");
    }

    @Test
    @DisplayName("Test 1: POST /api/cotizaciones - Debe crear cotización exitosamente y retornar 201")
    void testCrearCotizacion() throws Exception {
        // Given
        DetalleCotizacionDTO detalle = new DetalleCotizacionDTO();
        detalle.setSkuInterno(skuProducto1);
        detalle.setCantidad(5);
        detalle.setPrecioUnitario(new BigDecimal("150.00"));

        CotizacionRequestDTO request = new CotizacionRequestDTO();
        request.setClienteId(clienteId);
        request.setSucursalId(sucursalId);
        request.setVendedorId(vendedorId);
        request.setFechaValidez(LocalDate.now().plusDays(15));
        request.setNotas("Cotización de prueba");
        request.setTerminosCondiciones("Precio válido por 15 días");
        request.setDetalles(Arrays.asList(detalle));

        // When & Then
        mockMvc.perform(post("/api/cotizaciones")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.folio").exists())
                .andExpect(jsonPath("$.folio").value(startsWith("COT-")))
                .andExpect(jsonPath("$.estado").value("BORRADOR"))
                .andExpect(jsonPath("$.clienteId").value(clienteId))
                .andExpect(jsonPath("$.sucursalId").value(sucursalId))
                .andExpect(jsonPath("$.vendedorId").value(vendedorId))
                .andExpect(jsonPath("$.notas").value("Cotización de prueba"))
                .andExpect(jsonPath("$.detalles").isArray())
                .andExpect(jsonPath("$.detalles[0].skuInterno").value(skuProducto1))
                .andExpect(jsonPath("$.detalles[0].cantidad").value(5))
                .andExpect(jsonPath("$.total").value(870.00));
    }

    @Test
    @DisplayName("Test 2: PUT /api/cotizaciones/{id} - Debe actualizar cotización exitosamente y retornar 200")
    void testActualizarCotizacion() throws Exception {
        // Given - Crear cotización primero via HTTP
        DetalleCotizacionDTO detalle = new DetalleCotizacionDTO();
        detalle.setSkuInterno(skuProducto1);
        detalle.setCantidad(5);
        detalle.setPrecioUnitario(new BigDecimal("150.00"));

        CotizacionRequestDTO requestCrear = new CotizacionRequestDTO();
        requestCrear.setClienteId(clienteId);
        requestCrear.setSucursalId(sucursalId);
        requestCrear.setVendedorId(vendedorId);
        requestCrear.setFechaValidez(LocalDate.now().plusDays(15));
        requestCrear.setNotas("Cotización para actualizar");
        requestCrear.setDetalles(Arrays.asList(detalle));

        String responseCrear = mockMvc.perform(post("/api/cotizaciones")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestCrear)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long cotizacionId = objectMapper.readTree(responseCrear).get("id").asLong();

        // Actualizar la cotización
        DetalleCotizacionDTO detalleActualizado = new DetalleCotizacionDTO();
        detalleActualizado.setSkuInterno(skuProducto2);
        detalleActualizado.setCantidad(3);
        detalleActualizado.setPrecioUnitario(new BigDecimal("300.00"));

        CotizacionRequestDTO request = new CotizacionRequestDTO();
        request.setClienteId(clienteId);
        request.setSucursalId(sucursalId);
        request.setVendedorId(vendedorId);
        request.setFechaValidez(LocalDate.now().plusDays(30));
        request.setNotas("Cotización actualizada");
        request.setDetalles(Arrays.asList(detalleActualizado));

        // When & Then
        mockMvc.perform(put("/api/cotizaciones/" + cotizacionId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(cotizacionId))
                .andExpect(jsonPath("$.notas").value("Cotización actualizada"))
                .andExpect(jsonPath("$.detalles[0].skuInterno").value(skuProducto2))
                .andExpect(jsonPath("$.detalles[0].cantidad").value(3));
    }

    @Test
    @DisplayName("Test 3: GET /api/cotizaciones/{id} - Debe obtener cotización por ID y retornar 200")
    void testObtenerCotizacionPorId() throws Exception {
        // Given - Crear cotización via HTTP POST
        DetalleCotizacionDTO detalle = new DetalleCotizacionDTO();
        detalle.setSkuInterno(skuProducto1);
        detalle.setCantidad(5);
        detalle.setPrecioUnitario(new BigDecimal("150.00"));

        CotizacionRequestDTO requestCrear = new CotizacionRequestDTO();
        requestCrear.setClienteId(clienteId);
        requestCrear.setSucursalId(sucursalId);
        requestCrear.setVendedorId(vendedorId);
        requestCrear.setFechaValidez(LocalDate.now().plusDays(15));
        requestCrear.setNotas("Cotización de prueba");
        requestCrear.setDetalles(Arrays.asList(detalle));

        String responseCrear = mockMvc.perform(post("/api/cotizaciones")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestCrear)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long cotizacionId = objectMapper.readTree(responseCrear).get("id").asLong();
        String folio = objectMapper.readTree(responseCrear).get("folio").asText();

        // When & Then
        mockMvc.perform(get("/api/cotizaciones/" + cotizacionId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(cotizacionId))
                .andExpect(jsonPath("$.folio").value(folio))
                .andExpect(jsonPath("$.estado").value("BORRADOR"))
                .andExpect(jsonPath("$.clienteId").value(clienteId))
                .andExpect(jsonPath("$.detalles").isArray());
    }

    @Test
    @DisplayName("Test 4: GET /api/cotizaciones/folio/{folio} - Debe obtener cotización por folio y retornar 200")
    void testObtenerCotizacionPorFolio() throws Exception {
        // Given - Crear cotización via HTTP POST
        DetalleCotizacionDTO detalle = new DetalleCotizacionDTO();
        detalle.setSkuInterno(skuProducto1);
        detalle.setCantidad(5);
        detalle.setPrecioUnitario(new BigDecimal("150.00"));

        CotizacionRequestDTO requestCrear = new CotizacionRequestDTO();
        requestCrear.setClienteId(clienteId);
        requestCrear.setSucursalId(sucursalId);
        requestCrear.setVendedorId(vendedorId);
        requestCrear.setFechaValidez(LocalDate.now().plusDays(15));
        requestCrear.setNotas("Cotización de prueba");
        requestCrear.setDetalles(Arrays.asList(detalle));

        String responseCrear = mockMvc.perform(post("/api/cotizaciones")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestCrear)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String folio = objectMapper.readTree(responseCrear).get("folio").asText();
        Long cotizacionId = objectMapper.readTree(responseCrear).get("id").asLong();

        // When & Then
        mockMvc.perform(get("/api/cotizaciones/folio/" + folio)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.folio").value(folio))
                .andExpect(jsonPath("$.id").value(cotizacionId))
                .andExpect(jsonPath("$.estado").value("BORRADOR"));
    }

    @Test
    @DisplayName("Test 5: GET /api/cotizaciones - Debe listar cotizaciones con paginación y retornar 200")
    void testListarCotizaciones() throws Exception {
        // Given - Crear 3 cotizaciones via HTTP POST
        DetalleCotizacionDTO detalle = new DetalleCotizacionDTO();
        detalle.setSkuInterno(skuProducto1);
        detalle.setCantidad(5);
        detalle.setPrecioUnitario(new BigDecimal("150.00"));

        CotizacionRequestDTO requestCrear = new CotizacionRequestDTO();
        requestCrear.setClienteId(clienteId);
        requestCrear.setSucursalId(sucursalId);
        requestCrear.setVendedorId(vendedorId);
        requestCrear.setFechaValidez(LocalDate.now().plusDays(15));
        requestCrear.setNotas("Cotización 1");
        requestCrear.setDetalles(Arrays.asList(detalle));

        mockMvc.perform(post("/api/cotizaciones")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestCrear)))
                .andExpect(status().isCreated());

        requestCrear.setNotas("Cotización 2");
        mockMvc.perform(post("/api/cotizaciones")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestCrear)))
                .andExpect(status().isCreated());

        requestCrear.setNotas("Cotización 3");
        mockMvc.perform(post("/api/cotizaciones")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestCrear)))
                .andExpect(status().isCreated());

        // When & Then
        mockMvc.perform(get("/api/cotizaciones")
                        .header("Authorization", "Bearer " + token)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(3))))
                .andExpect(jsonPath("$.totalElements").value(greaterThanOrEqualTo(3)))
                .andExpect(jsonPath("$.pageable").exists());
    }

    @Test
    @DisplayName("Test 6: PUT /api/cotizaciones/{id}/estado - Debe cambiar estado y retornar 200")
    void testCambiarEstado() throws Exception {
        // Given - Crear cotización via HTTP POST
        DetalleCotizacionDTO detalle = new DetalleCotizacionDTO();
        detalle.setSkuInterno(skuProducto1);
        detalle.setCantidad(3);
        detalle.setPrecioUnitario(new BigDecimal("200.00"));

        CotizacionRequestDTO requestCrear = new CotizacionRequestDTO();
        requestCrear.setClienteId(clienteId);
        requestCrear.setSucursalId(sucursalId);
        requestCrear.setVendedorId(vendedorId);
        requestCrear.setFechaValidez(LocalDate.now().plusDays(15));
        requestCrear.setNotas("Cotización para cambiar estado");
        requestCrear.setDetalles(Arrays.asList(detalle));

        String responseCrear = mockMvc.perform(post("/api/cotizaciones")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestCrear)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long cotizacionId = objectMapper.readTree(responseCrear).get("id").asLong();

        CambiarEstadoRequestDTO request = new CambiarEstadoRequestDTO();
        request.setNuevoEstado("ENVIADA");

        // When & Then
        mockMvc.perform(put("/api/cotizaciones/" + cotizacionId + "/estado")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(cotizacionId))
                .andExpect(jsonPath("$.estado").value("ENVIADA"));
    }

    @Test
    @DisplayName("Test 7: POST /api/cotizaciones/{id}/convertir-venta - Debe convertir a venta y retornar 201")
    void testConvertirAVenta() throws Exception {
        // Given - Crear cotización via HTTP POST
        DetalleCotizacionDTO detalle = new DetalleCotizacionDTO();
        detalle.setSkuInterno(skuProducto1);
        detalle.setCantidad(2);
        detalle.setPrecioUnitario(new BigDecimal("300.00"));

        CotizacionRequestDTO requestCrear = new CotizacionRequestDTO();
        requestCrear.setClienteId(clienteId);
        requestCrear.setSucursalId(sucursalId);
        requestCrear.setVendedorId(vendedorId);
        requestCrear.setFechaValidez(LocalDate.now().plusDays(15));
        requestCrear.setNotas("Cotización para convertir a venta");
        requestCrear.setDetalles(Arrays.asList(detalle));

        String responseCrear = mockMvc.perform(post("/api/cotizaciones")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestCrear)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long cotizacionId = objectMapper.readTree(responseCrear).get("id").asLong();

        // Cambiar estado a ENVIADA
        CambiarEstadoRequestDTO cambiarEstado = new CambiarEstadoRequestDTO();
        cambiarEstado.setNuevoEstado("ENVIADA");
        mockMvc.perform(put("/api/cotizaciones/" + cotizacionId + "/estado")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cambiarEstado)))
                .andExpect(status().isOk());

        // Cambiar estado a ACEPTADA
        cambiarEstado.setNuevoEstado("ACEPTADA");
        mockMvc.perform(put("/api/cotizaciones/" + cotizacionId + "/estado")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cambiarEstado)))
                .andExpect(status().isOk());

        ConvertirVentaRequestDTO request = new ConvertirVentaRequestDTO();
        request.setMetodoPago("EFECTIVO");

        // When & Then
        mockMvc.perform(post("/api/cotizaciones/" + cotizacionId + "/convertir-venta")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ventaId").exists())
                .andExpect(jsonPath("$.cotizacionId").value(cotizacionId));
    }

    @Test
    @DisplayName("Test 8: GET /api/cotizaciones/estadisticas - Debe obtener estadísticas y retornar 200")
    void testObtenerEstadisticas() throws Exception {
        // Given - Crear 2 cotizaciones via HTTP POST
        DetalleCotizacionDTO detalle = new DetalleCotizacionDTO();
        detalle.setSkuInterno(skuProducto1);
        detalle.setCantidad(1);
        detalle.setPrecioUnitario(new BigDecimal("100.00"));

        CotizacionRequestDTO requestCrear = new CotizacionRequestDTO();
        requestCrear.setClienteId(clienteId);
        requestCrear.setSucursalId(sucursalId);
        requestCrear.setVendedorId(vendedorId);
        requestCrear.setFechaValidez(LocalDate.now().plusDays(15));
        requestCrear.setNotas("Estadística 1");
        requestCrear.setDetalles(Arrays.asList(detalle));

        mockMvc.perform(post("/api/cotizaciones")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestCrear)))
                .andExpect(status().isCreated());

        requestCrear.setNotas("Estadística 2");
        mockMvc.perform(post("/api/cotizaciones")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestCrear)))
                .andExpect(status().isCreated());

        // When & Then
        mockMvc.perform(get("/api/cotizaciones/estadisticas")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCotizaciones").exists())
                .andExpect(jsonPath("$.cotizacionesBorrador").exists())
                .andExpect(jsonPath("$.cotizacionesEnviadas").exists())
                .andExpect(jsonPath("$.cotizacionesAceptadas").exists())
                .andExpect(jsonPath("$.cotizacionesRechazadas").exists())
                .andExpect(jsonPath("$.cotizacionesVencidas").exists())
                .andExpect(jsonPath("$.cotizacionesConvertidas").exists())
                .andExpect(jsonPath("$.tasaConversion").exists());
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @DisplayName("Test 9: DELETE /api/cotizaciones/{id} - Debe eliminar cotización y retornar 200")
    void testEliminarCotizacion() throws Exception {
        // Given - Crear cotización via HTTP POST
        DetalleCotizacionDTO detalle = new DetalleCotizacionDTO();
        detalle.setSkuInterno(skuProducto1);
        detalle.setCantidad(1);
        detalle.setPrecioUnitario(new BigDecimal("100.00"));

        CotizacionRequestDTO requestCrear = new CotizacionRequestDTO();
        requestCrear.setClienteId(clienteId);
        requestCrear.setSucursalId(sucursalId);
        requestCrear.setVendedorId(vendedorId);
        requestCrear.setFechaValidez(LocalDate.now().plusDays(15));
        requestCrear.setNotas("Cotización para eliminar");
        requestCrear.setDetalles(Arrays.asList(detalle));

        String responseCrear = mockMvc.perform(post("/api/cotizaciones")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestCrear)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long cotizacionId = objectMapper.readTree(responseCrear).get("id").asLong();

        // When & Then
        mockMvc.perform(delete("/api/cotizaciones/" + cotizacionId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        // Verificar que se eliminó
        mockMvc.perform(get("/api/cotizaciones/" + cotizacionId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Test 10: POST /api/cotizaciones/marcar-vencidas - Debe marcar vencidas y retornar 200")
    void testMarcarVencidas() throws Exception {
        // Given - Crear cotización vencida via HTTP POST
        DetalleCotizacionDTO detalle = new DetalleCotizacionDTO();
        detalle.setSkuInterno(skuProducto1);
        detalle.setCantidad(2);
        detalle.setPrecioUnitario(new BigDecimal("250.00"));

        CotizacionRequestDTO requestCrear = new CotizacionRequestDTO();
        requestCrear.setClienteId(clienteId);
        requestCrear.setSucursalId(sucursalId);
        requestCrear.setVendedorId(vendedorId);
        requestCrear.setFechaValidez(LocalDate.now().minusDays(5)); // Vencida hace 5 días
        requestCrear.setNotas("Cotización vencida");
        requestCrear.setDetalles(Arrays.asList(detalle));

        String responseCrear = mockMvc.perform(post("/api/cotizaciones")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestCrear)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long cotizacionId = objectMapper.readTree(responseCrear).get("id").asLong();

        // Cambiar estado a ENVIADA para que pueda ser marcada como vencida
        CambiarEstadoRequestDTO cambiarEstado = new CambiarEstadoRequestDTO();
        cambiarEstado.setNuevoEstado("ENVIADA");
        mockMvc.perform(put("/api/cotizaciones/" + cotizacionId + "/estado")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cambiarEstado)))
                .andExpect(status().isOk());

        // When & Then
        mockMvc.perform(post("/api/cotizaciones/marcar-vencidas")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cantidadActualizadas").exists())
                .andExpect(jsonPath("$.cantidadActualizadas").value(greaterThanOrEqualTo(1)));
    }

    @Test
    @DisplayName("Test 11: POST /api/cotizaciones - Debe retornar 400 cuando faltan campos obligatorios")
    void testCrearCotizacion_CamposInvalidos() throws Exception {
        // Given - Request sin campos obligatorios
        CotizacionRequestDTO request = new CotizacionRequestDTO();
        request.setClienteId(null); // Campo obligatorio faltante

        // When & Then
        mockMvc.perform(post("/api/cotizaciones")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Test 12: GET /api/cotizaciones/{id} - Debe retornar 404 cuando la cotización no existe")
    void testObtenerCotizacion_NoExiste() throws Exception {
        // When
        var result = mockMvc.perform(get("/api/cotizaciones/99999")
                        .header("Authorization", "Bearer " + token))
                .andDo(print());  // Imprimir respuesta para debugging
        
        // Capturar status y body independientemente del código
        int status = result.andReturn().getResponse().getStatus();
        String body = result.andReturn().getResponse().getContentAsString();
        
        System.out.println("========== TEST OBTENER COTIZACION NO EXISTE ==========");
        System.out.println("Status: " + status);
        System.out.println("Body: " + body);
        System.out.println("=====================================================");
        
        // Then - ahora sí validar que sea 404
        result.andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Test 13: POST /api/cotizaciones - Debe retornar 403 sin autenticación")
    void testCrearCotizacion_SinAutenticacion() throws Exception {
        // Given
        DetalleCotizacionDTO detalle = new DetalleCotizacionDTO();
        detalle.setSkuInterno(skuProducto1);
        detalle.setCantidad(5);
        detalle.setPrecioUnitario(new BigDecimal("150.00"));

        CotizacionRequestDTO request = new CotizacionRequestDTO();
        request.setClienteId(clienteId);
        request.setSucursalId(sucursalId);
        request.setFechaValidez(LocalDate.now().plusDays(15));
        request.setDetalles(Arrays.asList(detalle));

        // When & Then - Sin token
        mockMvc.perform(post("/api/cotizaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

}
