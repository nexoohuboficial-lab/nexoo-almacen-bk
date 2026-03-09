package com.nexoohub.almacen.cotizaciones.controller;

import com.nexoohub.almacen.cotizaciones.dto.*;
import com.nexoohub.almacen.cotizaciones.service.CotizacionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller REST para gestión de cotizaciones (presupuestos).
 * 
 * <p>Gestiona el ciclo de vida completo de cotizaciones:</p>
 * <ul>
 *   <li>Creación de cotizaciones con generación automática de folio</li>
 *   <li>Actualización de cotizaciones en estado BORRADOR</li>
 *   <li>Cambio de estados (enviar, aceptar, rechazar)</li>
 *   <li>Conversión a ventas con validación de stock</li>
 *   <li>Seguimiento de vencimiento y pendientes</li>
 *   <li>Estadísticas y reportes</li>
 * </ul>
 * 
 * <p><b>Seguridad:</b> Requiere autenticación JWT.</p>
 * 
 * @see CotizacionService
 * @author NexooHub Development Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/cotizaciones")
public class CotizacionController {
    
    private final CotizacionService cotizacionService;
    
    public CotizacionController(CotizacionService cotizacionService) {
        this.cotizacionService = cotizacionService;
    }
    
    /**
     * Crea una nueva cotización.
     * 
     * <p>Flujo de operación:</p>
     * <ol>
     *   <li>Valida existencia de cliente, sucursal y productos</li>
     *   <li>Genera folio automático (COT-YYYY-NNNN)</li>
     *   <li>Calcula totales automáticamente</li>
     *   <li>Crea cotización en estado BORRADOR</li>
     * </ol>
     * 
     * @param request datos de la cotización
     * @return cotización creada con folio generado
     */
    @PostMapping
    public ResponseEntity<CotizacionResponseDTO> crearCotizacion(@Valid @RequestBody CotizacionRequestDTO request) {
        CotizacionResponseDTO cotizacion = cotizacionService.crearCotizacion(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(cotizacion);
    }
    
    /**
     * Actualiza una cotización existente.
     * 
     * <p><b>Restricción:</b> Solo se pueden actualizar cotizaciones en estado BORRADOR.</p>
     * 
     * @param id ID de la cotización
     * @param request nuevos datos de la cotización
     * @return cotización actualizada
     */
    @PutMapping("/{id}")
    public ResponseEntity<CotizacionResponseDTO> actualizarCotizacion(
            @PathVariable Long id,
            @Valid @RequestBody CotizacionRequestDTO request) {
        CotizacionResponseDTO cotizacion = cotizacionService.actualizarCotizacion(id, request);
        return ResponseEntity.ok(cotizacion);
    }
    
    /**
     * Obtiene una cotización por ID.
     * 
     * @param id ID de la cotización
     * @return cotización con todos sus detalles
     */
    @GetMapping("/{id}")
    public ResponseEntity<CotizacionResponseDTO> obtenerCotizacion(@PathVariable Long id) {
        CotizacionResponseDTO cotizacion = cotizacionService.obtenerCotizacionPorId(id);
        return ResponseEntity.ok(cotizacion);
    }
    
    /**
     * Obtiene una cotización por folio.
     * 
     * @param folio folio de la cotización (ej: COT-2026-0001)
     * @return cotización con todos sus detalles
     */
    @GetMapping("/folio/{folio}")
    public ResponseEntity<CotizacionResponseDTO> obtenerCotizacionPorFolio(@PathVariable String folio) {
        CotizacionResponseDTO cotizacion = cotizacionService.obtenerCotizacionPorFolio(folio);
        return ResponseEntity.ok(cotizacion);
    }
    
    /**
     * Lista todas las cotizaciones con paginación y filtros opcionales.
     * 
     * @param clienteId filtro por cliente (opcional)
     * @param sucursalId filtro por sucursal (opcional)
     * @param vendedorId filtro por vendedor (opcional)
     * @param estado filtro por estado (opcional): BORRADOR, ENVIADA, ACEPTADA, RECHAZADA, VENCIDA, CONVERTIDA
     * @param fechaInicio filtro por fecha de inicio (opcional)
     * @param fechaFin filtro por fecha de fin (opcional)
     * @param pageable configuración de paginación
     * @return página de cotizaciones
     */
    @GetMapping
    public ResponseEntity<Page<CotizacionResponseDTO>> listarCotizaciones(
            @RequestParam(required = false) Integer clienteId,
            @RequestParam(required = false) Integer sucursalId,
            @RequestParam(required = false) Integer vendedorId,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) LocalDateTime fechaInicio,
            @RequestParam(required = false) LocalDateTime fechaFin,
            @PageableDefault(size = 20, sort = "fechaCotizacion", direction = Sort.Direction.DESC) Pageable pageable) {
        
        Page<CotizacionResponseDTO> cotizaciones;
        
        // Si no hay filtros, listar todas
        if (clienteId == null && sucursalId == null && vendedorId == null && 
            estado == null && fechaInicio == null && fechaFin == null) {
            cotizaciones = cotizacionService.listarCotizaciones(pageable);
        } else {
            cotizaciones = cotizacionService.buscarConFiltros(
                clienteId, sucursalId, vendedorId, estado, fechaInicio, fechaFin, pageable
            );
        }
        
        return ResponseEntity.ok(cotizaciones);
    }
    
