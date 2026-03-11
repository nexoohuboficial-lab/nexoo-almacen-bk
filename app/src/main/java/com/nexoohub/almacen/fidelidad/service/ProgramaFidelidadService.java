package com.nexoohub.almacen.fidelidad.service;

import com.nexoohub.almacen.catalogo.entity.Cliente;
import com.nexoohub.almacen.catalogo.repository.ClienteRepository;
import com.nexoohub.almacen.common.exception.BusinessException;
import com.nexoohub.almacen.common.exception.ResourceNotFoundException;
import com.nexoohub.almacen.fidelidad.dto.*;
import com.nexoohub.almacen.fidelidad.entity.MovimientoPunto;
import com.nexoohub.almacen.fidelidad.entity.ProgramaFidelidad;
import com.nexoohub.almacen.fidelidad.repository.MovimientoPuntoRepository;
import com.nexoohub.almacen.fidelidad.repository.ProgramaFidelidadRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para gestión del programa de fidelidad de clientes.
 * 
 * <p><b>Reglas de negocio:</b></p>
 * <ul>
 *   <li>1 punto por cada $10 MXN de compra</li>
 *   <li>100 puntos = $10 MXN de descuento (ratio 10:1)</li>
 *   <li>Los puntos se registran automáticamente en cada venta a crédito o contado</li>
 *   <li>Los puntos no caducan</li>
 *   <li>Solo clientes activos pueden acumular puntos</li>
 * </ul>
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
@Service
@Transactional
public class ProgramaFidelidadService {

    private static final Logger log = LoggerFactory.getLogger(ProgramaFidelidadService.class);

    // Constantes de negocio
    private static final BigDecimal PESOS_POR_PUNTO = new BigDecimal("10.00"); // $10 MXN = 1 punto
    private static final int PUNTOS_MINIMOS_CANJE = 100; // Mínimo 100 puntos para canjear
    private static final BigDecimal DESCUENTO_POR_100_PUNTOS = new BigDecimal("10.00"); // 100 puntos = $10 MXN

    private final ProgramaFidelidadRepository programaRepository;
    private final MovimientoPuntoRepository movimientoRepository;
    private final ClienteRepository clienteRepository;

    public ProgramaFidelidadService(
            ProgramaFidelidadRepository programaRepository,
            MovimientoPuntoRepository movimientoRepository,
            ClienteRepository clienteRepository) {
        this.programaRepository = programaRepository;
        this.movimientoRepository = movimientoRepository;
        this.clienteRepository = clienteRepository;
    }

    /**
     * Crea un programa de fidelidad para un cliente.
     * 
     * @param clienteId ID del cliente
     * @return programa creado
     */
    public ProgramaFidelidadResponseDTO crearPrograma(Integer clienteId) {
        log.info("Creando programa de fidelidad para cliente ID: {}", clienteId);

        // Validar que el cliente existe
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con ID: " + clienteId));

        // Validar que no tenga ya un programa
        if (programaRepository.existsByClienteId(clienteId)) {
            throw new BusinessException("El cliente ya tiene un programa de fidelidad activo");
        }

        ProgramaFidelidad programa = new ProgramaFidelidad();
        programa.setClienteId(clienteId);
        programa.setPuntosAcumulados(0);
        programa.setTotalCompras(BigDecimal.ZERO);
        programa.setTotalCanjeado(BigDecimal.ZERO);
        programa.setActivo(true);

        ProgramaFidelidad guardado = programaRepository.save(programa);

        log.info("Programa de fidelidad creado exitosamente. ID: {}", guardado.getId());

        return mapToResponseDTO(guardado, cliente.getNombre());
    }

    /**
     * Consulta el programa de fidelidad de un cliente.
     * 
     * @param clienteId ID del cliente
     * @return información del programa
     */
    @Transactional(readOnly = true)
    public ProgramaFidelidadResponseDTO consultarPorCliente(Integer clienteId) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con ID: " + clienteId));

        ProgramaFidelidad programa = programaRepository.findByClienteId(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("El cliente no tiene programa de fidelidad"));

