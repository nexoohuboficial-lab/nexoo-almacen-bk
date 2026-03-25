package com.nexoohub.almacen.erp.service;

import com.nexoohub.almacen.common.exception.ResourceNotFoundException;
import com.nexoohub.almacen.erp.dto.*;
import com.nexoohub.almacen.erp.entity.CuentaPorPagar;
import com.nexoohub.almacen.erp.entity.PagoProveedor;
import com.nexoohub.almacen.erp.repository.CuentaPorPagarRepository;
import com.nexoohub.almacen.erp.repository.PagoProveedorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CuentaPorPagarService — ERP-01 Pruebas Unitarias")
class CuentaPorPagarServiceTest {

    @Mock
    private CuentaPorPagarRepository cxpRepo;

    @Mock
    private PagoProveedorRepository pagoRepo;

    @InjectMocks
    private CuentaPorPagarService cxpService;

    private CuentaPorPagarRequest cxpRequest;
    private CuentaPorPagar cxpEntity;
    private PagoProveedorRequest pagoRequest;

    @BeforeEach
    void setUp() {
        cxpRequest = new CuentaPorPagarRequest();
        cxpRequest.setProveedorId(1);
        cxpRequest.setNumeroFactura("FAC-001");
        cxpRequest.setDescripcion("Compra de insumos");
        cxpRequest.setMontoTotal(new BigDecimal("5000.00"));
        cxpRequest.setFechaFactura(LocalDate.now().minusDays(10));
        cxpRequest.setFechaVencimiento(LocalDate.now().plusDays(20));
        cxpRequest.setSucursalId(1);

        cxpEntity = new CuentaPorPagar();
        cxpEntity.setId(1);
        cxpEntity.setProveedorId(1);
        cxpEntity.setNumeroFactura("FAC-001");
        cxpEntity.setMontoTotal(new BigDecimal("5000.00"));
        cxpEntity.setMontoPagado(BigDecimal.ZERO);
        cxpEntity.setSaldoPendiente(new BigDecimal("5000.00"));
        cxpEntity.setFechaFactura(LocalDate.now().minusDays(10));
        cxpEntity.setFechaVencimiento(LocalDate.now().plusDays(20));
        cxpEntity.setEstatus("PENDIENTE");

        pagoRequest = new PagoProveedorRequest();
        pagoRequest.setMontoAbono(new BigDecimal("2000.00"));
        pagoRequest.setMetodoPago("TRANSFERENCIA");
        pagoRequest.setFechaPago(LocalDate.now());
    }

    @Test
    @DisplayName("registrar → éxito: crea CxP con saldo completo")
    void registrar_exito() {
        when(cxpRepo.save(any(CuentaPorPagar.class))).thenAnswer(i -> {
            CuentaPorPagar c = i.getArgument(0);
            c.setId(10);
            return c;
        });

        CuentaPorPagarResponse resp = cxpService.registrar(cxpRequest);

        assertThat(resp.getEstatus()).isEqualTo("PENDIENTE");
        assertThat(resp.getSaldoPendiente()).isEqualByComparingTo("5000.00");
        assertThat(resp.getDiasAntiguedad()).isGreaterThanOrEqualTo(10);
        verify(cxpRepo).save(any());
    }

    @Test
    @DisplayName("abonar → éxito: abono parcial cambia estatus a PARCIAL")
    void abonar_parcial() {
        when(cxpRepo.findById(1)).thenReturn(Optional.of(cxpEntity));
        when(pagoRepo.save(any(PagoProveedor.class))).thenAnswer(i -> i.getArgument(0));
        when(cxpRepo.save(any(CuentaPorPagar.class))).thenAnswer(i -> i.getArgument(0));

        CuentaPorPagarResponse resp = cxpService.abonar(1, pagoRequest);

        assertThat(resp.getEstatus()).isEqualTo("PARCIAL");
        assertThat(resp.getMontoPagado()).isEqualByComparingTo("2000.00");
        assertThat(resp.getSaldoPendiente()).isEqualByComparingTo("3000.00");
    }

    @Test
    @DisplayName("abonar → éxito: abono total cambia estatus a PAGADA")
    void abonar_total() {
        pagoRequest.setMontoAbono(new BigDecimal("5000.00"));
        when(cxpRepo.findById(1)).thenReturn(Optional.of(cxpEntity));
        when(pagoRepo.save(any(PagoProveedor.class))).thenAnswer(i -> i.getArgument(0));
        when(cxpRepo.save(any(CuentaPorPagar.class))).thenAnswer(i -> i.getArgument(0));

        CuentaPorPagarResponse resp = cxpService.abonar(1, pagoRequest);

        assertThat(resp.getEstatus()).isEqualTo("PAGADA");
        assertThat(resp.getSaldoPendiente()).isEqualByComparingTo("0.00");
    }

    @Test
    @DisplayName("abonar → error: CxP no encontrada")
    void abonar_noExiste() {
        when(cxpRepo.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cxpService.abonar(999, pagoRequest))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("abonar → error: CxP ya pagada")
    void abonar_yaPagada() {
        cxpEntity.setEstatus("PAGADA");
        when(cxpRepo.findById(1)).thenReturn(Optional.of(cxpEntity));

        assertThatThrownBy(() -> cxpService.abonar(1, pagoRequest))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("PAGADA");
    }

    @Test
    @DisplayName("abonar → error: abono excede saldo")
    void abonar_excedeSaldo() {
        pagoRequest.setMontoAbono(new BigDecimal("9999.99"));
        when(cxpRepo.findById(1)).thenReturn(Optional.of(cxpEntity));

        assertThatThrownBy(() -> cxpService.abonar(1, pagoRequest))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("excede");
    }

    @Test
    @DisplayName("listarPendientes → devuelve lista combinada PENDIENTE + PARCIAL")
    void listarPendientes_exito() {
        when(cxpRepo.findByEstatusOrderByFechaVencimientoAsc("PENDIENTE"))
            .thenReturn(new java.util.ArrayList<>(Collections.singletonList(cxpEntity)));
        when(cxpRepo.findByEstatusOrderByFechaVencimientoAsc("PARCIAL"))
            .thenReturn(Collections.emptyList());

        List<CuentaPorPagarResponse> result = cxpService.listarPendientes();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNumeroFactura()).isEqualTo("FAC-001");
    }
}
