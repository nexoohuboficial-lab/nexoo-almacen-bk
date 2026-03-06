package com.nexoohub.almacen.finanzas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexoohub.almacen.common.config.JwtUtil;
import com.nexoohub.almacen.common.entity.Usuario;
import com.nexoohub.almacen.common.repository.UsuarioRepository;
import com.nexoohub.almacen.finanzas.entity.ConfiguracionFinanciera;
import com.nexoohub.almacen.finanzas.repository.ConfiguracionFinancieraRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
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
class ConfiguracionFinancieraControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ConfiguracionFinancieraRepository configuracionRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;

    @BeforeEach
    void setUp() {
        usuarioRepository.deleteAll();

        Usuario usuario = new Usuario();
        usuario.setUsername("admin@nexoo.com");
        usuario.setPassword("$2a$10$test");
        usuario.setRole("ROLE_ADMIN");
        usuarioRepository.save(usuario);

        token = jwtUtil.generateToken("admin@nexoo.com");
    }

    @Test
    @Sql(scripts = "/test-data/configuracion-financiera.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void obtenerParametros_Exitoso() throws Exception {
        mockMvc.perform(get("/api/v1/finanzas/parametros")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.iva").value(0.16))
                .andExpect(jsonPath("$.margenGananciaBase").value(0.30))
                .andExpect(jsonPath("$.gastosFijosMensuales").value(15000.00))
                .andExpect(jsonPath("$.metaVentasMensual").value(150000.00))
                .andExpect(jsonPath("$.comisionTarjeta").value(0.03));
    }

    @Test
    void obtenerParametros_NoExiste() throws Exception {
        configuracionRepository.deleteAll();

        mockMvc.perform(get("/api/v1/finanzas/parametros")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    @Sql(scripts = "/test-data/configuracion-financiera.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void actualizarParametros_Exitoso() throws Exception {
        ConfiguracionFinanciera actualizada = new ConfiguracionFinanciera();
        actualizada.setIva(new BigDecimal("0.18"));
        actualizada.setMargenGananciaBase(new BigDecimal("0.35"));
        actualizada.setGastosFijosMensuales(new BigDecimal("20000.00"));
        actualizada.setMetaVentasMensual(new BigDecimal("200000.00"));
        actualizada.setComisionTarjeta(new BigDecimal("0.04"));

        mockMvc.perform(put("/api/v1/finanzas/parametros")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(actualizada)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exitoso").value(true))
                .andExpect(jsonPath("$.mensaje").exists());

        // Verificar que se actualizó en BD
        ConfiguracionFinanciera verificar = configuracionRepository.findById(1).orElseThrow();
        assertThat(verificar.getIva()).isEqualByComparingTo(new BigDecimal("0.18"));
        assertThat(verificar.getMargenGananciaBase()).isEqualByComparingTo(new BigDecimal("0.35"));
        assertThat(verificar.getGastosFijosMensuales()).isEqualByComparingTo(new BigDecimal("20000.00"));
    }

    @Test
    void actualizarParametros_NoExiste() throws Exception {
        configuracionRepository.deleteAll();

        ConfiguracionFinanciera actualizada = new ConfiguracionFinanciera();
        actualizada.setIva(new BigDecimal("0.16"));
        actualizada.setMargenGananciaBase(new BigDecimal("0.30"));

        mockMvc.perform(put("/api/v1/finanzas/parametros")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(actualizada)))
                .andExpect(status().isNotFound());
    }

    @Test
    void sinAutenticacion_RetornaForbidden() throws Exception {
        mockMvc.perform(get("/api/v1/finanzas/parametros"))
                .andExpect(status().isForbidden());
    }
}
