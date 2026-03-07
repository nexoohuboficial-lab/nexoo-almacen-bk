package com.nexoohub.almacen.catalogo.controller;

import com.nexoohub.almacen.catalogo.dto.CategoriaResponseDTO;
import com.nexoohub.almacen.catalogo.entity.Categoria;
import com.nexoohub.almacen.catalogo.mapper.CategoriaMapper;
import com.nexoohub.almacen.catalogo.service.CategoriaService;
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
@RequestMapping("/api/v1/categorias")
public class CategoriaController {
    
    @Autowired
    private CategoriaService categoriaService;
    
    @Autowired
    private CategoriaMapper mapper;

    @GetMapping
    public ResponseEntity<Page<CategoriaResponseDTO>> listarCategorias(
            @PageableDefault(size = 20, sort = "nombre") Pageable pageable) {
        Page<Categoria> categorias = categoriaService.listarCategorias(pageable);
        return ResponseEntity.ok(categorias.map(mapper::toResponseDTO));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> crearCategoria(@Valid @RequestBody Categoria categoria) {
        Categoria guardada = categoriaService.crear(categoria);
        
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("exitoso", true);
        respuesta.put("mensaje", "Categoría creada correctamente");
        respuesta.put("id", guardada.getId());
        
        return new ResponseEntity<>(respuesta, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> actualizarCategoria(@PathVariable("id") Integer id, @Valid @RequestBody Categoria detalles) {
        return categoriaService.buscarPorId(id)
                .map(categoriaExistente -> {
                    categoriaExistente.setNombre(detalles.getNombre());
                    categoriaExistente.setDescripcion(detalles.getDescripcion());
                    categoriaService.actualizar(categoriaExistente);
                    
                    Map<String, Object> respuesta = new HashMap<>();
                    respuesta.put("exitoso", true);
                    respuesta.put("mensaje", "Categoría actualizada correctamente");
                    
                    return ResponseEntity.ok(respuesta);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
