package com.nexoohub.almacen.adquisiciones.controller;

import com.nexoohub.almacen.adquisiciones.dto.AgregarAlCarritoRequest;
import com.nexoohub.almacen.adquisiciones.dto.CarritoResumenResponse;
import com.nexoohub.almacen.adquisiciones.dto.OrdenCompraResponse;
import com.nexoohub.almacen.adquisiciones.service.CarritoCompraService;
import com.nexoohub.almacen.adquisiciones.service.ExcelOrdenCompraGenerator;
import com.nexoohub.almacen.adquisiciones.service.OrdenCompraService;
import com.nexoohub.almacen.common.exception.BusinessException;
import com.nexoohub.almacen.common.repository.UsuarioRepository;
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
@RequestMapping("/api/v1/oc")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')") // Módulo restringido
public class OrdenCompraController {

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
    public ResponseEntity<Void> agregarAlCarrito(@Valid @RequestBody AgregarAlCarritoRequest request, Authentication auth) {
        carritoService.agregarAlCarrito(getUsuarioId(auth), request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/carrito/{catalogoId}")
    public ResponseEntity<Void> quitarDelCarrito(@PathVariable Integer catalogoId, Authentication auth) {
        carritoService.quitarDelCarrito(getUsuarioId(auth), catalogoId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/carrito")
    public ResponseEntity<CarritoResumenResponse> verCarrito(Authentication auth) {
        return ResponseEntity.ok(carritoService.verCarrito(getUsuarioId(auth)));
    }

    // ==========================================
    // ENDPOINTS DE ORDENES DE COMPRA
    // ==========================================

    @PostMapping("/generar")
    public ResponseEntity<List<OrdenCompraResponse>> generarOrdenes(Authentication auth) {
        return ResponseEntity.ok(ordenService.generarOrdenesDeCompra(auth.getName()));
    }

    @GetMapping
    public ResponseEntity<List<OrdenCompraResponse>> listarOrdenes(
            @RequestParam(required = false) Integer proveedorId,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) LocalDateTime fechaInicio) {
        return ResponseEntity.ok(ordenService.listarOrdenes(proveedorId, estado, fechaInicio));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<OrdenCompraResponse> cambiarEstado(
            @PathVariable Integer id,
            @RequestParam String estado,
            Authentication auth) {
        return ResponseEntity.ok(ordenService.actualizarEstado(id, estado, auth.getName()));
    }

    @PostMapping("/{id}/recibir")
    public ResponseEntity<OrdenCompraResponse> recibirOrden(
            @PathVariable Integer id,
            Authentication auth) {
        return ResponseEntity.ok(ordenService.recibirOrdenCompra(id, auth.getName()));
    }

    @GetMapping("/{id}/exportar-excel")
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
