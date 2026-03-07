package com.nexoohub.almacen.ventas.controller;

import com.nexoohub.almacen.ventas.dto.DevolucionRequestDTO;
import com.nexoohub.almacen.ventas.dto.DevolucionResponseDTO;
import com.nexoohub.almacen.ventas.service.DevolucionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestión de devoluciones.
 * 
 * <p>Endpoints disponibles:</p>
 * <ul>
 *   <li>POST /api/v1/devoluciones - Procesar una devolución</li>
 *   <li>GET /api/v1/devoluciones/{id} - Obtener devolución por ID</li>
 *   <li>GET /api/v1/devoluciones/venta/{ventaId} - Obtener devoluciones de una venta</li>
 * </ul>
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
@RestController
@RequestMapping("/api/v1/devoluciones")
public class DevolucionController {
    
    private final DevolucionService devolucionService;
    
    public DevolucionController(DevolucionService devolucionService) {
        this.devolucionService = devolucionService;
    }
    
    /**
     * Procesa una devolución de productos.
     * 
     * <p><b>Body esperado:</b></p>
     * <pre>
     * {
     *   "ventaId": 123,
     *   "sucursalId": 1,
     *   "motivo": "Producto defectuoso",
     *   "metodoReembolso": "EFECTIVO",
     *   "items": [
     *     {
     *       "skuInterno": "REPUESTO-001",
     *       "cantidad": 2,
     *       "motivoItem": "Pieza rota"
     *     }
     *   ]
     * }
     * </pre>
     * 
     * @param request DTO con información de la devolución
     * @return devolución procesada con código 201 (Created)
     */
    @PostMapping
    public ResponseEntity<DevolucionResponseDTO> procesarDevolucion(@Valid @RequestBody DevolucionRequestDTO request) {
        DevolucionResponseDTO response = devolucionService.procesarDevolucion(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * Obtiene una devolución por su ID.
     * 
     * @param id ID de la devolución
     * @return devolución encontrada
     */
    @GetMapping("/{id}")
    public ResponseEntity<DevolucionResponseDTO> obtenerDevolucionPorId(@PathVariable("id") Integer id) {
        DevolucionResponseDTO response = devolucionService.obtenerDevolucionPorId(id);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Obtiene todas las devoluciones asociadas a una venta.
     * 
     * @param ventaId ID de la venta
     * @return lista de devoluciones
     */
    @GetMapping("/venta/{ventaId}")
    public ResponseEntity<List<DevolucionResponseDTO>> obtenerDevolucionesPorVenta(@PathVariable("ventaId") Integer ventaId) {
        List<DevolucionResponseDTO> response = devolucionService.obtenerDevolucionesPorVenta(ventaId);
        return ResponseEntity.ok(response);
    }
}
