package com.nexoohub.almacen.adquisiciones.controller;

import com.nexoohub.almacen.adquisiciones.dto.OpcionCompraProveedorDTO;
import com.nexoohub.almacen.adquisiciones.service.ComparadorPreciosService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sup/comparador")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ComparadorPreciosController {

    private final ComparadorPreciosService comparadorPreciosService;

    public ComparadorPreciosController(ComparadorPreciosService comparadorPreciosService) {
        this.comparadorPreciosService = comparadorPreciosService;
    }

    /**
     * Compara los precios y opciones de compra de los proveedores para un producto específico.
     * 
     * @param sku Identificador interno del producto (SKU)
     * @return Lista de opciones de compra ordenadas de mejor a peor precio
     */
    @GetMapping("/producto/{sku}")
    public ResponseEntity<List<OpcionCompraProveedorDTO>> compararPrecios(@PathVariable String sku) {
        List<OpcionCompraProveedorDTO> opciones = comparadorPreciosService.compararPreciosParaProducto(sku);
        return ResponseEntity.ok(opciones);
    }
}
