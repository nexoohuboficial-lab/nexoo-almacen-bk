package com.nexoohub.almacen.caja.service;

import com.nexoohub.almacen.caja.dto.*;
import com.nexoohub.almacen.caja.entity.MovimientoCaja;
import com.nexoohub.almacen.caja.entity.TurnoCaja;
import com.nexoohub.almacen.caja.repository.MovimientoCajaRepository;
import com.nexoohub.almacen.caja.repository.TurnoCajaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias del CajaService — POS-01.
 * Cubre: apertura de turno, regla de turno duplicado, movimientos por tipo,
 * tipo inválido, cierre con arqueo Z, diferencia positiva/negativa y resumen.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CajaService — POS-01 Arqueos de Caja")
class CajaServiceTest {

    @Mock
    private TurnoCajaRepository turnoRepo;

    @Mock
    private MovimientoCajaRepository movimientoRepo;

    @InjectMocks
    private CajaService cajaService;

    private AbrirTurnoRequest abrirRequest;
    private TurnoCaja turnoAbierto;

    @BeforeEach
    void setUp() {
        abrirRequest = new AbrirTurnoRequest();
        abrirRequest.setEmpleadoId(1);
        abrirRequest.setSucursalId(1);
        abrirRequest.setFondoInicial(new BigDecimal("500.00"));

        turnoAbierto = new TurnoCaja();
        turnoAbierto.setId(1);
        turnoAbierto.setEmpleadoId(1);
        turnoAbierto.setSucursalId(1);
        turnoAbierto.setFondoInicial(new BigDecimal("500.00"));
        turnoAbierto.setEfectivoEsperado(new BigDecimal("500.00"));
        turnoAbierto.setTotalVentasEfectivo(BigDecimal.ZERO);
        turnoAbierto.setTotalVentasTarjeta(BigDecimal.ZERO);
        turnoAbierto.setTotalVentasCredito(BigDecimal.ZERO);
        turnoAbierto.setTotalRetiros(BigDecimal.ZERO);
        turnoAbierto.setTotalIngresosExtra(BigDecimal.ZERO);
        turnoAbierto.setEstado("ABIERTO");
    }

    // ----------------------------------------------------------------
    // abrirTurno
    // ----------------------------------------------------------------