    /**
     * Cambia el estado de una cotización.
     * 
     * <p>Estados permitidos:</p>
     * <ul>
     *   <li><b>ENVIADA:</b> Marca la cotización como enviada al cliente (desde BORRADOR)</li>
     *   <li><b>ACEPTADA:</b> Marca la cotización como aceptada por el cliente (desde ENVIADA)</li>
     *   <li><b>RECHAZADA:</b> Marca la cotización como rechazada (requiere motivo)</li>
     * </ul>
     * 
     * @param id ID de la cotización
     * @param request datos del cambio de estado (nuevo estado y motivo si aplica)
     * @return cotización con estado actualizado
     */
    @PutMapping("/{id}/estado")
    public ResponseEntity<CotizacionResponseDTO> cambiarEstado(
            @PathVariable Long id,
            @Valid @RequestBody CambiarEstadoRequestDTO request) {
        CotizacionResponseDTO cotizacion = cotizacionService.cambiarEstado(id, request);
        return ResponseEntity.ok(cotizacion);
    }
    
    /**
     * Convierte una cotización en venta.
     * 
     * <p>Flujo de operación:</p>
     * <ol>
     *   <li>Valida que la cotización pueda convertirse (estado ENVIADA o ACEPTADA, no vencida)</li>
     *   <li>Valida stock disponible de todos los productos</li>
     *   <li>Crea la venta con los mismos datos de la cotización</li>
     *   <li>Actualiza el inventario (descuenta stock)</li>
     *   <li>Marca la cotización como CONVERTIDA</li>
     * </ol>
     * 
     * @param id ID de la cotización
     * @param request datos de la conversión (método de pago)
     * @return ID de la venta creada
     */
    @PostMapping("/{id}/convertir-venta")
    public ResponseEntity<Map<String, Object>> convertirAVenta(
            @PathVariable Long id,
            @Valid @RequestBody ConvertirVentaRequestDTO request) {
        Integer ventaId = cotizacionService.convertirAVenta(id, request);
        
        Map<String, Object> response = new HashMap<>();
        response.put("ventaId", ventaId);
        response.put("cotizacionId", id);
        response.put("mensaje", "Cotización convertida exitosamente a venta");
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * Obtiene cotizaciones próximas a vencer.
     * 
     * <p>Devuelve cotizaciones cuya fecha de validez está dentro de los próximos N días
     * y que no han sido convertidas, rechazadas o ya marcadas como vencidas.</p>
     * 
     * @param dias días de anticipación para alertar (por defecto 7)
     * @return lista de cotizaciones próximas a vencer
     */
    @GetMapping("/vencimiento/proximas")
    public ResponseEntity<List<CotizacionResponseDTO>> obtenerProximasAVencer(
            @RequestParam(defaultValue = "7") int dias) {
        List<CotizacionResponseDTO> cotizaciones = cotizacionService.obtenerProximasAVencer(dias);
        return ResponseEntity.ok(cotizaciones);
    }
    
    /**
     * Obtiene cotizaciones pendientes de conversión.
     * 
     * <p>Devuelve cotizaciones en estado ENVIADA o ACEPTADA que no están vencidas
     * y aún no han sido convertidas a venta.</p>
     * 
     * @return lista de cotizaciones listas para convertir
     */
    @GetMapping("/pendientes-conversion")
    public ResponseEntity<List<CotizacionResponseDTO>> obtenerPendientesDeConversion() {
        List<CotizacionResponseDTO> cotizaciones = cotizacionService.obtenerPendientesDeConversion();
        return ResponseEntity.ok(cotizaciones);
    }
    
    /**
     * Obtiene estadísticas generales de cotizaciones.
     * 
     * <p>Incluye:</p>
     * <ul>
     *   <li>Conteo de cotizaciones por estado</li>
     *   <li>Valor total cotizado por estado</li>
     *   <li>Tasa de conversión (% convertidas a ventas)</li>
     *   <li>Tasa de aceptación (% aceptadas del total enviadas)</li>
     *   <li>Tasa de rechazo (% rechazadas del total enviadas)</li>
     * </ul>
     * 
     * @return estadísticas completas
     */
    @GetMapping("/estadisticas")
    public ResponseEntity<EstadisticasCotizacionDTO> obtenerEstadisticas() {
        EstadisticasCotizacionDTO estadisticas = cotizacionService.obtenerEstadisticas();
        return ResponseEntity.ok(estadisticas);
    }
    
    /**
     * Marca automáticamente las cotizaciones vencidas.
     * 
     * <p>Busca cotizaciones cuya fecha de validez ya pasó y las marca como VENCIDAS.
     * Este endpoint puede ser llamado por un job programado.</p>
     * 
     * @return mensaje con cantidad de cotizaciones actualizadas
     */
    @PostMapping("/marcar-vencidas")
    public ResponseEntity<Map<String, Object>> marcarCotizacionesVencidas() {
        int cantidadActualizadas = cotizacionService.marcarCotizacionesVencidas();
        
        Map<String, Object> response = new HashMap<>();
        response.put("cantidadActualizadas", cantidadActualizadas);
        response.put("mensaje", cantidadActualizadas + " cotizaciones marcadas como vencidas");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Elimina una cotización.
     * 
     * <p><b>Restricción:</b> Solo se pueden eliminar cotizaciones en estado BORRADOR.</p>
     * 
     * @param id ID de la cotización
     * @return mensaje de confirmación
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> eliminarCotizacion(@PathVariable Long id) {
        cotizacionService.eliminarCotizacion(id);
        
        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "Cotización eliminada exitosamente");
        
        return ResponseEntity.ok(response);
    }
}
