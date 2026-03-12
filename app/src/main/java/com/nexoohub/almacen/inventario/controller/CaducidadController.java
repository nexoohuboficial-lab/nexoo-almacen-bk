package com.nexoohub.almacen.inventario.controller;

import com.nexoohub.almacen.inventario.dto.ProductoCaducidadDTO;
import com.nexoohub.almacen.inventario.repository.InventarioSucursalRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Controlador para gestión de caducidad de productos.
 * 
 * <p>Endpoints disponibles:</p>
 * <ul>
 *   <li>GET /api/v1/inventario/caducidad/proximos - Productos próximos a caducar</li>
 *   <li>GET /api/v1/inventario/caducidad/vencidos - Productos ya caducados</li>
 * </ul>
 * 
 * @author NexooHub Development Team
 * @since 1.0
 */
@RestController
@RequestMapping("/api/v1/inventario/caducidad")
public class CaducidadController {
    
    private final InventarioSucursalRepository inventarioRepository;
    
    public CaducidadController(InventarioSucursalRepository inventarioRepository) {
        this.inventarioRepository = inventarioRepository;
    }
    
    /**
     * Obtiene productos próximos a caducar.
     * 
     * @param dias número de días límite (por defecto 30 días)
     * @return lista de productos próximos a caducar
     */
    @GetMapping("/proximos")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'SUPERVISOR', 'ALMACENISTA')")
    public ResponseEntity<List<ProductoCaducidadDTO>> obtenerProductosProximosCaducar(
            @RequestParam(defaultValue = "30") Integer dias) {
        LocalDate fechaLimite = LocalDate.now().plusDays(dias);
        List<ProductoCaducidadDTO> productos = inventarioRepository.obtenerProductosProximosCaducar(fechaLimite);
        return ResponseEntity.ok(productos);
    }
    
    /**
     * Obtiene productos ya caducados.
     * 
     * @return lista de productos caducados
     */
    @GetMapping("/vencidos")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'SUPERVISOR', 'ALMACENISTA')")
    public ResponseEntity<List<ProductoCaducidadDTO>> obtenerProductosCaducados() {
        List<ProductoCaducidadDTO> productos = inventarioRepository.obtenerProductosCaducados();
        return ResponseEntity.ok(productos);
    }
}