    @Test
    @DisplayName("abrirTurno → crea turno correctamente cuando no hay uno abierto")
    void abrirTurno_sinTurnoExistente_creaCorrectamente() {
        when(turnoRepo.findByEmpleadoIdAndSucursalIdAndEstado(1, 1, "ABIERTO")).thenReturn(Optional.empty());
        when(turnoRepo.save(any(TurnoCaja.class))).thenReturn(turnoAbierto);

        TurnoCaja resultado = cajaService.abrirTurno(abrirRequest);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getEstado()).isEqualTo("ABIERTO");
        verify(turnoRepo).save(any(TurnoCaja.class));
    }

    @Test
    @DisplayName("abrirTurno → lanza excepción si ya existe turno abierto para el empleado")
    void abrirTurno_conTurnoYaAbierto_lanzaExcepcion() {
        when(turnoRepo.findByEmpleadoIdAndSucursalIdAndEstado(1, 1, "ABIERTO"))
            .thenReturn(Optional.of(turnoAbierto));

        assertThatThrownBy(() -> cajaService.abrirTurno(abrirRequest))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Ya existe un turno ABIERTO");

        verify(turnoRepo, never()).save(any());
    }

    // ----------------------------------------------------------------
    // registrarMovimiento
    // ----------------------------------------------------------------

    @Test
    @DisplayName("registrarMovimiento VENTA_EFECTIVO → acumula correctamente el total")
    void registrarMovimiento_ventaEfectivo_acumulaTotal() {
        when(turnoRepo.findById(1)).thenReturn(Optional.of(turnoAbierto));
        when(turnoRepo.save(any())).thenReturn(turnoAbierto);
        MovimientoCaja movGuardado = new MovimientoCaja();
        movGuardado.setId(10);
        when(movimientoRepo.save(any())).thenReturn(movGuardado);

        MovimientoCajaRequest req = new MovimientoCajaRequest();
        req.setTurnoId(1);
        req.setTipo("VENTA_EFECTIVO");
        req.setMonto(new BigDecimal("200.00"));

        MovimientoCaja resultado = cajaService.registrarMovimiento(req);

        assertThat(resultado).isNotNull();
        assertThat(turnoAbierto.getTotalVentasEfectivo()).isEqualByComparingTo(new BigDecimal("200.00"));
        // Efectivo esperado = 500 + 200 = 700
        assertThat(turnoAbierto.getEfectivoEsperado()).isEqualByComparingTo(new BigDecimal("700.00"));
    }

    @Test
    @DisplayName("registrarMovimiento RETIRO → descuenta del efectivo esperado")
    void registrarMovimiento_retiro_descuentaDelEfectivo() {
        when(turnoRepo.findById(1)).thenReturn(Optional.of(turnoAbierto));
        when(turnoRepo.save(any())).thenReturn(turnoAbierto);
        when(movimientoRepo.save(any())).thenReturn(new MovimientoCaja());

        MovimientoCajaRequest req = new MovimientoCajaRequest();
        req.setTurnoId(1);
        req.setTipo("RETIRO");
        req.setMonto(new BigDecimal("100.00"));

        cajaService.registrarMovimiento(req);

        // Efectivo esperado = 500 - 100 = 400
        assertThat(turnoAbierto.getEfectivoEsperado()).isEqualByComparingTo(new BigDecimal("400.00"));
    }

    @Test
    @DisplayName("registrarMovimiento en turno CERRADO → lanza excepción")
    void registrarMovimiento_turnoCerrado_lanzaExcepcion() {
        turnoAbierto.setEstado("CERRADO");
        when(turnoRepo.findById(1)).thenReturn(Optional.of(turnoAbierto));

        MovimientoCajaRequest req = new MovimientoCajaRequest();
        req.setTurnoId(1);
        req.setTipo("VENTA_EFECTIVO");
        req.setMonto(new BigDecimal("100.00"));

        assertThatThrownBy(() -> cajaService.registrarMovimiento(req))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("turno CERRADO");
    }

    @Test
    @DisplayName("registrarMovimiento con tipo inválido → lanza IllegalArgumentException")
    void registrarMovimiento_tipoInvalido_lanzaExcepcion() {
        when(turnoRepo.findById(1)).thenReturn(Optional.of(turnoAbierto));

        MovimientoCajaRequest req = new MovimientoCajaRequest();
        req.setTurnoId(1);
        req.setTipo("TIPO_DESCONOCIDO");
        req.setMonto(new BigDecimal("50.00"));

        assertThatThrownBy(() -> cajaService.registrarMovimiento(req))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Tipo de movimiento no válido");
    }

    // ----------------------------------------------------------------
    // cerrarTurno
    // ----------------------------------------------------------------

    @Test
    @DisplayName("cerrarTurno → calcula diferencia correcta (sobrante)")
    void cerrarTurno_sobrante_calculaDiferenciaPositiva() {
        when(turnoRepo.findById(1)).thenReturn(Optional.of(turnoAbierto));
        when(turnoRepo.save(any())).thenReturn(turnoAbierto);

        CerrarTurnoRequest req = new CerrarTurnoRequest();
        req.setEfectivoReal(new BigDecimal("550.00")); // Se reportan 50 extras

        TurnoCaja cerrado = cajaService.cerrarTurno(1, req);

        assertThat(cerrado.getEstado()).isEqualTo("CERRADO");
        assertThat(cerrado.getDiferencia()).isEqualByComparingTo(new BigDecimal("50.00"));
    }

    @Test
    @DisplayName("cerrarTurno → calcula diferencia negativa (faltante)")
    void cerrarTurno_faltante_calculaDiferenciaнегativa() {
        when(turnoRepo.findById(1)).thenReturn(Optional.of(turnoAbierto));
        when(turnoRepo.save(any())).thenReturn(turnoAbierto);

        CerrarTurnoRequest req = new CerrarTurnoRequest();
        req.setEfectivoReal(new BigDecimal("480.00")); // Faltan 20

        cajaService.cerrarTurno(1, req);

        assertThat(turnoAbierto.getDiferencia()).isEqualByComparingTo(new BigDecimal("-20.00"));
    }

    @Test
    @DisplayName("cerrarTurno ya CERRADO → lanza IllegalStateException")
    void cerrarTurno_yaСerrado_lanzaExcepcion() {
        turnoAbierto.setEstado("CERRADO");
        when(turnoRepo.findById(1)).thenReturn(Optional.of(turnoAbierto));

        CerrarTurnoRequest req = new CerrarTurnoRequest();
        req.setEfectivoReal(new BigDecimal("500.00"));

        assertThatThrownBy(() -> cajaService.cerrarTurno(1, req))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("ya está CERRADO");
    }

    // ----------------------------------------------------------------
    // obtenerResumen
    // ----------------------------------------------------------------

    @Test
    @DisplayName("obtenerResumen → mapea correctamente el turno y sus movimientos")
    void obtenerResumen_turnoExistente_mapeaCorrectamente() {
        when(turnoRepo.findById(1)).thenReturn(Optional.of(turnoAbierto));
        when(movimientoRepo.findByTurnoIdOrderByFechaMovimientoAsc(1)).thenReturn(Collections.emptyList());

        ResumenTurnoResponse resumen = cajaService.obtenerResumen(1);

        assertThat(resumen).isNotNull();
        assertThat(resumen.getTurnoId()).isEqualTo(1);
        assertThat(resumen.getEstado()).isEqualTo("ABIERTO");
        assertThat(resumen.getMovimientos()).isEmpty();
    }
}
