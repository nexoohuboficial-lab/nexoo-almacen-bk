package com.nexoohub.almacen.inventario.controller;

import com.nexoohub.almacen.common.ApiResponse;
import com.nexoohub.almacen.common.exception.ResourceNotFoundException;
import com.nexoohub.almacen.inventario.dto.ProductoResumenDTO;
import com.nexoohub.almacen.inventario.entity.ProductoMaestro;
import com.nexoohub.almacen.inventario.repository.ProductoMaestroRepository;
import com.nexoohub.almacen.inventario.specification.ProductoMaestroSpecification;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/v1/productos")
@CrossOrigin(origins = "*")
public class ProductoController {
    
    // Definimos el logger para esta clase
    private static final Logger log = LoggerFactory.getLogger(ProductoController.class);

    @Autowired
    private ProductoMaestroRepository repository;

    @Autowired
    private ProductoMaestroRepository productoRepository;

    // ==========================================
    // 1. EL MOTOR DE BÚSQUEDA OMNICANAL (NUEVO)
    // ==========================================
    @GetMapping("/search")
    public ResponseEntity<Page<ProductoMaestro>> buscarProductos(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Integer categoriaId,
            @RequestParam(required = false) Integer motoId,
            @RequestParam(required = false) Integer anio,
            @PageableDefault(size = 20, page = 0) Pageable pageable) {
        
        // El Cerebro arma la consulta SQL dinámicamente
        Specification<ProductoMaestro> spec = ProductoMaestroSpecification.busquedaDinamica(q, categoriaId, motoId, anio);
        
        // El Repositorio ejecuta la consulta con los límites de paginación
        Page<ProductoMaestro> resultados = productoRepository.findAll(spec, pageable);
        return ResponseEntity.ok(resultados);
    }

    // ==========================================
    // 2. CREAR PRODUCTO MAESTRO
    // ==========================================
    @PostMapping
    public ResponseEntity<Map<String, Object>> crearProducto(@Valid @RequestBody ProductoMaestro producto) {
        ProductoMaestro guardado = productoRepository.save(producto);
        
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("exitoso", true);
        respuesta.put("mensaje", "Producto creado correctamente");
        respuesta.put("skuInterno", guardado.getSkuInterno());
        
        return new ResponseEntity<>(respuesta, HttpStatus.CREATED);
    }

    // --- NUEVO: Buscar un solo producto ---
    @GetMapping("/{sku}")
    public ResponseEntity<ApiResponse<ProductoMaestro>> obtenerPorSku(@PathVariable String sku) {
        log.info("Consultando producto con SKU: {}", sku);
        ProductoMaestro producto = repository.findById(sku)
            .orElseThrow(() -> new ResourceNotFoundException("El SKU " + sku + " no existe."));
        return ResponseEntity.ok(new ApiResponse<>("Producto encontrado", producto));
    }

    // --- NUEVO: Actualizar producto ---
    @PutMapping("/{sku}")
    public ResponseEntity<ApiResponse<ProductoMaestro>> actualizar(
            @PathVariable String sku, 
            @Valid @RequestBody ProductoMaestro detalles) {
        log.info("Actualizando producto SKU: {}", sku);
        ProductoMaestro productoExistente = repository.findById(sku)
            .orElseThrow(() -> new ResourceNotFoundException("El SKU " + sku + " no existe."));
        // Actualizamos los campos permitidos
        productoExistente.setNombreComercial(detalles.getNombreComercial());
        productoExistente.setClaveSat(detalles.getClaveSat());
        productoExistente.setStockMinimoGlobal(detalles.getStockMinimoGlobal());
        // El SKU interno NO se actualiza porque es la llave primaria
        ProductoMaestro actualizado = repository.save(productoExistente);
        return ResponseEntity.ok(new ApiResponse<>("Producto actualizado con éxito", actualizado));
    }

    // --- NUEVO: Eliminar producto (El que vimos antes) ---
    @DeleteMapping("/{sku}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable String sku) {
        log.info("Intento de eliminar producto: SKU {}", sku);
        ProductoMaestro producto = repository.findById(sku)
            .orElseThrow(() -> new ResourceNotFoundException("No se puede eliminar: El SKU " + sku + " no existe"));
        repository.delete(producto);
        log.info("Producto eliminado: SKU {}", sku);
        
        return ResponseEntity.ok(new ApiResponse<>("Producto eliminado correctamente", null));
    }

    @GetMapping("/mostrador")
    public ResponseEntity<List<ProductoResumenDTO>> buscarParaMostrador(
            @RequestParam String q,
            @RequestParam Integer sucursalId) {
        return ResponseEntity.ok(productoRepository.buscarParaMostrador(q, sucursalId));
    }

}
