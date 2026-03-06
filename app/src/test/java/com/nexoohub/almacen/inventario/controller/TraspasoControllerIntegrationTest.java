package com.nexoohub.almacen.inventario.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexoohub.almacen.common.config.JwtUtil;
import com.nexoohub.almacen.common.entity.Usuario;
import com.nexoohub.almacen.common.repository.UsuarioRepository;
import com.nexoohub.almacen.inventario.dto.TraspasoRequestDTO;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class TraspasoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private InventarioSucursalRepository inventarioRepository;

    @Autowired
    private ProductoMaestroRepository productoRepository;

    @Autowired
    private SucursalRepository sucursalRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;
    private Integer sucursalOrigenId;
    private Integer sucursalDestinoId;

    @BeforeEach
    void setUp() {
        inventarioRepository.deleteAll();
        productoRepository.deleteAll();
        sucursalRepository.deleteAll();
        usuarioRepository.deleteAll();

        Usuario usuario = new Usuario();
        usuario.setUsername("admin@nexoo.com");
        usuario.setPassword("$2a$10$test");
        usuario.setRole("ROLE_ADMIN");
        usuarioRepository.save(usuario);

        Sucursal sucursalOrigen = new Sucursal();
        sucursalOrigen.setNombre("Sucursal Centro");
        sucursalOrigen.setDireccion("Av. Principal 123");
        sucursalOrigen.setActivo(true);
        sucursalOrigenId = sucursalRepository.save(sucursalOrigen).getId();

        Sucursal sucursalDestino = new Sucursal();
        sucursalDestino.setNombre("Sucursal Norte");
        sucursalDestino.setDireccion("Calle Norte 456");
        sucursalDestino.setActivo(true);
        sucursalDestinoId = sucursalRepository.save(sucursalDestino).getId();

        token = jwtUtil.generateToken("admin@nexoo.com");
    }

    @Test
    void realizarTraspaso_Exitoso() throws Exception {
        // Crear producto
        ProductoMaestro producto = new ProductoMaestro();
        producto.setSkuInterno("TRASPASO001");
        producto.setNombreComercial("Producto a Traspasar");
        producto.setActivo(true);
        productoRepository.save(producto);

        // Inventario en sucursal origen
        InventarioSucursal invOrigen = new InventarioSucursal();
        invOrigen.setId(new InventarioSucursalId(sucursalOrigenId, "TRASPASO001"));
        invOrigen.setStockActual(100);
        invOrigen.setCostoPromedioPonderado(new BigDecimal("150.00"));
        inventarioRepository.save(invOrigen);

        // Inventario en sucursal destino (inicial en 0)
        InventarioSucursal invDestino = new InventarioSucursal();
        invDestino.setId(new InventarioSucursalId(sucursalDestinoId, "TRASPASO001"));
        invDestino.setStockActual(0);
        invDestino.setCostoPromedioPonderado(BigDecimal.ZERO);
        inventarioRepository.save(invDestino);

        // Crear solicitud de traspaso
        TraspasoRequestDTO.ItemTraspasoDTO item = new TraspasoRequestDTO.ItemTraspasoDTO();
        item.setSkuInterno("TRASPASO001");
        item.setCantidad(20);

        TraspasoRequestDTO request = new TraspasoRequestDTO();
        request.setSucursalOrigenId(sucursalOrigenId);
        request.setSucursalDestinoId(sucursalDestinoId);
        request.setComentarios("Traspaso de prueba");
        request.setItems(List.of(item));

        mockMvc.perform(post("/api/v1/inventario/traspasos")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exitoso").value(true))
                .andExpect(jsonPath("$.mensaje").value("Traspaso completado correctamente"))
                .andExpect(jsonPath("$.rastreoId").exists());

        // Verificar que se actualizaron los inventarios
        InventarioSucursal origenActualizado = inventarioRepository.findById(
                new InventarioSucursalId(sucursalOrigenId, "TRASPASO001")
        ).orElseThrow();
        assertThat(origenActualizado.getStockActual()).isEqualTo(80); // 100 - 20

        InventarioSucursal destinoActualizado = inventarioRepository.findById(
                new InventarioSucursalId(sucursalDestinoId, "TRASPASO001")
        ).orElseThrow();
        assertThat(destinoActualizado.getStockActual()).isEqualTo(20); // 0 + 20
    }

    @Test
    void realizarTraspaso_OrigenNulo() throws Exception {
        TraspasoRequestDTO.ItemTraspasoDTO item = new TraspasoRequestDTO.ItemTraspasoDTO();
        item.setSkuInterno("TEST001");
        item.setCantidad(10);

        TraspasoRequestDTO request = new TraspasoRequestDTO();
        request.setSucursalOrigenId(null);
        request.setSucursalDestinoId(sucursalDestinoId);
        request.setItems(List.of(item));

        mockMvc.perform(post("/api/v1/inventario/traspasos")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void realizarTraspaso_DestinoNulo() throws Exception {
        TraspasoRequestDTO.ItemTraspasoDTO item = new TraspasoRequestDTO.ItemTraspasoDTO();
        item.setSkuInterno("TEST001");
        item.setCantidad(10);

        TraspasoRequestDTO request = new TraspasoRequestDTO();
        request.setSucursalOrigenId(sucursalOrigenId);
        request.setSucursalDestinoId(null);
        request.setItems(List.of(item));

        mockMvc.perform(post("/api/v1/inventario/traspasos")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void realizarTraspaso_ItemsVacio() throws Exception {
        TraspasoRequestDTO request = new TraspasoRequestDTO();
        request.setSucursalOrigenId(sucursalOrigenId);
        request.setSucursalDestinoId(sucursalDestinoId);
        request.setItems(List.of());

        mockMvc.perform(post("/api/v1/inventario/traspasos")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void sinAutenticacion_RetornaForbidden() throws Exception {
        TraspasoRequestDTO request = new TraspasoRequestDTO();
        request.setSucursalOrigenId(sucursalOrigenId);
        request.setSucursalDestinoId(sucursalDestinoId);

        mockMvc.perform(post("/api/v1/inventario/traspasos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }
}
