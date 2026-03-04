package com.nexoohub.almacen.catalogo.controller;
import com.nexoohub.almacen.catalogo.entity.Proveedor;
import com.nexoohub.almacen.catalogo.repository.ProveedorRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/proveedores")
public class ProveedorController {

    @Autowired
    private ProveedorRepository proveedorRepository;

    @GetMapping
    public ResponseEntity<List<Proveedor>> listarProveedores() {
        return ResponseEntity.ok(proveedorRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> crearProveedor(@Valid @RequestBody Proveedor proveedor) {
        Proveedor guardado = proveedorRepository.save(proveedor);
        
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("exitoso", true);
        respuesta.put("mensaje", "Proveedor registrado correctamente");
        respuesta.put("id", guardado.getId());
        
        return new ResponseEntity<>(respuesta, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> actualizarProveedor(@PathVariable Integer id, @Valid @RequestBody Proveedor detalles) {
        return proveedorRepository.findById(id)
                .map(prov -> {
                    prov.setNombreEmpresa(detalles.getNombreEmpresa());
                    prov.setRfc(detalles.getRfc());
                    prov.setNombreContacto(detalles.getNombreContacto());
                    prov.setTelefono(detalles.getTelefono());
                    prov.setEmail(detalles.getEmail());
                    prov.setDireccion(detalles.getDireccion());
                    proveedorRepository.save(prov);
                    
                    Map<String, Object> respuesta = new HashMap<>();
                    respuesta.put("exitoso", true);
                    respuesta.put("mensaje", "Proveedor actualizado correctamente");
                    
                    return ResponseEntity.ok(respuesta);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
