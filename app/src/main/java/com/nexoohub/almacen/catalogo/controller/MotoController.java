package com.nexoohub.almacen.catalogo.controller;

import com.nexoohub.almacen.catalogo.entity.Moto;
import com.nexoohub.almacen.catalogo.repository.MotoRepository;
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
@RequestMapping("/api/v1/motos")
public class MotoController {

    @Autowired
    private MotoRepository motoRepository;

    @GetMapping
    public ResponseEntity<Page<Moto>> listarMotos(
            @PageableDefault(size = 50, sort = "marca") Pageable pageable) {
        return ResponseEntity.ok(motoRepository.findAll(pageable));
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
