package com.nexoohub.almacen.pos.controller;

import com.nexoohub.almacen.common.ApiResponse;
import com.nexoohub.almacen.pos.dto.CancelacionCfdiRequest;
import com.nexoohub.almacen.pos.dto.FacturaFiscalResponse;
import com.nexoohub.almacen.pos.dto.TimbradoRequest;
import com.nexoohub.almacen.pos.service.FacturacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para Facturación Electrónica SAT/CFDI (POS-03).
 */
@RestController
@RequestMapping("/api/v1/facturacion")
@CrossOrigin(origins = "*")
@Tag(name = "Facturación SAT", description = "Generación, cancelación y consulta de CFDI 4.0")
public class FacturacionController {

    private final FacturacionService facturacionService;

    public FacturacionController(FacturacionService facturacionService) {
        this.facturacionService = facturacionService;
    }

    @PostMapping("/timbrar")
    @PreAuthorize("hasAnyRole('ADMIN', 'CAJERO')")
    @Operation(summary = "Timbrar Factura CFDI", description = "Genera un CFDI 4.0 a través del PAC activo")
    public ResponseEntity<ApiResponse<FacturaFiscalResponse>> timbrar(
            @Valid @RequestBody TimbradoRequest request) {
        FacturaFiscalResponse factura = facturacionService.timbrarFactura(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new ApiResponse<>("Factura timbrada exitosamente (Folio SAT: " + factura.getUuid() + ")", factura));
    }

    @PostMapping("/{facturaId}/cancelar")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(summary = "Cancelar Factura", description = "Solicita cancelación de CFDI al SAT")
    public ResponseEntity<ApiResponse<FacturaFiscalResponse>> cancelarFactura(
            @PathVariable Integer facturaId,
            @Valid @RequestBody CancelacionCfdiRequest request) {
        FacturaFiscalResponse factura = facturacionService.cancelarFactura(facturaId, request);
        return ResponseEntity.ok(new ApiResponse<>("Factura cancelada", factura));
    }

    @GetMapping("/{facturaId}/descargar")
    @PreAuthorize("hasAnyRole('ADMIN', 'CAJERO', 'VENDEDOR')")
    @Operation(summary = "Descargar archivos de factura", description = "Retorna XML y URL del PDF")
    public ResponseEntity<ApiResponse<FacturaFiscalResponse>> descargarFactura(
            @PathVariable Integer facturaId) {
        FacturaFiscalResponse factura = facturacionService.descargarFactura(facturaId);
        return ResponseEntity.ok(new ApiResponse<>("Datos de descarga listos", factura));
    }

    @GetMapping("/cliente/{clienteId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CAJERO', 'VENDEDOR')")
    @Operation(summary = "Listar facturas por cliente", description = "Obtiene todo el historial de CFDI de un cliente")
    public ResponseEntity<ApiResponse<List<FacturaFiscalResponse>>> consultarFacturasCliente(
            @PathVariable Integer clienteId) {
        List<FacturaFiscalResponse> facturas = facturacionService.consultarFacturasPorCliente(clienteId);
        return ResponseEntity.ok(new ApiResponse<>("Facturas del cliente recuperadas", facturas));
    }
}
