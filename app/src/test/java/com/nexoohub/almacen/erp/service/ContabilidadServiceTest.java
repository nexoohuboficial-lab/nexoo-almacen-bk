package com.nexoohub.almacen.erp.service;

import com.nexoohub.almacen.common.exception.ResourceNotFoundException;
import com.nexoohub.almacen.erp.dto.MovimientoContableRequest;
import com.nexoohub.almacen.erp.dto.PolizaContableRequest;
import com.nexoohub.almacen.erp.dto.PolizaContableResponse;
import com.nexoohub.almacen.erp.dto.reportes.BalanzaComprobacionResponse;
import com.nexoohub.almacen.erp.dto.reportes.EstadoResultadosResponse;
import com.nexoohub.almacen.erp.entity.CuentaContable;
import com.nexoohub.almacen.erp.entity.MovimientoContable;
import com.nexoohub.almacen.erp.entity.PolizaContable;
import com.nexoohub.almacen.erp.repository.CuentaContableRepository;
import com.nexoohub.almacen.erp.repository.MovimientoContableRepository;
import com.nexoohub.almacen.erp.repository.PolizaContableRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ContabilidadService — ERP-02 Pruebas Unitarias")
class ContabilidadServiceTest {

    @Mock
    private CuentaContableRepository cuentaRepo;

    @Mock
    private PolizaContableRepository polizaRepo;

    @Mock
    private MovimientoContableRepository movimientoRepo;

    @InjectMocks
    private ContabilidadService contabilidadService;

    private PolizaContableRequest polizaReq;
    private CuentaContable cuentaCaja;
    private CuentaContable cuentaVentas;
    private CuentaContable cuentaGastos;

    @BeforeEach
    void setUp() {
        cuentaCaja = new CuentaContable();
        cuentaCaja.setId(1);
        cuentaCaja.setCodigo("102");
        cuentaCaja.setActiva(true);
        cuentaCaja.setNombre("Caja General");
        cuentaCaja.setNaturaleza("DEUDORA");

        cuentaVentas = new CuentaContable();
        cuentaVentas.setId(2);
        cuentaVentas.setCodigo("401");
        cuentaVentas.setActiva(true);
        cuentaVentas.setNombre("Ventas Netas");
        cuentaVentas.setNaturaleza("ACREEDORA");

        cuentaGastos = new CuentaContable();
        cuentaGastos.setId(3);
        cuentaGastos.setCodigo("601");
        cuentaGastos.setActiva(true);
        cuentaGastos.setNombre("Gastos Admin");
        cuentaGastos.setNaturaleza("DEUDORA");

        polizaReq = new PolizaContableRequest();
        polizaReq.setNumeroPoliza("POL-001");
        polizaReq.setFecha(LocalDate.now());
        polizaReq.setTipoPoliza("INGRESO");
        polizaReq.setConcepto("Venta del día");
        polizaReq.setUsuarioId(1);

        MovimientoContableRequest mCargo = new MovimientoContableRequest();
        mCargo.setCuentaId(1); // Caja
        mCargo.setCargo(new BigDecimal("1000.00"));
        mCargo.setAbono(BigDecimal.ZERO);

        MovimientoContableRequest mAbono = new MovimientoContableRequest();
        mAbono.setCuentaId(2); // Ventas
        mAbono.setCargo(BigDecimal.ZERO);
        mAbono.setAbono(new BigDecimal("1000.00"));

        polizaReq.setMovimientos(Arrays.asList(mCargo, mAbono));
    }

    @Test
    @DisplayName("registrarPoliza → éxito (Partida doble cuadra)")
    void registrarPoliza_exito() {
        when(polizaRepo.findByNumeroPoliza("POL-001")).thenReturn(Optional.empty());
        when(cuentaRepo.findById(1)).thenReturn(Optional.of(cuentaCaja));
        when(cuentaRepo.findById(2)).thenReturn(Optional.of(cuentaVentas));
        when(polizaRepo.save(any(PolizaContable.class))).thenAnswer(i -> {
            PolizaContable p = i.getArgument(0);
            p.setId(100);
            return p;
        });

        PolizaContableResponse resp = contabilidadService.registrarPoliza(polizaReq);

        assertThat(resp.getTotalCargo()).isEqualByComparingTo("1000.00");
        assertThat(resp.getTotalAbono()).isEqualByComparingTo("1000.00");
        assertThat(resp.getMovimientos()).hasSize(2);
        verify(polizaRepo).save(any());
    }

