package com.nexoohub.almacen.catalogo.controller;

import com.nexoohub.almacen.catalogo.dto.CompatibilidadResponseDTO;
import com.nexoohub.almacen.catalogo.entity.CompatibilidadProducto;
import com.nexoohub.almacen.catalogo.mapper.CompatibilidadMapper;
import com.nexoohub.almacen.catalogo.repository.CompatibilidadRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/compatibilidad")
public class CompatibilidadController {
    
    @Autowired
    private CompatibilidadRepository compatibilidadRepository;
    
    @Autowired
    private CompatibilidadMapper mapper;

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
    public ResponseEntity<Page<CompatibilidadResponseDTO>> buscarPorSku(
            @PathVariable String sku,
            @PageableDefault(size = 50) Pageable pageable) {
        Page<CompatibilidadProducto> compatibilidades = compatibilidadRepository.findBySkuInterno(sku, pageable);
        return ResponseEntity.ok(compatibilidades.map(mapper::toResponseDTO));
    }

    // 3. VER QUÉ PIEZAS LE QUEDAN A UNA MOTO (Búsqueda por ID de Moto)
    @GetMapping("/moto/{motoId}")
    public ResponseEntity<Page<CompatibilidadResponseDTO>> buscarPorMoto(
            @PathVariable Integer motoId,
            @PageableDefault(size = 50) Pageable pageable) {
        Page<CompatibilidadProducto> compatibilidades = compatibilidadRepository.findByMotoId(motoId, pageable);
        return ResponseEntity.ok(compatibilidades.map(mapper::toResponseDTO));
    }
}
