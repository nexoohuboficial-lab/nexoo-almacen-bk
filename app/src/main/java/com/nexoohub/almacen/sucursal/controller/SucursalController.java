package com.nexoohub.almacen.sucursal.controller;

import com.nexoohub.almacen.sucursal.entity.Sucursal;
import com.nexoohub.almacen.sucursal.repository.SucursalRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/v1/sucursales")
public class SucursalController {
    
    @Autowired
    private SucursalRepository sucursalRepository;

    // 1. OBTENER TODAS LAS SUCURSALES ACTIVAS
    @GetMapping
    public ResponseEntity<List<Sucursal>> listarSucursales() {
        List<Sucursal> sucursales = sucursalRepository.findByActivoTrue();
        return ResponseEntity.ok(sucursales);
    }

    // 2. CREAR UNA NUEVA SUCURSAL
    @PostMapping
    public ResponseEntity<Map<String, Object>> crearSucursal(@Valid @RequestBody Sucursal sucursal) {
        Sucursal guardada = sucursalRepository.save(sucursal);
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("exitoso", true);
        respuesta.put("mensaje", "Sucursal creada correctamente");
        respuesta.put("fechaCreacion", guardada.getFechaCreacion());
        return new ResponseEntity<>(respuesta, HttpStatus.CREATED);
    }

    // 3. ACTUALIZAR UNA SUCURSAL EXISTENTE
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> actualizarSucursal(@PathVariable Integer id, @Valid @RequestBody Sucursal detalles) {
        return sucursalRepository.findById(id)
                .map(sucursalExistente -> {
                    sucursalExistente.setNombre(detalles.getNombre());
                    sucursalExistente.setDireccion(detalles.getDireccion());
                    Sucursal actualizada = sucursalRepository.save(sucursalExistente);
                    
                    Map<String, Object> respuesta = new HashMap<>();
                    respuesta.put("exitoso", true);
                    respuesta.put("mensaje", "Sucursal actualizada correctamente");
                    respuesta.put("fechaActualizacion", actualizada.getFechaActualizacion());
                    
                    return ResponseEntity.ok(respuesta);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // 4. BORRADO LÓGICO (Desactivar)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivarSucursal(@PathVariable Integer id) {
        return sucursalRepository.findById(id)
                .map(sucursal -> {
                    sucursal.setActivo(false);
                    sucursalRepository.save(sucursal);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
