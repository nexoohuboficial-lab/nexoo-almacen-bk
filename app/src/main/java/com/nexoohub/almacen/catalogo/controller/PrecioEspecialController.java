package com.nexoohub.almacen.catalogo.controller;

import com.nexoohub.almacen.catalogo.entity.PrecioEspecial;
import com.nexoohub.almacen.catalogo.service.PrecioEspecialService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/precios-especiales")
public class PrecioEspecialController {

    @Autowired
    private PrecioEspecialService precioEspecialService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> crearPrecioEspecial(@Valid @RequestBody PrecioEspecial precioEspecial) {
        PrecioEspecial guardado = precioEspecialService.crear(precioEspecial);
        
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("exitoso", true);
        respuesta.put("mensaje", "Precio especial asignado correctamente");
        respuesta.put("id", guardado.getId());
        
        return new ResponseEntity<>(respuesta, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPrecioEspecial(@PathVariable Integer id) {
        precioEspecialService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}