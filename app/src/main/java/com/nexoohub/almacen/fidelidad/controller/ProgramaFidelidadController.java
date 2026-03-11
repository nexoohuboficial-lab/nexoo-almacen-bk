package com.nexoohub.almacen.fidelidad.controller;

import com.nexoohub.almacen.fidelidad.dto.*;
import com.nexoohub.almacen.fidelidad.service.ProgramaFidelidadService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller REST para gestión del programa de fidelidad.
 * 
 * <p>Proporciona endpoints para:</p>
 * <ul>
 *   <li>Crear programas de fidelidad para clientes</li>
 *   <li>Acumular puntos por compras</li>
 *   <li>Canjear puntos por descuentos</li>
 *   <li>Consultar saldo y historial de movimientos</li>
 *   <li>Obtener estadísticas del sistema</li>
 * </ul>
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
@RestController
@RequestMapping("/api/v1/fidelidad")
public class ProgramaFidelidadController {

    private final ProgramaFidelidadService fidelidadService;

    public ProgramaFidelidadController(ProgramaFidelidadService fidelidadService) {
        this.fidelidadService = fidelidadService;
    }

    /**
     * Crea un nuevo programa de fidelidad para un cliente.
     * 
     * @param clienteId ID del cliente
     * @return programa creado
     */
    @PostMapping("/programa")
    public ResponseEntity<Map<String, Object>> crearPrograma(@RequestParam Integer clienteId) {
        ProgramaFidelidadResponseDTO programa = fidelidadService.crearPrograma(clienteId);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("exitoso", true);
        respuesta.put("mensaje", "Programa de fidelidad creado exitosamente");
        respuesta.put("data", programa);

        return new ResponseEntity<>(respuesta, HttpStatus.CREATED);
    }

    /**
     * Consulta el programa de fidelidad de un cliente.
     * 
     * @param clienteId ID del cliente
     * @return información del programa
     */
    @GetMapping("/programa/cliente/{clienteId}")
    public ResponseEntity<ProgramaFidelidadResponseDTO> consultarPorCliente(@PathVariable Integer clienteId) {
        ProgramaFidelidadResponseDTO programa = fidelidadService.consultarPorCliente(clienteId);
        return ResponseEntity.ok(programa);
    }

    /**
     * Acumula puntos en el programa de un cliente.
     * 
     * @param request datos de la acumulación
     * @return programa actualizado
     */
    @PostMapping("/acumular")
    public ResponseEntity<Map<String, Object>> acumularPuntos(@Valid @RequestBody AcumularPuntosRequestDTO request) {
        ProgramaFidelidadResponseDTO programa = fidelidadService.acumularPuntos(request);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("exitoso", true);
        respuesta.put("mensaje", "Puntos acumulados exitosamente");
        respuesta.put("data", programa);

        return ResponseEntity.ok(respuesta);
    }

    /**
     * Canjea puntos por descuento.
     * 
     * @param request datos del canje
     * @return programa actualizado
     */
    @PostMapping("/canjear")
    public ResponseEntity<Map<String, Object>> canjearPuntos(@Valid @RequestBody CanjearPuntosRequestDTO request) {
        ProgramaFidelidadResponseDTO programa = fidelidadService.canjearPuntos(request);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("exitoso", true);
        respuesta.put("mensaje", "Puntos canjeados exitosamente");
        respuesta.put("data", programa);

        return ResponseEntity.ok(respuesta);
    }

    /**
     * Obtiene el historial de movimientos de puntos de un cliente.
     * 
     * @param clienteId ID del cliente
     * @return lista de movimientos
     */
    @GetMapping("/historial/cliente/{clienteId}")
    public ResponseEntity<List<MovimientoPuntoResponseDTO>> obtenerHistorial(@PathVariable Integer clienteId) {
        List<MovimientoPuntoResponseDTO> historial = fidelidadService.obtenerHistorial(clienteId);
        return ResponseEntity.ok(historial);
    }

    /**
     * Calcula el descuento en pesos que equivale a una cantidad de puntos.
     * 
     * @param puntos cantidad de puntos
     * @return descuento en MXN
     */
    @GetMapping("/calcular-descuento")
    public ResponseEntity<Map<String, Object>> calcularDescuento(@RequestParam Integer puntos) {
        BigDecimal descuento = fidelidadService.calcularDescuentoPorPuntos(puntos);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("puntos", puntos);
        respuesta.put("descuentoMXN", descuento);

        return ResponseEntity.ok(respuesta);
    }

    /**
     * Obtiene estadísticas generales del programa de fidelidad.
     * 
     * @return estadísticas del sistema
     */
    @GetMapping("/estadisticas")
    public ResponseEntity<EstadisticasFidelidadDTO> obtenerEstadisticas() {
        EstadisticasFidelidadDTO estadisticas = fidelidadService.obtenerEstadisticas();
        return ResponseEntity.ok(estadisticas);
    }

    /**
     * Desactiva el programa de fidelidad de un cliente.
     * 
     * @param clienteId ID del cliente
     * @return respuesta de éxito
     */
    @PatchMapping("/programa/cliente/{clienteId}/desactivar")
    public ResponseEntity<Map<String, Object>> desactivarPrograma(@PathVariable Integer clienteId) {
        fidelidadService.desactivarPrograma(clienteId);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("exitoso", true);
        respuesta.put("mensaje", "Programa de fidelidad desactivado");

        return ResponseEntity.ok(respuesta);
    }

    /**
     * Reactiva el programa de fidelidad de un cliente.
     * 
     * @param clienteId ID del cliente
     * @return respuesta de éxito
     */
    @PatchMapping("/programa/cliente/{clienteId}/reactivar")
    public ResponseEntity<Map<String, Object>> reactivarPrograma(@PathVariable Integer clienteId) {
        fidelidadService.reactivarPrograma(clienteId);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("exitoso", true);
        respuesta.put("mensaje", "Programa de fidelidad reactivado");

        return ResponseEntity.ok(respuesta);
    }
}
