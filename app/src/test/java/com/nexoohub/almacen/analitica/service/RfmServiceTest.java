package com.nexoohub.almacen.analitica.service;

import com.nexoohub.almacen.analitica.dto.RfmCalcularResponse;
import com.nexoohub.almacen.analitica.entity.SegmentoRfmCliente;
import com.nexoohub.almacen.analitica.mapper.RfmMapper;
import com.nexoohub.almacen.analitica.repository.SegmentoRfmClienteRepository;
import com.nexoohub.almacen.catalogo.entity.Cliente;
import com.nexoohub.almacen.ventas.entity.Venta;
import com.nexoohub.almacen.ventas.repository.VentaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RfmServiceTest {

    @Mock
    private SegmentoRfmClienteRepository rfmRepository;

    @Mock
    private VentaRepository ventaRepository;

    @Mock
    private RfmMapper rfmMapper;

    @InjectMocks
    private RfmService rfmService;

    private Cliente cliente1;
    private Cliente cliente2;

    @BeforeEach
    void setUp() {
        cliente1 = new Cliente();
        cliente1.setId(1);
        cliente1.setNombre("Cliente Frecuente");

        cliente2 = new Cliente();
        cliente2.setId(2);
        cliente2.setNombre("Cliente Perdido");
    }

    @Test
    void calcularRfmMasivo_VentasValidas_ActualizaSegmentos() {
        // Arrange: Crear ventas simuladas
        Venta v1 = new Venta();
        v1.setCliente(cliente1);
        v1.setTotal(new BigDecimal("1000.00"));
        v1.setFechaVenta(LocalDateTime.now().minusDays(2)); // Reciente (2 días)

        Venta v2 = new Venta();
        v2.setCliente(cliente1);
        v2.setTotal(new BigDecimal("2000.00"));
        v2.setFechaVenta(LocalDateTime.now().minusDays(5));

        Venta v3 = new Venta();
        v3.setCliente(cliente2);
        v3.setTotal(new BigDecimal("100.00"));
        v3.setFechaVenta(LocalDateTime.now().minusDays(365)); // Hace un año (Perdido)

        when(ventaRepository.findAll()).thenReturn(Arrays.asList(v1, v2, v3));
        when(rfmRepository.findByClienteId(anyInt())).thenReturn(Optional.empty());

        // Act
        RfmCalcularResponse response = rfmService.calcularRfmMasivo();

        // Assert
        assertNotNull(response);
        assertEquals(2, response.getClientesEvaluados()); // Dos clientes distintos evaluados
        verify(rfmRepository, times(2)).save(any(SegmentoRfmCliente.class));
    }

    @Test
    void calcularRfmMasivo_SinVentas_RetornaCeroClientesyMensaje() {
        // Arrange
        when(ventaRepository.findAll()).thenReturn(new ArrayList<>());

        // Act
        RfmCalcularResponse response = rfmService.calcularRfmMasivo();

        // Assert
        assertEquals(0, response.getClientesEvaluados());
        verify(rfmRepository, never()).save(any());
    }
}
