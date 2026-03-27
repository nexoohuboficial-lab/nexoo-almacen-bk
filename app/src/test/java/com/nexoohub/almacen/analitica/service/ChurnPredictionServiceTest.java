package com.nexoohub.almacen.analitica.service;

import com.nexoohub.almacen.analitica.entity.PrediccionChurnCliente;
import com.nexoohub.almacen.analitica.repository.PrediccionChurnClienteRepository;
import com.nexoohub.almacen.catalogo.entity.Cliente;
import com.nexoohub.almacen.catalogo.repository.ClienteRepository;
import com.nexoohub.almacen.ventas.entity.Venta;
import com.nexoohub.almacen.ventas.repository.VentaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChurnPredictionServiceTest {

    @Mock
    private PrediccionChurnClienteRepository churnRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private VentaRepository ventaRepository;

    @InjectMocks
    private ChurnPredictionService churnPredictionService;

    private Cliente clienteTest;

    @BeforeEach
    void setUp() {
        clienteTest = new Cliente();
        clienteTest.setId(1);
    }

    @Test
    void calcularRiesgoChurnGlobal_ClienteMasDe90Dias() {
        // Arrange
        when(clienteRepository.findAll()).thenReturn(Collections.singletonList(clienteTest));

        Venta ventaAntigua = new Venta();
        ventaAntigua.setFechaVenta(LocalDateTime.now().minusDays(100)); // Más de 90 días
        ventaAntigua.setTotal(new BigDecimal("100.00"));
        
        when(ventaRepository.findByClienteId(1)).thenReturn(Collections.singletonList(ventaAntigua));
        when(churnRepository.findByClienteId(1)).thenReturn(Optional.empty());

        // Act
        churnPredictionService.calcularRiesgoChurnGlobal();

        // Assert
        ArgumentCaptor<PrediccionChurnCliente> captor = ArgumentCaptor.forClass(PrediccionChurnCliente.class);
        verify(churnRepository).save(captor.capture());
        
        PrediccionChurnCliente resultado = captor.getValue();
        assertEquals(99, resultado.getScoreRiesgo(), "El score debe ser 99 para más de 90 días sin compras");
        assertTrue(resultado.getFactoresRiesgo().contains("Abandonado"));
    }

    @Test
    void calcularRiesgoChurnGlobal_ClienteSaludable() {
        // Arrange
        when(clienteRepository.findAll()).thenReturn(Collections.singletonList(clienteTest));

        Venta v1 = new Venta();
        v1.setFechaVenta(LocalDateTime.now().minusDays(20)); // Hace 20 días
        v1.setTotal(new BigDecimal("100.00"));
        
        Venta v2 = new Venta();
        v2.setFechaVenta(LocalDateTime.now().minusDays(10)); // Hace 10 días
        v2.setTotal(new BigDecimal("110.00"));

        when(ventaRepository.findByClienteId(1)).thenReturn(Arrays.asList(v1, v2));
        when(churnRepository.findByClienteId(1)).thenReturn(Optional.empty());

        // Act
        churnPredictionService.calcularRiesgoChurnGlobal();

        // Assert
        ArgumentCaptor<PrediccionChurnCliente> captor = ArgumentCaptor.forClass(PrediccionChurnCliente.class);
        verify(churnRepository).save(captor.capture());
        
        PrediccionChurnCliente resultado = captor.getValue();
        assertEquals(0, resultado.getScoreRiesgo(), "El score debe ser 0 para un cliente saludable");
        assertEquals(10, resultado.getFrecuenciaPromedioDias()); // 20 - 10 = 10 días de diferencia entre 1ra y ultima / 1
        assertTrue(resultado.getFactoresRiesgo().contains("saludable"));
    }

    @Test
    void calcularRiesgoChurnGlobal_ClienteCaeFrecuenciaYMonto() {
        // Arrange
        when(clienteRepository.findAll()).thenReturn(Collections.singletonList(clienteTest));

        Venta v1 = new Venta();
        v1.setFechaVenta(LocalDateTime.now().minusDays(50));
        v1.setTotal(new BigDecimal("200.00"));
        
        Venta v2 = new Venta();
        v2.setFechaVenta(LocalDateTime.now().minusDays(40)); 
        v2.setTotal(new BigDecimal("200.00"));
        
        // La frecuencia promedio será de 10 días. (50 a 40 = 10 días)
        // Pero la última compra (v2) fue hace 40 días, lo que es > 10 * 2 = 20 días. (Score +50)
        // Y el promedio es 200, la ultima tmb es 200, entonces monto no cayó. Score = 50.
        // Simularemos una caída de monto:
        v2.setTotal(new BigDecimal("50.00")); // Promedio = 125. 50 es < 125 * 0.70 (87.5). Por tanto monto cayó (Score +25)
        // Score esperado = 75.

        when(ventaRepository.findByClienteId(1)).thenReturn(Arrays.asList(v1, v2));
        when(churnRepository.findByClienteId(1)).thenReturn(Optional.empty());

        // Act
        churnPredictionService.calcularRiesgoChurnGlobal();

        // Assert
        ArgumentCaptor<PrediccionChurnCliente> captor = ArgumentCaptor.forClass(PrediccionChurnCliente.class);
        verify(churnRepository).save(captor.capture());
        
        PrediccionChurnCliente resultado = captor.getValue();
        assertEquals(75, resultado.getScoreRiesgo());
        assertTrue(resultado.getFactoresRiesgo().contains("doble de su frecuencia"));
        assertTrue(resultado.getFactoresRiesgo().contains("menor a su promedio histórico"));
    }
}
