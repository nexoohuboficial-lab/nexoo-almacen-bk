package com.nexoohub.almacen.catalogo.controller;

import com.nexoohub.almacen.catalogo.entity.TipoCliente;
import com.nexoohub.almacen.catalogo.repository.TipoClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tipos-cliente")
public class TipoClienteController {

    @Autowired
    private TipoClienteRepository tipoClienteRepository;

    @GetMapping
    public ResponseEntity<List<TipoCliente>> listarTiposCliente() {
        // Devuelve la lista completa: [1: Público General, 2: Taller Mecánico, etc.]
        return ResponseEntity.ok(tipoClienteRepository.findAll());
    }
}