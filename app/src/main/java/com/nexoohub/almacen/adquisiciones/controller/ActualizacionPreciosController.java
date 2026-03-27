package com.nexoohub.almacen.adquisiciones.controller;

import com.nexoohub.almacen.adquisiciones.dto.ActualizacionMasivaRequest;
import com.nexoohub.almacen.adquisiciones.dto.ActualizarPrecioRequest;
import com.nexoohub.almacen.adquisiciones.dto.OpcionCompraProveedorDTO;
import com.nexoohub.almacen.adquisiciones.dto.ResultadoActualizacionResponse;
import com.nexoohub.almacen.adquisiciones.entity.HistorialPrecioProveedor;
import com.nexoohub.almacen.adquisiciones.service.ActualizacionPreciosProveedorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/comparador/catalogo")
@RequiredArgsConstructor
@Tag(name = "Actualización de Precios Proveedor", description = "Módulo SUP-02: Actualización de precios del catálogo de proveedores")
public class ActualizacionPreciosController {

    private final ActualizacionPreciosProveedorService actualizacionService;

    @Operation(summary = "Actualizar precio individual", description = "Actualiza el precio de un artículo del catálogo y recalcula su rentabilidad")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/precio")
    public ResponseEntity<OpcionCompraProveedorDTO> actualizarPrecio(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarPrecioRequest request,
            Authentication authentication) {
        
        String usuario = authentication != null ? authentication.getName() : "SISTEMA";
        OpcionCompraProveedorDTO resultado = actualizacionService.actualizarPrecioIndividual(id, request, usuario);
        return ResponseEntity.ok(resultado);
    }

    @Operation(summary = "Actualización masiva de precios", description = "Procesa una lista de cambios de precios para aplicar al catálogo")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/actualizar-masivo")
    public ResponseEntity<ResultadoActualizacionResponse> actualizarMasivo(
            @Valid @RequestBody ActualizacionMasivaRequest request,
            Authentication authentication) {
        
        String usuario = authentication != null ? authentication.getName() : "SISTEMA";
        ResultadoActualizacionResponse resultado = actualizacionService.actualizarPreciosMasivo(request, usuario);
        return ResponseEntity.ok(resultado);
    }

    @Operation(summary = "Ver historial de precios", description = "Lista los cambios de precios históricos de un producto en el catálogo")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}/historial")
    public ResponseEntity<List<HistorialPrecioProveedor>> verHistorial(@PathVariable Long id) {
        return ResponseEntity.ok(actualizacionService.verHistorial(id));
    }
}
