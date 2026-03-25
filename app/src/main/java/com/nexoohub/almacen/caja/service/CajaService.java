package com.nexoohub.almacen.caja.service;

import com.nexoohub.almacen.caja.dto.*;
import com.nexoohub.almacen.caja.entity.MovimientoCaja;
import com.nexoohub.almacen.caja.entity.TurnoCaja;
import com.nexoohub.almacen.caja.repository.MovimientoCajaRepository;
import com.nexoohub.almacen.caja.repository.TurnoCajaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de negocio para el módulo de Caja y Arqueos (POS-01).
 *
 * <p>Reglas de negocio:</p>
 * <ul>
 *   <li>Un empleado solo puede tener UN turno ABIERTO a la vez por sucursal.</li>
 *   <li>Los movimientos solo se pueden registrar en turnos ABIERTOS.</li>
 *   <li>El cierre calcula: efectivoEsperado = fondo + ventasEfectivo + ingresosExtra - retiros</li>
 *   <li>La diferencia puede ser positiva (sobrante) o negativa (faltante).</li>
 * </ul>
 *
 * @author NexooHub Development Team
 * @since 1.0 — POS-01
 */
@Service
public class CajaService {

    private static final Logger log = LoggerFactory.getLogger(CajaService.class);

    private final TurnoCajaRepository turnoRepo;
    private final MovimientoCajaRepository movimientoRepo;

    public CajaService(TurnoCajaRepository turnoRepo, MovimientoCajaRepository movimientoRepo) {
        this.turnoRepo      = turnoRepo;
        this.movimientoRepo = movimientoRepo;
    }

    // ----------------------------------------------------------------
    // POST /api/v1/cajas/abrir
    // ----------------------------------------------------------------

    /**
     * Abre un nuevo turno de caja para el empleado en la sucursal indicada.
     * Regla: solo puede haber un turno ABIERTO por empleado + sucursal.
     */
    @Transactional
    public TurnoCaja abrirTurno(AbrirTurnoRequest request) {
        log.info("Abriendo turno de caja: empleado={}, sucursal={}", request.getEmpleadoId(), request.getSucursalId());

        // Regla de negocio: verificar que no haya turno ya abierto
        turnoRepo.findByEmpleadoIdAndSucursalIdAndEstado(
                request.getEmpleadoId(), request.getSucursalId(), "ABIERTO")
            .ifPresent(t -> {
                throw new IllegalStateException(
                    "Ya existe un turno ABIERTO (ID: " + t.getId() + ") para este empleado en esta sucursal.");
            });

        TurnoCaja turno = new TurnoCaja();
        turno.setSucursalId(request.getSucursalId());
        turno.setEmpleadoId(request.getEmpleadoId());
        turno.setFondoInicial(request.getFondoInicial());
        turno.setEfectivoEsperado(request.getFondoInicial()); // Al abrir, el esperado = fondo inicial
        turno.setObservaciones(request.getObservaciones());
        turno.setEstado("ABIERTO");

        TurnoCaja guardado = turnoRepo.save(turno);
        log.info("Turno de caja abierto con ID: {}", guardado.getId());
        return guardado;
    }

    // ----------------------------------------------------------------
    // POST /api/v1/cajas/movimientos
    // ----------------------------------------------------------------

    /**
     * Registra un movimiento (retiro, ingreso extra, venta) en un turno abierto.
     * Actualiza los totales acumulados del turno.
     */
    @Transactional
    public MovimientoCaja registrarMovimiento(MovimientoCajaRequest request) {
        log.info("Registrando movimiento tipo={} en turno={}", request.getTipo(), request.getTurnoId());

        TurnoCaja turno = turnoRepo.findById(request.getTurnoId())
            .orElseThrow(() -> new IllegalArgumentException("Turno no encontrado: " + request.getTurnoId()));

        if (!"ABIERTO".equals(turno.getEstado())) {
            throw new IllegalStateException("No se puede registrar movimientos en un turno CERRADO.");
        }

        // Crear el movimiento
        MovimientoCaja mov = new MovimientoCaja();
        mov.setTurno(turno);
        mov.setTipo(request.getTipo());
        mov.setMonto(request.getMonto());
        mov.setConcepto(request.getConcepto());
        mov.setReferencia(request.getReferencia());

        // Actualizar los totales acumulados del turno
        switch (request.getTipo()) {
            case "VENTA_EFECTIVO"  -> turno.setTotalVentasEfectivo(turno.getTotalVentasEfectivo().add(request.getMonto()));
            case "VENTA_TARJETA"   -> turno.setTotalVentasTarjeta(turno.getTotalVentasTarjeta().add(request.getMonto()));
            case "VENTA_CREDITO"   -> turno.setTotalVentasCredito(turno.getTotalVentasCredito().add(request.getMonto()));
            case "RETIRO"          -> turno.setTotalRetiros(turno.getTotalRetiros().add(request.getMonto()));
            case "INGRESO_EXTRA"   -> turno.setTotalIngresosExtra(turno.getTotalIngresosExtra().add(request.getMonto()));
            default -> throw new IllegalArgumentException("Tipo de movimiento no válido: " + request.getTipo());
        }

        // Recalcular efectivo esperado
        BigDecimal efectivoEsperado = turno.getFondoInicial()
            .add(turno.getTotalVentasEfectivo())
            .add(turno.getTotalIngresosExtra())
            .subtract(turno.getTotalRetiros());
        turno.setEfectivoEsperado(efectivoEsperado);

        turnoRepo.save(turno);
        MovimientoCaja guardado = movimientoRepo.save(mov);
        log.info("Movimiento ID={} registrado en turno={}", guardado.getId(), turno.getId());
        return guardado;
    }

