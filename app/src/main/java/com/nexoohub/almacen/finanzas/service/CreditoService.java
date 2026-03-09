package com.nexoohub.almacen.finanzas.service;

import com.nexoohub.almacen.catalogo.entity.Cliente;
import com.nexoohub.almacen.catalogo.repository.ClienteRepository;
import com.nexoohub.almacen.finanzas.dto.*;
import com.nexoohub.almacen.finanzas.entity.HistorialCredito;
import com.nexoohub.almacen.finanzas.entity.LimiteCredito;
import com.nexoohub.almacen.finanzas.repository.HistorialCreditoRepository;
import com.nexoohub.almacen.finanzas.repository.LimiteCreditoRepository;
import com.nexoohub.almacen.ventas.entity.Venta;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para gestión de control de crédito de clientes.
 * 
 * <p>Funcionalidades principales:</p>
 * <ul>
 *   <li>Configurar y modificar límites de crédito</li>
 *   <li>Validar disponibilidad de crédito antes de ventas</li>
 *   <li>Registrar cargos automáticos por ventas a crédito</li>
 *   <li>Procesar abonos/pagos</li>
 *   <li>Bloquear/desbloquear crédito automáticamente</li>
 *   <li>Generar historial completo de movimientos</li>
 * </ul>
 * 
 * @author NexooHub Development Team
 * @since 1.2.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CreditoService {

    private final LimiteCreditoRepository limiteCreditoRepository;
    private final HistorialCreditoRepository historialCreditoRepository;
    private final ClienteRepository clienteRepository;

    // ==================== GESTIÓN DE LÍMITES ====================

    /**
     * Crea un nuevo límite de crédito para un cliente.
     * 
     * @param request Datos del límite a crear
     * @return Límite de crédito creado
     * @throws IllegalArgumentException Si el cliente no existe o ya tiene límite configurado
     */
    public LimiteCreditoResponseDTO crearLimiteCredito(LimiteCreditoRequestDTO request) {
        log.info("🆕 Creando límite de crédito para cliente ID: {}", request.getClienteId());

        // Validar que el cliente existe
        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado: " + request.getClienteId()));

        // Validar que no existe límite previo
        limiteCreditoRepository.findByClienteId(request.getClienteId())
                .ifPresent(lc -> {
                    throw new IllegalArgumentException("El cliente ya tiene un límite de crédito configurado");
                });

        // Crear entidad
        LimiteCredito limiteCredito = new LimiteCredito();
        limiteCredito.setCliente(cliente);
        limiteCredito.setLimiteAutorizado(request.getLimiteAutorizado());
        limiteCredito.setSaldoUtilizado(BigDecimal.ZERO);
        limiteCredito.setEstado("ACTIVO");
        limiteCredito.setPlazoPagoDias(request.getPlazoPagoDias());
        limiteCredito.setMaxFacturasVencidas(request.getMaxFacturasVencidas());
        limiteCredito.setPermiteSobregiro(request.getPermiteSobregiro());
        limiteCredito.setMontoSobregiro(request.getMontoSobregiro());
        limiteCredito.setObservaciones(request.getObservaciones());
        limiteCredito.setFechaRevision(java.time.LocalDate.now());

        LimiteCredito guardado = limiteCreditoRepository.save(limiteCredito);
        log.info("✅ Límite de crédito creado exitosamente ID: {} para cliente: {}", 
                 guardado.getId(), cliente.getNombre());

        return mapToResponseDTO(guardado);
    }

    /**
     * Actualiza el límite de crédito de un cliente.
     * 
     * @param clienteId ID del cliente
     * @param request Nuevos datos del límite
     * @return Límite actualizado
     * @throws IllegalArgumentException Si no existe límite para el cliente
     */
    public LimiteCreditoResponseDTO actualizarLimiteCredito(Integer clienteId, LimiteCreditoRequestDTO request) {
        log.info("📝 Actualizando límite de crédito para cliente ID: {}", clienteId);

        LimiteCredito limiteCredito = limiteCreditoRepository.findByClienteId(clienteId)
                .orElseThrow(() -> new IllegalArgumentException("No existe límite de crédito para este cliente"));

        limiteCredito.setLimiteAutorizado(request.getLimiteAutorizado());
        limiteCredito.setPlazoPagoDias(request.getPlazoPagoDias());
        limiteCredito.setMaxFacturasVencidas(request.getMaxFacturasVencidas());
        limiteCredito.setPermiteSobregiro(request.getPermiteSobregiro());
        limiteCredito.setMontoSobregiro(request.getMontoSobregiro());
        limiteCredito.setObservaciones(request.getObservaciones());
        limiteCredito.setFechaRevision(java.time.LocalDate.now());

        LimiteCredito actualizado = limiteCreditoRepository.save(limiteCredito);
        log.info("✅ Límite de crédito actualizado: {} → monto: ${}",
                 actualizado.getId(), actualizado.getLimiteAutorizado());

        return mapToResponseDTO(actualizado);
    }

    /**
     * Obtiene el límite de crédito de un cliente.
     * 
     * @param clienteId ID del cliente
     * @return Límite de crédito
     * @throws IllegalArgumentException Si no existe límite para el cliente
     */
    @Transactional(readOnly = true)
    public LimiteCreditoResponseDTO obtenerLimitePorCliente(Integer clienteId) {
        LimiteCredito limiteCredito = limiteCreditoRepository.findByClienteId(clienteId)
                .orElseThrow(() -> new IllegalArgumentException("No existe límite de crédito para este cliente"));
        return mapToResponseDTO(limiteCredito);
    }

    /**
     * Lista todos los límites de crédito.
     * 
     * @return Lista de límites de crédito
     */
    @Transactional(readOnly = true)
    public List<LimiteCreditoResponseDTO> listarTodosLosLimites() {
        return limiteCreditoRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Lista clientes por estado de crédito.
     * 
     * @param estado Estado del crédito (ACTIVO, BLOQUEADO, SUSPENDIDO, INACTIVO)
     * @return Lista de límites con ese estado
     */
    @Transactional(readOnly = true)
    public List<LimiteCreditoResponseDTO> listarPorEstado(String estado) {
        return limiteCreditoRepository.findByEstado(estado).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Lista clientes con crédito próximo a excederse (>= 80%).
     * 
     * @return Lista de clientes en riesgo
     */
    @Transactional(readOnly = true)
    public List<LimiteCreditoResponseDTO> listarProximosAExceder() {
        return limiteCreditoRepository.findProximosAExceder().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Lista clientes en sobregiro (excedieron su límite).
     * 
     * @return Lista de clientes en sobregiro
     */
    @Transactional(readOnly = true)
    public List<LimiteCreditoResponseDTO> listarEnSobregiro() {
        return limiteCreditoRepository.findEnSobregiro().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    // ==================== VALIDACIÓN DE CRÉDITO ====================

    /**
     * Valida si un cliente tiene crédito disponible para un monto específico.
     * 
     * <p>Esta validación se debe ejecutar ANTES de crear una venta a crédito.</p>
     * 
     * @param clienteId ID del cliente
     * @param monto Monto a validar
     * @return Resultado de la validación con detalles
     */
    @Transactional(readOnly = true)
    public ValidacionCreditoDTO validarCreditoDisponible(Integer clienteId, BigDecimal monto) {
        log.info("🔍 Validando crédito para cliente ID: {} por monto: ${}", clienteId, monto);

        // Buscar límite de crédito
        LimiteCredito limiteCredito = limiteCreditoRepository.findByClienteId(clienteId)
                .orElse(null);

        // Si no tiene límite configurado
        if (limiteCredito == null) {
            log.warn("⚠️ Cliente {} no tiene límite de crédito configurado", clienteId);
            return ValidacionCreditoDTO.builder()
                    .creditoDisponible(false)
                    .montoDisponible(BigDecimal.ZERO)
                    .limiteAutorizado(BigDecimal.ZERO)
                    .saldoUtilizado(BigDecimal.ZERO)
                    .montoSolicitado(monto)
                    .estado("SIN_CONFIGURAR")
                    .mensaje("El cliente no tiene límite de crédito configurado")
                    .codigo("SIN_CREDITO")
                    .build();
        }

        // Si el crédito está bloqueado o inactivo
        if (!"ACTIVO".equals(limiteCredito.getEstado())) {
            log.warn("⚠️ Crédito del cliente {} está en estado: {}", clienteId, limiteCredito.getEstado());
            return ValidacionCreditoDTO.builder()
                    .creditoDisponible(false)
                    .montoDisponible(BigDecimal.ZERO)
                    .limiteAutorizado(limiteCredito.getLimiteAutorizado())
                    .saldoUtilizado(limiteCredito.getSaldoUtilizado())
                    .montoSolicitado(monto)
                    .estado(limiteCredito.getEstado())
                    .mensaje("Crédito " + limiteCredito.getEstado() + ": " + limiteCredito.getObservaciones())
                    .codigo("BLOQUEADO")
                    .build();
        }

        // Validar si tiene crédito suficiente
        boolean tieneCredito = limiteCredito.tieneCreditoDisponible(monto);
        BigDecimal disponible = limiteCredito.getCreditoDisponible();

        if (tieneCredito) {
            log.info("✅ Cliente {} tiene crédito disponible: ${}", clienteId, disponible);
            return ValidacionCreditoDTO.builder()
                    .creditoDisponible(true)
                    .montoDisponible(disponible)
                    .limiteAutorizado(limiteCredito.getLimiteAutorizado())
                    .saldoUtilizado(limiteCredito.getSaldoUtilizado())
                    .montoSolicitado(monto)
                    .estado(limiteCredito.getEstado())
                    .mensaje("Crédito disponible suficiente")
                    .codigo("OK")
                    .build();
        } else {
            log.warn("❌ Cliente {} NO tiene crédito suficiente. Disponible: ${}, Solicitado: ${}",
                    clienteId, disponible, monto);
            return ValidacionCreditoDTO.builder()
                    .creditoDisponible(false)
                    .montoDisponible(disponible)
                    .limiteAutorizado(limiteCredito.getLimiteAutorizado())
                    .saldoUtilizado(limiteCredito.getSaldoUtilizado())
                    .montoSolicitado(monto)
                    .estado(limiteCredito.getEstado())
                    .mensaje("Crédito insuficiente. Disponible: $" + disponible + ", Solicitado: $" + monto)
                    .codigo("LIMITE_EXCEDIDO")
                    .build();
        }
    }

    // ==================== REGISTRO DE MOVIMIENTOS ====================

    /**
     * Registra un cargo por venta a crédito.
     * 
     * <p>Este método se llama automáticamente cuando se crea una venta con metodoPago = "CREDITO".</p>
     * 
     * @param clienteId ID del cliente
     * @param venta Venta asociada
     * @param monto Monto del cargo
     * @param usuario Usuario que registra
     * @return Historial de crédito creado
     * @throws IllegalArgumentException Si excede el límite
     */
    public HistorialCreditoResponseDTO registrarCargo(Integer clienteId, Venta venta, BigDecimal monto, String usuario) {
        log.info("💳 Registrando cargo de ${} para cliente ID: {}", monto, clienteId);

        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));

        LimiteCredito limiteCredito = limiteCreditoRepository.findByClienteId(clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no tiene límite de crédito configurado"));

        // Validar que no exceda el límite
        if (!limiteCredito.tieneCreditoDisponible(monto)) {
            throw new IllegalArgumentException("El cargo excede el límite de crédito disponible");
        }

        // Actualizar saldo utilizado
        BigDecimal nuevoSaldo = limiteCredito.getSaldoUtilizado().add(monto);
        limiteCredito.setSaldoUtilizado(nuevoSaldo);
        limiteCreditoRepository.save(limiteCredito);

        // Crear registro en historial
        HistorialCredito historial = HistorialCredito.crearCargo(cliente, venta, monto, nuevoSaldo, usuario);
        HistorialCredito guardado = historialCreditoRepository.save(historial);

        log.info("✅ Cargo registrado ID: {} → Nuevo saldo cliente: ${}", guardado.getId(), nuevoSaldo);

        return mapToHistorialDTO(guardado);
    }

    /**
     * Registra un abono/pago de cliente.
     * 
     * @param request Datos del abono
     * @param usuario Usuario que registra
     * @return Historial de crédito creado
     * @throws IllegalArgumentException Si el cliente no existe o el monto excede el saldo
     */
    public HistorialCreditoResponseDTO registrarAbono(AbonoRequestDTO request, String usuario) {
        log.info("💰 Registrando abono de ${} para cliente ID: {}", request.getMonto(), request.getClienteId());

        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));

        LimiteCredito limiteCredito = limiteCreditoRepository.findByClienteId(request.getClienteId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente no tiene límite de crédito configurado"));

        // Validar que el monto no sea mayor al saldo utilizado
        if (request.getMonto().compareTo(limiteCredito.getSaldoUtilizado()) > 0) {
            throw new IllegalArgumentException("El abono no puede ser mayor al saldo utilizado");
        }

        // Actualizar saldo utilizado
        BigDecimal nuevoSaldo = limiteCredito.getSaldoUtilizado().subtract(request.getMonto());
        limiteCredito.setSaldoUtilizado(nuevoSaldo);

        // Si el cliente estaba bloqueado y ahora tiene saldo bajo el límite, reactivar
        if ("BLOQUEADO".equals(limiteCredito.getEstado()) && 
            nuevoSaldo.compareTo(limiteCredito.getLimiteAutorizado()) <= 0) {
            limiteCredito.activar();
            log.info("🔓 Cliente {} reactivado automáticamente tras pago", cliente.getNombre());
        }

        limiteCreditoRepository.save(limiteCredito);

        // Crear registro en historial
        HistorialCredito historial = HistorialCredito.crearAbono(
                cliente,
                request.getMonto(),
                nuevoSaldo,
                request.getMetodoPago(),
                request.getFolioComprobante(),
                request.getConcepto(),
                usuario
        );
        HistorialCredito guardado = historialCreditoRepository.save(historial);

        log.info("✅ Abono registrado ID: {} → Nuevo saldo cliente: ${}", guardado.getId(), nuevoSaldo);

        return mapToHistorialDTO(guardado);
    }

    // ==================== BLOQUEO Y DESBLOQUEO ====================

    /**
     * Bloquea el crédito de un cliente.
     * 
     * @param clienteId ID del cliente
     * @param motivo Razón del bloqueo
     */
    public void bloquearCredito(Integer clienteId, String motivo) {
        log.warn("🚫 Bloqueando crédito de cliente ID: {} - Motivo: {}", clienteId, motivo);

        LimiteCredito limiteCredito = limiteCreditoRepository.findByClienteId(clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no tiene límite de crédito configurado"));

        limiteCredito.bloquear(motivo);
        limiteCreditoRepository.save(limiteCredito);

        log.info("✅ Crédito bloqueado para cliente ID: {}", clienteId);
    }

    /**
     * Desbloquea/reactiva el crédito de un cliente.
     * 
     * @param clienteId ID del cliente
     */
    public void desbloquearCredito(Integer clienteId) {
        log.info("🔓 Desbloqueando crédito de cliente ID: {}", clienteId);

        LimiteCredito limiteCredito = limiteCreditoRepository.findByClienteId(clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no tiene límite de crédito configurado"));

        limiteCredito.activar();
        limiteCreditoRepository.save(limiteCredito);

        log.info("✅ Crédito activado para cliente ID: {}", clienteId);
    }

    /**
     * Suspende temporalmente el crédito de un cliente.
     * 
     * @param clienteId ID del cliente
     * @param motivo Razón de la suspensión
     */
    public void suspenderCredito(Integer clienteId, String motivo) {
        log.warn("⏸️ Suspendiendo crédito de cliente ID: {} - Motivo: {}", clienteId, motivo);

        LimiteCredito limiteCredito = limiteCreditoRepository.findByClienteId(clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no tiene límite de crédito configurado"));

        limiteCredito.suspender(motivo);
        limiteCreditoRepository.save(limiteCredito);

        log.info("✅ Crédito suspendido para cliente ID: {}", clienteId);
    }

    // ==================== HISTORIAL ====================

    /**
     * Lista el historial completo de un cliente.
     * 
     * @param clienteId ID del cliente
     * @param pageable Paginación
     * @return Página de movimientos
     */
    @Transactional(readOnly = true)
    public Page<HistorialCreditoResponseDTO> obtenerHistorialCliente(Integer clienteId, Pageable pageable) {
        return historialCreditoRepository.findByClienteId(clienteId, pageable)
                .map(this::mapToHistorialDTO);
    }

    /**
     * Lista solo cargos de un cliente.
     * 
     * @param clienteId ID del cliente
     * @param pageable Paginación
     * @return Página de cargos
     */
    @Transactional(readOnly = true)
    public Page<HistorialCreditoResponseDTO> obtenerCargosCliente(Integer clienteId, Pageable pageable) {
        return historialCreditoRepository.findCargosByClienteId(clienteId, pageable)
                .map(this::mapToHistorialDTO);
    }

    /**
     * Lista solo abonos de un cliente.
     * 
     * @param clienteId ID del cliente
     * @param pageable Paginación
     * @return Página de abonos
     */
    @Transactional(readOnly = true)
    public Page<HistorialCreditoResponseDTO> obtenerAbonosCliente(Integer clienteId, Pageable pageable) {
        return historialCreditoRepository.findAbonosByClienteId(clienteId, pageable)
                .map(this::mapToHistorialDTO);
    }

    /**
     * Obtiene el historial en un rango de fechas.
     * 
     * @param clienteId ID del cliente
     * @param fechaInicio Fecha inicial
     * @param fechaFin Fecha final
     * @return Lista de movimientos
     */
    @Transactional(readOnly = true)
    public List<HistorialCreditoResponseDTO> obtenerHistorialPorFechas(
            Integer clienteId,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin) {
        return historialCreditoRepository.findByClienteIdAndFechasBetween(clienteId, fechaInicio, fechaFin)
                .stream()
                .map(this::mapToHistorialDTO)
                .collect(Collectors.toList());
    }

    // ==================== MAPPERS ====================

    private LimiteCreditoResponseDTO mapToResponseDTO(LimiteCredito entity) {
        return LimiteCreditoResponseDTO.builder()
                .id(entity.getId())
                .cliente(LimiteCreditoResponseDTO.ClienteBasicoDTO.builder()
                        .id(entity.getCliente().getId())
                        .nombre(entity.getCliente().getNombre())
                        .rfc(entity.getCliente().getRfc())
                        .telefono(entity.getCliente().getTelefono())
                        .email(entity.getCliente().getEmail())
                        .build())
                .limiteAutorizado(entity.getLimiteAutorizado())
                .saldoUtilizado(entity.getSaldoUtilizado())
                .creditoDisponible(entity.getCreditoDisponible())
                .porcentajeUtilizacion(entity.getPorcentajeUtilizacion())
                .estado(entity.getEstado())
                .plazoPagoDias(entity.getPlazoPagoDias())
                .maxFacturasVencidas(entity.getMaxFacturasVencidas())
                .permiteSobregiro(entity.getPermiteSobregiro())
                .montoSobregiro(entity.getMontoSobregiro())
                .fechaRevision(entity.getFechaRevision())
                .observaciones(entity.getObservaciones())
                .build();
    }

    private HistorialCreditoResponseDTO mapToHistorialDTO(HistorialCredito entity) {
        return HistorialCreditoResponseDTO.builder()
                .id(entity.getId())
                .clienteId(entity.getCliente().getId())
                .clienteNombre(entity.getCliente().getNombre())
                .ventaId(entity.getVenta() != null ? entity.getVenta().getId() : null)
                .tipoMovimiento(entity.getTipoMovimiento())
                .monto(entity.getMonto())
                .saldoResultante(entity.getSaldoResultante())
                .metodoPago(entity.getMetodoPago())
                .folioComprobante(entity.getFolioComprobante())
                .concepto(entity.getConcepto())
                .observaciones(entity.getObservaciones())
                .fechaMovimiento(entity.getFechaMovimiento())
                .usuarioRegistro(entity.getUsuarioRegistro())
                .build();
    }
}
