package com.nexoohub.almacen.inventario.controller;

import com.nexoohub.almacen.inventario.dto.*;
import com.nexoohub.almacen.inventario.service.EscaneoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inventario")
public class EscaneoController {

    private final EscaneoService escaneoService;

    public EscaneoController(EscaneoService escaneoService) {
        this.escaneoService = escaneoService;
    }

    // ========================================================================
    // ESCANEO UNIVERSAL
    // ========================================================================

    /**
     * Endpoint universal de escaneo.
     * Recibe un código y un contexto (COMPRA, VENTA, INVENTARIO, GARANTIA)
     * y devuelve la información del producto + acción sugerida.
     * Compatible con: cámara de celular, cámara web, pistola USB.
     */
    @PostMapping("/escaneo")
    public ResponseEntity<EscaneoResponse> procesarEscaneo(
            @Valid @RequestBody EscaneoRequest request) {
        return ResponseEntity.ok(escaneoService.procesarEscaneo(request));
    }

    /**
     * Lookup rápido por código de barras (alias GET para POS/pistola).
     */
    @GetMapping("/productos/buscar-por-codigo")
    public ResponseEntity<EscaneoResponse> buscarPorCodigo(
            @RequestParam String codigo,
            @RequestParam(defaultValue = "1") Integer sucursalId) {
        EscaneoRequest req = new EscaneoRequest();
        req.setCodigo(codigo);
        req.setContexto("VENTA");
        req.setSucursalId(sucursalId);
        return ResponseEntity.ok(escaneoService.procesarEscaneo(req));
    }

    // ========================================================================
    // GESTIÓN DE CÓDIGOS DE BARRAS
    // ========================================================================

    /**
     * Vincula un nuevo código de barras/QR a un producto existente.
     */
    @PostMapping("/productos/{skuInterno}/codigos-barras")
    public ResponseEntity<CodigoBarrasResponse> vincularCodigo(
            @PathVariable String skuInterno,
            @Valid @RequestBody CodigoBarrasRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(escaneoService.vincularCodigo(skuInterno, request));
    }

    /**
     * Lista todos los códigos de barras activos vinculados a un producto.
     */
    @GetMapping("/productos/{skuInterno}/codigos-barras")
    public ResponseEntity<List<CodigoBarrasResponse>> listarCodigos(
            @PathVariable String skuInterno) {
        return ResponseEntity.ok(escaneoService.listarCodigos(skuInterno));
    }

    /**
     * Desactiva un código de barras (soft-delete).
     */
    @DeleteMapping("/codigos-barras/{id}")
    public ResponseEntity<Void> desactivarCodigo(@PathVariable Integer id) {
        escaneoService.desactivarCodigo(id);
        return ResponseEntity.noContent().build();
    }

    // ========================================================================
    // IMPORTACIÓN MASIVA
    // ========================================================================

    /**
     * Carga masiva de productos con sus códigos de barras.
     * Cada fila se procesa de forma independiente para tolerar fallas parciales.
     */
    @PostMapping("/productos/importar-masivo")
    public ResponseEntity<ImportacionMasivaResponse> importarMasivo(
            @Valid @RequestBody List<ImportacionMasivaItemRequest> items) {
        return ResponseEntity.ok(escaneoService.importarMasivo(items));
    }
}
