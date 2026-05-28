package com.nexoohub.almacen.adquisiciones.controller;

import com.nexoohub.almacen.adquisiciones.dto.AgregarAlCarritoRequest;
import com.nexoohub.almacen.adquisiciones.dto.CarritoResumenResponse;
import com.nexoohub.almacen.adquisiciones.dto.OrdenCompraResponse;
import com.nexoohub.almacen.adquisiciones.service.CarritoCompraService;
import com.nexoohub.almacen.adquisiciones.service.ExcelOrdenCompraGenerator;
import com.nexoohub.almacen.adquisiciones.service.OrdenCompraService;
import com.nexoohub.almacen.common.exception.BusinessException;
import com.nexoohub.almacen.common.repository.UsuarioRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/ordenes-compra")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')") // Módulo restringido
@Tag(name = "Órdenes de Compra V1", description = "Módulo SUP-03: Control de abastecimiento, carrito universal de proveedores y gestión de Órdenes de Compra (OC)")
public class OrdenCompraV1Controller {

    private final CarritoCompraService carritoService;
    private final OrdenCompraService ordenService;
    private final ExcelOrdenCompraGenerator excelGenerator;
    private final UsuarioRepository usuarioRepository;

    private Integer getUsuarioId(Authentication auth) {
        com.nexoohub.almacen.common.entity.Usuario usuario = usuarioRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new BusinessException("Usuario no encontrado"));
        return usuario.getId().intValue();
    }

    // ==========================================
    // ENDPOINTS DEL CARRITO
    // ==========================================

    @PostMapping("/carrito/agregar")
    @Operation(summary = "Agregar al carrito universal", description = "Añade un producto y cantidad al carrito de compras de proveedor para el usuario autenticado")
    public ResponseEntity<Void> agregarAlCarrito(@Valid @RequestBody AgregarAlCarritoRequest request, Authentication auth) {
        carritoService.agregarAlCarrito(getUsuarioId(auth), request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/carrito/{catalogoId}")
    @Operation(summary = "Quitar del carrito", description = "Elimina un artículo específico del carrito universal de compras")
    public ResponseEntity<Void> quitarDelCarrito(@PathVariable Integer catalogoId, Authentication auth) {
        carritoService.quitarDelCarrito(getUsuarioId(auth), catalogoId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/carrito")
    @Operation(summary = "Ver carrito universal", description = "Lista los artículos agregados actualmente al carrito agrupados por proveedor")
    public ResponseEntity<CarritoResumenResponse> verCarrito(Authentication auth) {
        return ResponseEntity.ok(carritoService.verCarrito(getUsuarioId(auth)));
    }

    // ==========================================
    // ENDPOINTS DE ORDENES DE COMPRA
    // ==========================================

    @PostMapping("/generar")
    @Operation(summary = "Generar Órdenes de Compra", description = "Fragmenta el carrito universal y crea Órdenes de Compra individuales por proveedor")
    public ResponseEntity<List<OrdenCompraResponse>> generarOrdenes(Authentication auth) {
        return ResponseEntity.ok(ordenService.generarOrdenesDeCompra(auth.getName()));
    }

    @GetMapping
    @Operation(summary = "Listar Órdenes de Compra", description = "Recupera el historial de órdenes de compra del sistema con filtros opcionales")
    public ResponseEntity<List<OrdenCompraResponse>> listarOrdenes(
            @RequestParam(required = false) Integer proveedorId,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) LocalDateTime fechaInicio) {
        return ResponseEntity.ok(ordenService.listarOrdenes(proveedorId, estado, fechaInicio));
    }

    @PatchMapping("/{id}/estado")
    @Operation(summary = "Cambiar estado de Orden de Compra", description = "Modifica manualmente el estado de una Orden de Compra (ej. ENVIADA, CANCELADA)")
    public ResponseEntity<OrdenCompraResponse> cambiarEstado(
            @PathVariable Integer id,
            @RequestParam String estado,
            Authentication auth) {
        return ResponseEntity.ok(ordenService.actualizarEstado(id, estado, auth.getName()));
    }

    @PostMapping("/{id}/recibir")
    @Operation(summary = "Confirmar recepción física de OC", description = "Cruza los artículos de la Orden de Compra con el inventario físico y actualiza el stock real")
    public ResponseEntity<OrdenCompraResponse> recibirOrden(
            @PathVariable Integer id,
            Authentication auth) {
        return ResponseEntity.ok(ordenService.recibirOrdenCompra(id, auth.getName()));
    }

    @GetMapping("/{id}/exportar-excel")
    @Operation(summary = "Exportar Orden de Compra a Excel", description = "Genera y descarga un reporte formal en formato XLSX usando Apache POI")
    public ResponseEntity<byte[]> exportarExcel(@PathVariable Integer id) {
        OrdenCompraResponse response = ordenService.obtenerOrdenCompraResponse(id);
        byte[] excelBytes = excelGenerator.generarExcel(response);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", response.getFolio() + ".xlsx");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(excelBytes);
    }
}
