package com.nexoohub.almacen.catalogo.controller;

import com.nexoohub.almacen.catalogo.entity.Moto;
import com.nexoohub.almacen.catalogo.repository.MotoRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/motos")
public class MotoController {

    @Autowired
    private MotoRepository motoRepository;

    @GetMapping
    public ResponseEntity<List<Moto>> listarMotos() {
        return ResponseEntity.ok(motoRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> crearMoto(@Valid @RequestBody Moto moto) {
        Moto guardada = motoRepository.save(moto);
        
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("exitoso", true);
        respuesta.put("mensaje", "Moto registrada correctamente");
        respuesta.put("id", guardada.getId());
        
        return new ResponseEntity<>(respuesta, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> actualizarMoto(@PathVariable Integer id, @Valid @RequestBody Moto detalles) {
        return motoRepository.findById(id)
                .map(motoExistente -> {
                    motoExistente.setMarca(detalles.getMarca());
                    motoExistente.setModelo(detalles.getModelo());
                    motoExistente.setCilindrada(detalles.getCilindrada());
                    motoRepository.save(motoExistente);
                    
                    Map<String, Object> respuesta = new HashMap<>();
                    respuesta.put("exitoso", true);
                    respuesta.put("mensaje", "Moto actualizada correctamente");
                    
                    return ResponseEntity.ok(respuesta);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