    @Test
    @DisplayName("registrarPoliza → error (Partida doble no cuadra)")
    void registrarPoliza_noCuadra() {
        polizaReq.getMovimientos().get(1).setAbono(new BigDecimal("999.00")); // Descuadre de 1 peso

        when(polizaRepo.findByNumeroPoliza("POL-001")).thenReturn(Optional.empty());
        when(cuentaRepo.findById(1)).thenReturn(Optional.of(cuentaCaja));
        when(cuentaRepo.findById(2)).thenReturn(Optional.of(cuentaVentas));

        assertThatThrownBy(() -> contabilidadService.registrarPoliza(polizaReq))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Partida doble no cuadra");

        verify(polizaRepo, never()).save(any());
    }

    @Test
    @DisplayName("registrarPoliza → error (Cuenta inactiva)")
    void registrarPoliza_cuentaInactiva() {
        cuentaCaja.setActiva(false);

        when(polizaRepo.findByNumeroPoliza("POL-001")).thenReturn(Optional.empty());
        when(cuentaRepo.findById(1)).thenReturn(Optional.of(cuentaCaja));

        assertThatThrownBy(() -> contabilidadService.registrarPoliza(polizaReq))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("inactiva");
    }

    @Test
    @DisplayName("generarBalanza → éxito con cuadre perfecto")
    void generarBalanza_exito() {
        when(cuentaRepo.findByActivaTrueOrderByCodigoAsc()).thenReturn(Arrays.asList(cuentaCaja, cuentaVentas));

        // Simulamos los movimientos ya guardados
        MovimientoContable m1 = new MovimientoContable();
        m1.setCuentaId(1);
        m1.setCargo(new BigDecimal("1000.00"));
        m1.setAbono(BigDecimal.ZERO);

        MovimientoContable m2 = new MovimientoContable();
        m2.setCuentaId(2);
        m2.setCargo(BigDecimal.ZERO);
        m2.setAbono(new BigDecimal("1000.00"));

        when(movimientoRepo.findMovimientosPorFecha(any(), any())).thenReturn(Arrays.asList(m1, m2));

        BalanzaComprobacionResponse resp = contabilidadService.generarBalanza(LocalDate.now(), LocalDate.now());

        assertThat(resp.isCuadrada()).isTrue();
        assertThat(resp.getTotalCargos()).isEqualByComparingTo("1000.00");
        assertThat(resp.getTotalAbonos()).isEqualByComparingTo("1000.00");
        assertThat(resp.getCuentas()).hasSize(2);
        
        // El saldo de la cuenta DEUDORA (Caja) debe ser 1000
        assertThat(resp.getCuentas().get(0).getSaldoFinal()).isEqualByComparingTo("1000.00");
    }

    @Test
    @DisplayName("generarEstadoResultados → éxito calculando Utilidad Neta")
    void generarEstadoResultados_exito() {
        MovimientoContable mVentas = new MovimientoContable();
        mVentas.setCuentaId(2); // Ventas (401)
        mVentas.setCargo(BigDecimal.ZERO);
        mVentas.setAbono(new BigDecimal("5000.00")); // Ingreso de 5000

        MovimientoContable mGastos = new MovimientoContable();
        mGastos.setCuentaId(3); // Gastos (601)
        mGastos.setCargo(new BigDecimal("2000.00")); // Gasto de 2000
        mGastos.setAbono(BigDecimal.ZERO);

        when(movimientoRepo.findMovimientosPorFecha(any(), any())).thenReturn(Arrays.asList(mVentas, mGastos));
        when(cuentaRepo.findById(2)).thenReturn(Optional.of(cuentaVentas));
        when(cuentaRepo.findById(3)).thenReturn(Optional.of(cuentaGastos));

        EstadoResultadosResponse resp = contabilidadService.generarEstadoResultados(LocalDate.now(), LocalDate.now());

        assertThat(resp.getIngresosNetos()).isEqualByComparingTo("5000.00");
        assertThat(resp.getGastosOperacion()).isEqualByComparingTo("2000.00");
        assertThat(resp.getCostoVentas()).isEqualByComparingTo("0.00");
        assertThat(resp.getUtilidadNeta()).isEqualByComparingTo("3000.00"); // 5000 - 0 - 2000 = 3000
    }
}
