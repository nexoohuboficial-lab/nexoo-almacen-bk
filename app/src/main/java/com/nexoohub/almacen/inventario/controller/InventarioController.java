package com.nexoohub.almacen.inventario.controller;

import com.nexoohub.almacen.inventario.dto.InventarioSucursalDTO;
import com.nexoohub.almacen.inventario.entity.InventarioSucursal;
import com.nexoohub.almacen.inventario.repository.InventarioSucursalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/v1/inventario")
public class InventarioController {

    @Autowired
    private InventarioSucursalRepository inventarioRepository;

    // 1. CONSULTAR INVENTARIO DE UNA SUCURSAL
    @GetMapping("/sucursales/{sucursalId}")
    public ResponseEntity<List<InventarioSucursalDTO>> obtenerInventarioPorSucursal(@PathVariable Integer sucursalId) {
        // Llamamos a la consulta mágica que creaste en el repositorio
        List<InventarioSucursalDTO> inventario = inventarioRepository.obtenerFotografiaInventario(sucursalId);
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
}