        return mapToResponseDTO(programa, cliente.getNombre());
    }

    /**
     * Acumula puntos en el programa de un cliente.
     * 
     * @param request datos de la acumulación
     * @return programa actualizado
     */
    public ProgramaFidelidadResponseDTO acumularPuntos(AcumularPuntosRequestDTO request) {
        log.info("Acumulando puntos para cliente ID: {} - Monto: {}", request.clienteId(), request.montoCompra());

        Cliente cliente = clienteRepository.findById(request.clienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con ID: " + request.clienteId()));

        // Obtener o crear programa
        ProgramaFidelidad programa = programaRepository.findByClienteId(request.clienteId())
                .orElseGet(() -> {
                    log.info("Cliente sin programa, creando uno nuevo...");
                    ProgramaFidelidad nuevo = new ProgramaFidelidad();
                    nuevo.setClienteId(request.clienteId());
                    nuevo.setPuntosAcumulados(0);
                    nuevo.setTotalCompras(BigDecimal.ZERO);
                    nuevo.setTotalCanjeado(BigDecimal.ZERO);
                    nuevo.setActivo(true);
                    return programaRepository.save(nuevo);
                });

        // Validar que el programa esté activo
        if (!programa.getActivo()) {
            throw new BusinessException("El programa de fidelidad del cliente está inactivo");
        }

        // Calcular puntos: 1 punto por cada $10 MXN
        int puntosGanados = request.montoCompra()
                .divide(PESOS_POR_PUNTO, 0, RoundingMode.DOWN)
                .intValue();

        if (puntosGanados == 0) {
            throw new BusinessException("El monto de compra es insuficiente para generar puntos (mínimo $10 MXN)");
        }

        // Actualizar programa
        programa.setPuntosAcumulados(programa.getPuntosAcumulados() + puntosGanados);
        programa.setTotalCompras(programa.getTotalCompras().add(request.montoCompra()));

        programaRepository.save(programa);

        // Registrar movimiento
        MovimientoPunto movimiento = new MovimientoPunto();
        movimiento.setProgramaId(programa.getId());
        movimiento.setTipoMovimiento("ACUMULACION");
        movimiento.setPuntos(puntosGanados);
        movimiento.setMontoAsociado(request.montoCompra());
        movimiento.setVentaId(request.ventaId());
        movimiento.setDescripcion(
                request.descripcion() != null 
                        ? request.descripcion() 
                        : "Acumulación por compra de $" + request.montoCompra()
        );

        movimientoRepository.save(movimiento);

        log.info("Puntos acumulados exitosamente. Cliente: {} - Puntos: {} - Total: {}", 
                 request.clienteId(), puntosGanados, programa.getPuntosAcumulados());

        return mapToResponseDTO(programa, cliente.getNombre());
    }

    /**
     * Canjea puntos por descuento en efectivo.
     * 
     * @param request datos del canje
     * @return programa actualizado
     */
    public ProgramaFidelidadResponseDTO canjearPuntos(CanjearPuntosRequestDTO request) {
        log.info("Canjeando puntos para cliente ID: {} - Puntos: {}", request.clienteId(), request.puntosACanjear());

        Cliente cliente = clienteRepository.findById(request.clienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con ID: " + request.clienteId()));

        ProgramaFidelidad programa = programaRepository.findByClienteId(request.clienteId())
                .orElseThrow(() -> new ResourceNotFoundException("El cliente no tiene programa de fidelidad"));

        // Validar que el programa esté activo
        if (!programa.getActivo()) {
            throw new BusinessException("El programa de fidelidad del cliente está inactivo");
        }

        // Validar puntos mínimos
        if (request.puntosACanjear() < PUNTOS_MINIMOS_CANJE) {
            throw new BusinessException("Se requieren mínimo " + PUNTOS_MINIMOS_CANJE + " puntos para canjear");
        }

        // Validar saldo suficiente
        if (programa.getPuntosAcumulados() < request.puntosACanjear()) {
            throw new BusinessException("Puntos insuficientes. Disponibles: " + programa.getPuntosAcumulados());
        }

        // Calcular descuento: 100 puntos = $10 MXN
        BigDecimal descuentoGenerado = new BigDecimal(request.puntosACanjear())
                .divide(new BigDecimal("100"), 2, RoundingMode.DOWN)
                .multiply(DESCUENTO_POR_100_PUNTOS);

        // Actualizar programa
        programa.setPuntosAcumulados(programa.getPuntosAcumulados() - request.puntosACanjear());
        programa.setTotalCanjeado(programa.getTotalCanjeado().add(descuentoGenerado));

        programaRepository.save(programa);

        // Registrar movimiento
        MovimientoPunto movimiento = new MovimientoPunto();
        movimiento.setProgramaId(programa.getId());
        movimiento.setTipoMovimiento("CANJE");
        movimiento.setPuntos(-request.puntosACanjear()); // Negativo para indicar salida
        movimiento.setMontoAsociado(descuentoGenerado);
        movimiento.setVentaId(request.ventaId());
        movimiento.setDescripcion(
                request.descripcion() != null 
                        ? request.descripcion() 
                        : "Canje de " + request.puntosACanjear() + " puntos por $" + descuentoGenerado + " MXN"
        );

        movimientoRepository.save(movimiento);

        log.info("Puntos canjeados exitosamente. Cliente: {} - Puntos: {} - Descuento: ${}", 
                 request.clienteId(), request.puntosACanjear(), descuentoGenerado);

        return mapToResponseDTO(programa, cliente.getNombre());
    }

    /**
     * Obtiene el historial de movimientos de un cliente.
     * 
     * @param clienteId ID del cliente
     * @return lista de movimientos
     */
    @Transactional(readOnly = true)
    public List<MovimientoPuntoResponseDTO> obtenerHistorial(Integer clienteId) {
        ProgramaFidelidad programa = programaRepository.findByClienteId(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("El cliente no tiene programa de fidelidad"));

        List<MovimientoPunto> movimientos = movimientoRepository.obtenerHistorialPorPrograma(programa.getId());

        return movimientos.stream()
                .map(this::mapMovimientoToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Calcula el descuento equivalente en pesos para una cantidad de puntos.
     * 
     * @param puntos cantidad de puntos
     * @return descuento en MXN
     */
    public BigDecimal calcularDescuentoPorPuntos(Integer puntos) {
        if (puntos < PUNTOS_MINIMOS_CANJE) {
            return BigDecimal.ZERO;
        }

        return new BigDecimal(puntos)
                .divide(new BigDecimal("100"), 2, RoundingMode.DOWN)
                .multiply(DESCUENTO_POR_100_PUNTOS);
    }

    /**
     * Obtiene las estadísticas generales del programa de fidelidad.
     * 
     * @return estadísticas del sistema
     */
    @Transactional(readOnly = true)
    public EstadisticasFidelidadDTO obtenerEstadisticas() {
        Long totalProgramas = programaRepository.contarProgramasActivos();
        Long totalPuntos = programaRepository.obtenerTotalPuntosEnSistema();

        return new EstadisticasFidelidadDTO(
                totalProgramas,
                totalPuntos,
                PESOS_POR_PUNTO.intValue(),
                PUNTOS_MINIMOS_CANJE
        );
    }

    /**
     * Desactiva el programa de fidelidad de un cliente.
     * 
     * @param clienteId ID del cliente
     */
    public void desactivarPrograma(Integer clienteId) {
        ProgramaFidelidad programa = programaRepository.findByClienteId(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("El cliente no tiene programa de fidelidad"));

        programa.setActivo(false);
        programaRepository.save(programa);

        log.info("Programa de fidelidad desactivado para cliente ID: {}", clienteId);
    }

    /**
     * Reactiva el programa de fidelidad de un cliente.
     * 
     * @param clienteId ID del cliente
     */
    public void reactivarPrograma(Integer clienteId) {
        ProgramaFidelidad programa = programaRepository.findByClienteId(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("El cliente no tiene programa de fidelidad"));

        programa.setActivo(true);
        programaRepository.save(programa);

        log.info("Programa de fidelidad reactivado para cliente ID: {}", clienteId);
    }

    // Métodos de mapeo
    private ProgramaFidelidadResponseDTO mapToResponseDTO(ProgramaFidelidad programa, String clienteNombre) {
        return new ProgramaFidelidadResponseDTO(
                programa.getId(),
                programa.getClienteId(),
                clienteNombre,
                programa.getPuntosAcumulados(),
                programa.getTotalCompras(),
                programa.getTotalCanjeado(),
                programa.getActivo(),
                programa.getFechaCreacion(),
                programa.getFechaActualizacion()
        );
    }

    private MovimientoPuntoResponseDTO mapMovimientoToDTO(MovimientoPunto movimiento) {
        return new MovimientoPuntoResponseDTO(
                movimiento.getId(),
                movimiento.getProgramaId(),
                movimiento.getTipoMovimiento(),
                movimiento.getPuntos(),
                movimiento.getMontoAsociado(),
                movimiento.getVentaId(),
                movimiento.getDescripcion(),
                movimiento.getFechaCreacion()
        );
    }
}
