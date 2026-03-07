package com.nexoohub.almacen.inventario.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexoohub.almacen.catalogo.entity.Categoria;
import com.nexoohub.almacen.catalogo.entity.CompatibilidadProducto;
import com.nexoohub.almacen.catalogo.entity.Moto;
import com.nexoohub.almacen.catalogo.entity.Proveedor;
import com.nexoohub.almacen.catalogo.repository.CategoriaRepository;
import com.nexoohub.almacen.catalogo.repository.CompatibilidadRepository;
import com.nexoohub.almacen.catalogo.repository.MotoRepository;
import com.nexoohub.almacen.catalogo.repository.ProveedorRepository;
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
    private ProveedorRepository proveedorRepository;

    @Autowired
    private CompatibilidadRepository compatibilidadRepository;

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

    @Test
    void buscarProductos_PorMarcaProducto() throws Exception {
        ProductoMaestro p1 = new ProductoMaestro();
        p1.setSkuInterno("HONDA001");
        p1.setNombreComercial("Filtro Honda");
        p1.setMarca("Honda");
        p1.setActivo(true);
        productoRepository.save(p1);

        ProductoMaestro p2 = new ProductoMaestro();
        p2.setSkuInterno("YAMAHA001");
        p2.setNombreComercial("Filtro Yamaha");
        p2.setMarca("Yamaha");
        p2.setActivo(true);
        productoRepository.save(p2);

        mockMvc.perform(get("/api/v1/productos/search?q=Honda")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].marca").value("HONDA")); // Auto-convertido a mayúsculas
    }

    @Test
    void buscarProductos_PorNombreCategoria() throws Exception {
        Categoria catAceites = new Categoria();
        catAceites.setNombre("Aceites");
        catAceites = categoriaRepository.save(catAceites);

        Categoria catFiltros = new Categoria();
        catFiltros.setNombre("Filtros");
        catFiltros = categoriaRepository.save(catFiltros);

        ProductoMaestro p1 = new ProductoMaestro();
        p1.setSkuInterno("ACEITE001");
        p1.setNombreComercial("Aceite 20W50");
        p1.setCategoria(catAceites);
        p1.setActivo(true);
        productoRepository.save(p1);

        ProductoMaestro p2 = new ProductoMaestro();
        p2.setSkuInterno("FILTRO001");
        p2.setNombreComercial("Filtro de Aceite");
        p2.setCategoria(catFiltros);
        p2.setActivo(true);
        productoRepository.save(p2);

        mockMvc.perform(get("/api/v1/productos/search?nombreCategoria=Aceites")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].skuInterno").value("ACEITE001"));
    }

    @Test
    void buscarProductos_PorNombreProveedor() throws Exception {
        Proveedor motul = new Proveedor();
        motul.setNombreEmpresa("Motul México");
        motul.setRfc("MOT123456789");
        motul = proveedorRepository.save(motul);

        Proveedor castrol = new Proveedor();
        castrol.setNombreEmpresa("Castrol México");
        castrol.setRfc("CAS123456789");
        castrol = proveedorRepository.save(castrol);

        ProductoMaestro p1 = new ProductoMaestro();
        p1.setSkuInterno("MOT001");
        p1.setNombreComercial("Aceite Motul");
        p1.setProveedor(motul);
        p1.setActivo(true);
        productoRepository.save(p1);

        ProductoMaestro p2 = new ProductoMaestro();
        p2.setSkuInterno("CAS001");
        p2.setNombreComercial("Aceite Castrol");
        p2.setProveedor(castrol);
        p2.setActivo(true);
        productoRepository.save(p2);

        mockMvc.perform(get("/api/v1/productos/search?nombreProveedor=Motul")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].skuInterno").value("MOT001"));
    }

    @Test
    void buscarProductos_PorMarcaMoto() throws Exception {
        Moto honda = new Moto();
        honda.setMarca("Honda");
        honda.setModelo("CBR600RR");
        honda.setCilindrada(600);
        honda = motoRepository.save(honda);

        Moto yamaha = new Moto();
        yamaha.setMarca("Yamaha");
        yamaha.setModelo("YZF-R6");
        yamaha.setCilindrada(600);
        yamaha = motoRepository.save(yamaha);

        ProductoMaestro p1 = new ProductoMaestro();
        p1.setSkuInterno("FILTRO_HONDA");
        p1.setNombreComercial("Filtro compatible Honda");
        p1.setActivo(true);
        productoRepository.save(p1);

        ProductoMaestro p2 = new ProductoMaestro();
        p2.setSkuInterno("FILTRO_YAMAHA");
        p2.setNombreComercial("Filtro compatible Yamaha");
        p2.setActivo(true);
        productoRepository.save(p2);

        CompatibilidadProducto comp1 = new CompatibilidadProducto();
        comp1.setSkuInterno("FILTRO_HONDA");
        comp1.setMoto(honda);
        comp1.setAnioInicio(2018);
        comp1.setAnioFin(2024);
        compatibilidadRepository.save(comp1);

        CompatibilidadProducto comp2 = new CompatibilidadProducto();
        comp2.setSkuInterno("FILTRO_YAMAHA");
        comp2.setMoto(yamaha);
        comp2.setAnioInicio(2018);
        comp2.setAnioFin(2024);
        compatibilidadRepository.save(comp2);

        mockMvc.perform(get("/api/v1/productos/search?marcaMoto=Honda")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].skuInterno").value("FILTRO_HONDA"));
    }

    @Test
    void buscarProductos_PorModeloMoto() throws Exception {
        Moto honda = new Moto();
        honda.setMarca("Honda");
        honda.setModelo("CBR600RR");
        honda.setCilindrada(600);
        honda = motoRepository.save(honda);

        ProductoMaestro p1 = new ProductoMaestro();
        p1.setSkuInterno("PASTILLA_CBR");
        p1.setNombreComercial("Pastillas CBR");
        p1.setActivo(true);
        productoRepository.save(p1);

        CompatibilidadProducto comp = new CompatibilidadProducto();
        comp.setSkuInterno("PASTILLA_CBR");
        comp.setMoto(honda);
        comp.setAnioInicio(2020);
        comp.setAnioFin(2024);
        compatibilidadRepository.save(comp);

        mockMvc.perform(get("/api/v1/productos/search?modeloMoto=CBR600")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].skuInterno").value("PASTILLA_CBR"));
    }

    @Test
    void buscarProductos_PorCilindrada() throws Exception {
        Moto moto250 = new Moto();
        moto250.setMarca("Kawasaki");
        moto250.setModelo("Ninja 250");
        moto250.setCilindrada(250);
        moto250 = motoRepository.save(moto250);

        Moto moto600 = new Moto();
        moto600.setMarca("Kawasaki");
        moto600.setModelo("Ninja 650");
        moto600.setCilindrada(650);
        moto600 = motoRepository.save(moto600);

        ProductoMaestro p1 = new ProductoMaestro();
        p1.setSkuInterno("ACEITE_250");
        p1.setNombreComercial("Aceite para 250cc");
        p1.setActivo(true);
        productoRepository.save(p1);

        ProductoMaestro p2 = new ProductoMaestro();
        p2.setSkuInterno("ACEITE_650");
        p2.setNombreComercial("Aceite para 650cc");
        p2.setActivo(true);
        productoRepository.save(p2);

        CompatibilidadProducto comp1 = new CompatibilidadProducto();
        comp1.setSkuInterno("ACEITE_250");
        comp1.setMoto(moto250);
        comp1.setAnioInicio(2015);
        comp1.setAnioFin(2024);
        compatibilidadRepository.save(comp1);

        CompatibilidadProducto comp2 = new CompatibilidadProducto();
        comp2.setSkuInterno("ACEITE_650");
        comp2.setMoto(moto600);
        comp2.setAnioInicio(2015);
        comp2.setAnioFin(2024);
        compatibilidadRepository.save(comp2);

        mockMvc.perform(get("/api/v1/productos/search?cilindrada=250")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].skuInterno").value("ACEITE_250"));
    }

    @Test
    void buscarProductos_BusquedaCombinada() throws Exception {
        Categoria catAceites = new Categoria();
        catAceites.setNombre("Aceites");
        catAceites = categoriaRepository.save(catAceites);

        Moto honda = new Moto();
        honda.setMarca("Honda");
        honda.setModelo("CBR600RR");
        honda.setCilindrada(600);
        honda = motoRepository.save(honda);

        ProductoMaestro p1 = new ProductoMaestro();
        p1.setSkuInterno("ACEITE_HONDA_600");
        p1.setNombreComercial("Aceite Honda 600");
        p1.setCategoria(catAceites);
        p1.setMarca("Honda");
        p1.setActivo(true);
        productoRepository.save(p1);

        CompatibilidadProducto comp = new CompatibilidadProducto();
        comp.setSkuInterno("ACEITE_HONDA_600");
        comp.setMoto(honda);
        comp.setAnioInicio(2020);
        comp.setAnioFin(2024);
        compatibilidadRepository.save(comp);

        // Búsqueda combinada: aceite para honda de 600cc
        mockMvc.perform(get("/api/v1/productos/search?nombreCategoria=Aceites&marcaMoto=Honda&cilindrada=600")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].skuInterno").value("ACEITE_HONDA_600"));
    }
}
