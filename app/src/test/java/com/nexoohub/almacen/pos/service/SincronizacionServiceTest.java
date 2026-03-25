package com.nexoohub.almacen.pos.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexoohub.almacen.common.exception.ResourceNotFoundException;
import com.nexoohub.almacen.pos.dto.LoteSincronizacionResponse;
import com.nexoohub.almacen.pos.dto.SyncLoteRequest;
import com.nexoohub.almacen.pos.dto.VentaOfflineSyncDTO;
import com.nexoohub.almacen.pos.entity.LoteSincronizacion;
import com.nexoohub.almacen.pos.repository.LoteSincronizacionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SincronizacionService — POS-04 Pruebas Unitarias")
class SincronizacionServiceTest {

    @Mock
    private LoteSincronizacionRepository loteRepo;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private SincronizacionService sincronizacionService;

    private SyncLoteRequest syncLoteRequest;
    private LoteSincronizacion loteGuardado;
    private VentaOfflineSyncDTO venta1;
    private VentaOfflineSyncDTO ventaInvalida;

    @BeforeEach
    void setUp() {
        venta1 = new VentaOfflineSyncDTO();
        venta1.setReferenciaLocal("LOC-001");
        venta1.setMontoTotal(new BigDecimal("100.50"));
        venta1.setMetodoDePago("EFECTIVO");
        venta1.setFechaCobroOffline(LocalDateTime.now().minusHours(1));

        ventaInvalida = new VentaOfflineSyncDTO();
        ventaInvalida.setReferenciaLocal("LOC-ERR");
        ventaInvalida.setMontoTotal(new BigDecimal("-10.00")); // Monto inválido
        ventaInvalida.setMetodoDePago("EFECTIVO");
        ventaInvalida.setFechaCobroOffline(LocalDateTime.now());

        syncLoteRequest = new SyncLoteRequest();
        syncLoteRequest.setCodigoLote("LOTE-123");
        syncLoteRequest.setSucursalId(1);
        syncLoteRequest.setCajaId(1);
        syncLoteRequest.setUsuarioId(10);
        syncLoteRequest.setFechaGeneracion(LocalDateTime.now());
        syncLoteRequest.setVentas(Collections.singletonList(venta1));

        loteGuardado = new LoteSincronizacion();
        loteGuardado.setId(1);
        loteGuardado.setCodigoLote("LOTE-123");
        loteGuardado.setEstatus("FALLIDO");
        loteGuardado.setTotalVentas(1);
        loteGuardado.setVentasProcesadas(0);
        loteGuardado.setIntentos(1);
        loteGuardado.setMontoTotalLote(new BigDecimal("100.50"));
        loteGuardado.setFechaSincronizacion(LocalDateTime.now());
    }

    @Test
    @DisplayName("procesarLote → exito: procesa ventas y guarda como PROCESADO")
    void procesarLote_exito() throws JsonProcessingException {
        when(loteRepo.findByCodigoLote("LOTE-123")).thenReturn(Optional.empty());
        when(objectMapper.writeValueAsString(anyList())).thenReturn("[{\"ref\":\"LOC-001\"}]");
        when(loteRepo.save(any(LoteSincronizacion.class))).thenAnswer(i -> {
            LoteSincronizacion l = i.getArgument(0);
            l.setId(100);
            return l;
        });

        LoteSincronizacionResponse response = sincronizacionService.procesarLote(syncLoteRequest);

        assertThat(response.getCodigoLote()).isEqualTo("LOTE-123");
        assertThat(response.getEstatus()).isEqualTo("PROCESADO");
        assertThat(response.getTotalVentas()).isEqualTo(1);
        assertThat(response.getVentasProcesadas()).isEqualTo(1);
        assertThat(response.getMontoTotalLote()).isEqualByComparingTo("100.50");
        verify(loteRepo).save(any(LoteSincronizacion.class));
    }

    @Test
    @DisplayName("procesarLote → error: lote duplicado lanza excepcion")
    void procesarLote_duplicado_lanzaExcepcion() {
        when(loteRepo.findByCodigoLote("LOTE-123")).thenReturn(Optional.of(loteGuardado));

        assertThatThrownBy(() -> sincronizacionService.procesarLote(syncLoteRequest))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("ya fue registrado previamente");

        verify(loteRepo, never()).save(any());
    }

    @Test
    @DisplayName("procesarLote → guarda como FALLIDO si ventas tienen error")
    void procesarLote_ventaInvalida_guardaFallido() throws JsonProcessingException {
        syncLoteRequest.setVentas(Arrays.asList(venta1, ventaInvalida));
        when(loteRepo.findByCodigoLote("LOTE-123")).thenReturn(Optional.empty());
        when(objectMapper.writeValueAsString(anyList())).thenReturn("[...]");
        when(loteRepo.save(any(LoteSincronizacion.class))).thenAnswer(i -> i.getArgument(0));

        LoteSincronizacionResponse response = sincronizacionService.procesarLote(syncLoteRequest);

        assertThat(response.getEstatus()).isEqualTo("FALLIDO");
        assertThat(response.getTotalVentas()).isEqualTo(2);
        assertThat(response.getVentasProcesadas()).isEqualTo(0);
        assertThat(response.getErroresDetalle()).contains("monto inválido");
    }

    @Test
    @DisplayName("reintentarLote → exito")
    void reintentarLote_exito() {
        loteGuardado.setEstatus("FALLIDO");
        when(loteRepo.findByCodigoLote("LOTE-123")).thenReturn(Optional.of(loteGuardado));
        when(loteRepo.save(any(LoteSincronizacion.class))).thenAnswer(i -> i.getArgument(0));

        LoteSincronizacionResponse response = sincronizacionService.reintentarLote("LOTE-123");

        assertThat(response.getEstatus()).isEqualTo("PROCESADO");
        assertThat(response.getIntentos()).isEqualTo(2);
        assertThat(response.getVentasProcesadas()).isEqualTo(1); // Mismo total ventas
    }

    @Test
    @DisplayName("reintentarLote → error: la lote no existe")
    void reintentarLote_noExiste_lanzaExcepcion() {
        when(loteRepo.findByCodigoLote("LOTE-404")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sincronizacionService.reintentarLote("LOTE-404"))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("No se encontró el lote");
    }

    @Test
    @DisplayName("reintentarLote → error: la lote ya esta procesada")
    void reintentarLote_yaProcesado_lanzaExcepcion() {
        loteGuardado.setEstatus("PROCESADO");
        when(loteRepo.findByCodigoLote("LOTE-123")).thenReturn(Optional.of(loteGuardado));

        assertThatThrownBy(() -> sincronizacionService.reintentarLote("LOTE-123"))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("ya se encuentra PROCESADO");
    }

    @Test
    @DisplayName("listarLotesFallidos → retorna lista correcta")
    void listarLotesFallidos_exito() {
        when(loteRepo.findByEstatus("FALLIDO")).thenReturn(Collections.singletonList(loteGuardado));

        List<LoteSincronizacionResponse> result = sincronizacionService.listarLotesFallidos();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCodigoLote()).isEqualTo("LOTE-123");
    }
}
