package com.nexoohub.almacen.catalogo.controller;
import com.nexoohub.almacen.catalogo.dto.ProveedorResponseDTO;
import com.nexoohub.almacen.catalogo.entity.Proveedor;
import com.nexoohub.almacen.catalogo.mapper.ProveedorMapper;
import com.nexoohub.almacen.catalogo.service.ProveedorService;
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
@RequestMapping("/api/v1/proveedores")
public class ProveedorController {

    @Autowired
    private ProveedorService proveedorService;
    
    @Autowired
    private ProveedorMapper mapper;

    @GetMapping
    public ResponseEntity<Page<ProveedorResponseDTO>> listarProveedores(
            @PageableDefault(size = 20, sort = "nombreEmpresa") Pageable pageable) {
        Page<Proveedor> proveedores = proveedorService.listarProveedores(pageable);
        return ResponseEntity.ok(proveedores.map(mapper::toResponseDTO));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> crearProveedor(@Valid @RequestBody Proveedor proveedor) {
        Proveedor guardado = proveedorService.crear(proveedor);
        
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("exitoso", true);
        respuesta.put("mensaje", "Proveedor registrado correctamente");
        respuesta.put("id", guardado.getId());
        
        return new ResponseEntity<>(respuesta, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> actualizarProveedor(@PathVariable("id") Integer id, @Valid @RequestBody Proveedor detalles) {
        return proveedorService.buscarPorId(id)
                .map(prov -> {
                    prov.setNombreEmpresa(detalles.getNombreEmpresa());
                    prov.setRfc(detalles.getRfc());
                    prov.setNombreContacto(detalles.getNombreContacto());
                    prov.setTelefono(detalles.getTelefono());
                    prov.setEmail(detalles.getEmail());
                    prov.setDireccion(detalles.getDireccion());
                    proveedorService.actualizar(prov);
                    
                    Map<String, Object> respuesta = new HashMap<>();
                    respuesta.put("exitoso", true);
                    respuesta.put("mensaje", "Proveedor actualizado correctamente");
                    
                    return ResponseEntity.ok(respuesta);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
