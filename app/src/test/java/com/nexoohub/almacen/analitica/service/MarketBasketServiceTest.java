package com.nexoohub.almacen.analitica.service;

import com.nexoohub.almacen.analitica.entity.ReglaAsociacionProductos;
import com.nexoohub.almacen.analitica.repository.ReglaAsociacionProductosRepository;
import com.nexoohub.almacen.ventas.entity.DetalleVenta;
import com.nexoohub.almacen.ventas.entity.Venta;
import com.nexoohub.almacen.ventas.repository.VentaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MarketBasketServiceTest {

    @Mock
    private VentaRepository ventaRepository;

    @Mock
    private ReglaAsociacionProductosRepository reglaRepository;

    @InjectMocks
    private MarketBasketService marketBasketService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void calcularReglasAsociacion_DeberiaCalcularSoporteConfianzaLiftCorrectamente() {
        // Arrange
        // Transaccion 1: A, B
        Venta v1 = crearVentaMock(1, "SKU-A", "SKU-B");
        // Transaccion 2: A, C
        Venta v2 = crearVentaMock(2, "SKU-A", "SKU-C");
        // Transaccion 3: A, B, C
        Venta v3 = crearVentaMock(3, "SKU-A", "SKU-B", "SKU-C");
        // Transaccion 4: B, C
        Venta v4 = crearVentaMock(4, "SKU-B", "SKU-C");

        when(ventaRepository.findAll()).thenReturn(Arrays.asList(v1, v2, v3, v4));

        // Act
        // Min Soporte = 0.25 (1/4 transacciones), Min Confianza = 0.5 (50%)
        marketBasketService.calcularReglasAsociacion(0.25, 0.5);

        // Assert
        verify(reglaRepository).deleteAll();
        
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<ReglaAsociacionProductos>> captor = ArgumentCaptor.forClass((Class)List.class);
        verify(reglaRepository).saveAll(captor.capture());

        List<ReglaAsociacionProductos> reglasGuardadas = captor.getValue();
        assertThat(reglasGuardadas).isNotEmpty();

        // Validamos la regla: SKU-A -> SKU-B
        // Total Tx = 4
        // F(A) = 3 (v1, v2, v3)
        // F(B) = 3 (v1, v3, v4)
        // F(A y B) = 2 (v1, v3)
        // Soporte = 2/4 = 0.5
        // Confianza A->B = 2/3 = 0.666...
        // Lift A->B = Confianza(A->B) / Soporte(B) = (2/3) / 0.75 = 0.888...
        
        ReglaAsociacionProductos reglaAB = reglasGuardadas.stream()
                .filter(r -> "SKU-A".equals(r.getSkuOrigen()) && "SKU-B".equals(r.getSkuDestino()))
                .findFirst()
                .orElseThrow();

        assertThat(reglaAB.getSoporte()).isEqualTo(0.5);
        assertThat(reglaAB.getConfianza()).isBetween(0.66, 0.67);
        assertThat(reglaAB.getLift()).isBetween(0.88, 0.89);
    }

    private Venta crearVentaMock(int id, String... skus) {
        Venta venta = new Venta();
        venta.setId(id);
        
        List<DetalleVenta> detalles = Arrays.stream(skus).map(sku -> {
            DetalleVenta d = new DetalleVenta();
            d.setVenta(venta);
            d.setSkuInterno(sku);
            return d;
        }).toList();
        
        venta.setDetalles(detalles);
        return venta;
    }
}
