package com.nexoohub.almacen.catalogo.controller;

import com.nexoohub.almacen.catalogo.entity.TipoCliente;
import com.nexoohub.almacen.catalogo.service.TipoClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tipos-cliente")
public class TipoClienteController {

    @Autowired
    private TipoClienteService tipoClienteService;

    @GetMapping
    public ResponseEntity<Page<TipoCliente>> listarTiposCliente(
            @PageableDefault(size = 20, sort = "nombre") Pageable pageable) {
        // Devuelve la lista completa: [1: Público General, 2: Taller Mecánico, etc.]
        return ResponseEntity.ok(tipoClienteService.listarTiposCliente(pageable));
    }
}