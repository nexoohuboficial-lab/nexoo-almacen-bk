package com.nexoohub.almacen.inventario.controller;

import com.nexoohub.almacen.inventario.service.AnalisisABCService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import static org.mockito.Mockito.mock;

/**
 * Configuración de test para AnalisisABCController
 * Fuerza el uso de mocks en lugar de beans reales
 */
@TestConfiguration
public class AnalisisABCControllerTestConfig {

    @Bean
    @Primary
    public AnalisisABCService analisisABCService() {
        return mock(AnalisisABCService.class);
    }
}
