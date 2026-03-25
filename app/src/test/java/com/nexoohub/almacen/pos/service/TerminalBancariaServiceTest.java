package com.nexoohub.almacen.pos.service;

import com.nexoohub.almacen.common.exception.ResourceNotFoundException;
import com.nexoohub.almacen.pos.dto.CancelacionPagoRequest;
import com.nexoohub.almacen.pos.dto.PagoTarjetaRequest;
import com.nexoohub.almacen.pos.dto.TransaccionBancariaResponse;
import com.nexoohub.almacen.pos.entity.LogTransaccionBancaria;
import com.nexoohub.almacen.pos.repository.LogTransaccionBancariaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TerminalBancariaService — POS-02 Pruebas Unitarias")
class TerminalBancariaServiceTest {

    @Mock
    private LogTransaccionBancariaRepository repository;

    @InjectMocks
    private TerminalBancariaService service;

    private PagoTarjetaRequest pagoRequest;
    private CancelacionPagoRequest cancelRequest;
    private LogTransaccionBancaria logAprobado;

    @BeforeEach
    void setUp() {
        pagoRequest = new PagoTarjetaRequest();
        pagoRequest.setReferenciaVenta("VTA-001");
        pagoRequest.setMonto(new BigDecimal("150.00"));
        pagoRequest.setTerminalId("TERM-1");

        cancelRequest = new CancelacionPagoRequest();
        cancelRequest.setReferenciaVenta("VTA-001");
        cancelRequest.setTerminalId("TERM-1");

        logAprobado = new LogTransaccionBancaria();
        logAprobado.setId(1);
        logAprobado.setReferenciaVenta("VTA-001");
        logAprobado.setTipoOperacion("VENTA");
        logAprobado.setMonto(new BigDecimal("150.00"));
        logAprobado.setEstatus("APROBADO");
        logAprobado.setAutorizacionBanco("123ABC");
        logAprobado.setTerminalId("TERM-1");
    }

    @Test
    @DisplayName("procesarPagoTarjeta → guarda log como APROBADO")
    void procesarPagoTarjeta_exito() {
        when(repository.save(any(LogTransaccionBancaria.class))).thenAnswer(invocation -> {
            LogTransaccionBancaria tx = invocation.getArgument(0);
            tx.setId(100);
            return tx;
        });

        TransaccionBancariaResponse response = service.procesarPagoTarjeta(pagoRequest);

        assertThat(response).isNotNull();
        assertThat(response.getEstatus()).isEqualTo("APROBADO");
        assertThat(response.getTipoOperacion()).isEqualTo("VENTA");
        assertThat(response.getMonto()).isEqualByComparingTo("150.00");
        verify(repository, times(1)).save(any(LogTransaccionBancaria.class));
    }

    @Test
    @DisplayName("cancelarPago → éxito si transacción original es APROBADA")
    void cancelarPago_exito() {
        when(repository.findByReferenciaVenta("VTA-001")).thenReturn(Optional.of(logAprobado));
        
        // Simular save del nuevo log de cancelación y la actualización del original
        when(repository.save(any(LogTransaccionBancaria.class))).thenAnswer(invocation -> {
            LogTransaccionBancaria tx = invocation.getArgument(0);
            if (tx.getTipoOperacion().equals("CANCELACION")) {
                tx.setId(101);
                return tx;
            }
            return tx;
        });

        TransaccionBancariaResponse response = service.cancelarPago(cancelRequest);

        assertThat(response).isNotNull();
        assertThat(response.getEstatus()).isEqualTo("CANCELADO");
        assertThat(response.getTipoOperacion()).isEqualTo("CANCELACION");
        assertThat(logAprobado.getEstatus()).isEqualTo("CANCELADO"); // El original se marcó cancelado
        verify(repository, times(2)).save(any(LogTransaccionBancaria.class));
    }

    @Test
    @DisplayName("cancelarPago → error si transacción no existe")
    void cancelarPago_noExiste_lanzaExcepcion() {
        when(repository.findByReferenciaVenta("VTA-001")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.cancelarPago(cancelRequest))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("No se encontró transacción");

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("cancelarPago → error si transacción no está APROBADA")
    void cancelarPago_noAprobada_lanzaExcepcion() {
        logAprobado.setEstatus("RECHAZADO");
        when(repository.findByReferenciaVenta("VTA-001")).thenReturn(Optional.of(logAprobado));

        assertThatThrownBy(() -> service.cancelarPago(cancelRequest))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Solo se pueden cancelar");

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("consultarEstatus → retorna DTO correctamente")
    void consultarEstatus_exito() {
        when(repository.findByReferenciaVenta("VTA-001")).thenReturn(Optional.of(logAprobado));

        TransaccionBancariaResponse response = service.consultarEstatus("VTA-001");

        assertThat(response).isNotNull();
        assertThat(response.getReferenciaVenta()).isEqualTo("VTA-001");
        assertThat(response.getEstatus()).isEqualTo("APROBADO");
    }

    @Test
    @DisplayName("consultarEstatus → lanza excepción si no existe")
    void consultarEstatus_noExiste_lanzaExcepcion() {
        when(repository.findByReferenciaVenta("VTA-002")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.consultarEstatus("VTA-002"))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("Transacción no encontrada");
    }
}
