package com.nexoohub.almacen.catalogo.controller;

import com.nexoohub.almacen.catalogo.entity.CompatibilidadProducto;
import com.nexoohub.almacen.catalogo.repository.CompatibilidadRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/compatibilidad")
public class CompatibilidadController {
    
    @Autowired
    private CompatibilidadRepository compatibilidadRepository;

    // 1. ENLAZAR UN PRODUCTO CON UNA MOTO
    @PostMapping
    public ResponseEntity<Map<String, Object>> enlazarProductoConMoto(@Valid @RequestBody CompatibilidadProducto compatibilidad) {
        CompatibilidadProducto guardada = compatibilidadRepository.save(compatibilidad);
        
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("exitoso", true);
        respuesta.put("mensaje", "Compatibilidad registrada correctamente");
        respuesta.put("id", guardada.getId());
        
        return new ResponseEntity<>(respuesta, HttpStatus.CREATED);
    }

    // 2. VER A QUÉ MOTOS LE QUEDA UNA PIEZA (Búsqueda por SKU)
    @GetMapping("/producto/{sku}")
    public ResponseEntity<List<CompatibilidadProducto>> buscarPorSku(@PathVariable String sku) {
        return ResponseEntity.ok(compatibilidadRepository.findBySkuInterno(sku));
    }

    // 3. VER QUÉ PIEZAS LE QUEDAN A UNA MOTO (Búsqueda por ID de Moto)
    @GetMapping("/moto/{motoId}")
    public ResponseEntity<List<CompatibilidadProducto>> buscarPorMoto(@PathVariable Integer motoId) {
        return ResponseEntity.ok(compatibilidadRepository.findByMotoId(motoId));
    }
}
