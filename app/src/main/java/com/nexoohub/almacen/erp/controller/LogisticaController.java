package com.nexoohub.almacen.erp.controller;

import com.nexoohub.almacen.common.ApiResponse;
import com.nexoohub.almacen.erp.dto.ChoferDTO;
import com.nexoohub.almacen.erp.dto.RutaEntregaRequest;
import com.nexoohub.almacen.erp.dto.RutaEntregaResponse;
import com.nexoohub.almacen.erp.dto.RutaFacturaRequest;
import com.nexoohub.almacen.erp.dto.VehiculoDTO;
import com.nexoohub.almacen.erp.service.LogisticaService;
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
@RequestMapping("/api/v1/logistica")
@CrossOrigin(origins = "*")
@Tag(name = "ERP — Logística y Entregas", description = "Control de Rutas, Flotilla propia y envíos por Paquetería (Guías)")
public class LogisticaController {

    private final LogisticaService logisticaService;

    public LogisticaController(LogisticaService logisticaService) {
        this.logisticaService = logisticaService;
    }

    // =====================================
    // Catálogos
    // =====================================

    @GetMapping("/vehiculos")
    @PreAuthorize("hasAnyRole('ADMIN', 'ALMACEN', 'GERENTE')")
    @Operation(summary = "Catálogo de Vehículos", description = "Devuelve los vehículos activos en la sucursal del operario")
    public ResponseEntity<ApiResponse<List<VehiculoDTO>>> listarVehiculos(
            @RequestParam Integer sucursalId) {
        return ResponseEntity.ok(new ApiResponse<>("Vehículos disponibles", logisticaService.listarVehiculos(sucursalId)));
    }

    @GetMapping("/choferes")
    @PreAuthorize("hasAnyRole('ADMIN', 'ALMACEN', 'GERENTE')")
    @Operation(summary = "Catálogo de Choferes", description = "Devuelve los choferes activos en la sucursal del operario")
    public ResponseEntity<ApiResponse<List<ChoferDTO>>> listarChoferes(
            @RequestParam Integer sucursalId) {
        return ResponseEntity.ok(new ApiResponse<>("Choferes disponibles", logisticaService.listarChoferes(sucursalId)));
    }

    // =====================================
    // Rutas / Envíos
    // =====================================

    @PostMapping("/rutas")
    @PreAuthorize("hasAnyRole('ADMIN', 'ALMACEN')")
    @Operation(summary = "Crear Ruta o Envío", description = "Registra una ruta con Flotilla local, o una orden de paquetería externa (ML, DHL)")
    public ResponseEntity<ApiResponse<RutaEntregaResponse>> crearRuta(
            @Valid @RequestBody RutaEntregaRequest req) {
        RutaEntregaResponse resp = logisticaService.crearRuta(req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("Ruta creada exitosamente", resp));
    }

    @GetMapping("/rutas")
    @PreAuthorize("hasAnyRole('ADMIN', 'ALMACEN', 'GERENTE')")
    @Operation(summary = "Listar Rutas/Envíos", description = "Lista las rutas y envíos en el rango de fechas programado")
    public ResponseEntity<ApiResponse<List<RutaEntregaResponse>>> listarRutas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return ResponseEntity.ok(new ApiResponse<>("Rutas encontradas", logisticaService.listarRutas(desde, hasta)));
    }

    @PostMapping("/rutas/{id}/facturas")
    @PreAuthorize("hasAnyRole('ADMIN', 'ALMACEN')")
    @Operation(summary = "Asignar Factura/Paquete a Ruta", description = "Asigna una factura/venta a la ruta, permitiendo capturar el número de guía de rastreo si amerita")
    public ResponseEntity<ApiResponse<RutaEntregaResponse>> asignarFactura(
            @PathVariable Integer id,
            @Valid @RequestBody RutaFacturaRequest req) {
        RutaEntregaResponse resp = logisticaService.asignarFactura(id, req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>("Paquete/Factura asignado a la ruta exitosamente", resp));
    }

    @PatchMapping("/rutas/{id}/estatus")
    @PreAuthorize("hasAnyRole('ADMIN', 'ALMACEN')")
    @Operation(summary = "Actualizar estatus de Ruta", description = "Transiciona el estado de la entrega (EN_TRANSITO, COMPLETADA, CANCELADA)")
    public ResponseEntity<ApiResponse<RutaEntregaResponse>> cambiarEstatusRuta(
            @PathVariable Integer id,
            @RequestParam String estatus) {
        return ResponseEntity.ok(new ApiResponse<>("Estatus actualizado exitosamente", logisticaService.cambiarEstatusRuta(id, estatus)));
    }
}
