package com.nexoohub.almacen.inventario.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexoohub.almacen.catalogo.entity.Categoria;
import com.nexoohub.almacen.catalogo.entity.Moto;
import com.nexoohub.almacen.catalogo.repository.CategoriaRepository;
import com.nexoohub.almacen.catalogo.repository.MotoRepository;
import com.nexoohub.almacen.common.config.JwtUtil;
import com.nexoohub.almacen.common.entity.Usuario;
import com.nexoohub.almacen.common.repository.UsuarioRepository;
import com.nexoohub.almacen.inventario.entity.InventarioSucursal;
import com.nexoohub.almacen.inventario.entity.InventarioSucursalId;
import com.nexoohub.almacen.inventario.entity.ProductoMaestro;
import com.nexoohub.almacen.inventario.repository.InventarioSucursalRepository;
import com.nexoohub.almacen.inventario.repository.ProductoMaestroRepository;
import com.nexoohub.almacen.sucursal.entity.Sucursal;
import com.nexoohub.almacen.sucursal.repository.SucursalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ProductoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductoMaestroRepository productoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private MotoRepository motoRepository;

    @Autowired
    private SucursalRepository sucursalRepository;

    @Autowired
    private InventarioSucursalRepository inventarioRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;
    private Integer sucursalId;

    @BeforeEach
    void setUp() {
        inventarioRepository.deleteAll();
        productoRepository.deleteAll();
        categoriaRepository.deleteAll();
        motoRepository.deleteAll();
        sucursalRepository.deleteAll();
        usuarioRepository.deleteAll();

        Usuario usuario = new Usuario();
        usuario.setUsername("admin@nexoo.com");
        usuario.setPassword("$2a$10$test");
        usuario.setRole("ROLE_ADMIN");
        usuarioRepository.save(usuario);

        Categoria categoria = new Categoria();
        categoria.setNombre("Frenos");
        categoriaRepository.save(categoria);

        Moto moto = new Moto();
        moto.setMarca("Honda");
        moto.setModelo("CBR 600RR");
        moto.setCilindrada(600);
        motoRepository.save(moto);

        Sucursal sucursal = new Sucursal();
        sucursal.setNombre("Sucursal Centro");
        sucursal.setDireccion("Av. Principal 123");
        sucursal.setActivo(true);
        sucursalId = sucursalRepository.save(sucursal).getId();

        token = jwtUtil.generateToken("admin@nexoo.com");
    }

    @Test
    void crearProducto_Exitoso() throws Exception {
        ProductoMaestro producto = new ProductoMaestro();
        producto.setSkuInterno("FILTRO001");
        producto.setNombreComercial("Filtro de Aceite Honda");
        producto.setClaveSat("12345678");
        producto.setStockMinimoGlobal(10);
        producto.setActivo(true);

        mockMvc.perform(post("/api/v1/productos")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(producto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.exitoso").value(true))
                .andExpect(jsonPath("$.mensaje").value("Producto creado correctamente"))
                .andExpect(jsonPath("$.skuInterno").value("FILTRO001"));

        assertThat(productoRepository.findById("FILTRO001")).isPresent();
    }

    @Test
    void crearProducto_SkuVacio() throws Exception {
        ProductoMaestro producto = new ProductoMaestro();
        producto.setSkuInterno("");
        producto.setNombreComercial("Test");

        mockMvc.perform(post("/api/v1/productos")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(producto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void obtenerPorSku_Exitoso() throws Exception {
        ProductoMaestro producto = new ProductoMaestro();
        producto.setSkuInterno("PASTILLA001");
        producto.setNombreComercial("Pastillas de Freno");
        producto.setActivo(true);
        productoRepository.save(producto);

        mockMvc.perform(get("/api/v1/productos/PASTILLA001")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Producto encontrado"))
                .andExpect(jsonPath("$.datos.skuInterno").value("PASTILLA001"))
                .andExpect(jsonPath("$.datos.nombreComercial").value("Pastillas de Freno"));
    }

    @Test
    void obtenerPorSku_NoExiste() throws Exception {
        mockMvc.perform(get("/api/v1/productos/NOEXISTE")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void actualizarProducto_Exitoso() throws Exception {
        ProductoMaestro producto = new ProductoMaestro();
        producto.setSkuInterno("ACEITE001");
        producto.setNombreComercial("Aceite Original");
        producto.setClaveSat("11111111");
        producto.setStockMinimoGlobal(5);
        producto.setActivo(true);
        productoRepository.save(producto);

        ProductoMaestro actualizado = new ProductoMaestro();
        actualizado.setSkuInterno("ACEITE001");
        actualizado.setNombreComercial("Aceite Actualizado");
        actualizado.setClaveSat("22222222");
        actualizado.setStockMinimoGlobal(15);
        actualizado.setActivo(true);

        mockMvc.perform(put("/api/v1/productos/ACEITE001")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(actualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Producto actualizado con éxito"))
                .andExpect(jsonPath("$.datos.nombreComercial").value("Aceite Actualizado"))
                .andExpect(jsonPath("$.datos.claveSat").value("22222222"));

        ProductoMaestro verificar = productoRepository.findById("ACEITE001").orElseThrow();
        assertThat(verificar.getNombreComercial()).isEqualTo("Aceite Actualizado");
        assertThat(verificar.getStockMinimoGlobal()).isEqualTo(15);
    }

    @Test
    void actualizarProducto_NoExiste() throws Exception {
        ProductoMaestro actualizado = new ProductoMaestro();
        actualizado.setSkuInterno("NOEXISTE");
        actualizado.setNombreComercial("Test");
        actualizado.setActivo(true);

        mockMvc.perform(put("/api/v1/productos/NOEXISTE")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(actualizado)))
                .andExpect(status().isNotFound());
    }

    @Test
    void eliminarProducto_Exitoso() throws Exception {
        ProductoMaestro producto = new ProductoMaestro();
        producto.setSkuInterno("DELETE001");
        producto.setNombreComercial("Producto a Eliminar");
        producto.setActivo(true);
        productoRepository.save(producto);

        assertThat(productoRepository.findById("DELETE001")).isPresent();

        mockMvc.perform(delete("/api/v1/productos/DELETE001")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Producto eliminado correctamente"));

        assertThat(productoRepository.findById("DELETE001")).isEmpty();
    }

    @Test
    void eliminarProducto_NoExiste() throws Exception {
        mockMvc.perform(delete("/api/v1/productos/NOEXISTE")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void buscarProductos_SinFiltros() throws Exception {
        ProductoMaestro p1 = new ProductoMaestro();
        p1.setSkuInterno("PROD001");
        p1.setNombreComercial("Producto Uno");
        p1.setActivo(true);
        productoRepository.save(p1);

        ProductoMaestro p2 = new ProductoMaestro();
        p2.setSkuInterno("PROD002");
        p2.setNombreComercial("Producto Dos");
        p2.setActivo(true);
        productoRepository.save(p2);

        mockMvc.perform(get("/api/v1/productos/search")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2));
    }

    @Test
    void buscarProductos_ConQuery() throws Exception {
        ProductoMaestro p1 = new ProductoMaestro();
        p1.setSkuInterno("FILTRO001");
        p1.setNombreComercial("Filtro de Aceite");
        p1.setActivo(true);
        productoRepository.save(p1);

        ProductoMaestro p2 = new ProductoMaestro();
        p2.setSkuInterno("PASTILLA001");
        p2.setNombreComercial("Pastillas de Freno");
        p2.setActivo(true);
        productoRepository.save(p2);

        mockMvc.perform(get("/api/v1/productos/search?q=Filtro")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].skuInterno").value("FILTRO001"));
    }

    @Test
    void buscarParaMostrador_Exitoso() throws Exception {
        // Crear producto con inventario
        ProductoMaestro producto = new ProductoMaestro();
        producto.setSkuInterno("MOSTRADOR001");
        producto.setNombreComercial("Producto Mostrador");
        producto.setActivo(true);
        productoRepository.save(producto);

        InventarioSucursal inventario = new InventarioSucursal();
        inventario.setId(new InventarioSucursalId(sucursalId, "MOSTRADOR001"));
        inventario.setStockActual(50);
        inventario.setCostoPromedioPonderado(new BigDecimal("100.00"));
        inventarioRepository.save(inventario);

        mockMvc.perform(get("/api/v1/productos/mostrador?q=Mostrador&sucursalId=" + sucursalId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void sinAutenticacion_RetornaForbidden() throws Exception {
        mockMvc.perform(get("/api/v1/productos/search"))
                .andExpect(status().isForbidden());
    }
}
