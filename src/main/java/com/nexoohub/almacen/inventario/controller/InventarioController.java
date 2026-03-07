package com.nexoohub.almacen.inventario.controller;

import com.nexoohub.almacen.inventario.dto.InventarioSucursalProjection;
import com.nexoohub.almacen.inventario.dto.ProductoStockBajoDTO;
import com.nexoohub.almacen.inventario.entity.InventarioSucursal;
import com.nexoohub.almacen.inventario.repository.InventarioSucursalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/inventario")
public class InventarioController {

    @Autowired
    private InventarioSucursalRepository inventarioRepository;

    // 1. CONSULTAR INVENTARIO DE UNA SUCURSAL
    @GetMapping("/sucursales/{sucursalId}")
    public ResponseEntity<Page<InventarioSucursalProjection>> obtenerInventarioPorSucursal(
            @PathVariable("sucursalId") Integer sucursalId,
            @PageableDefault(size = 50, sort = "nombreComercial") Pageable pageable) {
        // Llamamos a la consulta con proyección de interfaz
        Page<InventarioSucursalProjection> inventario = inventarioRepository.obtenerFotografiaInventarioPaginado(sucursalId, pageable);
        return ResponseEntity.ok(inventario);
    }

    // 2. REGISTRAR O ACTUALIZAR EXISTENCIAS EN ANAQUEL
    @PostMapping
    public ResponseEntity<Map<String, Object>> registrarStock(@RequestBody InventarioSucursal inventario) {
        InventarioSucursal guardado = inventarioRepository.save(inventario);
        
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("exitoso", true);
        respuesta.put("mensaje", "Inventario registrado/actualizado correctamente");
        respuesta.put("fechaRegistro", guardado.getFechaActualizacion() != null ? guardado.getFechaActualizacion() : guardado.getFechaCreacion());
        
        return new ResponseEntity<>(respuesta, HttpStatus.CREATED);
    }
    
    // 3. ALERTAS: PRODUCTOS CON STOCK BAJO EN UNA SUCURSAL
    @GetMapping("/alertas/stock-bajo/sucursales/{sucursalId}")
    public ResponseEntity<List<ProductoStockBajoDTO>> obtenerStockBajoPorSucursal(
            @PathVariable("sucursalId") Integer sucursalId) {
        List<ProductoStockBajoDTO> productosStockBajo = inventarioRepository.obtenerProductosStockBajo(sucursalId);
        return ResponseEntity.ok(productosStockBajo);
    }
    
    // 4. ALERTAS: PRODUCTOS CON STOCK BAJO EN TODAS LAS SUCURSALES
    @GetMapping("/alertas/stock-bajo")
    public ResponseEntity<List<ProductoStockBajoDTO>> obtenerTodoStockBajo() {
        List<ProductoStockBajoDTO> productosStockBajo = inventarioRepository.obtenerTodosProductosStockBajo();
        return ResponseEntity.ok(productosStockBajo);
    }
}
