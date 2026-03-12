package com.nexoohub.almacen.catalogo.controller;

import com.nexoohub.almacen.catalogo.dto.TipoClienteResponseDTO;
import com.nexoohub.almacen.catalogo.entity.TipoCliente;
import com.nexoohub.almacen.catalogo.mapper.TipoClienteMapper;
import com.nexoohub.almacen.catalogo.service.TipoClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tipos-cliente")
public class TipoClienteController {

    @Autowired
    private TipoClienteService tipoClienteService;
    
    @Autowired
    private TipoClienteMapper mapper;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'SUPERVISOR', 'VENDEDOR', 'ALMACENISTA', 'CAJERO', 'AUDITOR')")
    public ResponseEntity<Page<TipoClienteResponseDTO>> listarTiposCliente(
            @PageableDefault(size = 20, sort = "nombre") Pageable pageable) {
        // Devuelve la lista completa: [1: Público General, 2: Taller Mecánico, etc.]
        Page<TipoCliente> tipos = tipoClienteService.listarTiposCliente(pageable);
        return ResponseEntity.ok(tipos.map(mapper::toResponseDTO));
    }
}