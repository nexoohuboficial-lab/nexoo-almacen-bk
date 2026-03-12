package com.nexoohub.almacen.ventas.controller;
import com.nexoohub.almacen.ventas.dto.VentaRequestDTO;
import com.nexoohub.almacen.ventas.dto.VentaResponseDTO;
import com.nexoohub.almacen.ventas.entity.Venta;
import com.nexoohub.almacen.ventas.mapper.VentaMapper;
import com.nexoohub.almacen.ventas.service.VentaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * Controller REST para procesamiento de ventas.
 * 
 * <p>Gestiona el proceso completo de venta incluyendo:</p>
 * <ul>
 *   <li>Aplicación de precios dinámicos según tipo de cliente</li>
 *   <li>Validación de stock disponible</li>
 *   <li>Descuento automático de inventario</li>
 *   <li>Cálculo de totales y registro de transacción</li>
 * </ul>
 * 
 * <p><b>Seguridad:</b> Requiere autenticación JWT. El vendedor se obtiene del contexto de seguridad.</p>
 * 
 * @see VentaService#procesarVenta(VentaRequestDTO, String)
 * @author NexooHub Development Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/v1/ventas")
public class VentaController {

    @Autowired
    private VentaService ventaService;
    
    @Autowired
    private VentaMapper mapper;

    /**
     * Procesa y registra una nueva venta.
     * 
     * <p>Flujo de operación:</p>
     * <ol>
     *   <li>Valida existencia de cliente y vendedor</li>
     *   <li>Obtiene precios según tipo de cliente o precios especiales</li>
     *   <li>Verifica stock disponible en sucursal</li>
     *   <li>Descuenta inventario (stock = stock - cantidad)</li>
     *   <li>Registra venta con detalles</li>
     * </ol>
     * 
     * @param request datos de la venta (cliente, productos, cantidades)
     * @return venta registrada en formato DTO con detalles completos
     * @throws ResourceNotFoundException si cliente, vendedor o producto no existen
     * @throws StockInsuficienteException si no hay suficiente inventario
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR', 'CAJERO')")
    public ResponseEntity<VentaResponseDTO> realizarVenta(@Valid @RequestBody VentaRequestDTO request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Venta ventaRealizada = ventaService.procesarVenta(request, username);
        return new ResponseEntity<>(mapper.toResponseDTO(ventaRealizada), HttpStatus.CREATED);
    }
}