package com.nexoohub.almacen.erp.controller;

import com.nexoohub.almacen.common.ApiResponse;
import com.nexoohub.almacen.erp.dto.DevolucionProveedorRequest;
import com.nexoohub.almacen.erp.dto.DevolucionProveedorResponse;
import com.nexoohub.almacen.erp.service.DevolucionProveedorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/devoluciones/proveedores")
@CrossOrigin(origins = "*")
@Tag(name = "ERP — Devoluciones a Proveedor", description = "Gestión de mermas y generación de saldo a favor contra compras previas")
public class DevolucionProveedorController {

    private final DevolucionProveedorService devolucionService;

    public DevolucionProveedorController(DevolucionProveedorService devolucionService) {
        this.devolucionService = devolucionService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ALMACENISTA')")
    @Operation(summary = "Registrar Devolución", description = "Genera un folio de devolución en estatus CREADA con los productos mermados. No descuenta inventario todavía.")
    public ResponseEntity<ApiResponse<DevolucionProveedorResponse>> registrarDevolucion(
            @Valid @RequestBody DevolucionProveedorRequest req) {
        DevolucionProveedorResponse resp = devolucionService.registrarDevolucion(req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("Devolución a proveedor registrada en BORRADOR", resp));
    }

    @PostMapping("/{id}/aplicar")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @Operation(summary = "Aplicar Devolución (Impacta Inventario)", description = "Cambia el estatus a APLICADA. Descuenta las existencias directamente del inventario local y genera movimientos de SALIDA_DEVOLUCION_PROVEEDOR.")
    public ResponseEntity<ApiResponse<DevolucionProveedorResponse>> aplicarDevolucion(
            @PathVariable Integer id) {
        DevolucionProveedorResponse resp = devolucionService.aplicarDevolucion(id);
        return ResponseEntity.ok(new ApiResponse<>("Devolución aplicada. Inventarios descontados correctamente", resp));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ALMACENISTA', 'MOSTRADOR')")
    @Operation(summary = "Listar Devoluciones", description = "Busca devoluciones reportadas en una sucursal en un rango de fechas")
    public ResponseEntity<ApiResponse<List<DevolucionProveedorResponse>>> listarDevoluciones(
            @RequestParam Integer sucursalId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        return ResponseEntity.ok(new ApiResponse<>(
                "Devoluciones recuperadas", devolucionService.listar(sucursalId, inicio, fin)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ALMACENISTA', 'MOSTRADOR')")
    @Operation(summary = "Ver detalle", description = "Recupera los productos afectados en un folio de devolución específico")
    public ResponseEntity<ApiResponse<DevolucionProveedorResponse>> obtenerDevolucion(
            @PathVariable Integer id) {
        return ResponseEntity.ok(new ApiResponse<>(
                "Detalle de devolución recuperado", devolucionService.obtenerPorId(id)));
    }
}
