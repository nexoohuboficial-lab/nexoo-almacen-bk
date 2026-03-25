package com.nexoohub.almacen.caja.controller;

import com.nexoohub.almacen.caja.dto.*;
import com.nexoohub.almacen.caja.entity.MovimientoCaja;
import com.nexoohub.almacen.caja.entity.TurnoCaja;
import com.nexoohub.almacen.caja.service.CajaService;
import com.nexoohub.almacen.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controller REST para el módulo de Caja y Arqueos (POS-01).
 *
 * <p>Base URL: /api/v1/cajas</p>
 *
 * @author NexooHub Development Team
 * @since 1.0 — POS-01
 */
@RestController
@RequestMapping("/api/v1/cajas")
@CrossOrigin(origins = "*")
@Tag(name = "Cajas y Arqueos", description = "Gestión de turnos de caja, movimientos y arqueo Z (POS-01)")
public class CajaController {

    private static final Logger log = LoggerFactory.getLogger(CajaController.class);

    private final CajaService cajaService;

    public CajaController(CajaService cajaService) {
        this.cajaService = cajaService;
    }

    /**
     * Abre un turno de caja con fondo inicial.
     * Valida que el empleado no tenga ya un turno abierto en la misma sucursal.
     *
     * @param request datos de apertura (sucursalId, empleadoId, fondoInicial)
     * @return turno recién creado con estado ABIERTO y HTTP 201
     */
    @PostMapping("/abrir")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'CAJERO')")
    @Operation(summary = "Abrir turno de caja", description = "Registra fondo inicial y crea turno ABIERTO")
    public ResponseEntity<ApiResponse<TurnoCaja>> abrirTurno(@Valid @RequestBody AbrirTurnoRequest request) {
        log.info("Solicitud de apertura de turno: empleado={}, sucursal={}", request.getEmpleadoId(), request.getSucursalId());
        TurnoCaja turno = cajaService.abrirTurno(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new ApiResponse<>("Turno de caja abierto correctamente", turno));
    }

    /**
     * Registra un movimiento de efectivo en un turno activo.
     * Tipos válidos: RETIRO, INGRESO_EXTRA, VENTA_EFECTIVO, VENTA_TARJETA, VENTA_CREDITO
     *
     * @param request datos del movimiento (turnoId, tipo, monto)
     * @return movimiento registrado con HTTP 201
     */
    @PostMapping("/movimientos")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'CAJERO', 'VENDEDOR')")
    @Operation(summary = "Registrar movimiento de caja", description = "Registra retiro, ingreso o venta en un turno abierto")
    public ResponseEntity<ApiResponse<MovimientoCaja>> registrarMovimiento(
            @Valid @RequestBody MovimientoCajaRequest request) {
        log.info("Registrando movimiento tipo={} en turno={}", request.getTipo(), request.getTurnoId());
        MovimientoCaja movimiento = cajaService.registrarMovimiento(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new ApiResponse<>("Movimiento registrado correctamente", movimiento));
    }

    /**
     * Cierra el turno realizando el arqueo Z.
     * El empleado reporta el efectivo físico contado y el sistema calcula la diferencia.
     *
     * @param id      ID del turno a cerrar
     * @param request efectivo real contado por el empleado
     * @return turno cerrado con diferencia calculada y HTTP 200
     */
    @PostMapping("/{id}/cerrar")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'CAJERO')")
    @Operation(summary = "Cerrar turno y realizar arqueo Z", description = "Compara efectivo físico vs esperado y cierra el turno")
    public ResponseEntity<ApiResponse<TurnoCaja>> cerrarTurno(
            @PathVariable Integer id,
            @Valid @RequestBody CerrarTurnoRequest request) {
        log.info("Solicitud de cierre de turno ID={}", id);
        TurnoCaja turno = cajaService.cerrarTurno(id, request);
        return ResponseEntity.ok(new ApiResponse<>("Turno cerrado. Diferencia de arqueo: " + turno.getDiferencia(), turno));
    }

    /**
     * Devuelve el resumen completo de un turno: totales, arqueo y lista de movimientos.
     *
     * @param id ID del turno a consultar
     * @return resumen del turno con HTTP 200
     */
    @GetMapping("/{id}/resumen")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'CAJERO')")
    @Operation(summary = "Resumen del turno de caja", description = "Totales de ventas, retiros, efectivo esperado y lista de movimientos")
    public ResponseEntity<ApiResponse<ResumenTurnoResponse>> obtenerResumen(@PathVariable Integer id) {
        log.info("Consultando resumen del turno ID={}", id);
        ResumenTurnoResponse resumen = cajaService.obtenerResumen(id);
        return ResponseEntity.ok(new ApiResponse<>("Resumen del turno obtenido", resumen));
    }
}
