package com.nexoohub.almacen.sucursal.controller;

import com.nexoohub.almacen.sucursal.dto.SucursalResponseDTO;
import com.nexoohub.almacen.sucursal.entity.Sucursal;
import com.nexoohub.almacen.sucursal.mapper.SucursalMapper;
import com.nexoohub.almacen.sucursal.repository.SucursalRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/sucursales")
public class SucursalController {
    
    @Autowired
    private SucursalRepository sucursalRepository;
    
    @Autowired
    private SucursalMapper mapper;

    // 1. OBTENER TODAS LAS SUCURSALES ACTIVAS
    @GetMapping
    public ResponseEntity<Page<SucursalResponseDTO>> listarSucursales(
            @PageableDefault(size = 20, sort = "nombre") Pageable pageable) {
        Page<Sucursal> sucursales = sucursalRepository.findByActivoTrue(pageable);
        return ResponseEntity.ok(sucursales.map(mapper::toResponseDTO));
    }

    // 2. CREAR UNA NUEVA SUCURSAL
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> actualizarSucursal(@PathVariable("id") Integer id, @Valid @RequestBody Sucursal detalles) {
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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> desactivarSucursal(@PathVariable("id") Integer id) {
        return sucursalRepository.findById(id)
                .map(sucursal -> {
                    sucursal.setActivo(false);
                    sucursalRepository.save(sucursal);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
