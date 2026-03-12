package com.nexoohub.almacen.finanzas.controller;

import com.nexoohub.almacen.finanzas.dto.*;
import com.nexoohub.almacen.finanzas.service.CreditoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Controller REST para gestión de control de crédito de clientes.
 * 
 * <p><b>Endpoints principales:</b></p>
 * <ul>
 *   <li>POST /api/credito/limites - Crear límite de crédito</li>
 *   <li>GET /api/credito/limites/cliente/{clienteId} - Consultar límite</li>
 *   <li>PUT /api/credito/limites/cliente/{clienteId} - Actualizar límite</li>
 *   <li>GET /api/credito/validar - Validar crédito disponible</li>
 *   <li>POST /api/credito/abonos - Registrar pago</li>
 *   <li>GET /api/credito/historial/{clienteId} - Ver historial</li>
 * </ul>
 * 
 * @author NexooHub Development Team
 * @since 1.2.0
 */
@RestController
@RequestMapping("/api/credito")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Control de Crédito", description = "Gestión de límites de crédito y cuentas por cobrar de clientes")
public class CreditoController {

    private final CreditoService creditoService;

    // ==================== GESTIÓN DE LÍMITES ====================

    @PostMapping("/limites")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'SUPERVISOR')")
    @Operation(summary = "Crear límite de crédito", 
               description = "Configura el límite de crédito para un cliente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Límite creado exitosamente",
                     content = @Content(schema = @Schema(implementation = LimiteCreditoResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Cliente no existe o ya tiene límite configurado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<LimiteCreditoResponseDTO> crearLimiteCredito(
            @Valid @RequestBody LimiteCreditoRequestDTO request) {
        log.info("📥 POST /api/credito/limites - Cliente ID: {}", request.getClienteId());
        LimiteCreditoResponseDTO response = creditoService.crearLimiteCredito(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/limites/cliente/{clienteId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Actualizar límite de crédito",
               description = "Modifica el límite de crédito de un cliente existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Límite actualizado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Cliente no tiene límite configurado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<LimiteCreditoResponseDTO> actualizarLimiteCredito(
            @Parameter(description = "ID del cliente", required = true)
            @PathVariable Integer clienteId,
            @Valid @RequestBody LimiteCreditoRequestDTO request) {
        log.info("📝 PUT /api/credito/limites/cliente/{} - Nuevo límite: ${}",
                 clienteId, request.getLimiteAutorizado());
        LimiteCreditoResponseDTO response = creditoService.actualizarLimiteCredito(clienteId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/limites/cliente/{clienteId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'SUPERVISOR', 'AUDITOR')")
    @Operation(summary = "Consultar límite de crédito",
               description = "Obtiene el límite de crédito de un cliente con saldo y disponibilidad")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Límite encontrado",
                     content = @Content(schema = @Schema(implementation = LimiteCreditoResponseDTO.class))),
        @ApiResponse(responseCode = "404", description = "Cliente no tiene límite configurado")
    })
    public ResponseEntity<LimiteCreditoResponseDTO> obtenerLimitePorCliente(
            @Parameter(description = "ID del cliente", required = true)
            @PathVariable Integer clienteId) {
        log.info("🔍 GET /api/credito/limites/cliente/{}", clienteId);
        LimiteCreditoResponseDTO response = creditoService.obtenerLimitePorCliente(clienteId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/limites")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'SUPERVISOR', 'AUDITOR')")
    @Operation(summary = "Listar todos los límites de crédito",
               description = "Obtiene la lista completa de límites de crédito configurados")
    @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    public ResponseEntity<List<LimiteCreditoResponseDTO>> listarTodosLosLimites() {
        log.info("📋 GET /api/credito/limites - Listar todos");
        List<LimiteCreditoResponseDTO> response = creditoService.listarTodosLosLimites();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/limites/estado/{estado}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'SUPERVISOR', 'AUDITOR')")
    @Operation(summary = "Listar límites por estado",
               description = "Filtra límites de crédito por estado: ACTIVO, BLOQUEADO, SUSPENDIDO, INACTIVO")
    @ApiResponse(responseCode = "200", description = "Lista filtrada obtenida")
    public ResponseEntity<List<LimiteCreditoResponseDTO>> listarPorEstado(
            @Parameter(description = "Estado del crédito", required = true)
            @PathVariable String estado) {
        log.info("📋 GET /api/credito/limites/estado/{}", estado);
        List<LimiteCreditoResponseDTO> response = creditoService.listarPorEstado(estado);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/limites/activos")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'SUPERVISOR', 'AUDITOR')")
    @Operation(summary = "Listar créditos activos",
               description = "Obtiene todos los clientes con crédito activo")
    public ResponseEntity<List<LimiteCreditoResponseDTO>> listarActivos() {
        log.info("✅ GET /api/credito/limites/activos");
        List<LimiteCreditoResponseDTO> response = creditoService.listarPorEstado("ACTIVO");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/limites/bloqueados")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'AUDITOR')")
    @Operation(summary = "Listar créditos bloqueados",
               description = "Obtiene todos los clientes con crédito bloqueado")
    public ResponseEntity<List<LimiteCreditoResponseDTO>> listarBloqueados() {
        log.info("🚫 GET /api/credito/limites/bloqueados");
        List<LimiteCreditoResponseDTO> response = creditoService.listarPorEstado("BLOQUEADO");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/limites/riesgo")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'AUDITOR')")
    @Operation(summary = "Listar clientes en riesgo",
               description = "Obtiene clientes con utilización >= 80% de su límite de crédito")
    @ApiResponse(responseCode = "200", description = "Lista de clientes en riesgo")
    public ResponseEntity<List<LimiteCreditoResponseDTO>> listarProximosAExceder() {
        log.info("⚠️ GET /api/credito/limites/riesgo");
        List<LimiteCreditoResponseDTO> response = creditoService.listarProximosAExceder();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/limites/sobregiro")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'AUDITOR')")
    @Operation(summary = "Listar clientes en sobregiro",
               description = "Obtiene clientes que excedieron su límite de crédito")
    @ApiResponse(responseCode = "200", description = "Lista de clientes en sobregiro")
    public ResponseEntity<List<LimiteCreditoResponseDTO>> listarEnSobregiro() {
        log.info("🔴 GET /api/credito/limites/sobregiro");
        List<LimiteCreditoResponseDTO> response = creditoService.listarEnSobregiro();
        return ResponseEntity.ok(response);
    }

    // ==================== VALIDACIÓN ====================

    @GetMapping("/validar")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'SUPERVISOR', 'VENDEDOR', 'CAJERO')")
    @Operation(summary = "Validar crédito disponible",
               description = "Verifica si un cliente tiene crédito disponible para un monto específico. " +
                           "Usar ANTES de crear una venta a crédito.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Validación realizada",
                     content = @Content(schema = @Schema(implementation = ValidacionCreditoDTO.class)))
    })
    public ResponseEntity<ValidacionCreditoDTO> validarCreditoDisponible(
            @Parameter(description = "ID del cliente", required = true)
            @RequestParam Integer clienteId,
            @Parameter(description = "Monto a validar", required = true)
            @RequestParam BigDecimal monto) {
        log.info("🔍 GET /api/credito/validar?clienteId={}&monto={}", clienteId, monto);
        ValidacionCreditoDTO response = creditoService.validarCreditoDisponible(clienteId, monto);
        return ResponseEntity.ok(response);
    }

    // ==================== MOVIMIENTOS ====================

    @PostMapping("/abonos")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'CAJERO')")
    @Operation(summary = "Registrar abono/pago",
               description = "Registra un pago del cliente que reduce su saldo de crédito")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Abono registrado exitosamente",
                     content = @Content(schema = @Schema(implementation = HistorialCreditoResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Monto inválido o excede saldo"),
        @ApiResponse(responseCode = "404", description = "Cliente no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<HistorialCreditoResponseDTO> registrarAbono(
            @Valid @RequestBody AbonoRequestDTO request) {
        log.info("💰 POST /api/credito/abonos - Cliente ID: {}, Monto: ${}",
                 request.getClienteId(), request.getMonto());
        
        // TODO: Obtener usuario autenticado del SecurityContext
        String usuario = "SYSTEM";
        
        HistorialCreditoResponseDTO response = creditoService.registrarAbono(request, usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ==================== BLOQUEO/DESBLOQUEO ====================

    @PutMapping("/limites/cliente/{clienteId}/bloquear")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Bloquear crédito",
               description = "Bloquea el crédito de un cliente por morosidad u otro motivo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Crédito bloqueado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Cliente no tiene límite configurado")
    })
    public ResponseEntity<Map<String, String>> bloquearCredito(
            @Parameter(description = "ID del cliente", required = true)
            @PathVariable Integer clienteId,
            @Parameter(description = "Motivo del bloqueo", required = true)
            @RequestParam String motivo) {
        log.info("🚫 PUT /api/credito/limites/cliente/{}/bloquear - Motivo: {}", clienteId, motivo);
        creditoService.bloquearCredito(clienteId, motivo);
        return ResponseEntity.ok(Map.of(
            "mensaje", "Crédito bloqueado exitosamente",
            "clienteId", clienteId.toString(),
            "motivo", motivo
        ));
    }

    @PutMapping("/limites/cliente/{clienteId}/desbloquear")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Desbloquear crédito",
               description = "Reactiva el crédito de un cliente previamente bloqueado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Crédito desbloqueado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Cliente no tiene límite configurado")
    })
    public ResponseEntity<Map<String, String>> desbloquearCredito(
            @Parameter(description = "ID del cliente", required = true)
            @PathVariable Integer clienteId) {
        log.info("🔓 PUT /api/credito/limites/cliente/{}/desbloquear", clienteId);
        creditoService.desbloquearCredito(clienteId);
        return ResponseEntity.ok(Map.of(
            "mensaje", "Crédito desbloqueado exitosamente",
            "clienteId", clienteId.toString()
        ));
    }

    @PutMapping("/limites/cliente/{clienteId}/suspender")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Suspender crédito",
               description = "Suspende temporalmente el crédito de un cliente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Crédito suspendido exitosamente"),
        @ApiResponse(responseCode = "404", description = "Cliente no tiene límite configurado")
    })
    public ResponseEntity<Map<String, String>> suspenderCredito(
            @Parameter(description = "ID del cliente", required = true)
            @PathVariable Integer clienteId,
            @Parameter(description = "Motivo de la suspensión", required = true)
            @RequestParam String motivo) {
        log.info("⏸️ PUT /api/credito/limites/cliente/{}/suspender - Motivo: {}", clienteId, motivo);
        creditoService.suspenderCredito(clienteId, motivo);
        return ResponseEntity.ok(Map.of(
            "mensaje", "Crédito suspendido exitosamente",
            "clienteId", clienteId.toString(),
            "motivo", motivo
        ));
    }

    // ==================== HISTORIAL ====================

    @GetMapping("/historial/{clienteId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'SUPERVISOR', 'AUDITOR')")
    @Operation(summary = "Consultar historial de crédito",
               description = "Obtiene el historial completo de movimientos (cargos y abonos) de un cliente")
    @ApiResponse(responseCode = "200", description = "Historial obtenido exitosamente")
    public ResponseEntity<Page<HistorialCreditoResponseDTO>> obtenerHistorialCliente(
            @Parameter(description = "ID del cliente", required = true)
            @PathVariable Integer clienteId,
            @PageableDefault(size = 20, sort = "fechaMovimiento", direction = Sort.Direction.DESC)
            Pageable pageable) {
        log.info("📜 GET /api/credito/historial/{} - Página: {}", clienteId, pageable.getPageNumber());
        Page<HistorialCreditoResponseDTO> response = creditoService.obtenerHistorialCliente(clienteId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/historial/{clienteId}/cargos")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'SUPERVISOR', 'AUDITOR')")
    @Operation(summary = "Consultar cargos del cliente",
               description = "Obtiene solo los cargos (ventas a crédito) de un cliente")
    @ApiResponse(responseCode = "200", description = "Cargos obtenidos exitosamente")
    public ResponseEntity<Page<HistorialCreditoResponseDTO>> obtenerCargosCliente(
            @Parameter(description = "ID del cliente", required = true)
            @PathVariable Integer clienteId,
            @PageableDefault(size = 20, sort = "fechaMovimiento", direction = Sort.Direction.DESC)
            Pageable pageable) {
        log.info("💳 GET /api/credito/historial/{}/cargos", clienteId);
        Page<HistorialCreditoResponseDTO> response = creditoService.obtenerCargosCliente(clienteId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/historial/{clienteId}/abonos")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'SUPERVISOR', 'AUDITOR')")
    @Operation(summary = "Consultar abonos del cliente",
               description = "Obtiene solo los abonos (pagos) de un cliente")
    @ApiResponse(responseCode = "200", description = "Abonos obtenidos exitosamente")
    public ResponseEntity<Page<HistorialCreditoResponseDTO>> obtenerAbonosCliente(
            @Parameter(description = "ID del cliente", required = true)
            @PathVariable Integer clienteId,
            @PageableDefault(size = 20, sort = "fechaMovimiento", direction = Sort.Direction.DESC)
            Pageable pageable) {
        log.info("💰 GET /api/credito/historial/{}/abonos", clienteId);
        Page<HistorialCreditoResponseDTO> response = creditoService.obtenerAbonosCliente(clienteId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/historial/{clienteId}/rango")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'SUPERVISOR', 'AUDITOR')")
    @Operation(summary = "Consultar historial por rango de fechas",
               description = "Obtiene movimientos de un cliente en un período específico")
    @ApiResponse(responseCode = "200", description = "Historial filtrado obtenido")
    public ResponseEntity<List<HistorialCreditoResponseDTO>> obtenerHistorialPorFechas(
            @Parameter(description = "ID del cliente", required = true)
            @PathVariable Integer clienteId,
            @Parameter(description = "Fecha inicial (ISO 8601)", required = true)
            @RequestParam LocalDateTime fechaInicio,
            @Parameter(description = "Fecha final (ISO 8601)", required = true)
            @RequestParam LocalDateTime fechaFin) {
        log.info("📅 GET /api/credito/historial/{}/rango?fechaInicio={}&fechaFin={}",
                 clienteId, fechaInicio, fechaFin);
        List<HistorialCreditoResponseDTO> response = creditoService.obtenerHistorialPorFechas(
                clienteId, fechaInicio, fechaFin);
        return ResponseEntity.ok(response);
    }

    // ==================== MANEJO DE ERRORES ====================

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("❌ Error de validación: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
            "error", "BAD_REQUEST",
            "mensaje", ex.getMessage()
        ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception ex) {
        log.error("❌ Error interno: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
            "error", "INTERNAL_SERVER_ERROR",
            "mensaje", "Error interno del servidor: " + ex.getMessage()
        ));
    }
}