    // ----------------------------------------------------------------
    // POST /api/v1/cajas/{id}/cerrar
    // ----------------------------------------------------------------

    /**
     * Cierra el turno realizando el arqueo Z.
     * Calcula la diferencia entre el efectivo esperado y el contado por el empleado.
     */
    @Transactional
    public TurnoCaja cerrarTurno(Integer turnoId, CerrarTurnoRequest request) {
        log.info("Cerrando turno ID={}", turnoId);

        TurnoCaja turno = turnoRepo.findById(turnoId)
            .orElseThrow(() -> new IllegalArgumentException("Turno no encontrado: " + turnoId));

        if (!"ABIERTO".equals(turno.getEstado())) {
            throw new IllegalStateException("El turno ya está CERRADO.");
        }

        // Arqueo Z: comparar efectivo físico vs esperado
        BigDecimal diferencia = request.getEfectivoReal().subtract(turno.getEfectivoEsperado());

        turno.setEfectivoReal(request.getEfectivoReal());
        turno.setDiferencia(diferencia);
        turno.setEstado("CERRADO");
        turno.setFechaCierre(LocalDateTime.now());

        if (request.getObservaciones() != null) {
            turno.setObservaciones(request.getObservaciones());
        }

        TurnoCaja cerrado = turnoRepo.save(turno);
        log.info("Turno ID={} cerrado. Esperado={}, Real={}, Diferencia={}",
            turnoId, turno.getEfectivoEsperado(), request.getEfectivoReal(), diferencia);
        return cerrado;
    }

    // ----------------------------------------------------------------
    // GET /api/v1/cajas/{id}/resumen
    // ----------------------------------------------------------------

    /**
     * Genera el resumen completo del turno con todos sus movimientos y el resultado del arqueo.
     */
    @Transactional(readOnly = true)
    public ResumenTurnoResponse obtenerResumen(Integer turnoId) {
        log.info("Consultando resumen del turno ID={}", turnoId);

        TurnoCaja turno = turnoRepo.findById(turnoId)
            .orElseThrow(() -> new IllegalArgumentException("Turno no encontrado: " + turnoId));

        List<MovimientoCaja> movimientos = movimientoRepo.findByTurnoIdOrderByFechaMovimientoAsc(turnoId);

        ResumenTurnoResponse res = new ResumenTurnoResponse();
        res.setTurnoId(turno.getId());
        res.setSucursalId(turno.getSucursalId());
        res.setEmpleadoId(turno.getEmpleadoId());
        res.setEstado(turno.getEstado());
        res.setFechaApertura(turno.getFechaApertura());
        res.setFechaCierre(turno.getFechaCierre());
        res.setFondoInicial(turno.getFondoInicial());
        res.setTotalVentasEfectivo(turno.getTotalVentasEfectivo());
        res.setTotalVentasTarjeta(turno.getTotalVentasTarjeta());
        res.setTotalVentasCredito(turno.getTotalVentasCredito());
        res.setTotalRetiros(turno.getTotalRetiros());
        res.setTotalIngresosExtra(turno.getTotalIngresosExtra());
        res.setEfectivoEsperado(turno.getEfectivoEsperado());
        res.setEfectivoReal(turno.getEfectivoReal());
        res.setDiferencia(turno.getDiferencia());
        res.setObservaciones(turno.getObservaciones());

        // Estado del arqueo
        if (turno.getDiferencia() != null) {
            int comp = turno.getDiferencia().compareTo(BigDecimal.ZERO);
            res.setEstadoArqueo(comp > 0 ? "SOBRANTE" : comp < 0 ? "FALTANTE" : "OK");
        }

        // Mapear movimientos al DTO interno
        List<ResumenTurnoResponse.MovimientoResumen> movDtos = movimientos.stream().map(m -> {
            ResumenTurnoResponse.MovimientoResumen dto = new ResumenTurnoResponse.MovimientoResumen();
            dto.setId(m.getId());
            dto.setTipo(m.getTipo());
            dto.setMonto(m.getMonto());
            dto.setConcepto(m.getConcepto());
            dto.setReferencia(m.getReferencia());
            dto.setFechaMovimiento(m.getFechaMovimiento());
            return dto;
        }).collect(Collectors.toList());

        res.setMovimientos(movDtos);
        return res;
    }
}
