package com.nexoohub.almacen.analitica.controller;

import com.nexoohub.almacen.analitica.entity.ReglaAsociacionProductos;
import com.nexoohub.almacen.analitica.repository.ReglaAsociacionProductosRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class MarketBasketControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ReglaAsociacionProductosRepository reglaRepository;

    @Test
    @WithMockUser(roles = "ADMIN")
    void recalcularReglas_ConRolAdmin_DeberiaRetornar200() throws Exception {
        mockMvc.perform(post("/api/v1/analitica/canasta/calcular")
                        .param("minSoporte", "0.01")
                        .param("minConfianza", "0.05"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("success")));
    }

    @Test
    @WithMockUser(roles = "VENDEDOR")
    void recalcularReglas_ConRolVendedor_DeberiaRetornar403() throws Exception {
        mockMvc.perform(post("/api/v1/analitica/canasta/calcular"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "VENDEDOR")
    void obtenerSugerencias_DeberiaRetornarMejoresMatches() throws Exception {
        // Arrange
        reglaRepository.deleteAll();
        ReglaAsociacionProductos regla1 = new ReglaAsociacionProductos();
        regla1.setSkuOrigen("ITEM-1");
        regla1.setSkuDestino("ITEM-A");
        regla1.setSoporte(0.4);
        regla1.setConfianza(0.9);
        regla1.setLift(2.0);
        reglaRepository.save(regla1);

        ReglaAsociacionProductos regla2 = new ReglaAsociacionProductos();
        regla2.setSkuOrigen("ITEM-1");
        regla2.setSkuDestino("ITEM-B");
        regla2.setSoporte(0.2);
        regla2.setConfianza(0.5);
        regla2.setLift(1.5);
        reglaRepository.save(regla2);

        // Act & Assert
        mockMvc.perform(get("/api/v1/analitica/canasta/ITEM-1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.skuOrigen", is("ITEM-1")))
                .andExpect(jsonPath("$.recomendaciones", hasSize(2)))
                .andExpect(jsonPath("$.recomendaciones[0].skuDestino", is("ITEM-A")))
                .andExpect(jsonPath("$.recomendaciones[1].skuDestino", is("ITEM-B")));
                
        reglaRepository.deleteAll(); // Clean up
    }
}
